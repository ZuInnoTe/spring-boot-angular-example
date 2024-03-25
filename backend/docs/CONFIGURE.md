# Introduction

You can configure the application in various places:
* The application configuration (e.g. you can specify the location via ```--spring.config.location=/home/app/config-app.yml```. See also [Spring Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)).
* More detailed configuration of the logger, e.g. log4j2.yml. If you use Log4j2 then see also [Log4j2 configuration](https://logging.apache.org/log4j/2.x/manual/configuration.html).


Often you have the choice between different configuration file formats:
* [YAML](https://en.wikipedia.org/wiki/YAML)
* [JSON](https://en.wikipedia.org/wiki/JSON)
* [Properties](https://en.wikipedia.org/wiki/.properties)
* [XML](https://en.wikipedia.org/wiki/XML)

I recommend to choose one of them to simplify the life of the people responsible for operating your application. This project uses the Yaml file format, but you can just replace the Yaml files with corresponding configuration files in any of the other supported formats. You should probably avoid complex file formats, such as XML, as they can have a higher attack surface due to security issues (see [OWASP XML Security Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/XML_Security_Cheat_Sheet.html)).

You can also provide some configuration on the commmand line using the ```-Dname=value```.

# JDK Configuration
This demo uses only Long-Term-Support (LTS) versions of the JDK as they have longer time support and can be patched easily without the need for excessive retesting of an application. They are stable for running applications in production. Use always the latest JDK LTS version with the latest patch level.


Do not use non-LTS versions! They have very short support times and may cause compatibility issues with third party Java libraries.

## CPU
You should provide the application at least 2 cores (or 2048 timeslices) so it can work fast and efficient also taking into account the needs of the Java Garbage Collection (GC). This should be done independent where the application runs (e.g. container, VM, bare metal). See [here](https://developers.redhat.com/articles/2022/04/19/best-practices-java-single-core-containers) why.

## Memory 
Like for any Java application you need to configure how much heap the application can consume. You should never let the application consume 100% of the memory as the operating system (caches, direct memory etc.) also needs some memory to make the Java application running efficiently.

At a minimum the application should have 2GB of heap to have a good performance when multiple users use your application.

However, by default the maximum available heap is [only 50% of the available memory](https://www.baeldung.com/java-jvm-parameters-rampercentage) or sometimes [even only 25%](https://learn.microsoft.com/en-us/azure/developer/java/containers/overview). This means a lot of memory is not available to the application.

In the past one often specified the exact amount of memory for the heap (e.g. 2GB) for a Java application.

However, nowadays with the dynamic configuration of the memory available, one should specify during application startup the percentage of the memory reserved by the heap using the parameter ```-XX:MaxRAMPercentage``` (see [here](https://www.baeldung.com/java-jvm-parameters-rampercentage)).

Example:
```
HEAP_MEMORY_PERCENTAGE=80
java -XX:MaxRAMPercentage=$HEAP_MEMORY_PERCENTAGE [..] -jar springwebdemo.jar  [..]
```

You should allow the application to take up to 80% of the available memory (as in the example), so that 20% is left for the operating system, [direct buffers](https://www.baeldung.com/java-jvm-memory-types) etc.

Note: A search index or NoSQL database written in Java may have different requirements: Here you will need to have a good IO performance and should make memory available to the operating system disk cache. Hence, you should provide for the Java heap only maximum of 50% of the total memory (the rest is then automatically available for operating system caches). However, this is usually not relevant for Java web applications.
## Enable support for NUMA
The [NUMA memory allocator](https://openjdk.org/jeps/345) allocates memory object into a memory area optimized for specific needs (e.g. very fast memory if immediately used or slower memory if it is not likely to be used immediately). Most modern computers have a [NUMA](https://en.wikipedia.org/wiki/Non-uniform_memory_access) like memory architecture.  

Activate it with the option
```
-XX:+UseNUMA
```
If your environment does not support NUMA then the application will continue without NUMA support.
## Garbage Collection (GC)
The [Garbage Collection](https://en.wikipedia.org/wiki/Garbage_collection_(computer_science)) is an important part of any Java-based application. 
Recent Java versions (>= LTS 17) have added novel low-latency GC algorithms, which are very suitable - amongst others - for web applications:

* [Shenandoah GC](https://wiki.openjdk.org/display/shenandoah/Main)
* [ZGC](https://wiki.openjdk.org/display/zgc)

Shenandoah GC is more memory efficient for heaps smaller than 32 GB. Both are suitable for terabytes of Java heap. Other GCs, e.g. G1C, only support much smaller heaps properly.

Aside of the selection of the GC algorithm, you should as little as possible change the default parameter of the
Do not forget to provide enough memory to the application for an efficient GC as described above.

Example:
```
# Use -XX:+UseShenandoahGC if heap < 32 GB, use ZGC if heap > 32 GB
# Use 80% of the availabe ram for heap
HEAP_MEMORY_PERCENTAGE=80
MEMORY_KB=$(grep MemTotal /proc/meminfo | awk '{print $2}')
MEMORY_GB=$(( MEMORY_KB /  (1024*1024) )) 
HEAP_MEMORY_GB=$(( MEMORY_GB * HEAP_MEMORY_PERCENTAGE / 100 ))
[ "$HEAP_MEMORY_GB"  -lt 32 ] &&
   GARBAGE_COLLECTOR=+UseShenandoahGC ||
   GARBAGE_COLLECTOR=+UseZGC -XX:+ZGenerational
java -XX:+UseNUMA -XX:$GARBAGE_COLLECTOR -XX:MaxRAMPercentage=$HEAP_MEMORY_PERCENTAGE [..] -jar springwebdemo.jar  [..]
```
## TLS
You can configure TLS, so that your application can be accessed through an encrypted channel, on the command line using Java properties as follows:
```
java -Dserver.ssl.key-store-type=PKCS12 -Dserver.ssl.key-store=/home/app/backend.p12 -Dserver.ssl.key-alias=backend -Dserver.ssl.enabled=true -Dserver.ssl.key-store-password=$RANDOM_STR -Dserver.port=8443 -jar ManagementUI.jar  
```
In this example we set the Java properties "server.ssl.key-store-type", "server.ssl.key-store", "server.ssl.key-alias", "server.ssl.enabled", "server.ssl.key-store-password", "server.port"
Note: Java properties are supported by Java internal and third party libraries. Consult the Java documentation and your library which one they support.

You can generate a self-signed certificate for testing purposes:
```
# Generate self-signed certificate. For production purposes you should have a certificate signed by a private or public Certification Authority (CA).
RANDOM_STR=$(cat /dev/urandom |  tr -dc 'a-zA-Z0-9' | fold -w 50 | head -n 1)

# please check with security on the algorithm. 
keytool -genkeypair -alias backend -keyalg EC -groupname secp256r1 -storetype PKCS12 -keystore /home/app/backend.p12 -validity 365  -dname "cn=backend, ou=Spring Boot Angular Application, o=Unknown, c=Unknown" -storepass $RANDOM_STR
```

Usually for production applications you need to have a certificate signed by a certification authority. This can be an enterprise-internal one or one which signs certificate for the public Internet(e.g. [Let's Encrypt](https://letsencrypt.org/)).

# Spring
Spring Boot usually has very good production-ready default configurations, because one of the main objectives of Spring Boot was exactly that - good out-of-the-box configuration. However, certain aspects will still require to make good choices for configuration in terms of security, performance etc. We cannot describe them all here, but refer to the Spring documentation and general good practices.

We recommend to always use the latest version of Spring Boot as it contains every version improved default configurations.

We will only describe few selected configurations for Spring.
## Virtual Threads
[Virtual Threads](https://openjdk.org/jeps/444) are a new feature of JDK21 LTS and [Spring boot 3.2 has initial support](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.2-Release-Notes#support-for-virtual-threads) for them (which will be much more extended in future versions). Essentially they can lead to much more efficient use of memory for high-concurrency applications (e.g. web applications).

They can be activated in your application configuration file (see above).
Example for a configuration file in YAML format:
```
spring:
  threads:
    virtual: 
      enabled: true
```

An example can be also found [here](../src/main/resources/application.yml).
## Authentication: SAML2
SAML2 can be configured by activating the profile "saml2". This profile is provided by the application (see [Spring Boot Profiles](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.profiles)) and also the definition of the profile in the code ([../src/main/java/eu/zuinnote/example/springwebdemo/configuration/SecurityConfigurationSaml2.java](../src/main/java/eu/zuinnote/example/springwebdemo/configuration/SecurityConfigurationSaml2.java))).
The rest of the SAML properties come from [Spring Security for SAML2](https://docs.spring.io/spring-security/reference/servlet/saml2/index.html).


The following example shows how to configure SAML2 for an IDP "myidp" in the application properties file. You need to get from your SAML2 IDP the myidp-metadat.xml file containing the IDPs metadata.
Essentially we provide a key for signing and another one for encrypting SAML messages as well as we configure the IGAM SAML2 metadata for a specific enviroment. You can generate them yourself using keytool. NEVER store them in the container image - this is a security issue. Always fetch them during runtime of the container from a secret vault, such as Hashicorp Vault or AWS Secrets Manager, before starting the web application!

```
spring:
  profiles: # important!
    active: "saml2"
  security:
    saml2:
      relyingparty:
        registration:
          myidp:
            signing:
              credentials:
                - private-key-location: file:/home/app/saml-signing.key
                  certificate-location: file:/home/app/saml-signing.crt
            decryption: # do not use encryption, because encryption is about the certificate of the IdP and provided in the asserting party metadata uri. Decryption is about the key for the application to encrypt SAML messages for the application
              credentials:
                - private-key-location: file:/home/app/saml-encryption.key
                  certificate-location: file:/home/app/saml-encryption.crt
            singlelogout:
              binding: POST
              response-url: "{baseUrl}/logout/saml2/slo"
            assertingparty:
              metadata-uri: file:/home/app/myidp-metadata.xml
```

Find a complete configuration example in [../../config/config-saml2.yml](../../config/config-saml2.yml).
## Authentication: OIDC
You can configure OIDC as follows (see https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html for detailed configuration instructions)
```
spring:
  profiles: # important!
    active: "oidc"
  security:
    oauth2:
      client:
        registration: 
          myidp: 
            client-id: <client-id>  # Do not store in repository, dynamically load it from a secret vault during RUNTIME of the container
            client-secret: <secret> # Do not store in repository, dynamically load it from a secret vault during RUNTIME of the container
```

You need to get the client-id and the secret from your OIDC IDP. You must NEVER store them in the container image. Always fetch them during runtime of the container from a secret vault, such as Hashicorp Vault or AWS Secrets Manager, before starting the web application!

Find a complete configuration example in [../../config/config-oidc.yml](../../config/config-oidc.yml).

### Session cookie
At the moment, we need to configure the session cookie with a specific samesite policy so that it works with SAML. We use the following configuration.
```
server:
  servlet:
    session:
      cookie:
          same-site: none # Needed for SAML2 to work
```
Note: Even though it does not start with Spring, it is a Spring specific config.


# Application
We described in the introduction how you can specify a file that contains the configuration of spring properties and application-specific properties. The following application-specific properties are available for this application.


# Database
tbd

# Authentication
## Overview
saml2 or oidc, not both at the same time


## OIDC
tbd

## SAML2
tbd


# Logging
tbd


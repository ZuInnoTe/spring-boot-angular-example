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
   GARBAGE_COLLECTOR=+UseZGC
java -XX:+UseNUMA -XX:$GARBAGE_COLLECTOR -XX:MaxRAMPercentage=$HEAP_MEMORY_PERCENTAGE [..] -jar springwebdemo.jar  [..]
```
## TLS
You can configure TLS, so that your application can be accessed through an encrypted channel, on the command line using Java properties as follows:
```
java -Dserver.ssl.key-store-type=PKCS12 -Dserver.ssl.key-store=/home/app/backend.p12 -Dserver.ssl.key-alias=backend -Dserver.ssl.enabled=true -Dserver.ssl.key-store-password=$RANDOM_STR -Dserver.port=8443 -jar springwebdemo.jar  
```
In this example we set the Java properties "server.ssl.key-store-type", "server.ssl.key-store", "server.ssl.key-alias", "server.ssl.enabled", "server.ssl.key-store-password", "server.port"
Note: Java properties are supported by Java internal and third party libraries. Consult the Java documentation and your library which one they support.

You can generate a self-signed certificate for testing purposes:
```
# Generate self-signed certificate. For production purposes you should have a certificate signed by a private or public Certification Authority (CA).
RANDOM_STR=$(cat /dev/urandom |  tr -dc 'a-zA-Z0-9' | fold -w 50 | head -n 1)

# please check with security on the algorithm. 
keytool -genkeypair -alias backend -keyalg EC -groupname secp256r1 -storetype PKCS12 -keystore /home/app/backend.p12 -validity 90  -dname "cn=backend, ou=Spring Boot Angular Application, o=Unknown, c=Unknown" -storepass $RANDOM_STR
```

Note: You have to choose an algorithm for the certificate that balances performance and security. This constantly changes and you should at least yearly check that they offer still a good security trade-off. You can find a list of algorithms supported in JDK 21 LTS [here](https://docs.oracle.com/en/java/javase/21/docs/specs/security/standard-names.html#parameterspec-names).

[According to recommendations by Mozilla](https://wiki.mozilla.org./Security/Server_Side_TLS) you should have a certificate validity of maximum 90 days and you should replace them before they end.

More information on [save curves for elliptic-curve cryptography](https://safecurves.cr.yp.to/).

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
## Server TLS Protocols
You should only enable latest TLS protocols with [perfect forward secrecy](https://en.wikipedia.org/wiki/Forward_secrecy). You can find them in [recommendations by Mozilla](https://wiki.mozilla.org./Security/Server_Side_TLS).

Example:
```
server:
  ssl:
    enabled-protocols: TLSv1.3
    ciphers: TLS_AES_256_GCM_SHA384,TLS_CHACHA20_POLY1305_SHA256
```

See full example in [../../config/config-oidc.yml](../../config/config-oidc.yml).
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
          oidcidp: 
            client-id: <client-id>  # Do not store in repository, dynamically load it from a secret vault during RUNTIME of the container
            client-secret: <secret> # Do not store in repository, dynamically load it from a secret vault during RUNTIME of the container
        provider:
           oidcidp:
            issuer-uri: <issuer-uri> # idp metadata uri for auto-configuration. See https://docs.spring.io/spring-security/reference/servlet/oauth2/login/core.html#oauth2login-sample-application-config 
```

You need to get the client-id and the secret from your OIDC IDP. You must NEVER store them in the container image. Always fetch them during runtime of the container from a secret vault, such as Hashicorp Vault or AWS Secrets Manager, before starting the web application!

You can also configure an [OIDC Claims to Role Mapping](#oidc-claims-to-role-mapping) (see below). This allows you to authorise access to your controllers or even more fine-granular access using declarative policies with Spring Security Authorities in your application.

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

## Logging
You can configure various logging levels. Find here an example configuration where for different packages different log levels are chosen.
```
logging:
    level:
        root: "warn"
        org.springframework.web: "info"
        eu.europa.ecb.peer: "info"
        org.hibernate: "error"
```
We log only to the console as in most container environments the console output is forwarded to a central logging solutions. Nevertheless, you can easily configure a log file that rotates automatically and is compressed.
See [here](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging). Note: We use Log4j2 so you can also benefit from enhanced features provided by log4j2

Please set the logging levels according your environments! A production environment often needs warn or info, but never needs "debug". The more is logged the lower the performance of an application is. However, you should think carefully about what you need to log to operate/monitor/troubleshoot your application and what you need for security reasons. 

By default we make all loggers asynchronously (see [../src/main/resources/log4j2.component.properties](../src/main/resources/log4j2.component.properties)). You can overwrite it with a system property (see [here](https://logging.apache.org/log4j/2.x/manual/async.html#making-all-loggers-asynchronous)). This can increase the performance of logging in high-throughput scenarios (e.g. web applications with many users) significantly.

It can make sense to configure logging for certain Java classes as synchronous (e.g. for audit purposes) and for others as asyncrhonous to beefit from higher performance.

## Actuator
Spring Actuator provides useful information on the health of an application. However, the information exposed can contain sensitive data (such as heap dumps). You should carefully assess which [Spring Actuator Endpoints you want to expose](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.exposing). You should never expose them to unauthenticated users and never on the public Internet. Hence, we have in the default configuration all Spring Actuator endpoints deactivated and configure Actuator to sanitize sensitive data (does not help with heapdumps). Please read carefully the SpringBoot documentation if you want to enable one endpoint.

```
# actuator: be careful what you expose and only expose selected minimal health endpoints to authenticated specific users
management:
   endpoints:
        jmx:
           exposure:
              exclude: "*"  # Do not expose any endpoint and carefully assess which one to expose as it can be a security risk
        web:
           exposure:
              exclude: "*" # Do not expose any endpoint and carefully assess which one to expose as it can be a security risk
        show-values: never # Redact sensitive values
```

# Application
We described in the introduction how you can specify a file that contains the configuration of spring properties and application-specific properties. The following application-specific properties are available for this application.

## SAML2 Metadata endpoint
Enable the SAML2 metadata endpoint. Note: This is only temporarily needed to fetch the SAML2 application metadata to be provided to your SAML2 IDP for configuraing the application.
The endpoint can be found after enablement under (if you have configured the name igam in the config as described above): https://<URL>/saml2/service-provider-metadata/myidp

After you have downloaded the metadata you can disable the endpoint again.

Example:
```
application:
  saml2:
      enableMetadataEndpoint: false # should only be temporary enabled for security reasons. if enabled then you find it https://<URL>/saml2/service-provider-metadata/myidp (an xml will download as file in your browser)
```

You can find a complete example in [](../../config/config-saml2.yml).
## SAML2 Role extraction
Depending on your IDP configuration you will find the roles for your application in different SAML2 assertions. You can configure this as follows:
* samlRoleAttributeName: In which SAML assertitions the role can be found
* samlRoleAttributeSeparator: If one value contains multiple roles and how they are separated (e.g. using a comma). This is ignored if roles are provided as multi-value attributes

Example:
```
application:
  saml2:
       samlRoleAttributeName: "groups" # the SAML Assertation Attribute that contains the role(s)
       samlRoleAttributeSeparator: "," # if the SAML Asseration Attribute value contains multiple roles then you can specify the separator (if the roles are in multiple attributes then you can ignore it)
```

You can find a complete example in [](../../config/config-saml2.yml).

## OIDC Claims to Role Mapping
This allows you to authorise access within your application when you configure [OIDC for authentication](#authentication-oidc) (see above).

By default OIDC claims "scope, scp" are made available as a Spring Authority with the prefix "SCOPE_". These come from the [OIDC IdToken](https://openid.net/specs/openid-connect-core-1_0-final.html#StandardClaims). However, often additional claims are needed for Spring Security Authorities (roles), e.g. "groups" in a user directory. Those usually do not come from the OIDC IdToken, but only from the [UserInfo Endpoint](https://openid.net/specs/openid-connect-core-1_0-final.html#UserInfoResponse). You can configure here for both, IdToken and UserInfo endpoint, which claims should be mapped to Spring Security Authorities. Furthermore, you can configure for each claim how they are mapped to authorities. By default, it is assumed that the claims are JSON String arrays, but in case they are string you can define how they are extracted from the String using the claimsSeparatorMap. For example, lets assume the claim "groups" is returned by the UserInfo Endpoint as one String representing a comma-separated list groups. You can define as a separator the "," and the claim is then split accordingly so that you do not have the list of groups as one Spring Security Authority, but multiple representing each one of the groups.

Independent of this you can also map user attributes to Spring Security Authorities.

Finally, you can optionally define a prefix for each claim in the Spring Security Authorities. If you do not want any prefix just specify an empty String.

See [ARCHITECTURE.md](./ARCHITECTURE.md) on how to use the Spring Security Authorities to define permissions in your application.
```
     oidc:
        mapper: # map jwt claims to Spring Security authorities
            jwtIdTokenClaims: ["scope","scp"] # claims from a standard OIDC IdToken https://openid.net/specs/openid-connect-core-1_0-final.html#StandardClaims
            userClaims: ["groups"] # claims from the UserInfo OIDC Endpoint (https://openid.net/specs/openid-connect-core-1_0-final.html#UserInfoResponse)
            userAttributes: [] # user attributes to be mapped to Spring Security Authorities
            claimsSeparatorMap: {"scope": " ", "scp": " ", "groups": ","} # separator if claims are one string, otherwise a JSON array is assumed
            authoritiesPrefix: "ROLE_" # Prefix for Spring Security Authorities
```
## Web Security Headers
Web Security Headers are an additional line of defense to enable specific protection mechanisms against attacks (e.g. cross-site scripting) in the browser of the user.

You can customize some of the security headers of the application. Note: We do not make all headers customizable (e.g. CSFR) as they should be always on.

Additionally: If you configure a Content-Security-Policy (CSP) with "script-src" and "style-src" as below then the [application adds for web frameworks](../src/main/java/eu/zuinnote/example/springwebdemo/configuration/SPACspNonceFilter.java) - like [Angular](https://angular.dev/best-practices/security#content-security-policy) - additionally a nonce and updates the [index.html](../../frontend/src/index.html) dynamically each request with the securely random nonce by replacing the string ${cspNonce} with it. In this way you do not need to specify unsafe-inline.

You can configure for this cspNonceFilterPath which is an array of regex expressions that match the application paths of the frontend (e.g. in our case it is /, /ui/.*). The backend then when this path matches read the SPA html page from the classpath defineed in configuration value cspSPAPage (e.g. public/index.html). Within this html page it replaces the text configured in cspNonceFilterValue (e.g. ${cspNonce}) dynamically with the randomly generated CSP nonce value of each request.

Example:
```
application:
     https:
        headers:
          permissionPolicy: "accelerometer=(),  autoplay=(), camera=(), cross-origin-isolated=(), display-capture=(),  encrypted-media=(),  fullscreen=(), geolocation=(), gyroscope=(), keyboard-map=(), magnetometer=(), microphone=(), midi=(),  payment=(), picture-in-picture=(), publickey-credentials-get=(), screen-wake-lock=(), sync-xhr=(), usb=(), web-share=(), xr-spatial-tracking=(), clipboard-read=(), clipboard-write=(), gamepad=(),  hid=(), idle-detection=(), serial=(),  window-placement=()" 
          csp: "default-src 'none'; base-uri 'self'; script-src 'self'; style-src 'self'; img-src 'self'; connect-src 'self'; font-src 'self'; object-src 'none'; media-src 'none'; child-src 'self' form-action 'self'; frame-ancestors 'none'; navigate-to 'self'; block-all-mixed-content" # Currently the most strict policy available for Angular frontends
          cspNonceFilterPath: ["/","/ui/.*"]
          cspNonceFilterValue: "${cspNonce}"
          cspSPAPage: "public/index.html"
          referrerPolicy: "no-referrer" # possible values: https://github.com/spring-projects/spring-security/blob/main/web/src/main/java/org/springframework/security/web/header/writers/ReferrerPolicyHeaderWriter.java#L101
          coep: "require-corp; report-to=\"default\""
          coop: "same-origin; report-to=\"default\""
          corp: "same-origin"
```
Here we set the HTTP security headers:
* [Permission Policy](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Permissions-Policy)
  * Find here an online generator for permission policies: https://www.permissionspolicy.com/
* [Content Security Policy](https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP)
* [ReferrerPolicy](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Referrer-Policy)
* [Cross Origin Embedder Policy](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cross-Origin-Embedder-Policy) (COEP)
* [Cross Origin Opener Policy](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cross-Origin-Opener-Policy) (COOP)
* [Cross Origin Resource Policy](https://developer.mozilla.org/en-US/docs/Web/HTTP/Cross-Origin_Resource_Policy) (CORP)

You should consult the documentation of the headers, especially if you need to load resources (e.g. images) from another origin etc.

# Database
There are various configuration options for a database. We describe first some general configuration applicable for a wide range of databases and afterwards we explain additional specific database configuration settings. Keep in mind that there are much more options then that can be covered here and we recommend to read the corresponding documentation of Spring Boot and your database. You should always validate your database settings with automated realistic performance tests.

You can find a complete configuration example in [../../config/config-postgres.yml](../../config/config-postgres.yml).
## General
### Set the right database connection pool size
[Connection pooling](https://en.wikipedia.org/wiki/Connection_pool) is a very effective mean to quickly connect to your database, because connections will be reused avoiding time-consuming reconnects. Furthermore it reduces the connection overhead on the database significantly, meaning more compute is available for your queries. A common misconception is that you need to provide a large number of connections in the connection pool to serve thousands of users. In fact, the higher the number of connections in your connection pool the slower your queries on the database may be.

This means you should have a low total number of connections in a given connection pool, e.g. between 5-30. For most of the databases it is better that is more towards the lower end! Even if you have thousands of users it is very often fine to live with 5 connections in a connection pool. The number of optimal connections is NOT only determined by cores in the database or by how many users you have concurrently on the database. It is simply based on combination of factors (cores, disk, network). See also here for a video and detailed discussion (and some ideas to find your optimal pool size): https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing

If you have multiple application instances (ie containers, VMs etc.) accessing the database you should divide the low total number by the number of instances. E.g. let us assume you find out with testing that the total number of connections in a connection pool should be 10 and that you have two instances. Then configure 5 connections in the connection pool of each instance.

Note: If you work in a cloud setting with a lot of application instances (>3-4) and/or different applications accessing your database then you may consider a "central" connection pool, such as  [AWS RDS Proxy](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/rds-proxy.html) as the individual pools of your application may become too small.
 

Spring Boot has in recent versions out of the box support for a hikari connection pool (very fast, read the documentation to learn how to properly connect to a database avoiding anti-patterns). You can configure it as follows in recent Spring Boot versions:

Yaml-version:
```
spring:   
   datasource:
      hikari:
        maximum-pool-size: 5
```
 

You have also a couple of other settings in the Hikari pool:

https://github.com/brettwooldridge/HikariCP#gear-configuration-knobs-baby

There is no magic number that is right or wrong - work with a pool size taking into account also https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing and do testing!

### Configure JPA Batch inserts
JPA inserts/updates are by default executed in individual statements. This means it is very slow if they need to be done in large quantities. You can configure hibernate to batch them and this means the performance significantly increases (see [here](https://www.baeldung.com/spring-data-jpa-batch-inserts) and also the related [Hibernate documentation](https://docs.jboss.org/hibernate/orm/6.4/userguide/html_single/Hibernate_User_Guide.html#batch)).

You can configure them as follows
```
spring:
  jpa: 
     properties:
         hibernate:
            jdbc:
              batch_size: 20 # have a reasonable number between 5-30
            order_inserts: true
            order_updates: true
```
### Enable JPA Hibernate Caching
Hibernate allows to enable different types of caches (see [Hibernate documentation](https://docs.jboss.org/hibernate/orm/6.4/userguide/html_single/Hibernate_User_Guide.html#caching)):
* Second-Level Cache: If entity is not in the session-specific first-level cache then it is loaded from the session-independent second level cache
* Query Cache: Caches results from selected queries so that rarely changing data is directly delivered from the query cache. Note: You additionally must specify for each query that should be cacheable in your code base that is is cacheable (see [here](https://docs.spring.io/spring-framework/reference/integration/cache.html)).

Hibernate supports [different caching providers](https://docs.jboss.org/hibernate/orm/6.4/userguide/html_single/Hibernate_User_Guide.html#caching-config-provider). We use here [Ehcache](https://www.ehcache.org/) through its [JCache interface](https://www.ehcache.org/documentation/3.10/107.html). It must be included in the application (see [../build.gradle](../build.gradle)) 

```
spring:
  jpa:
     properties:
         hibernate:
            javax:
                cache:
                  provider: org.ehcache.jsr107.EhcacheCachingProvider
            cache:
              use_second_level_cache: true
              use_query_cache: true
              region:
                factory_class: org.hibernate.cache.jcache.JCacheRegionFactory
```

## Postgres
We describe here some specific recommendation for configuration the connection to Postgres databases.

### Read the documentation of the Postgres JDBC driver
Read the latest documentation on configuring the Postgres JDBC driver: https://jdbc.postgresql.org/documentation/use/
### Use the latest Postgres JDBC driver

Latest Postgres JDBC drivers have performance and security fixes, so you need to always include the latest version. You can install in your build tool (e.g. Gradle or Maven) a version plugin that show you if there are newer versions.

The Postgres website shows you also a recent version (note: they work also perfectly fine with recent LTS versions 17,21 etc.): https://jdbc.postgresql.org/download/
### Set assumeMinServer version

By setting assumeMinServer version you can make sure that the JDBC drivers use also some latest features for that server version. You can find valid values for this property here: 

https://github.com/pgjdbc/pgjdbc/blob/master/pgjdbc/src/main/java/org/postgresql/core/ServerVersion.java

Note: Do NOT set too low version - you may miss important performance features! Set it as high as possible for your setup.

You can configure it as follows in recent Spring Boot versions:

Yaml-version:
```
spring:   
   datasource:
      hikari:
        data-source-properties:
                assumeMinServerVersion: "17"
```
 

### Set ApplicationName to make requests for your application traceable

You should set an ApplicationName for your application - in this way it is easy to trace issues related to your application within the database. From the documentation:

"This allows a database administrator to see what applications are connected to the server and what resources they are using through views like pg_stat_activity."

Yaml-version:
```
spring:   
   datasource:
      hikari:
        data-source-properties:
                ApplicationName: "SpringBootWeb"
```
 
### Require SSL

You should enforce always SSL on the Postgres database server side. Independent of this it makes always sense to enforce it additionally at client-side, ie JDBC as well. As a minimum you should enable ssl and have as sslmode-require. Example Spring Boot configuration (assuming a recent (warning) Spring Boot version).

You can configure it as follows in recent Spring Boot versions:

Yaml-version:
```
spring:   
   datasource:
      hikari:
        data-source-properties:
                ssl: true
                sslmode: "require"
```
 

 
In this way you can simply connect via SSL to the database without managing truststores etc. at client side. This is already much better than an unencrypted connection.

Additionally you may want also to verify the public certificate of the database server. Postgres provides an extensive documentation how to do this: 

https://jdbc.postgresql.org/documentation/ssl/
### reWriteBatchedInserts
Set reWriteBatchedInserts to true.

This combines multiple inserts statements (as they often occur with JPA/hibernate) into one. According to the documentation this improves performance 2x-3x.

You can configure it as follows in recent Spring Boot versions:

Yaml-version:
```
spring:   
   datasource:
      hikari:
        data-source-properties:
                reWriteBatchedInserts: true
```
 

### Limit Buffering size

By default the Postgres driver uses all available Heap for buffering results from the database. This has many issues, such as that a lot of memory maybe taken by one large query result set or that a lot of Garbage collection needs to happen at once. Instead it can be faster and more reliable (no sudden out of memory errors for your application or unnecessary large provisioning of memory) to limit this.

Even if you limit the buffer size - it is still possible to query data much larger than your buffer size - it is just the amount of data fetched at once from the database. All this happens transparently in the background if you use Spring Boot - ie your application code does not change.

 

One important property to define there is maxResultBuffer. You can define it as a percentage of the heap or as an absolute value. Keep in mind that the available memory should be more then that amount * the number of connections in the connection pool (another reason for having only few connections in a connection pool). 

Then you should also limit the maximum number of rows that are fetched from the database in one go. Important: The result from the query can be much larger - the driver will transparently fetch the other rows once the first rows have been processed. E.g. let us assume your query result number of rows are 1000. You define on the driver configuration only 200 are fetched. Then once the driver fetched the first 200 it will fetch the next 200 transparently for the application (No need to develop some logic for that!).

Furthermore, limiting the buffering may also reduce the load on your database.

Postgres has also adapativeFetch - depending on the expected size of the query result it will decide itself how much it fetches in one go (up to the limit what fits into the buffer!).

You can configure it as follows in recent Spring Boot versions (note you may test/fine-tune a bit the concrete numbers):

Yaml-version:
```
spring:   
   datasource:
      hikari:
        data-source-properties:
                maxResultBuffer: "10p"
                adaptiveFetch: true
                adaptiveFetchMaximum: 200
                defaultRowFetchSize: 100
```
 
### Cleanup Savepoints

You can free a lot of resources on the database server if you cleanup savepoints (if you use them!) - especially if there are a lot of queries.

You can configure it as follows in recent Spring Boot versions 

Yaml-version:
```
spring:   
   datasource:
      hikari:
        data-source-properties:
                cleanupSavePoints: true
```
 
 




#!/bin/bash

### SAML2 Example
# Extract SAML Cert from the environment variables (e.g. injected from a secrets vault - you must NOT store them in the container image itself - this is insecure)
echo -e "$SAML_SIGNING_KEY" > /home/app/saml-signing.key
echo -e "$SAML_SIGNING_CERT"  > /home/app/saml-signing.crt
echo -e "$SAML_ENCRYPTION_KEY" > /home/app/saml-encryption.key
echo -e "$SAML_ENCRYPTION_CERT"  > /home/app/saml-encryption.crt

# Reset the environment variables so they do not contain secrets (reduce attack surface)
export SAML_SIGNING_KEY=empty
export SAML_SIGNING_CERT=empty
unset SAML_SIGNING_KEY
unset SAML_SIGNING_CERT
export SAML_ENCRYPTION_KEY=empty
export SAML_ENCRYPTION_CERT=empty
unset SAML_ENCRYPTION_KEY
unset SAML_ENCRYPTION_CERT


### OIDC EXAMPLE
# Assume that environment variables OIDC_CLIENT_ID and OIDC_SECRET are injected from a secret vault. You must NOT store them in the container image itself - this is insecure
# See at the end also the run application part where /home/app/oidc.yaml is given as a parameter
cat > /home/app/oidc.yml << EOF
spring:
   security:
    oauth2: 
      client:
        registration: 
          oidcidp: 
            client-id: ${OIDC_CLIENT_ID}  
            client-secret: ${OIDC_SECRET}
        provider:
           oidcidp:
            issuer-uri: <issuer-uri> # idp metadata uri for auto-configuration. See https://docs.spring.io/spring-security/reference/servlet/oauth2/login/core.html#oauth2login-sample-application-config 
EOF


### Database credential dynamic injection
# Extract the database credentials from environment variables
# We expect DB_HOST, DB_USER, DB_PASS, DB_NAME are injected from a secret vault
# See at the end also the run application part where /home/app/database.yaml is given as a parameter
cat > /home/app/database.yml << EOF
spring:
   datasource:
       url: jdbc:postgresql://$DB_HOST/$DB_NAME
       username: $DB_USER
       password: $DB_PASS
EOF


### Generate HTTPs certificate
# Generate self-signed certificate. For production purposes you should have a certificate signed by a private or public Certification Authority (CA).
RANDOM_STR=$(cat /dev/urandom |  tr -dc 'a-zA-Z0-9' | fold -w 50 | head -n 1)

# please check with security on the algorithm. 
keytool -genkeypair -alias backend -keyalg EC -groupname secp256r1 -storetype PKCS12 -keystore /home/app/backend.p12 -validity 365  -dname "cn=backend, ou=Spring Boot Angular Application, o=Unknown, c=Unknown" -storepass $RANDOM_STR


### Run application
# This scripts run the application
# Use -XX:+UseShenandoahGC if heap < 32 GB, use ZGC if heap > 32 GB
# Use 80% of the availabe ram for heap
HEAP_MEMORY_PERCENTAGE=80
MEMORY_KB=$(grep MemTotal /proc/meminfo | awk '{print $2}')
MEMORY_GB=$(( MEMORY_KB /  (1024*1024) )) 
HEAP_MEMORY_GB=$(( MEMORY_GB * HEAP_MEMORY_PERCENTAGE / 100 ))
[ "$HEAP_MEMORY_GB"  -lt 32 ] &&
   GARBAGE_COLLECTOR=+UseShenandoahGC ||
   GARBAGE_COLLECTOR=+UseZGC
java -XX:+UseNUMA -XX:$GARBAGE_COLLECTOR -XX:MaxRAMPercentage=$HEAP_MEMORY_PERCENTAGE -Dserver.ssl.key-store-type=PKCS12 -Dserver.ssl.key-store=/home/app/backend.p12 -Dserver.ssl.key-alias=backend -Dserver.ssl.enabled=true -Dserver.ssl.key-store-password=$RANDOM_STR -Dserver.port=8443 -jar springwebdemo.jar  --spring.config.location=/home/app/config.yml,/home/app/oidc.yml,/home/app/database.yml
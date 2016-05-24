#!/bin/bash
#######################################################
# Certification Authority                             #
#######################################################

mkdir group7Certs
cd group7Certs

# Since we do not have any certification authority, we will generate our own.
openssl genrsa -out group7CA.key 2048
openssl req -new -x509 -days 3650 -key group7CA.key -out group7CA.crt -subj "/C=US/ST=AZ/L=Phoenix/O=SS/OU=group7/CN=group7.mobicloud.asu.edu"
mkdir -p group7CA/newcerts
touch group7CA/index.txt
echo '01' > group7CA/serial

# Add the root certificate to cacerts of your JVM
sudo keytool -delete -noprompt -trustcacerts -alias group7CA -keystore ${JAVA_HOME}/jre/lib/security/cacerts -storepass changeit
sudo keytool -import -noprompt -trustcacerts -alias group7CA -file group7CA.crt -keystore ${JAVA_HOME}/jre/lib/security/cacerts -storepass changeit

# Create the trustore with the root certificate in it
keytool -import -keystore cacerts.jks -storepass myasubank -alias group7CA -file group7CA.crt -noprompt

#######################################################
# Group7 certificate                                  #
#######################################################

# Generate the keystore
keytool -genkey -v -alias group7 -keyalg RSA -validity 3650 -keystore group7.jks -storepass myasubank -keypass myasubank -dname "CN=group7.mobicloud.asu.edu, OU=group7, O=SS, L=Phoenix, ST=AZ, C=US"
# Then, generate the CSR to sign:
keytool -certreq -alias group7 -file group7.csr -keystore group7.jks -storepass myasubank
# Sign the certificate to the CA:
openssl ca -batch -keyfile group7CA.key -cert group7CA.crt -policy policy_anything -out group7.crt -infiles group7.csr
# Add the root certificate to the keystores
sudo keytool -importcert -alias group7CA -file group7CA.crt -keystore group7.jks -storepass myasubank -noprompt
# Add signed certificate to the keystores
sudo keytool -importcert -alias group7 -file group7CA/newcerts/01.pem -keystore group7.jks -storepass myasubank -noprompt

# Copy cert to resource folder
cd ..
cp ./group7Certs/group7.jks ./src/main/resources/

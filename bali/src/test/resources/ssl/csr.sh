#!/bin/sh

APP_NAME=$1

echo "Generating key and certificate for $APP_NAME"

openssl req \
  -new \
  -newkey rsa:2048 \
  -nodes \
  -out ${APP_NAME}.csr \
  -keyout ${APP_NAME}.key \
  -subj "/C=ES/ST=Madrid/L=Madrid/O=BALI/OU=DEVELOMENT/CN=${APP_NAME}KEY"

openssl pkcs8 -topk8 -nocrypt -in ${APP_NAME}.key -inform PEM -out ${APP_NAME}.der -outform DER

openssl x509 -req -sha256 -days 365 -in ${APP_NAME}.csr -signkey ${APP_NAME}.key -out ${APP_NAME}.crt

# REST SSL

This directory contains self-signed certificates for testing SSL/TLS,
generated based on instructions from the RabbitMQ tutorial on SSL/TLS:

https://www.rabbitmq.com/ssl.html#enabling-tls

## Generation

You can generated certificates using the following github repository: 

https://github.com/michaelklishin/tls-gen

But instead of using the local machine's name, `localhost` was used.
(simply modify the top-level `Makefile`, replacing all occurrences of 
`$(shell hostname)` with `locahost`). 
NB: In theory, it shouldn't matter what the hostname is.

### Certificates

Change into the directory where you cloned the above repository to
and run the following commands:

```
cd basic
make PASSWORD=adamstest
```

This will output the certificates in `./result`.

Optionally, run the following commands:

```
make verify
make info
```

Once happy with the certificates, copy them to a directory of your
choice (referred to as `/path/to/certs`).

### Keystore

Change into the directory where you copied the certificates to (`/path/to/certs`)
and run the following command to generate a Java keystore file 
in the directory `/path/to/keystore/`: 

```
keytool -import -alias server1 -file /path/to/certs/server_certificate.pem -keystore /path/to/keystore/adamstest.jks
```

The above command will ask you for a password, just use `adamstest`
like for the certificates.

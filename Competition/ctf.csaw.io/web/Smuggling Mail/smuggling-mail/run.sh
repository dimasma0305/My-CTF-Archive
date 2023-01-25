#!/usr/bin/env sh

docker build -t smuggling_mail .
docker run -it -p 4444:8080 --rm smuggling_mail

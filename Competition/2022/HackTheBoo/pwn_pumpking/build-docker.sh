#!/bin/sh
docker build --tag=pumpking .
docker run -it -p 1337:1337 --rm --name=pumpking pumpking


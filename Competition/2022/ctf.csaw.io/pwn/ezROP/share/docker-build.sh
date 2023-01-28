#!/bin/bash
docker rm -f ezrop
docker build --tag=ezrop .
docker run -p 9999:9999 --rm --name=ezrop ezrop
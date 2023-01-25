#!/bin/bash
docker build -t web_phishtale .
docker run --name=web_phishtale --rm -p1337:443 -it web_phishtale

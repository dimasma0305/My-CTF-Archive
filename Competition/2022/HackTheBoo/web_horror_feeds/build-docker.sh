#!/bin/bash
docker rm -f web_horror_feeds
docker build --tag=web_horror_feeds .
docker run -p 4444:1337 --rm --name=web_horror_feeds web_horror_feeds
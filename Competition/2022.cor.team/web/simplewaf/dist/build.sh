docker build . -t testing
docker container run -p 3456:3456 testing:latest
# docker exec -it 
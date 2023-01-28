FROM ubuntu:20.04

ENV HOST localhost:8080

RUN apt update && apt install -y python3 python3-pip
RUN pip3 install flask

RUN mkdir /app

WORKDIR /app

ADD ./requirements.txt /app
RUN pip install -r requirements.txt

ADD ./src /app
ENV FLASK_ENV=development



ENTRYPOINT ["flask", "run", "-h", "0.0.0.0", "-p", "8080"]

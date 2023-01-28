FROM python:3.4

EXPOSE 5000

ENV FLASK_APP main.py
ENV FLASK_DEBUG 0

RUN groupadd -g 444 nobodi
RUN useradd -r -u 444 -g nobodi -m nobodi

COPY requirements.txt /home/nobodi/

RUN apt-get update -y
RUN pip install -r /home/nobodi/requirements.txt


COPY fetch_app/ /home/nobodi/fetch_app
COPY flag.txt /home/nobodi/fetch_app/<REDACTED>

WORKDIR /home/nobodi/fetch_app
USER nobodi

CMD flask run --host 0.0.0.0

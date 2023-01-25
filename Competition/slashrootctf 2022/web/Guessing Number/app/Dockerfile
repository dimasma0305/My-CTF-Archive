FROM python:3.8-alpine

RUN pip3 install --upgrade pip

RUN adduser -D anonim

WORKDIR /home/anonim

ADD . .
ADD ./requirements.txt .

USER anonim

RUN pip install -r requirements.txt

ENV FLASK_APP=app.py
ENV FLASK_RUN_HOST=0.0.0.0
ENV FLASK_RUN_PORT=20202
ENV FLASK_DEBUG=False

EXPOSE 20202

CMD ["python","-m","flask","run"]
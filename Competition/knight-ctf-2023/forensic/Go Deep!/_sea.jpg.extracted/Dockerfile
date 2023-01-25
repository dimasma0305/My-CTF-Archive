FROM ubuntu:latest

RUN apt update
RUN apt-get install -y python-tk tk-dev libffi-dev libssl-dev pandoc \
	libgmp3-dev libzbar-dev tesseract-ocr xsel libpoppler-cpp-dev libmpc-dev \
	libdbus-glib-1-dev ruby libenchant-2-dev apktool nodejs groff binwalk \
	foremost tcpflow poppler-utils exiftool steghide stegsnow bison ffmpeg \
	libgd-dev less git

RUN git clone https://github.com/JohnHammond/katana.git

WORKDIR /katana

RUN python3.7 -m venv env
RUN source env/bin/activate
RUN python setup.py install

ENTRYPOINT [ "katana" ]
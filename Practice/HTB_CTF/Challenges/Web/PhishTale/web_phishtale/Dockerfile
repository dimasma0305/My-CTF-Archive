FROM alpine:3.13

# Install system packages
RUN apk add --no-cache --update supervisor hitch php7 zip curl apache2 \
    php7-apache2 php7-json php7-phar php7-iconv php7-mbstring php7-xml \
    openssl php7-openssl php7-ctype php7-dom php7-session php7-curl php7-zip

# Install varnish dependencies
RUN set -x && apk add --no-cache --upgrade --virtual .build-dependencies autoconf \
    automake build-base cpio gzip libedit-dev libtool linux-headers py-docutils \
    py3-sphinx tar sudo ca-certificates pcre-dev pcre2-dev libunwind-dev gcc libc-dev

# Build varnish from source
RUN cd /tmp && wget -qO- https://github.com/varnishcache/varnish-cache/archive/refs/tags/varnish-6.6.0.tar.gz \
    | tar -C /tmp --strip-components=1 -xvz && \
    sh autogen.sh && sh configure --without-jemalloc --with-unwind && make && sudo make install && \
    rm -rf /tmp/* /var/cache/*

# Fix iconv library
ENV LD_PRELOAD /usr/lib/preloadable_libiconv.so php

# Download composer
RUN wget https://getcomposer.org/composer-1.phar -O /usr/local/bin/composer && chmod +x /usr/local/bin/composer

# Copy configuration files
COPY config/supervisord.conf /etc/supervisord.conf
COPY config/hitch.conf /etc/hitch/hitch.conf
COPY config/default.vcl /etc/varnish/default.vcl
COPY config/httpd.conf /etc/apache2/httpd.conf

# Copy tls certificate
RUN mkdir -p /var/lib/hitch/
RUN mkdir -p /etc/hitch/certs
COPY config/cert.pem /etc/hitch/certs/cert.pem

# Setup permissions
COPY --chown=root challenge /www

# Switch working environment
WORKDIR /www

# Copy flag
COPY flag /root/flag

# Add readflag binary
COPY config/readflag.c /
RUN gcc -o /readflag /readflag.c && chmod 4755 /readflag && rm /readflag.c

# Install dependencies
RUN php7 /usr/local/bin/composer install
RUN chown -R apache:apache /www/var /www/vendor /www/public/static/exports

EXPOSE 443

# Run supervisord
CMD /usr/bin/supervisord -c /etc/supervisord.conf

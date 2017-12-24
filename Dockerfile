FROM ubuntu:16.04

RUN apt-get update
RUN apt-get install -y curl

# Install nodejs 9 & jdk
RUN curl -L https://deb.nodesource.com/setup_9.x | bash -
RUN apt-get install -y nodejs
RUN apt-get install openjdk-8-jdk -y

# Install latest Chrome
RUN apt-get install -yq libgconf-2-4
RUN apt-get update && apt-get install -y wget --no-install-recommends \
    && wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list' \
    && apt-get update \
    && apt-get install -y google-chrome-unstable \
      --no-install-recommends \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get purge --auto-remove -y curl \
    && rm -rf /src/*.deb

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

# Add this application
COPY dist /md2pdf
COPY node_modules /md2pdf/node_modules

RUN mkdir /workspace
VOLUME /workspace
WORKDIR /workspace

ENTRYPOINT ["nodejs", "/md2pdf/Md2PdfConverter.js"]

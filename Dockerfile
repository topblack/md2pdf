FROM ubuntu:16.04

RUN apt-get update

# install nodejs
RUN curl -L https://deb.nodesource.com/setup_9.x | bash -
RUN apt-get install -y nodejs
RUN apt-get install openjdk-8-jdk -y
RUN apt-get install maven -y

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

# test install
RUN npm --version
RUN node --version
RUN java -version

COPY dist /md2pdf
COPY node_modules /md2pdf/node_modules

ENTRYPOINT ["node", "/md2pdf/Md2PdfConverter.js"]

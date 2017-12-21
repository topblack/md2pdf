FROM ubuntu:16.04

RUN apt-get update && apt-get install -y curl libfontconfig git python python-pip firefox xvfb

# install nodejs
RUN curl -L https://deb.nodesource.com/setup_6.x | bash -
RUN apt-get install -y nodejs
RUN apt-get install openjdk-8-jdk -y
RUN apt-get install maven -y

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

# test install
RUN npm --version
RUN node --version
RUN python -V
RUN pip --version
RUN java -version

RUN npm install -g gulp; npm install; gulp
RUN mvn assembly:assembly -DdescriptorId=jar-with-dependencies

RUN cp -r dist /md2pdf
RUN cp -r node_modules /md2pdf/node_modules
RUN cp target/pdfutils*dependencies.jar /md2pdf/pdfutils.jar

ENTRYPOINT ["node", "/md2pdf/Md2PdfConverter.js"]
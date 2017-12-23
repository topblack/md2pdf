#!/bin/bash
rm -rf dist
gulp
if [ $? != 0 ]; then
  exit 1
fi 
mvn clean assembly:assembly -DdescriptorId=jar-with-dependencies
if [ $? != 0 ]; then
  exit 2
fi 
mv target/pdfutils-*-jar-with-dependencies.jar dist/pdfutils.jar
if [ $? != 0 ]; then
  exit 3
fi 

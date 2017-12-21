@echo off
rd /S /Q dist
call gulp
call mvn assembly:assembly -DdescriptorId=jar-with-dependencies
copy /Y target\pdfutils-*-jar-with-dependencies.jar dist\
rename dist\pdfutils-*-jar-with-dependencies.jar pdfutils.jar
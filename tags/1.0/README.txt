Devoxx 2010 Schedule Application
written by Teemu Pöntelin / Vaadin Ltd / http://vaadin.com/teemu
theme design by Jouni Koivuviita / Vaadin Ltd / http://vaadin.com/jouni

About
=====

Vaadin application for displaying Devoxx 2010 schedule using the Devoxx
REST interface as the data source for schedule details.

More information about the REST interface:
http://www.devoxx.com/display/Devoxx2K10/Schedule+REST+interface



Build the application
=====================

Package the war by running the package-war Ant target. After the 
build is successful you should find the war in a newly created "dist"
directory.



Dependencies
============

The application has dependencies to the following libraries (see /WebContent/WEB-INF/lib):
	
Vaadin
 - UI framework for the application
 - http://vaadin.com/download

Vaadin Calendar (AGPL)
 - Calendar add-on for Vaadin
 - http://vaadin.com/addon/vaadin-calendar

TouchScroll
 - TouchScroll add-on to provide better touch device support
 - http://vaadin.com/addon/touchscroll

CustomField
 - CustomField add-on for Vaadin
 - http://vaadin.com/addon/customfield

Apache log4j
 - Logging service
 - http://logging.apache.org/log4j/
 
org.json
 - JSON handling for Java
 - http://json.org/
 
 
Also during the build there are following dependencies (see /build-lib):

GWT
 - Google Web Toolkit for the client-side compilation
 - http://code.google.com/webtoolkit/
 
XMLTask
 - For modifying the web.xml from Ant script
 - http://www.oopsconsultancy.com/software/xmltask/
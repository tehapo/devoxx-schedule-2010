Conference Calendar Application
code:           Teemu Pöntelin / Vaadin Ltd / http://vaadin.com/teemu
theme design:   Jouni Koivuviita / Vaadin Ltd / http://vaadin.com/jouni

About
=====

Vaadin application for displaying conference schedule using the Devoxx
REST interface as the data source for schedule details.

More information about the REST interface:
http://www.devoxx.com/display/Devoxx2K10/Schedule+REST+interface



Build the application
=====================

Package the war by running any of the package-xyz-war Ant targets. After the 
build is successful you should find a conference-calendar.war in a newly created "dist"
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
 - Note that this application uses a custom build of the add-on for better performance. Namely the "selected" 
   class name is added and removed on client-side removing the need to update the whole calendar. See the 
   calendar-class-name.patch file for the changes made.
   
CustomField
 - CustomField add-on for Vaadin
 - http://vaadin.com/addon/customfield

BrowserCookies
 - BrowserCookies add-on for Vaadin
 - http://vaadin.com/addon/browsercookies

GoogleAnalyticsTracker
 - GoogleAnalyticsTracker add-on for Vaadin
 - http://vaadin.com/addon/googleanalyticstracker

AddThis
 - AddThis add-on for Vaadin
 - http://vaadin.com/addon/addthis

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
 
YUI Compressor
 - For minimizing the CSS theme file
 - http://developer.yahoo.com/yui/compressor/
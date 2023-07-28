# Noterganizer

Noterganizer is a Java-based notekeeping and organization application.
The [server](server) is built with Spring Boot and MongoDB, and the
[client](client) uses JavaFx. Main features are Markdown formatting 
support and organizing notes in a tree-like structure. Noterganizer is 
a part of my [programming portfolio](https://datafox.me).

## Usage

Builds for the client can be downloaded from 
[Releases](https://github.com/melodicore/noterganizer/releases), or
alternatively you can download or clone the repository and use the
gradle task `run` to run the application or use jpackage to build 
an executable binary.

No pre-built binaries for the server exist because you have to provide
your own arbitrary secret token for the remember me functionality.
Download or clone the repository and copy the
[application.properties.copyme](server/src/main/resources/application.properties.copyme)
file to a file named 'application.properties' and edit the respective token 
and optionally other options as well. After these steps you can run the 
server with the `bootRun` gradle task, or build an executable jar file
with the `bootJar` gradle task. Please note that the server needs MongoDB 
running on the same host with default settings. Please adjust the
application.properties file appropriately.
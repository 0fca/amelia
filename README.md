# Amelia
Amelia is a simple monitoring system.

# Structure
Amelia has classical server-client scheme. It uses java.net API to transfer data via TCP.
Amelia system is built from 2 parts: Amelia Server GUI app and <a href="https://github.com/Obsidiam/ameliaclient">Amelia Client CLI app</a>.
Server app uses multithreading. 

App transfers images in JFIF format, resolution is 150x100. App loads all data(image and metadata) to an array which
length is 8192.

Integer value of first byte in data packet sent by server tells how many bytes counting from index 1 contains 
bytes of client's machhine name.

![AmeliaFrame structure](https://i.imgur.com/mrvX6So.png)

## Amelia Server

Amelia can load some settings from .xml file. 
It must be located in the working directory of server app, it is called: init.xml.
The settings recognized by Amelia Server at the moment:
ADDR - IP address to which server should be bound
PORT - port on which server should listen
XML_PATH - the absolute path to other .xml files.


**Note**: Only ADDR and PORT markups are loaded by Amelia, PORT is the one applied at server’s startup.

Actually server is unable to send settings to clients due to its settings panel is in development at the moment.

## Amelia Client

Client app is able to load settings from plain text file called: settings. It must be located in working directory of client app.
The file is supposed to load IP address and port number. 
For an instance, client app will load settings at startup, then it will auto connect to the server using given data. There is a possibility to load settings manually using ‘load’ command. To print list of commands with short description, just type: ‘help’ or ‘?’. In order of applying changes made to settings, the connection must be restarted.

Configuration is saved in two situations: 
when exiting,
when the “Connector Thread” is supposed to end its work.
There is no option to save it manually during client’s work. 

Server app is built using State pattern.

# Features done
Amelia has following features:

* transfers images of screens of every connected client,
* provides data for every single connection like transfer speed, client IP, port,
* simple, light UI,
* shows when started and when ended monitoring process.

# Incoming features
I want to add following features:

* managable connection settings,
* submission of settings via TCP to every client,
* improve design.

# Quick glance
![W10&WinXP connection](https://i.imgur.com/VESKEVR.png)

Amelia is receiving screen state from physical Linux machine running on Windows 10 x64.

![settings](http://i.imgur.com/ZCJGvvv.png)

Amelia's server settings window. (In progress)

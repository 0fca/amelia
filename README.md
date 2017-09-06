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

Server app is built using State pattern.

# Features done
Amelia has following features:

* transfers images of screens of every connected client,
* provides data for every single connection like transfer speed, client IP, port,
* simple, light UI.

# Incoming features
I want to add following features:

* managable connection settings,
* submission of settings via TCP to every client,
* improve design.

# Quick glance
![W10&WinXP connection](http://i.imgur.com/CcHi7FV.png)

Amelia is receiving screen state from VM on which Windows 10 x64 is installed. 

![settings](http://i.imgur.com/ZCJGvvv.png)

Amelia's server settings window. (In progress)

# Amelia
Amelia is a simple monitoring system.

# Structure
Amelia has classical server-client scheme. It uses java.net API to transfer data via TCP.
Amelia system is built from 2 parts: Amelia Server GUI app and Amelia Client CLI app.
Server app is uses multithreading. 

App transfers images in JFIF format, resolution is 150x100. App loads all data(image and metadata) to an array which
length is 8192.

Integer value of first byte in data packet sent by server tells how many bytes counting from index 1 contains 
bytes of client's machhine name.

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
![W10 VM](https://github.com/Obsidiam/amelia/blob/master/46e073183a721aabe4809f72afe561c0.0.png)

Amelia is receiving screen state from VM on which Windows 10 x64 is installed. 

![settings](https://github.com/Obsidiam/amelia/blob/master/46e073183a721aabe4809f72afe561c0.0.png)

Amelia's server settings window. (In progress)

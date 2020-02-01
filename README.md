# Amelia
Amelia is a simple system which monitors state of desktops of all hosts in LAN.

# Structure
Amelia has classical server-client scheme. It uses java.net API to transfer data via TCP.
Amelia system is built from 2 parts: Amelia Server app and <a href="https://github.com/Obsidiam/ameliaclient">Alice Client CLI app</a>.
Server app uses multithreading, however there are only 5 threads(os services) that are taking care of view updates, transmission etc. This design is new, the design looked like: one connection = one thread. It caused the memory&CPU usage to be high.
User system was built using RetroFit and ReactiveX for Java. Classical MVC architeconic pattern was implemented. 

App transfers images in JFIF format, resolution is 250x150. App loads all data(image and metadata) to an array which
length is 8192.

**_Important_**: Integer value of first byte in data packet sent by server tells how many bytes counting from index 1 contains bytes of client's machine name.

![Amelia's Frame](https://i.imgur.com/c7dQK9G.png)

## Amelia Server

Amelia can load some settings from .xml file. 
It must be located in the working directory of server app, it is called: init.xml.
The settings recognized by Amelia Server at the moment:
ADDR - IP address to which server should be bound
PORT - port on which server should listen
XML_PATH - the absolute path to other .xml files.


**Note**: Only ADDR and PORT markups are loaded by Amelia, PORT is the one applied at server’s startup.

Actually server is unable to send settings to clients due to its settings panel is in development at the moment.

## Alice

Client app is able to load settings from plain text file called: settings. It must be located in working directory of client app.
The file is supposed to load IP address and port number. 
For an instance, client app will load settings at startup, then it will auto connect to the server using given data. There is a possibility to load settings manually using ‘load’ command. To print list of commands with short description, just type: ‘help’ or ‘?’. In order of applying changes made to settings, the connection must be restarted.

Configuration is saved in two situations: 
* when exiting,
* when the “Connector Thread” is supposed to end its work.
To save settings use command "save".

Server app is built using State pattern.

# Features done
Amelia has following features:

* transfers images of screens of every connected client,
* provides data for every single connection like transfer speed, client IP, port,
* simple, light UI,
* shows when started and when ended monitoring process.
Amelia is compatible with:
* Windows 10
* Windows 8
* Windows 7
* Windows Vista
* Windows XP

Amelia should be compatible with all Linux OS's, but I am not sure.

**Amelia Server** was tested on:
* Windows 10,
* Windows 7,
* Windows XP,
* openSUSE 42.2 Leap,
* Mint 18.1.

**Alice** was tested on:
* Windows 10,
* Windows 7,
* Windows XP,
* openSUSE 42.2 Leap,
* Mint 18.1,
* Ubuntu 15.04.

# Incoming features
I want to add following features:

* managable connection settings,
* submission of settings via TCP(or UDP) to every client,
* add more settings like: port, time step,
* remote desktop(using UDP).

# Quick glance
All screens were done while running in environmental conditions, used JRE version is 1.8.144.

![Amelia working on Mint 18.1](https://i.imgur.com/t6zYIBT.png)

Amelia Server's main view.

![RAM usage serving 4 clients](https://i.imgur.com/k8GYjbs.png)

Amelia working on the loopback on Mint 18.1 with 4 clients connected. Image shows its RAM usage.

![1 client RAM usage](https://i.imgur.com/4Fjmj1E.png)

Amelia working on Mint 18.1 x64 with client connected from localhost. RAM usage is what thr images shows.
![settings](http://i.imgur.com/ZCJGvvv.png)

Amelia's server settings window. (In progress)


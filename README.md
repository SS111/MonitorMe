DISCONTINUED
============
This project has been discontinued due to a lack of time and much newer and easier methods to achieve what I was trying to do.






Introduction
============
Welcome, to MonitorMe. I wanted to be able to easily manage my server from an Android (or any device that supports UDP networking!) device. At first, it seemed like a dream, and it would be imposssible to make a successor to Minecraft MobileAdmin. A few weeks later, I started handcrafting that dream. Here I am now.

Information
===========
MontiorMe is an open-source Bukkit plugin for monitoring a Minecraft server via Android (can be expanded to other platforms). This is currently in pre-alpha and it not even close to being finished.

I started out by using TCP, but realized this wouldn't work as only one user can be connected at a time. I have now switched to UDP and multiple users can be "connected". 

However, to send the data to right players, I'm using a hacky way to iterate through the hash map, which really lags the server. You can see an example of this in the logging out portion of the core file. If anyone has a way to simplify this, please make a pull request :)

Help me!
========
However, on a much sadder note, I don't have time to develop this on my own. If anyone wants to help me out, please contact me. Or, just make a pull request, and if it's to my standards, I'll accept it.


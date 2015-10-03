[![Stories in Ready](https://badge.waffle.io/MSOE-Supermileage/DAQ.png?label=ready&title=Ready)](https://waffle.io/MSOE-Supermileage/DAQ)

### Overview ###

This project is for the Milwaukee School of Engineering SuperMileage Team, a subset of SAE. The goal is to build a functional data acquisition system for Prototype Electric and Gas Vehicles for compete in the [Shell Eco-Marathon](http://www.shell.com/global/environment-society/ecomarathon/events/americas.html) and [SAE Supermileage](http://students.sae.org/cds/supermileage/) collegiate design and engineering competitions.

The system consists of an android phone as a HUD (heads up display) USB Tethered to a [Raspberry PI](https://www.raspberrypi.org/) that collects data on various sensors connected to the GPIO.

Check out the [Task Board](https://waffle.io/MSOE-Supermileage/DAQ).

### Setup ###

#### Tools/Kits ####
* [IntelliJ IDEA](http://www.jetbrains.com/idea/)
* [Java Development Kit 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
* [Android SDK](https://developer.android.com/sdk/index.html)
* [Python2.7](https://docs.python.org/2/)
    
#### Configuration ####
* SDK Version 21 or later for android 5.0.1
* The android portion of the project is currently out of date. Deploy scripts are needed to be written to deploy to a clean install of raspbian, and code will need to be imported to github.
* The Web Server Project is a windows application. The connection between the heads up display and the web server is not currenlty functional.

# ForceFormController-TouchEvents
This repository contains what can be considered as a user interface and control mechanism for ForceForm, a prototype shape changing interface developed at the Australian National University. http://tsimeris.com/itspaper.pdf 

This repository contains what can be considered as a stripped down version of a user interface and control mechanism for ForceForm, a prototype shape changing interface developed at the Australian National University. http://tsimeris.com/itspaper.pdf. 

**REQUIRED ITEMS**: ForceForm hardware, Digital to analog converter and associated electronics, arduino with hall effect sensor attached, the MccdaqDigitalToAnalog and HallEffectSensor-Arduino repositories also supplied in this github account.

This project contains two user interfaces. One is the Hall effect sensor interface, and the other shows a 2D grid of electromagnets which can be directly clicked to alter the settings of the actual hardware electromagnets. The basic setup is that the project reads Hall effect sensor values delivered to the serial port via the arduino as per the HallEffectSensor-Arduino repository, and the 2D user interface sets a config file which is in turn read by the MccdaqDigitalToAnalog repository, enacting changes with the device via a connected Digital to Analog Converter. All required hardware is located at the Australian National University as of October 2015, under the supervision of Tom Gedeon.

The main goal of this repository is to "glue" it all together - sends values to the config file for MccdaqDigitalToAnalog to send to the Digital to Analog converter, and receive the Hall effect sensor values to visualise how much the surface is being pushed down. 

It is hoped that the other two related repositories (less so this one) would be useful as standalone pieces of software.

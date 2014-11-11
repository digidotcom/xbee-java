XBee Java Library
=================

This project contains the source code of the XBee Java Library, an easy to use 
API developed in Java that allows you to interact with Digi International's
[XBee](http://www.digi.com/xbee/) radio frequency (RF) modules.

The project includes the Java source code and unit tests for the library and 
multiple examples, also available in source code format, that show how to use 
the available APIs.

The main features provided by the library are:

* Support for ZigBee, 802.15.4, DigiMesh and Point-to-Multipoint XBee devices.
* Support for API and API Escaped operating modes.
* Management of local (attached to the PC) and remote XBee device objects.
* Discovery of remote XBee devices that are associated to the same network as 
the local one.
* Configuration of local and remote XBee devices:
  * Configure common parameters with specific setters and getters.
  * Configure any other parameter with generic methods.
  * Execute AT commands.
  * Apply configuration changes.
  * Write configuration changes.
  * Reset the device.
* Transmission of data to all the XBee devices of the network or to a specific 
one.
* Reception of data from remote XBee devices:
  * Data polling.
  * Data reception callback.
* Reception of network status changes related to the local XBee device.
* IO lines management:
  * Configure IO lines.
  * Set IO line value.
  * Read IO line value.
  * Receive IO data samples from any remote XBee device of the network.


Start Here
----------
The best place to get started is the 
[XBee Java Library documentation](http://confluence.digi.com/display/XBJLIB).


How to Contribute
-----------------
The contributing guidelines are in the 
[CONTRIBUTING.md](https://github.com/digidotcom/XBeeJavaLibrary/blob/master/CONTRIBUTING.md) 
document.


License
-------
Copyright 2014 Digi International Inc. This software is open-source software. 

This Source Code Form is subject to the terms of the Mozilla Public
License, v. 2.0. If a copy of the MPL was not distributed with this file,
You can obtain one at http://mozilla.org/MPL/2.0/.

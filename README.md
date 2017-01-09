XBee Java Library
=================

This project contains the source code of the XBee Java Library, an easy-to-use 
API developed in Java that allows you to interact with Digi International's
[XBee](http://www.digi.com/xbee/) radio frequency (RF) modules. This source has 
been contributed by [Digi International](http://www.digi.com).

The project includes the Java source code, unit tests for the library, and 
multiple examples that show how to use the available APIs. The examples are
also available in source code format.

The main features of the library include:

* Support for ZigBee, 802.15.4, DigiMesh, Point-to-Multipoint, Wi-Fi and 
Cellular XBee devices.
* Support for API and API escaped operating modes.
* Support for Android.
* Management of local (attached to the PC) and remote XBee device objects.
* Discovery of remote XBee devices associated with the same network as the 
local device.
* Configuration of local and remote XBee devices:
  * Configure common parameters with specific setters and getters.
  * Configure any other parameter with generic methods.
  * Execute AT commands.
  * Apply configuration changes.
  * Write configuration changes.
  * Reset the device.
* Transmission of data to all the XBee devices on the network or to a specific 
device.
* Reception of data from remote XBee devices:
  * Data polling.
  * Data reception callback.
* Transmission and reception of IP and SMS messages.
* Reception of network status changes related to the local XBee device.
* IO lines management:
  * Configure IO lines.
  * Set IO line value.
  * Read IO line value.
  * Receive IO data samples from any remote XBee device on the network.
* Support for explicit frames and application layer fields (Source endpoint, 
Destination endpoint, Profile ID, and Cluster ID).


Start Here
----------
The best place to get started is the 
[XBee Java Library documentation](http://www.digi.com/resources/documentation/digidocs/90001438/Default.htm).


How to Contribute
-----------------
The contributing guidelines are in the 
[CONTRIBUTING.md](https://github.com/digidotcom/XBeeJavaLibrary/blob/master/CONTRIBUTING.md) 
document.


License
-------
Copyright 2017, Digi International Inc.

This Source Code Form is subject to the terms of the Mozilla Public
License, v. 2.0. If a copy of the MPL was not distributed with this
file, you can obtain one at http://mozilla.org/MPL/2.0/.
 
THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES 
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR 
ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES 
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN 
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF 
OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

### Licensing terms for RXTX

GNU Lesser General Public License as published by the Free Software Foundation; 
either version 2.1 of the License, or (at your option) any later version.

See http://www.gnu.org/licenses/lgpl.html

### Licensing terms for SLF4J

SLF4J source code and binaries are distributed under the MIT license.

See http://www.slf4j.org/license.html


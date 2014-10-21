/**
* Copyright (c) 2014 Digi International Inc.,
* All rights not expressly granted are reserved.
*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/.
*
* Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
* =======================================================================
*/
package com.digi.xbee.api.connection.serial;

/**
 * Helper class used to store port information.
 */
public class SerialPortInfo {
	
	// Variables
	private String portName;
	private String portDescription;
	
	/**
	 * Class constructor. Instances a new serial port info object with the 
	 * given parameters.
	 * 
	 * @param portName Name of the port.
	 */
	public SerialPortInfo(String portName) {
		this(portName, null);
	}
	
	/**
	 * Class constructor. Instances a new serial port info object with the 
	 * given parameters.
	 * 
	 * @param portName Name of the port.
	 * @param portDescription Description of the port.
	 */
	public SerialPortInfo(String portName, String portDescription) {
		this.portName = portName;
		this.portDescription = portDescription;
	}
	
	/**
	 * Retrieves the port name.
	 * 
	 * @return The port name.
	 */
	public String getPortName() {
		return portName;
	}
	
	/**
	 * Retrieves the serial port description.
	 * 
	 * @return The serial port description.
	 */
	public String getPortDescription() {
		return portDescription;
	}
	
	/**
	 * Sets the serial port description.
	 * 
	 * @param portDescription The serial port description.
	 */
	public void setPortDescription(String portDescription) {
		this.portDescription = portDescription;
	}
}

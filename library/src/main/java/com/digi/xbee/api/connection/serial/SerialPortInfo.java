/**
 * Copyright 2017, Digi International Inc.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES 
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR 
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES 
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN 
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF 
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.digi.xbee.api.connection.serial;

/**
 * Helper class used to store serial port information.
 */
public class SerialPortInfo {
	
	// Variables.
	private String portName;
	private String portDescription;
	
	/**
	 * Class constructor. Instantiates a new {@code SerialPortInfo} object with
	 * the given parameters.
	 * 
	 * @param portName Name of the port.
	 * 
	 * @throws NullPointerException if {@code portName == null}.
	 * 
	 * @see #SerialPortInfo(String, String)
	 */
	public SerialPortInfo(String portName) {
		this(portName, null);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code SerialPortInfo} object with
	 * the given parameters.
	 * 
	 * @param portName Name of the port.
	 * @param portDescription Description of the port. It may be {@code null}.
	 * 
	 * @throws NullPointerException if {@code portName == null}.
	 * 
	 * @see #SerialPortInfo(String)
	 */
	public SerialPortInfo(String portName, String portDescription) {
		if (portName == null)
			throw new NullPointerException("Serial port name cannot be null.");
		
		this.portName = portName;
		this.portDescription = portDescription;
	}
	
	/**
	 * Returns the serial port name.
	 * 
	 * @return The serial port name.
	 * 
	 * @see #getPortDescription()
	 */
	public String getPortName() {
		return portName;
	}
	
	/**
	 * Returns the serial port description.
	 * 
	 * @return The serial port description. It may be {@code null}.
	 * 
	 * @see #getPortName()
	 */
	public String getPortDescription() {
		return portDescription;
	}
	
	/**
	 * Sets the serial port description.
	 * 
	 * @param portDescription The serial port description.
	 * 
	 * @see #getPortDescription()
	 */
	public void setPortDescription(String portDescription) {
		this.portDescription = portDescription;
	}
}

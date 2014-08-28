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
package com.digi.xbee.api;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;

public class Raw802Device extends XBeeDevice {

	/**
	 * Class constructor. Instantiates a new {@code Raw802Device} object in the 
	 * given port name and baud rate.
	 * 
	 * @param port Serial port name where 802.15.4 device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device. 
	 *                 Other connection parameters will be set as default (8 
	 *                 data bits, 1 stop bit, no parity, no flow control).
	 * 
	 * @throws NullPointerException if {@code port == null}.
	 * @throws IllegalArgumentException if {@code baudRate < 0}.
	 */
	public Raw802Device(String port, int baudRate) {
		this(XBee.createConnectiontionInterface(port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code Raw802Device} object in the 
	 * given serial port name and settings.
	 * 
	 * @param port Serial port name where 802.15.4 device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device.
	 * @param dataBits Serial port data bits.
	 * @param stopBits Serial port data bits.
	 * @param parity Serial port data bits.
	 * @param flowControl Serial port data bits.
	 * 
	 * @throws NullPointerException if {@code port == null}.
	 * @throws IllegalArgumentException if {@code baudRate < 0} or
	 *                                  if {@code dataBits < 0} or
	 *                                  if {@code stopBits < 0} or
	 *                                  if {@code parity < 0} or
	 *                                  if {@code flowControl < 0}.
	 */
	public Raw802Device(String port, int baudRate, int dataBits, int stopBits, int parity, int flowControl) {
		this(port, new SerialPortParameters(baudRate, dataBits, stopBits, parity, flowControl));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code Raw802Device} object in the 
	 * given serial port name and parameters.
	 * 
	 * @param port Serial port name where 802.15.4 device is attached to.
	 * @param serialPortParameters Object containing the serial port parameters.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code serialPortParameters == null}.
	 * 
	 * @see SerialPortParameters
	 */
	public Raw802Device(String port, SerialPortParameters serialPortParameters) {
		this(XBee.createConnectiontionInterface(port, serialPortParameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code Raw802Device} object with the 
	 * given connection interface.
	 * 
	 * @param connectionInterface The connection interface with the physical 
	 *                            802.15.4 device.
	 * 
	 * @throws NullPointerException if {@code connectionInterface == null}
	 * 
	 * @see IConnectionInterface
	 */
	public Raw802Device(IConnectionInterface connectionInterface) {
		super(connectionInterface);
	}
	
	/**
	 * Class constructor. Instantiates a new remote {@code Raw802Device} object 
	 * with the given local {@code Raw802Device} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local 802.15.4 device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote 802.15.4 device
	 * @param xbee64BitAddress The 64-bit address to identify this remote 802.15.4 
	 *                         device.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code xbee64BitAddress == null}.
	 * 
	 * @see XBee64BitAddress
	 */
	public Raw802Device(XBeeDevice localXBeeDevice, XBee64BitAddress xbee64BitAddress) {
		super(localXBeeDevice, xbee64BitAddress);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#getXBeeProtocol()
	 */
	@Override
	public XBeeProtocol getXBeeProtocol() {
		return XBeeProtocol.RAW_802_15_4;
	}
}

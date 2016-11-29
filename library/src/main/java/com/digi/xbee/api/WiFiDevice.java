/**
 * Copyright (c) 2016 Digi International Inc.,
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
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeDeviceException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.WiFiAssociationIndicationStatus;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.utils.ByteUtils;

/**
 * This class represents a local Wi-Fi device.
 * 
 * @see XBeeDevice
 * @see CellularDevice
 * @see DigiMeshDevice
 * @see DigiPointDevice
 * @see Raw802Device
 * @see ZigBeeDevice
 */
public class WiFiDevice extends IPDevice {
	
	/**
	 * Class constructor. Instantiates a new {@code WiFiDevice} object in 
	 * the given port name and baud rate.
	 * 
	 * @param port Serial port name where Wi-Fi device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device. 
	 *                 Other connection parameters will be set as default (8 
	 *                 data bits, 1 stop bit, no parity, no flow control).
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see #WiFiDevice(IConnectionInterface)
	 * @see #WiFiDevice(String, SerialPortParameters)
	 * @see #WiFiDevice(String, int, int, int, int, int)
	 */
	public WiFiDevice(String port, int baudRate) {
		this(XBee.createConnectiontionInterface(port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code WiFiDevice} object in 
	 * the given serial port name and settings.
	 * 
	 * @param port Serial port name where Wi-Fi device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device.
	 * @param dataBits Serial port data bits.
	 * @param stopBits Serial port data bits.
	 * @param parity Serial port data bits.
	 * @param flowControl Serial port data bits.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 0} or
	 *                                  if {@code dataBits < 0} or
	 *                                  if {@code stopBits < 0} or
	 *                                  if {@code parity < 0} or
	 *                                  if {@code flowControl < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see #WiFiDevice(IConnectionInterface)
	 * @see #WiFiDevice(String, int)
	 * @see #WiFiDevice(String, SerialPortParameters)
	 */
	public WiFiDevice(String port, int baudRate, int dataBits, int stopBits, int parity, int flowControl) {
		this(port, new SerialPortParameters(baudRate, dataBits, stopBits, parity, flowControl));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code WiFiDevice} object in 
	 * the given serial port name and parameters.
	 * 
	 * @param port Serial port name where Wi-Fi device is attached to.
	 * @param serialPortParameters Object containing the serial port parameters.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code serialPortParameters == null}.
	 * 
	 * @see #WiFiDevice(IConnectionInterface)
	 * @see #WiFiDevice(String, int)
	 * @see #WiFiDevice(String, int, int, int, int, int)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	public WiFiDevice(String port, SerialPortParameters serialPortParameters) {
		this(XBee.createConnectiontionInterface(port, serialPortParameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code WiFiDevice} object with 
	 * the given connection interface.
	 * 
	 * @param connectionInterface The connection interface with the physical 
	 *                            Wi-Fi device.
	 * 
	 * @throws NullPointerException if {@code connectionInterface == null}
	 * 
	 * @see #WiFiDevice(String, int)
	 * @see #WiFiDevice(String, SerialPortParameters)
	 * @see #WiFiDevice(String, int, int, int, int, int)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 */
	public WiFiDevice(IConnectionInterface connectionInterface) {
		super(connectionInterface);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#open()
	 */
	@Override
	public void open() throws XBeeException {
		super.open();
		if (xbeeProtocol != XBeeProtocol.XBEE_WIFI)
			throw new XBeeDeviceException("XBee device is not a " + getXBeeProtocol().getDescription() + " device, it is a " + xbeeProtocol.getDescription() + " device.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#getXBeeProtocol()
	 */
	@Override
	public XBeeProtocol getXBeeProtocol() {
		return XBeeProtocol.XBEE_WIFI;
	}
	
	/**
	 * Returns the current association status of this Wi-Fi device.
	 * 
	 * <p>It indicates occurrences of errors during the Wi-Fi transceiver 
	 * initialization and connection.</p>
	 * 
	 * @return The association indication status of the Wi-Fi device.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout getting the association 
	 *                          indication status.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see com.digi.xbee.api.models.Wi-FiAssociationIndicationStatus
	 */
	public WiFiAssociationIndicationStatus getWiFiAssociationIndicationStatus() throws TimeoutException, 
			XBeeException {
		byte[] associationIndicationValue = getParameter("AI");
		return WiFiAssociationIndicationStatus.get(ByteUtils.byteArrayToInt(associationIndicationValue));
	}
}

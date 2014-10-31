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
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeDeviceException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.AssociationIndicationStatus;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;

/**
 * This class represents a local ZigBee device.
 * 
 * @see XBeeDevice
 * @see DigiMeshDevice
 * @see DigiPointDevice
 * @see Raw802Device
 */
public class ZigBeeDevice extends XBeeDevice {

	/**
	 * Class constructor. Instantiates a new {@code ZigBeeDevice} object in the 
	 * given port name and baud rate.
	 * 
	 * @param port Serial port name where ZigBee device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device. 
	 *                 Other connection parameters will be set as default (8 
	 *                 data bits, 1 stop bit, no parity, no flow control).
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 */
	public ZigBeeDevice(String port, int baudRate) {
		this(XBee.createConnectiontionInterface(port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code ZigBeeDevice} object in the 
	 * given serial port name and settings.
	 * 
	 * @param port Serial port name where ZigBee device is attached to.
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
	 */
	public ZigBeeDevice(String port, int baudRate, int dataBits, int stopBits, int parity, int flowControl) {
		this(port, new SerialPortParameters(baudRate, dataBits, stopBits, parity, flowControl));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code ZigBeeDevice} object in the 
	 * given serial port name and parameters.
	 * 
	 * @param port Serial port name where ZigBee device is attached to.
	 * @param serialPortParameters Object containing the serial port parameters.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code serialPortParameters == null}.
	 * 
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	public ZigBeeDevice(String port, SerialPortParameters serialPortParameters) {
		this(XBee.createConnectiontionInterface(port, serialPortParameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code ZigBeeDevice} object with the 
	 * given connection interface.
	 * 
	 * @param connectionInterface The connection interface with the physical 
	 *                            ZigBee device.
	 * 
	 * @throws NullPointerException if {@code connectionInterface == null}
	 * 
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 */
	public ZigBeeDevice(IConnectionInterface connectionInterface) {
		super(connectionInterface);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#open()
	 */
	@Override
	public void open() throws XBeeException {
		super.open();
		if (xbeeProtocol != XBeeProtocol.ZIGBEE)
			throw new XBeeDeviceException("XBee device is not a " + getXBeeProtocol().getDescription() + " device, it is a " + xbeeProtocol.getDescription() + " device.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#getNetwork()
	 */
	@Override
	public XBeeNetwork getNetwork() {
		if (!isOpen())
			throw new InterfaceNotOpenException();
		if (network == null)
			network = new ZigBeeNetwork(this);
		return network;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#getXBeeProtocol()
	 */
	@Override
	public XBeeProtocol getXBeeProtocol() {
		return XBeeProtocol.ZIGBEE;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#sendDataAsync(com.digi.xbee.api.models.XBee64BitAddress, com.digi.xbee.api.models.XBee16BitAddress, byte[])
	 */
	@Override
	public void sendDataAsync(XBee64BitAddress address64Bit, XBee16BitAddress address16bit, byte[] data) throws XBeeException {
		super.sendDataAsync(address64Bit, address16bit, data);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#sendData(com.digi.xbee.api.models.XBee64BitAddress, com.digi.xbee.api.models.XBee16BitAddress, byte[])
	 */
	@Override
	public void sendData(XBee64BitAddress address64Bit, XBee16BitAddress address16bit, byte[] data) throws TimeoutException, XBeeException {
		super.sendData(address64Bit, address16bit, data);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#getAssociationIndicationStatus()
	 */
	@Override
	public AssociationIndicationStatus getAssociationIndicationStatus() throws TimeoutException, XBeeException {
		return super.getAssociationIndicationStatus();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#forceDisassociate()
	 */
	@Override
	public void forceDisassociate() throws TimeoutException, XBeeException {
		super.forceDisassociate();
	}
}

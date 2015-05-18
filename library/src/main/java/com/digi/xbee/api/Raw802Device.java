/**
 * Copyright (c) 2014-2015 Digi International Inc.,
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
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeDeviceException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.AssociationIndicationStatus;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.models.XBeeTransmitOptions;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.raw.TX16Packet;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a local 802.15.4 device.
 * 
 * @see XBeeDevice
 * @see DigiMeshDevice
 * @see DigiPointDevice
 * @see ZigBeeDevice
 */
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
	 * @throws IllegalArgumentException if {@code baudRate < 0}.
	 * @throws NullPointerException if {@code port == null}.
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
	 * @throws IllegalArgumentException if {@code baudRate < 0} or
	 *                                  if {@code dataBits < 0} or
	 *                                  if {@code stopBits < 0} or
	 *                                  if {@code parity < 0} or
	 *                                  if {@code flowControl < 0}.
	 * @throws NullPointerException if {@code port == null}.
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
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#open()
	 */
	@Override
	public void open() throws XBeeException {
		super.open();
		if (isRemote())
			return;
		if (xbeeProtocol != XBeeProtocol.RAW_802_15_4)
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
			network = new Raw802Network(this);
		return network;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#getXBeeProtocol()
	 */
	@Override
	public XBeeProtocol getXBeeProtocol() {
		return XBeeProtocol.RAW_802_15_4;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#sendDataAsync(com.digi.xbee.api.models.XBee64BitAddress, byte[])
	 */
	@Override
	public void sendDataAsync(XBee64BitAddress address, byte[] data) throws XBeeException {
		super.sendDataAsync(address, data);
	}
	
	/**
	 * Sends the provided data to the XBee device of the network corresponding 
	 * to the given 16-bit address asynchronously.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote 
	 * device or for transmit status packet</p>
	 * 
	 * @param address The 16-bit address of the XBee that will receive the data.
	 * @param data Byte array containing data to be sent.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code address == null} or 
	 *                              if {@code data == null}.
	 * @throws XBeeException if there is any XBee related exception.
	 * 
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 * @see #sendData(RemoteXBeeDevice, byte[])
	 * @see #sendData(XBee16BitAddress, byte[])
	 * @see #sendData(XBee64BitAddress, byte[])
	 * @see #sendDataAsync(RemoteXBeeDevice, byte[])
	 * @see #sendDataAsync(XBee64BitAddress, byte[])
	 */
	public void sendDataAsync(XBee16BitAddress address, byte[] data) throws XBeeException {
		// Verify the parameters are not null, if they are null, throw an exception.
		if (address == null)
			throw new NullPointerException("Address cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send data to a remote device from a remote device.");
		
		logger.info(toString() + "Sending data asynchronously to {} >> {}.", address, HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket = new TX16Packet(getNextFrameID(), address, XBeeTransmitOptions.NONE, data);
		sendAndCheckXBeePacket(xbeePacket, true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#sendData(com.digi.xbee.api.models.XBee64BitAddress, byte[])
	 */
	@Override
	public void sendData(XBee64BitAddress address, byte[] data) throws TimeoutException, XBeeException {
		super.sendData(address, data);
	}
	
	/**
	 * Sends the provided data to the XBee device of the network corresponding 
	 * to the given 16-bit address.
	 * 
	 * <p>This method blocks until a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The received timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations use the method 
	 * {@link #sendData(XBee16BitAddress, byte[])}.</p>
	 * 
	 * @param address The 16-bit address of the XBee that will receive the data.
	 * @param data Byte array containing data to be sent.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code address == null} or 
	 *                              if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 * @see XBeeDevice#getReceiveTimeout()
	 * @see XBeeDevice#setReceiveTimeout(int)
	 * @see #sendData(RemoteXBeeDevice, byte[])
	 * @see #sendData(XBee64BitAddress, byte[])
	 * @see #sendDataAsync(RemoteXBeeDevice, byte[])
	 * @see #sendDataAsync(XBee16BitAddress, byte[])
	 * @see #sendDataAsync(XBee64BitAddress, byte[])
	 */
	public void sendData(XBee16BitAddress address, byte[] data) throws TimeoutException, XBeeException {
		// Verify the parameters are not null, if they are null, throw an exception.
		if (address == null)
			throw new NullPointerException("Address cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send data to a remote device from a remote device.");
		
		logger.info(toString() + "Sending data to {} >> {}.", address, HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket = new TX16Packet(getNextFrameID(), address, XBeeTransmitOptions.NONE, data);
		sendAndCheckXBeePacket(xbeePacket, false);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#set16BitAddress(com.digi.xbee.api.models.XBee16BitAddress)
	 */
	@Override
	public void set16BitAddress(XBee16BitAddress xbee16BitAddress) throws TimeoutException, XBeeException {
		super.set16BitAddress(xbee16BitAddress);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#getAssociationIndicationStatus()
	 */
	@Override
	public AssociationIndicationStatus getAssociationIndicationStatus() throws TimeoutException, XBeeException {
		return super.getAssociationIndicationStatus();
	}
}

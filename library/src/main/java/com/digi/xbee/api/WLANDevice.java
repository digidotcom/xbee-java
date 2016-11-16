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
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;
import com.digi.xbee.api.models.IP32BitAddress;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;

/**
 * This class provides common functionality for XBee WLAN devices.
 * 
 * @see CellularDevice
 */
public class WLANDevice extends XBeeDevice {

	// Constants
	private static final String OPERATION_EXCEPTION = "Operation not supported in this module.";
	
	// Variables
	protected IP32BitAddress ipAddress;
	
	/**
	 * Class constructor. Instantiates a new {@code WLANDevice} object in 
	 * the given port name and baud rate.
	 * 
	 * @param port Serial port name where WLAN device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device. 
	 *                 Other connection parameters will be set as default (8 
	 *                 data bits, 1 stop bit, no parity, no flow control).
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see #WLANDevice(IConnectionInterface)
	 * @see #WLANDevice(String, SerialPortParameters)
	 * @see #WLANDevice(String, int, int, int, int, int)
	 */
	protected WLANDevice(String port, int baudRate) {
		this(XBee.createConnectiontionInterface(port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code WLANDevice} object in 
	 * the given serial port name and settings.
	 * 
	 * @param port Serial port name where WLAN device is attached to.
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
	 * @see #WLANDevice(IConnectionInterface)
	 * @see #WLANDevice(String, int)
	 * @see #WLANDevice(String, SerialPortParameters)
	 */
	protected WLANDevice(String port, int baudRate, int dataBits, int stopBits, int parity, int flowControl) {
		this(port, new SerialPortParameters(baudRate, dataBits, stopBits, parity, flowControl));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code WLANDevice} object in 
	 * the given serial port name and parameters.
	 * 
	 * @param port Serial port name where WLAN device is attached to.
	 * @param serialPortParameters Object containing the serial port parameters.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code serialPortParameters == null}.
	 * 
	 * @see #WLANDevice(IConnectionInterface)
	 * @see #WLANDevice(String, int)
	 * @see #WLANDevice(String, int, int, int, int, int)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	protected WLANDevice(String port, SerialPortParameters serialPortParameters) {
		this(XBee.createConnectiontionInterface(port, serialPortParameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code WLANDevice} object with 
	 * the given connection interface.
	 * 
	 * @param connectionInterface The connection interface with the physical 
	 *                            WLAN device.
	 * 
	 * @throws NullPointerException if {@code connectionInterface == null}
	 * 
	 * @see #WLANDevice(String, int)
	 * @see #WLANDevice(String, SerialPortParameters)
	 * @see #WLANDevice(String, int, int, int, int, int)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 */
	protected WLANDevice(IConnectionInterface connectionInterface) {
		super(connectionInterface);
	}
	
	
	/**
	 * @deprecated This protocol does not support the network functionality.
	 */
	@Override
	public XBeeNetwork getNetwork() {
		// WLAN modules do not have a network of devices.
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#readDeviceInfo()
	 */
	@Override
	public void readDeviceInfo() throws TimeoutException, XBeeException {
		super.readDeviceInfo();
		
		// Read the module's IP address.
		byte[] response = getParameter("MY");
		ipAddress = new IP32BitAddress(response);
	}
	
	/**
	 * Returns the IP address of this WLAN device.
	 * 
	 * <p>To refresh this value use the {@link #readDeviceInfo()} method.</p>
	 * 
	 * @return The IP address of this WLAN device.
	 * 
	 * @see com.digi.xbee.api.models.IP23BitAddress
	 */
	public IP32BitAddress getIPAddress() {
		return ipAddress;
	}
	
	/**
	 * @deprecated This protocol does not have an associated 16-bit address.
	 */
	@Override
	public XBee16BitAddress get16BitAddress() {
		// WLAN modules do not have 16-bit address.
		return null;
	}
	
	/**
	 * @deprecated This protocol does not have not have a destination address. 
	 *             This method will raise an 
	 *             {@link UnsupportedOperationException}.
	 */
	@Override
	public XBee64BitAddress getDestinationAddress() throws TimeoutException,
			XBeeException {
		// Not supported in WLAN modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated This protocol does not have not have a destination address. 
	 *             This method will raise an 
	 *             {@link UnsupportedOperationException}.
	 */
	@Override
	public void setDestinationAddress(XBee64BitAddress xbee64BitAddress)
			throws TimeoutException, XBeeException {
		// Not supported in WLAN modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public byte[] getPANID() throws TimeoutException, XBeeException {
		// Not supported in WLAN modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void setPANID(byte[] panID) throws TimeoutException, XBeeException {
		// Not supported in WLAN modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void addDataListener(IDataReceiveListener listener) {
		// Not supported in WLAN modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void removeDataListener(IDataReceiveListener listener) {
		// Not supported in WLAN modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void addIOSampleListener(IIOSampleReceiveListener listener) {
		// Not supported in WLAN modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void removeIOSampleListener(IIOSampleReceiveListener listener) {
		// Not supported in WLAN modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public XBeeMessage readData() {
		// Not supported in WLAN modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public XBeeMessage readData(int timeout) {
		// Not supported in WLAN modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public XBeeMessage readDataFrom(RemoteXBeeDevice remoteXBeeDevice) {
		// Not supported in WLAN modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public XBeeMessage readDataFrom(RemoteXBeeDevice remoteXBeeDevice,
			int timeout) {
		// Not supported in WLAN modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void sendBroadcastData(byte[] data) throws TimeoutException,
			XBeeException {
		// Not supported in WLAN modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void sendData(RemoteXBeeDevice remoteXBeeDevice, byte[] data)
			throws TimeoutException, XBeeException {
		// Not supported in WLAN modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void sendDataAsync(RemoteXBeeDevice remoteXBeeDevice, byte[] data)
			throws XBeeException {
		// Not supported in WLAN modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
}

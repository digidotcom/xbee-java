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
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;
import com.digi.xbee.api.listeners.INetworkDataReceiveListener;
import com.digi.xbee.api.models.IP32BitAddress;
import com.digi.xbee.api.models.NetworkMessage;
import com.digi.xbee.api.models.NetworkProtocol;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.models.XBeePacketsQueue;
import com.digi.xbee.api.models.XBeeTransmitOptions;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.network.RXIPv4Packet;
import com.digi.xbee.api.packet.network.TXIPv4Packet;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class provides common functionality for XBee IP devices.
 * 
 * @see CellularDevice
 * @see WiFiDevice
 */
public class IPDevice extends XBeeDevice {

	// Constants
	private static final String OPERATION_EXCEPTION = "Operation not supported in this module.";
	
	protected static final short DEFAULT_SOURCE_PORT = 9750;
	
	protected static final NetworkProtocol DEFAULT_PROTOCOL = NetworkProtocol.TCP;
	
	// Variables
	protected IP32BitAddress ipAddress;
	
	protected int sourcePort = DEFAULT_SOURCE_PORT;
	
	/**
	 * Class constructor. Instantiates a new {@code IPDevice} object in 
	 * the given port name and baud rate.
	 * 
	 * @param port Serial port name where IP device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device. 
	 *                 Other connection parameters will be set as default (8 
	 *                 data bits, 1 stop bit, no parity, no flow control).
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see #IPDevice(IConnectionInterface)
	 * @see #IPDevice(String, SerialPortParameters)
	 * @see #IPDevice(String, int, int, int, int, int)
	 */
	protected IPDevice(String port, int baudRate) {
		this(XBee.createConnectiontionInterface(port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code IPDevice} object in 
	 * the given serial port name and settings.
	 * 
	 * @param port Serial port name where IP device is attached to.
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
	 * @see #IPDevice(IConnectionInterface)
	 * @see #IPDevice(String, int)
	 * @see #IPDevice(String, SerialPortParameters)
	 */
	protected IPDevice(String port, int baudRate, int dataBits, int stopBits, int parity, int flowControl) {
		this(port, new SerialPortParameters(baudRate, dataBits, stopBits, parity, flowControl));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code IPDevice} object in 
	 * the given serial port name and parameters.
	 * 
	 * @param port Serial port name where IP device is attached to.
	 * @param serialPortParameters Object containing the serial port parameters.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code serialPortParameters == null}.
	 * 
	 * @see #IPDevice(IConnectionInterface)
	 * @see #IPDevice(String, int)
	 * @see #IPDevice(String, int, int, int, int, int)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	protected IPDevice(String port, SerialPortParameters serialPortParameters) {
		this(XBee.createConnectiontionInterface(port, serialPortParameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code IPDevice} object with 
	 * the given connection interface.
	 * 
	 * @param connectionInterface The connection interface with the physical 
	 *                            IP device.
	 * 
	 * @throws NullPointerException if {@code connectionInterface == null}
	 * 
	 * @see #IPDevice(String, int)
	 * @see #IPDevice(String, SerialPortParameters)
	 * @see #IPDevice(String, int, int, int, int, int)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 */
	protected IPDevice(IConnectionInterface connectionInterface) {
		super(connectionInterface);
	}
	
	
	/**
	 * @deprecated This protocol does not support the network functionality.
	 */
	@Override
	public XBeeNetwork getNetwork() {
		// IP modules do not have a network of devices.
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
		// Read the source port.
		try {
			response = getParameter("C0");
			sourcePort = ByteUtils.byteArrayToInt(response);
		} catch (TimeoutException e) {
			// Do not refresh the source port value if there is an error reading
			// it from the module.
		} catch (XBeeException e) {
			// Do not refresh the source port value if there is an error reading
			// it from the module.
		}
	}
	
	/**
	 * Returns the IP address of this IP device.
	 * 
	 * <p>To refresh this value use the {@link #readDeviceInfo()} method.</p>
	 * 
	 * @return The IP address of this IP device.
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
		// IP modules do not have 16-bit address.
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
		// Not supported in IP modules.
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
		// Not supported in IP modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public byte[] getPANID() throws TimeoutException, XBeeException {
		// Not supported in IP modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void setPANID(byte[] panID) throws TimeoutException, XBeeException {
		// Not supported in IP modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void addDataListener(IDataReceiveListener listener) {
		// Not supported in IP modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void removeDataListener(IDataReceiveListener listener) {
		// Not supported in IP modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void addIOSampleListener(IIOSampleReceiveListener listener) {
		// Not supported in IP modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void removeIOSampleListener(IIOSampleReceiveListener listener) {
		// Not supported in IP modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public XBeeMessage readData() {
		// Not supported in IP modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public XBeeMessage readData(int timeout) {
		// Not supported in IP modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public XBeeMessage readDataFrom(RemoteXBeeDevice remoteXBeeDevice) {
		// Not supported in IP modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public XBeeMessage readDataFrom(RemoteXBeeDevice remoteXBeeDevice,
			int timeout) {
		// Not supported in IP modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void sendBroadcastData(byte[] data) throws TimeoutException,
			XBeeException {
		// Not supported in IP modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void sendData(RemoteXBeeDevice remoteXBeeDevice, byte[] data)
			throws TimeoutException, XBeeException {
		// Not supported in IP modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void sendDataAsync(RemoteXBeeDevice remoteXBeeDevice, byte[] data)
			throws XBeeException {
		// Not supported in IP modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#addNetworkDataListener(com.digi.xbee.api.listeners.INetworkDataReceiveListener)
	 */
	@Override
	public void addNetworkDataListener(INetworkDataReceiveListener listener) {
		super.addNetworkDataListener(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#removeNetworkDataListener(com.digi.xbee.api.listeners.INetworkDataReceiveListener)
	 */
	@Override
	public void removeNetworkDataListener(INetworkDataReceiveListener listener) {
		super.removeNetworkDataListener(listener);
	}
	
	/**
	 * Starts listening for incoming network transmissions in the provided port.
	 * 
	 * @param sourcePort Port to listen for incoming transmissions.
	 * 
	 * @throws IllegalArgumentException if {@code sourcePort < 0} or 
	 *                                  if {@code sourcePort > 65535}.
	 * @throws TimeoutException if there is a timeout setting the source port.
	 * @throws XBeeException if there is any error setting the source port.
	 * 
	 * @see #stopListening()
	 */
	public void startListening(int sourcePort) throws TimeoutException, XBeeException {
		if (sourcePort < 0 || sourcePort > 65535)
			throw new IllegalArgumentException("Source port must be between 0 and 65535.");
		
		setParameter("C0", ByteUtils.shortToByteArray((short)sourcePort));
		this.sourcePort = sourcePort;
	}
	
	/**
	 * Stops listening for incoming network transmissions.
	 * 
	 * @throws TimeoutException if there is a timeout processing the operation.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #startListening(int)
	 */
	public void stopListening() throws TimeoutException, XBeeException {
		setParameter("C0", ByteUtils.shortToByteArray((short)0));
		sourcePort = 0;
	}
	
	/**
	 * Sends the provided network data to the given IP address and port using 
	 * the specified network protocol. For TCP and TCP SSL protocols, you can 
	 * also indicate if the socket should be closed when data is sent.
	 * 
	 * <p>This method blocks till a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations use the method 
	 * {@link #sendNetworkDataAsync(IP32BitAddress, byte[])}.</p>
	 * 
	 * @param ipAddress The IP address to send network data to.
	 * @param destPort The destination port of the transmission.
	 * @param protocol The network protocol used for the transmission.
	 * @param closeSocket {@code true} to close the socket just after the 
	 *                    transmission. {@code false} to keep it open.
	 * @param data Byte array containing the network data to be sent.
	 * 
	 * @throws IllegalArgumentException if {@code destPort < 0} or 
	 *                                  if {@code destPort > 65535}
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ipAddress == null} or 
	 *                              if {@code protocol == null} or 
	 *                              if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #sendBroadcastNetworkData(int, byte[])
	 * @see #sendNetworkData(IP32BitAddress, int, NetworkProtocol, byte[])
	 * @see #sendNetworkDataAsync(IP32BitAddress, int, NetworkProtocol, byte[])
	 * @see #sendNetworkDataAsync(IP32BitAddress, int, NetworkProtocol, boolean, byte[])
	 * @see #setReceiveTimeout(int)
	 * @see com.digi.xbee.api.models.IP32BitAddress
	 * @see com.digi.xbee.api.models.NetworkProtocol
	 */
	public void sendNetworkData(IP32BitAddress ipAddress, int destPort, 
			NetworkProtocol protocol, boolean closeSocket, byte[] data) 
					throws TimeoutException, XBeeException {
		if (ipAddress == null)
			throw new NullPointerException("IP address cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		
		if (destPort < 0 || destPort > 65535)
			throw new IllegalArgumentException("Destination port must be between 0 and 65535.");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send network data from a remote device.");
		
		// The source port value depends on the protocol used in the transmission. For UDP, source port 
		// value must be the same as 'C0' one. For TCP it must be 0.
		int sourcePort = this.sourcePort;
		if (protocol != NetworkProtocol.UDP)
			sourcePort = 0;
		
		logger.debug(toString() + "Sending network data to {}:{} >> {}.", ipAddress, destPort, HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket = new TXIPv4Packet(getNextFrameID(), ipAddress, destPort, 
				sourcePort, protocol, closeSocket ? TXIPv4Packet.OPTIONS_CLOSE_SOCKET: TXIPv4Packet.OPTIONS_LEAVE_SOCKET_OPEN, data);
		
		sendAndCheckXBeePacket(xbeePacket, false);
	}
	
	/**
	 * Sends the provided network data to the given IP address and port using 
	 * the specified network protocol.
	 * 
	 * <p>This method blocks till a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations use the method 
	 * {@link #sendNetworkDataAsync(IP32BitAddress, byte[])}.</p>
	 * 
	 * @param ipAddress The IP address to send network data to.
	 * @param destPort The destination port of the transmission.
	 * @param protocol The network protocol used for the transmission.
	 * @param data Byte array containing the network data to be sent.
	 * 
	 * @throws IllegalArgumentException if {@code destPort < 0} or 
	 *                                  if {@code destPort > 65535}
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ipAddress == null} or 
	 *                              if {@code protocol == null} or 
	 *                              if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #sendBroadcastNetworkData(int, byte[])
	 * @see #sendNetworkData(IP32BitAddress, int, NetworkProtocol, boolean, byte[])
	 * @see #sendNetworkDataAsync(IP32BitAddress, int, NetworkProtocol, byte[])
	 * @see #sendNetworkDataAsync(IP32BitAddress, int, NetworkProtocol, boolean, byte[])
	 * @see #setReceiveTimeout(int)
	 * @see com.digi.xbee.api.models.IP32BitAddress
	 * @see com.digi.xbee.api.models.NetworkProtocol
	 */
	public void sendNetworkData(IP32BitAddress ipAddress, int destPort, NetworkProtocol protocol, byte[] data) 
			throws TimeoutException, XBeeException {
		sendNetworkData(ipAddress, destPort, protocol, false, data);
	}
	
	/**
	 * Sends the provided network data to the given IP address and port 
	 * asynchronously using the specified network protocol. For TCP and TCP SSL 
	 * protocols, you can also indicate if the socket should be closed when 
	 * data is sent.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote 
	 * device or for transmit status packet.</p>
	 * 
	 * @param ipAddress The IP address to send network data to.
	 * @param destPort The destination port of the transmission.
	 * @param protocol The network protocol used for the transmission.
	 * @param closeSocket {@code true} to close the socket just after the 
	 *                    transmission. {@code false} to keep it open.
	 * @param data Byte array containing the network data to be sent.
	 * 
	 * @throws IllegalArgumentException if {@code destPort < 0} or 
	 *                                  if {@code destPort > 65535}
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ipAddress == null} or 
	 *                              if {@code protocol == null} or 
	 *                              if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #sendBroadcastNetworkData(int, byte[])
	 * @see #sendNetworkData(IP32BitAddress, int, NetworkProtocol, byte[])
	 * @see #sendNetworkData(IP32BitAddress, int, NetworkProtocol, boolean, byte[])
	 * @see #sendNetworkDataAsync(IP32BitAddress, int, NetworkProtocol, byte[])
	 * @see com.digi.xbee.api.models.IP32BitAddress
	 * @see com.digi.xbee.api.models.NetworkProtocol
	 */
	public void sendNetworkDataAsync(IP32BitAddress ipAddress, int destPort, 
			NetworkProtocol protocol, boolean closeSocket, byte[] data) throws XBeeException {
		if (ipAddress == null)
			throw new NullPointerException("IP address cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		if (destPort < 0 || destPort > 65535)
			throw new IllegalArgumentException("Destination port must be between 0 and 65535.");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send network data from a remote device.");
		
		// The source port value depends on the protocol used in the transmission. For UDP, source port 
		// value must be the same as 'C0' one. For TCP it must be 0.
		int sourcePort = this.sourcePort;
		if (protocol != NetworkProtocol.UDP)
			sourcePort = 0;
		
		logger.debug(toString() + "Sending network data asynchronously to {}:{} >> {}.", ipAddress, destPort, HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket = new TXIPv4Packet(getNextFrameID(), ipAddress, destPort, sourcePort, 
				protocol, closeSocket ? TXIPv4Packet.OPTIONS_CLOSE_SOCKET: TXIPv4Packet.OPTIONS_LEAVE_SOCKET_OPEN, data);
		
		sendAndCheckXBeePacket(xbeePacket, true);
	}
	
	/**
	 * Sends the provided network data to the given IP address and port 
	 * asynchronously.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote 
	 * device or for transmit status packet.</p>
	 * 
	 * @param ipAddress The IP address to send network data to.
	 * @param destPort The destination port of the transmission.
	 * @param protocol The network protocol used for the transmission.
	 * @param data Byte array containing the network data to be sent.
	 * 
	 * @throws IllegalArgumentException if {@code destPort < 0} or 
	 *                                  if {@code destPort > 65535}
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ipAddress == null} or 
	 *                              if {@code protocol == null} or 
	 *                              if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #sendBroadcastNetworkData(int, byte[])
	 * @see #sendNetworkData(IP32BitAddress, int, NetworkProtocol, byte[])
	 * @see #sendNetworkData(IP32BitAddress, int, NetworkProtocol, boolean, byte[])
	 * @see #sendNetworkDataAsync(IP32BitAddress, int, NetworkProtocol, boolean, byte[])
	 * @see com.digi.xbee.api.models.IP32BitAddress
	 * @see com.digi.xbee.api.models.NetworkProtocol
	 */
	public void sendNetworkDataAsync(IP32BitAddress ipAddress, int destPort, 
			NetworkProtocol protocol, byte[] data) throws TimeoutException, XBeeException {
		sendNetworkDataAsync(ipAddress, destPort, protocol, false, data);
	}
	
	/**
	 * Sends the provided network data to all clients.
	 * 
	 * <p>This method blocks till a success or error transmit status arrives or 
	 * the configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * @param destPort The destination port of the transmission.
	 * @param data Byte array containing the network data to be sent.
	 * 
	 * @throws IllegalArgumentException if {@code destPort < 0} or 
	 *                                  if {@code destPort > 65535}
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #sendNetworkData(IP32BitAddress, int, NetworkProtocol, byte[])
	 * @see #sendNetworkData(IP32BitAddress, int, NetworkProtocol, boolean, byte[])
	 * @see #sendNetworkDataAsync(IP32BitAddress, int, NetworkProtocol, byte[])
	 * @see #sendNetworkDataAsync(IP32BitAddress, int, NetworkProtocol, boolean, byte[])
	 * @see #setReceiveTimeout(int)
	 */
	public void sendBroadcastNetworkData(int destPort, byte[] data) throws TimeoutException, XBeeException {
		sendNetworkData(IP32BitAddress.BROADCAST_ADDRESS, destPort, NetworkProtocol.UDP, false, data);
	}
	
	/**
	 * Reads new network data received by this XBee device during the 
	 * configured receive timeout.
	 * 
	 * <p>This method blocks until new network data is received or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations, register a {@code INetworkDataReceiveListener} 
	 * using the method {@link #addNetworkDataListener(INetworkDataReceiveListener)}.</p>
	 * 
	 * <p>Before reading network data you need to start listening for incoming 
	 * network data at a specific port. Use the {@code startListening} method 
	 * for that purpose. When finished, you can use the {@code stopListening} 
	 * method to stop listening for incoming network data.</p>
	 * 
	 * @return A {@code NetworkMessage} object containing the network data and 
	 *         the IP address that sent the data. {@code null} if this did not 
	 *         receive new network data during the configured receive timeout.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #readNetworkData(int)
	 * @see #readNetworkDataFrom(IP32BitAddress)
	 * @see #readNetworkDataFrom(IP32BitAddress, int)
	 * @see #setReceiveTimeout(int)
	 * @see #startListening(int)
	 * @see #stopListening()
	 * @see com.digi.xbee.api.models.NetworkMessage
	 */
	public NetworkMessage readNetworkData() {
		return readNetworkDataPacket(null, TIMEOUT_READ_PACKET);
	}
	
	/**
	 * Reads new network data received by this XBee device during the provided 
	 * timeout.
	 * 
	 * <p>This method blocks until new network data is received or the provided 
	 * timeout expires.</p>
	 * 
	 * <p>For non-blocking operations, register a {@code INetworkDataReceiveListener} 
	 * using the method {@link #addNetworkDataListener(INetworkDataReceiveListener)}.</p>
	 * 
	 * <p>Before reading network data you need to start listening for incoming 
	 * network data at a specific port. Use the {@code startListening} method 
	 * for that purpose. When finished, you can use the {@code stopListening} 
	 * method to stop listening for incoming network data.</p>
	 * 
	 * @param timeout The time to wait for new network data in milliseconds.
	 * 
	 * @return A {@code NetworkMessage} object containing the data and the IP 
	 *         address that sent the data. {@code null} if this device did not 
	 *         receive new data during {@code timeout} milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code timeout < 0}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * 
	 * @see #readNetworkData()
	 * @see #readNetworkDataFrom(IP32BitAddress)
	 * @see #readNetworkDataFrom(IP32BitAddress, int)
	 * @see #startListening(int)
	 * @see #stopListening()
	 * @see com.digi.xbee.api.models.NetworkMessage
	 */
	public NetworkMessage readNetworkData(int timeout) {
		if (timeout < 0)
			throw new IllegalArgumentException("Read timeout must be 0 or greater.");
		
		return readNetworkDataPacket(null, timeout);
	}
	
	/**
	 * Reads new network data received from the given IP address during the 
	 * configured receive timeout.
	 * 
	 * <p>This method blocks until new data from the provided IP address is 
	 * received or the configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations, register a {@code INetworkDataReceiveListener} 
	 * using the method {@link #addNetworkDataListener(INetworkDataReceiveListener)}.</p>
	 * 
	 * <p>Before reading network data you need to start listening for incoming 
	 * network data at a specific port. Use the {@code startListening} method 
	 * for that purpose. When finished, you can use the {@code stopListening} 
	 * method to stop listening for incoming network data.</p>
	 * 
	 * @param ipAddress The IP address to read data from.
	 * 
	 * @return A {@code NetworkMessage} object containing the network data and 
	 *         the IP address of the remote node that sent the data. 
	 *         {@code null} if this device did not receive new network data 
	 *         from the provided IP address during the configured receive 
	 *         timeout.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ipAddress == null}.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #readNetworkData()
	 * @see #readNetworkData(int)
	 * @see #readNetworkDataFrom(IP32BitAddress, int)
	 * @see #setReceiveTimeout(int)
	 * @see #startListening(int)
	 * @see #stopListening()
	 * @see com.digi.xbee.api.models.IP32BitAddress
	 * @see com.digi.xbee.api.models.NetworkMessage
	 */
	public NetworkMessage readNetworkDataFrom(IP32BitAddress ipAddress) {
		if (ipAddress == null)
			throw new NullPointerException("IP address cannot be null.");
		
		return readNetworkDataPacket(ipAddress, TIMEOUT_READ_PACKET);
	}
	
	/**
	 * Reads new network data received from the given IP address during the 
	 * provided timeout.
	 * 
	 * <p>This method blocks until new network data from the provided IP 
	 * address is received or the given timeout expires.</p>
	 * 
	 * <p>For non-blocking operations, register a {@code INetworkDataReceiveListener} 
	 * using the method {@link #addNetworkDataListener(INetworkDataReceiveListener)}.</p>
	 * 
	 * <p>Before reading network data you need to start listening for incoming 
	 * network data at a specific port. Use the {@code startListening} method 
	 * for that purpose. When finished, you can use the {@code stopListening} 
	 * method to stop listening for incoming network data.</p>
	 * 
	 * @param ipAddress The IP address to read data from.
	 * @param timeout The time to wait for new network data in milliseconds.
	 * 
	 * @return A {@code NetworkMessage} object containing the network data and 
	 *         the IP address that sent the data. {@code null} if this device 
	 *         did not receive new network data from the provided IP address 
	 *         during {@code timeout} milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code timeout < 0}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ipAddress == null}.
	 * 
	 * @see #readNetworkDataFrom(IP32BitAddress)
	 * @see #readNetworkData()
	 * @see #readNetworkData(int)
	 * @see #startListening(int)
	 * @see #stopListening()
	 * @see com.digi.xbee.api.models.IP32BitAddress
	 * @see com.digi.xbee.api.models.NetworkMessage
	 */
	public NetworkMessage readNetworkDataFrom(IP32BitAddress ipAddress, int timeout) {
		if (ipAddress == null)
			throw new NullPointerException("IP address cannot be null.");
		if (timeout < 0)
			throw new IllegalArgumentException("Read timeout must be 0 or greater.");
		
		return readNetworkDataPacket(ipAddress, timeout);
	}
	
	/**
	 * Reads a new network data packet received by this IP XBee device during 
	 * the provided timeout.
	 * 
	 * <p>This method blocks until new network data is received or the given 
	 * timeout expires.</p>
	 * 
	 * <p>If the provided IP address is {@code null} the method returns 
	 * the first network data packet read from any IP address.
	 * <br>
	 * If the IP address is not {@code null} the method returns the first 
	 * data package read from the provided IP address.
	 * </p>
	 * 
	 * @param remoteIPAddress The IP address to get a network data packet from. 
	 *                        {@code null} to read a network data packet from 
	 *                        any IP address.
	 * @param timeout The time to wait for a network data packet in milliseconds.
	 * 
	 * @return A {@code NetworkMessage} received by this device, containing the 
	 *         data and the source IP address that sent the network data. 
	 *         {@code null} if this device did not receive new network data 
	 *         during {@code timeout} milliseconds, or if any error occurs while
	 *         trying to get the source of the message.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * 
	 * @see com.digi.xbee.api.models.IP32BitAddress
	 * @see com.digi.xbee.api.models.XBeeMessage
	 */
	private NetworkMessage readNetworkDataPacket(IP32BitAddress remoteIPAddress, int timeout) {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		XBeePacketsQueue xbeePacketsQueue = dataReader.getXBeePacketsQueue();
		XBeePacket xbeePacket = null;
		
		if (remoteIPAddress != null)
			xbeePacket = xbeePacketsQueue.getFirstNetworkDataPacketFrom(remoteIPAddress, timeout);
		else
			xbeePacket = xbeePacketsQueue.getFirstNetworkDataPacket(timeout);
		
		if (xbeePacket == null)
			return null;
		
		// Obtain the data and IP address from the packet.
		byte[] data = null;
		IP32BitAddress ipAddress = null;
		int sourcePort;
		int destPort;
		NetworkProtocol protocol = NetworkProtocol.TCP;
		
		switch (((XBeeAPIPacket)xbeePacket).getFrameType()) {
		case RX_IPV4:
			RXIPv4Packet receivePacket = (RXIPv4Packet)xbeePacket;
			data = receivePacket.getData();
			ipAddress = receivePacket.getSourceAddress();
			sourcePort = receivePacket.getSourcePort();
			destPort = receivePacket.getDestPort();
			break;
		default:
			return null;
		}
		
		// Create and return the XBee message.
		return new NetworkMessage(ipAddress, sourcePort, destPort, protocol, data);
	}
}

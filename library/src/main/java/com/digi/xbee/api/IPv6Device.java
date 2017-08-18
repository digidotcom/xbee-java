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
package com.digi.xbee.api;

import java.net.Inet6Address;
import java.net.UnknownHostException;

import android.content.Context;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.connection.android.AndroidUSBPermissionListener;
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.listeners.IIPDataReceiveListener;
import com.digi.xbee.api.models.IPMessage;
import com.digi.xbee.api.models.IPProtocol;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.models.XBeePacketsQueue;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.thread.RXIPv6Packet;
import com.digi.xbee.api.packet.thread.TXIPv6Packet;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class provides common functionality for XBee IPv6 devices.
 * 
 * @see ThreadDevice
 * 
 * @since 1.2.1
 */
public class IPv6Device extends XBeeDevice {

	// Constants
	private static final String OPERATION_EXCEPTION = "Operation not supported in this module.";
	
	protected static final short DEFAULT_SOURCE_PORT = 9750;
	
	protected static final IPProtocol DEFAULT_PROTOCOL = IPProtocol.TCP;
	
	// Variables
	protected int sourcePort = DEFAULT_SOURCE_PORT;
	
	/**
	 * Class constructor. Instantiates a new {@code IPv6Device} object in 
	 * the given port name and baud rate.
	 * 
	 * @param port Serial port name where IPv6 device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device. 
	 *                 Other connection parameters will be set as default (8 
	 *                 data bits, 1 stop bit, no parity, no flow control).
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see #IPv6Device(IConnectionInterface)
	 * @see #IPv6Device(String, SerialPortParameters)
	 * @see #IPv6Device(String, int, int, int, int, int)
	 * @see #IPv6Device(Context, int)
	 * @see #IPv6Device(Context, int, AndroidUSBPermissionListener)
	 * @see #IPv6Device(Context, String, int)
	 * @see #IPv6Device(Context, String, SerialPortParameters)
	 */
	protected IPv6Device(String port, int baudRate) {
		this(XBee.createConnectiontionInterface(port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code IPv6Device} object in 
	 * the given serial port name and settings.
	 * 
	 * @param port Serial port name where IPv6 device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device.
	 * @param dataBits Serial port data bits.
	 * @param stopBits Serial port stop bits.
	 * @param parity Serial port parity.
	 * @param flowControl Serial port flow control.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 0} or
	 *                                  if {@code dataBits < 0} or
	 *                                  if {@code stopBits < 0} or
	 *                                  if {@code parity < 0} or
	 *                                  if {@code flowControl < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see #IPv6Device(IConnectionInterface)
	 * @see #IPv6Device(String, int)
	 * @see #IPv6Device(String, SerialPortParameters)
	 * @see #IPv6Device(Context, int)
	 * @see #IPv6Device(Context, int, AndroidUSBPermissionListener)
	 * @see #IPv6Device(Context, String, int)
	 * @see #IPv6Device(Context, String, SerialPortParameters)
	 */
	protected IPv6Device(String port, int baudRate, int dataBits, int stopBits,
			int parity, int flowControl) {
		this(port, new SerialPortParameters(baudRate, dataBits, stopBits, parity, flowControl));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code IPv6Device} object in 
	 * the given serial port name and parameters.
	 * 
	 * @param port Serial port name where IPv6 device is attached to.
	 * @param serialPortParameters Object containing the serial port parameters.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code serialPortParameters == null}.
	 * 
	 * @see #IPv6Device(IConnectionInterface)
	 * @see #IPv6Device(String, int)
	 * @see #IPv6Device(String, int, int, int, int, int)
	 * @see #IPv6Device(Context, int)
	 * @see #IPv6Device(Context, int, AndroidUSBPermissionListener)
	 * @see #IPv6Device(Context, String, int)
	 * @see #IPv6Device(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	protected IPv6Device(String port, SerialPortParameters serialPortParameters) {
		this(XBee.createConnectiontionInterface(port, serialPortParameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code IPv6Device} object for
	 * Android with the given parameters.
	 * 
	 * @param context The Android context.
	 * @param baudRate The USB connection baud rate.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 1}.
	 * @throws NullPointerException if {@code context == null}.
	 * 
	 * @see #IPv6Device(IConnectionInterface)
	 * @see #IPv6Device(String, int)
	 * @see #IPv6Device(String, int, int, int, int, int)
	 * @see #IPv6Device(String, SerialPortParameters)
	 * @see #IPv6Device(Context, int, AndroidUSBPermissionListener)
	 * @see #IPv6Device(Context, String, int)
	 * @see #IPv6Device(Context, String, SerialPortParameters)
	 */
	protected IPv6Device(Context context, int baudRate) {
		super(XBee.createConnectiontionInterface(context, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code IPv6Device} object for
	 * Android with the given parameters.
	 * 
	 * @param context The Android context.
	 * @param baudRate The USB connection baud rate.
	 * @param permissionListener The USB permission listener that will be 
	 *                           notified when user grants USB permissions.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 1}.
	 * @throws NullPointerException if {@code context == null}.
	 * 
	 * @see #IPv6Device(IConnectionInterface)
	 * @see #IPv6Device(String, int)
	 * @see #IPv6Device(String, int, int, int, int, int)
	 * @see #IPv6Device(String, SerialPortParameters)
	 * @see #IPv6Device(Context, int)
	 * @see #IPv6Device(Context, String, int)
	 * @see #IPv6Device(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.android.AndroidUSBPermissionListener
	 */
	protected IPv6Device(Context context, int baudRate, AndroidUSBPermissionListener permissionListener) {
		super(XBee.createConnectiontionInterface(context, baudRate, permissionListener));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code IPv6Device} object for
	 * Android with the given parameters.
	 * 
	 * <p>This constructor uses the Digi Android Serial Port API based on the
	 * RxTx library to communicate with the devices.</p>
	 * 
	 * @param context The Android application context.
	 * @param port Serial port name where XBee device is attached to.
	 * @param baudRate The serial port connection baud rate.
	 * 
	 * @throws NullPointerException If {@code context == null} or
	 *                              if {@code port == null}.
	 * @throws IllegalArgumentException if {@code baudRate < 1}.
	 * 
	 * @see #IPv6Device(IConnectionInterface)
	 * @see #IPv6Device(String, int)
	 * @see #IPv6Device(String, int, int, int, int, int)
	 * @see #IPv6Device(String, SerialPortParameters)
	 * @see #IPv6Device(Context, int)
	 * @see #IPv6Device(Context, int, AndroidUSBPermissionListener)
	 * @see #IPv6Device(Context, String, SerialPortParameters)
	 */
	protected IPv6Device(Context context, String port, int baudRate) {
		super(XBee.createConnectiontionInterface(context, port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code IPv6Device} object for
	 * Android with the given parameters.
	 * 
	 * <p>This constructor uses the Digi Android Serial Port API based on the
	 * RxTx library to communicate with the devices.</p>
	 * 
	 * @param context The Android application context.
	 * @param port Serial port name where XBee device is attached to.
	 * @param parameters The serial port parameters.
	 * 
	 * @throws NullPointerException If {@code context == null} or
	 *                              if {@code port == null} or
	 *                              if {@code parameters == null}.
	 * 
	 * @see #IPv6Device(IConnectionInterface)
	 * @see #IPv6Device(String, int)
	 * @see #IPv6Device(String, int, int, int, int, int)
	 * @see #IPv6Device(String, SerialPortParameters)
	 * @see #IPv6Device(Context, int)
	 * @see #IPv6Device(Context, int, AndroidUSBPermissionListener)
	 * @see #IPv6Device(Context, String, int)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	protected IPv6Device(Context context, String port, SerialPortParameters parameters) {
		super(XBee.createConnectiontionInterface(context, port, parameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code IPv6Device} object with 
	 * the given connection interface.
	 * 
	 * @param connectionInterface The connection interface with the physical 
	 *                            IPv6 device.
	 * 
	 * @throws NullPointerException if {@code connectionInterface == null}
	 * 
	 * @see #IPv6Device(String, int)
	 * @see #IPv6Device(String, SerialPortParameters)
	 * @see #IPv6Device(String, int, int, int, int, int)
	 * @see #IPv6Device(Context, int)
	 * @see #IPv6Device(Context, int, AndroidUSBPermissionListener)
	 * @see #IPv6Device(Context, String, int)
	 * @see #IPv6Device(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 */
	protected IPv6Device(IConnectionInterface connectionInterface) {
		super(connectionInterface);
	}
	
	/**
	 * @deprecated This protocol does not support the network functionality.
	 */
	@Override
	public XBeeNetwork getNetwork() {
		// IPv6 modules do not have a network of devices.
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#readDeviceInfo()
	 */
	@Override
	public void readDeviceInfo() throws TimeoutException, XBeeException {
		super.readDeviceInfo();
		// Generate the Mesh-Local IPv6 address.
		byte[] response = getParameter("MY");
		try {
			ipv6Address = (Inet6Address) Inet6Address.getByAddress(response);
		} catch (UnknownHostException e) {
			throw new XBeeException(e);
		}
		
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
	 * @deprecated This protocol does not have an associated 16-bit address.
	 */
	@Override
	public XBee16BitAddress get16BitAddress() {
		// IPv6 modules do not have 16-bit address.
		return null;
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. Use
	 *             {@link #getIPv6DestinationAddress()} instead.
	 *             This method will raise an 
	 *             {@link UnsupportedOperationException}.
	 */
	@Override
	public XBee64BitAddress getDestinationAddress() throws TimeoutException,
			XBeeException {
		// Not supported in IPv6 modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. Use
	 *             {@link #setIPv6DestinationAddress(Inet6Address)} instead.
	 *             This method will raise an 
	 *             {@link UnsupportedOperationException}.
	 */
	@Override
	public void setDestinationAddress(XBee64BitAddress xbee64BitAddress)
			throws TimeoutException, XBeeException {
		// Not supported in IPv6 modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void addDataListener(IDataReceiveListener listener) {
		// Not supported in IPv6 modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void removeDataListener(IDataReceiveListener listener) {
		// Not supported in IPv6 modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public XBeeMessage readData() {
		// Not supported in IPv6 modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public XBeeMessage readData(int timeout) {
		// Not supported in IPv6 modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public XBeeMessage readDataFrom(RemoteXBeeDevice remoteXBeeDevice) {
		// Not supported in IPv6 modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public XBeeMessage readDataFrom(RemoteXBeeDevice remoteXBeeDevice,
			int timeout) {
		// Not supported in IPv6 modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void sendBroadcastData(byte[] data) throws TimeoutException,
			XBeeException {
		// Not supported in IPv6 modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void sendData(RemoteXBeeDevice remoteXBeeDevice, byte[] data)
			throws TimeoutException, XBeeException {
		// Not supported in IPv6 modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void sendDataAsync(RemoteXBeeDevice remoteXBeeDevice, byte[] data)
			throws XBeeException {
		// Not supported in IPv6 modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#addIPDataListener(com.digi.xbee.api.listeners.IIPDataReceiveListener)
	 */
	@Override
	public void addIPDataListener(IIPDataReceiveListener listener) {
		super.addIPDataListener(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#removeIPDataListener(com.digi.xbee.api.listeners.IIPDataReceiveListener)
	 */
	@Override
	public void removeIPDataListener(IIPDataReceiveListener listener) {
		super.removeIPDataListener(listener);
	}
	
	/**
	 * Starts listening for incoming IPv6 transmissions in the provided port.
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
	 * Stops listening for incoming IPv6 transmissions.
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
	 * Sends the provided IPv6 data to the given IPv6 address and port using 
	 * the specified IPv6 protocol.
	 * 
	 * <p>This method blocks till a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations use the method 
	 * {@link #sendIPDataAsync(Inet6Address, int, IPProtocol, byte[])}.</p>
	 * 
	 * @param ipv6Address The IPv6 address to send IPv6 data to.
	 * @param destPort The destination port of the transmission.
	 * @param protocol The IPv6 protocol used for the transmission.
	 * @param data Byte array containing the IPv6 data to be sent.
	 * 
	 * @throws IllegalArgumentException if {@code destPort < 0} or 
	 *                                  if {@code destPort > 65535}
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ipv6Address == null} or 
	 *                              if {@code protocol == null} or 
	 *                              if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #sendIPDataAsync(Inet6Address, int, IPProtocol, byte[])
	 * @see #setReceiveTimeout(int)
	 * @see com.digi.xbee.api.models.IPProtocol
	 * @see java.net.Inet6Address
	 */
	public void sendIPData(Inet6Address ipv6Address, int destPort, 
			IPProtocol protocol, byte[] data) throws TimeoutException, XBeeException {
		sendIPData(ipv6Address, destPort, protocol, data, false);
	}
	
	/**
	 * Sends the provided IPv6 data to the given IPv6 address and port 
	 * asynchronously using the specified IPv6 protocol.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote 
	 * device or for transmit status packet.</p>
	 * 
	 * @param ipv6Address The IPv6 address to send IPv6 data to.
	 * @param destPort The destination port of the transmission.
	 * @param protocol The IPv6 protocol used for the transmission.
	 * @param data Byte array containing the IPv6 data to be sent.
	 * 
	 * @throws IllegalArgumentException if {@code destPort < 0} or 
	 *                                  if {@code destPort > 65535}
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ipv6Address == null} or 
	 *                              if {@code protocol == null} or 
	 *                              if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #sendIPData(Inet6Address, int, IPProtocol, byte[])
	 * @see com.digi.xbee.api.models.IPProtocol
	 * @see java.net.Inet6Address
	 */
	public void sendIPDataAsync(Inet6Address ipv6Address, int destPort, 
			IPProtocol protocol, byte[] data) throws XBeeException {
		sendIPData(ipv6Address, destPort, protocol, data, true);
	}
	
	/**
	 * Sends the provided IPv6 data to the given IPv6 address and port using 
	 * the specified IPv6 protocol.
	 * 
	 * <p>Transmissions can be performed synchronously or asynchronously. 
	 * Synchronous operation blocks till a success or error response arrives 
	 * or the configured receive timeout expires. Asynchronous transmissions 
	 * do not wait for answer from the remote device or for transmit status 
	 * packet.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For synchronous operations use this method:</p>
	 * <ul>
	 *   <li>{@link #sendIPData(Inet6Address, int, IPProtocol, byte[])}.</li>
	 * </ul>
	 * <p>For asynchronous operations use this one:</p>
	 * <ul>
	 *   <li>{@link #sendIPDataAsync(Inet6Address, int, IPProtocol, byte[])}.</li>
	 * </ul>
	 * 
	 * @param ipv6Address The IPv6 address to send IPv6 data to.
	 * @param destPort The destination port of the transmission.
	 * @param protocol The IPv6 protocol used for the transmission.
	 * @param data Byte array containing the IPv6 data to be sent.
	 * @param async Boolean that should be set to {@code true} if the 
	 *        transmission should be asynchronous, and {@code false} otherwise.
	 * 
	 * @throws IllegalArgumentException if {@code destPort < 0} or 
	 *                                  if {@code destPort > 65535}
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ipv6Address == null} or 
	 *                              if {@code protocol == null} or 
	 *                              if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #sendIPData(Inet6Address, int, IPProtocol, byte[])
	 * @see #sendIPDataAsync(Inet6Address, int, IPProtocol, byte[])
	 * @see #setReceiveTimeout(int)
	 * @see com.digi.xbee.api.models.IPProtocol
	 * @see java.net.Inet6Address
	 */
	private void sendIPData(Inet6Address ipv6Address, int destPort, 
			IPProtocol protocol, byte[] data, boolean async) throws XBeeException {
		if (ipv6Address == null)
			throw new NullPointerException("IPv6 address cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		if (destPort < 0 || destPort > 65535)
			throw new IllegalArgumentException("Destination port must be between 0 and 65535.");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send IPv6 data from a remote device.");
		
		// The source port value depends on the protocol used in the transmission. For UDP, source port 
		// value must be the same as 'C0' one. For TCP it must be 0.
		int sourcePort = this.sourcePort;
		if (protocol != IPProtocol.UDP)
			sourcePort = 0;
		
		if (async)
			logger.debug(toString() + "Sending IPv6 data asynchronously to {}:{} >> {}.", ipv6Address,
					destPort, HexUtils.prettyHexString(data));
		else
			logger.debug(toString() + "Sending IPv6 data to {}:{} >> {}.", ipv6Address,
					destPort, HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket = new TXIPv6Packet(getNextFrameID(), ipv6Address, destPort,
				sourcePort, protocol, data);
		
		sendAndCheckXBeePacket(xbeePacket, async);
	}
	
	/**
	 * Reads new IPv6 data received by this XBee device during the 
	 * configured receive timeout.
	 * 
	 * <p>This method blocks until new IPv6 data is received or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations, register a {@code IIPDataReceiveListener} 
	 * using the method {@link #addIPDataListener(IIPDataReceiveListener)}.</p>
	 * 
	 * <p>Before reading IPv6 data you need to start listening for incoming 
	 * IPv6 data at a specific port. Use the {@code startListening} method 
	 * for that purpose. When finished, you can use the {@code stopListening} 
	 * method to stop listening for incoming IPv6 data.</p>
	 * 
	 * @return A {@code IPMessage} object containing the IPv6 data and 
	 *         the IPv6 address that sent the data. {@code null} if this did not 
	 *         receive new IPv6 data during the configured receive timeout.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #readIPData(int)
	 * @see #readIPDataFrom(Inet6Address)
	 * @see #readIPDataFrom(Inet6Address, int)
	 * @see #setReceiveTimeout(int)
	 * @see #startListening(int)
	 * @see #stopListening()
	 * @see com.digi.xbee.api.models.IPMessage
	 */
	public IPMessage readIPData() {
		return readIPDataPacket(null, TIMEOUT_READ_PACKET);
	}
	
	/**
	 * Reads new IPv6 data received by this XBee device during the provided 
	 * timeout.
	 * 
	 * <p>This method blocks until new IPv6 data is received or the provided 
	 * timeout expires.</p>
	 * 
	 * <p>For non-blocking operations, register a {@code IIPDataReceiveListener} 
	 * using the method {@link #addIPDataListener(IIPDataReceiveListener)}.</p>
	 * 
	 * <p>Before reading IPv6 data you need to start listening for incoming 
	 * IPv6 data at a specific port. Use the {@code startListening} method 
	 * for that purpose. When finished, you can use the {@code stopListening} 
	 * method to stop listening for incoming IPv6 data.</p>
	 * 
	 * @param timeout The time to wait for new IPv6 data in milliseconds.
	 * 
	 * @return A {@code IPMessage} object containing the data and the IPv6 
	 *         address that sent the data. {@code null} if this device did not 
	 *         receive new data during {@code timeout} milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code timeout < 0}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * 
	 * @see #readIPData()
	 * @see #readIPDataFrom(Inet6Address)
	 * @see #readIPDataFrom(Inet6Address, int)
	 * @see #startListening(int)
	 * @see #stopListening()
	 * @see com.digi.xbee.api.models.IPMessage
	 */
	public IPMessage readIPData(int timeout) {
		if (timeout < 0)
			throw new IllegalArgumentException("Read timeout must be 0 or greater.");
		
		return readIPDataPacket(null, timeout);
	}
	
	/**
	 * Reads new IPv6 data received from the given IPv6 address during the 
	 * configured receive timeout.
	 * 
	 * <p>This method blocks until new data from the provided IPv6 address is 
	 * received or the configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations, register a {@code IIPDataReceiveListener} 
	 * using the method {@link #addIPDataListener(IIPDataReceiveListener)}.</p>
	 * 
	 * <p>Before reading IPv6 data you need to start listening for incoming 
	 * IPv6 data at a specific port. Use the {@code startListening} method 
	 * for that purpose. When finished, you can use the {@code stopListening} 
	 * method to stop listening for incoming IPv6 data.</p>
	 * 
	 * @param ipv6Address The IPv6 address to read data from.
	 * 
	 * @return A {@code IPMessage} object containing the IPv6 data and 
	 *         the IPv6 address of the remote node that sent the data. 
	 *         {@code null} if this device did not receive new IPv6 data 
	 *         from the provided IPv6 address during the configured receive 
	 *         timeout.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ipv6Address == null}.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #readIPData()
	 * @see #readIPData(int)
	 * @see #readIPDataFrom(Inet6Address, int)
	 * @see #setReceiveTimeout(int)
	 * @see #startListening(int)
	 * @see #stopListening()
	 * @see com.digi.xbee.api.models.IPMessage
	 * @see java.net.Inet6Address
	 */
	public IPMessage readIPDataFrom(Inet6Address ipv6Address) {
		if (ipv6Address == null)
			throw new NullPointerException("IPv6 address cannot be null.");
		
		return readIPDataPacket(ipv6Address, TIMEOUT_READ_PACKET);
	}
	
	/**
	 * Reads new IPv6 data received from the given IPv6 address during the 
	 * provided timeout.
	 * 
	 * <p>This method blocks until new IPv6 data from the provided IPv6 
	 * address is received or the given timeout expires.</p>
	 * 
	 * <p>For non-blocking operations, register a {@code IIPDataReceiveListener} 
	 * using the method {@link #addIPDataListener(IIPDataReceiveListener)}.</p>
	 * 
	 * <p>Before reading IPv6 data you need to start listening for incoming 
	 * IPv6 data at a specific port. Use the {@code startListening} method 
	 * for that purpose. When finished, you can use the {@code stopListening} 
	 * method to stop listening for incoming IPv6 data.</p>
	 * 
	 * @param ipv6Address The IPv6 address to read data from.
	 * @param timeout The time to wait for new IPv6 data in milliseconds.
	 * 
	 * @return An {@code IPMessage} object containing the IPv6 data and 
	 *         the IPv6 address that sent the data. {@code null} if this device 
	 *         did not receive new IPv6 data from the provided IPv6 address 
	 *         during {@code timeout} milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code timeout < 0}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ipv6Address == null}.
	 * 
	 * @see #readIPDataFrom(Inet6Address)
	 * @see #readIPData()
	 * @see #readIPData(int)
	 * @see #startListening(int)
	 * @see #stopListening()
	 * @see com.digi.xbee.api.models.IPMessage
	 * @see java.net.Inet6Address
	 */
	public IPMessage readIPDataFrom(Inet6Address ipv6Address, int timeout) {
		if (ipv6Address == null)
			throw new NullPointerException("IPv6 address cannot be null.");
		if (timeout < 0)
			throw new IllegalArgumentException("Read timeout must be 0 or greater.");
		
		return readIPDataPacket(ipv6Address, timeout);
	}
	
	/**
	 * Reads a new IPv6 data packet received by this IPv6 XBee device during 
	 * the provided timeout.
	 * 
	 * <p>This method blocks until new IPv6 data is received or the given 
	 * timeout expires.</p>
	 * 
	 * <p>If the provided IPv6 address is {@code null} the method returns 
	 * the first IPv6 data packet read from any IPv6 address.
	 * <br>
	 * If the IPv6 address is not {@code null} the method returns the first 
	 * data package read from the provided IPv6 address.
	 * </p>
	 * 
	 * @param remoteIPAddress The IPv6 address to get an IPv6 data packet from. 
	 *                        {@code null} to read an IPv6 data packet from 
	 *                        any IPv6 address.
	 * @param timeout The time to wait for a IPv6 data packet in milliseconds.
	 * 
	 * @return A {@code IPMessage} received by this device, containing the 
	 *         data and the source IPv6 address that sent the IPv6 data. 
	 *         {@code null} if this device did not receive new IPv6 data 
	 *         during {@code timeout} milliseconds, or if any error occurs while
	 *         trying to get the source of the message.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * 
	 * @see com.digi.xbee.api.models.IPMessage
	 * @see java.net.Inet6Address
	 */
	private IPMessage readIPDataPacket(Inet6Address remoteIPAddress, int timeout) {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		XBeePacketsQueue xbeePacketsQueue = dataReader.getXBeePacketsQueue();
		XBeePacket xbeePacket = null;
		
		if (remoteIPAddress != null)
			xbeePacket = xbeePacketsQueue.getFirstIPv6DataPacketFrom(remoteIPAddress, timeout);
		else
			xbeePacket = xbeePacketsQueue.getFirstIPv6DataPacket(timeout);
		
		if (xbeePacket == null)
			return null;
		
		// Obtain the data and IPv6 address from the packet.
		byte[] data = null;
		Inet6Address ipv6Address = null;
		int sourcePort;
		int destPort;
		IPProtocol protocol = IPProtocol.TCP;
		
		switch (((XBeeAPIPacket)xbeePacket).getFrameType()) {
		case RX_IPV6:
			RXIPv6Packet receivePacket = (RXIPv6Packet)xbeePacket;
			data = receivePacket.getData();
			ipv6Address = receivePacket.getSourceAddress();
			sourcePort = receivePacket.getSourcePort();
			destPort = receivePacket.getDestPort();
			break;
		default:
			return null;
		}
		
		// Create and return the IPv6 message.
		return new IPMessage(ipv6Address, sourcePort, destPort, protocol, data);
	}
}

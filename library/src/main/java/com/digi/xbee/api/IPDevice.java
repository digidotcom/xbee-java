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

import java.net.Inet4Address;
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
import com.digi.xbee.api.packet.ip.RXIPv4Packet;
import com.digi.xbee.api.packet.ip.TXIPv4Packet;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class provides common functionality for XBee IP devices.
 * 
 * @see CellularDevice
 * @see WiFiDevice
 * 
 * @since 1.2.0
 */
public class IPDevice extends XBeeDevice {

	// Constants
	public static final String BROADCAST_IP = "255.255.255.255";
	
	private static final String OPERATION_EXCEPTION = "Operation not supported in this module.";
	
	protected static final short DEFAULT_SOURCE_PORT = 9750;
	
	protected static final IPProtocol DEFAULT_PROTOCOL = IPProtocol.TCP;
	
	// Variables
	protected Inet4Address ipAddress;
	
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
	 * @see #IPDevice(Context, int)
	 * @see #IPDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #IPDevice(Context, String, int)
	 * @see #IPDevice(Context, String, SerialPortParameters)
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
	 * @see #IPDevice(Context, int)
	 * @see #IPDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #IPDevice(Context, String, int)
	 * @see #IPDevice(Context, String, SerialPortParameters)
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
	 * @see #IPDevice(Context, int)
	 * @see #IPDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #IPDevice(Context, String, int)
	 * @see #IPDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	protected IPDevice(String port, SerialPortParameters serialPortParameters) {
		this(XBee.createConnectiontionInterface(port, serialPortParameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code IPDevice} object for
	 * Android with the given parameters.
	 * 
	 * @param context The Android context.
	 * @param baudRate The USB connection baud rate.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 1}.
	 * @throws NullPointerException if {@code context == null}.
	 * 
	 * @see #IPDevice(IConnectionInterface)
	 * @see #IPDevice(String, int)
	 * @see #IPDevice(String, int, int, int, int, int)
	 * @see #IPDevice(String, SerialPortParameters)
	 * @see #IPDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #IPDevice(Context, String, int)
	 * @see #IPDevice(Context, String, SerialPortParameters)
	 */
	protected IPDevice(Context context, int baudRate) {
		super(XBee.createConnectiontionInterface(context, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code IPDevice} object for
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
	 * @see #IPDevice(IConnectionInterface)
	 * @see #IPDevice(String, int)
	 * @see #IPDevice(String, int, int, int, int, int)
	 * @see #IPDevice(String, SerialPortParameters)
	 * @see #IPDevice(Context, int)
	 * @see #IPDevice(Context, String, int)
	 * @see #IPDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.android.AndroidUSBPermissionListener
	 */
	protected IPDevice(Context context, int baudRate, AndroidUSBPermissionListener permissionListener) {
		super(XBee.createConnectiontionInterface(context, baudRate, permissionListener));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code IPDevice} object for
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
	 * @see #IPDevice(IConnectionInterface)
	 * @see #IPDevice(String, int)
	 * @see #IPDevice(String, int, int, int, int, int)
	 * @see #IPDevice(String, SerialPortParameters)
	 * @see #IPDevice(Context, int)
	 * @see #IPDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #IPDevice(Context, String, SerialPortParameters)
	 */
	protected IPDevice(Context context, String port, int baudRate) {
		super(XBee.createConnectiontionInterface(context, port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code IPDevice} object for
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
	 * @see #IPDevice(IConnectionInterface)
	 * @see #IPDevice(String, int)
	 * @see #IPDevice(String, int, int, int, int, int)
	 * @see #IPDevice(String, SerialPortParameters)
	 * @see #IPDevice(Context, int)
	 * @see #IPDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #IPDevice(Context, String, int)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	protected IPDevice(Context context, String port, SerialPortParameters parameters) {
		super(XBee.createConnectiontionInterface(context, port, parameters));
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
	 * @see #IPDevice(Context, int)
	 * @see #IPDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #IPDevice(Context, String, int)
	 * @see #IPDevice(Context, String, SerialPortParameters)
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
		try {
			ipAddress = (Inet4Address) Inet4Address.getByAddress(response);
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
	 * Returns the IP address of this IP device.
	 * 
	 * <p>To refresh this value use the {@link #readDeviceInfo()} method.</p>
	 * 
	 * @return The IP address of this IP device.
	 * 
	 * @see java.net.Inet4Address
	 */
	public Inet4Address getIPAddress() {
		return ipAddress;
	}
	
	/**
	 * Sets the destination IP address.
	 * 
	 * @param address Destination IP address.
	 * 
	 * @throws NullPointerException if {@code address == null}.
	 * @throws TimeoutException if there is a timeout setting the destination
	 *                          address.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getDestinationIPAddress()
	 * @see java.net.Inet4Address
	 */
	public void setDestinationIPAddress(Inet4Address address) throws TimeoutException, XBeeException {
		if (address == null)
			throw new NullPointerException("Destination IP address cannot be null.");
		
		setParameter("DL", address.getAddress());
	}
	
	/**
	 * Returns the destination IP address.
	 * 
	 * @return The configured destination IP address.
	 * 
	 * @throws TimeoutException if there is a timeout reading the destination
	 *                          address.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setDestinationIPAddress(Inet4Address)
	 * @see java.net.Inet4Address
	 */
	public Inet4Address getDestinationIPAddress() throws TimeoutException, XBeeException {
		try {
			return (Inet4Address) Inet4Address.getByAddress(getParameter("DL"));
		} catch (UnknownHostException e) {
			throw new XBeeException(e);
		}
	}
	
	/**
	 * @deprecated This protocol does not have an associated IPv6 address.
	 */
	@Override
	public Inet6Address getIPv6Address() {
		return null;
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public Inet6Address getIPv6DestinationAddress()
			throws TimeoutException, XBeeException {
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void setIPv6DestinationAddress(Inet6Address ipv6Address)
			throws TimeoutException, XBeeException {
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
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
	 * @deprecated Operation not supported in this protocol. Use
	 *             {@link #getDestinationIPAddress()} instead.
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
	 * @deprecated Operation not supported in this protocol. Use
	 *             {@link #setDestinationIPAddress(Inet4Address)} instead.
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
	 * Starts listening for incoming IP transmissions in the provided port.
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
	 * Stops listening for incoming IP transmissions.
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
	 * Sends the provided IP data to the given IP address and port using 
	 * the specified IP protocol. For TCP and TCP SSL protocols, you can 
	 * also indicate if the socket should be closed when data is sent.
	 * 
	 * <p>This method blocks till a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations use the method 
	 * {@link #sendIPDataAsync(Inet4Address, int, IPProtocol, byte[])}.</p>
	 * 
	 * @param ipAddress The IP address to send IP data to.
	 * @param destPort The destination port of the transmission.
	 * @param protocol The IP protocol used for the transmission.
	 * @param closeSocket {@code true} to close the socket just after the 
	 *                    transmission. {@code false} to keep it open.
	 * @param data Byte array containing the IP data to be sent.
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
	 * @see #sendBroadcastIPData(int, byte[])
	 * @see #sendIPData(Inet4Address, int, IPProtocol, byte[])
	 * @see #sendIPDataAsync(Inet4Address, int, IPProtocol, byte[])
	 * @see #sendIPDataAsync(Inet4Address, int, IPProtocol, boolean, byte[])
	 * @see #setReceiveTimeout(int)
	 * @see com.digi.xbee.api.models.IPProtocol
	 * @see java.net.Inet4Address
	 */
	public void sendIPData(Inet4Address ipAddress, int destPort, 
			IPProtocol protocol, boolean closeSocket, byte[] data) 
					throws TimeoutException, XBeeException {
		sendIPDataImpl(ipAddress, destPort, protocol, closeSocket, data);
	}
	
	/**
	 * Sends the provided IP data to the given IP address and port using 
	 * the specified IP protocol.
	 * 
	 * <p>This method blocks till a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations use the method 
	 * {@link #sendIPDataAsync(Inet4Address, int, IPProtocol, byte[])}.</p>
	 * 
	 * @param ipAddress The IP address to send IP data to.
	 * @param destPort The destination port of the transmission.
	 * @param protocol The IP protocol used for the transmission.
	 * @param data Byte array containing the IP data to be sent.
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
	 * @see #sendBroadcastIPData(int, byte[])
	 * @see #sendIPData(Inet4Address, int, IPProtocol, boolean, byte[])
	 * @see #sendIPDataAsync(Inet4Address, int, IPProtocol, byte[])
	 * @see #sendIPDataAsync(Inet4Address, int, IPProtocol, boolean, byte[])
	 * @see #setReceiveTimeout(int)
	 * @see com.digi.xbee.api.models.IPProtocol
	 * @see java.net.Inet4Address
	 */
	public void sendIPData(Inet4Address ipAddress, int destPort, IPProtocol protocol, byte[] data) 
			throws TimeoutException, XBeeException {
		sendIPDataImpl(ipAddress, destPort, protocol, false, data);
	}
	
	/**
	 * Sends the provided IP data to the given IP address and port 
	 * asynchronously using the specified IP protocol. For TCP and TCP SSL 
	 * protocols, you can also indicate if the socket should be closed when 
	 * data is sent.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote 
	 * device or for transmit status packet.</p>
	 * 
	 * @param ipAddress The IP address to send IP data to.
	 * @param destPort The destination port of the transmission.
	 * @param protocol The IP protocol used for the transmission.
	 * @param closeSocket {@code true} to close the socket just after the 
	 *                    transmission. {@code false} to keep it open.
	 * @param data Byte array containing the IP data to be sent.
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
	 * @see #sendBroadcastIPData(int, byte[])
	 * @see #sendIPData(Inet4Address, int, IPProtocol, byte[])
	 * @see #sendIPData(Inet4Address, int, IPProtocol, boolean, byte[])
	 * @see #sendIPDataAsync(Inet4Address, int, IPProtocol, byte[])
	 * @see com.digi.xbee.api.models.IPProtocol
	 * @see java.net.Inet4Address
	 */
	public void sendIPDataAsync(Inet4Address ipAddress, int destPort, 
			IPProtocol protocol, boolean closeSocket, byte[] data) throws XBeeException {
		sendIPDataAsyncImpl(ipAddress, destPort, protocol, closeSocket, data);
	}
	
	/**
	 * Sends the provided IP data to the given IP address and port 
	 * asynchronously.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote 
	 * device or for transmit status packet.</p>
	 * 
	 * @param ipAddress The IP address to send IP data to.
	 * @param destPort The destination port of the transmission.
	 * @param protocol The IP protocol used for the transmission.
	 * @param data Byte array containing the IP data to be sent.
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
	 * @see #sendBroadcastIPData(int, byte[])
	 * @see #sendIPData(Inet4Address, int, IPProtocol, byte[])
	 * @see #sendIPData(Inet4Address, int, IPProtocol, boolean, byte[])
	 * @see #sendIPDataAsync(Inet4Address, int, IPProtocol, boolean, byte[])
	 * @see com.digi.xbee.api.models.IPProtocol
	 * @see java.net.Inet4Address
	 */
	public void sendIPDataAsync(Inet4Address ipAddress, int destPort, 
			IPProtocol protocol, byte[] data) throws TimeoutException, XBeeException {
		sendIPDataAsyncImpl(ipAddress, destPort, protocol, false, data);
	}
	
	/**
	 * Sends the provided IP data to all clients.
	 * 
	 * <p>This method blocks till a success or error transmit status arrives or 
	 * the configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * @param destPort The destination port of the transmission.
	 * @param data Byte array containing the IP data to be sent.
	 * 
	 * @throws IllegalArgumentException if {@code destPort < 0} or 
	 *                                  if {@code destPort > 65535}
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #sendIPData(Inet4Address, int, IPProtocol, byte[])
	 * @see #sendIPData(Inet4Address, int, IPProtocol, boolean, byte[])
	 * @see #sendIPDataAsync(Inet4Address, int, IPProtocol, byte[])
	 * @see #sendIPDataAsync(Inet4Address, int, IPProtocol, boolean, byte[])
	 * @see #setReceiveTimeout(int)
	 */
	public void sendBroadcastIPData(int destPort, byte[] data) throws TimeoutException, XBeeException {
		try {
			sendIPData((Inet4Address) Inet4Address.getByName(BROADCAST_IP), destPort, IPProtocol.UDP, false, data);
		} catch (UnknownHostException e) {
			throw new XBeeException(e);
		}
	}
	
	/**
	 * Reads new IP data received by this XBee device during the 
	 * configured receive timeout.
	 * 
	 * <p>This method blocks until new IP data is received or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations, register a {@code IIPDataReceiveListener} 
	 * using the method {@link #addIPDataListener(IIPDataReceiveListener)}.</p>
	 * 
	 * <p>Before reading IP data you need to start listening for incoming 
	 * IP data at a specific port. Use the {@code startListening} method 
	 * for that purpose. When finished, you can use the {@code stopListening} 
	 * method to stop listening for incoming IP data.</p>
	 * 
	 * @return A {@code IPMessage} object containing the IP data and 
	 *         the IP address that sent the data. {@code null} if this did not 
	 *         receive new IP data during the configured receive timeout.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #readIPData(int)
	 * @see #readIPDataFrom(Inet4Address)
	 * @see #readIPDataFrom(Inet4Address, int)
	 * @see #setReceiveTimeout(int)
	 * @see #startListening(int)
	 * @see #stopListening()
	 * @see com.digi.xbee.api.models.IPMessage
	 */
	public IPMessage readIPData() {
		return readIPDataPacket(null, TIMEOUT_READ_PACKET);
	}
	
	/**
	 * Reads new IP data received by this XBee device during the provided 
	 * timeout.
	 * 
	 * <p>This method blocks until new IP data is received or the provided 
	 * timeout expires.</p>
	 * 
	 * <p>For non-blocking operations, register a {@code IIPDataReceiveListener} 
	 * using the method {@link #addIPDataListener(IIPDataReceiveListener)}.</p>
	 * 
	 * <p>Before reading IP data you need to start listening for incoming 
	 * IP data at a specific port. Use the {@code startListening} method 
	 * for that purpose. When finished, you can use the {@code stopListening} 
	 * method to stop listening for incoming IP data.</p>
	 * 
	 * @param timeout The time to wait for new IP data in milliseconds.
	 * 
	 * @return A {@code IPMessage} object containing the data and the IP 
	 *         address that sent the data. {@code null} if this device did not 
	 *         receive new data during {@code timeout} milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code timeout < 0}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * 
	 * @see #readIPData()
	 * @see #readIPDataFrom(Inet4Address)
	 * @see #readIPDataFrom(Inet4Address, int)
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
	 * Reads new IP data received from the given IP address during the 
	 * configured receive timeout.
	 * 
	 * <p>This method blocks until new data from the provided IP address is 
	 * received or the configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations, register a {@code IIPDataReceiveListener} 
	 * using the method {@link #addIPDataListener(IIPDataReceiveListener)}.</p>
	 * 
	 * <p>Before reading IP data you need to start listening for incoming 
	 * IP data at a specific port. Use the {@code startListening} method 
	 * for that purpose. When finished, you can use the {@code stopListening} 
	 * method to stop listening for incoming IP data.</p>
	 * 
	 * @param ipAddress The IP address to read data from.
	 * 
	 * @return A {@code IPMessage} object containing the IP data and 
	 *         the IP address of the remote node that sent the data. 
	 *         {@code null} if this device did not receive new IP data 
	 *         from the provided IP address during the configured receive 
	 *         timeout.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ipAddress == null}.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #readIPData()
	 * @see #readIPData(int)
	 * @see #readIPDataFrom(Inet4Address, int)
	 * @see #setReceiveTimeout(int)
	 * @see #startListening(int)
	 * @see #stopListening()
	 * @see com.digi.xbee.api.models.IPMessage
	 * @see java.net.Inet4Address
	 */
	public IPMessage readIPDataFrom(Inet4Address ipAddress) {
		if (ipAddress == null)
			throw new NullPointerException("IP address cannot be null.");
		
		return readIPDataPacket(ipAddress, TIMEOUT_READ_PACKET);
	}
	
	/**
	 * Reads new IP data received from the given IP address during the 
	 * provided timeout.
	 * 
	 * <p>This method blocks until new IP data from the provided IP 
	 * address is received or the given timeout expires.</p>
	 * 
	 * <p>For non-blocking operations, register a {@code IIPDataReceiveListener} 
	 * using the method {@link #addIPDataListener(IIPDataReceiveListener)}.</p>
	 * 
	 * <p>Before reading IP data you need to start listening for incoming 
	 * IP data at a specific port. Use the {@code startListening} method 
	 * for that purpose. When finished, you can use the {@code stopListening} 
	 * method to stop listening for incoming IP data.</p>
	 * 
	 * @param ipAddress The IP address to read data from.
	 * @param timeout The time to wait for new IP data in milliseconds.
	 * 
	 * @return An {@code IPMessage} object containing the IP data and 
	 *         the IP address that sent the data. {@code null} if this device 
	 *         did not receive new IP data from the provided IP address 
	 *         during {@code timeout} milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code timeout < 0}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ipAddress == null}.
	 * 
	 * @see #readIPDataFrom(Inet4Address)
	 * @see #readIPData()
	 * @see #readIPData(int)
	 * @see #startListening(int)
	 * @see #stopListening()
	 * @see com.digi.xbee.api.models.IPMessage
	 * @see java.net.Inet4Address
	 */
	public IPMessage readIPDataFrom(Inet4Address ipAddress, int timeout) {
		if (ipAddress == null)
			throw new NullPointerException("IP address cannot be null.");
		if (timeout < 0)
			throw new IllegalArgumentException("Read timeout must be 0 or greater.");
		
		return readIPDataPacket(ipAddress, timeout);
	}
	
	/**
	 * Reads a new IP data packet received by this IP XBee device during 
	 * the provided timeout.
	 * 
	 * <p>This method blocks until new IP data is received or the given 
	 * timeout expires.</p>
	 * 
	 * <p>If the provided IP address is {@code null} the method returns 
	 * the first IP data packet read from any IP address.
	 * <br>
	 * If the IP address is not {@code null} the method returns the first 
	 * data package read from the provided IP address.
	 * </p>
	 * 
	 * @param remoteIPAddress The IP address to get a IP data packet from. 
	 *                        {@code null} to read a IP data packet from 
	 *                        any IP address.
	 * @param timeout The time to wait for a IP data packet in milliseconds.
	 * 
	 * @return A {@code IPMessage} received by this device, containing the 
	 *         data and the source IP address that sent the IP data. 
	 *         {@code null} if this device did not receive new IP data 
	 *         during {@code timeout} milliseconds, or if any error occurs while
	 *         trying to get the source of the message.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * 
	 * @see com.digi.xbee.api.models.XBeeMessage
	 * @see java.net.Inet4Address
	 */
	private IPMessage readIPDataPacket(Inet4Address remoteIPAddress, int timeout) {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		XBeePacketsQueue xbeePacketsQueue = dataReader.getXBeePacketsQueue();
		XBeePacket xbeePacket = null;
		
		if (remoteIPAddress != null)
			xbeePacket = xbeePacketsQueue.getFirstIPDataPacketFrom(remoteIPAddress, timeout);
		else
			xbeePacket = xbeePacketsQueue.getFirstIPDataPacket(timeout);
		
		if (xbeePacket == null)
			return null;
		
		// Obtain the data and IP address from the packet.
		byte[] data = null;
		Inet4Address ipAddress = null;
		int sourcePort;
		int destPort;
		IPProtocol protocol = IPProtocol.TCP;
		
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
		
		// Create and return the IP message.
		return new IPMessage(ipAddress, sourcePort, destPort, protocol, data);
	}
	
	/**
	 * Sends the provided IP data to the given IP address and port using 
	 * the specified IP protocol. For TCP and TCP SSL protocols, you can 
	 * also indicate if the socket should be closed when data is sent.
	 * 
	 * <p>This method blocks till a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * @param ipAddress The IP address to send IP data to.
	 * @param destPort The destination port of the transmission.
	 * @param protocol The IP protocol used for the transmission.
	 * @param closeSocket {@code true} to close the socket just after the 
	 *                    transmission. {@code false} to keep it open.
	 * @param data Byte array containing the IP data to be sent.
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
	 * @see #sendBroadcastIPData(int, byte[])
	 * @see #sendIPData(Inet4Address, int, IPProtocol, byte[])
	 * @see #sendIPData(Inet4Address, int, IPProtocol, boolean, byte[])
	 * @see #sendIPDataAsync(Inet4Address, int, IPProtocol, byte[])
	 * @see #sendIPDataAsync(Inet4Address, int, IPProtocol, boolean, byte[])
	 * @see #setReceiveTimeout(int)
	 * @see com.digi.xbee.api.models.IPProtocol
	 * @see java.net.Inet4Address
	 */
	private void sendIPDataImpl(Inet4Address ipAddress, int destPort, 
			IPProtocol protocol, boolean closeSocket, byte[] data) 
					throws TimeoutException, XBeeException {
		if (ipAddress == null)
			throw new NullPointerException("IP address cannot be null");
		if (protocol == null)
			throw new NullPointerException("Protocol cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		
		if (destPort < 0 || destPort > 65535)
			throw new IllegalArgumentException("Destination port must be between 0 and 65535.");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send IP data from a remote device.");
		
		// The source port value depends on the protocol used in the transmission. For UDP, source port 
		// value must be the same as 'C0' one. For TCP it must be 0.
		int sourcePort = this.sourcePort;
		if (protocol != IPProtocol.UDP)
			sourcePort = 0;
		
		logger.debug(toString() + "Sending IP data to {}:{} >> {}.", ipAddress, destPort, HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket = new TXIPv4Packet(getNextFrameID(), ipAddress, destPort, 
				sourcePort, protocol, closeSocket ? TXIPv4Packet.OPTIONS_CLOSE_SOCKET: TXIPv4Packet.OPTIONS_LEAVE_SOCKET_OPEN, data);
		
		sendAndCheckXBeePacket(xbeePacket, false);
	}
	
	/**
	 * Sends the provided IP data to the given IP address and port 
	 * asynchronously using the specified IP protocol. For TCP and TCP SSL 
	 * protocols, you can also indicate if the socket should be closed when 
	 * data is sent.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote 
	 * device or for transmit status packet.</p>
	 * 
	 * @param ipAddress The IP address to send IP data to.
	 * @param destPort The destination port of the transmission.
	 * @param protocol The IP protocol used for the transmission.
	 * @param closeSocket {@code true} to close the socket just after the 
	 *                    transmission. {@code false} to keep it open.
	 * @param data Byte array containing the IP data to be sent.
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
	 * @see #sendBroadcastIPData(int, byte[])
	 * @see #sendIPData(Inet4Address, int, IPProtocol, byte[])
	 * @see #sendIPData(Inet4Address, int, IPProtocol, boolean, byte[])
	 * @see #sendIPDataAsync(Inet4Address, int, IPProtocol, byte[])
	 * @see #sendIPDataAsync(Inet4Address, int, IPProtocol, boolean, byte[])
	 * @see com.digi.xbee.api.models.IPProtocol
	 * @see java.net.Inet4Address
	 */
	private void sendIPDataAsyncImpl(Inet4Address ipAddress, int destPort, 
			IPProtocol protocol, boolean closeSocket, byte[] data) throws XBeeException {
		if (ipAddress == null)
			throw new NullPointerException("IP address cannot be null");
		if (protocol == null)
			throw new NullPointerException("Protocol cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		if (destPort < 0 || destPort > 65535)
			throw new IllegalArgumentException("Destination port must be between 0 and 65535.");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send IP data from a remote device.");
		
		// The source port value depends on the protocol used in the transmission. For UDP, source port 
		// value must be the same as 'C0' one. For TCP it must be 0.
		int sourcePort = this.sourcePort;
		if (protocol != IPProtocol.UDP)
			sourcePort = 0;
		
		logger.debug(toString() + "Sending IP data asynchronously to {}:{} >> {}.", ipAddress, destPort, HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket = new TXIPv4Packet(getNextFrameID(), ipAddress, destPort, sourcePort, 
				protocol, closeSocket ? TXIPv4Packet.OPTIONS_CLOSE_SOCKET: TXIPv4Packet.OPTIONS_LEAVE_SOCKET_OPEN, data);
		
		sendAndCheckXBeePacket(xbeePacket, true);
	}
}

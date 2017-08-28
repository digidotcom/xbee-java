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

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.connection.android.AndroidUSBPermissionListener;
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeDeviceException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.AssociationIndicationStatus;
import com.digi.xbee.api.models.CoAPURI;
import com.digi.xbee.api.models.HTTPMethodEnum;
import com.digi.xbee.api.models.IPProtocol;
import com.digi.xbee.api.models.RemoteATCommandOptions;
import com.digi.xbee.api.models.ThreadAssociationIndicationStatus;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.packet.thread.CoAPTxRequestPacket;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

import android.content.Context;

/**
 * This class represents a local Thread device.
 * 
 * @see CellularDevice
 * @see DigiPointDevice
 * @see DigiMeshDevice
 * @see Raw802Device
 * @see WiFiDevice
 * @see XBeeDevice
 * @see ZigBeeDevice
 * 
 * @since 1.2.1
 */
public class ThreadDevice extends IPv6Device {

	// Constants
	private static final String OPERATION_EXCEPTION = "Operation not supported in Thread protocol.";
	
	private static final String ERROR_PROTOCOL_ILLEGAL = String.format("Protocol must be %s or %s.", 
			IPProtocol.UDP.getName(), IPProtocol.COAP.getName());
	
	/**
	 * Class constructor. Instantiates a new {@code ThreadDevice} object in 
	 * the given port name and baud rate.
	 * 
	 * @param port Serial port name where Thread device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device. 
	 *                 Other connection parameters will be set as default (8 
	 *                 data bits, 1 stop bit, no parity, no flow control).
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see #ThreadDevice(IConnectionInterface)
	 * @see #ThreadDevice(String, SerialPortParameters)
	 * @see #ThreadDevice(String, int, int, int, int, int)
	 * @see #ThreadDevice(Context, int)
	 * @see #ThreadDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #ThreadDevice(Context, String, int)
	 * @see #ThreadDevice(Context, String, SerialPortParameters)
	 */
	public ThreadDevice(String port, int baudRate) {
		this(XBee.createConnectiontionInterface(port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code ThreadDevice} object in 
	 * the given serial port name and settings.
	 * 
	 * @param port Serial port name where Thread device is attached to.
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
	 * @see #ThreadDevice(IConnectionInterface)
	 * @see #ThreadDevice(String, int)
	 * @see #ThreadDevice(String, SerialPortParameters)
	 * @see #ThreadDevice(Context, int)
	 * @see #ThreadDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #ThreadDevice(Context, String, int)
	 * @see #ThreadDevice(Context, String, SerialPortParameters)
	 */
	public ThreadDevice(String port, int baudRate, int dataBits, int stopBits, int parity, int flowControl) {
		this(port, new SerialPortParameters(baudRate, dataBits, stopBits, parity, flowControl));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code ThreadDevice} object in 
	 * the given serial port name and parameters.
	 * 
	 * @param port Serial port name where Thread device is attached to.
	 * @param serialPortParameters Object containing the serial port parameters.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code serialPortParameters == null}.
	 * 
	 * @see #ThreadDevice(IConnectionInterface)
	 * @see #ThreadDevice(String, int)
	 * @see #ThreadDevice(String, int, int, int, int, int)
	 * @see #ThreadDevice(Context, int)
	 * @see #ThreadDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #ThreadDevice(Context, String, int)
	 * @see #ThreadDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	public ThreadDevice(String port, SerialPortParameters serialPortParameters) {
		this(XBee.createConnectiontionInterface(port, serialPortParameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code ThreadDevice} object for
	 * Android with the given parameters.
	 * 
	 * @param context The Android context.
	 * @param baudRate The USB connection baud rate.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 1}.
	 * @throws NullPointerException if {@code context == null}.
	 * 
	 * @see #ThreadDevice(IConnectionInterface)
	 * @see #ThreadDevice(String, int)
	 * @see #ThreadDevice(String, int, int, int, int, int)
	 * @see #ThreadDevice(String, SerialPortParameters)
	 * @see #ThreadDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #ThreadDevice(Context, String, int)
	 * @see #ThreadDevice(Context, String, SerialPortParameters)
	 */
	public ThreadDevice(Context context, int baudRate) {
		super(XBee.createConnectiontionInterface(context, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code ThreadDevice} object for
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
	 * @see #ThreadDevice(IConnectionInterface)
	 * @see #ThreadDevice(String, int)
	 * @see #ThreadDevice(String, int, int, int, int, int)
	 * @see #ThreadDevice(String, SerialPortParameters)
	 * @see #ThreadDevice(Context, int)
	 * @see #ThreadDevice(Context, String, int)
	 * @see #ThreadDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.android.AndroidUSBPermissionListener
	 */
	public ThreadDevice(Context context, int baudRate, AndroidUSBPermissionListener permissionListener) {
		super(XBee.createConnectiontionInterface(context, baudRate, permissionListener));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code ThreadDevice} object for
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
	 * @see #ThreadDevice(IConnectionInterface)
	 * @see #ThreadDevice(String, int)
	 * @see #ThreadDevice(String, int, int, int, int, int)
	 * @see #ThreadDevice(String, SerialPortParameters)
	 * @see #ThreadDevice(Context, int)
	 * @see #ThreadDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #ThreadDevice(Context, String, SerialPortParameters)
	 */
	public ThreadDevice(Context context, String port, int baudRate) {
		super(XBee.createConnectiontionInterface(context, port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code ThreadDevice} object for
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
	 * @see #ThreadDevice(IConnectionInterface)
	 * @see #ThreadDevice(String, int)
	 * @see #ThreadDevice(String, int, int, int, int, int)
	 * @see #ThreadDevice(String, SerialPortParameters)
	 * @see #ThreadDevice(Context, int)
	 * @see #ThreadDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #ThreadDevice(Context, String, int)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	public ThreadDevice(Context context, String port, SerialPortParameters parameters) {
		super(XBee.createConnectiontionInterface(context, port, parameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code ThreadDevice} object with 
	 * the given connection interface.
	 * 
	 * @param connectionInterface The connection interface with the physical 
	 *                            Thread device.
	 * 
	 * @throws NullPointerException if {@code connectionInterface == null}
	 * 
	 * @see #ThreadDevice(String, int)
	 * @see #ThreadDevice(String, SerialPortParameters)
	 * @see #ThreadDevice(String, int, int, int, int, int)
	 * @see #ThreadDevice(Context, int)
	 * @see #ThreadDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #ThreadDevice(Context, String, int)
	 * @see #ThreadDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 */
	public ThreadDevice(IConnectionInterface connectionInterface) {
		super(connectionInterface);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#open()
	 */
	@Override
	public void open() throws XBeeException {
		super.open();
		if (xbeeProtocol != XBeeProtocol.THREAD)
			throw new XBeeDeviceException("XBee device is not a " + getXBeeProtocol().getDescription() +
					" device, it is a " + xbeeProtocol.getDescription() + " device.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#getXBeeProtocol()
	 */
	@Override
	public XBeeProtocol getXBeeProtocol() {
		return XBeeProtocol.THREAD;
	}
	
	/**
	 * Returns whether the device is associated to a network or not.
	 * 
	 * @return {@code true} if the device is connected to a network, 
	 *         {@code false} otherwise.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout getting the association 
	 *                          indication status.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getThreadAssociationIndicationStatus()
	 * @see com.digi.xbee.api.models.ThreadAssociationIndicationStatus
	 */
	public boolean isConnected() throws TimeoutException, XBeeException {
		ThreadAssociationIndicationStatus status = getThreadAssociationIndicationStatus();
		return status == ThreadAssociationIndicationStatus.ASSOCIATED;
	}
	
	/**
	 * Returns the current association status of this Thread device.
	 * 
	 * @return The association indication status of the Thread device.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout getting the association 
	 *                          indication status.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see com.digi.xbee.api.models.ThreadAssociationIndicationStatus
	 */
	public ThreadAssociationIndicationStatus getThreadAssociationIndicationStatus() throws TimeoutException, 
			XBeeException {
		byte[] associationIndicationValue = getParameter("AI");
		return ThreadAssociationIndicationStatus.get(ByteUtils.byteArrayToInt(associationIndicationValue));
	}
	
	/**
	 * @deprecated Operation not supported in Thread protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	protected AssociationIndicationStatus getAssociationIndicationStatus()
			throws TimeoutException, XBeeException {
		// Not supported in Thread.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * Sends the provided CoAP IPv6 data to the given IPv6 address and port using 
	 * the specified IPv6 protocol.
	 * 
	 * <p>This method blocks till a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations use the method 
	 * {@link #sendCoAPDataAsync(Inet6Address, String, HTTPMethodEnum, byte[])}.</p>
	 * 
	 * @param ipv6Address The IPv6 address to send CoAP IPv6 data to.
	 * @param uri Uniform Resource Identifier. Every CoAP message must have a 
	 *            valid URI string and must meet the following criteria. There 
	 *            are built-in CoAP URIs:
	 *            <ul>
	 *              <li><b>CoAPURI.URI_DATA_TRANSMISSION:</b> "XB/TX" 
	 *              for data transmissions (HTTP method must be set to PUT)</li>
	 *              <li><b>CoAPURI.URI_AT_COMMAND:</b> "XB/AT" for 
	 *              AT Command operation (HTTP method must be set to PUT or GET). 
	 *              After the URI, an AT command needs to be specified, for example: 
	 *              CoAPURI.URI_AT_COMMAND + "/NI"</li>
	 *              <li><b>CoAPURI.URI_IO_SAMPLING:</b> "XB/IO" for 
	 *              IO operation (HTTP method must be set to POST)</li>
	 *            </ul>
	 * @param method HTTP method used for the transmission.
	 * @param data Byte array containing the CoAP IPv6 data to be sent.
	 * 
	 * @return A byte array containing the response, if any. Otherwise, {@code null}.
	 * 
	 * @throws IllegalArgumentException if {@code uri} starts with "XB/AT" and 
	 *                                  its length is lesser than 8 (XB/AT/XX).
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ipv6Address == null} or 
	 *                              if {@code uri == null} or 
	 *                              if {@code method == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #sendCoAPData(Inet6Address, String, HTTPMethodEnum, boolean, byte[])
	 * @see #sendCoAPDataAsync(Inet6Address, String, HTTPMethodEnum, byte[])
	 * @see #sendCoAPDataAsync(Inet6Address, String, HTTPMethodEnum, boolean, byte[])
	 * @see #setReceiveTimeout(int)
	 * @see com.digi.xbee.api.models.HTTPMethodEnum
	 * @see java.net.Inet6Address
	 */
	public byte[] sendCoAPData(Inet6Address ipv6Address, String uri,
			HTTPMethodEnum method, byte[] data) throws TimeoutException, IllegalArgumentException, XBeeException {
		boolean applyChanges = uri.startsWith(CoAPURI.URI_AT_COMMAND);
		return sendCoAPData(ipv6Address, uri, method, applyChanges, data, false);
	}
	
	/**
	 * Sends the provided CoAP IPv6 data to the given IPv6 address and port using 
	 * the specified IPv6 protocol.
	 * 
	 * <p>This method blocks till a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations use the method 
	 * {@link #sendCoAPDataAsync(Inet6Address, String, HTTPMethodEnum, 
	 * boolean, byte[])}.</p>
	 * 
	 * @param ipv6Address The IPv6 address to send CoAP IPv6 data to.
	 * @param uri Uniform Resource Identifier. Every CoAP message must have a 
	 *            valid URI string and must meet the following criteria. There 
	 *            are built-in CoAP URIs:
	 *            <ul>
	 *              <li><b>CoAPURI.URI_DATA_TRANSMISSION:</b> "XB/TX" 
	 *              for data transmissions (HTTP method must be set to PUT)</li>
	 *              <li><b>CoAPURI.URI_AT_COMMAND:</b> "XB/AT" for 
	 *              AT Command operation (HTTP method must be set to PUT or GET). 
	 *              After the URI, an AT command needs to be specified, for example: 
	 *              CoAPURI.URI_AT_COMMAND + "/NI"</li>
	 *              <li><b>CoAPURI.URI_IO_SAMPLING:</b> "XB/IO" for 
	 *              IO operation (HTTP method must be set to POST)</li>
	 *            </ul>
	 * @param method HTTP method used for the transmission.
	 * @param applyChanges {@code true} to apply the changes after sending the 
	 *                     packet, {@code false} otherwise.
	 * @param data Byte array containing the CoAP IPv6 data to be sent.
	 * 
	 * @return A byte array containing the response, if any. Otherwise, {@code null}.
	 * 
	 * @throws IllegalArgumentException if {@code uri} starts with "XB/AT" and 
	 *                                  its length is lesser than 8 (XB/AT/XX).
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ipv6Address == null} or 
	 *                              if {@code uri == null} or 
	 *                              if {@code method == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #sendCoAPData(Inet6Address, String, HTTPMethodEnum, byte[])
	 * @see #sendCoAPDataAsync(Inet6Address, String, HTTPMethodEnum, byte[])
	 * @see #sendCoAPDataAsync(Inet6Address, String, HTTPMethodEnum, boolean, byte[])
	 * @see #setReceiveTimeout(int)
	 * @see com.digi.xbee.api.models.HTTPMethodEnum
	 * @see java.net.Inet6Address
	 */
	public byte[] sendCoAPData(Inet6Address ipv6Address, String uri, HTTPMethodEnum method,
			boolean applyChanges, byte[] data) throws TimeoutException, IllegalArgumentException, XBeeException {
		return sendCoAPData(ipv6Address, uri, method, applyChanges, data, false);
	}
	
	/**
	 * Sends the provided CoAP IPv6 data to the given IPv6 address and port 
	 * asynchronously using the specified IPv6 protocol.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote 
	 * device or for transmit status packet.</p>
	 * 
	 * <p>For blocking operations use the method 
	 * {@link #sendCoAPData(Inet6Address, String, HTTPMethodEnum, byte[])}.</p>
	 * 
	 * @param ipv6Address The IPv6 address to send CoAP IPv6 data to.
	 * @param uri Uniform Resource Identifier. Every CoAP message must have a 
	 *            valid URI string and must meet the following criteria. There 
	 *            are built-in CoAP URIs:
	 *            <ul>
	 *              <li><b>CoAPURI.URI_DATA_TRANSMISSION:</b> "XB/TX" 
	 *              for data transmissions (PUT)</li>
	 *              <li><b>CoAPURI.URI_AT_COMMAND:</b> "XB/AT" for 
	 *              AT Command operation (PUT or GET). After the URI, an AT command 
	 *              needs to be specified, for example: 
	 *              CoAPURI.URI_AT_COMMAND + "/NI"</li>
	 *              <li><b>CoAPURI.URI_IO_SAMPLING:</b> "XB/IO" for 
	 *              IO operation (POST)</li>
	 *            </ul>
	 * @param method HTTP method used for the transmission.
	 * @param data Byte array containing the CoAP IPv6 data to be sent.
	 * 
	 * @throws IllegalArgumentException if {@code uri} starts with "XB/AT" and 
	 *                                  its length is lesser than 8 (XB/AT/XX).
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ipv6Address == null} or 
	 *                              if {@code uri == null} or 
	 *                              if {@code method == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #sendCoAPData(Inet6Address, String, HTTPMethodEnum, byte[])
	 * @see #sendCoAPData(Inet6Address, String, HTTPMethodEnum, boolean, byte[])
	 * @see #sendCoAPDataAsync(Inet6Address, String, HTTPMethodEnum, boolean, byte[])
	 * @see #setReceiveTimeout(int)
	 * @see com.digi.xbee.api.models.HTTPMethodEnum
	 * @see java.net.Inet6Address
	 */
	public void sendCoAPDataAsync(Inet6Address ipv6Address, String uri,
			HTTPMethodEnum method, byte[] data) throws TimeoutException, IllegalArgumentException, XBeeException {
		boolean applyChanges = uri.startsWith(CoAPURI.URI_AT_COMMAND);
		sendCoAPData(ipv6Address, uri, method, applyChanges, data, true);
	}
	
	/**
	 * Sends the provided CoAP IPv6 data to the given IPv6 address and port 
	 * asynchronously using the specified IPv6 protocol.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote 
	 * device or for transmit status packet.</p>
	 * 
	 * <p>For blocking operations use the method 
	 * {@link #sendCoAPData(Inet6Address, String, HTTPMethodEnum, boolean, byte[])}.</p>
	 * 
	 * @param ipv6Address The IPv6 address to send CoAP IPv6 data to.
	 * @param uri Uniform Resource Identifier. Every CoAP message must have a 
	 *            valid URI string and must meet the following criteria. There 
	 *            are built-in CoAP URIs:
	 *            <ul>
	 *              <li><b>CoAPURI.URI_DATA_TRANSMISSION:</b> "XB/TX" 
	 *              for data transmissions (PUT)</li>
	 *              <li><b>CoAPURI.URI_AT_COMMAND:</b> "XB/AT" for 
	 *              AT Command operation (PUT or GET). After the URI, an AT command 
	 *              needs to be specified, for example: 
	 *              CoAPURI.URI_AT_COMMAND + "/NI"</li>
	 *              <li><b>CoAPURI.URI_IO_SAMPLING:</b> "XB/IO" for 
	 *              IO operation (POST)</li>
	 *            </ul>
	 * @param method HTTP method used for the transmission.
	 * @param applyChanges {@code true} to apply the changes after sending the 
	 *                     packet, {@code false} otherwise.
	 * @param data Byte array containing the CoAP IPv6 data to be sent.
	 * 
	 * @throws IllegalArgumentException if {@code uri} starts with "XB/AT" and 
	 *                                  its length is lesser than 8 (XB/AT/XX).
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ipv6Address == null} or 
	 *                              if {@code uri == null} or 
	 *                              if {@code method == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #sendCoAPData(Inet6Address, String, HTTPMethodEnum, byte[])
	 * @see #sendCoAPData(Inet6Address, String, HTTPMethodEnum, boolean, byte[])
	 * @see #sendCoAPDataAsync(Inet6Address, String, HTTPMethodEnum, byte[])
	 * @see #setReceiveTimeout(int)
	 * @see com.digi.xbee.api.models.HTTPMethodEnum
	 * @see java.net.Inet6Address
	 */
	public void sendCoAPDataAsync(Inet6Address ipv6Address, String uri, HTTPMethodEnum method,
			boolean applyChanges, byte[] data) throws TimeoutException, IllegalArgumentException, XBeeException {
		sendCoAPData(ipv6Address, uri, method, applyChanges, data, true);
	}
	
	/**
	 * Sends the provided CoAP IPv6 data to the given IPv6 address and port 
	 * using the specified IPv6 protocol.
	 * 
	 * <p>CoAP transmissions can be performed synchronously or asynchronously. 
	 * Synchronous operations block till a success or error response arrives 
	 * or the configured receive timeout expires. Asynchronous transmissions
	 * do not wait for answer from the remote device or for transmit status 
	 * packet.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For synchronous operations use these methods:</p>
	 * <ul>
	 *   <li>{@link #sendCoAPData(Inet6Address, String, HTTPMethodEnum, byte[])}.</li>
	 *   <li>{@link #sendCoAPData(Inet6Address, String, HTTPMethodEnum, boolean, byte[])}.</li>
	 * </ul>
	 * <p>For asynchronous operations use these ones:</p>
	 * <ul>
	 *   <li>{@link #sendCoAPDataAsync(Inet6Address, String, HTTPMethodEnum, byte[])}.</li>
	 *   <li>{@link #sendCoAPDataAsync(Inet6Address, String, HTTPMethodEnum, boolean, byte[])}.</li>
	 * </ul>
	 * 
	 * @param ipv6Address The IPv6 address to send CoAP IPv6 data to.
	 * @param uri Uniform Resource Identifier. Every CoAP message must have a 
	 *            valid URI string and must meet the following criteria. There 
	 *            are built-in CoAP URIs:
	 *            <ul>
	 *              <li><b>CoAPURI.URI_DATA_TRANSMISSION:</b> "XB/TX" 
	 *              for data transmissions (PUT)</li>
	 *              <li><b>CoAPURI.URI_AT_COMMAND:</b> "XB/AT" for 
	 *              AT Command operation (PUT or GET). After the URI, an AT command 
	 *              needs to be specified, for example: 
	 *              CoAPURI.URI_AT_COMMAND + "/NI"</li>
	 *              <li><b>CoAPURI.URI_IO_SAMPLING:</b> "XB/IO" for 
	 *              IO operation (POST)</li>
	 *            </ul>
	 * @param method HTTP method used for the transmission.
	 * @param applyChanges {@code true} to apply the changes after sending the 
	 *                     packet, {@code false} otherwise.
	 * @param data Byte array containing the CoAP IPv6 data to be sent.
	 * @param async {@code true} to make an asynchronous transmission, {@code false} 
	 *              to block till a success or error response arrives or the 
	 *              configured receive timeout expires .
	 * 
	 * @return A byte array containing the response, if any. Otherwise, {@code null}.
	 * 
	 * @throws IllegalArgumentException if {@code uri} starts with "XB/AT" and 
	 *                                  its length is lesser than 8 (XB/AT/XX).
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ipv6Address == null} or 
	 *                              if {@code uri == null} or 
	 *                              if {@code method == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #sendCoAPData(Inet6Address, String, HTTPMethodEnum, byte[])
	 * @see #sendCoAPData(Inet6Address, String, HTTPMethodEnum, boolean, byte[])
	 * @see #sendCoAPDataAsync(Inet6Address, String, HTTPMethodEnum, byte[])
	 * @see #sendCoAPDataAsync(Inet6Address, String, HTTPMethodEnum, boolean, byte[])
	 * @see #setReceiveTimeout(int)
	 * @see com.digi.xbee.api.models.HTTPMethodEnum
	 * @see java.net.Inet6Address
	 */
	private byte[] sendCoAPData(Inet6Address ipv6Address, String uri, HTTPMethodEnum method,
			boolean applyChanges, byte[] data, boolean async) throws TimeoutException, IllegalArgumentException, XBeeException {
		if (ipv6Address == null)
			throw new NullPointerException("IPv6 address cannot be null");
		if (uri == null)
			throw new NullPointerException("Uri cannot be null");
		if (method == null)
			throw new NullPointerException("HTTP method cannot be null");
		
		// If AT command uri is used but no AT command is specified throw an error.
		if (uri.startsWith(CoAPURI.URI_AT_COMMAND) 
			&& uri.length() < CoAPURI.URI_AT_COMMAND.length() + 3)
			throw new IllegalArgumentException("AT command URI must contain an AT command.");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send CoAP IPv6 data from a remote device.");
		
		if (async)
			logger.debug(toString() + "Sending CoAP IPv6 data asynchronously to {} >> {}.", ipv6Address,
					HexUtils.prettyHexString(data));
		else
			logger.debug(toString() + "Sending CoAP IPv6 data to {} >> {}.", ipv6Address,
					HexUtils.prettyHexString(data));
		
		CoAPTxRequestPacket coAPPacket = new CoAPTxRequestPacket(getNextFrameID(),
				applyChanges ? RemoteATCommandOptions.OPTION_APPLY_CHANGES: RemoteATCommandOptions.OPTION_NONE,
				method, ipv6Address, uri, data);
		
		// Check for a transmit status and CoAP RX Response.
		return sendAndCheckCoAPPacket(coAPPacket, async);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.IPv6Device#sendIPData(java.net.Inet6Address, int, com.digi.xbee.api.models.IPProtocol, byte[])
	 */
	@Override
	public void sendIPData(Inet6Address ipv6Address, int destPort,
			IPProtocol protocol, byte[] data) throws TimeoutException,
			XBeeException {
		if (protocol != IPProtocol.UDP && protocol != IPProtocol.COAP)
			throw new IllegalArgumentException(ERROR_PROTOCOL_ILLEGAL);
		super.sendIPData(ipv6Address, destPort, protocol, data);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.IPv6Device#sendIPDataAsync(java.net.Inet6Address, int, com.digi.xbee.api.models.IPProtocol, byte[])
	 */
	@Override
	public void sendIPDataAsync(Inet6Address ipv6Address, int destPort,
			IPProtocol protocol, byte[] data) throws XBeeException {
		if (protocol != IPProtocol.UDP && protocol != IPProtocol.COAP)
			throw new IllegalArgumentException(ERROR_PROTOCOL_ILLEGAL);
		super.sendIPDataAsync(ipv6Address, destPort, protocol, data);
	}
}

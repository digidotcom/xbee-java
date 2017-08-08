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

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.connection.android.AndroidUSBPermissionListener;
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.ISMSReceiveListener;
import com.digi.xbee.api.models.IPProtocol;

import android.content.Context;

/**
 * This class provides common functionality for XBee Low-Power Wide-Area Network
 * devices.
 * 
 * @see CellularDevice
 * @see NBIoTDevice
 * @see XBeeDevice
 * 
 * @since 1.2.1
 */
public class LPWANDevice extends CellularDevice {
	
	// Constants
	private static final String OPERATION_EXCEPTION = "Operation not supported in this module.";
	
	private static final String ONLY_UDP_TRANSMISSIONS = "This protocol only supports UDP transmissions.";
	
	/**
	 * Class constructor. Instantiates a new {@code LPWANDevice} object in 
	 * the given port name and baud rate.
	 * 
	 * @param port Serial port name where LP WAN device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device. 
	 *                 Other connection parameters will be set as default (8 
	 *                 data bits, 1 stop bit, no parity, no flow control).
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see #LPWANDevice(IConnectionInterface)
	 * @see #LPWANDevice(String, SerialPortParameters)
	 * @see #LPWANDevice(String, int, int, int, int, int)
	 * @see #LPWANDevice(Context, int)
	 * @see #LPWANDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #LPWANDevice(Context, String, int)
	 * @see #LPWANDevice(Context, String, SerialPortParameters)
	 */
	protected LPWANDevice(String port, int baudRate) {
		this(XBee.createConnectiontionInterface(port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code LPWANDevice} object in 
	 * the given serial port name and settings.
	 * 
	 * @param port Serial port name where LP WAN device is attached to.
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
	 * @see #LPWANDevice(IConnectionInterface)
	 * @see #LPWANDevice(String, int)
	 * @see #LPWANDevice(String, SerialPortParameters)
	 * @see #LPWANDevice(Context, int)
	 * @see #LPWANDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #LPWANDevice(Context, String, int)
	 * @see #LPWANDevice(Context, String, SerialPortParameters)
	 */
	protected LPWANDevice(String port, int baudRate, int dataBits, int stopBits, int parity, int flowControl) {
		this(port, new SerialPortParameters(baudRate, dataBits, stopBits, parity, flowControl));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code LPWANDevice} object in 
	 * the given serial port name and parameters.
	 * 
	 * @param port Serial port name where LP WAN device is attached to.
	 * @param serialPortParameters Object containing the serial port parameters.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code serialPortParameters == null}.
	 * 
	 * @see #LPWANDevice(IConnectionInterface)
	 * @see #LPWANDevice(String, int)
	 * @see #LPWANDevice(String, int, int, int, int, int)
	 * @see #LPWANDevice(Context, int)
	 * @see #LPWANDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #LPWANDevice(Context, String, int)
	 * @see #LPWANDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	protected LPWANDevice(String port, SerialPortParameters serialPortParameters) {
		this(XBee.createConnectiontionInterface(port, serialPortParameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code LPWANDevice} object for
	 * Android with the given parameters.
	 * 
	 * @param context The Android context.
	 * @param baudRate The USB connection baud rate.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 1}.
	 * @throws NullPointerException if {@code context == null}.
	 * 
	 * @see #LPWANDevice(IConnectionInterface)
	 * @see #LPWANDevice(String, int)
	 * @see #LPWANDevice(String, int, int, int, int, int)
	 * @see #LPWANDevice(String, SerialPortParameters)
	 * @see #LPWANDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #LPWANDevice(Context, String, int)
	 * @see #LPWANDevice(Context, String, SerialPortParameters)
	 */
	protected LPWANDevice(Context context, int baudRate) {
		super(XBee.createConnectiontionInterface(context, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code LPWANDevice} object for
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
	 * @see #LPWANDevice(IConnectionInterface)
	 * @see #LPWANDevice(String, int)
	 * @see #LPWANDevice(String, int, int, int, int, int)
	 * @see #LPWANDevice(String, SerialPortParameters)
	 * @see #LPWANDevice(Context, int)
	 * @see #LPWANDevice(Context, String, int)
	 * @see #LPWANDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.android.AndroidUSBPermissionListener
	 */
	protected LPWANDevice(Context context, int baudRate, AndroidUSBPermissionListener permissionListener) {
		super(XBee.createConnectiontionInterface(context, baudRate, permissionListener));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code LPWANDevice} object for
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
	 * @see #LPWANDevice(IConnectionInterface)
	 * @see #LPWANDevice(String, int)
	 * @see #LPWANDevice(String, int, int, int, int, int)
	 * @see #LPWANDevice(String, SerialPortParameters)
	 * @see #LPWANDevice(Context, int)
	 * @see #LPWANDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #LPWANDevice(Context, String, SerialPortParameters)
	 */
	protected LPWANDevice(Context context, String port, int baudRate) {
		super(XBee.createConnectiontionInterface(context, port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code LPWANDevice} object for
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
	 * @see #LPWANDevice(IConnectionInterface)
	 * @see #LPWANDevice(String, int)
	 * @see #LPWANDevice(String, int, int, int, int, int)
	 * @see #LPWANDevice(String, SerialPortParameters)
	 * @see #LPWANDevice(Context, int)
	 * @see #LPWANDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #LPWANDevice(Context, String, int)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	protected LPWANDevice(Context context, String port, SerialPortParameters parameters) {
		super(XBee.createConnectiontionInterface(context, port, parameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code LPWANDevice} object with 
	 * the given connection interface.
	 * 
	 * @param connectionInterface The connection interface with the physical 
	 *                            IP device.
	 * 
	 * @throws NullPointerException if {@code connectionInterface == null}
	 * 
	 * @see #LPWANDevice(String, int)
	 * @see #LPWANDevice(String, SerialPortParameters)
	 * @see #LPWANDevice(String, int, int, int, int, int)
	 * @see #LPWANDevice(Context, int)
	 * @see #LPWANDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #LPWANDevice(Context, String, int)
	 * @see #LPWANDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 */
	protected LPWANDevice(IConnectionInterface connectionInterface) {
		super(connectionInterface);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void addSMSListener(ISMSReceiveListener listener) {
		// Not supported in Cellular.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void removeSMSListener(ISMSReceiveListener listener) {
		// Not supported in Cellular.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void sendSMS(String phoneNumber, String data)
			throws TimeoutException, XBeeException {
		// Not supported in Cellular.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void sendSMSAsync(String phoneNumber, String data)
			throws TimeoutException, XBeeException {
		// Not supported in Cellular.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * Sends the provided IP data to the given IP address and port using 
	 * the specified IP protocol (must be UDP).
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
	 * @throws IllegalArgumentException if {@code protocol != IPProtocol.UDP}.
	 */
	@Override
	public void sendIPData(Inet4Address ipAddress, int destPort, IPProtocol protocol, 
			byte[] data) throws TimeoutException, XBeeException {
		if (protocol != IPProtocol.UDP)
			throw new IllegalArgumentException(ONLY_UDP_TRANSMISSIONS);
		super.sendIPData(ipAddress, destPort, protocol, data);
	}
	
	/**
	 * @deprecated Use {@link #sendIPData(Inet4Address, int, IPProtocol, byte[])} 
	 *             instead.
	 */
	@Override
	public void sendIPData(Inet4Address ipAddress, int destPort, IPProtocol protocol, 
			boolean closeSocket, byte[] data) throws TimeoutException, XBeeException {
		sendIPData(ipAddress, destPort, protocol, data);
	}
	
	/**
	 * Sends the provided IP data to the given IP address and port 
	 * asynchronously using the specified protocol (must be UDP).
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote 
	 * device or for transmit status packet.</p>
	 * 
	 * @throws IllegalArgumentException if {@code protocol != IPProtocol.UDP}.
	 */
	@Override
	public void sendIPDataAsync(Inet4Address ipAddress, int destPort, IPProtocol protocol, 
			byte[] data) throws TimeoutException, XBeeException {
		if (protocol != IPProtocol.UDP)
			throw new IllegalArgumentException(ONLY_UDP_TRANSMISSIONS);
		super.sendIPDataAsync(ipAddress, destPort, protocol, data);
	}
	
	/**
	 * @deprecated Use {@link #sendIPDataAsync(Inet4Address, int, IPProtocol, byte[])} 
	 *             instead.
	 */
	@Override
	public void sendIPDataAsync(Inet4Address ipAddress, int destPort, IPProtocol protocol, 
			boolean closeSocket, byte[] data) throws XBeeException {
		sendIPDataAsync(ipAddress, destPort, protocol, data);
	}

}

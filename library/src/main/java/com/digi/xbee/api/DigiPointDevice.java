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

import android.content.Context;

import java.net.Inet6Address;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.connection.android.AndroidUSBPermissionListener;
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeDeviceException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IExplicitDataReceiveListener;
import com.digi.xbee.api.models.APIOutputMode;
import com.digi.xbee.api.models.ExplicitXBeeMessage;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;

/**
 * This class represents a local DigiPoint device.
 * 
 * @see CellularDevice
 * @see DigiMeshDevice
 * @see Raw802Device
 * @see ThreadDevice
 * @see WiFiDevice
 * @see XBeeDevice
 * @see ZigBeeDevice
 */
public class DigiPointDevice extends XBeeDevice {

	// Constants
	private static final String OPERATION_EXCEPTION = "Operation not supported in DigiPoint protocol.";
	
	/**
	 * Class constructor. Instantiates a new {@code DigiPointDevice} object in the 
	 * given port name and baud rate.
	 * 
	 * @param port Serial port name where point-to-multipoint device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device. 
	 *                 Other connection parameters will be set as default (8 
	 *                 data bits, 1 stop bit, no parity, no flow control).
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see #DigiPointDevice(IConnectionInterface)
	 * @see #DigiPointDevice(String, SerialPortParameters)
	 * @see #DigiPointDevice(String, int, int, int, int, int)
	 * @see #DigiPointDevice(Context, int)
	 * @see #DigiPointDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #DigiPointDevice(Context, String, int)
	 * @see #DigiPointDevice(Context, String, SerialPortParameters)
	 */
	public DigiPointDevice(String port, int baudRate) {
		this(XBee.createConnectiontionInterface(port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code DigiPointDevice} object in the 
	 * given serial port name and settings.
	 * 
	 * @param port Serial port name where point-to-multipoint device is attached to.
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
	 * @see #DigiPointDevice(IConnectionInterface)
	 * @see #DigiPointDevice(String, int)
	 * @see #DigiPointDevice(String, SerialPortParameters)
	 * @see #DigiPointDevice(Context, int)
	 * @see #DigiPointDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #DigiPointDevice(Context, String, int)
	 * @see #DigiPointDevice(Context, String, SerialPortParameters)
	 */
	public DigiPointDevice(String port, int baudRate, int dataBits, int stopBits, int parity, int flowControl) {
		this(port, new SerialPortParameters(baudRate, dataBits, stopBits, parity, flowControl));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code DigiPointDevice} object in the 
	 * given serial port name and parameters.
	 * 
	 * @param port Serial port name where point-to-multipoint device is attached to.
	 * @param serialPortParameters Object containing the serial port parameters.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code serialPortParameters == null}.
	 * 
	 * @see #DigiPointDevice(IConnectionInterface)
	 * @see #DigiPointDevice(String, int)
	 * @see #DigiPointDevice(String, int, int, int, int, int)
	 * @see #DigiPointDevice(Context, int)
	 * @see #DigiPointDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #DigiPointDevice(Context, String, int)
	 * @see #DigiPointDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	public DigiPointDevice(String port, SerialPortParameters serialPortParameters) {
		this(XBee.createConnectiontionInterface(port, serialPortParameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code DigiPointDevice} object for
	 * Android with the given parameters.
	 * 
	 * @param context The Android context.
	 * @param baudRate The USB connection baud rate.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 1}.
	 * @throws NullPointerException if {@code context == null}.
	 * 
	 * @see #DigiPointDevice(IConnectionInterface)
	 * @see #DigiPointDevice(String, int)
	 * @see #DigiPointDevice(String, SerialPortParameters)
	 * @see #DigiPointDevice(String, int, int, int, int, int)
	 * @see #DigiPointDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #DigiPointDevice(Context, String, int)
	 * @see #DigiPointDevice(Context, String, SerialPortParameters)
	 * 
	 * @since 1.2.0
	 */
	public DigiPointDevice(Context context, int baudRate) {
		super(XBee.createConnectiontionInterface(context, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code DigiPointDevice} object for
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
	 * @see #DigiPointDevice(IConnectionInterface)
	 * @see #DigiPointDevice(String, int)
	 * @see #DigiPointDevice(String, SerialPortParameters)
	 * @see #DigiPointDevice(String, int, int, int, int, int)
	 * @see #DigiPointDevice(Context, int)
	 * @see #DigiPointDevice(Context, String, int)
	 * @see #DigiPointDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.android.AndroidUSBPermissionListener
	 * 
	 * @since 1.2.0
	 */
	public DigiPointDevice(Context context, int baudRate, AndroidUSBPermissionListener permissionListener) {
		super(XBee.createConnectiontionInterface(context, baudRate, permissionListener));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code DigiPointDevice} object for
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
	 * @see #DigiPointDevice(IConnectionInterface)
	 * @see #DigiPointDevice(String, int)
	 * @see #DigiPointDevice(String, SerialPortParameters)
	 * @see #DigiPointDevice(String, int, int, int, int, int)
	 * @see #DigiPointDevice(Context, int)
	 * @see #DigiPointDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #DigiPointDevice(Context, String, SerialPortParameters)
	 * 
	 * @since 1.2.0
	 */
	public DigiPointDevice(Context context, String port, int baudRate) {
		super(XBee.createConnectiontionInterface(context, port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code DigiPointDevice} object for
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
	 * @see #DigiPointDevice(IConnectionInterface)
	 * @see #DigiPointDevice(String, int)
	 * @see #DigiPointDevice(String, SerialPortParameters)
	 * @see #DigiPointDevice(String, int, int, int, int, int)
	 * @see #DigiPointDevice(Context, int)
	 * @see #DigiPointDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #DigiPointDevice(Context, String, int)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 * 
	 * @since 1.2.0
	 */
	public DigiPointDevice(Context context, String port, SerialPortParameters parameters) {
		super(XBee.createConnectiontionInterface(context, port, parameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code DigiPointDevice} object with the 
	 * given connection interface.
	 * 
	 * @param connectionInterface The connection interface with the physical 
	 *                            point-to-multipoint device.
	 * 
	 * @throws NullPointerException if {@code connectionInterface == null}
	 * 
	 * @see #DigiPointDevice(String, int)
	 * @see #DigiPointDevice(String, SerialPortParameters)
	 * @see #DigiPointDevice(String, int, int, int, int, int)
	 * @see #DigiPointDevice(Context, int)
	 * @see #DigiPointDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #DigiPointDevice(Context, String, int)
	 * @see #DigiPointDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 */
	public DigiPointDevice(IConnectionInterface connectionInterface) {
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
		if (xbeeProtocol != XBeeProtocol.DIGI_POINT)
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
			network = new DigiPointNetwork(this);
		return network;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#getXBeeProtocol()
	 */
	@Override
	public XBeeProtocol getXBeeProtocol() {
		return XBeeProtocol.DIGI_POINT;
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
	 * @see com.digi.xbee.api.XBeeDevice#readExplicitData()
	 */
	@Override
	public ExplicitXBeeMessage readExplicitData() {
		return super.readExplicitData();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#readExplicitData(int)
	 */
	@Override
	public ExplicitXBeeMessage readExplicitData(int timeout) {
		return super.readExplicitData(timeout);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#readExplicitDataFrom(com.digi.xbee.api.RemoteXBeeDevice)
	 */
	@Override
	public ExplicitXBeeMessage readExplicitDataFrom(RemoteXBeeDevice remoteXBeeDevice) {
		return super.readExplicitDataFrom(remoteXBeeDevice);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#readExplicitDataFrom(com.digi.xbee.api.RemoteXBeeDevice, int)
	 */
	@Override
	public ExplicitXBeeMessage readExplicitDataFrom(
			RemoteXBeeDevice remoteXBeeDevice, int timeout) {
		return super.readExplicitDataFrom(remoteXBeeDevice, timeout);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#addExplicitDataListener(com.digi.xbee.api.listeners.IExplicitDataReceiveListener)
	 */
	@Override
	public void addExplicitDataListener(IExplicitDataReceiveListener listener) {
		super.addExplicitDataListener(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#removeExplicitDataListener(com.digi.xbee.api.listeners.IExplicitDataReceiveListener)
	 */
	@Override
	public void removeExplicitDataListener(IExplicitDataReceiveListener listener) {
		super.removeExplicitDataListener(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#getAPIOutputMode()
	 */
	@Override
	public APIOutputMode getAPIOutputMode() throws TimeoutException, XBeeException {
		return super.getAPIOutputMode();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#setAPIOutputMode(com.digi.xbee.api.models.APIOutputMode)
	 */
	@Override
	public void setAPIOutputMode(APIOutputMode apiOutputMode) throws TimeoutException, XBeeException {
		super.setAPIOutputMode(apiOutputMode);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#sendExplicitData(com.digi.xbee.api.RemoteXBeeDevice, int, int, int, int, byte[])
	 */
	@Override
	public void sendExplicitData(RemoteXBeeDevice remoteXBeeDevice, int sourceEndpoint, int destEndpoint, int clusterID,
			int profileID, byte[] data) throws TimeoutException, XBeeException {
		super.sendExplicitData(remoteXBeeDevice, sourceEndpoint, destEndpoint, clusterID, profileID, data);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#sendExplicitData(com.digi.xbee.api.models.XBee64BitAddress, com.digi.xbee.api.models.XBee16BitAddress, int, int, int, int, byte[])
	 */
	@Override
	public void sendExplicitData(XBee64BitAddress address64Bit, XBee16BitAddress address16bit, int sourceEndpoint, int destEndpoint, 
			int clusterID, int profileID, byte[] data) throws TimeoutException, XBeeException {
		super.sendExplicitData(address64Bit, address16bit, sourceEndpoint, destEndpoint, clusterID, profileID, data);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#sendBroadcastExplicitData(int, int, int, int, byte[])
	 */
	@Override
	public void sendBroadcastExplicitData(int sourceEndpoint, int destEndpoint, int clusterID, int profileID, 
			byte[] data) throws TimeoutException, XBeeException {
		super.sendBroadcastExplicitData(sourceEndpoint, destEndpoint, clusterID, profileID, data);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#sendExplicitDataAsync(com.digi.xbee.api.RemoteXBeeDevice, int, int, int, int, byte[])
	 */
	@Override
	public void sendExplicitDataAsync(RemoteXBeeDevice xbeeDevice, int sourceEndpoint, int destEndpoint, int clusterID,
			int profileID, byte[] data) throws XBeeException {
		super.sendExplicitDataAsync(xbeeDevice, sourceEndpoint, destEndpoint, clusterID, profileID, data);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#sendExplicitDataAsync(com.digi.xbee.api.models.XBee64BitAddress, com.digi.xbee.api.models.XBee16BitAddress, int, int, int, int, byte[])
	 */
	@Override
	public void sendExplicitDataAsync(XBee64BitAddress address64Bit, XBee16BitAddress address16Bit, int sourceEndpoint,
			int destEndpoint, int clusterID, int profileID, byte[] data) throws XBeeException {
		super.sendExplicitDataAsync(address64Bit, address16Bit, sourceEndpoint, destEndpoint, clusterID, profileID, data);
	}
	
	/**
	 * @deprecated DigiPoint protocol does not have an associated IPv6 address.
	 */
	@Override
	public Inet6Address getIPv6Address() {
		// DigiPoint protocol does not have IPv6 address.
		return null;
	}
	
	/**
	 * @deprecated Operation not supported in DigiPoint protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public Inet6Address getIPv6DestinationAddress()
			throws TimeoutException, XBeeException {
		// Not supported in DigiPoint.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in DigiPoint protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void setIPv6DestinationAddress(Inet6Address ipv6Address)
			throws TimeoutException, XBeeException {
		// Not supported in DigiPoint.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
}

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
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeDeviceException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IExplicitDataReceiveListener;
import com.digi.xbee.api.models.APIOutputMode;
import com.digi.xbee.api.models.AssociationIndicationStatus;
import com.digi.xbee.api.models.ExplicitXBeeMessage;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.models.XBeeTransmitOptions;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ExplicitAddressingPacket;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a local ZigBee device.
 * 
 * @see CellularDevice
 * @see DigiPointDevice
 * @see DigiMeshDevice
 * @see Raw802Device
 * @see ThreadDevice
 * @see WiFiDevice
 * @see XBeeDevice
 */
public class ZigBeeDevice extends XBeeDevice {

	// Constants
	private static final String OPERATION_EXCEPTION = "Operation not supported in ZigBee protocol.";
	
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
	 * 
	 * @see #ZigBeeDevice(IConnectionInterface)
	 * @see #ZigBeeDevice(String, SerialPortParameters)
	 * @see #ZigBeeDevice(String, int, int, int, int, int)
	 * @see #ZigBeeDevice(Context, int)
	 * @see #ZigBeeDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #ZigBeeDevice(Context, String, int)
	 * @see #ZigBeeDevice(Context, String, SerialPortParameters)
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
	 * 
	 * @see #ZigBeeDevice(IConnectionInterface)
	 * @see #ZigBeeDevice(String, int)
	 * @see #ZigBeeDevice(String, SerialPortParameters)
	 * @see #ZigBeeDevice(Context, int)
	 * @see #ZigBeeDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #ZigBeeDevice(Context, String, int)
	 * @see #ZigBeeDevice(Context, String, SerialPortParameters)
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
	 * @see #ZigBeeDevice(IConnectionInterface)
	 * @see #ZigBeeDevice(String, int)
	 * @see #ZigBeeDevice(String, int, int, int, int, int)
	 * @see #ZigBeeDevice(Context, int)
	 * @see #ZigBeeDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #ZigBeeDevice(Context, String, int)
	 * @see #ZigBeeDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	public ZigBeeDevice(String port, SerialPortParameters serialPortParameters) {
		this(XBee.createConnectiontionInterface(port, serialPortParameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code ZigBeeDevice} object for
	 * Android with the given parameters.
	 * 
	 * @param context The Android context.
	 * @param baudRate The USB connection baud rate.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 1}.
	 * @throws NullPointerException if {@code context == null}.
	 * 
	 * @see #ZigBeeDevice(IConnectionInterface)
	 * @see #ZigBeeDevice(String, int)
	 * @see #ZigBeeDevice(String, SerialPortParameters)
	 * @see #ZigBeeDevice(String, int, int, int, int, int)
	 * @see #ZigBeeDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #ZigBeeDevice(Context, String, int)
	 * @see #ZigBeeDevice(Context, String, SerialPortParameters)
	 * 
	 * @since 1.2.0
	 */
	public ZigBeeDevice(Context context, int baudRate) {
		super(XBee.createConnectiontionInterface(context, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code ZigBeeDevice} object for
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
	 * @see #ZigBeeDevice(IConnectionInterface)
	 * @see #ZigBeeDevice(String, int)
	 * @see #ZigBeeDevice(String, SerialPortParameters)
	 * @see #ZigBeeDevice(String, int, int, int, int, int)
	 * @see #ZigBeeDevice(Context, int)
	 * @see #ZigBeeDevice(Context, String, int)
	 * @see #ZigBeeDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.android.AndroidUSBPermissionListener
	 * 
	 * @since 1.2.0
	 */
	public ZigBeeDevice(Context context, int baudRate, AndroidUSBPermissionListener permissionListener) {
		super(XBee.createConnectiontionInterface(context, baudRate, permissionListener));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code XBeeDevice} object for
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
	 * @see #ZigBeeDevice(String, int)
	 * @see #ZigBeeDevice(String, SerialPortParameters)
	 * @see #ZigBeeDevice(String, int, int, int, int, int)
	 * @see #ZigBeeDevice(Context, int)
	 * @see #ZigBeeDevice(Context, String, int)
	 * @see #ZigBeeDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #ZigBeeDevice(Context, String, SerialPortParameters)
	 * 
	 * @since 1.2.0
	 */
	public ZigBeeDevice(Context context, String port, int baudRate) {
		super(XBee.createConnectiontionInterface(context, port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code XBeeDevice} object for
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
	 * @see #ZigBeeDevice(String, int)
	 * @see #ZigBeeDevice(String, SerialPortParameters)
	 * @see #ZigBeeDevice(String, int, int, int, int, int)
	 * @see #ZigBeeDevice(Context, int)
	 * @see #ZigBeeDevice(Context, String, int)
	 * @see #ZigBeeDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #ZigBeeDevice(Context, String, int)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 * 
	 * @since 1.2.0
	 */
	public ZigBeeDevice(Context context, String port, SerialPortParameters parameters) {
		super(XBee.createConnectiontionInterface(context, port, parameters));
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
	 * @see #ZigBeeDevice(IConnectionInterface)
	 * @see #ZigBeeDevice(String, int)
	 * @see #ZigBeeDevice(String, SerialPortParameters)
	 * @see #ZigBeeDevice(String, int, int, int, int, int)
	 * @see #ZigBeeDevice(Context, int)
	 * @see #ZigBeeDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #ZigBeeDevice(Context, String, int)
	 * @see #ZigBeeDevice(Context, String, SerialPortParameters)
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
	public ExplicitXBeeMessage readExplicitDataFrom(RemoteXBeeDevice remoteXBeeDevice, int timeout) {
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
	 * Sends a multicast transmission with the provided data to the given 
	 * group ID.
	 * 
	 * <p>This method blocks till a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the setReceiveTimeout method 
	 * and can be consulted with getReceiveTimeout method.</p>
	 * 
	 * @param groupID 16-bit address of the destination group ID.
	 * @param sourceEndpoint Source endpoint for the transmission.
	 * @param destEndpoint Destination endpoint for the transmission.
	 * @param clusterID Cluster ID used in the transmission.
	 * @param profileID Profile ID used in the transmission.
	 * @param data Byte array containing the data to be sent.
	 * 
	 * @throws IllegalArgumentException if {@code sourceEndpoint < 0} or 
	 *                                  if {@code sourceEndpoint > 0xFF} or 
	 *                                  if {@code destEndpoint < 0} or 
	 *                                  if {@code destEndpoint > 0xFF} or 
	 *                                  if {@code clusterID < 0} or 
	 *                                  if {@code clusterID > 0xFFFF} or 
	 *                                  if {@code profileID < 0} or 
	 *                                  if {@code profileID > 0xFFFF}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code groupID == null} or 
	 *                              if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #sendMulticastDataAsync(XBee16BitAddress, int, int, int, int, byte[])
	 * @see #getReceiveTimeout()
	 * @see #setReceiveTimeout(int)
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 */
	public void sendMulticastData(XBee16BitAddress groupID, int sourceEndpoint, int destEndpoint, int clusterID,
			int profileID, byte[] data) throws XBeeException {
		// Verify the parameters are not null, if they are null, throw an exception.
		if (groupID == null)
			throw new NullPointerException("Destination group ID cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null.");
		if (sourceEndpoint < 0 || sourceEndpoint > 0xFF)
			throw new IllegalArgumentException("Source endpoint must be between 0 and 0xFF.");
		if (destEndpoint < 0 || destEndpoint > 0xFF)
			throw new IllegalArgumentException("Destination endpoint must be between 0 and 0xFF.");
		if (clusterID < 0 || clusterID > 0xFFFF)
			throw new IllegalArgumentException("Cluster ID must be between 0 and 0xFFFF.");
		if (profileID < 0 || profileID > 0xFFFF)
			throw new IllegalArgumentException("Profile ID must be between 0 and 0xFFFF.");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send multicast data to a remote device from a remote device.");
		
		logger.debug(toString() + "Sending multicast data to {} [{} - {} - {} - {}] >> {}.", groupID, 
				HexUtils.integerToHexString(sourceEndpoint, 1), HexUtils.integerToHexString(destEndpoint, 1), 
				HexUtils.integerToHexString(clusterID, 2), HexUtils.integerToHexString(profileID, 2), 
				HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket = new ExplicitAddressingPacket(getNextFrameID(), XBee64BitAddress.UNKNOWN_ADDRESS, 
				groupID, sourceEndpoint, destEndpoint, clusterID, profileID, 0, XBeeTransmitOptions.ENABLE_MULTICAST, data);
		sendAndCheckXBeePacket(xbeePacket, false);
	}
	
	/**
	 * Sends a multicast asynchronous transmission with the provided data to 
	 * the given group ID.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote 
	 * device or for transmit status packet.</p>
	 * 
	 * @param groupID 16-bit address of the destination group ID.
	 * @param sourceEndpoint Source endpoint for the transmission.
	 * @param destEndpoint Destination endpoint for the transmission.
	 * @param clusterID Cluster ID used in the transmission.
	 * @param profileID Profile ID used in the transmission.
	 * @param data Byte array containing the data to be sent.
	 * 
	 * @throws IllegalArgumentException if {@code sourceEndpoint < 0} or 
	 *                                  if {@code sourceEndpoint > 0xFF} or 
	 *                                  if {@code destEndpoint < 0} or 
	 *                                  if {@code destEndpoint > 0xFF} or 
	 *                                  if {@code clusterID < 0} or 
	 *                                  if {@code clusterID > 0xFFFF} or 
	 *                                  if {@code profileID < 0} or 
	 *                                  if {@code profileID > 0xFFFF}.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code groupID == null} or 
	 *                              if {@code data == null}.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #sendMulticastData(XBee16BitAddress, int, int, int, int, byte[])
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public void sendMulticastDataAsync(XBee16BitAddress groupID, int sourceEndpoint, int destEndpoint, int clusterID,
			int profileID, byte[] data) throws XBeeException {
		// Verify the parameters are not null, if they are null, throw an exception.
		if (groupID == null)
			throw new NullPointerException("Destination group ID cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null.");
		if (sourceEndpoint < 0 || sourceEndpoint > 0xFF)
			throw new IllegalArgumentException("Source endpoint must be between 0 and 0xFF.");
		if (destEndpoint < 0 || destEndpoint > 0xFF)
			throw new IllegalArgumentException("Destination endpoint must be between 0 and 0xFF.");
		if (clusterID < 0 || clusterID > 0xFFFF)
			throw new IllegalArgumentException("Cluster ID must be between 0 and 0xFFFF.");
		if (profileID < 0 || profileID > 0xFFFF)
			throw new IllegalArgumentException("Profile ID must be between 0 and 0xFFFF.");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send multicast data to a remote device from a remote device.");
		
		logger.debug(toString() + "Sending multicast data asynchronously to {} [{} - {} - {} - {}] >> {}.", groupID, 
				HexUtils.integerToHexString(sourceEndpoint, 1), HexUtils.integerToHexString(destEndpoint, 1), 
				HexUtils.integerToHexString(clusterID, 2), HexUtils.integerToHexString(profileID, 2), 
				HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket = new ExplicitAddressingPacket(getNextFrameID(), XBee64BitAddress.UNKNOWN_ADDRESS, 
				groupID, sourceEndpoint, destEndpoint, clusterID, profileID, 0, XBeeTransmitOptions.ENABLE_MULTICAST, data);
		sendAndCheckXBeePacket(xbeePacket, true);
	}
	
	/**
	 * @deprecated ZigBee protocol does not have an associated IPv6 address.
	 */
	@Override
	public Inet6Address getIPv6Address() {
		// ZigBee protocol does not have IPv6 address.
		return null;
	}
	
	/**
	 * @deprecated Operation not supported in ZigBee protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public Inet6Address getIPv6DestinationAddress()
			throws TimeoutException, XBeeException {
		// Not supported in ZigBee.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in ZigBee protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void setIPv6DestinationAddress(Inet6Address ipv6Address)
			throws TimeoutException, XBeeException {
		// Not supported in ZigBee.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
}

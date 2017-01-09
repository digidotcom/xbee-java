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

import java.util.Set;

import android.content.Context;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.connection.android.AndroidUSBPermissionListener;
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeDeviceException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOMode;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.io.IOValue;
import com.digi.xbee.api.listeners.ISMSReceiveListener;
import com.digi.xbee.api.models.CellularAssociationIndicationStatus;
import com.digi.xbee.api.models.PowerLevel;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeIMEIAddress;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.cellular.TXSMSPacket;
import com.digi.xbee.api.utils.ByteUtils;

/**
 * This class represents a local Cellular device.
 * 
 * @see XBeeDevice
 * @see DigiMeshDevice
 * @see DigiPointDevice
 * @see Raw802Device
 * @see WiFiDevice
 * @see ZigBeeDevice
 * 
 * @since 1.2.0
 */
public class CellularDevice extends IPDevice {

	// Constants
	private static final String OPERATION_EXCEPTION = "Operation not supported in Cellular protocol.";
	
	// Variables
	private XBeeIMEIAddress imeiAddress;
	
	/**
	 * Class constructor. Instantiates a new {@code CellularDevice} object in 
	 * the given port name and baud rate.
	 * 
	 * @param port Serial port name where Cellular device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device. 
	 *                 Other connection parameters will be set as default (8 
	 *                 data bits, 1 stop bit, no parity, no flow control).
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see #CellularDevice(IConnectionInterface)
	 * @see #CellularDevice(String, SerialPortParameters)
	 * @see #CellularDevice(String, int, int, int, int, int)
	 * @see #CellularDevice(Context, int)
	 * @see #CellularDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #CellularDevice(Context, String, int)
	 * @see #CellularDevice(Context, String, SerialPortParameters)
	 */
	public CellularDevice(String port, int baudRate) {
		this(XBee.createConnectiontionInterface(port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code CellularDevice} object in 
	 * the given serial port name and settings.
	 * 
	 * @param port Serial port name where Cellular device is attached to.
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
	 * @see #CellularDevice(IConnectionInterface)
	 * @see #CellularDevice(String, int)
	 * @see #CellularDevice(String, SerialPortParameters)
	 * @see #CellularDevice(Context, int)
	 * @see #CellularDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #CellularDevice(Context, String, int)
	 * @see #CellularDevice(Context, String, SerialPortParameters)
	 */
	public CellularDevice(String port, int baudRate, int dataBits, int stopBits, int parity, int flowControl) {
		this(port, new SerialPortParameters(baudRate, dataBits, stopBits, parity, flowControl));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code CellularDevice} object in 
	 * the given serial port name and parameters.
	 * 
	 * @param port Serial port name where Cellular device is attached to.
	 * @param serialPortParameters Object containing the serial port parameters.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code serialPortParameters == null}.
	 * 
	 * @see #CellularDevice(IConnectionInterface)
	 * @see #CellularDevice(String, int)
	 * @see #CellularDevice(String, int, int, int, int, int)
	 * @see #CellularDevice(Context, int)
	 * @see #CellularDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #CellularDevice(Context, String, int)
	 * @see #CellularDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	public CellularDevice(String port, SerialPortParameters serialPortParameters) {
		this(XBee.createConnectiontionInterface(port, serialPortParameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code DigiMeshDevice} object for
	 * Android with the given parameters.
	 * 
	 * @param context The Android context.
	 * @param baudRate The USB connection baud rate.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 1}.
	 * @throws NullPointerException if {@code context == null}.
	 * 
	 * @see #CellularDevice(IConnectionInterface)
	 * @see #CellularDevice(String, int)
	 * @see #CellularDevice(String, int, int, int, int, int)
	 * @see #CellularDevice(String, SerialPortParameters)
	 * @see #CellularDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #CellularDevice(Context, String, int)
	 * @see #CellularDevice(Context, String, SerialPortParameters)
	 */
	public CellularDevice(Context context, int baudRate) {
		super(XBee.createConnectiontionInterface(context, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code DigiMeshDevice} object for
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
	 * @see #CellularDevice(IConnectionInterface)
	 * @see #CellularDevice(String, int)
	 * @see #CellularDevice(String, int, int, int, int, int)
	 * @see #CellularDevice(String, SerialPortParameters)
	 * @see #CellularDevice(Context, int)
	 * @see #CellularDevice(Context, String, int)
	 * @see #CellularDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.android.AndroidUSBPermissionListener
	 */
	public CellularDevice(Context context, int baudRate, AndroidUSBPermissionListener permissionListener) {
		super(XBee.createConnectiontionInterface(context, baudRate, permissionListener));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code DigiMeshDevice} object for
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
	 * @see #CellularDevice(IConnectionInterface)
	 * @see #CellularDevice(String, int)
	 * @see #CellularDevice(String, int, int, int, int, int)
	 * @see #CellularDevice(String, SerialPortParameters)
	 * @see #CellularDevice(Context, int)
	 * @see #CellularDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #CellularDevice(Context, String, SerialPortParameters)
	 */
	public CellularDevice(Context context, String port, int baudRate) {
		super(XBee.createConnectiontionInterface(context, port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code DigiMeshDevice} object for
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
	 * @see #CellularDevice(IConnectionInterface)
	 * @see #CellularDevice(String, int)
	 * @see #CellularDevice(String, int, int, int, int, int)
	 * @see #CellularDevice(String, SerialPortParameters)
	 * @see #CellularDevice(Context, int)
	 * @see #CellularDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #CellularDevice(Context, String, int)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	public CellularDevice(Context context, String port, SerialPortParameters parameters) {
		super(XBee.createConnectiontionInterface(context, port, parameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code CellularDevice} object with 
	 * the given connection interface.
	 * 
	 * @param connectionInterface The connection interface with the physical 
	 *                            Cellular device.
	 * 
	 * @throws NullPointerException if {@code connectionInterface == null}
	 * 
	 * @see #CellularDevice(String, int)
	 * @see #CellularDevice(String, SerialPortParameters)
	 * @see #CellularDevice(String, int, int, int, int, int)
	 * @see #CellularDevice(Context, int)
	 * @see #CellularDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #CellularDevice(Context, String, int)
	 * @see #CellularDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 */
	public CellularDevice(IConnectionInterface connectionInterface) {
		super(connectionInterface);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#open()
	 */
	@Override
	public void open() throws XBeeException {
		super.open();
		if (xbeeProtocol != XBeeProtocol.CELLULAR)
			throw new XBeeDeviceException("XBee device is not a " + getXBeeProtocol().getDescription() + " device, it is a " + xbeeProtocol.getDescription() + " device.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#getXBeeProtocol()
	 */
	@Override
	public XBeeProtocol getXBeeProtocol() {
		return XBeeProtocol.CELLULAR;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#readDeviceInfo()
	 */
	@Override
	public void readDeviceInfo() throws TimeoutException, XBeeException {
		super.readDeviceInfo();
		
		// Generate the IMEI address.
		imeiAddress = new XBeeIMEIAddress(xbee64BitAddress.getValue());
	}
	
	/**
	 * Returns whether the device is connected to the Internet or not.
	 * 
	 * @return {@code true} if the device is connected to the Internet, 
	 *         {@code false} otherwise.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout getting the association 
	 *                          indication status.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getCellularAssociationIndicationStatus()
	 * @see com.digi.xbee.api.models.CellularAssociationIndicationStatus
	 */
	public boolean isConnected() throws TimeoutException, XBeeException {
		CellularAssociationIndicationStatus status = getCellularAssociationIndicationStatus();
		return status == CellularAssociationIndicationStatus.SUCCESSFULLY_CONNECTED;
	}
	
	/**
	 * Returns the IMEI address of this Cellular device.
	 * 
	 * <p>To refresh this value use the {@link #readDeviceInfo()} method.</p>
	 * 
	 * @return The IMEI address of this Cellular device.
	 * 
	 * @see com.digi.xbee.api.models.XBeeIMEIAddress
	 */
	public XBeeIMEIAddress getIMEIAddress() {
		return imeiAddress;
	}
	
	/**
	 * Returns the current association status of this Cellular device.
	 * 
	 * <p>It indicates occurrences of errors during the modem initialization 
	 * and connection.</p>
	 * 
	 * @return The association indication status of the Cellular device.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout getting the association 
	 *                          indication status.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see com.digi.xbee.api.models.CellularAssociationIndicationStatus
	 */
	public CellularAssociationIndicationStatus getCellularAssociationIndicationStatus() throws TimeoutException, 
			XBeeException {
		byte[] associationIndicationValue = getParameter("AI");
		return CellularAssociationIndicationStatus.get(ByteUtils.byteArrayToInt(associationIndicationValue));
	}
	
	/**
	 * @deprecated Cellular protocol does not have an associated 64-bit address.
	 */
	@Override
	public XBee64BitAddress get64BitAddress() {
		// Cellular protocol does not have 64-bit address.
		return null;
	}
	
	/**
	 * @deprecated Operation not supported in Cellular protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public int getADCValue(IOLine ioLine) throws TimeoutException,
			XBeeException {
		// Not supported in Cellular.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in Cellular protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public Set<IOLine> getDIOChangeDetection() throws TimeoutException,
			XBeeException {
		// Not supported in Cellular.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in Cellular protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void setDIOChangeDetection(Set<IOLine> lines)
			throws TimeoutException, XBeeException {
		// Not supported in Cellular.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in Cellular protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public IOValue getDIOValue(IOLine ioLine) throws TimeoutException,
			XBeeException {
		// Not supported in Cellular.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in Cellular protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void setDIOValue(IOLine ioLine, IOValue ioValue)
			throws TimeoutException, XBeeException {
		// Not supported in Cellular.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in Cellular protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public IOMode getIOConfiguration(IOLine ioLine) throws TimeoutException,
			XBeeException {
		// Not supported in Cellular.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in Cellular protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void setIOConfiguration(IOLine ioLine, IOMode ioMode)
			throws TimeoutException, XBeeException {
		// Not supported in Cellular.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in Cellular protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public int getIOSamplingRate() throws TimeoutException, XBeeException {
		// Not supported in Cellular.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in Cellular protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void setIOSamplingRate(int rate) throws TimeoutException,
			XBeeException {
		// Not supported in Cellular.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Cellular protocol does not have node identifier.
	 */
	@Override
	public String getNodeID() {
		// Cellular protocol does not have Node Identifier.
		return null;
	}
	
	/**
	 * @deprecated Operation not supported in Cellular protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void setNodeID(String nodeID) throws TimeoutException, XBeeException {
		// Not supported in Cellular.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in Cellular protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public PowerLevel getPowerLevel() throws TimeoutException, XBeeException {
		// Not supported in Cellular.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in Cellular protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void setPowerLevel(PowerLevel powerLevel) throws TimeoutException,
			XBeeException {
		// Not supported in Cellular.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in Cellular protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public double getPWMDutyCycle(IOLine ioLine) throws TimeoutException, 
			XBeeException {
		// Not supported in Cellular.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in Cellular protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void setPWMDutyCycle(IOLine ioLine, double dutyCycle)
			throws TimeoutException, XBeeException {
		// Not supported in Cellular.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in Cellular protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public IOSample readIOSample() throws TimeoutException, XBeeException {
		// Not supported in Cellular.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#addSMSListener(com.digi.xbee.api.listeners.ISMSReceiveListener)
	 */
	@Override
	public void addSMSListener(ISMSReceiveListener listener) {
		super.addSMSListener(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#removeSMSListener(com.digi.xbee.api.listeners.ISMSReceiveListener)
	 */
	@Override
	public void removeSMSListener(ISMSReceiveListener listener) {
		super.removeSMSListener(listener);
	}
	
	/**
	 * Sends the provided SMS message to the given phone number.
	 * 
	 * <p>This method blocks till a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations use the method 
	 * {@link #sendSMSAsync(String, String)}.</p>
	 * 
	 * @param phoneNumber The phone number to send the SMS to.
	 * @param data String containing the text of the SMS.
	 * 
	 * @throws IllegalArgumentException if {@code phoneNumber.length() > 20} or
	 *                                  if {@code phoneNumber} is invalid.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code phoneNumber == null} or 
	 *                              if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the SMS.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #setReceiveTimeout(int)
	 * @see #sendSMSAsync(String, String)
	 */
	public void sendSMS(String phoneNumber, String data) throws TimeoutException, XBeeException {
		if (phoneNumber == null)
			throw new NullPointerException("Phone number cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send SMS from a remote device.");
		
		logger.debug(toString() + "Sending SMS to {} >> {}.", phoneNumber, data);
		
		XBeePacket xbeePacket = new TXSMSPacket(getNextFrameID(), phoneNumber, data);
		
		sendAndCheckXBeePacket(xbeePacket, false);
	}
	
	/**
	 * Sends asynchronously the provided SMS to the given phone number.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer or for transmit 
	 * status packet.</p>
	 * 
	 * @param phoneNumber The phone number to send the SMS to.
	 * @param data String containing the text of the SMS.
	 * 
	 * @throws IllegalArgumentException if {@code phoneNumber.length() > 20} or
	 *                                  if {@code phoneNumber} is invalid.
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code phoneNumber == null} or 
	 *                              if {@code data == null}.
	 * @throws TimeoutException if there is a timeout sending the SMS.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #sendSMS(String, String)
	 */
	public void sendSMSAsync(String phoneNumber, String data) throws TimeoutException, XBeeException {
		if (phoneNumber == null)
			throw new NullPointerException("Phone number cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send SMS from a remote device.");
		
		logger.debug(toString() + "Sending SMS asynchronously to {} >> {}.", phoneNumber, data);
		
		XBeePacket xbeePacket = new TXSMSPacket(getNextFrameID(), phoneNumber, data);
		
		sendAndCheckXBeePacket(xbeePacket, true);
	}
}

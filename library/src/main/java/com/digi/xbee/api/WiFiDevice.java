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

import java.io.ByteArrayInputStream;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.connection.android.AndroidUSBPermissionListener;
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeDeviceException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IPacketReceiveListener;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.AccessPoint;
import com.digi.xbee.api.models.IPAddressingMode;
import com.digi.xbee.api.models.WiFiAssociationIndicationStatus;
import com.digi.xbee.api.models.WiFiEncryptionType;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ATCommandPacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.utils.ByteUtils;

/**
 * This class represents a local Wi-Fi device.
 * 
 * @see XBeeDevice
 * @see CellularDevice
 * @see DigiMeshDevice
 * @see DigiPointDevice
 * @see Raw802Device
 * @see ZigBeeDevice
 * 
 * @since 1.2.0
 */
public class WiFiDevice extends IPDevice {
	
	// Constants.
	private final static int DEFAULT_ACCESS_POINT_TIMETOUT = 15000; // 15 seconds of timeout to connect, disconnect and scan access points.
	
	private static final String AS_COMMAND = "AS";
	private static final String ERROR_ALREADY_CONNECTED = "Device is already connected to an access point.";
	
	private static final int DISCOVER_TIMEOUT = 30000;
	
	// Variables.
	private boolean scanningAccessPoints = false;
	private boolean scanningAccessPointsError = false;
	
	protected int accessPointTimeout = DEFAULT_ACCESS_POINT_TIMETOUT;
	
	/**
	 * Class constructor. Instantiates a new {@code WiFiDevice} object in 
	 * the given port name and baud rate.
	 * 
	 * @param port Serial port name where Wi-Fi device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device. 
	 *                 Other connection parameters will be set as default (8 
	 *                 data bits, 1 stop bit, no parity, no flow control).
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see #WiFiDevice(IConnectionInterface)
	 * @see #WiFiDevice(String, SerialPortParameters)
	 * @see #WiFiDevice(String, int, int, int, int, int)
	 * @see #WiFiDevice(Context, int)
	 * @see #WiFiDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #WiFiDevice(Context, String, int)
	 * @see #WiFiDevice(Context, String, SerialPortParameters)
	 */
	public WiFiDevice(String port, int baudRate) {
		this(XBee.createConnectiontionInterface(port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code WiFiDevice} object in 
	 * the given serial port name and settings.
	 * 
	 * @param port Serial port name where Wi-Fi device is attached to.
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
	 * @see #WiFiDevice(IConnectionInterface)
	 * @see #WiFiDevice(String, int)
	 * @see #WiFiDevice(String, SerialPortParameters)
	 * @see #WiFiDevice(Context, int)
	 * @see #WiFiDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #WiFiDevice(Context, String, int)
	 * @see #WiFiDevice(Context, String, SerialPortParameters)
	 */
	public WiFiDevice(String port, int baudRate, int dataBits, int stopBits, int parity, int flowControl) {
		this(port, new SerialPortParameters(baudRate, dataBits, stopBits, parity, flowControl));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code WiFiDevice} object in 
	 * the given serial port name and parameters.
	 * 
	 * @param port Serial port name where Wi-Fi device is attached to.
	 * @param serialPortParameters Object containing the serial port parameters.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code serialPortParameters == null}.
	 * 
	 * @see #WiFiDevice(IConnectionInterface)
	 * @see #WiFiDevice(String, int)
	 * @see #WiFiDevice(String, int, int, int, int, int)
	 * @see #WiFiDevice(Context, int)
	 * @see #WiFiDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #WiFiDevice(Context, String, int)
	 * @see #WiFiDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	public WiFiDevice(String port, SerialPortParameters serialPortParameters) {
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
	 * @see #WiFiDevice(IConnectionInterface)
	 * @see #WiFiDevice(String, int)
	 * @see #WiFiDevice(String, int, int, int, int, int)
	 * @see #WiFiDevice(String, SerialPortParameters)
	 * @see #WiFiDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #WiFiDevice(Context, String, int)
	 * @see #WiFiDevice(Context, String, SerialPortParameters)
	 */
	public WiFiDevice(Context context, int baudRate) {
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
	 * @see #WiFiDevice(IConnectionInterface)
	 * @see #WiFiDevice(String, int)
	 * @see #WiFiDevice(String, int, int, int, int, int)
	 * @see #WiFiDevice(String, SerialPortParameters)
	 * @see #WiFiDevice(Context, int)
	 * @see #WiFiDevice(Context, String, int)
	 * @see #WiFiDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.android.AndroidUSBPermissionListener
	 */
	public WiFiDevice(Context context, int baudRate, AndroidUSBPermissionListener permissionListener) {
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
	 * @see #WiFiDevice(IConnectionInterface)
	 * @see #WiFiDevice(String, int)
	 * @see #WiFiDevice(String, int, int, int, int, int)
	 * @see #WiFiDevice(String, SerialPortParameters)
	 * @see #WiFiDevice(Context, int)
	 * @see #WiFiDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #WiFiDevice(Context, String, SerialPortParameters)
	 */
	public WiFiDevice(Context context, String port, int baudRate) {
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
	 * @see #WiFiDevice(IConnectionInterface)
	 * @see #WiFiDevice(String, int)
	 * @see #WiFiDevice(String, int, int, int, int, int)
	 * @see #WiFiDevice(String, SerialPortParameters)
	 * @see #WiFiDevice(Context, int)
	 * @see #WiFiDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #WiFiDevice(Context, String, int)
	 * @see com.digi.xbee.api.connection.serial.SerialPortParameters
	 */
	public WiFiDevice(Context context, String port, SerialPortParameters parameters) {
		super(XBee.createConnectiontionInterface(context, port, parameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code WiFiDevice} object with 
	 * the given connection interface.
	 * 
	 * @param connectionInterface The connection interface with the physical 
	 *                            Wi-Fi device.
	 * 
	 * @throws NullPointerException if {@code connectionInterface == null}
	 * 
	 * @see #WiFiDevice(String, int)
	 * @see #WiFiDevice(String, SerialPortParameters)
	 * @see #WiFiDevice(String, int, int, int, int, int)
	 * @see #WiFiDevice(Context, int)
	 * @see #WiFiDevice(Context, int, AndroidUSBPermissionListener)
	 * @see #WiFiDevice(Context, String, int)
	 * @see #WiFiDevice(Context, String, SerialPortParameters)
	 * @see com.digi.xbee.api.connection.IConnectionInterface
	 */
	public WiFiDevice(IConnectionInterface connectionInterface) {
		super(connectionInterface);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#open()
	 */
	@Override
	public void open() throws XBeeException {
		super.open();
		if (xbeeProtocol != XBeeProtocol.XBEE_WIFI)
			throw new XBeeDeviceException("XBee device is not a " + getXBeeProtocol().getDescription() + " device, it is a " + xbeeProtocol.getDescription() + " device.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.XBeeDevice#getXBeeProtocol()
	 */
	@Override
	public XBeeProtocol getXBeeProtocol() {
		return XBeeProtocol.XBEE_WIFI;
	}
	
	/**
	 * Returns the current association status of this Wi-Fi device.
	 * 
	 * <p>It indicates occurrences of errors during the Wi-Fi transceiver 
	 * initialization and connection.</p>
	 * 
	 * @return The association indication status of the Wi-Fi device.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout getting the association 
	 *                          indication status.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see com.digi.xbee.api.models.WiFiAssociationIndicationStatus
	 */
	public WiFiAssociationIndicationStatus getWiFiAssociationIndicationStatus() throws TimeoutException, 
			XBeeException {
		byte[] associationIndicationValue = getParameter("AI");
		return WiFiAssociationIndicationStatus.get(ByteUtils.byteArrayToInt(associationIndicationValue));
	}
	
	/**
	 * Finds and reports the access point that matches the supplied SSID.
	 * 
	 * <p>This method blocks until the access point is discovered or the 
	 * configured access point timeout expires.</p>
	 * 
	 * <p>The access point timeout is configured using the 
	 * {@code setAccessPointTimeout} method and can be consulted with 
	 * {@code getAccessPointTimeout} method.</p>
	 * 
	 * @param ssid The SSID of the access point to discover.
	 * 
	 * @return The discovered access point with the given SSID, {@code null} 
	 *         if the timeout expires and the access point was not found.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ssid == null}.
	 * @throws TimeoutException if there is a timeout getting the access point.
	 * @throws XBeeException if there is an error sending the discovery command.
	 * 
	 * @see #getAccessPointTimeout()
	 * @see #scanAccessPoints()
	 * @see #setAccessPointTimeout(int)
	 */
	public AccessPoint getAccessPoint(String ssid) throws XBeeException {
		if (ssid == null)
			throw new NullPointerException("SSID cannot be null.");
		
		logger.debug("{}AS for '{}' access point.", toString(), ssid);
		
		List<AccessPoint> accessPointsList = scanAccessPoints();
		
		for (AccessPoint accessPoint:accessPointsList) {
			if (accessPoint.getSSID().equals(ssid))
				return accessPoint;
		}
		
		return null;
	}
	
	/**
	 * Performs a scan to search for access points in the vicinity.
	 * 
	 * <p>This method blocks until all the access points are discovered or the 
	 * configured access point timeout expires.</p>
	 * 
	 * <p>The access point timeout is configured using the 
	 * {@code setAccessPointTimeout} method and can be consulted with 
	 * {@code getAccessPointTimeout} method.</p>
	 * 
	 * @return The list of access points discovered.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout scanning the access points.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getAccessPoint(String)
	 * @see #getAccessPointTimeout()
	 * @see #setAccessPointTimeout(int)
	 */
	public List<AccessPoint> scanAccessPoints() throws XBeeException {
		// Check if the connection is open.
		if (!isOpen())
			throw new InterfaceNotOpenException();
		
		final ArrayList<AccessPoint> accessPointsList = new ArrayList<AccessPoint>();
		
		switch (getOperatingMode()) {
		case AT:
		case UNKNOWN:
		default:
			throw new InvalidOperatingModeException(operatingMode);
		case API:
		case API_ESCAPE:
			// Subscribe listener to wait for active scan responses.
			IPacketReceiveListener packetReceiveListener = new IPacketReceiveListener() {
				/*
				 * (non-Javadoc)
				 * @see com.digi.xbee.api.listeners.IPacketReceiveListener#packetReceived(com.digi.xbee.api.packet.XBeePacket)
				 */
				@Override
				public void packetReceived(XBeePacket receivedPacket) {
					if (!scanningAccessPoints)
						return;
					
					try {
						if (((XBeeAPIPacket)receivedPacket).getFrameType() != APIFrameType.AT_COMMAND_RESPONSE)
							return;
						ATCommandResponsePacket response = (ATCommandResponsePacket)receivedPacket;
						if (!response.getCommand().equals(AS_COMMAND))
							return;
						
						// Check for error.
						if (response.getStatus() == ATCommandStatus.ERROR) {
							scanningAccessPointsError = true;
							scanningAccessPoints = false;
						// Check for end of discovery.
						} else if (response.getCommandValue() == null 
								|| response.getCommandValue().length == 0)
							scanningAccessPoints = false;
						else {
							AccessPoint accessPoint = parseDiscoveredAccessPoint(response.getCommandValue());
							if (accessPoint != null)
								accessPointsList.add(accessPoint);
						}
					} catch (ClassCastException e) {
						// Do nothing here.
					}
				}
			};
			
			logger.debug("{}Start scanning access points.", toString());
			addPacketListener(packetReceiveListener);
			
			try {
				scanningAccessPoints = true;
				// Send the active scan command.
				sendPacketAsync(new ATCommandPacket(getNextFrameID(), AS_COMMAND, ""));
				
				// Wait until the discovery process finishes or timeouts.
				long deadLine = System.currentTimeMillis() + DISCOVER_TIMEOUT;
				while (scanningAccessPoints && System.currentTimeMillis() < deadLine)
					sleep(100);
				
				// Check if we exited because of a timeout.
				if (scanningAccessPoints)
					throw new TimeoutException();
				// Check if there was an error in the active scan command (device is already connected).
				if (scanningAccessPointsError)
					throw new XBeeException(ERROR_ALREADY_CONNECTED);
			} finally {
				scanningAccessPoints = false;
				scanningAccessPointsError = false;
				removePacketListener(packetReceiveListener);
				logger.debug("{}Stop scanning access points.", toString());
			}
		}
		
		return accessPointsList;
	}
	
	/**
	 * Connects to the access point with provided SSID.
	 * 
	 * <p>This method blocks until the connection with the access point is 
	 * established or the configured access point timeout expires.</p>
	 * 
	 * <p>The access point timeout is configured using the 
	 * {@code setAccessPointTimeout} method and can be consulted with 
	 * {@code getAccessPointTimeout} method.</p>
	 * 
	 * <p>Once the module is connected to the access point, you can issue 
	 * the {@link #writeChanges()} method to save the connection settings. This 
	 * way the module will try to connect to the access point every time it 
	 * is powered on.</p>
	 * 
	 * @param ssid The SSID of the access point to connect to.
	 * @param password The password for the access point, {@code null} if it 
	 *                 does not have any encryption enabled.
	 * 
	 * @return {@code true} if the module connected to the access point 
	 *         successfully, {@code false} otherwise.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code ssid == null}.
	 * @throws TimeoutException if there is a timeout sending the connect 
	 *                          commands.
	 * @throws XBeeException if the SSID provided is {@code null} or if there 
	 *                       is any other XBee related exception.
	 * 
	 * @see #connect(AccessPoint, String)
	 * @see #disconnect()
	 * @see #getAccessPoint(String)
	 * @see #getAccessPointTimeout()
	 * @see #scanAccessPoints()
	 * @see #setAccessPointTimeout(int)
	 */
	public boolean connect(String ssid, String password) 
			throws TimeoutException, XBeeException {
		if (ssid == null)
			throw new NullPointerException("SSID cannot be null.");
		
		AccessPoint accessPoint = getAccessPoint(ssid);
		if (accessPoint == null)
			throw new XBeeException("Couldn't find any access point with the proviced SSID.");
		
		return connect(accessPoint, password);
	}
	
	/**
	 * Connects to the provided access point.
	 * 
	 * <p>This method blocks until the connection with the access point is 
	 * established or the configured access point timeout expires.</p>
	 * 
	 * <p>The access point timeout is configured using the 
	 * {@code setAccessPointTimeout} method and can be consulted with 
	 * {@code getAccessPointTimeout} method.</p>
	 * 
	 * <p>Once the module is connected to the access point, you can issue 
	 * the {@code writeSettings} method to save the connection settings. This 
	 * way the module will try to connect to the access point every time it 
	 * is powered on.</p>
	 * 
	 * @param accessPoint The access point to connect to.
	 * @param password The password for the access point, {@code null} if it 
	 *                 does not have any encryption enabled.
	 * 
	 * @return {@code true} if the module connected to the access point 
	 *         successfully, {@code false} otherwise.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws NullPointerException if {@code accessPoint == null}.
	 * @throws TimeoutException if there is a timeout sending the connect 
	 *                          commands.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #connect(String, String)
	 * @see #disconnect()
	 * @see #getAccessPoint(String)
	 * @see #getAccessPointTimeout()
	 * @see #scanAccessPoints()
	 * @see #setAccessPointTimeout(int)
	 * @see com.digi.xbee.api.models.AccessPoint
	 */
	public boolean connect(AccessPoint accessPoint, String password) 
			throws TimeoutException, XBeeException {
		if (accessPoint == null)
			throw new NullPointerException("Access point cannot be null.");
		
		// Set connection parameters.
		setParameter("ID", accessPoint.getSSID().getBytes());
		setParameter("EE", new byte[]{(byte)accessPoint.getEncryptionType().getID()});
		if (password != null && accessPoint.getEncryptionType() != WiFiEncryptionType.NONE)
			setParameter("PK", password.getBytes());
		
		// Wait for the module to connect to the access point.
		long deadLine = System.currentTimeMillis() + accessPointTimeout;
		while (System.currentTimeMillis() < deadLine) {
			sleep(100);
			// Get the association indication value of the module.
			byte[] status = getParameter("AI");
			if (status == null || status.length < 1)
				continue;
			if (status[0] == 0)
				return true;
		}
		
		return false;
	}
	
	/**
	 * Disconnects from the access point the device is connected to.
	 * 
	 * <p>This method blocks until the device disconnects totally from the 
	 * access point or the configured access point timeout expires.</p>
	 * 
	 * <p>The access point timeout is configured using the 
	 * {@code setAccessPointTimeout} method and can be consulted with 
	 * {@code getAccessPointTimeout} method.</p>
	 * 
	 * @return {@code true} if the module disconnected from the access point 
	 *         successfully, {@code false} otherwise.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout sending the disconnect 
	 *                          command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #connect(AccessPoint, String)
	 * @see #connect(String, String)
	 * @see #getAccessPointTimeout()
	 * @see #setAccessPointTimeout(int)
	 */
	public boolean disconnect() throws TimeoutException, XBeeException {
		executeParameter("NR");
		// Wait for the module to connect to the access point.
		long deadLine = System.currentTimeMillis() + accessPointTimeout;
		while (System.currentTimeMillis() < deadLine) {
			sleep(100);
			// Get the association indication value of the module.
			byte[] status = getParameter("AI");
			if (status == null || status.length < 1)
				continue;
			// Status 0x23 (35) means the SSID is not configured.
			if (status[0] == 0x23)
				return true;
		}
		
		return false;
	}
	
	/**
	 * Returns whether the device is connected to an access point or not.
	 * 
	 * @return {@code true} if the device is connected to an access point, 
	 *         {@code false} otherwise.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout getting the association 
	 *                          indication status.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getWiFiAssociationIndicationStatus()
	 * @see com.digi.xbee.api.models.WiFiAssociationIndicationStatus
	 */
	public boolean isConnected() throws TimeoutException, XBeeException {
		WiFiAssociationIndicationStatus status = getWiFiAssociationIndicationStatus();
		
		return status == WiFiAssociationIndicationStatus.SUCCESSFULLY_JOINED;
	}
	
	/**
	 * Parses the given active scan API data and returns an {@code AccessPoint} 
	 * object.
	 * 
	 * @param data Data to parse.
	 * 
	 * @return Discovered access point. {@code null} if the parsed data does
	 *         not correspond to an access point.
	 */
	private AccessPoint parseDiscoveredAccessPoint(byte[] data) {
		AccessPoint accessPoint = null;
		
		int version;
		int channel;
		int security;
		int signalStrength;
		int signalQuality;
		
		String ssidName = "";
		
		ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
		if (inputStream.available() == 0)
			return null;
		// Read the version.
		version = ByteUtils.byteArrayToInt((ByteUtils.readBytes(1, inputStream)));
		if (inputStream.available() == 0)
			return null;
		// Read the channel.
		channel = ByteUtils.byteArrayToInt((ByteUtils.readBytes(1, inputStream)));
		if (inputStream.available() == 0)
			return null;
		// Read the encryption type.
		security = ByteUtils.byteArrayToInt((ByteUtils.readBytes(1, inputStream)));
		if (inputStream.available() == 0)
			return null;
		// Read the signal strength and generate the signal quality.
		signalStrength = ByteUtils.byteArrayToInt((ByteUtils.readBytes(1, inputStream)));
		signalQuality = getSignalQuality(version, signalStrength);
		
		// Read the SSID name.
		if (inputStream.available() == 0)
			return null;
		int readByte = -1;
		while ((readByte = inputStream.read()) != -1)
			ssidName += new String(new byte[]{(byte)(readByte & 0xFF)});
		
		// Build the access point object.
		accessPoint = new AccessPoint(ssidName, WiFiEncryptionType.get(security), channel, signalQuality);
		
		logger.debug("{}Discovered: SSID[{}], encryption[{}], channel[{}], signal quality[{}].",
				toString(), ssidName, WiFiEncryptionType.get(security).name(), channel, signalQuality);
		
		return accessPoint;
	}
	
	/**
	 * Converts the signal strength value in signal quality (%) based on the 
	 * provided Wi-Fi version.
	 * 
	 * @param wifiVersion Wi-Fi protocol version of the Wi-Fi XBee device.
	 * @param signalStrength Signal strength value to convert to %.
	 * 
	 * @return The signal quality in %.
	 */
	private int getSignalQuality(int wifiVersion, int signalStrength) {
		int quality;
		
		if (wifiVersion == 1) {
			if (signalStrength <= -100)
				quality = 0;
			else if(signalStrength >= -50)
				quality = 100;
			else
				quality = (2 * (signalStrength + 100)); 
		} else
			quality = 2 * signalStrength;
		
		if (quality > 100)
			quality = 100;
		if (quality < 0)
			quality = 0;
		
		return quality;
	}
	
	/**
	 * Sleeps the thread for the given number of milliseconds.
	 * 
	 * @param milliseconds The number of milliseconds that the thread should 
	 *        be sleeping.
	 */
	private void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) { }
	}
	
	/**
	 * Returns the configured access point timeout for connecting, 
	 * disconnecting and scanning access points.
	 * 
	 * @return The current access point timeout in milliseconds.
	 * 
	 * @see #setAccessPointTimeout(int)
	 */
	public int getAccessPointTimeout() {
		return accessPointTimeout;
	}
	
	/**
	 * Configures the access point timeout in milliseconds for connecting, 
	 * disconnecting and scanning access points.
	 *  
	 * @param accessPointTimeout The new access point timeout in milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code accessPointTimeout < 0}.
	 * 
	 * @see #getAccessPointTimeout()
	 */
	public void setAccessPointTimeout(int accessPointTimeout) {
		if (accessPointTimeout < 0)
			throw new IllegalArgumentException("Access point timeout cannot be less than 0.");
		
		this.accessPointTimeout = accessPointTimeout;
	}
	
	/**
	 * Sets the IP addressing mode.
	 * 
	 * @param mode IP addressing mode.
	 * 
	 * @throws NullPointerException if {@code mode == null}.
	 * @throws TimeoutException if there is a timeout setting the IP addressing
	 *                          mode.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getIPAddressingMode()
	 * @see IPAddressingMode
	 */
	public void setIPAddressingMode(IPAddressingMode mode) throws TimeoutException, XBeeException {
		if (mode == null)
			throw new NullPointerException("IP addressing mode cannot be null.");
		
		setParameter("MA", ByteUtils.intToByteArray(mode.getID()));
	}
	
	/**
	 * Returns the IP addressing mode.
	 * 
	 * @return The configured IP addressing mode.
	 * 
	 * @throws TimeoutException if there is a timeout reading the IP addressing
	 *                          mode.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setIPAddressingMode(IPAddressingMode)
	 * @see IPAddressingMode
	 */
	public IPAddressingMode getIPAddressingMode() throws TimeoutException, XBeeException {
		return IPAddressingMode.get(ByteUtils.byteArrayToInt(getParameter("MA")));
	}
	
	/**
	 * Sets the IP address of the module.
	 * 
	 * <p>This method <b>can only be called</b> if the module is configured in
	 * {@link IPAddressingMode#STATIC} mode. Otherwise an {@code XBeeException}
	 * will be thrown.</p>
	 * 
	 * @param address IP address.
	 * 
	 * @throws NullPointerException if {@code address == null}.
	 * @throws TimeoutException if there is a timeout setting the IP address.
	 * @throws XBeeException if the module is in {@link IPAddressingMode#DHCP}
	 *                       mode or there is any other XBee related exception.
	 * 
	 * @see #getIPAddress()
	 * @see #getIPAddressingMode()
	 * @see Inet4Address
	 */
	public void setIPAddress(Inet4Address address) throws TimeoutException, XBeeException {
		if (address == null)
			throw new NullPointerException("IP address cannot be null.");
		
		setParameter("MY", address.getAddress());
	}
	
	/**
	 * Sets the IP address subnet mask.
	 * 
	 * <p>This method <b>can only be called</b> if the module is configured in
	 * {@link IPAddressingMode#STATIC} mode. Otherwise an {@code XBeeException}
	 * will be thrown.</p>
	 * 
	 * @param address IP address subnet mask.
	 * 
	 * @throws NullPointerException if {@code address == null}.
	 * @throws TimeoutException if there is a timeout setting the IP address
	 *                          mask.
	 * @throws XBeeException if the module is in {@link IPAddressingMode#DHCP}
	 *                       mode or there is any other XBee related exception.
	 * 
	 * @see #getIPAddressingMode()
	 * @see #getIPAddressMask()
	 * @see Inet4Address
	 */
	public void setIPAddressMask(Inet4Address address) throws TimeoutException, XBeeException {
		if (address == null)
			throw new NullPointerException("Address mask cannot be null.");
		
		setParameter("MK", address.getAddress());
	}
	
	/**
	 * Returns the IP address subnet mask.
	 * 
	 * @return The configured IP address subnet mask.
	 * 
	 * @throws TimeoutException if there is a timeout reading the IP address
	 *                          mask.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setIPAddressMask(Inet4Address)
	 * @see Inet4Address
	 */
	public Inet4Address getIPAddressMask() throws TimeoutException, XBeeException {
		try {
			return (Inet4Address) Inet4Address.getByAddress(getParameter("MK"));
		} catch (UnknownHostException e) {
			throw new XBeeException(e);
		}
	}
	
	/**
	 * Sets the IP address of the gateway.
	 * 
	 * <p>This method <b>can only be called</b> if the module is configured in
	 * {@link IPAddressingMode#STATIC} mode. Otherwise an {@code XBeeException}
	 * will be thrown.</p>
	 * 
	 * @param address IP address of the gateway.
	 * 
	 * @throws NullPointerException if {@code address == null}.
	 * @throws TimeoutException if there is a timeout setting the gateway IP
	 *                          address.
	 * @throws XBeeException if the module is in {@link IPAddressingMode#DHCP}
	 *                       mode or there is any other XBee related exception.
	 * 
	 * @see #getGatewayIPAddress()
	 * @see #getIPAddressingMode()
	 * @see Inet4Address
	 */
	public void setGatewayIPAddress(Inet4Address address) throws TimeoutException, XBeeException {
		if (address == null)
			throw new NullPointerException("Gateway address cannot be null.");
		
		setParameter("GW", address.getAddress());
	}
	
	/**
	 * Returns the IP address of the gateway.
	 * 
	 * @return The configured IP address of the gateway.
	 * 
	 * @throws TimeoutException if there is a timeout reading the gateway IP
	 *                          address.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setGatewayIPAddress(Inet4Address)
	 * @see Inet4Address
	 */
	public Inet4Address getGatewayIPAddress() throws TimeoutException, XBeeException {
		try {
			return (Inet4Address) Inet4Address.getByAddress(getParameter("GW"));
		} catch (UnknownHostException e) {
			throw new XBeeException(e);
		}
	}
	
	/**
	 * Sets the IP address of domain name server.
	 * 
	 * @param address DNS address.
	 * 
	 * @throws NullPointerException if {@code address == null}.
	 * @throws TimeoutException if there is a timeout setting the DNS address.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getDNSAddress()
	 * @see Inet4Address
	 */
	public void setDNSAddress(Inet4Address address) throws TimeoutException, XBeeException {
		if (address == null)
			throw new NullPointerException("DNS address cannot be null.");
		
		setParameter("NS", address.getAddress());
	}
	
	/**
	 * Returns the IP address of domain name server.
	 * 
	 * @return The configured DNS address.
	 * 
	 * @throws TimeoutException if there is a timeout reading the DNS address.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setDNSAddress(Inet4Address)
	 * @see Inet4Address
	 */
	public Inet4Address getDNSAddress() throws TimeoutException, XBeeException {
		try {
			return (Inet4Address) Inet4Address.getByAddress(getParameter("NS"));
		} catch (UnknownHostException e) {
			throw new XBeeException(e);
		}
	}

}

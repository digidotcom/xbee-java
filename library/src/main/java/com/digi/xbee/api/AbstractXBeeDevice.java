/**
 * Copyright (c) 2014 Digi International Inc.,
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.connection.DataReader;
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.exceptions.ATCommandException;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.TransmitException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOMode;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.io.IOValue;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;
import com.digi.xbee.api.listeners.IPacketReceiveListener;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.AssociationIndicationStatus;
import com.digi.xbee.api.models.HardwareVersion;
import com.digi.xbee.api.models.HardwareVersionEnum;
import com.digi.xbee.api.models.PowerLevel;
import com.digi.xbee.api.models.RemoteATCommandOptions;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.models.XBeeTransmitStatus;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ATCommandPacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.packet.common.IODataSampleRxIndicatorPacket;
import com.digi.xbee.api.packet.common.RemoteATCommandPacket;
import com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket;
import com.digi.xbee.api.packet.common.TransmitStatusPacket;
import com.digi.xbee.api.packet.raw.RX16IOPacket;
import com.digi.xbee.api.packet.raw.RX64IOPacket;
import com.digi.xbee.api.packet.raw.TXStatusPacket;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

public abstract class AbstractXBeeDevice {
	
	// Constants.
	protected static int DEFAULT_RECEIVE_TIMETOUT = 2000; // 2.0 seconds of timeout to receive packet and command responses.
	protected static int TIMEOUT_BEFORE_COMMAND_MODE = 1200;
	protected static int TIMEOUT_ENTER_COMMAND_MODE = 1500;
	
	// Variables.
	protected IConnectionInterface connectionInterface;
	
	protected DataReader dataReader = null;
	
	protected XBeeProtocol xbeeProtocol = XBeeProtocol.UNKNOWN;
	
	protected OperatingMode operatingMode = OperatingMode.UNKNOWN;
	
	protected XBee16BitAddress xbee16BitAddress = XBee16BitAddress.UNKNOWN_ADDRESS;
	protected XBee64BitAddress xbee64BitAddress = XBee64BitAddress.UNKNOWN_ADDRESS;
	
	protected int currentFrameID = 0xFF;
	protected int receiveTimeout = DEFAULT_RECEIVE_TIMETOUT;
	
	protected Logger logger;
	
	private String nodeID;
	private String firmwareVersion;
	
	private HardwareVersion hardwareVersion;
	
	protected AbstractXBeeDevice localXBeeDevice;
	
	private Object ioLock = new Object();
	
	private boolean ioPacketReceived = false;
	private boolean applyConfigurationChanges = true;
	
	private byte[] ioPacketPayload;
	
	/**
	 * Class constructor. Instantiates a new {@code XBeeDevice} object in the 
	 * given port name and baud rate.
	 * 
	 * @param port Serial port name where XBee device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device. 
	 *                 Other connection parameters will be set as default (8 
	 *                 data bits, 1 stop bit, no parity, no flow control).
	 * 
	 * @throws NullPointerException if {@code port == null}.
	 * @throws IllegalArgumentException if {@code baudRate < 0}.
	 */
	public AbstractXBeeDevice(String port, int baudRate) {
		this(XBee.createConnectiontionInterface(port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code XBeeDevice} object in the 
	 * given serial port name and settings.
	 * 
	 * @param port Serial port name where XBee device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device.
	 * @param dataBits Serial port data bits.
	 * @param stopBits Serial port data bits.
	 * @param parity Serial port data bits.
	 * @param flowControl Serial port data bits.
	 * 
	 * @throws NullPointerException if {@code port == null}.
	 * @throws IllegalArgumentException if {@code baudRate < 0} or
	 *                                  if {@code dataBits < 0} or
	 *                                  if {@code stopBits < 0} or
	 *                                  if {@code parity < 0} or
	 *                                  if {@code flowControl < 0}.
	 */
	public AbstractXBeeDevice(String port, int baudRate, int dataBits, int stopBits, int parity, int flowControl) {
		this(port, new SerialPortParameters(baudRate, dataBits, stopBits, parity, flowControl));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code XBeeDevice} object in the 
	 * given serial port name and parameters.
	 * 
	 * @param port Serial port name where XBee device is attached to.
	 * @param serialPortParameters Object containing the serial port parameters.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code serialPortParameters == null}.
	 * 
	 * @see SerialPortParameters
	 */
	public AbstractXBeeDevice(String port, SerialPortParameters serialPortParameters) {
		this(XBee.createConnectiontionInterface(port, serialPortParameters));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code XBeeDevice} object with the 
	 * given connection interface.
	 * 
	 * @param connectionInterface The connection interface with the physical 
	 *                            XBee device.
	 * 
	 * @throws NullPointerException if {@code connectionInterface == null}.
	 * 
	 * @see IConnectionInterface
	 */
	public AbstractXBeeDevice(IConnectionInterface connectionInterface) {
		if (connectionInterface == null)
			throw new NullPointerException("ConnectionInterface cannot be null.");
		
		this.connectionInterface = connectionInterface;
		this.logger = LoggerFactory.getLogger(this.getClass());
		logger.debug(toString() + "Using the connection interface {}.", 
				connectionInterface.getClass().getSimpleName());
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteXBeeDevice} object 
	 * with the given local {@code XBeeDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local XBee device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote XBee device.
	 * @param addr64 The 64-bit address to identify this XBee device.
	 * 
	 * @throws IllegalArgumentException If {@code localXBeeDevice.isRemote() == true}.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code addr64 == null}.
	 * 
	 * @see XBee64BitAddress
	 */
	public AbstractXBeeDevice(XBeeDevice localXBeeDevice, XBee64BitAddress addr64) {
		this(localXBeeDevice, addr64, null, null);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteXBeeDevice} object 
	 * with the given local {@code XBeeDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local XBee device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote XBee device.
	 * @param addr64 The 64-bit address to identify this XBee device.
	 * @param addr16 The 16-bit address to identify this XBee device. It might 
	 *               be {@code null}.
	 * @param id The node identifier of this XBee device. It might be 
	 *           {@code null}.
	 * 
	 * @throws IllegalArgumentException If {@code localXBeeDevice.isRemote() == true}.
	 * @throws NullPointerException If {@code localXBeeDevice == null} or
	 *                              if {@code addr64 == null}.
	 * 
	 * @see XBee64BitAddress
	 * @see XBee16BitAddress
	 */
	public AbstractXBeeDevice(XBeeDevice localXBeeDevice, XBee64BitAddress addr64, 
			XBee16BitAddress addr16, String id) {
		if (localXBeeDevice == null)
			throw new NullPointerException("Local XBee device cannot be null.");
		if (addr64 == null)
			throw new NullPointerException("XBee 64-bit address of the device cannot be null.");
		if (localXBeeDevice.isRemote())
			throw new IllegalArgumentException("The given local XBee device is remote.");
		
		this.localXBeeDevice = localXBeeDevice;
		this.connectionInterface = localXBeeDevice.getConnectionInterface();
		this.xbee64BitAddress = addr64;
		this.xbee16BitAddress = addr16;
		if (addr16 == null)
			xbee16BitAddress = XBee16BitAddress.UNKNOWN_ADDRESS;
		this.nodeID = id;
		this.logger = LoggerFactory.getLogger(this.getClass());
		logger.debug(toString() + "Using the connection interface {}.", 
				connectionInterface.getClass().getSimpleName());
	}
	
	/**
	 * Retrieves the connection interface associated to this XBee device.
	 * 
	 * @return XBee device's connection interface.
	 * 
	 * @see IConnectionInterface
	 */
	public IConnectionInterface getConnectionInterface() {
		return connectionInterface;
	}
	
	/**
	 * Retrieves whether or not the XBee device is a remote device.
	 * 
	 * @return {@code true} if the XBee device is a remote device, 
	 *         {@code false} otherwise.
	 */
	abstract public boolean isRemote();
	
	/**
	 * Reads some parameters from the device and obtains its protocol.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout reading the parameters.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #get64BitAddress()
	 * @see #get16BitAddress()
	 * @see #getNodeID()
	 * @see #setNodeID(String)
	 * @see #getHardwareVersion()
	 * @see #getFirmwareVersion()
	 * @see #getXBeeProtocol()
	 */
	public void readDeviceInfo() throws TimeoutException, XBeeException {
		byte[] response = null;
		// Get the 64-bit address.
		if (xbee64BitAddress == null || xbee64BitAddress == XBee64BitAddress.UNKNOWN_ADDRESS) {
			String addressHigh;
			String addressLow;
			
			response = getParameter("SH");
			addressHigh = HexUtils.byteArrayToHexString(response);
			
			response = getParameter("SL");
			addressLow = HexUtils.byteArrayToHexString(response);
			
			while(addressLow.length() < 8)
				addressLow = "0" + addressLow;
			
			xbee64BitAddress = new XBee64BitAddress(addressHigh + addressLow);
		}
		// Get the Node ID.
		response = getParameter("NI");
		nodeID = new String(response);
		
		// Get the hardware version.
		if (hardwareVersion == null) {
			response = getParameter("HV");
			hardwareVersion = HardwareVersion.get(response[0]);
		}
		// Get the firmware version.
		response = getParameter("VR");
		firmwareVersion = HexUtils.byteArrayToHexString(response);
		
		// Obtain the device protocol.
		xbeeProtocol = XBeeProtocol.determineProtocol(hardwareVersion, firmwareVersion);
		
		// Get the 16-bit address. This must be done after obtaining the protocol because 
		// DigiMesh protocol does not have 16-bit addresses.
		if (getXBeeProtocol() != XBeeProtocol.DIGI_MESH) {
			response = getParameter("MY");
			xbee16BitAddress = new XBee16BitAddress(response);
		}
	}
	
	/**
	 * Retrieves the 16-bit address of the XBee device.
	 * 
	 * @return The 16-bit address of the XBee device.
	 * 
	 * @see XBee16BitAddress
	 */
	public XBee16BitAddress get16BitAddress() {
		return xbee16BitAddress;
	}
	
	/**
	 * Retrieves the 64-bit address of the XBee device.
	 * 
	 * @return The 64-bit address of the XBee device.
	 * 
	 * @see XBee64BitAddress
	 */
	public XBee64BitAddress get64BitAddress() {
		return xbee64BitAddress;
	}
	
	/**
	 * Retrieves the Operating mode (AT, API or API escaped) of the XBee device.
	 * 
	 * @return The operating mode of the XBee device.
	 * 
	 * @see OperatingMode
	 */
	protected OperatingMode getOperatingMode() {
		if (isRemote())
			return localXBeeDevice.getOperatingMode();
		return operatingMode;
	}
	
	/**
	 * Retrieves the XBee Protocol of the XBee device.
	 * 
	 * @return The XBee device protocol.
	 * 
	 * @see XBeeProtocol
	 * @see #setXBeeProtocol(XBeeProtocol)
	 */
	public XBeeProtocol getXBeeProtocol() {
		return xbeeProtocol;
	}
	
	/**
	 * Retrieves the node identifier of the XBee device.
	 * 
	 * @return The node identifier of the device.
	 * 
	 * @see #setNodeID(String)
	 */
	public String getNodeID() {
		return nodeID;
	}
	
	/**
	 * Sets the node identifier of the XBee device.
	 * 
	 * @param nodeID The new node id of the device.
	 * 
	 * @throws IllegalArgumentException if {@code nodeID.length > 20}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code nodeID == null}.
	 * @throws TimeoutException if there is a timeout setting the node ID value.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getNodeID()
	 */
	public void setNodeID(String nodeID) throws TimeoutException, XBeeException {
		if (nodeID == null)
			throw new NullPointerException("Node ID cannot be null.");
		if (nodeID.length() > 20)
			throw new IllegalArgumentException("Node ID length must be less than 21.");
		
		setParameter("NI", nodeID.getBytes());
		
		this.nodeID = nodeID;
	}
	
	/**
	 * Retrieves the firmware version (hexadecimal string value) of the XBee device.
	 * 
	 * @return The firmware version of the XBee device.
	 */
	public String getFirmwareVersion() {
		return firmwareVersion;
	}
	
	/**
	 * Retrieves the hardware version of the XBee device.
	 * 
	 * @return The hardware version of the XBee device.
	 * 
	 * @see HardwareVersion
	 * @see HardwareVersionEnum
	 */
	public HardwareVersion getHardwareVersion() {
		return hardwareVersion;
	}
	
	/**
	 * Updates the current device reference with the data provided for the given 
	 * device.
	 * 
	 * <p><b>This is only for internal use.</b></p>
	 * 
	 * @param device The XBee Device to get the data from.
	 */
	public void updateDeviceDataFrom(AbstractXBeeDevice device) {
		// TODO Should the devices have the same protocol??
		// TODO Should be allow to update a local from a remote or viceversa?? Maybe 
		// this must be in the Local/Remote device class(es) and not here... 
		
		this.nodeID = device.getNodeID();
		
		// Only update the 64-bit address if the original is null or unknown.
		XBee64BitAddress addr64 = device.get64BitAddress();
		if (addr64 != null && addr64 != XBee64BitAddress.UNKNOWN_ADDRESS
				&& !addr64.equals(xbee64BitAddress) 
				&& (xbee64BitAddress == null 
					|| xbee64BitAddress.equals(XBee64BitAddress.UNKNOWN_ADDRESS))) {
			xbee64BitAddress = addr64;
		}
		
		// TODO Change here the 16-bit address or maybe in ZigBee and 802.15.4?
		// TODO Should the 16-bit address be always updated? Or following the same rule as the 64-bit address.
		XBee16BitAddress addr16 = device.get16BitAddress();
		if (addr16 != null && !addr16.equals(xbee16BitAddress)) {
			xbee16BitAddress = addr16;
		}
		
		//this.deviceType = device.deviceType; // This is not yet done.
		
		// The operating mode: only API/API2. Do we need this for a remote device?
		// The protocol of the device should be the same.
		// The hardware version should be the same.
		// The firmware version can change...
	}
	
	/**
	 * Adds the provided listener to the list of listeners to be notified
	 * when new packets are received. 
	 * 
	 * <p>If the listener has been already included, this method does nothing.
	 * </p>
	 * 
	 * @param listener Listener to be notified when new packets are received.
	 * 
	 * @see IPacketReceiveListener
	 * @see #removePacketListener(IPacketReceiveListener)
	 */
	protected void addPacketListener(IPacketReceiveListener listener) {
		if (dataReader == null)
			return;
		dataReader.addPacketReceiveListener(listener);
	}
	
	/**
	 * Removes the provided listener from the list of packets listeners. 
	 * 
	 * <p>If the listener was not in the list this method does nothing.</p>
	 * 
	 * @param listener Listener to be removed from the list of listeners.
	 * 
	 * @see IPacketReceiveListener
	 * @see #addPacketListener(IPacketReceiveListener)
	 */
	protected void removePacketListener(IPacketReceiveListener listener) {
		if (dataReader == null)
			return;
		dataReader.removePacketReceiveListener(listener);
	}
	
	/**
	 * Adds the provided listener to the list of listeners to be notified
	 * when new data is received. 
	 * 
	 * <p>If the listener has been already included this method does nothing.
	 * </p>
	 * 
	 * @param listener Listener to be notified when new data is received.
	 * 
	 * @see IDataReceiveListener
	 * @see #removeDataListener(IDataReceiveListener)
	 */
	protected void addDataListener(IDataReceiveListener listener) {
		if (dataReader == null)
			return;
		dataReader.addDataReceiveListener(listener);
	}
	
	/**
	 * Removes the provided listener from the list of data listeners. 
	 * 
	 * <p>If the listener was not in the list this method does nothing.</p>
	 * 
	 * @param listener Listener to be removed from the list of listeners.
	 * 
	 * @see IDataReceiveListener
	 * @see #addDataListener(IDataReceiveListener)
	 */
	protected void removeDataListener(IDataReceiveListener listener) {
		if (dataReader == null)
			return;
		dataReader.removeDataReceiveListener(listener);
	}
	
	/**
	 * Adds the provided listener to the list of listeners to be notified
	 * when new IO samples are received. 
	 * 
	 * <p>If the listener has been already included this method does nothing.
	 * </p>
	 * 
	 * @param listener Listener to be notified when new IO samples are received.
	 * 
	 * @throws NullPointerException if {@code listener == null}
	 * 
	 * @see IIOSampleReceiveListener
	 * @see #removeIOSampleListener(IIOSampleReceiveListener)
	 */
	protected void addIOSampleListener(IIOSampleReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		if (dataReader == null)
			return;
		dataReader.addIOSampleReceiveListener(listener);
	}
	
	/**
	 * Removes the provided listener from the list of IO samples listeners. 
	 * 
	 * <p>If the listener was not in the list this method does nothing.</p>
	 * 
	 * @param listener Listener to be removed from the list of listeners.
	 * 
	 * @throws NullPointerException if {@code listener == null}
	 * 
	 * @see IIOSampleReceiveListener
	 * @see #addIOSampleListener(IIOSampleReceiveListener)
	 */
	protected void removeIOSampleListener(IIOSampleReceiveListener listener) {
		if (listener == null)
			throw new NullPointerException("Listener cannot be null.");
		if (dataReader == null)
			return;
		dataReader.removeIOSampleReceiveListener(listener);
	}
	
	/**
	 * Sends the given AT command and waits for answer or until the configured 
	 * receive timeout expires.
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * @param command AT command to be sent.
	 * @return An {@code ATCommandResponse} object containing the response of 
	 *         the command or {@code null} if there is no response.
	 *         
	 * @throws InvalidOperatingModeException if the operating mode is different than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws TimeoutException if the configured time expires while waiting 
	 *                          for the command reply.
	 * @throws IOException if an I/O error occurs while sending the AT command.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code command == null}.
	 * 
	 * @see ATCommand
	 * @see ATCommandResponse
	 * @see #setReceiveTimeout(int)
	 * @see #getReceiveTimeout()
	 */
	protected ATCommandResponse sendATCommand(ATCommand command) 
			throws InvalidOperatingModeException, TimeoutException, IOException {
		// Check if command is null.
		if (command == null)
			throw new NullPointerException("AT command cannot be null.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		ATCommandResponse response = null;
		OperatingMode operatingMode = getOperatingMode();
		switch (operatingMode) {
		case AT:
		case UNKNOWN:
		default:
			throw new InvalidOperatingModeException(operatingMode);
		case API:
		case API_ESCAPE:
			// Create the corresponding AT command packet depending on if the device is local or remote.
			XBeePacket packet;
			if (isRemote()) {
				XBee16BitAddress remote16BitAddress = get16BitAddress();
				if (remote16BitAddress == null)
					remote16BitAddress = XBee16BitAddress.UNKNOWN_ADDRESS;
				int remoteATCommandOptions = RemoteATCommandOptions.OPTION_NONE;
				if (isApplyConfigurationChangesEnabled())
					remoteATCommandOptions |= RemoteATCommandOptions.OPTION_APPLY_CHANGES;
				packet = new RemoteATCommandPacket(getNextFrameID(), get64BitAddress(), remote16BitAddress, remoteATCommandOptions, command.getCommand(), command.getParameter());
			} else {
				// TODO: If the apply configuration changes option is enabled, send an AT command frame. 
				//       If the apply configuration changes option is disabled, send a queue AT command frame.
				packet = new ATCommandPacket(getNextFrameID(), command.getCommand(), command.getParameter());
			}
			if (command.getParameter() == null)
				logger.debug(toString() + "Sending AT command '{}'.", command.getCommand());
			else
				logger.debug(toString() + "Sending AT command '{} {}'.", command.getCommand(), HexUtils.prettyHexString(command.getParameter()));
			try {
				// Send the packet and build the corresponding response depending on if the device is local or remote.
				XBeePacket answerPacket;
				if (isRemote())
					answerPacket = localXBeeDevice.sendXBeePacket(packet);
				else
					answerPacket = sendXBeePacket(packet);
				if (answerPacket instanceof ATCommandResponsePacket)
					response = new ATCommandResponse(command, ((ATCommandResponsePacket)answerPacket).getCommandValue(), ((ATCommandResponsePacket)answerPacket).getStatus());
				else if (answerPacket instanceof RemoteATCommandResponsePacket)
					response = new ATCommandResponse(command, ((RemoteATCommandResponsePacket)answerPacket).getCommandValue(), ((RemoteATCommandResponsePacket)answerPacket).getStatus());
				
				if (response.getResponse() != null)
					logger.debug(toString() + "AT command response: {}.", HexUtils.prettyHexString(response.getResponse()));
				else
					logger.debug(toString() + "AT command response: null.");
			} catch (ClassCastException e) {
				logger.error("Received an invalid packet type after sending an AT command packet." + e);
			}
		}
		return response;
	}
	
	/**
	 * Sends the given XBee packet asynchronously.
	 * 
	 * <p>To be notified when the answer is received, use 
	 * {@link #sendXBeePacket(XBeePacket, IPacketReceiveListener)}.</p>
	 * 
	 * @param packet XBee packet to be sent asynchronously.
	 * 
	 * @throws InvalidOperatingModeException if the operating mode is different than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws IOException if an I/O error occurs while sending the XBee packet.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code packet == null}.
	 * 
	 * @see XBeePacket
	 * @see #sendXBeePacketAsync(XBeePacket)
	 * @see #sendXBeePacket(XBeePacket)
	 * @see #sendXBeePacket(XBeePacket, boolean)
	 * @see #sendXBeePacket(XBeePacket, IPacketReceiveListener)
	 * @see #sendXBeePacket(XBeePacket, IPacketReceiveListener, boolean)
	 */
	protected void sendXBeePacketAsync(XBeePacket packet) 
			throws InvalidOperatingModeException, IOException {
		sendXBeePacket(packet, null);
	}
	
	/**
	 * Sends the given XBee packet asynchronously and registers the given packet
	 * listener (if not {@code null}) to wait for an answer.
	 * 
	 * @param packet XBee packet to be sent.
	 * @param packetReceiveListener Listener for the operation, {@code null} 
	 *                              not to be notified when the answer arrives.
	 *                              
	 * @throws InvalidOperatingModeException if the operating mode is different than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws IOException if an I/O error occurs while sending the XBee packet.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code packet == null}.
	 * 
	 * @see XBeePacket
	 * @see IPacketReceiveListener
	 * @see #sendXBeePacket(XBeePacket)
	 * @see #sendXBeePacket(XBeePacket, boolean)
	 * @see #sendXBeePacket(XBeePacket, IPacketReceiveListener)
	 * @see #sendXBeePacketAsync(XBeePacket)
	 * @see #sendXBeePacketAsync(XBeePacket, boolean)
	 */
	protected void sendXBeePacket(XBeePacket packet, IPacketReceiveListener packetReceiveListener)
			throws InvalidOperatingModeException, IOException {
		// Check if the packet to send is null.
		if (packet == null)
			throw new NullPointerException("XBee packet cannot be null.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		OperatingMode operatingMode = getOperatingMode();
		switch (operatingMode) {
		case AT:
		case UNKNOWN:
		default:
			throw new InvalidOperatingModeException(operatingMode);
		case API:
		case API_ESCAPE:
			// Add the required frame ID and subscribe listener if given.
			if (packet instanceof XBeeAPIPacket) {
				if (((XBeeAPIPacket)packet).needsAPIFrameID()) {
					if (((XBeeAPIPacket)packet).getFrameID() == XBeeAPIPacket.NO_FRAME_ID)
						((XBeeAPIPacket)packet).setFrameID(getNextFrameID());
					if (packetReceiveListener != null)
						dataReader.addPacketReceiveListener(packetReceiveListener, ((XBeeAPIPacket)packet).getFrameID());
				} else if (packetReceiveListener != null)
					dataReader.addPacketReceiveListener(packetReceiveListener);
			}
			
			// Write packet data.
			writePacket(packet);
			break;
		}
	}
	
	/**
	 * Sends the given XBee packet synchronously and blocks until response is 
	 * received or receive timeout is reached.
	 * 
	 * <p>The receive timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>Use {@link #sendXBeePacketAsync(XBeePacket, boolean)} for non-blocking 
	 * operations.</p>
	 * 
	 * @param packet XBee packet to be sent.
	 * @return An {@code XBeePacket} containing the response of the sent packet 
	 *         or {@code null} if there is no response.
	 *         
	 * @throws InvalidOperatingModeException if the operating mode is different than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws TimeoutException if the configured time expires while waiting for the packet reply.
	 * @throws IOException if an I/O error occurs while sending the XBee packet.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code packet == null}.
	 * 
	 * @see XBeePacket
	 * @see #sendXBeePacket(XBeePacket)
	 * @see #sendXBeePacket(XBeePacket, IPacketReceiveListener)
	 * @see #sendXBeePacket(XBeePacket, IPacketReceiveListener, boolean)
	 * @see #sendXBeePacketAsync(XBeePacket)
	 * @see #sendXBeePacketAsync(XBeePacket, boolean)
	 * @see #setReceiveTimeout(int)
	 * @see #getReceiveTimeout()
	 */
	protected XBeePacket sendXBeePacket(final XBeePacket packet) 
			throws InvalidOperatingModeException, TimeoutException, IOException {
		// Check if the packet to send is null.
		if (packet == null)
			throw new NullPointerException("XBee packet cannot be null.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		OperatingMode operatingMode = getOperatingMode();
		switch (operatingMode) {
		case AT:
		case UNKNOWN:
		default:
			throw new InvalidOperatingModeException(operatingMode);
		case API:
		case API_ESCAPE:
			// Build response container.
			ArrayList<XBeePacket> responseList = new ArrayList<XBeePacket>();
			
			// If the packet does not need frame ID, send it async. and return null.
			if (packet instanceof XBeeAPIPacket) {
				if (!((XBeeAPIPacket)packet).needsAPIFrameID()) {
					sendXBeePacketAsync(packet);
					return null;
				}
			} else {
				sendXBeePacketAsync(packet);
				return null;
			}
			
			// Add the required frame ID to the packet if necessary.
			insertFrameID(packet);
			
			// Generate a packet received listener for the packet to be sent.
			IPacketReceiveListener packetReceiveListener = createPacketReceivedListener(packet, responseList);
			
			// Add the packet listener to the data reader.
			addPacketListener(packetReceiveListener);
			
			// Write the packet data.
			writePacket(packet);
			try {
				// Wait for response or timeout.
				synchronized (responseList) {
					try {
						responseList.wait(receiveTimeout);
					} catch (InterruptedException e) {}
				}
				// After the wait check if we received any response, if not throw timeout exception.
				if (responseList.size() < 1)
					throw new TimeoutException();
				// Return the received packet.
				return responseList.get(0);
			} finally {
				// Always remove the packet listener from the list.
				removePacketListener(packetReceiveListener);
			}
		}
	}
	
	/**
	 * Insert (if possible) the next frame ID stored in the device to the 
	 * provided packet.
	 * 
	 * @param xbeePacket The packet to add the frame ID.
	 * 
	 * @see XBeePacket
	 */
	private void insertFrameID(XBeePacket xbeePacket) {
		if (xbeePacket instanceof XBeeAPIPacket)
			return;
		
		if (((XBeeAPIPacket)xbeePacket).needsAPIFrameID() && ((XBeeAPIPacket)xbeePacket).getFrameID() == XBeeAPIPacket.NO_FRAME_ID)
			((XBeeAPIPacket)xbeePacket).setFrameID(getNextFrameID());
	}
	
	/**
	 * Retrieves the packet listener corresponding to the provided sent packet. 
	 * 
	 * <p>The listener will filter those packets  matching with the Frame ID of 
	 * the sent packet storing them in the provided responseList array.</p>
	 * 
	 * @param sentPacket The packet sent.
	 * @param responseList List of packets received that correspond to the 
	 *                     frame ID of the packet sent.
	 * 
	 * @return A packet receive listener that will filter the packets received 
	 *         corresponding to the sent one.
	 *         
	 * @see IPacketReceiveListener
	 * @see XBeePacket
	 */
	private IPacketReceiveListener createPacketReceivedListener(final XBeePacket sentPacket, final ArrayList<XBeePacket> responseList) {
		IPacketReceiveListener packetReceiveListener = new IPacketReceiveListener() {
			/*
			 * (non-Javadoc)
			 * @see com.digi.xbee.api.listeners.IPacketReceiveListener#packetReceived(com.digi.xbee.api.packet.XBeePacket)
			 */
			@Override
			public void packetReceived(XBeePacket receivedPacket) {
				// Check if it is the packet we are waiting for.
				if (((XBeeAPIPacket)receivedPacket).checkFrameID((((XBeeAPIPacket)sentPacket).getFrameID()))) {
					// Security check to avoid class cast exceptions. It has been observed that parallel processes 
					// using the same connection but with different frame index may collide and cause this exception at some point.
					if (sentPacket instanceof XBeeAPIPacket
							&& receivedPacket instanceof XBeeAPIPacket) {
						XBeeAPIPacket sentAPIPacket = (XBeeAPIPacket)sentPacket;
						XBeeAPIPacket receivedAPIPacket = (XBeeAPIPacket)receivedPacket;
						
						// If the packet sent is an AT command, verify that the received one is an AT command response and 
						// the command matches in both packets.
						if (sentAPIPacket.getFrameType() == APIFrameType.AT_COMMAND) {
							if (receivedAPIPacket.getFrameType() != APIFrameType.AT_COMMAND_RESPONSE)
								return;
							if (!((ATCommandPacket)sentAPIPacket).getCommand().equalsIgnoreCase(((ATCommandResponsePacket)receivedPacket).getCommand()))
								return;
						}
						// If the packet sent is a remote AT command, verify that the received one is a remote AT command response and 
						// the command matches in both packets.
						if (sentAPIPacket.getFrameType() == APIFrameType.REMOTE_AT_COMMAND_REQUEST) {
							if (receivedAPIPacket.getFrameType() != APIFrameType.REMOTE_AT_COMMAND_RESPONSE)
								return;
							if (!((RemoteATCommandPacket)sentAPIPacket).getCommand().equalsIgnoreCase(((RemoteATCommandResponsePacket)receivedPacket).getCommand()))
								return;
						}
					}
					
					// Verify that the sent packet is not the received one! This can happen when the echo mode is enabled in the 
					// serial port.
					if (!isSamePacket(sentPacket, receivedPacket)) {
						responseList.add(receivedPacket);
						synchronized (responseList) {
							responseList.notify();
						}
					}
				}
			}
		};
		
		return packetReceiveListener;
	}
	
	/**
	 * Retrieves whether or not the sent packet is the same than the received one.
	 * 
	 * @param sentPacket The packet sent.
	 * @param receivedPacket The packet received.
	 * 
	 * @return {@code true} if the sent packet is the same than the received 
	 *         one, {@code false} otherwise.
	 *         
	 * @see XBeePacket
	 */
	private boolean isSamePacket(XBeePacket sentPacket, XBeePacket receivedPacket) {
		// TODO Should not we implement the {@code equals} method in the XBeePacket??
		if (HexUtils.byteArrayToHexString(sentPacket.generateByteArray()).equals(HexUtils.byteArrayToHexString(receivedPacket.generateByteArray())))
			return true;
		return false;
	}
	
	/**
	 * Writes the given XBee packet in the connection interface.
	 * 
	 * @param packet XBee packet to be written.
	 * 
	 * @throws IOException if an I/O error occurs while writing the XBee packet 
	 *                     in the connection interface.
	 */
	private void writePacket(XBeePacket packet) throws IOException {
		logger.debug(toString() + "Sending XBee packet: \n{}", packet.toPrettyString());
		// Write bytes with the required escaping mode.
		switch (operatingMode) {
		case API:
		default:
			connectionInterface.writeData(packet.generateByteArray());
			break;
		case API_ESCAPE:
			connectionInterface.writeData(packet.generateByteArrayEscaped());
			break;
		}
	}
	
	/**
	 * Retrieves the next Frame ID of the XBee protocol.
	 * 
	 * @return The next Frame ID.
	 */
	protected int getNextFrameID() {
		if (isRemote())
			return localXBeeDevice.getNextFrameID();
		if (currentFrameID == 0xff) {
			// Reset counter.
			currentFrameID = 1;
		} else
			currentFrameID ++;
		return currentFrameID;
	}
	
	/**
	 * Sends the provided {@code XBeePacket} and determines if the transmission 
	 * status is success for synchronous transmissions. If the status is not 
	 * success, an {@code TransmitException} is thrown.
	 * 
	 * @param packet The {@code XBeePacket} to be sent.
	 * @param asyncTransmission Determines whether or not the transmission 
	 *                          should be made asynchronously.
	 * 
	 * @throws TransmitException if {@code packet} is not an instance of {@code TransmitStatusPacket} or 
	 *                           if {@code packet} is not an instance of {@code TXStatusPacket} or 
	 *                           if its transmit status is different than {@code XBeeTransmitStatus.SUCCESS}.
	 * @throws XBeeException if there is any other XBee related error.
	 * 
	 * @see XBeePacket
	 */
	protected void sendAndCheckXBeePacket(XBeePacket packet, boolean asyncTransmission) throws TransmitException, XBeeException {
		XBeePacket receivedPacket = null;
		
		// Send the XBee packet.
		try {
			if (asyncTransmission)
				sendXBeePacketAsync(packet);
			else
				receivedPacket = sendXBeePacket(packet);
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
		
		// If the transmission is async. we are done.
		if (asyncTransmission)
			return;
		
		// Check if the packet received is a valid transmit status packet.
		if (receivedPacket == null)
			throw new TransmitException(null);
		if (receivedPacket instanceof TransmitStatusPacket) {
			if (((TransmitStatusPacket)receivedPacket).getTransmitStatus() == null)
				throw new TransmitException(null);
			else if (((TransmitStatusPacket)receivedPacket).getTransmitStatus() != XBeeTransmitStatus.SUCCESS)
				throw new TransmitException(((TransmitStatusPacket)receivedPacket).getTransmitStatus());
		} else if (receivedPacket instanceof TXStatusPacket) {
			if (((TXStatusPacket)receivedPacket).getTransmitStatus() == null)
				throw new TransmitException(null);
			else if (((TXStatusPacket)receivedPacket).getTransmitStatus() != XBeeTransmitStatus.SUCCESS)
				throw new TransmitException(((TXStatusPacket)receivedPacket).getTransmitStatus());
		} else
			throw new TransmitException(null);
	}
	
	/**
	 * Sets the configuration of the given IO line.
	 * 
	 * @param ioLine The IO line to configure.
	 * @param mode The IO mode to set to the IO line.
	 * 
	 * @throws TimeoutException if there is a timeout sending the set 
	 *                          configuration command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code ioLine == null} or
	 *                              if {@code ioMode == null}.
	 * 
	 * @see IOLine
	 * @see IOMode
	 * @see #getIOConfiguration(IOLine)
	 */
	public void setIOConfiguration(IOLine ioLine, IOMode ioMode) throws TimeoutException, XBeeException {
		// Check IO line.
		if (ioLine == null)
			throw new NullPointerException("IO line cannot be null.");
		if (ioMode == null)
			throw new NullPointerException("IO mode cannot be null.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		setParameter(ioLine.getConfigurationATCommand(), new byte[]{(byte)ioMode.getID()});
	}
	
	/**
	 * Retrieves the configuration mode of the provided IO line.
	 * 
	 * @param ioLine The IO line to get its configuration.
	 * 
	 * @return The IO mode (configuration) of the provided IO line.
	 * 
	 * @throws TimeoutException if there is a timeout sending the get 
	 *                          configuration command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code ioLine == null}.
	 * 
	 * @see IOLine
	 * @see IOMode
	 * @see #setIOConfiguration(IOLine, IOMode)
	 */
	public IOMode getIOConfiguration(IOLine ioLine) throws TimeoutException, XBeeException {
		// Check IO line.
		if (ioLine == null)
			throw new NullPointerException("DIO pin cannot be null.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		// Check if the received configuration mode is valid.
		int ioModeValue = getParameter(ioLine.getConfigurationATCommand())[0];
		IOMode dioMode = IOMode.getIOMode(ioModeValue, ioLine);
		if (dioMode == null)
			throw new OperationNotSupportedException("Received configuration mode '" + HexUtils.integerToHexString(ioModeValue, 1) + "' is not valid.");
		
		// Return the configuration mode.
		return dioMode;
	}
	
	/**
	 * Sets the digital value (high or low) to the provided IO line.
	 * 
	 * @param ioLine The IO line to set its value.
	 * @param value The IOValue to set to the IO line ({@code HIGH} or 
	 *              {@code LOW}).
	 * 
	 * @throws TimeoutException if there is a timeout sending the set DIO 
	 *                          command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code ioLine == null} or 
	 *                              if {@code ioValue == null}.
	 * 
	 * @see IOLine
	 * @see IOValue
	 * @see IOMode#DIGITAL_OUT_HIGH
	 * @see IOMode#DIGITAL_OUT_LOW
	 * @see #getIOConfiguration(IOLine)
	 * @see #setIOConfiguration(IOLine, IOMode)
	 */
	public void setDIOValue(IOLine ioLine, IOValue ioValue) throws TimeoutException, XBeeException {
		// Check IO line.
		if (ioLine == null)
			throw new NullPointerException("IO line cannot be null.");
		// Check IO value.
		if (ioValue == null)
			throw new NullPointerException("IO value cannot be null.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		setParameter(ioLine.getConfigurationATCommand(), new byte[]{(byte)ioValue.getID()});
	}
	
	/**
	 * Retrieves the digital value of the provided IO line (must be configured 
	 * as digital I/O).
	 * 
	 * @param ioLine The IO line to get its digital value.
	 * 
	 * @return The digital value corresponding to the provided IO line.
	 * 
	 * @throws NullPointerException if {@code ioLine == null}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout sending the get IO values 
	 *                          command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see IOLine
	 * @see IOMode#DIGITAL_IN
	 * @see IOMode#DIGITAL_OUT_HIGH
	 * @see IOMode#DIGITAL_OUT_LOW
	 * @see #getIOConfiguration(IOLine)
	 * @see #setIOConfiguration(IOLine, IOMode)
	 */
	public IOValue getDIOValue(IOLine ioLine) throws TimeoutException, XBeeException {
		// Check IO line.
		if (ioLine == null)
			throw new NullPointerException("IO line cannot be null.");
		
		// Obtain an IO Sample from the XBee device.
		IOSample ioSample = readIOSample();
		
		// Check if the IO sample contains the expected IO line and value.
		if (!ioSample.hasDigitalValues() || !ioSample.getDigitalValues().containsKey(ioLine))
			throw new OperationNotSupportedException("Answer does not contain digital data for " + ioLine.getName() + ".");
		
		// Return the digital value. 
		return ioSample.getDigitalValues().get(ioLine);
	}
	
	/**
	 * Sets the duty cycle (in %) of the provided IO line. 
	 * 
	 * <p>IO line must be PWM capable({@code hasPWMCapability()}) and 
	 * it must be configured as PWM Output ({@code IOMode.PWM}).</p>
	 * 
	 * @param ioLine The IO line to set its duty cycle value.
	 * @param value The duty cycle of the PWM.
	 * 
	 * @throws TimeoutException if there is a timeout sending the set PWM duty 
	 *                          cycle command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws IllegalArgumentException if {@code ioLine.hasPWMCapability() == false} or 
	 *                                  if {@code value < 0} or
	 *                                  if {@code value > 1023}.
	 * @throws NullPointerException if {@code ioLine == null}.
	 * 
	 * @see IOLine
	 * @see IOMode#PWM
	 * @see #getPWMDutyCycle(IOLine)
	 * @see #getIOConfiguration(IOLine)
	 * @see #setIOConfiguration(IOLine, IOMode)
	 */
	public void setPWMDutyCycle(IOLine ioLine, double dutyCycle) throws TimeoutException, XBeeException {
		// Check IO line.
		if (ioLine == null)
			throw new NullPointerException("IO line cannot be null.");
		// Check if the IO line has PWM capability.
		if (!ioLine.hasPWMCapability())
			throw new IllegalArgumentException("Provided IO line does not have PWM capability.");
		// Check duty cycle limits.
		if (dutyCycle < 0 || dutyCycle > 100)
			throw new IllegalArgumentException("Duty Cycle must be between 0% and 100%.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		// Convert the value.
		int finaldutyCycle = (int)(dutyCycle * 1023.0/100.0);
		
		setParameter(ioLine.getPWMDutyCycleATCommand(), ByteUtils.intToByteArray(finaldutyCycle));
	}
	
	/**
	 * Gets the PWM duty cycle (in %) corresponding to the provided IO line.
	 * 
	 * <p>IO line must be PWM capable ({@code hasPWMCapability()}) and 
	 * it must be configured as PWM Output ({@code IOMode.PWM}).</p>
	 * 
	 * @param ioLine The IO line to get its PWM duty cycle.
	 * 
	 * @return The PWM duty cycle value corresponding to the provided IO line 
	 *         (0% - 100%).
	 * 
	 * @throws TimeoutException if there is a timeout sending the get PWM duty 
	 *                          cycle command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws IllegalArgumentException if {@code ioLine.hasPWMCapability() == false}.
	 * @throws NullPointerException if {@code ioLine == null}.
	 * 
	 * @see IOLine
	 * @see IOMode#PWM
	 * @see #setPWMDutyCycle(IOLine, double)
	 * @see #getIOConfiguration(IOLine)
	 * @see #setIOConfiguration(IOLine, IOMode)
	 */
	public double getPWMDutyCycle(IOLine ioLine) throws TimeoutException, XBeeException {
		// Check IO line.
		if (ioLine == null)
			throw new NullPointerException("IO line cannot be null.");
		// Check if the IO line has PWM capability.
		if (!ioLine.hasPWMCapability())
			throw new IllegalArgumentException("Provided IO line does not have PWM capability.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		byte[] value = getParameter(ioLine.getPWMDutyCycleATCommand());
		
		// Return the PWM duty cycle value.
		int readValue = ByteUtils.byteArrayToInt(value);
		return Math.round((readValue * 100.0/1023.0) * 100.0) / 100.0;
	}
	
	/**
	 * Retrieves the analog value of the provided IO line (must be configured 
	 * as ADC).
	 * 
	 * @param ioLine The IO line to get its analog value.
	 * 
	 * @return The analog value corresponding to the provided IO line.
	 * 
	 * @throws TimeoutException if there is a timeout sending the get IO values
	 *                          command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code ioLine == null}.
	 * 
	 * @see IOLine
	 * @see IOMode#ADC
	 * @see #getIOConfiguration(IOLine)
	 * @see #setIOConfiguration(IOLine, IOMode)
	 */
	public int getADCValue(IOLine ioLine) throws TimeoutException, XBeeException {
		// Check IO line.
		if (ioLine == null)
			throw new NullPointerException("IO line cannot be null.");
		
		// Obtain an IO Sample from the XBee device.
		IOSample ioSample = readIOSample();
		
		// Check if the IO sample contains the expected IO line and value.
		if (!ioSample.hasAnalogValues() || !ioSample.getAnalogValues().containsKey(ioLine))
			throw new OperationNotSupportedException("Answer does not contain analog data for " + ioLine.getName() + ".");
		
		// Return the analog value.
		return ioSample.getAnalogValues().get(ioLine);
	}
	
	/**
	 * Sets the 64 bit destination extended address.
	 * 
	 * <p>{@link XBee64BitAddress#BROADCAST_ADDRESS} is the broadcast address 
	 * for the PAN. {@link XBee64BitAddress#COORDINATOR_ADDRESS} can be used to 
	 * address the Pan Coordinator.</p>
	 * 
	 * @param xbee64BitAddress Destination address.
	 * 
	 * @throws NullPointerException if {@code xbee64BitAddress == null}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout sending the set 
	 *                          destination address command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see XBee64BitAddress
	 */
	public void setDestinationAddress(XBee64BitAddress xbee64BitAddress) throws TimeoutException, XBeeException {
		if (xbee64BitAddress == null)
			throw new NullPointerException("Address cannot be null.");
		
		// This method needs to apply changes after modifying the destination 
		// address, but only if the destination address could be set successfully.
		boolean applyChanges = isApplyConfigurationChangesEnabled();
		if (applyChanges)
			enableApplyConfigurationChanges(false);
		
		byte[] address = xbee64BitAddress.getValue();
		try {
			setParameter("DH", Arrays.copyOfRange(address, 0, 4));
			setParameter("DL", Arrays.copyOfRange(address, 4, 8));
			applyChanges();
		} finally {
			// Always restore the old value of the AC.
			enableApplyConfigurationChanges(applyChanges);
		}
	}
	
	/**
	 * Retrieves the 64 bit destination extended address.
	 * 
	 * @return 64 bit destination address.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout sending the get
	 *                          destination address command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see XBee64BitAddress
	 */
	public XBee64BitAddress getDestinationAddress() throws TimeoutException, XBeeException {
		byte[] dh = getParameter("DH");
		byte[] dl = getParameter("DL");
		byte[] address = new byte[dh.length + dl.length];
		
		System.arraycopy(dh, 0, address, 0, dh.length);
		System.arraycopy(dl, 0, address, dh.length, dl.length);
		
		return new XBee64BitAddress(address);
	}
	
	/**
	 * Sets the IO sampling rate to enable periodic sampling.
	 * 
	 * <p>All enabled digital IO and analog inputs will be sampled and
	 * transmitted every {@code rate} milliseconds to the configured destination
	 * address.</p>
	 * 
	 * <p>A sample rate of {@code 0} ms. disables this feature.</p>
	 * 
	 * @param rate IO sampling rate in milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code rate < 0} or {@code rate >
	 *                                  0xFFFF}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout sending the set IO
	 *                          sampling rate command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setDestinationAddress(XBee64BitAddress)
	 * @see #getDestinationAddress()
	 * @see #getIOSamplingRate()
	 */
	public void setIOSamplingRate(int rate) throws TimeoutException, XBeeException {
		// Check range.
		if (rate < 0 || rate > 0xFFFF)
			throw new IllegalArgumentException("Rate must be between 0 and 0xFFFF.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		setParameter("IR", ByteUtils.intToByteArray(rate));
	}
	
	/**
	 * Retrieves the IO sampling rate.
	 * 
	 * <p>A sample rate of {@code 0} means the IO sampling feature is disabled.
	 * </p>
	 * 
	 * @return IO sampling rate in milliseconds.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout sending the get IO
	 *                          sampling rate command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setIOSamplingRate(int)
	 */
	public int getIOSamplingRate() throws TimeoutException, XBeeException {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		byte[] rate = getParameter("IR");
		return ByteUtils.byteArrayToInt(rate);
	}
	
	/**
	 * Sets the digital IO lines to be monitored and sampled when their status
	 * changes.
	 * 
	 * <p>If a change is detected on an enabled digital IO pin, a digital IO
	 * sample is immediately transmitted to the configured destination address.
	 * </p>
	 * 
	 * <p>A {@code null} set disables this feature.</p>
	 * 
	 * @param lines Set of IO lines to be monitored, {@code null} to disable 
	 *              this feature.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout sending the set DIO
	 *                          change detection command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getDIOChangeDetection()
	 * @see #setDestinationAddress(XBee64BitAddress)
	 * @see #getDestinationAddress()
	 */
	public void setDIOChangeDetection(Set<IOLine> lines) throws TimeoutException, XBeeException {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		byte[] bitfield = new byte[2];
		
		if (lines != null) {
			for (IOLine line : lines) {
				int i = line.getIndex();
				if (i < 8)
					bitfield[1] = (byte) (bitfield[1] | (1 << i));
				else
					bitfield[0] = (byte) (bitfield[0] | (1 << i - 8));
			}
		}
		
		setParameter("IC", bitfield);
	}
	
	/**
	 * Retrieves the set of IO lines that are monitored for change detection.
	 * 
	 * <p>A {@code null} set means the DIO change detection feature is disabled.
	 * </p>
	 * 
	 * @return Set of digital IO lines that are monitored for change detection,
	 *         {@code null} if there are no monitored lines.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout sending the get DIO
	 *                          change detection command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setDIOChangeDetection(Set)
	 */
	public Set<IOLine> getDIOChangeDetection() throws TimeoutException, XBeeException {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		byte[] bitfield = getParameter("IC");
		TreeSet<IOLine> lines = new TreeSet<IOLine>();
		int mask = (bitfield[0] << 8) + bitfield[1];
		
		for (int i = 0; i < 16; i++) {
			if (ByteUtils.isBitEnabled(mask, i))
				lines.add(IOLine.getDIO(i));
		}
		
		if (lines.size() > 0)
			return lines;
		return null;
	}
	
	/**
	 * Applies changes to all command registers causing queued command register
	 * values to be applied.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout sending the get Apply
	 *                          Changes command.
	 * @throws XBeeException if there is any other XBee related exception.
	 */
	public void applyChanges() throws TimeoutException, XBeeException {
		executeParameter("AC");
	}
	
	/**
	 * Checks if the provided {@code ATCommandResponse} is valid throwing an 
	 * {@code ATCommandException} in case it is not.
	 * 
	 * @param response The {@code ATCommandResponse} to check.
	 * 
	 * @throws ATCommandException if {@code response == null} or 
	 *                            if {@code response.getResponseStatus() != ATCommandStatus.OK}.
	 */
	protected void checkATCommandResponseIsValid(ATCommandResponse response) throws ATCommandException {
		if (response == null || response.getResponseStatus() == null)
			throw new ATCommandException(null);
		else if (response.getResponseStatus() != ATCommandStatus.OK)
			throw new ATCommandException(response.getResponseStatus());
	}
	
	/**
	 * Retrieves an IO sample from the XBee device containing the value of all
	 * enabled digital IO and analog input channels.
	 * 
	 * @return An IO sample containing the value of all enabled digital IO and
	 *         analog input channels.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout getting the IO sample.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see IOSample
	 */
	public IOSample readIOSample() throws TimeoutException, XBeeException {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		// Try to build an IO Sample from the sample payload.
		byte[] samplePayload = getParameter("IS");
		IOSample ioSample;
		
		// If it is a local 802.15.4 device, the response does not contain the
		// IO sample, so we have to create a packet listener to receive the
		// sample.
		if (!isRemote() && getXBeeProtocol() == XBeeProtocol.RAW_802_15_4) {
			samplePayload = receiveRaw802IOPacket();
			if (samplePayload == null)
				throw new TimeoutException("Timeout waiting for the IO response packet.");
		}
		
		try {
			ioSample = new IOSample(samplePayload);
		} catch (IllegalArgumentException e) {
			throw new XBeeException("Couldn't create the IO sample.", e);
		} catch (NullPointerException e) {
			throw new XBeeException("Couldn't create the IO sample.", e);
		}
		return ioSample;
	}
	
	/**
	 * Retrieves the latest 802.15.4 IO packet and returns its value.
	 * 
	 * @return The value of the latest received 802.15.4 IO packet. 
	 */
	private byte[] receiveRaw802IOPacket() {
		ioPacketReceived = false;
		ioPacketPayload = null;
		addPacketListener(IOPacketReceiveListener);
		synchronized (ioLock) {
			try {
				ioLock.wait(receiveTimeout);
			} catch (InterruptedException e) { }
		}
		removePacketListener(IOPacketReceiveListener);
		if (ioPacketReceived)
			return ioPacketPayload;
		return null;
	}
	
	/**
	 * Custom listener for 802.15.4 IO packets. It will try to receive an 802.15.4 IO 
	 * sample packet.
	 * 
	 * <p>When an IO sample packet is received, it saves its payload and notifies 
	 * the object that was waiting for the reception.</p>
	 */
	private IPacketReceiveListener IOPacketReceiveListener = new IPacketReceiveListener() {
		/*
		 * (non-Javadoc)
		 * @see com.digi.xbee.api.listeners.IPacketReceiveListener#packetReceived(com.digi.xbee.api.packet.XBeePacket)
		 */
		@Override
		public void packetReceived(XBeePacket receivedPacket) {
			// Discard non API packets.
			if (!(receivedPacket instanceof XBeeAPIPacket))
				return;
			// If we already have received an IO packet, ignore this packet.
			if (ioPacketReceived)
				return;
			
			// Save the packet value (IO sample payload)
			switch (((XBeeAPIPacket)receivedPacket).getFrameType()) {
			case IO_DATA_SAMPLE_RX_INDICATOR:
				ioPacketPayload = ((IODataSampleRxIndicatorPacket)receivedPacket).getRFData();
				break;
			case RX_IO_16:
				ioPacketPayload = ((RX16IOPacket)receivedPacket).getRFData();
				break;
			case RX_IO_64:
				ioPacketPayload = ((RX64IOPacket)receivedPacket).getRFData();
				break;
			default:
				return;
			}
			// Set the IO packet received flag.
			ioPacketReceived = true;
			
			// Continue execution by notifying the lock object.
			synchronized (ioLock) {
				ioLock.notify();
			}
		}
	};
	
	/**
	 * Performs a software reset on the module and blocks until the process
	 * is completed.
	 * 
	 * @throws TimeoutException if the configured time expires while waiting 
	 *                          for the command reply.
	 * @throws XBeeException if there is any other XBee related exception.
	 */
	abstract public void reset() throws TimeoutException, XBeeException;
	
	/**
	 * Sets the given parameter with the provided value in the XBee device.
	 * 
	 * @param parameter The AT command corresponding to the parameter to be set.
	 * @param parameterValue The value of the parameter to set.
	 * 
	 * @throws IllegalArgumentException if {@code parameter.length() != 2}.
	 * @throws NullPointerException if {@code parameter == null} or 
	 *                              if {@code parameterValue == null}.
	 * @throws TimeoutException if there is a timeout setting the parameter.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getParameter(String)
	 * @see #executeParameter(String)
	 */
	public void setParameter(String parameter, byte[] parameterValue) throws TimeoutException, XBeeException {
		if (parameterValue == null)
			throw new NullPointerException("Value of the parameter cannot be null.");
		
		sendParameter(parameter, parameterValue);
	}
	
	/**
	 * Gets the value of the given parameter from the XBee device.
	 * 
	 * @param parameter The AT command corresponding to the parameter to be get.
	 * @return A byte array containing the value of the parameter.
	 * 
	 * @throws IllegalArgumentException if {@code parameter.length() != 2}.
	 * @throws NullPointerException if {@code parameter == null}.
	 * @throws TimeoutException if there is a timeout getting the parameter value.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setParameter(String)
	 * @see #executeParameter(String)
	 */
	public byte[] getParameter(String parameter) throws TimeoutException, XBeeException {
		byte[] parameterValue = sendParameter(parameter, null);
		
		// Check if the response is null, if so throw an exception (maybe it was a write-only parameter).
		if (parameterValue == null)
			throw new OperationNotSupportedException("Couldn't get the '" + parameter + "' value.");
		return parameterValue;
	}
	
	/**
	 * Executes the given parameter in the XBee device. This method is intended to be used for 
	 * those parameters that cannot be read or written, they just execute some action in the 
	 * XBee module.
	 * 
	 * @param parameter The AT command corresponding to the parameter to be executed.
	 * 
	 * @throws IllegalArgumentException if {@code parameter.length() != 2}.
	 * @throws NullPointerException if {@code parameter == null}.
	 * @throws TimeoutException if there is a timeout executing the parameter.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setParameter(String)
	 * @see #getParameter(String)
	 */
	public void executeParameter(String parameter) throws TimeoutException, XBeeException {
		sendParameter(parameter, null);
	}
	
	/**
	 * Sends the given AT parameter to the XBee device with an optional argument or value 
	 * and returns the response (likely the value) of that parameter in a byte array format.
	 * 
	 * @param parameter The AT command corresponding to the parameter to be executed.
	 * @param parameterValue The value of the parameter to set (if any).
	 * 
	 * @throws IllegalArgumentException if {@code parameter.length() != 2}.
	 * @throws NullPointerException if {@code parameter == null}.
	 * @throws TimeoutException if there is a timeout executing the parameter.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setParameter(String)
	 * @see #getParameter(String)
	 * @see #executeParameter(String)
	 */
	private byte[] sendParameter(String parameter, byte[] parameterValue) throws TimeoutException, XBeeException {
		if (parameter == null)
			throw new NullPointerException("Parameter cannot be null.");
		if (parameter.length() != 2)
			throw new IllegalArgumentException("Parameter must contain exactly 2 characters.");
		
		ATCommand atCommand = new ATCommand(parameter, parameterValue);
		
		// Create and send the AT Command.
		ATCommandResponse response = null;
		try {
			response = sendATCommand(atCommand);
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
		
		// Check if AT Command response is valid.
		checkATCommandResponseIsValid(response);
		
		// Return the response value.
		return response.getResponse();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return connectionInterface.toString();
	}
	
	/**
	 * Enables or disables the apply configuration changes option.
	 * 
	 * <p>Enabling this option means that when any parameter of the XBee device is set, it will 
	 * be also applied. If this option is disabled you will need to issue the {@code #applyChanges()} 
	 * method in order to apply the changes in all the parameters that were previously set.</p>
	 * 
	 * @see #isApplyConfigurationChangesEnabled()
	 */
	public void enableApplyConfigurationChanges(boolean enabled) {
		applyConfigurationChanges = enabled;
	}
	
	/**
	 * Retrieves whether or not the apply configuration changes option is enabled.
	 * 
	 * @return True if the option is enabled, false otherwise.
	 * 
	 * @see #enableApplyConfigurationChanges(boolean)
	 */
	public boolean isApplyConfigurationChangesEnabled() {
		return applyConfigurationChanges;
	}
	
	/**
	 * Configures the 16-bit address (network address) of the XBee device with the provided one.
	 * 
	 * @param xbee16BitAddress The new 16-bit address.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code xbee16BitAddress == null}.
	 * @throws TimeoutException if there is a timeout setting the address.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see XBee16BitAddress
	 * @see #get16BitAddress()
	 */
	protected void set16BitAddress(XBee16BitAddress xbee16BitAddress) throws TimeoutException, XBeeException {
		if (xbee16BitAddress == null)
			throw new NullPointerException("16-bit address canot be null.");
		
		setParameter("MY", xbee16BitAddress.getValue());
		
		this.xbee16BitAddress = xbee16BitAddress;
	}
	
	/**
	 * Retrieves the operating PAN ID of the XBee device.
	 * 
	 * @return The operating PAN ID of the XBee device.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout getting the PAN ID.
	 * @throws XBeeException if there is any other XBee related exception.
	 */
	public byte[] getPANID() throws TimeoutException, XBeeException {
		switch (getXBeeProtocol()) {
		case ZIGBEE:
			return getParameter("OP");
		default:
			return getParameter("ID");
		}
	}
	
	/**
	 * Sets the PAN ID of the XBee device.
	 * 
	 * @param panID The new PAN ID of the XBee device.
	 * 
	 * @throws IllegalArgumentException if {@code panID.length == 0} or 
	 *                                  if {@code panID.length > 8}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code panID == null}.
	 * @throws TimeoutException if there is a timeout setting the PAN ID.
	 * @throws XBeeException if there is any other XBee related exception.
	 */
	public void setPANID(byte[] panID) throws TimeoutException, XBeeException {
		if (panID == null)
			throw new NullPointerException("PAN ID cannot be null.");
		if (panID.length == 0)
			throw new IllegalArgumentException("Length of the PAN ID cannot be 0.");
		if (panID.length > 8)
			throw new IllegalArgumentException("Length of the PAN ID cannot be longer than 8 bytes.");
		
		setParameter("ID", panID);
	}
	
	/**
	 * Retrieves the output power level of the XBee device.
	 * 
	 * @return The output power level of the XBee device.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout getting the power level.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see PowerLevel
	 */
	public PowerLevel getPowerLevel() throws TimeoutException, XBeeException {
		byte[] powerLevelValue = getParameter("PL");
		return PowerLevel.get(ByteUtils.byteArrayToInt(powerLevelValue));
	}
	
	/**
	 * Sets the output power level of the XBee device.
	 * 
	 * @param powerLevel The new output power level to be set in the XBee device.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code powerLevel == null}.
	 * @throws TimeoutException if there is a timeout setting the power level.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see PowerLevel
	 */
	public void setPowerLevel(PowerLevel powerLevel) throws TimeoutException, XBeeException {
		if (powerLevel == null)
			throw new NullPointerException("Power level cannot be null.");
		
		setParameter("PL", ByteUtils.intToByteArray(powerLevel.getValue()));
	}
	
	/**
	 * Retrieves the current association indication status of the XBee device.
	 * 
	 * @return The association indication status of the XBee device.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout getting the association indication status.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see AssociationIndicationStatus
	 */
	protected AssociationIndicationStatus getAssociationIndicationStatus() throws TimeoutException, XBeeException {
		byte[] associationIndicationValue = getParameter("AI");
		return AssociationIndicationStatus.get(ByteUtils.byteArrayToInt(associationIndicationValue));
	}
	
	/**
	 * Forces the XBee device to disassociate from the network and reattempt to associate.
	 * 
	 * <p>Only valid for End Devices.</p>
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout executing the disassociation command.
	 * @throws XBeeException if there is any other XBee related exception.
	 */
	protected void forceDisassociate() throws TimeoutException, XBeeException {
		executeParameter("DA");
	}
	
	/**
	 * Writes parameter values to non-volatile memory of the XBee device so that parameter 
	 * modifications persist through subsequent resets.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout executing the write changes command.
	 * @throws XBeeException if there is any other XBee related exception.
	 */
	public void writeChanges() throws TimeoutException, XBeeException {
		executeParameter("WR");
	}
}

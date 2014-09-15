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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.connection.DataReader;
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.exceptions.ATCommandException;
import com.digi.xbee.api.exceptions.InterfaceAlreadyOpenException;
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
import com.digi.xbee.api.listeners.IPacketReceiveListener;
import com.digi.xbee.api.listeners.ISerialDataReceiveListener;
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.models.XBeeTransmitOptions;
import com.digi.xbee.api.models.XBeeTransmitStatus;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ATCommandPacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.packet.common.RemoteATCommandPacket;
import com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket;
import com.digi.xbee.api.packet.common.TransmitPacket;
import com.digi.xbee.api.packet.common.TransmitStatusPacket;
import com.digi.xbee.api.packet.raw.TXStatusPacket;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

public class XBeeDevice {
	
	// Constants.
	protected static int DEFAULT_RECEIVE_TIMETOUT = 2000; // 2.0 seconds of timeout to receive packet and command responses.
	protected static int TIMEOUT_BEFORE_COMMAND_MODE = 1200;
	protected static int TIMEOUT_ENTER_COMMAND_MODE = 1500;
	
	private static String COMMAND_MODE_CHAR = "+";
	private static String COMMAND_MODE_OK = "OK\r";
	
	// Variables.
	protected IConnectionInterface connectionInterface;
	
	protected DataReader dataReader = null;
	
	protected XBeeProtocol xbeeProtocol = XBeeProtocol.UNKNOWN;
	
	protected OperatingMode operatingMode = OperatingMode.UNKNOWN;
	
	protected XBee64BitAddress xbee64BitAddress;
	
	protected int currentFrameID = 0xFF;
	protected int receiveTimeout = DEFAULT_RECEIVE_TIMETOUT;
	
	protected Logger logger;
	
	private XBeeDevice localXBeeDevice;
	
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
	public XBeeDevice(String port, int baudRate) {
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
	public XBeeDevice(String port, int baudRate, int dataBits, int stopBits, int parity, int flowControl) {
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
	public XBeeDevice(String port, SerialPortParameters serialPortParameters) {
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
	public XBeeDevice(IConnectionInterface connectionInterface) {
		if (connectionInterface == null)
			throw new NullPointerException("ConnectionInterface cannot be null.");
		
		this.connectionInterface = connectionInterface;
		this.logger = LoggerFactory.getLogger(this.getClass());
		logger.debug(toString() + "Using the connection interface {}.", 
				connectionInterface.getClass().getSimpleName());
	}
	
	/**
	 * Class constructor. Instantiates a new remote {@code XBeeDevice} object 
	 * with the given local {@code XBeeDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local XBee device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote XBee device.
	 * @param xbee64BitAddress The 64-bit address to identify this remote XBee 
	 *                         device.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code xbee64BitAddress == null}.
	 * 
	 * @see XBee64BitAddress
	 */
	public XBeeDevice(XBeeDevice localXBeeDevice, XBee64BitAddress xbee64BitAddress) {
		if (localXBeeDevice == null)
			throw new NullPointerException("Local XBee device cannot be null.");
		if (xbee64BitAddress == null)
			throw new NullPointerException("XBee 64 bit address of the remote device cannot be null.");
		if (localXBeeDevice.isRemote())
			throw new IllegalArgumentException("The given local XBee device is remote.");
		
		this.localXBeeDevice = localXBeeDevice;
		this.connectionInterface = localXBeeDevice.getConnectionInterface();
		this.xbee64BitAddress = xbee64BitAddress;
		this.logger = LoggerFactory.getLogger(this.getClass());
		logger.debug(toString() + "Using the connection interface {}.", 
				connectionInterface.getClass().getSimpleName());
	}
	
	/**
	 * Opens the connection interface associated with this XBee device.
	 * 
	 * @throws XBeeException if there is any problem opening the device.
	 * @throws InterfaceAlreadyOpenException if the device is already open.
	 * 
	 * @see #isOpen()
	 * @see #close()
	 */
	public void open() throws XBeeException {
		logger.info(toString() + "Opening the connection interface...");
		
		// First, verify that the connection is not already open.
		if (connectionInterface.isOpen())
			throw new InterfaceAlreadyOpenException();
		
		// Connect the interface.
		connectionInterface.open();
		
		logger.info(toString() + "Connection interface open.");
		
		// Data reader initialization and determining operating mode should be only 
		// done for local XBee devices.
		if (isRemote())
			return;
		
		// Initialize the data reader.
		dataReader = new DataReader(connectionInterface, operatingMode);
		dataReader.start();
		
		// Determine the operating mode of the XBee device if it is unknown.
		if (operatingMode == OperatingMode.UNKNOWN)
			operatingMode = determineOperatingMode();
		
		// Check if the operating mode is a valid and supported one.
		if (operatingMode == OperatingMode.UNKNOWN) {
			close();
			throw new InvalidOperatingModeException("Could not determine operating mode.");
		} else if (operatingMode == OperatingMode.AT) {
			close();
			throw new InvalidOperatingModeException(operatingMode);
		}
	}
	
	/**
	 * Closes the connection interface associated with this XBee device.
	 * 
	 * @see #isOpen()
	 * @see #open()
	 */
	public void close() {
		// Stop XBee reader.
		if (dataReader != null && dataReader.isRunning())
			dataReader.stopReader();
		// Close interface.
		connectionInterface.close();
		logger.info(toString() + "Connection interface closed.");
	}
	
	/**
	 * Retrieves whether or not the connection interface associated to the 
	 * device is open.
	 * 
	 * @return {@code true} if the interface is open, {@code false} otherwise.
	 * 
	 * @see #open()
	 * @see #close()
	 */
	public boolean isOpen() {
		if (connectionInterface != null)
			return connectionInterface.isOpen();
		return false;
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
	public boolean isRemote() {
		return localXBeeDevice != null;
	}
	
	/**
	 * Determines the operating mode of the XBee device.
	 * 
	 * @return The operating mode of the XBee device.
	 * 
	 * @throws OperationNotSupportedException if the packet is being sent from 
	 *                                        a remote device.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * 
	 * @see OperatingMode
	 */
	protected OperatingMode determineOperatingMode() throws OperationNotSupportedException {
		try {
			// Check if device is in API or API Escaped operating modes.
			operatingMode = OperatingMode.API;
			dataReader.setXBeeReaderMode(operatingMode);
			
			ATCommandResponse response = sendATCommand(new ATCommand("AP"));
			if (response.getResponse() != null && response.getResponse().length > 0) {
				if (response.getResponse()[0] != OperatingMode.API.getID())
					operatingMode = OperatingMode.API_ESCAPE;
				logger.debug(toString() + "Using {}.", operatingMode.getName());
				return operatingMode;
			}
		} catch (TimeoutException e) {
			// Check if device is in AT operating mode.
			operatingMode = OperatingMode.AT;
			dataReader.setXBeeReaderMode(operatingMode);
			
			try {
				// It is necessary to wait at least 1 second to enter in command mode after 
				// sending any data to the device.
				Thread.sleep(TIMEOUT_BEFORE_COMMAND_MODE);
				// Try to enter in AT command mode, if so the module is in AT mode.
				boolean success = enterATCommandMode();
				if (success)
					return OperatingMode.AT;
			} catch (TimeoutException e1) {
				logger.error(e1.getMessage(), e1);
			} catch (InvalidOperatingModeException e1) {
				logger.error(e1.getMessage(), e1);
			} catch (InterruptedException e1) {
				logger.error(e1.getMessage(), e1);
			}
		} catch (InvalidOperatingModeException e) {
			logger.error("Invalid operating mode", e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return OperatingMode.UNKNOWN;
	}
	
	/**
	 * Attempts to put the device in AT Command mode. Only valid if device is 
	 * working in AT mode.
	 * 
	 * @return {@code true} if the device entered in AT command mode, 
	 *         {@code false} otherwise.
	 *         
	 * @throws InvalidOperatingModeException if the operating mode cannot be 
	 *                                       determined or is not supported.
	 * @throws TimeoutException if the configured time expires.
	 * @throws InterfaceNotOpenException if the device is not open.
	 */
	public boolean enterATCommandMode() throws InvalidOperatingModeException, TimeoutException {
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		if (operatingMode != OperatingMode.AT)
			throw new InvalidOperatingModeException("Invalid mode. Command mode can be only accessed while in AT mode.");
		
		// Enter in AT command mode (send '+++'). The process waits 1,5 seconds for the 'OK\n'.
		byte[] readData = new byte[256];
		try {
			// Send the command mode sequence.
			connectionInterface.writeData(COMMAND_MODE_CHAR.getBytes());
			connectionInterface.writeData(COMMAND_MODE_CHAR.getBytes());
			connectionInterface.writeData(COMMAND_MODE_CHAR.getBytes());
			
			// Wait some time to let the module generate a response.
			Thread.sleep(TIMEOUT_ENTER_COMMAND_MODE);
			
			// Read data from the device (it should answer with 'OK\r').
			int readBytes = connectionInterface.readData(readData);
			if (readBytes < COMMAND_MODE_OK.length())
				throw new TimeoutException();
			
			// Check if the read data is 'OK\r'.
			String readString = new String(readData, 0, readBytes);
			if (!readString.contains(COMMAND_MODE_OK))
				return false;
			
			// Read data was 'OK\r'.
			return true;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
		return false;
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
	public OperatingMode getOperatingMode() {
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
	 * Sets the XBee protocol of the XBee device.
	 * 
	 * @param xbeeProtocol The XBee protocol to set.
	 * 
	 * @see XBeeProtocol
	 * @see #getXBeeProtocol()
	 */
	protected void setXBeeProtocol(XBeeProtocol xbeeProtocol) {
		this.xbeeProtocol = xbeeProtocol;
	}
	
	/**
	 * Sends the given AT command and waits for answer or until the configured 
	 * receive timeout expires.
	 * 
	 * <p>The received timeout is configured using the {@code setReceiveTimeout}
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
	 * @throws OperationNotSupportedException if the packet is being sent from 
	 *                                        a remote device.
	 * @throws IOException if an I/O error occurs while sending the AT command.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code command == null}.
	 * 
	 * @see ATCommand
	 * @see ATCommandResponse
	 * @see #setReceiveTimeout(int)
	 * @see #getReceiveTimeout()
	 */
	public ATCommandResponse sendATCommand(ATCommand command) 
			throws InvalidOperatingModeException, TimeoutException, OperationNotSupportedException, IOException {
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
			// Create AT command packet
			XBeePacket packet;
			if (isRemote())
				packet = new RemoteATCommandPacket(getNextFrameID(), get64BitAddress(), XBee16BitAddress.UNKNOWN_ADDRESS, XBeeTransmitOptions.NONE, command.getCommand(), command.getParameter());
			else
				packet = new ATCommandPacket(getNextFrameID(), command.getCommand(), command.getParameter());
			if (command.getParameter() == null)
				logger.debug(toString() + "Sending AT command '{}'.", command.getCommand());
			else
				logger.debug(toString() + "Sending AT command '{} {}'.", command.getCommand(), HexUtils.prettyHexString(command.getParameter()));
			try {
				// Send the packet and build response.
				XBeePacket answerPacket = sendXBeePacket(packet, true);
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
	 * Sends the given XBee packet synchronously and blocks until the response 
	 * is received or the configured receive timeout expires.
	 * 
	 * <p>The received timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>Use {@link #sendXBeePacketAsync(XBeePacket)} for non-blocking 
	 * operations.</p>
	 * 
	 * @param packet XBee packet to be sent.
	 * 
	 * @return An {@code XBeePacket} object containing the response of the sent
	 *         packet or {@code null} if there is no response.
	 * 
	 * @throws XBeeException if there is any error sending the XBee packet.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code packet == null}.
	 * 
	 * @see XBeePacket
	 * @see #sendXBeePacket(XBeePacket, IPacketReceiveListener)
	 * @see #sendXBeePacketAsync(XBeePacket)
	 * @see #setReceiveTimeout(int)
	 * @see #getReceiveTimeout()
	 */
	public XBeePacket sendXBeePacket(XBeePacket packet) throws XBeeException {
		try {
			return sendXBeePacket(packet, !isRemote());
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
	}
	
	/**
	 * Sends the given XBee packet synchronously and blocks until response is 
	 * received or receive timeout is reached.
	 * 
	 * <p>The received timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>Use {@link #sendXBeePacketAsync(XBeePacket, boolean)} for non-blocking 
	 * operations.</p>
	 * 
	 * @param packet XBee packet to be sent.
	 * @param sentFromLocalDevice Indicates whether or not the packet was sent 
	 *                            from a local device.
	 * @return An {@code XBeePacket} containing the response of the sent packet 
	 *         or {@code null} if there is no response.
	 *         
	 * @throws InvalidOperatingModeException if the operating mode is different than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws TimeoutException if the configured time expires while waiting for the packet reply.
	 * @throws OperationNotSupportedException if the packet is being sent from a remote device.
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
	private XBeePacket sendXBeePacket(final XBeePacket packet, boolean sentFromLocalDevice) 
			throws InvalidOperatingModeException, TimeoutException, OperationNotSupportedException, IOException {
		// Check if the packet to send is null.
		if (packet == null)
			throw new NullPointerException("XBee packet cannot be null.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		// Check if the packet is being sent from a remote device.
		if (!sentFromLocalDevice)
			throw new OperationNotSupportedException("Remote devices cannot send data to other remote devices.");
		
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
					sendXBeePacketAsync(packet, sentFromLocalDevice);
					return null;
				}
			} else {
				sendXBeePacketAsync(packet, sentFromLocalDevice);
				return null;
			}
			
			// Add the required frame ID to the packet if necessary.
			insertFrameID(packet);
			
			// Generate a packet received listener for the packet to be sent.
			IPacketReceiveListener packetReceiveListener = createPacketReceivedListener(packet, responseList);
			
			// Add the packet listener to the data reader.
			dataReader.addPacketReceiveListener(packetReceiveListener);
			
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
				dataReader.removePacketReceiveListener(packetReceiveListener);
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
	 * Sends the given XBee packet asynchronously and registers the given packet
	 * listener (if not {@code null}) to wait for an answer.
	 * 
	 * @param packet XBee packet to be sent.
	 * @param packetReceiveListener Listener for the operation, {@code null} 
	 *                              not to be notified when the answer arrives.
	 * @param sentFromLocalDevice Indicates whether or not the packet was sent 
	 *                            from a local device.
	 *                              
	 * @throws InvalidOperatingModeException if the operating mode is different than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws OperationNotSupportedException if the packet is being sent from a remote device.
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
	private void sendXBeePacket(XBeePacket packet, IPacketReceiveListener packetReceiveListener, boolean sentFromLocalDevice)
			throws InvalidOperatingModeException, OperationNotSupportedException, IOException {
		// Check if the packet to send is null.
		if (packet == null)
			throw new NullPointerException("XBee packet cannot be null.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		// Check if the packet is being sent from a remote device.
		if (!sentFromLocalDevice)
			throw new OperationNotSupportedException("Remote devices cannot send data to other remote devices.");
		
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
	 * Sends the given XBee packet asynchronously.
	 * 
	 * <p>To be notified when the answer is received, use 
	 * {@link #sendXBeePacket(XBeePacket, IPacketReceiveListener)}.</p>
	 * 
	 * @param packet XBee packet to be sent asynchronously.
	 * @param sentFromLocalDevice Indicates whether or not the packet was sent 
	 *                            from a local device.
	 * 
	 * @throws InvalidOperatingModeException if the operating mode is different than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws OperationNotSupportedException if the packet is being sent from a remote device.
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
	private void sendXBeePacketAsync(XBeePacket packet, boolean sentFromLocalDevice) 
			throws InvalidOperatingModeException, OperationNotSupportedException, IOException {
		sendXBeePacket(packet, null, sentFromLocalDevice);
	}
	
	/**
	 * Sends the given XBee packet and registers the given packet listener 
	 * (if not {@code null}) to wait for an answer.
	 * 
	 * @param packet XBee packet to be sent.
	 * @param packetReceiveListener Listener for the operation, {@code null} 
	 *                              not to be notified when the answer arrives.
	 *                              
	 * @throws XBeeException if there is any error sending the XBee packet.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code packet == null}.
	 * 
	 * @see XBeePacket
	 * @see IPacketReceiveListener
	 * @see #sendXBeePacket(XBeePacket)
	 * @see #sendXBeePacketAsync(XBeePacket)
	 */
	public void sendXBeePacket(XBeePacket packet, IPacketReceiveListener packetReceiveListener)
			throws XBeeException {
		try {
			sendXBeePacket(packet, packetReceiveListener, !isRemote());
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
	}
	
	/**
	 * Sends the given XBee packet asynchronously.
	 * 
	 * <p>To be notified when the answer is received, use 
	 * {@link #sendXBeePacket(XBeePacket, IPacketReceiveListener)}.</p>
	 * 
	 * @param packet XBee packet to be sent asynchronously.
	 * 
	 * @throws XBeeException if there is any error sending the XBee packet.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code packet == null}.
	 * 
	 * @see XBeePacket
	 * @see #sendXBeePacket(XBeePacket)
	 * @see #sendXBeePacket(XBeePacket, IPacketReceiveListener)
	 */
	public void sendXBeePacketAsync(XBeePacket packet) 
			throws IOException, XBeeException {
		try {
			sendXBeePacket(packet, null, !isRemote());
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
	}
	
	/**
	 * Writes the given XBee packet in the connection interface.
	 * 
	 * @param packet XBee packet to be written.
	 * 
	 * @throws IOException if an I/O error occurs while writing the XBee packet 
	 *                     in the connection interface.
	 */
	protected void writePacket(XBeePacket packet) throws IOException {
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
	public int getNextFrameID() {
		if (currentFrameID == 0xff) {
			// Reset counter.
			currentFrameID = 1;
		} else
			currentFrameID ++;
		return currentFrameID;
	}
	
	/**
	 * Retrieves the configured timeout for receiving packets in synchronous 
	 * operations.
	 * 
	 * @return The current receive timeout in milliseconds.
	 * 
	 * @see #setReceiveTimeout(int)
	 */
	public int getReceiveTimeout() {
		return receiveTimeout;
	}
	
	/**
	 * Configures the timeout in milliseconds for receiving packets in 
	 * synchronous operations.
	 *  
	 * @param receiveTimeout The new receive timeout in milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code receiveTimeout < 0}.
	 * 
	 * @see #getReceiveTimeout()
	 */
	public void setReceiveTimeout(int receiveTimeout) {
		if (receiveTimeout < 0)
			throw new IllegalArgumentException("Receive timeout cannot be less than 0.");
		
		this.receiveTimeout = receiveTimeout;
	}
	
	/**
	 * Starts listening for packets in the provided packets listener.
	 * 
	 * <p>The provided listener is added to the list of listeners to be notified
	 * when new packets are received. If the listener has been already 
	 * included, this method does nothing.</p>
	 * 
	 * @param listener Listener to be notified when new packets are received.
	 * 
	 * @see IPacketReceiveListener
	 * @see #stopListeningForPackets(IPacketReceiveListener)
	 */
	public void startListeningForPackets(IPacketReceiveListener listener) {
		if (dataReader == null)
			return;
		dataReader.addPacketReceiveListener(listener);
	}
	
	/**
	 * Stops listening for packets in the provided packets listener. 
	 * 
	 * <p>The provided listener is removed from the list of packets listeners. 
	 * If the listener was not in the list this method does nothing.</p>
	 * 
	 * @param listener Listener to be removed from the list of listeners.
	 * 
	 * @see IPacketReceiveListener
	 * @see #startListeningForPackets(IPacketReceiveListener)
	 */
	public void stopListeningForPackets(IPacketReceiveListener listener) {
		if (dataReader == null)
			return;
		dataReader.removePacketReceiveListener(listener);
	}
	
	/**
	 * Starts listening for serial data in the provided serial data listener.
	 *  
	 * <p>The provided listener is added to the list of listeners to be notified
	 * when new serial data is received. If the listener has been already 
	 * included this method does nothing.</p>
	 * 
	 * @param listener Listener to be notified when new serial data is received.
	 * 
	 * @see ISerialDataReceiveListener
	 * @see #stopListeningForSerialData(ISerialDataReceiveListener)
	 */
	public void startListeningForSerialData(ISerialDataReceiveListener listener) {
		if (dataReader == null)
			return;
		dataReader.addSerialDatatReceiveListener(listener);
	}
	
	/**
	 * Stops listening for serial data in the provided serial data listener.
	 * 
	 * <p>The provided listener is removed from the list of serial data 
	 * listeners. If the listener was not in the list this method does nothing.</p>
	 * 
	 * @param listener Listener to be removed from the list of listeners.
	 * 
	 * @see ISerialDataReceiveListener
	 * @see #startListeningForSerialData(ISerialDataReceiveListener)
	 */
	public void stopListeningForSerialData(ISerialDataReceiveListener listener) {
		if (dataReader == null)
			return;
		dataReader.removeSerialDataReceiveListener(listener);
	}
	
	/**
	 * Sends the provided data to the XBee device of the network corresponding 
	 * to the given 64-bit address asynchronously.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote 
	 * device or for transmit status packet.</p>
	 * 
	 * @param address The 64-bit address of the XBee that will receive the data.
	 * @param data Byte array containing data to be sent.
	 * 
	 * @throws XBeeException if there is any XBee related exception.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code address == null} or 
	 *                              if {@code data == null}.
	 * 
	 * @see XBee64BitAddress
	 * @see #sendSerialDataAsync(XBee16BitAddress, byte[])
	 * @see #sendSerialDataAsync(XBeeDevice, byte[])
	 * @see #sendSerialData(XBee16BitAddress, byte[])
	 * @see #sendSerialData(XBee64BitAddress, byte[])
	 * @see #sendSerialData(XBeeDevice, byte[])
	 */
	public void sendSerialDataAsync(XBee64BitAddress address, byte[] data) throws XBeeException {
		// Verify the parameters are not null, if they are null, throw an exception.
		if (address == null)
			throw new NullPointerException("Address cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send data to a remote device from a remote device.");
		
		logger.info(toString() + "Sending serial data asynchronously to {} >> {}.", address, HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket = new TransmitPacket(getNextFrameID(), address, XBee16BitAddress.UNKNOWN_ADDRESS, 0, XBeeTransmitOptions.NONE, data);
		sendAndCheckXBeePacket(xbeePacket, true);
	}
	
	/**
	 * Sends the provided data to the XBee device of the network corresponding 
	 * to the given 64-Bit/16-Bit address asynchronously.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote 
	 * device or for transmit status packet.</p>
	 * 
	 * @param address64Bit The 64-bit address of the XBee that will receive the 
	 *                     data.
	 * @param address16bit The 16-bit address of the XBee that will receive the 
	 *                     data. If it is unknown the 
	 *                     {@code XBee16BitAddress.UNKNOWN_ADDRESS} must be used.
	 * @param data Byte array containing data to be sent.
	 * 
	 * @throws XBeeException if a remote device is trying to send serial data or 
	 *                       if there is any other XBee related exception.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code address64Bit == null} or 
	 *                              if {@code address16bit == null} or
	 *                              if {@code data == null}.
	 * 
	 * @see XBee64BitAddress
	 * @see XBee16BitAddress
	 * @see #getReceiveTimeout()
	 * @see #setReceiveTimeout(int)
	 * @see #sendSerialData(XBee64BitAddress, byte[])
	 * @see #sendSerialData(XBee16BitAddress, byte[])
	 * @see #sendSerialData(XBee64BitAddress, XBee16BitAddress, byte[])
	 * @see #sendSerialData(XBeeDevice, byte[])
	 * @see #sendSerialDataAsync(XBee64BitAddress, byte[])
	 * @see #sendSerialDataAsync(XBee16BitAddress, byte[])
	 * @see #sendSerialDataAsync(XBeeDevice, byte[])
	 */
	protected void sendSerialDataAsync(XBee64BitAddress address64Bit, XBee16BitAddress address16bit, byte[] data) throws XBeeException {
		// Verify the parameters are not null, if they are null, throw an exception.
		if (address64Bit == null)
			throw new NullPointerException("64-bit address cannot be null");
		if (address16bit == null)
			throw new NullPointerException("16-bit address cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send data to a remote device from a remote device.");
		
		logger.info(toString() + "Sending serial data asynchronously to {}[{}] >> {}.", 
				address64Bit, address16bit, HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket = new TransmitPacket(getNextFrameID(), address64Bit, address16bit, 0, XBeeTransmitOptions.NONE, data);
		sendAndCheckXBeePacket(xbeePacket, true);
	}
	
	/**
	 * Sends the provided data to the provided XBee device asynchronously.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote 
	 * device or for transmit status packet.</p>
	 * 
	 * @param xbeeDevice The XBee device of the network that will receive the data.
	 * @param data Byte array containing data to be sent.
	 * 
	 * @throws XBeeException if there is any XBee related exception.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code xbeeDevice == null} or 
	 *                              if {@code data == null}.
	 *                              
	 * @see #sendSerialDataAsync(XBee64BitAddress, byte[])
	 * @see #sendSerialDataAsync(XBee16BitAddress, byte[])
	 * @see #sendSerialData(XBee64BitAddress, byte[])
	 * @see #sendSerialData(XBee16BitAddress, byte[])
	 * @see #sendSerialData(XBeeDevice, byte[])
	 */
	public void sendSerialDataAsync(XBeeDevice xbeeDevice, byte[] data) throws XBeeException {
		if (xbeeDevice == null)
			throw new NullPointerException("XBee device cannot be null");
		sendSerialDataAsync(xbeeDevice.get64BitAddress(), data);
	}
	
	/**
	 * Sends the provided data to the XBee device of the network corresponding 
	 * to the given 64-bit address.
	 * 
	 * <p>This method blocks till a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The received timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations use the method 
	 * {@link #sendSerialData(XBee64BitAddress, byte[])}.</p>
	 * 
	 * @param address The 64-bit address of the XBee that will receive the data.
	 * @param data Byte array containing data to be sent.
	 * 
	 * @throws TimeoutException if there is a timeout sending the serial data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code address == null} or 
	 *                              if {@code data == null}.
	 * 
	 * @see XBee64BitAddress
	 * @see #getReceiveTimeout()
	 * @see #setReceiveTimeout(int)
	 * @see #sendSerialData(XBee16BitAddress, byte[])
	 * @see #sendSerialData(XBeeDevice, byte[])
	 * @see #sendSerialDataAsync(XBee64BitAddress, byte[])
	 * @see #sendSerialDataAsync(XBee16BitAddress, byte[])
	 * @see #sendSerialDataAsync(XBeeDevice, byte[])
	 */
	public void sendSerialData(XBee64BitAddress address, byte[] data) throws TimeoutException, XBeeException {
		// Verify the parameters are not null, if they are null, throw an exception.
		if (address == null)
			throw new NullPointerException("Address cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send data to a remote device from a remote device.");
		
		logger.info(toString() + "Sending serial data to {} >> {}.", address, HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket = new TransmitPacket(getNextFrameID(), address, XBee16BitAddress.UNKNOWN_ADDRESS, 0, XBeeTransmitOptions.NONE, data);
		sendAndCheckXBeePacket(xbeePacket, false);
	}
	
	/**
	 * Sends the provided data to the XBee device of the network corresponding 
	 * to the given 64-Bit/16-Bit address.
	 * 
	 * <p>This method blocks till a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The received timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations use the method 
	 * {@link #sendSerialData(XBee16BitAddress, byte[])}.</p>
	 * 
	 * @param address64Bit The 64-bit address of the XBee that will receive the 
	 *                     data.
	 * @param address16bit The 16-bit address of the XBee that will receive the 
	 *                     data. If it is unknown the 
	 *                     {@code XBee16BitAddress.UNKNOWN_ADDRESS} must be used.
	 * @param data Byte array containing data to be sent.
	 * 
	 * @throws TimeoutException if there is a timeout sending the serial data.
	 * @throws XBeeException if a remote device is trying to send serial data or 
	 *                       if there is any other XBee related exception.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code address64Bit == null} or 
	 *                              if {@code address16bit == null} or
	 *                              if {@code data == null}.
	 * 
	 * @see XBee64BitAddress
	 * @see XBee16BitAddress
	 * @see #getReceiveTimeout()
	 * @see #setReceiveTimeout(int)
	 * @see #sendSerialData(XBee64BitAddress, byte[])
	 * @see #sendSerialData(XBee16BitAddress, byte[])
	 * @see #sendSerialData(XBeeDevice, byte[])
	 * @see #sendSerialDataAsync(XBee64BitAddress, byte[])
	 * @see #sendSerialDataAsync(XBee16BitAddress, byte[])
	 * @see #sendSerialDataAsync(XBee64BitAddress, XBee16BitAddress, byte[])
	 * @see #sendSerialDataAsync(XBeeDevice, byte[])
	 */
	protected void sendSerialData(XBee64BitAddress address64Bit, XBee16BitAddress address16bit, byte[] data) throws TimeoutException, XBeeException {
		// Verify the parameters are not null, if they are null, throw an exception.
		if (address64Bit == null)
			throw new NullPointerException("64-bit address cannot be null");
		if (address16bit == null)
			throw new NullPointerException("16-bit address cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		// Check if device is remote.
		if (isRemote())
			throw new OperationNotSupportedException("Cannot send data to a remote device from a remote device.");
		
		logger.info(toString() + "Sending serial data to {}[{}] >> {}.", 
				address64Bit, address16bit, HexUtils.prettyHexString(data));
		
		XBeePacket xbeePacket = new TransmitPacket(getNextFrameID(), address64Bit, address16bit, 0, XBeeTransmitOptions.NONE, data);
		sendAndCheckXBeePacket(xbeePacket, false);
	}
	
	/**
	 * Sends the provided data to the given XBee device.
	 * 
	 * <p>This method blocks till a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The received timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations use the method 
	 * {@link #sendSerialDataAsync(XBeeDevice, byte[])}.</p>
	 * 
	 * @param xbeeDevice The XBee device of the network that will receive the data.
	 * @param data Byte array containing data to be sent.
	 * 
	 * @throws TimeoutException if there is a timeout sending the serial data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code xbeeDevice == null} or 
	 *                              if {@code data == null}.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #setReceiveTimeout(int)
	 * @see #sendSerialData(XBee64BitAddress, byte[])
	 * @see #sendSerialData(XBee16BitAddress, byte[])
	 * @see #sendSerialDataAsync(XBee64BitAddress, byte[])
	 * @see #sendSerialDataAsync(XBee16BitAddress, byte[])
	 * @see #sendSerialDataAsync(XBeeDevice, byte[])
	 */
	public void sendSerialData(XBeeDevice xbeeDevice, byte[] data) throws TimeoutException, XBeeException {
		if (xbeeDevice == null)
			throw new NullPointerException("XBee device cannot be null");
		sendSerialData(xbeeDevice.get64BitAddress(), data);
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
				sendXBeePacketAsync(packet, true);
			else
				receivedPacket = sendXBeePacket(packet, true);
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
		
		// Create and send the AT Command.
		String atCommand = ioLine.getConfigurationATCommand();
		ATCommandResponse response = null;
		try {
			response = sendATCommand(new ATCommand(atCommand, new byte[]{(byte)ioMode.getID()}));
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
		
		// Check if AT Command response is valid.
		checkATCommandResponseIsValid(response);
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
		
		// Create and send the AT Command.
		ATCommandResponse response = null;
		try {
			response = sendATCommand(new ATCommand(ioLine.getConfigurationATCommand()));
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
		
		// Check if AT Command response is valid.
		checkATCommandResponseIsValid(response);
		
		// Check if the response contains the configuration value.
		if (response.getResponse() == null || response.getResponse().length == 0)
			throw new OperationNotSupportedException("Answer does not conain the configuration value.");
		
		// Check if the received configuration mode is valid.
		int ioModeValue = response.getResponse()[0];
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
		
		// Create and send the AT Command.
		String atCommand = ioLine.getConfigurationATCommand();
		byte[] valueByte = new byte[]{(byte)ioValue.getID()};
		ATCommandResponse response = null;
		try {
			response = sendATCommand(new ATCommand(atCommand, valueByte));
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
		
		// Check if AT Command response is valid.
		checkATCommandResponseIsValid(response);
	}
	
	/**
	 * Retrieves the digital value of the provided IO line (must be configured 
	 * as digital I/O).
	 * 
	 * @param ioLine The IO line to get its digital value.
	 * 
	 * @return The digital value corresponding to the provided IO line.
	 * 
	 * @throws TimeoutException if there is a timeout sending the get IO values 
	 *                          command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code ioLine == null}.
	 * 
	 * @see IOLine
	 * @see IOMode#DIGITAL_IN
	 * @see IOMode#DIGITAL_OUT_HIGH
	 * @see IOMode#DIGITAL_OUT_LOW
	 * @see #getIOConfiguration(IOLine)
	 * @see #setIOConfiguration(IOLine, IOMode)
	 */
	public IOValue getDIOValue(IOLine ioLine) throws TimeoutException, XBeeException {
		// Obtain an IO Sample from the XBee device.
		IOSample ioSample = getIOSample(ioLine);
		
		// Check if the IO sample contains the expected IO line and value.
		if (!ioSample.hasDigitalValues() || !ioSample.getDigitalValues().containsKey(ioLine))
			throw new OperationNotSupportedException("Answer does not conain digital data for " + ioLine.getName() + ".");
		
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
		
		// Create and send the AT Command.
		String atCommand = ioLine.getPWMDutyCycleATCommand();
		ATCommandResponse response = null;
		try {
			response = sendATCommand(new ATCommand(atCommand, ByteUtils.intToByteArray(finaldutyCycle)));
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
		
		// Check if AT Command response is valid.
		checkATCommandResponseIsValid(response);
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
		
		// Create and send the AT Command.
		ATCommandResponse response = null;
		try {
			response = sendATCommand(new ATCommand(ioLine.getPWMDutyCycleATCommand()));
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
		
		// Check if AT Command response is valid.
		checkATCommandResponseIsValid(response);
		
		// Check if the response contains the PWM value.
		if (response.getResponse() == null || response.getResponse().length == 0)
			throw new OperationNotSupportedException("Answer does not conain PWM duty cycle value.");
		
		// Return the PWM duty cycle value.
		int readValue = ByteUtils.byteArrayToInt(response.getResponse());
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
		IOSample ioSample = getIOSample(ioLine);
		
		// Check if the IO sample contains the expected IO line and value.
		if (!ioSample.hasAnalogValues() || !ioSample.getAnalogValues().containsKey(ioLine))
			throw new OperationNotSupportedException("Answer does not conain analog data for " + ioLine.getName() + ".");
		
		// Return the analog value.
		return ioSample.getAnalogValues().get(ioLine);
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
	 * Retrieves an IO sample from the XBee device containing the value of the 
	 * provided IO line.
	 * 
	 * @param ioLine The IO line to obtain its associated IO sample.
	 * @return An IO sample containing the value of the provided IO line.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code ioLine == null}.
	 * @throws TimeoutException if there is a timeout getting the IO sample.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see IOSample
	 * @see IOLine
	 */
	protected IOSample getIOSample(IOLine ioLine) throws TimeoutException, XBeeException {
		if (ioLine == null)
			throw new NullPointerException("IO line cannot be null.");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		// Create and send the AT Command.
		ATCommandResponse response = null;
		try {
			response = sendATCommand(new ATCommand(ioLine.getReadIOATCommand()));
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
		
		// Check if AT Command response is valid.
		checkATCommandResponseIsValid(response);
		
		// Try to build an IO Sample from the sample payload.
		IOSample ioSample;
		try {
			ioSample = new IOSample(response.getResponse());
		} catch (IllegalArgumentException e) {
			throw new XBeeException("Couldn't create the IO sample.", e);
		} catch (NullPointerException e) {
			throw new XBeeException("Couldn't create the IO sample.", e);
		}
		return ioSample;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return connectionInterface.toString();
	}
}

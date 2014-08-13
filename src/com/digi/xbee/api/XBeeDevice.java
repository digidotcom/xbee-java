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
import com.digi.xbee.api.packet.common.TransmitPacket;
import com.digi.xbee.api.packet.common.TransmitStatusPacket;
import com.digi.xbee.api.packet.raw.TX16Packet;
import com.digi.xbee.api.packet.raw.TX64Packet;
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
	
	private Logger logger;
	
	/**
	 * Class constructor. Instantiates a new XBeeDevice object in the given port name and baud rate.
	 * 
	 * @param port Serial port name where XBee device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device. Other 
	 * 					connection parameters will be set as default (8 data bits, 
	 * 					1 stop bit, no parity, no flow control).
	 * 
	 * @throws NullPointerException if {@code port == null}.
	 * @throws IllegalArgumentException if {@code baudRate < 0}.
	 */
	public XBeeDevice(String port, int baudRate) {
		this(XBee.createConnectiontionInterface(port, baudRate));
	}
	
	/**
	 * Class constructor. Instantiates a new XBeeDevice object in the given serial port name and 
	 * settings.
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
	 * Class constructor. Instantiates a new XBeeDevice object in the given serial port name and 
	 * parameters.
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
	 * Class constructor. Instantiates a new XBeeDevice object with the given connection 
	 * interface.
	 * 
	 * @param connectionInterface The connection interface with the physical XBee device.
	 * 
	 * @throws NullPointerException if {@code connectionInterface == null}
	 * 
	 * @see IConnectionInterface
	 */
	public XBeeDevice(IConnectionInterface connectionInterface) {
		if (connectionInterface == null)
			throw new NullPointerException("ConnectionInterface cannot be null.");
		
		this.connectionInterface = connectionInterface;
		this.logger = LoggerFactory.getLogger(XBeeDevice.class);
		logger.debug(toString() + "Using the connection interface {}.", 
				connectionInterface.getClass().getSimpleName(),  connectionInterface.toString());
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
	 * Opens the connection interface associated with this XBee device.
	 * 
	 * @throws InterfaceAlreadyOpenException if the device is already open.
	 * @throws XBeeException if there is any problem opening the device.
	 */
	public void open() throws XBeeException {
		logger.info(toString() + "Opening the connection interface...");
		
		// First, verify that the connection is not already open.
		if (connectionInterface.isOpen())
			throw new InterfaceAlreadyOpenException();
		
		// Connect the interface.
		connectionInterface.open();
		
		logger.info(toString() + "Connection interface open.");
		
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
	 * Retrieves whether or not the connection interface associated to the device is 
	 * open.
	 * 
	 * @return True if the interface is open, false otherwise.
	 */
	public boolean isOpen() {
		if (connectionInterface != null)
			return connectionInterface.isOpen();
		return false;
	}
	
	/**
	 * Determines the operating mode of the XBee device.
	 * 
	 * @return The operating mode of the XBee device.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * 
	 * @see OperatingMode
	 */
	protected OperatingMode determineOperatingMode() {
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
	 * Attempts to put the device in AT Command mode. Only valid if device is working in AT mode.
	 * 
	 * @return {@code true} if the device entered in AT command mode, {@code false} otherwise.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws InvalidOperatingModeException if the operating mode cannot be determined or is not supported.
	 * @throws TimeoutException if the configured time expires.
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
	 * Retrieves the 64-Bit address of the XBee device.
	 * 
	 * @return The 64-Bit address of the XBee device.
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
	 */
	protected void setXBeeProtocol(XBeeProtocol xbeeProtocol) {
		this.xbeeProtocol = xbeeProtocol;
	}
	
	/**
	 * Sends the given AT command and waits for answer or until receive timeout 
	 * is reached.
	 * 
	 * @param command AT command to be sent.
	 * @return An {@code ATCommandResponse} object containing the response of the command or {@code null}
	 *         if there is no response.
	 * @throws NullPointerException if {@code command == null}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws InvalidOperatingModeException if the operating mode is different than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws TimeoutException if the configured time expires while waiting for the command reply.
	 * @throws IOException if an I/O error occurs while sending the AT command.
	 * 
	 * @see #setReceiveTimeout(int)
	 * @see #getReceiveTimeout()
	 * @see ATCommand
	 * @see ATCommandResponse
	 */
	public ATCommandResponse sendATCommand(ATCommand command) 
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
			// Create AT command packet
			XBeeAPIPacket packet = new ATCommandPacket(getNextFrameID(), command.getCommand(), command.getParameter());
			if (command.getParameter() == null)
				logger.debug(toString() + "Sending AT command '{}'.", command.getCommand());
			else
				logger.debug(toString() + "Sending AT command '{} {}'.", command.getCommand(), HexUtils.prettyHexString(command.getParameter()));
			try {
				// Send the packet and build response.
				ATCommandResponsePacket answerPacket = (ATCommandResponsePacket)sendXBeePacket(packet);
				response = new ATCommandResponse(command, answerPacket.getCommandData(), answerPacket.getStatus());
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
	 * Sends the given XBee packet synchronously and waits until response is 
	 * received or receive timeout is reached.
	 * 
	 * @param packet XBee packet to be sent.
	 * @return XBeePacket object containing the response of the sent packet or {@code null}
	 *         if there is no response.
	 * @throws NullPointerException if {@code packet == null}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws InvalidOperatingModeException if the operating mode is different than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws TimeoutException if the configured time expires while waiting for the packet reply.
	 * @throws IOException if an I/O error occurs while sending the XBee packet.
	 * 
	 * @see #setReceiveTimeout(int)
	 * @see #getReceiveTimeout()
	 * @see XBeePacket
	 */
	public XBeePacket sendXBeePacket(final XBeePacket packet) 
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
	 * Insert (if possible) the next frame ID stored in the device to the provided packet.
	 * 
	 * @param xbeePacket The packet to add the frame ID.
	 */
	private void insertFrameID(XBeePacket xbeePacket) {
		if (xbeePacket instanceof XBeeAPIPacket)
			return;
		
		if (((XBeeAPIPacket)xbeePacket).needsAPIFrameID() && ((XBeeAPIPacket)xbeePacket).getFrameID() == XBeeAPIPacket.NO_FRAME_ID)
			((XBeeAPIPacket)xbeePacket).setFrameID(getNextFrameID());
	}
	
	/**
	 * Retrieves the packet listener corresponding to the provided sent packet. The listener will filter those packets 
	 * matching with the Frame ID of the sent packet storing them in the provided responseList array.
	 * 
	 * @param sentPacket The packet sent.
	 * @param responseList List of packets received that correspond to the frame ID of the packet sent.
	 * @return A packet receive listener that will filter the packets received corresponding to the sent one.
	 */
	private IPacketReceiveListener createPacketReceivedListener(final XBeePacket sentPacket, final ArrayList<XBeePacket> responseList) {
		IPacketReceiveListener packetReceiveListener = new IPacketReceiveListener() {
			/*
			 * (non-Javadoc)
			 * @see com.digi.xbee.listeners.XBeePacketListener#packetReceived(byte[])
			 */
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
	 * @return True if the sent packet is the same than the received one, false otherwise.
	 */
	private boolean isSamePacket(XBeePacket sentPacket, XBeePacket receivedPacket) {
		if (HexUtils.byteArrayToHexString(sentPacket.generateByteArray()).equals(HexUtils.byteArrayToHexString(receivedPacket.generateByteArray())))
			return true;
		return false;
	}
	
	/**
	 * Sends the given XBee packet asynchronously and registers the given packet
	 * listener (if not null) to wait for an answer.
	 * 
	 * @param packet XBee packet to be sent.
	 * @param packetReceiveListener Listener for the operation, {@code null} to not be notifie
	 *                              when the answer arrives.
	 * @throws NullPointerException if {@code packet == null}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws InvalidOperatingModeException if the operating mode is different than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws IOException if an I/O error occurs while sending the XBee packet.
	 */
	public void sendXBeePacket(XBeePacket packet, IPacketReceiveListener packetReceiveListener)
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
	 * Sends the given XBee packet asynchronously.
	 * 
	 * <p>To be notified when the answer is received, use {@link #sendXBeePacket(XBeePacket, IPacketReceiveListener)}.</p>
	 * 
	 * @param packet XBee packet to be sent asynchronously.
	 * @throws NullPointerException if {@code packet == null}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws InvalidOperatingModeException if the operating mode is different than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws IOException if an I/O error occurs while sending the XBee packet.
	 */
	public void sendXBeePacketAsync(XBeePacket packet) 
			throws InvalidOperatingModeException, IOException {
		sendXBeePacket(packet, null);
	}
	
	/**
	 * Writes the given XBee packet in the connection interface.
	 * 
	 * @param packet XBee packet to be written.
	 * @throws IOException if an I/O error occurs while writing the XBee packet in the connection interface.
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
	 * @return The current receive timeout.
	 */
	public int getReceiveTimeout() {
		return receiveTimeout;
	}
	
	/**
	 * Configures the timeout for receiving packets in synchronous operations.
	 *  
	 * @param receiveTimeout The new receive timeout.
	 * 
	 * @throws IllegalArgumentException if {@code receiveTimeout < 0}.
	 */
	public void setReceiveTimeout(int receiveTimeout) {
		if (receiveTimeout < 0)
			throw new IllegalArgumentException("Receive timeout cannot be less than 0.");
		
		this.receiveTimeout = receiveTimeout;
	}
	
	/**
	 * Starts listening for packets in the provided packets listener. Adds 
	 * the given listener to the list of listeners to be notified when new 
	 * packets are received.
	 * 
	 * @param listener Listener to be notified when new packets are received.
	 */
	public void startListeningForPackets(IPacketReceiveListener listener) {
		if (dataReader == null)
			return;
		dataReader.addPacketReceiveListener(listener);
	}
	
	/**
	 * Stops listening for packets in the provided packets listener. Removes 
	 * the given listener from the list of packets listeners.
	 * 
	 * @param listener Listener to be removed from the list of listeners.
	 */
	public void stopListeningForPackets(IPacketReceiveListener listener) {
		if (dataReader == null)
			return;
		dataReader.removePacketReceiveListener(listener);
	}
	
	/**
	 * Starts listening for serial data in the provided serial data listener. Adds 
	 * the given listener to the list of listeners to be notified when new serial 
	 * data is received.
	 * 
	 * @param listener Listener to be notified when new serial data is received.
	 */
	public void startListeningForSerialData(ISerialDataReceiveListener listener) {
		if (dataReader == null)
			return;
		dataReader.addSerialDatatReceiveListener(listener);
	}
	
	/**
	 * Stops listening for serial data in the provided serial data listener. Removes 
	 * the given listener from the list of serial data listeners.
	 * 
	 * @param listener Listener to be removed from the list of listeners.
	 */
	public void stopListeningForSerialData(ISerialDataReceiveListener listener) {
		if (dataReader == null)
			return;
		dataReader.removeSerialDataReceiveListener(listener);
	}
	
	/**
	 * Sends the provided data to the XBee device of the network corresponding to the 
	 * provided 64-Bit address asynchronously.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote device or for 
	 * transmit status packet</p>
	 * 
	 * @param address The 64-Bit address of the XBee that will receive the data.
	 * @param data Byte array containing data to be sent.
	 * @throws NullPointerException if {@code address == null} or {@code data == null}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws XBeeException if there is any XBee related exception.
	 * 
	 * @see XBee64BitAddress
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
		
		XBeePacket xbeePacket;
		
		logger.info(toString() + "Sending serial data asynchronously to {} >> {}.", address, HexUtils.prettyHexString(data));
		
		// Depending on the protocol of the XBee device, the packet to send may vary.
		switch (getXBeeProtocol()) {
		case RAW_802_15_4:
			// Generate and send the Tx64 packet.
			xbeePacket = new TX64Packet(getNextFrameID(), address, XBeeTransmitOptions.NONE, data);
			break;
		default:
			// Generate and send the Transmit packet.
			xbeePacket = new TransmitPacket(getNextFrameID(), address, XBee16BitAddress.UNKNOWN_ADDRESS, 0, XBeeTransmitOptions.NONE, data);
		}
		sendAndCheckXBeePacket(xbeePacket, true);
	}
	
	/**
	 * Sends the provided data to the XBee device of the network corresponding to the 
	 * provided 16-Bit address asynchronously.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote device or for 
	 * transmit status packet</p>
	 * 
	 * @param address The 16-Bit address of the XBee that will receive the data.
	 * @param data Byte array containing data to be sent.
	 * @throws NullPointerException if {@code address == null} or {@code data == null}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws XBeeException if there is any XBee related exception.
	 * 
	 * @see XBee16BitAddress
	 */
	public void sendSerialDataAsync(XBee16BitAddress address, byte[] data) throws XBeeException {
		// Verify the parameters are not null, if they are null, throw an exception.
		if (address == null)
			throw new NullPointerException("Address cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		XBeePacket xbeePacket;
		
		logger.info(toString() + "Sending serial data asynchronously to {} >> {}.", address, HexUtils.prettyHexString(data));
		
		// Depending on the protocol of the XBee device, the packet to send may vary.
		switch (getXBeeProtocol()) {
		case RAW_802_15_4:
			// Generate and send the Tx16 packet.
			xbeePacket = new TX16Packet(getNextFrameID(), address, XBeeTransmitOptions.NONE, data);
			break;
		default:
			// Generate and send the Transmit packet.
			xbeePacket = new TransmitPacket(getNextFrameID(), XBee64BitAddress.UNKNOWN_ADDRESS, address, 0, XBeeTransmitOptions.NONE, data);
		}
		sendAndCheckXBeePacket(xbeePacket, true);
	}
	
	/**
	 * Sends the provided data to the provided XBee device asynchronously.
	 * 
	 * <p>Asynchronous transmissions do not wait for answer from the remote device or for transmit status packet</p>
	 * 
	 * @param xbeeDevice The XBee device of the network that will receive the data.
	 * @param data Byte array containing data to be sent.
	 * @throws NullPointerException if {@code xbeeDevice == null} or {@code data == null}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws XBeeException if there is any XBee related exception.
	 * 
	 * @see XBeeDevice
	 */
	public void sendSerialDataAsync(XBeeDevice xbeeDevice, byte[] data) throws XBeeException {
		if (xbeeDevice == null)
			throw new NullPointerException("XBee device cannot be null");
		sendSerialDataAsync(xbeeDevice.get64BitAddress(), data);
	}
	
	/**
	 * Sends the provided data to the XBee device of the network corresponding to the 
	 * provided 64-Bit address.
	 * 
	 * @param address The 64-Bit address of the XBee that will receive the data.
	 * @param data Byte array containing data to be sent.
	 * @throws NullPointerException if {@address == null} or {@data == null}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout sending the serial data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see XBee64BitAddress
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
		
		XBeePacket xbeePacket;
		
		logger.info(toString() + "Sending serial data to {} >> {}.", address, HexUtils.prettyHexString(data));
		
		// Depending on the protocol of the XBee device, the packet to send may vary.
		switch (getXBeeProtocol()) {
		case RAW_802_15_4:
			// Generate and send the Tx64 packet.
			xbeePacket = new TX64Packet(getNextFrameID(), address, XBeeTransmitOptions.NONE, data);
			break;
		default:
			// Generate and send the Transmit packet.
			xbeePacket = new TransmitPacket(getNextFrameID(), address, XBee16BitAddress.UNKNOWN_ADDRESS, 0, XBeeTransmitOptions.NONE, data);
		}
		sendAndCheckXBeePacket(xbeePacket, false);
	}
	
	/**
	 * Sends the provided data to the XBee device of the network corresponding to the 
	 * provided 16-Bit address.
	 * 
	 * @param address The 16-Bit address of the XBee that will receive the data.
	 * @param data Byte array containing data to be sent.
	 * @throws NullPointerException if {@address == null} or {@data == null}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout sending the serial data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see XBee16BitAddress
	 */
	public void sendSerialData(XBee16BitAddress address, byte[] data) throws TimeoutException, XBeeException {
		// Verify the parameters are not null, if they are null, throw an exception.
		if (address == null)
			throw new NullPointerException("Address cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		XBeePacket xbeePacket;
		
		logger.info(toString() + "Sending serial data to {} >> {}.", address, HexUtils.prettyHexString(data));
		
		// Depending on the protocol of the XBee device, the packet to send may vary.
		switch (getXBeeProtocol()) {
		case RAW_802_15_4:
			// Generate and send the Tx16 packet.
			xbeePacket = new TX16Packet(getNextFrameID(), address, XBeeTransmitOptions.NONE, data);
			break;
		default:
			// Generate and send the Transmit packet.
			xbeePacket = new TransmitPacket(getNextFrameID(), XBee64BitAddress.UNKNOWN_ADDRESS, address, 0, XBeeTransmitOptions.NONE, data);
		}
		sendAndCheckXBeePacket(xbeePacket, false);
	}
	
	/**
	 * Sends the provided data to the provided XBee device.
	 * 
	 * @param xbeeDevice The XBee device of the network that will receive the data.
	 * @param data Byte array containing data to be sent.
	 * @throws NullPointerException if {@code xbeeDevice == null} or {@code data == null}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout sending the serial data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see XBeeDevice
	 */
	public void sendSerialData(XBeeDevice xbeeDevice, byte[] data) throws TimeoutException, XBeeException {
		if (xbeeDevice == null)
			throw new NullPointerException("XBee device cannot be null");
		sendSerialData(xbeeDevice.get64BitAddress(), data);
	}
	
	/**
	 * Sends the provided {@code XBeePacket} and determines if the transmission status is success 
	 * for synchronous transmissions. If the status is not success, an {@code TransmitException} is
	 * thrown.
	 * 
	 * @param packet The {@code XBeePacket} to be sent.
	 * @param asyncTransmission Determines whether or not the transmission should be made asynchronously.
	 * @throws TransmitException if {@value packet} is not an instance of {@code TransmitStatusPacket} or 
	 *                           if {@value packet} is not an instance of {@code TXStatusPacket} or 
	 *                           if its transmit status is different than {@code XBeeTransmitStatus.SUCCESS}.
	 * @throws XBeeException if there is any other XBee related error.
	 */
	private void sendAndCheckXBeePacket(XBeePacket packet, boolean asyncTransmission) throws TransmitException, XBeeException {
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
	 * @throws NullPointerException if {@code ioLine == null} or
	 *                              if {@code ioMode == null}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout sending the set configuration command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getIOConfiguration(IOLine)
	 * @see IOLine
	 * @see IOMode
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
	 * @return The IO mode (configuration) of the provided IO line.
	 * @throws NullPointerException if {@code ioLine == null}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout sending the get configuration command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setIOConfiguration(IOLine, IOMode)
	 * @see IOLine
	 * @see IOMode
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
	 * @param value The IOValue to set to the IO line ({@code HIGH} or {@code LOW}).
	 * @throws NullPointerException if {@code ioLine == null} or 
	 *                              if {@code ioValue == null}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout sending the set DIO command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setIOConfiguration(IOLine, IOMode)
	 * @see IOLine
	 * @see IOValue
	 * @see IOMode.DIGITAL_OUT_HIGH
	 * @see IOMode.DIGITAL_OUT_LOW
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
	 * Retrieves the digital value of the provided IO line (must be configured as digital I/O).
	 * 
	 * @param ioLine The IO line to get its digital value.
	 * @return The digital value corresponding to the provided IO line.
	 * @throws NullPointerException if {@code ioLine == null}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout sending the get IO values command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setIOConfiguration(IOLine, IOMode)
	 * @see IOLine
	 * @see IOMode.DIGITAL_IN
	 * @see IOMode.DIGITAL_OUT_HIGH
	 * @see IOMode.DIGITAL_OUT_LOW
	 */
	public IOValue getDIOValue(IOLine ioLine) throws TimeoutException, XBeeException {
		// Check IO line.
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
		
		// Try to build an IO Sample from the received response.
		IOSample ioSample;
		try {
			ioSample = new IOSample(response.getResponse());
		} catch (IllegalArgumentException e) {
			throw new XBeeException("Couldn't create the IO sample.", e);
		} catch (NullPointerException e) {
			throw new XBeeException("Couldn't create the IO sample.", e);
		}
		
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
	 * @throws NullPointerException if {@code ioLine == null}.
	 * @throws IllegalArgumentException if {@code ioLine.hasPWMCapability() == false} or 
	 *                                  if {@code value < 0} or
	 *                                  if {@code value > 1023}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout sending the set PWM duty cycle command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setIOConfiguration(IOLine, IOMode)
	 * @see IOLine
	 * @see IOMode.PWM
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
	 * @return The PWM duty cycle value corresponding to the provided IO line (0% - 100%).
	 * @throws NullPointerException if {@code ioLine == null}.
	 * @throws IllegalArgumentException if {@code ioLine.hasPWMCapability() == false}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout sending the get PWM duty cycle command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setIOConfiguration(IOLine, IOMode)
	 * @see IOLine
	 * @see IOMode.PWM
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
	 * Retrieves the analog value of the provided IO line (must be configured as ADC).
	 * 
	 * @param ioLine The IO line to get its analog value.
	 * @return The analog value corresponding to the provided IO line.
	 * @throws NullPointerException if {@code ioLine == null}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout sending the get IO values command.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #setIOConfiguration(IOLine, IOMode)
	 * @see IOLine
	 * @see IOMode.ADC
	 */
	public int getADCValue(IOLine ioLine) throws TimeoutException, XBeeException {
		// Check IO line.
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
		
		// Try to build an IO Sample from the received response.
		IOSample ioSample;
		try {
			ioSample = new IOSample(response.getResponse());
		} catch (IllegalArgumentException e) {
			throw new XBeeException("Couldn't create the IO sample.", e);
		} catch (NullPointerException e) {
			throw new XBeeException("Couldn't create the IO sample.", e);
		}
		
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
	 * @throws ATCommandException if {@code response == null} or 
	 *                            if {@code response.getResponseStatus() != ATCommandStatus.OK}.
	 */
	private void checkATCommandResponseIsValid(ATCommandResponse response) throws ATCommandException {
		if (response == null || response.getResponseStatus() == null)
			throw new ATCommandException(null);
		else if (response.getResponseStatus() != ATCommandStatus.OK)
			throw new ATCommandException(response.getResponseStatus());
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return connectionInterface.toString();
	}
}

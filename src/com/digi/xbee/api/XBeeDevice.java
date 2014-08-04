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
import com.digi.xbee.api.exceptions.ConnectionException;
import com.digi.xbee.api.exceptions.InterfaceAlreadyOpenException;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeDeviceException;
import com.digi.xbee.api.listeners.IPacketReceiveListener;
import com.digi.xbee.api.listeners.ISerialDataReceiveListener;
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.models.XBeeTransmitOptions;
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
	 * @throws ConnectionException if the device is already open or if any error occurs when 
	 *                             opening the device, such as an invalid connection interface,
	 *                             an invalid configuration or the connection interface is busy.
	 * @throws XBeeDeviceException if the operating mode cannot be determined or is not supported.
	 * @throws TimeoutException if the configured time expires when trying to open the device.
	 */
	public void open() throws ConnectionException, XBeeDeviceException, TimeoutException {
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
	 * 
	 * @throws TimeoutException if the configured time expires.
	 * @throws InterfaceNotOpenException if the interface is not open.
	 * 
	 * @see OperatingMode
	 */
	protected OperatingMode determineOperatingMode() throws InterfaceNotOpenException, TimeoutException {
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
			if (e.getErrorCode() == XBeeException.CONNECTION_TIMEOUT) {
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
				} catch (InvalidOperatingModeException e1) {
					// TODO Log this exception.
				} catch (XBeeException e1) {
					// TODO Log this exception
				} catch (InterruptedException e2) {
					// TODO Log this exception
				}
			}
		} catch (InvalidOperatingModeException e) {
			// TODO Log this exception.
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
	 * @throws XBeeException
	 * @throws InvalidModeException 
	 */
	public boolean enterATCommandMode() throws InvalidOperatingModeException, XBeeException {
		if (operatingMode != OperatingMode.AT)
			throw new InvalidOperatingModeException("Invalid mode. Command mode can only be accesses while in AT mode.");
		
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
				throw new XBeeException(XBeeException.CONNECTION_TIMEOUT);
			
			// Check if the read data is 'OK\r'.
			String readString = new String(readData, 0, readBytes);
			if (!readString.contains(COMMAND_MODE_OK))
				return false;
			
			// Read data was 'OK\r'.
			return true;
		} catch (IOException e) {
			// TODO Log this exception.
		} catch (InterruptedException e) {
			// TODO Log this exception.
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
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws InvalidOperatingModeException if the operating mode is different than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws TimeoutException if the configured time expires while waiting for the command reply.
	 * @throws IOException if an I/O error occurs while sending the AT command.
	 * @throws NullPointerException if {@code command == null}
	 * 
	 * @see #setReceiveTimeout(int)
	 * @see #getReceiveTimeout()
	 * 
	 * @see ATCommand
	 * @see ATCommandResponse
	 */
	public ATCommandResponse sendATCommand(ATCommand command) 
			throws InterfaceNotOpenException, InvalidOperatingModeException, TimeoutException, IOException {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		// Check if command is null.
		if (command == null)
			throw new NullPointerException("AT command cannot be null.");
		
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
				logger.debug(toString() + "AT command response: {}.", HexUtils.prettyHexString(response.getResponse()));
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
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws InvalidOperatingModeException if the operating mode is different than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws TimeoutException if the configured time expires while waiting for the packet reply.
	 * @throws IOException if an I/O error occurs while sending the XBee packet.
	 * 
	 * @see #setReceiveTimeout(int)
	 * @see #getReceiveTimeout()
	 * 
	 * @see XBeePacket
	 */
	public XBeePacket sendXBeePacket(final XBeePacket packet) 
			throws InterfaceNotOpenException, InvalidOperatingModeException, TimeoutException, IOException {
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
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws InvalidOperatingModeException if the operating mode is different than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws IOException if an I/O error occurs while sending the XBee packet.
	 */
	public void sendXBeePacket(XBeePacket packet,IPacketReceiveListener packetReceiveListener)
			throws InterfaceNotOpenException, InvalidOperatingModeException, IOException {
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
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws InvalidOperatingModeException if the operating mode is different than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws IOException if an I/O error occurs while sending the XBee packet.
	 */
	public void sendXBeePacketAsync(XBeePacket packet) 
			throws InterfaceNotOpenException, InvalidOperatingModeException, IOException {
		sendXBeePacket(packet, null);
	}
	
	/**
	 * Writes the given XBee packet in the connection interface.
	 * 
	 * @param packet XBee packet to be written.
	 * 
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
	 * provided 64-Bit address.
	 * See {@link com.digi.xbee.api.models.XBee64BitAddress}.
	 * 
	 * @param address The 64-Bit address of the XBee that will receive the data.
	 * @param data Byte array containing data to be sent.
	 * @return True if the data was sent successfully, false otherwise.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws InvalidOperatingModeException if the operating mode is different than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws TimeoutException if the configured time expires.
	 * @throws IOException if an I/O error occurs while sending the data.
	 * 
	 * @throws NullPointerException if {@address == null} or {@data == null}.
	 */
	public boolean sendSerialData(XBee64BitAddress address, byte[] data) 
			throws InterfaceNotOpenException, InvalidOperatingModeException, TimeoutException, IOException {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		// Verify the parameters are not null, if they are null, throw an exception.
		if (address == null)
			throw new NullPointerException("Address cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		
		XBeePacket xbeePacket;
		XBeePacket receivedPacket;
		
		logger.info(toString() + "Sending serial data to {} >> {}.", address, HexUtils.byteArrayToHexString(data));
		
		// Depending on the protocol of the XBee device, the packet to send may vary.
		switch (getXBeeProtocol()) {
		case RAW_802_15_4:
			xbeePacket = new TX64Packet(getNextFrameID(), address, XBeeTransmitOptions.NONE, data);
			receivedPacket = sendXBeePacket(xbeePacket);
			// If receivedPacket is null it means that packet was sent asynchronously, return true.
			if (receivedPacket == null)
				return true;
			// Verify that the packet was sent successfully checking the received transmit status.
			if (receivedPacket instanceof TXStatusPacket) {
				switch (((TXStatusPacket)receivedPacket).getTransmitStatus()) {
				case SUCCESS:
					return true;
				default:
					return false;
				}
			} else
				return false;
		default:
			xbeePacket = new TransmitPacket(getNextFrameID(), address, XBee16BitAddress.UNKNOWN_ADDRESS, 0, XBeeTransmitOptions.NONE, data);
			receivedPacket = sendXBeePacket(xbeePacket);
			// If receivedPacket is null it means that packet was sent asynchronously, return true.
			if (receivedPacket == null)
				return true;
			// Verify that the packet was sent successfully checking the received transmit status.
			if (receivedPacket instanceof TransmitStatusPacket) {
				switch (((TransmitStatusPacket)receivedPacket).getTransmitStatus()) {
				case SUCCESS:
					return true;
				default:
					return false;
				}
			} else
				return false;
		}
	}
	
	/**
	 * Sends the provided data to the XBee device of the network corresponding to the 
	 * provided 16-Bit address.
	 * See {@link com.digi.xbee.api.models.XBee16BitAddress}.
	 * 
	 * @param address The 16-Bit address of the XBee that will receive the data.
	 * @param data Byte array containing data to be sent.
	 * @return True if the data was sent successfully, false otherwise.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws InvalidOperatingModeException if the operating mode is different than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws TimeoutException if the configured time expires.
	 * @throws IOException if an I/O error occurs while sending the data.
	 * 
	 * @throws NullPointerException if {@address == null} or {@data == null}.
	 */
	public boolean sendSerialData(XBee16BitAddress address, byte[] data) 
			throws InterfaceNotOpenException, InvalidOperatingModeException, TimeoutException, IOException {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		// Verify the parameters are not null, if they are null, throw an exception.
		if (address == null)
			throw new NullPointerException("Address cannot be null");
		if (data == null)
			throw new NullPointerException("Data cannot be null");
		
		XBeePacket xbeePacket;
		XBeePacket receivedPacket;
		
		logger.info(toString() + "Sending serial data to {} >> {}.", address, HexUtils.byteArrayToHexString(data));
		
		// Depending on the protocol of the XBee device, the packet to send may vary.
		switch (getXBeeProtocol()) {
		case RAW_802_15_4:
			xbeePacket = new TX16Packet(getNextFrameID(), address, XBeeTransmitOptions.NONE, data);
			receivedPacket = sendXBeePacket(xbeePacket);
			// If receivedPacket is null it means that packet was sent asynchronously, return true.
			if (receivedPacket == null)
				return true;
			// Verify that the packet was sent successfully checking the received transmit status.
			if (receivedPacket instanceof TXStatusPacket) {
				switch (((TXStatusPacket)receivedPacket).getTransmitStatus()) {
				case SUCCESS:
					return true;
				default:
					return false;
				}
			} else
				return false;
		default:
			xbeePacket = new TransmitPacket(getNextFrameID(), XBee64BitAddress.UNKNOWN_ADDRESS, address, 0, XBeeTransmitOptions.NONE, data);
			receivedPacket = sendXBeePacket(xbeePacket);
			// If receivedPacket is null it means that packet was sent asynchronously, return true.
			if (receivedPacket == null)
				return true;
			// Verify that the packet was sent successfully checking the received transmit status.
			if (receivedPacket instanceof TransmitStatusPacket) {
				switch (((TransmitStatusPacket)receivedPacket).getTransmitStatus()) {
				case SUCCESS:
					return true;
				default:
					return false;
				}
			} else
				return false;
		}
	}
	
	/**
	 * Sends the provided data to the provided XBee device.
	 * See {@link com.digi.xbee.api.XBeeDevice}.
	 * 
	 * @param xbeeDevice The XBee device of the network that will receive the data.
	 * @param data Byte array containing data to be sent.
	 * @return True if the data was sent successfully, false otherwise.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws InvalidOperatingModeException if the operating mode is different than {@link OperatingMode#API} and 
	 *                                       {@link OperatingMode#API_ESCAPE}.
	 * @throws TimeoutException if the configured time expires.
	 * @throws IOException if an I/O error occurs while sending the data.
	 * 
	 * @throws NullPointerException if {@code xbeeDevice == null} or {@code data == null}.
	 */
	public boolean sendSerialData(XBeeDevice xbeeDevice, byte[] data) 
			throws InterfaceNotOpenException, InvalidOperatingModeException, TimeoutException, IOException {
		if (xbeeDevice == null)
			throw new NullPointerException("XBee device cannot be null");
		return sendSerialData(xbeeDevice.get64BitAddress(), data);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return connectionInterface.toString();
	}
}

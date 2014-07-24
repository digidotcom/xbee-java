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

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.connection.DataReader;
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IPacketReceiveListener;
import com.digi.xbee.api.listeners.ISerialDataReceiveListener;
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.models.XBeeTransmitOptions;
import com.digi.xbee.api.packet.GenericXBeePacket;
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
	
	// Variables.
	protected IConnectionInterface connectionInterface;
	
	protected DataReader dataReader = null;
	
	protected XBeeProtocol xbeeProtocol = XBeeProtocol.UNKNOWN;
	
	protected OperatingMode operatingMode = OperatingMode.UNKNOWN;
	
	protected XBee64BitAddress xbee64BitAddress;
	
	protected int currentFrameID = 0xFF;
	protected int receiveTimeout = DEFAULT_RECEIVE_TIMETOUT;
	
	/**
	 * Class constructor. Instantiates a new XBeeDevice object in the given port name and baud rate.
	 * 
	 * @param port Serial port name where XBee device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device. Other 
	 * 					connection parameters will be set as default (8 data bits, 
	 * 					1 stop bit, no parity, no flow control).
	 * 
	 * @throws NullPointerException if {@code port == null}.
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
			throw new IllegalArgumentException("ConnectionInterface cannot be null.");
		
		this.connectionInterface = connectionInterface;
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
	 * @throws XBeeException if the device is already connected or
	 *                       if any error occurs when connecting the device.
	 * @throws InvalidOperatingModeException if the operating mode is different than {@code XBeeMode.API}
	 *                                       and {@code XBeeMode.API_ESCAPE}.
	 */
	public void open() throws XBeeException, InvalidOperatingModeException {
		// First, verify that the connection is not already open.
		if (connectionInterface.isOpen())
			throw new XBeeException(XBeeException.CONNECTION_ALREADY_OPEN);
		
		// Connect the interface.
		connectionInterface.open();
		
		// Initialize the data reader.
		dataReader = new DataReader(connectionInterface);
		dataReader.start();
		
		// Determine the operating mode of the XBee device if it is unknown.
		if (operatingMode == OperatingMode.UNKNOWN)
			operatingMode = determineConnectionMode();
		
		// Check if the operating mode is a valid and supported one.
		if (operatingMode == OperatingMode.UNKNOWN) {
			close();
			throw new InvalidOperatingModeException("Could not determine operating mode.");
		} else if (operatingMode == OperatingMode.AT) {
			close();
			throw new InvalidOperatingModeException("Unsupported operating mode AT.");
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
	 * Determines the connection mode of the XBee device.
	 * 
	 * @return The operating mode of the XBee device.
	 * 
	 * @see OperatingMode
	 */
	protected OperatingMode determineConnectionMode() {
		// Check if device is in API or API Escaped operating modes.
		try {
			operatingMode = OperatingMode.API;
			dataReader.setXBeeReaderMode(operatingMode);
			ATCommandResponse response = sendATCommand(new ATCommand("AP"));
			if (response.getResponse() != null) {
				if (response.getResponse()[0] == OperatingMode.API.getID())
					return OperatingMode.API;
				else
					return OperatingMode.API_ESCAPE;
			}
		} catch (XBeeException e) {
			// TODO: Check if device is in AT mode here and return it if so!!.
			e.printStackTrace();
			return OperatingMode.UNKNOWN;
		} catch (InvalidOperatingModeException e) {
			return OperatingMode.UNKNOWN;
		}
		return OperatingMode.UNKNOWN;
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
	 * @return an {@code ATCommandResponse} object containing the response of the command.
	 * @throws InvalidOperatingModeException if the operating mode is different than {@code XBeeMode.API}
	 *                                       and {@code XBeeMode.API_ESCAPE}.
	 * @throws XBeeException
	 * @throws NullPointerException if {@code command == null}
	 * 
	 * @see #setReceiveTimeout(int)
	 * @see #getReceiveTimeout()
	 * 
	 * @see ATCommand
	 * @see ATCommandResponse
	 */
	public ATCommandResponse sendATCommand(ATCommand command) throws InvalidOperatingModeException, XBeeException {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new XBeeException(XBeeException.CONNECTION_NOT_OPEN);
		
		// Check if command is null.
		if (!connectionInterface.isOpen())
			throw new NullPointerException("AT command cannot be null.");
		
		ATCommandResponse response = null;
		switch (getOperatingMode()) {
		case AT:
		case UNKNOWN:
		default:
			throw new InvalidOperatingModeException();
		case API:
		case API_ESCAPE:
			// Create AT command packet
			XBeeAPIPacket packet = new ATCommandPacket(getNextFrameID(), command.getCommand(), command.getParameter());
			try {
				// Send the packet and build response.
				ATCommandResponsePacket answerPacket = (ATCommandResponsePacket)sendXBeePacket(packet);
				response = new ATCommandResponse(command, answerPacket.getCommandData(), answerPacket.getStatus());
			} catch (InvalidOperatingModeException e) {
				// Ignore, we will never enter here.
			} catch (ClassCastException e1) {
				System.err.println("Received an invalid packet type after sending an AT Command packet.");
				e1.printStackTrace();
			}
		}
		return response;
	}
	
	/**
	 * Sends the given XBee packet synchronously and waits until response is 
	 * received or receive timeout is reached.
	 * 
	 * @param packet XBee packet to be sent.
	 * @return XBeePacket object containing the response of the sent packet.
	 * @throws InvalidOperatingModeException if the operating mode is different than {@code XBeeMode.API}
	 *                                       and {@code XBeeMode.API_ESCAPE}.
	 * @throws XBeeException
	 * 
	 * @see #setReceiveTimeout(int)
	 * @see #getReceiveTimeout()
	 * 
	 * @see XBeePacket
	 */
	public XBeePacket sendXBeePacket(final XBeePacket packet) throws InvalidOperatingModeException, XBeeException {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new XBeeException(XBeeException.CONNECTION_NOT_OPEN);
		
		switch (getOperatingMode()) {
		case AT:
		case UNKNOWN:
		default:
			throw new InvalidOperatingModeException();
		case API:
		case API_ESCAPE:
			// Build response container.
			ArrayList<XBeePacket> responseList = new ArrayList<XBeePacket>();
			
			// If the packet is generic we don't wait for answer, send it as an async. packet.
			if (packet instanceof GenericXBeePacket) {
				sendXBeePacketAsync(packet);
				return null;
			}
			
			// Add the required frame ID to the packet if necessary.
			insertFrameID(packet);
			
			// Generate a packet received listener for the packet to be sent.
			IPacketReceiveListener packetReceiveListener = createPacketReceivedListener(packet, responseList);
			
			// Add the packet listener to the data reader.
			dataReader.addPacketReceiveListener(packetReceiveListener);
			try {
				// Write the packet data.
				writePacket(packet);
				// Wait for response or timeout.
				synchronized (responseList) {
					try {
						responseList.wait(receiveTimeout);
					} catch (InterruptedException e) {}
				}
				// After the wait check if we received any response, if not throw timeout exception.
				if (responseList.size() < 1)
					throw new XBeeException(XBeeException.CONNECTION_TIMEOUT);
				// Return the received packet.
				return responseList.get(0);
			} catch (IOException e) {
				throw new XBeeException(XBeeException.GENERIC, e.getMessage());
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
	 * @param packetReceiveListener Listener for the operation, may be null.
	 * @throws InvalidOperatingModeException
	 * @throws XBeeException
	 */
	public void sendXBeePacket(XBeePacket packet, IPacketReceiveListener packetReceiveListener) throws InvalidOperatingModeException, XBeeException {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new XBeeException(XBeeException.CONNECTION_NOT_OPEN);
		
		switch (getOperatingMode()) {
		case AT:
		case UNKNOWN:
		default:
			throw new InvalidOperatingModeException();
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
			try {
				// Write packet data.
				writePacket(packet);
			} catch (IOException e) {
				throw new XBeeException(XBeeException.GENERIC, e.getMessage());
			}
			break;
		}
	}
	
	/**
	 * Sends the given XBee packet asynchronously. 
	 * 
	 * @param packet XBee packet to be sent asynchronously.
	 * @throws InvalidModeException
	 * @throws XBeeException
	 */
	public void sendXBeePacketAsync(XBeePacket packet) throws InvalidOperatingModeException, XBeeException {
		sendXBeePacket(packet, null);
	}
	
	/**
	 * Writes the given XBee packet in the connection interface.
	 * 
	 * @param packet XBee packet to be written.
	 * @throws IOException
	 */
	protected void writePacket(XBeePacket packet) throws IOException {
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
	 */
	public void setReceiveTimeout(int receiveTimeout) {
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
	 * @throws InvalidOperatingModeException
	 * @throws XBeeException
	 */
	public boolean sendSerialData(XBee64BitAddress address, byte[] data) throws InvalidOperatingModeException, XBeeException {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new XBeeException(XBeeException.CONNECTION_NOT_OPEN);
		
		XBeePacket xbeePacket;
		XBeePacket receivedPacket;
		
		// Verify the parameters are not null, if they are null, throw an exception.
		if (address == null || data == null)
			throw new XBeeException(XBeeException.INVALID_ARGUMENT, "Address and data cannot be null");
		
		// Depending on the protocol of the XBee device, the packet to send may vary.
		switch (getXBeeProtocol()) {
		case RAW_802_15_4:
			xbeePacket = new TX64Packet(getNextFrameID(), address, XBeeTransmitOptions.NONE, data);
			receivedPacket = sendXBeePacket(xbeePacket);
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
	 * @throws InvalidOperatingModeException
	 * @throws XBeeException
	 */
	public boolean sendSerialData(XBee16BitAddress address, byte[] data) throws InvalidOperatingModeException, XBeeException {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new XBeeException(XBeeException.CONNECTION_NOT_OPEN);
		
		XBeePacket xbeePacket;
		XBeePacket receivedPacket;
		
		// Verify the parameters are not null, if they are null, throw an exception.
		if (address == null || data == null)
			throw new XBeeException(XBeeException.INVALID_ARGUMENT, "Address and data cannot be null");
		
		// Depending on the protocol of the XBee device, the packet to send may vary.
		switch (getXBeeProtocol()) {
		case RAW_802_15_4:
			xbeePacket = new TX16Packet(getNextFrameID(), address, XBeeTransmitOptions.NONE, data);
			receivedPacket = sendXBeePacket(xbeePacket);
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
	 * @throws InvalidOperatingModeException
	 * @throws XBeeException
	 */
	public boolean sendSerialData(XBeeDevice xbeeDevice, byte[] data) throws InvalidOperatingModeException, XBeeException {
		if (xbeeDevice == null)
			throw new XBeeException(XBeeException.INVALID_ARGUMENT, "XBee device cannot be null");
		return sendSerialData(xbeeDevice.get64BitAddress(), data);
	}
}

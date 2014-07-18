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
import com.digi.xbee.api.models.XBeeMode;
import com.digi.xbee.api.packet.GenericXBeePacket;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.XBeeAPIType;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ATCommandPacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.utils.HexUtils;

public class XBeeDevice {
	
	// Constants.
	protected static int DEFAULT_RECEIVE_TIMETOUT = 2000; // 2.0 seconds of timeout to receive packet and command responses.
	
	// Variables.
	protected IConnectionInterface connectionInterface;
	
	protected DataReader dataReader = null;
	
	protected XBeeMode operatingMode = XBeeMode.UNKNOWN;
	
	protected int currentFrameID = 0xFF;
	protected int receiveTimeout = DEFAULT_RECEIVE_TIMETOUT;
	
	/**
	 * Class constructor. Instantiates a new XBeeDevice object in the given port name and baud rate.
	 * 
	 * @param port Serial port name where XBee device is attached to.
	 * @param baudRate Serial port baud rate to communicate with the device. Other 
	 * 					connection parameters will be set as default (8 data bits, 
	 * 					1 stop bit, no parity, no flow control).
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
	 * @throws XBeeException 
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
	 */
	public XBeeDevice(String port, SerialPortParameters serialPortParameters) {
		this(XBee.createConnectiontionInterface(port, serialPortParameters));
	}
	
	/**
	 * Class constructor. Instantiates a new XBeeDevice object with the given connection 
	 * interface.
	 * 
	 * @param communicationInterface The connection interface with the physical XBee device.
	 */
	public XBeeDevice(IConnectionInterface connectionInterface) {
		this.connectionInterface = connectionInterface;
	}
	
	/**
	 * Retrieves the connection interface associated to this XBee device.
	 * 
	 * @return XBee device's connection interface.
	 */
	public IConnectionInterface getConnectionInterface() {
		return connectionInterface;
	}
	
	/**
	 * Opens the connection interface associated with this XBee device.
	 */
	public void connect() throws XBeeException, InvalidOperatingModeException {
		// First, verify that the connection is not already open.
		if (connectionInterface.isConnected())
			throw new XBeeException(XBeeException.CONNECTION_ALREADY_OPEN);
		
		// Connect the interface.
		connectionInterface.connect();
		
		// Determine the operating mode of the XBee device.
		operatingMode = determineConnectionMode();
		if (operatingMode == XBeeMode.UNKNOWN) {
			disconnect();
			throw new InvalidOperatingModeException("Could not determine operating mode.");
		} else if (operatingMode == XBeeMode.AT) {
			disconnect();
			throw new InvalidOperatingModeException("Unsupported operating mode AT.");
		}
	}
	
	/**
	 * Closes the connection interface associated with this XBee device.
	 */
	public void disconnect() {
		// Stop XBee reader.
		if (dataReader != null && dataReader.isRunning())
			dataReader.stopReader();
		// Close interface.
		connectionInterface.disconnect();
	}
	
	/**
	 * Retrieves whether or not the connection interface associated to the device is 
	 * connected.
	 * 
	 * @return True if the interface is connected, false otherwise.
	 */
	public boolean isConnected() {
		if (connectionInterface != null)
			return connectionInterface.isConnected();
		return false;
	}
	
	/**
	 * Determines the connection mode of the XBee device.
	 * See {@link com.digi.xbee.api.models.XBeeMode}.
	 * 
	 * @return The operating mode of the XBee device.
	 */
	protected XBeeMode determineConnectionMode() {
		// Instantiate the data reader.
		dataReader = new DataReader(connectionInterface);
		dataReader.start();
		
		// Check if device is in API or API Escaped operating modes.
		try {
			operatingMode = XBeeMode.API;
			dataReader.setXBeeReaderMode(operatingMode);
			ATCommandResponse response = sendATCommand(new ATCommand("AP"));
			if (response.getResponse() != null) {
				if (response.getResponse()[0] == 0x01)
					return XBeeMode.API;
				else
					return XBeeMode.API_ESCAPE;
			}
		} catch (XBeeException e) {
			// TODO: Check if device is in AT mode here and return it if so!!.
			e.printStackTrace();
			return XBeeMode.UNKNOWN;
		} catch (InvalidOperatingModeException e) {
			return XBeeMode.UNKNOWN;
		}
		return XBeeMode.UNKNOWN;
	}
	
	/**
	 * Retrieves the Operating mode (AT, API or API escaped) of the XBee device.
	 * 
	 * @return The operating mode of the XBee device.
	 */
	public XBeeMode getOperatingMode() {
		return operatingMode;
	}
	
	/**
	 * Sends the given AT command and waits for answer or until receive timeout 
	 * is reached.
	 * See {@link #setReceiveTimeout(int)} and {@link #getReceiveTimeout()} for more
	 * information about default timeout.
	 * 
	 * @param command AT command to be sent.
	 * @return an ATCommandResponse object containing the response of the command.
	 * @throws InvalidOperatingModeException
	 * @throws XBeeException
	 */
	public ATCommandResponse sendATCommand(ATCommand command) throws InvalidOperatingModeException, XBeeException {
		// Check connection.
		if (!connectionInterface.isConnected())
			throw new XBeeException(XBeeException.CONNECTION_NOT_OPEN);
		
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
	 * See {@link #setReceiveTimeout(int)} and {@link #getReceiveTimeout()} for more
	 * information about default timeout.
	 * 
	 * @param packet XBee packet to be sent.
	 * @return XBeePacket object containing the response of the sent packet.
	 * @throws InvalidOperatingModeException
	 * @throws XBeeException
	 */
	public XBeePacket sendXBeePacket(final XBeePacket packet) throws InvalidOperatingModeException, XBeeException {
		// Check connection.
		if (!connectionInterface.isConnected())
			throw new XBeeException(XBeeException.CONNECTION_NOT_OPEN);
		
		switch (getOperatingMode()) {
		case AT:
		case UNKNOWN:
		default:
			throw new InvalidOperatingModeException();
		case API:
		case API_ESCAPE:
			// Build response container.
			final ArrayList<XBeePacket> responseList = new ArrayList<XBeePacket>();
			
			// If the packet is generic we don't wait for answer, send it as an async. packet.
			if (packet instanceof GenericXBeePacket) {
				sendXBeePacketAsync(packet);
				return null;
			}
			
			// Add the required frame ID.
			if (((XBeeAPIPacket)packet).hasAPIFrameID() && ((XBeeAPIPacket)packet).getFrameID() == XBeeAPIPacket.NO_FRAME_ID)
				((XBeeAPIPacket)packet).setFrameID(getNextFrameID());
			IPacketReceiveListener packetReceiveListener = new IPacketReceiveListener() {
				/*
				 * (non-Javadoc)
				 * @see com.digi.xbee.listeners.XBeePacketListener#packetReceived(byte[])
				 */
				public void packetReceived(XBeePacket receivedPacket) {
					// Check if it is the packet we are waiting for.
					if (((XBeeAPIPacket)receivedPacket).hasAPIFrameID() && 
							(((XBeeAPIPacket)receivedPacket).getFrameID() == (((XBeeAPIPacket)packet).getFrameID()))) {
						// Security check to avoid class cast exceptions. It has been observed that parallel processes 
						// using the same connection but with different frame index may collide and cause this exception at some point.
						if (packet instanceof XBeeAPIPacket
								&& receivedPacket instanceof XBeeAPIPacket) {
							XBeeAPIPacket sentAPIPacket = (XBeeAPIPacket)packet;
							XBeeAPIPacket receivedAPIPacket = (XBeeAPIPacket)receivedPacket;
							if (sentAPIPacket.getAPIID() == XBeeAPIType.AT_COMMAND) {
								if (receivedAPIPacket.getAPIID() != XBeeAPIType.AT_COMMAND_RESPONSE)
									return;
								if (!((ATCommandPacket)sentAPIPacket).getCommand().equalsIgnoreCase(((ATCommandResponsePacket)receivedPacket).getCommand()))
									return;
							}
						}
						if (!HexUtils.byteArrayToHexString(packet.generateByteArray()).equals(HexUtils.byteArrayToHexString(receivedPacket.generateByteArray()))) {
							responseList.add(receivedPacket);
							synchronized (responseList) {
								responseList.notify();
							}
						}
					}
				}
			};
			
			// Add the packet listener.
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
		if (!connectionInterface.isConnected())
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
				if (((XBeeAPIPacket)packet).hasAPIFrameID()) {
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
	public void startlisteningForPackets(IPacketReceiveListener listener) {
		dataReader.addPacketReceiveListener(listener);
	}
	
	/**
	 * Stops listening for packets in the provided packets listener. Removes 
	 * the given listener from the list of packets listeners.
	 * 
	 * @param listener Listener to be removed from the list of listeners.
	 */
	public void stoplisteningForPackets(IPacketReceiveListener listener) {
		dataReader.removePacketReceiveListener(listener);
	}
	
	/**
	 * Starts listening for serial data in the provided serial data listener. Adds 
	 * the given listener to the list of listeners to be notified when new serial 
	 * data is received.
	 * 
	 * @param listener Listener to be notified when new serial data is received.
	 */
	public void startlisteningForSerialData(ISerialDataReceiveListener listener) {
		dataReader.addSerialDatatReceiveListener(listener);
	}
	
	/**
	 * Stops listening for serial data in the provided serial data listener. Removes 
	 * the given listener from the list of serial data listeners.
	 * 
	 * @param listener Listener to be removed from the list of listeners.
	 */
	public void stoplisteningForSerialData(ISerialDataReceiveListener listener) {
		dataReader.removeSerialDataReceiveListener(listener);
	}
}

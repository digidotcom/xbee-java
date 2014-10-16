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
import java.util.Arrays;

import com.digi.xbee.api.connection.DataReader;
import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.exceptions.InterfaceAlreadyOpenException;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;
import com.digi.xbee.api.listeners.IModemStatusReceiveListener;
import com.digi.xbee.api.listeners.IPacketReceiveListener;
import com.digi.xbee.api.listeners.ISerialDataReceiveListener;
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.models.XBeePacketsQueue;
import com.digi.xbee.api.models.XBeeTransmitOptions;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ReceivePacket;
import com.digi.xbee.api.packet.common.TransmitPacket;
import com.digi.xbee.api.packet.raw.RX16Packet;
import com.digi.xbee.api.packet.raw.RX64Packet;
import com.digi.xbee.api.packet.raw.TX64Packet;
import com.digi.xbee.api.utils.HexUtils;

public class XBeeDevice extends AbstractXBeeDevice {

	// Constants.
	private static int TIMEOUT_RESET = 5000;
	private static int TIMEOUT_READ_PACKET = 3000;
	
	private static String COMMAND_MODE_CHAR = "+";
	private static String COMMAND_MODE_OK = "OK\r";
	
	// Variables.
	protected XBeeNetwork network;
	
	private Object resetLock = new Object();
	
	private boolean modemStatusReceived = false;
	
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
		super(port, baudRate);
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
		super(port, baudRate, dataBits, stopBits, parity, flowControl);
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
		super(port, serialPortParameters);
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
		super(connectionInterface);
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
		
		// Initialize the data reader.
		dataReader = new DataReader(connectionInterface, operatingMode, this);
		dataReader.start();
		
		// Wait 10 milliseconds until the dataReader thread is started.
		// This is because when the connection is opened immediately after 
		// closing it, there is sometimes a concurrency problem and the 
		// dataReader thread never dies.
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {}
		
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
		
		// Read the device info (obtain its parameters and protocol).
		readDeviceInfo();
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
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#isRemote()
	 */
	@Override
	public boolean isRemote() {
		return false;
	}
	
	/**
	 * Returns the network associated with the device.
	 * 
	 * @return The XBee network of the device.
	 * 
	 * @throws InterfaceNotOpenException If the device is not open.
	 * 
	 * @see XBeeNetwork
	 */
	public XBeeNetwork getNetwork() {
		if (!isOpen())
			throw new InterfaceNotOpenException();
		if (network == null)
			network = new XBeeNetwork(this);
		return network;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#getOperatingMode()
	 */
	@Override
	public OperatingMode getOperatingMode() {
		return super.getOperatingMode();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#getNextFrameID()
	 */
	@Override
	protected int getNextFrameID() {
		return super.getNextFrameID();
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
	private boolean enterATCommandMode() throws InvalidOperatingModeException, TimeoutException {
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
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#startListeningForPackets(com.digi.xbee.api.listeners.IPacketReceiveListener)
	 */
	@Override
	public void startListeningForPackets(IPacketReceiveListener listener) {
		super.startListeningForPackets(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#stopListeningForPackets(com.digi.xbee.api.listeners.IPacketReceiveListener)
	 */
	@Override
	public void stopListeningForPackets(IPacketReceiveListener listener) {
		super.stopListeningForPackets(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#startListeningForSerialData(com.digi.xbee.api.listeners.ISerialDataReceiveListener)
	 */
	@Override
	public void startListeningForSerialData(ISerialDataReceiveListener listener) {
		super.startListeningForSerialData(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#stopListeningForSerialData(com.digi.xbee.api.listeners.ISerialDataReceiveListener)
	 */
	@Override
	public void stopListeningForSerialData(ISerialDataReceiveListener listener) {
		super.stopListeningForSerialData(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#startListeningForIOSamples(com.digi.xbee.api.listeners.IIOSampleReceiveListener)
	 */
	@Override
	public void startListeningForIOSamples(IIOSampleReceiveListener listener) {
		super.startListeningForIOSamples(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#stopListeningForIOSamples(com.digi.xbee.api.listeners.IIOSampleReceiveListener)
	 */
	@Override
	public void stopListeningForIOSamples(IIOSampleReceiveListener listener) {
		super.stopListeningForIOSamples(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#startListeningForModemStatusEvents(com.digi.xbee.api.listeners.IModemStatusReceiveListener)
	 */
	@Override
	public void startListeningForModemStatusEvents(IModemStatusReceiveListener listener) {
		super.startListeningForModemStatusEvents(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#stopListeningForModemStatusEvents(com.digi.xbee.api.listeners.IModemStatusReceiveListener)
	 */
	@Override
	public void stopListeningForModemStatusEvents(IModemStatusReceiveListener listener) {
		super.stopListeningForModemStatusEvents(listener);
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
	 * @see #sendSerialDataAsync(AbstractXBeeDevice, byte[])
	 * @see #sendSerialData(XBee16BitAddress, byte[])
	 * @see #sendSerialData(XBee64BitAddress, byte[])
	 * @see #sendSerialData(AbstractXBeeDevice, byte[])
	 */
	protected void sendSerialDataAsync(XBee64BitAddress address, byte[] data) throws XBeeException {
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
		
		XBeePacket xbeePacket;
		switch (getXBeeProtocol()) {
		case RAW_802_15_4:
			xbeePacket = new TX64Packet(getNextFrameID(), address, XBeeTransmitOptions.NONE, data);
			break;
		default:
			xbeePacket = new TransmitPacket(getNextFrameID(), address, XBee16BitAddress.UNKNOWN_ADDRESS, 0, XBeeTransmitOptions.NONE, data);
		}
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
	 * @see #sendSerialData(AbstractXBeeDevice, byte[])
	 * @see #sendSerialDataAsync(XBee64BitAddress, byte[])
	 * @see #sendSerialDataAsync(XBee16BitAddress, byte[])
	 * @see #sendSerialDataAsync(AbstractXBeeDevice, byte[])
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
	 * @see #sendSerialData(AbstractXBeeDevice, byte[])
	 */
	public void sendSerialDataAsync(RemoteXBeeDevice xbeeDevice, byte[] data) throws XBeeException {
		if (xbeeDevice == null)
			throw new NullPointerException("Remote XBee device cannot be null");
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
	 * @see #sendSerialData(AbstractXBeeDevice, byte[])
	 * @see #sendSerialDataAsync(XBee64BitAddress, byte[])
	 * @see #sendSerialDataAsync(XBee16BitAddress, byte[])
	 * @see #sendSerialDataAsync(AbstractXBeeDevice, byte[])
	 */
	protected void sendSerialData(XBee64BitAddress address, byte[] data) throws TimeoutException, XBeeException {
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
		
		XBeePacket xbeePacket;
		switch (getXBeeProtocol()) {
		case RAW_802_15_4:
			xbeePacket = new TX64Packet(getNextFrameID(), address, XBeeTransmitOptions.NONE, data);
			break;
		default:
			xbeePacket = new TransmitPacket(getNextFrameID(), address, XBee16BitAddress.UNKNOWN_ADDRESS, 0, XBeeTransmitOptions.NONE, data);
		}
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
	 * @see #sendSerialData(AbstractXBeeDevice, byte[])
	 * @see #sendSerialDataAsync(XBee64BitAddress, byte[])
	 * @see #sendSerialDataAsync(XBee16BitAddress, byte[])
	 * @see #sendSerialDataAsync(XBee64BitAddress, XBee16BitAddress, byte[])
	 * @see #sendSerialDataAsync(AbstractXBeeDevice, byte[])
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
	 * Sends the provided data to the given XBee device choosing the optimal send method 
	 * depending on the protocol of the local XBee device.
	 * 
	 * <p>This method blocks till a success or error response arrives or the 
	 * configured receive timeout expires.</p>
	 * 
	 * <p>The received timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * <p>For non-blocking operations use the method 
	 * {@link #sendSerialDataAsync(AbstractXBeeDevice, byte[])}.</p>
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
	 * @see #sendSerialDataAsync(AbstractXBeeDevice, byte[])
	 */
	public void sendSerialData(RemoteXBeeDevice xbeeDevice, byte[] data) throws TimeoutException, XBeeException {
		if (xbeeDevice == null)
			throw new NullPointerException("Remote XBee device cannot be null");
		
		switch (getXBeeProtocol()) {
		case ZIGBEE:
		case DIGI_POINT:
			if (xbeeDevice.get64BitAddress() != null && xbeeDevice.get16BitAddress() != null)
				sendSerialData(xbeeDevice.get64BitAddress(), xbeeDevice.get16BitAddress(), data);
			else
				sendSerialData(xbeeDevice.get64BitAddress(), data);
			break;
		case RAW_802_15_4:
			if (this instanceof Raw802Device) {
				if (xbeeDevice.get64BitAddress() != null)
					((Raw802Device)this).sendSerialData(xbeeDevice.get64BitAddress(), data);
				else
					((Raw802Device)this).sendSerialData(xbeeDevice.get16BitAddress(), data);
			} else
				sendSerialData(xbeeDevice.get64BitAddress(), data);
			break;
		case DIGI_MESH:
		default:
			sendSerialData(xbeeDevice.get64BitAddress(), data);
		}
	}
	
	/**
	 * Sends the provided data to all the XBee nodes of the network (broadcast).
	 * 
	 * <p>This method blocks till a success or error transmit status arrives or 
	 * the configured receive timeout expires.</p>
	 * 
	 * <p>The received timeout is configured using the {@code setReceiveTimeout}
	 * method and can be consulted with {@code getReceiveTimeout} method.</p>
	 * 
	 * @param data Byte array containing data to be sent.
	 * 
	 * @throws NullPointerException if {@code data == null}.
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws TimeoutException if there is a timeout sending the serial data.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see #getReceiveTimeout()
	 * @see #setReceiveTimeout(int)
	 */
	public void sendBroadcastSerialData(byte[] data) throws TimeoutException, XBeeException {
		sendSerialData(XBee64BitAddress.BROADCAST_ADDRESS, data);
	}
	
	/**
	 * Sends the given XBee packet and registers the given packet listener 
	 * (if not {@code null}) to wait for an answer.
	 * 
	 * @param packet XBee packet to be sent.
	 * @param packetReceiveListener Listener for the operation, {@code null} 
	 *                              not to be notified when the answer arrives.
	 *                              
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code packet == null}.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see XBeePacket
	 * @see IPacketReceiveListener
	 * @see #sendXBeePacket(XBeePacket)
	 * @see #sendXBeePacketAsync(XBeePacket)
	 */
	public void sendPacket(XBeePacket packet, IPacketReceiveListener packetReceiveListener) throws XBeeException {
		try {
			sendXBeePacket(packet, packetReceiveListener);
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
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code packet == null}.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see XBeePacket
	 * @see #sendXBeePacket(XBeePacket)
	 * @see #sendXBeePacket(XBeePacket, IPacketReceiveListener)
	 */
	public void sendPacketAsync(XBeePacket packet) throws XBeeException {
		try {
			super.sendXBeePacket(packet, null);
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
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
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code packet == null}.
	 * @throws TimeoutException if there is a timeout sending the XBee packet.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see XBeePacket
	 * @see #sendXBeePacket(XBeePacket, IPacketReceiveListener)
	 * @see #sendXBeePacketAsync(XBeePacket)
	 * @see #setReceiveTimeout(int)
	 * @see #getReceiveTimeout()
	 */
	public XBeePacket sendPacket(XBeePacket packet) throws TimeoutException, XBeeException {
		try {
			return super.sendXBeePacket(packet);
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
	}
	
	/**
	 * Waits until a Modem Status packet with status 0x00 (hardware reset) or 
	 * 0x01 (Watchdog timer reset) is received or the timeout is reached.
	 * 
	 * @return True if the Modem Status packet is received, false otherwise.
	 */
	private boolean waitForModemStatusPacket() {
		modemStatusReceived = false;
		startListeningForPackets(modemStatusListener);
		synchronized (resetLock) {
			try {
				resetLock.wait(TIMEOUT_RESET);
			} catch (InterruptedException e) { }
		}
		stopListeningForPackets(modemStatusListener);
		return modemStatusReceived;
	}
	
	/**
	 * Custom listener for Modem Status packets.
	 * 
	 * <p>When a Modem Status packet is received with status 0x00 or 0x01, it 
	 * notifies the object that was waiting for the reception.</p>
	 */
	private IPacketReceiveListener modemStatusListener = new IPacketReceiveListener() {
		/*
		 * (non-Javadoc)
		 * @see com.digi.xbee.api.listeners.IPacketReceiveListener#packetReceived(com.digi.xbee.api.packet.XBeePacket)
		 */
		public void packetReceived(XBeePacket receivedPacket) {
			// Discard non API packets.
			if (!(receivedPacket instanceof XBeeAPIPacket))
				return;
			
			byte[] hardwareReset = new byte[] {(byte) 0x8A, 0x00};
			byte[] watchdogTimerReset = new byte[] {(byte) 0x8A, 0x01};
			if (Arrays.equals(receivedPacket.getPacketData(), hardwareReset) ||
					Arrays.equals(receivedPacket.getPacketData(), watchdogTimerReset)) {
				modemStatusReceived = true;
				// Continue execution by notifying the lock object.
				synchronized (resetLock) {
					resetLock.notify();
				}
			}
		}
	};
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#reset()
	 */
	@Override
	public void reset() throws TimeoutException, XBeeException {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		logger.info(toString() + "Resetting the local module...");
		
		ATCommandResponse response = null;
		try {
			response = sendATCommand(new ATCommand("FR"));
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		}
		
		// Check if AT Command response is valid.
		checkATCommandResponseIsValid(response);
		
		// Wait for a Modem Status packet.
		if (!waitForModemStatusPacket())
			throw new TimeoutException("Timeout waiting for the Modem Status packet.");
		
		logger.info(toString() + "Module reset successfully.");
	}
	
	/**
	 * Retrieves an XBee Message object received by the local XBee device and 
	 * containing the data and the source address of the node that sent the 
	 * data.
	 * 
	 * <p>The method will try to read (receive) a data packet during the configured 
	 * receive timeout.</p> 
	 * 
	 * @return An XBee Message object containing the data and the source address 
	 *         of the node that sent the data. Null if the local device didn't 
	 *         receive a data packet during the configured receive timeout.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * 
	 * @see XBeeMessage
	 * @see #getReceiveTimeout()
	 * @see #setReceiveTimeout(int)
	 */
	public XBeeMessage readData() {
		return readDataPacket(null, TIMEOUT_READ_PACKET);
	}
	
	/**
	 * Retrieves an XBee Message object received by the local XBee device and 
	 * containing the data and the source address of the node that sent the 
	 * data.
	 * 
	 * <p>The method will try to read (receive) a data packet during the provided 
	 * timeout.</p> 
	 * 
	 * @param timeout The time to wait for a data packet in milliseconds.
	 * @return An XBee Message object containing the data and the source address 
	 *         of the node that sent the data. Null if the local device didn't 
	 *         receive a data packet during the provided timeout.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws IllegalArgumentException if {@code timeout < 0}.
	 * 
	 * @see XBeeMessage
	 */
	public XBeeMessage readData(int timeout) {
		if (timeout < 0)
			throw new IllegalArgumentException("Read timeout must be 0 or greater.");
		
		return readDataPacket(null, timeout);
	}
	
	/**
	 * Retrieves an XBee Message object received by the local XBee device that was 
	 * sent by the provided remote XBee device. The XBee Message contains the data 
	 * and the source address of the node that sent the data.
	 * 
	 * <p>The method will try to read (receive) a data packet from the provided 
	 * remote device during the configured receive timeout.</p> 
	 * 
	 * @param remoteXBeeDevice The remote device to get a data packet from.
	 * @return An XBee Message object containing the data and the source address 
	 *         of the node that sent the data. Null if the local device didn't 
	 *         receive a data packet from the remote XBee device during the 
	 *         configured receive timeout.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws NullPointerException if {@code remoteXBeeDevice == null}.
	 * 
	 * @see XBeeMessage
	 * @see RemoteXBeeDevice
	 * @see #getReceiveTimeout()
	 * @see #setReceiveTimeout(int)
	 */
	public XBeeMessage readDataFrom(RemoteXBeeDevice remoteXBeeDevice) {
		if (remoteXBeeDevice == null)
			throw new NullPointerException("Remote XBee device cannot be null.");
		
		return readDataPacket(remoteXBeeDevice, TIMEOUT_READ_PACKET);
	}
	
	/**
	 * Retrieves an XBee Message object received by the local XBee device that was 
	 * sent by the provided remote XBee device. The XBee Message contains the data 
	 * and the source address of the node that sent the data.
	 * 
	 * <p>The method will try to read (receive) a data packet from the provided 
	 * remote device during the provided timeout.</p> 
	 * 
	 * @param remoteXBeeDevice The remote device to get a data packet from.
	 * @param timeout The time to wait for a data packet in milliseconds.
	 * @return An XBee Message object containing the data and the source address 
	 *         of the node that sent the data. Null if the local device didn't 
	 *         receive a data packet from the remote XBee device during the 
	 *         provided timeout.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * @throws IllegalArgumentException if {@code timeout < 0}.
	 * @throws NullPointerException if {@code remoteXBeeDevice == null}.
	 * 
	 * @see XBeeMessage
	 * @see RemoteXBeeDevice
	 */
	public XBeeMessage readDataFrom(RemoteXBeeDevice remoteXBeeDevice, int timeout) {
		if (remoteXBeeDevice == null)
			throw new NullPointerException("Remote XBee device cannot be null.");
		if (timeout < 0)
			throw new IllegalArgumentException("Read timeout must be 0 or greater.");
		
		return readDataPacket(remoteXBeeDevice, timeout);
	}
	
	/**
	 * Retrieves an XBee Message object received by the local XBee device. The 
	 * XBee Message contains the data and the source address of the node that 
	 * sent the data. Depending on if the provided remote XBee device is null 
	 * or not, the method will get the first data packet read from any remote 
	 * XBee device or from the provided one.
	 * 
	 * <p>The method will try to read (receive) a data packet from the provided 
	 * remote device or any other device during the provided timeout.</p> 
	 * 
	 * @param remoteXBeeDevice The remote device to get a data packet from. Null to 
	 *                         read a data packet sent by any remote XBee device.
	 * @param timeout The time to wait for a data packet in milliseconds.
	 * @return An XBee Message object containing the data and the source address 
	 *         of the node that sent the data.
	 * 
	 * @throws InterfaceNotOpenException if the device is not open.
	 * 
	 * @see XBeeMessage
	 * @see RemoteXBeeDevice
	 */
	private XBeeMessage readDataPacket(RemoteXBeeDevice remoteXBeeDevice, int timeout) {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		XBeePacketsQueue xbeePacketsQueue = dataReader.getXBeePacketsQueue();
		XBeePacket xbeePacket = null;
		
		if (remoteXBeeDevice != null)
			xbeePacket = xbeePacketsQueue.getFirstDataPacketFrom(remoteXBeeDevice, timeout);
		else
			xbeePacket = xbeePacketsQueue.getFirstDataPacket(timeout);
		
		if (xbeePacket == null)
			return null;
		
		// Obtain the source address and data from the packet.
		RemoteXBeeDevice remoteDevice;
		byte[] data;
		
		APIFrameType packetType = ((XBeeAPIPacket)xbeePacket).getFrameType();
		switch (packetType) {
		case RECEIVE_PACKET:
			remoteDevice = new RemoteXBeeDevice(this, ((ReceivePacket)xbeePacket).get64bitSourceAddress());
			data = ((ReceivePacket)xbeePacket).getRFData();
			break;
		case RX_16:
			remoteDevice = new RemoteRaw802Device(this, ((RX16Packet)xbeePacket).get16bitSourceAddress());
			data = ((RX16Packet)xbeePacket).getRFData();
			break;
		case RX_64:
			remoteDevice = new RemoteXBeeDevice(this, ((RX64Packet)xbeePacket).get64bitSourceAddress());
			data = ((RX64Packet)xbeePacket).getRFData();
			break;
		default:
			return null;
		}
		
		// TODO: The remote XBee device should be retrieved from the XBee Network (contained 
		// in the xbeeDevice variable). If the network does not contain such remote device, 
		// then it should be instantiated and added there.
		
		// Create and return the XBee message.
		return new XBeeMessage(remoteDevice, data, ((XBeeAPIPacket)xbeePacket).isBroadcast());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#toString()
	 */
	@Override
	public String toString() {
		String id = getNodeID();
		if (id == null)
			id = "";
		String addr64 = get64BitAddress() == null ? "" : get64BitAddress().toString();
		
		if (id.length() == 0 && addr64.length() == 0)
			return super.toString();
		
		StringBuilder message = new StringBuilder(super.toString());
		message.append(addr64);
		if (id.length() > 0) {
			message.append(" (");
			message.append(id);
			message.append(")");
		}
		message.append(" - ");
		
		return message.toString();
	}
}

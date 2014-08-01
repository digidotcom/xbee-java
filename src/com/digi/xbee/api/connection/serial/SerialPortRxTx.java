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
package com.digi.xbee.api.connection.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.CommPortOwnershipListener;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.exceptions.InterfaceInUseException;
import com.digi.xbee.api.exceptions.InvalidConfigurationException;
import com.digi.xbee.api.exceptions.InvalidInterfaceException;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;

public class SerialPortRxTx extends AbstractSerialPort implements SerialPortEventListener, CommPortOwnershipListener {
	
	// Variables
	private RXTXPort serialPort;
	
	private InputStream inputStream;
	
	private OutputStream outputStream;
	
	private Thread breakThread;
	
	private boolean breakEnabled = false;
	
	private CommPortIdentifier portIdentifier = null;
	
	private Logger logger;
	
	/**
	 * Class constructor. Instances a new object of type SerialPort with
	 * the given parameters.
	 * 
	 * @param port Serial port name to use.
	 * @param parameters Serial port parameters.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code parameters == null}.
	 * 
	 * @see SerialPortParameters
	 */
	public SerialPortRxTx(String port, SerialPortParameters parameters) {
		super(port, parameters, DEFAULT_PORT_TIMEOUT);
		this.logger = LoggerFactory.getLogger(SerialPortRxTx.class);
	}
	
	/**
	 * Class constructor. Instances a new object of type SerialPort with
	 * the given parameters.
	 * 
	 * @param port Serial port name to use.
	 * @param parameters Serial port parameters.
	 * @param receiveTimeout Serial port receive timeout in milliseconds.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code parameters == null}.
	 * @throws IllegalArgumentException if {@code receiveTimeout < 0}.
	 *
	 * @see SerialPortParameters
	 */
	public SerialPortRxTx(String port, SerialPortParameters parameters, int receiveTimeout) {
		super(port, parameters, receiveTimeout);
		this.logger = LoggerFactory.getLogger(SerialPortRxTx.class);
	}
	
	/**
	 * Class constructor. Instances a new object of type SerialPort with
	 * the given parameters.
	 * 
	 * @param port Serial port name to use.
	 * @param baudRate Serial port baud rate, the rest of parameters will be set by default.
	 * 
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see AbstractSerialPort#DEFAULT_DATA_BITS
	 * @see AbstractSerialPort#DEFAULT_FLOW_CONTROL
	 * @see AbstractSerialPort#DEFAULT_PARITY
	 * @see AbstractSerialPort#DEFAULT_STOP_BITS
	 * @see AbstractSerialPort#DEFAULT_PORT_TIMEOUT
	 */
	public SerialPortRxTx(String port, int baudRate) {
		super(port, baudRate, DEFAULT_PORT_TIMEOUT);
		this.logger = LoggerFactory.getLogger(SerialPortRxTx.class);
	}
	
	/**
	 * Class constructor. Instances a new object of type SerialPort with
	 * the given parameters.
	 * 
	 * @param port Serial port name to use.
	 * @param baudRate Serial port baud rate, the rest of parameters will be set by default.
	 * @param receiveTimeout Serial port receive timeout in milliseconds.
	 * 
	 * @throws NullPointerException if {@code port == null}.
	 * @throws IllegalArgumentException if {@code receiveTimeout < 0}.
	 * 
	 * @see AbstractSerialPort#DEFAULT_DATA_BITS
	 * @see AbstractSerialPort#DEFAULT_FLOW_CONTROL
	 * @see AbstractSerialPort#DEFAULT_PARITY
	 * @see AbstractSerialPort#DEFAULT_STOP_BITS
	 */
	public SerialPortRxTx(String port, int baudRate, int receiveTimeout) {
		super(port, baudRate, receiveTimeout);
		this.logger = LoggerFactory.getLogger(SerialPortRxTx.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.XBeeInterface#open()
	 */
	public void open() throws InvalidInterfaceException, InterfaceInUseException, InvalidConfigurationException {
		// Check that the given serial port exists.
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(port);
		} catch (NoSuchPortException e) {
			throw new InvalidInterfaceException("No such port: " + port);
		}
		try {
			// Get the serial port.
			serialPort = (RXTXPort)portIdentifier.open(PORT_ALIAS + " " + port, receiveTimeout);
			// Set port as connected.
			connectionOpen = true;
			// Configure the port.
			if (parameters == null)
				parameters = new SerialPortParameters(baudRate, DEFAULT_DATA_BITS, DEFAULT_STOP_BITS, DEFAULT_PARITY, DEFAULT_FLOW_CONTROL);
			serialPort.setSerialPortParams(baudRate, parameters.dataBits, parameters.stopBits, parameters.parity);
			serialPort.setFlowControlMode(parameters.flowControl);
			
			serialPort.enableReceiveTimeout(receiveTimeout);
			
			// Set the port ownership.
			portIdentifier.addPortOwnershipListener(this);
			
			// Initialize input and output streams before setting the listener.
			inputStream = serialPort.getInputStream();
			outputStream = serialPort.getOutputStream();
			// Activate data received event.
			serialPort.notifyOnDataAvailable(true);
			// Register serial port event listener to be notified when data is available.
			serialPort.addEventListener(this);
		} catch (PortInUseException e) {
			throw new InterfaceInUseException("Port " + port + " is already in use by other application(s)");
		} catch (UnsupportedCommOperationException e) {
			throw new InvalidConfigurationException(e.getMessage());
		} catch (TooManyListenersException e) {
			throw new InvalidConfigurationException(e.getMessage());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.XBeeInterface#close()
	 */
	public void close() {
		try {
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
			if (outputStream != null) {
				outputStream.close();
				outputStream = null;
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		if (serialPort != null) {
			try {
				serialPort.notifyOnDataAvailable(false);
				serialPort.removeEventListener();
				portIdentifier.removePortOwnershipListener(this);
				Thread closeThread = new Thread("Closing thread for " + port) {
					public void run() {
						synchronized (serialPort) {
							serialPort.close();
							serialPort = null;
							connectionOpen = false;
						}
					};
				};
				closeThread.start();
			} catch (Exception e) { }
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see purejavacomm.SerialPortEventListener#serialEvent(purejavacomm.SerialPortEvent)
	 */
	public void serialEvent(SerialPortEvent event) {
		// Listen only to data available event.
		switch (event.getEventType()) {
		case SerialPortEvent.DATA_AVAILABLE:
			// Check if serial device has been disconnected or not.
			try {
				getInputStream().available();
			} catch (Exception e) {
				// Serial device has been disconnected.
				close();
				synchronized (this) {
					this.notify();
				}
				break;
			}
			// Notify data is available by waking up the read thread.
			try {
				if (getInputStream().available() > 0) {
					synchronized (this) {
						this.notify();
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			break;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.AbstractXBeeSerialPort#setBreak(boolean)
	 */
	public void setBreak(boolean enabled) {
		breakEnabled = enabled;
		if(breakEnabled){
			if (breakThread == null) {
				breakThread = new Thread() {
					public void run() {
						while (breakEnabled && serialPort != null)
							serialPort.sendBreak(100);
					};
				};
				breakThread.start();
			}
		} else {
			if (breakThread != null)
				breakThread.interrupt();
			breakThread = null;
			serialPort.sendBreak(0);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.XBeeInterface#getInputStream()
	 */
	public InputStream getInputStream() {
		return inputStream;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.XBeeInterface#getOutputStream()
	 */
	public OutputStream getOutputStream() {
		return outputStream;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.AbstractXBeeSerialPort#setReadTimeout(int)
	 */
	public void setReadTimeout(int timeout) {
		serialPort.disableReceiveTimeout();
		serialPort.enableReceiveTimeout(timeout);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.AbstractXBeeSerialPort#getReadTimeout()
	 */
	public int getReadTimeout() {
		return serialPort.getReceiveTimeout();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.AbstractXBeeSerialPort#setDTR(boolean)
	 */
	public void setDTR(boolean state) {
		serialPort.setDTR(state);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.AbstractXBeeSerialPort#setRTS(boolean)
	 */
	public void setRTS(boolean state) {
		serialPort.setRTS(state);
	}
	
	@Override
	/**
	 * @throws IllegalArgumentException if {@code baudRate < 0} or
	 *                                  if {@code dataBits < 0} or
	 *                                  if {@code stopBits < 0} or
	 *                                  if {@code parity < 0} or
	 *                                  if {@code flowControl < 0}.
	 */
	public void setPortParameters(int baudRate, int dataBits, int stopBits,
			int parity, int flowControl) throws OperationNotSupportedException {
		parameters = new SerialPortParameters(baudRate, dataBits, stopBits, parity, flowControl);
		
		if (serialPort != null) {
			try {
				serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);
				serialPort.setFlowControlMode(flowControl);
			} catch (UnsupportedCommOperationException e) {
				throw new OperationNotSupportedException(e.getMessage());
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.AbstractXBeeSerialPort#sendBreak(int)
	 */
	public void sendBreak(int duration) {
		if (serialPort != null)
			serialPort.sendBreak(duration);
	}
	
	/*
	 * (non-Javadoc)
	 * @see gnu.io.CommPortOwnershipListener#ownershipChange(int)
	 */
	public void ownershipChange(int nType) {
		switch (nType) {
		case CommPortOwnershipListener.PORT_OWNERSHIP_REQUESTED:
			onSerialOwnershipRequested(null);
			break;
		}
	}
	
	/**
	 * Releases the port on any ownership request in the same application instance.
	 * 
	 * @param data The port requester.
	 */
	private void onSerialOwnershipRequested(Object data) {
		try {
			throw new Exception();
		} catch (Exception e) {
			StackTraceElement[] elems = e.getStackTrace();
			String requester = elems[elems.length - 4].getClassName();
			synchronized (this) {
				this.notify();
			}
			close();
			String myPackage = this.getClass().getPackage().getName();
			if (requester.startsWith(myPackage))
				requester = "another AT connection";
			logger.warn("Connection for port {} canceled due to ownership request from {}.", port, requester);
		}
	}
	
	/**
	 * Retrieves the list of serial ports of the PC.
	 * 
	 * @return List of available serial ports.
	 */
	public static String[] listSerialPorts() {
		ArrayList<String> serialPorts = new ArrayList<String>();
		
		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> comPorts = CommPortIdentifier.getPortIdentifiers();
		if (comPorts == null)
			return serialPorts.toArray(new String[serialPorts.size()]);
		
		while (comPorts.hasMoreElements()) {
			CommPortIdentifier identifier = (CommPortIdentifier)comPorts.nextElement();
			if (identifier == null)
				continue;
			String strName = identifier.getName();
			serialPorts.add(strName);
		}
		return serialPorts.toArray(new String[serialPorts.size()]);
	}
	
	/**
	 * Retrieves the list of serial ports of the PC with description.
	 * 
	 * @return List of available serial ports with their description.
	 */
	public static ArrayList<SerialPortInfo> listSerialPortsInfo() {
		ArrayList<SerialPortInfo> ports = new ArrayList<SerialPortInfo>();
		
		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> comPorts = CommPortIdentifier.getPortIdentifiers();
		if (comPorts == null)
			return ports;
		
		while (comPorts.hasMoreElements()) {
			CommPortIdentifier identifier = (CommPortIdentifier)comPorts.nextElement();
			if (identifier == null)
				continue;
			ports.add(new SerialPortInfo(identifier.getName()));
		}
		return ports;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.AbstractXBeeSerialPort#isCTS()
	 */
	public boolean isCTS() {
		return serialPort.isCTS();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.AbstractXBeeSerialPort#isDSR()
	 */
	public boolean isDSR() {
		return serialPort.isDSR();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.AbstractXBeeSerialPort#isCD()
	 */
	public boolean isCD() {
		return serialPort.isCD();
	}
}

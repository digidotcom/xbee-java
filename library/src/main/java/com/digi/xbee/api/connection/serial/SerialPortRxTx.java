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

import com.digi.xbee.api.exceptions.ConnectionException;
import com.digi.xbee.api.exceptions.InterfaceInUseException;
import com.digi.xbee.api.exceptions.InvalidConfigurationException;
import com.digi.xbee.api.exceptions.InvalidInterfaceException;
import com.digi.xbee.api.exceptions.PermissionDeniedException;

/**
 * This class represents a serial port using the RxTx library to communicate
 * with it.
 */
public class SerialPortRxTx extends AbstractSerialPort implements SerialPortEventListener, CommPortOwnershipListener {
	
	// Variables.
	private final Object lock = new Object();
	
	private RXTXPort serialPort;
	
	private InputStream inputStream;
	
	private OutputStream outputStream;
	
	private Thread breakThread;
	
	private boolean breakEnabled = false;
	
	private CommPortIdentifier portIdentifier = null;
	
	private Logger logger;
	
	/**
	 * Class constructor. Instances a new {@code SerialPortRxTx} object using
	 * the given parameters.
	 * 
	 * @param port Serial port name to use.
	 * @param parameters Serial port parameters.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code parameters == null}.
	 * 
	 * @see #SerialPortRxTx(String, int)
	 * @see #SerialPortRxTx(String, int, int)
	 * @see #SerialPortRxTx(String, SerialPortParameters, int)
	 * @see SerialPortParameters
	 */
	public SerialPortRxTx(String port, SerialPortParameters parameters) {
		this(port, parameters, DEFAULT_PORT_TIMEOUT);
	}
	
	/**
	 * Class constructor. Instances a new {@code SerialPortRxTx} object using
	 * the given parameters.
	 * 
	 * @param port Serial port name to use.
	 * @param parameters Serial port parameters.
	 * @param receiveTimeout Serial port receive timeout in milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code receiveTimeout < 0}.
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code parameters == null}.
	 * 
	 * @see #SerialPortRxTx(String, int)
	 * @see #SerialPortRxTx(String, int, int)
	 * @see #SerialPortRxTx(String, SerialPortParameters)
	 * @see SerialPortParameters
	 */
	public SerialPortRxTx(String port, SerialPortParameters parameters, int receiveTimeout) {
		super(port, parameters, receiveTimeout);
		this.logger = LoggerFactory.getLogger(SerialPortRxTx.class);
	}
	
	/**
	 * Class constructor. Instances a new {@code SerialPortRxTx} object using
	 * the given parameters.
	 * 
	 * @param port Serial port name to use.
	 * @param baudRate Serial port baud rate, the rest of parameters will be 
	 *                 set by default.
	 * 
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see #DEFAULT_DATA_BITS
	 * @see #DEFAULT_FLOW_CONTROL
	 * @see #DEFAULT_PARITY
	 * @see #DEFAULT_STOP_BITS
	 * @see #DEFAULT_PORT_TIMEOUT
	 * @see #SerialPortRxTx(String, int, int)
	 * @see #SerialPortRxTx(String, SerialPortParameters)
	 * @see #SerialPortRxTx(String, SerialPortParameters, int)
	 * @see SerialPortParameters
	 */
	public SerialPortRxTx(String port, int baudRate) {
		this(port, baudRate, DEFAULT_PORT_TIMEOUT);
	}
	
	/**
	 * Class constructor. Instances a new {@code SerialPortRxTx} object using
	 * the given parameters.
	 * 
	 * @param port Serial port name to use.
	 * @param baudRate Serial port baud rate, the rest of parameters will be 
	 *                 set by default.
	 * @param receiveTimeout Serial port receive timeout in milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code receiveTimeout < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see #DEFAULT_DATA_BITS
	 * @see #DEFAULT_FLOW_CONTROL
	 * @see #DEFAULT_PARITY
	 * @see #DEFAULT_STOP_BITS
	 * @see #SerialPortRxTx(String, int)
	 * @see #SerialPortRxTx(String, SerialPortParameters)
	 * @see #SerialPortRxTx(String, SerialPortParameters, int)
	 * @see SerialPortParameters
	 */
	public SerialPortRxTx(String port, int baudRate, int receiveTimeout) {
		super(port, baudRate, receiveTimeout);
		this.logger = LoggerFactory.getLogger(SerialPortRxTx.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#open()
	 */
	@Override
	public void open() throws InterfaceInUseException, InvalidInterfaceException, InvalidConfigurationException, PermissionDeniedException {
		// Check that the given serial port exists.
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(port);
		} catch (NoSuchPortException e) {
			throw new InvalidInterfaceException("No such port: " + port, e);
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
			throw new InterfaceInUseException("Port " + port + " is already in use by other application(s)", e);
		} catch (UnsupportedCommOperationException e) {
			throw new InvalidConfigurationException(e.getMessage(), e);
		} catch (TooManyListenersException e) {
			throw new InvalidConfigurationException(e.getMessage(), e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#close()
	 */
	@Override
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
		synchronized (lock) {
			if (serialPort != null) {
				try {
					serialPort.notifyOnDataAvailable(false);
					serialPort.removeEventListener();
					portIdentifier.removePortOwnershipListener(this);
					serialPort.close();
					serialPort = null;
					connectionOpen = false;
				} catch (Exception e) { }
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see gnu.io.SerialPortEventListener#serialEvent(gnu.io.SerialPortEvent)
	 */
	@Override
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
	@Override
	public String toString() {
		return super.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.serial.AbstractSerialPort#setBreak(boolean)
	 */
	@Override
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
	 * @see com.digi.xbee.api.connection.IConnectionInterface#getInputStream()
	 */
	@Override
	public InputStream getInputStream() {
		return inputStream;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.serial.AbstractSerialPort#setReadTimeout(int)
	 */
	@Override
	public void setReadTimeout(int timeout) {
		serialPort.disableReceiveTimeout();
		serialPort.enableReceiveTimeout(timeout);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.serial.AbstractSerialPort#getReadTimeout()
	 */
	@Override
	public int getReadTimeout() {
		return serialPort.getReceiveTimeout();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.serial.AbstractSerialPort#setDTR(boolean)
	 */
	@Override
	public void setDTR(boolean state) {
		serialPort.setDTR(state);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.serial.AbstractSerialPort#setRTS(boolean)
	 */
	@Override
	public void setRTS(boolean state) {
		serialPort.setRTS(state);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.serial.AbstractSerialPort#setPortParameters(int, int, int, int, int)
	 */
	@Override
	public void setPortParameters(int baudRate, int dataBits, int stopBits,
			int parity, int flowControl) throws InvalidConfigurationException, ConnectionException {
		parameters = new SerialPortParameters(baudRate, dataBits, stopBits, parity, flowControl);
		
		if (serialPort != null) {
			try {
				serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);
				serialPort.setFlowControlMode(flowControl);
			} catch (UnsupportedCommOperationException e) {
				throw new InvalidConfigurationException(e.getMessage(), e);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.serial.AbstractSerialPort#sendBreak(int)
	 */
	@Override
	public void sendBreak(int duration) {
		if (serialPort != null)
			serialPort.sendBreak(duration);
	}
	
	/*
	 * (non-Javadoc)
	 * @see gnu.io.CommPortOwnershipListener#ownershipChange(int)
	 */
	@Override
	public void ownershipChange(int nType) {
		switch (nType) {
		case CommPortOwnershipListener.PORT_OWNERSHIP_REQUESTED:
			onSerialOwnershipRequested(null);
			break;
		}
	}
	
	/**
	 * Releases the port on any ownership request in the same application 
	 * instance.
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
	 * Retrieves the list of available serial ports in the system.
	 * 
	 * @return List of available serial ports.
	 * 
	 * @see #listSerialPortsInfo()
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
	 * Retrieves the list of available serial ports with their information.
	 * 
	 * @return List of available serial ports with their information.
	 * 
	 * @see #listSerialPorts()
	 * @see SerialPortInfo
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
	 * @see com.digi.xbee.api.connection.serial.AbstractSerialPort#isCTS()
	 */
	@Override
	public boolean isCTS() {
		return serialPort.isCTS();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.serial.AbstractSerialPort#isDSR()
	 */
	@Override
	public boolean isDSR() {
		return serialPort.isDSR();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.serial.AbstractSerialPort#isCD()
	 */
	@Override
	public boolean isCD() {
		return serialPort.isCD();
	}
}

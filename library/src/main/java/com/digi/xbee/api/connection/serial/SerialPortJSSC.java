/**
 * Copyright (c) 2014-2015 Digi International Inc., All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of
 * the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api.connection.serial;

import jssc.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.exceptions.ConnectionException;
import com.digi.xbee.api.exceptions.InterfaceInUseException;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidConfigurationException;
import com.digi.xbee.api.exceptions.InvalidInterfaceException;
import com.digi.xbee.api.exceptions.PermissionDeniedException;
import com.digi.xbee.api.utils.HexUtils;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 * This class represents a serial port using the jSSC library to communicate with it.
 */
public class SerialPortJSSC extends AbstractSerialPort implements SerialPortEventListener {

	// Variables.
	private final Object lock = new Object();

	private SerialPort serialPort;

//	private InputStream inputStream;
//	private OutputStream outputStream;
//	private Thread breakThread;
	private boolean breakEnabled = false;

	private PipedOutputStream serialInputWriter;
	private PipedInputStream serialInput;

	private PipedOutputStream serialOuput;
	private PipedInputStream serialOutputReader;
	private SerialPortWriter writerThread;

//	private CommPortIdentifier portIdentifier = null;
	private Logger logger;

	/**
	 * Class constructor. Instances a new {@code SerialPortJSSC} object using the given parameters.
	 *
	 * @param port Serial port name to use.
	 * @param parameters Serial port parameters.
	 *
	 * @throws NullPointerException if {@code port == null} or if {@code parameters == null}.
	 *
	 * @see #SerialPortJSSC(String, int)
	 * @see #SerialPortJSSC(String, int, int)
	 * @see #SerialPortJSSC(String, SerialPortParameters, int)
	 * @see SerialPortParameters
	 */
	public SerialPortJSSC(String port, SerialPortParameters parameters) {
		this(port, parameters, DEFAULT_PORT_TIMEOUT);
	}

	/**
	 * Class constructor. Instances a new {@code SerialPortJSSC} object using the given parameters.
	 *
	 * @param port Serial port name to use.
	 * @param parameters Serial port parameters.
	 * @param receiveTimeout Serial port receive timeout in milliseconds.
	 *
	 * @throws IllegalArgumentException if {@code receiveTimeout < 0}.
	 * @throws NullPointerException if {@code port == null} or if {@code parameters == null}.
	 *
	 * @see #SerialPortJSSC(String, int)
	 * @see #SerialPortJSSC(String, int, int)
	 * @see #SerialPortJSSC(String, SerialPortParameters)
	 * @see SerialPortParameters
	 */
	public SerialPortJSSC(String port, SerialPortParameters parameters, int receiveTimeout) {
		super(port, parameters, receiveTimeout);
		this.logger = LoggerFactory.getLogger(SerialPortJSSC.class);
	}

	/**
	 * Class constructor. Instances a new {@code SerialPortJSSC} object using the given parameters.
	 *
	 * @param port Serial port name to use.
	 * @param baudRate Serial port baud rate, the rest of parameters will be set by default.
	 *
	 * @throws NullPointerException if {@code port == null}.
	 *
	 * @see #DEFAULT_DATA_BITS
	 * @see #DEFAULT_FLOW_CONTROL
	 * @see #DEFAULT_PARITY
	 * @see #DEFAULT_STOP_BITS
	 * @see #DEFAULT_PORT_TIMEOUT
	 * @see #SerialPortJSSC(String, int, int)
	 * @see #SerialPortJSSC(String, SerialPortParameters)
	 * @see #SerialPortJSSC(String, SerialPortParameters, int)
	 * @see SerialPortParameters
	 */
	public SerialPortJSSC(String port, int baudRate) {
		this(port, baudRate, DEFAULT_PORT_TIMEOUT);
	}

	/**
	 * Class constructor. Instances a new {@code SerialPortJSSC} object using the given parameters.
	 *
	 * @param port Serial port name to use.
	 * @param baudRate Serial port baud rate, the rest of parameters will be set by default.
	 * @param receiveTimeout Serial port receive timeout in milliseconds.
	 *
	 * @throws IllegalArgumentException if {@code receiveTimeout < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 *
	 * @see #DEFAULT_DATA_BITS
	 * @see #DEFAULT_FLOW_CONTROL
	 * @see #DEFAULT_PARITY
	 * @see #DEFAULT_STOP_BITS
	 * @see #SerialPortJSSC(String, int)
	 * @see #SerialPortJSSC(String, SerialPortParameters)
	 * @see #SerialPortJSSC(String, SerialPortParameters, int)
	 * @see SerialPortParameters
	 */
	public SerialPortJSSC(String port, int baudRate, int receiveTimeout) {
		super(port, baudRate, receiveTimeout);
		this.logger = LoggerFactory.getLogger(SerialPortJSSC.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#open()
	 */
	@Override
	public void open() throws InterfaceInUseException, InvalidInterfaceException, InvalidConfigurationException, PermissionDeniedException {
		logger.debug("Openning port " + port);
		try {
			// Get the serial port.
			serialPort = new SerialPort(port);
			serialPort.openPort();
			// Set port as connected.
			connectionOpen = true;
			// Configure the port.
			if (parameters == null) {
				parameters = new SerialPortParameters(baudRate, DEFAULT_DATA_BITS, DEFAULT_STOP_BITS, DEFAULT_PARITY, DEFAULT_FLOW_CONTROL);
			}
			serialPort.setParams(baudRate, parameters.dataBits, parameters.stopBits, parameters.parity);
			serialPort.setFlowControlMode(parameters.flowControl);

			try {
				serialInputWriter = new PipedOutputStream();
				serialInput = new PipedInputStream(serialInputWriter);

				serialOuput = new PipedOutputStream();
				serialOutputReader = new PipedInputStream(serialOuput);

				writerThread = new SerialPortWriter(serialOutputReader, serialPort);
				writerThread.start();
			} catch (IOException ex) {
				logger.error("Cannot create pipes !", ex);
			}

			// Register serial port event listener to be notified when data is available.
			serialPort.addEventListener(this);
			logger.debug("Port {} opened ", port);

		} catch (SerialPortException ex) {
			//FIXME throw the right exception
//			throw new InvalidConfigurationException(e.getMessage(), e);
//			throw new InterfaceInUseException("Port " + port + " is already in use by other application(s)", e);
			logger.error("Error while opening serial port", ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#close()
	 */
	@Override
	public void close() {
		try {
			writerThread.interrupt();
			writerThread = null;

			if (serialInput != null) {
				serialInput.close();
				serialInput = null;
			}
			if (serialOuput != null) {
				serialOuput.close();
				serialOuput = null;
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		synchronized (lock) {
			if (serialPort != null) {
				try {
//					serialPort.notifyOnDataAvailable(false);
					serialPort.removeEventListener();
//					portIdentifier.removePortOwnershipListener(this);
					serialPort.closePort();
					serialPort = null;
					connectionOpen = false;
				} catch (Exception e) {
				}
			}
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
		//FIXME
//		if (breakEnabled) {
//			if (breakThread == null) {
//				breakThread = new Thread() {
//					public void run() {
//						while (breakEnabled && serialPort != null) {
//							serialPort.sendBreak(100);
//						}
//					}
//				;
//				};
//				breakThread.start();
//			}
//		} else {
//			if (breakThread != null) {
//				breakThread.interrupt();
//			}
//			breakThread = null;
//			serialPort.sendBreak(0);
//		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#getInputStream()
	 */
	@Override
	public InputStream getInputStream() {
		return serialInput;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		return serialOuput;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.serial.AbstractSerialPort#setReadTimeout(int)
	 */
	@Override
	public void setReadTimeout(int timeout) {
		//FIXME not possible in jSSC
//		serialPort.disableReceiveTimeout();
//		serialPort.enableReceiveTimeout(timeout);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.serial.AbstractSerialPort#getReadTimeout()
	 */
	@Override
	public int getReadTimeout() {
		//FIXME not possible in jSSC
		return -1;
//		return serialPort.getReceiveTimeout();
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.serial.AbstractSerialPort#setDTR(boolean)
	 */
	@Override
	public void setDTR(boolean state) {
		try {
			serialPort.setDTR(state);
		} catch (SerialPortException ex) {
			logger.error("Cannot set DTR to " + state, ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.serial.AbstractSerialPort#setRTS(boolean)
	 */
	@Override
	public void setRTS(boolean state) {
		try {
			serialPort.setRTS(state);
		} catch (SerialPortException ex) {
			logger.error("Cannot set RTS to " + state, ex);
		}
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
				serialPort.setParams(baudRate, dataBits, stopBits, parity);
				serialPort.setFlowControlMode(flowControl);
			} catch (SerialPortException ex) {
				throw new InvalidConfigurationException(ex.getMessage(), ex);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.serial.AbstractSerialPort#sendBreak(int)
	 */
	@Override
	public void sendBreak(int duration) {
		//FIXME sendBreak not supported by jSSC
//		if (serialPort != null) {
//			serialPort.sendBreak(duration);
//		}
	}

	/**
	 * Retrieves the list of available serial ports in the system.
	 *
	 * @return List of available serial ports.
	 *
	 * @see #listSerialPortsInfo()
	 */
	public static String[] listSerialPorts() {
		return SerialPortList.getPortNames();
	}

	/**
	 * Retrieves the list of available serial ports with their information.
	 *
	 * @return List of available serial ports with their information.
	 *
	 * @see #listSerialPorts()
	 * @see SerialPortInfo
	 */
	public static List<SerialPortInfo> listSerialPortsInfo() {
		List<SerialPortInfo> ports = new ArrayList<SerialPortInfo>();
		for (String p : listSerialPorts()) {
			ports.add(new SerialPortInfo(p));
		}
		return ports;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.serial.AbstractSerialPort#isCTS()
	 */
	@Override
	public boolean isCTS() {
		try {
			return serialPort.isCTS();
		} catch (SerialPortException ex) {
			throw new InterfaceNotOpenException("Cannot define is port is CTS", ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.serial.AbstractSerialPort#isDSR()
	 */
	@Override
	public boolean isDSR() {
		try {
			return serialPort.isDSR();
		} catch (SerialPortException ex) {
			throw new InterfaceNotOpenException("Cannot define is port is DSR", ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.serial.AbstractSerialPort#isCD()
	 */
	@Override
	public boolean isCD() {
		//FIXME not supported in jSSC
		return true;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		logger.debug("new serial event");
		if (event.isRXCHAR()) {
			try {
				final byte[] data = serialPort.readBytes();
				logger.debug("Data received : {}", HexUtils.byteArrayToHexString(data));
				serialInputWriter.write(data);
			} catch (IOException ex) {
				logger.error("Cannot read bytes from port " + ex);
			} catch (SerialPortException ex) {
				logger.error("Cannot read bytes from port " + ex);
			}

			synchronized (this) {
				this.notify();
			}
		}
	}

	public class SerialPortWriter extends Thread {

		private final InputStream input;
		private final SerialPort port;

		public SerialPortWriter(InputStream input, SerialPort port) {
			super("SerialPortWriter");
			this.input = input;
			this.port = port;
		}

		@Override
		public void run() {
			logger.debug("Port writer thread started");
			try {
				int readByte = 1;
				while (readByte >= 0) {
					readByte = input.read();
					logger.debug("New byte to write : {}", readByte);
					if (readByte >= 0) {
						port.writeByte((byte) readByte);
					}
				}
			} catch (IOException ex) {
				logger.error("Error while reading output pipe");
			} catch (SerialPortException ex) {
				logger.error("Error while writing on serial port");
			}
		}

	};
}

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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.exceptions.ConnectionException;
import com.digi.xbee.api.exceptions.InvalidConfigurationException;

/**
 * Abstract class that provides common functionality to work with serial ports.
 */
public abstract class AbstractSerialPort implements IConnectionInterface {
	
	// Constants
	/**
	 * Default receive timeout: {@value} seconds.
	 * 
	 * <p>When the specified number of milliseconds have elapsed, read will 
	 * return immediately.</p>
	 */
	public static final int DEFAULT_PORT_TIMEOUT = 10;
	
	/**
	 * Default number of data bits: {@value}.
	 */
	public static final int DEFAULT_DATA_BITS = 8;
	
	/**
	 * Default number of stop bits: {@value}.
	 */
	public static final int DEFAULT_STOP_BITS = 1;
	
	/**
	 * Default parity: {@value} (None).
	 */
	public static final int DEFAULT_PARITY = 0;
	
	/**
	 * Default flow control: {@value} (None).
	 */
	public static final int DEFAULT_FLOW_CONTROL = 0;
	
	protected static final int FLOW_CONTROL_HW = 3;
	
	protected static final String PORT_ALIAS = "Serial Port";
	
	// Variables
	protected String port;
	
	protected int baudRate;
	protected int receiveTimeout;
	
	protected SerialPortParameters parameters;
	
	protected boolean connectionOpen = false;
	
	private Logger logger;
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code AbstractSerialPort} with the given parameters.
	 * 
	 * @param port COM port name to use.
	 * @param parameters Serial connection parameters.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code parameters == null}.
	 * 
	 * @see SerialPortParameters
	 */
	protected AbstractSerialPort(String port, SerialPortParameters parameters) {
		this(port, parameters, DEFAULT_PORT_TIMEOUT);
	}
	
	/**
	 * Class constructor. Instances a new object of type 
	 * {@code AbstractSerialPort} with the given parameters.
	 * 
	 * @param port COM port name to use.
	 * @param baudRate Serial connection baud rate, the rest of parameters will 
	 *                 be set by default.
	 * 
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see AbstractSerialPort#DEFAULT_DATA_BITS
	 * @see AbstractSerialPort#DEFAULT_FLOW_CONTROL
	 * @see AbstractSerialPort#DEFAULT_PARITY
	 * @see AbstractSerialPort#DEFAULT_STOP_BITS
	 * @see AbstractSerialPort#DEFAULT_PORT_TIMEOUT
	 */
	protected AbstractSerialPort(String port, int baudRate) {
		this(port, new SerialPortParameters(baudRate, DEFAULT_DATA_BITS, DEFAULT_STOP_BITS, DEFAULT_PARITY, DEFAULT_FLOW_CONTROL), DEFAULT_PORT_TIMEOUT);
	}
	
	/**
	 * Class constructor. Instances a new object of type 
	 * {@code AbstractSerialPort} with the given parameters.
	 * 
	 * @param port COM port name to use.
	 * @param baudRate Serial port baud rate, the rest of parameters will be 
	 *        set by default.
	 * @param receiveTimeout Receive timeout in milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code receiveTimeout < 0}.
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see AbstractSerialPort#DEFAULT_DATA_BITS
	 * @see AbstractSerialPort#DEFAULT_FLOW_CONTROL
	 * @see AbstractSerialPort#DEFAULT_PARITY
	 * @see AbstractSerialPort#DEFAULT_STOP_BITS
	 */
	protected AbstractSerialPort(String port, int baudRate, int receiveTimeout) {
		this(port, new SerialPortParameters(baudRate, DEFAULT_DATA_BITS, DEFAULT_STOP_BITS, DEFAULT_PARITY, DEFAULT_FLOW_CONTROL), receiveTimeout);
	}
	
	/**
	 * Class constructor. Instances a new object of type 
	 * {@code AbstractSerialPort} with the given parameters.
	 * 
	 * @param port COM port name to use.
	 * @param parameters Serial connection parameters.
	 * @param receiveTimeout Serial connection receive timeout in milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code receiveTimeout < 0}.
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code parameters == null}.
	 *
	 * @see SerialPortParameters
	 */
	protected AbstractSerialPort(String port, SerialPortParameters parameters, int receiveTimeout) {
		if (port == null)
			throw new NullPointerException("Serial port cannot be null");

		if (parameters == null)
			throw new NullPointerException("SerialPortParameters cannot be null");

		if (receiveTimeout < 0)
			throw new IllegalArgumentException("Receive timeout cannot be less than 0");

		this.port = port;
		this.baudRate = parameters.baudrate;
		this.receiveTimeout = receiveTimeout;
		this.parameters = parameters;
		this.logger = LoggerFactory.getLogger(AbstractSerialPort.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#isOpen()
	 */
	@Override
	public boolean isOpen() {
		return connectionOpen;
	}
	
	/**
	 * Retrieves the name of the Serial Port.
	 * 
	 * @return Port name.
	 */
	public String getPort() {
		return port;
	}
	
	/**
	 * Sets the DTR line with the given state.
	 * 
	 * @param state {@code true} to set the line status high, {@code false} to 
	 *              set it low.
	 */
	public abstract void setDTR(boolean state);
	
	/**
	 * Sets the RTS line with the given state.
	 * 
	 * @param state {@code true} to set the line status high, {@code false} to 
	 *              set it low.
	 */
	public abstract void setRTS(boolean state);
	
	/**
	 * Retrieves the state of the CTS line.
	 * 
	 * @return {@code true} if the line is high, {@code false} otherwise.
	 */
	public abstract boolean isCTS();
	
	/**
	 * Retrieves the state of the DSR line.
	 * 
	 * @return {@code true} if the line is high, {@code false} otherwise.
	 */
	public abstract boolean isDSR();
	
	/**
	 * Retrieves the state of the CD line.
	 * 
	 * @return {@code true} if the line is high, {@code false} otherwise.
	 */
	public abstract boolean isCD();
	
	/**
	 * Retrieves whether or not the port's flow control is configured in 
	 * hardware mode.
	 *  
	 * @return {@code true} if the flow control is hardware, {@code false} 
	 *         otherwise.
	 */
	public boolean isHardwareFlowControl() {
		return parameters.flowControl == FLOW_CONTROL_HW;
	}
	
	/**
	 * Sets the new parameters of the serial port.
	 * 
	 * @param baudRate The new value of baud rate.
	 * @param dataBits The new value of data bits.
	 * @param stopBits The new value of stop bits.
	 * @param parity The new value of parity.
	 * @param flowControl The new value of flow control.
	 * 
	 * @throws InvalidConfigurationException if the configuration is invalid.
	 * @throws ConnectionException if any error occurs when setting the serial 
	 *                             port parameters
	 * @throws IllegalArgumentException if {@code baudRate < 0} or
	 *                                  if {@code dataBits < 0} or
	 *                                  if {@code stopBits < 0} or
	 *                                  if {@code parity < 0} or
	 *                                  if {@code flowControl < 0}.
	 */
	public void setPortParameters(int baudRate, int dataBits, int stopBits, int parity, int flowControl) throws InvalidConfigurationException, ConnectionException {
		SerialPortParameters parameters = new SerialPortParameters(baudRate, dataBits, stopBits, parity, flowControl);
		setPortParameters(parameters);
	}
	
	/**
	 * Sets the new parameters of the serial port.
	 * 
	 * @param parameters The new serial port parameters.
	 * 
	 * @throws InvalidConfigurationException if the configuration is invalid.
	 * @throws ConnectionException if any error occurs when setting the serial 
	 *                             port parameters.
	 * @throws NullPointerException if {@code parameters == null}.
	 * 
	 * @see SerialPortParameters
	 */
	public void setPortParameters(SerialPortParameters parameters) throws InvalidConfigurationException, ConnectionException {
		if (parameters == null)
			throw new NullPointerException("Serial port parameters cannot be null.");
		
		baudRate = parameters.baudrate;
		this.parameters = parameters;
		if (isOpen()) {
			close();
			open();
		}
	}
	
	/**
	 * Enables or disables the break line.
	 * 
	 * @param enabled {@code true} to enable the Break line, {@code false} to 
	 *                disable it.
	 */
	public abstract void setBreak(boolean enabled);
	
	/**
	 * Sends a break signal of the given duration (in milliseconds).
	 * 
	 * @param duration Duration of the break signal.
	 */
	public abstract void sendBreak(int duration);
	
	/**
	 * Sets the read timeout of the serial port (in milliseconds).
	 * 
	 * @param timeout The new read timeout.
	 */
	public abstract void setReadTimeout(int timeout);
	
	/**
	 * Retrieves the read timeout of the serial port (in milliseconds).
	 * 
	 * @return The read timeout.
	 */
	public abstract int getReadTimeout();
	
	/**
	 * Purges the serial port removing all input data.
	 */
	public void purge() {
		if (getInputStream() != null) {
			try {
				byte[] availableBytes = new byte[getInputStream().available()];
				if (getInputStream().available() > 0)
					getInputStream().read(availableBytes, 0, getInputStream().available());
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Flushes the available data of the output stream.
	 */
	public void flush() {
		if (getOutputStream() != null) {
			try {
				getOutputStream().flush();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#writeData(byte[])
	 */
	@Override
	public void writeData(byte[] data) throws IOException {
		if (data == null)
			throw new NullPointerException("Data to be sent cannot be null.");
		
		if (getOutputStream() != null) {
			// Writing data in ports without any device connected and configured with 
			// hardware flow-control causes the majority of serial libraries to hang.
			
			// Before writing any data, check if the port is configured with hardware 
			// flow-control and, if so, try to write the data up to 3 times verifying 
			// that the CTS line is high (there is a device connected to the other side 
			// ready to receive data).
			if (isHardwareFlowControl()) {
				int tries = 0;
				while (tries < 3 && !isCTS()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) { }
					tries += 1;
				}
				if (isCTS()) {
					getOutputStream().write(data);
					getOutputStream().flush();
				}
			} else {
				getOutputStream().write(data);
				getOutputStream().flush();
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#writeData(byte[], int, int)
	 */
	@Override
	public void writeData(byte[] data, int offset, int length) throws IOException {
		if (data == null)
			throw new NullPointerException("Data to be sent cannot be null.");
		if (offset < 0)
			throw new IllegalArgumentException("Offset cannot be less than 0.");
		if (length < 1)
			throw new IllegalArgumentException("Length cannot be less than 0.");
		if (offset >= data.length)
			throw new IllegalArgumentException("Offset must be less than the data length.");
		if (offset + length > data.length)
			throw new IllegalArgumentException("Offset + length cannot be great than the data length.");
		
		if (getOutputStream() != null) {
			// Writing data in ports without any device connected and configured with 
			// hardware flow-control causes the majority of serial libraries to hang.
			
			// Before writing any data, check if the port is configured with hardware 
			// flow-control and, if so, try to write the data up to 3 times verifying 
			// that the CTS line is high (there is a device connected to the other side 
			// ready to receive data).
			if (isHardwareFlowControl()) {
				int tries = 0;
				while (tries < 3 && !isCTS()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) { }
					tries += 1;
				}
				if (isCTS()) {
					getOutputStream().write(data, offset, length);
					getOutputStream().flush();
				}
			} else {
				getOutputStream().write(data, offset, length);
				getOutputStream().flush();
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#readData(byte[])
	 */
	@Override
	public int readData(byte[] data) throws IOException {
		if (data == null)
			throw new NullPointerException("Buffer cannot be null.");
		
		int readBytes = 0;
		if (getInputStream() != null)
			readBytes = getInputStream().read(data);
		return readBytes;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.connection.IConnectionInterface#readData(byte[], int, int)
	 */
	@Override
	public int readData(byte[] data, int offset, int length) throws IOException {
		if (data == null)
			throw new NullPointerException("Buffer cannot be null.");
		if (offset < 0)
			throw new IllegalArgumentException("Offset cannot be less than 0.");
		if (length < 1)
			throw new IllegalArgumentException("Length cannot be less than 0.");
		if (offset >= data.length)
			throw new IllegalArgumentException("Offset must be less than the buffer length.");
		if (offset + length > data.length)
			throw new IllegalArgumentException("Offset + length cannot be great than the buffer length.");
		
		int readBytes = 0;
		if (getInputStream() != null)
			readBytes = getInputStream().read(data, offset, length);
		return readBytes;
	}
	
	/**
	 * Reads the given number of bytes from the serial port.
	 * 
	 * @param buffer The buffer where the read bytes will be placed.
	 * 
	 * @return The number of bytes read.
	 * 
	 * @throws IOException If the first byte cannot be read for any reason other than the end of the file, 
	 *                     if the input stream has been closed, or 
	 *                     if some other I/O error occurs. 
	 * @throws NullPointerException if {@code buffer == null}.
	 */
	public int readDataBlocking(byte[] buffer) throws IOException {
		if (buffer == null)
			throw new NullPointerException("Buffer cannot be null.");
		
		int readBytes = 0;
		if (getInputStream() != null)
			readBytes = getInputStream().read(buffer);
		return readBytes;
	}
	
	/**
	 * Retrieves the XBee port serial parameters.
	 * 
	 * @return The XBee port serial parameters.
	 * 
	 * @see SerialPortParameters
	 */
	public SerialPortParameters getPortParameters() {
		if (parameters != null)
			return parameters;
		return new SerialPortParameters(baudRate, DEFAULT_DATA_BITS, 
				DEFAULT_STOP_BITS, DEFAULT_PARITY, DEFAULT_FLOW_CONTROL);
	}
	
	/**
	 * Retrieves the serial port receive timeout (in milliseconds).
	 * 
	 * @return The serial port receive timeout.
	 */
	public int getReceiveTimeout() {
		return receiveTimeout;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (parameters != null) {
			String parity = "N";
			String flowControl = "N";
			if (parameters.parity == 1)
				parity = "O";
			else if (parameters.parity == 2)
				parity = "E";
			else if (parameters.parity == 3)
				parity = "M";
			else if (parameters.parity == 4)
				parity = "S";
			if (parameters.flowControl == 1 
					|| parameters.flowControl == 2
					|| parameters.flowControl == 3)
				flowControl = "H";
			else if (parameters.flowControl == 4 
					|| parameters.flowControl == 8
					|| parameters.flowControl == 12)
				flowControl = "S";
			return "[" + port + " - " + baudRate + "/" + parameters.dataBits + 
					"/"  + parity + "/" + parameters.stopBits + "/" + flowControl + "] ";
		} else
			return "[" + port + " - " + baudRate + "/8/N/1/N] ";
	}
}

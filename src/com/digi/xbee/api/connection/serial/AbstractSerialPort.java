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
import java.io.InputStream;
import java.io.OutputStream;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.exceptions.ConnectionException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.XBeeDeviceException;
import com.digi.xbee.api.exceptions.XBeeException;

public abstract class AbstractSerialPort implements IConnectionInterface {
	
	// Constants
	/**
	 * Default receive timeout: {@value} seconds.
	 * 
	 * <p>When the specified number of milliseconds have elapsed, read will 
	 * return immediately.</p>
	 */
	public static final int DEFAULT_PORT_TIMEOUT = 10;
	
	// TODO: Add the rest of JavaDoc.
	public static final int DEFAULT_DATA_BITS = 8;
	public static final int DEFAULT_STOP_BITS = 1;
	public static final int DEFAULT_PARITY = 0;
	public static final int DEFAULT_FLOW_CONTROL = 0;
	
	protected static final int FLOW_CONTROL_HW = 3;
	
	protected static final String PORT_ALIAS = "Serial Port";
	
	// Variables
	protected String port;
	
	protected int baudRate;
	protected int receiveTimeout;
	
	protected SerialPortParameters parameters;
	
	protected boolean connectionOpen = false;
	
	/**
	 * Class constructor. Instantiates a new object of type AbstractXBeeSerialPort with
	 * the given parameters.
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
	 * Class constructor. Instances a new object of type AbstractXBeeSerialPort with
	 * the given parameters.
	 * 
	 * @param port COM port name to use.
	 * @param baudRate Serial connection baud rate, the rest of parameters will be set by default.
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
	 * Class constructor. Instances a new object of type {@code AbstractSerialPort} with
	 * the given parameters.
	 * 
	 * @param port COM port name to use.
	 * @param baudRate Serial port baud rate, the rest of parameters will be set by default.
	 * @param receiveTimeout Receive timeout in milliseconds.
	 * 
	 * @throws NullPointerException if {@code port == null}.
	 * @throws IllegalArgumentException if {@code receiveTimeout < 0}.
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
	 * Class constructor. Instances a new object of type AbstractXBeeSerialPort with
	 * the given parameters.
	 * 
	 * @param port COM port name to use.
	 * @param parameters Serial connection parameters.
	 * @param receiveTimeout Serial connection receive timeout in milliseconds.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code parameters == null}.
	 * @throws IllegalArgumentException if {@code receiveTimeout < 0}.
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
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.XBeeInterface#open()
	 */
	public abstract void open() throws ConnectionException, XBeeDeviceException;
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.XBeeInterface#close()
	 */
	public abstract void close();
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.XBeeInterface#isConnected()
	 */
	public boolean isOpen() {
		return connectionOpen;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.XBeeInterface#getInputStream()
	 */
	public abstract InputStream getInputStream();
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.XBeeInterface#getOutputStream()
	 */
	public abstract OutputStream getOutputStream();
	
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
	 * @param state True to set the line status high, false to set it low.
	 */
	public abstract void setDTR(boolean state);
	
	/**
	 * Sets the RTS line with the given state.
	 * 
	 * @param state True to set the line status high, false to set it low.
	 */
	public abstract void setRTS(boolean state);
	
	/**
	 * Retrieves the state of the CTS line.
	 * 
	 * @return True if the line is high, false otherwise.
	 */
	public abstract boolean isCTS();
	
	/**
	 * Retrieves the state of the DSR line.
	 * 
	 * @return True if the line is high, false otherwise.
	 */
	public abstract boolean isDSR();
	
	/**
	 * Retrieves the state of the CD line.
	 * 
	 * @return True if the line is high, false otherwise.
	 */
	public abstract boolean isCD();
	
	/**
	 * Retrieves whether or not the port's flow control is configured in hardware 
	 * mode.
	 *  
	 * @return True if the flow control is hardware, false otherwise.
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
	 * @throws XBeeException
	 * @throws InvalidOperatingModeException 
	 */
	public void setPortParameters(int baudRate, int dataBits, int stopBits, int parity, int flowControl) throws XBeeException, InvalidOperatingModeException {
		SerialPortParameters parameters = new SerialPortParameters(baudRate, dataBits, stopBits, parity, flowControl);
		setPortParameters(parameters);
	}
	
	/**
	 * Sets the new parameters of the serial port.
	 * 
	 * @param parameters The new serial port parameters.
	 * @throws XBeeException
	 * @throws InvalidOperatingModeException 
	 * 
	 * @throws NullPointerException if {@code parameters == null}.
	 */
	public void setPortParameters(SerialPortParameters parameters) throws XBeeException, InvalidOperatingModeException {
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
	 * @param enabled True to enable the Break line, false to disable it.
	 */
	public abstract void setBreak(boolean enabled);
	
	/**
	 * Sends a break signal of the given duration.
	 * 
	 * @param duration Duration of the break signal.
	 */
	public abstract void sendBreak(int duration);
	
	/**
	 * Sets the read timeout of the serial port.
	 * 
	 * @param timeout The new read timeout.
	 */
	public abstract void setReadTimeout(int timeout);
	
	/**
	 * Retrieves the read timeout of the serial port.
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
				e.printStackTrace();
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
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Sends the given byte array to the serial port.
	 * 
	 * @param data The byte array to be sent.
	 * @throws IOException 
	 * 
	 * @throws NullPointerException if {@code data == null}.
	 */
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
	
	/**
	 * Reads the given number of bytes from the serial port.
	 * 
	 * @param buffer The buffer where the read bytes will be placed.
	 * @param numBytes The number of bytes to read.
	 * @return The number of bytes read.
	 * @throws IOException
	 * 
	 * @throws NullPointerException if {@code buffer == null}.
	 * @throws IllegalArgumentException if {@code numBytes < 1}.
	 */
	public int readData(byte[] buffer, int numBytes) throws IOException {
		if (buffer == null)
			throw new NullPointerException("Buffer cannot be null.");
		if (numBytes < 1)
			throw new IllegalArgumentException("Bytes to read cannot be less than 1.");
		
		int readBytes = 0;
		if (getInputStream() != null)
			readBytes = getInputStream().read(buffer, 0, numBytes);
		return readBytes;
	}
	
	/**
	 * Reads the given number of bytes from the serial port.
	 * 
	 * @param buffer The buffer where the read bytes will be placed.
	 * @return The number of bytes read.
	 * @throws IOException
	 * 
	 * @throws NullPointerException if {@code buffer == null}.
	 */
	public int readData(byte[] buffer) throws IOException {
		if (buffer == null)
			throw new NullPointerException("Buffer cannot be null.");
		
		int readBytes = 0;
		if (getInputStream() != null && getInputStream().available() > 0) {
			int numBytesToRead = getInputStream().available();
			if (numBytesToRead > buffer.length)
				numBytesToRead = buffer.length;
			readBytes = getInputStream().read(buffer, 0, numBytesToRead);
		}
		return readBytes;
	}
	
	/**
	 * Reads the given number of bytes from the serial port.
	 * 
	 * @param buffer The buffer where the read bytes will be placed.
	 * @return The number of bytes read.
	 * @throws IOException
	 * 
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
	 */
	public SerialPortParameters getPortParameters() {
		if (parameters != null)
			return parameters;
		return new SerialPortParameters(baudRate, DEFAULT_DATA_BITS, 
				DEFAULT_STOP_BITS, DEFAULT_PARITY, DEFAULT_FLOW_CONTROL);
	}
	
	/**
	 * Retrieves the serial port receive timeout.
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
			return port + " - " + baudRate + "/" + parameters.dataBits + 
					"/"  + parity + "/" + parameters.stopBits + "/" + flowControl;
		} else
			return port + " - " + baudRate + "/8/N/1/N";
	}
}

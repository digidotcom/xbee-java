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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;

import com.digi.android.serial.ISerialPortEventListener;
import com.digi.android.serial.NoSuchPortException;
import com.digi.android.serial.PortInUseException;
import com.digi.android.serial.SerialPort;
import com.digi.android.serial.SerialPortEvent;
import com.digi.android.serial.SerialPortManager;
import com.digi.android.serial.UnsupportedCommOperationException;
import com.digi.xbee.api.exceptions.ConnectionException;
import com.digi.xbee.api.exceptions.InterfaceInUseException;
import com.digi.xbee.api.exceptions.InvalidConfigurationException;
import com.digi.xbee.api.exceptions.InvalidInterfaceException;
import com.digi.xbee.api.exceptions.PermissionDeniedException;

/**
 * This class represents a serial port interface making use of the Digi Android
 * Serial Port library based on the RxTx implementation.
 * 
 * @since 1.2.0
 */
public class SerialPortDigiAndroid extends AbstractSerialPort implements ISerialPortEventListener {
	
	// Variables.
	private SerialPort serialPort;
	
	private InputStream inputStream;
	
	private OutputStream outputStream;
	
	private Thread breakThread;
	
	private boolean breakEnabled = false;
	
	private Logger logger;
	
	private Context context;
	
	/**
	 * Class constructor. Instantiates a new {@code SerialPortDigiAndroid} 
	 * object using the given parameters.
	 * 
	 * @param context The Android application context.
	 * @param port Serial port name to instantiate.
	 * @param baudRate Serial port baud rate, the rest of parameters will be 
	 *                 set by default.
	 * 
	 * @throws IllegalArgumentException if {@code baudRate < 1}.
	 * @throws NullPointerException if {@code context == null} or
	 *                              if {@code port == null}.
	 * 
	 * @see #DEFAULT_DATA_BITS
	 * @see #DEFAULT_FLOW_CONTROL
	 * @see #DEFAULT_PARITY
	 * @see #DEFAULT_STOP_BITS
	 * @see #DEFAULT_PORT_TIMEOUT
	 * @see #SerialPortDigiAndroid(Context, String, int, int)
	 * @see #SerialPortDigiAndroid(Context, String, SerialPortParameters)
	 * @see #SerialPortDigiAndroid(Context, String, SerialPortParameters, int)
	 */
	public SerialPortDigiAndroid(Context context, String port, int baudRate) {
		super(port, baudRate, DEFAULT_PORT_TIMEOUT);
		
		this.context = context;
		this.logger = LoggerFactory.getLogger(SerialPortDigiAndroid.class);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code SerialPortDigiAndroid} 
	 * object using the given parameters.
	 * 
	 * @param context The Android application context.
	 * @param port Serial port name to instantiate.
	 * @param baudRate Serial port baud rate, the rest of parameters will be 
	 *                 set by default.
	 * @param receiveTimeout Serial port receive timeout in milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code baudrate < 1} or
	 *                                  if {@code receiveTimeout < 0}.
	 * @throws NullPointerException if {@code context == null} or
	 *                              if {@code port == null}.
	 * 
	 * @see #DEFAULT_DATA_BITS
	 * @see #DEFAULT_FLOW_CONTROL
	 * @see #DEFAULT_PARITY
	 * @see #DEFAULT_STOP_BITS
	 * @see #SerialPortDigiAndroid(Context, String, int)
	 * @see #SerialPortDigiAndroid(Context, String, SerialPortParameters)
	 * @see #SerialPortDigiAndroid(Context, String, SerialPortParameters, int)
	 */
	public SerialPortDigiAndroid(Context context, String port, int baudRate, int receiveTimeout) {
		super(port, baudRate, receiveTimeout);
		
		this.context = context;
		this.logger = LoggerFactory.getLogger(SerialPortDigiAndroid.class);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code SerialPortDigiAndroid} 
	 * object using the given parameters.
	 * 
	 * @param context The Android application context.
	 * @param port Serial port name to instantiate.
	 * @param parameters Serial port parameters to use.
	 * 
	 * @throws NullPointerException if {@code context == null} or
	 *                              if {@code port == null} or
	 *                              if {@code parameters == null}.
	 * 
	 * @see #SerialPortDigiAndroid(Context, String, int)
	 * @see #SerialPortDigiAndroid(Context, String, int, int)
	 * @see #SerialPortDigiAndroid(Context, String, SerialPortParameters, int)
	 * @see SerialPortParameters
	 */
	public SerialPortDigiAndroid(Context context, String port, SerialPortParameters parameters) {
		super(port, parameters, DEFAULT_PORT_TIMEOUT);
		
		this.context = context;
		this.logger = LoggerFactory.getLogger(SerialPortDigiAndroid.class);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code SerialPortDigiAndroid} 
	 * object using the given parameters.
	 * 
	 * @param context The Android application context.
	 * @param port Serial port name to instantiate.
	 * @param parameters Serial port parameters to use.
	 * @param receiveTimeout Serial port receive timeout in milliseconds.
	 * 
	 * @throws IllegalArgumentException if {@code receiveTimeout < 0}.
	 * @throws NullPointerException if {@code context == null} or
	 *                              if {@code port == null} or
	 *                              if {@code parameters == null}.
	 * 
	 * @see #SerialPortDigiAndroid(Context, String, int)
	 * @see #SerialPortDigiAndroid(Context, String, int, int)
	 * @see #SerialPortDigiAndroid(Context, String, SerialPortParameters)
	 * @see SerialPortParameters
	 */
	public SerialPortDigiAndroid(Context context, String port, SerialPortParameters parameters, int receiveTimeout) {
		super(port, parameters, receiveTimeout);
		
		this.context = context;
		this.logger = LoggerFactory.getLogger(SerialPortDigiAndroid.class);
	}
	
	@Override
	public void open() throws InterfaceInUseException, InvalidInterfaceException, InvalidConfigurationException, PermissionDeniedException {
		try {
			// Instantiate Serial Port Manager
			SerialPortManager serialPortManager = new SerialPortManager(context);
			// Get the serial port.
			serialPort = serialPortManager.openSerialPort(port);
			// Set port as connected.
			connectionOpen = true;
			// Configure the port.
			if (parameters == null)
				parameters = new SerialPortParameters(baudRate, DEFAULT_DATA_BITS, DEFAULT_STOP_BITS, DEFAULT_PARITY, DEFAULT_FLOW_CONTROL);
			serialPort.setPortParameters(baudRate, parameters.dataBits, parameters.stopBits, parameters.parity, parameters.flowControl);
			serialPort.enableReceiveTimeout(receiveTimeout);
			// Initialize input and output streams before setting the listener.
			inputStream = serialPort.getInputStream();
			outputStream = serialPort.getOutputStream();
			// Activate data received event.
			serialPort.notifyOnDataAvailable(true);
			// Register serial port event listener to be notified when data is available.
			serialPort.registerEventListener(this);
		} catch (PortInUseException e) {
			throw new InterfaceInUseException("Port " + port + " is already in use by other application(s)", e);
		} catch (UnsupportedCommOperationException e) {
			throw new InvalidConfigurationException("Invalid serial port configuration: " + port + " " + e.getMessage(), e);
		} catch (TooManyListenersException e) {
			throw new InvalidConfigurationException("Invalid serial port configuration: " + port + " " + e.getMessage(), e);
		} catch (IOException e) {
			throw new InvalidConfigurationException("Error retrieving serial port streams: " + port, e);
		} catch (NoSuchPortException e) {
			throw new InvalidInterfaceException("No such port: " + port, e);
		}
	}
	
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
		if (serialPort != null) {
			try {
				serialPort.notifyOnDataAvailable(false);
				serialPort.unregisterEventListener();
				synchronized (serialPort) {
					serialPort.close();
					serialPort = null;
					connectionOpen = false;
				}
			} catch (Exception e) { }
		}
	}
	
	@Override
	public void serialEvent(SerialPortEvent event) {
		// Listen only to data available event.
		switch (event.getEventType()) {
		case DATA_AVAILABLE:
			// Check if serial device has been disconnected or not.
			try {
				getInputStream().available();
			} catch (Exception e) {
				// Serial device has been disconnected.
				close();
				synchronized (this) {
					//System.out.println("notify");
					this.notify();
				}
				break;
			}
			// Notify data is available by waking up the read thread.
			try {
				if (getInputStream().available() > 0) {
					synchronized (this) {
						//System.out.println("notify");
						this.notify();
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
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
	
	@Override
	public InputStream getInputStream() {
		return inputStream;
	}
	
	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}
	
	@Override
	public void setReadTimeout(int timeout) {
		serialPort.disableReceiveTimeout();
		try {
			serialPort.enableReceiveTimeout(timeout);
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int getReadTimeout() {
		return serialPort.getReceiveTimeout();
	}
	
	@Override
	public void setDTR(boolean state) {
		serialPort.setDTR(state);
	}
	
	@Override
	public void setRTS(boolean state) {
		serialPort.setRTS(state);
	}
	
	@Override
	public void setPortParameters(int baudRate, int dataBits, int stopBits,
			int parity, int flowControl) throws InvalidConfigurationException, ConnectionException {
		parameters = new SerialPortParameters(baudRate, dataBits, stopBits, parity, flowControl);
		
		if (serialPort != null) {
			try {
				serialPort.setPortParameters(baudRate, dataBits, stopBits, parity, flowControl);
			} catch (UnsupportedCommOperationException e) {
				throw new InvalidConfigurationException(e.getMessage(), e);
			}
		}
	}
	
	@Override
	public void sendBreak(int duration) {
		if (serialPort != null)
			serialPort.sendBreak(duration);
	}
	
	@Override
	public boolean isCTS() {
		return serialPort.isCTS();
	}
	
	@Override
	public boolean isDSR() {
		return serialPort.isDSR();
	}
	
	@Override
	public boolean isCD() {
		return serialPort.isCD();
	}
}

package com.digi.xbee.api;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.connection.serial.SerialPortRxTx;

public class XBee {
	
	/**
	 * Retrieves a serial port connection interface with the given serial port name and baud rate.
	 * 
	 * @param port Serial port name.
	 * @param baudRate Serial port baud rate.
	 * @return The serial port connection interface.
	 * 
	 * @throws NullPointerException if {@code port == null}.
	 * 
	 * @see IConnectionInterface
	 */
	public static IConnectionInterface createConnectiontionInterface(String port, int baudRate) {
		IConnectionInterface connectionInterface = new SerialPortRxTx(port, baudRate);
		return connectionInterface;
	}
	
	/**
	 * Retrieves a serial port connection interface with the given serial port parameters.
	 * 
	 * @param port Serial port name.
	 * @param serialPortParameters Serial port parameters.
	 * @return The serial port connection interface.
	 * 
	 * @throws NullPointerException if {@code port == null} or
	 *                              if {@code serialPortParameters == null}.
	 * 
	 * @see SerialPortParameters
	 * @see IConnectionInterface
	 */
	public static IConnectionInterface createConnectiontionInterface(String port, SerialPortParameters serialPortParameters) {
		IConnectionInterface connectionInterface = new SerialPortRxTx(port, serialPortParameters);
		return connectionInterface;
	}
}

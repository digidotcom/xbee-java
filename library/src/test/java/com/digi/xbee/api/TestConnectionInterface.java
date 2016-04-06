package com.digi.xbee.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.connection.ISerialConnectionInterfaceFactory;
import com.digi.xbee.api.connection.serial.SerialPortParameters;
import com.digi.xbee.api.exceptions.InterfaceInUseException;
import com.digi.xbee.api.exceptions.InvalidConfigurationException;
import com.digi.xbee.api.exceptions.InvalidInterfaceException;
import com.digi.xbee.api.exceptions.PermissionDeniedException;

public class TestConnectionInterface implements IConnectionInterface {
	
	private static final TestConnectionInterface connectionInterface = new TestConnectionInterface();
	private static final TestSerialFactory testSerialFactory = new TestSerialFactory();
	
	private boolean isOpen = false;
	
	// Set a new TestSerialFactory as the serial connection interface factory. 
	static {
		XBee.setSerialConnectionInterfaceFactory(testSerialFactory);
	}
	
	static class TestSerialFactory implements ISerialConnectionInterfaceFactory {
		
		/*
		 * (non-Javadoc)
		 * @see com.digi.xbee.api.connection.ISerialConnectionInterfaceFactory#createInterface(String, int)
		 */
		@Override
		public IConnectionInterface createInterface(String port, int baudrate) {
			return connectionInterface;
		}
		
		/*
		 * (non-Javadoc)
		 * @see com.digi.xbee.api.connection.ISerialConnectionInterfaceFactory#createInterface(String, SerialPortParameters)
		 */
		@Override
		public IConnectionInterface createInterface(String port, SerialPortParameters serialPortParameters) {
			return connectionInterface;
		}
	}
	
	static TestConnectionInterface getInstance() {
		return connectionInterface;
	}
	
	static TestSerialFactory getTestSerialFactoryInstance() {
		return testSerialFactory;
	}
	
	@Override
	public void writeData(byte[] data, int offset, int length)
			throws IOException {
		// Do nothing.
	}
	
	@Override
	public void writeData(byte[] data) throws IOException {
		// Do nothing.
	}
	
	@Override
	public int readData(byte[] data, int offset, int length) throws IOException {
		return 0;
	}
	
	@Override
	public int readData(byte[] data) throws IOException {
		return 0;
	}
	
	@Override
	public void open() throws InterfaceInUseException,
			InvalidInterfaceException, InvalidConfigurationException,
			PermissionDeniedException {
		isOpen = true;
	}
	
	@Override
	public boolean isOpen() {
		return isOpen;
	}
	
	@Override
	public OutputStream getOutputStream() {
		return null;
	}
	
	@Override
	public InputStream getInputStream() {
		return null;
	}
	
	@Override
	public void close() {
		isOpen = false;
	}
}

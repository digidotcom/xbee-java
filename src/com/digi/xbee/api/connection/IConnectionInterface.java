package com.digi.xbee.api.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.XBeeException;

/**
 * This interface represents a protocol independent connection with an XBee device.
 * The connection can be made using sockets, serial port, etc.
 * 
 * As an important point, the class implementing this interface must call this.notify()
 * whenever new data is available to read. Not doing this will make the DataReader class
 * to wait forever for new data.
 */
public interface IConnectionInterface {

	/**
	 * Attempts to open the connection interface.
	 * 
	 * @throws XBeeException;
	 * @throws InvalidOperatingModeException;
	 */
	public void connect() throws XBeeException, InvalidOperatingModeException;
	
	/**
	 * Attempts to close the connection interface.
	 */
	public void disconnect();
	
	/**
	 * Retrieves whether the connection interface is open or not.
	 * 
	 * @return True if the connection interface is open, false otherwise.
	 */
	public boolean isConnected();
	
	/**
	 * Retrieves the connection interface input stream to read data from.
	 * 
	 * @return The connection interface input stream to read data from.
	 */
	public InputStream getInputStream();
	
	/**
	 * Retrieves the connection interface output stream to write data to.
	 * 
	 * @return The connection interface output stream to write data to.
	 */
	public OutputStream getOutputStream();
	
	/**
	 * Writes the given data in the connection interface.
	 * 
	 * @param data The data to be written in the connection interface.
	 * @throws IOException;
	 */
	public void writeData(byte[] data) throws IOException;
}

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
package com.digi.xbee.api.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.digi.xbee.api.exceptions.XBeeException;

/**
 * This interface represents a protocol independent connection with an XBee device.
 * The connection can be made using sockets, serial port, etc.
 * 
 * <p>As an important point, the class implementing this interface must call this.notify()
 * whenever new data is available to read. Not doing this will make the DataReader class
 * to wait forever for new data.<p>
 */
public interface IConnectionInterface {

	/**
	 * Attempts to open the connection interface.
	 * 
	 * @throws XBeeException if there is any error opening the connection interface.
	 */
	public void open() throws XBeeException;
	
	/**
	 * Attempts to close the connection interface.
	 */
	public void close();
	
	/**
	 * Retrieves whether the connection interface is open or not.
	 * 
	 * @return True if the connection interface is open, false otherwise.
	 */
	public boolean isOpen();
	
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
	 * @throws NullPointerException if {@code data == null}.
	 * @throws IOException if there is any problem writing in the output stream;
	 */
	public void writeData(byte[] data) throws IOException;
	
	/**
	 * Writes the given data in the connection interface.
	 * 
	 * @param data he data to be written in the connection interface.
	 * @param offset The start offset in the data.
	 * @param length The number of bytes to write.
	 * @throws NullPointerException if {@code data == null}.
	 * @throws IllegalArgumentException if {@code offset < 0} or
	 *                                  if {@code length < 1} or
	 *                                  if {@code offset >= data.length} or
	 *                                  if {@code offset + length > data.length}.
	 * @throws IOException if there is any problem writing in the output stream;
	 */
	public void writeData(byte[] data, int offset, int length) throws IOException;
	
	/**
	 * Reads data from the connection interface and stores it in the provided 
	 * byte array. Returns the number of read bytes.
	 * 
	 * @param data The byte array to store the read data.
	 * @return The number of bytes read.
	 * @throws NullPointerException if {@code data == null}.
	 * @throws IOException if there is any problem reading from the input stream;
	 */
	public int readData(byte[] data) throws IOException;
	
	/**
	 * Reads data from the connection interface and stores it in the provided 
	 * byte array. Returns the number of read bytes.
	 * 
	 * @param data The byte array to store the read data.
	 * @param offset The start offset in data array at which the data is written.
	 * @param length Maximum number of bytes to read.
	 * @return The number of bytes read.
	 * @throws NullPointerException if {@code data == null}.
	 * @throws IllegalArgumentException if {@code offset < 0} or
	 *                                  if {@code length < 1} or
	 *                                  if {@code offset >= data.length} or
	 *                                  if {@code offset + length > data.length}.
	 * @throws IOException if there is any problem reading from the input stream;
	 */
	public int readData(byte[] data, int offset, int length) throws IOException;
}

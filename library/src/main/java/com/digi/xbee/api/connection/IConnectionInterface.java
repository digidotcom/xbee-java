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
package com.digi.xbee.api.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.digi.xbee.api.exceptions.InterfaceInUseException;
import com.digi.xbee.api.exceptions.InvalidConfigurationException;
import com.digi.xbee.api.exceptions.InvalidInterfaceException;
import com.digi.xbee.api.exceptions.PermissionDeniedException;

/**
 * This interface represents a protocol independent connection with an XBee 
 * device.
 * 
 * <p>The connection can be made using sockets, serial port, etc.</p>
 * 
 * <p>As an important point, the class implementing this interface must call 
 * {@code this.notify()} whenever new data is available to read. Not doing this 
 * will make the {@code DataReader} class to wait forever for new data.</p>
 */
public interface IConnectionInterface {

	/**
	 * Attempts to open the connection interface.
	 * 
	 * @throws InterfaceInUseException if the interface is in use by other 
	 *                                 application(s).
	 * @throws InvalidConfigurationException if the configuration used to open 
	 *                                       the interface is invalid.
	 * @throws InvalidInterfaceException if the interface is invalid or does 
	 *                                   not exist.
	 * @throws PermissionDeniedException if you do not have permissions to 
	 *                                   access the interface.
	 * 
	 * @see #close()
	 * @see #isOpen()
	 */
	public void open() throws InterfaceInUseException, InvalidInterfaceException, InvalidConfigurationException, PermissionDeniedException;
	
	/**
	 * Attempts to close the connection interface.
	 * 
	 * @see #isOpen()
	 * @see #open()
	 */
	public void close();
	
	/**
	 * Returns whether the connection interface is open or not.
	 * 
	 * @return {@code true} if the connection interface is open, {@code false} 
	 *         otherwise.
	 *         
	 * @see #close()
	 * @see #open()
	 */
	public boolean isOpen();
	
	/**
	 * Returns the connection interface input stream to read data from.
	 * 
	 * @return The connection interface input stream to read data from.
	 * 
	 * @see #getOutputStream()
	 * @see java.io.InputStream
	 */
	public InputStream getInputStream();
	
	/**
	 * Returns the connection interface output stream to write data to.
	 * 
	 * @return The connection interface output stream to write data to.
	 * 
	 * @see #getInputStream()
	 * @see java.io.OutputStream
	 */
	public OutputStream getOutputStream();
	
	/**
	 * Writes the given data in the connection interface.
	 * 
	 * @param data The data to be written in the connection interface.
	 * 
	 * @throws IOException if there is any problem writing to the output stream.
	 * @throws NullPointerException if {@code data == null}.
	 * 
	 * @see #writeData(byte[], int, int)
	 */
	public void writeData(byte[] data) throws IOException;
	
	/**
	 * Writes the given data in the connection interface.
	 * 
	 * @param data The data to be written in the connection interface.
	 * @param offset The start offset in the data to write.
	 * @param length The number of bytes to write.
	 * 
	 * @throws IllegalArgumentException if {@code offset < 0} or
	 *                                  if {@code length < 1} or
	 *                                  if {@code offset >= data.length} or
	 *                                  if {@code offset + length > data.length}.
	 * @throws IOException if there is any problem writing to the output stream.
	 * @throws NullPointerException if {@code data == null}.
	 * 
	 * @see #writeData(byte[])
	 */
	public void writeData(byte[] data, int offset, int length) throws IOException;
	
	/**
	 * Reads data from the connection interface and stores it in the provided 
	 * byte array returning the number of read bytes.
	 * 
	 * @param data The byte array to store the read data.
	 * 
	 * @return The number of bytes read.
	 * 
	 * @throws IOException if there is any problem reading from the input stream.
	 * @throws NullPointerException if {@code data == null}.
	 * 
	 * @see #readData(byte[], int, int)
	 */
	public int readData(byte[] data) throws IOException;
	
	/**
	 * Reads the given number of bytes at the given offset from the connection
	 * interface and stores it in the provided byte array returning the number
	 * of read bytes.
	 * 
	 * @param data The byte array to store the read data.
	 * @param offset The start offset in data array at which the data is written.
	 * @param length Maximum number of bytes to read.
	 * 
	 * @return The number of bytes read.
	 * 
	 * @throws IllegalArgumentException if {@code offset < 0} or
	 *                                  if {@code length < 1} or
	 *                                  if {@code offset >= data.length} or
	 *                                  if {@code offset + length > data.length}.
	 * @throws IOException if there is any problem reading from the input stream.
	 * @throws NullPointerException if {@code data == null}.
	 * 
	 * @see #readData(byte[])
	 */
	public int readData(byte[] data, int offset, int length) throws IOException;
}

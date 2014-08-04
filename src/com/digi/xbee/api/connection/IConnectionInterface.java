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

import com.digi.xbee.api.exceptions.InterfaceInUseException;
import com.digi.xbee.api.exceptions.InvalidConfigurationException;
import com.digi.xbee.api.exceptions.InvalidInterfaceException;
import com.digi.xbee.api.exceptions.PermissionDeniedException;

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
	 * @throws InterfaceInUseException if the interface is in use by other application(s).
	 * @throws InvalidInterfaceException if the interface is invalid or does not exist.
	 * @throws InvalidConfigurationException if the configuration used to open the interface is invalid.
	 * @throws PermissionDeniedException if you do not have permissions to access the interface.
	 */
	public void open() throws InterfaceInUseException, InvalidInterfaceException, InvalidConfigurationException, PermissionDeniedException;
	
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
	 * @throws IOException if an I/O error occurs.
	 */
	public void writeData(byte[] data) throws IOException;
	
	// TODO: Add a read method.
}

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
package com.digi.xbee.api.exceptions;

/**
 * This exception will be thrown when trying to perform any operation over a 
 * closed interface.
 */
public class InterfaceNotOpenException extends ConnectionException {

	// Constants
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an {@code InterfaceNotOpenedException} with {@code null} as its 
	 * error detail message.
	 */
	public InterfaceNotOpenException() {
		super();
	}
	
	/**
	 * Creates an {@code InterfaceNotOpenedException} with the specified 
	 * message.
	 * 
	 * @param message The associated message.
	 */
	public InterfaceNotOpenException(String message) {
		super(message);
	}
}

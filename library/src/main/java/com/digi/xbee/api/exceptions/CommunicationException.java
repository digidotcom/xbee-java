/**
 * Copyright (c) 2014-2015 Digi International Inc.,
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
 * This exception will be thrown when any problem related to the communication
 * with the XBee device occurs.
 * 
 * @see XBeeException
 */
public class CommunicationException extends XBeeException {

	// Constants
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a {@code CommunicationException} with {@code null} as its error 
	 * detail message.
	 */
	public CommunicationException() {
		super();
	}
	
	/**
	 * Creates a {@code CommunicationException} with the specified cause.
	 * 
	 * @param cause The cause of this exception.
	 * 
	 * @see Throwable
	 */
	public CommunicationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a {@code CommunicationException} with the specified message.
	 * 
	 * @param message The associated message.
	 */
	public CommunicationException(String message) {
		super(message);
	}

	/**
	 * Creates a {@code CommunicationException} with the specified message and 
	 * cause.
	 * 
	 * @param message The associated message.
	 * @param cause The cause of this exception.
	 * 
	 * @see Throwable
	 */
	public CommunicationException(String message, Throwable cause) {
		super(message, cause);
	}
}

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
 * This exception will be thrown when there is an error parsing an API packet
 * from the input stream.
 * 
 * @see CommunicationException
 */
public class InvalidPacketException extends CommunicationException {

	// Constants
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "The XBee API packet is not properly formed.";

	/**
	 * Creates a {@code InvalidPacketException} with {@value #DEFAULT_MESSAGE} 
	 * as its error detail message.
	 */
	public InvalidPacketException() {
		super(DEFAULT_MESSAGE);
	}
	
	/**
	 * Creates a {@code InvalidPacketException} with the specified message.
	 * 
	 * @param message The associated message.
	 */
	public InvalidPacketException(String message) {
		super(message);
	}
	
	/**
	 * Creates an {@code InvalidPacketException} with the specified 
	 * message and cause.
	 * 
	 * @param message The associated message.
	 * @param cause The cause of this exception.
	 * 
	 * @see Throwable
	 */
	public InvalidPacketException(String message, Throwable cause) {
		super(message, cause);
	}
}

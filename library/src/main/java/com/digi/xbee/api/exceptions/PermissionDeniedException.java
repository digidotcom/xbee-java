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
 * This exception will be thrown when the user does not have the appropriate
 * access to the connection interface. Usually happens when the XBee device is 
 * communicating through a serial port.
 * 
 * @see ConnectionException
 */
public class PermissionDeniedException extends ConnectionException {

	// Constants
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "You don't have the required permissions to access the connection interface.";

	/**
	 * Creates a {@code PermissionDeniedException} with 
	 * {@value #DEFAULT_MESSAGE} as its error detail message.
	 */
	public PermissionDeniedException() {
		super(DEFAULT_MESSAGE);
	}
	
	/**
	 * Creates a {@code PermissionDeniedException} with the specified message.
	 * 
	 * @param message The associated message.
	 */
	public PermissionDeniedException(String message) {
		super(message);
	}
	
	/**
	 * Creates a {@code PermissionDeniedException} with the specified 
	 * message and cause.
	 * 
	 * @param message The associated message.
	 * @param cause The cause of this exception.
	 * 
	 * @see Throwable
	 */
	public PermissionDeniedException(String message, Throwable cause) {
		super(message, cause);
	}
}

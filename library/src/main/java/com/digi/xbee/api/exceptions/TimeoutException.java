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
 * This exception will be thrown when performing synchronous operations and the
 * configured time expires.
 * 
 * @see CommunicationException
 */
public class TimeoutException extends CommunicationException {

	// Constants
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "There was a timeout while executing the requested operation.";

	/**
	 * Creates a {@code TimeoutException} with {@value #DEFAULT_MESSAGE} as its 
	 * error detail message.
	 */
	public TimeoutException() {
		super(DEFAULT_MESSAGE);
	}
	
	/**
	 * Creates a {@code TimeoutException} with the specified message.
	 * 
	 * @param message The associated message.
	 */
	public TimeoutException(String message) {
		super(message);
	}
	
	/**
	 * Creates a {@code TimeoutException} with the specified message and cause.
	 * 
	 * @param message The associated message.
	 * @param cause The cause of this exception.
	 * 
	 * @see Throwable
	 */
	public TimeoutException(String message, Throwable cause) {
		super(message, cause);
	}
}

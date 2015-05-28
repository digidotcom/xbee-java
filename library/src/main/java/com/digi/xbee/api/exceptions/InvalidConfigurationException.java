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
 * This exception will be thrown when trying to open an interface with an
 * invalid configuration. Usually happens when the XBee device is 
 * communicating through a serial port.
 * 
 * @see ConnectionException
 */
public class InvalidConfigurationException extends ConnectionException {

	// Constants
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "The configuration used to open the interface is invalid.";

	/**
	 * Creates an {@code InvalidConfigurationException} with 
	 * {@value #DEFAULT_MESSAGE} as its error detail message.
	 */
	public InvalidConfigurationException() {
		super(DEFAULT_MESSAGE);
	}
	
	/**
	 * Creates an {@code InvalidConfigurationException} with the specified 
	 * message.
	 * 
	 * @param message The associated message.
	 */
	public InvalidConfigurationException(String message) {
		super(message);
	}
	
	/**
	 * Creates an {@code InvalidConfigurationException} with the specified 
	 * message and cause.
	 * 
	 * @param message The associated message.
	 * @param cause The cause of this exception.
	 * 
	 * @see Throwable
	 */
	public InvalidConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}

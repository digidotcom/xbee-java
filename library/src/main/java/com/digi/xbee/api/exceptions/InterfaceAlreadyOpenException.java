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
 * This exception will be thrown when trying to open an interface that is
 * already opened by the application.
 * 
 * @see RuntimeException
 */
public class InterfaceAlreadyOpenException extends RuntimeException {

	// Constants
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "The connection interface is already open.";

	/**
	 * Creates an {@code InterfaceAlreadyOpenedException} with 
	 * {@value #DEFAULT_MESSAGE} as its error detail message.
	 */
	public InterfaceAlreadyOpenException() {
		super(DEFAULT_MESSAGE);
	}
	
	/**
	 * Creates an {@code InterfaceAlreadyOpenedException} with the specified 
	 * message.
	 * 
	 * @param message The associated message.
	 */
	public InterfaceAlreadyOpenException(String message) {
		super(message);
	}
	
	/**
	 * Creates an {@code InterfaceAlreadyOpenException} with the specified 
	 * message and cause.
	 * 
	 * @param message The associated message.
	 * @param cause The cause of this exception.
	 * 
	 * @see Throwable
	 */
	public InterfaceAlreadyOpenException(String message, Throwable cause) {
		super(message, cause);
	}
}

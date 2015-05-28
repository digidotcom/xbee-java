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
 * Generic XBee API exception. This class and its subclasses indicate
 * conditions that an application might want to catch. This exception can be 
 * thrown when any problem related to the XBee device occurs.
 * 
 * @see Exception
 */
public class XBeeException extends Exception {

	// Constants
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates an {@code XBeeException} with {@code null} as its error 
	 * detail message.
	 */
	public XBeeException() {
		super();
	}
	
	/**
	 * Creates an {@code XBeeException} with the specified cause.
	 * 
	 * @param cause The cause of this exception.
	 * 
	 * @see Throwable
	 */
	public XBeeException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates an {@code XBeeException} with the specified message.
	 * 
	 * @param message The associated message.
	 */
	public XBeeException(String message) {
		super(message);
	}

	/**
	 * Creates an {@code XBeeException} with the specified message and cause.
	 * 
	 * @param message The associated message.
	 * @param cause The cause of this exception.
	 * 
	 * @see Throwable
	 */
	public XBeeException(String message, Throwable cause) {
		super(message, cause);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Throwable#getCause()
	 */
	@Override
	public Throwable getCause() {
		return super.getCause();
	}
}

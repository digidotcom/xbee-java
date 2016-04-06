/**
 * Copyright (c) 2014-2016 Digi International Inc.,
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
 * This exception will be thrown when the provided connection interface cannot 
 * be created.
 * 
 * @see RuntimeException
 */
public class SerialConnectionInterfaceNotCreatedException extends RuntimeException {
	
	// Constants.
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "Connection interface could not be created.";
	
	/**
	 * Constructs a new {@code SerialConnectionInterfaceNotCreatedException} with 
	 * {@value #DEFAULT_MESSAGE} as its error detail message.
	 * 
	 * <p>The cause is not initialized, and may subsequently be initialized by
	 * a call to {@link #initCause}.</p>
	 */
	public SerialConnectionInterfaceNotCreatedException() {
		super(DEFAULT_MESSAGE);
	}
	
	/**
	 * Constructs a new {@code SerialConnectionInterfaceNotCreatedException} with the 
	 * specified detail message.
	 * 
	 * <p>The cause is not initialized, and may subsequently be initialized by
	 * a call to {@link #initCause}.</p>
	 * 
	 * @param message The detail message (which is saved for later retrieval
	 *                by the {@link #getMessage()} method).
	 */
	public SerialConnectionInterfaceNotCreatedException(String message) {
		super(message);
	}
	
	/**
	 * Creates an {@code SerialConnectionInterfaceNotCreatedException} with the 
	 * specified message and cause.
	 * 
	 * @param message The detail message (which is saved for later retrieval
	 *                by the {@link #getMessage()} method).
	 * @param cause The cause (which is saved for later retrieval by the
	 *              {@link #getCause()} method).  (A {@code null} value is
	 *              permitted, and indicates that the cause is nonexistent or
	 *              unknown.)
	 * 
	 * @see Throwable
	 */
	public SerialConnectionInterfaceNotCreatedException(String message, Throwable cause) {
		super(message, cause);
	}
}

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

import com.digi.xbee.api.models.OperatingMode;

/**
 * This exception will be thrown when performing any action with the XBee 
 * device and its operating mode is different than {@link OperatingMode#API} 
 * and {@link OperatingMode#API_ESCAPE}.
 * 
 * @see XBeeDeviceException
 * @see com.digi.xbee.api.models.OperatingMode
 */
public class InvalidOperatingModeException extends XBeeDeviceException {

	// Constants
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "The operating mode of the XBee device is not supported by the library.";

	/**
	 * Creates an {@code InvalidOperatingModeException} with 
	 * {@value #DEFAULT_MESSAGE} as its error detail message.
	 */
	public InvalidOperatingModeException() {
		super(DEFAULT_MESSAGE);
	}
	
	/**
	 * Creates an {@code InvalidOperatingModeException} with the specified
	 * operating mode.
	 * 
	 * @param mode The unsupported operating mode.
	 * 
	 * @see com.digi.xbee.api.models.OperatingMode
	 */
	public InvalidOperatingModeException(OperatingMode mode) {
		super("Unsupported operating mode: " + mode);
	}
	
	/**
	 * Creates an {@code InvalidOperatingModeException} with the specified 
	 * message.
	 * 
	 * @param message The associated message.
	 */
	public InvalidOperatingModeException(String message) {
		super(message);
	}
	
	/**
	 * Creates an {@code InvalidOperatingModeException} with the specified 
	 * message and cause.
	 * 
	 * @param message The associated message.
	 * @param cause The cause of this exception.
	 * 
	 * @see Throwable
	 */
	public InvalidOperatingModeException(String message, Throwable cause) {
		super(message, cause);
	}
}

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

import com.digi.xbee.api.models.OperatingMode;

/**
 * This exception will be thrown if the operating mode is different than
 * {@link OperatingMode#API} and {@link OperatingMode#API_ESCAPE}.
 * 
 * @see OperatingMode
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
	 * message.
	 * 
	 * @param message The associated message.
	 */
	public InvalidOperatingModeException(String message) {
		super(message);
	}
}

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
 * This exception is thrown whenever an action related with AT mode is performed
 * while in API or API2 modes and vice-versa. 
 * 
 * @see OperatingMode
 */
public class InvalidOperatingModeException extends Exception {

	// Constants
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an InvalidXBeeModeException with the specified message.
	 * 
	 * @param message The associated message.
	 */
	public InvalidOperatingModeException(String message) {
		super(message);
	}
	
	/**
	 * Creates an InvalidXBeeModeException with the specified message.
	 */
	public InvalidOperatingModeException() {
		super("Device is configured with an unsupported operating mode.");
	}
}

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

/**
 * This exception is thrown when there is an error parsing an API
 * packet from the input stream.
 */
public class PacketParsingException extends Exception {
	
	// Constants
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an ParsingException with the specified message.
	 * 
	 * @param message The associated message.
	 */
	public PacketParsingException(String message) {
		super(message);
	}
}

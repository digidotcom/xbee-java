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

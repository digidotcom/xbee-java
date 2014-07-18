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

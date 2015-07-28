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

import com.digi.xbee.api.models.ATCommandStatus;

/**
 * This exception will be thrown when receiving a command response containing 
 * a status different than {@code ATCommandStatus#OK} after sending an XBee 
 * AT command.
 * 
 * @see CommunicationException
 * @see com.digi.xbee.api.models.ATCommandStatus
 */
public class ATCommandException extends CommunicationException {

	// Constants.
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "There was a problem sending the AT command packet.";
	
	// Variables.
	private final ATCommandStatus atCommandStatus;
	
	/**
	 * Creates an {@code ATCommandException} with the provided 
	 * {@code ATCommandStatus} indicating the status of the AT command response 
	 * and {@value #DEFAULT_MESSAGE} as its error detail message.
	 * 
	 * @param atCommandStatus The status of the AT command response.
	 * 
	 * @see com.digi.xbee.api.models.ATCommandStatus
	 */
	public ATCommandException(ATCommandStatus atCommandStatus) {
		super(DEFAULT_MESSAGE);
		this.atCommandStatus = atCommandStatus;
	}
	
	/**
	 * Creates an {@code ATCommandException} with the specified message and 
	 * {@code ATCommandStatus} indicating the status of the AT command response.
	 * 
	 * @param message The associated message.
	 * @param atCommandStatus The status of the AT command response.
	 * 
	 * @see com.digi.xbee.api.models.ATCommandStatus
	 */
	public ATCommandException(String message, ATCommandStatus atCommandStatus) {
		super(message);
		this.atCommandStatus = atCommandStatus;
	}
	
	/**
	 * Creates an {@code ATCommandException} with the specified message, cause 
	 * and {@code ATCommandStatus} indicating the status of the AT command 
	 * response.
	 * 
	 * @param message The associated message.
	 * @param cause The cause of this exception.
	 * @param atCommandStatus The status of the AT command response.
	 * 
	 * @see Throwable
	 * @see com.digi.xbee.api.models.ATCommandStatus
	 */
	public ATCommandException(String message, Throwable cause, ATCommandStatus atCommandStatus) {
		super(message, cause);
		this.atCommandStatus = atCommandStatus;
	}
	
	/**
	 * Returns the {@code ATCommandStatus} of the exception containing 
	 * information about the AT command response.
	 * 
	 * @return The status of the AT command response.
	 * 
	 * @see com.digi.xbee.api.models.ATCommandStatus
	 */
	public ATCommandStatus getCommandStatus() {
		return atCommandStatus;
	}
	
	/**
	 * Returns the text containing the status of the AT command response 
	 * from the exception.
	 * 
	 * @return The text with the status of the AT command response.
	 */
	public String getCommandStatusMessage() {
		if (atCommandStatus != null)
			return atCommandStatus.getDescription();
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		String message = super.getMessage();
		
		if (message == null)
			message = "";
		
		if (atCommandStatus != null) {
			if (message.length() > 0)
				message = message + " > ";
			message = message + atCommandStatus.getDescription();
		}
		
		return message;
	}
}

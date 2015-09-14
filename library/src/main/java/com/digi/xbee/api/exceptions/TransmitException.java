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

import com.digi.xbee.api.models.XBeeTransmitStatus;

/**
 * This exception will be thrown when receiving a transmit status different than 
 * {@code XBeeTransmitStatus#SUCCESS} after sending an XBee API packet.
 * 
 * @see CommunicationException
 * @see com.digi.xbee.api.models.XBeeTransmitStatus
 */
public class TransmitException extends CommunicationException {

	// Constants.
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "There was a problem transmitting the XBee API packet.";
	
	// Variables.
	private final XBeeTransmitStatus transmitStatus;
	
	/**
	 * Creates a {@code TransmitException} with the provided 
	 * {@code XBeeTransmitStatus} indicating the status of the transmission and 
	 * {@value #DEFAULT_MESSAGE} as its error detail message.
	 * 
	 * @param transmitStatus The status of the transmission.
	 * 
	 * @see com.digi.xbee.api.models.XBeeTransmitStatus
	 */
	public TransmitException(XBeeTransmitStatus transmitStatus) {
		super(DEFAULT_MESSAGE);
		this.transmitStatus = transmitStatus;
	}
	
	/**
	 * Creates a {@code TransmitException} with the specified message and 
	 * {@code XBeeTransmitStatus} indicating the status of the transmission.
	 * 
	 * @param message The associated message.
	 * @param transmitStatus The status of the transmission.
	 * 
	 * @see com.digi.xbee.api.models.XBeeTransmitStatus
	 */
	public TransmitException(String message, XBeeTransmitStatus transmitStatus) {
		super(message);
		this.transmitStatus = transmitStatus;
	}
	
	/**
	 * Creates a {@code TransmitException} with the specified message, cause and 
	 * {@code XBeeTransmitStatus} indicating the status of the transmission.
	 * 
	 * @param message The associated message.
	 * @param cause The cause of this exception.
	 * @param transmitStatus The status of the transmission.
	 * 
	 * @see Throwable
	 * @see com.digi.xbee.api.models.XBeeTransmitStatus
	 */
	public TransmitException(String message, Throwable cause, XBeeTransmitStatus transmitStatus) {
		super(message, cause);
		this.transmitStatus = transmitStatus;
	}
	
	/**
	 * Returns the {@code XBeeTransmitStatus} of the exception containing 
	 * information about the transmission.
	 * 
	 * @return The status of the transmission.
	 * 
	 * @see com.digi.xbee.api.models.XBeeTransmitStatus
	 */
	public XBeeTransmitStatus getTransmitStatus() {
		return transmitStatus;
	}
	
	/**
	 * Returns the text containing the status of the transmission from 
	 * the exception.
	 * 
	 * @return The text with the status of the transmission.
	 */
	public String getTransmitStatusMessage() {
		if (transmitStatus != null)
			return transmitStatus.getDescription();
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
		
		if (transmitStatus != null) {
			if (message.length() > 0)
				message = message + " > ";
			message = message + transmitStatus.toString();
		}
		
		return message;
	}
}

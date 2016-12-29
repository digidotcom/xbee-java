/**
 * Copyright (c) 2016 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api.models;

import java.util.regex.Pattern;

/**
 * This class represents an SMS message containing the phone number that sent  
 * the message and the content (data) of the message. 
 * 
 * <p>This class is used within the XBee Java Library to read SMS sent to 
 * Cellular devices.</p>
 * 
 * @since 1.2.0
 */
public class SMSMessage {

	// Constants.
	private static final String PHONE_NUMBER_PATTERN = "^\\+?\\d+$";
	
	// Variables.
	private final String phoneNumber;
	private final String data;
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code SMSMessage} with the given parameters.
	 * 
	 * @param phoneNumber The phone number that sent the message.
	 * @param data String containing the message text.
	 * 
	 * @throws IllegalArgumentException if {@code phoneNumber} is invalid.
	 * @throws NullPointerException if {@code phoneNumber == null} or
	 *                              if {@code data == null}.
	 */
	public SMSMessage(String phoneNumber, String data) {
		if (phoneNumber == null)
			throw new NullPointerException("Phone number cannot be null.");
		if (data == null)
			throw new NullPointerException("Data cannot be null.");
		if (!Pattern.matches(PHONE_NUMBER_PATTERN, phoneNumber))
			throw new IllegalArgumentException("Invalid phone number.");
		
		this.phoneNumber = phoneNumber;
		this.data = data;
	}
	
	/**
	 * Returns the phone number that sent the message.
	 * 
	 * @return The phone number that sent the message.
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	/**
	 * Returns a string containing the data of the message.
	 * 
	 * @return A string containing the data of the message.
	 */
	public String getData() {
		return data;
	}
}

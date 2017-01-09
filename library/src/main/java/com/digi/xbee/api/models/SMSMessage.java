/**
 * Copyright 2017, Digi International Inc.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES 
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR 
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES 
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN 
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF 
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
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

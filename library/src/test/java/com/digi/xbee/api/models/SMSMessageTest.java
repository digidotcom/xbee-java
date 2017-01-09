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

import static org.junit.Assert.*;

import org.junit.Test;

public class SMSMessageTest {

	// Constants.
	private final static String DATA = "Data";
	private final static String PHONE = "0123456789";
	private final static String INVALID_PHONE = "+31AB9540Bc 44";
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.SMSMessage#SMSMessage(String, String)}.
	 * 
	 * <p>Verify that the {@code SMSMessage} cannot be created if the phone number is null.</p>
	 */
	@Test(expected=NullPointerException.class)
	public void testCreateNullPhone() {
		new SMSMessage(null, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.SMSMessage#SMSMessage(String, String)}.
	 * 
	 * <p>Verify that the {@code SMSMessage} cannot be created if the data is null.</p>
	 */
	@Test(expected=NullPointerException.class)
	public void testCreateNullData() {
		new SMSMessage(PHONE, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.SMSMessage#SMSMessage(String, String)}.
	 * 
	 * <p>Verify that the {@code SMSMessage} cannot be created if the phone number is 
	 * not valid.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateInvalidPhone() {
		new SMSMessage(INVALID_PHONE, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.SMSMessage#SMSMessage(String, String)}, 
	 * {@link com.digi.xbee.api.models.SMSMessage#getPhoneNumber()} and
	 * {@link com.digi.xbee.api.models.SMSMessage#getData()}.
	 * 
	 * <p>Verify that the {@code SMSMessage} can be created successfully and the getters work 
	 * properly.</p>
	 */
	@Test
	public void testCreateSuccessNotBroadcast() {
		SMSMessage smsMessage = new SMSMessage(PHONE, DATA);
		
		assertEquals(PHONE, smsMessage.getPhoneNumber());
		assertEquals(DATA, smsMessage.getData());
	}
}

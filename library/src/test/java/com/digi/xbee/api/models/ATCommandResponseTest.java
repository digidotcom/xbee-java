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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ATCommandResponseTest {

	// Constants.
	private static final String VALID_RESPONSE = "A random response";
	
	// Variables.
	private ATCommand atCommand;
	
	@Before
	public void setup() {
		// Mock the AT Command (it will not be accessed).
		atCommand = Mockito.mock(ATCommand.class);
	}
	
	@Test
	/**
	 * Verify that AT Command object cannot be created using invalid parameters.
	 */
	public void testCreateWithInvalidParameters() {
		// Test with null command.
		try {
			new ATCommandResponse(null);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		try {
			new ATCommandResponse(null, ATCommandStatus.OK);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		try {
			new ATCommandResponse(null, VALID_RESPONSE.getBytes());
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		try {
			new ATCommandResponse(null, VALID_RESPONSE.getBytes(), ATCommandStatus.OK);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		// Test with null status.
		try {
			new ATCommandResponse(atCommand, VALID_RESPONSE.getBytes(), null);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		try {
			new ATCommandResponse(atCommand, (ATCommandStatus)null);
			fail("Object should not have been created.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
	}
	
	@Test
	/**
	 * Verify that AT Command Response object can be created using a valid command.
	 */
	public void testCreateWithValidCommand() {
		// Test with valid command.
		ATCommandResponse commandResponse = null;
		try {
			commandResponse = new ATCommandResponse(atCommand);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}
		
		assertEquals(atCommand, commandResponse.getCommand());
		assertEquals(ATCommandStatus.OK, commandResponse.getResponseStatus());
		assertNull(commandResponse.getResponse());
		assertNull(commandResponse.getResponseString());
	}
	
	@Test
	/**
	 * Verify that AT Command Response object can be created using a valid command and resposne status.
	 */
	public void testCreateWithValidCommandAndStatus() {
		// Test with valid command.
		ATCommandResponse commandResponse = null;
		try {
			commandResponse = new ATCommandResponse(atCommand, ATCommandStatus.TX_FAILURE);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}
		
		assertEquals(atCommand, commandResponse.getCommand());
		assertEquals(ATCommandStatus.TX_FAILURE, commandResponse.getResponseStatus());
		assertNull(commandResponse.getResponse());
		assertNull(commandResponse.getResponseString());
	}
	
	@Test
	/**
	 * Verify that AT Command Response object can be created using a valid command and response.
	 */
	public void testCreateWithValidCommandAndResponse() {
		// Test with valid command and response.
		ATCommandResponse commandResponse = null;
		try {
			commandResponse = new ATCommandResponse(atCommand, VALID_RESPONSE.getBytes());
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}

		assertEquals(atCommand, commandResponse.getCommand());
		assertEquals(ATCommandStatus.OK, commandResponse.getResponseStatus());
		assertArrayEquals(VALID_RESPONSE.getBytes(), commandResponse.getResponse());
		assertEquals(VALID_RESPONSE, commandResponse.getResponseString());
	}
	
	@Test
	/**
	 * Verify that AT Command Response object can be created using a valid command and parameter string.
	 */
	public void testCreateWithValidCommandAndResponseAndStatus() {
		// Test with valid command, response and status.
		ATCommandResponse commandResponse = null;
		try {
			commandResponse = new ATCommandResponse(atCommand, VALID_RESPONSE.getBytes(), ATCommandStatus.ERROR);
		} catch (Exception e) {
			fail("This exception should have not been thrown.");
		}

		assertEquals(atCommand, commandResponse.getCommand());
		assertEquals(ATCommandStatus.ERROR, commandResponse.getResponseStatus());
		assertArrayEquals(VALID_RESPONSE.getBytes(), commandResponse.getResponse());
		assertEquals(VALID_RESPONSE, commandResponse.getResponseString());
	}
}

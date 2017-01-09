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
package com.digi.xbee.api.exceptions;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.digi.xbee.api.models.ATCommandStatus;

public class ATCommandExceptionTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#ATCommandException(ATCommandStatus)}.
	 */
	@Test
	public final void testCreateATCommandExceptionATCommandStatus() {
		// Setup the resources for the test.
		String message = "There was a problem sending the AT command packet.";
		ATCommandStatus atCommandStatus = ATCommandStatus.ERROR;
		
		// Call the method under test.
		ATCommandException e = new ATCommandException(atCommandStatus);
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not have the expected message", 
				e.getMessage(), is(equalTo(message + " > " + atCommandStatus.toString())));
		assertThat("Created 'ATCommandException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
		assertThat("Created 'ATCommandException' does not have the expected AT command status", 
				e.getCommandStatus(), is(equalTo(atCommandStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#ATCommandException(ATCommandStatus)}.
	 */
	@Test
	public final void testCreateATCommandExceptionATCommandStatusNull() {
		// Setup the resources for the test.
		String message = "There was a problem sending the AT command packet.";
		ATCommandStatus atCommandStatus = null;
		
		// Call the method under test.
		ATCommandException e = new ATCommandException(atCommandStatus);
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'ATCommandException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
		assertThat("Created 'ATCommandException' does not have the expected AT command status", 
				e.getCommandStatus(), is(nullValue(ATCommandStatus.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#ATCommandException(String, ATCommandStatus)}.
	 */
	@Test
	public final void testCreateATCommandExceptionMessageATCommandStatus() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = ATCommandStatus.INVALID_COMMAND;
		String message = "This is the message";
		
		// Call the method under test.
		ATCommandException e = new ATCommandException(message, atCommandStatus);
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not have the expected message", 
				e.getMessage(), is(equalTo(message + " > " + atCommandStatus.toString())));
		assertThat("Created 'ATCommandException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
		assertThat("Created 'ATCommandException' does not have the expected AT command status", 
				e.getCommandStatus(), is(equalTo(atCommandStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#ATCommandException(String, ATCommandStatus)}.
	 */
	@Test
	public final void testCreateATCommandExceptionMessageNullAndATCommandStatus() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = ATCommandStatus.INVALID_PARAMETER;
		String message = null;
		
		// Call the method under test.
		ATCommandException e = new ATCommandException(message, atCommandStatus);
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not have the expected message", 
				e.getMessage(), is(equalTo(atCommandStatus.toString())));
		assertThat("Created 'ATCommandException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
		assertThat("Created 'ATCommandException' does not have the expected AT command status", 
				e.getCommandStatus(), is(equalTo(atCommandStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#ATCommandException(String, ATCommandStatus)}.
	 */
	@Test
	public final void testCreateATCommandExceptionMessageAndATCommandStatusNull() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = null;
		String message = "This is the message";
		
		// Call the method under test.
		ATCommandException e = new ATCommandException(message, atCommandStatus);
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'ATCommandException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
		assertThat("Created 'ATCommandException' does not have the expected AT command status", 
				e.getCommandStatus(), is(nullValue(ATCommandStatus.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#ATCommandException(String, ATCommandStatus)}.
	 */
	@Test
	public final void testCreateATCommandExceptionMessageAndATCommandStatusBothNull() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = null;
		String message = null;
		
		// Call the method under test.
		ATCommandException e = new ATCommandException(message, atCommandStatus);
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not have the expected message", 
				e.getMessage(), is(equalTo("")));
		assertThat("Created 'ATCommandException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
		assertThat("Created 'ATCommandException' does not have the expected AT command status", 
				e.getCommandStatus(), is(nullValue(ATCommandStatus.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#ATCommandException(String, Throwable, ATCommandStatus)}.
	 */
	@Test
	public final void testCreateATCommandExceptionMessageAndCauseAndATCommandStatus() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = ATCommandStatus.OK;
		String message = "This is the message";
		Throwable cause = new Exception();
		
		// Call the method under test.
		ATCommandException e = new ATCommandException(message, cause, atCommandStatus);
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not have the expected message", 
				e.getMessage(), is(equalTo(message + " > " + atCommandStatus.toString())));
		assertThat("Created 'ATCommandException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
		assertThat("Created 'ATCommandException' does not have the expected AT command status", 
				e.getCommandStatus(), is(equalTo(atCommandStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#ATCommandException(String, Throwable, ATCommandStatus)}.
	 */
	@Test
	public final void testCreateATCommandExceptionMessageNullAndCauseAndATCommandStatus() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = ATCommandStatus.TX_FAILURE;
		String message = null;
		Throwable cause = new Exception();
		
		// Call the method under test.
		ATCommandException e = new ATCommandException(message, cause, atCommandStatus);
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not have the expected message", 
				e.getMessage(), is(equalTo(atCommandStatus.toString())));
		assertThat("Created 'ATCommandException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
		assertThat("Created 'ATCommandException' does not have the expected AT command status", 
				e.getCommandStatus(), is(equalTo(atCommandStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#ATCommandException(String, Throwable, ATCommandStatus)}.
	 */
	@Test
	public final void testCreateATCommandExceptionMessageAndCauseNullAndATCommandStatus() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = ATCommandStatus.UNKNOWN;
		String message = "This is the message";
		Throwable cause = null;
		
		// Call the method under test.
		ATCommandException e = new ATCommandException(message, cause, atCommandStatus);
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not have the expected message", 
				e.getMessage(), is(equalTo(message + " > " + atCommandStatus.toString())));
		assertThat("Created 'ATCommandException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
		assertThat("Created 'ATCommandException' does not have the expected AT command status", 
				e.getCommandStatus(), is(equalTo(atCommandStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#ATCommandException(String, Throwable, ATCommandStatus)}.
	 */
	@Test
	public final void testCreateATCommandExceptionMessageAndCauseAndATCommandStatusNull() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = null;
		String message = "This is the message";
		Throwable cause = new Exception();
		
		// Call the method under test.
		ATCommandException e = new ATCommandException(message, cause, atCommandStatus);
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'ATCommandException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
		assertThat("Created 'ATCommandException' does not have the expected AT command status", 
				e.getCommandStatus(), is(nullValue(ATCommandStatus.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#ATCommandException(String, Throwable, ATCommandStatus)}.
	 */
	@Test
	public final void testCreateATCommandExceptionMessageNullAndCauseNullAndATCommandStatus() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = ATCommandStatus.ERROR;
		String message = null;
		Throwable cause = null;
		
		// Call the method under test.
		ATCommandException e = new ATCommandException(message, cause, atCommandStatus);
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not have the expected message", 
				e.getMessage(), is(equalTo(atCommandStatus.toString())));
		assertThat("Created 'ATCommandException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
		assertThat("Created 'ATCommandException' does not have the expected AT command status", 
				e.getCommandStatus(), is(equalTo(atCommandStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#ATCommandException(String, Throwable, ATCommandStatus)}.
	 */
	@Test
	public final void testCreateATCommandExceptionMessageNullAndCauseAndATCommandStatusNull() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = null;
		String message = null;
		Throwable cause = new Exception();
		
		// Call the method under test.
		ATCommandException e = new ATCommandException(message, cause, atCommandStatus);
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not have the expected message", 
				e.getMessage(), is(equalTo("")));
		assertThat("Created 'ATCommandException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
		assertThat("Created 'ATCommandException' does not have the expected AT command status", 
				e.getCommandStatus(), is(nullValue(ATCommandStatus.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#ATCommandException(String, Throwable, ATCommandStatus)}.
	 */
	@Test
	public final void testCreateATCommandExceptionMessageAndCauseNullAndATCommandStatusNull() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = null;
		String message = "This is the message";
		Throwable cause = null;
		
		// Call the method under test.
		ATCommandException e = new ATCommandException(message, cause, atCommandStatus);
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'ATCommandException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
		assertThat("Created 'ATCommandException' does not have the expected AT command status", 
				e.getCommandStatus(), is(nullValue(ATCommandStatus.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#ATCommandException(String, Throwable, ATCommandStatus)}.
	 */
	@Test
	public final void testCreateATCommandExceptionMessageAndCauseAndATCommandStatusAllNull() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = null;
		String message = null;
		Throwable cause = null;
		
		// Call the method under test.
		ATCommandException e = new ATCommandException(message, cause, atCommandStatus);
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not have the expected message", 
				e.getMessage(), is(equalTo("")));
		assertThat("Created 'ATCommandException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
		assertThat("Created 'ATCommandException' does not have the expected AT command status", 
				e.getCommandStatus(), is(nullValue(ATCommandStatus.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#getCommandStatus()}.
	 */
	@Test
	public final void testGetCommandStatus() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = ATCommandStatus.INVALID_COMMAND;
		ATCommandException e = new ATCommandException(atCommandStatus);
		
		// Call the method under test.
		ATCommandStatus result = e.getCommandStatus();
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not have the expected AT command status", 
				result, is(equalTo(atCommandStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#getCommandStatus()}.
	 */
	@Test
	public final void testGetCommandStatusNull() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = null;
		ATCommandException e = new ATCommandException(atCommandStatus);
		
		// Call the method under test.
		ATCommandStatus result = e.getCommandStatus();
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not have the expected AT command status", 
				result, is(equalTo(atCommandStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#getCommandStatusMessage()}.
	 */
	@Test
	public final void testGetCommandStatusMessage() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = ATCommandStatus.INVALID_PARAMETER;
		ATCommandException e = new ATCommandException(atCommandStatus);
		
		// Call the method under test.
		String result = e.getCommandStatusMessage();
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not return the expected AT command status message", 
				result, is(equalTo(atCommandStatus.getDescription())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#getCommandStatusMessage()}.
	 */
	@Test
	public final void testGetCommandStatusMessageNull() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = null;
		ATCommandException e = new ATCommandException(atCommandStatus);
		
		// Call the method under test.
		String result = e.getCommandStatusMessage();
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not return the expected AT command status message", 
				result, is(nullValue(String.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#getMessage()}.
	 */
	@Test
	public final void testGetMessageOnlyCommandStatus() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = ATCommandStatus.UNKNOWN;
		ATCommandException e = new ATCommandException(atCommandStatus);
		
		// Call the method under test.
		String result = e.getMessage();
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not return the expected message", 
				result, is(equalTo("There was a problem sending the AT command packet. > " + atCommandStatus.toString())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#getMessage()}.
	 */
	@Test
	public final void testGetMessageOnlyCommandStatusNull() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = null;
		ATCommandException e = new ATCommandException(atCommandStatus);
		
		// Call the method under test.
		String result = e.getMessage();
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not return the expected message", 
				result, is(equalTo("There was a problem sending the AT command packet.")));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#getMessage()}.
	 */
	@Test
	public final void testGetMessageOnlyCommandStatusAndMessage() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = ATCommandStatus.OK;
		String message = "This is the message";
		ATCommandException e = new ATCommandException(message, atCommandStatus);
		
		// Call the method under test.
		String result = e.getMessage();
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not return the expected message", 
				result, is(equalTo(message + " > " + atCommandStatus.toString())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#getMessage()}.
	 */
	@Test
	public final void testGetMessageOnlyCommandStatusNullAndMessage() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = null;
		String message = "This is the message";
		ATCommandException e = new ATCommandException(message, atCommandStatus);
		
		// Call the method under test.
		String result = e.getMessage();
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not return the expected message", 
				result, is(equalTo(message)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#getMessage()}.
	 */
	@Test
	public final void testGetMessageOnlyCommandStatusAndMessageNull() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = ATCommandStatus.TX_FAILURE;
		String message = null;
		ATCommandException e = new ATCommandException(message, atCommandStatus);
		
		// Call the method under test.
		String result = e.getMessage();
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not return the expected message", 
				result, is(equalTo(atCommandStatus.toString())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.ATCommandException#getMessage()}.
	 */
	@Test
	public final void testGetMessageOnlyCommandStatusAndMessageBothNull() {
		// Setup the resources for the test.
		ATCommandStatus atCommandStatus = null;
		String message = null;
		ATCommandException e = new ATCommandException(message, atCommandStatus);
		
		// Call the method under test.
		String result = e.getMessage();
		
		// Verify the result.
		assertThat("Created 'ATCommandException' does not return the expected message", 
				result, is(equalTo("")));
	}
}

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

import com.digi.xbee.api.models.XBeeTransmitStatus;

public class TransmitExceptionTest {

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
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#TransmitException(XBeeTransmitStatus)}.
	 */
	@Test
	public final void testCreateTransmitExceptionXBeeTransmitStatus() {
		// Setup the resources for the test.
		String message = "There was a problem transmitting the XBee API packet.";
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.NO_BUFFERS;
		
		// Call the method under test.
		TransmitException e = new TransmitException(transmitStatus);
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not have the expected message", 
				e.getMessage(), is(equalTo(message + " > " + transmitStatus.toString())));
		assertThat("Created 'TransmitException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
		assertThat("Created 'TransmitException' does not have the expected transmit status", 
				e.getTransmitStatus(), is(equalTo(transmitStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#TransmitException(XBeeTransmitStatus)}.
	 */
	@Test
	public final void testCreateTransmitExceptionXBeeTransmitStatusNull() {
		// Setup the resources for the test.
		String message = "There was a problem transmitting the XBee API packet.";
		XBeeTransmitStatus transmitStatus = null;
		
		// Call the method under test.
		TransmitException e = new TransmitException(transmitStatus);
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'TransmitException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
		assertThat("Created 'TransmitException' does not have the expected transmit status", 
				e.getTransmitStatus(), is(nullValue(XBeeTransmitStatus.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#TransmitException(String, XBeeTransmitStatus)}.
	 */
	@Test
	public final void testCreateTransmitExceptionMessageXBeeTransmitStatus() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.KEY_NOT_AUTHORIZED;
		String message = "This is the message";
		
		// Call the method under test.
		TransmitException e = new TransmitException(message, transmitStatus);
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not have the expected message", 
				e.getMessage(), is(equalTo(message + " > " + transmitStatus.toString())));
		assertThat("Created 'TransmitException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
		assertThat("Created 'TransmitException' does not have the expected transmit status", 
				e.getTransmitStatus(), is(equalTo(transmitStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#TransmitException(String, XBeeTransmitStatus)}.
	 */
	@Test
	public final void testCreateTransmitExceptionMessageNullAndXBeeTransmitStatus() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.SOFTWARE_ERROR;
		String message = null;
		
		// Call the method under test.
		TransmitException e = new TransmitException(message, transmitStatus);
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not have the expected message", 
				e.getMessage(), is(equalTo(transmitStatus.toString())));
		assertThat("Created 'TransmitException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
		assertThat("Created 'TransmitException' does not have the expected transmit status", 
				e.getTransmitStatus(), is(equalTo(transmitStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#TransmitException(String, XBeeTransmitStatus)}.
	 */
	@Test
	public final void testCreateTransmitExceptionMessageAndXBeeTransmitStatusNull() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = null;
		String message = "This is the message";
		
		// Call the method under test.
		TransmitException e = new TransmitException(message, transmitStatus);
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'TransmitException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
		assertThat("Created 'TransmitException' does not have the expected transmit status", 
				e.getTransmitStatus(), is(nullValue(XBeeTransmitStatus.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#TransmitException(String, XBeeTransmitStatus)}.
	 */
	@Test
	public final void testCreateTransmitExceptionMessageAndXBeeTransmitStatusBothNull() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = null;
		String message = null;
		
		// Call the method under test.
		TransmitException e = new TransmitException(message, transmitStatus);
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not have the expected message", 
				e.getMessage(), is(equalTo("")));
		assertThat("Created 'TransmitException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
		assertThat("Created 'TransmitException' does not have the expected transmit status", 
				e.getTransmitStatus(), is(nullValue(XBeeTransmitStatus.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#TransmitException(String, Throwable, XBeeTransmitStatus)}.
	 */
	@Test
	public final void testCreateTransmitExceptionMessageAndCauseAndXBeeTransmitStatus() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.SUCCESS;
		String message = "This is the message";
		Throwable cause = new Exception();
		
		// Call the method under test.
		TransmitException e = new TransmitException(message, cause, transmitStatus);
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not have the expected message", 
				e.getMessage(), is(equalTo(message + " > " + transmitStatus.toString())));
		assertThat("Created 'TransmitException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
		assertThat("Created 'TransmitException' does not have the expected transmit status", 
				e.getTransmitStatus(), is(equalTo(transmitStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#TransmitException(String, Throwable, XBeeTransmitStatus)}.
	 */
	@Test
	public final void testCreateTransmitExceptionMessageNullAndCauseAndXBeeTransmitStatus() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.BROADCAST_ERROR_APS;
		String message = null;
		Throwable cause = new Exception();
		
		// Call the method under test.
		TransmitException e = new TransmitException(message, cause, transmitStatus);
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not have the expected message", 
				e.getMessage(), is(equalTo(transmitStatus.toString())));
		assertThat("Created 'TransmitException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
		assertThat("Created 'TransmitException' does not have the expected transmit status", 
				e.getTransmitStatus(), is(equalTo(transmitStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#TransmitException(String, Throwable, XBeeTransmitStatus)}.
	 */
	@Test
	public final void testCreateTransmitExceptionMessageAndCauseNullAndXBeeTransmitStatus() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.PURGED;
		String message = "This is the message";
		Throwable cause = null;
		
		// Call the method under test.
		TransmitException e = new TransmitException(message, cause, transmitStatus);
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not have the expected message", 
				e.getMessage(), is(equalTo(message + " > " + transmitStatus.toString())));
		assertThat("Created 'TransmitException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
		assertThat("Created 'TransmitException' does not have the expected transmit status", 
				e.getTransmitStatus(), is(equalTo(transmitStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#TransmitException(String, Throwable, XBeeTransmitStatus)}.
	 */
	@Test
	public final void testCreateTransmitExceptionMessageAndCauseAndXBeeTransmitStatusNull() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = null;
		String message = "This is the message";
		Throwable cause = new Exception();
		
		// Call the method under test.
		TransmitException e = new TransmitException(message, cause, transmitStatus);
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'TransmitException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
		assertThat("Created 'TransmitException' does not have the expected transmit status", 
				e.getTransmitStatus(), is(nullValue(XBeeTransmitStatus.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#TransmitException(String, Throwable, XBeeTransmitStatus)}.
	 */
	@Test
	public final void testCreateTransmitExceptionMessageNullAndCauseNullAndXBeeTransmitStatus() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.SUCCESS;
		String message = null;
		Throwable cause = null;
		
		// Call the method under test.
		TransmitException e = new TransmitException(message, cause, transmitStatus);
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not have the expected message", 
				e.getMessage(), is(equalTo(transmitStatus.toString())));
		assertThat("Created 'TransmitException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
		assertThat("Created 'TransmitException' does not have the expected transmit status", 
				e.getTransmitStatus(), is(equalTo(transmitStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#TransmitException(String, Throwable, XBeeTransmitStatus)}.
	 */
	@Test
	public final void testCreateTransmitExceptionMessageNullAndCauseAndXBeeTransmitStatusNull() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = null;
		String message = null;
		Throwable cause = new Exception();
		
		// Call the method under test.
		TransmitException e = new TransmitException(message, cause, transmitStatus);
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not have the expected message", 
				e.getMessage(), is(equalTo("")));
		assertThat("Created 'TransmitException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
		assertThat("Created 'TransmitException' does not have the expected transmit status", 
				e.getTransmitStatus(), is(nullValue(XBeeTransmitStatus.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#TransmitException(String, Throwable, XBeeTransmitStatus)}.
	 */
	@Test
	public final void testCreateTransmitExceptionMessageAndCauseNullAndXBeeTransmitStatusNull() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = null;
		String message = "This is the message";
		Throwable cause = null;
		
		// Call the method under test.
		TransmitException e = new TransmitException(message, cause, transmitStatus);
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'TransmitException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
		assertThat("Created 'TransmitException' does not have the expected transmit status", 
				e.getTransmitStatus(), is(nullValue(XBeeTransmitStatus.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#TransmitException(String, Throwable, XBeeTransmitStatus)}.
	 */
	@Test
	public final void testCreateTransmitExceptionMessageAndCauseAndXBeeTransmitStatusAllNull() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = null;
		String message = null;
		Throwable cause = null;
		
		// Call the method under test.
		TransmitException e = new TransmitException(message, cause, transmitStatus);
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not have the expected message", 
				e.getMessage(), is(equalTo("")));
		assertThat("Created 'TransmitException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
		assertThat("Created 'TransmitException' does not have the expected transmit status", 
				e.getTransmitStatus(), is(nullValue(XBeeTransmitStatus.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#getTransmitStatus()}.
	 */
	@Test
	public final void testGetTransmitStatus() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.RESOURCE_ERROR;
		TransmitException e = new TransmitException(transmitStatus);
		
		// Call the method under test.
		XBeeTransmitStatus result = e.getTransmitStatus();
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not have the expected transmit status", 
				result, is(equalTo(transmitStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#getTransmitStatus()}.
	 */
	@Test
	public final void testGetTransmitStatusNull() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = null;
		TransmitException e = new TransmitException(transmitStatus);
		
		// Call the method under test.
		XBeeTransmitStatus result = e.getTransmitStatus();
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not have the expected transmit status", 
				result, is(equalTo(transmitStatus)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#getTransmitStatusMessage()}.
	 */
	@Test
	public final void testGetTransmitStatusMessage() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.INVALID_ENDPOINT;
		TransmitException e = new TransmitException(transmitStatus);
		
		// Call the method under test.
		String result = e.getTransmitStatusMessage();
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not return the expected transmit status message", 
				result, is(equalTo(transmitStatus.getDescription())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#getTransmitStatusMessage()}.
	 */
	@Test
	public final void testGetTransmitStatusMessageNull() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = null;
		TransmitException e = new TransmitException(transmitStatus);
		
		// Call the method under test.
		String result = e.getTransmitStatusMessage();
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not return the expected transmit status message", 
				result, is(nullValue(String.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#getMessage()}.
	 */
	@Test
	public final void testGetMessageOnlyTransmitStatus() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.UNKNOWN;
		TransmitException e = new TransmitException(transmitStatus);
		
		// Call the method under test.
		String result = e.getMessage();
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not return the expected message", 
				result, is(equalTo("There was a problem transmitting the XBee API packet. > " + transmitStatus.toString())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#getMessage()}.
	 */
	@Test
	public final void testGetMessageOnlyTransmitStatusNull() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = null;
		TransmitException e = new TransmitException(transmitStatus);
		
		// Call the method under test.
		String result = e.getMessage();
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not return the expected message", 
				result, is(equalTo("There was a problem transmitting the XBee API packet.")));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#getMessage()}.
	 */
	@Test
	public final void testGetMessageOnlyTransmitStatusAndMessage() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.WIFI_PHYSICAL_ERROR;
		String message = "This is the message";
		TransmitException e = new TransmitException(message, transmitStatus);
		
		// Call the method under test.
		String result = e.getMessage();
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not return the expected message", 
				result, is(equalTo(message + " > " + transmitStatus.toString())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#getMessage()}.
	 */
	@Test
	public final void testGetMessageOnlyTransmitStatusNullAndMessage() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = null;
		String message = "This is the message";
		TransmitException e = new TransmitException(message, transmitStatus);
		
		// Call the method under test.
		String result = e.getMessage();
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not return the expected message", 
				result, is(equalTo(message)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#getMessage()}.
	 */
	@Test
	public final void testGetMessageOnlyTransmitStatusAndMessageNull() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = XBeeTransmitStatus.WIFI_PHYSICAL_ERROR;
		String message = null;
		TransmitException e = new TransmitException(message, transmitStatus);
		
		// Call the method under test.
		String result = e.getMessage();
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not return the expected message", 
				result, is(equalTo(transmitStatus.toString())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.TransmitException#getMessage()}.
	 */
	@Test
	public final void testGetMessageOnlyTransmitStatusAndMessageBothNull() {
		// Setup the resources for the test.
		XBeeTransmitStatus transmitStatus = null;
		String message = null;
		TransmitException e = new TransmitException(message, transmitStatus);
		
		// Call the method under test.
		String result = e.getMessage();
		
		// Verify the result.
		assertThat("Created 'TransmitException' does not return the expected message", 
				result, is(equalTo("")));
	}
}

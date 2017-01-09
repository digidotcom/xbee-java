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

public class XBeeExceptionTest {

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
	 * Test method for {@link com.digi.xbee.api.exceptions.XBeeException#XBeeException()}.
	 */
	@Test
	public final void testCreateXBeeExceptionDefault() {
		// Setup the resources for the test.
		
		// Call the method under test.
		XBeeException e = new XBeeException();
		
		// Verify the result.
		assertThat("Created 'XBeeException' does not have the expected message", 
				e.getMessage(), is(nullValue(String.class)));
		assertThat("Created 'XBeeException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.XBeeException#XBeeException(Throwable))}.
	 */
	@Test
	public final void testCreateXBeeExceptionCause() {
		// Setup the resources for the test.
		Throwable cause = new Exception();
		
		// Call the method under test.
		XBeeException e = new XBeeException(cause);
		
		// Verify the result.
		assertThat("Created 'XBeeException' does not have the expected message", 
				e.getMessage(), is(equalTo(cause.toString())));
		assertThat("Created 'XBeeException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.XBeeException#XBeeException(Throwable)}.
	 */
	@Test
	public final void testCreateXBeeExceptionCauseNull() {
		// Setup the resources for the test.
		Throwable cause = null;
		
		// Call the method under test.
		XBeeException e = new XBeeException(cause);
		
		// Verify the result.
		assertThat("Created 'XBeeException' does not have the expected message", 
				e.getMessage(), is(nullValue(String.class)));
		assertThat("Created 'XBeeException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.XBeeException#XBeeException(String)}.
	 */
	@Test
	public final void testCreateXBeeExceptionMessage() {
		// Setup the resources for the test.
		String message = "This is the message";
		
		// Call the method under test.
		XBeeException e = new XBeeException(message);
		
		// Verify the result.
		assertThat("Created 'XBeeException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'XBeeException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.XBeeException#XBeeException(String)}.
	 */
	@Test
	public final void testCreateXBeeExceptionMessageNull() {
		// Setup the resources for the test.
		String message = null;
		
		// Call the method under test.
		XBeeException e = new XBeeException(message);
		
		// Verify the result.
		assertThat("Created 'XBeeException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'XBeeException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.XBeeException#XBeeException(String, Throwable)}.
	 */
	@Test
	public final void testCreateXBeeExceptionMessageAndCause() {
		// Setup the resources for the test.
		String message = "This is the message";
		Throwable cause = new Exception();
		
		// Call the method under test.
		XBeeException e = new XBeeException(message, cause);
		
		// Verify the result.
		assertThat("Created 'XBeeException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'XBeeException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.XBeeException#XBeeException(String, Throwable)}.
	 */
	@Test
	public final void testXBeeExceptionMessageNullAndCause() {
		// Setup the resources for the test.
		String message = null;
		Throwable cause = new Exception();
		
		// Call the method under test.
		XBeeException e = new XBeeException(message, cause);
		
		// Verify the result.
		assertThat("Created 'XBeeException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'XBeeException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.XBeeException#XBeeException(String, Throwable)}.
	 */
	@Test
	public final void testCreateXBeeExceptionMessageAndCauseNull() {
		// Setup the resources for the test.
		String message = "This is the message";
		Throwable cause = null;
		
		// Call the method under test.
		XBeeException e = new XBeeException(message, cause);
		
		// Verify the result.
		assertThat("Created 'XBeeException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'XBeeException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.XBeeException#XBeeException(String, Throwable)}.
	 */
	@Test
	public final void testCreateXBeeExceptionMessageAndCauseBothNull() {
		// Setup the resources for the test.
		String message = null;
		Throwable cause = null;
		
		// Call the method under test.
		XBeeException e = new XBeeException(message, cause);
		
		// Verify the result.
		assertThat("Created 'XBeeException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'XBeeException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.XBeeException#getCause()}.
	 */
	@Test
	public final void testGetCause() {
		// Setup the resources for the test.
		Throwable cause = new Exception();
		XBeeException e = new XBeeException();
		e.initCause(cause);
		
		// Call the method under test.
		Throwable result = e.getCause();
		
		// Verify the result.
		assertThat("Created 'XBeeException' does not have the expected cause", 
				result, is(equalTo(cause)));
	}
}

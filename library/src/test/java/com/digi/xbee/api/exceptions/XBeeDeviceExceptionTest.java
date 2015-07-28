/**
 * Copyright (c) 2015 Digi International Inc.,
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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class XBeeDeviceExceptionTest {

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
	 * Test method for {@link com.digi.xbee.api.exceptions.XBeeDeviceException#XBeeDeviceException()}.
	 */
	@Test
	public final void testCreateXBeeDeviceExceptionDefault() {
		// Setup the resources for the test.
		
		// Call the method under test.
		XBeeDeviceException e = new XBeeDeviceException();
		
		// Verify the result.
		assertThat("Created 'XBeeDeviceException' does not have the expected message", 
				e.getMessage(), is(nullValue(String.class)));
		assertThat("Created 'XBeeDeviceException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.XBeeDeviceException#XBeeDeviceException(String)}.
	 */
	@Test
	public final void testCreateXBeeDeviceExceptionMessage() {
		// Setup the resources for the test.
		String message = "This is the message";
		
		// Call the method under test.
		XBeeDeviceException e = new XBeeDeviceException(message);
		
		// Verify the result.
		assertThat("Created 'XBeeDeviceException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'XBeeDeviceException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.XBeeDeviceException#XBeeDeviceException(String)}.
	 */
	@Test
	public final void testCreateXBeeDeviceExceptionMessageNull() {
		// Setup the resources for the test.
		String message = null;
		
		// Call the method under test.
		XBeeDeviceException e = new XBeeDeviceException(message);
		
		// Verify the result.
		assertThat("Created 'XBeeDeviceException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'XBeeDeviceException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.XBeeDeviceException#XBeeDeviceException(Throwable))}.
	 */
	@Test
	public final void testCreateXBeeDeviceExceptionCause() {
		// Setup the resources for the test.
		Throwable cause = new Exception();
		
		// Call the method under test.
		XBeeDeviceException e = new XBeeDeviceException(cause);
		
		// Verify the result.
		assertThat("Created 'XBeeDeviceException' does not have the expected message", 
				e.getMessage(), is(equalTo(cause.toString())));
		assertThat("Created 'XBeeDeviceException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.XBeeDeviceException#XBeeDeviceException(Throwable)}.
	 */
	@Test
	public final void testCreateXBeeDeviceExceptionCauseNull() {
		// Setup the resources for the test.
		Throwable cause = null;
		
		// Call the method under test.
		XBeeDeviceException e = new XBeeDeviceException(cause);
		
		// Verify the result.
		assertThat("Created 'XBeeDeviceException' does not have the expected message", 
				e.getMessage(), is(nullValue(String.class)));
		assertThat("Created 'XBeeDeviceException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.XBeeDeviceException#XBeeDeviceException(String, Throwable)}.
	 */
	@Test
	public final void testCreateXBeeDeviceExceptionMessageAndCause() {
		// Setup the resources for the test.
		String message = "This is the message";
		Throwable cause = new Exception();
		
		// Call the method under test.
		XBeeDeviceException e = new XBeeDeviceException(message, cause);
		
		// Verify the result.
		assertThat("Created 'XBeeDeviceException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'XBeeDeviceException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.XBeeDeviceException#XBeeDeviceException(String, Throwable)}.
	 */
	@Test
	public final void testXBeeDeviceExceptionMessageNullAndCause() {
		// Setup the resources for the test.
		String message = null;
		Throwable cause = new Exception();
		
		// Call the method under test.
		XBeeDeviceException e = new XBeeDeviceException(message, cause);
		
		// Verify the result.
		assertThat("Created 'XBeeDeviceException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'XBeeDeviceException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.XBeeDeviceException#XBeeDeviceException(String, Throwable)}.
	 */
	@Test
	public final void testCreateXBeeDeviceExceptionMessageAndCauseNull() {
		// Setup the resources for the test.
		String message = "This is the message";
		Throwable cause = null;
		
		// Call the method under test.
		XBeeDeviceException e = new XBeeDeviceException(message, cause);
		
		// Verify the result.
		assertThat("Created 'XBeeDeviceException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'XBeeDeviceException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.XBeeDeviceException#XBeeDeviceException(String, Throwable)}.
	 */
	@Test
	public final void testCreateXBeeDeviceExceptionMessageAndCauseBothNull() {
		// Setup the resources for the test.
		String message = null;
		Throwable cause = null;
		
		// Call the method under test.
		XBeeDeviceException e = new XBeeDeviceException(message, cause);
		
		// Verify the result.
		assertThat("Created 'XBeeDeviceException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'XBeeDeviceException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
	}
}

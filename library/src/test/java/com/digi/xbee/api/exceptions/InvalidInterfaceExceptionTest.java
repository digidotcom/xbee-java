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

public class InvalidInterfaceExceptionTest {

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
	 * Test method for {@link com.digi.xbee.api.exceptions.InvalidInterfaceException#InvalidInterfaceException()}.
	 */
	@Test
	public final void testCreateInvalidInterfaceExceptionDefault() {
		// Setup the resources for the test.
		String expectedMessage = "The connection interface you are trying to access is invalid or does not exist.";
		
		// Call the method under test.
		InvalidInterfaceException e = new InvalidInterfaceException();
		
		// Verify the result.
		assertThat("Created 'InvalidInterfaceException' does not have the expected message", 
				e.getMessage(), is(equalTo(expectedMessage)));
		assertThat("Created 'InvalidInterfaceException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.InvalidInterfaceException#InvalidInterfaceException(String)}.
	 */
	@Test
	public final void testCreateInvalidInterfaceExceptionMessage() {
		// Setup the resources for the test.
		String message = "This is the message";
		
		// Call the method under test.
		InvalidInterfaceException e = new InvalidInterfaceException(message);
		
		// Verify the result.
		assertThat("Created 'InvalidInterfaceException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'InvalidInterfaceException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.InvalidInterfaceException#InvalidInterfaceException(String)}.
	 */
	@Test
	public final void testCreateInvalidInterfaceExceptionMessageNull() {
		// Setup the resources for the test.
		String message = null;
		
		// Call the method under test.
		InvalidInterfaceException e = new InvalidInterfaceException(message);
		
		// Verify the result.
		assertThat("Created 'InvalidInterfaceException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'InvalidInterfaceException' does not have the expected cause", 
				e.getCause(), is(nullValue(Throwable.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.InvalidInterfaceException#InvalidInterfaceException(String, Throwable)}.
	 */
	@Test
	public final void testCreateInvalidInterfaceExceptionMessageAndCause() {
		// Setup the resources for the test.
		String message = "This is the message";
		Throwable cause = new Exception();
		
		// Call the method under test.
		InvalidInterfaceException e = new InvalidInterfaceException(message, cause);
		
		// Verify the result.
		assertThat("Created 'InvalidInterfaceException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'InvalidInterfaceException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.InvalidInterfaceException#InvalidInterfaceException(String, Throwable)}.
	 */
	@Test
	public final void testInvalidInterfaceExceptionMessageNullAndCause() {
		// Setup the resources for the test.
		String message = null;
		Throwable cause = new Exception();
		
		// Call the method under test.
		InvalidInterfaceException e = new InvalidInterfaceException(message, cause);
		
		// Verify the result.
		assertThat("Created 'InvalidInterfaceException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'InvalidInterfaceException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.InvalidInterfaceException#InvalidInterfaceException(String, Throwable)}.
	 */
	@Test
	public final void testCreateInvalidInterfaceExceptionMessageAndCauseNull() {
		// Setup the resources for the test.
		String message = "This is the message";
		Throwable cause = null;
		
		// Call the method under test.
		InvalidInterfaceException e = new InvalidInterfaceException(message, cause);
		
		// Verify the result.
		assertThat("Created 'InvalidInterfaceException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'InvalidInterfaceException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.exceptions.InvalidInterfaceException#InvalidInterfaceException(String, Throwable)}.
	 */
	@Test
	public final void testCreateInvalidInterfaceExceptionMessageAndCauseBothNull() {
		// Setup the resources for the test.
		String message = null;
		Throwable cause = null;
		
		// Call the method under test.
		InvalidInterfaceException e = new InvalidInterfaceException(message, cause);
		
		// Verify the result.
		assertThat("Created 'InvalidInterfaceException' does not have the expected message", 
				e.getMessage(), is(equalTo(message)));
		assertThat("Created 'InvalidInterfaceException' does not have the expected cause", 
				e.getCause(), is(equalTo(cause)));
	}
}

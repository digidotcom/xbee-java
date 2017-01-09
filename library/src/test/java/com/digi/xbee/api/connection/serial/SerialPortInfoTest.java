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
package com.digi.xbee.api.connection.serial;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SerialPortInfoTest {
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
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
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortInfo#SerialPortInfo(String)}.
	 */
	@Test
	public final void testCreateSerialPortInfoNullName() {
		// Setup the resources for the test.
		String name = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Serial port name cannot be null.")));
		
		// Call the method under test.
		new SerialPortInfo(name);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortInfo#SerialPortInfo(String)}.
	 */
	@Test
	public final void testCreateSerialPortInfoEmptyName() {
		// Setup the resources for the test.
		String name = "";
		
		// Call the method under test.
		SerialPortInfo info = new SerialPortInfo(name);
		
		// Verify the result.
		assertThat(info.getPortName(), is(equalTo(name)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortInfo#SerialPortInfo(String)}.
	 */
	@Test
	public final void testCreateSerialPortInfoValidName() {
		// Setup the resources for the test.
		String name = "/dev/ttyS0";
		
		// Call the method under test.
		SerialPortInfo info = new SerialPortInfo(name);
		
		// Verify the result.
		assertThat(info.getPortName(), is(equalTo(name)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortInfo#SerialPortInfo(String, String)}.
	 */
	@Test
	public final void testCreateSerialPortInfoWithDescriptionNullName() {
		// Setup the resources for the test.
		String name = null;
		String description = "This is the port description";
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Serial port name cannot be null.")));
		
		// Call the method under test.
		new SerialPortInfo(name, description);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortInfo#SerialPortInfo(String, String)}.
	 */
	@Test
	public final void testCreateSerialPortInfoWithDescriptionEmptyName() {
		// Setup the resources for the test.
		String name = "";
		String description = "This is the port description";
		
		// Call the method under test.
		SerialPortInfo info = new SerialPortInfo(name, description);
		
		// Verify the result.
		assertThat(info.getPortName(), is(equalTo(name)));
		assertThat(info.getPortDescription(), is(equalTo(description)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortInfo#SerialPortInfo(String, String)}.
	 */
	@Test
	public final void testCreateSerialPortInfoWithDescriptionNullDescription() {
		// Setup the resources for the test.
		String name = "/dev/ttyS0";
		String description = null;
		
		// Call the method under test.
		SerialPortInfo info = new SerialPortInfo(name, description);
		
		// Verify the result.
		assertThat(info.getPortName(), is(equalTo(name)));
		assertThat(info.getPortDescription(), is(nullValue(String.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortInfo#SerialPortInfo(String, String)}.
	 */
	@Test
	public final void testCreateSerialPortInfoWithDescriptionEmptyDescription() {
		// Setup the resources for the test.
		String name = "/dev/ttyS0";
		String description = "";
		
		// Call the method under test.
		SerialPortInfo info = new SerialPortInfo(name, description);
		
		// Verify the result.
		assertThat(info.getPortName(), is(equalTo(name)));
		assertThat(info.getPortDescription(), is(equalTo(description)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortInfo#SerialPortInfo(String, String)}.
	 */
	@Test
	public final void testCreateSerialPortInfoWithDescriptionValidNameAndDescription() {
		// Setup the resources for the test.
		String name = "/dev/ttyS0";
		String description = "This is another description";
		
		// Call the method under test.
		SerialPortInfo info = new SerialPortInfo(name, description);
		
		// Verify the result.
		assertThat(info.getPortName(), is(equalTo(name)));
		assertThat(info.getPortDescription(), is(equalTo(description)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortInfo#getPortName()}.
	 */
	@Test
	public final void testGetPortName() {
		// Setup the resources for the test.
		String name = "/dev/ttyS0";
		String description = "This is another description";
		SerialPortInfo info = new SerialPortInfo(name, description);
		
		// Call the method under test.
		String result = info.getPortName();
		
		// Verify the result.
		assertThat(result, is(equalTo(name)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortInfo#getPortDescription()}.
	 */
	@Test
	public final void testGetPortDescription() {
		// Setup the resources for the test.
		String name = "/dev/ttyS0";
		String description = "This is another description";
		SerialPortInfo info = new SerialPortInfo(name, description);
		
		// Call the method under test.
		String result = info.getPortDescription();
		
		// Verify the result.
		assertThat(result, is(equalTo(description)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortInfo#setPortDescription(String)}.
	 */
	@Test
	public final void testSetPortDescriptionNullValue() {
		// Setup the resources for the test.
		String name = "/dev/ttyS0";
		String origDescription = "This is another description";
		String finalDescription = null;
		SerialPortInfo info = new SerialPortInfo(name, origDescription);
		
		assertThat(info.getPortDescription(), is(equalTo(origDescription)));
		
		// Call the method under test.
		info.setPortDescription(finalDescription);
		
		String result = info.getPortDescription();
		
		// Verify the result.
		assertThat(result, is(nullValue(String.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortInfo#setPortDescription(String)}.
	 */
	@Test
	public final void testSetPortDescriptionEmptyValue() {
		// Setup the resources for the test.
		String name = "/dev/ttyS0";
		String origDescription = "This is another description";
		String finalDescription = "";
		SerialPortInfo info = new SerialPortInfo(name, origDescription);
		
		assertThat(info.getPortDescription(), is(equalTo(origDescription)));
		
		// Call the method under test.
		info.setPortDescription(finalDescription);
		
		String result = info.getPortDescription();
		
		// Verify the result.
		assertThat(result, is(equalTo(finalDescription)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortInfo#setPortDescription(String)}.
	 */
	@Test
	public final void testSetPortDescriptionValidValue() {
		// Setup the resources for the test.
		String name = "/dev/ttyS0";
		String origDescription = "This is another description";
		String finalDescription = "New port description";
		SerialPortInfo info = new SerialPortInfo(name, origDescription);
		
		assertThat(info.getPortDescription(), is(equalTo(origDescription)));
		
		// Call the method under test.
		info.setPortDescription(finalDescription);
		
		String result = info.getPortDescription();
		
		// Verify the result.
		assertThat(result, is(equalTo(finalDescription)));
	}
}

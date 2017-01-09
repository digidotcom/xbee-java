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
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.digi.xbee.api.connection.serial.SerialPortParameters;

public class SerialPortParametersTest {
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
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortParameters#SerialPortParameters(int, int, int, int, int)}.
	 */
	@Test
	public final void testCreateSerialPortParametersNegativeBaudrate() {
		// Setup the resources for the test.
		int baudrate = -9600;
		int dataBits = 8;
		int stopBits = 1;
		int parity = 0;
		int flowControl = 0;
				
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Baudrate cannot be less than 0.")));
				
		// Call the method under test.
		new SerialPortParameters(baudrate, dataBits, stopBits, parity, flowControl);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortParameters#SerialPortParameters(int, int, int, int, int)}.
	 */
	@Test
	public final void testCreateSerialPortParametersNegativeDataBits() {
		// Setup the resources for the test.
		int baudrate = 9600;
		int dataBits = -8;
		int stopBits = 1;
		int parity = 0;
		int flowControl = 0;
				
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Number of data bits cannot be less than 0.")));
				
		// Call the method under test.
		new SerialPortParameters(baudrate, dataBits, stopBits, parity, flowControl);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortParameters#SerialPortParameters(int, int, int, int, int)}.
	 */
	@Test
	public final void testCreateSerialPortParametersNegativeStopBits() {
		// Setup the resources for the test.
		int baudrate = 9600;
		int dataBits = 8;
		int stopBits = -1;
		int parity = 0;
		int flowControl = 0;
				
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Number of stop bits cannot be less than 0.")));
				
		// Call the method under test.
		new SerialPortParameters(baudrate, dataBits, stopBits, parity, flowControl);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortParameters#SerialPortParameters(int, int, int, int, int)}.
	 */
	@Test
	public final void testCreateSerialPortParametersNegativeParity() {
		// Setup the resources for the test.
		int baudrate = 9600;
		int dataBits = 8;
		int stopBits = 1;
		int parity = -6;
		int flowControl = 0;
				
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Illegal parity value.")));
				
		// Call the method under test.
		new SerialPortParameters(baudrate, dataBits, stopBits, parity, flowControl);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortParameters#SerialPortParameters(int, int, int, int, int)}.
	 */
	@Test
	public final void testCreateSerialPortParametersNegativeFlowControl() {
		// Setup the resources for the test.
		int baudrate = 9600;
		int dataBits = 8;
		int stopBits = 1;
		int parity = 0;
		int flowControl = -9;
				
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Illegal flow control value.")));
				
		// Call the method under test.
		new SerialPortParameters(baudrate, dataBits, stopBits, parity, flowControl);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortParameters#SerialPortParameters(int, int, int, int, int)}.
	 */
	@Test
	public final void testCreateSerialPortParameters() {
		// Setup the resources for the test.
		int baudrate = 9600;
		int dataBits = 8;
		int stopBits = 1;
		int parity = 0;
		int flowControl = 0;
		
		// Call the method under test.
		SerialPortParameters result = new SerialPortParameters(baudrate, dataBits, stopBits, parity, flowControl);
		
		// Verify the result.
		assertThat(result.baudrate, is(equalTo(baudrate)));
		assertThat(result.dataBits, is(equalTo(dataBits)));
		assertThat(result.stopBits, is(equalTo(stopBits)));
		assertThat(result.parity, is(equalTo(parity)));
		assertThat(result.flowControl, is(equalTo(flowControl)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortParameters#equals(Object)}.
	 * 
	 * <p>Test the equals method with a {@code null} value.</p>
	 */
	@Test
	public final void testEqualsWithNull() {
		// Setup the resources for the test.
		SerialPortParameters parameters = new SerialPortParameters(9600, 8, 1, 0, 0);
		
		// Call the method under test.
		boolean areEqual = parameters.equals(null);
		
		// Verify the result.
		assertThat("Serial port parameters cannot be equal to null", areEqual, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortParameters#equals(Object)}.
	 * 
	 * <p>Test the equals method with a non {@code SerialPortParameters} value.</p>
	 */
	@Test
	public final void testEqualsWithNonSerialPortParameters() {
		// Setup the resources for the test.
		SerialPortParameters parameters = new SerialPortParameters(9600, 8, 1, 0, 0);
		
		// Call the method under test.
		boolean areEqual = parameters.equals(new Object());
		
		// Verify the result.
		assertThat("Serial port parameters cannot be equal to an Object", areEqual, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.connection.serial.SerialPortParameters(Object)}.
	 * 
	 * <p>Test the equals method with different baudrates.</p>
	 */
	@Test
	public final void testEqualsWithDifferentBaudrates() {
		// Setup the resources for the test.
		SerialPortParameters parameters1 = new SerialPortParameters(9600, 8, 1, 0, 0);
		SerialPortParameters parameters2 = new SerialPortParameters(115200, 8, 1, 0, 0);
		
		// Call the method under test.
		boolean areEqual1 = parameters1.equals(parameters2);
		boolean areEqual2 = parameters2.equals(parameters1);
		
		// Verify the result.
		assertThat("Serial port parameters1 must be different from Serial port parameters2", areEqual1, is(equalTo(false)));
		assertThat("Serial port parameters2 must be different from Serial port parameters1", areEqual2, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.connection.serial.SerialPortParameters(Object)}.
	 * 
	 * <p>Test the equals method with different number of data bits.</p>
	 */
	@Test
	public final void testEqualsWithDifferentDataBits() {
		// Setup the resources for the test.
		SerialPortParameters parameters1 = new SerialPortParameters(9600, 8, 1, 0, 0);
		SerialPortParameters parameters2 = new SerialPortParameters(9600, 7, 1, 0, 0);
		
		// Call the method under test.
		boolean areEqual1 = parameters1.equals(parameters2);
		boolean areEqual2 = parameters2.equals(parameters1);
		
		// Verify the result.
		assertThat("Serial port parameters1 must be different from Serial port parameters2", areEqual1, is(equalTo(false)));
		assertThat("Serial port parameters2 must be different from Serial port parameters1", areEqual2, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.connection.serial.SerialPortParameters(Object)}.
	 * 
	 * <p>Test the equals method with different number of stop bits.</p>
	 */
	@Test
	public final void testEqualsWithDifferentStopBits() {
		// Setup the resources for the test.
		SerialPortParameters parameters1 = new SerialPortParameters(9600, 8, 1, 0, 0);
		SerialPortParameters parameters2 = new SerialPortParameters(9600, 8, 2, 0, 0);
		
		// Call the method under test.
		boolean areEqual1 = parameters1.equals(parameters2);
		boolean areEqual2 = parameters2.equals(parameters1);
		
		// Verify the result.
		assertThat("Serial port parameters1 must be different from Serial port parameters2", areEqual1, is(equalTo(false)));
		assertThat("Serial port parameters2 must be different from Serial port parameters1", areEqual2, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.connection.serial.SerialPortParameters(Object)}.
	 * 
	 * <p>Test the equals method with different parity.</p>
	 */
	@Test
	public final void testEqualsWithDifferentParity() {
		// Setup the resources for the test.
		SerialPortParameters parameters1 = new SerialPortParameters(9600, 8, 1, 0, 0);
		SerialPortParameters parameters2 = new SerialPortParameters(9600, 8, 1, 2, 0);
		
		// Call the method under test.
		boolean areEqual1 = parameters1.equals(parameters2);
		boolean areEqual2 = parameters2.equals(parameters1);
		
		// Verify the result.
		assertThat("Serial port parameters1 must be different from Serial port parameters2", areEqual1, is(equalTo(false)));
		assertThat("Serial port parameters2 must be different from Serial port parameters1", areEqual2, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.connection.serial.SerialPortParameters(Object)}.
	 * 
	 * <p>Test the equals method with different flow control.</p>
	 */
	@Test
	public final void testEqualsWithDifferentFlowControl() {
		// Setup the resources for the test.
		SerialPortParameters parameters1 = new SerialPortParameters(9600, 8, 1, 0, 0);
		SerialPortParameters parameters2 = new SerialPortParameters(9600, 8, 1, 0, 1);
		
		// Call the method under test.
		boolean areEqual1 = parameters1.equals(parameters2);
		boolean areEqual2 = parameters2.equals(parameters1);
		
		// Verify the result.
		assertThat("Serial port parameters1 must be different from Serial port parameters2", areEqual1, is(equalTo(false)));
		assertThat("Serial port parameters2 must be different from Serial port parameters1", areEqual2, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortParameters#equals(Object)}.
	 * 
	 * <p>Test the equals method with equal {@code SerialPortParameters}.</p>
	 */
	@Test
	public final void testEqualsIsSymetric() {
		// Setup the resources for the test.
		SerialPortParameters parameters1 = new SerialPortParameters(9600, 8, 1, 0, 0);
		SerialPortParameters parameters2 = new SerialPortParameters(9600, 8, 1, 0, 0);
		
		// Call the method under test.
		boolean areEqual1 = parameters1.equals(parameters2);
		boolean areEqual2 = parameters2.equals(parameters1);
		
		// Verify the result.
		assertThat("Serial port parameters1 must be equal to Serial port parameters2", areEqual1, is(equalTo(true)));
		assertThat("Serial port parameters2 must be equal to Serial port parameters1", areEqual2, is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortParameters#equals(Object)}.
	 */
	@Test
	public final void testEqualsIsReflexive() {
		// Setup the resources for the test.
		SerialPortParameters parameters = new SerialPortParameters(9600, 8, 1, 0, 0);
		
		// Call the method under test.
		boolean areEqual = parameters.equals(parameters);
		
		// Verify the result.
		assertThat("Serial port parameters must be equal to itself", areEqual, is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortParameters#equals(Object)}.
	 */
	@Test
	public final void testEqualsIsTransitive() {
		// Setup the resources for the test.
		SerialPortParameters parameters1 = new SerialPortParameters(9600, 8, 1, 0, 0);
		SerialPortParameters parameters2 = new SerialPortParameters(9600, 8, 1, 0, 0);
		SerialPortParameters parameters3 = new SerialPortParameters(9600, 8, 1, 0, 0);
		
		// Call the method under test.
		boolean areEqual1 = parameters1.equals(parameters2);
		boolean areEqual2 = parameters2.equals(parameters3);
		boolean areEqual3 = parameters1.equals(parameters3);
		
		// Verify the result.
		assertThat("Serial port parameters1 must be equal to Serial port parameters2", areEqual1, is(equalTo(true)));
		assertThat("Serial port parameters2 must be equal to Serial port parameters3", areEqual2, is(equalTo(true)));
		assertThat("Serial port parameters1 must be equal to Serial port parameters3", areEqual3, is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortParameters#equals(Object)}.
	 */
	@Test
	public final void testEqualsIsConsistent() {
		// Setup the resources for the test.
		SerialPortParameters parameters1 = new SerialPortParameters(9600, 8, 1, 0, 0);
		SerialPortParameters parameters2 = new SerialPortParameters(9600, 8, 1, 0, 0);
		SerialPortParameters parameters3 = new SerialPortParameters(115200, 8, 1, 0, 0);
		
		// Verify the result.
		assertThat("Consistent test fail parameters1,parameters2", parameters1.equals(parameters2), is(equalTo(true)));
		assertThat("Consistent test fail parameters1,parameters2", parameters1.equals(parameters2), is(equalTo(true)));
		assertThat("Consistent test fail parameters1,parameters2", parameters1.equals(parameters2), is(equalTo(true)));
		assertThat("Consistent test fail parameters3,parameters1", parameters3.equals(parameters1), is(equalTo(false)));
		assertThat("Consistent test fail parameters3,parameters1", parameters3.equals(parameters1), is(equalTo(false)));
		assertThat("Consistent test fail parameters3,parameters1", parameters3.equals(parameters1), is(equalTo(false)));

	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortParameters#hashCode()}.
	 */
	@Test
	public final void testHashCodeWithEqualSerialPortParameters() {
		// Setup the resources for the test.
		SerialPortParameters parameters1 = new SerialPortParameters(9600, 8, 1, 0, 0);
		SerialPortParameters parameters2 = new SerialPortParameters(9600, 8, 1, 0, 0);
		
		// Call the method under test.
		int hashHV1 = parameters1.hashCode();
		int hashHV2 = parameters2.hashCode();
		
		// Verify the result.
		assertThat("Serial port parameters1 must be equal to Serial port parameters2", parameters1.equals(parameters2), is(equalTo(true)));
		assertThat("Serial port parameters2 must be equal to Serial port parameters1", parameters2.equals(parameters1), is(equalTo(true)));
		assertThat("Hash codes must be equal", hashHV1, is(equalTo(hashHV2)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortParameters#hashCode()}.
	 */
	@Test
	public final void testHashCodeWithDifferentSerialPortParameters() {
		// Setup the resources for the test.
		SerialPortParameters parameters1 = new SerialPortParameters(9600, 8, 1, 0, 0);
		SerialPortParameters parameters2 = new SerialPortParameters(115200, 8, 1, 0, 0);
		
		int hashHV1 = parameters1.hashCode();
		int hashHV2 = parameters2.hashCode();
		
		// Verify the result.
		assertThat("Serial port parameters1 must be different from Serial port parameters2", parameters1.equals(parameters2), is(equalTo(false)));
		assertThat("Serial port parameters2 must be different from to Serial port parameters1", parameters2.equals(parameters1), is(equalTo(false)));
		assertThat("Hash codes must be different", hashHV1, is(not(equalTo(hashHV2))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortParameters#hashCode()}.
	 */
	@Test
	public final void testHashCodeIsConsistent() {
		// Setup the resources for the test.
		SerialPortParameters parameters = new SerialPortParameters(9600, 8, 1, 0, 0);
		
		// Call the method under test.
		int initialHashCode = parameters.hashCode();
		
		// Verify the result.
		assertThat("Consistent hashcode test fails", parameters.hashCode(), is(equalTo(initialHashCode)));
		assertThat("Consistent hashcode test fails", parameters.hashCode(), is(equalTo(initialHashCode)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.serial.SerialPortParameters#toString()}.
	 */
	@Test
	public void testToString() {
		// Setup the resources for the test.
		int baudrate = 9600;
		int dataBits = 8;
		int stopBits = 1;
		int parity = 0;
		int flowControl = 0;
		
		SerialPortParameters parameters = new SerialPortParameters(baudrate, dataBits, stopBits, parity, flowControl);
		
		// Call the method under test.
		String result = parameters.toString();
		
		// Verify the result.
		assertThat("toString() method does not produce the expected output", result, is(equalTo(
				String.format("Baud Rate: %d, Data Bits: %d, Stop Bits: %d, Parity: %d, Flow Control: %d", 
						baudrate, dataBits, stopBits, parity, flowControl))));
	}
}

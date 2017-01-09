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
package com.digi.xbee.api.io;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;

import org.junit.Test;

import com.digi.xbee.api.exceptions.OperationNotSupportedException;

public class IOSampleTest {

	// Constants.
	private static final byte[] INVALID_IO_DATA = new byte[]{0x00, 0x01, 0x02, 0x03};
	
	/*
	 * DIGITAL I/Os CONFIGURATION
	 * --------------------------------------------------
	 * DIO0, DIO4 and DIO9 are configured as digital I/O.
	 * 
	 * ADCs CONFIGURATION
	 * --------------------------------------------------
	 * DIO1 and DIO3 are configured as ADC. Power Supply voltage is provided.
	 * 
	 * DIGITAL I/Os VALUES
	 * --------------------------------------------------
	 * DIO0 is in HIGH state.
	 * DIO4 is in LOW state.
	 * DIO9 is in HIGH state.
	 * 
	 * ADCs VALUES
	 * --------------------------------------------------
	 * DIO1 value is 0x020C (524)
	 * DIO3 value is 0x00FA (250)
	 * Power Supply value is 0x04E2 (1250)
	 */
	private static final byte[] IO_DATA_ONLY_DIGITAL = new byte[]{0x01, 0x02, 0x11, 0x00, 0x02, 0x01};
	private static final byte[] IO_DATA_ONLY_ANALOG = new byte[]{0x01, 0x00, 0x00, (byte)0x8A, 0x02, 0x0C, 0x00, (byte)0xFA, 0x04, (byte)0xE2};
	private static final byte[] IO_DATA_MIXED = new byte[]{0x01, 0x02, 0x11, (byte)0x8A, 0x02, 0x01, 0x02, 0x0C, 0x00, (byte)0xFA, 0x04, (byte)0xE2};
	
	private static final int DIO1_ANALOG_VALUE = 524;
	private static final int DIO3_ANALOG_VALUE = 250;
	private static final int POWER_SUPPLY_VALUE = 1250;
	
	private static final int DIGITAL_MASK = 529; // 0x0211
	private static final int ANALOG_MASK = 138; // 0x8A
	
	/**
	 * Verify that the IOSample object is not correctly instantiated when the IO data byte 
	 * array is null.
	 */
	@Test(expected=NullPointerException.class)
	public void testCreateIOSampleWithNullData() {
		// Instantiate an IOSample object with null IO data.
		new IOSample(null);
	}
	
	/**
	 * Verify that the IOSample object is not correctly instantiated when the IO data byte 
	 * array is not valid.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateIOSampleWithInvalidData() {
		// Instantiate an IOSample object with invalid IO data.
		new IOSample(INVALID_IO_DATA);
	}
	
	/**
	 * Verify that digital values are successfully parsed and stored in the sample 
	 * when it only contains digital information.
	 */
	@Test
	public void testGetDigitalValuesFromDigitalData() {
		// Create an IO sample with the digital data.
		IOSample ioSample = new IOSample(IO_DATA_ONLY_DIGITAL);
		
		// Verify that the digital mask is correct.
		assertEquals(DIGITAL_MASK, ioSample.getDigitalMask());
		
		// Verify that the IO sample has digital values.
		assertTrue(ioSample.hasDigitalValues());
		
		// Try to get the values of all the DIO lines, only DIO0, DIO4 and 
		// DIO9 should have digital values, verify them.
		for (IOLine ioLine:IOLine.values()) {
			if (ioLine == IOLine.DIO0_AD0) {
				assertTrue(ioSample.hasDigitalValue(ioLine));
				assertEquals(IOValue.HIGH, ioSample.getDigitalValue(ioLine));
				assertEquals(IOValue.HIGH, ioSample.getDigitalValues().get(ioLine));
			} else if (ioLine == IOLine.DIO4_AD4) {
				assertTrue(ioSample.hasDigitalValue(ioLine));
				assertEquals(IOValue.LOW, ioSample.getDigitalValue(ioLine));
				assertEquals(IOValue.LOW, ioSample.getDigitalValues().get(ioLine));
			} else if (ioLine == IOLine.DIO9) {
				assertTrue(ioSample.hasDigitalValue(ioLine));
				assertEquals(IOValue.HIGH, ioSample.getDigitalValue(ioLine));
				assertEquals(IOValue.HIGH, ioSample.getDigitalValues().get(ioLine));
			} else {
				assertFalse(ioSample.hasDigitalValue(ioLine));
				assertNull(ioSample.getDigitalValue(ioLine));
				assertNull(ioSample.getDigitalValues().get(ioLine));
			}
		}
	}
	
	/**
	 * Verify that no analog values are retrieved from the sample when it 
	 * only contains digital information.
	 */
	@Test
	public void testGetAnalogValuesFromDigitalData() {
		// Create an IO sample with the digital data.
		IOSample ioSample = new IOSample(IO_DATA_ONLY_DIGITAL);
		
		// Verify that the analog mask is 0.
		assertEquals(0, ioSample.getAnalogMask());
		
		// Verify that the IO sample does not have analog values.
		assertFalse(ioSample.hasAnalogValues());
		
		// Verify that there is not any analog value.
		for (IOLine ioLine:IOLine.values()) {
			assertFalse(ioSample.hasAnalogValue(ioLine));
			assertNull(ioSample.getAnalogValue(ioLine));
			assertNull(ioSample.getAnalogValues().get(ioLine));
		}
	}
	
	/**
	 * Verify that power supply value not retrieved from the sample when it 
	 * only contains digital information.
	 * 
	 * @throws OperationNotSupportedException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testGetPowerSupplyValueFromDigitalData() throws OperationNotSupportedException {
		// Create an IO sample with the analog data.
		IOSample ioSample = new IOSample(IO_DATA_ONLY_DIGITAL);
		
		// Verify that the IO sample does not have power supply value.
		assertFalse(ioSample.hasPowerSupplyValue());
			
		ioSample.getPowerSupplyValue();
	}
	
	/**
	 * Verify that no digital values are retrieved from the sample when it 
	 * only contains analog and power supply information.
	 */
	@Test
	public void testGetDigitalValuesFromAnalogData() {
		// Create an IO sample with the analog data.
		IOSample ioSample = new IOSample(IO_DATA_ONLY_ANALOG);
		
		// Verify that the digital mask is 0.
		assertEquals(0, ioSample.getDigitalMask());
		
		// Verify that the IO sample does not have digital values.
		assertFalse(ioSample.hasDigitalValues());
		
		// Verify that there is not any digital value.
		for (IOLine ioLine:IOLine.values()) {
			assertFalse(ioSample.hasDigitalValue(ioLine));
			assertNull(ioSample.getDigitalValue(ioLine));
			assertNull(ioSample.getDigitalValues().get(ioLine));
		}
	}
	
	/**
	 * Verify that analog values are successfully parsed and stored in the sample 
	 * when it only contains analog and power supply information.
	 */
	@Test
	public void testGetAnalogValuesFromAnalogData() {
		// Create an IO sample with the analog data.
		IOSample ioSample = new IOSample(IO_DATA_ONLY_ANALOG);
		
		// Verify that the analog mask is correct.
		assertEquals(ANALOG_MASK, ioSample.getAnalogMask());
		
		// Verify that the IO sample has analog values.
		assertTrue(ioSample.hasAnalogValues());
		
		// Try to get the values of all the DIO lines, only DIO1 and DIO3 lines 
		// should have analog values, verify them.
		for (IOLine ioLine:IOLine.values()) {
			if (ioLine == IOLine.DIO1_AD1) {
				assertTrue(ioSample.hasAnalogValue(ioLine));
				assertEquals(DIO1_ANALOG_VALUE, (int)ioSample.getAnalogValue(ioLine));
				assertEquals(DIO1_ANALOG_VALUE, (int)ioSample.getAnalogValues().get(ioLine));
			} else if (ioLine == IOLine.DIO3_AD3) {
				assertTrue(ioSample.hasAnalogValue(ioLine));
				assertEquals(DIO3_ANALOG_VALUE, (int)ioSample.getAnalogValue(ioLine));
				assertEquals(DIO3_ANALOG_VALUE, (int)ioSample.getAnalogValues().get(ioLine));
			} else {
				assertFalse(ioSample.hasAnalogValue(ioLine));
				assertNull(ioSample.getAnalogValue(ioLine));
				assertNull(ioSample.getAnalogValues().get(ioLine));
			}
		}
	}
	
	/**
	 * Verify that power supply value is successfully parsed and stored in the sample 
	 * when it only contains analog and power supply information.
	 * 
	 * @throws OperationNotSupportedException 
	 */
	@Test
	public void testGetPowerSupplyValueFromAnalogData() throws OperationNotSupportedException {
		// Create an IO sample with the analog data.
		IOSample ioSample = new IOSample(IO_DATA_ONLY_ANALOG);
		
		// Verify that the IO sample has power supply value.
		assertTrue(ioSample.hasPowerSupplyValue());
			
		assertEquals(POWER_SUPPLY_VALUE, ioSample.getPowerSupplyValue());
	}
	
	/**
	 * Verify that digital values are successfully parsed and stored in the sample 
	 * when it contains digital, analog and power supply information.
	 */
	@Test
	public void testGetDigitalValuesFromMixedData() {
		// Create an IO sample with mixed (digital + analog) data.
		IOSample ioSample = new IOSample(IO_DATA_MIXED);
		
		// Verify that the digital mask is correct.
		assertEquals(DIGITAL_MASK, ioSample.getDigitalMask());
		
		// Verify that the IO sample has digital values.
		assertTrue(ioSample.hasDigitalValues());
		
		// Try to get the values of all the DIO lines, only DIO0, DIO4 and 
		// DIO9 should have digital values, verify them.
		for (IOLine ioLine:IOLine.values()) {
			if (ioLine == IOLine.DIO0_AD0) {
				assertTrue(ioSample.hasDigitalValue(ioLine));
				assertEquals(IOValue.HIGH, ioSample.getDigitalValue(ioLine));
				assertEquals(IOValue.HIGH, ioSample.getDigitalValues().get(ioLine));
			} else if (ioLine == IOLine.DIO4_AD4) {
				assertTrue(ioSample.hasDigitalValue(ioLine));
				assertEquals(IOValue.LOW, ioSample.getDigitalValue(ioLine));
				assertEquals(IOValue.LOW, ioSample.getDigitalValues().get(ioLine));
			} else if (ioLine == IOLine.DIO9) {
				assertTrue(ioSample.hasDigitalValue(ioLine));
				assertEquals(IOValue.HIGH, ioSample.getDigitalValue(ioLine));
				assertEquals(IOValue.HIGH, ioSample.getDigitalValues().get(ioLine));
			} else {
				assertFalse(ioSample.hasDigitalValue(ioLine));
				assertNull(ioSample.getDigitalValue(ioLine));
				assertNull(ioSample.getDigitalValues().get(ioLine));
			}
		}
	}
	
	/**
	 * Verify that analog values are successfully parsed and stored in the sample 
	 * when it contains digital, analog and power supply information.
	 */
	@Test
	public void testGetAnalogValuesFromMixedData() {
		// Create an IO sample with mixed (digital + analog) data.
		IOSample ioSample = new IOSample(IO_DATA_MIXED);
		
		// Verify that the analog mask is correct.
		assertEquals(ANALOG_MASK, ioSample.getAnalogMask());
		
		// Verify that the IO sample has analog values.
		assertTrue(ioSample.hasAnalogValues());
		
		// Try to get the values of all the DIO lines, only DIO1 and DIO3 lines 
		// should have analog values, verify them.
		for (IOLine ioLine:IOLine.values()) {
			if (ioLine == IOLine.DIO1_AD1) {
				assertTrue(ioSample.hasAnalogValue(ioLine));
				assertEquals(DIO1_ANALOG_VALUE, (int)ioSample.getAnalogValue(ioLine));
				assertEquals(DIO1_ANALOG_VALUE, (int)ioSample.getAnalogValues().get(ioLine));
			} else if (ioLine == IOLine.DIO3_AD3) {
				assertTrue(ioSample.hasAnalogValue(ioLine));
				assertEquals(DIO3_ANALOG_VALUE, (int)ioSample.getAnalogValue(ioLine));
				assertEquals(DIO3_ANALOG_VALUE, (int)ioSample.getAnalogValues().get(ioLine));
			} else {
				assertFalse(ioSample.hasAnalogValue(ioLine));
				assertNull(ioSample.getAnalogValue(ioLine));
				assertNull(ioSample.getAnalogValues().get(ioLine));
			}
		}
	}
	
	/**
	 * Verify that power supply value is successfully parsed and stored in the sample 
	 * when it contains digital, analog and power supply information.
	 * 
	 * @throws OperationNotSupportedException 
	 */
	@Test
	public void testGetPowerSupplyValueFromMixedData() throws OperationNotSupportedException {
		// Create an IO sample with mixed (digital + analog) data.
		IOSample ioSample = new IOSample(IO_DATA_MIXED);
		
		// Verify that the IO sample has power supply value.
		assertTrue(ioSample.hasPowerSupplyValue());
			
		assertEquals(POWER_SUPPLY_VALUE, ioSample.getPowerSupplyValue());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.io.IOSample#toString()}.
	 * 
	 * @throws OperationNotSupportedException 
	 */
	@Test
	public void testToString() throws OperationNotSupportedException {
		IOSample[] samples = new IOSample[] {new IOSample(IO_DATA_ONLY_DIGITAL), 
				new IOSample(IO_DATA_ONLY_ANALOG), 
				new IOSample(IO_DATA_MIXED)};
		for (IOSample s:samples) {
			String result = s.toString();
			if(s.hasDigitalValues()) {
				HashMap<IOLine, IOValue> map = s.getDigitalValues();
				for (IOLine l: map.keySet()) {
					String entry = "[" + l + ": " + map.get(l) + "]";
					assertEquals("toString() method does not produce the expected output",
							true, result.contains(entry));
				}
			}
			
			if(s.hasAnalogValues()) {
				HashMap<IOLine, Integer> map = s.getAnalogValues();
				for (IOLine l: map.keySet()) {
					String entry = "[" + l + ": " + map.get(l) + "]";
					assertEquals("toString() method does not produce the expected output",
							true, result.contains(entry));
				}
			}
			
			if(s.hasPowerSupplyValue()) {
				String entry = "[Power supply voltage: " + s.getPowerSupplyValue() + "]";
				assertEquals("toString() method does not produce the expected output",
						true, result.contains(entry));
			}
			
			assertEquals("toString() method does not produce the expected output",
					true, result.startsWith("{") & result.endsWith("}"));
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.io.IOSample#getDigitalValues()}.
	 * 
	 * @throws OperationNotSupportedException 
	 */
	@Test
	public void testGetDigitalValuesModify() {
		// Setup the resources for the test.
		IOSample ioSample = new IOSample(IO_DATA_MIXED);
		
		// Call the method under test.
		HashMap<IOLine, IOValue> values = ioSample.getDigitalValues();
		HashMap<IOLine, IOValue> backup = new IOSample(IO_DATA_MIXED).getDigitalValues();
		
		Iterator<IOLine> it = values.keySet().iterator();
		
		while (it.hasNext()) {
			IOLine line = it.next();
			
			if (values.get(line) == IOValue.HIGH)
				values.put(line, IOValue.LOW);
		}
		
		HashMap<IOLine, IOValue> result = ioSample.getDigitalValues();
		
		// Verify the result.
		assertThat(result, is(equalTo(backup)));
		assertThat(result.hashCode(), is(equalTo(backup.hashCode())));
		assertThat(result.hashCode(), is(not(equalTo(values.hashCode()))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.io.IOSample#getDigitalValues()}.
	 * 
	 * @throws OperationNotSupportedException 
	 */
	@Test
	public void testGetAnalogValuesModify() {
		// Setup the resources for the test.
		IOSample ioSample = new IOSample(IO_DATA_MIXED);
		
		// Call the method under test.
		HashMap<IOLine, Integer> values = ioSample.getAnalogValues();
		HashMap<IOLine, Integer> backup = new IOSample(IO_DATA_MIXED).getAnalogValues();
		
		Iterator<IOLine> it = values.keySet().iterator();
		
		while (it.hasNext()) {
			IOLine line = it.next();
			values.put(line, values.get(line) + 10);
		}
		
		HashMap<IOLine, Integer> result = ioSample.getAnalogValues();
		
		// Verify the result.
		assertThat(result, is(equalTo(backup)));
		assertThat(result.hashCode(), is(equalTo(backup.hashCode())));
		assertThat(result.hashCode(), is(not(equalTo(values.hashCode()))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.io.IOSample#getDigitalHSBMask()}.
	 */
	@Test
	public void testGetDigitalHSBMask() {
		// Setup the resources for the test.
		IOSample ioSample = new IOSample(IO_DATA_ONLY_DIGITAL);
		int expected = IO_DATA_ONLY_DIGITAL[1] & 0x7F;
		
		// Call the method under test.
		int result = ioSample.getDigitalHSBMask();
		
		// Verify the result.
		assertThat(result, is(equalTo(expected)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.io.IOSample#getDigitalLSBMask()}.
	 */
	@Test
	public void testGetDigitalLSBMask() {
		// Setup the resources for the test.
		IOSample ioSample = new IOSample(IO_DATA_ONLY_DIGITAL);
		int expected = IO_DATA_ONLY_DIGITAL[2] & 0xFF;
		
		// Call the method under test.
		int result = ioSample.getDigitalLSBMask();
		
		// Verify the result.
		assertThat(result, is(equalTo(expected)));
	}
}

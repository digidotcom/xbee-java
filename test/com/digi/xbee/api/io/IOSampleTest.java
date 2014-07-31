/**
* Copyright (c) 2014 Digi International Inc.,
* All rights not expressly granted are reserved.
*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/.
*
* Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
* =======================================================================
*/
package com.digi.xbee.api.io;

import static org.junit.Assert.*;

import org.junit.Test;

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
	 * DIO1 and DIO3 are configured as ADC.
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
	 */
	private static final byte[] IO_DATA_ONLY_DIGITAL = new byte[]{0x01, 0x02, 0x11, 0x00, 0x02, 0x01};
	private static final byte[] IO_DATA_ONLY_ANALOG = new byte[]{0x01, 0x00, 0x00, 0x0A, 0x02, 0x0C, 0x00, (byte)0xFA};
	private static final byte[] IO_DATA_MIXED = new byte[]{0x01, 0x02, 0x11, 0x0A, 0x02, 0x01, 0x02, 0x0C, 0x00, (byte)0xFA};
	
	private static final int DIO1_ANALOG_VALUE = 524;
	private static final int DIO3_ANALOG_VALUE = 250;
	
	private static final int DIGITAL_MASK = 529; // 0x0211
	private static final int ANALOG_MASK = 10; // 0x000A
	
	@Test
	/**
	 * Verify that the IOSample object is not correctly instantiated when the IO data byte 
	 * array is not valid.
	 */
	public void testIOSampleInvalidData() {
		// Instantiate an IOSample object with invalid IO data.
		try {
			new IOSample(INVALID_IO_DATA);
			fail("IO sample shouldn't have been instantiated correctly.");
		} catch (Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
		
		// Instantiate an IOSample object with null IO data.
		try {
			new IOSample(INVALID_IO_DATA);
			fail("IO sample shouldn't have been instantiated correctly.");
		} catch (Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
	}
	
	@Test
	/**
	 * Verify that 
	 */
	public void testIOSampleOnlyDigitalData() {
		// Create an IO sample with the digital data.
		IOSample ioSample = new IOSample(IO_DATA_ONLY_DIGITAL);
		
		// Verify that the digital mask is correct.
		assertEquals(DIGITAL_MASK, ioSample.getDigitalMask());
		
		// Verify that the analog mask is 0.
		assertEquals(0, ioSample.getAnalogMask());
		
		// Verify that the IO sample has digital values.
		assertTrue(ioSample.hasDigitalValues());
		
		// Verify that the IO sample does not have analog values.
		assertFalse(ioSample.hasAnalogValues());
		
		// Try to get the values of all the DIO lines, only DIO0, DIO4 and 
		// DIO9 should have digital values, verify them.
		for (IOLine ioLine:IOLine.values()) {
			if (ioLine == IOLine.DIO0_AD0)
				assertEquals(ioSample.getDigitalValues().get(ioLine), IOValue.HIGH);
			else if (ioLine == IOLine.DIO4_AD4)
				assertEquals(ioSample.getDigitalValues().get(ioLine), IOValue.LOW);
			else if (ioLine == IOLine.DIO9)
				assertEquals(ioSample.getDigitalValues().get(ioLine), IOValue.HIGH);
			else
				assertNull(ioSample.getDigitalValues().get(ioLine));
		}
		
		// Verify that there is not any analog value.
		for (IOLine ioLine:IOLine.values())
			assertNull(ioSample.getAnalogValues().get(ioLine));
	}
	
	@Test
	public void testIOSampleOnlyAnalogData() {
		// Create an IO sample with the analog data.
		IOSample ioSample = new IOSample(IO_DATA_ONLY_ANALOG);
		
		// Verify that the digital mask is 0.
		assertEquals(0, ioSample.getDigitalMask());
		
		// Verify that the analog mask is correct.
		assertEquals(ANALOG_MASK, ioSample.getAnalogMask());
		
		// Verify that the IO sample does not have digital values.
		assertFalse(ioSample.hasDigitalValues());
		
		// Verify that the IO sample has analog values. 
		assertTrue(ioSample.hasAnalogValues());
		
		// Try to get the values of all the DIO lines, only DIO1 and DIO3 lines 
		// should have analog values, verify them.
		for (IOLine ioLine:IOLine.values()) {
			if (ioLine == IOLine.DIO1_AD1)
				assertEquals(DIO1_ANALOG_VALUE, (int)ioSample.getAnalogValues().get(ioLine));
			else if (ioLine == IOLine.DIO3_AD3)
				assertEquals(DIO3_ANALOG_VALUE, (int)ioSample.getAnalogValues().get(ioLine));
			else
				assertNull(ioSample.getAnalogValues().get(ioLine));
		}
		
		// Verify that there is not any digital value.
		for (IOLine ioLine:IOLine.values())
			assertNull(ioSample.getDigitalValues().get(ioLine));
	}
	
	@Test
	public void testIOSampleMixedData() {
		// Create an IO sample with mixed (digital + analog) data.
		IOSample ioSample = new IOSample(IO_DATA_MIXED);
		
		// Verify that the digital mask is correct.
		assertEquals(DIGITAL_MASK, ioSample.getDigitalMask());
		
		// Verify that the analog mask is correct.
		assertEquals(ANALOG_MASK, ioSample.getAnalogMask());
		
		// Verify that the IO sample has digital values.
		assertTrue(ioSample.hasDigitalValues());
		
		// Verify that the IO sample has analog values. 
		assertTrue(ioSample.hasAnalogValues());
		
		// Try to get the values of all the DIO lines, only DIO0, DIO4 and 
		// DIO9 should have digital values, verify them.
		for (IOLine ioLine:IOLine.values()) {
			if (ioLine == IOLine.DIO0_AD0)
				assertEquals(ioSample.getDigitalValues().get(ioLine), IOValue.HIGH);
			else if (ioLine == IOLine.DIO4_AD4)
				assertEquals(ioSample.getDigitalValues().get(ioLine), IOValue.LOW);
			else if (ioLine == IOLine.DIO9)
				assertEquals(ioSample.getDigitalValues().get(ioLine), IOValue.HIGH);
			else
				assertNull(ioSample.getDigitalValues().get(ioLine));
		}
		
		// Try to get the values of all the DIO lines, only DIO1 and DIO3 lines 
		// should have analog values, verify them.
		for (IOLine ioLine:IOLine.values()) {
			if (ioLine == IOLine.DIO1_AD1)
				assertEquals(DIO1_ANALOG_VALUE, (int)ioSample.getAnalogValues().get(ioLine));
			else if (ioLine == IOLine.DIO3_AD3)
				assertEquals(DIO3_ANALOG_VALUE, (int)ioSample.getAnalogValues().get(ioLine));
			else
				assertNull(ioSample.getAnalogValues().get(ioLine));
		}
	}
}

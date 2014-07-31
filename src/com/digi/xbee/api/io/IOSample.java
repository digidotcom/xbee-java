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

import java.util.HashMap;

import com.digi.xbee.api.utils.ByteUtils;

/**
 * This class represents an IO Data Sample. The sample is built using the parameters of 
 * the constructor.
 * 
 * Digital Channel Mask
 * ------------------------------------------------------------------
 * Indicates which digital IO lines are configured in the module. Each
 * bit corresponds to one digital IO line on the module:
 * 
 * 		bit 0 = AD0/DIO0 
 * 		bit 1 = AD1/DIO1 
 * 		bit 2 = AD2/DIO2 
 * 		bit 3 = AD3/DIO3 
 * 		bit 4 = DIO4 
 * 		bit 5 = ASSOC/DIO5
 * 		bit 6 = RTS/DIO6
 * 		bit 7 = CTS/GPIO7
 * 		bit 8 = DTR / SLEEP_RQ / DIO8 
 * 		bit 9 = ON_SLEEP / DIO9 
 * 		bit 10 = RSSI/DIO10
 * 		bit 11 = PWM/DIO11
 * 		bit 12 = CD/DIO12
 * 		bit 13 = DIO13
 * 		bit 14 = DIO14
 * 		bit 15 = N/A
 * 
 * 		Example: mask of 0x040B means DIO0, DIO1, DIO2, DIO3 and DIO10 enabled.
 * 		0 0 0 0 0 1 0 0 0 0 0 0 1 0 1 1
 * 
 * Analog Channel Mask
 * -----------------------------------------------------------------------
 * Indicates which lines are configured as ADC. Each bit in the analog channel 
 * mask corresponds to one ADC line on the module.
 * 
 * 		bit 0 = AD0/DIO0
 * 		bit 1 = AD1/DIO1
 * 		bit 2 = AD2/DIO2
 * 		bit 3 = AD3/DIO3
 * 		bit 4 = AD4/DIO4
 * 		bit 5 = ASSOC/AD5/DIO5
 * 		bit 6 = N/A
 * 		bit 7 = Supply Voltage Value
 * 
 * 		Example: mask of 0x83 means AD0, and AD1 enabled.
 * 		0 0 0 0 0 0 1 1
 * 
 * 		Analog reads are encapsulated using the AnalogRead object.
 * 
 * @see AnalogRead
 */
public class IOSample {
	
	// Variables.
	private final byte[] ioSampleBytes;
	
	private int digitalHSBMask;
	private int digitalLSBMask;
	private int digitalMask;
	private int analogMask;
	private int digitalHSBValues;
	private int digitalLSBValues;
	private int digitalValues;
	
	private final HashMap<IOLine, Integer> analogValuesMap = new HashMap<IOLine, Integer>();
	private final HashMap<IOLine, IOValue> digitalValuesMap = new HashMap<IOLine, IOValue>();
	
	/**
	 * Class constructor. Instances a new object of type {@code IOSample} with the given 
	 * IO sample byte array.
	 * 
	 * @param ioSampleBytes The byte array corresponding to an IO sample.
	 */
	public IOSample(byte[] ioSampleBytes) {
		if (ioSampleBytes == null)
			throw new NullPointerException("IO sample byte array cannot be null.");
		
		if (ioSampleBytes.length < 5)
			throw new IllegalArgumentException("IO sample byte array must be longer than 4.");
		
		this.ioSampleBytes = ioSampleBytes;
		parseIOSample();
	}
	
	/**
	 * Parses the information contained in the IO sample bytes reading the value of 
	 * each configured DIO and ADC.
	 */
	private void parseIOSample() {
		int dataIndex = 4;
		
		// Obtain the digital masks.                        // Available digital IOs
		digitalHSBMask = ioSampleBytes[1] & 0x7F;	// 0 1 1 1 1 1 1 1
		digitalLSBMask = ioSampleBytes[2] & 0xFF;	// 1 1 1 1 1 1 1 1
		// Combine the masks mask.
		digitalMask = (digitalHSBMask << 8) + digitalLSBMask;
		// Obtain the analog masks.                         // Available analog IOs
		analogMask = ioSampleBytes[3] & 0xBF;		// 1 0 1 1 1 1 1 1
		
		// Read the digital values (if any). There are 16 possible digital lines.
		if (digitalMask > 0) {
			// Obtain the digital values.
			digitalHSBValues = ioSampleBytes[4] & 0x7F;
			digitalLSBValues = ioSampleBytes[5] & 0xFF;
			// Combine the values.
			digitalValues = (digitalHSBValues << 8) + digitalLSBValues;
			
			for (int i = 0; i < 16; i++) {
				if (!ByteUtils.isBitEnabled(digitalMask, i))
					continue;
				if (ByteUtils.isBitEnabled(digitalValues, i))
					digitalValuesMap.put(IOLine.getDIO(i), IOValue.HIGH);
				else
					digitalValuesMap.put(IOLine.getDIO(i), IOValue.LOW);
			}
			// Increase the data index to read the analog values.
			dataIndex += 2;
		}
		
		// Read the analog values (if any). There are 6 possible analog lines.
		int adcIndex = 0;
		while ((ioSampleBytes.length - dataIndex) > 1 && adcIndex < 6) {
			if (!ByteUtils.isBitEnabled(analogMask, adcIndex)) {
				adcIndex += 1;
				continue;
			}
			analogValuesMap.put(IOLine.getDIO(adcIndex), ((ioSampleBytes[dataIndex] & 0xFF) << 8) + (ioSampleBytes[dataIndex + 1] & 0xFF));
			// Increase the data index to read the next analog values.
			dataIndex += 2;
			adcIndex += 1;
		}
		// TODO: Read supply voltage?
	}
	
	/**
	 * Retrieves the HSB of the digital mask.
	 * 
	 * @return HSB of the digital mask.
	 */
	public int getDigitalHSBMask() {
		return digitalHSBMask;
	}
	
	/**
	 * Retrieves the LSB of the digital mask.
	 * 
	 * @return LSB of the digital mask.
	 */
	public int getDigitalLSBMask() {
		return digitalLSBMask;
	}
	
	/**
	 * Retrieves the combined (HSB + LSB) digital mask.
	 * 
	 * @return The combined digital mask.
	 */
	public int getDigitalMask() {
		return digitalMask;
	}
	
	/**
	 * Checks whether or not the IOSample has digital values.
	 *  
	 * @return True if there are digital values, false otherwise.
	 */
	public boolean hasDigitalValues() {
		return digitalValuesMap.size() > 0;
	}
	
	/**
	 * Retrieves the digital values map.
	 * 
	 * @return HashMap with the digital value of each configured IO line.
	 */
	public HashMap<IOLine, IOValue> getDigitalValues() {
		return digitalValuesMap;
	}
	
	/**
	 * Retrieves the analog mask.
	 * 
	 * @return Analog mask.
	 */
	public int getAnalogMask() {
		return analogMask;
	}
	
	/**
	 * Retrieves whether or not the IOSample has analog values.
	 *  
	 * @return True if there are analog values, false otherwise.
	 */
	public boolean hasAnalogValues() {
		return analogValuesMap.size() > 0;
	}
	
	/**
	 * Retrieves the analog values map.
	 * 
	 * @return HashMap with the analog value of each configured IO line.
	 */
	public HashMap<IOLine, Integer> getAnalogValues() {
		return analogValuesMap;
	}
}

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

import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.utils.ByteUtils;

/**
 * This class represents an IO Data Sample. The sample is built using the parameters of 
 * the constructor. The sample contains an analog and digital mask indicating which IO lines 
 * are configured with that functionality.
 * 
 * <p>Depending on the protocol the XBee device is executing, the digital and analog masks are 
 * retrieved in separated bytes (2 bytes for the digital mask and 1 for the analog mask) or 
 * merged contained (digital and analog masks are contained in 2 bytes).</p> 
 * <br>
 * <p><b>802.15.4 Protocol</b></p>
 * <br>
 * <p>Digital and analog channels masks</p>
 * <p>------------------------------------------------------------------</p>
 * <p>Indicates which digital and ADC IO lines are configured in the module. Each
 * bit corresponds to one digital or ADC IO line on the module:</p>
 * <br>
 * <BLOCKQUOTE>
 *      <p>bit 0 =  DIO0</p>1
 *      <p>bit 1 =  DIO1</p>0
 *      <p>bit 2 =  DIO2</p>0
 *      <p>bit 3 =  DIO3</p>1
 *      <p>bit 4 =  DIO4</p>0
 *      <p>bit 5 =  DIO5</p>1
 *      <p>bit 6 =  DIO6</p>0
 *      <p>bit 7 =  DIO7</p>0
 *      <p>bit 8 =  DIO8</p>0
 *      <p>bit 9 =  AD0</p>0
 *      <p>bit 10 = AD1</p>1
 *      <p>bit 11 = AD2</p>1
 *      <p>bit 12 = AD3</p>0
 *      <p>bit 13 = AD4</p>0
 *      <p>bit 14 = AD5</p>0
 *      <p>bit 15 = N/A</p>0
 * <br>
 *      <p>Example: mask of {@code 0x0C29} means DIO0, DIO3, DIO5, AD1 and AD2 enabled.</p>
 *      <p>0 0 0 0 1 1 0 0 0 0 1 0 1 0 0 1</p>
 * </BLOCKQUOTE>
 * <br><br>
 * <p><b>Other Protocols</b></p>
 * <br>
 * <p>Digital Channel Mask</p>
 * <p>------------------------------------------------------------------</p>
 * <p>Indicates which digital IO lines are configured in the module. Each
 * bit corresponds to one digital IO line on the module:</p>
 * <br>
 * <BLOCKQUOTE>
 *      <p>bit 0 =  DIO0/AD0</p>
 *      <p>bit 1 =  DIO1/AD1</p> 
 *      <p>bit 2 =  DIO2/AD2</p>
 *      <p>bit 3 =  DIO3/AD3</p>
 *      <p>bit 4 =  DIO4/AD4</p>
 *      <p>bit 5 =  DIO5/AD5/ASSOC</p>
 *      <p>bit 6 =  DIO6/RTS</p>
 *      <p>bit 7 =  DIO7/CTS</p>
 *      <p>bit 8 =  DIO8/DTR/SLEEP_RQ</p>
 *      <p>bit 9 =  DIO9/ON_SLEEP</p>
 *      <p>bit 10 = DIO10/PWM0/RSSI</p>
 *      <p>bit 11 = DIO11/PWM1</p>
 *      <p>bit 12 = DIO12/CD</p>
 *      <p>bit 13 = DIO13</p>
 *      <p>bit 14 = DIO14</p>
 *      <p>bit 15 = N/A</p>
 * <br>
 *      <p>Example: mask of {@code 0x040B} means DIO0, DIO1, DIO2, DIO3 and DIO10 enabled.</p>
 *      <p>0 0 0 0 0 1 0 0 0 0 0 0 1 0 1 1</p>
 * <br><br>
 * </BLOCKQUOTE>
 * <p>Analog Channel Mask</p>
 * <p>-----------------------------------------------------------------------</p>
 * <p>Indicates which lines are configured as ADC. Each bit in the analog channel 
 * mask corresponds to one ADC line on the module.</p>
 * <br>
 * <BLOCKQUOTE>
 *      <p>bit 0 = AD0/DIO0</p>
 *      <p>bit 1 = AD1/DIO1</p>
 *      <p>bit 2 = AD2/DIO2</p>
 *      <p>bit 3 = AD3/DIO3</p>
 *      <p>bit 4 = AD4/DIO4</p>
 *      <p>bit 5 = AD5/DIO5/ASSOC</p>
 *      <p>bit 6 = N/A</p>
 *      <p>bit 7 = Supply Voltage Value</p>
 * <br>
 *      <p>Example: mask of {@code 0x83} means AD0, and AD1 enabled.</p>
 *      <p>0 0 0 0 0 0 1 1</p>
 * </BLOCKQUOTE>
 */
public class IOSample {
	
	// Variables.
	private final byte[] ioSamplePayload;
	
	private int digitalHSBMask;
	private int digitalLSBMask;
	private int digitalMask;
	private int analogMask;
	private int digitalHSBValues;
	private int digitalLSBValues;
	private int digitalValues;
	private int powerSupplyVoltage;
	
	private final HashMap<IOLine, Integer> analogValuesMap = new HashMap<IOLine, Integer>();
	private final HashMap<IOLine, IOValue> digitalValuesMap = new HashMap<IOLine, IOValue>();
	
	/**
	 * Class constructor. Instances a new object of type {@code IOSample} with the given 
	 * IO sample payload.
	 * 
	 * @param ioSamplePayload The payload corresponding to an IO sample.
	 * 
	 * @throws NullPointerException if {@code ioSamplePayload == null}.
	 * @throws IllegalArgumentException if {@code ioSamplePayload.length < 4}.
	 */
	public IOSample(byte[] ioSamplePayload) {
		if (ioSamplePayload == null)
			throw new NullPointerException("IO sample payload cannot be null.");
		
		if (ioSamplePayload.length < 5)
			throw new IllegalArgumentException("IO sample payload must be longer than 4.");
		
		this.ioSamplePayload = ioSamplePayload;
		if (ioSamplePayload.length % 2 != 0)
			parseRawIOSample();
		else
			parseIOSample();
	}
	
	/**
	 * Parses the information contained in the IO sample bytes reading the value of 
	 * each configured DIO and ADC.
	 */
	private void parseRawIOSample() {
		int dataIndex = 3;
		
		// Obtain the digital mask.                 // Available digital IOs in 802.15.4
		digitalHSBMask = ioSamplePayload[1] & 0x01;	// 0 0 0 0 0 0 0 1
		digitalLSBMask = ioSamplePayload[2] & 0xFF;	// 1 1 1 1 1 1 1 1
		// Combine the masks.
		digitalMask = (digitalHSBMask << 8) + digitalLSBMask;
		// Obtain the analog mask.                                              // Available analog IOs in 802.15.4
		analogMask = ((ioSamplePayload[1] <<8) + ioSamplePayload[2]) & 0x7E00;	// 0 1 1 1 1 1 1 0 0 0 0 0 0 0 0 0
		
		// Read the digital values (if any). There are 9 possible digital lines in 
		// 802.15.4 protocol. The digital mask indicates if there is any digital 
		// line enabled to read its value. If 0, no digital values are received.
		if (digitalMask > 0) {
			// Obtain the digital values.
			digitalHSBValues = ioSamplePayload[3] & 0x7F;
			digitalLSBValues = ioSamplePayload[4] & 0xFF;
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
		// The analog mask indicates if there is any analog line enabled to read 
		// its value. If 0, no analog values are received.
		int adcIndex = 9;
		while ((ioSamplePayload.length - dataIndex) > 1 && adcIndex < 16) {
			if (!ByteUtils.isBitEnabled(analogMask, adcIndex)) {
				adcIndex += 1;
				continue;
			}
			// 802.15.4 protocol does not provide power supply value, so get just the ADC data.
			analogValuesMap.put(IOLine.getDIO(adcIndex - 9), ((ioSamplePayload[dataIndex] & 0xFF) << 8) + (ioSamplePayload[dataIndex + 1] & 0xFF));
			// Increase the data index to read the next analog values.
			dataIndex += 2;
			adcIndex += 1;
		}
	}
	
	/**
	 * Parses the information contained in the IO sample bytes reading the value of 
	 * each configured DIO and ADC.
	 */
	private void parseIOSample() {
		int dataIndex = 4;
		
		// Obtain the digital masks.                // Available digital IOs
		digitalHSBMask = ioSamplePayload[1] & 0x7F;	// 0 1 1 1 1 1 1 1
		digitalLSBMask = ioSamplePayload[2] & 0xFF;	// 1 1 1 1 1 1 1 1
		// Combine the masks.
		digitalMask = (digitalHSBMask << 8) + digitalLSBMask;
		// Obtain the analog mask.                  // Available analog IOs
		analogMask = ioSamplePayload[3] & 0xBF;		// 1 0 1 1 1 1 1 1
		
		// Read the digital values (if any). There are 16 possible digital lines.
		// The digital mask indicates if there is any digital line enabled to read 
		// its value. If 0, no digital values are received.
		if (digitalMask > 0) {
			// Obtain the digital values.
			digitalHSBValues = ioSamplePayload[4] & 0x7F;
			digitalLSBValues = ioSamplePayload[5] & 0xFF;
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
		// The analog mask indicates if there is any analog line enabled to read 
		// its value. If 0, no analog values are received.
		int adcIndex = 0;
		while ((ioSamplePayload.length - dataIndex) > 1 && adcIndex < 8) {
			if (!ByteUtils.isBitEnabled(analogMask, adcIndex)) {
				adcIndex += 1;
				continue;
			}
			// When analog index is 7, it means that the analog value corresponds to the power 
			// supply voltage, therefore this value should be stored in a different value.
			if (adcIndex == 7)
				powerSupplyVoltage = ((ioSamplePayload[dataIndex] & 0xFF) << 8) + (ioSamplePayload[dataIndex + 1] & 0xFF);
			else
				analogValuesMap.put(IOLine.getDIO(adcIndex), ((ioSamplePayload[dataIndex] & 0xFF) << 8) + (ioSamplePayload[dataIndex + 1] & 0xFF));
			// Increase the data index to read the next analog values.
			dataIndex += 2;
			adcIndex += 1;
		}
	}
	
	/**
	 * Retrieves the HSB of the digital mask.
	 * 
	 * @return HSB of the digital mask.
	 * 
	 * @see #getDigitalLSBMask()
	 * @see #getDigitalMask()
	 */
	public int getDigitalHSBMask() {
		return digitalHSBMask;
	}
	
	/**
	 * Retrieves the LSB of the digital mask.
	 * 
	 * @return LSB of the digital mask.
	 * 
	 * @see #getDigitalHSBMask()
	 * @see #getDigitalMask()
	 */
	public int getDigitalLSBMask() {
		return digitalLSBMask;
	}
	
	/**
	 * Retrieves the combined (HSB + LSB) digital mask.
	 * 
	 * @return The combined digital mask.
	 * 
	 * @see #getDigitalLSBMask()
	 * @see #getDigitalHSBMask()
	 */
	public int getDigitalMask() {
		return digitalMask;
	}
	
	/**
	 * Checks whether or not the {@code IOSample} has digital values.
	 * 
	 * @return {@code true} if there are digital values, {@code false} otherwise.
	 */
	public boolean hasDigitalValues() {
		return digitalValuesMap.size() > 0;
	}
	
	/**
	 * Retrieves whether or not the given IO line has a digital value.
	 * 
	 * @param ioLine The IO line to check if has a digital value.
	 * @return {@code true} if the given IO line has a digital value, {@code false} otherwise.
	 * 
	 * @see IOLine
	 */
	public boolean hasDigitalValue(IOLine ioLine) {
		return digitalValuesMap.containsKey(ioLine);
	}
	
	/**
	 * Retrieves the digital values map.
	 * 
	 * @return {@code HashMap} with the digital value of each configured IO line.
	 * 
	 * @see #getDigitalValue(IOLine)
	 * @see IOLine
	 * @see IOValue
	 */
	public HashMap<IOLine, IOValue> getDigitalValues() {
		return digitalValuesMap;
	}
	
	/**
	 * Retrieves the digital value of the provided IO line.
	 * 
	 * @param ioLine The IO line to get its digital value.
	 * @return The IOValue of the given IO line. 
	 * @throws IllegalArgumentException if the given IO line does not have 
	 *                                  an associated digital value.
	 * 
	 * @see IOLine
	 * @see IOValue
	 */
	public IOValue getDigitalValue(IOLine ioLine) {
		if (!digitalValuesMap.containsKey(ioLine))
			throw new IllegalArgumentException(ioLine.getName() + " does not have a digital value.");
		return digitalValuesMap.get(ioLine);
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
	 * Retrieves whether or not the {@code IOSample} has analog values.
	 *  
	 * @return {@code true} if there are analog values, {@code false} otherwise.
	 * 
	 * @see #getAnalogValues()
	 * @see IOLine
	 */
	public boolean hasAnalogValues() {
		return analogValuesMap.size() > 0;
	}
	
	/**
	 * Retrieves whether or not the given IO line has an analog value.
	 * 
	 * @param ioLine The IO line to check if has an analog value.
	 * @return {@code true} if the given IO line has an analog value, {@code false} otherwise.
	 * 
	 * @see IOLine
	 */
	public boolean hasAnalogValue(IOLine ioLine) {
		return analogValuesMap.containsKey(ioLine);
	}
	
	/**
	 * Retrieves the analog values map.
	 * 
	 * @return {@code HashMap} with the analog value of each configured IO line.
	 * 
	 * @see #hasAnalogValues()
	 * @see #getAnalogValue()
	 * @see IOLine
	 */
	public HashMap<IOLine, Integer> getAnalogValues() {
		return analogValuesMap;
	}
	
	/**
	 * Retrieves the analog value of the provided IO line.
	 * 
	 * @param ioLine The IO line to get its analog value.
	 * @return The analog value of the given IO line. 
	 * @throws IllegalArgumentException if the given IO line does not have 
	 *                                  an associated analog value.
	 * 
	 * @see IOLine
	 */
	public int getAnalogValue(IOLine ioLine) {
		if (!analogValuesMap.containsKey(ioLine))
			throw new IllegalArgumentException(ioLine.getName() + " does not have an analog value.");
		return analogValuesMap.get(ioLine);
	}
	
	/**
	 * Retrieves whether or not the IOSample has power supply value.
	 * 
	 * @return {@code true} if the IOSample has power supply value, {@code false} otherwise.
	 */
	public boolean hasPowerSupplyValue() {
		return ByteUtils.isBitEnabled(analogMask, 7);
	}
	
	/**
	 * Retrieves the value of the power supply voltage.
	 * 
	 * @return The value of the power supply voltage.
	 * @throws OperationNotSupportedException if the IOSample does not have power supply value.
	 */
	public int getPowerSupplyValue() throws OperationNotSupportedException {
		if (!ByteUtils.isBitEnabled(analogMask, 7))
			throw new OperationNotSupportedException();
		return powerSupplyVoltage;
	}
}

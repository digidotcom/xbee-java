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

import java.util.HashMap;

/**
 * Enumerates the different IO lines that can be found in the XBee devices. 
 * 
 * <p>Depending on the hardware and firmware of the device, the number of lines 
 * that can be used as well as their functionality may vary. Refer to the 
 * product manual to learn more about the IO lines of your XBee device.</p>
 */
public enum IOLine {

	// Enumeration types.
	DIO0_AD0("DIO0/AD0", 0, "D0", null),
	DIO1_AD1("DIO1/AD1", 1, "D1", null),
	DIO2_AD2("DIO2/AD2", 2, "D2", null),
	DIO3_AD3("DIO3/AD3", 3, "D3", null),
	DIO4_AD4("DIO4/AD4", 4, "D4", null),
	DIO5_AD5("DIO5/AD5", 5, "D5", null),
	DIO6("DIO6", 6, "D6", null),
	DIO7("DIO7", 7, "D7", null),
	DIO8("DIO8", 8, "D8", null),
	DIO9("DIO9", 9, "D9", null),
	DIO10_PWM0("DIO10/PWM0", 10, "P0", "M0"),
	DIO11_PWM1("DIO11/PWM1", 11, "P1", "M1"),
	DIO12("DIO12", 12, "P2", null),
	DIO13("DIO13", 13, "P3", null),
	DIO14("DIO14", 14, "P4", null),
	DIO15("DIO15", 15, "P5", null),
	DIO16("DIO16", 16, "P6", null),
	DIO17("DIO17", 17, "P7", null),
	DIO18("DIO18", 18, "P8", null),
	DIO19("DIO19", 19, "P9", null);
	
	// Variables.
	private final static HashMap <Integer, IOLine> lookupTableIndex = new HashMap<Integer, IOLine>();
	
	private final String name;
	private final String atCommand;
	private final String atPWMCommand;
	
	private final int index;
	
	static {
		for (IOLine dio:values())
			lookupTableIndex.put(dio.getIndex(), dio);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code IOLine} enumeration entry 
	 * with the given parameters.
	 * 
	 * @param name The name of the IO line.
	 * @param index The index associated to the IO line.
	 * @param atCommand The AT command corresponding to the IO line.
	 * @param atPWMCommand The PWM AT command corresponding to the IO line 
	 *                     (if any).
	 */
	private IOLine(String name, int index, String atCommand, String atPWMCommand) {
		this.name = name;
		this.index = index;
		this.atCommand = atCommand;
		this.atPWMCommand = atPWMCommand;
	}
	
	/**
	 * Returns the name of the IO line.
	 * 
	 * @return The name of the IO line.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the index of the IO line.
	 * 
	 * @return The index of the IO line.
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Returns the configuration AT command associated to the IO line.
	 * 
	 * @return The configuration AT command associated to the IO line.
	 */
	public String getConfigurationATCommand() {
		return atCommand;
	}
	
	/**
	 * Returns whether or not the IO line has PWM capability.
	 * 
	 * @return {@code true} if the provided IO line has PWM, {@code false} 
	 *         otherwise.
	 */
	public boolean hasPWMCapability() {
		return atPWMCommand != null;
	}
	
	/**
	 * Returns the PWM AT command associated to the IO line.
	 * 
	 * @return The PWM AT command associated to the IO line.
	 */
	public String getPWMDutyCycleATCommand() {
		return atPWMCommand;
	}
	
	/**
	 * Returns the {@code IOLine} associated to the given index.
	 * 
	 * @param index The index corresponding to the {@code IOLine} to retrieve.
	 * 
	 * @return The {@code IOLine} associated to the given index.
	 */
	public static IOLine getDIO(int index) {
		if (lookupTableIndex.containsKey(index))
			return lookupTableIndex.get(index);
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
}

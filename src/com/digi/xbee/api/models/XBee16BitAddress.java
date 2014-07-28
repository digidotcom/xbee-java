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
package com.digi.xbee.api.models;

import java.util.Arrays;

import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a 16 bit address of the XBee ZigBee protocol. In this
 * protocol, each device is assigned a 16 bit address when it joins a network.
 */
public final class XBee16BitAddress {

	// Constants
	public static final XBee16BitAddress BROADCAST_ADDRESS = new XBee16BitAddress("FFFF");
	public static final XBee16BitAddress UNKNOWN_ADDRESS = new XBee16BitAddress("FFFE");
	
	private static final String XBEE_16_BIT_ADDRESS_PATTERN = "(0[xX])?[0-9a-fA-F]{1,4}";
	
	private static final int HASH_SEED = 23;
	
	// Variables
	private final int hsb;
	private final int lsb;
	
	/**
	 * Class constructor. Instances a new object of type XBee16BitAddress
	 * with the given parameters.
	 * 
	 * @param hsb High significant byte of the address.
	 * @param lsb Low significant byte of the address.
	 * 
	 * @throws IllegalArgumentException if {@code hsb > 255} or
	 *                                  if {@code hsb < 0} or
	 *                                  if {@code lsb > 255} or
	 *                                  if {@code lsb < 0}.
	 */
	public XBee16BitAddress(int hsb, int lsb) {
		if (hsb > 255 || hsb < 0)
			throw new IllegalArgumentException("HSB must be betwwen 0 and 255.");
		if (lsb > 255 || lsb < 0)
			throw new IllegalArgumentException("LSB must be betwwen 0 and 255.");
		
		this.hsb = hsb;
		this.lsb = lsb;
	}
	
	/**
	 * Class constructor. Instances a new object of type XBee16BitAddress
	 * with the given parameters.
	 * 
	 * @param address Address as byte array.
	 * 
	 * @throws NullPointerException if {@code address == null}.
	 * @throws IllegalArgumentException if {@code address.length > 2}.
	 */
	public XBee16BitAddress(byte[] address) {
		if (address == null)
			throw new NullPointerException("Address cannot be null.");
		if (address.length > 2)
			throw new IllegalArgumentException("Address cannot contain more than 2 bytes.");
		
		// Check array size.
		if (address.length == 0) {
			hsb = 0;
			lsb = 0;
		} else if (address.length == 1) {
			hsb = 0;
			lsb = address[0];
		} else {
			hsb = address[0];
			lsb = address[1];
		}
	}
	
	/**
	 * Class constructor. Instances a new object of type XBee16BitAddress
	 * with the given parameters.
	 * 
	 * @param address String containing the address.
	 * 
	 * @throws NullPointerException if {@code address == null}.
	 * @throws IllegalArgumentException if {@code address} is not valid.
	 */
	public XBee16BitAddress(String address) {
		if (address == null)
			throw new NullPointerException("Address cannot be null.");
		if (!address.matches(XBEE_16_BIT_ADDRESS_PATTERN))
			throw new IllegalArgumentException("Address must follow this pattern: (0x)XXXX.");
		
		// Convert the string into a byte array.
		byte[] byteAddress = HexUtils.hexStringToByteArray(address);
		// Check array size.
		if (byteAddress.length == 0) {
			hsb = 0;
			lsb = 0;
		} else if (byteAddress.length == 1) {
			hsb = 0;
			lsb = byteAddress[0];
		} else {
			hsb = byteAddress[0];
			lsb = byteAddress[1];
		}
	}
	
	/**
	 * Retrieves the address high significant byte.
	 * 
	 * @return Address high significant byte.
	 */
	public int getHsb() {
		return hsb;
	}
	
	/**
	 * Retrieves the address low significant byte.
	 * 
	 * @return Address low significant byte.
	 */
	public int getLsb() {
		return lsb;
	}
	
	/**
	 * Retrieves the address value.
	 * 
	 * @return Address value as byte array.
	 */
	public byte[] getValue() {
		return new byte[] {(byte) hsb, (byte) lsb};
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof XBee16BitAddress))
			return false;
		XBee16BitAddress addr = (XBee16BitAddress)obj;
		return Arrays.equals(addr.getValue(), getValue());
	}
	
	@Override
	public int hashCode() {
		int hash = HASH_SEED;
		for (byte b:getValue())
			hash = hash * (hash + b);
		return hash;
	}
	
	@Override
	public String toString() {
		return HexUtils.byteArrayToHexString(new byte[] {(byte)hsb, (byte)lsb});
	}
}

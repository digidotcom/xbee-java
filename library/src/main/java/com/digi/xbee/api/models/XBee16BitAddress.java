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
package com.digi.xbee.api.models;

import java.util.Arrays;

import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a 16-bit network address.
 * 
 * <p>This address is only applicable for:</p>
 * <ul>
 *   <li>802.15.4</li>
 *   <li>ZigBee</li>
 *   <li>ZNet 2.5</li>
 *   <li>XTend (Legacy)</li>
 * </ul>
 * 
 * <p>DigiMesh and Point-to-Multipoint protocols don't support 16-bit 
 * addressing.</p>
 * 
 * <p>Each device has its own 16-bit address which is unique in the network. 
 * It is automatically assigned when the radio joins the network for ZigBee 
 * and ZNet 2.5, and manually configured in 802.15.4 radios.</p>
 * 
 */
public final class XBee16BitAddress {

	// Constants
	/**
	 * 16-bit address reserved for the coordinator (value: 0000).
	 */
	public static final XBee16BitAddress COORDINATOR_ADDRESS = new XBee16BitAddress("0000");
	/**
	 * 16-bit broadcast address (value: FFFF).
	 */
	public static final XBee16BitAddress BROADCAST_ADDRESS = new XBee16BitAddress("FFFF");
	/**
	 * 16-bit unknown address (value: FFFE).
	 */
	public static final XBee16BitAddress UNKNOWN_ADDRESS = new XBee16BitAddress("FFFE");
	
	/**
	 * Pattern for the 16-bit address string: {@value}.
	 */
	private static final String XBEE_16_BIT_ADDRESS_PATTERN = "(0[xX])?[0-9a-fA-F]{1,4}";
	
	private static final int HASH_SEED = 23;
	
	// Variables
	private final byte[] address;
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code XBee16BitAddress} with the given parameters.
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
			throw new IllegalArgumentException("HSB must be between 0 and 255.");
		if (lsb > 255 || lsb < 0)
			throw new IllegalArgumentException("LSB must be between 0 and 255.");
		
		address = new byte[2];
		address[0] = (byte) hsb;
		address[1] = (byte) lsb;
	}
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code XBee16BitAddress} with the given parameters.
	 * 
	 * @param address The 16-bit address as byte array.
	 * 
	 * @throws IllegalArgumentException if {@code address.length < 1} or
	 *                                  if {@code address.length > 2}.
	 * @throws NullPointerException if {@code address == null}.
	 */
	public XBee16BitAddress(byte[] address) {
		if (address == null)
			throw new NullPointerException("Address cannot be null.");
		if (address.length < 1)
			throw new IllegalArgumentException("Address must contain at least 1 byte.");
		if (address.length > 2)
			throw new IllegalArgumentException("Address cannot contain more than 2 bytes.");
		
		// Check array size.
		this.address = new byte[2];
		int diff = this.address.length - address.length;
		for (int i = 0; i < diff; i++)
			this.address[i] = 0;
		for (int i = diff; i < this.address.length; i++)
			this.address[i] = address[i - diff];
	}
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code XBee16BitAddress} with the given parameters.
	 * 
	 * <p>The string must be the hexadecimal representation of a 16-bit 
	 * address.</p> 
	 * 
	 * @param address String containing the 16-bit address.
	 * 
	 * @throws IllegalArgumentException if {@code address.length() < 1} or
	 *                                  if {@code address} contains 
	 *                                  non-hexadecimal characters and is longer
	 *                                  than 8 bytes.
	 * @throws NullPointerException if {@code address == null}.
	 */
	public XBee16BitAddress(String address) {
		if (address == null)
			throw new NullPointerException("Address cannot be null.");
		if (address.length() < 1)
			throw new IllegalArgumentException("Address must contain at least 1 character.");
		if (!address.matches(XBEE_16_BIT_ADDRESS_PATTERN))
			throw new IllegalArgumentException("Address must follow this pattern: (0x)XXXX.");
		
		// Convert the string into a byte array.
		byte[] byteAddress = HexUtils.hexStringToByteArray(address);
		// Check array size.
		this.address = new byte[2];
		int diff = this.address.length - byteAddress.length;
		for (int i = 0; i < diff; i++)
			this.address[i] = 0;
		for (int i = diff; i < this.address.length; i++)
			this.address[i] = byteAddress[i - diff];
	}
	
	/**
	 * Returns the address high significant byte.
	 * 
	 * @return Address high significant byte.
	 */
	public int getHsb() {
		return address[0];
	}
	
	/**
	 * Returns the address low significant byte.
	 * 
	 * @return Address low significant byte.
	 */
	public int getLsb() {
		return address[1];
	}
	
	/**
	 * Returns the 16-bit address value in byte array format.
	 * 
	 * @return Address value as byte array.
	 */
	public byte[] getValue() {
		return Arrays.copyOf(address, address.length);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof XBee16BitAddress))
			return false;
		XBee16BitAddress addr = (XBee16BitAddress)obj;
		return Arrays.equals(addr.getValue(), getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = HASH_SEED;
		for (byte b:getValue())
			hash = hash * (hash + b);
		return hash;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return HexUtils.byteArrayToHexString(address);
	}
}

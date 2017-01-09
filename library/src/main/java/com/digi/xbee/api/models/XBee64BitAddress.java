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
 * This class represents a 64-bit address (also known as MAC address). 
 * 
 * <p>The 64-bit address is a unique device address assigned during 
 * manufacturing. This address is unique to each physical device.</p>
 */
public final class XBee64BitAddress {

	// Constants
	/**
	 * 64-bit address reserved for the coordinator (value: 0000000000000000).
	 */
	public static final XBee64BitAddress COORDINATOR_ADDRESS = new XBee64BitAddress("0000");
	/**
	 * 64-bit broadcast address (value: 000000000000FFFF).
	 */
	public static final XBee64BitAddress BROADCAST_ADDRESS = new XBee64BitAddress("FFFF");
	/**
	 * 64-bit unknown address (value: FFFFFFFFFFFFFFFF).
	 */
	public static final XBee64BitAddress UNKNOWN_ADDRESS = new XBee64BitAddress("FFFFFFFFFFFFFFFF");
	
	private static final String DEVICE_ID_SEPARATOR = "-";
	private static final String DEVICE_ID_MAC_SEPARATOR = "FF";
	
	/**
	 * Pattern for the 64-bit address string: {@value}.
	 */
	private static final String XBEE_64_BIT_ADDRESS_PATTERN = "(0[xX])?[0-9a-fA-F]{1,16}";
	
	private static final int HASH_SEED = 23;
	
	// Variables
	private final byte[] address;
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code XBee64BitAddress} with the given parameters.
	 * 
	 * @param address The XBee 64-bit address as byte array.
	 * 
	 * @throws IllegalArgumentException if {@code address.length > 8} or
	 *                                  if {@code address.length < 1}.
	 * @throws NullPointerException if {@code address == null}.
	 */
	public XBee64BitAddress(byte[] address) {
		if (address == null)
			throw new NullPointerException("Address cannot be null.");
		if (address.length < 1)
			throw new IllegalArgumentException("Address must contain at least 1 byte.");
		if (address.length > 8)
			throw new IllegalArgumentException("Address cannot contain more than 8 bytes.");
		
		this.address = new byte[8];
		int diff = this.address.length - address.length;
		for (int i = 0; i < diff; i++)
			this.address[i] = 0;
		for (int i = diff; i < this.address.length; i++)
			this.address[i] = address[i - diff];
	}
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code XBee64BitAddress} with the given parameters.
	 * 
	 * <p>The string must be the hexadecimal representation of a 64-bit 
	 * address.</p>
	 * 
	 * @param address The XBee 64-bit address as string.
	 * 
	 * @throws IllegalArgumentException if {@code address.length() < 1} or
	 *                                  if {@code address} contains 
	 *                                  non-hexadecimal characters and is longer
	 *                                  than 8 bytes.
	 * @throws NullPointerException if {@code address == null}.
	 */
	public XBee64BitAddress(String address) {
		if (address == null)
			throw new NullPointerException("Address cannot be null.");
		if (address.length() < 1)
			throw new IllegalArgumentException("Address must contain at least 1 character.");
		if (!address.matches(XBEE_64_BIT_ADDRESS_PATTERN))
			throw new IllegalArgumentException("Address must follow this pattern: (0x)0013A20040XXXXXX.");
		
		byte[] byteAddress = HexUtils.hexStringToByteArray(address);
		this.address = new byte[8];
		int diff = this.address.length - byteAddress.length;
		for (int i = 0; i < diff; i++)
			this.address[i] = 0;
		for (int i = diff; i < this.address.length; i++)
			this.address[i] = byteAddress[i - diff];
	}
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code XBee64BitAddress} with the given bytes being {@code b0} the 
	 * more significant byte and {@code b7} the less significant byte.
	 * 
	 * @param b0 XBee 64-bit address bit 0.
	 * @param b1 XBee 64-bit address bit 1.
	 * @param b2 XBee 64-bit address bit 2.
	 * @param b3 XBee 64-bit address bit 3.
	 * @param b4 XBee 64-bit address bit 4.
	 * @param b5 XBee 64-bit address bit 5.
	 * @param b6 XBee 64-bit address bit 6.
	 * @param b7 XBee 64-bit address bit 7.
	 * 
	 * @throws IllegalArgumentException if {@code b0 > 255} or
	 *                                  if {@code b0 < 0} or
	 *                                  if {@code b1 > 255} or
	 *                                  if {@code b1 < 0} or
	 *                                  if {@code b2 > 255} or
	 *                                  if {@code b2 < 0} or
	 *                                  if {@code b3 > 255} or
	 *                                  if {@code b3 < 0} or
	 *                                  if {@code b4 > 255} or
	 *                                  if {@code b4 < 0} or
	 *                                  if {@code b5 > 255} or
	 *                                  if {@code b5 < 0} or
	 *                                  if {@code b6 > 255} or
	 *                                  if {@code b6 < 0} or
	 *                                  if {@code b7 > 255} or
	 *                                  if {@code b7 < 0}.
	 */
	public XBee64BitAddress(int b0, int b1, int b2, int b3, int b4, int b5, int b6, int b7) {
		if (b0 > 255 || b0 < 0)
			throw new IllegalArgumentException("B0 must be between 0 and 255.");
		if (b1 > 255 || b1 < 0)
			throw new IllegalArgumentException("B1 must be between 0 and 255.");
		if (b2 > 255 || b2 < 0)
			throw new IllegalArgumentException("B2 must be between 0 and 255.");
		if (b3 > 255 || b3 < 0)
			throw new IllegalArgumentException("B3 must be between 0 and 255.");
		if (b5 > 255 || b5 < 0)
			throw new IllegalArgumentException("B4 must be between 0 and 255.");
		if (b4 > 255 || b4 < 0)
			throw new IllegalArgumentException("B5 must be between 0 and 255.");
		if (b6 > 255 || b6 < 0)
			throw new IllegalArgumentException("B6 must be between 0 and 255.");
		if (b7 > 255 || b7 < 0)
			throw new IllegalArgumentException("B7 must be between 0 and 255.");
		
		address = new byte[8];
		address[0] = (byte) b0;
		address[1] = (byte) b1;
		address[2] = (byte) b2;
		address[3] = (byte) b3;
		address[4] = (byte) b4;
		address[5] = (byte) b5;
		address[6] = (byte) b6;
		address[7] = (byte) b7;
	}
	
	/**
	 * Returns the XBee 64-bit address value as byte array.
	 * 
	 * @return XBee 64-bit address value as byte array.
	 */
	public byte[] getValue() {
		return Arrays.copyOf(address, address.length);
	}
	
	/**
	 * Generates the Device ID corresponding to this {@code XBee64BitAddress} 
	 * to be used in Device Cloud.
	 * 
	 * @return Device ID corresponding to this address.
	 */
	public String generateDeviceID() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++)
				sb.append(HexUtils.byteArrayToHexString(new byte[]{0}));
			sb.append(DEVICE_ID_SEPARATOR);
		}
		// Here we should have "00000000-00000000-"
		// Append first three bytes of the MAC Address, discard first 2.
		sb.append(HexUtils.byteArrayToHexString(new byte[]{address[2], address[3], address[4]}));
		sb.append(DEVICE_ID_MAC_SEPARATOR);
		sb.append(DEVICE_ID_SEPARATOR);
		sb.append(DEVICE_ID_MAC_SEPARATOR);
		// Here we should have "00000000-00000000-XXXXXXFF-FF"
		// Append second three bytes of the MAC Address.
		sb.append(HexUtils.byteArrayToHexString(new byte[]{address[5], address[6], address[7]}));
		return sb.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof XBee64BitAddress))
			return false;
		XBee64BitAddress addr = (XBee64BitAddress)obj;
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

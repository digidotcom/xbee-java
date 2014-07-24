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
 * This class represents a 64 bit address (also known as MAC address). Each
 * XBee device has its own 64 bit address which is unique.
 */
public final class XBee64BitAddress {

	// Constants
	public static final XBee64BitAddress COORDINATOR_ADDRESS = new XBee64BitAddress("0000");
	public static final XBee64BitAddress BROADCAST_ADDRESS = new XBee64BitAddress("FFFF");
	public static final XBee64BitAddress UNKNOWN_ADDRESS = new XBee64BitAddress("FFFE");
	
	private static final String DEVICE_ID_SEPARATOR = "-";
	private static final String DEVICE_ID_MAC_SEPARATOR = "FF";
	
	private static final int HASH_SEED = 23;
	
	// Variables
	private final byte[] address;
	
	/**
	 * Class constructor. Instances a new object of type XBee64BitAddress
	 * with the given parameters-
	 * 
	 * @param address The XBee 64 bit address as byte array.
	 * 
	 * @throws NullPointerException if {@code address == null}.
	 * @throws IllegalArgumentException if {@code address.length > 8}.
	 */
	public XBee64BitAddress(byte[] address) {
		if (address == null)
			throw new NullPointerException("Address cannot be null.");
		if (address.length > 8)
			throw new IllegalArgumentException("Address cannot contain more than 8 bytes.");
		
		this.address = address;
	}
	
	/**
	 * Class constructor. Instances a new object of type XBee64BitAddress
	 * with the given parameters-
	 * 
	 * @param address The XBee 64 bit address as integer array.
	 * 
	 * @throws NullPointerException if {@code address == null}.
	 * @throws IllegalArgumentException if {@code address.length > 8}.
	 */
	public XBee64BitAddress(int[] address) {
		if (address == null)
			throw new NullPointerException("Address cannot be null.");
		if (address.length > 8)
			throw new IllegalArgumentException("Address cannot contain more than 8 integers.");
		
		this.address = new byte[address.length];
		for (int i = 0; i < address.length; i++)
			this.address[i] = (byte)address[i];
	}
	
	/**
	 * Class constructor. Instances a new object of type XBee64BitAddress
	 * with the given parameters-
	 * 
	 * @param address The XBee 64 bit address as string.
	 * 
	 * @throws NullPointerException if {@code address == null}.
	 */
	public XBee64BitAddress(String address) {
		if (address == null)
			throw new NullPointerException("Address cannot be null.");
		
		byte[] byteAddress = HexUtils.hexStringToByteArray(address);
		this.address = new byte[8];
		int diff = 8 - byteAddress.length;
		for (int i = 0; i < diff; i++)
			this.address[i] = 0;
		for (int i = diff; i < 8; i++)
			this.address[i] = byteAddress[i - diff];
	}
	
	/**
	 * Class constructor. Instances a new object of type XBee64BitAddress
	 * with the given bytes being b0 the more significant byte and b7 the 
	 * less significant byte.
	 * 
	 * @param b0 XBee 64 bit address b0.
	 * @param b1 XBee 64 bit address b1.
	 * @param b2 XBee 64 bit address b2.
	 * @param b3 XBee 64 bit address b3.
	 * @param b4 XBee 64 bit address b4.
	 * @param b5 XBee 64 bit address b5.
	 * @param b6 XBee 64 bit address b6.
	 * @param b7 XBee 64 bit address b7.
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
			throw new IllegalArgumentException("B0 must be betwwen 0 and 255.");
		if (b1 > 255 || b1 < 0)
			throw new IllegalArgumentException("B1 must be betwwen 0 and 255.");
		if (b2 > 255 || b2 < 0)
			throw new IllegalArgumentException("B2 must be betwwen 0 and 255.");
		if (b3 > 255 || b3 < 0)
			throw new IllegalArgumentException("B3 must be betwwen 0 and 255.");
		if (b5 > 255 || b5 < 0)
			throw new IllegalArgumentException("B4 must be betwwen 0 and 255.");
		if (b5 > 255 || b5 < 0)
			throw new IllegalArgumentException("B5 must be betwwen 0 and 255.");
		if (b6 > 255 || b6 < 0)
			throw new IllegalArgumentException("B6 must be betwwen 0 and 255.");
		if (b7 > 255 || b7 < 0)
			throw new IllegalArgumentException("B7 must be betwwen 0 and 255.");
		
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
	 * Retrieves the XBee 64 bit address value as byte array.
	 * 
	 * @return XBee 64 bit address value as byte array.
	 */
	public byte[] getValue() {
		return address;
	}
	
	/**
	 * Generates a Device ID to be used in Device Cloud from this XBee 64-bit Address.
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
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof XBee64BitAddress))
			return false;
		XBee64BitAddress addr = (XBee64BitAddress)obj;
		return Arrays.equals(addr.getValue(), getValue());
		// TODO: Does this compare really work?
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
		return HexUtils.byteArrayToHexString(address);
	}
}

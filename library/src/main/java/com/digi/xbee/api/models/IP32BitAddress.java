/**
 * Copyright (c) 2016 Digi International Inc.,
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class represents a 32-bit IP address used in some protocols where each
 * radio module has its own 32 bit IP address.
 * 
 * <p>This address is only applicable for:</p>
 * <ul>
 *   <li>Cellular</li>
 * </ul>
 */
public class IP32BitAddress {
	
	// Constants
	/**
	 * Broadcast IP address (value: "255.255.255.255").
	 */
	public static final IP32BitAddress BROADCAST_ADDRESS = new IP32BitAddress("255.255.255.255");
	
	/**
	 * Localhost IP address (value: "127.0.0.1").
	 */
	public static final IP32BitAddress LOCALHOST_ADDRESS = new IP32BitAddress("127.0.0.1");
	
	private static final String ERROR_IP_NULL = "IP address cannot be null.";
	private static final String ERROR_IP_TOO_LONG = "IP address cannot be longer than 4 bytes.";
	private static final String ERROR_IP_INVALID = "Invalid IP address.";
	
	private static final int HASH_SEED = 23;
	
	public final static String IP_V4_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	
	// Variables
	private byte[] address;
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code IP32BitAddress} with the given parameters.
	 * 
	 * @param address The 32 bit IP address as byte array.
	 * 
	 * @throws IllegalArgumentException If {@code address.length > 4}.
	 * @throws NullPointerException If {@code address == null}.
	 */
	public IP32BitAddress(byte[] address) {
		if (address == null)
			throw new NullPointerException(ERROR_IP_NULL);
		if (address.length > 4)
			throw new IllegalArgumentException(ERROR_IP_TOO_LONG);
		
		generateIPAddress(address);
	}
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code IP32BitAddress} with the given parameters.
	 * 
	 * @param address The 32 bit IP address as integer array.
	 * 
	 * @throws IllegalArgumentException If {@code address.length > 8}.
	 * @throws NullPointerException If {@code address == null}.
	 */
	public IP32BitAddress(int[] address) {
		if (address == null)
			throw new NullPointerException(ERROR_IP_NULL);
		if (address.length > 4)
			throw new IllegalArgumentException(ERROR_IP_TOO_LONG);
		
		byte[] byteAddress = new byte[address.length];
		for (int i = 0; i < address.length; i++)
			byteAddress[i] = (byte)address[i];
		
		generateIPAddress(byteAddress);
	}
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code IP32BitAddress} with the given parameters.
	 * 
	 * @param address The 32 bit IP address as string.
	 * 
	 * @throws IllegalArgumentException If the given address doesn't match the
	 *                                  IP address pattern.
	 * @throws NullPointerException If {@code address == null}.
	 */
	public IP32BitAddress(String address) {
		if (address == null)
			throw new NullPointerException(ERROR_IP_NULL);
		
		Pattern p = Pattern.compile(IP_V4_PATTERN);
		Matcher m = p.matcher(address);
		if (!m.matches())
			throw new IllegalArgumentException(ERROR_IP_INVALID);
		
		byte[] byteAddress = new byte[4];
		
		byteAddress[0] = (byte)Integer.parseInt(m.group(1));
		byteAddress[1] = (byte)Integer.parseInt(m.group(2));
		byteAddress[2] = (byte)Integer.parseInt(m.group(3));
		byteAddress[3] = (byte)Integer.parseInt(m.group(4));
		
		generateIPAddress(byteAddress);
	}
	
	/**
	 * Generates and saves the IP byte address based on the given byte array.
	 * 
	 * @param byteAddress The byte array used to generate the final IP byte 
	 *                    address.
	 */
	private void generateIPAddress(byte[] byteAddress) {
		this.address = new byte[4];
		
		int diff = 4 - byteAddress.length;
		for (int i = 0; i < diff; i++)
			this.address[i] = 0;
		for (int i = diff; i < 4; i++)
			this.address[i] = byteAddress[i - diff];
	}
	
	/**
	 * Retrieves the 32 bit IP address value as byte array.
	 * 
	 * @return 32 bit IP address value as byte array.
	 */
	public byte[] getValue() {
		return address;
	}
	
	/**
	 * Retrieves the 32 bit IP address value as string.
	 * 
	 * @return 32 bit IP address value as string.
	 */
	public String getValueString() {
		String prettyIP = "";
		for (int i = 0; i < address.length; i++) {
			if (i != 0)
				prettyIP += ".";
			prettyIP += address[i] & 0xFF;
		}
		
		return prettyIP;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof IP32BitAddress))
			return false;
		IP32BitAddress addr = (IP32BitAddress)obj;
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
	public String toString() {
		return getValueString();
	}
}

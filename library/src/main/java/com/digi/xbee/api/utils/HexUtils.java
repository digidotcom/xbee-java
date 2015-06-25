/**
 * Copyright (c) 2014-2015 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api.utils;

/**
 * Utility class containing methods to work with hexadecimal values and several 
 * data type conversions.
 */
public class HexUtils {

	// Constants.
	private static final String HEXES = "0123456789ABCDEF";
	private static final String HEX_HEADER = "0x";
	
	/**
	 * Converts the given byte array into an hex string.
	 * 
	 * @param value Byte array to convert to hex string.
	 * 
	 * @return Converted byte array to hex string.
	 * 
	 * @throws NullPointerException if {@code value == null}.
	 * 
	 * @see #hexStringToByteArray(String)
	 */
	public static String byteArrayToHexString(byte[] value) {
		if (value == null )
			throw new NullPointerException("Value to convert cannot be null.");
		
		final StringBuilder hex = new StringBuilder(2 * value.length );
		for (final byte b : value) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4))
				.append(HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}
	
	/**
	 * Converts the given byte into an hex string.
	 * 
	 * @param value Byte to convert to hex string.
	 * 
	 * @return Converted byte to hex string.
	 */
	public static String byteToHexString(byte value) {
		final StringBuilder hex = new StringBuilder(2);
		byte b = value;
		hex.append(HEXES.charAt((b & 0xF0) >> 4))
			.append(HEXES.charAt((b & 0x0F)));
		return hex.toString();
	}
	
	/**
	 * Converts the given hex string into a byte array.
	 * 
	 * @param value Hex string to convert to byte array.
	 * 
	 * @return Byte array of the given hex string.
	 * 
	 * @throws NullPointerException if {@code value == null}.
	 * 
	 * @see #byteArrayToHexString(byte[])
	 */
	public static byte[] hexStringToByteArray(String value) {
		if (value == null)
			throw new NullPointerException("Value to convert cannot be null.");
		
		value = value.trim();
		if (value.startsWith(HEX_HEADER))
			value = value.substring((HEX_HEADER).length());
		int len = value.length();
		if (len % 2 != 0) {
			value = "0" + value;
			len = value.length();
		}
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(value.charAt(i), 16) << 4)
					+ Character.digit(value.charAt(i+1), 16));
		}
		return data;
	}
	
	/**
	 * Checks whether the given parameter is a string or a numeric value.
	 * 
	 * @param parameter Parameter to check.
	 * 
	 * @return {@code true} if the given parameter is a string,
	 *         {@code false} otherwise.
	 * 
	 * @throws NullPointerException if {@code parameter == null}.
	 */
	public static boolean containsLetters(String parameter) {
		if (parameter == null)
			throw new NullPointerException("Parameter cannot be null.");
		
		byte[] byteArray = parameter.getBytes();
		for (int i = 0; i < byteArray.length; i++){
			if (!((byteArray[i] >= '0') && (byteArray[i] <= '9')))
				return true;
		}
		return false;
	}
	
	/**
	 * Converts the given integer into an hexadecimal string.
	 * 
	 * @param value The integer value to convert to hexadecimal string.
	 * @param minBytes The minimum number of bytes to be represented.
	 * 
	 * @return The integer value as hexadecimal string.
	 * 
	 * @throws IllegalArgumentException if {@code minBytes <= 0}.
	 */
	public static String integerToHexString(int value, int minBytes) {
		if (minBytes <= 0)
			throw new IllegalArgumentException("Minimum number of bytes must be greater than 0.");
		
		String f = String.format("%%0%dX", minBytes*2);
		return String.format(f, value);
	}
	
	/**
	 * Converts the given hexadecimal string to a pretty format by splitting the 
	 * content byte by byte.
	 * 
	 * @param hexString The hexadecimal string to convert.
	 * 
	 * @return The hexadecimal string with pretty format.
	 * 
	 * @throws NullPointerException if {@code hexString == null}.
	 * 
	 * @see #prettyHexString(byte[])
	 */
	public static String prettyHexString(String hexString) {
		if (hexString == null)
			throw new NullPointerException("Hexadecimal string cannot be null.");
		
		String copy = hexString.toUpperCase();
		for (final char c : copy.toCharArray()) {
			if (!HEXES.contains(""+c))
				throw new IllegalArgumentException("Given string cannot contain non-hexadecimal characters.");
		}
		
		String prettyHexString = "";
		if (copy.length() % 2 != 0)
			copy = "0" + copy;
		int iterations = copy.length() / 2;
		for (int i = 0; i < iterations; i++)
			prettyHexString += copy.substring(2 * i, 2 * i + 2) + " ";
		return prettyHexString.trim();
	}
	
	/**
	 * Converts the given byte array into an hex string and retrieves it 
	 * in pretty format by splitting the content byte by byte.
	 * 
	 * @param value The byte array to convert to pretty hex string.
	 * 
	 * @return The hexadecimal pretty string.
	 * 
	 * @throws NullPointerException if {@code value == null}.
	 * 
	 * @see #prettyHexString(String)
	 */
	public static String prettyHexString(byte[] value) {
		return prettyHexString(byteArrayToHexString(value));
	}
}

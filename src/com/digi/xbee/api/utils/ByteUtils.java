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
package com.digi.xbee.api.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ByteUtils {

	/**
	 * Reads the given amount of bytes from the given Byte array
	 * input stream.
	 * 
	 * @param numBytes Number of bytes to read.
	 * @param inputStream Byte array input stream to read from.
	 * @return Read bytes.
	 * 
	 * @throws NullPointerException if {@code inputStream == null}.
	 * @throws IllegalArgumentException if {@code numBytes < 0}.
	 */
	public static byte[] readBytes(int numBytes, ByteArrayInputStream inputStream) {
		if (inputStream == null)
			throw new NullPointerException("Input stream cannot be null.");
		if (numBytes < 0)
			throw new IllegalArgumentException("Number of bytes to read must be great than 0.");
		
		byte[] data = new byte[numBytes];
		inputStream.read(data, 0, numBytes);
		return data;
	}
	
	/**
	 * Reads a null terminated String from the given byte array input stream.
	 * 
	 * @param inputStream Byte array input stream to read from.
	 * @return The read String.
	 * 
	 * @throws NullPointerException if {@code inputStream == null}.
	 */
	public static String readString(ByteArrayInputStream inputStream) {
		if (inputStream == null)
			throw new NullPointerException("Input stream cannot be null.");
		
		StringBuilder sb = new StringBuilder();
		byte readByte;
		while (((readByte = (byte)inputStream.read()) != 0x00) && readByte != -1)
			sb.append((char)readByte);
		return sb.toString();
	}
	
	/**
	 * Converts the given long into a byte array.
	 * 
	 * @param value Long to convert to.
	 * @return Byte array of the given long (8 bytes length).
	 */
	public static byte[] longToByteArray(long value) {
		return new byte[] {
				(byte)((value >>> 56) & 0xFF),
				(byte)((value >>> 48) & 0xFF),
				(byte)((value >>> 40) & 0xFF),
				(byte)((value >>> 32) & 0xFF),
				(byte)((value >>> 24) & 0xFF),
				(byte)((value >>> 16) & 0xFF),
				(byte)((value >>> 8) & 0xFF),
				(byte)(value & 0xFF)
		};
	}
	
	/**
	 * Converts the given byte array (8 bytes length max) into a long.
	 * 
	 * @param b Byte array to convert to long (8 bytes length max).
	 * @return Converted long.
	 * 
	 * @throws NullPointerException if {@code b == null}.
	 */
	public static long byteArrayToLong(byte[] b) {
		if (b == null)
			throw new NullPointerException("Byte array cannot be null.");
		
		byte[] values = b;
		if (b.length < 8) {
			values = new byte[8];
			int diff = 8 - b.length;
			for (int i = 0; i < diff; i++)
				values[i] = 0;
			for (int i = diff; i < 8; i++)
				values[i] = b[i - diff];
		}
		return ((long)values[0] << 56) 
				+ ((long)(values[1] & 0xFF) << 48) 
				+ ((long)(values[2] & 0xFF) << 40) 
				+ ((long)(values[3] & 0xFF) << 32) 
				+ ((long)(values[4] & 0xFF) << 24) 
				+ ((values[5] & 0xFF) << 16) 
				+ ((values[6] & 0xFF) <<  8) 
				+ (values[7] & 0xFF);
	}
	
	/**
	 * Converts the given integer into a byte array.
	 * 
	 * @param value Integer to convert to.
	 * @return Byte array of the given integer (4 bytes length).
	 */
	public static byte[] intToByteArray(int value) {
		return new byte[] {
				(byte)((value >>> 24) & 0xFF),
				(byte)((value >>> 16) & 0xFF),
				(byte)((value >>> 8) & 0xFF),
				(byte)(value & 0xFF)
		};
	}
	
	/**
	 * Converts the given byte array (4 bytes length max) into an
	 * integer.
	 * 
	 * @param b Byte array to convert to integer (4 bytes length max).
	 * @return Converted integer.
	 * 
	 * @throws NullPointerException if {@code b == null}.
	 */
	public static int byteArrayToInt(byte[] b) {
		if (b == null)
			throw new NullPointerException("Byte array cannot be null.");
		
		byte[] values = b;
		if (b.length < 4) {
			values = new byte[4];
			int diff = 4 - b.length;
			for (int i = 0; i < diff; i++)
				values[i] = 0;
			for (int i = diff; i < 4; i++)
				values[i] = b[i - diff];
		}
		return ((values[0] & 0xFF) << 24)
				| ((values[1] & 0xFF) << 16)
				| ((values[2] & 0xFF) << 8)
				| (values[3] & 0xFF);
	}
	
	/**
	 * Converts the given short into a byte array.
	 * 
	 * @param value Short to convert to.
	 * @return Byte array of the given short (2 bytes length).
	 */
	public static byte[] shortToByteArray(short value) {
		byte[] b = new byte[2];
		b[0] = (byte)((value >> 8) & 0xFF);
		b[1] = (byte)(value & 0xFF);
		return b;
	}
	
	/**
	 * Converts the given byte array to short.
	 * 
	 * @param b byte array to convert to.
	 * @return short of the given byte array (2 bytes length).
	 * 
	 * @throws NullPointerException if {@code b == null}.
	 */
	public static short byteArrayToShort(byte[] b) {
		if (b == null)
			throw new NullPointerException("Byte array cannot be null.");
		
		return (short) (((b[0] << 8) & 0xFF00) 
						| b[1] & 0x00FF);
	}
	
	/**
	 * Converts the given string into a byte array.
	 * 
	 * @param value String to convert to.
	 * @return Byte array of the given string.
	 * 
	 * @throws NullPointerException if {@code value == null}.
	 */
	public static byte[] stringToByteArray(String value) {
		if (value == null)
			throw new NullPointerException("Value cannot be null.");
		
		return value.getBytes();
	}
	
	/**
	 * Converts the given string byte array into a string.
	 * 
	 * @param value Byte array to convert to string.
	 * @return Converted String.
	 * 
	 * @throws NullPointerException if {@code value == null}.
	 */
	public static String byteArrayToString(byte[] value) {
		if (value == null)
			throw new NullPointerException("Byte array cannot be null.");
		
		return new String(value);
	}
	
	/**
	 * Converts the given byte into a integer.
	 * 
	 * @param b Byte to convert to.
	 * @return converted byte into integer.
	 */
	public static int byteToInt(byte b) {
		return (int) b & 0xFF;
	}
	
	/**
	 * Returns whether the specified bit of the given integer is set to 1
	 * or not.
	 * 
	 * @param containerInteger Integer to check bit position enablement.
	 * @param bitPosition Position of the bit to check in the integer.
	 * @return True if the given bit position is set to 1 in the integer, false otherwise.
	 */
	public static boolean isBitEnabled(int containerInteger, int bitPosition) {
		return (((containerInteger & 0xFFFFFFFF) >> bitPosition) & 0x01) == 0x01;
	}
	
	/**
	 * Reads an integer value from the given byte using the given bit offset and the given
	 * bit size.
	 * 
	 * @param containerByte Byte to read integer from.
	 * @param bitOffset Offset inside the byte to start reading integer value.
	 * @param bitLength Size in bits of the integer value.
	 * @return The integer value read.
	 */
	public static int readIntegerFromByte(byte containerByte, int bitOffset, int bitLength) {
		int readInteger = 0;
		for (int i = 0; i < bitLength; i++) {
			if (isBitEnabled(containerByte, bitOffset + i))
				readInteger = readInteger | (int)Math.pow(2, i);
		}
		return readInteger;
	}
	
	/**
	 * Reads a boolean value from the given byte at the given bit position.
	 * 
	 * @param containerByte Byte to read boolean from.
	 * @param bitOffset Offset of the bit to read.
	 * @return The boolean value.
	 */
	public static boolean readBooleanFromByte(byte containerByte, int bitOffset) {
		return isBitEnabled(containerByte, bitOffset);
	}
	
	/**
	 * Reads from the given byte array input stream until a CR character is
	 * found or end of stream. Read bytes are returned.
	 * 
	 * @param inputStream Byte array input stream to read from.
	 * @return The read bytes.
	 * 
	 * @throws NullPointerException if {@code inputStream == null}.
	 */
	public static byte[] readUntilCR(ByteArrayInputStream inputStream) {
		if (inputStream == null)
			throw new NullPointerException("Input stream cannot be null.");
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte readByte;
		while (((readByte = (byte)inputStream.read()) != 0x0D) && readByte != -1)
			outputStream.write(readByte);
		return outputStream.toByteArray();
	}
	
	/**
	 * Generates a new byte array of the given size using the
	 * given data and filling with ascii zeros (0x48) the remaining space.
	 * If new size is lower than current, array is truncated.
	 * 
	 * @param data Data to use in the new array.
	 * @param finalSize Final size of the array.
	 * @return Final array of the given size replacing with zeros the remaining space.
	 * 
	 * @throws NullPointerException if {@code data == null}.
	 */
	public static byte[] newByteArray(byte[] data, int finalSize) {
		if (data == null)
			throw new NullPointerException("Data cannot be null.");
		
		byte[] filledArray = new byte[finalSize];
		int diff = finalSize - data.length;
		if (diff >= 0) {
			for (int i = 0; i < diff; i++)
				filledArray[i] = '0';
			System.arraycopy(data, 0, filledArray, diff, data.length);
		} else 
			System.arraycopy(data, 0, filledArray, 0, finalSize);
		return filledArray;
	}
	
	/**
	 * Swaps the given byte array order.
	 * 
	 * @param source Byte array to swap.
	 * @return The swapped byte array.
	 */
	public static byte[] swapByteArray(byte[] source) {
		byte[] swapped = new byte[source.length];
		for (int i = 0; i < source.length; i++)
			swapped[source.length - i - 1] = source[i];
		return swapped;
	}
}

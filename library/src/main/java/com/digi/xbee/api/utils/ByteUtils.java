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
package com.digi.xbee.api.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Utility class containing methods to work with bytes and byte arrays and 
 * several data type conversions.
 */
public class ByteUtils {

	/**
	 * Reads the given amount of bytes from the given byte array input stream.
	 * 
	 * @param numBytes Number of bytes to read.
	 * @param inputStream Byte array input stream to read bytes from.
	 * 
	 * @return An array with the read bytes.
	 * 
	 * @throws IllegalArgumentException if {@code numBytes < 0}.
	 * @throws NullPointerException if {@code inputStream == null}.
	 * 
	 * @see #readString(ByteArrayInputStream)
	 * @see #readUntilCR(ByteArrayInputStream)
	 */
	public static byte[] readBytes(int numBytes, ByteArrayInputStream inputStream) {
		if (inputStream == null)
			throw new NullPointerException("Input stream cannot be null.");
		if (numBytes < 0)
			throw new IllegalArgumentException("Number of bytes to read must be equal or greater than 0.");
		
		byte[] data = new byte[numBytes];
		int len = inputStream.read(data, 0, numBytes);
		if (len == - 1)
			return new byte[0];
		if (len < numBytes) {
			byte[] d = new byte[len];
			System.arraycopy(data, 0, d, 0, len);
			return d;
		}
		return data;
	}
	
	/**
	 * Reads a null-terminated string from the given byte array input stream.
	 * 
	 * @param inputStream Byte array input stream to read string  from.
	 * 
	 * @return The read string from the given {@code ByteArrayInputStream}.
	 * 
	 * @throws NullPointerException if {@code inputStream == null}.
	 * 
	 * @see #readBytes(int, ByteArrayInputStream)
	 * @see #readUntilCR(ByteArrayInputStream)
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
	 * Converts the given long value into a byte array.
	 * 
	 * @param value Long value to convert to byte array.
	 * 
	 * @return Byte array of the given long value (8 bytes length).
	 * 
	 * @see #byteArrayToLong(byte[])
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
	 * @param byteArray Byte array to convert to long (8 bytes length max).
	 * 
	 * @return Converted long value.
	 * 
	 * @throws NullPointerException if {@code b == null}.
	 * 
	 * @see #longToByteArray(long)
	 */
	public static long byteArrayToLong(byte[] byteArray) {
		if (byteArray == null)
			throw new NullPointerException("Byte array cannot be null.");
		
		if (byteArray.length == 0)
			return 0;
		
		byte[] values = byteArray;
		if (byteArray.length < 8) {
			values = new byte[8];
			int diff = values.length - byteArray.length;
			for (int i = 0; i < diff; i++)
				values[i] = 0;
			for (int i = diff; i < values.length; i++)
				values[i] = byteArray[i - diff];
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
	 * Converts the given integer value into a byte array.
	 * 
	 * @param value Integer value to convert to byte array.
	 * 
	 * @return Byte array of the given integer (4 bytes length).
	 * 
	 * @see #byteArrayToInt(byte[])
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
	 * Converts the given byte array (4 bytes length max) into an integer.
	 * 
	 * @param byteArray Byte array to convert to integer (4 bytes length max).
	 * 
	 * @return Converted integer value.
	 * 
	 * @throws NullPointerException if {@code byteArray == null}.
	 * 
	 * @see #intToByteArray(int)
	 */
	public static int byteArrayToInt(byte[] byteArray) {
		if (byteArray == null)
			throw new NullPointerException("Byte array cannot be null.");
		
		if (byteArray.length == 0)
			return 0;
		
		byte[] values = byteArray;
		if (byteArray.length < 4) {
			values = new byte[4];
			int diff = values.length - byteArray.length;
			for (int i = 0; i < diff; i++)
				values[i] = 0;
			for (int i = diff; i < values.length; i++)
				values[i] = byteArray[i - diff];
		}
		return ((values[0] & 0xFF) << 24)
				| ((values[1] & 0xFF) << 16)
				| ((values[2] & 0xFF) << 8)
				| (values[3] & 0xFF);
	}
	
	/**
	 * Converts the given short value into a byte array.
	 * 
	 * @param value Short value to convert to byte array.
	 * 
	 * @return Byte array of the given short (2 bytes length).
	 * 
	 * @see #byteArrayToShort(byte[])
	 */
	public static byte[] shortToByteArray(short value) {
		byte[] b = new byte[2];
		b[0] = (byte)((value >> 8) & 0xFF);
		b[1] = (byte)(value & 0xFF);
		return b;
	}
	
	/**
	 * Converts the given byte array (2 bytes length max) to short.
	 * 
	 * @param byteArray Byte array to convert to short (2 bytes length max).
	 * 
	 * @return Converted short value.
	 * 
	 * @throws NullPointerException if {@code byteArray == null}.
	 * 
	 * @see #shortToByteArray(short)
	 */
	public static short byteArrayToShort(byte[] byteArray) {
		if (byteArray == null)
			throw new NullPointerException("Byte array cannot be null.");
		
		if (byteArray.length == 0)
			return 0;
		
		byte[] values = byteArray;
		if (byteArray.length < 2) {
			values = new byte[2];
			values[1] = byteArray[0];
			values[0] = 0;
		}
		
		return (short) (((values[0] << 8) & 0xFF00) 
						| values[1] & 0x00FF);
	}
	
	/**
	 * Converts the given string into a byte array.
	 * 
	 * @param value String to convert to byte array.
	 * 
	 * @return Byte array of the given string.
	 * 
	 * @throws NullPointerException if {@code value == null}.
	 * 
	 * @see #byteArrayToString(byte[])
	 */
	public static byte[] stringToByteArray(String value) {
		if (value == null)
			throw new NullPointerException("Value cannot be null.");
		
		return value.getBytes();
	}
	
	/**
	 * Converts the given byte array into a string.
	 * 
	 * @param value Byte array to convert to string.
	 * 
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
	 * Converts the given byte into an integer.
	 * 
	 * @param b Byte to convert to integer.
	 * 
	 * @return Converted byte into integer.
	 */
	public static int byteToInt(byte b) {
		return (int) b & 0xFF;
	}
	
	/**
	 * Returns whether the specified bit of the given integer is set to 1
	 * or not.
	 * 
	 * @param containerInteger Integer to check the given bit position
	 *                         enablement state.
	 * @param bitPosition Position of the bit to check its enablement state.
	 * 
	 * @return {@code true} if the given bit position is set to {@code 1} 
	 *         in the {@code containerInteger}, {@code false} otherwise.
	 *         
	 * @throws IllegalArgumentException if {@code bitPosition < 0} or
	 *                                  if {@code bitPosition > 31}.
	 */
	public static boolean isBitEnabled(int containerInteger, int bitPosition) {
		if (bitPosition < 0 || bitPosition > 31)
			throw new IllegalArgumentException("Bit position must be between 0 and 31.");
		
		return (((containerInteger & 0xFFFFFFFF) >> bitPosition) & 0x01) == 0x01;
	}
	
	/**
	 * Reads an integer value from the given byte using the given bit offset 
	 * and the given bit size.
	 * 
	 * @param containerByte Byte to read the integer from.
	 * @param bitOffset Offset inside the byte to start reading integer value.
	 * @param bitLength Size in bits of the integer value to read.
	 * 
	 * @return The integer read value.
	 * 
	 * @throws IllegalArgumentException if {@code bitOffset < 0} or
	 *                                  if {@code bitOffset > 7} or
	 *                                  if {@code bitLength < 0} or
	 *                                  if {@code bitLength > 8}.
	 */
	public static int readIntegerFromByte(byte containerByte, int bitOffset, int bitLength) {
		if (bitOffset < 0 || bitOffset > 7)
			throw new IllegalArgumentException("Offset must be between 0 and 7.");
		if (bitLength < 0 || bitLength > 7)
			throw new IllegalArgumentException("Length must be between 0 and 8.");
		
		int readInteger = 0;
		for (int i = 0; i < bitLength; i++) {
			if (bitOffset + i > 7)
				break;
			if (isBitEnabled(containerByte, bitOffset + i))
				readInteger = readInteger | (int)Math.pow(2, i);
		}
		return readInteger;
	}
	
	/**
	 * Reads a boolean value from the given byte at the given bit position.
	 * 
	 * @param containerByte Byte to read boolean value from.
	 * @param bitOffset Offset inside the byte to read the boolean value.
	 * 
	 * @return The read boolean value.
	 * 
	 * @throws IllegalArgumentException if {@code bitOffset < 0} or
	 *                                  if {@code bitOffset > 31}.
	 */
	public static boolean readBooleanFromByte(byte containerByte, int bitOffset) {
		if (bitOffset < 0 || bitOffset > 31)
			throw new IllegalArgumentException("Bit offset must be between 0 and 7.");
		
		return isBitEnabled(containerByte, bitOffset);
	}
	
	/**
	 * Reads from the given byte array input stream until a CR character is
	 * found or the end of stream is reached. Read bytes are returned.
	 * 
	 * @param inputStream Byte array input stream to read from.
	 * 
	 * @return An array with the read bytes.
	 * 
	 * @throws NullPointerException if {@code inputStream == null}.
	 * 
	 * @see #readBytes(int, ByteArrayInputStream)
	 * @see #readString(ByteArrayInputStream)
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
	 * Generates a new byte array of the given size using the given data and 
	 * filling with ASCII zeros (0x48) the remaining space.
	 * 
	 * <p>If new size is lower than current, array is truncated.</p>
	 * 
	 * @param data Data to use in the new array.
	 * @param finalSize Final size of the array.
	 * 
	 * @return Final byte array of the given size containing the given data and
	 *         replacing with zeros the remaining space.
	 * 
	 * @throws IllegalArgumentException if {@code finalSize < 0}.
	 * @throws NullPointerException if {@code data == null}.
	 */
	public static byte[] newByteArray(byte[] data, int finalSize) {
		if (data == null)
			throw new NullPointerException("Data cannot be null.");
		if (finalSize < 0)
			throw new IllegalArgumentException("Final size must be equal or greater than 0.");
		
		if (finalSize == 0)
			return new byte[0];
		
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
	 * 
	 * @return The swapped byte array.
	 * 
	 * @throws NullPointerException if {@code source == null}.
	 */
	public static byte[] swapByteArray(byte[] source) {
		if (source == null)
			throw new NullPointerException("Source cannot be null.");
		
		if (source.length == 0)
			return new byte[0];
		
		byte[] swapped = new byte[source.length];
		for (int i = 0; i < source.length; i++)
			swapped[source.length - i - 1] = source[i];
		return swapped;
	}
}

package com.digi.xbee.api.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;

public class StringUtils {
	
	// Constants.
	private final static String UTF8_CHARSET = "UTF-8";
	
	/**
	 * Transforms the given string to upper case format.
	 * 
	 * @param source String to transform to upper case.
	 * 
	 * @return The given string in upper case.
	 * 
	 * @throws NullPointerException if {@code source == null}.
	 */
	public static String stringToUpperCase(String source) {
		if (source == null)
			throw new NullPointerException("String cannot be null.");
		
		return source.toUpperCase(new Locale(UTF8_CHARSET));
	}
	
	/**
	 * Transforms the given string to the corresponding byte array.
	 * 
	 * @param source The {@code String} to transform to a byte array.
	 * 
	 * @return The given string as a byte array.
	 * 
	 * @throws NullPointerException if {@code source == null}.
	 * 
	 * @see #byteArrayToString(byte[])
	 * @see #byteArrayToString(byte[], int, int)
	 */
	public static byte[] stringToByteArray(String source) {
		if (source == null)
			throw new NullPointerException("Value cannot be null.");
		
		byte[] byteArray;
		try {
			byteArray = source.getBytes(UTF8_CHARSET);
		} catch (UnsupportedEncodingException e) {
			byteArray = source.getBytes(Charset.defaultCharset());
		}
		return byteArray;
	}
	
	/**
	 * Transforms the given byte array to its corresponding string.
	 * 
	 * @param byteArray The byte array to transform to string.
	 * 
	 * @return The given byte array as string.
	 * 
	 * @throws NullPointerException if {@code byteArray == null}.
	 * 
	 * @see #stringToByteArray(String)
	 * @see #byteArrayToString(byte[], int, int)
	 */
	public static String byteArrayToString(byte[] byteArray) {
		return byteArrayToString(byteArray, 0, byteArray.length);
	}
	
	/**
	 * Transforms the given byte array to its corresponding string using the
	 * given parameters.
	 * 
	 * @param byteArray The byte array to transform to string.
	 * @param offset Offset in the array to start reading bytes.
	 * @param numBytes Number of bytes to read from the array.
	 * 
	 * @return The given byte array as string.
	 * 
	 * @throws NullPointerException if {@code byteArray == null}.
	 * 
	 * @see #byteArrayToString(byte[])
	 * @see #stringToByteArray(String)
	 */
	public static String byteArrayToString(byte[] byteArray, int offset, int numBytes) {
		if (byteArray == null)
			throw new NullPointerException("Value cannot be null.");
		
		String value;
		try {
			value = new String(byteArray, offset, numBytes, UTF8_CHARSET);
		} catch (UnsupportedEncodingException e) {
			value = new String(byteArray, offset, numBytes, Charset.defaultCharset());
		}
		return value;
	}
}

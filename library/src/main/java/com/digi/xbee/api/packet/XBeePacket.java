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
package com.digi.xbee.api.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;

import com.digi.xbee.api.exceptions.InvalidPacketException;
import com.digi.xbee.api.models.SpecialByte;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This abstract class represents the basic structure of an XBee packet.
 * 
 * <p>Derived classes should implement their own payload generation depending 
 * on their type.</p>
 * 
 * <p>Generic actions like checksum compute or packet length calculation is 
 * performed here.</p>
 */
public abstract class XBeePacket {

	private static final int HASH_SEED = 23;
	
	// Variables.
	private XBeeChecksum checksum;
	
	/**
	 * Class constructor. Instantiates a new {@code XBeePacket} object.
	 */
	protected XBeePacket() {
		checksum = new XBeeChecksum();
	}

	/**
	 * Generates the XBee packet byte array. 
	 * 
	 * <p>Use only while working in API mode 1. If API mode is 2, use 
	 * {@link #generateByteArrayEscaped()}.</p>
	 * 
	 * @return The XBee packet byte array.
	 * 
	 * @see #generateByteArrayEscaped()
	 */
	public byte[] generateByteArray() {
		checksum.reset();
		byte[] packetData = getPacketData();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		os.write(SpecialByte.HEADER_BYTE.getValue());
		if (packetData != null) {
			byte[] length = ByteUtils.shortToByteArray((short)packetData.length);
			int msb = length[0];
			int lsb = length[1];
			os.write(msb);
			os.write(lsb);
			for (int i = 0; i < packetData.length; i++) {
				checksum.add(packetData[i]);
				os.write(packetData[i]);
			}
		} else {
			os.write(0);
			os.write(0);
		}
		os.write((byte)checksum.generate() & 0xFF);
		return os.toByteArray();
	}

	/**
	 * Generates the XBee packet byte array escaping the special bytes.
	 * 
	 * <p>Use only while working in API mode 2. If API mode is 1 use 
	 * {@link #generateByteArray()}.</p>
	 * 
	 * @return The XBee packet byte array with escaped characters.
	 * 
	 * @see #generateByteArray()
	 */
	public byte[] generateByteArrayEscaped() {
		byte[] unescapedArray = generateByteArray();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		// Write header byte and do not escape it.
		os.write(SpecialByte.HEADER_BYTE.getValue());
		for (int i = 1; i < unescapedArray.length; i++) {
			// Start at 1 to avoid escaping header byte.
			if (SpecialByte.isSpecialByte(unescapedArray[i])) {
				os.write(SpecialByte.ESCAPE_BYTE.getValue());
				SpecialByte specialByte = SpecialByte.get(unescapedArray[i]);
				os.write(specialByte.escapeByte());
			} else
				os.write(unescapedArray[i]);
		}
		return os.toByteArray();
	}

	/**
	 * Returns the packet data.
	 * 
	 * @return The packet data.
	 */
	public abstract byte[] getPacketData();

	/**
	 * Returns the packet length.
	 * 
	 * @return The packet length.
	 */
	public int getPacketLength() {
		byte[] packetData = getPacketData();
		if (packetData == null)
			return 0;
		return packetData.length;
	}
	
	/**
	 * Returns the packet checksum.
	 * 
	 * <p>To calculate: Not including frame delimiters and length, add all 
	 * bytes keeping only the lowest 8 bits of the result and subtract the 
	 * result from {@code 0xFF}.</p>
	 * 
	 * @return The packet checksum.
	 */
	public int getChecksum() {
		checksum.reset();
		byte[] packetData = getPacketData();
		if (packetData != null)
			checksum.add(packetData);
		return (byte)checksum.generate() & 0xFF;
	}
	
	/**
	 * Returns a map with the XBee packet parameters and their values.
	 * 
	 * @return A sorted map containing the XBee packet parameters with their 
	 *         values.
	 */
	public LinkedHashMap<String, String> getParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Start delimiter", HexUtils.integerToHexString(SpecialByte.HEADER_BYTE.getValue(), 1));
		parameters.put("Length", HexUtils.prettyHexString(HexUtils.integerToHexString(getPacketLength(), 2)) + " (" + getPacketLength() + ")");
		parameters.putAll(getPacketParameters());
		parameters.put("Checksum", toString().substring(toString().length() - 2));
		return parameters;
	}
	
	/**
	 * Returns a map with the XBee packet parameters and their values.
	 * 
	 * @return A sorted map containing the XBee packet parameters with their 
	 *         values.
	 */
	protected abstract LinkedHashMap<String, String> getPacketParameters();
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof XBeePacket))
			return false;
		XBeePacket packet = (XBeePacket)obj;
		
		return Arrays.equals(packet.generateByteArray(), generateByteArray());
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = HASH_SEED;
		
		byte [] array = generateByteArray();
		for (byte b: array)
			hash = 31 * (hash + b);
		return hash;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return HexUtils.byteArrayToHexString(generateByteArray());
	}
	
	/**
	 * Returns a pretty string representing the packet.
	 * 
	 * @return Pretty String representing the packet.
	 */
	public String toPrettyString() {
		String value = "Packet: " + toString() + "\n";
		LinkedHashMap<String, String> parameters = getParameters();
		for (String parameter:parameters.keySet())
			value = value + parameter + ": " + parameters.get(parameter) + "\n";
		return value;
	}
	
	/**
	 * Parses the given hexadecimal string and returns a Generic XBee packet. 
	 * 
	 * <p>The string can contain white spaces.</p>
	 * 
	 * @param packet The hexadecimal string to parse.
	 * @param mode The operating mode to parse the packet (API 1 or API 2).
	 * 
	 * @return The generated Generic XBee Packet.
	 * 
	 * @throws IllegalArgumentException if {@code mode != OperatingMode.API } and
	 *                                  if {@code mode != OperatingMode.API_ESCAPE}.
	 * @throws InvalidPacketException if the given string does not represent a 
	 *                                valid frame: invalid checksum, length, 
	 *                                start delimiter, etc.
	 * @throws NullPointerException if {@code packet == null}.
	 * 
	 * @see com.digi.xbee.api.models.OperatingMode#API
	 * @see com.digi.xbee.api.models.OperatingMode#API_ESCAPE
	 */
	public static XBeePacket parsePacket(String packet, OperatingMode mode) throws InvalidPacketException {
		if (packet == null)
			throw new NullPointerException("Packet cannot be null.");
			
		return parsePacket(HexUtils.hexStringToByteArray(packet.trim().replace(" ",  "")), mode);
	}
	
	/**
	 * Parses the given byte array and returns a Generic XBee packet.
	 * 
	 * @param packet The byte array to parse.
	 * @param mode The operating mode to parse the packet (API 1 or API 2).
	 * 
	 * @return The generated Generic XBee Packet.
	 * 
	 * @throws IllegalArgumentException if {@code mode != OperatingMode.API } and
	 *                                  if {@code mode != OperatingMode.API_ESCAPE} 
	 *                                  or if {@code packet.length == 0}.
	 * @throws InvalidPacketException if the given byte array does not represent 
	 *                                a valid frame: invalid checksum, length, 
	 *                                start delimiter, etc.
	 * @throws NullPointerException if {@code packet == null}.
	 * 
	 * @see com.digi.xbee.api.models.OperatingMode#API
	 * @see com.digi.xbee.api.models.OperatingMode#API_ESCAPE
	 */
	public static XBeePacket parsePacket(byte[] packet, OperatingMode mode) throws InvalidPacketException {
		if (packet == null)
			throw new NullPointerException("Packet byte array cannot be null.");
		
		if (mode != OperatingMode.API && mode != OperatingMode.API_ESCAPE)
			throw new IllegalArgumentException("Operating mode must be API or API Escaped.");
		
		if (packet.length == 0)
			throw new IllegalArgumentException("Packet length should be greater than 0.");
		
		if (packet.length > 1 && ((packet[0] & 0xFF) != SpecialByte.HEADER_BYTE.getValue()))
			throw new InvalidPacketException("Invalid start delimiter.");
		
		XBeePacketParser parser = new XBeePacketParser();
		XBeePacket xbeePacket = parser.parsePacket(new ByteArrayInputStream(packet, 1, packet.length - 1), mode);
		return xbeePacket;
	}
}

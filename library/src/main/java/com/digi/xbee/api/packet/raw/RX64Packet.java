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
package com.digi.xbee.api.packet.raw;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents an RX (Receive) 64 Request packet. Packet is built 
 * using the parameters of the constructor or providing a valid API payload.
 * 
 * <p>When the module receives an RF packet, it is sent out the UART using 
 * this message type.</p>
 * 
 * <p>This packet is the response to TX (transmit) 64 Request packets.</p>
 * 
 * @see TX64Packet
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 *
 */
public class RX64Packet extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 11; // 1 (Frame type) + 8 (64-bit address) + 1 (signal strength) + 1 (receive options)
	
	// Variables.
	private final XBee64BitAddress sourceAddress64;
	
	private final int rssi;
	private final int receiveOptions;
	
	private byte[] rfData;
	
	private Logger logger;
	
	/**
	 * Creates a new {@code RX64Packet} object from the given payload.
	 * 
	 * @param payload The API frame payload. It must start with the frame type 
	 *                corresponding to a RX64 packet ({@code 0x80}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 * 
	 * @return Parsed RX 64 packet.
	 * 
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.RX_64.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH} or
	 *                                  if {@code rssi < 0} or
	 *                                  if {@code rssi > 100} or
	 *                                  if {@code receiveOptions < 0} or
	 *                                  if {@code receiveOptions > 255}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static RX64Packet createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException("RX64 packet payload cannot be null.");
		
		// 1 (Frame type) + 8 (64-bit address) + 1 (signal strength) + 1 (receive options)
		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException("Incomplete RX64 packet.");
		
		if ((payload[0] & 0xFF) != APIFrameType.RX_64.getValue())
			throw new IllegalArgumentException("Payload is not a RX64 packet.");
		
		// payload[0] is the frame type.
		int index = 1;
		
		// 8 bytes of 64-bit address.
		XBee64BitAddress sourceAddress64 = new XBee64BitAddress(Arrays.copyOfRange(payload, index, index + 8));
		index = index + 8;
		
		// Signal strength byte.
		int signalStrength = payload[index] & 0xFF;
		index = index + 1;
		
		// Receive options byte.
		int receiveOptions = payload[index] & 0xFF;
		index = index + 1;
		
		// Get data.
		byte[] data = null;
		if (index < payload.length)
			data = Arrays.copyOfRange(payload, index, payload.length);
		
		return new RX64Packet(sourceAddress64, signalStrength, receiveOptions, data);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RX64Packet} object with
	 * the given parameters.
	 * 
	 * @param sourceAddress64 64-bit address of the sender.
	 * @param rssi Received signal strength indicator.
	 * @param receiveOptions Bitfield indicating the receive options.
	 * @param rfData Received RF data.
	 * 
	 * @throws IllegalArgumentException if {@code rssi < 0} or
	 *                                  if {@code rssi > 100} or
	 *                                  if {@code receiveOptions < 0} or
	 *                                  if {@code receiveOptions > 255}.
	 * @throws NullPointerException if {@code sourceAddress64 == null}.
	 * 
	 * @see com.digi.xbee.api.models.XBeeReceiveOptions
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public RX64Packet(XBee64BitAddress sourceAddress64, int rssi, int receiveOptions, byte[] rfData) {
		super(APIFrameType.RX_64);
		
		if (sourceAddress64 == null)
			throw new NullPointerException("64-bit source address cannot be null.");
		if (rssi < 0 || rssi > 100)
			throw new IllegalArgumentException("RSSI value must be between 0 and 100.");
		if (receiveOptions < 0 || receiveOptions > 255)
			throw new IllegalArgumentException("Receive options value must be between 0 and 255.");
		
		this.sourceAddress64 = sourceAddress64;
		this.rssi = rssi;
		this.receiveOptions = receiveOptions;
		this.rfData = rfData;
		this.logger = LoggerFactory.getLogger(RX64Packet.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(sourceAddress64.getValue());
			os.write(rssi);
			os.write(receiveOptions);
			if (rfData != null)
				os.write(rfData);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return os.toByteArray();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#needsAPIFrameID()
	 */
	@Override
	public boolean needsAPIFrameID() {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#isBroadcast()
	 */
	@Override
	public boolean isBroadcast() {
		return ByteUtils.isBitEnabled(getReceiveOptions(), 1)
				|| ByteUtils.isBitEnabled(getReceiveOptions(), 2);
	}
	
	/**
	 * Returns the 64-bit sender/source address. 
	 * 
	 * @return The 64-bit sender/source address.
	 * 
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public XBee64BitAddress get64bitSourceAddress() {
		return sourceAddress64;
	}
	
	/**
	 * Returns the Received Signal Strength Indicator (RSSI).
	 * 
	 * @return The Received Signal Strength Indicator (RSSI).
	 */
	public int getRSSI() {
		return rssi;
	}
	
	/**
	 * Returns the receive options bitfield.
	 * 
	 * @return Receive options bitfield.
	 * 
	 * @see com.digi.xbee.api.models.XBeeReceiveOptions
	 */
	public int getReceiveOptions() {
		return receiveOptions;
	}
	
	/**
	 * Sets the received RF data.
	 * 
	 * @param rfData Received RF data.
	 */
	public void setRFData(byte[] rfData) {
		if (rfData == null)
			this.rfData = null;
		else
			this.rfData = Arrays.copyOf(rfData, rfData.length);
	}
	
	/**
	 * Returns the received RF data.
	 * 
	 * @return Received RF data.
	 */
	public byte[] getRFData() {
		if (rfData == null)
			return null;
		return Arrays.copyOf(rfData, rfData.length);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("64-bit source address", HexUtils.prettyHexString(sourceAddress64.toString()));
		parameters.put("RSSI", HexUtils.prettyHexString(HexUtils.integerToHexString(rssi, 1)));
		parameters.put("Options", HexUtils.prettyHexString(HexUtils.integerToHexString(receiveOptions, 1)));
		if (rfData != null)
			parameters.put("RF data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(rfData)));
		return parameters;
	}
}

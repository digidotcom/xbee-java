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
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a TX (Transmit) 64 Request packet. Packet is built 
 * using the parameters of the constructor or providing a valid API payload.
 * 
 * <p>A TX Request message will cause the module to transmit data as an RF 
 * Packet.</p>
 * 
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 */
public class TX64Packet extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 11; // 1 (Frame type) + 1 (frame ID) + 8 (address) + 1 (transmit options)
	
	// Variables.
	private final int transmitOptions;
	
	private final XBee64BitAddress destAddress64;
	
	private byte[] rfData;
	
	private Logger logger;
	
	/**
	 * Creates a new {@code TX64Packet} object from the given payload.
	 * 
	 * @param payload The API frame payload. It must start with the frame type 
	 *                corresponding to a TX64 Request packet ({@code 0x00}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 * 
	 * @return Parsed TX (transmit) 64 Request packet.
	 * 
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.TX_64.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH} or
	 *                                  if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code transmitOptions < 0} or
	 *                                  if {@code transmitOptions > 255}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static TX64Packet createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException("TX64 Request packet payload cannot be null.");
		
		// 1 (Frame type) + 1 (frame ID) + 8 (address) + 1 (transmit options)
		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException("Incomplete TX64 Request packet.");
		
		if ((payload[0] & 0xFF) != APIFrameType.TX_64.getValue())
			throw new IllegalArgumentException("Payload is not a TX64 Request packet.");
		
		// payload[0] is the frame type.
		int index = 1;
		
		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;
		
		// 8 bytes of address, starting at 2nd byte.
		XBee64BitAddress destAddress64 = new XBee64BitAddress(Arrays.copyOfRange(payload, index, index + 8));
		index = index + 8;
		
		// Transmit options byte.
		int transmitOptions = payload[index] & 0xFF;
		index = index + 1;
		
		// Get data.
		byte[] data = null;
		if (index < payload.length)
			data = Arrays.copyOfRange(payload, index, payload.length);
		
		return new TX64Packet(frameID, destAddress64, transmitOptions, data);
	}

	/**
	 * Class constructor. Instantiates a new {@code TX64Packet} object with
	 * the given parameters.
	 * 
	 * @param frameID Frame ID.
	 * @param destAddress64 64-bit address of the destination device.
	 * @param transmitOptions Bitfield of supported transmission options.
	 * @param rfData RF Data that is sent to the destination device.
	 * 
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code transmitOptions < 0} or
	 *                                  if {@code transmitOptions > 255}.
	 * @throws NullPointerException if {@code destAddress64 == null}.
	 * 
	 * @see com.digi.xbee.api.models.XBeeTransmitOptions
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public TX64Packet(int frameID, XBee64BitAddress destAddress64, int transmitOptions, byte[] rfData) {
		super(APIFrameType.TX_64);
		
		if (destAddress64 == null)
			throw new NullPointerException("64-bit destination address cannot be null.");
		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException("Frame ID must be between 0 and 255.");
		if (transmitOptions < 0 || transmitOptions > 255)
			throw new IllegalArgumentException("Transmit options must be between 0 and 255.");
		
		this.frameID = frameID;
		this.destAddress64 = destAddress64;
		this.transmitOptions = transmitOptions;
		this.rfData = rfData;
		this.logger = LoggerFactory.getLogger(TX64Packet.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(destAddress64.getValue());
			os.write(transmitOptions);
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
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#isBroadcast()
	 */
	@Override
	public boolean isBroadcast() {
		return get64bitDestinationAddress().equals(XBee64BitAddress.BROADCAST_ADDRESS);
	}
	
	/**
	 * Returns the 64-bit destination address.
	 * 
	 * @return The 64-bit destination address.
	 * 
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public XBee64BitAddress get64bitDestinationAddress() {
		return destAddress64;
	}
	
	/**
	 * Returns the transmit options bitfield.
	 * 
	 * @return Transmit options bitfield.
	 * 
	 * @see com.digi.xbee.api.models.XBeeTransmitOptions
	 */
	public int getTransmitOptions() {
		return transmitOptions;
	}
	
	/**
	 * Sets the RF data to send.
	 * 
	 * @param rfData RF Data to send.
	 */
	public void setRFData(byte[] rfData) {
		if (rfData == null)
			this.rfData = null;
		else
			this.rfData = Arrays.copyOf(rfData, rfData.length);
	}
	
	/**
	 * Returns the RF Data to send.
	 * 
	 * @return RF data to send.
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
		parameters.put("64-bit dest. address", HexUtils.prettyHexString(destAddress64.toString()));
		parameters.put("Options", HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1)));
		if (rfData != null)
			parameters.put("RF data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(rfData)));
		return parameters;
	}
}

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a basic and unknown XBee packet where the payload is
 * set as a byte array without a defined structure.
 * 
 * @see XBeeAPIPacket
 */
public class UnknownXBeePacket extends XBeeAPIPacket {
	
	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 1; // 1 (Frame type)
	
	// Variables
	protected byte[] rfData;
	
	private Logger logger;
	
	/**
	 * Creates a new {@code UnknownXBeePacket} from the given payload.
	 * 
	 * @param payload The API frame payload. The first byte will be the frame 
	 *                type.
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 * 
	 * @return Parsed Unknown packet.
	 * 
	 * @throws IllegalArgumentException if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static UnknownXBeePacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException("Unknown packet payload cannot be null.");
		
		// 1 (Frame type)
		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException("Incomplete Unknown packet.");
		
		// payload[0] is the frame type.
		int apiID = payload[0] & 0xFF;
		int index = 1;
		
		byte[] commandData = null;
		if (index < payload.length)
			commandData = Arrays.copyOfRange(payload, index, payload.length);
		
		return new UnknownXBeePacket(apiID, commandData);
	}
	
	/**
	 * Class constructor. Instantiates an XBee packet with the given packet 
	 * data.
	 * 
	 * @param apiIDValue The XBee API integer value of the packet.
	 * @param rfData The XBee RF Data.
	 * 
	 * @throws IllegalArgumentException if {@code apiIDValue < 0} or
	 *                                  if {@code apiIDValue > 255}.
	 */
	public UnknownXBeePacket(int apiIDValue, byte[] rfData) {
		super(apiIDValue);
		this.rfData = rfData;
		this.logger = LoggerFactory.getLogger(UnknownXBeePacket.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try {
			if (rfData != null)
				data.write(rfData);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return data.toByteArray();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#needsAPIFrameID()
	 */
	@Override
	public boolean needsAPIFrameID() {
		return false;
	}
	
	/**
	 * Sets the XBee RF Data.
	 * 
	 * @param rfData The new XBee RF Data.
	 */
	public void setRFData(byte[] rfData) {
		if (rfData == null)
			this.rfData = null;
		else
			this.rfData = Arrays.copyOf(rfData, rfData.length);
	}
	
	/**
	 * Returns the XBee RF Data of the packet.
	 * 
	 * @return The RF Data.
	 */
	public byte[] getRFData() {
		if (rfData == null)
			return null;
		return Arrays.copyOf(rfData, rfData.length);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#isBroadcast()
	 */
	@Override
	public boolean isBroadcast() {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	@Override
	protected LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		if (rfData != null)
			parameters.put("RF Data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(rfData)));
		return parameters;
	}
}

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
package com.digi.xbee.api.packet.common;

import java.util.LinkedHashMap;

import com.digi.xbee.api.models.ModemStatusEvent;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a Modem Status packet. Packet is built using the 
 * parameters of the constructor or providing a valid API payload.
 * 
 * <p>RF module status messages are sent from the module in response to specific 
 * conditions and indicates the state of the modem in that moment.</p>
 * 
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 */
public class ModemStatusPacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 2; // 1 (Frame type) + 1 (Modem status)
	
	// Variables.
	private ModemStatusEvent modemStatusEvent;
	
	/**
	 * Creates a new {@code ModemStatusPacket} object from the given payload.
	 * 
	 * @param payload The API frame payload. It must start with the frame type 
	 *                corresponding to a Modem Status packet ({@code 0x8A}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 * 
	 * @return Parsed Modem Status packet.
	 * 
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.MODEM_STATUS.getValue()} or
	 *                                  if {@code payload.length < {@value #MIN_API_PAYLOAD_LENGTH}}.
	 * @throws NullPointerException if {@code payload == null} or 
	 *                              if {@code modemStatusEvent == null}.
	 */
	public static ModemStatusPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException("Modem Status packet payload cannot be null.");
		
		// 1 (Frame type) + 1 (Modem status)
		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException("Incomplete Modem Status packet.");
		
		if ((payload[0] & 0xFF) != APIFrameType.MODEM_STATUS.getValue())
			throw new IllegalArgumentException("Payload is not a Modem Status packet.");
		
		// Get the Modem status byte (byte 1).
		int status = payload[1] & 0xFF;
		
		// Get the Modem Status enum. entry.
		ModemStatusEvent modemStatusEvent = ModemStatusEvent.get(status);
		
		return new ModemStatusPacket(modemStatusEvent);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code ModemStatusPacket} object
	 * with the given modem status.
	 * 
	 * @param modemStatusEvent Modem status event enum. entry.
	 * 
	 * @throws NullPointerException if {@code modemStatusEvent == null}.
	 */
	public ModemStatusPacket(ModemStatusEvent modemStatusEvent) {
		super(APIFrameType.MODEM_STATUS);
		
		if (modemStatusEvent == null)
			throw new NullPointerException("Modem Status event cannot be null.");
		
		this.modemStatusEvent = modemStatusEvent;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	public byte[] getAPIPacketSpecificData() {
		byte[] data = new byte[1];
		data[0] = (byte)(modemStatusEvent.getId() & 0xFF);
		return data;
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
	 * Returns modem status event enum. entry.
	 * 
	 * @return Modem status event enum. entry.
	 */
	public ModemStatusEvent getStatus() {
		return modemStatusEvent;
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
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Status", HexUtils.prettyHexString(HexUtils.integerToHexString(modemStatusEvent.getId(), 1)) + " (" + modemStatusEvent.getDescription() + ")");
		return parameters;
	}
}

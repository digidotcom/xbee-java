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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a Transmit Packet. Packet is built using the parameters 
 * of the constructor or providing a valid API payload.
 * 
 * <p>A Transmit Request API frame causes the module to send data as an RF 
 * packet to the specified destination.</p>
 * 
 * <p>The 64-bit destination address should be set to {@code 0x000000000000FFFF} 
 * for a broadcast transmission (to all devices).</p>
 * 
 * <p>The coordinator can be addressed by either setting the 64-bit address to 
 * all {@code 0x00} and the 16-bit address to {@code 0xFFFE}, OR by setting the 
 * 64-bit address to the coordinator's 64-bit address and the 16-bit address to 
 * {@code 0x0000}.</p>
 * 
 * <p>For all other transmissions, setting the 16-bit address to the correct 
 * 16-bit address can help improve performance when transmitting to multiple 
 * destinations.</p>
 * 
 * <p>If a 16-bit address is not known, this field should be set to 
 * {@code 0xFFFE} (unknown).</p> 
 * 
 * <p>The Transmit Status frame 
 * ({@link com.digi.xbee.api.packet.APIFrameType#TRANSMIT_REQUEST}) will 
 * indicate the discovered 16-bit address, if successful (see 
 * {@link com.digi.xbee.api.packet.common.TransmitStatusPacket}).</p>
 * 
 * <p>The broadcast radius can be set from {@code 0} up to {@code NH}. If set 
 * to {@code 0}, the value of {@code NH} specifies the broadcast radius
 * (recommended). This parameter is only used for broadcast transmissions.</p>
 * 
 * <p>The maximum number of payload bytes can be read with the {@code NP} 
 * command.</p>
 * 
 * <p>Several transmit options can be set using the transmit options bitfield.
 * </p>
 * 
 * @see com.digi.xbee.api.models.XBeeTransmitOptions
 * @see com.digi.xbee.api.models.XBee16BitAddress#COORDINATOR_ADDRESS
 * @see com.digi.xbee.api.models.XBee16BitAddress#UNKNOWN_ADDRESS
 * @see com.digi.xbee.api.models.XBee64BitAddress#BROADCAST_ADDRESS
 * @see com.digi.xbee.api.models.XBee64BitAddress#COORDINATOR_ADDRESS
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 */
public class TransmitPacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 14; // 1 (Frame type) + 1 (frame ID) + 8 (64-bit address) + 2 (16-bit address) + 1 (broadcast radious) + 1 (options)
	
	// Variables.
	private final XBee64BitAddress destAddress64;
	
	private final XBee16BitAddress destAddress16;
	
	private final int broadcastRadius;
	private final int transmitOptions;
	
	private byte[] rfData;
	
	private Logger logger;
	
	/**
	 * Creates a new {@code TransmitPacket} object from the given payload.
	 * 
	 * @param payload The API frame payload. It must start with the frame type 
	 *                corresponding to a Transmit packet ({@code 0x10}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 * 
	 * @return Parsed Transmit Request packet.
	 * 
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.TRANSMIT_REQUEST.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH} or
	 *                                  if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code broadcastRadius < 0} or
	 *                                  if {@code broadcastRadius > 255} or
	 *                                  if {@code transmitOptions < 0} or
	 *                                  if {@code transmitOptions > 255}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static TransmitPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException("Transmit packet payload cannot be null.");
		
		// 1 (Frame type) + 1 (frame ID) + 8 (64-bit address) + 2 (16-bit address) + 1 (broadcast radious) + 1 (options)
		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException("Incomplete Transmit packet.");
		
		if ((payload[0] & 0xFF) != APIFrameType.TRANSMIT_REQUEST.getValue())
			throw new IllegalArgumentException("Payload is not a Transmit packet.");
		
		// payload[0] is the frame type.
		int index = 1;
		
		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;
		
		// 8 bytes of 64-bit address.
		XBee64BitAddress destAddress64 = new XBee64BitAddress(Arrays.copyOfRange(payload, index, index + 8));
		index = index + 8;
		
		// 2 bytes of 16-bit address.
		XBee16BitAddress destAddress16 = new XBee16BitAddress(payload[index] & 0xFF, payload[index + 1] & 0xFF);
		index = index + 2;
		
		// Broadcast radious byte.
		int broadcastRadius = payload[index] & 0xFF;
		index = index + 1;
		
		// Options byte.
		int options = payload[index] & 0xFF;
		index = index + 1;
		
		// Get RF data.
		byte[] rfData = null;
		if (index < payload.length)
			rfData = Arrays.copyOfRange(payload, index, payload.length);
		
		return new TransmitPacket(frameID, destAddress64, destAddress16, broadcastRadius, options, rfData);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code TransmitPacket} object
	 * with the given parameters.
	 * 
	 * @param frameID Frame ID.
	 * @param destAddress64 64-bit address of the destination device.
	 * @param destAddress16 16-bit address of the destination device.
	 * @param broadcastRadius maximum number of hops a broadcast transmission 
	 *                        can occur.
	 * @param transmitOptions Bitfield of supported transmission options.
	 * @param rfData RF Data that is sent to the destination device.
	 * 
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code broadcastRadius < 0} or
	 *                                  if {@code broadcastRadius > 255} or
	 *                                  if {@code transmitOptions < 0} or
	 *                                  if {@code transmitOptions > 255}.
	 * @throws NullPointerException if {@code destAddress64 == null} or
	 *                              if {@code destAddress16 == null}.
	 * 
	 * @see com.digi.xbee.api.models.XBeeTransmitOptions
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public TransmitPacket(int frameID, XBee64BitAddress destAddress64, XBee16BitAddress destAddress16, 
			int broadcastRadius, int transmitOptions, byte[] rfData) {
		super(APIFrameType.TRANSMIT_REQUEST);
		
		if (destAddress64 == null)
			throw new NullPointerException("64-bit destination address cannot be null.");
		if (destAddress16 == null)
			throw new NullPointerException("16-bit destination address cannot be null.");
		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException("Frame ID must be between 0 and 255.");
		if (broadcastRadius < 0 || broadcastRadius > 255)
			throw new IllegalArgumentException("Broadcast radius must be between 0 and 255.");
		if (transmitOptions < 0 || transmitOptions > 255)
			throw new IllegalArgumentException("Transmit options must be between 0 and 255.");
		
		this.frameID = frameID;
		this.destAddress64 = destAddress64;
		this.destAddress16 = destAddress16;
		this.broadcastRadius = broadcastRadius;
		this.transmitOptions = transmitOptions;
		this.rfData = rfData;
		this.logger = LoggerFactory.getLogger(TransmitPacket.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try {
			data.write(destAddress64.getValue());
			data.write(destAddress16.getValue());
			data.write(broadcastRadius);
			data.write(transmitOptions);
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
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#isBroadcast()
	 */
	@Override
	public boolean isBroadcast() {
		return get64bitDestinationAddress().equals(XBee64BitAddress.BROADCAST_ADDRESS) 
				|| get16bitDestinationAddress().equals(XBee16BitAddress.BROADCAST_ADDRESS);
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
	 * Returns the 16-bit destination address.
	 * 
	 * @return The 16-bit destination address.
	 * 
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 */
	public XBee16BitAddress get16bitDestinationAddress() {
		return destAddress16;
	}
	
	/**
	 * Returns the broadcast radius.
	 * 
	 * @return The broadcast radius.
	 */
	public int getBroadcastRadius() {
		return broadcastRadius;
	}
	
	/**
	 * Returns the transmit options bitfield.
	 * 
	 * @return The transmit options bitfield.
	 * 
	 * @see com.digi.xbee.api.models.XBeeTransmitOptions
	 */
	public int getTransmitOptions() {
		return transmitOptions;
	}
	
	/**
	 * Sets the RF Data to send.
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
	 * @return RF Data to send.
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
		parameters.put("16-bit dest. address", HexUtils.prettyHexString(destAddress16.toString()));
		parameters.put("Broadcast radius", HexUtils.prettyHexString(HexUtils.integerToHexString(broadcastRadius, 1)) + " (" + broadcastRadius + ")");
		parameters.put("Options", HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1)));
		if (rfData != null)
			parameters.put("RF data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(rfData)));
		return parameters;
	}
}

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

import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents an RX16 Address IO packet. Packet is built using the 
 * parameters of the constructor or providing a valid API payload.
 * 
 * <p>I/O data is sent out the UART using an API frame.</p>
 * 
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 */
public class RX16IOPacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 5; // 1 (Frame type) + 2 (16-bit address) + 1 (RSSI) + 1 (receive options)
	
	// Variables.
	private final XBee16BitAddress sourceAddress16;
	
	private IOSample ioSample;
	
	private final int rssi;
	private final int receiveOptions;
	
	private byte[] rfData;
	
	private Logger logger;
	
	/**
	 * Creates a new {@code RX16IOPacket} object from the given payload.
	 * 
	 * @param payload The API frame payload. It must start with the frame type 
	 *                corresponding to a RX16 Address IO packet ({@code 0x83}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 * 
	 * @return Parsed RX16 Address IO packet.
	 * 
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.RX_16.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH} or
	 *                                  if {@code rssi < 0} or
	 *                                  if {@code rssi > 100} or
	 *                                  if {@code receiveOptions < 0} or
	 *                                  if {@code receiveOptions > 255}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static RX16IOPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException("RX16 Address IO packet payload cannot be null.");
		
		// 1 (Frame type) + 2 (16-bit address) + 1 (RSSI) + 1 (receive options)
		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException("Incomplete RX16 Address IO packet.");
		
		if ((payload[0] & 0xFF) != APIFrameType.RX_IO_16.getValue())
			throw new IllegalArgumentException("Payload is not a RX16 Address IO packet.");
		
		// payload[0] is the frame type.
		int index = 1;
		
		// 2 bytes of 16-bit address.
		XBee16BitAddress sourceAddress16 = new XBee16BitAddress(payload[index] & 0xFF, payload[index + 1] & 0xFF);
		index = index + 2;
		
		// Received Signal Strength Indicator byte.
		int rssi = payload[index] & 0xFF;
		index = index + 1;
				
		// Received Signal Strength Indicator byte.
		int receiveOptions = payload[index] & 0xFF;
		index = index + 1;
				
		// Get data.
		byte[] data = null;
		if (index < payload.length)
			data = Arrays.copyOfRange(payload, index, payload.length);
		
		return new RX16IOPacket(sourceAddress16, rssi, receiveOptions, data);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RX16IOPacket} object with 
	 * the given parameters.
	 * 
	 * @param sourceAddress16 16-bit address of the sender.
	 * @param rssi Received signal strength indicator.
	 * @param receiveOptions Bitfield indicating the receive options.
	 * @param rfData Received RF data.
	 * 
	 * @throws IllegalArgumentException if {@code rssi < 0} or
	 *                                  if {@code rssi > 100} or
	 *                                  if {@code receiveOptions < 0} or
	 *                                  if {@code receiveOptions > 255}.
	 * @throws NullPointerException if {@code sourceAddress16 == null}.
	 * 
	 * @see com.digi.xbee.api.models.XBeeReceiveOptions
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 */
	public RX16IOPacket(XBee16BitAddress sourceAddress16, int rssi, int receiveOptions, byte[] rfData) {
		super(APIFrameType.RX_IO_16);
		
		if (sourceAddress16 == null)
			throw new NullPointerException("16-bit source address cannot be null.");
		if (rssi < 0 || rssi > 100)
			throw new IllegalArgumentException("RSSI value must be between 0 and 100.");
		if (receiveOptions < 0 || receiveOptions > 255)
			throw new IllegalArgumentException("Receive options value must be between 0 and 255.");
		
		this.sourceAddress16 = sourceAddress16;
		this.rssi = rssi;
		this.receiveOptions = receiveOptions;
		this.rfData = rfData;
		if (rfData != null && rfData.length >= 5)
			ioSample = new IOSample(rfData);
		else
			ioSample = null;
		this.logger = LoggerFactory.getLogger(RX16IOPacket.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(sourceAddress16.getValue());
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
	 * Returns the 16-bit sender/source address. 
	 * 
	 * @return The 16-bit sender/source address.
	 * 
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 */
	public XBee16BitAddress get16bitSourceAddress() {
		return sourceAddress16;
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
	 * Returns the IO sample corresponding to the data contained in the packet.
	 * 
	 * @return The IO sample of the packet, {@code null} if the packet has not 
	 *         any data or if the sample could not be generated correctly.
	 * 
	 * @see com.digi.xbee.api.io.IOSample
	 */
	public IOSample getIOSample() {
		return ioSample;
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
		
		// Modify the ioSample accordingly.
		if (rfData != null && rfData.length >= 5)
			ioSample = new IOSample(this.rfData);
		else
			ioSample = null;
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
		parameters.put("16-bit source address", HexUtils.prettyHexString(sourceAddress16.toString()));
		parameters.put("RSSI", HexUtils.prettyHexString(HexUtils.integerToHexString(rssi, 1)));
		parameters.put("Options", HexUtils.prettyHexString(HexUtils.integerToHexString(receiveOptions, 1)));
		if (ioSample != null) {
			parameters.put("Number of samples", HexUtils.prettyHexString(HexUtils.integerToHexString(1, 1))); // There is always 1 sample.
			parameters.put("Digital channel mask", HexUtils.prettyHexString(HexUtils.integerToHexString(ioSample.getDigitalMask(), 2)));
			parameters.put("Analog channel mask", HexUtils.prettyHexString(HexUtils.integerToHexString(ioSample.getAnalogMask(), 2)));
			for (int i = 0; i < 16; i++) {
				if (ioSample.hasDigitalValue(IOLine.getDIO(i)))
					parameters.put(IOLine.getDIO(i).getName() + " digital value", ioSample.getDigitalValue(IOLine.getDIO(i)).getName());
			}
			for (int i = 0; i < 6; i++) {
				if (ioSample.hasAnalogValue(IOLine.getDIO(i)))
					parameters.put(IOLine.getDIO(i).getName() + " analog value", HexUtils.prettyHexString(HexUtils.integerToHexString(ioSample.getAnalogValue(IOLine.getDIO(i)), 2)));
			}
		} else if (rfData != null)
			parameters.put("RF data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(rfData)));
		return parameters;
	}
}

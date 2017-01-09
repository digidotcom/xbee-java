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
package com.digi.xbee.api.packet.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents an Explicit RX Indicator packet. Packet is 
 * built using the parameters of the constructor or providing a valid API 
 * payload.
 * 
 * <p>When the modem receives an RF packet it is sent out the UART using this 
 * message type (when AO=1).</p>
 * 
 * <p>This packet is received when external devices send explicit addressing 
 * packets to this module.</p>
 * 
 * <p>Among received data, some options can also be received indicating 
 * transmission parameters.</p> 
 * 
 * @see com.digi.xbee.api.models.XBeeReceiveOptions
 * @see com.digi.xbee.api.packet.common.ExplicitAddressingPacket
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 */
public class ExplicitRxIndicatorPacket extends XBeeAPIPacket {

	// Constants
	private static final int MIN_API_PAYLOAD_LENGTH = 18; // 1 (Frame type)  + 8 (64-bit address) + 2 (16-bit address) + 1 (source endpoint) + 1 (destination endpoint) + 2 (cluster ID) + 2 (profile ID) + 1 (receive options)
	
	public static final int DATA_ENDPOINT = 0xE8;
	public static final int DATA_CLUSTER = 0x0011;
	public static final int DIGI_PROFILE = 0xC105;
	
	// Variables
	private final XBee64BitAddress sourceAddress64;
	
	private final XBee16BitAddress sourceAddress16;
	
	private final int sourceEndpoint;
	private final int destEndpoint;
	private final int clusterID;
	private final int profileID;
	private final int receiveOptions;
	
	private byte[] rfData;
	
	private Logger logger;
	
	/**
	 * Creates a new {@code ExplicitRxIndicatorPacket} object from the given 
	 * payload.
	 * 
	 * @param payload The API frame payload. It must start with the frame type 
	 *                corresponding to an Explicit RX Indicator packet 
	 *                ({@code 0x91}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 * 
	 * @return Parsed Explicit RX Indicator packet.
	 * 
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.EXPLICIT_RX_INDICATOR.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH} or
	 *                                  if {@code sourceEndpoint < 0} or 
	 *                                  if {@code sourceEndpoint > 255} or 
	 *                                  if {@code destEndpoint < 0} or 
	 *                                  if {@code destEndpoint > 255} or 
	 *                                  if {@code clusterID < 0} or 
	 *                                  if {@code clusterID > 65535} or
	 *                                  if {@code profileID < 0} or 
	 *                                  if {@code profileID > 65535} or
	 *                                  if {@code transmitOptions < 0} or
	 *                                  if {@code transmitOptions > 255}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static ExplicitRxIndicatorPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException("Explicit Rx Indicator packet payload cannot be null.");
		
		// 1 (Frame type) + 8 (64-bit address) + 2 (16-bit address) + 1 (source endpoint) + 1 (destination endpoint) + 2 (cluster ID) + 2 (profile ID) + 1 (receive options)
		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException("Incomplete Explicit Rx Indicator packet.");
		
		if ((payload[0] & 0xFF) != APIFrameType.EXPLICIT_RX_INDICATOR.getValue())
			throw new IllegalArgumentException("Payload is not an Explicit Rx Indicator packet.");
		
		// payload[0] is the frame type.
		int index = 1;
		
		// 8 bytes of 64-bit address.
		XBee64BitAddress destAddress64 = new XBee64BitAddress(Arrays.copyOfRange(payload, index, index + 8));
		index = index + 8;
		
		// 2 bytes of 16-bit address.
		XBee16BitAddress destAddress16 = new XBee16BitAddress(payload[index] & 0xFF, payload[index + 1] & 0xFF);
		index = index + 2;
		
		// Source endpoint byte.
		int sourceEndpoint = payload[index] & 0xFF;
		index = index + 1;
		
		// Destination endpoint byte.
		int destEndpoint = payload[index] & 0xFF;
		index = index + 1;
		
		// 2 bytes of cluster ID.
		int clusterID = (payload[index] & 0xFF) << 8 | payload[index + 1] & 0xFF;
		index = index + 2;
		
		// 2 bytes of profile ID.
		int profileID = (payload[index] & 0xFF) << 8 | payload[index + 1] & 0xFF;
		index = index + 2;
		
		// Receive options byte.
		int receiveOptions = payload[index] & 0xFF;
		index = index + 1;
		
		// Get RF data.
		byte[] rfData = null;
		if (index < payload.length)
			rfData = Arrays.copyOfRange(payload, index, payload.length);
		
		return new ExplicitRxIndicatorPacket(destAddress64, destAddress16, sourceEndpoint, destEndpoint, clusterID, profileID, receiveOptions, rfData);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code ExplicitRxIndicatorPacket} 
	 * object with the given parameters.
	 * 
	 * @param sourceAddress64 64-bit address of the sender device.
	 * @param sourceAddress16 16-bit address of the sender device.
	 * @param sourceEndpoint Endpoint of the source that initiated the 
	 *                       transmission.
	 * @param destEndpoint Endpoint of the destination the message was 
	 *                     addressed to.
	 * @param clusterID Cluster ID the packet was addressed to.
	 * @param profileID Profile ID the packet was addressed to.
	 * @param receiveOptions BitField of receive options.
	 * @param rfData Received RF data.
	 * 
	 * @throws IllegalArgumentException if {@code sourceEndpoint < 0} or 
	 *                                  if {@code sourceEndpoint > 255} or 
	 *                                  if {@code destEndpoint < 0} or 
	 *                                  if {@code destEndpoint > 255} or 
	 *                                  if {@code clusterID < 0} or 
	 *                                  if {@code clusterID > 65535} or
	 *                                  if {@code profileID < 0} or 
	 *                                  if {@code profileID > 65535} or
	 *                                  if {@code receiveOptions < 0} or
	 *                                  if {@code receiveOptions > 255}.
	 * @throws NullPointerException if {@code sourceAddress64 == null} or 
	 *                              if {@code sourceAddress16 == null}.
	 * 
	 * @see com.digi.xbee.api.models.XBeeReceiveOptions
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public ExplicitRxIndicatorPacket(XBee64BitAddress sourceAddress64, XBee16BitAddress sourceAddress16, 
			int sourceEndpoint, int destEndpoint, int clusterID, int profileID,
			int receiveOptions, byte[] rfData){
		super(APIFrameType.EXPLICIT_RX_INDICATOR);
		
		if (sourceAddress64 == null)
			throw new NullPointerException("64-bit source address cannot be null.");
		if (sourceAddress16 == null)
			throw new NullPointerException("16-bit source address cannot be null.");
		if (sourceEndpoint < 0 || sourceEndpoint > 255)
			throw new IllegalArgumentException("Source endpoint must be between 0 and 255.");
		if (destEndpoint < 0 || destEndpoint > 255)
			throw new IllegalArgumentException("Destination endpoint must be between 0 and 255.");
		if (clusterID < 0 || clusterID > 65535)
			throw new IllegalArgumentException("Cluster ID must be between 0 and 65535.");
		if (profileID < 0 || profileID > 65535)
			throw new IllegalArgumentException("Profile ID must be between 0 and 65535.");
		if (receiveOptions < 0 || receiveOptions > 255)
			throw new IllegalArgumentException("Receive options must be between 0 and 255.");
		
		this.sourceAddress64 = sourceAddress64;
		this.sourceAddress16 = sourceAddress16;
		this.sourceEndpoint = sourceEndpoint;
		this.destEndpoint = destEndpoint;
		this.clusterID = clusterID;
		this.profileID = profileID;
		this.receiveOptions = receiveOptions;
		this.rfData = rfData;
		this.logger = LoggerFactory.getLogger(ExplicitRxIndicatorPacket.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	public byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try {
			data.write(sourceAddress64.getValue());
			data.write(sourceAddress16.getValue());
			data.write(sourceEndpoint);
			data.write(destEndpoint);
			data.write(clusterID >> 8);
			data.write(clusterID);
			data.write(profileID >> 8);
			data.write(profileID);
			data.write(receiveOptions);
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
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#isBroadcast()
	 */
	@Override
	public boolean isBroadcast() {
		if (ByteUtils.isBitEnabled(getReceiveOptions(), 1))
			return true;
		return false;
	}
	
	/**
	 * Returns the 64 bit sender/source address.
	 * 
	 * @return The 64 bit sender/source address.
	 * 
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public XBee64BitAddress get64BitSourceAddress() {
		return sourceAddress64;
	}
	
	/**
	 * Returns the 16 bit sender/source address.
	 * 
	 * @return The 16 bit sender/source address.
	 * 
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 */
	public XBee16BitAddress get16BitSourceAddress() {
		return sourceAddress16;
	}
	
	/**
	 * Returns the source endpoint of the transmission.
	 * 
	 * @return The source endpoint of the transmission.
	 */
	public int getSourceEndpoint() {
		return sourceEndpoint;
	}
	
	/**
	 * Returns the destination endpoint of the transmission.
	 * 
	 * @return The destination endpoint of the transmission.
	 */
	public int getDestinationEndpoint() {
		return destEndpoint;
	}
	
	/**
	 * Returns the cluster ID used in the transmission.
	 * 
	 * @return The cluster ID used in the transmission.
	 */
	public int getClusterID() {
		return clusterID;
	}
	
	/**
	 * Returns the profile ID used in the transmission.
	 * 
	 * @return The profile ID used in the transmission.
	 */
	public int getProfileID() {
		return profileID;
	}
	
	/**
	 * Returns the receive options bitfield.
	 * 
	 * @return The receive options bitfield.
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
		parameters.put("16-bit source address", HexUtils.prettyHexString(sourceAddress16.toString()));
		parameters.put("Source endpoint", HexUtils.prettyHexString(HexUtils.integerToHexString(sourceEndpoint, 1)));
		parameters.put("Dest. endpoint", HexUtils.prettyHexString(HexUtils.integerToHexString(destEndpoint, 1)));
		parameters.put("Cluster ID", HexUtils.prettyHexString(HexUtils.integerToHexString(clusterID, 2)));
		parameters.put("Profile ID", HexUtils.prettyHexString(HexUtils.integerToHexString(profileID, 2)));
		parameters.put("Receive options", HexUtils.prettyHexString(HexUtils.integerToHexString(receiveOptions, 1)));
		if (rfData != null)
			parameters.put("RF data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(rfData)));
		return parameters;
	}
}

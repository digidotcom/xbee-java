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
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents an Explicit Addressing Command packet. Packet is 
 * built using the parameters of the constructor or providing a valid API 
 * payload.
 * 
 * <p>Allows application layer fields (endpoint and cluster ID) to be 
 * specified for a data transmission. Similar to the Transmit Request, but 
 * also requires application layer addressing fields to be specified 
 * (endpoints, cluster ID, profile ID). An Explicit Addressing Request API 
 * frame causes the module to send data as an RF packet to the specified 
 * destination, using the specified source and destination endpoints, cluster 
 * ID, and profile ID.</p>
 * 
 * <p>The 64-bit destination address should be set to 
 * {@code 0x000000000000FFFF} for a broadcast transmission (to all
 * devices).</p>
 * 
 * <p>The coordinator can be addressed by either setting the 64-bit address 
 * to all {@code 0x00} and the 16-bit address to {@code 0xFFFE}, OR by 
 * setting the 64-bit address to the coordinator's 64-bit address and the 
 * 16-bit address to {@code 0x0000}.</p>
 * 
 * <p>For all other transmissions, setting the 16-bit address to the correct 
 * 16-bit address can help improve performance when transmitting to
 * multiple destinations.</p>
 * 
 * <p>If a 16-bit address is not known, this field should be set to 
 * {@code 0xFFFE} (unknown).</p>.
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
 * command. Note: if source routing is used, the RF payload will be reduced 
 * by two bytes per intermediate hop in the source route.</p>
 * 
 * <p>Several transmit options can be set using the transmit options bitfield.
 * </p>
 * 
 * @see com.digi.xbee.api.models.XBeeTransmitOptions
 * @see com.digi.xbee.api.models.XBee16BitAddress#COORDINATOR_ADDRESS
 * @see com.digi.xbee.api.models.XBee16BitAddress#UNKNOWN_ADDRESS
 * @see com.digi.xbee.api.models.XBee64BitAddress#BROADCAST_ADDRESS
 * @see com.digi.xbee.api.models.XBee64BitAddress#COORDINATOR_ADDRESS
 * @see com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 */
public class ExplicitAddressingPacket extends XBeeAPIPacket {

	// Constants
	private static final int MIN_API_PAYLOAD_LENGTH = 20; // 1 (Frame type) + 1 (frame ID) + 8 (64-bit address) + 2 (16-bit address) + 1 (source endpoint) + 1 (destination endpoint) + 2 (cluster ID) + 2 (profile ID) + 1 (broadcast radius) + 1 (options)
	
	// Variables
	private final XBee64BitAddress destAddress64;
	
	private final XBee16BitAddress destAddress16;
	
	private final int broadcastRadius;
	private final int transmitOptions;
	
	private final int sourceEndpoint;
	private final int destEndpoint;
	private final int clusterID;
	private final int profileID;
	private byte[] rfData;
	
	private Logger logger;
	
	/**
	 * Creates a new {@code ExplicitAddressingPacket} object from the given 
	 * payload.
	 * 
	 * @param payload The API frame payload. It must start with the frame type 
	 *                corresponding to an Explicit Addressing packet 
	 *                ({@code 0x11}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 * 
	 * @return Parsed Explicit Addressing packet.
	 * 
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.EXPLICIT_ADDRESSING_COMMAND_FRAME.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH} or
	 *                                  if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or 
	 *                                  if {@code sourceEndpoint < 0} or 
	 *                                  if {@code sourceEndpoint > 255} or 
	 *                                  if {@code destEndpoint < 0} or 
	 *                                  if {@code destEndpoint > 255} or 
	 *                                  if {@code clusterID < 0} or 
	 *                                  if {@code clusterID > 65535} or 
	 *                                  if {@code profileID < 0} or 
	 *                                  if {@code profileID > 65535} or 
	 *                                  if {@code broadcastRadius < 0} or
	 *                                  if {@code broadcastRadius > 255} or
	 *                                  if {@code transmitOptions < 0} or
	 *                                  if {@code transmitOptions > 255}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static ExplicitAddressingPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException("Explicit Addressing packet payload cannot be null.");
		
		// 1 (Frame type) + 1 (frame ID) + 8 (64-bit address) + 2 (16-bit address) + 1 (source endpoint) + 1 (destination endpoint) + 2 (cluster ID) + 2 (profile ID) + 1 (broadcast radius) + 1 (options)
		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException("Incomplete Explicit Addressing packet.");
		
		if ((payload[0] & 0xFF) != APIFrameType.EXPLICIT_ADDRESSING_COMMAND_FRAME.getValue())
			throw new IllegalArgumentException("Payload is not an Explicit Addressing packet.");
		
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
		
		// Broadcast radius byte.
		int broadcastRadius = payload[index] & 0xFF;
		index = index + 1;
		
		// Options byte.
		int options = payload[index] & 0xFF;
		index = index + 1;
		
		// Get RF data.
		byte[] rfData = null;
		if (index < payload.length)
			rfData = Arrays.copyOfRange(payload, index, payload.length);
		
		return new ExplicitAddressingPacket(frameID, destAddress64, destAddress16, sourceEndpoint, destEndpoint, clusterID, profileID, broadcastRadius, options, rfData);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code ExplicitAddressingPacket} 
	 * object with the given parameters.
	 * 
	 * @param frameID Frame ID.
	 * @param destAddress64 64-bit address of the destination device.
	 * @param destAddress16 16-bit address of the destination device.
	 * @param sourceEndpoint Source endpoint for the transaction.
	 * @param destEndpoint Destination endpoint for the transaction.
	 * @param clusterID Cluster ID used in the transaction.
	 * @param profileID Profile ID used in the transaction.
	 * @param broadcastRadius Maximum number of hops a broadcast transmission 
	 *                        can traverse. Set to 0 to use the network 
	 *                        maximum hops value.
	 * @param transmitOptions Bitfield of supported transmission options.
	 * @param rfData RF Data that is sent to the destination device.
	 * 
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or 
	 *                                  if {@code sourceEndpoint < 0} or 
	 *                                  if {@code sourceEndpoint > 255} or 
	 *                                  if {@code destEndpoint < 0} or 
	 *                                  if {@code destEndpoint > 255} or 
	 *                                  if {@code clusterID < 0} or 
	 *                                  if {@code clusterID > 65535} or 
	 *                                  if {@code profileID < 0} or 
	 *                                  if {@code profileID > 65535} or 
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
	public ExplicitAddressingPacket(int frameID, XBee64BitAddress destAddress64, XBee16BitAddress destAddress16,
			int sourceEndpoint, int destEndpoint, int clusterID, int profileID, int broadcastRadius,
			int transmitOptions, byte[] rfData) {
		super(APIFrameType.EXPLICIT_ADDRESSING_COMMAND_FRAME);
		
		if (destAddress64 == null)
			throw new NullPointerException("64-bit destination address cannot be null.");
		if (destAddress16 == null)
			throw new NullPointerException("16-bit destination address cannot be null.");
		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException("Frame ID must be between 0 and 255.");
		if (sourceEndpoint < 0 || sourceEndpoint > 255)
			throw new IllegalArgumentException("Source endpoint must be between 0 and 255.");
		if (destEndpoint < 0 || destEndpoint > 255)
			throw new IllegalArgumentException("Destination endpoint must be between 0 and 255.");
		if (clusterID < 0 || clusterID > 65535)
			throw new IllegalArgumentException("Cluster ID must be between 0 and 65535.");
		if (profileID < 0 || profileID > 65535)
			throw new IllegalArgumentException("Profile ID must be between 0 and 65535.");
		if (broadcastRadius < 0 || broadcastRadius > 255)
			throw new IllegalArgumentException("Broadcast radius must be between 0 and 255.");
		if (transmitOptions < 0 || transmitOptions > 255)
			throw new IllegalArgumentException("Transmit options must be between 0 and 255.");
		
		this.frameID = frameID;
		this.destAddress64 = destAddress64;
		this.destAddress16 = destAddress16;
		this.sourceEndpoint = sourceEndpoint;
		this.destEndpoint = destEndpoint;
		this.clusterID = clusterID;
		this.profileID = profileID;
		this.broadcastRadius = broadcastRadius;
		this.transmitOptions = transmitOptions;
		this.rfData = rfData;
		this.logger = LoggerFactory.getLogger(ExplicitAddressingPacket.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	public byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try {
			data.write(destAddress64.getValue());
			data.write(destAddress16.getValue());
			data.write(sourceEndpoint);
			data.write(destEndpoint);
			data.write(clusterID >> 8);
			data.write(clusterID);
			data.write(profileID >> 8);
			data.write(profileID);
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
		if (get64BitDestinationAddress().equals(XBee64BitAddress.BROADCAST_ADDRESS) 
				|| get16BitDestinationAddress().equals(XBee16BitAddress.BROADCAST_ADDRESS))
			return true;
		return false;
	}
	
	/**
	 * Returns the 64-bit destination address.
	 * 
	 * @return The 64-bit destination address.
	 * 
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public XBee64BitAddress get64BitDestinationAddress() {
		return destAddress64;
	}
	
	/**
	 * Returns the 16-bit destination address.
	 * 
	 * @return The 16-bit destination address.
	 * 
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 */
	public XBee16BitAddress get16BitDestinationAddress() {
		return destAddress16;
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
		parameters.put("Frame ID", HexUtils.prettyHexString(HexUtils.integerToHexString(frameID, 1)) + " (" + frameID + ")");
		parameters.put("64-bit dest. address", HexUtils.prettyHexString(destAddress64.toString()));
		parameters.put("16-bit dest. address", HexUtils.prettyHexString(destAddress16.toString()));
		parameters.put("Source endpoint", HexUtils.prettyHexString(HexUtils.integerToHexString(sourceEndpoint, 1)));
		parameters.put("Dest. endpoint", HexUtils.prettyHexString(HexUtils.integerToHexString(destEndpoint, 1)));
		parameters.put("Cluster ID", HexUtils.prettyHexString(HexUtils.integerToHexString(clusterID, 2)));
		parameters.put("Profile ID", HexUtils.prettyHexString(HexUtils.integerToHexString(profileID, 2)));
		parameters.put("Broadcast radius", HexUtils.prettyHexString(HexUtils.integerToHexString(broadcastRadius, 1)) + " (" + broadcastRadius + ")");
		parameters.put("Transmit options", HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1)));
		if (rfData != null)
			parameters.put("RF data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(rfData)));
		return parameters;
	}
}

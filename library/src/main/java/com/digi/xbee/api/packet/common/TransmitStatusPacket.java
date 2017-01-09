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
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBeeDiscoveryStatus;
import com.digi.xbee.api.models.XBeeTransmitStatus;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a Transmit Status Packet. Packet is built using the 
 * parameters of the constructor or providing a valid API payload.
 * 
 * <p>When a Transmit Request is completed, the module sends a Transmit Status 
 * message. This message will indicate if the packet was transmitted 
 * successfully or if there was a failure.</p>
 * 
 * <p>This packet is the response to standard and explicit transmit requests.
 * </p>
 * 
 * @see TransmitPacket
 */
public class TransmitStatusPacket extends XBeeAPIPacket {
	
	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 7; // 1 (Frame type) + 1 (frame ID) + 2 (16-bit address) + 1 (retry count) + 1 (delivery status) + 1 (discovery status)
		
	// Variables.
	private final XBee16BitAddress destAddress16;
	
	private final int tranmistRetryCount;
	private final XBeeTransmitStatus transmitStatus;
	private final XBeeDiscoveryStatus discoveryStatus;
	
	private Logger logger;
	
	/**
	 * Creates a new {@code TransmitStatusPacket} object from the given payload.
	 * 
	 * @param payload The API frame payload. It must start with the frame type 
	 *                corresponding to a Transmit Status packet ({@code 0x8B}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 * 
	 * @return Parsed Transmit Status packet.
	 * 
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.TRANSMIT_STATUS.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH} or
	 *                                  if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code tranmistRetryCount < 0} or
	 *                                  if {@code tranmistRetryCount > 255}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static TransmitStatusPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException("Transmit Status packet payload cannot be null.");
		
		// 1 (Frame type) + 1 (frame ID) + 2 (16-bit address) + 1 (retry count) + 1 (delivery status) + 1 (discovery status)
		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException("Incomplete Transmit Status packet.");
		
		if ((payload[0] & 0xFF) != APIFrameType.TRANSMIT_STATUS.getValue())
			throw new IllegalArgumentException("Payload is not a Transmit Status packet.");
		
		// payload[0] is the frame type.
		int index = 1;
		
		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;
		
		// 2 bytes of 16-bit address.
		XBee16BitAddress address = new XBee16BitAddress(payload[index] & 0xFF, payload[index + 1] & 0xFF);
		index = index + 2;
		
		// Retry count byte.
		int retryCount = payload[index] & 0xFF;
		index = index + 1;
		
		// Delivery status byte.
		int deliveryStatus = payload[index] & 0xFF;
		index = index + 1;
		
		// Discovery status byte.
		int discoveryStatus = payload[index] & 0xFF;
		
		// TODO if XBeeTransmitStatus is unknown????
		return new TransmitStatusPacket(frameID, address, retryCount, 
				XBeeTransmitStatus.get(deliveryStatus), XBeeDiscoveryStatus.get(discoveryStatus));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code TransmitStatusPacket} 
	 * object with the given parameters.
	 * 
	 * @param frameID Frame ID.
	 * @param destAddress16 16-bit Network address the packet was delivered to.
	 * @param tranmistRetryCount The number of application transmission retries 
	 *                           that took place.
	 * @param transmitStatus Transmit status.
	 * @param discoveryStatus Discovery status.
	 * 
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code tranmistRetryCount < 0} or
	 *                                  if {@code tranmistRetryCount > 255}.
	 * @throws NullPointerException if {@code destAddress16 == null} or
	 *                              if {@code transmitStatus == null} or
	 *                              if {@code discoveryStatus == null}.
	 * 
	 * @see com.digi.xbee.api.models.XBeeDiscoveryStatus
	 * @see com.digi.xbee.api.models.XBeeTransmitStatus
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 */
	public TransmitStatusPacket(int frameID, XBee16BitAddress destAddress16, int tranmistRetryCount, 
			XBeeTransmitStatus transmitStatus, XBeeDiscoveryStatus discoveryStatus) {
		super(APIFrameType.TRANSMIT_STATUS);
		
		if (destAddress16 == null)
			throw new NullPointerException("16-bit destination address cannot be null.");
		if (transmitStatus == null)
			throw new NullPointerException("Delivery status cannot be null.");
		if (discoveryStatus == null)
			throw new NullPointerException("Discovery status cannot be null.");
		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException("Frame ID must be between 0 and 255.");
		if (tranmistRetryCount < 0 || tranmistRetryCount > 255)
			throw new IllegalArgumentException("Transmit retry count must be between 0 and 255.");
		
		this.frameID = frameID;
		this.destAddress16 = destAddress16;
		this.tranmistRetryCount = tranmistRetryCount;
		this.transmitStatus = transmitStatus;
		this.discoveryStatus = discoveryStatus;
		this.logger = LoggerFactory.getLogger(TransmitStatusPacket.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try {
			data.write(destAddress16.getValue());
			data.write(tranmistRetryCount);
			data.write(transmitStatus.getId());
			data.write(discoveryStatus.getId());
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
	 * Returns the transmit retry count.
	 * 
	 * @return Transmit retry count.
	 */
	public int getTransmitRetryCount() {
		return tranmistRetryCount;
	}
	
	/**
	 * Returns the transmit status.
	 * 
	 * @return Transmit status.
	 * 
	 * @see com.digi.xbee.api.models.XBeeTransmitStatus
	 */
	public XBeeTransmitStatus getTransmitStatus() {
		return transmitStatus;
	}
	
	/**
	 * Returns the discovery status.
	 * 
	 * @return Discovery status.
	 * 
	 * @see XBeeDiscoveryStatus
	 */
	public XBeeDiscoveryStatus getDiscoveryStatus() {
		return discoveryStatus;
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
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("16-bit dest. address", HexUtils.prettyHexString(destAddress16.toString()));
		parameters.put("Tx. retry count", 
				HexUtils.prettyHexString(HexUtils.integerToHexString(tranmistRetryCount, 1)) 
				+ " (" + tranmistRetryCount + ")");
		parameters.put("Delivery status", 
				HexUtils.prettyHexString(HexUtils.integerToHexString(transmitStatus.getId(), 1)) 
				+ " (" + transmitStatus.getDescription() + ")");
		parameters.put("Discovery status", 
				HexUtils.prettyHexString(HexUtils.integerToHexString(discoveryStatus.getId(), 1)) 
				+ " (" + discoveryStatus.getDescription() + ")");
		return parameters;
	}
}

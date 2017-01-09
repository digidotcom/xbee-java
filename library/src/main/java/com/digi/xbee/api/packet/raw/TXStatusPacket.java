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
package com.digi.xbee.api.packet.raw;

import java.util.LinkedHashMap;

import com.digi.xbee.api.models.XBeeTransmitStatus;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a TX (Transmit) Status packet. Packet is built using 
 * the parameters of the constructor or providing a valid API payload.
 * 
 * <p>When a TX Request is completed, the module sends a TX Status message. 
 * This message will indicate if the packet was transmitted successfully or if 
 * there was a failure.</p>
 * 
 * @see TX16Packet
 * @see TX64Packet
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 */
public class TXStatusPacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 3; // 1 (Frame type) + 1 (frame ID) + 1 (status)
	
	// Variables.
	private final XBeeTransmitStatus transmitStatus;
	
	/**
	 * Creates a new {@code TXStatusPacket} object from the given payload.
	 * 
	 * @param payload The API frame payload. It must start with the frame type 
	 *                corresponding to a TX Status packet ({@code 0x89}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 * 
	 * @return Parsed TX status packet.
	 * 
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.TX_STATUS.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH} or
	 *                                  if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static TXStatusPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException("TX Status packet payload cannot be null.");
		
		// 1 (Frame type) + 1 (frame ID) + 1 (status)
		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException("Incomplete TX Status packet.");
		
		if ((payload[0] & 0xFF) != APIFrameType.TX_STATUS.getValue())
			throw new IllegalArgumentException("Payload is not a TX Status packet.");
		
		// payload[0] is the frame type.
		int index = 1;
		
		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;
		
		// Status byte.
		int status = payload[index] & 0xFF;
		
		// TODO if status is unknown????
		return new TXStatusPacket(frameID, XBeeTransmitStatus.get(status));
	}
	
	/**
	 * Class constructor. Instantiates a new {@code TXStatusPacket} object
	 * with the given parameters.
	 * 
	 * @param frameID Packet frame ID.
	 * @param transmitStatus Transmit status.
	 * 
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 * @throws NullPointerException if {@code transmitStatus == null}.
	 * 
	 * @see com.digi.xbee.api.models.XBeeTransmitStatus
	 */
	public TXStatusPacket(int frameID, XBeeTransmitStatus transmitStatus) {
		super(APIFrameType.TX_STATUS);
		
		if (transmitStatus == null)
			throw new NullPointerException("Transmit status cannot be null.");
		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException("Frame ID must be between 0 and 255.");
		
		this.frameID = frameID;
		this.transmitStatus = transmitStatus;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		return new byte[] {(byte)transmitStatus.getId()};
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
	 * Returns the transmit status.
	 * 
	 * @return Transmit status.
	 * 
	 * @see com.digi.xbee.api.models.XBeeTransmitStatus
	 */
	public XBeeTransmitStatus getTransmitStatus() {
		return transmitStatus;
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
		parameters.put("Status", 
				HexUtils.prettyHexString(HexUtils.integerToHexString(transmitStatus.getId(), 1)) 
				+ " (" + transmitStatus.getDescription() + ")");
		return parameters;
	}
}

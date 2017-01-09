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
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a Receive Packet. Packet is built using the parameters
 * of the constructor or providing a valid API payload.
 * 
 * <p>When the module receives an RF packet, it is sent out the UART using this 
 * message type.</p>
 * 
 * <p>This packet is received when external devices send transmit request 
 * packets to this module.</p>
 * 
 * <p>Among received data, some options can also be received indicating 
 * transmission parameters.</p>
 * 
 * @see TransmitPacket
 * @see com.digi.xbee.api.models.XBeeReceiveOptions
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 */
public class ReceivePacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 12; // 1 (Frame type) + 8 (32-bit address) + 2 (16-bit address) + 1 (receive options)
	
	// Variables.
	private final XBee64BitAddress sourceAddress64;
	
	private final XBee16BitAddress sourceAddress16;
	
	private final int receiveOptions;
	
	private byte[] rfData;
	
	private Logger logger;

	/**
	 * Creates a new {@code ReceivePacket} object from the given payload.
	 * 
	 * @param payload The API frame payload. It must start with the frame type 
	 *                corresponding to a Receive packet ({@code 0x90}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 * 
	 * @return Parsed ZigBee Receive packet.
	 * 
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.RECEIVE_PACKET.getValue()} or
	 *                                  if {@code payload.length < {@value #MIN_API_PAYLOAD_LENGTH}} or
	 *                                  if {@code receiveOptions < 0} or
	 *                                  if {@code receiveOptions > 255}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static ReceivePacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException("Receive packet payload cannot be null.");
		
		// 1 (Frame type) + 8 (32-bit address) + 2 (16-bit address) + 1 (receive options)
		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException("Incomplete Receive packet.");
		
		if ((payload[0] & 0xFF) != APIFrameType.RECEIVE_PACKET.getValue())
			throw new IllegalArgumentException("Payload is not a Receive packet.");
		
		// payload[0] is the frame type.
		int index = 1;
		
		// 2 bytes of 16-bit address.
		XBee64BitAddress sourceAddress64 = new XBee64BitAddress(Arrays.copyOfRange(payload, index, index + 8));
		index = index + 8;
		
		// 2 bytes of 16-bit address.
		XBee16BitAddress sourceAddress16 = new XBee16BitAddress(payload[index] & 0xFF, payload[index + 1] & 0xFF);
		index = index + 2;
		
		// Receive options
		int receiveOptions = payload[index] & 0xFF;
		index = index + 1;
		
		// Get data.
		byte[] data = null;
		if (index < payload.length)
			data = Arrays.copyOfRange(payload, index, payload.length);
		
		return new ReceivePacket(sourceAddress64, sourceAddress16, receiveOptions, data);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code ReceivePacket} object
	 * with the given parameters.
	 * 
	 * @param sourceAddress64 64-bit address of the sender.
	 * @param sourceAddress16 16-bit address of the sender.
	 * @param receiveOptions Bitfield indicating the receive options.
	 * @param rfData Received RF data.
	 * 
	 * @throws IllegalArgumentException if {@code receiveOptions < 0} or
	 *                                  if {@code receiveOptions > 255}.
	 * @throws NullPointerException if {@code sourceAddress64 == null} or 
	 *                              if {@code sourceAddress16 == null}.
	 * 
	 * @see com.digi.xbee.api.models.XBeeReceiveOptions
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public ReceivePacket(XBee64BitAddress sourceAddress64, XBee16BitAddress sourceAddress16, int receiveOptions, byte[] rfData){
		super(APIFrameType.RECEIVE_PACKET);
		
		if (sourceAddress64 == null)
			throw new NullPointerException("64-bit source address cannot be null.");
		if (sourceAddress16 == null)
			throw new NullPointerException("16-bit source address cannot be null.");
		if (receiveOptions < 0 || receiveOptions > 255)
			throw new IllegalArgumentException("Receive options value must be between 0 and 255.");
		
		this.sourceAddress64 = sourceAddress64;
		this.sourceAddress16 = sourceAddress16;
		this.receiveOptions = receiveOptions;
		this.rfData = rfData;
		this.logger = LoggerFactory.getLogger(ReceivePacket.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try {
			data.write(sourceAddress64.getValue());
			data.write(sourceAddress16.getValue());
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
		return ByteUtils.isBitEnabled(getReceiveOptions(), 1);
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
		parameters.put("16-bit source address", HexUtils.prettyHexString(sourceAddress16.toString()));
		parameters.put("Receive options", HexUtils.prettyHexString(HexUtils.integerToHexString(receiveOptions, 1)));
		if (rfData != null)
			parameters.put("RF data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(rfData)));
		return parameters;
	}
}

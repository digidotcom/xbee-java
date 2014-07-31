/**
* Copyright (c) 2014 Digi International Inc.,
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
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeTransmitOptions;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a ZigBee Transmit Request packet. Packet is built
 * using the parameters of the constructor.
 * 
 * A Transmit Request API frame causes the module to send data as an RF packet to the specified destination.
 * 
 * The 64-bit destination address should be set to 0x000000000000FFFF for a broadcast transmission (to all
 * devices). See {@link com.digi.xbee.api.models.XBee64BitAddress}. The coordinator can be addressed by either 
 * setting the 64-bit address to all 0x00s and the 16-bit address to 0xFFFE, OR by setting the 64-bit 
 * address to the coordinator's 64-bit address and the 16-bit address to 0x0000. For all other transmissions,
 * setting the 16-bit address to the correct 16-bit address can help improve performance when transmitting to
 * multiple destinations. If a 16-bit address is not known, this field should be set to 0xFFFE (unknown). See 
 * {@link com.digi.xbee.api.models.XBee16BitAddress}. The Transmit Status frame (0x8B) will indicate the discovered 
 * 16-bit address, if successful. See {@link com.digi.xbee.api.packet.common.TransmitStatusPacket}.
 * 
 * The broadcast radius can be set from 0 up to NH. If set to 0, the value of NH specifies the broadcast radius
 * (recommended). This parameter is only used for broadcast transmissions.
 * 
 * The maximum number of payload bytes can be read with the NP command.
 * 
 * Several transmit options can be set using the transmit options bitfield. See
 * {@link com.digi.xbee.api.models.XBeeTransmitOptions}.
 */
public class TransmitPacket extends XBeeAPIPacket {

	// Variables		
	private final XBee64BitAddress destAddress64;
	
	private final XBee16BitAddress destAddress16;
	
	private final int broadcastRadius;
	private final int transmitOptions;
	
	private byte[] rfData;
	
	private Logger logger;
	
	/**
	 * Class constructor. Instances a new object of type ZigBeeTransmitRequest with
	 * the given parameters.
	 * 
	 * @param frameID Frame ID.
	 * @param destAddress64 64-bit address of the destination device.
	 * @param destAddress16 16-bit address of the destination device.
	 * @param broadcastRadius maximum number of hops a broadcast transmission can occur.
	 * @param transmitOptions Bitfield of supported transmission options.
	 * @param rfData RF Data that is sent to the destination device.
	 * 
	 * @throws NullPointerException if {@code destAddress64 == null} or
	 *                              if {@code destAddress16 == null}.
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code broadcastRadius < 0} or
	 *                                  if {@code broadcastRadius > 255} or
	 *                                  if {@code transmitOptions < 0} or
	 *                                  if {@code transmitOptions > 255}.
	 * 
	 * @see XBeeTransmitOptions
	 */
	public TransmitPacket(int frameID, XBee64BitAddress destAddress64, XBee16BitAddress destAddress16, int broadcastRadius, int transmitOptions, byte[] rfData) {
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
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIData()
	 */
	public byte[] getAPIData() {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try {
			data.write(frameID);
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
	 * @see com.digi.xbee.packet.XBeeAPIPacket#hasAPIFrameID()
	 */
	public boolean needsAPIFrameID() {
		return true;
	}
	
	/**
	 * Retrieves the 64 bit destination address.
	 * 
	 * @return The 64 bit destination address.
	 */
	public XBee64BitAddress get64BitDestinationAddress() {
		return destAddress64;
	}
	
	/**
	 * Retrieves the 16 bit destination address.
	 * 
	 * @return The 16 bit destination address.
	 */
	public XBee16BitAddress get16BitDestinationAddress() {
		return destAddress16;
	}
	
	/**
	 * Retrieves the broadcast radius.
	 * 
	 * @return Broadcast radius.
	 */
	public int getBroadCastRadius() {
		return broadcastRadius;
	}
	
	/**
	 * Retrieves the transmit options bitfield.
	 * 
	 * @return Transmit options bitfield.
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
		this.rfData = rfData;
	}
	
	/**
	 * Retrieves the RF Data to send.
	 * 
	 * @return RF Data to send.
	 */
	public byte[] getRFData() {
		return rfData;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Frame ID", HexUtils.prettyHexString(HexUtils.integerToHexString(frameID, 1)) + " (" + frameID + ")");
		parameters.put("64-bit dest. address", HexUtils.prettyHexString(destAddress64.toString()));
		parameters.put("16-bit dest. address", HexUtils.prettyHexString(destAddress16.toString()));
		parameters.put("Broadcast radius", HexUtils.prettyHexString(HexUtils.integerToHexString(broadcastRadius, 1)) + " (" + broadcastRadius + ")");
		parameters.put("Options", HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1)));
		if (rfData != null)
			parameters.put("RF data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(rfData)));
		return parameters;
	}
}

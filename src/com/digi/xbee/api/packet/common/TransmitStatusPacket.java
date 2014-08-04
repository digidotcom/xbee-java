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
import com.digi.xbee.api.models.XBeeDiscoveryStatus;
import com.digi.xbee.api.models.XBeeTransmitStatus;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a Transmit Status Packet. Packet is built using the parameters of 
 * the constructor.
 * 
 * When a TX Request is completed, the module sends a TX Status message. This message will indicate if the
 * packet was transmitted successfully or if there was a failure. This packet is the response to standard
 * and explicit transmit requests. See {@link com.digi.xbee.api.packet.common.TransmitPacket} and
 * {@link com.digi.xbee.api.packet.zigbee.ExplicitAddressingZigBeePacket}.
 */
public class TransmitStatusPacket extends XBeeAPIPacket {
	
	// Variables
	private final XBee16BitAddress destAddress16;
	
	private final int tranmistRetryCount;
	private final XBeeTransmitStatus transmitStatus;
	private final XBeeDiscoveryStatus discoveryStatus;
	
	private Logger logger;
	
	/**
	 * Class constructor. Instances a new object of type TransmitStatusPacket with
	 * the given parameters.
	 * 
	 * @param frameID Frame ID.
	 * @param destAddress16 16-bit Network address the packet was delivered to.
	 * @param tranmistRetryCount The number of application transmission retries that took place.
	 * @param transmitStatus Transmit status.
	 * @param discoveryStatus Discovery status.
	 * 
	 * @throws NullPointerException if {@code destAddress16 == null} or
	 *                              if {@code transmitStatus == null} or
	 *                              if {@code discoveryStatus == null}.
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code tranmistRetryCount < 0} or
	 *                                  if {@code tranmistRetryCount > 255} .
	 * 
	 * @see XBeeTransmitStatus
	 * @see XBeeDiscoveryStatus
	 */
	public TransmitStatusPacket(int frameID, XBee16BitAddress destAddress16, int tranmistRetryCount, XBeeTransmitStatus transmitStatus, 
			XBeeDiscoveryStatus discoveryStatus) {
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
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIData()
	 */
	public byte[] getAPIData() {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try {
			data.write(frameID);
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
	 * @see com.digi.xbee.packet.XBeeAPIPacket#hasAPIFrameID()
	 */
	public boolean needsAPIFrameID() {
		return true;
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
	 * Retrieves the transmit retry count.
	 * 
	 * @return Transmit retry count.
	 */
	public int getTransmitRetryCount() {
		return tranmistRetryCount;
	}
	
	/**
	 * Retrieves the transmit status.
	 * 
	 * @return Transmit status.
	 * 
	 * @see XBeeTransmitStatus
	 */
	public XBeeTransmitStatus getTransmitStatus() {
		return transmitStatus;
	}
	
	/**
	 * Retrieves the discovery status.
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
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Frame ID", HexUtils.prettyHexString(HexUtils.integerToHexString(frameID, 1)) + " (" + frameID + ")");
		parameters.put("16-bit dest. address", HexUtils.prettyHexString(destAddress16.toString()));
		parameters.put("Tx. retry count", HexUtils.prettyHexString(HexUtils.integerToHexString(tranmistRetryCount, 1)) + " (" + tranmistRetryCount + ")");
		parameters.put("Delivery status", HexUtils.prettyHexString(HexUtils.integerToHexString(transmitStatus.getId(), 1)) + " (" + transmitStatus.getDescription() + ")");
		parameters.put("Discovery status", HexUtils.prettyHexString(HexUtils.integerToHexString(discoveryStatus.getId(), 1)) + " (" + discoveryStatus.getDescription() + ")");
		return parameters;
	}
}

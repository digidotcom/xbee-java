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
package com.digi.xbee.api.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a basic and unknown XBee packet where the payload is
 * set as a byte array without a defined structure.
 */
public class UnknownXBeePacket extends XBeeAPIPacket {
	
	// Variables
	protected byte[] rfData;
	
	private Logger logger;
	
	/**
	 * Class constructor. Instances an XBee packet with the given packet data.
	 * 
	 * @param apiIDValue The XBee API integer value of the packet.
	 * @param rfData The XBee RF Data.
	 */
	public UnknownXBeePacket(int apiIDValue, byte[] rfData) {
		super(apiIDValue);
		this.rfData = rfData;
		this.logger = LoggerFactory.getLogger(UnknownXBeePacket.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIData()
	 */
	public byte[] getAPIData() {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try {
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
		return false;
	}
	
	/**
	 * Sets the XBee RF Data.
	 * 
	 * @param rfData The new XBee RF Data.
	 */
	public void setRFData(byte[] rfData) {
		this.rfData = rfData;
	}
	
	/**
	 * Retrieves the XBee RF Data of the packet.
	 * 
	 * @return The RF Data.
	 */
	public byte[] getRFData() {
		return rfData;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	protected LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		if (rfData != null)
			parameters.put("RF Data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(rfData)));
		return parameters;
	}
}

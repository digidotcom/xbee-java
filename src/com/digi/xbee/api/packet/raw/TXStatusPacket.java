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
package com.digi.xbee.api.packet.raw;

import java.util.LinkedHashMap;

import com.digi.xbee.api.models.XBeeTransmitStatus;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

public class TXStatusPacket extends XBeeAPIPacket {
	
	// Variables
	private final XBeeTransmitStatus transmitStatus;
	
	/**
	 * Class constructor. Instances a new object of type TransmitStatusPacket with
	 * the given parameters.
	 * 
	 * @param frameID Packet frame ID.
	 * @param transmitStatus Transmit status. See {@link com.digi.xbee.api.models.XBeeTransmitStatus}.
	 * 
	 * @throws NullPointerException if {@code transmitStatus == null}.
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
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
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIData()
	 */
	public byte[] getAPIData() {
		byte[] data = new byte[2];
		data[0] = (byte)frameID;
		data[1] = (byte)transmitStatus.getId();
		return data;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#hasAPIFrameID()
	 */
	public boolean needsAPIFrameID() {
		return true;
	}
	
	/**
	 * Retrieves the transmit status.
	 * See {@link com.digi.xbee.api.models.XBeeTransmitStatus}.
	 * 
	 * @return Transmit status. See {@link com.digi.xbee.api.models.XBeeTransmitStatus}.
	 */
	public XBeeTransmitStatus getTransmitStatus() {
		return transmitStatus;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeePacket#getPacketParameters()
	 */
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Frame ID", HexUtils.prettyHexString(HexUtils.integerToHexString(frameID, 1)) + " (" + frameID + ")");
		parameters.put("Status", HexUtils.prettyHexString(HexUtils.integerToHexString(transmitStatus.getId(), 1) + " (" + transmitStatus.getDescription() + ")"));
		return parameters;
	}
}

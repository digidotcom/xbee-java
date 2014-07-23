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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

public class TX16Packet extends XBeeAPIPacket {

	// Variables
	private final int transmitOptions;
	
	private final XBee16BitAddress destAddress;
	
	private byte[] data;
	
	/**
	 * Class constructor. Instances a new object of type Transmit16Packet with
	 * the given parameters.
	 * 
	 * @param frameID Frame ID.
	 * @param destAddress 16-bit address of the destination device.
	 * @param transmitOptions Bitfield of supported transmission options. See {@link com.digi.xbee.api.models.api.XBeeTransmitOptions}.
	 * @param data RF Data that is sent to the destination device.
	 * 
	 * @throws NullPointerException if {@code destAddress == null}.
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code transmitOptions < 0} or
	 *                                  if {@code transmitOptions > 255}.
	 */
	public TX16Packet(int frameID, XBee16BitAddress destAddress, int transmitOptions, byte[] data) {
		super(APIFrameType.TX_16);
		
		if (destAddress == null)
			throw new NullPointerException("Destination address cannot be null.");
		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException("Frame ID must be between 0 and 255.");
		if (transmitOptions < 0 || transmitOptions > 255)
			throw new IllegalArgumentException("Transmit options must be between 0 and 255.");
		
		this.frameID = frameID;
		this.destAddress = destAddress;
		this.transmitOptions = transmitOptions;
		this.data = data;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIData()
	 */
	public byte[] getAPIData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(frameID);
			os.write(destAddress.getValue());
			os.write(transmitOptions);
			if (data != null)
				os.write(data);
		} catch (IOException e) {
			// TODO: Revisit this when logging feature is implemented.
			//e.printStackTrace();
		}
		return os.toByteArray();
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
	public XBee16BitAddress getDestinationAddress() {
		return destAddress;
	}
	
	/**
	 * Retrieves the transmit options bitfield.
	 * 
	 * @return Transmit options bitfield.
	 */
	public int getOptions() {
		return transmitOptions;
	}
	
	/**
	 * Sets the data to send.
	 * 
	 * @param data RF Data to send.
	 */
	public void setData(byte[] data) {
		this.data = data;
	}
	
	/**
	 * Retrieves the RF Data to send.
	 * 
	 * @return Data to send.
	 */
	public byte[] getData() {
		return data;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeePacket#getPacketParameters()
	 */
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Frame ID", HexUtils.prettyHexString(HexUtils.integerToHexString(frameID, 1)) + " (" + frameID + ")");
		parameters.put("16-bit dest. address", HexUtils.prettyHexString(destAddress.toString()));
		parameters.put("Options", HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1)));
		if (data != null)
			parameters.put("RF data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)));
		return parameters;
	}
}

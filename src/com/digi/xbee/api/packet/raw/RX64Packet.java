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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

public class RX64Packet extends XBeeAPIPacket {

	// Variables
	private final XBee64BitAddress sourceAddress;
	
	private final int rssi;
	private final int receiveOptions;
	
	private byte[] receivedData;
	
	private Logger logger;
	
	/**
	 * Class constructor. Instances a new object of type {@code RX64Packet} with
	 * the given parameters.
	 * 
	 * @param sourceAddress 64-bit address of the sender.
	 * @param rssi Received signal strength indicator.
	 * @param receiveOptions Bitfield indicating the receive options.
	 * @param receivedData Received RF data.
	 * 
	 * @throws IllegalArgumentException if {@code rssi < 0} or
	 *                                  if {@code rssi > 100} or
	 *                                  if {@code receiveOptions > 255} or
	 *                                  if {@code receiveOptions > 255}.
	 * @throws NullPointerException if {@code sourceAddress == null}.
	 * 
	 * @see XBeeReceiveOptions
	 * @see XBee64BitAddress
	 */
	public RX64Packet(XBee64BitAddress sourceAddress, int rssi, int receiveOptions, byte[] receivedData) {
		super(APIFrameType.RX_64);
		
		if (sourceAddress == null)
			throw new NullPointerException("Source address cannot be null.");
		if (rssi < 0 || rssi > 100)
			throw new IllegalArgumentException("RSSI value must be between 0 and 100.");
		if (receiveOptions < 0 || receiveOptions > 255)
			throw new IllegalArgumentException("Receive options value must be between 0 and 255.");
		
		this.sourceAddress = sourceAddress;
		this.rssi = rssi;
		this.receiveOptions = receiveOptions;
		this.receivedData = receivedData;
		this.logger = LoggerFactory.getLogger(RX64Packet.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIData()
	 */
	@Override
	public byte[] getAPIData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(sourceAddress.getValue());
			os.write(rssi);
			os.write(receiveOptions);
			if (receivedData != null)
				os.write(receivedData);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return os.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#needsAPIFrameID()
	 */
	@Override
	public boolean needsAPIFrameID() {
		return false;
	}
	
	/**
	 * Retrieves the 64 bit sender/source address. 
	 * 
	 * @return The 64 bit sender/source address.
	 * 
	 * @see XBee64BitAddress
	 */
	public XBee64BitAddress getSourceAddress() {
		return sourceAddress;
	}
	
	/**
	 * Retrieves the receive options bitfield.
	 * 
	 * @return Receive options bitfield.
	 * 
	 * @see XBeeReceiveOptions
	 */
	public int getReceiveOptions() {
		return receiveOptions;
	}
	
	/**
	 * Sets the received RF data.
	 * 
	 * @param receivedData Received RF data.
	 */
	public void setReceivedData (byte[] receivedData){
		this.receivedData = receivedData;
	}
	
	/**
	 * Retrieves the received RF data.
	 * 
	 * @return Received RF data.
	 */
	public byte[] getReceivedData (){
		return receivedData;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("64-bit source address", HexUtils.prettyHexString(sourceAddress.toString()));
		parameters.put("RSSI", HexUtils.prettyHexString(HexUtils.integerToHexString(rssi, 1)));
		parameters.put("Options", HexUtils.prettyHexString(HexUtils.integerToHexString(receiveOptions, 1)));
		if (receivedData != null)
			parameters.put("RF data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(receivedData)));
		return parameters;
	}
}

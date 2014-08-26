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

import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBeeReceiveOptions;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.HexUtils;

public class RX16IOPacket extends XBeeAPIPacket {

	// Variables
	private final XBee16BitAddress sourceAddress;
	
	private final IOSample ioSample;
	
	private final int rssi;
	private final int receiveOptions;
	
	private byte[] receivedData;
	
	private Logger logger;
	
	/**
	 * Class constructor. Instances a new object of type Rx16IOPacket with
	 * the given parameters.
	 * 
	 * @param sourceAddress 16-bit address of the sender.
	 * @param rssi Received signal strength indicator.
	 * @param receiveOptions Bitfield indicating the receive options.
	 * @param receivedData Received RF data.
	 * 
	 * @throws NullPointerException if {@code sourceAddress == null}.
	 * @throws IllegalArgumentException if {@code rssi < 0} or
	 *                                  if {@code rssi > 100} or
	 *                                  if {@code receiveOptions < 0} or
	 *                                  if {@code receiveOptions > 255}.
	 * 
	 * @see XBeeReceiveOptions
	 */
	public RX16IOPacket(XBee16BitAddress sourceAddress, int rssi, int receiveOptions, byte[] receivedData) {
		super(APIFrameType.RX_IO_16);
		
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
		if (receivedData != null)
			ioSample = new IOSample(receivedData);
		else
			ioSample = null;
		this.logger = LoggerFactory.getLogger(RX16IOPacket.class);
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
	 * Retrieves the 16-bit sender/source address. 
	 * 
	 * @return The 16-bit sender/source address.
	 * 
	 * @see XBee16BitAddress
	 */
	public XBee16BitAddress getSourceAddress() {
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
	 * Retrieves the IO sample corresponding to the data contained in the packet.
	 * 
	 * @return The IO sample of the packet, null if the packet has not any data or 
	 *         if the sample could not be generated correctly.
	 * 
	 * @see IOSample
	 */
	public IOSample getIOSample() {
		return ioSample;
	}
	
	/**
	 * Sets the received RF data.
	 * 
	 * @param receivedData Received RF data.
	 */
	public void setReceivedData (byte[] receivedData) {
		this.receivedData = receivedData;
	}
	
	/**
	 * Retrieves the received RF data.
	 * 
	 * @return Received RF data.
	 */
	public byte[] getReceivedData () {
		return receivedData;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("16-bit source address", HexUtils.prettyHexString(sourceAddress.toString()));
		parameters.put("RSSI", HexUtils.prettyHexString(HexUtils.integerToHexString(rssi, 1)));
		parameters.put("Options", HexUtils.prettyHexString(HexUtils.integerToHexString(receiveOptions, 1)));
		if (ioSample != null) {
			parameters.put("Number of samples", HexUtils.prettyHexString(HexUtils.integerToHexString(1, 1))); // There is always 1 sample.
			parameters.put("Digital channel mask", HexUtils.prettyHexString(HexUtils.integerToHexString(ioSample.getDigitalMask(), 2)));
			parameters.put("Analog channel mask", HexUtils.prettyHexString(HexUtils.integerToHexString(ioSample.getAnalogMask(), 2)));
			for (int i = 0; i < 16; i++) {
				if (ioSample.hasDigitalValue(IOLine.getDIO(i)))
					parameters.put(IOLine.getDIO(i).getName() + " digital value", ioSample.getDigitalValue(IOLine.getDIO(i)).getName());
			}
			for (int i = 0; i < 6; i++) {
				if (ioSample.hasAnalogValue(IOLine.getDIO(i)))
					parameters.put(IOLine.getDIO(i).getName() + " analog value", HexUtils.prettyHexString(HexUtils.integerToHexString(ioSample.getAnalogValue(IOLine.getDIO(i)), 2)));
			}
		} else if (receivedData != null)
			parameters.put("RF data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(receivedData)));
		return parameters;
	}
}

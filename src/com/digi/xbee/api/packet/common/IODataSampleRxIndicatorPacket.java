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

import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeReceiveOptions;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.raw.RX64Packet;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents an IO Data Sample RX Indicator packet. Packet is built using the parameters of 
 * the constructor.
 * 
 * When the module receives an IO sample frame from a remote device, it sends the sample out the UART using
 * this frame type (when AO=0). Only modules running API firmware will send IO samples out the UART.
 * 
 * Among received data, some options can also be received indicating transmission parameters.
 * 
 * @see XBeeReceiveOptions
 */
public class IODataSampleRxIndicatorPacket extends XBeeAPIPacket {

	// Variables.
	private final XBee64BitAddress sourceAddress64;
	private final XBee16BitAddress sourceAddress16;
	
	private final IOSample ioSample;
	
	private final int receiveOptions;
	
	private byte[] receivedData;
	
	private Logger logger;
	
	/**
	 * Class constructor. Instances a new object of type ZigBeeIODataSampleRxIndicatorPacket with
	 * the given parameters.
	 * 
	 * @param sourceAddress64 64-bit address of the sender.
	 * @param sourceAddress16 16-bit address of the sender.
	 * @param receiveOptions Receive options.
	 * @param receivedData Received RF data.
	 * 
	 * @throws NullPointerException if {@code sourceAddress64 == null} or 
	 *                              if {@code sourceAddress16 == null}.
	 * @throws IllegalArgumentException if {@code receiveOptions < 0} or
	 *                                  if {@code receiveOptions > 255}.
	 * 
	 * @see XBeeReceiveOptions
	 */
	public IODataSampleRxIndicatorPacket(XBee64BitAddress sourceAddress64, XBee16BitAddress sourceAddress16, int receiveOptions, byte[] receivedData) {
		super(APIFrameType.IO_DATA_SAMPLE_RX_INDICATOR);
		
		if (sourceAddress64 == null)
			throw new NullPointerException("64-bit source address cannot be null.");
		if (sourceAddress16 == null)
			throw new NullPointerException("16-bit source address cannot be null.");
		if (receiveOptions < 0 || receiveOptions > 255)
			throw new IllegalArgumentException("Receive options value must be between 0 and 255.");
		
		this.sourceAddress64 = sourceAddress64;
		this.sourceAddress16 = sourceAddress16;
		this.receiveOptions = receiveOptions;
		this.receivedData = receivedData;
		if (receivedData != null)
			ioSample = new IOSample(receivedData);
		else
			ioSample = null;
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
			os.write(sourceAddress64.getValue());
			os.write(sourceAddress16.getValue());
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
	 * Retrieves the 64-bit sender/source address. 
	 * 
	 * @return The 64-bit sender/source address.
	 * 
	 * @see XBee64BitAddress
	 */
	public XBee64BitAddress get64bitSourceAddress() {
		return sourceAddress64;
	}
	
	/**
	 * Retrieves the 16-bit sender/source address. 
	 * 
	 * @return 16-bit sender/source address.
	 * 
	 * @see XBee16BitAddress
	 */
	public XBee16BitAddress get16bitSourceAddress() {
		return sourceAddress16;
	}
	
	/**
	 * Retrieves the receive options. 
	 * 
	 * @return Receive options.
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
		parameters.put("64-bit source address", HexUtils.prettyHexString(sourceAddress64.toString()));
		parameters.put("16-bit source address", HexUtils.prettyHexString(sourceAddress16.toString()));
		parameters.put("Receive options", HexUtils.prettyHexString(HexUtils.integerToHexString(receiveOptions, 1)));
		if (ioSample != null) {
			parameters.put("Number of samples", HexUtils.prettyHexString(HexUtils.integerToHexString(1, 1))); // There is always 1 sample.
			parameters.put("Digital channel mask", HexUtils.prettyHexString(HexUtils.integerToHexString(ioSample.getDigitalMask(), 2)));
			parameters.put("Analog channel mask", HexUtils.prettyHexString(HexUtils.integerToHexString(ioSample.getAnalogMask(), 1)));
			for (int i = 0; i < 16; i++) {
				if (ioSample.hasDigitalValue(IOLine.getDIO(i)))
					parameters.put(IOLine.getDIO(i).getName() + " digital value", ioSample.getDigitalValue(IOLine.getDIO(i)).getName());
			}
			for (int i = 0; i < 6; i++) {
				if (ioSample.hasAnalogValue(IOLine.getDIO(i)))
					parameters.put(IOLine.getDIO(i).getName() + " analog value", HexUtils.prettyHexString(HexUtils.integerToHexString(ioSample.getAnalogValue(IOLine.getDIO(i)), 2)));
			}
			if (ioSample.hasPowerSupplyValue())
				try {
					parameters.put("Power supply value", HexUtils.prettyHexString(HexUtils.integerToHexString(ioSample.getPowerSupplyValue(), 2)));
				} catch (OperationNotSupportedException e) { }
		} else if (receivedData != null)
			parameters.put("RF data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(receivedData)));
		return parameters;
	}
}

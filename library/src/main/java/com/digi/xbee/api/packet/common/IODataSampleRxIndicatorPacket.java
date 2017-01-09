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

import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents an IO Data Sample RX Indicator packet. Packet is built 
 * using the parameters of the constructor or providing a valid API payload.
 * 
 * <p>When the module receives an IO sample frame from a remote device, it 
 * sends the sample out the UART using this frame type (when AO=0). Only modules
 * running API firmware will send IO samples out the UART.</p>
 * 
 * <p>Among received data, some options can also be received indicating 
 * transmission parameters.</p>
 * 
 * @see com.digi.xbee.api.models.XBeeReceiveOptions
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 */
public class IODataSampleRxIndicatorPacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 12; // 1 (Frame type) + 8 (32-bit address) + 2 (16-bit address) + 1 (receive options)
	
	// Variables.
	private final XBee64BitAddress sourceAddress64;
	private final XBee16BitAddress sourceAddress16;
	
	private IOSample ioSample;
	
	private final int receiveOptions;
	
	private byte[] rfData;
	
	private Logger logger;
	
	/**
	 * Creates a new {@code IODataSampleRxIndicatorPacket} object from the 
	 * given payload.
	 * 
	 * @param payload The API frame payload. It must start with the frame type 
	 *                corresponding to a IO Data Sample RX Indicator packet ({@code 0x92}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 * 
	 * @return Parsed IO Data Sample Rx Indicator packet.
	 * 
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.IO_DATA_SAMPLE_RX_INDICATOR.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH} or
	 *                                  if {@code receiveOptions < 0} or
	 *                                  if {@code receiveOptions > 255}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static IODataSampleRxIndicatorPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException("IO Data Sample RX Indicator packet payload cannot be null.");
		
		// 1 (Frame type) + 8 (32-bit address) + 2 (16-bit address) + 1 (receive options)
		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException("Incomplete IO Data Sample RX Indicator packet.");
		
		if ((payload[0] & 0xFF) != APIFrameType.IO_DATA_SAMPLE_RX_INDICATOR.getValue())
			throw new IllegalArgumentException("Payload is not a IO Data Sample RX Indicator packet.");
		
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
		
		return new IODataSampleRxIndicatorPacket(sourceAddress64, sourceAddress16, receiveOptions, data);
	}
	
	/**
	 * Class constructor. Instantiates a new 
	 * {@code IODataSampleRxIndicatorPacket} object with the given parameters.
	 * 
	 * @param sourceAddress64 64-bit address of the sender.
	 * @param sourceAddress16 16-bit address of the sender.
	 * @param receiveOptions Receive options.
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
	public IODataSampleRxIndicatorPacket(XBee64BitAddress sourceAddress64, XBee16BitAddress sourceAddress16, int receiveOptions, byte[] rfData) {
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
		this.rfData = rfData;
		if (rfData != null && rfData.length >= 5)
			ioSample = new IOSample(rfData);
		else
			ioSample = null;
		this.logger = LoggerFactory.getLogger(IODataSampleRxIndicatorPacket.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(sourceAddress64.getValue());
			os.write(sourceAddress16.getValue());
			os.write(receiveOptions);
			if (rfData != null)
				os.write(rfData);
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
	 * @return 16-bit sender/source address.
	 * 
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 */
	public XBee16BitAddress get16bitSourceAddress() {
		return sourceAddress16;
	}
	
	/**
	 * Returns the receive options. 
	 * 
	 * @return Receive options.
	 * 
	 * @see com.digi.xbee.api.models.XBeeReceiveOptions
	 */
	public int getReceiveOptions() {
		return receiveOptions;
	}
	
	/**
	 * Returns the IO sample corresponding to the data contained in the packet.
	 * 
	 * @return The IO sample of the packet, {@code null} if the packet has not 
	 *         any data or if the sample could not be generated correctly.
	 * 
	 * @see com.digi.xbee.api.io.IOSample
	 */
	public IOSample getIOSample() {
		return ioSample;
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
		
		// Modify the ioSample accordingly.
		if (rfData != null && rfData.length >= 5)
			ioSample = new IOSample(this.rfData);
		else
			ioSample = null;
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
		} else if (rfData != null)
			parameters.put("RF data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(rfData)));
		return parameters;
	}
}

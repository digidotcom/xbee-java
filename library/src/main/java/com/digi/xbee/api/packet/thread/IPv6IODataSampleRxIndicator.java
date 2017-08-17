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
package com.digi.xbee.api.packet.thread;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents an IPv6 IO Data Sample Rx Indicator packet. Packet is 
 * built using the parameters of  the constructor.
 * 
 * <p>When the module receives an IO sample frame from a remote device, it sends 
 * the sample out the UART or SPI using this frame type. Only modules running 
 * API mode will be able to receive IO samples.</p>
 * 
 * <h2>Digital Channel Mask</h2>
 * ------------------------------------------------------------------
 * <p>Indicates which digital IO lines have sampling enabled. Each
 * bit corresponds to one digital IO line on the module:</p>
 * 
 * <ul>
 * 		<li>bit 0 = AD0/DIO0</li>
 * 		<li>bit 1 = AD1/DIO1</li>
 * 		<li>bit 2 = AD2/DIO2</li>
 * 		<li>bit 3 = AD3/DIO3</li>
 * 		<li>bit 4 = DIO4</li>
 * 		<li>bit 5 = ASSOC/DIO5</li>
 * 		<li>bit 6 = RTS/DIO6</li>
 * 		<li>bit 7 = CTS/DIO7</li>
 * 		<li>bit 8 = N/A</li>
 * 		<li>bit 9 = N/A</li>
 * 		<li>bit 10 = RSSI/DIO10</li>
 * 		<li>bit 11 = PWM/DIO11</li>
 * 		<li>bit 12 = CD/DIO12</li>
 * 		<li>bit 13 = N/A</li>
 * 		<li>bit 14 = N/A</li>
 * 		<li>bit 15 = N/A</li>
 * </ul>
 * 
 * <p>Example: mask of 0x002F means DIO 0, 1, 2, 3 and 5 enabled.<br>
 * 0 0 0 0 0 0 0 0 0 0 1 0 1 1 1 1</p>
 * 
 * <h2>Analog Channel Mask</h2>
 * -----------------------------------------------------------------------
 * <p>Indicates which lines have analog inputs enabled for sampling. Each
 * bit in the analog channel mask corresponds to one analog input channel.</p>
 * 
 * <ul>
 * 		<li>bit 0 = AD0</li>
 * 		<li>bit 1 = AD1</li>
 * 		<li>bit 2 = AD2</li>
 * 		<li>bit 3 = AD3</li>
 * 		<li>bit 4 = N/A</li>
 * 		<li>bit 5 = N/A</li>
 * 		<li>bit 6 = N/A</li>
 * 		<li>bit 7 = Supply Voltage Value</li>
 * </ul>
 * 
 * <p>Example: mask of 0x83 means ADO 0, 1 and supply voltage enabled.<br>
 * 1 0 0 0 0 0 1 1</p>
 * 
 * @see com.digi.xbee.api.models.XBeeReceiveOptions
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 * 
 * @since 1.2.1
 */
public class IPv6IODataSampleRxIndicator extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 17; // 1 (Frame type) + 16 (IPv6 source address)
	
	private static final String ERROR_PAYLOAD_NULL = "IPv6 IO Data Sample RX Indicator packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete IPv6 IO Data Sample RX Indicator packet.";
	private static final String ERROR_NOT_IORXIPV6 = "Payload is not an IPv6 IO Data Sample RX Indicator packet.";
	private static final String ERROR_SOURCE_ADDR_NULL = "Source address cannot be null.";

	private static final String OPERATION_EXCEPTION = "Operation not supported in this module.";

	// Variables.
	private final Inet6Address sourceAddress;

	private IOSample ioSample;

	private byte[] rfData;

	private Logger logger;

	/**
	 * Creates a new {@code IPv6IODataSampleRxIndicator} object from the 
	 * given payload.
	 * 
	 * @param payload The API frame payload. It must start with the frame type 
	 *                corresponding to a IPv6 IO Data Sample RX Indicator 
	 *                packet ({@code 0xA7}). The byte array must be in 
	 *                {@code OperatingMode.API} mode.
	 * 
	 * @return Parsed IPv6 IO Data Sample Rx Indicator packet.
	 * 
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.IPV6_IO_DATA_SAMPLE_RX_INDICATOR.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static IPv6IODataSampleRxIndicator createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		// 1 (Frame type) + 8 (32-bit address) + 2 (16-bit address) + 1 (receive options)
		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);
		
		if ((payload[0] & 0xFF) != APIFrameType.IPV6_IO_DATA_SAMPLE_RX_INDICATOR.getValue())
			throw new IllegalArgumentException(ERROR_NOT_IORXIPV6);

		// payload[0] is the frame type.
		int index = 1;

		// 16 bytes of IPv6 source address.
		Inet6Address sourceAddress;
		try {
			sourceAddress = (Inet6Address) Inet6Address.getByAddress(Arrays.copyOfRange(payload, index, index + 16));
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException(e);
		}
		index = index + 16;

		// Get data.
		byte[] data = null;
		if (index < payload.length)
			data = Arrays.copyOfRange(payload, index, payload.length);

		return new IPv6IODataSampleRxIndicator(sourceAddress, data);
	}

	/**
	 * Class constructor. Instantiates a new 
	 * {@code IPv6IODataSampleRxIndicator} object with the given parameters.
	 * 
	 * @param sourceAddress IPv6 address of the sender.
	 * @param rfData Received RF data.
	 * 
	 * @throws NullPointerException if {@code sourceAddress == null}.
	 * 
	 * @see java.net.Inet6Address
	 */
	public IPv6IODataSampleRxIndicator(Inet6Address sourceAddress, byte[] rfData) {
		super(APIFrameType.IPV6_IO_DATA_SAMPLE_RX_INDICATOR);
		
		if (sourceAddress == null)
			throw new NullPointerException(ERROR_SOURCE_ADDR_NULL);

		this.sourceAddress = sourceAddress;
		this.rfData = rfData;
		if (rfData != null && rfData.length >= 5)
			ioSample = new IOSample(rfData);
		else
			ioSample = null;
		this.logger = LoggerFactory.getLogger(IPv6IODataSampleRxIndicator.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(sourceAddress.getAddress());
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

	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public boolean isBroadcast() {
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}

	/**
	 * Retrieves the source IPv6 address.
	 *
	 * @return The source IPv6 address.
	 *
	 * @see java.net.Inet6Address
	 */
	public Inet6Address getSourceAddress() {
		return sourceAddress;
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
		parameters.put("IPv6 source address", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(sourceAddress.getAddress())) +
				" (" + sourceAddress.getHostAddress() + ")");
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
					parameters.put(IOLine.getDIO(i).getName() +
							" analog value", HexUtils.prettyHexString(HexUtils.integerToHexString(ioSample.getAnalogValue(IOLine.getDIO(i)), 2)) +
							" (" + ioSample.getAnalogValue(IOLine.getDIO(i)) + ")");
			}
			if (ioSample.hasPowerSupplyValue())
				try {
					parameters.put("Power supply value", HexUtils.prettyHexString(HexUtils.integerToHexString(ioSample.getPowerSupplyValue(), 2)));
				} catch (XBeeException e) { }
		} else if (rfData != null)
			parameters.put("RF data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(rfData)));
		return parameters;
	}
}

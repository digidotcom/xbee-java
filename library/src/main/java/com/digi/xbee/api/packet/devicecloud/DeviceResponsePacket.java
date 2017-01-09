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
package com.digi.xbee.api.packet.devicecloud;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a Device Response packet. Packet is built using the
 * parameters of the constructor or providing a valid API payload.
 *
 * <p>This frame type is sent to the serial port by the host in response to the
 * {@link DeviceRequestPacket}. It should be sent within five seconds to avoid
 * a timeout error.</p>
 *
 * @see DeviceRequestPacket
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 * 
 * @since 1.2.0
 */
public class DeviceResponsePacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 4; /* 1 (Frame type) + 1 (Frame ID) + 1 (Device request ID) + 1 (Reserved) */

	private static final String ERROR_PAYLOAD_NULL = "Device Response packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete Device Response packet.";
	private static final String ERROR_NOT_VALID = "Payload is not a Device Response packet.";
	private static final String ERROR_FRAME_ID_ILLEGAL = "Frame ID must be between 0 and 255.";
	private static final String ERROR_REQUEST_ID_ILLEGAL = "Device request ID must be between 0 and 255.";

	// Variables.
	private int requestID;

	private byte[] responseData;

	private Logger logger;

	/**
	 * Creates a new {@code DeviceResponsePacket} object from the given payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a Device Response packet ({@code 0x2A}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 *
	 * @return Parsed Device Response packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.DEVICE_RESPONSE.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static DeviceResponsePacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.DEVICE_RESPONSE.getValue())
			throw new IllegalArgumentException(ERROR_NOT_VALID);

		// payload[0] is the frame type.
		int index = 1;

		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;

		// Device Request ID byte.
		int requestID = payload[index] & 0xFF;
		index = index + 1;

		// Reserved byte.
		index = index + 1;

		// Get data.
		byte[] data = null;
		if (index < payload.length)
			data = Arrays.copyOfRange(payload, index, payload.length);

		return new DeviceResponsePacket(frameID, requestID, data);
	}

	/**
	 * Class constructor. Instantiates a new {@code DeviceResponsePacket} object
	 * with the given parameters.
	 *
	 * @param frameID Frame ID.
	 * @param requestID Device Request ID. This number should match the device
	 *                  request ID in the device request. Otherwise, an error
	 *                  will occur. (0 has no special meaning in this case.)
	 * @param responseData Data of the response.
	 *
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code requestID < 0} or
	 *                                  if {@code requestID > 255}.
	 */
	public DeviceResponsePacket(int frameID, int requestID, byte[] responseData) {
		super(APIFrameType.DEVICE_RESPONSE);

		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException(ERROR_FRAME_ID_ILLEGAL);
		if (requestID < 0 || requestID > 255)
			throw new IllegalArgumentException(ERROR_REQUEST_ID_ILLEGAL);

		this.frameID = frameID;
		this.requestID = requestID;
		this.responseData = responseData;
		this.logger = LoggerFactory.getLogger(DeviceResponsePacket.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(requestID);
			os.write(0x00); // Reserved.
			if (responseData != null)
				os.write(responseData);
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
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#isBroadcast()
	 */
	@Override
	public boolean isBroadcast() {
		return false;
	}

	/**
	 * Sets ID of the device response.
	 *
	 * @param requestID ID of the device response.
	 *
	 * @throws IllegalArgumentException if {@code requestID < 0} or
	 *                                  if {@code requestID > 255}.
	 *
	 * @see #getRequestID()
	 */
	public void setRequestID(int requestID) {
		if (requestID < 0 || requestID > 255)
			throw new IllegalArgumentException(ERROR_REQUEST_ID_ILLEGAL);

		this.requestID = requestID;
	}

	/**
	 * Retrieves the ID of the device response.
	 *
	 * @return The ID of the device response.
	 *
	 * @see #setRequestID(int)
	 */
	public int getRequestID() {
		return requestID;
	}

	/**
	 * Sets the data of the device response.
	 *
	 * @param responseData Data of the device response.
	 *
	 * @see #getResponseData()
	 */
	public void setResponseData(byte[] responseData) {
		this.responseData = responseData;
	}

	/**
	 * Retrieves the data of the device response.
	 *
	 * @return The data of the device response.
	 *
	 * @see #setResponseData(byte[])
	 */
	public byte[] getResponseData() {
		return responseData;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Device Request ID", HexUtils.prettyHexString(HexUtils.integerToHexString(requestID, 1)) + " (" + requestID + ")");
		parameters.put("Reserved", "00");
		if (responseData != null)
			parameters.put("Data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(responseData)));
		return parameters;
	}
}

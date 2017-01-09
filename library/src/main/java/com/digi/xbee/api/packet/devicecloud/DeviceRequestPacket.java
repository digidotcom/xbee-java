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
 * This class represents a Device Request packet. Packet is built
 * using the parameters of the constructor or providing a valid API payload.
 *
 * <p>This frame type is sent out the serial port when the XBee module receives
 * a valid device request from Device Cloud.</p>
 *
 * @see DeviceResponsePacket
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 * 
 * @since 1.2.0
 */
public class DeviceRequestPacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 5; /* 1 (Frame type) + 1 (Device request ID) + 1 (Transport) +
																1 (Flags) + 1 (Target length) */

	private static final String ERROR_PAYLOAD_NULL = "Device Request packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete Device Request packet.";
	private static final String ERROR_NOT_VALID = "Payload is not a Device Request packet.";
	private static final String ERROR_REQUEST_ID_ILLEGAL = "Device request ID must be between 0 and 255.";
	private static final String ERROR_TARGET_ILLEGAL = "Target lenght cannot exceed 255 bytes.";

	// Variables.
	private int requestID;
	private final int transport = 0x00; // Reserved.
	private final int flags = 0x00; // Reserved.

	private String target;

	private byte[] requestData;

	private Logger logger;

	/**
	 * Creates a new {@code DeviceRequestPacket} object from the given payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a Device Request packet ({@code 0xB9}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 *
	 * @return Parsed Device Request packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.DEVICE_REQUEST.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static DeviceRequestPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.DEVICE_REQUEST.getValue())
			throw new IllegalArgumentException(ERROR_NOT_VALID);

		// payload[0] is the frame type.
		int index = 1;

		// Device Request ID byte.
		int requestID = payload[index] & 0xFF;
		index = index + 1;

		// Transport byte, reserved.
		index = index + 1;

		// Flags byte, reserved.
		index = index + 1;

		// Target length byte.
		int targetLength = payload[index] & 0xFF;
		index = index + 1;

		// Target string.
		String target = null;
		if (targetLength > 0) {
			target = new String(Arrays.copyOfRange(payload, index, index + targetLength));
			index = index + targetLength;
		}

		// Get data.
		byte[] data = null;
		if (index < payload.length)
			data = Arrays.copyOfRange(payload, index, payload.length);

		return new DeviceRequestPacket(requestID, target, data);
	}

	/**
	 * Class constructor. Instantiates a new {@code DeviceRequestPacket} object
	 * with the given parameters.
	 *
	 * @param requestID Identifies the device request. (0 has no special meaning.)
	 * @param target Device request target.
	 * @param requestData Data of the request.
	 *
	 * @throws IllegalArgumentException if {@code requestID < 0} or
	 *                                  if {@code requestID > 255} or
	 *                                  if {@code target.length() > 255}.
	 */
	public DeviceRequestPacket(int requestID, String target, byte[] requestData) {
		super(APIFrameType.DEVICE_REQUEST);

		if (requestID < 0 || requestID > 255)
			throw new IllegalArgumentException(ERROR_REQUEST_ID_ILLEGAL);
		if (target != null && target.length() > 255)
			throw new IllegalArgumentException(ERROR_TARGET_ILLEGAL);

		this.requestID = requestID;
		this.target = target;
		this.requestData = requestData;
		this.logger = LoggerFactory.getLogger(DeviceRequestPacket.class);
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
			os.write(transport);
			os.write(flags);
			if (target != null) {
				os.write(target.length());
				os.write(target.getBytes());
			} else
				os.write(0x00);
			if (requestData != null)
				os.write(requestData);
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
		return false;
	}

	/**
	 * Sets ID of the device request.
	 *
	 * @param requestID ID of the device request.
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
	 * Retrieves the ID of the device request.
	 *
	 * @return The ID of the device request.
	 *
	 * @see #setRequestID(int)
	 */
	public int getRequestID() {
		return requestID;
	}

	/**
	 * Retrieves the transport.
	 *
	 * @return The transport.
	 */
	public int getTransport() {
		return transport;
	}

	/**
	 * Retrieves the flags.
	 *
	 * @return The flags.
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * Sets the device request target.
	 *
	 * @param target The device request target.
	 *
	 * @throws IllegalArgumentException if {@code target.length() > 255}.
	 *
	 * @see #getRequestTarget()
	 */
	public void setRequestTarget(String target) {
		if (target != null && target.length() > 255)
			throw new IllegalArgumentException(ERROR_TARGET_ILLEGAL);

		this.target = target;
	}

	/**
	 * Retrieves the device request target.
	 *
	 * @return The device request target.
	 *
	 * @see #setRequestTarget(String)
	 */
	public String getRequestTarget() {
		return target;
	}

	/**
	 * Sets the data of the device request.
	 *
	 * @param requestData Data of the device request.
	 *
	 * @see #getRequestData()
	 */
	public void setRequestData(byte[] requestData) {
		this.requestData = requestData;
	}

	/**
	 * Retrieves the data of the device request.
	 *
	 * @return The data of the device request.
	 *
	 * @see #setRequestData(byte[])
	 */
	public byte[] getRequestData() {
		return requestData;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Device Request ID", HexUtils.prettyHexString(HexUtils.integerToHexString(requestID, 1)) + " (" + requestID + ")");
		parameters.put("Transport", HexUtils.prettyHexString(HexUtils.integerToHexString(transport, 1)));
		parameters.put("Flags", HexUtils.prettyHexString(HexUtils.integerToHexString(flags, 1)));
		if (target != null) {
			parameters.put("Target length", HexUtils.prettyHexString(HexUtils.integerToHexString(target.length(), 1)) + " (" + target.length() + ")");
			parameters.put("Target", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(target.getBytes())) + " (" + target + ")");
		} else
			parameters.put("Target length", HexUtils.prettyHexString(HexUtils.integerToHexString(0, 1)) + " (0)");
		if (requestData != null)
			parameters.put("Data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(requestData)));
		return parameters;
	}
}

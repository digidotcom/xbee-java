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
import java.util.LinkedHashMap;

import com.digi.xbee.api.models.DeviceCloudStatus;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a Send Data Response packet. Packet is built
 * using the parameters of the constructor or providing a valid API payload.
 *
 * <p>This frame type is sent out the serial port in response to the
 * {@link SendDataRequestPacket}, providing its frame ID is non-zero.</p>
 *
 * @see SendDataRequestPacket
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 * 
 * @since 1.2.0
 */
public class SendDataResponsePacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 3; /* 1 (Frame type) + 1 (Frame ID) + 1 (Status) */

	private static final String ERROR_PAYLOAD_NULL = "Send Data Response packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete Send Data Response packet.";
	private static final String ERROR_NOT_VALID = "Payload is not a Send Data Response packet.";
	private static final String ERROR_FRAME_ID_ILLEGAL = "Frame ID must be between 0 and 255.";
	private static final String ERROR_STATUS_NULL = "Status cannot be null.";

	// Variables.
	private DeviceCloudStatus status;

	/**
	 * Creates a new {@code SendDataResponsePacket} object from the given
	 * payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a Send Data Response packet
	 *                ({@code 0xB8}). The byte array must be in
	 *                {@code OperatingMode.API} mode.
	 *
	 * @return Parsed Send Data Response packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.SEND_DATA_RESPONSE.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static SendDataResponsePacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.SEND_DATA_RESPONSE.getValue())
			throw new IllegalArgumentException(ERROR_NOT_VALID);

		// payload[0] is the frame type.
		int index = 1;

		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;

		// Status byte.
		DeviceCloudStatus status = DeviceCloudStatus.get(payload[index] & 0xFF);

		return new SendDataResponsePacket(frameID, status);
	}

	/**
	 * Class constructor. Instantiates a new {@code SendDataResponsePacket}
	 * object with the given parameters.
	 *
	 * @param frameID Frame ID.
	 * @param status Status.
	 *
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 * @throws NullPointerException if {@code status == null}.
	 *
	 * @see DeviceCloudStatus
	 */
	public SendDataResponsePacket(int frameID, DeviceCloudStatus status) {
		super(APIFrameType.SEND_DATA_RESPONSE);

		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException(ERROR_FRAME_ID_ILLEGAL);
		if (status == null)
			throw new NullPointerException(ERROR_STATUS_NULL);

		this.frameID = frameID;
		this.status = status;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		os.write(status.getID());
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
	 * Sets the file upload status.
	 *
	 * @param status File upload status.
	 *
	 * @throws NullPointerException if {@code status == null}.
	 *
	 * @see #getStatus()
	 * @see DeviceCloudStatus
	 */
	public void setStatus(DeviceCloudStatus status) {
		if (status == null)
			throw new NullPointerException(ERROR_STATUS_NULL);

		this.status = status;
	}

	/**
	 * Retrieves the file upload status.
	 *
	 * @return The file upload status.
	 *
	 * @see #setStatus(DeviceCloudStatus)
	 * @see DeviceCloudStatus
	 */
	public DeviceCloudStatus getStatus() {
		return status;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Status", HexUtils.prettyHexString(HexUtils.integerToHexString(status.getID(), 1)) + " (" + status.getName() + ")");
		return parameters;
	}
}

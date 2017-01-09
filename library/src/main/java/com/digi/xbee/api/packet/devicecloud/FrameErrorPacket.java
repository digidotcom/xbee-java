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

import com.digi.xbee.api.models.FrameError;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a Frame Error packet. Packet is built using the
 * parameters of the constructor or providing a valid API payload.
 *
 * <p>This frame type is sent to the serial port for any type of frame error.
 * </p>
 *
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 * 
 * @since 1.2.0
 */
public class FrameErrorPacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 2; /* 1 (Frame type) + 1 (Frame error status) */

	private static final String ERROR_PAYLOAD_NULL = "Frame Error packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete Frame Error packet.";
	private static final String ERROR_NOT_VALID = "Payload is not a Frame Error packet.";
	private static final String ERROR_STATUS_NULL = "Frame error cannot be null.";

	// Variables.
	private FrameError error;

	/**
	 * Creates a new {@code FrameErrorPacket} object from the given payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a Frame Error packet ({@code 0xFE}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 *
	 * @return Parsed Frame Error packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.FRAME_ERROR.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static FrameErrorPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.FRAME_ERROR.getValue())
			throw new IllegalArgumentException(ERROR_NOT_VALID);

		// payload[0] is the frame type.
		int index = 1;

		// Status byte.
		FrameError error = FrameError.get(payload[index] & 0xFF);

		return new FrameErrorPacket(error);
	}

	/**
	 * Class constructor. Instantiates a new {@code FrameErrorPacket} object
	 * with the given parameters.
	 *
	 * @param error Frame error.
	 *
	 * @throws IllegalArgumentException if {@code error == null}.
	 *
	 * @see FrameError
	 */
	public FrameErrorPacket(FrameError error) {
		super(APIFrameType.FRAME_ERROR);

		if (error == null)
			throw new NullPointerException(ERROR_STATUS_NULL);

		this.error = error;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		os.write(error.getID());
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
	 * Sets the frame error.
	 *
	 * @param error The frame error.
	 *
	 * @throws NullPointerException if {@code error == null}.
	 *
	 * @see #getError()
	 * @see FrameError
	 */
	public void setError(FrameError error) {
		if (error == null)
			throw new NullPointerException(ERROR_STATUS_NULL);

		this.error = error;
	}

	/**
	 * Retrieves the frame error.
	 *
	 * @return The frame error.
	 *
	 * @see #setError(FrameError)
	 * @see FrameError
	 */
	public FrameError getError() {
		return error;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Error", HexUtils.prettyHexString(HexUtils.integerToHexString(error.getID(), 1)) + " (" + error.getName() + ")");
		return parameters;
	}
}

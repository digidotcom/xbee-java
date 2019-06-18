/*
 * Copyright 2019, Digi International Inc.
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
package com.digi.xbee.api.packet.relay;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import com.digi.xbee.api.models.XBeeLocalInterface;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a User Data Relay packet. Packet is built using the
 * parameters of the constructor.
 *
 * The User Data Relay packet allows for data to come in on an interface with
 * a designation of the target interface for the data to be output on.
 *
 * The destination interface must be one of the interfaces found in the
 * corresponding enumerator. See {@link XBeeLocalInterface}.
 *
 * @see UserDataRelayOutputPacket
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 *
 * @since 1.3.0
 */
public class UserDataRelayPacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 3; /* 1 (Frame type) + 1 (frame ID) + 1 (dest interface) */

	private static final String ERROR_PAYLOAD_NULL = "User Data Relay packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete User Data Relay packet.";
	private static final String ERROR_NOT_USER_DATA_RELAY = "Payload is not a User Data Relay packet.";
	private static final String ERROR_INTERFACE_NULL = "Destination interface cannot be null.";
	private static final String ERROR_FRAME_ID_ILLEGAL = "Frame ID must be between 0 and 255.";

	// Variables.
	private XBeeLocalInterface localInterface;

	private byte[] data;

	private Logger logger;

	/**
	 * Creates a new {@code UserDataRelayPacket} object from the given payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a User Data Relay packet ({@code 0x2D}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 *
	 * @return Parsed User Data Relay packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.USER_DATA_RELAY.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static UserDataRelayPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.USER_DATA_RELAY.getValue())
			throw new IllegalArgumentException(ERROR_NOT_USER_DATA_RELAY);

		// payload[0] is the frame type.
		int index = 1;

		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;

		// Destination interface byte.
		XBeeLocalInterface destInterface = XBeeLocalInterface.get(payload[index] & 0xFF);
		index = index + 1;

		// Get data.
		byte[] data = null;
		if (index < payload.length)
			data = Arrays.copyOfRange(payload, index, payload.length);

		return new UserDataRelayPacket(frameID, destInterface, data);
	}

	/**
	 * Class constructor. Instantiates a new {@code UserDataRelayPacket}
	 * object with the given parameters.
	 *
	 * @param frameID Frame ID.
	 * @param localInterface The destination {@code XBeeLocalInterface}.
	 * @param data RF Data that is sent to the destination interface.
	 *
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 * @throws NullPointerException if {@code localInterface == null}.
	 */
	public UserDataRelayPacket(int frameID, XBeeLocalInterface localInterface, byte[] data) {
		super(APIFrameType.USER_DATA_RELAY);

		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException(ERROR_FRAME_ID_ILLEGAL);
		if (localInterface == null)
			throw new NullPointerException(ERROR_INTERFACE_NULL);

		this.frameID = frameID;
		this.localInterface = localInterface;
		this.data = data;
		this.logger = LoggerFactory.getLogger(UserDataRelayPacket.class);
	}

	@Override
	public byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(localInterface.getID());
			if (data != null)
				os.write(data);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return os.toByteArray();
	}

	@Override
	public boolean needsAPIFrameID() {
		return true;
	}

	@Override
	public boolean isBroadcast() {
		return false;
	}
	
	/**
	 * Sets the destination XBee local interface.
	 *
	 * @param localInterface The new destination interface.
	 *
	 * @throws NullPointerException if {@code localInterface == null}.
	 *
	 * @see #getDestinationInterface()
	 * @see XBeeLocalInterface
	 */
	public void setDestinationInterface(XBeeLocalInterface localInterface) {
		if (localInterface == null)
			throw new NullPointerException(ERROR_INTERFACE_NULL);
		this.localInterface = localInterface;
	}
	
	/**
	 * Retrieves the the destination XBee local interface.
	 *
	 * @return The the destination interface.
	 *
	 * @see #setDestinationInterface(XBeeLocalInterface)
	 * @see XBeeLocalInterface
	 */
	public XBeeLocalInterface getDestinationInterface() {
		return localInterface;
	}
	
	/**
	 * Sets the data to send.
	 *
	 * @param data Data to send.
	 *
	 * @see #getData()
	 */
	public void setData(byte[] data) {
		this.data = data;
	}
	
	/**
	 * Retrieves the data to send.
	 *
	 * @return Data to send.
	 *
	 * @see #setData(byte[])
	 */
	public byte[] getData() {
		return data;
	}

	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
		parameters.put("Destination interface", String.format("%s (%s)", HexUtils.prettyHexString(HexUtils.integerToHexString(
				localInterface.getID(), 1)), localInterface.getDescription()));
		if (data != null)
			parameters.put("Data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)));
		return parameters;
	}
}

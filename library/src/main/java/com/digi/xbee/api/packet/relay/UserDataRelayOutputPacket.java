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
 * This class represents a User Data Relay Output packet. Packet is built
 * using the parameters of the constructor.
 *
 * The User Data Relay Output packet can be received from any XBee local
 * interface.
 *
 * The source interface must be one of the interfaces found in the
 * corresponding enumerator. See {@link XBeeLocalInterface}.
 *
 * @see UserDataRelayPacket
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 *
 * @since 1.3.0
 */
public class UserDataRelayOutputPacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 2; /* 1 (Frame type) + 1 (src interface) */

	private static final String ERROR_PAYLOAD_NULL = "User Data Relay Output packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete User Data Relay Output packet.";
	private static final String ERROR_NOT_USER_DATA_RELAY = "Payload is not a User Data Relay Output packet.";
	private static final String ERROR_INTERFACE_NULL = "Source interface cannot be null.";

	// Variables.
	private XBeeLocalInterface localInterface;

	private byte[] data;

	private Logger logger;

	/**
	 * Creates a new {@code UserDataRelayOutputPacket} object from the given
	 * payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a User Data Relay packet ({@code 0xAD}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 *
	 * @return Parsed User Data Relay Output packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.USER_DATA_RELAY_OUTPUT.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static UserDataRelayOutputPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.USER_DATA_RELAY_OUTPUT.getValue())
			throw new IllegalArgumentException(ERROR_NOT_USER_DATA_RELAY);

		// payload[0] is the frame type.
		int index = 1;

		// Source interface byte.
		XBeeLocalInterface srcInterface = XBeeLocalInterface.get(payload[index] & 0xFF);
		index = index + 1;

		// Get data.
		byte[] data = null;
		if (index < payload.length)
			data = Arrays.copyOfRange(payload, index, payload.length);

		return new UserDataRelayOutputPacket(srcInterface, data);
	}

	/**
	 * Class constructor. Instantiates a new {@code UserDataRelayOutputPacket}
	 * object with the given parameters.
	 *
	 * @param localInterface The source {@code XBeeLocalInterface}.
	 * @param data RF Data that is received from the source interface.
	 *
	 * @throws NullPointerException if {@code localInterface == null}.
	 */
	public UserDataRelayOutputPacket(XBeeLocalInterface localInterface, byte[] data) {
		super(APIFrameType.USER_DATA_RELAY_OUTPUT);

		if (localInterface == null)
			throw new NullPointerException(ERROR_INTERFACE_NULL);

		this.localInterface = localInterface;
		this.data = data;
		this.logger = LoggerFactory.getLogger(UserDataRelayOutputPacket.class);
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
		return false;
	}

	@Override
	public boolean isBroadcast() {
		return false;
	}

	/**
	 * Sets the source XBee local interface.
	 *
	 * @param localInterface The new source interface.
	 *
	 * @throws NullPointerException if {@code localInterface == null}.
	 *
	 * @see #getSourceInterface()
	 * @see XBeeLocalInterface
	 */
	public void setSourceInterface(XBeeLocalInterface localInterface) {
		if (localInterface == null)
			throw new NullPointerException(ERROR_INTERFACE_NULL);
		this.localInterface = localInterface;
	}

	/**
	 * Retrieves the the source XBee local interface.
	 *
	 * @return The the source interface.
	 *
	 * @see #setSourceInterface(XBeeLocalInterface)
	 * @see XBeeLocalInterface
	 */
	public XBeeLocalInterface getSourceInterface() {
		return localInterface;
	}

	/**
	 * Sets the received data.
	 *
	 * @param data Received data.
	 *
	 * @see #getData()
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	/**
	 * Retrieves the received data.
	 *
	 * @return Received data.
	 *
	 * @see #setData(byte[])
	 */
	public byte[] getData() {
		return data;
	}

	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
		parameters.put("Source interface", String.format("%s (%s)", HexUtils.prettyHexString(HexUtils.integerToHexString(
				localInterface.getID(), 1)), localInterface.getDescription()));
		if (data != null)
			parameters.put("Data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)));
		return parameters;
	}
}

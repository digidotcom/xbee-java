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
package com.digi.xbee.api.packet;

import java.util.HashMap;

import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This enumeration lists all the available frame types used in any XBee 
 * protocol.
 */
public enum APIFrameType {

	// Enumeration elements.
	UNKNOWN (-1, "Unknown packet"),
	TX_64 (0x00, "TX (Transmit) Request 64-bit address"),
	TX_16 (0x01, "TX (Transmit) Request 16-bit address"),
	REMOTE_AT_COMMAND_REQUEST_WIFI (0x07, "Remote AT Command Request (Wi-Fi)"),
	AT_COMMAND (0x08, "AT Command"),
	AT_COMMAND_QUEUE (0x09, "AT Command Queue"),
	TRANSMIT_REQUEST (0x10, "Transmit Request"),
	EXPLICIT_ADDRESSING_COMMAND_FRAME (0x11, "Explicit Addressing Command Frame"),
	REMOTE_AT_COMMAND_REQUEST (0x17, "Remote AT Command Request"),
	TX_SMS (0x1F, "TX SMS"),
	TX_IPV4 (0x20, "TX IPv4"),
	SEND_DATA_REQUEST (0x28, "Send Data Request"),
	DEVICE_RESPONSE (0x2A, "Device Response"),
	RX_64 (0x80, "RX (Receive) Packet 64-bit Address"),
	RX_16 (0x81, "RX (Receive) Packet 16-bit Address"),
	RX_IO_64 (0x82, "IO Data Sample RX 64-bit Address Indicator"),
	RX_IO_16 (0x83, "IO Data Sample RX 16-bit Address Indicator"),
	REMOTE_AT_COMMAND_RESPONSE_WIFI (0x87, "Remote AT Command Response (Wi-Fi)"),
	AT_COMMAND_RESPONSE (0x88, "AT Command Response"),
	TX_STATUS (0x89, "TX (Transmit) Status"),
	MODEM_STATUS (0x8A, "Modem Status"),
	TRANSMIT_STATUS (0x8B, "Transmit Status"),
	IO_DATA_SAMPLE_RX_INDICATOR_WIFI (0x8F, "IO Data Sample RX Indicator (Wi-Fi)"),
	RECEIVE_PACKET (0x90, "Receive Packet"),
	EXPLICIT_RX_INDICATOR (0x91, "Explicit RX Indicator"),
	IO_DATA_SAMPLE_RX_INDICATOR (0x92, "IO Data Sample RX Indicator"),
	REMOTE_AT_COMMAND_RESPONSE (0x97, "Remote Command Response"),
	RX_SMS (0x9F, "RX SMS"),
	RX_IPV4 (0xB0, "RX IPv4"),
	SEND_DATA_RESPONSE (0xB8, "Send Data Response"),
	DEVICE_REQUEST (0xB9, "Device Request"),
	DEVICE_RESPONSE_STATUS (0xBA, "Device Response Status"),
	FRAME_ERROR (0xFE, "Frame Error"),
	GENERIC (0xFF, "Generic");
	
	// Variables.
	private final int idValue;
	
	private final String name;
	
	private static final HashMap<Integer, APIFrameType> lookupTable = new HashMap<Integer, APIFrameType>();
	
	static {
		for (APIFrameType type:values())
			lookupTable.put(type.getValue(), type);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code APIFrameType} object with
	 * the given value and name.
	 * 
	 * @param idValue Frame type value.
	 * @param name Frame type name.
	 */
	APIFrameType(int idValue, String name) {
		this.idValue = idValue;
		this.name = name;
	}
	
	/**
	 * Returns the {@code APIFrameType} associated with the given ID value.
	 * 
	 * @param value ID value to retrieve {@code APIFrameType}.
	 * 
	 * @return The {@code APIFrameType} for the given ID value, {@code #UNKNOWN}
	 *         if it does not supported.
	 */
	public static APIFrameType get(int value) {
		APIFrameType type = lookupTable.get(value);
		if (type == null)
			return UNKNOWN;
		return type; 
	}
	
	/**
	 * Returns the API frame type value.
	 * 
	 * @return The API frame type value.
	 */
	public int getValue() {
		return idValue;
	}
	
	/**
	 * Returns the API frame type name.
	 * 
	 * @return API frame type name.
	 */
	public String getName() {
		return name;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return "(" + HexUtils.byteArrayToHexString(ByteUtils.intToByteArray(idValue)) + ") " + name;
	}
}

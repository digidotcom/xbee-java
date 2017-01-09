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
package com.digi.xbee.api.packet.cellular;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a TX (Transmit) SMS packet. Packet is built
 * using the parameters of the constructor or providing a valid API payload.
 *
 * <p>A TX SMS message will cause the cellular module to send an SMS.</p>
 *
 * @see RXSMSPacket
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 * 
 * @since 1.2.0
 */
public class TXSMSPacket extends XBeeAPIPacket {

	// Constants.
	static final int PHONE_NUMBER_LENGTH = 20;
	static final String PHONE_NUMBER_PATTERN = "^\\+?\\d+$";

	static final String ERROR_PHONE_NUMBER_LENGTH = "Phone number length cannot be greater than " + PHONE_NUMBER_LENGTH + " bytes.";
	static final String ERROR_PHONE_NUMBER_NULL = "Phone number cannot be null.";
	static final String ERROR_PHONE_NUMBER_INVALID = "Phone number invalid, only numbers and '+' prefix allowed.";

	private static final int MIN_API_PAYLOAD_LENGTH = 3 /* 1 (Frame type) + 1 (frame ID) + 1 (transmit options) */ + PHONE_NUMBER_LENGTH;

	private static final String ERROR_PAYLOAD_NULL = "TX SMS packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete TX SMS packet.";
	private static final String ERROR_NOT_TXSMS = "Payload is not a TX SMS packet.";
	private static final String ERROR_FRAME_ID_ILLEGAL = "Frame ID must be between 0 and 255.";

	// Variables.
	private int transmitOptions = 0x00; // Reserved field.

	private byte[] phoneNumber;

	private String data;

	private Logger logger;

	/**
	 * Creates a new {@code TXSMSPacket} object from the given payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a TX SMS packet ({@code 0x1F}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 *
	 * @return Parsed TX SMS packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.TX_SMS.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static TXSMSPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.TX_SMS.getValue())
			throw new IllegalArgumentException(ERROR_NOT_TXSMS);

		// payload[0] is the frame type.
		int index = 1;

		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;

		// Transmit options byte, reserved.
		index = index + 1;

		// Bytes of phone number.
		byte[] phoneNumber = Arrays.copyOfRange(payload, index, index + PHONE_NUMBER_LENGTH);
		index = index + PHONE_NUMBER_LENGTH;

		// Get data.
		byte[] data = null;
		if (index < payload.length)
			data = Arrays.copyOfRange(payload, index, payload.length);

		return new TXSMSPacket(frameID, new String(phoneNumber).replace("\0", ""), data == null ? null : new String(data));
	}

	/**
	 * Class constructor. Instantiates a new {@code TXSMSPacket} object with
	 * the given parameters.
	 *
	 * @param frameID Frame ID.
	 * @param phoneNumber Phone number. Only numbers and '+' prefix allowed.
	 * @param data Data to send as body of the SMS message.
	 *
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code phoneNumber.length() > }{@value #PHONE_NUMBER_LENGTH} or
	 *                                  if {@code phoneNumber} is invalid.
	 * @throws NullPointerException if {@code phoneNumber == null}.
	 */
	public TXSMSPacket(int frameID, String phoneNumber, String data) {
		super(APIFrameType.TX_SMS);

		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException(ERROR_FRAME_ID_ILLEGAL);
		if (phoneNumber == null)
			throw new NullPointerException(ERROR_PHONE_NUMBER_NULL);
		if (phoneNumber.length() > PHONE_NUMBER_LENGTH)
			throw new IllegalArgumentException(ERROR_PHONE_NUMBER_LENGTH);
		if (!Pattern.matches(PHONE_NUMBER_PATTERN, phoneNumber))
			throw new IllegalArgumentException(ERROR_PHONE_NUMBER_INVALID);

		this.frameID = frameID;
		this.phoneNumber = Arrays.copyOf(phoneNumber.getBytes(), PHONE_NUMBER_LENGTH);
		this.data = data;
		this.logger = LoggerFactory.getLogger(TXSMSPacket.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write((byte)transmitOptions); // Transmit options, reserved.
			os.write(phoneNumber);
			if (data != null)
				os.write(data.getBytes());
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
	 * Sets the phone number.
	 *
	 * <p>Only numbers and '+' prefix allowed.</p>
	 *
	 * @param phoneNumber The phone number.
	 *
	 * @throws IllegalArgumentException if {@code phoneNumber.length() > }{@value #PHONE_NUMBER_LENGTH} or
	 *                                  if {@code phoneNumber} is invalid.
	 * @throws NullPointerException if {@code phoneNumber == null}.
	 *
	 * @see #getPhoneNumber()
	 * @see #getPhoneNumberByteArray()
	 */
	public void setPhoneNumber(String phoneNumber) {
		if (phoneNumber == null)
			throw new NullPointerException(ERROR_PHONE_NUMBER_NULL);
		if (phoneNumber.length() > PHONE_NUMBER_LENGTH)
			throw new IllegalArgumentException(ERROR_PHONE_NUMBER_LENGTH);
		if (!Pattern.matches(PHONE_NUMBER_PATTERN, phoneNumber))
			throw new IllegalArgumentException(ERROR_PHONE_NUMBER_INVALID);

		this.phoneNumber = Arrays.copyOf(phoneNumber.getBytes(), PHONE_NUMBER_LENGTH);
	}

	/**
	 * Returns the phone number byte array.
	 *
	 * @return The phone number byte array.
	 *
	 * @see #getPhoneNumber()
	 * @see #setPhoneNumber(String)
	 */
	public byte[] getPhoneNumberByteArray() {
		return phoneNumber;
	}

	/**
	 * Returns the phone number.
	 *
	 * @return The phone number.
	 *
	 * @see #getPhoneNumberByteArray()
	 * @see #setPhoneNumber(String)
	 */
	public String getPhoneNumber() {
		return new String(phoneNumber).replace("\0", "");
	}

	/**
	 * Sets the data to send.
	 *
	 * @param data Data to send.
	 *
	 * @see #getData()
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * Returns the data to send.
	 *
	 * @return Data to send.
	 *
	 * @see #setData(String)
	 */
	public String getData() {
		return data;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Transmit options", HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1)));
		parameters.put("Phone number", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(phoneNumber)) + " (" + new String(phoneNumber).replaceAll("\0", "") + ")");
		if (data != null)
			parameters.put("Data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data.getBytes())) + " (" + data + ")");
		return parameters;
	}
}

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.RestFulStatusEnum;
import com.digi.xbee.api.models.TLV;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a CoAP Passthru Tx Response packet. Packet is built
 * using the parameters of the constructor.
 * 
 * <p>A CoAP Passthru Tx Response is an explicit, meaning a complete and 
 * therefore complex, form of a CoAP response which is sent to the serial port 
 * of a server Thread device for transmission to a remote client Thread device. 
 * The client will send a {@link CoAPPassthruRxResponsePacket} out of its 
 * serial port.</p>
 * 
 * <p>The token field is used by client and server to match requests and 
 * responses.</p>
 * 
 * <p>The code field contains the RESTful response code.</p>
 * 
 * <p>The options field can contain a list of TLVs. See 
 * {@link com.digi.xbee.api.models.TLV}.</p>
 * 
 * <p>The packet also include an optional payload.</p>
 * 
 * @see CoAPPassthruTxRequestPacket
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 * 
 * @since 1.2.1
 */
public class CoAPPassthruTxResponsePacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 5; /* 1 (Frame type) + 1 (token length) + 2 (Restful Status) + 1 (options length) */

	private static final String ERROR_PAYLOAD_NULL = "CoAP Passthru Tx Response packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete CoAP Passthru Tx Response packet.";
	private static final String ERROR_NOT_COAP_PASS_TX_RESP = "Payload is not a CoAP Passthru Tx Response packet.";
	private static final String ERROR_STATUS_NULL = "RESTFul status cannot be null.";

	private static final String OPERATION_EXCEPTION = "Operation not supported in this module.";

	// Variables.
	private byte[] token;

	private RestFulStatusEnum restFulStatus;

	private ArrayList<TLV> options;

	private byte[] payload;

	private Logger logger;

	/**
	 * Creates a new {@code CoAPPassthruTxResponsePacket} object from the given payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a TX IPv6 packet ({@code 0x1D}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 *
	 * @return Parsed CoAP Passthru Tx Response packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.COAP_PASSTHRU_TX_RESPONSE.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH} or
	 *                                  if {@code TLV byte[] length > 0 && TLV byte[] length < 3}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static CoAPPassthruTxResponsePacket createPacket(byte[] payload) throws IllegalArgumentException {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.COAP_PASSTHRU_TX_RESPONSE.getValue())
			throw new IllegalArgumentException(ERROR_NOT_COAP_PASS_TX_RESP);

		// payload[0] is the frame type.
		int index = 1;

		// Token length byte.
		int tokenLength = payload[index] & 0xFF;
		index = index + 1;

		// Token (length depends on previous field).
		byte[] token = null;
		if (index < index + tokenLength)
			token = Arrays.copyOfRange(payload, index, index + tokenLength);
		index = index + tokenLength;

		// 2 bytes of RESTFul status.
		RestFulStatusEnum restFulStatus = RestFulStatusEnum.get((payload[index] & 0xFF) << 8 | payload[index + 1] & 0xFF);
		index = index + 2;

		// Options length byte.
		int optionsLength = payload[index] & 0xFF;
		index = index + 1;

		// Options (length depends on previous field).
		ArrayList<TLV> options = null;
		if (index < index + optionsLength)
			options = TLV.parseTLV(Arrays.copyOfRange(payload, index, index + optionsLength));
		index = index + optionsLength;

		// Get data.
		byte[] data = null;
		if (index < payload.length)
			data = Arrays.copyOfRange(payload, index, payload.length);

		return new CoAPPassthruTxResponsePacket(token, restFulStatus, options, data);
	}

	/**
	 * Class constructor. Instantiates a new {@code CoAPPassthruTxResponsePacket} object with
	 * the given parameters.
	 *
	 * @param token Token.
	 * @param restFulStatus RESTful response code.
	 * @param options List of TLVs.
	 * @param payload Payload.
	 *
	 * @throws NullPointerException if {@code restFulStatus == null}.
	 *
	 * @see com.digi.xbee.api.models.RestFulStatusEnum
	 * @see com.digi.xbee.api.models.TLV
	 */
	public CoAPPassthruTxResponsePacket(byte[] token, RestFulStatusEnum restFulStatus,
			ArrayList<TLV> options, byte[] payload) {
		super(APIFrameType.COAP_PASSTHRU_TX_RESPONSE);

		if (restFulStatus == null)
			throw new NullPointerException(ERROR_STATUS_NULL);

		this.token = token;
		this.restFulStatus = restFulStatus;
		this.options = options;
		this.payload = payload;
		this.logger = LoggerFactory.getLogger(CoAPPassthruTxResponsePacket.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(token.length);
			os.write(token);
			os.write(restFulStatus.getID());
			int length = 0;
			for (TLV option: options)
				length += option.toByteArray().length;
			os.write(length);
			for (TLV option: options)
				os.write(option.toByteArray());
			if (payload != null)
				os.write(payload);
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

	/**
	 * @deprecated Operation not supported in this protocol. This method will
	 *             raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public boolean isBroadcast() {
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}

	/**
	 * Sets the token used by client and server to match requests with 
	 * responses.
	 * 
	 * @param token The token.
	 * 
	 * @see #getToken()
	 */
	public void setToken(byte[] token) {
		if (token == null)
			this.token = null;
		else
			this.token = token;
	}

	/**
	 * Retrieves the token used by client and server to match requests with 
	 * responses.
	 * 
	 * @return The token.
	 * 
	 * @see #setToken(byte[])
	 */
	public byte[] getToken() {
		return token;
	}

	/**
	 * Sets the RESTFul status used for the transmission.
	 * 
	 * @param restFulStatus RESTFul status.
	 * 
	 * @see #getStatus()
	 * @see com.digi.xbee.api.models.RestFulStatusEnum
	 */
	public void setStatus(RestFulStatusEnum restFulStatus) {
		if (restFulStatus == null)
			throw new NullPointerException(ERROR_STATUS_NULL);

		this.restFulStatus = restFulStatus;
	}

	/**
	 * Returns the RESTFul status used for the transmission.
	 * 
	 * @return RESTFul status.
	 * 
	 * @see #setStatus(RestFulStatusEnum)
	 * @see com.digi.xbee.api.models.RestFulStatusEnum
	 */
	public RestFulStatusEnum getStatus() {
		return restFulStatus;
	}

	/**
	 * Sets the list of TLVs.
	 * 
	 * @param options The list of TLVs.
	 * 
	 * @see #getOptions()
	 * @see com.digi.xbee.api.models.TLV
	 */
	public void setOptions(ArrayList<TLV> options) {
		this.options = options;
	}

	/**
	 * Retrieves the list of TLVs.
	 * 
	 * @return The list of TLVs.
	 * 
	 * @see #setOptions(ArrayList)
	 * @see com.digi.xbee.api.models.TLV
	 */
	public ArrayList<TLV> getOptions() {
		return options;
	}

	/**
	 * Sets the payload data.
	 * 
	 * @param payload Payload data to send.
	 * 
	 * @see #getPayload()
	 */
	public void setPayload(byte[] payload) {
		if (payload == null)
			this.payload = null;
		else
			this.payload = Arrays.copyOf(payload, payload.length);
	}

	/**
	 * Returns the payload data to send.
	 * 
	 * @return Payload data to send.
	 * 
	 * @see #setPayload(byte[])
	 */
	public byte[] getPayload() {
		if (payload == null)
			return null;
		return Arrays.copyOf(payload, payload.length);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Token length", HexUtils.prettyHexString(HexUtils.integerToHexString(token.length, 1)) + " (" + token.length + ")");
		parameters.put("Token", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(token)));
		parameters.put("Code", HexUtils.prettyHexString(HexUtils.integerToHexString(restFulStatus.getID(), 2)));
		int length = 0;
		for (TLV tlv: options)
			length += tlv.toByteArray().length;
		parameters.put("Options length", HexUtils.prettyHexString(HexUtils.integerToHexString(length, 1)) + " (" + length + ")");
		StringBuilder opts = new StringBuilder();
		for (TLV tlv: options) {
			opts.append(HexUtils.prettyHexString(HexUtils.byteArrayToHexString(tlv.toByteArray())));
			opts.append(" ");
		}
		if (options.size() > 0)
			parameters.put("Options", opts.toString());
		if (payload != null)
			parameters.put("RF data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(payload)));
		return parameters;
	}
}

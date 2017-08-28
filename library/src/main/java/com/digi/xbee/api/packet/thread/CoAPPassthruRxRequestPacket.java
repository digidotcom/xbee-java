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

import com.digi.xbee.api.models.HTTPMethodEnum;
import com.digi.xbee.api.models.TLV;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a CoAP Passthru Rx Request packet. Packet is built
 * using the parameters of the constructor.
 * 
 * <p>A CoAP Passthru Rx Request is an explicit, meaning a complete and 
 * therefore complex, form of a CoAP request which is sent out the serial port 
 * of a server Thread device for response by an external device. The external 
 * device is expected to send a {@link CoAPPassthruTxResponsePacket} into the 
 * serial port of the server Thread device to send the response to the client.
 * </p>
 * 
 * <p>The token field is used by client and server to match requests and 
 * responses.</p>
 * 
 * <p>The method indicates the HTTP function used for the transmission. It is 
 * specified by the {@link HTTPMethodEnum} enumerator.</p>
 * 
 * <p>The URI field is a string that must be {@value #URI_DATA_TRANSMISSION} for
 * data transmission (PUT) or {@value #URI_AT_COMMAND} for AT Command operations
 * (PUT or GET).</p>
 * 
 * <p>The options field can contain a list of TLVs. See 
 * {@link com.digi.xbee.api.models.TLV}.</p>
 * 
 * <p>The packet also include an optional payload.</p>
 * 
 * @see CoAPPassthruTxResponsePacket
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 * 
 * @since 1.2.1
 */
public class CoAPPassthruRxRequestPacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 10; /* 1 (Frame type) + 1 (token length) + 1 (RESTful method) +
																1 (URI length) + 5 (URI) + 1 (options length) */

	private static final String ERROR_PAYLOAD_NULL = "CoAP Passthru Rx Request packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete CoAP Passthru Rx Request packet.";
	private static final String ERROR_NOT_COAP_PASS_RX_REQ = "Payload is not a CoAP Passthru Rx Request packet.";
	private static final String ERROR_METHOD_NULL = "HTTP Method cannot be null.";
	private static final String ERROR_URI_NULL = "URI cannot be null.";

	private static final String OPERATION_EXCEPTION = "Operation not supported in this module.";

	// Variables.
	private byte[] token;

	private HTTPMethodEnum method;

	private String uri;

	private ArrayList<TLV> options;

	private byte[] payload;

	private Logger logger;

	/**
	 * Creates a new {@code CoAPPassthruRxRequestPacket} object from the given payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a TX IPv6 packet ({@code 0x1D}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 *
	 * @return Parsed CoAP Passthru Rx Request packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.COAP_PASSTHRU_RX_REQUEST.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH} or
	 *                                  if {@code TLV byte[] length > 0 && TLV byte[] length < 3}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static CoAPPassthruRxRequestPacket createPacket(byte[] payload) throws IllegalArgumentException {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.COAP_PASSTHRU_RX_REQUEST.getValue())
			throw new IllegalArgumentException(ERROR_NOT_COAP_PASS_RX_REQ);

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

		// Method.
		HTTPMethodEnum method = HTTPMethodEnum.get(payload[index] & 0xFF);
		index = index + 1;

		// URI length.
		int uriLength = payload[index] & 0xFF;
		index = index + 1;

		// URI (length depends on previous field).
		String uri = null;
		if (index < index + uriLength)
			uri = new String(Arrays.copyOfRange(payload, index, index + uriLength));
		index = index + uriLength;

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

		return new CoAPPassthruRxRequestPacket(token, method, uri, options, data);
	}

	/**
	 * Class constructor. Instantiates a new {@code CoAPPassthruRxRequestPacket} object with
	 * the given parameters.
	 *
	 * @param token Token.
	 * @param method HTTP method used for the transmission.
	 * @param uri Uniform Resource Identifier.
	 * @param options List of TLVs.
	 * @param payload Payload.
	 *
	 * @throws NullPointerException if {@code restFulStatus == null}.
	 *
	 * @see com.digi.xbee.api.models.RestFulStatusEnum
	 * @see com.digi.xbee.api.models.TLV
	 */
	public CoAPPassthruRxRequestPacket(byte[] token, HTTPMethodEnum method,
			String uri, ArrayList<TLV> options, byte[] payload) {
		super(APIFrameType.COAP_PASSTHRU_RX_REQUEST);

		if (method == null)
			throw new NullPointerException(ERROR_METHOD_NULL);
		if (uri == null)
			throw new NullPointerException(ERROR_URI_NULL);

		this.token = token;
		this.method = method;
		this.uri = uri;
		this.options = options;
		this.payload = payload;
		this.logger = LoggerFactory.getLogger(CoAPPassthruRxRequestPacket.class);
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
			os.write(method.getValue());
			os.write(uri.length());
			os.write(uri.getBytes());
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
	 * Sets the HTTP method used for the transmission.
	 * 
	 * @param method HTTP method.
	 * 
	 * @see #getMethod()
	 * @see com.digi.xbee.api.models.HTTPMethodEnum
	 */
	public void setMethod(HTTPMethodEnum method) {
		if (method == null)
			throw new NullPointerException(ERROR_METHOD_NULL);

		this.method = method;
	}

	/**
	 * Returns the HTTP method used for the transmission.
	 * 
	 * @return HTTP method.
	 * 
	 * @see #setMethod(HTTPMethodEnum)
	 * @see com.digi.xbee.api.models.HTTPMethodEnum
	 */
	public HTTPMethodEnum getMethod() {
		return method;
	}

	/**
	 * Sets the URI.
	 * 
	 * @param uri URI.
	 * 
	 * @see #getURI()
	 */
	public void setURI(String uri) {
		if (uri == null)
			throw new NullPointerException(ERROR_URI_NULL);

		this.uri = uri;
	}

	/**
	 * Retrieves the URI.
	 * 
	 * @return The URI.
	 * 
	 * @see #setURI(String)
	 */
	public String getURI() {
		return uri;
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
		parameters.put("Method", HexUtils.prettyHexString(HexUtils.integerToHexString(method.getValue(), 1)) + " (" + method.getName() + ")");
		parameters.put("URI length", HexUtils.prettyHexString(HexUtils.integerToHexString(uri.length(), 1)) + " (" + uri.length() + ")");
		parameters.put("URI", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(uri.getBytes())) + " (" + uri + ")");
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

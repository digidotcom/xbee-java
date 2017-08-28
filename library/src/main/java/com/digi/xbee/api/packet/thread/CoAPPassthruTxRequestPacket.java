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
import java.net.Inet6Address;
import java.net.UnknownHostException;
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
 * This class represents a CoAP Passthru Tx Request packet. Packet is built
 * using the parameters of the constructor.
 * 
 * <p>A CoAP Passthru Tx Request is an explicit, meaning a complete and 
 * therefore complex, form of a CoAP request which is sent to the serial port 
 * of a client Thread device for transmission to a remote server Thread device. 
 * The server  will send a {@link CoAPPassthruRxResponsePacket}, which will 
 * come out of the client Thread device's serial port.</p>
 * 
 * <p>If the frame ID specified in the packet is zero, no response is requested
 * or expected.</p>
 * 
 * <p>The method indicates the HTTP function used for the transmission. It is 
 * specified by the {@link HTTPMethodEnum} enumerator.</p>
 * 
 * <p>The URI field is a string that must be {@value #URI_DATA_TRANSMISSION} for
 * data transmission (PUT) or {@value #URI_AT_COMMAND} for AT Command operations
 * (PUT or GET).</p>
 * 
 * <p>The options field can contain a list of TLVs. See 
 * {@link com.digi.xbee.models.TLV}.</p>
 * 
 * <p>The packet also include an optional payload. For data transmission, it
 * should contain the data to send; for AT Command operations, empty to query
 * the setting (GET) or the new value (PUT).</p>
 * 
 * @see CoAPPassthruRxResponsePacket
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 * 
 * @since 1.2.1
 */
public class CoAPPassthruTxRequestPacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 26; /* 1 (Frame type) + 1 (Frame ID) + 1 (RESTful method) + 16 (dest address) +
																1 (URI length) + 5 (URI) + 1 (options length) */

	private static final String ERROR_PAYLOAD_NULL = "CoAP Passthru Tx Request packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete CoAP Passthru Tx Request packet.";
	private static final String ERROR_NOT_COAP_PASS_TX_REQ = "Payload is not a CoAP Passthru Tx Request packet.";
	private static final String ERROR_DEST_ADDR_NULL = "Destination address cannot be null.";
	private static final String ERROR_METHOD_NULL = "HTTP Method cannot be null.";
	private static final String ERROR_URI_NULL = "URI cannot be null.";
	private static final String ERROR_FRAME_ID_ILLEGAL = "Frame ID must be between 0 and 255.";

	private static final String OPERATION_EXCEPTION = "Operation not supported in this module.";

	// Variables.
	private HTTPMethodEnum method;

	private Inet6Address destAddress;

	private String uri;

	private ArrayList<TLV> options;

	private byte[] payload;

	private Logger logger;

	/**
	 * Creates a new {@code CoAPPassthruTxRequestPacket} object from the given payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a TX IPv6 packet ({@code 0x1E}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 *
	 * @return Parsed CoAP Passthru Tx Response packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.COAP_PASSTHRU_TX_REQUEST.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH} or
	 *                                  if {@code TLV byte[] length > 0 && TLV byte[] length < 3}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static CoAPPassthruTxRequestPacket createPacket(byte[] payload) throws IllegalArgumentException {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.COAP_PASSTHRU_TX_REQUEST.getValue())
			throw new IllegalArgumentException(ERROR_NOT_COAP_PASS_TX_REQ);

		// payload[0] is the frame type.
		int index = 1;

		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;

		// Method.
		HTTPMethodEnum method = HTTPMethodEnum.get(payload[index] & 0xFF);
		index = index + 1;

		// 16 bytes of IP destination address.
		Inet6Address destAddress;
		try {
			destAddress = (Inet6Address) Inet6Address.getByAddress(Arrays.copyOfRange(payload, index, index + 16));
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException(e);
		}
		index = index + 16;

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

		return new CoAPPassthruTxRequestPacket(frameID, method, destAddress, uri, options, data);
	}

	/**
	 * Class constructor. Instantiates a new {@code CoAPPassthruTxRequestPacket} object with
	 * the given parameters.
	 *
	 * @param frameID Frame ID.
	 * @param method HTTP method used for the transmission.
	 * @param destAddress IPv6 address of the destination device.
	 * @param uri Uniform Resource Identifier.
	 * @param options List of TLVs.
	 * @param payload Payload.
	 *
	 *@throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 *
	 * @throws NullPointerException if {@code method == null} or 
	 *                              if {@code destAddress == null} or
	 *                              if {@code uri == null}.
	 *
	 * @see com.digi.xbee.api.models.HTTPMethodEnum
	 * @see com.digi.xbee.api.models.TLV
	 * @see java.net.Inet6Address
	 */
	public CoAPPassthruTxRequestPacket(int frameID, HTTPMethodEnum method, Inet6Address destAddress,
			String uri, ArrayList<TLV> options, byte[] payload) {
		super(APIFrameType.COAP_PASSTHRU_TX_REQUEST);

		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException(ERROR_FRAME_ID_ILLEGAL);
		if (method == null)
			throw new NullPointerException(ERROR_METHOD_NULL);
		if (destAddress == null)
			throw new NullPointerException(ERROR_DEST_ADDR_NULL);
		if (uri == null)
			throw new NullPointerException(ERROR_URI_NULL);

		this.frameID = frameID;
		this.method = method;
		this.destAddress = destAddress;
		this.uri = uri;
		this.options = options;
		this.payload = payload;
		this.logger = LoggerFactory.getLogger(CoAPPassthruTxRequestPacket.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(method.getValue());
			os.write(destAddress.getAddress());
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
	 * Sets the destination IPv6 address.
	 *
	 * @param destAddress The new destination IPv6 address.
	 *
	 * @throws NullPointerException if {@code destAddress == null}.
	 *
	 * @see #getDestAddress()
	 * @see java.net.Inet6Address
	 */
	public void setDestAddress(Inet6Address destAddress) {
		if (destAddress == null)
			throw new NullPointerException(ERROR_DEST_ADDR_NULL);

		this.destAddress = destAddress;
	}

	/**
	 * Retrieves the destination IPv6 address.
	 *
	 * @return The destination IPv6 address.
	 *
	 * @see #setDestAddress(Inet6Address)
	 * @see java.net.Inet6Address
	 */
	public Inet6Address getDestAddress() {
		return destAddress;
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
		parameters.put("Frame ID", HexUtils.prettyHexString(HexUtils.integerToHexString(frameID, 1)) + " (" + frameID + ")");
		parameters.put("Method", HexUtils.prettyHexString(HexUtils.integerToHexString(method.getValue(), 1)) + " (" + method.getName() + ")");
		parameters.put("Destination address", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(destAddress.getAddress())) +
				" (" + destAddress.getHostAddress() + ")");
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

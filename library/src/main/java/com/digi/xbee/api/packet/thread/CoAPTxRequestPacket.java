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
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.CoAPURI;
import com.digi.xbee.api.models.HTTPMethodEnum;
import com.digi.xbee.api.models.RemoteATCommandOptions;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a CoAP Tx Request packet. Packet is built
 * using the parameters of the constructor.
 * 
 * <p>A CoAP Tx Request packet is used to send a CoAP message to a remote device 
 * of the network identified by an IPv6 address.</p>
 * 
 * <p>If the frame ID specified in the packet is different than zero, a Transmit 
 * Status frame (0x8B) is received with the status of the transmission. See 
 * {@link com.digi.xbee.api.packet.common.TransmitStatusPacket}.</p>
 * 
 * <p>The method indicates the HTTP function used for the transmission. It is 
 * specified by the {@link HTTPMethodEnum} enumerator.</p>
 * 
 * <p>The URI field is a string that must be {@value CoAPURI#URI_DATA_TRANSMISSION} 
 * for data transmission (PUT), {@value CoAPURI#URI_AT_COMMAND} for AT Command 
 * operations (PUT or GET) or {@value CoAPURI#URI_IO_SAMPLING} for IO 
 * operation (POST).</p>
 * 
 * <p>The packet also includes an optional payload. For data transmission, it
 * should contain the data to send; for AT Command operations, empty to query
 * the setting (GET) or the new value (PUT).</p>
 * 
 * @see CoAPRxResponsePacket
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 * 
 * @since 1.2.1
 */
public class CoAPTxRequestPacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 26; /* 1 (Frame type) + 1 (frame ID) + 1 (transmit options) + 
															 1 (RESTful method) + 16 (dest address) + 1 (URI length) + 5 (URI) */

	private static final String ERROR_PAYLOAD_NULL = "CoAP Tx Request packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete CoAP Tx Request packet.";
	private static final String ERROR_NOT_COAP_TX_REQ = "Payload is not a CoAP Tx Request packet.";
	private static final String ERROR_DEST_ADDR_NULL = "Destination address cannot be null.";
	private static final String ERROR_METHOD_NULL = "HTTP Method cannot be null.";
	private static final String ERROR_URI_NULL = "URI cannot be null.";
	private static final String ERROR_FRAME_ID_ILLEGAL = "Frame ID must be between 0 and 255.";
	private static final String ERROR_OPTIONS_INVALID = "Transmit options can only be " + RemoteATCommandOptions.OPTION_NONE +
			" or " + RemoteATCommandOptions.OPTION_APPLY_CHANGES + ".";

	private static final String OPERATION_EXCEPTION = "Operation not supported in this module.";

	// Variables.
	private int transmitOptions;

	private HTTPMethodEnum method;

	private Inet6Address destAddress;

	private String uri;

	private byte[] payload;

	private Logger logger;

	/**
	 * Creates a new {@code CoAPTxRequestPacket} object from the given payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a CoAP Tx Request packet ({@code 0x1C}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 *
	 * @return Parsed CoAP Tx Request packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.COAP_TX_REQUEST.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static CoAPTxRequestPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.COAP_TX_REQUEST.getValue())
			throw new IllegalArgumentException(ERROR_NOT_COAP_TX_REQ);

		// payload[0] is the frame type.
		int index = 1;

		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;

		// Transmit options.
		int transmitOptions = payload[index] & 0xFF;
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

		// Get data.
		byte[] data = null;
		if (index < payload.length)
			data = Arrays.copyOfRange(payload, index, payload.length);

		return new CoAPTxRequestPacket(frameID, transmitOptions, method, destAddress, uri, data);
	}

	/**
	 * Class constructor. Instantiates a new {@code CoAPTxRequestPacket} object with
	 * the given parameters.
	 *
	 * @param frameID Frame ID.
	 * @param transmitOptions Bitfield of supported transmission options.
	 * @param method HTTP method used for the transmission.
	 * @param destAddress IPv6 address of the destination device.
	 * @param uri Uniform Resource Identifier.
	 * @param payload Payload.
	 *
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code uri contains }{@value CoAPURI#URI_AT_COMMAND} and {@code transmitOptions != }{@value RemoteATCommandOptions#OPTION_NONE} or {@code transmitOptions != }{@value RemoteATCommandOptions#OPTION_APPLY_CHANGES}
	 *                                  if {@code uri does not contain }{@value CoAPURI#URI_AT_COMMAND} and {@code transmitOptions != }{@value RemoteATCommandOptions#OPTION_NONE}.
	 * @throws NullPointerException if {@code method == null} or 
	 *                              if {@code destAddress == null} or
	 *                              if {@code uri == null}.
	 *
	 * @see com.digi.xbee.api.models.HTTPMethodEnum
	 * @see com.digi.xbee.api.models.RemoteATCommandOptions
	 * @see java.net.Inet6Address
	 */
	public CoAPTxRequestPacket(int frameID, int transmitOptions, HTTPMethodEnum method,
			Inet6Address destAddress, String uri, byte[] payload) {
		super(APIFrameType.COAP_TX_REQUEST);

		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException(ERROR_FRAME_ID_ILLEGAL);
		if ((uri != null && !uri.contains(CoAPURI.URI_AT_COMMAND) && transmitOptions != RemoteATCommandOptions.OPTION_NONE) 
				|| (uri != null && uri.contains(CoAPURI.URI_AT_COMMAND) && transmitOptions != RemoteATCommandOptions.OPTION_NONE && transmitOptions != RemoteATCommandOptions.OPTION_APPLY_CHANGES))
			throw new IllegalArgumentException(ERROR_OPTIONS_INVALID);
		if (method == null)
			throw new NullPointerException(ERROR_METHOD_NULL);
		if (destAddress == null)
			throw new NullPointerException(ERROR_DEST_ADDR_NULL);
		if (uri == null)
			throw new NullPointerException(ERROR_URI_NULL);

		this.frameID = frameID;
		this.transmitOptions = transmitOptions;
		this.method = method;
		this.destAddress = destAddress;
		this.uri = uri;
		this.payload = payload;
		this.logger = LoggerFactory.getLogger(CoAPTxRequestPacket.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(transmitOptions & 0xFF);
			os.write(method.getValue() & 0xFF);
			os.write(destAddress.getAddress());
			os.write(uri.length() & 0xFF);
			os.write(uri.getBytes());
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
	 * Sets the transmit options bifield.
	 * 
	 * @param transmitOptions The transmit options bitfield.
	 * 
	 * @throws IllegalArgumentException if {@code uri contains }{@value CoAPURI#URI_AT_COMMAND} and {@code transmitOptions != }{@value RemoteATCommandOptions#OPTION_NONE} or {@code transmitOptions != }{@value RemoteATCommandOptions#OPTION_APPLY_CHANGES}
	 *                                  if {@code uri does not contain }{@value CoAPURI#URI_AT_COMMAND} and {@code transmitOptions != }{@value RemoteATCommandOptions#OPTION_NONE}.
	 * 
	 * @see #getTransmitOptions()
	 * @see com.digi.xbee.api.models.RemoteATCommandOptions
	 */
	public void setTransmitOptions(int transmitOptions) {
		if ((uri != null && !uri.contains(CoAPURI.URI_AT_COMMAND) && transmitOptions != RemoteATCommandOptions.OPTION_NONE) 
				|| (uri != null && uri.contains(CoAPURI.URI_AT_COMMAND) && transmitOptions != RemoteATCommandOptions.OPTION_NONE && transmitOptions != RemoteATCommandOptions.OPTION_APPLY_CHANGES))
			throw new IllegalArgumentException(ERROR_OPTIONS_INVALID);

		this.transmitOptions = transmitOptions;
	}

	/**
	 * Retrieves the transmit options bitfield.
	 * 
	 * @return Transmit options bitfield.
	 * 
	 * @see #setTransmitOptions(int)
	 * @see com.digi.xbee.api.models.RemoteATCommandOptions
	 */
	public int getTransmitOptions() {
		return transmitOptions;
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
	 * @throws NullPointerException if {@code uri == null}.
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
	 * Sets the new transmission data.
	 *
	 * @param payload The transmission data.
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
	 * Retrieves the transmission data.
	 *
	 * @return The transmission data.
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
		parameters.put("Options", HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1)));
		parameters.put("Method", HexUtils.prettyHexString(HexUtils.integerToHexString(method.getValue(), 1)) + " (" + method.getName() + ")");
		parameters.put("Destination address", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(destAddress.getAddress())) +
				" (" + destAddress.getHostAddress() + ")");
		parameters.put("URI length", HexUtils.prettyHexString(HexUtils.integerToHexString(uri.length(), 1)) + " (" + uri.length() + ")");
		parameters.put("URI", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(uri.getBytes())) + " (" + uri + ")");
		if (payload != null)
			parameters.put("Payload", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(payload)));
		return parameters;
	}
}


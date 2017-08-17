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

import com.digi.xbee.api.models.IPProtocol;
import com.digi.xbee.api.models.RestFulStatusEnum;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a CoAP Rx Response packet. Packet is built using the 
 * parameters of the constructor.
 * 
 * <p>When the module receives a CoAP packet, it is sent out the UART using 
 * this message type.</p>
 * 
 * <p>This packet is received when external devices send CoAP Tx Request 
 * packets to the module. See 
 * {@link com.digi.xbee.api.packet.thread.CoAPTxRequestPacket}.</p>
 * 
 * @see CoAPTxRequestPacket
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 * 
 * @since 1.2.1
 */
public class CoAPRxResponsePacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 41; /* 1 (Frame type) + 1 (frame ID) + 16 (dest address) + 16 (source address) +
																2 (dest port) + 2 (source port) + 1 (protocol) + 2 (Restful Status) */

	private static final String ERROR_PAYLOAD_NULL = "CoAP Rx Response packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete CoAP Rx Response packet.";
	private static final String ERROR_NOT_COAP_RX_RESP = "Payload is not a CoAP Rx Response packet.";
	private static final String ERROR_DEST_ADDR_NULL = "Destination address cannot be null.";
	private static final String ERROR_SOURCE_ADDR_NULL = "Source address cannot be null.";
	private static final String ERROR_PROTOCOL_NULL = "Protocol cannot be null.";
	private static final String ERROR_STATUS_NULL = "RESTFul status cannot be null.";
	private static final String ERROR_FRAME_ID_ILLEGAL = "Frame ID must be between 0 and 255.";
	private static final String ERROR_DEST_PORT_ILLEGAL = "Destination port must be between 0 and 65535.";
	private static final String ERROR_SOURCE_PORT_ILLEGAL = "Source port must be between 0 and 65535.";

	private static final String OPERATION_EXCEPTION = "Operation not supported in this module.";

	// Variables.
	private Inet6Address destAddress;
	private Inet6Address sourceAddress;

	private int destPort;
	private int sourcePort;

	private IPProtocol protocol;

	private RestFulStatusEnum restFulStatus;

	private byte[] data;

	private Logger logger;

	/**
	 * Creates a new {@code CoAPRxResponsePacket} object from the given payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a CoAP Rx Response packet ({@code 0x9C}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 *
	 * @return Parsed CoAP Tx Request packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.COAP_RX_RESPONSE.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static CoAPRxResponsePacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.COAP_RX_RESPONSE.getValue())
			throw new IllegalArgumentException(ERROR_NOT_COAP_RX_RESP);

		// payload[0] is the frame type.
		int index = 1;

		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;

		// 16 bytes of IP destination address.
		Inet6Address destAddress;
		try {
			destAddress = (Inet6Address) Inet6Address.getByAddress(Arrays.copyOfRange(payload, index, index + 16));
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException(e);
		}
		index = index + 16;

		// 16 bytes of IP source address.
		Inet6Address sourceAddress;
		try {
			sourceAddress = (Inet6Address) Inet6Address.getByAddress(Arrays.copyOfRange(payload, index, index + 16));
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException(e);
		}
		index = index + 16;

		// 2 bytes of destination port.
		int destPort = (payload[index] & 0xFF) << 8 | payload[index + 1] & 0xFF;
		index = index + 2;

		// 2 bytes of source port.
		int sourcePort = (payload[index] & 0xFF) << 8 | payload[index + 1] & 0xFF;
		index = index + 2;

		// Transmit options.
		IPProtocol protocol = IPProtocol.get(payload[index] & 0xFF);
		index = index + 1;

		// 2 bytes of RESTFul status.
		RestFulStatusEnum restFulStatus = RestFulStatusEnum.get((payload[index] & 0xFF) << 8 | payload[index + 1] & 0xFF);
		index = index + 2;

		// Get data.
		byte[] rfData = null;
		if (index < payload.length)
			rfData = Arrays.copyOfRange(payload, index, payload.length);

		return new CoAPRxResponsePacket(frameID, destAddress, sourceAddress, destPort, sourcePort, protocol, restFulStatus, rfData);
	}

	/**
	 * Class constructor. Instantiates a new {@code CoAPRxResponsePacket} object with
	 * the given parameters.
	 *
	 * @param frameID Frame ID.
	 * @param destAddress IPv6 address of the destination device.
	 * @param sourceAddress IPv6 address of the source device.
	 * @param destPort Destination port number.
	 * @param sourcePort Source port number.
	 * @param protocol Protocol used for transmitted data.
	 * @param restFulStatus RESTful status.
	 * @param rfData RF transmit data bytes.
	 *
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code destPort < 0} or
	 *                                  if {@code destPort > 65535} or
	 *                                  if {@code sourcePort < 0} or
	 *                                  if {@code sourcePort > 65535}.
	 * @throws NullPointerException if {@code destAddress == null} or 
	 *                              if {@code sourceAddress == null} or
	 *                              if {@code protocol == null} or
	 *                              if {@code restFulStatus == null}.
	 *
	 * @see java.net.Inet6Address
	 * @see com.digi.xbee.api.models.IPProtocol
	 * @see com.digi.xbee.api.models.RestFulStatusEnum
	 */
	public CoAPRxResponsePacket(int frameID, Inet6Address destAddress, Inet6Address sourceAddress,
			int destPort, int sourcePort, IPProtocol protocol, RestFulStatusEnum restFulStatus, byte[] rfData) {
		super(APIFrameType.COAP_RX_RESPONSE);

		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException(ERROR_FRAME_ID_ILLEGAL);
		if (destAddress == null)
			throw new NullPointerException(ERROR_DEST_ADDR_NULL);
		if (sourceAddress == null)
			throw new NullPointerException(ERROR_SOURCE_ADDR_NULL);
		if (destPort < 0 || destPort > 65535)
			throw new IllegalArgumentException(ERROR_DEST_PORT_ILLEGAL);
		if (sourcePort < 0 || sourcePort > 65535)
			throw new IllegalArgumentException(ERROR_SOURCE_PORT_ILLEGAL);
		if (protocol == null)
			throw new NullPointerException(ERROR_PROTOCOL_NULL);
		if (restFulStatus == null)
			throw new NullPointerException(ERROR_STATUS_NULL);

		this.frameID = frameID;
		this.destAddress = destAddress;
		this.sourceAddress = sourceAddress;
		this.destPort = destPort;
		this.sourcePort = sourcePort;
		this.protocol = protocol;
		this.restFulStatus = restFulStatus;
		this.data = rfData;
		this.logger = LoggerFactory.getLogger(CoAPRxResponsePacket.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(destAddress.getAddress());
			os.write(sourceAddress.getAddress());
			os.write(ByteUtils.shortToByteArray((short)destPort));
			os.write(ByteUtils.shortToByteArray((short)sourcePort));
			os.write(protocol.getID());
			os.write(ByteUtils.shortToByteArray((short)restFulStatus.getID()));
			if (data != null)
				os.write(data);
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
	 * Retrieves the source IPv6 address.
	 *
	 * @return The source IPv6 address.
	 *
	 * @see #setSourceAddress(Inet6Address)
	 * @see java.net.Inet6Address
	 */
	public Inet6Address getSourceAddress() {
		return sourceAddress;
	}

	/**
	 * Sets the source IPv6 address.
	 *
	 * @param sourceAddress The new source IPv6 address.
	 *
	 * @throws NullPointerException if {@code sourceAddress == null}.
	 *
	 * @see #getSourceAddress()
	 * @see java.net.Inet6Address
	 */
	public void setSourceAddress(Inet6Address sourceAddress) {
		if (sourceAddress == null)
			throw new NullPointerException(ERROR_SOURCE_ADDR_NULL);

		this.sourceAddress = sourceAddress;
	}

	/**
	 * Retrieves the destination port.
	 *
	 * @return The destination port.
	 *
	 * @see #setDestPort(int)
	 */
	public int getDestPort() {
		return destPort;
	}

	/**
	 * Sets the destination port.
	 *
	 * @param destPort The new destination port.
	 *
	 * @throws IllegalArgumentException if {@code destPort < 0} or
	 *                                  if {@code destPort > 65535}.
	 *
	 * @see #getDestPort()
	 */
	public void setDestPort(int destPort) {
		if (destPort < 0 || destPort > 65535)
			throw new IllegalArgumentException(ERROR_DEST_PORT_ILLEGAL);

		this.destPort = destPort;
	}

	/**
	 * Retrieves the source port.
	 *
	 * @return The source port.
	 *
	 * @see #setSourcePort(int)
	 */
	public int getSourcePort() {
		return sourcePort;
	}

	/**
	 * Sets the source port.
	 *
	 * @param sourcePort The new source port.
	 *
	 * @throws IllegalArgumentException if {@code sourcePort < 0} or
	 *                                  if {@code sourcePort > 65535}.
	 *
	 * @see #getSourcePort()
	 */
	public void setSourcePort(int sourcePort) {
		if (sourcePort < 0 || sourcePort > 65535)
			throw new IllegalArgumentException(ERROR_SOURCE_PORT_ILLEGAL);

		this.sourcePort = sourcePort;
	}

	/**
	 * Retrieves the IP protocol.
	 *
	 * @return The IP protocol.
	 *
	 * @see #setProtocol(IPProtocol)
	 * @see IPProtocol
	 */
	public IPProtocol getProtocol() {
		return protocol;
	}

	/**
	 * Sets the IP protocol.
	 *
	 * @param protocol The new IP protocol.
	 *
	 * @throws NullPointerException if {@code protocol == null}.
	 *
	 * @see #getProtocol()
	 * @see IPProtocol
	 */
	public void setProtocol(IPProtocol protocol) {
		if (protocol == null)
			throw new NullPointerException(ERROR_PROTOCOL_NULL);

		this.protocol = protocol;
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
	 * Sets the new transmission data.
	 *
	 * @param rfData The transmission data.
	 *
	 * @see #getData()
	 */
	public void setData(byte[] rfData) {
		if (rfData == null)
			this.data = null;
		else
			this.data = Arrays.copyOf(rfData, rfData.length);
	}

	/**
	 * Retrieves the transmission data.
	 *
	 * @return The transmission data.
	 *
	 * @see #setData(byte[])
	 */
	public byte[] getData() {
		if (data == null)
			return null;
		return Arrays.copyOf(data, data.length);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Destination address", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(destAddress.getAddress())) +
				" (" + destAddress.getHostAddress() + ")");
		parameters.put("Source address", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(sourceAddress.getAddress())) +
				" (" + sourceAddress.getHostAddress() + ")");
		parameters.put("Destination port", HexUtils.prettyHexString(HexUtils.integerToHexString(destPort, 2)) + " (" + destPort + ")");
		parameters.put("Source port", HexUtils.prettyHexString(HexUtils.integerToHexString(sourcePort, 2)) + " (" + sourcePort + ")");
		parameters.put("Protocol", HexUtils.prettyHexString(HexUtils.integerToHexString(protocol.getID(), 1)) + " (" + protocol.getName() + ")");
		parameters.put("RESTful Response Code", HexUtils.prettyHexString(HexUtils.integerToHexString(restFulStatus.getID(), 2)) + " (" + restFulStatus.getDescription() + ")");
		if (data != null)
			parameters.put("Data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)));
		return parameters;
	}
}


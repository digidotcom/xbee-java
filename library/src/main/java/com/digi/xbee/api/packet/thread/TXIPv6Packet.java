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
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a TX (Transmit) IPv6 packet. Packet is built
 * using the parameters of the constructor or providing a valid API payload.
 *
 * @see RXIPv6Packet
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 * 
 * @since 1.2.1
 */
public class TXIPv6Packet extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 24; /* 1 (Frame type) + 1 (frame ID) + 16 (dest address) + 2 (dest port) +
																2 (source port) + 1 (protocol) + 1 (transmit options) */

	private static final String ERROR_PAYLOAD_NULL = "TX IPv6 packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete TX IPv6 packet.";
	private static final String ERROR_NOT_TXIPV6 = "Payload is not a TX IPv6 packet.";
	private static final String ERROR_DEST_ADDR_NULL = "Destination address cannot be null.";
	private static final String ERROR_PROTOCOL_NULL = "Protocol cannot be null.";
	private static final String ERROR_FRAME_ID_ILLEGAL = "Frame ID must be between 0 and 255.";
	private static final String ERROR_PORT_ILLEGAL = "Port must be between 0 and 65535.";

	private static final String OPERATION_EXCEPTION = "Operation not supported in this module.";

	// Variables.
	private Inet6Address destAddress;

	private int destPort;
	private int sourcePort;

	private IPProtocol protocol;

	private byte[] data;

	private Logger logger;

	/**
	 * Creates a new {@code TXIPv6Packet} object from the given payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a TX IPv6 packet ({@code 0x1A}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 *
	 * @return Parsed TX IPv6 packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.TX_IPV6.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static TXIPv6Packet createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.TX_IPV6.getValue())
			throw new IllegalArgumentException(ERROR_NOT_TXIPV6);

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

		// 2 bytes of destination port.
		int destPort = (payload[index] & 0xFF) << 8 | payload[index + 1] & 0xFF;
		index = index + 2;

		// 2 bytes of source port.
		int sourcePort = (payload[index] & 0xFF) << 8 | payload[index + 1] & 0xFF;
		index = index + 2;

		// Protocol byte.
		IPProtocol protocol = IPProtocol.get(payload[index] & 0xFF);
		index = index + 1;

		// Transmit options byte, reserved.
		index = index + 1;

		// Get data.
		byte[] data = null;
		if (index < payload.length)
			data = Arrays.copyOfRange(payload, index, payload.length);

		return new TXIPv6Packet(frameID, destAddress, destPort, sourcePort, protocol, data);
	}

	/**
	 * Class constructor. Instantiates a new {@code TXIPv6Packet} object with
	 * the given parameters.
	 *
	 * @param frameID Frame ID.
	 * @param destAddress IPv6 address of the destination device.
	 * @param destPort Destination port number.
	 * @param sourcePort Source port number.
	 * @param protocol Protocol used for transmitted data.
	 * @param data Transmit data bytes.
	 *
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code destPort < 0} or
	 *                                  if {@code destPort > 65535} or
	 *                                  if {@code sourcePort < 0} or
	 *                                  if {@code sourcePort > 65535}.
	 * @throws NullPointerException if {@code destAddress == null} or
	 *                              if {@code protocol == null}.
	 *
	 * @see IPProtocol
	 * @see java.net.Inet6Address
	 */
	public TXIPv6Packet(int frameID, Inet6Address destAddress, int destPort, int sourcePort,
			IPProtocol protocol, byte[] data) {
		super(APIFrameType.TX_IPV6);

		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException(ERROR_FRAME_ID_ILLEGAL);
		if (destPort < 0 || destPort > 65535)
			throw new IllegalArgumentException(ERROR_PORT_ILLEGAL);
		if (sourcePort < 0 || sourcePort > 65535)
			throw new IllegalArgumentException(ERROR_PORT_ILLEGAL);
		if (destAddress == null)
			throw new NullPointerException(ERROR_DEST_ADDR_NULL);
		if (protocol == null)
			throw new NullPointerException(ERROR_PROTOCOL_NULL);

		this.frameID = frameID;
		this.destAddress = destAddress;
		this.destPort = destPort;
		this.sourcePort = sourcePort;
		this.protocol = protocol;
		this.data = data;
		this.logger = LoggerFactory.getLogger(TXIPv6Packet.class);
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
			os.write(ByteUtils.shortToByteArray((short)destPort));
			os.write(ByteUtils.shortToByteArray((short)sourcePort));
			os.write(protocol.getID() & 0xFF);
			os.write(0x00); // Transmit options byte is always 0 (Reserved).
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
			throw new IllegalArgumentException(ERROR_PORT_ILLEGAL);

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
			throw new IllegalArgumentException(ERROR_PORT_ILLEGAL);

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
	 * Retrieves the transmit options.
	 *
	 * @return Transmit options.
	 */
	public int getTransmitOptions() {
		return 0x00; // Transmit options byte is always 0, reserved.
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

	/**
	 * Sets the new transmission data.
	 *
	 * @param data The transmission data.
	 *
	 * @see #getData()
	 */
	public void setData(byte[] data) {
		if (data == null)
			this.data = null;
		else
			this.data = Arrays.copyOf(data, data.length);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Destination address", HexUtils.prettyHexString(destAddress.getAddress()) + " (" + destAddress.getHostAddress() + ")");
		parameters.put("Destination port", HexUtils.prettyHexString(HexUtils.integerToHexString(destPort, 2)) + " (" + destPort + ")");
		parameters.put("Source port", HexUtils.prettyHexString(HexUtils.integerToHexString(sourcePort, 2)) + " (" + sourcePort + ")");
		parameters.put("Protocol", HexUtils.prettyHexString(HexUtils.integerToHexString(protocol.getID(), 1)) + " (" + protocol.getName() + ")");
		parameters.put("Transmit options", "00 (Reserved)");
		if (data != null)
			parameters.put("Data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)));
		return parameters;
	}
}

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
package com.digi.xbee.api.packet.ip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.IPDevice;
import com.digi.xbee.api.models.IPProtocol;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents an RX (Receive) IPv4 packet. Packet is built
 * using the parameters of the constructor or providing a valid API payload.
 *
 * @see TXIPv4Packet
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 * 
 * @since 1.2.0
 */
public class RXIPv4Packet extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 11; /* 1 (Frame type) + 4 (source address) + 2 (dest port) +
																2 (source port) + 1 (protocol) + 1 (status) */

	private static final String ERROR_PAYLOAD_NULL = "RX IPv4 packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete RX IPv4 packet.";
	private static final String ERROR_NOT_RXIPV4 = "Payload is not a RX IPv4 packet.";
	private static final String ERROR_SOURCE_ADDR_NULL = "Source address cannot be null.";
	private static final String ERROR_PROTOCOL_NULL = "Protocol cannot be null.";
	private static final String ERROR_PORT_ILLEGAL = "Port must be between 0 and 65535.";

	// Variables.
	private Inet4Address sourceAddress;

	private int destPort;
	private int sourcePort;

	private IPProtocol protocol;

	private byte[] data;

	private Logger logger;

	/**
	 * Creates a new {@code RXIPv4Packet} object from the given payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a RX IPv4 packet ({@code 0xB0}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 *
	 * @return Parsed RX IPv4 packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.RX_IPV4.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static RXIPv4Packet createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.RX_IPV4.getValue())
			throw new IllegalArgumentException(ERROR_NOT_RXIPV4);

		// payload[0] is the frame type.
		int index = 1;

		// 4 bytes of IP source address.
		Inet4Address sourceAddress;
		try {
			sourceAddress = (Inet4Address) Inet4Address.getByAddress(Arrays.copyOfRange(payload, index, index + 4));
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException(e);
		}
		index = index + 4;

		// 2 bytes of destination port.
		int destPort = (payload[index] & 0xFF) << 8 | payload[index + 1] & 0xFF;
		index = index + 2;

		// 2 bytes of source port.
		int sourcePort = (payload[index] & 0xFF) << 8 | payload[index + 1] & 0xFF;
		index = index + 2;

		// Protocol byte.
		IPProtocol protocol = IPProtocol.get(payload[index] & 0xFF);
		index = index + 1;

		// Status byte, reserved.
		index = index + 1;

		// Get data.
		byte[] data = null;
		if (index < payload.length)
			data = Arrays.copyOfRange(payload, index, payload.length);

		return new RXIPv4Packet(sourceAddress, destPort, sourcePort, protocol, data);
	}

	/**
	 * Class constructor. Instantiates a new {@code RXIPv4Packet} object with
	 * the given parameters.
	 *
	 * @param sourceAddress IP address of the source device.
	 * @param destPort Destination port number.
	 * @param sourcePort Source port number.
	 * @param protocol Protocol used for transmitted data.
	 * @param data Receive data bytes.
	 *
	 * @throws IllegalArgumentException if {@code destPort < 0} or
	 *                                  if {@code destPort > 65535} or
	 *                                  if {@code sourcePort < 0} or
	 *                                  if {@code sourcePort > 65535} or
	 *                                  if {@code transmitOptions} are invalid.
	 * @throws NullPointerException if {@code destAddress == null} or
	 *                              if {@code protocol == null}.
	 *
	 * @see IPProtocol
	 * @see java.net.Inet4Address
	 */
	public RXIPv4Packet(Inet4Address sourceAddress, int destPort,
			int sourcePort, IPProtocol protocol, byte[] data) {
		super(APIFrameType.RX_IPV4);

		if (destPort < 0 || destPort > 65535)
			throw new IllegalArgumentException(ERROR_PORT_ILLEGAL);
		if (sourcePort < 0 || sourcePort > 65535)
			throw new IllegalArgumentException(ERROR_PORT_ILLEGAL);
		if (sourceAddress == null)
			throw new NullPointerException(ERROR_SOURCE_ADDR_NULL);
		if (protocol == null)
			throw new NullPointerException(ERROR_PROTOCOL_NULL);

		this.sourceAddress = sourceAddress;
		this.destPort = destPort;
		this.sourcePort = sourcePort;
		this.protocol = protocol;
		this.data = data;
		this.logger = LoggerFactory.getLogger(RXIPv4Packet.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(sourceAddress.getAddress());
			os.write(destPort >> 8);
			os.write(destPort);
			os.write(sourcePort >> 8);
			os.write(sourcePort);
			os.write(protocol.getID());
			os.write(0x00); // Status byte, reserved.
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
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#isBroadcast()
	 */
	@Override
	public boolean isBroadcast() {
		return sourceAddress.getHostAddress().equals(IPDevice.BROADCAST_IP);
	}

	/**
	 * Retrieves the source IP address.
	 *
	 * @return The source IP address.
	 *
	 * @see #setSourceAddress(Inet4Address)
	 * @see java.net.Inet4Address
	 */
	public Inet4Address getSourceAddress() {
		return sourceAddress;
	}

	/**
	 * Sets the destination IP address.
	 *
	 * @param sourceAddress The new destination IP address.
	 *
	 * @throws NullPointerException if {@code destAddress == null}.
	 *
	 * @see #getSourceAddress()
	 * @see java.net.Inet4Address
	 */
	public void setSourceAddress(Inet4Address sourceAddress) {
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
	 * Retrieves the received data.
	 *
	 * @return The received data.
	 *
	 * @see #setData(byte[])
	 */
	public byte[] getData() {
		if (data == null)
			return null;
		return Arrays.copyOf(data, data.length);
	}

	/**
	 * Sets the new received data.
	 *
	 * @param data The received data.
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
		parameters.put("Source address", HexUtils.prettyHexString(sourceAddress.getAddress()) + " (" + sourceAddress.getHostAddress() + ")");
		parameters.put("Destination port", HexUtils.prettyHexString(HexUtils.integerToHexString(destPort, 2)) + " (" + destPort + ")");
		parameters.put("Source port", HexUtils.prettyHexString(HexUtils.integerToHexString(sourcePort, 2)) + " (" + sourcePort + ")");
		parameters.put("Protocol", HexUtils.prettyHexString(HexUtils.integerToHexString(protocol.getID(), 1)) + " (" + protocol.getName() + ")");
		parameters.put("Status", HexUtils.prettyHexString(HexUtils.integerToHexString(0x00, 1)) + " (Reserved)"); // Status byte is always 0.
		if (data != null)
			parameters.put("Data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)));
		return parameters;
	}
}

/**
 * Copyright (c) 2016 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api.packet.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.IP32BitAddress;
import com.digi.xbee.api.models.NetworkProtocol;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents an RX (Receive) IPv4 packet. Packet is built
 * using the parameters of the constructor or providing a valid API payload.
 *
 * @see TXIPv4Packet
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 */
public class RXIPv4Packet extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 11; /* 1 (Frame type) + 4 (dest address) + 2 (dest port) +
																2 (source port) + 1 (protocol) + 1 (status) */

	private static final String ERROR_PAYLOAD_NULL = "RX IPv4 packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete RX IPv4 packet.";
	private static final String ERROR_NOT_RXIPV4 = "Payload is not a RX IPv4 packet.";
	private static final String ERROR_DEST_ADDR_NULL = "Destination address cannot be null.";
	private static final String ERROR_PROTOCOL_NULL = "Protocol cannot be null.";
	private static final String ERROR_PORT_ILLEGAL = "Port must be between 0 and 65535.";

	// Variables.
	private IP32BitAddress destAddress;

	private int destPort;
	private int sourcePort;

	private NetworkProtocol protocol;

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

		// 4 bytes of IP 32-bit destination address.
		IP32BitAddress destAddress = new IP32BitAddress(Arrays.copyOfRange(payload, index, index + 4));
		index = index + 4;

		// 2 bytes of destination port.
		int destPort = (payload[index] & 0xFF) << 8 | payload[index + 1] & 0xFF;
		index = index + 2;

		// 2 bytes of source port.
		int sourcePort = (payload[index] & 0xFF) << 8 | payload[index + 1] & 0xFF;
		index = index + 2;

		// Protocol byte.
		NetworkProtocol protocol = NetworkProtocol.get(payload[index] & 0xFF);
		index = index + 1;

		// Status byte, reserved.
		index = index + 1;

		// Get data.
		byte[] data = null;
		if (index < payload.length)
			data = Arrays.copyOfRange(payload, index, payload.length);

		return new RXIPv4Packet(destAddress, destPort, sourcePort, protocol, data);
	}

	/**
	 * Class constructor. Instantiates a new {@code RXIPv4Packet} object with
	 * the given parameters.
	 *
	 * @param destAddress 32-bit IP address of the destination device.
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
	 * @see IP32BitAddress
	 * @see NetworkProtocol
	 */
	public RXIPv4Packet(IP32BitAddress destAddress, int destPort, int sourcePort,
			NetworkProtocol protocol, byte[] data) {
		super(APIFrameType.RX_IPV4);

		if (destPort < 0 || destPort > 65535)
			throw new IllegalArgumentException(ERROR_PORT_ILLEGAL);
		if (sourcePort < 0 || sourcePort > 65535)
			throw new IllegalArgumentException(ERROR_PORT_ILLEGAL);
		if (destAddress == null)
			throw new NullPointerException(ERROR_DEST_ADDR_NULL);
		if (protocol == null)
			throw new NullPointerException(ERROR_PROTOCOL_NULL);

		this.destAddress = destAddress;
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
			os.write(destAddress.getValue());
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
		return false;
	}

	/**
	 * Retrieves the 32-bit destination IP address.
	 *
	 * @return The 32-bit destination IP address.
	 *
	 * @see #setDestAddress(IP32BitAddress)
	 * @see IP32BitAddress
	 */
	public IP32BitAddress getDestAddress() {
		return destAddress;
	}

	/**
	 * Sets the 32-bit destination IP address.
	 *
	 * @param destAddress The new 32-bit destination IP address.
	 *
	 * @throws NullPointerException if {@code destAddress == null}.
	 *
	 * @see #getDestAddress()
	 * @see IP32BitAddress
	 */
	public void setDestAddress(IP32BitAddress destAddress) {
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
	 * Retrieves the network protocol.
	 *
	 * @return The network protocol.
	 *
	 * @see #setProtocol(NetworkProtocol)
	 * @see NetworkProtocol
	 */
	public NetworkProtocol getProtocol() {
		return protocol;
	}

	/**
	 * Sets the network protocol.
	 *
	 * @param protocol The new network protocol.
	 *
	 * @throws NullPointerException if {@code protocol == null}.
	 *
	 * @see #getProtocol()
	 * @see NetworkProtocol
	 */
	public void setProtocol(NetworkProtocol protocol) {
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
		parameters.put("Destination address", destAddress.toString());
		parameters.put("Destination port", String.valueOf(destPort));
		parameters.put("Source port", String.valueOf(sourcePort));
		parameters.put("Protocol", protocol.getName());
		if (data != null)
			parameters.put("Data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)));
		return parameters;
	}
}

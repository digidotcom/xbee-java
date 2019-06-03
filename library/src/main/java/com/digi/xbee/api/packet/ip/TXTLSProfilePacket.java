/**
 * Copyright 2019, Digi International Inc.
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
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a TX (Transmit) request with TLS profile packet.
 * The packet implies the use of TLS and behaves similar to the TX Request
 * (0x20) frame, with the protocol field replaced with a TLS Profile field to
 * choose from the profiles configured with the $0, $1, and $2 configuration 
 * commands. Packet is built using the parameters of the constructor or
 * providing a valid API payload.
 *
 * @see TXIPv4Packet
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 * 
 * @since 1.3.0
 */
public class TXTLSProfilePacket extends XBeeAPIPacket {

	// Constants.
	/** This option will close the socket after the transmission. */
	public static final int OPTIONS_CLOSE_SOCKET = TXIPv4Packet.OPTIONS_CLOSE_SOCKET;
	/** This option will leave socket open after the transmission. */
	public static final int OPTIONS_LEAVE_SOCKET_OPEN = TXIPv4Packet.OPTIONS_LEAVE_SOCKET_OPEN;

	private static final int MIN_API_PAYLOAD_LENGTH = 12; /* 1 (Frame type) + 1 (frame ID) + 4 (dest address) + 2 (dest port) +
																2 (source port) + 1 (profile) + 1 (transmit options) */

	private static final String ERROR_PAYLOAD_NULL = "TX request with TLS profile packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete TX request with TLS profile packet.";
	private static final String ERROR_NOT_TXTLS = "Payload is not a TX request with TLS profile packet.";
	private static final String ERROR_DEST_ADDR_NULL = "Destination address cannot be null.";
	private static final String ERROR_FRAME_ID_ILLEGAL = "Frame ID must be between 0 and 255.";
	private static final String ERROR_PORT_ILLEGAL = "Port must be between 0 and 65535.";
	private static final String ERROR_PROFILE_ILLEGAL = "Profile must be between 0 and 255.";
	private static final String ERROR_OPTIONS_INVALID = "Transmit options can only be " + OPTIONS_CLOSE_SOCKET +
			" or " + OPTIONS_LEAVE_SOCKET_OPEN + ".";

	// Variables.
	private Inet4Address destAddress;

	private int destPort;
	private int sourcePort;
	private int transmitOptions;
	private int profile;

	private byte[] data;

	private Logger logger;

	/**
	 * Creates a new {@code TXTLSProfilePacket} object from the given payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a TX request with TLS profile packet
	 *                ({@code 0x23}). The byte array must be in
	 *                {@code OperatingMode.API} mode.
	 *
	 * @return Parsed TX request with TLS profile packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.TX_REQUEST_TLS_PROFILE.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static TXTLSProfilePacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.TX_REQUEST_TLS_PROFILE.getValue())
			throw new IllegalArgumentException(ERROR_NOT_TXTLS);

		// payload[0] is the frame type.
		int index = 1;

		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;

		// 4 bytes of IP destination address.
		Inet4Address destAddress;
		try {
			destAddress = (Inet4Address) Inet4Address.getByAddress(Arrays.copyOfRange(payload, index, index + 4));
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

		// Profile byte.
		int profile = payload[index] & 0xFF;
		index = index + 1;

		// Transmit options byte.
		int transmitOptions = payload[index] & 0xFF;
		index = index + 1;

		// Get data.
		byte[] data = null;
		if (index < payload.length)
			data = Arrays.copyOfRange(payload, index, payload.length);

		return new TXTLSProfilePacket(frameID, destAddress, destPort, sourcePort, profile, transmitOptions, data);
	}

	/**
	 * Class constructor. Instantiates a new {@code TXTLSProfilePacket} object
	 * with the given parameters.
	 *
	 * @param frameID Frame ID.
	 * @param destAddress IP address of the destination device.
	 * @param destPort Destination port number.
	 * @param sourcePort Source port number.
	 * @param profile Zero-indexed number that indicates the profile as
	 *                specified by the corresponding $num command.
	 * @param transmitOptions Transmit options bitfield. Can be
	 *                        {@link #OPTIONS_LEAVE_SOCKET_OPEN} or
	 *                        {@link #OPTIONS_CLOSE_SOCKET}.
	 * @param data Transmit data bytes.
	 *
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code destPort < 0} or
	 *                                  if {@code destPort > 65535} or
	 *                                  if {@code sourcePort < 0} or
	 *                                  if {@code sourcePort > 65535} or
	 *                                  if {@code profile < 0} or
	 *                                  if {@code profile > 255} or
	 *                                  if {@code transmitOptions} are invalid.
	 * @throws NullPointerException if {@code destAddress == null}.
	 *
	 * @see java.net.Inet4Address
	 */
	public TXTLSProfilePacket(int frameID, Inet4Address destAddress, int destPort, int sourcePort,
			int profile, int transmitOptions, byte[] data) {
		super(APIFrameType.TX_REQUEST_TLS_PROFILE);

		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException(ERROR_FRAME_ID_ILLEGAL);
		if (destPort < 0 || destPort > 65535)
			throw new IllegalArgumentException(ERROR_PORT_ILLEGAL);
		if (sourcePort < 0 || sourcePort > 65535)
			throw new IllegalArgumentException(ERROR_PORT_ILLEGAL);
		if (transmitOptions != OPTIONS_CLOSE_SOCKET && transmitOptions != OPTIONS_LEAVE_SOCKET_OPEN)
			throw new IllegalArgumentException(ERROR_OPTIONS_INVALID);
		if (profile < 0 || profile > 255)
			throw new IllegalArgumentException(ERROR_PROFILE_ILLEGAL);
		if (destAddress == null)
			throw new NullPointerException(ERROR_DEST_ADDR_NULL);

		this.frameID = frameID;
		this.destAddress = destAddress;
		this.destPort = destPort;
		this.sourcePort = sourcePort;
		this.profile = profile;
		this.transmitOptions = transmitOptions;
		this.data = data;
		this.logger = LoggerFactory.getLogger(TXTLSProfilePacket.class);
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
			os.write(destPort >> 8);
			os.write(destPort);
			os.write(sourcePort >> 8);
			os.write(sourcePort);
			os.write(profile);
			os.write(transmitOptions);
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

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#isBroadcast()
	 */
	@Override
	public boolean isBroadcast() {
		return destAddress.getHostAddress().equals(IPDevice.BROADCAST_IP);
	}

	/**
	 * Returns the destination IP address.
	 *
	 * @return The destination IP address.
	 *
	 * @see #setDestAddress(Inet4Address)
	 * @see java.net.Inet4Address
	 */
	public Inet4Address getDestAddress() {
		return destAddress;
	}

	/**
	 * Sets the destination IP address.
	 *
	 * @param destAddress The new destination IP address.
	 *
	 * @throws NullPointerException if {@code destAddress == null}.
	 *
	 * @see #getDestAddress()
	 * @see java.net.Inet4Address
	 */
	public void setDestAddress(Inet4Address destAddress) {
		if (destAddress == null)
			throw new NullPointerException(ERROR_DEST_ADDR_NULL);

		this.destAddress = destAddress;
	}

	/**
	 * Returns the destination port.
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
	 * Returns the source port.
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
	 * Returns the TLS profile.
	 *
	 * @return The TLS profile.
	 *
	 * @see #setProfile(int)
	 */
	public int getProfile() {
		return profile;
	}

	/**
	 * Sets the TLS profile.
	 *
	 * @param profile The new TLS profile.
	 * 
	 * @throws IllegalArgumentException if {@code profile < 0} or
	 *                                  if {@code profile > 255}.
	 * 
	 * @see #getProfile()
	 */
	public void setProfile(int profile) {
		if (profile < 0 || profile > 255)
			throw new IllegalArgumentException(ERROR_PROFILE_ILLEGAL);
		this.profile = profile;;
	}

	/**
	 * Returns the transmit options.
	 *
	 * @return Transmit options.
	 *
	 * @see #OPTIONS_CLOSE_SOCKET
	 * @see #OPTIONS_LEAVE_SOCKET_OPEN
	 * @see #setTransmitOptions(int)
	 */
	public int getTransmitOptions() {
		return transmitOptions;
	}

	/**
	 * Sets the transmit options.
	 *
	 * @param transmitOptions Transmit options. Can be
	 *                        {@link #OPTIONS_CLOSE_SOCKET} or
	 *                        {@link #OPTIONS_LEAVE_SOCKET_OPEN}.
	 *
	 * @throws IllegalArgumentException if {@code transmitOptions} are invalid.
	 *
	 * @see #OPTIONS_CLOSE_SOCKET
	 * @see #OPTIONS_LEAVE_SOCKET_OPEN
	 * @see #getTransmitOptions()
	 */
	public void setTransmitOptions(int transmitOptions) {
		if (transmitOptions != OPTIONS_CLOSE_SOCKET && transmitOptions != OPTIONS_LEAVE_SOCKET_OPEN)
			throw new IllegalArgumentException(ERROR_OPTIONS_INVALID);

		this.transmitOptions = transmitOptions;
	}

	/**
	 * Returns the transmission data.
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
		parameters.put("Profile", HexUtils.prettyHexString(HexUtils.integerToHexString(profile, 1)) + " (" + profile + ")");
		parameters.put("Transmit options", HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1)));
		if (data != null)
			parameters.put("Data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(data)));
		return parameters;
	}
}

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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.net.Inet6Address;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.ATStringCommands;
import com.digi.xbee.api.models.RemoteATCommandOptions;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents an IPv6 Remote AT Command Request packet. Packet is 
 * built using the parameters of the constructor.
 * 
 * <p>Used to query or set module parameters on a remote device identified by 
 * an IPv6 address. For parameter changes on the remote device to take effect, 
 * changes must be applied, either by setting the apply changes options bit, 
 * or by sending an AC command to the remote.</p>
 * 
 * <p>Remote Command options are set as a bitfield.</p>
 * 
 * <p>If configured, command response is received as an IPv6 Remote AT Command 
 * Response packet. See {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandResponsePacket}.</p>
 * 
 * @see IPv6RemoteATCommandResponsePacket
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 * 
 * @since 1.2.1
 */
public class IPv6RemoteATCommandRequestPacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 21; // 1 (Frame type) + 1 (frame ID) + 16 (IPv6 destination address) + 1 (transmit options byte) + 2 (AT command)

	private static final String ERROR_PAYLOAD_NULL = "IPv6 Remote AT command packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete IPv6 Remote AT command packet.";
	private static final String ERROR_NOT_REMOTE_IPV6_REQ = "Payload is not an IPv6 Remote AT command packet.";
	private static final String ERROR_DEST_ADDR_NULL = "Destination address cannot be null.";
	private static final String ERROR_AT_CMD_NULL = "AT command cannot be null.";
	private static final String ERROR_FRAME_ID_ILLEGAL = "Frame ID must be between 0 and 255.";
	private static final String ERROR_OPTIONS_INVALID = "Transmit options can only be " + RemoteATCommandOptions.OPTION_APPLY_CHANGES +
			" or " + RemoteATCommandOptions.OPTION_NONE + ".";

	private static final String OPERATION_EXCEPTION = "Operation not supported in this module.";

	// Variables.
	private final Inet6Address destAddress;

	private final int transmitOptions;

	private final String command;

	private byte[] parameter;

	private Logger logger;

	/**
	 * Creates a new {@code IPv6RemoteATCommandRequestPacket} object from the given 
	 * payload.
	 * 
	 * @param payload The API frame payload. It must start with the frame type 
	 *                corresponding to an IPv6 Remote AT Command packet ({@code 0x1B}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 * 
	 * @return Parsed IPv6 Remote AT Command Request packet.
	 * 
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.IPV6_REMOTE_AT_COMMAND_REQUEST.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH} or
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static IPv6RemoteATCommandRequestPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		// 1 (Frame type) + 1 (frame ID) + 16 (IPv6 destination address) + 1 (transmit options byte) + 2 (AT command)
		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.IPV6_REMOTE_AT_COMMAND_REQUEST.getValue())
			throw new IllegalArgumentException(ERROR_NOT_REMOTE_IPV6_REQ);

		// payload[0] is the frame type.
		int index = 1;

		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;

		// 16 bytes of IPv6 destination address.
		Inet6Address destAddress;
		try {
			destAddress = (Inet6Address) Inet6Address.getByAddress(Arrays.copyOfRange(payload, index, index + 16));
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException(e);
		}
		index = index + 16;

		// Options byte.
		int transmitOptions = payload[index] & 0xFF;
		index = index + 1;

		// 2 bytes of AT command.
		String command = new String(new byte[]{payload[index], payload[index + 1]});
		index = index + 2;

		// Get data.
		byte[] parameterData = null;
		if (index < payload.length)
			parameterData = Arrays.copyOfRange(payload, index, payload.length);

		return new IPv6RemoteATCommandRequestPacket(frameID, destAddress, transmitOptions, command, parameterData);
	}

	/**
	 * Class constructor. Instantiates a new {@code IPv6RemoteATCommandRequestPacket}
	 * object with the given parameters.
	 * 
	 * @param frameID The Frame ID.
	 * @param destAddress IPv6 address of the destination device.
	 * @param transmitOptions Bitfield of supported transmission options.
	 * @param command AT command.
	 * @param parameter AT command parameter as String.
	 * 
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code transmitOptions != }{@value RemoteATCommandOptions#OPTION_APPLY_CHANGES} or
	 *                                  if {@code transmitOptions != }{@value RemoteATCommandOptions#OPTION_NONE}.
	 * @throws NullPointerException if {@code destAddress == null} or
	 *                              if {@code command == null}.
	 * 
	 * @see java.net.Inet6Address
	 * @see com.digi.xbee.api.models.RemoteATCommandOptions
	 */
	public IPv6RemoteATCommandRequestPacket(int frameID, Inet6Address destAddress, 
			int transmitOptions, String command, String parameter) {
		super(APIFrameType.IPV6_REMOTE_AT_COMMAND_REQUEST);

		if (destAddress == null)
			throw new NullPointerException(ERROR_DEST_ADDR_NULL);
		if (command == null)
			throw new NullPointerException(ERROR_AT_CMD_NULL);
		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException(ERROR_FRAME_ID_ILLEGAL);
		if (transmitOptions != RemoteATCommandOptions.OPTION_APPLY_CHANGES && transmitOptions != RemoteATCommandOptions.OPTION_NONE)
			throw new IllegalArgumentException(ERROR_OPTIONS_INVALID);

		this.frameID = frameID;
		this.destAddress = destAddress;
		this.transmitOptions = transmitOptions;
		this.command = command;
		if (parameter != null)
			this.parameter = parameter.getBytes();
		this.logger = LoggerFactory.getLogger(IPv6RemoteATCommandRequestPacket.class);
	}

	/**
	 * Class constructor. Instantiates a new {@code IPv6RemoteATCommandRequestPacket} 
	 * object with the given parameters.
	 * 
	 * @param frameID Frame ID.
	 * @param destAddress IPv6 address of the destination device.
	 * @param transmitOptions Bitfield of supported transmission options.
	 * @param command AT command.
	 * @param parameter AT command parameter as byte array.
	 * 
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code transmitOptions != }{@value RemoteATCommandOptions#OPTION_APPLY_CHANGES} or
	 *                                  if {@code transmitOptions != }{@value RemoteATCommandOptions#OPTION_NONE}.
	 * @throws NullPointerException if {@code destAddress == null} or
	 *                              if {@code command == null}.
	 * 
	 * @see java.net.Inet6Address
	 * @see com.digi.xbee.api.models.RemoteATCommandOptions
	 */
	public IPv6RemoteATCommandRequestPacket(int frameID, Inet6Address destAddress, 
			int transmitOptions, String command, byte[] parameter) {
		super(APIFrameType.IPV6_REMOTE_AT_COMMAND_REQUEST);

		if (destAddress == null)
			throw new NullPointerException(ERROR_DEST_ADDR_NULL);
		if (command == null)
			throw new NullPointerException(ERROR_AT_CMD_NULL);
		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException(ERROR_FRAME_ID_ILLEGAL);
		if (transmitOptions != RemoteATCommandOptions.OPTION_APPLY_CHANGES && transmitOptions != RemoteATCommandOptions.OPTION_NONE)
			throw new IllegalArgumentException(ERROR_OPTIONS_INVALID);

		this.frameID = frameID;
		this.destAddress = destAddress;
		this.transmitOptions = transmitOptions;
		this.command = command;
		this.parameter = parameter;
		this.logger = LoggerFactory.getLogger(IPv6RemoteATCommandRequestPacket.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try {
			data.write(destAddress.getAddress());
			data.write(transmitOptions & 0xFF);
			data.write(ByteUtils.stringToByteArray(command));
			if (parameter != null)
				data.write(parameter);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return data.toByteArray();
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
	 * @see java.net.Inet6Address
	 */
	public Inet6Address getDestAddress() {
		return destAddress;
	}

	/**
	 * Returns the transmit options bitfield.
	 * 
	 * @return Transmit options bitfield.
	 * 
	 * @see com.digi.xbee.api.models.RemoteATCommandOptions
	 */
	public int getTransmitOptions() {
		return transmitOptions;
	}

	/**
	 * Returns the AT Command.
	 * 
	 * @return The AT Command.
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Sets the AT command parameter as String.
	 * 
	 * @param parameter The AT command parameter as String.
	 * 
	 * @see #getParameter()
	 * @see #getParameterAsString()
	 * @see #setParameter(byte[])
	 */
	public void setParameter(String parameter) {
		if (parameter == null)
			this.parameter = null;
		else
			this.parameter = parameter.getBytes();
	}

	/**
	 * Sets the AT command parameter as byte array.
	 * 
	 * @param parameter The AT command parameter as byte array.
	 * 
	 * @see #getParameter()
	 * @see #getParameterAsString()
	 * @see #setParameter(String)
	 */
	public void setParameter(byte[] parameter) {
		this.parameter = parameter;
	}

	/**
	 * Returns the AT command parameter as byte array.
	 * 
	 * @return The AT command parameter as byte array.
	 * 
	 * @see #getParameterAsString()
	 * @see #setParameter(String parameter)
	 * @see #setParameter(byte[] parameter)
	 */
	public byte[] getParameter() {
		return parameter;
	}

	/**
	 * Returns the AT command parameter as String.
	 * 
	 * @return The AT command parameter as String, {@code null} if not 
	 *         parameter is set.
	 * 
	 * @see #getParameter()
	 * @see #setParameter(String parameter)
	 * @see #setParameter(byte[] parameter)
	 */
	public String getParameterAsString() {
		if (parameter == null)
			return null;
		return new String(parameter);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Destination address", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(destAddress.getAddress())) + 
				" (" + destAddress.getHostAddress() + ")");
		parameters.put("Command options", HexUtils.prettyHexString(HexUtils.integerToHexString(transmitOptions, 1)));
		parameters.put("AT Command", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(command.getBytes())) + " (" + command + ")");
		if (parameter != null) {
			if (ATStringCommands.get(command) != null)
				parameters.put("Parameter", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(parameter)) + " (" + new String(parameter) + ")");
			else
				parameters.put("Parameter", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(parameter)));
		}
		return parameters;
	}
}

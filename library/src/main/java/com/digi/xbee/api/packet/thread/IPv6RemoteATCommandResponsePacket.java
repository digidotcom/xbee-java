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

import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.ATStringCommands;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents an IPv6 Remote AT Command Response packet. Packet is 
 * built using the parameters of the constructor.
 * 
 * <p>If a module receives a remote command response RF data frame in response 
 * to an IPv6 Remote AT Command Request, the module will send an IPv6 Remote 
 * AT Command Response message out the UART.</p>
 * 
 * <p>Remote Command options are set as a bitfield.</p>
 * 
 * <p>This packet is received in response of the IPv6 Remote AT Command Request 
 * packet. See {@link com.digi.xbee.api.packet.thread.IPv6RemoteATCommandRequestPacket}.</p>
 * 
 * <p>Response also returns a field with its status. See 
 * {@link com.digi.xbee.api.models.ATCommandStatus}.</p> 
 * 
 * @see IPv6RemoteATCommandRequestPacket
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 * 
 * @since 1.2.1
 */
public class IPv6RemoteATCommandResponsePacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 21; // 1 (Frame type) + 1 (frame ID) + 16 (IPv6 source address)  + 2 (AT command) + 1 (status)

	private static final String ERROR_PAYLOAD_NULL = "IPv6 Remote AT command response packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete IPv6 Remote AT command response packet.";
	private static final String ERROR_NOT_REMOTE_IPV6_RESP = "Payload is not an IPv6 Remote AT command response packet.";
	private static final String ERROR_SOURCE_ADDR_NULL = "Source address cannot be null.";
	private static final String ERROR_AT_CMD_NULL = "AT command cannot be null.";
	private static final String ERROR_STATUS_NULL = "AT command status cannot be null.";
	private static final String ERROR_FRAME_ID_ILLEGAL = "Frame ID must be between 0 and 255.";

	private static final String OPERATION_EXCEPTION = "Operation not supported in this module.";

	// Variables.
	private final Inet6Address sourceAddress;

	private final String command;

	private final ATCommandStatus status;

	private byte[] commandValue;

	private Logger logger;

	/**
	 * Creates a new {@code IPv6RemoteATCommandResponsePacket} object from the given 
	 * payload.
	 * 
	 * @param payload The API frame payload. It must start with the frame type 
	 *                corresponding to an IPv6 Remote AT Command response packet ({@code 0x9B}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 * 
	 * @return Parsed IPv6 Remote AT Command Response packet.
	 * 
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.IPV6_REMOTE_AT_COMMAND_RESPONSE.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH} or
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static IPv6RemoteATCommandResponsePacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		// 1 (Frame type) + 1 (frame ID) + 16 (IPv6 source address)  + 2 (AT command) + 1 (status)
		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.IPV6_REMOTE_AT_COMMAND_RESPONSE.getValue())
			throw new IllegalArgumentException(ERROR_NOT_REMOTE_IPV6_RESP);

		// payload[0] is the frame type.
		int index = 1;

		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;

		// 16 bytes of IPv6 source address.
		Inet6Address sourceAddress;
		try {
			sourceAddress = (Inet6Address) Inet6Address.getByAddress(Arrays.copyOfRange(payload, index, index + 16));
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException(e);
		}
		index = index + 16;

		// 2 bytes of AT command.
		String command = new String(new byte[]{payload[index], payload[index + 1]});
		index = index + 2;

		// Status byte.
		int status = payload[index] & 0xFF;
		index = index + 1;

		// Get data.
		byte[] commandData = null;
		if (index < payload.length)
			commandData = Arrays.copyOfRange(payload, index, payload.length);

		// TODO if ATCommandStatus is unknown????
		return new IPv6RemoteATCommandResponsePacket(frameID, sourceAddress, command,
				ATCommandStatus.get(status), commandData);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code IPv6RemoteATCommandResponsePacket}
	 * object with the given parameters.
	 * 
	 * @param frameID The Frame ID.
	 * @param sourceAddress IPv6 address of the source device.
	 * @param command AT command.
	 * @param status The command status.
	 * @param commandValue The AT command response value as String.
	 * 
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 * @throws NullPointerException if {@code sourceAddress == null} or
	 *                              if {@code command == null} or
	 *                              if {@code status == null}.
	 * 
	 * @see java.net.Inet6Address
	 * @see com.digi.xbee.api.models.ATCommandStatus
	 */
	public IPv6RemoteATCommandResponsePacket(int frameID, Inet6Address sourceAddress, 
			String command, ATCommandStatus status, String commandValue) {
		super(APIFrameType.IPV6_REMOTE_AT_COMMAND_RESPONSE);

		if (sourceAddress == null)
			throw new NullPointerException(ERROR_SOURCE_ADDR_NULL);
		if (command == null)
			throw new NullPointerException(ERROR_AT_CMD_NULL);
		if (status == null)
			throw new NullPointerException(ERROR_STATUS_NULL);
		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException(ERROR_FRAME_ID_ILLEGAL);

		this.frameID = frameID;
		this.sourceAddress = sourceAddress;
		this.command = command;
		this.status = status;
		if (commandValue != null)
			this.commandValue = commandValue.getBytes();
		this.logger = LoggerFactory.getLogger(IPv6RemoteATCommandResponsePacket.class);
	}

	/**
	 * Class constructor. Instantiates a new {@code IPv6RemoteATCommandResponsePacket}
	 * object with the given parameters.
	 * 
	 * @param frameID The Frame ID.
	 * @param sourceAddress IPv6 address of the source device.
	 * @param command AT command.
	 * @param status The command status.
	 * @param commandValue The AT command response value.
	 * 
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 * @throws NullPointerException if {@code sourceAddress == null} or
	 *                              if {@code command == null} or
	 *                              if {@code status == null}.
	 * 
	 * @see java.net.Inet6Address
	 * @see com.digi.xbee.api.models.ATCommandStatus
	 */
	public IPv6RemoteATCommandResponsePacket(int frameID, Inet6Address sourceAddress, 
			String command, ATCommandStatus status, byte[] commandValue) {
		super(APIFrameType.IPV6_REMOTE_AT_COMMAND_RESPONSE);

		if (sourceAddress == null)
			throw new NullPointerException(ERROR_SOURCE_ADDR_NULL);
		if (command == null)
			throw new NullPointerException(ERROR_AT_CMD_NULL);
		if (status == null)
			throw new NullPointerException(ERROR_STATUS_NULL);
		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException(ERROR_FRAME_ID_ILLEGAL);

		this.frameID = frameID;
		this.sourceAddress = sourceAddress;
		this.command = command;
		this.status = status;
		this.commandValue = commandValue;
		this.logger = LoggerFactory.getLogger(IPv6RemoteATCommandResponsePacket.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try {
			data.write(sourceAddress.getAddress());
			data.write(ByteUtils.stringToByteArray(command));
			data.write(status.getId() & 0xFF);
			if (commandValue != null)
				data.write(commandValue);
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
	 * Retrieves the source IPv6 address.
	 *
	 * @return The source IPv6 address.
	 *
	 * @see java.net.Inet6Address
	 */
	public Inet6Address getSourceAddress() {
		return sourceAddress;
	}

	/**
	 * Returns the AT command response status.
	 * 
	 * @return The AT command response status.
	 * 
	 * @see com.digi.xbee.api.models.ATCommandStatus
	 */
	public ATCommandStatus getStatus() {
		return status;
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
	 * Sets the AT command response value as String.
	 * 
	 * @param commandValue The AT command response value as String.
	 * 
	 * @see #getCommandValue()
	 * @see #getCommandValueAsString()
	 */
	public void setCommandValue(String commandValue) {
		if (commandValue == null)
			this.commandValue = null;
		else
			this.commandValue = commandValue.getBytes();
	}

	/**
	 * Sets the AT command response response value as byte array.
	 * 
	 * @param commandValue The AT command response value as byte array.
	 * 
	 * @see #getCommandValue()
	 * @see #getCommandValueAsString()
	 */
	public void setCommandValue(byte[] commandValue) {
		this.commandValue = commandValue;
	}

	/**
	 * Retrieves the AT command response value as byte array.
	 * 
	 * @return The AT command response value as byte array.
	 * 
	 * @see #getCommandValueAsString()
	 * @see #setCommandValue(String commandValue)
	 * @see #setCommandValue(byte[] commandValue)
	 */
	public byte[] getCommandValue() {
		return commandValue;
	}

	/**
	 * Returns the AT command response value as String.
	 * 
	 * @return The AT command response value as String, {@code null} if no 
	 *         value is set.
	 * 
	 * @see #getCommandValue()
	 * @see #setCommandValue(String commandValue)
	 * @see #setCommandValue(byte[] commandValue)
	 */
	public String getCommandValueAsString() {
		if (commandValue == null)
			return null;
		return new String(commandValue);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Source address", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(sourceAddress.getAddress())) + " (" + sourceAddress.getHostAddress() + ")");
		parameters.put("AT Command", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(command.getBytes())) + " (" + command + ")");
		parameters.put("Status", HexUtils.prettyHexString(HexUtils.integerToHexString(status.getId(), 1)) + " (" + status.getDescription() + ")");
		if (commandValue != null) {
			if (ATStringCommands.get(command) != null)
				parameters.put("Response", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(commandValue)) + " (" + new String(commandValue) + ")");
			else
				parameters.put("Response", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(commandValue)));
		}
		return parameters;
	}
}

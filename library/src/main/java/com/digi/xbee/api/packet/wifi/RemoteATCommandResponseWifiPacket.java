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
package com.digi.xbee.api.packet.wifi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.ATStringCommands;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a Remote AT Command Response (Wi-Fi) packet. Packet is
 * built using the parameters of the constructor or providing a valid API
 * payload.
 *
 * <p>If a module receives a remote command response RF data frame in response
 * to a Remote AT Command Request, the module will send a Remote AT Command
 * Response message out the UART. Some commands may send back multiple frames--
 * for example, Node Discover ({@code ND}) command.</p>
 *
 * <p>This packet is received in response of a {@code RemoteATCommandPacket}.
 * </p>
 *
 * <p>Response also includes an {@code ATCommandStatus} object with the status
 * of the AT command.</p>
 *
 * @see RemoteATCommandWifiPacket
 * @see com.digi.xbee.api.models.ATCommandStatus
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 * 
 * @since 1.2.0
 */
public class RemoteATCommandResponseWifiPacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 13; // 1 (Frame type) + 1 (frame ID) + 8 (IP address) + 2 (AT command) + 1 (status)

	private static final String ERROR_PAYLOAD_NULL = "Remote AT Command Response (Wi-Fi) packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete Remote AT Command Response (Wi-Fi) packet.";
	private static final String ERROR_NOT_VALID_PACKET = "Payload is not a Remote AT Command Response (Wi-Fi) packet.";
	private static final String ERROR_FRAME_ID_ILLEGAL = "Frame ID must be between 0 and 255.";
	private static final String ERROR_SOURCE_ADDRESS_NULL = "Source address cannot be null.";
	private static final String ERROR_AT_COMMAND_NULL = "AT command cannot be null.";
	private static final String ERROR_STATUS_NULL = "AT command status cannot be null.";

	// Variables.
	private final Inet4Address sourceAddress;

	private final ATCommandStatus status;

	private final String command;

	private byte[] commandValue;

	private Logger logger;

	/**
	 * Creates an new {@code RemoteATCommandResponseWifiPacket} object from the
	 * given payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a Remote AT Command Response (Wi-Fi)
	 *                packet ({@code 0x87}). The byte array must be in
	 *                {@code OperatingMode.API} mode.
	 *
	 * @return Parsed Remote AT Command Response (Wi-Fi) packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.REMOTE_AT_COMMAND_RESPONSE_WIFI.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static RemoteATCommandResponseWifiPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.REMOTE_AT_COMMAND_RESPONSE_WIFI.getValue())
			throw new IllegalArgumentException(ERROR_NOT_VALID_PACKET);

		// payload[0] is the frame type.
		int index = 1;

		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;

		// 8 bytes of source address.
		Inet4Address sourceAddress;
		try {
			sourceAddress = (Inet4Address) Inet4Address.getByAddress(Arrays.copyOfRange(payload, index + 4, index + 8));
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException(e);
		}
		index = index + 8;

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

		return new RemoteATCommandResponseWifiPacket(frameID, sourceAddress, command,
				ATCommandStatus.get(status), commandData);
	}

	/**
	 * Class constructor. Instantiates a new
	 * {@code RemoteATCommandResponseWifiPacket} object with the given parameters.
	 *
	 * @param frameID frame ID.
	 * @param sourceAddress IP address of the remote radio returning
	 *                      response.
	 * @param command The AT command.
	 * @param status The command status.
	 * @param commandValue The AT command response value.
	 *
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 * @throws NullPointerException if {@code sourceAddress == null} or
	 *                              if {@code command == null} or
	 *                              if {@code status == null}.
	 *
	 * @see com.digi.xbee.api.models.ATCommandStatus
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 * @see java.net.Inet4Address
	 */
	public RemoteATCommandResponseWifiPacket(int frameID, Inet4Address sourceAddress,
			String command, ATCommandStatus status, byte[] commandValue) {
		super(APIFrameType.REMOTE_AT_COMMAND_RESPONSE_WIFI);

		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException(ERROR_FRAME_ID_ILLEGAL);
		if (sourceAddress == null)
			throw new NullPointerException(ERROR_SOURCE_ADDRESS_NULL);
		if (command == null)
			throw new NullPointerException(ERROR_AT_COMMAND_NULL);
		if (status == null)
			throw new NullPointerException(ERROR_STATUS_NULL);

		this.frameID = frameID;
		this.sourceAddress = sourceAddress;
		this.command = command;
		this.status = status;
		this.commandValue = commandValue;
		this.logger = LoggerFactory.getLogger(RemoteATCommandResponseWifiPacket.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(new byte[]{0x00, 0x00, 0x00, 0x00}); // First 4 bytes of the 64-bit source address.
			os.write(sourceAddress.getAddress());
			os.write(ByteUtils.stringToByteArray(command));
			os.write(status.getId());
			if (commandValue != null)
				os.write(commandValue);
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
		return false;
	}

	/**
	 * Returns the IP source address.
	 *
	 * @return The IP source address.
	 *
	 * @see java.net.Inet4Address
	 */
	public Inet4Address getSourceAddress() {
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
	 * Returns the AT command.
	 *
	 * @return The AT command.
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
	 * @see #setCommandValue(byte[])
	 */
	public void setCommandValue(String commandValue) {
		if (commandValue == null)
			this.commandValue = null;
		else
			this.commandValue = commandValue.getBytes();
	}

	/**
	 * Sets the AT response response value.
	 *
	 * @param commandValue The AT command response value.
	 *
	 * @see #getCommandValue()
	 * @see #getCommandValueAsString()
	 * @see #setCommandValue(String)
	 */
	public void setCommandValue(byte[] commandValue) {
		this.commandValue = commandValue;
	}

	/**
	 * Retrieves the AT command response value.
	 *
	 * @return The AT command response value.
	 *
	 * @see #getCommandValueAsString()
	 * @see #setCommandValue(byte[])
	 * @see #setCommandValue(String)
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
	 * @see #setCommandValue(byte[])
	 * @see #setCommandValue(String)
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
		parameters.put("Source address", "00 00 00 00 " + HexUtils.prettyHexString(HexUtils.byteArrayToHexString(sourceAddress.getAddress())) + " (" + sourceAddress.getHostAddress() + ")");
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

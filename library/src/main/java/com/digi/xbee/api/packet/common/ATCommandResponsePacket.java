/**
 * Copyright (c) 2014-2015 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api.packet.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;
import com.digi.xbee.api.models.ATStringCommands;

/**
 * This class represents an AT Command Response packet. Packet is built using 
 * the parameters of the constructor or providing a valid API payload.
 * 
 * <p>In response to an AT Command message, the module will send an AT Command 
 * Response message. Some commands will send back multiple frames (for example, 
 * the ND (Node Discover) command).</p>
 * 
 * <p>This packet is received in response of an {@code ATCommandPacket}.</p>
 * 
 * <p>Response also includes an {@code ATCommandStatus} object with the status 
 * of the AT command.</p>
 * 
 * @see ATCommandPacket
 * @see com.digi.xbee.api.models.ATCommandStatus
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 */
public class ATCommandResponsePacket extends XBeeAPIPacket {
	
	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 5; // 1 (Frame type) + 1 (frame ID) + 2 (AT command) + 1 (status byte)
	
	// Variables.
	private final ATCommandStatus status;
	
	private final String command;
	
	private byte[] commandValue;
	
	private Logger logger;
	
	/**
	 * Creates a new {@code ATCommandResponsePacket} object from the given 
	 * payload.
	 * 
	 * @param payload The API frame payload. It must start with the frame type 
	 *                corresponding to a AT Command Response packet ({@code 0x88}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 * 
	 * @return Parsed AT Command Response packet.
	 * 
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.AT_COMMAND.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH} or
	 *                                  if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static ATCommandResponsePacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException("AT Command Response packet payload cannot be null.");
		
		// 1 (Frame type) + 1 (frame ID) + 2 (AT command) + 1 (status byte)
		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException("Incomplete AT Command Response packet.");
		
		if ((payload[0] & 0xFF) != APIFrameType.AT_COMMAND_RESPONSE.getValue())
			throw new IllegalArgumentException("Payload is not an AT Command Response packet.");
		
		// payload[0] is the frame type.
		int index = 1;
		
		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;
		
		// 2 bytes of AT command, starting at 2nd byte.
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
		return new ATCommandResponsePacket(frameID, ATCommandStatus.get(status), command, commandData);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code ATCommandResponsePacket} 
	 * object with the given parameters.
	 * 
	 * @param frameID The XBee API frame ID.
	 * @param status The AT command response status.
	 * @param command The AT command.
	 * @param commandValue The AT command response value.
	 * 
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 * @throws NullPointerException if {@code status == null} or 
	 *                              if {@code command == null}.
	 * 
	 * @see com.digi.xbee.api.models.ATCommandStatus
	 */
	public ATCommandResponsePacket(int frameID, ATCommandStatus status, String command, byte[] commandValue) {
		super(APIFrameType.AT_COMMAND_RESPONSE);
		
		if (command == null)
			throw new NullPointerException("AT command cannot be null.");
		if (status == null)
			throw new NullPointerException("AT command status cannot be null.");
		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException("Frame ID must be between 0 and 255.");
		
		this.frameID = frameID;
		this.status = status;
		this.command = command;
		this.commandValue = commandValue;
		this.logger = LoggerFactory.getLogger(ATCommandResponsePacket.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(command.getBytes());
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
	
	/**
	 * Returns the AT command response status.
	 * 
	 * @return The AT command response status.
	 * 
	 * @see ATCommandStatus
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
	 */
	public void setCommandValue(String commandValue) {
		if (commandValue == null)
			this.commandValue = null;
		else
			this.commandValue = commandValue.getBytes();
	}
	
	/**
	 * Sets the AT command response value.
	 * 
	 * @param commandValue The AT command response value.
	 */
	public void setCommandValue(byte[] commandValue) {
		this.commandValue = commandValue;
	}
	
	/**
	 * Returns the AT command response value.
	 * 
	 * @return The AT command response value.
	 */
	public byte[] getCommandValue() {
		return commandValue;
	}
	
	/**
	 * Returns the AT command response value as String.
	 * 
	 * @return The AT command response value as String, {@code null} if no 
	 *         value is set.
	 */
	public String getCommandValueAsString() {
		if (commandValue == null)
			return null;
		return new String(commandValue);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#isBroadcast()
	 */
	@Override
	public boolean isBroadcast() {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
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

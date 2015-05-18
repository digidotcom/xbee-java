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
import com.digi.xbee.api.models.ATStringCommands;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a Remote AT Command Response packet. Packet is built 
 * using the parameters of the constructor or providing a valid API payload.
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
 * @see RemoteATCommandPacket
 * @see com.digi.xbee.api.models.ATCommandStatus
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 */
public class RemoteATCommandResponsePacket extends XBeeAPIPacket {
	
	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 15; // 1 (Frame type) + 1 (frame ID) + 8 (32-bit address) + 2 (16-bit address) + 2 (AT command) + 1 (status)
	
	// Variables.
	private final XBee64BitAddress sourceAddress64;
	
	private final XBee16BitAddress sourceAddress16;
	
	private final ATCommandStatus status;
	
	private final String command;
	
	private byte[] commandValue;
	
	private Logger logger;
	
	/**
	 * Creates an new {@code RemoteATCommandResponsePacket} object from the 
	 * given payload.
	 * 
	 * @param payload The API frame payload. It must start with the frame type 
	 *                corresponding to a Remote AT Command Response packet ({@code 0x97}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 * 
	 * @return Parsed Remote AT Command Response packet.
	 * 
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.REMOTE_AT_COMMAND_RESPONSE.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH} or
	 *                                  if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static RemoteATCommandResponsePacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException("Remote AT Command Response packet payload cannot be null.");
		
		// 1 (Frame type) + 1 (frame ID) + 8 (32-bit address) + 2 (16-bit address) + 2 (AT command) + 1 (status)
		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException("Incomplete Remote AT Command Response packet.");
		
		if ((payload[0] & 0xFF) != APIFrameType.REMOTE_AT_COMMAND_RESPONSE.getValue())
			throw new IllegalArgumentException("Payload is not a Remote AT Command Response packet.");
		
		// payload[0] is the frame type.
		int index = 1;
		
		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;
		
		// 8 bytes of 64-bit address.
		XBee64BitAddress sourceAddress64 = new XBee64BitAddress(Arrays.copyOfRange(payload, index, index + 8));
		index = index + 8;
		
		// 2 bytes of 16-bit address.
		XBee16BitAddress sourceAddress16 = new XBee16BitAddress(payload[index] & 0xFF, payload[index + 1] & 0xFF);
		index = index + 2;
		
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
		return new RemoteATCommandResponsePacket(frameID, sourceAddress64, 
				sourceAddress16, command, ATCommandStatus.get(status), commandData);
	}
	
	/**
	 * Class constructor. Instantiates a new 
	 * {@code RemoteATCommandResponsePacket} object with the given parameters.
	 * 
	 * @param frameID frame ID.
	 * @param sourceAddress64 64-bit address of the remote radio returning 
	 *                        response.
	 * @param sourceAddress16 16-bit network address of the remote.
	 * @param command The AT command.
	 * @param status The command status.
	 * @param commandValue The AT command response value.
	 * 
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 * @throws NullPointerException if {@code sourceAddress64 == null} or
	 *                              if {@code sourceAddress16 == null} or
	 *                              if {@code command == null} or
	 *                              if {@code status == null}.
	 * 
	 * @see com.digi.xbee.api.models.ATCommandStatus
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public RemoteATCommandResponsePacket(int frameID, XBee64BitAddress sourceAddress64, XBee16BitAddress sourceAddress16, 
			String command, ATCommandStatus status, byte[] commandValue) {
		super(APIFrameType.REMOTE_AT_COMMAND_RESPONSE);
		
		if (sourceAddress64 == null)
			throw new NullPointerException("64-bit source address cannot be null.");
		if (sourceAddress16 == null)
			throw new NullPointerException("16-bit source address cannot be null.");
		if (command == null)
			throw new NullPointerException("AT command cannot be null.");
		if (status == null)
			throw new NullPointerException("AT command status cannot be null.");
		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException("Frame ID must be between 0 and 255.");
		
		this.frameID = frameID;
		this.sourceAddress64 = sourceAddress64;
		this.sourceAddress16 = sourceAddress16;
		this.command = command;
		this.status = status;
		this.commandValue = commandValue;
		this.logger = LoggerFactory.getLogger(RemoteATCommandResponsePacket.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try {
			data.write(sourceAddress64.getValue());
			data.write(sourceAddress16.getValue());
			data.write(ByteUtils.stringToByteArray(command));
			data.write(status.getId());
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
	 * Returns the 64-bit source address. 
	 * 
	 * @return The 64-bit source address.
	 * 
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public XBee64BitAddress get64bitSourceAddress() {
		return sourceAddress64;
	}
	
	/**
	 * Returns the 16-bit source address.
	 * 
	 * @return The 16-bit source address.
	 * 
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 */
	public XBee16BitAddress get16bitSourceAddress() {
		return sourceAddress16;
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
	 */
	public void setCommandValue(byte[] commandValue) {
		this.commandValue = commandValue;
	}
	
	/**
	 * Retrieves the AT command response value.
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
		parameters.put("64-bit source address", HexUtils.prettyHexString(sourceAddress64.toString()));
		parameters.put("16-bit source address", HexUtils.prettyHexString(sourceAddress16.toString()));
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

/**
* Copyright (c) 2014 Digi International Inc.,
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
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;
import com.digi.xbee.api.models.ATStringCommands;

/**
 * This class represents an AT Command Response packet. Packet is built using the parameters of 
 * the constructor.
 * 
 * In response to an AT Command message, the module will send an AT Command Response message. Some
 * commands will send back multiple frames (for example, the ND (Node Discover) command).
 * 
 * This packet is received in response of the AT Command packet. See {@link com.digi.xbee.packet.common.ATCommandPacket}.
 * 
 * Response also returns a field with its status. See {@link com.digi.xbee.models.ATComandStatus}.
 */
public class ATCommandResponsePacket extends XBeeAPIPacket {
	
	// Variables
	private final ATCommandStatus status;
	
	private final String command;
	
	private byte[] commandData;
	
	private Logger logger;
	
	/**
	 * Class constructor. Instances a new object of type ATCommandResponsePacket
	 * with the given parameters.
	 * 
	 * @param frameID The XBee API frame ID.
	 * @param status The AT command response status. See {@link com.digi.xbee.models.ATCommandStatus}
	 * @param command The AT command.
	 * @param commandData The AT command response data.
	 * 
	 * @throws NullPointerException if {@code status == null} or 
	 *                              if {@code command == null}.
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 */
	public ATCommandResponsePacket(int frameID, ATCommandStatus status, String command, byte[] commandData) {
		super(APIFrameType.AT_COMMAND_RESPONSE);
		
		if (command == null)
			throw new NullPointerException("Command cannot be null.");
		if (status == null)
			throw new NullPointerException("Command status cannot be null.");
		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException("Frame ID must be between 0 and 255.");
		
		this.frameID = frameID;
		this.status = status;
		this.command = command;
		this.commandData = commandData;
		this.logger = LoggerFactory.getLogger(ATCommandResponsePacket.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIData()
	 */
	public byte[] getAPIData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(frameID);
			os.write(command.getBytes());
			os.write(status.getId());
			if (commandData != null)
				os.write(commandData);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return os.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#hasAPIFrameID()
	 */
	public boolean needsAPIFrameID() {
		return true;
	}
	
	/**
	 * Retrieves the AT command response status.
	 * See {@link com.digi.xbee.models.ATCommandStatus}
	 * 
	 * @return The AT command response status.
	 */
	public ATCommandStatus getStatus() {
		return status;
	}
	
	/**
	 * Retrieves the AT response command.
	 * 
	 * @return The AT response command.
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * Sets the AT response command data.
	 * 
	 * @param commandData The AT response command data.
	 */
	public void setCommandData(byte[] commandData) {
		this.commandData = commandData;
	}
	
	/**
	 * Retrieves the AT response command data.
	 * 
	 * @return The AT response command data.
	 */
	public byte[] getCommandData() {
		return commandData;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Frame ID", HexUtils.prettyHexString(HexUtils.integerToHexString(frameID, 1)) + " (" + frameID + ")");
		parameters.put("AT Command", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(command.getBytes())) + " (" + command + ")");
		parameters.put("Status", HexUtils.prettyHexString(HexUtils.integerToHexString(status.getId(), 1)) + " (" + status.getDescription() + ")");
		if (commandData != null) {
			if (ATStringCommands.get(command) != null)
				parameters.put("Response", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(commandData)) + " (" + new String(commandData) + ")");
			else
				parameters.put("Response", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(commandData)));
		}
		return parameters;
	}
}

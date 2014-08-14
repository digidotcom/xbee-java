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
 * <p>In response to an AT Command message, the module will send an AT Command Response message. Some
 * commands will send back multiple frames (for example, the ND (Node Discover) command).</p>
 * 
 * <p>This packet is received in response of an {@code ATCommandPacket}.</p>
 * 
 * <p>Response also includes an {@code ATComandStatus} object with the status of the AT command.</p>
 * 
 * @see ATCommandPacket
 * @see ATComandStatus 
 */
public class ATCommandResponsePacket extends XBeeAPIPacket {
	
	// Variables
	private final ATCommandStatus status;
	
	private final String command;
	
	private byte[] commandValue;
	
	private Logger logger;
	
	/**
	 * Class constructor. Instances a new object of type ATCommandResponsePacket
	 * with the given parameters.
	 * 
	 * @param frameID The XBee API frame ID.
	 * @param status The AT command response status.
	 * @param command The AT command.
	 * @param commandValue The AT command response value.
	 * 
	 * @throws NullPointerException if {@code status == null} or 
	 *                              if {@code command == null}.
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 * 
	 * @see ATCommandStatus
	 */
	public ATCommandResponsePacket(int frameID, ATCommandStatus status, String command, byte[] commandValue) {
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
		this.commandValue = commandValue;
		this.logger = LoggerFactory.getLogger(ATCommandResponsePacket.class);
	}
	
	@Override
	public byte[] getAPIData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(frameID);
			os.write(command.getBytes());
			os.write(status.getId());
			if (commandValue != null)
				os.write(commandValue);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return os.toByteArray();
	}
	
	@Override
	public boolean needsAPIFrameID() {
		return true;
	}
	
	/**
	 * Retrieves the AT command response status.
	 * 
	 * @return The AT command response status.
	 * 
	 * @see ATCommandStatus
	 */
	public ATCommandStatus getStatus() {
		return status;
	}
	
	/**
	 * Retrieves the AT command.
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
	 * Retrieves the AT command response value.
	 * 
	 * @return The AT command response value.
	 */
	public byte[] getCommandValue() {
		return commandValue;
	}
	
	/**
	 * Retrieves the AT command response value as String.
	 * 
	 * @return The AT command response value as String, null if no value is set.
	 */
	public String getCommandValueAsString() {
		if (commandValue == null)
			return null;
		return new String(commandValue);
	}
	
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Frame ID", HexUtils.prettyHexString(HexUtils.integerToHexString(frameID, 1)) + " (" + frameID + ")");
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

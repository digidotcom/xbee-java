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

import com.digi.xbee.api.models.ATStringCommands;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents an AT Command XBee packet. Packet is built
 * using the parameters of the constructor.
 * 
 * <p>Used to query or set module parameters on the local device. This API
 * command applies changes after executing the command. (Changes made to
 * module parameters take effect once changes are applied.).</p>
 * 
 * <p>Command response is received as an {@code ATCommandResponsePacket}.</p>
 * 
 * @see ATCommandResponsePacket
 */
public class ATCommandPacket extends XBeeAPIPacket {

	// Variables
	private final String command;
	
	private byte[] parameter;
	
	private Logger logger;
	
	/**
	 * Class constructor. Instances a new object of type ATCommandPacket
	 * with the given parameters.
	 * 
	 * @param frameID XBee API frame ID.
	 * @param command AT command.
	 * @param parameter AT command parameter as String.
	 * 
	 * @throws NullPointerException if {@code command == null}.
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 */
	public ATCommandPacket(int frameID, String command, String parameter) {
		super(APIFrameType.AT_COMMAND);
		
		if (command == null)
			throw new NullPointerException("Command cannot be null.");
		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException("Frame ID must be between 0 and 255.");
		
		this.frameID = frameID;
		this.command = command;
		if (parameter != null)
			this.parameter = parameter.getBytes();
		this.logger = LoggerFactory.getLogger(ATCommandPacket.class);
	}
	
	/**
	 * Class constructor. Instances a new object of type ATCommandPacket
	 * with the given parameters.
	 * 
	 * @param frameID XBee API frame ID.
	 * @param command AT command.
	 * @param parameter AT command parameter.
	 * 
	 * @throws NullPointerException if {@code command == null}.
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 */
	public ATCommandPacket(int frameID, String command, byte[] parameter) {
		super(APIFrameType.AT_COMMAND);
		
		if (command == null)
			throw new NullPointerException("Command cannot be null.");
		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException("Frame ID must be between 0 and 255.");
		
		this.frameID = frameID;
		this.command = command;
		this.parameter = parameter;
		this.logger = LoggerFactory.getLogger(ATCommandPacket.class);
	}
	
	@Override
	public byte[] getAPIData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(frameID);
			os.write(command.getBytes());
			if (parameter != null)
				os.write(parameter);
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
	 * Retrieves the AT command.
	 * 
	 * @return The AT command.
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * Sets the AT command parameter as String.
	 * 
	 * @param parameter The AT command parameter as String.
	 */
	public void setParameter(String parameter) {
		if (parameter == null)
			this.parameter = null;
		else
			this.parameter = parameter.getBytes();
	}
	
	/**
	 * Sets the AT command parameter.
	 * 
	 * @param parameter The AT command parameter.
	 */
	public void setParameter(byte[] parameter) {
		this.parameter = parameter;
	}
	
	/**
	 * Retrieves the AT command parameter.
	 * 
	 * @return The AT command parameter.
	 */
	public byte[] getParameter() {
		return parameter;
	}
	
	/**
	 * Retrieves the AT command parameter as String.
	 * 
	 * @return The AT command parameter as String, null if no parameter is set.
	 */
	public String getParameterAsString() {
		if (parameter == null)
			return null;
		return new String(parameter);
	}
	
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("Frame ID", HexUtils.prettyHexString(HexUtils.integerToHexString(frameID, 1)) + " (" + frameID + ")");
		parameters.put("AT Command", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(command.getBytes())) + " (" + command + ")");
		if (parameter != null) {
			if (ATStringCommands.get(command) != null)
				parameters.put("Parameter", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(parameter)) + new String(parameter));
			else
				parameters.put("Parameter", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(parameter)));
		}
		return parameters;
	}
}

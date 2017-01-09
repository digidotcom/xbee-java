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
package com.digi.xbee.api.packet.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.ATStringCommands;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents an AT Command XBee packet. Packet is built
 * using the parameters of the constructor or providing a valid API payload.
 * 
 * <p>Used to query or set module parameters on the local device. This API
 * command applies changes after executing the command. (Changes made to
 * module parameters take effect once changes are applied.).</p>
 * 
 * <p>Command response is received as an {@code ATCommandResponsePacket}.</p>
 * 
 * @see ATCommandResponsePacket
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 */
public class ATCommandPacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 4; // 1 (Frame type) + 1 (frame ID) + 2 (AT command)
	
	// Variables.
	private final String command;
	
	private byte[] parameter;
	
	private Logger logger;
	
	/**
	 * Creates a new {@code ATCommandPacket} object from the given payload.
	 * 
	 * @param payload The API frame payload. It must start with the frame type 
	 *                corresponding to a AT Command packet ({@code 0x08}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 * 
	 * @return Parsed AT Command packet.
	 * 
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.AT_COMMAND.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH} or
	 *                                  if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static ATCommandPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException("AT Command packet payload cannot be null.");
		
		// 1 (Frame type) + 1 (frame ID) + 2 (AT command)
		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException("Incomplete AT Command packet.");
		
		if ((payload[0] & 0xFF) != APIFrameType.AT_COMMAND.getValue())
			throw new IllegalArgumentException("Payload is not an AT Command packet.");
		
		// payload[0] is the frame type.
		int index = 1;
		
		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;
		
		// 2 bytes of AT command, starting at 2nd byte.
		String command = new String(new byte[]{payload[index], payload[index + 1]});
		index = index + 2;
		
		// Get data.
		byte[] parameterData = null;
		if (index < payload.length)
			parameterData = Arrays.copyOfRange(payload, index, payload.length);
		
		return new ATCommandPacket(frameID, command, parameterData);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code ATCommandPacket} object
	 * with the given parameters.
	 * 
	 * @param frameID XBee API frame ID.
	 * @param command AT command.
	 * @param parameter AT command parameter as String, {@code null} if it is 
	 *                  not required.
	 * 
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 * @throws NullPointerException if {@code command == null}.
	 */
	public ATCommandPacket(int frameID, String command, String parameter) {
		this(frameID, command, parameter == null ? null : parameter.getBytes());
	}
	
	/**
	 * Class constructor. Instantiates a new {@code ATCommandPacket} object
	 * with the given parameters.
	 * 
	 * @param frameID XBee API frame ID.
	 * @param command AT command.
	 * @param parameter AT command parameter {@code null} if it is not required.
	 * 
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 * @throws NullPointerException if {@code command == null}.
	 */
	public ATCommandPacket(int frameID, String command, byte[] parameter) {
		super(APIFrameType.AT_COMMAND);
		
		if (command == null)
			throw new NullPointerException("AT command cannot be null.");
		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException("Frame ID must be between 0 and 255.");
		
		this.frameID = frameID;
		this.command = command;
		this.parameter = parameter;
		this.logger = LoggerFactory.getLogger(ATCommandPacket.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(command.getBytes());
			if (parameter != null)
				os.write(parameter);
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
	 * Returns the AT command.
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
	 * Returns the AT command parameter.
	 * 
	 * @return The AT command parameter.
	 */
	public byte[] getParameter() {
		return parameter;
	}
	
	/**
	 * Returns the AT command parameter as String.
	 * 
	 * @return The AT command parameter as String, {@code null} if no parameter 
	 *         is set.
	 */
	public String getParameterAsString() {
		if (parameter == null)
			return null;
		return new String(parameter);
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
		if (parameter != null) {
			if (ATStringCommands.get(command) != null)
				parameters.put("Parameter", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(parameter)) + " (" + new String(parameter) + ")");
			else
				parameters.put("Parameter", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(parameter)));
		}
		return parameters;
	}
}

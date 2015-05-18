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

import com.digi.xbee.api.models.ATStringCommands;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.ByteUtils;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a Remote AT Command Request packet. Packet is built
 * using the parameters of the constructor or providing a valid API payload.
 * 
 * <p>Used to query or set module parameters on a remote device. For parameter 
 * changes on the remote device to take effect, changes must be applied, either 
 * by setting the apply changes options bit, or by sending an {@code AC} command 
 * to the remote node.</p>
 * 
 * <p>Remote Command options are set as a bitfield.</p>
 * 
 * <p>If configured, command response is received as a 
 * {@code RemoteATCommandResponse packet}.</p>
 * 
 * @see RemoteATCommandResponsePacket
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 */
public class RemoteATCommandPacket extends XBeeAPIPacket {
	
	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 15; // 1 (Frame type) + 1 (frame ID) + 8 (64-bit address) + 2 (16-bit address) + 1 (transmit options byte) + 2 (AT command)
	
	// Variables.
	private final XBee64BitAddress destAddress64;
	
	private final XBee16BitAddress destAddress16;
	
	private final int transmitOptions;
	
	private final String command;
	
	private byte[] parameter;
	
	private Logger logger;
	
	/**
	 * Creates a new {@code RemoteATCommandPacket} object from the given 
	 * payload.
	 * 
	 * @param payload The API frame payload. It must start with the frame type 
	 *                corresponding to a Remote AT Command packet ({@code 0x17}).
	 *                The byte array must be in {@code OperatingMode.API} mode.
	 * 
	 * @return Parsed Remote AT Command Request packet.
	 * 
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.REMOTE_AT_COMMAND_REQUEST.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH} or
	 *                                  if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code transmitOptions < 0} or
	 *                                  if {@code transmitOptions > 255}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static RemoteATCommandPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException("Remote AT Command packet payload cannot be null.");
		
		// 1 (Frame type) + 1 (frame ID) + 8 (64-bit address) + 2 (16-bit address) + 1 (transmit options byte) + 2 (AT command)
		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException("Incomplete Remote AT Command packet.");
		
		if ((payload[0] & 0xFF) != APIFrameType.REMOTE_AT_COMMAND_REQUEST.getValue())
			throw new IllegalArgumentException("Payload is not a Remote AT Command packet.");
		
		// payload[0] is the frame type.
		int index = 1;
		
		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;
		
		// 8 bytes of 64-bit address.
		XBee64BitAddress destAddress64 = new XBee64BitAddress(Arrays.copyOfRange(payload, index, index + 8));
		index = index + 8;
		
		// 2 bytes of 16-bit address.
		XBee16BitAddress destAddress16 = new XBee16BitAddress(payload[index] & 0xFF, payload[index + 1] & 0xFF);
		index = index + 2;
		
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
		
		return new RemoteATCommandPacket(frameID, destAddress64, destAddress16, transmitOptions, 
				command, parameterData);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteATCommandRequest}
	 * object with the given parameters.
	 * 
	 * @param frameID The Frame ID.
	 * @param destAddress64 64-bit address of the destination device.
	 * @param destAddress16 16-bit address of the destination device.
	 * @param transmitOptions Bitfield of supported transmission options.
	 * @param command AT command.
	 * @param parameter AT command parameter as String.
	 * 
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code transmitOptions < 0} or
	 *                                  if {@code transmitOptions > 255}.
	 * @throws NullPointerException if {@code destAddress64 == null} or
	 *                              if {@code destAddress16 == null} or
	 *                              if {@code command == null}.
	 * 
	 * @see com.digi.xbee.api.models.XBeeTransmitOptions
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public RemoteATCommandPacket(int frameID, XBee64BitAddress destAddress64, XBee16BitAddress destAddress16, 
			int transmitOptions, String command, String parameter) {
		super(APIFrameType.REMOTE_AT_COMMAND_REQUEST);
		
		if (destAddress64 == null)
			throw new NullPointerException("64-bit destination address cannot be null.");
		if (destAddress16 == null)
			throw new NullPointerException("16-bit destination address cannot be null.");
		if (command == null)
			throw new NullPointerException("AT command cannot be null.");
		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException("Frame ID must be between 0 and 255.");
		if (transmitOptions < 0 || transmitOptions > 255)
			throw new IllegalArgumentException("Options value must be between 0 and 255.");
		
		this.frameID = frameID;
		this.destAddress64 = destAddress64;
		this.destAddress16 = destAddress16;
		this.transmitOptions = transmitOptions;
		this.command = command;
		if (parameter != null)
			this.parameter = parameter.getBytes();
		this.logger = LoggerFactory.getLogger(RemoteATCommandPacket.class);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteATCommandRequest} 
	 * object with the given parameters.
	 * 
	 * @param frameID Frame ID.
	 * @param destAddress64 64-bit address of the destination device.
	 * @param destAddress16 16-bit address of the destination device.
	 * @param transmitOptions Bitfield of supported transmission options.
	 * @param command AT command.
	 * @param parameter AT command parameter.
	 * 
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255} or
	 *                                  if {@code transmitOptions < 0} or
	 *                                  if {@code transmitOptions > 255}.
	 * @throws NullPointerException if {@code destAddress64 == null} or
	 *                              if {@code destAddress16 == null} or
	 *                              if {@code command == null}.
	 * 
	 * @see com.digi.xbee.api.models.XBeeTransmitOptions
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public RemoteATCommandPacket(int frameID, XBee64BitAddress destAddress64, XBee16BitAddress destAddress16, 
			int transmitOptions, String command, byte[] parameter) {
		super(APIFrameType.REMOTE_AT_COMMAND_REQUEST);
		
		if (destAddress64 == null)
			throw new NullPointerException("64-bit destination address cannot be null.");
		if (destAddress16 == null)
			throw new NullPointerException("16-bit destination address cannot be null.");
		if (command == null)
			throw new NullPointerException("AT command cannot be null.");
		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException("Frame ID must be between 0 and 255.");
		if (transmitOptions < 0 || transmitOptions > 255)
			throw new IllegalArgumentException("Options value must be between 0 and 255.");
		
		this.frameID = frameID;
		this.destAddress64 = destAddress64;
		this.destAddress16 = destAddress16;
		this.transmitOptions = transmitOptions;
		this.command = command;
		this.parameter = parameter;
		this.logger = LoggerFactory.getLogger(RemoteATCommandPacket.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try {
			data.write(destAddress64.getValue());
			data.write(destAddress16.getValue());
			data.write(transmitOptions);
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
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#isBroadcast()
	 */
	@Override
	public boolean isBroadcast() {
		return get64bitDestinationAddress().equals(XBee64BitAddress.BROADCAST_ADDRESS) 
				|| get16bitDestinationAddress().equals(XBee16BitAddress.BROADCAST_ADDRESS);
	}
	
	/**
	 * Returns the 64 bit destination address.
	 * 
	 * @return The 64 bit destination address.
	 * 
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public XBee64BitAddress get64bitDestinationAddress() {
		return destAddress64;
	}
	
	/**
	 * Returns the 16 bit destination address.
	 * 
	 * @return The 16 bit destination address.
	 * 
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 */
	public XBee16BitAddress get16bitDestinationAddress() {
		return destAddress16;
	}
	
	/**
	 * Returns the transmit options bitfield.
	 * 
	 * @return Transmit options bitfield.
	 * 
	 * @see com.digi.xbee.api.models.XBeeTransmitOptions
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
	 * @return The AT command parameter as String, {@code null} if not 
	 *         parameter is set.
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
		parameters.put("64-bit dest. address", HexUtils.prettyHexString(destAddress64.toString()));
		parameters.put("16-bit dest. address", HexUtils.prettyHexString(destAddress16.toString()));
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

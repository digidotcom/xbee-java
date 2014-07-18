package com.digi.xbee.api.packet.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.XBeeAPIType;
import com.digi.xbee.api.utils.HexUtils;
import com.digi.xbee.api.models.ATStringCommands;

/**
 * This class represents an AT Command XBee packet. Packet is built
 * using the parameters of the constructor.
 * 
 * Used to query or set module parameters on the local device. This API
 * command applies changes after executing the command. (Changes made to
 * module parameters take effect once changes are applied.).
 * 
 * Command response is received as an AT Command Response packet.
 * See {@link com.digi.xbee.packet.common.ATCommandResponsePacket}.
 */
public class ATCommandPacket extends XBeeAPIPacket {

	// Variables	
	private String command;
	
	private byte[] parameter;
	
	/**
	 * Class constructor. Instances a new object of type ATCommandPacket
	 * with the given parameters.
	 * 
	 * @param frameID The XBee API frame ID.
	 * @param command The AT command.
	 * @param parameter The AT command parameter as String.
	 */
	public ATCommandPacket(int frameID, String command, String parameter) {
		this(frameID, command, parameter.getBytes());
	}
	
	/**
	 * Class constructor. Instances a new object of type ATCommandPacket
	 * with the given parameters.
	 * 
	 * @param frameID The XBee API frame ID.
	 * @param command The AT command.
	 * @param parameter The AT command parameter.
	 */
	public ATCommandPacket(int frameID, String command, byte[] parameter) {
		super(XBeeAPIType.AT_COMMAND);
		this.frameID = frameID;
		this.command = command;
		this.parameter = parameter;
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
			if (parameter != null)
				os.write(parameter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return os.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#hasAPIFrameID()
	 */
	public boolean hasAPIFrameID() {
		return true;
	}
	
	/**
	 * Sets the AT command.
	 * 
	 * @param command The AT command.
	 */
	public void setCommand(String command) {
		this.command = command;
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
	 * @return The AT command parameter as String, null if not parameter is set.
	 */
	public String getParameterAsString() {
		if (parameter == null)
			return null;
		return new String(parameter);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
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

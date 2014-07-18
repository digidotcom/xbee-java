package com.digi.xbee.api.models;

import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents the response of an AT Command sent from the
 * connected XBee device or from a remote device after executing an
 * AT Command. See {@link com.digi.xbee.models.ATCommand}.
 * 
 * Among the executed command, this object contains the response data
 * and the command status. See {@link com.digi.xbee.models.ATCommandStatus}. 
 */
public class ATCommandResponse {
	
	// Variables
	private ATCommand command;
	
	private byte[] response;
	
	private ATCommandStatus status = ATCommandStatus.OK;
	
	/**
	 * Class constructor. Instances a new object of type ATCommandResponse
	 * with the given parameters.
	 * 
	 * @param command The AT command which generated the response.
	 * @param response The command response.
	 */
	public ATCommandResponse(ATCommand command, byte[] response) {
		this(command, response, ATCommandStatus.OK);
	}
	
	/**
	 * Class constructor. Instances a new object of type ATCommandResponse
	 * with the given parameters.
	 * 
	 * @param command The AT command which generated the response.
	 * @param response The command response.
	 * @param status Response status. See {@link com.digi.xbee.api.models.ATCommandStatus}.
	 */
	public ATCommandResponse(ATCommand command, byte[] response, ATCommandStatus status) {
		this.command = command;
		this.response = response;
		this.status = status;
	}
	
	/**
	 * Retrieves the AT command which generated the response.
	 * 
	 * @return The AT command which generated the response.
	 */
	public ATCommand getCommand() {
		return command;
	}
	
	/**
	 * Retrieves the AT command response data if any.
	 * 
	 * @return The AT command response if any, null otherwise.
	 */
	public byte[] getResponse() {
		return response;
	}
	
	/**
	 * Retrieves the AT command response as string if any.
	 * 
	 * @return The AT command response as string if any.
	 */
	public String getResponseString() {
		if (response == null)
			return "";
		return HexUtils.byteArrayToHexString(response).replace("\n", "");
	}
	
	/**
	 * Retrieves the AT command response status.
	 *  See {@link com.digi.xbee.models.ATCommandStatus}.
	 * 
	 * @return The AT command response status. See {@link com.digi.xbee.models.ATCommandStatus}.
	 */
	public ATCommandStatus getResponseStatus() {
		return status;
	}
	
	/**
	 * Sets the AT command which generated the response.
	 * 
	 * @param command The AT command which generated the response.
	 */
	public void setCommand(ATCommand command) {
		this.command = command;
	}
	
	/**
	 * Sets the AT command response data if any.
	 * 
	 * @param response The AT command response data, maybe null.
	 */
	public void setResponse(byte[] response) {
		this.response = response;
	}
	
	/**
	 * Sets the AT command response status.
	 * See {@link com.digi.xbee.models.ATCommandStatus}.
	 * 
	 * @param status The AT command response status. See {@link com.digi.xbee.models.ATCommandStatus}.
	 */
	public void setResponseStatus(ATCommandStatus status) {
		this.status = status;
	}
}

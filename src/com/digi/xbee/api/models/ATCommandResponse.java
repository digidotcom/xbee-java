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
package com.digi.xbee.api.models;

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
	 * 
	 * @throws NullPointerException if {@code command == null}.
	 */
	public ATCommandResponse(ATCommand command) {
		this(command, null, ATCommandStatus.OK);
	}
	
	/**
	 * Class constructor. Instances a new object of type ATCommandResponse
	 * with the given parameters.
	 * 
	 * @param command The AT command which generated the response.
	 * @param status Response status.
	 * 
	 * @throws NullPointerException if {@code command == null} or 
	 *                              if {@code status == null}..
	 */
	public ATCommandResponse(ATCommand command, ATCommandStatus status) {
		this(command, null, status);
	}
	
	/**
	 * Class constructor. Instances a new object of type ATCommandResponse
	 * with the given parameters.
	 * 
	 * @param command The AT command which generated the response.
	 * @param response The command response.
	 * 
	 * @throws NullPointerException if {@code command == null} or 
	 *                              if {@code response == null}.
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
	 * @param status Response status.
	 * 
	 * @throws NullPointerException if {@code command == null} or 
	 *                              if {@code status == null}.
	 * 
	 * @see ATCommandStatus
	 */
	public ATCommandResponse(ATCommand command, byte[] response, ATCommandStatus status) {
		if (command == null)
			throw new NullPointerException("Command cannot be null.");
		if (status == null)
			throw new NullPointerException("Status cannot be null.");
		
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
			return null;
		return new String(response);
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
}

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
 * This class represents the response of an AT Command sent from the connected 
 * XBee device or from a remote device after executing an AT Command.
 * 
 * <p>Among the executed command, this object contains the response data and the 
 * command status.</p>
 * 
 * @see ATCommand
 * @see ATCommandStatus
 */
public class ATCommandResponse {
	
	// Variables
	private final ATCommand command;
	
	private final byte[] response;
	
	private final ATCommandStatus status;
	
	/**
	 * Class constructor. Instances a new object of type 
	 * {@code ATCommandResponse} with the given parameters.
	 * 
	 * @param command The AT command which generated the response.
	 * 
	 * @throws NullPointerException if {@code command == null}.
	 * 
	 * @see ATCommand
	 */
	public ATCommandResponse(ATCommand command) {
		this(command, null, ATCommandStatus.OK);
	}
	
	/**
	 * Class constructor. Instances a new object of type 
	 * {@code ATCommandResponse} with the given parameters.
	 * 
	 * @param command The AT command which generated the response.
	 * @param status Response status.
	 * 
	 * @throws NullPointerException if {@code command == null} or 
	 *                              if {@code status == null}.
	 * @see ATCommand
	 * @see ATCommandStatus
	 */
	public ATCommandResponse(ATCommand command, ATCommandStatus status) {
		this(command, null, status);
	}
	
	/**
	 * Class constructor. Instances a new object of type 
	 * {@code ATCommandResponse} with the given parameters.
	 * 
	 * @param command The AT command which generated the response.
	 * @param response The command response.
	 * 
	 * @throws NullPointerException if {@code command == null} or 
	 *                              if {@code response == null}.
	 * 
	 * @see ATCommand
	 */
	public ATCommandResponse(ATCommand command, byte[] response) {
		this(command, response, ATCommandStatus.OK);
	}
	
	/**
	 * Class constructor. Instances a new object of type 
	 * {@code ATCommandResponse} with the given parameters.
	 * 
	 * @param command The AT command which generated the response.
	 * @param response The command response.
	 * @param status Response status.
	 * 
	 * @throws NullPointerException if {@code command == null} or 
	 *                              if {@code status == null}.
	 * 
	 * @see ATCommand
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
	 * 
	 * @see ATCommand
	 */
	public ATCommand getCommand() {
		return command;
	}
	
	/**
	 * Retrieves the AT command response data if any.
	 * 
	 * @return The AT command response if any, {@code null} otherwise.
	 */
	public byte[] getResponse() {
		return response;
	}
	
	/**
	 * Retrieves the AT command response as string if any.
	 * 
	 * @return The AT command response as string if any, {@code null} otherwise.
	 */
	public String getResponseString() {
		if (response == null)
			return null;
		return new String(response);
	}
	
	/**
	 * Retrieves the AT command response status.
	 * 
	 * @return The AT command response status.
	 * 
	 * @see ATCommandStatus
	 */
	public ATCommandStatus getResponseStatus() {
		return status;
	}
}
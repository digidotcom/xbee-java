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
package com.digi.xbee.api.models;

/**
 * This class represents the response of an AT Command sent by the connected 
 * XBee device or by a remote device after executing an AT Command.
 * 
 * <p>Among the executed command, this object contains the response data and 
 * the command status.</p>
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
	 * Class constructor. Instantiates a new object of type 
	 * {@code ATCommandResponse} with the given parameters.
	 * 
	 * @param command The {@code ATCommand} that generated the response.
	 * 
	 * @throws NullPointerException if {@code command == null}.
	 * 
	 * @see ATCommand
	 */
	public ATCommandResponse(ATCommand command) {
		this(command, null, ATCommandStatus.OK);
	}
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code ATCommandResponse} with the given parameters.
	 * 
	 * @param command The {@code ATCommand} that generated the response.
	 * @param status The {@code ATCommandStatus} containing the response 
	 *               status.
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
	 * Class constructor. Instantiates a new object of type 
	 * {@code ATCommandResponse} with the given parameters.
	 * 
	 * @param command The {@code ATCommand} that generated the response.
	 * @param response The command response in byte array format.
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
	 * Class constructor. Instantiates a new object of type 
	 * {@code ATCommandResponse} with the given parameters.
	 * 
	 * @param command The {@code ATCommand} that generated the response.
	 * @param response The command response in byte array format.
	 * @param status The {@code ATCommandStatus} containing the response 
	 *               status.
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
	 * Returns the AT command that generated the response.
	 * 
	 * @return The AT command that generated the response.
	 * 
	 * @see ATCommand
	 */
	public ATCommand getCommand() {
		return command;
	}
	
	/**
	 * Returns the AT command response data in byte array format if any.
	 * 
	 * @return The AT command response data in byte array format, 
	 *         {@code null} if there is not response data.
	 */
	public byte[] getResponse() {
		return response;
	}
	
	/**
	 * Returns the AT command response data as string if any.
	 * 
	 * @return The AT command response data as string, {@code null} if there 
	 *         is not response data.
	 */
	public String getResponseString() {
		if (response == null)
			return null;
		return new String(response);
	}
	
	/**
	 * Returns the AT command response status.
	 * 
	 * @return The AT command response status.
	 * 
	 * @see ATCommandStatus
	 */
	public ATCommandStatus getResponseStatus() {
		return status;
	}
}

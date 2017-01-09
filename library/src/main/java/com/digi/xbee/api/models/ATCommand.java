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
 * This class represents an AT command used to read or set different properties 
 * of the XBee device.
 * 
 * <p>AT commands can be sent directly to the connected device or to remote 
 * devices and may have parameters.</p> 
 * 
 * <p>After executing an AT Command, an AT Response is received from the 
 * device.</p>
 * 
 * @see ATCommandResponse
 */
public class ATCommand {
	
	// Variables
	private final String command;
	
	private byte[] parameter;
	
	/**
	 * Class constructor. Instantiates a new object of type {@code ATCommand} 
	 * with the given parameters.
	 * 
	 * @param command The AT Command alias.
	 * 
	 * @throws IllegalArgumentException if {@code command.length() != 2}.
	 * @throws NullPointerException if {@code command == null}.
	 */
	public ATCommand(String command) {
		this(command, (String)null);
	}
	
	/**
	 * Class constructor. Instantiates a new object of type {@code ATCommand} 
	 * with the given parameters.
	 * 
	 * <p>If not parameter is required the constructor 
	 * {@link #ATCommand(String)} is recommended.</p>
	 * 
	 * @param command The AT Command alias.
	 * @param parameter The command parameter as string.
	 * 
	 * @throws IllegalArgumentException if {@code command.length() != 2}.
	 * @throws NullPointerException if {@code command == null}.
	 */
	public ATCommand(String command, String parameter) {
		this(command, parameter == null ? null : parameter.getBytes());
	}
	
	/**
	 * Class constructor. Instantiates a new object of type {@code ATCommand} 
	 * with the given parameters.
	 * 
	 * <p>If not parameter is required the constructor 
	 * {@link #ATCommand(String)} is recommended.</p>
	 * 
	 * @param command The AT Command alias.
	 * @param parameter The command parameter as byte array.
	 * 
	 * @throws IllegalArgumentException if {@code command.length() != 2}.
	 * @throws NullPointerException if {@code command == null}.
	 */
	public ATCommand(String command, byte[] parameter) {
		if (command == null)
			throw new NullPointerException("Command cannot be null.");
		if (command.length() != 2)
			throw new IllegalArgumentException("Command lenght must be 2.");
		
		this.command = command;
		this.parameter = parameter;
	}
	
	/**
	 * Returns the AT command alias.
	 * 
	 * @return The AT command alias.
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * Returns the AT command parameter.
	 * 
	 * @return The AT command parameter, {@code null} if the command does not 
	 *         have a parameter.
	 */
	public byte[] getParameter() {
		return parameter;
	}
	
	/**
	 * Returns the AT command parameter in string format.
	 * 
	 * @return The AT command parameter, {@code null} if the command does not 
	 *         have a parameter.
	 */
	public String getParameterString() {
		if (parameter == null)
			return null;
		return new String(parameter);
	}
	
	/**
	 * Sets the AT command parameter as string.
	 * 
	 * @param parameter The AT command parameter as string.
	 */
	public void setParameter(String parameter) {
		this.parameter = parameter.getBytes();
	}
	
	/**
	 * Sets the AT command parameter as byte array.
	 * 
	 * @param parameter The AT command parameter as byte array.
	 */
	public void setParameter(byte[] parameter) {
		this.parameter = parameter;
	}
}

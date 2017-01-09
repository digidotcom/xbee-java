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

import java.util.HashMap;

/**
 * Enumerates several AT commands used to parse AT command packets. The list 
 * of AT Command alias listed here represents those AT commands whose values 
 * should be parsed as strings.
 */
public enum ATStringCommands {
	
	NI("NI"),
	KY("KY"),
	NK("NK"),
	ZU("ZU"),
	ZV("ZV"),
	CC("CC");
	
	// Variables
	private final static HashMap<String, ATStringCommands> lookupTable = new HashMap<String, ATStringCommands>();
	
	static {
		for (ATStringCommands atStringCommand:values())
			lookupTable.put(atStringCommand.getCommand(), atStringCommand);
	}
	
	private final String command;
	
	/**
	 * Class constructor. Instantiates a new enumeration element of type 
	 * {@code ATStringCommands} with the given AT Command alias.
	 * 
	 * @param command The AT Command alias.
	 */
	private ATStringCommands(String command) {
		this.command = command;
	}
	
	/**
	 * Returns the AT Command alias.
	 * 
	 * @return The AT Command alias.
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * Returns the {@code ATStringCommands} for the given AT Command alias.
	 * 
	 * @param command The AT Command alias to retrieve the corresponding 
	 *                {@code ATStringCommands}.
	 * 
	 * @return The {@code ATStringCommands} associated to the given AT Command 
	 *         alias. 
	 */
	public static ATStringCommands get(String command) {
		return lookupTable.get(command.toUpperCase());
	}
}

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

import java.util.HashMap;

/**
 * Enumerates several AT commands used to parse AT command packets.
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
	
	private ATStringCommands(String command) {
		this.command = command;
	}
	
	public String getCommand() {
		return command;
	}
	
	public static ATStringCommands get(String command) {
		return lookupTable.get(command.toUpperCase());
	}
}

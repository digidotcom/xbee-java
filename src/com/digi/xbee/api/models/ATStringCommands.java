package com.digi.xbee.api.models;

import java.util.HashMap;

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

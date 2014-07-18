package com.digi.xbee.api.models;

import java.util.HashMap;

/**
 * This class lists all the possible states of an AT Command
 * after executing it.
 */
public enum ATCommandStatus {

	// Enumeration elements
	OK (0, "Status OK"),
	ERROR (1, "Status Error"),
	INVALID_COMMAND (2, "Invalid command"), 
	INVALID_PARAMETER (3, "Invalid parameter"),
	TX_FAILURE (4, "TX failure"),
	UNKNOWN (64, "Unknown status");
	
	// Variables
	private int id;
	
	private String description;
	
	private static HashMap<Integer, ATCommandStatus> lookupTable = new HashMap<Integer, ATCommandStatus>();
	
	static {
		for (ATCommandStatus at:values())
			lookupTable.put(at.getId(), at);
	}
	
	/**
	 * Class constructor. Instances a new enumeration element of type ATCommandStatus
	 * with the given parameters.
	 * 
	 * @param id AT Command Status ID.
	 * @param description AT Command Status description.
	 */
	ATCommandStatus(int id, String description) {
		this.id = id;
		this.description = description;
	}
	
	/**
	 * Retrieves the AT Command Status ID.
	 * 
	 * @return The AT Command Status ID.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Retrieves the AT Command Status description.
	 * 
	 * @return AT Command Status description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Retrieves the AT Command Status for the given ID.
	 * 
	 * @param id ID to retrieve AT Command Status.
	 * @return AT Command Status associated with the given ID.
	 */
	public static ATCommandStatus get(int id) {
		if (lookupTable.get(id) == null)
			return UNKNOWN;
		return lookupTable.get(id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		return description;
	}
}

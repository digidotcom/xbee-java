package com.digi.xbee.api.models;

import java.util.HashMap;

import com.digi.xbee.api.packet.devicecloud.SendDataRequestPacket;

/**
 * Enumerates the different options for the {@link SendDataRequestPacket}.
 */
public enum SendDataRequestOptions {

	// Enumeration types.
	OVERWRITE(0, "Overwrite"),
	ARCHIVE(1, "Archive"),
	APPEND(2, "Append"),
	TRANSIENT(3, "Transient data (do not store)");

	// Variables.
	private int id;

	private String name;

	private static HashMap<Integer, SendDataRequestOptions> lookupTable = new HashMap<Integer, SendDataRequestOptions>();

	static {
		for (SendDataRequestOptions option:values())
			lookupTable.put(option.getID(), option);
	}

	/**
	 * Creates a new {@code SendDataRequestOptions} entry with the given ID.
	 *
	 * @param id Option ID.
	 * @param name Option name.
	 */
	SendDataRequestOptions(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Retrieves the option ID.
	 *
	 * @return The option ID.
	 */
	public int getID() {
		return id;
	}

	/**
	 * Retrieves the option name.
	 *
	 * @return The option name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves the {@code SendDataRequestOptions} for the given ID.
	 *
	 * @param id ID to retrieve.
	 *
	 * @return The {@code SendDataRequestOptions} associated to the given ID.
	 */
	public static SendDataRequestOptions get(int id) {
		return lookupTable.get(id);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
}

package com.digi.xbee.api.packet;

import java.util.LinkedHashMap;

import com.digi.xbee.api.utils.HexUtils;

/**
 * This abstract class provides the basic structure of a ZigBee API frame. Derived classes
 * should implement their own methods to generate the API data and frame ID in case
 * they support it. Basic operations such as frame type retrieval are performed in
 * this class.
 */
public abstract class XBeeAPIPacket extends XBeePacket {

	// Constants
	public final static int NO_FRAME_ID = 9999;
	
	// Variables
	private XBeeAPIType apiID = null;
	
	private int apiIDValue;
	
	protected int frameID = NO_FRAME_ID;

	/**
	 * Class constructor. Instances a new object of type AbstractXBeeAPIPacket
	 * with the given API ID.
	 * 
	 * @param apiID XBee packet API ID.
	 */
	protected XBeeAPIPacket(XBeeAPIType apiID) {
		super();
		this.apiID = apiID;
		apiIDValue = apiID.getValue();
	}
	
	/**
	 * Class constructor. Instances a new object of type AbstractXBeeAPIPacket
	 * with the given API ID integer.
	 * 
	 * @param apiIDValue XBee packet API ID integer value.
	 */
	protected XBeeAPIPacket(int apiIDValue) {
		super();
		this.apiIDValue = apiIDValue;
	}
	
	/**
	 * Retrieves the XBee packet API ID.
	 * 
	 * @return The XBee packet API ID.
	 */
	public XBeeAPIType getAPIID() {
		return apiID;
	}
	
	/**
	 * Retrieves the XBee packet API ID integer value.
	 * 
	 * @return The XBee packet API ID integer value.
	 */
	public int getAPIIDValue() {
		return apiIDValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.AbstractXBeePacket#getPacketData()
	 */
	public byte[] getPacketData() {
		byte[] data = new byte[getAPIData().length + 1];
		data[0] = (byte)apiIDValue;
		System.arraycopy(getAPIData(), 0, data, 1, getAPIData().length);
		return data;
	}
	
	/**
	 * Retrieves the XBee API packet data.
	 * 
	 * @return The XBee API packet data.
	 */
	public abstract byte[] getAPIData();
	
	/**
	 * Retrieves whether the API packet has API Frame ID or not.
	 * 
	 * @return True if the packet has API Frame ID, false otherwise.
	 */
	public abstract boolean hasAPIFrameID();
	
	/**
	 * Gets the Frame ID
	 * 
	 * @return frame ID
	 */
	public int getFrameID() {
		return frameID;
	}
	
	/**
	 * Sets the frame ID
	 * 
	 * @param frameID frame ID to set
	 */
	public void setFrameID(int frameID) {
		this.frameID = frameID;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeePacket#getPacketParameters()
	 */
	protected LinkedHashMap<String, String> getPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		if (getAPIID() != null)
			parameters.put("Frame type", HexUtils.prettyHexString(HexUtils.integerToHexString(apiIDValue, 1)) + " (" + getAPIID().getName() + ")");
		else
			parameters.put("Frame type", HexUtils.prettyHexString(HexUtils.integerToHexString(apiIDValue, 1)));
		parameters.putAll(getAPIPacketParameters());
		return parameters;
	}
	
	/**
	 * retrieves a map with the XBee packet parameters and their values.
	 * 
	 * @return A sorted map containing the XBee packet parameters with their values.
	 */
	protected abstract LinkedHashMap<String, String> getAPIPacketParameters();
}

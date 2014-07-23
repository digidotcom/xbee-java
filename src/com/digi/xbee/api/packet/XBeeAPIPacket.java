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
	private APIFrameType frameType = null;
	
	private int frameTypeValue;
	
	protected int frameID = NO_FRAME_ID;

	/**
	 * Class constructor. Instances a new object of type AbstractXBeeAPIPacket
	 * with the given API ID.
	 * 
	 * @param frameType XBee packet frame type.
	 * 
	 * @throws NullPointerException if {@code frameType == null}.
	 */
	protected XBeeAPIPacket(APIFrameType frameType) {
		super();
		
		if (frameType == null)
			throw new NullPointerException("Frame type cannot be null.");
		
		this.frameType = frameType;
		frameTypeValue = frameType.getValue();
	}
	
	/**
	 * Class constructor. Instances a new object of type AbstractXBeeAPIPacket
	 * with the given API ID integer.
	 * 
	 * @param frameTypeValue XBee packet frame type integer value.
	 * 
	 * @throws NullPointerException if {@code frameTypeValue < 0} or 
	 *                              if {@code frameTypeValue > 255}.
	 */
	protected XBeeAPIPacket(int frameTypeValue) {
		super();
		
		if (frameTypeValue < 0 || frameTypeValue > 255)
			throw new IllegalArgumentException("Frame type value must be between 0 and 255.");
		
		this.frameTypeValue = frameTypeValue;
	}
	
	/**
	 * Retrieves the XBee packet frame type.
	 * 
	 * @return The XBee packet frame type.
	 */
	public APIFrameType getFrameType() {
		return frameType;
	}
	
	/**
	 * Retrieves the XBee packet frame type integer value.
	 * 
	 * @return The XBee packet frame type integer value.
	 */
	public int getFrameTypeValue() {
		return frameTypeValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.AbstractXBeePacket#getPacketData()
	 */
	public byte[] getPacketData() {
		byte[] data = new byte[getAPIData().length + 1];
		data[0] = (byte)frameTypeValue;
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
	 * Retrieves whether the API packet needs API Frame ID or not.
	 * 
	 * @return True if the packet needs API Frame ID, false otherwise.
	 */
	public abstract boolean needsAPIFrameID();
	
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
	 * 
	 * @throws IllegalArgumentException if {@code frameID < 0} or 
	 *                                  if {@code frameID > 255}.
	 */
	public void setFrameID(int frameID) {
		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException("Frame ID must be between 0 and 255.");
		
		this.frameID = frameID;
	}
	
	/**
	 * Returns whether the given id is the current frame id.
	 * 
	 * @param id the frame id to check.
	 * @return {@code true} if frame ID is equal to the {@code id} provided, 
	 *         {@code false} otherwise or if the frame does not need an id.
	 *         
	 * @see #needsAPIFrameID()
	 * @see #getFrameID()
	 * @see #setFrameID(int)
	 */
	public boolean checkFrameID(int id) {
		if (needsAPIFrameID() && getFrameID() == id)
			return true;
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeePacket#getPacketParameters()
	 */
	protected LinkedHashMap<String, String> getPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		if (getFrameType() != null)
			parameters.put("Frame type", HexUtils.prettyHexString(HexUtils.integerToHexString(frameTypeValue, 1)) + " (" + getFrameType().getName() + ")");
		else
			parameters.put("Frame type", HexUtils.prettyHexString(HexUtils.integerToHexString(frameTypeValue, 1)));
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

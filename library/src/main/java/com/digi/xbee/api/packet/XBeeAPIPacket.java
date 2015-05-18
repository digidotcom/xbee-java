/**
 * Copyright (c) 2014-2015 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.utils.HexUtils;

/**
 * This abstract class provides the basic structure of a ZigBee API frame.
 * 
 * <p>Derived classes should implement their own methods to generate the API 
 * data and frame ID in case they support it.</p>
 * 
 * <p>Basic operations such as frame type retrieval are performed in this class.
 * </p>
 * 
 * @see XBeePacket
 */
public abstract class XBeeAPIPacket extends XBeePacket {

	// Constants.
	public final static int NO_FRAME_ID = 9999;
	
	// Variables.
	protected int frameID = NO_FRAME_ID;
	
	private APIFrameType frameType = null;
	
	private int frameTypeValue;
	
	private Logger logger;

	/**
	 * Class constructor. Instantiates a new {@code XBeeAPIPacket} object with 
	 * the given API frame type.
	 * 
	 * @param frameType XBee packet frame type.
	 * 
	 * @throws NullPointerException if {@code frameType == null}.
	 * 
	 * @see APIFrameType
	 */
	protected XBeeAPIPacket(APIFrameType frameType) {
		super();
		
		if (frameType == null)
			throw new NullPointerException("Frame type cannot be null.");
		
		this.frameType = frameType;
		frameTypeValue = frameType.getValue();
		
		this.logger = LoggerFactory.getLogger(XBeeAPIPacket.class);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code XBeeAPIPacket} object with 
	 * the given frame type value.
	 * 
	 * @param frameTypeValue XBee packet frame type integer value.
	 * 
	 * @throws IllegalArgumentException if {@code frameTypeValue < 0} or 
	 *                                  if {@code frameTypeValue > 255}.
	 */
	protected XBeeAPIPacket(int frameTypeValue) {
		super();
		
		if (frameTypeValue < 0 || frameTypeValue > 255)
			throw new IllegalArgumentException("Frame type value must be between 0 and 255.");
		
		this.frameTypeValue = frameTypeValue;
		this.frameType = APIFrameType.get(frameTypeValue);
		
		this.logger = LoggerFactory.getLogger(XBeeAPIPacket.class);
	}
	
	/**
	 * Returns the XBee packet frame type.
	 * 
	 * If {@code APIFrameType#UNKNOWN} is returned, the real value of the frame
	 * type is returned by {@code #getFrameTypeValue()}.
	 * 
	 * @return The XBee packet frame type.
	 * 
	 * @see APIFrameType
	 */
	public APIFrameType getFrameType() {
		return frameType;
	}
	
	/**
	 * Returns the XBee packet frame type integer value.
	 * 
	 * @return The XBee packet frame type integer value.
	 */
	public int getFrameTypeValue() {
		return frameTypeValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeePacket#getPacketData()
	 */
	@Override
	public byte[] getPacketData() {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		
		data.write(frameTypeValue);
		
		byte[] apiData = getAPIData();
		if (apiData == null)
			apiData = new byte[0];
		if (apiData != null && apiData.length > 0) {
			try {
				data.write(apiData);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		return data.toByteArray();
	}
	
	/**
	 * Returns the XBee API packet data.
	 * 
	 * <p>This does not include the frame ID if it is needed.</p>
	 * 
	 * @return The XBee API packet data.
	 */
	public byte[] getAPIData() {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		
		byte[] apiData = getAPIPacketSpecificData();
		if (apiData == null)
			apiData = new byte[0];
		
		if (needsAPIFrameID())
			data.write(frameID);
		
		if (apiData != null && apiData.length > 0) {
			try {
				data.write(apiData);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		return data.toByteArray();
	}
	
	/**
	 * Returns the XBee API packet specific data.
	 * 
	 * <p>This does not include the frame ID if it is needed.</p>
	 * 
	 * @return The XBee API packet data.
	 */
	protected abstract byte[] getAPIPacketSpecificData();
	
	/**
	 * Returns whether the API packet needs API Frame ID or not.
	 * 
	 * @return {@code true} if the packet needs API Frame ID, {@code false} 
	 *         otherwise.
	 */
	public abstract boolean needsAPIFrameID();
	
	/**
	 * Returns the Frame ID of the API packet.
	 * 
	 * <p>If the frame ID is not configured or if the API packet does not need 
	 * a Frame ID ({@code if (!needsAPIFrameID())}) this method returns 
	 * {@code NO_FRAME_ID} ({@value #NO_FRAME_ID}).</p>
	 * 
	 * @return The frame ID.
	 * 
	 * @see #NO_FRAME_ID
	 * @see #needsAPIFrameID()
	 * @see #setFrameID(int)
	 */
	public int getFrameID() {
		if (needsAPIFrameID())
			return frameID;
		return NO_FRAME_ID;
	}
	
	/**
	 * Sets the frame ID of the API packet.
	 * 
	 * <p>If the API packet does not need a frame ID 
	 * ({@code if (!needsAPIFrameID())}), this method does nothing.</p>
	 * 
	 * @param frameID The frame ID to set.
	 * 
	 * @throws IllegalArgumentException if {@code frameID < 0} or 
	 *                                  if {@code frameID > 255}.
	 * 
	 * @see #getFrameID()
	 */
	public void setFrameID(int frameID) {
		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException("Frame ID must be between 0 and 255.");
		
		if (needsAPIFrameID())
			this.frameID = frameID;
	}
	
	/**
	 * Returns whether or not the packet is a broadcast packet.
	 * 
	 * @return {@code true} if the packet is a broadcast packet, {@code false} 
	 *         otherwise.
	 */
	public abstract boolean isBroadcast();
	
	/**
	 * Returns whether the given ID is the current frame ID.
	 * 
	 * @param id The frame id to check.
	 * 
	 * @return {@code true} if frame ID is equal to the {@code id} provided, 
	 *         {@code false} otherwise or if the frame does not need an ID.
	 * 
	 * @see #getFrameID()
	 * @see #needsAPIFrameID()
	 * @see #setFrameID(int)
	 */
	public boolean checkFrameID(int id) {
		return needsAPIFrameID() && getFrameID() == id;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeePacket#getPacketParameters()
	 */
	@Override
	protected LinkedHashMap<String, String> getPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		if (getFrameType() != null)
			parameters.put("Frame type", HexUtils.prettyHexString(HexUtils.integerToHexString(frameTypeValue, 1)) + " (" + getFrameType().getName() + ")");
		else
			parameters.put("Frame type", HexUtils.prettyHexString(HexUtils.integerToHexString(frameTypeValue, 1)));
		
		if (needsAPIFrameID()) {
			if (frameID == NO_FRAME_ID)
				parameters.put("Frame ID", "(NO FRAME ID)");
			else
				parameters.put("Frame ID", HexUtils.prettyHexString(HexUtils.integerToHexString(frameID, 1)) + " (" + frameID + ")");
		}
		
		LinkedHashMap<String, String> apiParams = getAPIPacketParameters();
		if (apiParams != null)
			parameters.putAll(apiParams);
		return parameters;
	}
	
	/**
	 * Returns a map with the XBee packet parameters and their values.
	 * 
	 * @return A sorted map containing the XBee packet parameters with their 
	 *         values.
	 */
	protected abstract LinkedHashMap<String, String> getAPIPacketParameters();
}

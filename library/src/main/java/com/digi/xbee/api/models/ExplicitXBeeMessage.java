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
package com.digi.xbee.api.models;

import com.digi.xbee.api.RemoteXBeeDevice;

/**
 * This class represents an Explicit XBee message containing the remote XBee 
 * device the message belongs to, the content (data) of the message, a flag 
 * indicating if the message is a broadcast message (was received or is being 
 * sent via broadcast) and all the application layer fields: source endpoint, 
 * destination endpoint, cluster ID and profile ID. 
 * 
 * <p>This class is used within the XBee Java Library to read explicit data 
 * sent by remote devices.</p>
 */
public class ExplicitXBeeMessage extends XBeeMessage {

	// Variables.
	private final byte[] clusterID;
	private final byte[] profileID;
	
	private final int sourceEndpoint;
	private final int destEndpoint;
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code ExplicitXBeeMessage} with the given parameters.
	 * 
	 * @param remoteXBeeDevice The remote XBee device the message belongs to. 
	 *                         (device that sent the message)
	 * @param sourceEndpoint Endpoint of the source that initiated the 
	 *                       transmission.
	 * @param destEndpoint Endpoint of the destination the message was 
	 *                            addressed to.
	 * @param clusterID Cluster ID the packet was addressed to.
	 * @param profileID Profile ID the packet was addressed to.
	 * @param data Byte array containing the data of the message.
	 * 
	 * @throws IllegalArgumentException if {@code sourceEndpoint < 0} or 
	 *                                  if {@code sourceEndpoint > 255} or 
	 *                                  if {@code destEndpoint < 0} or 
	 *                                  if {@code destEndpoint > 255} or 
	 *                                  if {@code clusterID.length != 2} or 
	 *                                  if {@code profileID.length != 2}.
	 * @throws NullPointerException if {@code remoteXBeeDevice == null} or
	 *                              if {@code data == null} or 
	 *                              if {@code clusterID == null} or 
	 *                              if {@code profileID == null}.
	 * 
	 * @see com.digi.xbee.api.RemoteXBeeDevice
	 */
	public ExplicitXBeeMessage(RemoteXBeeDevice remoteXBeeDevice, int sourceEndpoint, int destEndpoint, byte[] clusterID, byte[] profileID, byte[] data) {
		this(remoteXBeeDevice, sourceEndpoint, destEndpoint, clusterID, profileID, data, false);
	}
	
	/**
	 * Class constructor. Instantiates a new object of type 
	 * {@code XBeeMessage} with the given parameters.
	 * 
	 * @param remoteXBeeDevice The remote XBee device the message belongs to. 
	 *                         (device that sent the message)
	 * @param sourceEndpoint Endpoint of the source that initiated the 
	 *                       transmission.
	 * @param destEndpoint Endpoint of the destination the message was 
	 *                            addressed to.
	 * @param clusterID Cluster ID the packet was addressed to.
	 * @param profileID Profile ID the packet was addressed to.
	 * @param data Byte array containing the data of the message.
	 * @param isBroadcast Indicates if the message was received via broadcast.
	 * 
	 * @throws IllegalArgumentException if {@code sourceEndpoint < 0} or 
	 *                                  if {@code sourceEndpoint > 255} or 
	 *                                  if {@code destEndpoint < 0} or 
	 *                                  if {@code destEndpoint > 255} or 
	 *                                  if {@code clusterID.length != 2} or 
	 *                                  if {@code profileID.length != 2}.
	 * @throws NullPointerException if {@code remoteXBeeDevice == null} or
	 *                              if {@code data == null} or 
	 *                              if {@code clusterID == null} or 
	 *                              if {@code profileID == null}.
	 * 
	 * @see com.digi.xbee.api.RemoteXBeeDevice
	 */
	public ExplicitXBeeMessage(RemoteXBeeDevice remoteXBeeDevice, int sourceEndpoint, int destEndpoint, byte[] clusterID, byte[] profileID, byte[] data, boolean isBroadcast) {
		super(remoteXBeeDevice, data, isBroadcast);
		
		if (clusterID == null)
			throw new NullPointerException("Cluster ID cannot be null.");
		if (profileID == null)
			throw new NullPointerException("Profile ID cannot be null.");
		if (sourceEndpoint < 0 || sourceEndpoint > 255)
			throw new IllegalArgumentException("Source endpoint must be between 0 and 255.");
		if (destEndpoint < 0 || destEndpoint > 255)
			throw new IllegalArgumentException("Destination endpoint must be between 0 and 255.");
		if (clusterID.length != 2)
			throw new IllegalArgumentException("Cluster ID length must be 2 bytes.");
		if (profileID.length != 2)
			throw new IllegalArgumentException("Profile ID length must be 2 bytes.");
		
		this.sourceEndpoint = sourceEndpoint;
		this.destEndpoint = destEndpoint;
		this.clusterID = clusterID;
		this.profileID = profileID;
	}
	
	/**
	 * Returns the endpoint of the source that initiated the transmission.
	 * 
	 * @return The endpoint of the source that initiated the transmission.
	 */
	public int getSourceEndpoint() {
		return sourceEndpoint;
	}
	
	/**
	 * Returns the endpoint of the destination the message was addressed to.
	 * 
	 * @return The endpoint of the destination the message was addressed to.
	 */
	public int getDestinationEndpoint() {
		return destEndpoint;
	}
	
	/**
	 * Returns the cluster ID the packet was addressed to.
	 * 
	 * @return The cluster ID the packet was addressed to.
	 */
	public byte[] getClusterID() {
		return clusterID;
	}
	
	/**
	 * Returns the profile ID the packet was addressed to.
	 * 
	 * @return The profile ID the packet was addressed to.
	 */
	public byte[] getProfileID() {
		return profileID;
	}
}

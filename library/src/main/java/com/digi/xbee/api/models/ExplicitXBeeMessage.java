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
	private final int sourceEndpoint;
	private final int destEndpoint;
	private final int clusterID;
	private final int profileID;
	
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
	 *                                  if {@code sourceEndpoint > 0xFF} or 
	 *                                  if {@code destEndpoint < 0} or 
	 *                                  if {@code destEndpoint > 0xFF} or 
	 *                                  if {@code clusterID < 0} or 
	 *                                  if {@code clusterID > 0xFFFF} or 
	 *                                  if {@code profileID < 0} or 
	 *                                  if {@code profileID > 0xFFFF}.
	 * @throws NullPointerException if {@code remoteXBeeDevice == null} or
	 *                              if {@code data == null}.
	 * 
	 * @see com.digi.xbee.api.RemoteXBeeDevice
	 */
	public ExplicitXBeeMessage(RemoteXBeeDevice remoteXBeeDevice, int sourceEndpoint, int destEndpoint, int clusterID, int profileID, byte[] data) {
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
	 *                                  if {@code sourceEndpoint > 0xFF} or 
	 *                                  if {@code destEndpoint < 0} or 
	 *                                  if {@code destEndpoint > 0xFF} or 
	 *                                  if {@code clusterID < 0} or 
	 *                                  if {@code clusterID > 0xFFFF} or 
	 *                                  if {@code profileID < 0} or 
	 *                                  if {@code profileID > 0xFFFF}.
	 * @throws NullPointerException if {@code remoteXBeeDevice == null} or
	 *                              if {@code data == null}.
	 * 
	 * @see com.digi.xbee.api.RemoteXBeeDevice
	 */
	public ExplicitXBeeMessage(RemoteXBeeDevice remoteXBeeDevice, int sourceEndpoint, int destEndpoint, int clusterID, int profileID, byte[] data, boolean isBroadcast) {
		super(remoteXBeeDevice, data, isBroadcast);
		
		if (sourceEndpoint < 0 || sourceEndpoint > 0xFF)
			throw new IllegalArgumentException("Source endpoint must be between 0 and 0xFF.");
		if (destEndpoint < 0 || destEndpoint > 0xFF)
			throw new IllegalArgumentException("Destination endpoint must be between 0 and 0xFF.");
		if (clusterID < 0 || clusterID > 0xFFFF)
			throw new IllegalArgumentException("Cluster ID must be between 0 and 0xFF.");
		if (profileID < 0 || profileID > 0xFFFF)
			throw new IllegalArgumentException("Profile ID must be between 0 and 0xFF.");
		
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
	public int getClusterID() {
		return clusterID;
	}
	
	/**
	 * Returns the profile ID the packet was addressed to.
	 * 
	 * @return The profile ID the packet was addressed to.
	 */
	public int getProfileID() {
		return profileID;
	}
}

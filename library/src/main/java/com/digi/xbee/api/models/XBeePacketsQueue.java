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

import java.util.LinkedList;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ReceivePacket;
import com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket;
import com.digi.xbee.api.packet.raw.RX64IOPacket;
import com.digi.xbee.api.packet.raw.RX64Packet;

/**
 * This class represents a queue of XBee packets used for sequential packets 
 * reading within the XBee Java API. 
 * 
 * <p>The class provides some methods to get specific packet types from different 
 * source nodes.</p>
 * 
 * @see XBeePacket
 */
public class XBeePacketsQueue {

	// Constants.
	private static final int DEFAULT_MAX_LENGTH = 50;
	
	// Variables.
	private int maxLength = DEFAULT_MAX_LENGTH;
	
	private static LinkedList<XBeePacket> packetsList;
	
	/**
	 * Class constructor. Instances a new object of type {@code XBeePacketsQueue}.
	 */
	public XBeePacketsQueue() {
		this(DEFAULT_MAX_LENGTH);
	}
	
	/**
	 * Class constructor. Instances a new object of type {@code XBeePacketsQueue} 
	 * with the given maximum length.
	 * 
	 * @param maxLength Maximum length of the queue.
	 * 
	 * @throws IllegalArgumentException if {@code maxLength < 1}.
	 */
	public XBeePacketsQueue(int maxLength) {
		if (maxLength < 1)
			throw new IllegalArgumentException("Queue length must be greater than 0.");
		
		this.maxLength = maxLength;
		packetsList = new LinkedList<XBeePacket>();
	}
	
	/**
	 * Adds the provided packet to the list of packets. If the queue is full the 
	 * first packet will be discarded to add the given one.
	 * 
	 * @param xbeePacket The XBee packet to be added to the list.
	 * 
	 * @see XBeePacket
	 */
	public void addPacket(XBeePacket xbeePacket) {
		if (packetsList.size() == maxLength)
			packetsList.removeFirst();
		packetsList.add(xbeePacket);
	}
	
	/**
	 * Clears the list of packets.
	 */
	public void clearQueue() {
		packetsList.clear();
	}
	
	/**
	 * Retrieves the first packet from the queue. Null if the queue is empty.
	 * 
	 * @return The first packet from the queue, null if it is empty.
	 * 
	 * @see XBeePacket
	 */
	public XBeePacket getFirstPacket() {
		if (packetsList.isEmpty())
			return null;
		return packetsList.pop();
	}
	
	/**
	 * Retrieves the first packet from the queue which its 64-bit source address matches with 
	 * the address of the provided remote XBee device.
	 * 
	 * @param remoteXBeeDevice The XBee device containing the 64-bit address to look for in 
	 *                         the list of packets.
	 * @return The first XBee packet which its 64-bit address matches with the address of 
	 *         the provided remote XBee device.
	 * 
	 * @see RemoteXBeeDevice
	 * @see XBeePacket
	 */
	public XBeePacket getFirstPacketFrom(RemoteXBeeDevice remoteXBeeDevice) {
		for (int i = 0; i < packetsList.size(); i++) {
			XBeePacket xbeePacket = packetsList.get(i);
			if (addressesMatch(xbeePacket, remoteXBeeDevice))
				return packetsList.remove(i);
		}
		return null;
	}
	
	/**
	 * Retrieves the first data packet from the queue. Null if the queue is empty or there 
	 * is not any data packet inside.
	 * 
	 * @return The first packet from the queue, null if it is empty or there is not any 
	 *         data packet inside.
	 * 
	 * @see XBeePacket
	 */
	public XBeePacket getFirstDataPacket() {
		if (packetsList.isEmpty())
			return null;
		for (int i = 0; i < packetsList.size(); i++) {
			XBeePacket xbeePacket = packetsList.get(i);
			if (isDataPacket(xbeePacket))
				return packetsList.remove(i);
		}
		return null;
	}
	
	/**
	 * Retrieves the first data packet from the queue which its 64-bit source address matches 
	 * with  the address of the provided remote XBee device.
	 * 
	 * @param remoteXBeeDevice The XBee device containing the 64-bit address to look for in 
	 *                         the list of packets.
	 * @return The first XBee data packet which its 64-bit address matches with the address of 
	 *         the provided remote XBee device.
	 * 
	 * @see RemoteXBeeDevice
	 * @see XBeePacket
	 */
	public XBeePacket getFirstDataPacketFrom(RemoteXBeeDevice remoteXBeeDevice) {
		for (int i = 0; i < packetsList.size(); i++) {
			XBeePacket xbeePacket = packetsList.get(i);
			if (isDataPacket(xbeePacket) && addressesMatch(xbeePacket, remoteXBeeDevice))
				return packetsList.remove(i);
		}
		return null;
	}
	
	/**
	 * Retrieves whether or not the source address of the provided XBee packet matches with the 
	 * address of the given remote XBee device.
	 * 
	 * @param xbeePacket The XBee packet to compare its address with the remote XBee device.
	 * @param remoteXBeeDevice The remote XBee device to compare its address with the XBee packet.
	 * @return True if the source address of the provided packet (if it has) matches with the address of 
	 *         the remote XBee device.
	 * 
	 * @see RemoteXBeeDevice
	 * @see XBeePacket
	 */
	private boolean addressesMatch(XBeePacket xbeePacket, RemoteXBeeDevice remoteXBeeDevice) {
		if (!(xbeePacket instanceof XBeeAPIPacket))
			return false;
		APIFrameType packetType = ((XBeeAPIPacket)xbeePacket).getFrameType();
		switch (packetType) {
		case RECEIVE_PACKET:
			if (((ReceivePacket)xbeePacket).get64bitSourceAddress().equals(remoteXBeeDevice.get64BitAddress()))
				return true;
		case REMOTE_AT_COMMAND_RESPONSE:
			if (((RemoteATCommandResponsePacket)xbeePacket).get64bitSourceAddress().equals(remoteXBeeDevice.get64BitAddress()))
				return true;
		case RX_16:
			// TODO: Uncomment these lines when the get16BitAddress() method of the XBee device is implemented.
			//if (((RX16Packet)xbeePacket).get16bitSourceAddress().equals(remoteXBeeDevice.get16BitAddress()))
			//	return true;
			return false;
		case RX_64:
			if (((RX64Packet)xbeePacket).get64bitSourceAddress().equals(remoteXBeeDevice.get64BitAddress()))
				return true;
		case RX_IO_16:
			// TODO: Uncomment these lines when the get16BitAddress() method of the XBee device is implemented.
			//if (((RX16IOPacket)xbeePacket).get16bitSourceAddress().equals(remoteXBeeDevice.get16BitAddress()))
			//	return true;
			return false;
		case RX_IO_64:
			if (((RX64IOPacket)xbeePacket).get64bitSourceAddress().equals(remoteXBeeDevice.get64BitAddress()))
				return true;
		default:
			return false;
		}
	}
	
	/**
	 * Retrieves whether or not the given XBee packet is a data packet.
	 * 
	 * @param xbeePacket The XBee packet to check if is data packet.
	 * @return True if the XBee packet is a data packet, false otherwise.
	 * 
	 * @see XBeePacket
	 */
	private boolean isDataPacket(XBeePacket xbeePacket) {
		if (!(xbeePacket instanceof XBeeAPIPacket))
			return false;
		APIFrameType packetType = ((XBeeAPIPacket)xbeePacket).getFrameType();
		switch (packetType) {
			case RECEIVE_PACKET:
			case RX_16:
			case RX_64:
				return true;
			default:
				return false;
		}
	}
}

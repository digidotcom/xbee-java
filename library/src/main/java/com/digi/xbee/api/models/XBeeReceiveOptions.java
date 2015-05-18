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

/**
 * This class lists all the possible options that have been set while
 * receiving an XBee packet.
 * 
 * <p>The receive options are usually set as a bitfield meaning that the 
 * options can be combined using the '|' operand.</p>
 */
public class XBeeReceiveOptions {

	/**
	 * No special receive options (value: {@value}).
	 */
	public static final int NONE = 0x00;
	
	/**
	 * Packet was acknowledged (value: {@value}).
	 * 
	 * <p>Not valid for Wi-Fi protocol</p>
	 */
	public static final int PACKET_ACKNOWLEDGED = 0x01;
	
	/**
	 * Packet was a broadcast packet (value: {@value}).
	 * 
	 * <p>Not valid for Wi-Fi protocol</p>
	 */
	public static final int BROADCAST_PACKET = 0x02;
	
	/**
	 * Packet encrypted with APS encryption (value: {@value}).
	 * 
	 * <p>Only valid for ZigBee XBee protocol.</p>
	 */
	public static final int APS_ENCRYPTED = 0x20;
	
	/**
	 * Packet was sent from an end device, if known (value: {@value}).
	 * 
	 * <p>Only valid for ZigBee XBee protocol.</p>
	 */
	public static final int SENT_FROM_END_DEVICE = 0x40;
}

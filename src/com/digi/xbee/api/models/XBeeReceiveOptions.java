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

/**
 * This class lists all the possible options that have been set while
 * receiving an XBee packet. The receive options are usually
 * set as a bitfield meaning that the options can be combined using
 * the '|' operand.
 */
public class XBeeReceiveOptions {

	/**
	 * No special receive options.
	 */
	public static final int NONE = 0x00;
	
	/**
	 * Packet was acknowledged. Not valid for WiFi protocol.
	 */
	public static final int PACKET_ACKNOWLEDGED = 0x01;
	
	/**
	 * Packet was a broadcast packet. Not valid for WiFi protocol.
	 */
	public static final int BROADCAST_PACKET = 0x02;
	
	/**
	 * Packet encrypted with APS encryption. Only valid for ZigBee
	 * XBee protocol.
	 */
	public static final int APS_ENCRYPTED = 0x20;
	
	/**
	 * Packet was sent from an end device (if known). Only valid 
	 * for ZigBee XBee protocol.
	 */
	public static final int SENT_FROM_END_DEVICE = 0x40;
}

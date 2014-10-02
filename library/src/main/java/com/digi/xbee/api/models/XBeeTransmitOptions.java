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
 * This class lists all the possible options that can be set while transmitting 
 * an XBee packet.
 * 
 * <p>The transmit options are usually set as a bitfield meaning that the 
 * options can be combined using the '|' operand.</p>
 */
public class XBeeTransmitOptions {

	/**
	 * No special transmit options (value: {@value}).
	 */
	public static final int NONE = 0x00;
	
	/**
	 * Disables ACK (value: {@value}).
	 */
	public static final int DISABLE_ACK = 0x01;
	
	/**
	 * Applies changes in the remote device. If this option is
	 * not set, an AC command must be sent before changes will take effect.
	 */
	public final static int APPLY_CHANGES = 0x02;
	
	/**
	 * Sends packet with broadcast {@code PAN ID}. Packet will be sent to all 
	 * devices in the same channel ignoring the {@code PAN ID} (value: {@value}).
	 * 
	 * <p>It cannot be combined with other options. Only valid for 
	 * 802.15.4 XBee protocol.</p>
	 */
	public static final int USE_BROADCAST_PAN_ID = 0x04;
	
	/**
	 * Enables APS encryption, only if {@code EE=1} (value: {@value}).
	 * 
	 * <p>Enabling APS encryption decreases the maximum number of RF payload 
	 * bytes by 4 (below the value reported by {@code NP}).</p>
	 * 
	 * <p>Only valid for ZigBee XBee protocol.</p>
	 */
	public static final int ENABLE_APS_ENCRYPTION = 0x20;
	
	/**
	 * Uses the extended transmission timeout (value: {@value}).
	 * 
	 * <p>Setting the extended timeout bit causes the stack to set the extended 
	 * transmission timeout for the destination address.</p>
	 * 
	 * <p>Only valid for ZigBee XBee protocol.</p>
	 */
	public static final int USE_EXTENDED_TIMEOUT = 0x40;
}

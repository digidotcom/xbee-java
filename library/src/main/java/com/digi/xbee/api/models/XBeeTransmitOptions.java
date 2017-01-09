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

/**
 * This class lists all the possible options that can be set while 
 * transmitting an XBee data packet.
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
	 * Disables acknowledgments on all unicasts (value: {@value}).
	 * 
	 * <p>Only valid for DigiMesh, 802.15.4 and Point-to-multipoint 
	 * protocols.</p>
	 */
	public static final int DISABLE_ACK = 0x01;
	
	/**
	 * Disables the retries and router repair in the frame (value: {@value}).
	 * 
	 * <p>Only valid for ZigBee protocol.</p>
	 */
	public static final int DISABLE_RETRIES_AND_REPAIR = 0x01;
	
	/**
	 * Doesn't attempt Route Discovery (value: {@value}).
	 * 
	 * <p>Disables Route Discovery on all DigiMesh unicasts.</p>
	 * 
	 * <p>Only valid for DigiMesh protocol.</p>
	 */
	public static final int DONT_ATTEMPT_RD = 0x02;
	
	/**
	 * Sends packet with broadcast {@code PAN ID}. Packet will be sent to all 
	 * devices in the same channel ignoring the {@code PAN ID} 
	 * (value: {@value}).
	 * 
	 * <p>It cannot be combined with other options.</p>
	 * 
	 * <p>Only valid for 802.15.4 XBee protocol.</p>
	 */
	public static final int USE_BROADCAST_PAN_ID = 0x04;
	
	/**
	 * Enables unicast NACK messages (value: {@value}).
	 * 
	 * <p>NACK message is enabled on the packet.</p>
	 * 
	 * <p>Only valid for DigiMesh 868/900 protocol.</p>
	 */
	public static final int ENABLE_UNICAST_NACK = 0x04;
	
	/**
	 * Enables unicast trace route messages (value: {@value}).
	 * 
	 * <p>Trace route is enabled on the packets.</p>
	 * 
	 * <p>Only valid for DigiMesh 868/900 protocol.</p>
	 */
	public static final int ENABLE_UNICAST_TRACE_ROUTE = 0x04;
	
	/**
	 * Enables multicast transmission request (value: {@value}).
	 * 
	 * <p>Only valid for ZigBee XBee protocol.</p>
	 */
	public static final int ENABLE_MULTICAST = 0x08;
	
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
	 * <p>Setting the extended timeout bit causes the stack to set the 
	 * extended transmission timeout for the destination address.</p>
	 * 
	 * <p>Only valid for ZigBee XBee protocol.</p>
	 */
	public static final int USE_EXTENDED_TIMEOUT = 0x40;
	
	/**
	 * Transmission is performed using point-to-Multipoint mode 
	 * (value: {@value}).
	 * 
	 * <p>Only valid for DigiMesh 868/900 and Point-to-Multipoint 868/900 
	 * protocols.</p>
	 */
	public static final int POINT_MULTIPOINT_MODE = 0x40;
	
	/**
	 * Transmission is performed using repeater mode (value: {@value}).
	 * 
	 * <p>Only valid for DigiMesh 868/900 and Point-to-Multipoint 868/900 
	 * protocols.</p>
	 */
	public static final int REPEATER_MODE = 0x80;
	
	/**
	 * Transmission is performed using DigiMesh mode (value: {@value}).
	 * 
	 * <p>Only valid for DigiMesh 868/900 and Point-to-Multipoint 868/900 
	 * protocols.</p>
	 */
	public static final int DIGIMESH_MODE = 0xC0;
}

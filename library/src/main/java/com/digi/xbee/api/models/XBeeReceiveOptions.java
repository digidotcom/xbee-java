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

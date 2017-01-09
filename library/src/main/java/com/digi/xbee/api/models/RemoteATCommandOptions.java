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

import com.digi.xbee.api.packet.common.RemoteATCommandPacket;

/**
 * This class lists all the possible options that can be set while transmitting
 * a remote AT Command.
 * 
 * <p>These options are usually set as a bitfield meaning that the options 
 * can be combined using the '|' operand.</p>
 * 
 * @see RemoteATCommandPacket
 */
public class RemoteATCommandOptions {
	
	/**
	 * No special transmit options (value: {@value}).
	 */
	public final static int OPTION_NONE = 0x00;
	
	/**
	 * Disables ACK (value: {@value}).
	 */
	public final static int OPTION_DISABLE_ACK = 0x01;
	
	/**
	 * Applies changes in the remote device (value: {@value}).
	 * 
	 * <p>If this option is not set, AC command must be sent before changes 
	 * will take effect.</p>
	 */
	public final static int OPTION_APPLY_CHANGES = 0x02;
	
	/**
	 * Uses the extended transmission timeout (value: {@value}).
	 * 
	 * <p>Setting the extended timeout bit causes the stack to set the extended 
	 * transmission timeout for the destination address.</p>
	 * 
	 * <p>Only valid for ZigBee XBee protocol.</p>
	 */
	public final static int OPTION_EXTENDED_TIMEOUT = 0x40;
}

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

import java.util.Set;

/**
 * Enumerates the different options used in the discovery process.
 */
public enum DiscoveryOptions {

	/**
	 * Append device type identifier (DD) to the discovery response.
	 * 
	 * <p>Valid for the following protocols:</p>
	 * <ul>
	 *   <li>DigiMesh</li>
	 *   <li>Point-to-multipoint (Digi Point)</li>
	 *   <li>ZigBee</li>
	 * </ul>
	 */
	APPEND_DD(0x01, "Append device type identifier (DD)"),
	
	/**
	 * Local device sends response frame when discovery is issued.
	 * 
	 * <p>Valid for the following protocols:</p>
	 * <ul>
	 *   <li>DigiMesh</li>
	 *   <li>Point-to-multipoint (Digi Point)</li>
	 *   <li>ZigBee</li>
	 *   <li>802.15.4</li>
	 * </ul>
	 */
	DISCOVER_MYSELF(0x02, "Local device sends response frame"),
	
	/**
	 * Append RSSI of the last hop to the discovery response.
	 * 
	 * <p>Valid for the following protocols:</p>
	 * <ul>
	 *   <li>DigiMesh</li>
	 *   <li>Point-to-multipoint (Digi Point)</li>
	 * </ul>
	 */
	APPEND_RSSI(0x04, "Append RSSI (of the last hop)");
	
	// Variables.
	private final int value;
	
	private final String description;
	
	private DiscoveryOptions(int value, String description) {
		this.value = value;
		this.description = description;
	}
	
	/**
	 * Returns the value of the discovery option.
	 * 
	 * @return The discovery option value.
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Returns the description of the discovery option.
	 * 
	 * @return The discovery option description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Calculates the total value of a combination of several options for the 
	 * given protocol.
	 * 
	 * @param protocol The {@code XBeeProtocol} to calculate the value of all 
	 *                 the given discovery options.
	 * @param options Collection of options to get the final value.
	 * 
	 * @return The value to be configured in the module depending on the given
	 *         collection of options and the protocol.
	 * 
	 * @see XBeeProtocol
	 */
	public static int calculateDiscoveryValue (XBeeProtocol protocol, Set<DiscoveryOptions> options) {
		// Calculate value to be configured.
		int value = 0;
		switch (protocol) {
		case ZIGBEE:
		case ZNET:
			for (DiscoveryOptions op: options) {
				if (op == DiscoveryOptions.APPEND_RSSI)
					continue;
				value = value + op.getValue();
			}
			break;
		case DIGI_MESH:
		case DIGI_POINT:
		case XLR:
			// TODO [XLR_DM] The next version of the XLR will add DigiMesh support.
			// For the moment only point-to-multipoint is supported in this kind of devices.
		case XLR_DM:
			for (DiscoveryOptions op: options)
				value = value + op.getValue();
			break;
		case RAW_802_15_4:
		case UNKNOWN:
		default:
			if (options.contains(DiscoveryOptions.DISCOVER_MYSELF))
				value = 1; // This is different for 802.15.4.
			break;
		}
		return value;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s (%d)", description, value);
	}
}

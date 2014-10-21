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
package com.digi.xbee.api;

import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;

public class RemoteZigBeeDevice extends RemoteXBeeDevice {

	/**
	 * Class constructor. Instantiates a new {@code RemoteXBeeDevice} object 
	 * with the given local {@code ZigBee} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local ZigBee device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote ZigBee device.
	 * @param addr64 The 64-bit address to identify this remote ZigBee device.
	 * 
	 * @throws IllegalArgumentException If {@code localXBeeDevice.isRemote() == true}.
	 * @throws NullPointerException If {@code localXBeeDevice == null} or
	 *                              if {@code addr64 == null}.
	 * 
	 * @see XBee64BitAddress
	 */
	public RemoteZigBeeDevice(ZigBeeDevice localXBeeDevice, XBee64BitAddress addr64) {
		super(localXBeeDevice, addr64);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteXBeeDevice} object 
	 * with the given local {@code XBeeDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local XBee device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote ZigBee device.
	 * @param addr64 The 64-bit address to identify this remote ZigBee device.
	 * 
	 * @throws IllegalArgumentException If {@code localXBeeDevice.isRemote() == true} or 
	 *                                  if {@code localXBeeDevice.getXBeeProtocol() != XBeeProtocol.ZIGBEE}.
	 * @throws NullPointerException If {@code localXBeeDevice == null} or
	 *                              if {@code addr64 == null}.
	 * 
	 * @see XBee64BitAddress
	 */
	public RemoteZigBeeDevice(XBeeDevice localXBeeDevice, XBee64BitAddress addr64) {
		super(localXBeeDevice, addr64);
		
		// Verify the local device has ZigBee protocol.
		if (localXBeeDevice.getXBeeProtocol() != XBeeProtocol.ZIGBEE)
			throw new IllegalArgumentException("The protocol of the local XBee device is not " + XBeeProtocol.ZIGBEE.getDescription() + ".");
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteXBeeDevice} object 
	 * with the given local {@code XBeeDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local XBee device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote ZigBee device.
	 * @param addr64 The 64-bit address to identify this remote ZigBee device.
	 * @param addr16 The 16-bit address to identify this remote ZigBee device. 
	 *               It might be {@code null}.
	 * @param ni The node identifier of this remote ZigBee device. It might be 
	 *           {@code null}.
	 * 
	 * @throws IllegalArgumentException If {@code localXBeeDevice.isRemote() == true} or 
	 *                                  if {@code localXBeeDevice.getXBeeProtocol() != XBeeProtocol.ZIGBEE}.
	 * @throws NullPointerException If {@code localXBeeDevice == null} or
	 *                              if {@code addr64 == null}.
	 * 
	 * @see XBee64BitAddress
	 * @see XBee16BitAddress
	 */
	public RemoteZigBeeDevice(XBeeDevice localXBeeDevice, XBee64BitAddress addr64, 
			XBee16BitAddress addr16, String ni) {
		super(localXBeeDevice, addr64, addr16, ni);
		
		// Verify the local device has ZigBee protocol.
		if (localXBeeDevice.getXBeeProtocol() != XBeeProtocol.ZIGBEE)
			throw new IllegalArgumentException("The protocol of the local XBee device is not " + XBeeProtocol.ZIGBEE.getDescription() + ".");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#getXBeeProtocol()
	 */
	@Override
	public XBeeProtocol getXBeeProtocol() {
		return XBeeProtocol.ZIGBEE;
	}
}

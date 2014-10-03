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

import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;

public class RemoteDigiPointDevice extends RemoteXBeeDevice {

	/**
	 * Class constructor. Instantiates a new {@code RemoteXBeeDevice} object 
	 * with the given local {@code DigiPointDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local point-to-multipoint device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote point-to-multipoint device.
	 * @param xbee64BitAddress The 64-bit address to identify this remote point-to-multipoint 
	 *                         device.
	 * 
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code xbee64BitAddress == null}.
	 * 
	 * @see XBee64BitAddress
	 */
	public RemoteDigiPointDevice(DigiPointDevice localXBeeDevice, XBee64BitAddress xbee64BitAddress) {
		super(localXBeeDevice, xbee64BitAddress);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteXBeeDevice} object 
	 * with the given local {@code XBeeDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local XBee device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote point-to-multipoint device.
	 * @param xbee64BitAddress The 64-bit address to identify this remote point-to-multipoint 
	 *                         device.
	 * 
	 * @throws IllegalArgumentException if {@code localXBeeDevice.getXBeeProtocol() != XBeeProtocol.DIGI_POINT}.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code xbee64BitAddress == null}.
	 * 
	 * @see XBee64BitAddress
	 */
	public RemoteDigiPointDevice(XBeeDevice localXBeeDevice, XBee64BitAddress xbee64BitAddress) {
		super(localXBeeDevice, xbee64BitAddress);
		
		// Verify the local device has point-to-multipoint protocol.
		if (localXBeeDevice.getXBeeProtocol() != XBeeProtocol.DIGI_POINT)
			throw new IllegalArgumentException("The protocol of the local XBee device is not " + XBeeProtocol.DIGI_POINT.getDescription() + ".");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#getXBeeProtocol()
	 */
	@Override
	public XBeeProtocol getXBeeProtocol() {
		return XBeeProtocol.DIGI_POINT;
	}
}

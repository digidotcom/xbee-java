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

public class RemoteRaw802Device extends RemoteXBeeDevice {

	/**
	 * Class constructor. Instantiates a new {@code RemoteXBeeDevice} object 
	 * with the given local {@code Raw802Device} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local 802.15.4 device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote 802.15.4 device.
	 * @param xbee64BitAddress The 64-bit address to identify this remote 802.15.4 
	 *                         device.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code xbee64BitAddress == null}.
	 * 
	 * @see XBee64BitAddress
	 */
	public RemoteRaw802Device(Raw802Device localXBeeDevice, XBee64BitAddress xbee64BitAddress) {
		super(localXBeeDevice, xbee64BitAddress);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteXBeeDevice} object 
	 * with the given local {@code XBeeDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local XBee device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote 802.15.4 device.
	 * @param xbee64BitAddress The 64-bit address to identify this remote 802.15.4 
	 *                         device.
	 * 
	 * @throws IllegalArgumentException if {@code localXBeeDevice.getXBeeProtocol() != XBeeProtocol.RAW_802_15_4}.
	 *                              if {@code xbee64BitAddress == null}.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code xbee64BitAddress == null}.
	 * 
	 * @see XBee64BitAddress
	 */
	public RemoteRaw802Device(XBeeDevice localXBeeDevice, XBee64BitAddress xbee64BitAddress) {
		super(localXBeeDevice, xbee64BitAddress);
		
		// Verify the local device has 802.15.4 protocol.
		if (localXBeeDevice.getXBeeProtocol() != XBeeProtocol.RAW_802_15_4)
			throw new IllegalArgumentException("The protocol of the local XBee device is not " + XBeeProtocol.RAW_802_15_4.getDescription() + ".");
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteXBeeDevice} object 
	 * with the given local {@code Raw802Device} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local 802.15.4 device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote 802.15.4 device.
	 * @param xbee64BitAddress The 16-bit address to identify this remote 802.15.4 
	 *                         device.
	 * 
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code xbee16BitAddress == null}.
	 * 
	 * @see XBee16BitAddress
	 */
	public RemoteRaw802Device(Raw802Device localXBeeDevice, XBee16BitAddress xbee16BitAddress) {
		super(localXBeeDevice, XBee64BitAddress.UNKNOWN_ADDRESS);
		
		this.xbee16BitAddress = xbee16BitAddress;
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteXBeeDevice} object 
	 * with the given local {@code XBeeDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local 802.15.4 device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote 802.15.4 device.
	 * @param xbee64BitAddress The 16-bit address to identify this remote 802.15.4 
	 *                         device.
	 * 
	 * @throws IllegalArgumentException if {@code localXBeeDevice.getXBeeProtocol() != XBeeProtocol.RAW_802_15_4}.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code xbee16BitAddress == null}.
	 * 
	 * @see XBee16BitAddress
	 */
	public RemoteRaw802Device(XBeeDevice localXBeeDevice, XBee16BitAddress xbee16BitAddress) {
		super(localXBeeDevice, XBee64BitAddress.UNKNOWN_ADDRESS);
		
		// Verify the local device has 802.15.4 protocol.
		if (localXBeeDevice.getXBeeProtocol() != XBeeProtocol.RAW_802_15_4)
			throw new IllegalArgumentException("The protocol of the local XBee device is not " + XBeeProtocol.RAW_802_15_4.getDescription() + ".");
		
		this.xbee16BitAddress = xbee16BitAddress;
	}
	
	/**
	 * Sets the XBee64BitAddress of this remote 802.15.4 device.
	 * 
	 * @param xbee64BitAddress The XBee64BitAddress to be set to the device.
	 * 
	 * @see XBee64BitAddress
	 */
	public void set64BitAddress(XBee64BitAddress xbee64BitAddress) {
		this.xbee64BitAddress = xbee64BitAddress;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#getXBeeProtocol()
	 */
	@Override
	public XBeeProtocol getXBeeProtocol() {
		return XBeeProtocol.RAW_802_15_4;
	}
}

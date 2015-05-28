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
package com.digi.xbee.api;

import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;

/**
 * This class represents a remote DigiPoint device.
 * 
 * @see RemoteXBeeDevice
 * @see RemoteDigiMeshDevice
 * @see RemoteRaw802Device
 * @see RemoteZigBeeDevice
 */
public class RemoteDigiPointDevice extends RemoteXBeeDevice {

	/**
	 * Class constructor. Instantiates a new {@code RemoteDigiPointDevice} object 
	 * with the given local {@code DigiPointDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local point-to-multipoint device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote point-to-multipoint device.
	 * @param addr64 The 64-bit address to identify this remote point-to-multipoint 
	 *               device.
	 * 
	 * @throws IllegalArgumentException if {@code localXBeeDevice.isRemote() == true}.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code addr64 == null}.
	 * 
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public RemoteDigiPointDevice(DigiPointDevice localXBeeDevice, XBee64BitAddress addr64) {
		super(localXBeeDevice, addr64);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteDigiPointDevice} object 
	 * with the given local {@code XBeeDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local XBee device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote point-to-multipoint device.
	 * @param addr64 The 64-bit address to identify this remote point-to-multipoint 
	 *               device.
	 * 
	 * @throws IllegalArgumentException if {@code localXBeeDevice.isRemote() == true} or 
	 *                                  if {@code localXBeeDevice.getXBeeProtocol() != XBeeProtocol.DIGI_POINT}.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code addr64 == null}.
	 * 
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public RemoteDigiPointDevice(XBeeDevice localXBeeDevice, XBee64BitAddress addr64) {
		super(localXBeeDevice, addr64);
		
		// Verify the local device has point-to-multipoint protocol.
		if (localXBeeDevice.getXBeeProtocol() != XBeeProtocol.DIGI_POINT)
			throw new IllegalArgumentException("The protocol of the local XBee device is not " + XBeeProtocol.DIGI_POINT.getDescription() + ".");
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteDigiPointDevice} object 
	 * with the given local {@code XBeeDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local XBee device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote point-to-multipoint device.
	 * @param addr64 The 64-bit address to identify this remote point-to-multipoint 
	 *               device.
	 * @param id The node identifier of this remote point-to-multipoint device. 
	 *           It might be {@code null}.
	 * 
	 * @throws IllegalArgumentException if {@code localXBeeDevice.isRemote() == true} or 
	 *                                  if {@code localXBeeDevice.getXBeeProtocol() != XBeeProtocol.DIGI_POINT}.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code addr64 == null}.
	 * 
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public RemoteDigiPointDevice(XBeeDevice localXBeeDevice, XBee64BitAddress addr64, String id) {
		super(localXBeeDevice, addr64, null, id);
		
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

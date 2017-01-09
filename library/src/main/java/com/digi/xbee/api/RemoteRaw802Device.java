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
package com.digi.xbee.api;

import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.AssociationIndicationStatus;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;

/**
 * This class represents a remote 802.15.4 device.
 * 
 * @see RemoteXBeeDevice
 * @see RemoteDigiMeshDevice
 * @see RemoteDigiPointDevice
 * @see RemoteZigBeeDevice
 */
public class RemoteRaw802Device extends RemoteXBeeDevice {

	/**
	 * Class constructor. Instantiates a new {@code RemoteRaw802Device} object 
	 * with the given local {@code Raw802Device} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local 802.15.4 device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote 802.15.4 device.
	 * @param addr64 The 64-bit address to identify this remote 802.15.4 device.
	 * 
	 * @throws IllegalArgumentException if {@code localXBeeDevice.isRemote() == true}.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code addr64 == null}.
	 * 
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public RemoteRaw802Device(Raw802Device localXBeeDevice, XBee64BitAddress addr64) {
		super(localXBeeDevice, addr64);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteRaw802Device} object 
	 * with the given local {@code XBeeDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local XBee device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote 802.15.4 device.
	 * @param addr64 The 64-bit address to identify this remote 802.15.4 device.
	 * @param addr16 The 16-bit address to identify this remote 802.15.4 device. 
	 *               It might be {@code null}.
	 * @param id The node identifier of this remote 802.15.4 device. It might be 
	 *           {@code null}.
	 * 
	 * @throws IllegalArgumentException if {@code localXBeeDevice.isRemote() == true} or  
	 *                                  if {@code localXBeeDevice.getXBeeProtocol() != XBeeProtocol.RAW_802_15_4}
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code addr64 == null}.
	 * 
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public RemoteRaw802Device(XBeeDevice localXBeeDevice, XBee64BitAddress addr64, 
			XBee16BitAddress addr16, String id) {
		super(localXBeeDevice, addr64, addr16, id);
		
		// Verify the local device has 802.15.4 protocol.
		if (localXBeeDevice.getXBeeProtocol() != XBeeProtocol.RAW_802_15_4)
			throw new IllegalArgumentException("The protocol of the local XBee device is not " + XBeeProtocol.RAW_802_15_4.getDescription() + ".");
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteRaw802Device} object 
	 * with the given local {@code Raw802Device} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local 802.15.4 device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote 802.15.4 device.
	 * @param addr16 The 16-bit address to identify this remote 802.15.4 
	 *               device.
	 * 
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code addr16 == null}.
	 * 
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 */
	public RemoteRaw802Device(Raw802Device localXBeeDevice, XBee16BitAddress addr16) {
		super(localXBeeDevice, XBee64BitAddress.UNKNOWN_ADDRESS);
		
		this.xbee16BitAddress = addr16;
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteRaw802Device} object 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local 802.15.4 device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote 802.15.4 device.
	 * @param addr16 The 16-bit address to identify this remote 802.15.4 
	 *               device.
	 * 
	 * @throws IllegalArgumentException if {@code localXBeeDevice.getXBeeProtocol() != XBeeProtocol.RAW_802_15_4}.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code addr16 == null}.
	 * 
	 * @see com.digi.xbee.api.models.XBee16BitAddress
	 */
	public RemoteRaw802Device(XBeeDevice localXBeeDevice, XBee16BitAddress addr16) {
		super(localXBeeDevice, XBee64BitAddress.UNKNOWN_ADDRESS);
		
		// Verify the local device has 802.15.4 protocol.
		if (localXBeeDevice.getXBeeProtocol() != XBeeProtocol.RAW_802_15_4)
			throw new IllegalArgumentException("The protocol of the local XBee device is not " + XBeeProtocol.RAW_802_15_4.getDescription() + ".");
		
		this.xbee16BitAddress = addr16;
	}
	
	/**
	 * Sets the XBee64BitAddress of this remote 802.15.4 device.
	 * 
	 * @param addr64 The 64-bit address to be set to the device.
	 * 
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public void set64BitAddress(XBee64BitAddress addr64) {
		this.xbee64BitAddress = addr64;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#getXBeeProtocol()
	 */
	@Override
	public XBeeProtocol getXBeeProtocol() {
		return XBeeProtocol.RAW_802_15_4;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#set16BitAddress(com.digi.xbee.api.models.XBee16BitAddress)
	 */
	@Override
	public void set16BitAddress(XBee16BitAddress xbee16BitAddress) throws TimeoutException, XBeeException {
		super.set16BitAddress(xbee16BitAddress);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#getAssociationIndicationStatus()
	 */
	@Override
	public AssociationIndicationStatus getAssociationIndicationStatus() throws TimeoutException, XBeeException {
		return super.getAssociationIndicationStatus();
	}
}
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

import java.io.IOException;

import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;

public class RemoteXBeeDevice extends AbstractXBeeDevice {

	/**
	 * Class constructor. Instantiates a new {@code RemoteXBeeDevice} object 
	 * with the given local {@code XBeeDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local XBee device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote XBee device.
	 * @param xbee64BitAddress The 64-bit address to identify this remote XBee 
	 *                         device.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code xbee64BitAddress == null}.
	 * 
	 * @see XBee64BitAddress
	 */
	public RemoteXBeeDevice(XBeeDevice localXBeeDevice, XBee64BitAddress xbee64BitAddress) {
		super(localXBeeDevice, xbee64BitAddress);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#readDeviceInfo()
	 */
	public void readDeviceInfo() throws TimeoutException, XBeeException {
		super.readDeviceInfo();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#isRemote()
	 */
	@Override
	public boolean isRemote() {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#reset()
	 */
	@Override
	public void reset() throws TimeoutException, XBeeException {
		// Check connection.
		if (!connectionInterface.isOpen())
			throw new InterfaceNotOpenException();
		
		logger.info(toString() + "Resetting the remote module ({})...", get64BitAddress());
		
		ATCommandResponse response = null;
		try {
			response = sendATCommand(new ATCommand("FR"));
		} catch (IOException e) {
			throw new XBeeException("Error writing in the communication interface.", e);
		} catch (TimeoutException e) {
			// Remote 802.15.4 devices do not respond to the AT command.
			if (localXBeeDevice.getXBeeProtocol() == XBeeProtocol.RAW_802_15_4)
				return;
			else
				throw e;
		}
		
		// Check if AT Command response is valid.
		checkATCommandResponseIsValid(response);
	}
}

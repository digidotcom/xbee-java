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

import java.net.Inet6Address;

import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;

/**
 * This class represents a remote DigiMesh device.
 * 
 * @see RemoteDigiPointDevice
 * @see RemoteRaw802Device
 * @see RemoteThreadDevice
 * @see RemoteXBeeDevice
 * @see RemoteZigBeeDevice
 */
public class RemoteDigiMeshDevice extends RemoteXBeeDevice {

	// Constants
	private static final String OPERATION_EXCEPTION = "Operation not supported in DigiMesh protocol.";
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteDigiMeshDevice} object 
	 * with the given local {@code DigiMeshDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local DigiMesh device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote DigiMesh device.
	 * @param addr64 The 64-bit address to identify this remote DigiMesh device.
	 * 
	 * @throws IllegalArgumentException if {@code localXBeeDevice.isRemote() == true}.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code addr64 == null}.
	 * 
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public RemoteDigiMeshDevice(DigiMeshDevice localXBeeDevice, XBee64BitAddress addr64) {
		super(localXBeeDevice, addr64);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteDigiMeshDevice} object 
	 * with the given local {@code XBeeDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local XBee device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote DigiMesh device.
	 * @param addr64 The 64-bit address to identify this remote DigiMesh device.
	 * 
	 * @throws IllegalArgumentException if {@code localXBeeDevice.isRemote() == true} or 
	 *                                  if {@code localXBeeDevice.getXBeeProtocol() != XBeeProtocol.DIGI_MESH}.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code addr64 == null}.
	 * 
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public RemoteDigiMeshDevice(XBeeDevice localXBeeDevice, XBee64BitAddress addr64) {
		super(localXBeeDevice, addr64);
		
		// Verify the local device has DigiMesh protocol.
		if (localXBeeDevice.getXBeeProtocol() != XBeeProtocol.DIGI_MESH)
			throw new IllegalArgumentException("The protocol of the local XBee device is not " + XBeeProtocol.DIGI_MESH.getDescription() + ".");
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteDigiMeshDevice} object 
	 * with the given local {@code XBeeDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local XBee device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote DigiMesh device.
	 * @param addr64 The 64-bit address to identify this remote DigiMesh device.
	 * @param id The node identifier of this remote DigiMesh device. It might 
	 *           be {@code null}.
	 * 
	 * @throws IllegalArgumentException if {@code localXBeeDevice.isRemote() == true} or 
	 *                                  if {@code localXBeeDevice.getXBeeProtocol() != XBeeProtocol.DIGI_MESH}.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code addr64 == null}.
	 * 
	 * @see com.digi.xbee.api.models.XBee64BitAddress
	 */
	public RemoteDigiMeshDevice(XBeeDevice localXBeeDevice, XBee64BitAddress addr64, String id) {
		super(localXBeeDevice, addr64, null, id);
		
		// Verify the local device has DigiMesh protocol.
		if (localXBeeDevice.getXBeeProtocol() != XBeeProtocol.DIGI_MESH)
			throw new IllegalArgumentException("The protocol of the local XBee device is not " + XBeeProtocol.DIGI_MESH.getDescription() + ".");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#getXBeeProtocol()
	 */
	@Override
	public XBeeProtocol getXBeeProtocol() {
		return XBeeProtocol.DIGI_MESH;
	}
	
	/**
	 * @deprecated DigiMesh protocol does not have an associated IPv6 address.
	 */
	@Override
	public Inet6Address getIPv6Address() {
		// DigiMesh protocol does not have IPv6 address.
		return null;
	}
	
	/**
	 * @deprecated Operation not supported in DigiMesh protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public Inet6Address getIPv6DestinationAddress()
			throws TimeoutException, XBeeException {
		// Not supported in DigiMesh.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in DigiMesh protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	public void setIPv6DestinationAddress(Inet6Address ipv6Address)
			throws TimeoutException, XBeeException {
		// Not supported in DigiMesh.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
}

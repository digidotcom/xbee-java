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

import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.AssociationIndicationStatus;
import com.digi.xbee.api.models.ThreadAssociationIndicationStatus;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.utils.ByteUtils;

/**
 * This class represents a remote Thread device.
 * 
 * @see RemoteDigiMeshDevice
 * @see RemoteDigiPointDevice
 * @see RemoteRaw802Device
 * @see RemoteXBeeDevice
 * @see RemoteZigBeeDevice
 * 
 * @since 1.2.1
 */
public class RemoteThreadDevice extends RemoteXBeeDevice {

	// Constants
	private static final String OPERATION_EXCEPTION = "Operation not supported in Thread protocol.";
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteThreadDevice} object 
	 * with the given local {@code ThreadDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local Thread device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote Thread device.
	 * @param ipv6Addr The IPv6 address to identify this remote Thread device.
	 * 
	 * @throws IllegalArgumentException if {@code localXBeeDevice.isRemote() == true}.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code ipv6addr == null}.
	 * 
	 * @see #RemoteThreadDevice(XBeeDevice, Inet6Address)
	 * @see #RemoteThreadDevice(XBeeDevice, Inet6Address, String)
	 * @see com.digi.xbee.api.ThreadDevice
	 * @see java.net.Inet6Address
	 */
	public RemoteThreadDevice(ThreadDevice localXBeeDevice, Inet6Address ipv6Addr) {
		super(localXBeeDevice, ipv6Addr);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteThreadDevice} object 
	 * with the given local {@code XBeeDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local XBee device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote Thread device.
	 * @param ipv6Addr The IPv6 address to identify this remote Thread device.
	 * 
	 * @throws IllegalArgumentException if {@code localXBeeDevice.isRemote() == true} or 
	 *                                  if {@code localXBeeDevice.getXBeeProtocol() != XBeeProtocol.THREAD}.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code ipv6addr == null}.
	 * 
	 * @see #RemoteThreadDevice(ThreadDevice, Inet6Address)
	 * @see #RemoteThreadDevice(XBeeDevice, Inet6Address, String)
	 * @see com.digi.xbee.api.XBeeDevice
	 * @see java.net.Inet6Address
	 */
	public RemoteThreadDevice(XBeeDevice localXBeeDevice, Inet6Address ipv6Addr) {
		super(localXBeeDevice, ipv6Addr);
		
		// Verify the local device has Thread protocol.
		if (localXBeeDevice.getXBeeProtocol() != XBeeProtocol.THREAD)
			throw new IllegalArgumentException("The protocol of the local XBee device is not " + XBeeProtocol.THREAD.getDescription() + ".");
	}
	
	/**
	 * Class constructor. Instantiates a new {@code RemoteThreadDevice} object 
	 * with the given local {@code XBeeDevice} which contains the connection 
	 * interface to be used.
	 * 
	 * @param localXBeeDevice The local XBee device that will behave as 
	 *                        connection interface to communicate with this 
	 *                        remote Thread device.
	 * @param ipv6Addr The IPv6 address to identify this remote Thread device.
	 * @param ni The node identifier of this remote Thread device. It might be 
	 *           {@code null}.
	 * 
	 * @throws IllegalArgumentException if {@code localXBeeDevice.isRemote() == true} or 
	 *                                  if {@code localXBeeDevice.getXBeeProtocol() != XBeeProtocol.THREAD}.
	 * @throws NullPointerException if {@code localXBeeDevice == null} or
	 *                              if {@code ipv6Address == null}.
	 * 
	 * @see #RemoteThreadDevice(ThreadDevice, Inet6Address)
	 * @see #RemoteThreadDevice(XBeeDevice, Inet6Address)
	 * @see com.digi.xbee.api.XBeeDevice
	 * @see java.net.Inet6Address
	 */
	public RemoteThreadDevice(XBeeDevice localXBeeDevice, Inet6Address ipv6Addr, String ni) {
		super(localXBeeDevice, ipv6Addr, ni);
		
		// Verify the local device has Thread protocol.
		if (localXBeeDevice.getXBeeProtocol() != XBeeProtocol.THREAD)
			throw new IllegalArgumentException("The protocol of the local XBee device is not " + XBeeProtocol.THREAD.getDescription() + ".");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#getXBeeProtocol()
	 */
	@Override
	public XBeeProtocol getXBeeProtocol() {
		return XBeeProtocol.THREAD;
	}
	
	/**
	 * @deprecated Operation not supported in Thread protocol. This method
	 *             will raise an {@link UnsupportedOperationException}.
	 */
	@Override
	protected AssociationIndicationStatus getAssociationIndicationStatus()
			throws TimeoutException, XBeeException {
		// Not supported in Thread.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * Returns the current association status of this Thread device.
	 * 
	 * @return The association indication status of the Thread device.
	 * 
	 * @throws InterfaceNotOpenException if this device connection is not open.
	 * @throws TimeoutException if there is a timeout getting the association 
	 *                          indication status.
	 * @throws XBeeException if there is any other XBee related exception.
	 * 
	 * @see com.digi.xbee.api.models.ThreadAssociationIndicationStatus
	 */
	public ThreadAssociationIndicationStatus getThreadAssociationIndicationStatus() throws TimeoutException, 
			XBeeException {
		byte[] associationIndicationValue = getParameter("AI");
		return ThreadAssociationIndicationStatus.get(ByteUtils.byteArrayToInt(associationIndicationValue));
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.AbstractXBeeDevice#forceDisassociate()
	 */
	@Override
	public void forceDisassociate() throws TimeoutException, XBeeException {
		super.forceDisassociate();
	}
	
	/**
	 * @deprecated This protocol does not have an associated 16-bit address.
	 */
	@Override
	public XBee16BitAddress get16BitAddress() {
		// IPv6 modules do not have 16-bit address.
		return null;
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. Use
	 *             {@link #getIPv6DestinationAddress()} instead.
	 *             This method will raise an 
	 *             {@link UnsupportedOperationException}.
	 */
	@Override
	public XBee64BitAddress getDestinationAddress() throws TimeoutException,
			XBeeException {
		// Not supported in IPv6 modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
	
	/**
	 * @deprecated Operation not supported in this protocol. Use
	 *             {@link #setIPv6DestinationAddress(Inet6Address)} instead.
	 *             This method will raise an 
	 *             {@link UnsupportedOperationException}.
	 */
	@Override
	public void setDestinationAddress(XBee64BitAddress xbee64BitAddress)
			throws TimeoutException, XBeeException {
		// Not supported in IPv6 modules.
		throw new UnsupportedOperationException(OPERATION_EXCEPTION);
	}
}

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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.TransmitException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.packet.common.ExplicitAddressingPacket;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ZigBeeDevice.class})
public class SendMulticastDataTest {
	
	// Constants.
	private static final XBee16BitAddress XBEE_16BIT_ADDRESS = new XBee16BitAddress("0123");
	
	private static final byte[] CLUSTER_ID = new byte[]{0x15, 0x54};
	private static final byte[] PROFILE_ID = new byte[]{(byte) 0xC1, 0x05};
	
	private static final int SOURCE_ENDPOINT = 0xA0;
	private static final int DESTINATION_ENDPOINT = 0xA1;
	
	private static final String DATA = "data";
	
	// Variables.
	private ZigBeeDevice zigBeeDevice;
	
	private ExplicitAddressingPacket explicitAddressingPacket;
	
	@Before
	public void setup() throws Exception {
		// Instantiate a ZigBee object with a mocked interface.
		zigBeeDevice = PowerMockito.spy(new ZigBeeDevice(Mockito.mock(SerialPortRxTx.class)));
		
		// Mock Explicit Addressing packet.
		explicitAddressingPacket = Mockito.mock(ExplicitAddressingPacket.class);
		
		// Whenever an ExplicitAddressingPacket class is instantiated, the mocked transmitPacket packet should be returned.
		PowerMockito.whenNew(ExplicitAddressingPacket.class).withAnyArguments().thenReturn(explicitAddressingPacket);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendMulticastData(XBee16BitAddress, int, int, byte[], byte[], byte[])}.
	 * 
	 * <p>Verify that multicast data cannot be sent if the 16-bit address is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendMulticastData64BitAddrNull() throws TimeoutException, XBeeException {
		zigBeeDevice.sendMulticastData(null, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendMulticastData(XBee16BitAddress, int, int, byte[], byte[], byte[])}.
	 * 
	 * <p>Verify that multicast data cannot be sent if the source endpoint is negative.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSendMulticastDataSourceEndpointNegative() throws TimeoutException, XBeeException {
		zigBeeDevice.sendMulticastData(XBEE_16BIT_ADDRESS, -44, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendMulticastData(XBee16BitAddress, int, int, byte[], byte[], byte[])}.
	 * 
	 * <p>Verify that multicast data cannot be sent if the source endpoint is greater than 255.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSendMulticastDataSourceEndpointGreater() throws TimeoutException, XBeeException {
		zigBeeDevice.sendMulticastData(XBEE_16BIT_ADDRESS, 256, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendMulticastData(XBee16BitAddress, int, int, byte[], byte[], byte[])}.
	 * 
	 * <p>Verify that multicast data cannot be sent if the source endpoint is negative.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSendMulticastDataDestinationEndpointNegative() throws TimeoutException, XBeeException {
		zigBeeDevice.sendMulticastData(XBEE_16BIT_ADDRESS, SOURCE_ENDPOINT, -59, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendMulticastData(XBee16BitAddress, int, int, byte[], byte[], byte[])}.
	 * 
	 * <p>Verify that multicast data cannot be sent if the source endpoint is greater than 255.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSendMulticastDataDestinationEndpointGreater() throws TimeoutException, XBeeException {
		zigBeeDevice.sendMulticastData(XBEE_16BIT_ADDRESS, SOURCE_ENDPOINT, 256, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendMulticastData(XBee16BitAddress, int, int, byte[], byte[], byte[])}.
	 * 
	 * <p>Verify that multicast data cannot be sent if the cluster ID is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendMulticastDataClusterIDNull() throws TimeoutException, XBeeException {
		zigBeeDevice.sendMulticastData(XBEE_16BIT_ADDRESS, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, null, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendMulticastData(XBee16BitAddress, int, int, byte[], byte[], byte[])}.
	 * 
	 * <p>Verify that multicast data cannot be sent if the cluster ID has an invalid length.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSendMulticastDataClusterIDInvalidLength() throws TimeoutException, XBeeException {
		zigBeeDevice.sendMulticastData(XBEE_16BIT_ADDRESS, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, new byte[]{0x12}, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendMulticastData(XBee16BitAddress, int, int, byte[], byte[], byte[])}.
	 * 
	 * <p>Verify that multicast data cannot be sent if the profile ID is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendMulticastDataProfileIDNull() throws TimeoutException, XBeeException {
		zigBeeDevice.sendMulticastData(XBEE_16BIT_ADDRESS, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, null, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendMulticastData(XBee16BitAddress, int, int, byte[], byte[], byte[])}.
	 * 
	 * <p>Verify that multicast data cannot be sent if the profile ID has an invalid length.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testSendMulticastDataProfileIDInvalidLength() throws TimeoutException, XBeeException {
		zigBeeDevice.sendMulticastData(XBEE_16BIT_ADDRESS, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, new byte[]{0x12}, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendMulticastData(XBee16BitAddress, int, int, byte[], byte[], byte[])}.
	 * 
	 * <p>Verify that multicast data cannot be sent if the data is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendMulticastDataDataNull() throws TimeoutException, XBeeException {
		zigBeeDevice.sendMulticastData(XBEE_16BIT_ADDRESS, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendMulticastData(XBee16BitAddress, int, int, byte[], byte[], byte[])}.
	 * 
	 * <p>Verify that multicast data cannot be sent if the device is not open.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSendMulticastDataConnectionClosed() throws TimeoutException, XBeeException {
		// Throw an Interface not open exception when sending and checking any Explicit Addressing packet.
		Mockito.doThrow(new InterfaceNotOpenException()).when(zigBeeDevice).sendAndCheckXBeePacket(Mockito.any(ExplicitAddressingPacket.class), Mockito.eq(false));
		
		zigBeeDevice.sendMulticastData(XBEE_16BIT_ADDRESS, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendMulticastData(XBee16BitAddress, int, int, byte[], byte[], byte[])}.
	 * 
	 * <p>Verify that multicast data cannot be sent if the device has an invalid operating mode.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSendMulticastDataInvalidOperatingMode() throws TimeoutException, XBeeException {
		// Throw an invalid operating mode exception when sending and checking any Explicit Addressing packet.
		Mockito.doThrow(new InvalidOperatingModeException()).when(zigBeeDevice).sendAndCheckXBeePacket(Mockito.any(ExplicitAddressingPacket.class), Mockito.eq(false));
		
		zigBeeDevice.sendMulticastData(XBEE_16BIT_ADDRESS, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendMulticastData(XBee16BitAddress, int, int, byte[], byte[], byte[])}.
	 * 
	 * <p>Verify that multicast data cannot be sent if there is a timeout sending and checking the Explicit Addressing packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TimeoutException.class)
	public void testSendMulticastDataTimeout() throws TimeoutException, XBeeException {
		// Throw a timeout exception when sending and checking any Explicit Addressing packet.
		Mockito.doThrow(new TimeoutException()).when(zigBeeDevice).sendAndCheckXBeePacket(Mockito.any(ExplicitAddressingPacket.class), Mockito.eq(false));
		
		zigBeeDevice.sendMulticastData(XBEE_16BIT_ADDRESS, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendMulticastData(XBee16BitAddress, int, int, byte[], byte[], byte[])}.
	 * 
	 * <p>Verify that multicast data cannot be sent if there is a transmit exception when sending and checking the Explicit 
	 * Addressing packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TransmitException.class)
	public void testSendMulticastDataTransmitException() throws TimeoutException, XBeeException {
		// Throw a transmit exception when sending and checking any Explicit Addressing packet.
		Mockito.doThrow(new TransmitException(null)).when(zigBeeDevice).sendAndCheckXBeePacket(Mockito.any(ExplicitAddressingPacket.class), Mockito.eq(false));
		
		zigBeeDevice.sendMulticastData(XBEE_16BIT_ADDRESS, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendMulticastData(XBee16BitAddress, int, int, byte[], byte[], byte[])}.
	 * 
	 * <p>Verify that multicast data cannot be sent if there is an IO error when sending and checking the Explicit 
	 * Addressing packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=XBeeException.class)
	public void testSendMulticastDataIOException() throws TimeoutException, XBeeException {
		// Throw an XBee exception when sending and checking any Explicit Addressing packet.
		Mockito.doThrow(new XBeeException()).when(zigBeeDevice).sendAndCheckXBeePacket(Mockito.any(ExplicitAddressingPacket.class), Mockito.eq(false));
		
		zigBeeDevice.sendMulticastData(XBEE_16BIT_ADDRESS, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendMulticastData(XBee16BitAddress, int, int, byte[], byte[], byte[])}.
	 * 
	 * <p>Verify that multicast data cannot be sent if the sender is a remote XBee device.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testSendMulticastDataFromRemoteDevices() throws TimeoutException, XBeeException {
		// Return that the XBee device is remote when asked.
		Mockito.when(zigBeeDevice.isRemote()).thenReturn(true);
		
		zigBeeDevice.sendMulticastData(XBEE_16BIT_ADDRESS, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendMulticastData(XBee16BitAddress, int, int, byte[], byte[], byte[])}.
	 * 
	 * <p>Verify that multicast data is sent successfully if there is not any error when sending and checking the Explicit 
	 * Addressing packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testSendMulticastDataSuccess() throws TimeoutException, XBeeException {
		// Do nothing when sending and checking any Explicit Addressing packet.
		Mockito.doNothing().when(zigBeeDevice).sendAndCheckXBeePacket(Mockito.any(ExplicitAddressingPacket.class), Mockito.eq(false));
		
		zigBeeDevice.sendMulticastData(XBEE_16BIT_ADDRESS, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
		
		// Verify the sendAndCheckXBeePacket(XBeePacket, boolean) method was called.
		Mockito.verify(zigBeeDevice, Mockito.times(1)).sendAndCheckXBeePacket(Mockito.eq(explicitAddressingPacket), Mockito.eq(false));
	}
}

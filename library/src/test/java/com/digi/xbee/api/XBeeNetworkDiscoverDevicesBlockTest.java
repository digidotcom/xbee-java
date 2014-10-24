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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;

@PrepareForTest({XBeeNetwork.class})
@RunWith(PowerMockRunner.class)
public class XBeeNetworkDiscoverDevicesBlockTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Variables.
	private XBeeNetwork network;
	
	private XBeeDevice deviceMock;
	
	private NodeDiscovery ndMock;
	
	private RemoteXBeeDevice idFoundDevice;
	
	@Before
	public void setUp() throws Exception {
		ndMock = PowerMockito.mock(NodeDiscovery.class);
		deviceMock = PowerMockito.mock(XBeeDevice.class);
		IConnectionInterface cInterfaceMock = PowerMockito.mock(IConnectionInterface.class);
		
		PowerMockito.when(deviceMock.getConnectionInterface()).thenReturn(cInterfaceMock);
		PowerMockito.when(cInterfaceMock.toString()).thenReturn("Mocked IConnectionInterface for XBeeNetwork test.");
		
		idFoundDevice = new RemoteXBeeDevice(deviceMock, new XBee64BitAddress("0013A20040A9E77E"), 
				XBee16BitAddress.UNKNOWN_ADDRESS, "id");
		
		List<RemoteXBeeDevice> idFoundDevices = new ArrayList<RemoteXBeeDevice>();
		idFoundDevices.add(idFoundDevice);
		
		PowerMockito.whenNew(NodeDiscovery.class).withArguments(deviceMock).thenReturn(ndMock);
		PowerMockito.when(ndMock.discoverDeviceByNodeID(Mockito.anyString())).thenReturn(idFoundDevice);
		PowerMockito.when(ndMock.discoverDevicesByNodeID(Mockito.anyString())).thenReturn(idFoundDevices);
		PowerMockito.when(ndMock.discoverDevices()).thenReturn(idFoundDevices);
		
		network = PowerMockito.spy(new XBeeNetwork(deviceMock));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDeviceByNodeID(String)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when passing a 
	 * {@code null} id.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDeviceByNodeIDNullId() throws XBeeException {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Device identifier cannot be null.")));
						
		// Call the method under test.
		network.discoverDeviceByNodeID(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDeviceByNodeID(String)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * passing an empty id.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDeviceByNodeIDEmptyId() throws XBeeException {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Device identifier cannot be an empty string.")));
						
		// Call the method under test.
		network.discoverDeviceByNodeID("");
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDeviceByNodeID(String)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDeviceByID() throws XBeeException {
		// Setup the resources for the test.
		String id = "id";
		
		// Call the method under test.
		RemoteXBeeDevice found = network.discoverDeviceByNodeID(id);
		
		// Verify the result.
		Mockito.verify(ndMock, Mockito.times(1)).discoverDeviceByNodeID(id);
		
		assertThat("Found device must not be null", found, is(not(nullValue())));
		assertThat("Not expected 64-bit address in found device", found.get64BitAddress(), is(equalTo(idFoundDevice.get64BitAddress())));
		assertThat("Not expected 16-bit address in found device", found.get16BitAddress(), is(equalTo(idFoundDevice.get16BitAddress())));
		assertThat("Not expected id in found device", found.getNodeID(), is(equalTo(idFoundDevice.getNodeID())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevicesByNodeID(String)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when passing a 
	 * {@code null} id.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDevicesByNodeIDNullId() throws XBeeException {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Device identifier cannot be null.")));
		
		// Call the method under test.
		network.discoverDevicesByNodeID(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevicesByNodeID(String)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * passing an empty id.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDevicesByNodeIDEmptyId() throws XBeeException {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Device identifier cannot be an empty string.")));
		
		// Call the method under test.
		network.discoverDevicesByNodeID("");
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevicesByNodeID(String)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDevicesByNodeID() throws XBeeException {
		// Setup the resources for the test.
		String id = "id";
		
		// Call the method under test.
		List<RemoteXBeeDevice> found = network.discoverDevicesByNodeID(id);
		
		// Verify the result.
		Mockito.verify(ndMock, Mockito.times(1)).discoverDevicesByNodeID(id);
		
		assertThat("Found device list must not be null", found, is(not(nullValue())));
		assertThat("Found device list must have one device", found.size(), is(equalTo(1)));
		
		RemoteXBeeDevice d = found.get(0);
		assertThat("Not expected 64-bit address in found device", d.get64BitAddress(), is(equalTo(idFoundDevice.get64BitAddress())));
		assertThat("Not expected 16-bit address in found device", d.get16BitAddress(), is(equalTo(idFoundDevice.get16BitAddress())));
		assertThat("Not expected id in found device", d.getNodeID(), is(equalTo(idFoundDevice.getNodeID())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevices()}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDevices() throws XBeeException {
		// Call the method under test.
		List<RemoteXBeeDevice> found = network.discoverDevices();
		
		// Verify the result.
		Mockito.verify(ndMock, Mockito.times(1)).discoverDevices();
		
		assertThat("Found device list must not be null", found, is(not(nullValue())));
		assertThat("Found device list must have one device", found.size(), is(equalTo(1)));
		
		RemoteXBeeDevice d = found.get(0);
		assertThat("Not expected 64-bit address in found device", d.get64BitAddress(), is(equalTo(idFoundDevice.get64BitAddress())));
		assertThat("Not expected 16-bit address in found device", d.get16BitAddress(), is(equalTo(idFoundDevice.get16BitAddress())));
		assertThat("Not expected id in found device", d.getNodeID(), is(equalTo(idFoundDevice.getNodeID())));
	}
}

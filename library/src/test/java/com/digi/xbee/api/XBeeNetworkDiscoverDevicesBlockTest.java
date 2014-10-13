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
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

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
import com.digi.xbee.api.models.DiscoveryOptions;
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
	
	@SuppressWarnings("unchecked")
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
		PowerMockito.when(ndMock.discoverDeviceByID(Mockito.anyString(), Mockito.anyLong())).thenReturn(idFoundDevice);
		PowerMockito.when(ndMock.discoverAllDevicesByID(Mockito.anyString(), Mockito.anyLong())).thenReturn(idFoundDevices);
		PowerMockito.when(ndMock.discoverDevices(Mockito.anySet(), Mockito.anyLong())).thenReturn(idFoundDevices);
		
		network = PowerMockito.spy(new XBeeNetwork(deviceMock));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDeviceByID(String)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when passing a 
	 * {@code null} id.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDeviceByIDOnlyIdNull() throws XBeeException {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Device identifier cannot be null.")));
						
		// Call the method under test.
		network.discoverDeviceByID(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDeviceByID(String)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * passing an empty id.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDeviceByIDOnlyIdEmpty() throws XBeeException {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Device identifier cannot be an empty string.")));
						
		// Call the method under test.
		network.discoverDeviceByID("");
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDeviceByID(String)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDeviceByIDOnlyIdValid() throws XBeeException {
		// Setup the resources for the test.
		String id = "id";
		
		// Call the method under test.
		RemoteXBeeDevice found = network.discoverDeviceByID(id);
		
		// Verify the result.
		Mockito.verify(ndMock, Mockito.times(1)).discoverDeviceByID(id, NodeDiscovery.USE_DEVICE_TIMEOUT);
		
		assertThat("Found device must not be null", found, is(not(nullValue())));
		assertThat("Not expected 64-bit address in found device", found.get64BitAddress(), is(equalTo(idFoundDevice.get64BitAddress())));
		assertThat("Not expected 16-bit address in found device", found.get16BitAddress(), is(equalTo(idFoundDevice.get16BitAddress())));
		assertThat("Not expected id in found device", found.getNodeID(), is(equalTo(idFoundDevice.getNodeID())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDeviceByID(String, long)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when passing a 
	 * {@code null} id.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDeviceByIDIdNull() throws XBeeException {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Device identifier cannot be null.")));
						
		// Call the method under test.
		network.discoverDeviceByID(null, 500);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDeviceByID(String, long)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * passing an empty id.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDeviceByIDIdEmpty() throws XBeeException {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Device identifier cannot be an empty string.")));
						
		// Call the method under test.
		network.discoverDeviceByID("", 500);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDeviceByID(String, long)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * passing a wait forever.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDeviceByIDWaitForever() throws XBeeException {
		// Setup the resources for the test.
		String id = "id";
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("The discovery devices process cannot block forever.")));
		
		// Call the method under test.
		network.discoverDeviceByID(id, NodeDiscovery.WAIT_FOREVER);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDeviceByID(String, long)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * passing a negative timeout.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDeviceByIDNegativeTimeout() throws XBeeException {
		// Setup the resources for the test.
		String id = "id";
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("The timeout must be bigger than 0.")));
		
		// Call the method under test.
		network.discoverDeviceByID(id, -5);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDeviceByID(String, long)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDeviceByIDValidTimeout() throws XBeeException {
		// Setup the resources for the test.
		String id = "id";
		long timeout = 500;
		
		// Call the method under test.
		RemoteXBeeDevice found = network.discoverDeviceByID(id, timeout);
		
		// Verify the result.
		Mockito.verify(ndMock, Mockito.times(1)).discoverDeviceByID(id, timeout);
		
		assertThat("Found device must not be null", found, is(not(nullValue())));
		assertThat("Not expected 64-bit address in found device", found.get64BitAddress(), is(equalTo(idFoundDevice.get64BitAddress())));
		assertThat("Not expected 16-bit address in found device", found.get16BitAddress(), is(equalTo(idFoundDevice.get16BitAddress())));
		assertThat("Not expected id in found device", found.getNodeID(), is(equalTo(idFoundDevice.getNodeID())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverAllDevicesByID(String, long)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when passing a 
	 * {@code null} id.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverAllDevicesByIDNullId() throws XBeeException {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Device identifier cannot be null.")));
		
		// Call the method under test.
		network.discoverAllDevicesByID(null, NodeDiscovery.USE_DEVICE_TIMEOUT);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverAllDevicesByID(String, long)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * passing an empty id.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverAllDevicesByIDEmptyId() throws XBeeException {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Device identifier cannot be an empty string.")));
		
		// Call the method under test.
		network.discoverAllDevicesByID("", NodeDiscovery.USE_DEVICE_TIMEOUT);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverAllDevicesByID(String, long)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * passing a wait forever.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverAllDevicesByIDWaitForever() throws XBeeException {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("The discovery devices process cannot block forever.")));
		
		// Call the method under test.
		network.discoverAllDevicesByID("id", NodeDiscovery.WAIT_FOREVER);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverAllDevicesByID(String, long)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * passing a negative timeout.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverAllDevicesByIDNegativeTimeout() throws XBeeException {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("The timeout must be bigger than 0.")));
		
		// Call the method under test.
		network.discoverAllDevicesByID("id", -6);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverAllDevicesByID(String, long)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverAllDevicesByIDValidTimeout() throws XBeeException {
		// Setup the resources for the test.
		String id = "id";
		long timeout = 500;
		
		// Call the method under test.
		List<RemoteXBeeDevice> found = network.discoverAllDevicesByID(id, timeout);
		
		// Verify the result.
		Mockito.verify(ndMock, Mockito.times(1)).discoverAllDevicesByID(id, timeout);
		
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
		Mockito.verify(ndMock, Mockito.times(1)).discoverDevices(null, NodeDiscovery.USE_DEVICE_TIMEOUT);
		
		assertThat("Found device list must not be null", found, is(not(nullValue())));
		assertThat("Found device list must have one device", found.size(), is(equalTo(1)));
		
		RemoteXBeeDevice d = found.get(0);
		assertThat("Not expected 64-bit address in found device", d.get64BitAddress(), is(equalTo(idFoundDevice.get64BitAddress())));
		assertThat("Not expected 16-bit address in found device", d.get16BitAddress(), is(equalTo(idFoundDevice.get16BitAddress())));
		assertThat("Not expected id in found device", d.getNodeID(), is(equalTo(idFoundDevice.getNodeID())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevices(long)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * passing a wait forever.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDevicesWaitForever() throws XBeeException {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("The discovery process cannot block forever.")));
		
		// Call the method under test.
		network.discoverDevices(NodeDiscovery.WAIT_FOREVER);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevices(long)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * passing a negative timeout.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDevicesNegativeTimeout() throws XBeeException {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("The timeout must be bigger than 0.")));
		
		// Call the method under test.
		network.discoverDevices(-6);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevices(long)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDevicesValidTimeout() throws XBeeException {
		// Setup the resources for the test.
		long timeout = 500;
		
		// Call the method under test.
		List<RemoteXBeeDevice> found = network.discoverDevices(timeout);
		
		// Verify the result.
		Mockito.verify(ndMock, Mockito.times(1)).discoverDevices(null, timeout);
		
		assertThat("Found device list must not be null", found, is(not(nullValue())));
		assertThat("Found device list must have one device", found.size(), is(equalTo(1)));
		
		RemoteXBeeDevice d = found.get(0);
		assertThat("Not expected 64-bit address in found device", d.get64BitAddress(), is(equalTo(idFoundDevice.get64BitAddress())));
		assertThat("Not expected 16-bit address in found device", d.get16BitAddress(), is(equalTo(idFoundDevice.get16BitAddress())));
		assertThat("Not expected id in found device", d.getNodeID(), is(equalTo(idFoundDevice.getNodeID())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevices(java.util.Set, long))}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * passing a wait forever.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDevicesOptionsWaitForever() throws XBeeException {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("The discovery process cannot block forever.")));
		
		// Call the method under test.
		network.discoverDevices((Set<DiscoveryOptions>) null, NodeDiscovery.WAIT_FOREVER);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevices(java.util.Set, long))}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * passing a negative timeout.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDevicesOptionsNegativeTimeout() throws XBeeException {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("The timeout must be bigger than 0.")));
		
		// Call the method under test.
		network.discoverDevices((Set<DiscoveryOptions>) null, -6);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevices(Set, long))}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDevicesOptionsValidTimeout() throws XBeeException {
		// Setup the resources for the test.
		long timeout = 500;
		Set<DiscoveryOptions> options = EnumSet.of(DiscoveryOptions.APPEND_DD);
		
		// Call the method under test.
		List<RemoteXBeeDevice> found = network.discoverDevices(options, timeout);
		
		// Verify the result.
		Mockito.verify(ndMock, Mockito.times(1)).discoverDevices(options, timeout);
		
		assertThat("Found device list must not be null", found, is(not(nullValue())));
		assertThat("Found device list must have one device", found.size(), is(equalTo(1)));
		
		RemoteXBeeDevice d = found.get(0);
		assertThat("Not expected 64-bit address in found device", d.get64BitAddress(), is(equalTo(idFoundDevice.get64BitAddress())));
		assertThat("Not expected 16-bit address in found device", d.get16BitAddress(), is(equalTo(idFoundDevice.get16BitAddress())));
		assertThat("Not expected id in found device", d.getNodeID(), is(equalTo(idFoundDevice.getNodeID())));
	}
}

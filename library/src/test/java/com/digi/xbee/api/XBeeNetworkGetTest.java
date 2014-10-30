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
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;

@RunWith(PowerMockRunner.class)
public class XBeeNetworkGetTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private static final String NODE_ID = "id";
	
	// Variables.
	private XBeeNetwork network;
	
	private XBeeDevice localDevice;
	
	private RemoteXBeeDevice remoteDevice1;
	private RemoteXBeeDevice remoteDevice2;
	private RemoteXBeeDevice remoteDevice3;
	
	@Before
	public void setUp() {
		// Mock the local device.
		localDevice = PowerMockito.mock(XBeeDevice.class);
		Mockito.when(localDevice.getConnectionInterface()).thenReturn(Mockito.mock(IConnectionInterface.class));
		
		// Mock the network.
		network = PowerMockito.spy(new XBeeNetwork(localDevice));
		
		// Mock the remote device 1.
		remoteDevice1 = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(remoteDevice1.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN);
		Mockito.when(remoteDevice1.getNodeID()).thenReturn(NODE_ID);
		Mockito.when(remoteDevice1.get64BitAddress()).thenReturn(new XBee64BitAddress("0123456789ABCDEF"));
		
		// Mock the remote device 2.
		remoteDevice2 = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(remoteDevice2.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN);
		Mockito.when(remoteDevice2.getNodeID()).thenReturn(NODE_ID);
		Mockito.when(remoteDevice2.get64BitAddress()).thenReturn(XBee64BitAddress.UNKNOWN_ADDRESS);
		Mockito.when(remoteDevice2.get16BitAddress()).thenReturn(new XBee16BitAddress("1111"));
		
		
		// Mock the remote device 3.
		remoteDevice3 = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(remoteDevice3.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN);
		Mockito.when(remoteDevice3.getNodeID()).thenReturn("id2");
		Mockito.when(remoteDevice3.get64BitAddress()).thenReturn(new XBee64BitAddress("23456789ABCDEF01"));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(String)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when passing a 
	 * {@code null} id.</p>
	 */
	@Test
	public void testGetDeviceByIDNullId() {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Device identifier cannot be null.")));
		
		String id = null;
		
		// Call the method under test.
		network.getDevice(id);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(String)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * passing an empty id.</p>
	 */
	@Test
	public void testGetDeviceByIDEmptyId() {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Device identifier cannot be an empty string.")));
		
		// Call the method under test.
		network.getDevice("");
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(String)}.
	 * 
	 * <p>Verify that we get a null object if the network is empty.</p>
	 */
	@Test
	public void testGetDeviceByIDEmptyNetwork() {
		// Call the method under test.
		RemoteXBeeDevice found = network.getDevice(NODE_ID);
		
		assertNull(found);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(String)}.
	 * 
	 * <p>Verify that we get a null object if the network does not contain
	 * any device with provided node identifier.</p>
	 */
	@Test
	public void testGetDeviceByIDNoMatchingDevices() {
		// Add a remote device to the network.
		network.addRemoteDevice(remoteDevice3);
		
		// Call the method under test.
		RemoteXBeeDevice found = network.getDevice(NODE_ID);
		
		assertNull(found);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(String)}.
	 * 
	 * <p>Verify that if the network contains a remote device with the provided 
	 * node identifier, it is returned successfully.</p>
	 */
	@Test
	public void testGetDeviceByIDValid() {
		// Add two remote devices to the network.
		network.addRemoteDevice(remoteDevice1);
		network.addRemoteDevice(remoteDevice3);
		
		// Call the method under test.
		RemoteXBeeDevice found = network.getDevice(NODE_ID);
		
		assertEquals(remoteDevice1, found);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(String)}.
	 * 
	 * <p>Verify that if the network contains a remote device with the provided 
	 * node identifier, it is returned successfully.</p>
	 */
	@Test
	public void testGetDeviceByIDValidUnkown64BitAddress() {
		// Add two remote devices to the network.
		network.addRemoteDevice(remoteDevice2);
		network.addRemoteDevice(remoteDevice3);
		
		// Call the method under test.
		RemoteXBeeDevice found = network.getDevice(NODE_ID);
		
		assertEquals(remoteDevice2, found);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevices(String)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when passing a 
	 * {@code null} id.</p>
	 */
	@Test
	public void testGetAllDevicesByIDNullId() {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Device identifier cannot be null.")));
		
		// Call the method under test.
		network.getDevices(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevices(String)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * passing an empty id.</p>
	 */
	@Test
	public void testGetAllDevicesByIDEmptyId() {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Device identifier cannot be an empty string.")));
		
		// Call the method under test.
		network.getDevices("");
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevices(String)}.
	 * 
	 * <p>Verify that we obtain an empty RemoteXBeeDevice list if the network
	 * is empty.</p>
	 */
	@Test
	public void testGetAllDevicesByIDEmptyNetwork() {
		// Call the method under test.
		List<RemoteXBeeDevice> remotes = network.getDevices(NODE_ID);
		
		assertEquals(remotes.size(), 0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevices(String)}.
	 * 
	 * <p>Verify that we obtain an empty RemoteXBeeDevice list if the network
	 * does not contain any device with the provided node identifier.</p>
	 */
	@Test
	public void testGetAllDevicesByIDNoMatchingDevices() {
		// Add the remote device to the network.
		network.addRemoteDevice(remoteDevice3);
		
		// Call the method under test.
		List<RemoteXBeeDevice> remotes = network.getDevices(NODE_ID);
		
		assertEquals(remotes.size(), 0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevices(String)}.
	 * 
	 * <p>Verify that if the network contains a remote device with the provided 
	 * node identifier, it is returned successfully.</p>
	 */
	@Test
	public void testGetAllDevicesByIDOneDevice() {
		// Add two remote devices to the network.
		network.addRemoteDevice(remoteDevice1);
		network.addRemoteDevice(remoteDevice3);
		
		// Call the method under test.
		List<RemoteXBeeDevice> remotes = network.getDevices(NODE_ID);
		
		assertEquals(remotes.size(), 1);
		assertEquals(remotes.get(0), remoteDevice1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevices(String)}.
	 * 
	 * <p>Verify that if the network contains several remote devices with the 
	 * provided node identifier, they are returned successfully.</p>
	 */
	@Test
	public void testGetAllDevicesByIDSeveralDevice() {
		// Add several remote devices to the network.
		network.addRemoteDevice(remoteDevice1);
		network.addRemoteDevice(remoteDevice2);
		network.addRemoteDevice(remoteDevice3);
		
		// Call the method under test.
		List<RemoteXBeeDevice> remotes = network.getDevices(NODE_ID);
		
		assertEquals(remotes.size(), 2);
		assertThat(remotes.contains(remoteDevice1), is(equalTo(true)));
		assertThat(remotes.contains(remoteDevice2), is(equalTo(true)));
		assertThat(remotes.contains(remoteDevice3), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevices()}.
	 * 
	 * <p>Verify that we obtain an empty RemoteXBeeDevice list if the network
	 * is empty.</p>
	 */
	@Test
	public void testGetDevicesEmptyNetwork() {
		// Call the method under test.
		List<RemoteXBeeDevice> remotes = network.getDevices();
		
		assertEquals(remotes.size(), 0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevices()}.
	 * 
	 * <p>Verify that if the network contains a remote device, it is returned 
	 * successfully.</p>
	 */
	@Test
	public void testGetDevicesOneDevice() {
		// Add a remote device to the network.
		network.addRemoteDevice(remoteDevice1);
		
		// Call the method under test.
		List<RemoteXBeeDevice> remotes = network.getDevices();
		
		assertEquals(remotes.size(), 1);
		assertEquals(remotes.get(0), remoteDevice1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevices()}.
	 * 
	 * <p>Verify that if the network contains several remote devices, they are
	 * returned successfully.</p>
	 */
	@Test
	public void testGetDevicesSeveralDevices() {
		// Add several remote devices to the network.
		network.addRemoteDevice(remoteDevice1);
		network.addRemoteDevice(remoteDevice2);
		network.addRemoteDevice(remoteDevice3);
		
		Set<RemoteXBeeDevice> addedRemotes = new HashSet<RemoteXBeeDevice>();
		addedRemotes.add(remoteDevice1);
		addedRemotes.add(remoteDevice2);
		addedRemotes.add(remoteDevice3);
		
		// Call the method under test.
		List<RemoteXBeeDevice> remotes = network.getDevices();
		
		assertEquals(remotes.size(), 3);
		
		Set<RemoteXBeeDevice> obtainedRemotes = new HashSet<RemoteXBeeDevice>();
		obtainedRemotes.addAll(remotes);
		
		assertEquals(obtainedRemotes, addedRemotes);
	}
}

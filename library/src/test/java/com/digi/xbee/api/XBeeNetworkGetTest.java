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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

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
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
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
	private RemoteXBeeDevice remoteDeviceUNI;
	private RemoteXBeeDevice remoteDeviceUN64Addr;
	
	@Before
	public void setUp() {
		// Mock the local device.
		localDevice = PowerMockito.mock(XBeeDevice.class);
		Mockito.when(localDevice.getConnectionInterface()).thenReturn(Mockito.mock(IConnectionInterface.class));
		Mockito.when(localDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		// Mock the network.
		network = PowerMockito.spy(new XBeeNetwork(localDevice));
		
		// Mock the remote device 1.
		remoteDevice1 = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(remoteDevice1.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN);
		Mockito.when(remoteDevice1.getNodeID()).thenReturn(NODE_ID);
		Mockito.when(remoteDevice1.get64BitAddress()).thenReturn(new XBee64BitAddress("0123456789ABCDEF"));
		Mockito.when(remoteDevice1.get16BitAddress()).thenReturn(new XBee16BitAddress("2222"));
		
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
		
		// Mock the remote device with unknown (null) id.
		remoteDeviceUNI = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(remoteDeviceUNI.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN);
		Mockito.when(remoteDeviceUNI.getNodeID()).thenReturn(null);
		Mockito.when(remoteDeviceUNI.get64BitAddress()).thenReturn(new XBee64BitAddress("23456789ABCDEF89"));
		
		// Mock the remote device with unknown 64-bit address.
		remoteDeviceUN64Addr = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(remoteDeviceUN64Addr.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN);
		Mockito.when(remoteDeviceUN64Addr.getNodeID()).thenReturn("uk64");
		Mockito.when(remoteDeviceUN64Addr.get64BitAddress()).thenReturn(null);
		Mockito.when(remoteDeviceUN64Addr.get16BitAddress()).thenReturn(new XBee16BitAddress("1234"));
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
		
		// Verify the result.
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
		
		// Verify the result.
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
		
		// Verify the result.
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
		
		// Verify the result.
		assertEquals(remoteDevice2, found);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(String)}.
	 * 
	 * <p>Verify that if the network contains a remote device with the provided 
	 * node identifier, it is returned successfully although it contains devices
	 * with unknown NI.</p>
	 */
	@Test
	public void testGetDeviceByIDWithDevicesWithUnknownID() {
		// Add two remote devices to the network.
		network.addRemoteDevice(remoteDeviceUNI);
		network.addRemoteDevice(remoteDevice1);
		
		// Call the method under test.
		RemoteXBeeDevice found = network.getDevice(NODE_ID);
		
		// Verify the result.
		assertEquals(remoteDevice1, found);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(String)}.
	 * 
	 * <p>Verify that if the network contains a remote device with an unknown 
	 * 64-bit address, it is returned successfully.</p>
	 */
	@Test
	public void testGetDeviceByIDUnknown64bitAddress() {
		// Add several remote devices to the network.
		network.addRemoteDevice(remoteDevice1);
		network.addRemoteDevice(remoteDeviceUN64Addr);
		network.addRemoteDevice(remoteDevice2);
		
		// Call the method under test.
		RemoteXBeeDevice found = network.getDevice("uk64");
		
		// Verify the result.
		assertThat(found, is(equalTo(remoteDeviceUN64Addr)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(String)}.
	 * 
	 * <p>Verify that if the network does not return a device that is not 
	 * contained.</p>
	 */
	@Test
	public void testGetDeviceByIDNotIncluded() {
		// Add several remote devices to the network.
		network.addRemoteDevice(remoteDevice1);
		network.addRemoteDevice(remoteDeviceUN64Addr);
		network.addRemoteDevice(remoteDevice2);
		
		// Call the method under test.
		RemoteXBeeDevice found = network.getDevice("aaaaaaaa");
		
		// Verify the result.
		assertThat(found, is(nullValue(RemoteXBeeDevice.class)));
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
		
		// Verify the result.
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
		
		// Verify the result.
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
		
		// Verify the result.
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
	public void testGetAllDevicesByIDSeveralDevices() {
		// Add several remote devices to the network.
		network.addRemoteDevice(remoteDevice1);
		network.addRemoteDevice(remoteDevice2);
		network.addRemoteDevice(remoteDevice3);
		
		// Call the method under test.
		List<RemoteXBeeDevice> remotes = network.getDevices(NODE_ID);
		
		// Verify the result.
		assertEquals(remotes.size(), 2);
		assertThat(remotes.contains(remoteDevice1), is(equalTo(true)));
		assertThat(remotes.contains(remoteDevice2), is(equalTo(true)));
		assertThat(remotes.contains(remoteDevice3), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(String)}.
	 * 
	 * <p>Verify that if the network contains a remote device with an unknown 
	 * 64-bit address, it is returned successfully.</p>
	 */
	@Test
	public void testGetAllDevicesByIDUnknown16bitAddress() {
		// Add several remote devices to the network.
		network.addRemoteDevice(remoteDevice1);
		network.addRemoteDevice(remoteDeviceUN64Addr);
		network.addRemoteDevice(remoteDevice2);
		
		// Call the method under test.
		List<RemoteXBeeDevice> remotes = network.getDevices("uk64");
		
		// Verify the result.
		assertEquals(remotes.size(), 1);
		assertThat(remotes.contains(remoteDeviceUN64Addr), is(equalTo(true)));
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
		
		// Verify the result.
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
		
		// Verify the result.
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
		
		// Verify the result.
		assertEquals(remotes.size(), 3);
		
		Set<RemoteXBeeDevice> obtainedRemotes = new HashSet<RemoteXBeeDevice>();
		obtainedRemotes.addAll(remotes);
		
		assertEquals(obtainedRemotes, addedRemotes);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(XBee64BitAddress)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when passing a 
	 * {@code null} 64-bit addr.</p>
	 */
	@Test
	public void testGetDeviceBy64BitAddrNullAddr() {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("64-bit address cannot be null.")));
		
		XBee64BitAddress address = null;
		
		// Call the method under test.
		network.getDevice(address);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(XBee64BitAddress)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * passing an unknown 64-bit address.</p>
	 */
	@Test
	public void testGetDeviceBy64BitAddrUnknownAddr() {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("64-bit address cannot be unknown.")));
		
		XBee64BitAddress address = XBee64BitAddress.UNKNOWN_ADDRESS;
		
		// Call the method under test.
		network.getDevice(address);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(XBee64BitAddress)}.
	 * 
	 * <p>Verify that we get a null object if the network is empty.</p>
	 */
	@Test
	public void testGetDeviceBy64BitAddrEmptyNetwork() {
		// Setup the resources for the test.
		XBee64BitAddress address = new XBee64BitAddress("0123456789ABCDEF");
		
		// Call the method under test.
		RemoteXBeeDevice found = network.getDevice(address);
		
		// Verify the result.
		assertNull(found);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(XBee64BitAddress)}.
	 * 
	 * <p>Verify that we get a null object if the network does not contain
	 * any device with provided 64-bit address.</p>
	 */
	@Test
	public void testGetDeviceBy64BitAddrNoMatchingDevices() {
		// Add a remote device to the network.
		network.addRemoteDevice(remoteDevice3);
		
		// Call the method under test.
		RemoteXBeeDevice found = network.getDevice(new XBee64BitAddress("1234"));
		
		// Verify the result.
		assertNull(found);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(XBee64BitAddress)}.
	 * 
	 * <p>Verify that if the network contains a remote device with the provided 
	 * 64-bit address, it is returned successfully.</p>
	 */
	@Test
	public void testGetDeviceBy64BitAddrValid() {
		// Add two remote devices to the network.
		XBee64BitAddress address = new XBee64BitAddress("0123456789ABCDEF");
		network.addRemoteDevice(remoteDevice1);
		network.addRemoteDevice(remoteDevice3);
		
		// Call the method under test.
		RemoteXBeeDevice found = network.getDevice(address);
		
		// Verify the result.
		assertEquals(remoteDevice1, found);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(XBee64BitAddress)}.
	 * 
	 * <p>Verify that if the network contains a remote device with the provided 
	 * 64-bit address, it is returned successfully although it contains devices
	 * with unknown NI.</p>
	 */
	@Test
	public void testGetDeviceBy64BitAddrWithDevicesWithUnknownID() {
		// Add two remote devices to the network.
		XBee64BitAddress address = new XBee64BitAddress("0123456789ABCDEF");
		network.addRemoteDevice(remoteDeviceUNI);
		network.addRemoteDevice(remoteDevice1);
		
		// Call the method under test.
		RemoteXBeeDevice found = network.getDevice(address);
		
		// Verify the result.
		assertEquals(remoteDevice1, found);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(XBee16BitAddress)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when passing a 
	 * {@code null} 16-bit addr.</p>
	 * 
	 * @throws OperationNotSupportedException 
	 */
	@Test
	public void testGetDeviceBy16BitAddrNullAddr() throws OperationNotSupportedException {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("16-bit address cannot be null.")));
		
		XBee16BitAddress address = null;
		
		// Call the method under test.
		network.getDevice(address);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(XBee16BitAddress)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * passing an unknown 16-bit address.</p>
	 * 
	 * @throws OperationNotSupportedException 
	 */
	@Test
	public void testGetDeviceBy16BitAddrUnknownAddr() throws OperationNotSupportedException {
		// Setup the resources for the test.
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("16-bit address cannot be unknown.")));
		
		XBee16BitAddress address = XBee16BitAddress.UNKNOWN_ADDRESS;
		
		// Call the method under test.
		network.getDevice(address);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(XBee16BitAddress)}.
	 * 
	 * <p>An {@code OperationNotSupportedException} exception must be thrown when 
	 * trying to get a Digi Mesh device by its 16-bit address.</p>
	 * 
	 * @throws OperationNotSupportedException 
	 */
	@Test
	public void testGetDeviceBy16BitAddrDigiMeshDev() throws OperationNotSupportedException {
		// Setup the resources for the test.
		network.addRemoteDevice(remoteDevice1);
		Mockito.when(localDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_MESH);
		
		exception.expect(OperationNotSupportedException.class);
		exception.expectMessage(is(equalTo("DigiMesh protocol does not support 16-bit addressing.")));
		
		XBee16BitAddress address = XBee16BitAddress.UNKNOWN_ADDRESS;
		
		// Call the method under test.
		network.getDevice(address);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(XBee16BitAddress)}.
	 * 
	 * <p>An {@code OperationNotSupportedException} exception must be thrown when 
	 * trying to get a Digi Point device by its 16-bit address.</p>
	 * 
	 * @throws OperationNotSupportedException 
	 */
	@Test
	public void testGetDeviceBy16BitAddrDigiPointDev() throws OperationNotSupportedException {
		// Setup the resources for the test.
		network.addRemoteDevice(remoteDevice1);
		Mockito.when(localDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_POINT);
		
		exception.expect(OperationNotSupportedException.class);
		exception.expectMessage(is(equalTo("Point-to-Multipoint protocol does not support 16-bit addressing.")));
		
		XBee16BitAddress address = XBee16BitAddress.UNKNOWN_ADDRESS;
		
		// Call the method under test.
		network.getDevice(address);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(XBee16BitAddress)}.
	 * 
	 * <p>Verify that we get a null object if the network is empty.</p>
	 * 
	 * @throws OperationNotSupportedException 
	 */
	@Test
	public void testGetDeviceBy16BitAddrEmptyNetwork() throws OperationNotSupportedException {
		// Setup the resources for the test.
		XBee16BitAddress address = new XBee16BitAddress("1234");
		
		// Call the method under test.
		RemoteXBeeDevice found = network.getDevice(address);
		
		// Verify the result.
		assertNull(found);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(XBee16BitAddress)}.
	 * 
	 * <p>Verify that we get a null object if the network does not contain
	 * any device with provided 16-bit address.</p>
	 * 
	 * @throws OperationNotSupportedException 
	 */
	@Test
	public void testGetDeviceBy16BitAddrNoMatchingDevices() throws OperationNotSupportedException {
		// Add a remote device to the network.
		network.addRemoteDevice(remoteDevice3);
		
		// Call the method under test.
		RemoteXBeeDevice found = network.getDevice(new XBee16BitAddress("1234"));
		
		// Verify the result.
		assertNull(found);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(XBee16BitAddress)}.
	 * 
	 * <p>Verify that if the network contains a remote device with the provided 
	 * 16-bit address, it is returned successfully.</p>
	 * 
	 * @throws OperationNotSupportedException 
	 */
	@Test
	public void testGetDeviceBy16BitAddrValid() throws OperationNotSupportedException {
		// Add two remote devices to the network.
		XBee16BitAddress address = new XBee16BitAddress("2222");
		network.addRemoteDevice(remoteDevice1);
		network.addRemoteDevice(remoteDevice3);
		
		// Call the method under test.
		RemoteXBeeDevice found = network.getDevice(address);
		
		// Verify the result.
		assertEquals(remoteDevice1, found);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getDevice(XBee16BitAddress)}.
	 * 
	 * <p>Verify that if the network contains a remote device with the provided 
	 * 16-bit address, it is returned successfully although it contains devices
	 * with unknown 64-bit address.</p>
	 * 
	 * @throws OperationNotSupportedException 
	 */
	@Test
	public void testGetDeviceBy64BitAddrWithDevicesWithUnknown64BitAddr() throws OperationNotSupportedException {
		// Add two remote devices to the network.
		XBee16BitAddress address = new XBee16BitAddress("1234");
		network.addRemoteDevice(remoteDeviceUNI);
		network.addRemoteDevice(remoteDeviceUN64Addr);
		network.addRemoteDevice(remoteDevice1);
		
		// Call the method under test.
		RemoteXBeeDevice found = network.getDevice(address);
		
		// Verify the result.
		assertEquals(remoteDeviceUN64Addr, found);
	}
}

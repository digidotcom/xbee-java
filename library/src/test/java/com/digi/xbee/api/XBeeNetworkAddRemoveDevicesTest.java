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
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
public class XBeeNetworkAddRemoveDevicesTest {

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
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
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
		Mockito.when(remoteDeviceUNI.get16BitAddress()).thenReturn(XBee16BitAddress.UNKNOWN_ADDRESS);
		
		// Mock the remote device with unknown 64-bit address.
		remoteDeviceUN64Addr = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(remoteDeviceUN64Addr.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN);
		Mockito.when(remoteDeviceUN64Addr.getNodeID()).thenReturn("uk64");
		Mockito.when(remoteDeviceUN64Addr.get64BitAddress()).thenReturn(null);
		Mockito.when(remoteDeviceUN64Addr.get16BitAddress()).thenReturn(new XBee16BitAddress("1234"));
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#addRemoteDevice(RemoteXBeeDevice)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when trying to 
	 * add a {@code null} device.</p>
	 */
	@Test
	public void testAddRemoteDeviceNull() {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Remote device cannot be null.")));
		
		RemoteXBeeDevice dev = null;
		
		// Call the method under test.
		network.addRemoteDevice(dev);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#addRemoteDevice(RemoteXBeeDevice)}.
	 * 
	 * <p>Verify that if a remote not in the network is successfully added.</p>
	 */
	@Test
	public void testAddRemoteDeviceNotInTheNetwork() {
		
		assertThat(network.getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		RemoteXBeeDevice added = network.addRemoteDevice(remoteDevice1);
		
		// Verify the result.
		assertThat(network.getNumberOfDevices(), is(equalTo(1)));
		assertThat(network.getDevices().contains(remoteDevice1), is(equalTo(true)));
		assertThat(added, is(equalTo(remoteDevice1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#addRemoteDevice(RemoteXBeeDevice)}.
	 * 
	 * <p>Verify that if a remote already in the network is updated.</p>
	 */
	@Test
	public void testAddRemoteDeviceAlreadyInTheNetwork() {
		// Setup the resources for the test.
		
		XBee64BitAddress add64 = new XBee64BitAddress("23456789ABCDEF89");
		XBee16BitAddress add16 = new XBee16BitAddress("5555");
		
		RemoteXBeeDevice dev = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(dev.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN);
		Mockito.when(dev.getNodeID()).thenReturn(null);
		Mockito.when(dev.get64BitAddress()).thenReturn(add64);
		Mockito.when(dev.get16BitAddress()).thenReturn(XBee16BitAddress.UNKNOWN_ADDRESS);
		
		RemoteXBeeDevice dev2 = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(dev2.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN);
		Mockito.when(dev2.getNodeID()).thenReturn("newID");
		Mockito.when(dev2.get64BitAddress()).thenReturn(add64);
		Mockito.when(dev2.get16BitAddress()).thenReturn(add16);
		
		network.addRemoteDevice(dev);
		
		assertThat(network.getNumberOfDevices(), is(equalTo(1)));
		assertThat(network.getDevices().contains(dev), is(equalTo(true)));
		
		// Call the method under test.
		RemoteXBeeDevice added = network.addRemoteDevice(dev2);
		
		// Verify the result.
		assertThat(network.getNumberOfDevices(), is(equalTo(1)));
		assertThat(network.getDevices().contains(dev2), is(equalTo(false)));
		assertThat(network.getDevices().contains(dev), is(equalTo(true)));
		assertThat(added, is(equalTo(dev)));
		Mockito.verify(dev).updateDeviceDataFrom(dev2);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#addRemoteDevice(RemoteXBeeDevice)}.
	 * 
	 * <p>Verify that if a remote (without 64-bit address) not in the network 
	 * is successfully added.</p>
	 */
	@Test
	public void testAddRemoteDeviceNotInTheNetworkOnly16BitAddr() {
		
		assertThat(network.getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		RemoteXBeeDevice added = network.addRemoteDevice(remoteDeviceUN64Addr);
		
		// Verify the result.
		assertThat(network.getNumberOfDevices(), is(equalTo(1)));
		assertThat(network.getDevices().contains(remoteDeviceUN64Addr), is(equalTo(true)));
		assertThat(added, is(equalTo(remoteDeviceUN64Addr)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#addRemoteDevice(RemoteXBeeDevice)}.
	 * 
	 * <p>Verify that if a remote (without 64-bit address) already in the 
	 * network is updated.</p>
	 */
	@Test
	public void testAddRemoteDeviceAlreadyInTheNetworkOnly16BitAddr() {
		// Setup the resources for the test.
		XBee64BitAddress add64 = new XBee64BitAddress("23456789ABCDEF89");
		XBee16BitAddress add16 = new XBee16BitAddress("5555");
		
		RemoteXBeeDevice dev = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(dev.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN);
		Mockito.when(dev.getNodeID()).thenReturn(null);
		Mockito.when(dev.get64BitAddress()).thenReturn(XBee64BitAddress.UNKNOWN_ADDRESS);
		Mockito.when(dev.get16BitAddress()).thenReturn(add16);
		
		RemoteXBeeDevice dev2 = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(dev2.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN);
		Mockito.when(dev2.getNodeID()).thenReturn("newID");
		Mockito.when(dev2.get16BitAddress()).thenReturn(add16);
		Mockito.when(dev2.get64BitAddress()).thenReturn(add64);
		
		network.addRemoteDevice(dev);
		
		assertThat(network.getNumberOfDevices(), is(equalTo(1)));
		assertThat(network.getDevices().contains(dev), is(equalTo(true)));
		
		// Call the method under test.
		RemoteXBeeDevice added = network.addRemoteDevice(dev2);
		
		// Verify the result.
		assertThat(network.getNumberOfDevices(), is(equalTo(1)));
		assertThat(network.getDevices().contains(dev2), is(equalTo(false)));
		assertThat(network.getDevices().contains(dev), is(equalTo(true)));
		assertThat(added, is(equalTo(dev)));
		Mockito.verify(dev).updateDeviceDataFrom(dev2);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#addRemoteDevice(RemoteXBeeDevice)}.
	 * 
	 * <p>Verify that if a remote (without 64-bit/16-bit addresses) not in the 
	 * network is not added.</p>
	 */
	@Test
	public void testAddRemoteDeviceNotInTheNetworkUnknown64And16BitAddr() {
		// Setup the resources for the test.
		RemoteXBeeDevice dev = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(dev.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN);
		Mockito.when(dev.getNodeID()).thenReturn(null);
		Mockito.when(dev.get64BitAddress()).thenReturn(XBee64BitAddress.UNKNOWN_ADDRESS);
		Mockito.when(dev.get16BitAddress()).thenReturn(XBee16BitAddress.UNKNOWN_ADDRESS);
		
		assertThat(network.getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		RemoteXBeeDevice added = network.addRemoteDevice(dev);
		
		// Verify the result.
		assertThat(network.getNumberOfDevices(), is(equalTo(0)));
		assertThat(added, is(nullValue(RemoteXBeeDevice.class)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#addRemoteDevices(java.util.List)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when trying to 
	 * add a {@code null} list of devices.</p>
	 */
	@Test
	public void testAddRemoteDevicesNull() {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("The list of remote devices cannot be null.")));
		
		List<RemoteXBeeDevice> list = null;
		
		// Call the method under test.
		network.addRemoteDevices(list);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#addRemoteDevices(java.util.List)}.
	 * 
	 * <p>Verify that if an empty list of remotes not in the network is 
	 * successfully added.</p>
	 */
	@Test
	public void testAddRemoteDevicesEmptyList() {
		// Setup the resources for the test.
		List<RemoteXBeeDevice> list = new ArrayList<RemoteXBeeDevice>(0);
		assertThat(network.getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		network.addRemoteDevices(list);
		
		// Verify the result.
		assertThat(network.getNumberOfDevices(), is(equalTo(0)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#addRemoteDevices(java.util.List)}.
	 * 
	 * <p>Verify that if a list with null items are added to the network.</p>
	 */
	@Test
	public void testAddRemoteDevicesListWithNullItem() {
		// Setup the resources for the test.
		List<RemoteXBeeDevice> list = new ArrayList<RemoteXBeeDevice>(0);
		list.add(null);
		list.add(null);
		assertThat(network.getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		List<RemoteXBeeDevice> added = network.addRemoteDevices(list);
		
		// Verify the result.
		assertThat(network.getNumberOfDevices(), is(equalTo(0)));
		assertThat(added.size(), is(equalTo(0)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#addRemoteDevices(java.util.List)}.
	 * 
	 * <p>Verify that if a remote not in the network is successfully added.</p>
	 */
	@Test
	public void testAddRemoteDevicesNotInTheNetwork() {
		// Setup the resources for the test.
		List<RemoteXBeeDevice> list = new ArrayList<RemoteXBeeDevice>(0);
		list.add(remoteDevice1);
		list.add(remoteDevice2);
		
		assertThat(network.getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		List<RemoteXBeeDevice> added = network.addRemoteDevices(list);
		
		// Verify the result.
		assertThat(network.getNumberOfDevices(), is(equalTo(list.size())));
		assertThat(network.getDevices().contains(remoteDevice1), is(equalTo(true)));
		assertThat(network.getDevices().contains(remoteDevice2), is(equalTo(true)));
		assertThat(added.size(), is(equalTo(list.size())));
		Mockito.verify(network).addRemoteDevice(remoteDevice1);
		Mockito.verify(network).addRemoteDevice(remoteDevice2);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#addRemoteDevices(java.util.List)}.
	 * 
	 * <p>Verify that if a list of remotes already in the network is updated.</p>
	 */
	@Test
	public void testAddRemoteDevicesAlreadyInTheNetwork() {
		// Setup the resources for the test.
		
		XBee64BitAddress add64 = new XBee64BitAddress("23456789ABCDEF89");
		XBee16BitAddress add16 = new XBee16BitAddress("5555");
		
		RemoteXBeeDevice dev = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(dev.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN);
		Mockito.when(dev.getNodeID()).thenReturn(null);
		Mockito.when(dev.get64BitAddress()).thenReturn(add64);
		Mockito.when(dev.get16BitAddress()).thenReturn(XBee16BitAddress.UNKNOWN_ADDRESS);
		
		List<RemoteXBeeDevice> list = new ArrayList<RemoteXBeeDevice>(0);
		
		RemoteXBeeDevice dev2 = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(dev2.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN);
		Mockito.when(dev2.getNodeID()).thenReturn("newID");
		Mockito.when(dev2.get64BitAddress()).thenReturn(add64);
		Mockito.when(dev2.get16BitAddress()).thenReturn(add16);
		
		list.add(dev2);
		list.add(remoteDevice2);
		
		network.addRemoteDevice(dev);
		network.addRemoteDevice(remoteDevice1);
		
		assertThat(network.getNumberOfDevices(), is(equalTo(2)));
		assertThat(network.getDevices().contains(dev), is(equalTo(true)));
		assertThat(network.getDevices().contains(remoteDevice1), is(equalTo(true)));
		
		// Call the method under test.
		List<RemoteXBeeDevice> added = network.addRemoteDevices(list);
		
		// Verify the result.
		assertThat(network.getNumberOfDevices(), is(equalTo(list.size() + 1)));
		assertThat(network.getDevices().contains(dev2), is(equalTo(false)));
		assertThat(network.getDevices().contains(dev), is(equalTo(true)));
		assertThat(network.getDevices().contains(remoteDevice1), is(equalTo(true)));
		assertThat(network.getDevices().contains(remoteDevice2), is(equalTo(true)));
		assertThat(added.size(), is(equalTo(list.size())));
		Mockito.verify(dev).updateDeviceDataFrom(dev2);
		Mockito.verify(network).addRemoteDevice(dev2);
		Mockito.verify(network).addRemoteDevice(remoteDevice2);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#removeRemoteDevice(RemoteXBeeDevice)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when trying to 
	 * remove a {@code null} device.</p>
	 */
	@Test
	public void testRemoveRemoteDeviceNull() {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Remote device cannot be null.")));
		
		RemoteXBeeDevice dev = null;
		
		// Call the method under test.
		network.removeRemoteDevice(dev);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#removeRemoteDevice(RemoteXBeeDevice)}.
	 * 
	 * <p>Verify that if a remote not in the network is not removed.</p>
	 */
	@Test
	public void testRemoteRemoteDeviceNotInTheNetwork() {
		// Setup the resources for the test.
		network.addRemoteDevice(remoteDevice1);
		
		// Call the method under test.
		network.removeRemoteDevice(remoteDevice2);
		
		// Verify the result.
		assertThat(network.getNumberOfDevices(), is(equalTo(1)));
		assertThat(network.getDevices().contains(remoteDevice1), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#removeRemoteDevice(RemoteXBeeDevice)}.
	 * 
	 * <p>Verify that if a remote already in the network is removed.</p>
	 */
	@Test
	public void testRemoveRemoteDeviceAlreadyInTheNetwork() {
		// Setup the resources for the test.
		network.addRemoteDevice(remoteDevice1);
		
		// Call the method under test.
		network.removeRemoteDevice(remoteDevice1);
		
		// Verify the result.
		assertThat(network.getNumberOfDevices(), is(equalTo(0)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#removeRemoteDevice(RemoteXBeeDevice)}.
	 * 
	 * <p>Verify that if a remote (without 64-bit address) in the network 
	 * is successfully removed.</p>
	 */
	@Test
	public void testRemoveRemoteDeviceAlreadyInTheNetworkOnly16BitAddr() {
		// Setup the resources for the test.
		network.addRemoteDevice(remoteDeviceUN64Addr);
		
		// Call the method under test.
		network.removeRemoteDevice(remoteDeviceUN64Addr);
		
		// Verify the result.
		assertThat(network.getNumberOfDevices(), is(equalTo(0)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#removeRemoteDevice(RemoteXBeeDevice)}.
	 * 
	 * <p>Verify that if a remote (without 64-bit/16-bit addresses) not in the 
	 * network is not removed.</p>
	 */
	@Test
	public void testRemoveRemoteDeviceNotInTheNetworkUnknown64And16BitAddr() {
		// Setup the resources for the test.
		RemoteXBeeDevice dev = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(dev.getXBeeProtocol()).thenReturn(XBeeProtocol.UNKNOWN);
		Mockito.when(dev.getNodeID()).thenReturn(null);
		Mockito.when(dev.get64BitAddress()).thenReturn(XBee64BitAddress.UNKNOWN_ADDRESS);
		Mockito.when(dev.get16BitAddress()).thenReturn(XBee16BitAddress.UNKNOWN_ADDRESS);
		
		network.addRemoteDevice(remoteDevice1);
		
		assertThat(network.getNumberOfDevices(), is(equalTo(1)));
		
		// Call the method under test.
		network.removeRemoteDevice(dev);
		
		// Verify the result.
		assertThat(network.getNumberOfDevices(), is(equalTo(1)));
		assertThat(network.getDevices().contains(remoteDevice1), is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#clearDeviceList()}.
	 * 
	 * <p>Verify that the network is properly cleared.</p>
	 */
	@Test
	public void testClearDeviceList() {
		// Setup the resources for the test.
		network.addRemoteDevice(remoteDevice1);
		network.addRemoteDevice(remoteDevice2);
		network.addRemoteDevice(remoteDevice3);
		network.addRemoteDevice(remoteDeviceUNI);
		network.addRemoteDevice(remoteDeviceUN64Addr);
		
		// Call the method under test.
		network.clearDeviceList();
		
		// Verify the result.
		assertThat(network.getNumberOfDevices(), is(equalTo(0)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#getNumberOfDevices()}.
	 * 
	 * <p>Verify that the number of devices is properly updated.</p>
	 */
	@Test
	public void testGetNumberOfDevices() {
		
		assertThat(network.getNumberOfDevices(), is(equalTo(0)));
		
		network.addRemoteDevice(remoteDevice1);
		assertThat(network.getNumberOfDevices(), is(equalTo(1)));
		
		network.addRemoteDevice(remoteDevice2);
		assertThat(network.getNumberOfDevices(), is(equalTo(2)));
		
		network.addRemoteDevice(remoteDevice3);
		assertThat(network.getNumberOfDevices(), is(equalTo(3)));
		
		network.addRemoteDevice(remoteDeviceUNI);
		assertThat(network.getNumberOfDevices(), is(equalTo(4)));
		
		network.addRemoteDevice(remoteDeviceUN64Addr);
		assertThat(network.getNumberOfDevices(), is(equalTo(5)));
		
		network.removeRemoteDevice(remoteDeviceUN64Addr);
		assertThat(network.getNumberOfDevices(), is(equalTo(4)));
		
		network.removeRemoteDevice(remoteDeviceUNI);
		assertThat(network.getNumberOfDevices(), is(equalTo(3)));
		
		network.removeRemoteDevice(remoteDevice3);
		assertThat(network.getNumberOfDevices(), is(equalTo(2)));
		
		network.removeRemoteDevice(remoteDevice2);
		assertThat(network.getNumberOfDevices(), is(equalTo(1)));
		
		network.removeRemoteDevice(remoteDevice1);
		assertThat(network.getNumberOfDevices(), is(equalTo(0)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#toString()}.
	 */
	@Test
	public void testToString() {
		
		// Call the method under test.
		String result = network.toString();
		
		// Verify the result.
		assertThat(result, is(equalTo(network.getClass().getName() 
				+ " [" + localDevice.toString() + "] @" 
				+ Integer.toHexString(network.hashCode()))));
	}
}

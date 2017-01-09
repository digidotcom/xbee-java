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
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
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
		PowerMockito.when(ndMock.discoverDevice(Mockito.anyString())).thenReturn(idFoundDevice);
		PowerMockito.when(ndMock.discoverDevices(Mockito.anyListOf(String.class))).thenReturn(idFoundDevices);
		
		network = PowerMockito.spy(new XBeeNetwork(deviceMock));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevice(String)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when passing a 
	 * {@code null} id.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=NullPointerException.class)
	public final void testDiscoverDeviceNullId() throws XBeeException {
		// Call the method under test.
		network.discoverDevice(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevice(String)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * passing an empty id.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public final void testDiscoverDeviceEmptyId() throws XBeeException {
		// Call the method under test.
		network.discoverDevice("");
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevice(String)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDevice() throws XBeeException {
		// Setup the resources for the test.
		String id = "id";
		
		// Call the method under test.
		RemoteXBeeDevice found = network.discoverDevice(id);
		
		// Verify the result.
		Mockito.verify(ndMock, Mockito.times(1)).discoverDevice(id);
		
		assertThat("Found device must not be null", found, is(not(nullValue())));
		assertThat("Not expected 64-bit address in found device", found.get64BitAddress(), is(equalTo(idFoundDevice.get64BitAddress())));
		assertThat("Not expected 16-bit address in found device", found.get16BitAddress(), is(equalTo(idFoundDevice.get16BitAddress())));
		assertThat("Not expected id in found device", found.getNodeID(), is(equalTo(idFoundDevice.getNodeID())));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevices(List)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when passing a 
	 * {@code null} id.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=NullPointerException.class)
	public final void testDiscoverDevicesNullList() throws XBeeException {
		// Call the method under test.
		network.discoverDevices(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevices(List)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * passing an empty id.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=IllegalArgumentException.class)
	public final void testDiscoverDevicesEmptyList() throws XBeeException {
		// Setup the resources for the test.
		List<String> list = new ArrayList<String>();
		
		// Call the method under test.
		network.discoverDevices(list);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevices(List)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDevices() throws XBeeException {
		// Setup the resources for the test.
		List<String> list = new ArrayList<String>();
		list.add("id");
		
		// Call the method under test.
		List<RemoteXBeeDevice> found = network.discoverDevices(list);
		
		// Verify the result.
		Mockito.verify(ndMock, Mockito.times(1)).discoverDevices(list);
		
		assertThat("Found device list must not be null", found, is(not(nullValue())));
		assertThat("Found device list must have one device", found.size(), is(equalTo(1)));
		
		RemoteXBeeDevice d = found.get(0);
		assertThat("Not expected 64-bit address in found device", d.get64BitAddress(), is(equalTo(idFoundDevice.get64BitAddress())));
		assertThat("Not expected 16-bit address in found device", d.get16BitAddress(), is(equalTo(idFoundDevice.get16BitAddress())));
		assertThat("Not expected id in found device", d.getNodeID(), is(equalTo(idFoundDevice.getNodeID())));
	}
}

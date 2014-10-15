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

import java.util.EnumSet;
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
import com.digi.xbee.api.listeners.IDiscoveryListener;
import com.digi.xbee.api.models.DiscoveryOptions;

@PrepareForTest({XBeeNetwork.class})
@RunWith(PowerMockRunner.class)
public class XBeeNetworkDiscoverDevicesListenerTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Variables.
	private XBeeNetwork network;
	
	private XBeeDevice deviceMock;
	
	private NodeDiscovery ndMock;
	
	@Before
	public void setUp() throws Exception {
		ndMock = PowerMockito.mock(NodeDiscovery.class);
		
		deviceMock = PowerMockito.mock(XBeeDevice.class);
		IConnectionInterface cInterfaceMock = PowerMockito.mock(IConnectionInterface.class);
		
		PowerMockito.when(deviceMock.getConnectionInterface()).thenReturn(cInterfaceMock);
		PowerMockito.when(cInterfaceMock.toString()).thenReturn("Mocked IConnectionInterface for XBeeNetwork test.");
		
		PowerMockito.whenNew(NodeDiscovery.class).withArguments(deviceMock).thenReturn(ndMock);
		
		network = PowerMockito.spy(new XBeeNetwork(deviceMock));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevices(IDiscoveryListener)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when 
	 * passing a null listener.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDevicesNullListener() throws XBeeException {
		// Setup the resources for the test.
		IDiscoveryListener listener = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Listener cannot be null.")));
		
		// Call the method under test.
		network.discoverDevices(listener);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevices(IDiscoveryListener)}.
	 */
	@Test
	public final void testDiscoverDevicesValidListener() {
		// Setup the resources for the test.
		IDiscoveryListener listener = PowerMockito.mock(IDiscoveryListener.class);
		
		// Call the method under test.
		network.discoverDevices(listener);
		
		// Verify the result.
		Mockito.verify(ndMock, Mockito.times(1)).discoverDevices(listener, null, NodeDiscovery.USE_DEVICE_TIMEOUT);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevices(IDiscoveryListener, long)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * pass a negative value for the timeout.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDevicesNegativeTimeout() throws XBeeException {
		// Setup the resources for the test.
		IDiscoveryListener listener = PowerMockito.mock(IDiscoveryListener.class);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("The timeout must be bigger than 0.")));
		
		// Call the method under test.
		network.discoverDevices(listener, -6);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevices(IDiscoveryListener, long)}.
	 */
	@Test
	public final void testDiscoverDevicesValidListenerAndTimeout() {
		// Setup the resources for the test.
		IDiscoveryListener listener = PowerMockito.mock(IDiscoveryListener.class);
		long timeout = 500;
		
		// Call the method under test.
		network.discoverDevices(listener, timeout);
		
		// Verify the result.
		Mockito.verify(ndMock, Mockito.times(1)).discoverDevices(listener, null, timeout);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevices(IDiscoveryListener, Set, long)}.
	 * 
	 * <p>An {@code IllegalArgumentException} exception must be thrown when 
	 * pass a negative value for the timeout.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testDiscoverDevicesOptionsNegativeTimeout() throws XBeeException {
		// Setup the resources for the test.
		IDiscoveryListener listener = PowerMockito.mock(IDiscoveryListener.class);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("The timeout must be bigger than 0.")));
		
		// Call the method under test.
		network.discoverDevices(listener, (Set<DiscoveryOptions>) null, -6);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#discoverDevices(IDiscoveryListener, Set, long)}.
	 */
	@Test
	public final void testDiscoverDevicesValidListenerTimeoutAndOptions() {
		// Setup the resources for the test.
		IDiscoveryListener listener = PowerMockito.mock(IDiscoveryListener.class);
		long timeout = 500;
		Set<DiscoveryOptions> options = EnumSet.of(DiscoveryOptions.APPEND_DD, DiscoveryOptions.DISCOVER_MYSELF);
		
		// Call the method under test.
		network.discoverDevices(listener, options, timeout);
		
		// Verify the result.
		Mockito.verify(ndMock, Mockito.times(1)).discoverDevices(listener, options, timeout);
	}
}

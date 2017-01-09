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

import static org.junit.Assert.*;

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
import org.powermock.reflect.Whitebox;

import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.listeners.IDiscoveryListener;

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
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#addDiscoveryListener(IDiscoveryListener)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when passing 
	 * a null listener.</p>
	 */
	@Test(expected=NullPointerException.class)
	public void testAddNullDiscoveryListener() {
		// Setup the resources for the test.
		IDiscoveryListener listener = null;
		
		// Call the method under test.
		network.addDiscoveryListener(listener);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#addDiscoveryListener(IDiscoveryListener)}.
	 */
	@Test
	public void testAddDiscoveryListener() {
		// Setup the resources for the test.
		IDiscoveryListener listener = PowerMockito.mock(IDiscoveryListener.class);
		
		// Call the method under test.
		network.addDiscoveryListener(listener);
		
		// Verify that the listener has been added successfully.
		List<IDiscoveryListener> internalListenersList = Whitebox.<List<IDiscoveryListener>> getInternalState(network, "discoveryListeners");
		assertEquals(internalListenersList.size(), 1);
		assertEquals(internalListenersList.get(0), listener);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#removeDiscoveryListener(IDiscoveryListener)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when passing 
	 * a null listener.</p>
	 */
	@Test(expected=NullPointerException.class)
	public void testRemoveNullDiscoveryListener() {
		// Setup the resources for the test.
		IDiscoveryListener listener = null;
		
		// Call the method under test.
		network.removeDiscoveryListener(listener);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#removeDiscoveryListener(IDiscoveryListener)}.
	 */
	@Test
	public void testRemoveDiscoveryListener() {
		// Setup the resources for the test.
		IDiscoveryListener listener = PowerMockito.mock(IDiscoveryListener.class);
		
		// Add the listener.
		network.addDiscoveryListener(listener);
		
		// Verify that the listener has been added successfully.
		List<IDiscoveryListener> internalListenersList = Whitebox.<List<IDiscoveryListener>> getInternalState(network, "discoveryListeners");
		assertEquals(internalListenersList.size(), 1);
		assertEquals(internalListenersList.get(0), listener);
		
		// Call the method under test.
		network.removeDiscoveryListener(listener);
		
		// Verify that the listener has been removed successfully.
		assertEquals(internalListenersList.size(), 0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#startDiscoveryProcess()}.
	 */
	@Test
	public void testStartDiscoveryProcessWithoutListeners() {
		// Setup the resources for the test.
		ArrayList<IDiscoveryListener> listeners = new ArrayList<IDiscoveryListener>();
		
		// Call the method under test.
		network.startDiscoveryProcess();
		
		// Verify the result.
		Mockito.verify(ndMock, Mockito.times(1)).startDiscoveryProcess(listeners);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#startDiscoveryProcess()}.
	 * 
	 * <p>An {@code IllegalStateException} exception must be thrown when trying
	 * to start a discovery process when it is already running.</p>
	 */
	@Test(expected=IllegalStateException.class)
	public void testStartDiscoveryProcessAlreadyRunning() {
		// Setup the resources for the test.
		Mockito.when(ndMock.isRunning()).thenReturn(true);
		
		// Call the method under test.
		network.startDiscoveryProcess();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#startDiscoveryProcess()}.
	 */
	@Test
	public void testStartDiscoveryProcess() {
		// Setup the resources for the test.
		IDiscoveryListener listener = PowerMockito.mock(IDiscoveryListener.class);
		ArrayList<IDiscoveryListener> listeners = new ArrayList<IDiscoveryListener>();
		listeners.add(listener);
		
		// Add the listener.
		network.addDiscoveryListener(listener);
		
		// Call the method under test.
		network.startDiscoveryProcess();
		
		// Verify the result.
		Mockito.verify(ndMock, Mockito.times(1)).startDiscoveryProcess(listeners);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#stopDiscoveryProcess()}.
	 */
	@Test
	public void testStopDiscoveryProcess() {
		// Call the method under test.
		network.stopDiscoveryProcess();
		
		// Verify the result.
		Mockito.verify(ndMock, Mockito.times(1)).stopDiscoveryProcess();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeNetwork#isDiscoveryRunning()}.
	 */
	@Test
	public void testIsDiscoveryRunning() {
		// Call the method under test.
		boolean isRunning = network.isDiscoveryRunning();
		
		// Verify the result.
		Mockito.verify(ndMock, Mockito.times(1)).isRunning();
		
		assertEquals(isRunning, Whitebox.getInternalState(ndMock, "running"));
	}
}

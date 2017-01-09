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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.digi.xbee.api.NodeDiscovery;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import com.digi.xbee.api.models.XBee64BitAddress;

@RunWith(PowerMockRunner.class)
@PrepareForTest({NodeDiscovery.class})
public class IDiscoveryListenerTest {
	
	// Constants.
	private static final XBee64BitAddress XBEE_64_BIT_ADDRESS = new XBee64BitAddress("0123456789ABCDEF");
	
	private static final String ERROR = "--error--";
	
	private static final String NOTIFY_DEVICE_DISCOVERED = "notifyDeviceDiscovered";
	private static final String NOTIFY_DISCOVERY_ERROR = "notifyDiscoveryError";
	private static final String NOTIFY_DISCOVERY_FINISHED = "notifyDiscoveryFinished";
	
	// Variables.	
	private static RemoteXBeeDevice remoteDevice;
	
	private ArrayList<IDiscoveryListener> listeners = new ArrayList<IDiscoveryListener>();
	
	private MyDiscoverListener discoverListener;
	
	private NodeDiscovery nodeDiscovery;
	
	@BeforeClass
	public static void setupOnce() {
		// Mock the RemoteXBeeDevice.
		remoteDevice = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(remoteDevice.get64BitAddress()).thenReturn(XBEE_64_BIT_ADDRESS);
	}
	
	@Before
	public void setup() throws Exception {
		// Discover listener.
		discoverListener = PowerMockito.spy(new MyDiscoverListener());
		listeners.add(discoverListener);
		
		// Node discovery.
		nodeDiscovery = PowerMockito.spy(new NodeDiscovery(Mockito.mock(XBeeDevice.class)));
		
		// Stub the 'notifyDeviceDiscovered' method of the nodeDiscovery instance.
		PowerMockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				RemoteXBeeDevice remote = (RemoteXBeeDevice) args[1]; // args[0] is the listener.
				discoverListener.deviceDiscovered(remote);
				return null;
			}
		}).when(nodeDiscovery, NOTIFY_DEVICE_DISCOVERED, Mockito.any(List.class), (RemoteXBeeDevice) Mockito.any());
		
		// Stub the 'notifyDiscoveryError' method of the nodeDiscovery instance.
		PowerMockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				String error = (String) args[1]; // args[0] is the listener.
				discoverListener.discoveryError(error);
				return null;
			}
		}).when(nodeDiscovery, NOTIFY_DISCOVERY_ERROR, Mockito.any(List.class), Mockito.anyString());
		
		// Stub the 'notifyDiscoveryFinished' method of the nodeDiscovery instance.
		PowerMockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				String error = (String) args[1]; // args[0] is the listener.
				discoverListener.discoveryFinished(error);
				return null;
			}
		}).when(nodeDiscovery, NOTIFY_DISCOVERY_FINISHED, Mockito.any(List.class), Mockito.anyString());
	}

	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IDiscoveryListener#deviceDiscovered(RemoteXBeeDevice)}.
	 * 
	 * <p>Verify that the device discovered callback of the IDiscoveryListener interface is executed 
	 * correctly when a remote device is discovered.</p>
	 */
	@Test
	public void testDeviceDiscoveredEvent() {
		discoverListener.deviceDiscovered(remoteDevice);
		
		assertEquals(remoteDevice, discoverListener.getRemoteXBeeDevice());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IDiscoveryListener#discoveryError(String)}.
	 * 
	 * <p>Verify that the discovery error callback of the IDiscoveryListener interface is executed 
	 * correctly when an error occurs.</p>
	 */
	@Test
	public void testDiscoveryErrorEvent() {
		discoverListener.discoveryError(ERROR);
		
		assertEquals(ERROR, discoverListener.getError());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IDiscoveryListener#discoveryFinished(String)}.
	 * 
	 * <p>Verify that the discovery finished callback of the IDiscoveryListener interface is executed 
	 * correctly when the discovery process finishes.</p>
	 */
	@Test
	public void testDiscoveryFinishedEvent() {
		discoverListener.discoveryFinished(null);
		
		assertNull(discoverListener.getError());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IDiscoveryListener#discoveryFinished(String)}.
	 * 
	 * <p>Verify that the discovery finished callback of the IDiscoveryListener interface is executed 
	 * correctly when the discovery process finishes with error.</p>
	 */
	@Test
	public void testDiscoveryFinishedWithErrorEvent() {
		discoverListener.discoveryFinished(ERROR);
		
		assertEquals(ERROR, discoverListener.getError());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IDiscoveryListener#deviceDiscovered(RemoteXBeeDevice)}.
	 * 
	 * Verify that, when a device is discovered, the callback of the listener is executed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceDiscovered() throws Exception {
		// Fire the private notifyDeviceDiscovered method of the nodeDiscovery.
		Whitebox.invokeMethod(nodeDiscovery, NOTIFY_DEVICE_DISCOVERED, listeners, remoteDevice);
		
		// Verify that the listener callback was executed one time.
		Mockito.verify(discoverListener, Mockito.times(1)).deviceDiscovered(remoteDevice);
		
		assertEquals(remoteDevice, discoverListener.getRemoteXBeeDevice());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IDiscoveryListener#discoveryError(String)}.
	 * 
	 * Verify that, when an error occurs, the callback of the listener is executed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDiscoveryError() throws Exception {
		// Fire the private notifyDiscoveryError method of the nodeDiscovery.
		Whitebox.invokeMethod(nodeDiscovery, NOTIFY_DISCOVERY_ERROR, listeners, ERROR);
		
		// Verify that the listener callback was executed one time.
		Mockito.verify(discoverListener, Mockito.times(1)).discoveryError(ERROR);
		
		assertEquals(ERROR, discoverListener.getError());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IDiscoveryListener#discoveryFinished(String)}.
	 * 
	 * Verify that, when the process finishes, the callback of the listener is executed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDiscoveryFinished() throws Exception {
		// Fire the private notifyDiscoveryFinished method of the nodeDiscovery.
		Whitebox.invokeMethod(nodeDiscovery, NOTIFY_DISCOVERY_FINISHED, listeners, null);
		
		// Verify that the listener callback was executed one time.
		Mockito.verify(discoverListener, Mockito.times(1)).discoveryFinished(null);
		
		assertNull(discoverListener.getError());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IDiscoveryListener#discoveryFinished(String)}.
	 * 
	 * Verify that, when the process finishes with error, the callback of the listener is executed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDiscoveryFinishedWithError() throws Exception {
		// Fire the private notifyDiscoveryFinished method of the nodeDiscovery.
		Whitebox.invokeMethod(nodeDiscovery, NOTIFY_DISCOVERY_FINISHED, listeners, ERROR);
		
		// Verify that the listener callback was executed one time.
		Mockito.verify(discoverListener, Mockito.times(1)).discoveryFinished(ERROR);
		
		assertEquals(ERROR, discoverListener.getError());
	}
	
	/**
	 * Helper class to test the IDiscoveryListener.
	 *
	 */
	private class MyDiscoverListener implements IDiscoveryListener {
		
		// Variables.
		private RemoteXBeeDevice discoveredDevice = null;
		private String error = "";

		/*
		 * (non-Javadoc)
		 * @see com.digi.xbee.api.listeners.IDiscoveryListener#deviceDiscovered(com.digi.xbee.api.RemoteXBeeDevice)
		 */
		@Override
		public void deviceDiscovered(RemoteXBeeDevice discoveredDevice) {
			this.discoveredDevice = discoveredDevice;
		}

		/*
		 * (non-Javadoc)
		 * @see com.digi.xbee.api.listeners.IDiscoveryListener#discoveryError(java.lang.String)
		 */
		@Override
		public void discoveryError(String error) {
			this.error = error;
		}

		/*
		 * (non-Javadoc)
		 * @see com.digi.xbee.api.listeners.IDiscoveryListener#discoveryFinished(java.lang.String)
		 */
		@Override
		public void discoveryFinished(String error) {
			this.error = error;
		}
		
		/**
		 * Retrieves the discovered remote XBee device.
		 * 
		 * @return The discovered remote XBee device.
		 */
		public RemoteXBeeDevice getRemoteXBeeDevice() {
			return discoveredDevice;
		}
		
		/**
		 * Retrieves the error message.
		 * 
		 * @return The error message.
		 */
		public String getError() {
			return error;
		}
	}

}

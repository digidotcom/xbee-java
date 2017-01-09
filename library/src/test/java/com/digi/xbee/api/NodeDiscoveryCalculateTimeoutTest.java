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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.utils.ByteUtils;

@PrepareForTest({XBeeDevice.class, NodeDiscovery.class})
@RunWith(PowerMockRunner.class)
public class NodeDiscoveryCalculateTimeoutTest {
	
	// Constants.
	private static final String CALCULATE_TIMEOUT_METHOD = "calculateTimeout";
	private static final String NOTIFY_DISCOVERY_ERROR_METHOD = "notifyDiscoveryError";
	
	// Variables.
	private NodeDiscovery nd;
	
	private XBeeDevice deviceMock;
	
	private XBeeNetwork networkMock;
	
	@Before
	public void setUp() {
		deviceMock = PowerMockito.mock(XBeeDevice.class);
		networkMock = PowerMockito.mock(XBeeNetwork.class);
		
		PowerMockito.when(deviceMock.isOpen()).thenReturn(true);
		PowerMockito.when(deviceMock.getNetwork()).thenReturn(networkMock);
		
		nd = PowerMockito.spy(new NodeDiscovery(deviceMock));
	}
	
	/**
	 * Check that if the N? and NT parameters cannot be read, the timeout used
	 * is the default one.
	 * 
	 * @throws XBeeException
	 */
	@Test
	public void testCalculateTimeoutErrorReadingParameters() throws Exception {
		// Setup the resources for the test.
		PowerMockito.doThrow(new XBeeException()).when(deviceMock).getParameter("N?");
		PowerMockito.doThrow(new XBeeException()).when(deviceMock).getParameter("NT");
		
		// Call the method under test.
		long timeout = Whitebox.<Long> invokeMethod(nd, CALCULATE_TIMEOUT_METHOD, (List<IDiscoveryListenerTest>) null);
		assertEquals(NodeDiscovery.DEFAULT_TIMEOUT, timeout);
		
		// Verify that the error is notified to the listeners.
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(NOTIFY_DISCOVERY_ERROR_METHOD, Mockito.anyList(), Mockito.anyString());
	}
	
	/**
	 * Check that, in ZigBee devices, the timeout used is NT.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCalculateTimeoutZigBeeDevice() throws Exception {
		// Setup the resources for the test.
		long timeout = 7000;
		byte[] deviceTimeout = ByteUtils.longToByteArray(timeout / 100);
		
		PowerMockito.when(deviceMock.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		PowerMockito.doThrow(new XBeeException()).when(deviceMock).getParameter("N?");
		PowerMockito.when(deviceMock.getParameter("NT")).thenReturn(deviceTimeout);
		
		// Call the method under test.
		long result = Whitebox.<Long> invokeMethod(nd, CALCULATE_TIMEOUT_METHOD, (List<IDiscoveryListenerTest>) null);
		assertEquals(timeout, result);
	}
	
	/**
	 * Check that, in DigiMesh devices whose firmware does not have the N?
	 * parameter, the timeout used is NT + 3000.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCalculateTimeoutDigiMeshDeviceNonExistingN() throws Exception {
		// Setup the resources for the test.
		long timeout = 7000;
		byte[] deviceTimeout = ByteUtils.longToByteArray(timeout / 100);
		
		PowerMockito.when(deviceMock.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_MESH);
		PowerMockito.doThrow(new XBeeException()).when(deviceMock).getParameter("N?");
		PowerMockito.when(deviceMock.getParameter("NT")).thenReturn(deviceTimeout);
		PowerMockito.when(deviceMock.getParameter("SM")).thenReturn(new byte[] {0x00});
		
		// Call the method under test.
		long result = Whitebox.<Long> invokeMethod(nd, CALCULATE_TIMEOUT_METHOD, (List<IDiscoveryListenerTest>) null);
		assertEquals(timeout + 3000, result);
	}
	
	/**
	 * Check that, in DigiMesh devices whose firmware has the N? parameter, 
	 * the timeout used is the value of that parameter.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCalculateTimeoutDigiMeshDeviceExistingN() throws Exception {
		// Setup the resources for the test.
		long timeout = 7000;
		byte[] deviceTimeout = ByteUtils.longToByteArray(timeout);
		
		PowerMockito.when(deviceMock.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_MESH);
		PowerMockito.when(deviceMock.getParameter("N?")).thenReturn(deviceTimeout);
		PowerMockito.when(deviceMock.getParameter("SM")).thenReturn(new byte[] {0x00});
		
		// Call the method under test.
		long result = Whitebox.<Long> invokeMethod(nd, CALCULATE_TIMEOUT_METHOD, (List<IDiscoveryListenerTest>) null);
		assertEquals(timeout, result);
	}
	
	/**
	 * Check that, in DigiMesh devices whose firmware does not have the N? 
	 * parameter and are configured as 'Sleep Support' (SM=7), the timeout used 
	 * is 2.1 * (NT + 3000).
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCalculateTimeoutDigiMeshDeviceSleepSupportNonExistingN() throws Exception {
		// Setup the resources for the test.
		long timeout = 7000;
		byte[] deviceTimeout = ByteUtils.longToByteArray(timeout / 100);
		
		PowerMockito.when(deviceMock.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_MESH);
		PowerMockito.doThrow(new XBeeException()).when(deviceMock).getParameter("N?");
		PowerMockito.when(deviceMock.getParameter("NT")).thenReturn(deviceTimeout);
		PowerMockito.when(deviceMock.getParameter("SM")).thenReturn(new byte[] {0x07});
		
		// Call the method under test.
		long result = Whitebox.<Long> invokeMethod(nd, CALCULATE_TIMEOUT_METHOD, (List<IDiscoveryListenerTest>) null);
		assertEquals((long) (2.1 * (timeout + 3000)), result);
	}
	
	/**
	 * Check that, in DigiMesh devices whose firmware has the N? parameter and 
	 * are configured as 'Sleep Support' (SM=7), the timeout used is 
	 * 2.1 * N?.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCalculateTimeoutDigiMeshDeviceSleepSupportExistingN() throws Exception {
		// Setup the resources for the test.
		long timeout = 7000;
		byte[] deviceTimeout = ByteUtils.longToByteArray(timeout);
		
		PowerMockito.when(deviceMock.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_MESH);
		PowerMockito.when(deviceMock.getParameter("N?")).thenReturn(deviceTimeout);
		PowerMockito.when(deviceMock.getParameter("SM")).thenReturn(new byte[] {0x07});
		
		// Call the method under test.
		long result = Whitebox.<Long> invokeMethod(nd, CALCULATE_TIMEOUT_METHOD, (List<IDiscoveryListenerTest>) null);
		assertEquals((long) (2.1 * timeout), result);
	}
	
	/**
	 * Check that, in DigiPoint devices whose firmware does not have the N?
	 * parameter, the timeout used is NT + 8000.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCalculateTimeoutDigiPointDeviceNonExistingN() throws Exception {
		// Setup the resources for the test.
		long timeout = 7000;
		byte[] deviceTimeout = ByteUtils.longToByteArray(timeout / 100);
		
		PowerMockito.when(deviceMock.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_POINT);
		PowerMockito.doThrow(new XBeeException()).when(deviceMock).getParameter("N?");
		PowerMockito.when(deviceMock.getParameter("NT")).thenReturn(deviceTimeout);
		
		// Call the method under test.
		long result = Whitebox.<Long> invokeMethod(nd, CALCULATE_TIMEOUT_METHOD, (List<IDiscoveryListenerTest>) null);
		assertEquals(timeout + 8000, result);
	}
	
	/**
	 * Check that, in DigiPoint devices whose firmware has the N? parameter, 
	 * the timeout used is the value of that parameter.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCalculateTimeoutDigiPointDeviceExistingN() throws Exception {
		// Setup the resources for the test.
		long timeout = 7000;
		byte[] deviceTimeout = ByteUtils.longToByteArray(timeout);
		
		PowerMockito.when(deviceMock.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_POINT);
		PowerMockito.when(deviceMock.getParameter("N?")).thenReturn(deviceTimeout);
		
		// Call the method under test.
		long result = Whitebox.<Long> invokeMethod(nd, CALCULATE_TIMEOUT_METHOD, (List<IDiscoveryListenerTest>) null);
		assertEquals(timeout, result);
	}

}

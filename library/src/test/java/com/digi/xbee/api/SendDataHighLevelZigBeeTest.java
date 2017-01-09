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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.common.ExplicitAddressingPacket;
import com.digi.xbee.api.packet.common.TransmitPacket;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBeeDevice.class})
public class SendDataHighLevelZigBeeTest {
	
	// Constants.
	private static final XBee64BitAddress XBEE_64BIT_ADDRESS = new XBee64BitAddress("0123456789ABCDEF");
	private static final XBee16BitAddress XBEE_16BIT_ADDRESS = new XBee16BitAddress("0123");
	
	private static final int SOURCE_ENDPOINT = 0xA0;
	private static final int DESTINATION_ENDPOINT = 0xA1;
	private static final int CLUSTER_ID = 0x1554;
	private static final int PROFILE_ID = 0xC105;
	
	private static final String DATA = "data";
	
	// Variables.
	private ZigBeeDevice zigbeeDevice;
	private RemoteZigBeeDevice mockedRemoteZigBeeDevice;
	
	private TransmitPacket transmitPacket;
	private ExplicitAddressingPacket explicitAddressingPacket;
	
	@Before
	public void setup() throws Exception {
		// Instantiate a ZigBeeDevice object with a mocked interface.
		zigbeeDevice = PowerMockito.spy(new ZigBeeDevice(Mockito.mock(SerialPortRxTx.class)));
		
		// Mock Transmit packet.
		transmitPacket = Mockito.mock(TransmitPacket.class);
		// Mock the explicit addressing packet.
		explicitAddressingPacket = Mockito.mock(ExplicitAddressingPacket.class);
		
		// Mock a RemoteZigBeeDevice to be used as parameter to send data.
		mockedRemoteZigBeeDevice = Mockito.mock(RemoteZigBeeDevice.class);
		Mockito.when(mockedRemoteZigBeeDevice.get64BitAddress()).thenReturn(XBEE_64BIT_ADDRESS);
		Mockito.when(mockedRemoteZigBeeDevice.get16BitAddress()).thenReturn(XBEE_16BIT_ADDRESS);
		
		// Whenever a TransmitPacket class is instantiated, the mocked transmitPacket packet should be returned.
		PowerMockito.whenNew(TransmitPacket.class).withAnyArguments().thenReturn(transmitPacket);
		// Whenever an ExplicitAddressingPacket class is instantiated, the mocked explicitAddressingPacket packet should be returned.
		PowerMockito.whenNew(ExplicitAddressingPacket.class).withAnyArguments().thenReturn(explicitAddressingPacket);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendData(XBee64BitAddress, XBee16BitAddress, byte[])}.
	 * 
	 * <p>Verify that the super com.digi.xbee.api.XBeeDevice#sendData(XBee64BitAddress, XBee16BitAddress, byte[]) method is 
	 * called when executing the com.digi.xbee.api.ZigBeeDevice#sendData(XBee64BitAddress, XBee16BitAddress, byte[]) method.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testSendData6416Success() throws TimeoutException, XBeeException {
		// Do nothing when sending and checking any TransmitPacket packet.
		Mockito.doNothing().when(zigbeeDevice).sendAndCheckXBeePacket(Mockito.any(TransmitPacket.class), Mockito.eq(false));
		
		zigbeeDevice.sendData(XBEE_64BIT_ADDRESS, XBEE_16BIT_ADDRESS, DATA.getBytes());
		
		// Verify the sendAndCheckXBeePacket(XBeePacket, boolean) method was called.
		Mockito.verify(zigbeeDevice, Mockito.times(1)).sendAndCheckXBeePacket(Mockito.eq(transmitPacket), Mockito.eq(false));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendDataAsync(XBee64BitAddress, XBee16BitAddress, byte[])}.
	 * 
	 * <p>Verify that the super com.digi.xbee.api.XBeeDevice#sendDataAsync(XBee64BitAddress, XBee16BitAddress, byte[]) method is 
	 * called when executing the com.digi.xbee.api.ZigBeeDevice#sendDataAsync(XBee64BitAddress, XBee16BitAddress, byte[]) method.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testSendDataAsync6416Success() throws TimeoutException, XBeeException {
		// Do nothing when sending and checking any TransmitPacket packet.
		Mockito.doNothing().when(zigbeeDevice).sendAndCheckXBeePacket(Mockito.any(TransmitPacket.class), Mockito.eq(true));
		
		zigbeeDevice.sendDataAsync(XBEE_64BIT_ADDRESS, XBEE_16BIT_ADDRESS, DATA.getBytes());
		
		// Verify the sendAndCheckXBeePacket(XBeePacket, boolean) method was called.
		Mockito.verify(zigbeeDevice, Mockito.times(1)).sendAndCheckXBeePacket(Mockito.eq(transmitPacket), Mockito.eq(true));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendExplicitData(RemoteXBeeDevice, int, int, int, int, byte[])}.
	 * 
	 * <p>Verify that the super com.digi.xbee.api.XBeeDevice#sendExplicitData(RemoteXBeeDevice, int, int, int, int, byte[]) method is 
	 * called when executing the com.digi.xbee.api.ZigBeeDevice#sendExplicitData(RemoteXBeeDevice, int, int, int, int, byte[]) method.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testSendExplicitDataRemoteDeviceSuccess() throws TimeoutException, XBeeException {
		// Do nothing when the sendExplicitData(XBee64BitAddress, XBee16BitAddress, int, int, int, int, byte[]) method is called.
		Mockito.doNothing().when(zigbeeDevice).sendExplicitData(Mockito.any(XBee64BitAddress.class), Mockito.any(XBee16BitAddress.class), Mockito.anyInt(), Mockito.anyInt(), 
				Mockito.anyInt(), Mockito.anyInt(), Mockito.any(byte[].class));
		
		zigbeeDevice.sendExplicitData(mockedRemoteZigBeeDevice, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
		
		// Verify the sendExplicitData(XBee64BitAddress, XBee16BitAddress, int, int, int, int, byte[]) method was called.
		Mockito.verify(zigbeeDevice, Mockito.times(1)).sendExplicitData(Mockito.eq(XBEE_64BIT_ADDRESS), Mockito.eq(XBEE_16BIT_ADDRESS), Mockito.eq(SOURCE_ENDPOINT), 
				Mockito.eq(DESTINATION_ENDPOINT), Mockito.eq(CLUSTER_ID), Mockito.eq(PROFILE_ID), Mockito.eq(DATA.getBytes()));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendExplicitData(XBee64BitAddress, XBee16BitAddress, int, int, int, int, byte[])}.
	 * 
	 * <p>Verify that the super com.digi.xbee.api.XBeeDevice#sendExplicitData(XBee64BitAddress, XBee16BitAddress, int, int, int, int, byte[]) method is 
	 * called when executing the com.digi.xbee.api.ZigBeeDevice#sendExplicitData(XBee64BitAddress, XBee16BitAddress, int, int, int, int, byte[]) method.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testSendExplicitData6416Success() throws TimeoutException, XBeeException {
		// Do nothing when sending and checking any TransmitPacket packet.
		Mockito.doNothing().when(zigbeeDevice).sendAndCheckXBeePacket(Mockito.any(ExplicitAddressingPacket.class), Mockito.eq(false));
		
		zigbeeDevice.sendExplicitData(XBEE_64BIT_ADDRESS, XBEE_16BIT_ADDRESS, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
		
		// Verify the sendAndCheckXBeePacket(XBeePacket, boolean) method was called.
		Mockito.verify(zigbeeDevice, Mockito.times(1)).sendAndCheckXBeePacket(Mockito.eq(explicitAddressingPacket), Mockito.eq(false));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendBroadcastExplicitData(int, int, int, int, byte[])}.
	 * 
	 * <p>Verify that the super com.digi.xbee.api.XBeeDevice#sendBroadcastExplicitData(int, int, int, int, byte[]) method is 
	 * called when executing the com.digi.xbee.api.ZigBeeDevice#sendBroadcastExplicitData(int, int, int, int, byte[]) method.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testSendBroadcastExplicitDataSuccess() throws TimeoutException, XBeeException {
		// Do nothing when the sendExplicitData(XBee64BitAddress, int, int, int, int, byte[]) method is called.
		Mockito.doNothing().when(zigbeeDevice).sendExplicitData(Mockito.any(XBee64BitAddress.class), Mockito.anyInt(), Mockito.anyInt(), 
				Mockito.anyInt(), Mockito.anyInt(), Mockito.any(byte[].class));
		
		zigbeeDevice.sendBroadcastExplicitData(SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
		
		// Verify the sendExplicitData(XBee64BitAddress, int, int, int, int, byte[]) method was called (64BitAddress must be the broadcast one).
		Mockito.verify(zigbeeDevice, Mockito.times(1)).sendExplicitData(Mockito.eq(XBee64BitAddress.BROADCAST_ADDRESS), Mockito.eq(SOURCE_ENDPOINT), 
				Mockito.eq(DESTINATION_ENDPOINT), Mockito.eq(CLUSTER_ID), Mockito.eq(PROFILE_ID), Mockito.eq(DATA.getBytes()));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendExplicitDataAsync(RemoteXBeeDevice, int, int, int, int, byte[])}.
	 * 
	 * <p>Verify that the super com.digi.xbee.api.XBeeDevice#sendExplicitDataAsync(RemoteXBeeDevice, int, int, int, int, byte[]) method is 
	 * called when executing the com.digi.xbee.api.ZigBeeDevice#sendExplicitDataAsync(RemoteXBeeDevice, int, int, int, int, byte[]) method.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testSendExplicitDataAsyncRemoteDeviceSuccess() throws TimeoutException, XBeeException {
		// Do nothing when the sendExplicitDataAsync(XBee64BitAddress, XBee16BitAddress, int, int, int, int, byte[]) method is called.
		Mockito.doNothing().when(zigbeeDevice).sendExplicitDataAsync(Mockito.any(XBee64BitAddress.class), Mockito.any(XBee16BitAddress.class), Mockito.anyInt(), Mockito.anyInt(), 
				Mockito.anyInt(), Mockito.anyInt(), Mockito.any(byte[].class));
		
		zigbeeDevice.sendExplicitDataAsync(mockedRemoteZigBeeDevice, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
		
		// Verify the sendExplicitDataAsync(XBee64BitAddress, XBee16BitAddress, int, int, int, int, byte[]) method was called.
		Mockito.verify(zigbeeDevice, Mockito.times(1)).sendExplicitDataAsync(Mockito.eq(XBEE_64BIT_ADDRESS), Mockito.eq(XBEE_16BIT_ADDRESS), Mockito.eq(SOURCE_ENDPOINT), 
				Mockito.eq(DESTINATION_ENDPOINT), Mockito.eq(CLUSTER_ID), Mockito.eq(PROFILE_ID), Mockito.eq(DATA.getBytes()));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ZigBeeDevice#sendExplicitDataAsync(XBee64BitAddress, XBee16BitAddress, int, int, int, int, byte[])}.
	 * 
	 * <p>Verify that the super com.digi.xbee.api.XBeeDevice#sendExplicitDataAsync(XBee64BitAddress, XBee16BitAddress, int, int, int, int, byte[]) method is 
	 * called when executing the com.digi.xbee.api.ZigBeeDevice#sendExplicitDataAsync(XBee64BitAddress, XBee16BitAddress, int, int, int, int, byte[]) method.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testSendExplicitDataAsync64Success() throws TimeoutException, XBeeException {
		// Do nothing when sending and checking any ExplicitAddressingPacket packet.
		Mockito.doNothing().when(zigbeeDevice).sendAndCheckXBeePacket(Mockito.any(ExplicitAddressingPacket.class), Mockito.eq(true));
		
		zigbeeDevice.sendExplicitDataAsync(XBEE_64BIT_ADDRESS, XBEE_16BIT_ADDRESS, SOURCE_ENDPOINT, DESTINATION_ENDPOINT, CLUSTER_ID, PROFILE_ID, DATA.getBytes());
		
		// Verify the sendAndCheckXBeePacket(XBeePacket, boolean) method was called.
		Mockito.verify(zigbeeDevice, Mockito.times(1)).sendAndCheckXBeePacket(Mockito.eq(explicitAddressingPacket), Mockito.eq(true));
	}
}

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

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.common.TransmitPacket;
import com.digi.xbee.api.packet.raw.TX64Packet;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBeeDevice.class})
public class SendBroadcastSerialDataAsyncTest {
	
	// Constants.
	private static final String SEND_DATA = "data";
	private static final byte[] SEND_DATA_BYTES = SEND_DATA.getBytes();
	
	private static final String SEND_XBEE_PACKET_ASYNC_METHOD = "sendXBeePacketAsync";
	
	// Variables.
	private SerialPortRxTx mockedPort;
	private XBeeDevice xbeeDevice;
	
	private TX64Packet tx64Packet;
	private TransmitPacket transmitPacket;
	
	@Before
	public void setup() throws Exception {
		// Mock an RxTx IConnectionInterface.
		mockedPort = Mockito.mock(SerialPortRxTx.class);
		// When checking if the connection is open, return true.
		Mockito.when(mockedPort.isOpen()).thenReturn(true);
		
		// Instantiate an XBeeDevice object with the mocked interface.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(mockedPort));
		// When checking the operating mode, return API.
		Mockito.when(xbeeDevice.getOperatingMode()).thenReturn(OperatingMode.API);
		
		// Mock Tx64 packet.
		tx64Packet = Mockito.mock(TX64Packet.class);
		
		// Mock Transmit Request packet.
		transmitPacket = Mockito.mock(TransmitPacket.class);
		
		// Whenever a TX64Packet class is instantiated, the mocked tx64Packet packet should be returned.
		PowerMockito.whenNew(TX64Packet.class).withAnyArguments().thenReturn(tx64Packet);
		
		// Whenever a TransmitPacket class is instantiated, the mocked transmitPacket packet should be returned.
		PowerMockito.whenNew(TransmitPacket.class).withAnyArguments().thenReturn(transmitPacket);
	}
	
	/**
	 * Verify that we receive a NullPointerException when the data to be sent is null.
	 * 
	 * @throws Exception
	 */
	@Test(expected=NullPointerException.class)
	public void testSendBroadcastSerialDataAsyncInvalidParams() throws Exception {
		// Try to send broadcast serial data with null data.
		xbeeDevice.sendBroadcastSerialDataAsync(null);	
	}
	
	/**
	 * Verify that broadcast serial data async. send fails when the connection is closed.
	 * 
	 * @throws Exception
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSendBroadcastSerialDataAsyncConnectionClosed() throws Exception {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Send broadcast serial data asynchronously.
		xbeeDevice.sendBroadcastSerialDataAsync(SEND_DATA_BYTES);
	}
	
	/**
	 * Verify that broadcast serial data is considered successfully sent when the {@code sendXBeePacket} 
	 * method does not throw any exception. In this test case the protocol of the XBee device is 802.15.4.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testSendBroadcastSerialDataAsync802Success() throws Exception {
		// Return that the protocol of the device is 802.15.4 when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		
		// Stub the sendPacketAsync method to do nothing when called.
		PowerMockito.doNothing().when(xbeeDevice, SEND_XBEE_PACKET_ASYNC_METHOD, Mockito.eq(tx64Packet));
		
		// Send broadcast serial data asynchronously.
		xbeeDevice.sendBroadcastSerialDataAsync(SEND_DATA_BYTES);
		
		// Verify the sendXBeePacketAsync method was called 1 time.
		PowerMockito.verifyPrivate(xbeeDevice, Mockito.times(1)).invoke(SEND_XBEE_PACKET_ASYNC_METHOD, (XBeeAPIPacket)Mockito.any());
	}
	
	/**
	 * Verify that broadcast serial data send fails when the operating mode is AT. In this test case the 
	 * protocol of the XBee device is 802.15.4.
	 * 
	 * @throws Exception
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSendBroadcastSerialData802AsyncInvalidOperatingMode() throws Exception {
		// Return that the operating mode of the device is AT when asked.
		Mockito.when(xbeeDevice.getOperatingMode()).thenReturn(OperatingMode.AT);
		// Return that the protocol of the device is 802.15.4 when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		
		// Send broadcast serial data.
		xbeeDevice.sendBroadcastSerialDataAsync(SEND_DATA_BYTES);
	}
	
	/**
	 * Verify that broadcast serial data send fails (XBee exception thrown) when the 
	 * {@code sendXBeePacketAsync} method throws an IO exception. In this test case the 
	 * protocol of the XBee device is 802.15.4.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testSendBroadcastSerialDataAsync802IOError() throws Exception {
		// Return that the protocol of the device is 802.15.4 when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		
		// Throw an IO exception when trying to send an XBee packet asynchronously.
		PowerMockito.doThrow(new IOException()).when(xbeeDevice, SEND_XBEE_PACKET_ASYNC_METHOD, Mockito.eq(tx64Packet));
		
		// Send broadcast serial data.
		try {
			xbeeDevice.sendBroadcastSerialDataAsync(SEND_DATA_BYTES);
			fail("Broadcast data shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(XBeeException.class, e.getClass());
			assertEquals(IOException.class, e.getCause().getClass());
		}
	}
	
	/**
	 * Verify that broadcast serial data is considered successfully sent when the {@code sendXBeePacket} 
	 * method does not throw any exception. In this test case the protocol of the XBee device is ZigBee 
	 * (other protocols but 802.15.4 behave the same way).
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testSendBroadcastSerialDataAsyncOtherProtocolsSuccess() throws Exception {
		// Return that the protocol of the device is ZigBee when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		// Stub the sendXBeePacketAsync method to do nothing when called.
		PowerMockito.doNothing().when(xbeeDevice, SEND_XBEE_PACKET_ASYNC_METHOD, Mockito.eq(transmitPacket));
		
		// Send broadcast serial data asynchronously.
		xbeeDevice.sendBroadcastSerialDataAsync(SEND_DATA_BYTES);
		
		// Verify the sendXBeePacketAsync method was called 1 time.
		PowerMockito.verifyPrivate(xbeeDevice, Mockito.times(1)).invoke(SEND_XBEE_PACKET_ASYNC_METHOD, (XBeeAPIPacket)Mockito.any());
	}
	
	/**
	 * Verify that broadcast serial data send fails when the operating mode is AT. In this test case 
	 * the protocol of the XBee device is ZigBee (other protocols but 802.15.4 behave the 
	 * same way).
	 * 
	 * @throws Exception
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSendBroadcastSerialDataAsyncOtherProtocolsInvalidOperatingMode() throws Exception {
		// Return that the operating mode of the device is AT when asked.
		Mockito.when(xbeeDevice.getOperatingMode()).thenReturn(OperatingMode.AT);
		// Return that the protocol of the device is ZigBee when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		// Send broadcast serial data.
		xbeeDevice.sendBroadcastSerialDataAsync(SEND_DATA_BYTES);
	}
	
	/**
	 * Verify that broadcast serial data send fails (XBee exception thrown) when the 
	 * {@code sendXBeePacketAsync}  method throws an IO exception. In this test case the protocol 
	 * of the XBee device is ZigBee (other protocols but 802.15.4 behave the same way).
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testSendBroadcastSerialDataAsyncOtherProtocolsIOError() throws Exception {
		// Return that the protocol of the device is ZigBee when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		// Throw an IO exception when trying to send an XBee packet asynchronously.
		PowerMockito.doThrow(new IOException()).when(xbeeDevice, SEND_XBEE_PACKET_ASYNC_METHOD, Mockito.eq(transmitPacket));
		
		// Send broadcast serial data.
		try {
			xbeeDevice.sendBroadcastSerialDataAsync(SEND_DATA_BYTES);
			fail("Broadcast data shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(XBeeException.class, e.getClass());
			assertEquals(IOException.class, e.getCause().getClass());
		}
	}
}
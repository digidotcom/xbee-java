/**
 * Copyright (c) 2014-2016 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api.models;

import static org.junit.Assert.*;

import java.util.ArrayList;

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

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket;
import com.digi.xbee.api.packet.common.ReceivePacket;
import com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket;
import com.digi.xbee.api.packet.raw.RX16IOPacket;
import com.digi.xbee.api.packet.raw.RX16Packet;
import com.digi.xbee.api.packet.raw.RX64IOPacket;
import com.digi.xbee.api.packet.raw.RX64Packet;

@RunWith(PowerMockRunner.class)
@PrepareForTest({System.class, XBeePacketsQueue.class})
public class XBeePacketsQueueTest {

	// Constants.
	private final static int MAX_LENGTH = XBeePacketsQueue.DEFAULT_MAX_LENGTH;
	
	private final static String ADDRESS_64_1 = "0123456789ABCDEF";
	private final static String ADDRESS_64_2 = "0123456701234567";
	private final static String ADDRESS_64_3 = "0123012301230123";
	private final static String ADDRESS_16_1 = "0123";
	private final static String ADDRESS_16_2 = "4567";
	private final static String METHOD_SLEEP = "sleep";
	private final static String METHOD_IS_DATA_PACKET = "isDataPacket";
	private final static String METHOD_IS_EXPLICIT_DATA_PACKET = "isExplicitDataPacket";
	private final static String METHOD_ADDRESSES_MATCH = "addressesMatch";
	
	// Variables.
	private long currentMillis = 0;
	
	private static XBee64BitAddress xbee64BitAddress1;
	private static XBee64BitAddress xbee64BitAddress2;
	private static XBee64BitAddress xbee64BitAddress3;
	
	private static XBee16BitAddress xbee16BitAddress1;
	private static XBee16BitAddress xbee16BitAddress2;
	
	private static ReceivePacket mockedReceivePacket;
	private static RemoteATCommandResponsePacket mockedRemoteATCommandPacket;
	private static RX64IOPacket mockedRxIO64Packet;
	private static RX16IOPacket mockedRxIO16Packet;
	private static RX64Packet mockedRx64Packet;
	private static RX16Packet mockedRx16Packet;
	private static ExplicitRxIndicatorPacket mockedExplicitRxIndicatorPacket;
	
	@BeforeClass
	public static void setupOnce() {
		// Create 3 64-bit addresses.
		xbee64BitAddress1 = new XBee64BitAddress(ADDRESS_64_1);
		xbee64BitAddress2 = new XBee64BitAddress(ADDRESS_64_2);
		xbee64BitAddress3 = new XBee64BitAddress(ADDRESS_64_3);
		
		// Create a 16-bit address.
		xbee16BitAddress1 = new XBee16BitAddress(ADDRESS_16_1);
		xbee16BitAddress2 = new XBee16BitAddress(ADDRESS_16_2);
		
		// Create some dummy packets.
		// ReceivePacket.
		mockedReceivePacket = Mockito.mock(ReceivePacket.class);
		Mockito.when(mockedReceivePacket.getFrameType()).thenReturn(APIFrameType.RECEIVE_PACKET);
		// RemoteATCommandResponsePacket.
		mockedRemoteATCommandPacket = Mockito.mock(RemoteATCommandResponsePacket.class);
		Mockito.when(mockedRemoteATCommandPacket.getFrameType()).thenReturn(APIFrameType.REMOTE_AT_COMMAND_RESPONSE);
		// RX64IOPacket.
		mockedRxIO64Packet = Mockito.mock(RX64IOPacket.class);
		Mockito.when(mockedRxIO64Packet.getFrameType()).thenReturn(APIFrameType.RX_IO_64);
		// RX16IOPacket.
		mockedRxIO16Packet = Mockito.mock(RX16IOPacket.class);
		Mockito.when(mockedRxIO16Packet.getFrameType()).thenReturn(APIFrameType.RX_IO_16);
		// RX64Packet.
		mockedRx64Packet = Mockito.mock(RX64Packet.class);
		Mockito.when(mockedRx64Packet.getFrameType()).thenReturn(APIFrameType.RX_64);
		// RX16Packet.
		mockedRx16Packet = Mockito.mock(RX16Packet.class);
		Mockito.when(mockedRx16Packet.getFrameType()).thenReturn(APIFrameType.RX_16);
		// ExplicitRxIndicatorPacket.
		mockedExplicitRxIndicatorPacket = Mockito.mock(ExplicitRxIndicatorPacket.class);
		Mockito.when(mockedExplicitRxIndicatorPacket.getFrameType()).thenReturn(APIFrameType.EXPLICIT_RX_INDICATOR);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#XBeePacketsQueue(int)}.
	 * 
	 * <p>Verify that the {@code XBeePacketsQueue} cannot be created if max length is negative.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateNegativeLength() {
		new XBeePacketsQueue(-1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#XBeePacketsQueue(int)}.
	 * 
	 * <p>Verify that the {@code XBeePacketsQueue} cannot be created if max length is 0.</p>
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateZeroLength() {
		new XBeePacketsQueue(0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#XBeePacketsQueue()}, 
	 * {@link com.digi.xbee.api.models.XBeePacketsQueue#getMaxSize()} and 
	 * {@link com.digi.xbee.api.models.XBeePacketsQueue#getCurrentSize()}.
	 * 
	 * <p>Verify that the {@code XBeePacketsQueue} can be created successfully.</p>
	 */
	@Test
	public void testCreateSuccess() {
		XBeePacketsQueue xbeePacketsQueue = new XBeePacketsQueue();
		
		assertEquals(MAX_LENGTH, xbeePacketsQueue.getMaxSize());
		assertEquals(0, xbeePacketsQueue.getCurrentSize());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#XBeePacketsQueue(int)}, 
	 * {@link com.digi.xbee.api.models.XBeePacketsQueue#getMaxSize()} and 
	 * {@link com.digi.xbee.api.models.XBeePacketsQueue#getCurrentSize()}.
	 * 
	 * <p>Verify that the {@code XBeePacketsQueue} can be created successfully using a custom length.</p>
	 */
	@Test
	public void testCreateCustomSizeSuccess() {
		XBeePacketsQueue xbeePacketsQueue = new XBeePacketsQueue(5);
		
		assertEquals(5, xbeePacketsQueue.getMaxSize());
		assertEquals(0, xbeePacketsQueue.getCurrentSize());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#addPacket(XBeePacket)} and 
	 * {@link com.digi.xbee.api.models.XBeePacketsQueue#clearQueue()}.
	 * 
	 * <p>Verify that the queue allows for adding packets and clearing the queue.</p>
	 */
	@Test
	public void testAddAndClear() {
		XBeePacketsQueue xbeePacketsQueue = new XBeePacketsQueue(5);
		
		// Add 3 packets to the queue.
		for (int i = 0; i < 3; i++)
			xbeePacketsQueue.addPacket(Mockito.mock(XBeePacket.class));
		
		// Verify the current size of the queue is 3.
		assertEquals(3, xbeePacketsQueue.getCurrentSize());
		
		// Verify the queue can be cleared.
		xbeePacketsQueue.clearQueue();
		assertEquals(0, xbeePacketsQueue.getCurrentSize());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#addPacket(XBeePacket)} and 
	 * {@link com.digi.xbee.api.models.XBeePacketsQueue#getFirstPacket(int)}.
	 * 
	 * <p>Verify that the circular buffer behavior works and the queue overrides old packets when 
	 * the limit has been reached and new packets are added.</p>
	 */
	@Test
	public void testCircularBuffer() {
		// Create a list of packets for the test.
		XBeeAPIPacket[] mockedPackets = new XBeeAPIPacket[10];
		for (int i = 0; i < mockedPackets.length; i++) {
			XBeeAPIPacket mockedPacket = Mockito.mock(XBeeAPIPacket.class);
			Mockito.when(mockedPacket.getFrameID()).thenReturn(i);
			mockedPackets[i] = mockedPacket;
		}
		
		// Create an XBeePacketsQueue of 5 slots.
		XBeePacketsQueue xbeePacketsQueue = new XBeePacketsQueue(5);
		
		// Fill the queue with the 5 first packets (0, 1, 2, 3, 4).
		int index = 0;
		int i = 0;
		for (i = 0; i < 5; i ++)
			xbeePacketsQueue.addPacket(mockedPackets[index + i]);
		index += i;
		
		// Get the first packet from the queue, verify it has 0 as frame ID.
		XBeeAPIPacket apiPacket = (XBeeAPIPacket)xbeePacketsQueue.getFirstPacket(0);
		assertEquals(0, apiPacket.getFrameID());
		
		// Add 2 more packets to the queue (5, 6).
		for (i = 0; i < 2; i ++)
			xbeePacketsQueue.addPacket(mockedPackets[index + i]);
		index += i;
		
		// Packet with frame ID 1 should have been overridden and we should receive packet 
		// with frame ID 2 when requested.
		apiPacket = (XBeeAPIPacket)xbeePacketsQueue.getFirstPacket(0);
		assertEquals(2, apiPacket.getFrameID());
		
		// Add the last 3 packets to the queue (7, 8, 9).
		for (i = 0; i < 3; i ++)
			xbeePacketsQueue.addPacket(mockedPackets[index + i]);
		index += i;
		
		// In this case packets with index 3 and 4 have been overridden and we should receive 
		// packet with frame ID 5 when requested.
		apiPacket = (XBeeAPIPacket)xbeePacketsQueue.getFirstPacket(0);
		assertEquals(5, apiPacket.getFrameID());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#addPacket(XBeePacket)} and 
	 * {@link com.digi.xbee.api.models.XBeePacketsQueue#getFirstPacketFrom(RemoteXBeeDevice, int)}.
	 * 
	 * <p>Verify that when requesting the first packet sent from a specific XBee device, the queue 
	 * returns the first packet from that device it finds or null if there is not any packet 
	 * sent by that device in the queue.</p>.
	 */
	@Test
	public void testGetFirstPacketFrom() {
		// Create a mocked remote XBee device.
		RemoteXBeeDevice mockedRemoteDevice = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(mockedRemoteDevice.get64BitAddress()).thenReturn(xbee64BitAddress1);
		// Create an XBeePacketsQueue of 5 slots.
		XBeePacketsQueue xbeePacketsQueue = new XBeePacketsQueue(5);
		
		// Add 2 dummy packets.
		for (int i = 0; i < 2; i ++)
			xbeePacketsQueue.addPacket(Mockito.mock(XBeePacket.class));
		
		// Add a packet from the mocked 64-bit address.
		Mockito.when(mockedRemoteATCommandPacket.get64bitSourceAddress()).thenReturn(xbee64BitAddress1);
		xbeePacketsQueue.addPacket(mockedRemoteATCommandPacket);
		
		// Add 2 additional packets from different senders.
		Mockito.when(mockedRxIO64Packet.get64bitSourceAddress()).thenReturn(xbee64BitAddress2);
		xbeePacketsQueue.addPacket(mockedRxIO64Packet);
		Mockito.when(mockedExplicitRxIndicatorPacket.get64BitSourceAddress()).thenReturn(xbee64BitAddress3);
		xbeePacketsQueue.addPacket(mockedExplicitRxIndicatorPacket);
		
		// Request the first packet from the queue sent by the mocked remote device and 
		// verify it is our 'xbeePacket'.
		assertEquals(mockedRemoteATCommandPacket, xbeePacketsQueue.getFirstPacketFrom(mockedRemoteDevice, 0));
		
		// Request another packet from the queue sent by the mocked remote device, 
		// verify it is null (there are no more packets sent by that device).
		assertNull(xbeePacketsQueue.getFirstPacketFrom(mockedRemoteDevice, 0));
		
		// Verify the queue length is 4.
		assertEquals(4, xbeePacketsQueue.getCurrentSize());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#addPacket(XBeePacket)} and 
	 * {@link com.digi.xbee.api.models.XBeePacketsQueue#getFirstDataPacket(int)}.
	 * 
	 * <p>Verify that when requesting the first data packet of the queue, it returns the first data 
	 * packet it finds or null if there is not any data packet in the queue.</p>
	 */
	@Test
	public void testGetFirstDataPacket() {
		// Create an XBeePacketsQueue of 5 slots.
		XBeePacketsQueue xbeePacketsQueue = new XBeePacketsQueue(5);
		
		// Add 2 dummy packets.
		for (int i = 0; i < 2; i ++)
			xbeePacketsQueue.addPacket(Mockito.mock(XBeePacket.class));
		
		// Add a data packet.
		xbeePacketsQueue.addPacket(mockedReceivePacket);
		
		// Add additional (non-data) packets.
		xbeePacketsQueue.addPacket(mockedRxIO64Packet);
		xbeePacketsQueue.addPacket(mockedExplicitRxIndicatorPacket);
		
		// Request the first data packet from the queue and verify it is our 'dataPacket'.
		assertEquals(mockedReceivePacket, xbeePacketsQueue.getFirstDataPacket(0));
		
		// Request another data packet from the queue, verify it is null (there are no more 
		// data packets in the list).
		assertNull(xbeePacketsQueue.getFirstDataPacket(0));
		
		// Verify the queue length is 4.
		assertEquals(4, xbeePacketsQueue.getCurrentSize());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#addPacket(XBeePacket)} and 
	 * {@link com.digi.xbee.api.models.XBeePacketsQueue#getFirstDataPacketFrom(RemoteXBeeDevice, int)}.
	 * 
	 * <p>Verify that when requesting the first data packet sent from a specific XBee device, the 
	 * queue returns the first data packet from that device it finds or null if there is not any data 
	 * packet sent by that device in the queue.</p>.
	 */
	@Test
	public void testGetFirstDataPacketFrom() {
		// Create a mocked remote XBee device.
		RemoteXBeeDevice mockedRemoteDevice = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(mockedRemoteDevice.get64BitAddress()).thenReturn(xbee64BitAddress1);
		// Create an XBeePacketsQueue of 5 slots.
		XBeePacketsQueue xbeePacketsQueue = new XBeePacketsQueue(5);
		
		// Add 2 dummy packets.
		for (int i = 0; i < 2; i ++)
			xbeePacketsQueue.addPacket(Mockito.mock(XBeePacket.class));
		
		// Add a data packet from the mocked 64-bit address.
		Mockito.when(mockedReceivePacket.get64bitSourceAddress()).thenReturn(xbee64BitAddress1);
		xbeePacketsQueue.addPacket(mockedReceivePacket);
		
		// Add additional (non-data) packets from the same sender.
		Mockito.when(mockedRxIO64Packet.get64bitSourceAddress()).thenReturn(xbee64BitAddress1);
		xbeePacketsQueue.addPacket(mockedRxIO64Packet);
		Mockito.when(mockedExplicitRxIndicatorPacket.get64BitSourceAddress()).thenReturn(xbee64BitAddress1);
		xbeePacketsQueue.addPacket(mockedExplicitRxIndicatorPacket);
		
		// Request the first data packet from the queue sent by the mocked remote device and 
		// verify it is our 'dataPacket'.
		assertEquals(mockedReceivePacket, xbeePacketsQueue.getFirstDataPacketFrom(mockedRemoteDevice, 0));
		
		// Request another data packet from the queue sent by the mocked remote device, 
		// verify it is null (there are no more data packets sent by that device).
		assertNull(xbeePacketsQueue.getFirstDataPacketFrom(mockedRemoteDevice, 0));
		
		// Verify the queue length is 4.
		assertEquals(4, xbeePacketsQueue.getCurrentSize());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#addPacket(XBeePacket)} and 
	 * {@link com.digi.xbee.api.models.XBeePacketsQueue#getFirstExplicitDataPacket(int)}.
	 * 
	 * <p>Verify that when requesting the first explicit data packet of the queue, it returns the first 
	 * explicit data packet it finds or null if there is not any explicit data packet in the queue.</p>
	 */
	@Test
	public void testGetFirstExplicitDataPacket() {
		// Create an XBeePacketsQueue of 5 slots.
		XBeePacketsQueue xbeePacketsQueue = new XBeePacketsQueue(5);
		
		// Add 2 dummy packets.
		for (int i = 0; i < 2; i ++)
			xbeePacketsQueue.addPacket(Mockito.mock(XBeePacket.class));
		
		// Add an explicit data packet.
		xbeePacketsQueue.addPacket(mockedExplicitRxIndicatorPacket);
		
		// Add a data packet.
		xbeePacketsQueue.addPacket(mockedReceivePacket);
		
		// Add a dummy packet again.
		xbeePacketsQueue.addPacket(Mockito.mock(XBeePacket.class));
		
		// Request the first explicit data packet from the queue and verify it is our explicit data packet.
		assertEquals(mockedExplicitRxIndicatorPacket, xbeePacketsQueue.getFirstExplicitDataPacket(0));
		
		// Request another explicit data packet from the queue, verify it is null (there are no more 
		// explicit data packets in the list).
		assertNull(xbeePacketsQueue.getFirstExplicitDataPacket(0));
		
		// Verify the queue length is 4.
		assertEquals(4, xbeePacketsQueue.getCurrentSize());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#addPacket(XBeePacket)} and 
	 * {@link com.digi.xbee.api.models.XBeePacketsQueue#getFirstExpicitDataPacketFrom(RemoteXBeeDevice, int)}.
	 * 
	 * <p>Verify that when requesting the first explicit data packet sent from a specific XBee device, the 
	 * queue returns the first explicit data packet from that device it finds or null if there is not any 
	 * explicit data packet sent by that device in the queue.</p>.
	 */
	@Test
	public void testGetFirstExplicitDataPacketFrom() {
		// Create a mocked remote XBee device.
		RemoteXBeeDevice mockedRemoteDevice = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(mockedRemoteDevice.get64BitAddress()).thenReturn(xbee64BitAddress1);
		// Create an XBeePacketsQueue of 5 slots.
		XBeePacketsQueue xbeePacketsQueue = new XBeePacketsQueue(5);
		
		// Add 2 dummy packets.
		for (int i = 0; i < 2; i ++)
			xbeePacketsQueue.addPacket(Mockito.mock(XBeePacket.class));
		
		// Add an explicit data packet from the mocked 64-bit address.
		Mockito.when(mockedExplicitRxIndicatorPacket.get64BitSourceAddress()).thenReturn(xbee64BitAddress1);
		xbeePacketsQueue.addPacket(mockedExplicitRxIndicatorPacket);
		
		// Add a data packet from the mocked 64-bit address.
		Mockito.when(mockedReceivePacket.get64bitSourceAddress()).thenReturn(xbee64BitAddress1);
		xbeePacketsQueue.addPacket(mockedReceivePacket);
		
		// Add a packet again.
		xbeePacketsQueue.addPacket(Mockito.mock(XBeePacket.class));
		
		// Request the first explicit data packet from the queue sent by the mocked remote device and 
		// verify it is our explicit data Packet.
		assertEquals(mockedExplicitRxIndicatorPacket, xbeePacketsQueue.getFirstExplicitDataPacketFrom(mockedRemoteDevice, 0));
		
		// Request another explicit data packet from the queue sent by the mocked remote device, 
		// verify it is null (there are no more explicit data packets sent by that device).
		assertNull(xbeePacketsQueue.getFirstExplicitDataPacketFrom(mockedRemoteDevice, 0));
		
		// Verify the queue length is 4.
		assertEquals(4, xbeePacketsQueue.getCurrentSize());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#getFirstPacket(int)}.
	 * 
	 * <p>Verify that when requesting the first packet of the queue with a timeout greater than 
	 * 0 and the queue is empty, the timeout elapses and a null packet is received.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testGetFirstPacketTimeout() throws Exception {
		// Create an XBeePacketsQueue of 5 slots but don't fill it.
		XBeePacketsQueue xbeePacketsQueue = PowerMockito.spy(new XBeePacketsQueue(5));
		
		// Get the current time.
		currentMillis = System.currentTimeMillis();
		
		// Prepare the System class to return our fixed currentMillis variable when requested.
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.currentTimeMillis()).thenReturn(currentMillis);
		
		// When the sleep method is called, add 100ms to the currentMillis variable.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Object[] args = invocation.getArguments();
				int sleepTime = (Integer)args[0];
				changeMillisToReturn(sleepTime);
				return null;
			}
		}).when(xbeePacketsQueue, METHOD_SLEEP, Mockito.anyInt());
		
		// Request the first packet with 5s of timeout.
		XBeePacket xbeePacket = xbeePacketsQueue.getFirstPacket(5000);
		
		// Verify that the sleep method was called 50 times (50 * 100ms = 5s) and the packet 
		// retrieved is null.
		PowerMockito.verifyPrivate(xbeePacketsQueue, Mockito.times(50)).invoke(METHOD_SLEEP, 100);
		assertNull(xbeePacket);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#getFirstPacketFrom(RemoteXBeeDevice, int)}.
	 * 
	 * <p>Verify that when requesting the first packet of the queue sent by a specific remote 
	 * XBee Device with a timeout greater than 0 and the queue does not have any packet sent by 
	 * that device, the timeout elapses and a null packet is received.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testGetFirstPacketFromTimeout() throws Exception {
		// Create a mocked remote XBee device.
		RemoteXBeeDevice mockedRemoteDevice = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(mockedRemoteDevice.get64BitAddress()).thenReturn(xbee64BitAddress1);
		
		// Configure 2 frames with the other 2 mocked addresses.
		Mockito.when(mockedRemoteATCommandPacket.get64bitSourceAddress()).thenReturn(xbee64BitAddress2);
		Mockito.when(mockedReceivePacket.get64bitSourceAddress()).thenReturn(xbee64BitAddress3);
		
		// Create an XBeePacketsQueue of 5 slots.
		XBeePacketsQueue xbeePacketsQueue = PowerMockito.spy(new XBeePacketsQueue(5));
		
		// Fill the queue with some packets.
		xbeePacketsQueue.addPacket(Mockito.mock(XBeePacket.class));
		xbeePacketsQueue.addPacket(mockedRemoteATCommandPacket);
		xbeePacketsQueue.addPacket(Mockito.mock(XBeePacket.class));
		xbeePacketsQueue.addPacket(mockedReceivePacket);
		
		// Get the current time.
		currentMillis = System.currentTimeMillis();
		
		// Prepare the System class to return our fixed currentMillis variable when requested.
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.currentTimeMillis()).thenReturn(currentMillis);
		
		// When the sleep method is called, add 100ms to the currentMillis variable.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Object[] args = invocation.getArguments();
				int sleepTime = (Integer)args[0];
				changeMillisToReturn(sleepTime);
				return null;
			}
		}).when(xbeePacketsQueue, METHOD_SLEEP, Mockito.anyInt());
		
		// Request the first packet from our remote XBee device with 5s of timeout.
		XBeePacket xbeePacket = xbeePacketsQueue.getFirstPacketFrom(mockedRemoteDevice, 5000);
		
		// Verify that the sleep method was called 50 times (50 * 100ms = 5s) and the packet 
		// retrieved is null (there was not any packet from our remote XBee device in the queue).
		PowerMockito.verifyPrivate(xbeePacketsQueue, Mockito.times(50)).invoke(METHOD_SLEEP, 100);
		assertNull(xbeePacket);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#getFirstDataPacket(int)}.
	 * 
	 * <p>Verify that when requesting the first data packet of the queue with a timeout greater than 
	 * 0 and the queue does not have any data packet, the timeout elapses and a null data packet is 
	 * received.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testGetFirstDataPacketTimeout() throws Exception {
		// Create an XBeePacketsQueue of 5 slots.
		XBeePacketsQueue xbeePacketsQueue = PowerMockito.spy(new XBeePacketsQueue(5));
		
		// Add some dummy packets (non data packets).
		for (int i = 0; i < 3; i ++)
			xbeePacketsQueue.addPacket(Mockito.mock(XBeePacket.class));
		
		xbeePacketsQueue.addPacket(mockedExplicitRxIndicatorPacket);
		xbeePacketsQueue.addPacket(mockedRxIO64Packet);
		
		// Get the current time.
		currentMillis = System.currentTimeMillis();
		
		// Prepare the System class to return our fixed currentMillis variable when requested.
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.currentTimeMillis()).thenReturn(currentMillis);
		
		// When the sleep method is called, add 100ms to the currentMillis variable.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Object[] args = invocation.getArguments();
				int sleepTime = (Integer)args[0];
				changeMillisToReturn(sleepTime);
				return null;
			}
		}).when(xbeePacketsQueue, METHOD_SLEEP, Mockito.anyInt());
		
		// Request the first data packet with 5s of timeout.
		XBeePacket xbeePacket = xbeePacketsQueue.getFirstDataPacket(5000);
		
		// Verify that the sleep method was called 50 times (50 * 100ms = 5s) and the data 
		// packet retrieved is null (there was not any data packet in the queue).
		PowerMockito.verifyPrivate(xbeePacketsQueue, Mockito.times(50)).invoke(METHOD_SLEEP, 100);
		assertNull(xbeePacket);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#getFirstDataPacketFrom(RemoteXBeeDevice, int)}.
	 * 
	 * <p>Verify that when requesting the first data packet of the queue sent by a specific remote 
	 * XBee Device with a timeout greater than 0 and the queue does not have any data packet sent by 
	 * that device, the timeout elapses and a null data packet is received.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testGetFirstDataPacketFromTimeout() throws Exception {
		// Create a mocked remote XBee device.
		RemoteXBeeDevice mockedRemoteDevice = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(mockedRemoteDevice.get64BitAddress()).thenReturn(xbee64BitAddress1);
		
		// Configure 2 frames with the other 2 mocked addresses.
		Mockito.when(mockedRemoteATCommandPacket.get64bitSourceAddress()).thenReturn(xbee64BitAddress2);
		Mockito.when(mockedReceivePacket.get64bitSourceAddress()).thenReturn(xbee64BitAddress3);
		
		// Create an XBeePacketsQueue of 5 slots.
		XBeePacketsQueue xbeePacketsQueue = PowerMockito.spy(new XBeePacketsQueue(5));
		
		// Fill the queue with some packets.
		xbeePacketsQueue.addPacket(Mockito.mock(XBeePacket.class));
		xbeePacketsQueue.addPacket(mockedRemoteATCommandPacket);
		xbeePacketsQueue.addPacket(Mockito.mock(XBeePacket.class));
		xbeePacketsQueue.addPacket(mockedReceivePacket);
		
		// Get the current time.
		currentMillis = System.currentTimeMillis();
		
		// Prepare the System class to return our fixed currentMillis variable when requested.
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.currentTimeMillis()).thenReturn(currentMillis);
		
		// When the sleep method is called, add 100ms to the currentMillis variable.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Object[] args = invocation.getArguments();
				int sleepTime = (Integer)args[0];
				changeMillisToReturn(sleepTime);
				return null;
			}
		}).when(xbeePacketsQueue, METHOD_SLEEP, Mockito.anyInt());
		
		// Request the first data packet from our remote XBee device with 5s of timeout.
		XBeePacket xbeePacket = xbeePacketsQueue.getFirstDataPacketFrom(mockedRemoteDevice, 5000);
		
		// Verify that the sleep method was called 50 times (50 * 100ms = 5s) and the data packet 
		// retrieved is null (there was not any data packet from our remote XBee device in the queue).
		PowerMockito.verifyPrivate(xbeePacketsQueue, Mockito.times(50)).invoke(METHOD_SLEEP, 100);
		assertNull(xbeePacket);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#getFirstExplicitDataPacket(int)}.
	 * 
	 * <p>Verify that when requesting the first explicit data packet of the queue with a timeout greater than 
	 * 0 and the queue does not have any explicit data packet, the timeout elapses and a null explicit data 
	 * packet is received.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testGetFirstExplicitDataPacketTimeout() throws Exception {
		// Create an XBeePacketsQueue of 5 slots.
		XBeePacketsQueue xbeePacketsQueue = PowerMockito.spy(new XBeePacketsQueue(5));
		
		// Add some dummy packets (non explicit data packets).
		for (int i = 0; i < 3; i ++)
			xbeePacketsQueue.addPacket(Mockito.mock(XBeePacket.class));
		
		xbeePacketsQueue.addPacket(mockedReceivePacket);
		xbeePacketsQueue.addPacket(mockedRx64Packet);
		
		// Get the current time.
		currentMillis = System.currentTimeMillis();
		
		// Prepare the System class to return our fixed currentMillis variable when requested.
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.currentTimeMillis()).thenReturn(currentMillis);
		
		// When the sleep method is called, add 100ms to the currentMillis variable.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Object[] args = invocation.getArguments();
				int sleepTime = (Integer)args[0];
				changeMillisToReturn(sleepTime);
				return null;
			}
		}).when(xbeePacketsQueue, METHOD_SLEEP, Mockito.anyInt());
		
		// Request the first explicit data packet with 5s of timeout.
		XBeePacket xbeePacket = xbeePacketsQueue.getFirstExplicitDataPacket(5000);
		
		// Verify that the sleep method was called 50 times (50 * 100ms = 5s) and the explicit data 
		// packet retrieved is null (there was not any explicit data packet in the queue).
		PowerMockito.verifyPrivate(xbeePacketsQueue, Mockito.times(50)).invoke(METHOD_SLEEP, 100);
		assertNull(xbeePacket);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#getFirstExplicitDataPacketFrom(RemoteXBeeDevice, int)}.
	 * 
	 * <p>Verify that when requesting the first explicit data packet of the queue sent by a specific 
	 * remote XBee Device with a timeout greater than 0 and the queue does not have any explicit data 
	 * packet sent by that device, the timeout elapses and a null explicit data packet is received.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testGetFirstExplicitDataPacketFromTimeout() throws Exception {
		// Create a mocked remote XBee device.
		RemoteXBeeDevice mockedRemoteDevice = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(mockedRemoteDevice.get64BitAddress()).thenReturn(xbee64BitAddress1);
		
		// Configure 2 frames with the other 2 mocked addresses.
		Mockito.when(mockedRemoteATCommandPacket.get64bitSourceAddress()).thenReturn(xbee64BitAddress2);
		Mockito.when(mockedReceivePacket.get64bitSourceAddress()).thenReturn(xbee64BitAddress3);
		
		// Configure a non-explicit data packet with the address of the remote device.
		Mockito.when(mockedRx64Packet.get64bitSourceAddress()).thenReturn(xbee64BitAddress1);
		
		// Create an XBeePacketsQueue of 5 slots.
		XBeePacketsQueue xbeePacketsQueue = PowerMockito.spy(new XBeePacketsQueue(5));
		
		// Fill the queue with some packets.
		xbeePacketsQueue.addPacket(Mockito.mock(XBeePacket.class));
		xbeePacketsQueue.addPacket(mockedRemoteATCommandPacket);
		xbeePacketsQueue.addPacket(Mockito.mock(XBeePacket.class));
		xbeePacketsQueue.addPacket(mockedReceivePacket);
		xbeePacketsQueue.addPacket(mockedRx64Packet);
		
		// Get the current time.
		currentMillis = System.currentTimeMillis();
		
		// Prepare the System class to return our fixed currentMillis variable when requested.
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.currentTimeMillis()).thenReturn(currentMillis);
		
		// When the sleep method is called, add 100ms to the currentMillis variable.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Object[] args = invocation.getArguments();
				int sleepTime = (Integer)args[0];
				changeMillisToReturn(sleepTime);
				return null;
			}
		}).when(xbeePacketsQueue, METHOD_SLEEP, Mockito.anyInt());
		
		// Request the first explicit data packet from our remote XBee device with 5s of timeout.
		XBeePacket xbeePacket = xbeePacketsQueue.getFirstExplicitDataPacketFrom(mockedRemoteDevice, 5000);
		
		// Verify that the sleep method was called 50 times (50 * 100ms = 5s) and the explicit data packet 
		// retrieved is null (there was not any explicit data packet from our remote XBee device in the queue).
		PowerMockito.verifyPrivate(xbeePacketsQueue, Mockito.times(50)).invoke(METHOD_SLEEP, 100);
		assertNull(xbeePacket);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#isDataPacket(XBeePacket)}.
	 * 
	 * <p>Verify that the {@code isDataPacket} method of the {@code XBeePacketsQueue} class works 
	 * successfully for data packets.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testIsDataPacketTrue() throws Exception {
		ArrayList<XBeePacket> dataPackets = new ArrayList<XBeePacket>();
		
		// Fill the list of data packets.
		dataPackets.add(mockedReceivePacket);
		dataPackets.add(mockedRx64Packet);
		dataPackets.add(mockedRx16Packet);
		
		// Create an XBeePacketsQueue.
		XBeePacketsQueue xbeePacketsQueue = PowerMockito.spy(new XBeePacketsQueue());
		
		// Verify that packets contained in the data packets list are actually data packets.
		for (XBeePacket packet:dataPackets)
			assertTrue((Boolean)Whitebox.invokeMethod(xbeePacketsQueue, METHOD_IS_DATA_PACKET, packet));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#isDataPacket(XBeePacket)}.
	 * 
	 * <p>Verify that the {@code isDataPacket} method of the {@code XBeePacketsQueue} class works 
	 * successfully for non-data packets.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testIsDataPacketFalse() throws Exception {
		ArrayList<XBeePacket> noDataPackets = new ArrayList<XBeePacket>();
		
		// Fill the list of no-data packets.
		noDataPackets.add(mockedRemoteATCommandPacket);
		noDataPackets.add(mockedRxIO64Packet);
		noDataPackets.add(mockedRxIO16Packet);
		
		// Create an XBeePacketsQueue.
		XBeePacketsQueue xbeePacketsQueue = PowerMockito.spy(new XBeePacketsQueue());
		
		// Verify that packets contained in the non-data packets list are actually non-data packets.
		for (XBeePacket packet:noDataPackets)
			assertFalse((Boolean)Whitebox.invokeMethod(xbeePacketsQueue, METHOD_IS_DATA_PACKET, packet));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#isExplicitDataPacket(XBeePacket)}.
	 * 
	 * <p>Verify that the {@code isExplicitDataPacket} method of the {@code XBeePacketsQueue} class works 
	 * successfully for explicit data packets.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testIsExplicitDataPacketTrue() throws Exception {
		ArrayList<XBeePacket> explicitDataPackets = new ArrayList<XBeePacket>();
		
		// Add an explicit data packet to the list.
		explicitDataPackets.add(mockedExplicitRxIndicatorPacket);
		
		// Create an XBeePacketsQueue.
		XBeePacketsQueue xbeePacketsQueue = PowerMockito.spy(new XBeePacketsQueue());
		
		// Verify that packets contained in the explicit data packets list are actually explicit data packets.
		for (XBeePacket packet:explicitDataPackets)
			assertTrue((Boolean)Whitebox.invokeMethod(xbeePacketsQueue, METHOD_IS_EXPLICIT_DATA_PACKET, packet));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#isExplicitDataPacket(XBeePacket)}.
	 * 
	 * <p>Verify that the {@code isExplicitDataPacket} method of the {@code XBeePacketsQueue} class works 
	 * successfully for non-explicit data packets.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testIsExplicitDataPacketFalse() throws Exception {
		ArrayList<XBeePacket> noExplicitDataPackets = new ArrayList<XBeePacket>();
		
		// Fill the list of no-explicit data packets.
		noExplicitDataPackets.add(mockedRemoteATCommandPacket);
		noExplicitDataPackets.add(mockedRxIO64Packet);
		noExplicitDataPackets.add(mockedRxIO16Packet);
		noExplicitDataPackets.add(mockedReceivePacket);
		noExplicitDataPackets.add(mockedRx64Packet);
		noExplicitDataPackets.add(mockedRx16Packet);
		
		// Create an XBeePacketsQueue.
		XBeePacketsQueue xbeePacketsQueue = PowerMockito.spy(new XBeePacketsQueue());
		
		// Verify that packets contained in the non-explicit data packets list are actually non-explicit data packets.
		for (XBeePacket packet:noExplicitDataPackets)
			assertFalse((Boolean)Whitebox.invokeMethod(xbeePacketsQueue, METHOD_IS_EXPLICIT_DATA_PACKET, packet));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#addressesMatch(XBeePacket, RemoteXBeeDevice)}.
	 * 
	 * <p>Verify that the {@code isDataPacket} method of the {@code XBeePacketsQueue} class works 
	 * successfully for no API packets.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testAddressesMatchNoAPIPacket() throws Exception {
		// Create a mocked remote XBee device.
		RemoteXBeeDevice mockedRemoteDevice = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(mockedRemoteDevice.get64BitAddress()).thenReturn(xbee64BitAddress1);
		
		// Create an XBeePacketsQueue.
		XBeePacketsQueue xbeePacketsQueue = PowerMockito.spy(new XBeePacketsQueue());
		
		// Verify that the 'addressesMatch' method returns false for no API packets.
		assertFalse((Boolean)Whitebox.invokeMethod(xbeePacketsQueue, METHOD_ADDRESSES_MATCH, Mockito.mock(XBeePacket.class), mockedRemoteDevice));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#addressesMatch(XBeePacket, RemoteXBeeDevice)}.
	 * 
	 * <p>Verify that the {@code addressesMatch} method of the {@code XBeePacketsQueue} class works 
	 * successfully for packets with 64-bit address.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testAddressesMatch64BitAddress() throws Exception {
		ArrayList<XBeePacket> api64Packets = new ArrayList<XBeePacket>();
		
		// Create a mocked remote XBee device.
		RemoteXBeeDevice mockedRemoteDevice = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(mockedRemoteDevice.get64BitAddress()).thenReturn(xbee64BitAddress1);
		
		// Fill the list of API packets.
		api64Packets.add(mockedReceivePacket);
		api64Packets.add(mockedRemoteATCommandPacket);
		api64Packets.add(mockedRx64Packet);
		api64Packets.add(mockedRxIO64Packet);
		
		// Create an XBeePacketsQueue.
		XBeePacketsQueue xbeePacketsQueue = PowerMockito.spy(new XBeePacketsQueue());
		
		// Verify the addresses match.
		Mockito.when(mockedReceivePacket.get64bitSourceAddress()).thenReturn(xbee64BitAddress1);
		Mockito.when(mockedRemoteATCommandPacket.get64bitSourceAddress()).thenReturn(xbee64BitAddress1);
		Mockito.when(mockedRxIO64Packet.get64bitSourceAddress()).thenReturn(xbee64BitAddress1);
		Mockito.when(mockedRx64Packet.get64bitSourceAddress()).thenReturn(xbee64BitAddress1);
		for (XBeePacket packet:api64Packets)
			assertTrue((Boolean)Whitebox.invokeMethod(xbeePacketsQueue, METHOD_ADDRESSES_MATCH, packet, mockedRemoteDevice));
		
		// Verify the addresses don't match.
		Mockito.when(mockedReceivePacket.get64bitSourceAddress()).thenReturn(xbee64BitAddress2);
		Mockito.when(mockedRemoteATCommandPacket.get64bitSourceAddress()).thenReturn(xbee64BitAddress2);
		Mockito.when(mockedRxIO64Packet.get64bitSourceAddress()).thenReturn(xbee64BitAddress2);
		Mockito.when(mockedRx64Packet.get64bitSourceAddress()).thenReturn(xbee64BitAddress2);
		for (XBeePacket packet:api64Packets)
			assertFalse((Boolean)Whitebox.invokeMethod(xbeePacketsQueue, METHOD_ADDRESSES_MATCH, packet, mockedRemoteDevice));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeePacketsQueue#addressesMatch(XBeePacket, RemoteXBeeDevice)}.
	 * 
	 * <p>Verify that the {@code addressesMatch} method of the {@code XBeePacketsQueue} class works 
	 * successfully for packets with 16-bit address.</p>
	 */
	@Test
	public void testAddressesMatch16BitAddress() throws Exception {
		ArrayList<XBeePacket> api16Packets = new ArrayList<XBeePacket>();
		
		// Create a mocked remote XBee device.
		RemoteXBeeDevice mockedRemoteDevice = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(mockedRemoteDevice.get16BitAddress()).thenReturn(xbee16BitAddress1);
		
		// Fill the list of API packets.
		api16Packets.add(mockedReceivePacket);
		api16Packets.add(mockedRemoteATCommandPacket);
		api16Packets.add(mockedRx16Packet);
		api16Packets.add(mockedRxIO16Packet);
		
		// Create an XBeePacketsQueue.
		XBeePacketsQueue xbeePacketsQueue = PowerMockito.spy(new XBeePacketsQueue());
		
		// Verify the addresses match.
		Mockito.when(mockedReceivePacket.get16bitSourceAddress()).thenReturn(xbee16BitAddress1);
		Mockito.when(mockedRemoteATCommandPacket.get16bitSourceAddress()).thenReturn(xbee16BitAddress1);
		Mockito.when(mockedRxIO16Packet.get16bitSourceAddress()).thenReturn(xbee16BitAddress1);
		Mockito.when(mockedRx16Packet.get16bitSourceAddress()).thenReturn(xbee16BitAddress1);
		for (XBeePacket packet:api16Packets)
			assertTrue((Boolean)Whitebox.invokeMethod(xbeePacketsQueue, METHOD_ADDRESSES_MATCH, packet, mockedRemoteDevice));
		
		// Verify the addresses don't match.
		Mockito.when(mockedReceivePacket.get16bitSourceAddress()).thenReturn(xbee16BitAddress2);
		Mockito.when(mockedRemoteATCommandPacket.get16bitSourceAddress()).thenReturn(xbee16BitAddress2);
		Mockito.when(mockedRxIO16Packet.get16bitSourceAddress()).thenReturn(xbee16BitAddress2);
		Mockito.when(mockedRx16Packet.get16bitSourceAddress()).thenReturn(xbee16BitAddress2);
		for (XBeePacket packet:api16Packets)
			assertFalse((Boolean)Whitebox.invokeMethod(xbeePacketsQueue, METHOD_ADDRESSES_MATCH, packet, mockedRemoteDevice));
	}
	
	/**
	 * Helper method that changes the milliseconds to return when the System.currentMillis() 
	 * method is invoked.
	 * 
	 * @param time The time to all to the milliseconds to return.
	 */
	public void changeMillisToReturn(int time) {
		currentMillis += time;
		
		// Prepare the System class to return our fixed currentMillis variable when requested.
		PowerMockito.when(System.currentTimeMillis()).thenReturn(currentMillis);
	}
}

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
package com.digi.xbee.api.listeners;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.digi.xbee.api.connection.DataReader;
import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.packet.common.ReceivePacket;
import com.digi.xbee.api.packet.raw.RX16Packet;
import com.digi.xbee.api.packet.raw.RX64Packet;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DataReader.class})
public class ISerialDataReceiveListenerTest {
	
	// Constants.
	private static final XBee16BitAddress XBEE_16BIT_ADDRESS = new XBee16BitAddress("0123");
	private static final XBee64BitAddress XBEE_64BIT_ADDRESS = new XBee64BitAddress("0123456789ABCDEF");
	
	private static final String RECEIVED_DATA = "data";
	private static final byte[] RECEIVED_DATA_BYTES = RECEIVED_DATA.getBytes();
	
	private static final String PACKET_RECEIVED_METHOD = "packetReceived";
	private static final String NOTIFY_SERIAL_DATA_RECEIVED_METHOD = "notifySerialDataReceived";
	
	private static final int RECEIVE_OPTIONS_UNICAST = 1;
	private static final int RECEIVE_OPTIONS_BROADCAST = 2;
	private static final int RECEIVE_OPTIONS_BROADCAST_PAN = 3;
	
	private static final boolean IS_BROADCAST_DATA = true;
	private static final boolean IS_NOT_BROADCAST_DATA = false;
	
	// Variables.
	private MyReceiveListener receiveSerialDataListener;
	
	private RX16Packet rx16Packet;
	private RX64Packet rx64Packet;
	private ReceivePacket receivePacket;
	private ATCommandResponsePacket invalidPacket;
	
	private DataReader dataReader;
	
	@Before
	public void setup() throws Exception {
		// Serial data receive listener.
		receiveSerialDataListener = PowerMockito.spy(new MyReceiveListener());
		
		// Data reader.
		dataReader = PowerMockito.spy(new DataReader(Mockito.mock(IConnectionInterface.class), OperatingMode.UNKNOWN));
		// Stub the 'notifySerialDataReceived' method of the dataReader instance so it directly notifies the 
		// listeners instead of opening a new thread per listener (which is what the real method does). This avoids us 
		// having to wait for the executor to run the threads.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				String address = (String)args[0];
				byte[] data = (byte[])args[1];
				boolean isBroadcastData = (Boolean)args[2];
				notifySerialReceivedListeners(address, data, isBroadcastData);
				return null;
			}
		}).when(dataReader, NOTIFY_SERIAL_DATA_RECEIVED_METHOD, Mockito.anyString(), Mockito.anyObject(), Mockito.anyBoolean());
		
		// Mock Rx16 Packet.
		rx16Packet = Mockito.mock(RX16Packet.class);
		Mockito.when(rx16Packet.getFrameType()).thenReturn(APIFrameType.RX_16);
		Mockito.when(rx16Packet.getReceivedData()).thenReturn(RECEIVED_DATA_BYTES);
		Mockito.when(rx16Packet.getSourceAddress()).thenReturn(XBEE_16BIT_ADDRESS);
		Mockito.when(rx16Packet.getReceiveOptions()).thenReturn(RECEIVE_OPTIONS_UNICAST);
		
		// Mock Rx64 Packet.
		rx64Packet = Mockito.mock(RX64Packet.class);
		Mockito.when(rx64Packet.getFrameType()).thenReturn(APIFrameType.RX_64);
		Mockito.when(rx64Packet.getReceivedData()).thenReturn(RECEIVED_DATA_BYTES);
		Mockito.when(rx64Packet.getSourceAddress()).thenReturn(XBEE_64BIT_ADDRESS);
		Mockito.when(rx64Packet.getReceiveOptions()).thenReturn(RECEIVE_OPTIONS_UNICAST);
		
		// Mock Receive Packet.
		receivePacket = Mockito.mock(ReceivePacket.class);
		Mockito.when(receivePacket.getFrameType()).thenReturn(APIFrameType.RECEIVE_PACKET);
		Mockito.when(receivePacket.getReceivedData()).thenReturn(RECEIVED_DATA_BYTES);
		Mockito.when(receivePacket.get64bitAddress()).thenReturn(XBEE_64BIT_ADDRESS);
		Mockito.when(receivePacket.getReceiveOptions()).thenReturn(RECEIVE_OPTIONS_UNICAST);
		
		// Mock an invalid packet.
		invalidPacket = Mockito.mock(ATCommandResponsePacket.class);
	}
	
	/**
	 * Verify that the unicast callback of the ISerialDataReceive interface is executed correctly.
	 */
	@Test
	public void testUnicastSerialDataReceiveEvent() {
		receiveSerialDataListener.serialDataReceived(XBEE_64BIT_ADDRESS.toString(), RECEIVED_DATA_BYTES);
		
		assertEquals(XBEE_64BIT_ADDRESS.toString(), receiveSerialDataListener.getAddress());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveSerialDataListener.getSerialData());
	}
	
	/**
	 * Verify that the broadcast callback of the ISerialDataReceive interface is executed correctly.
	 */
	@Test
	public void testBroadcastSerialDataReceiveEvent() {
		receiveSerialDataListener.broadcastSerialDataReceived(XBEE_64BIT_ADDRESS.toString(), RECEIVED_DATA_BYTES);
		
		assertEquals(XBEE_64BIT_ADDRESS.toString(), receiveSerialDataListener.getAddress());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveSerialDataListener.getSerialData());
	}
	
	/**
	 * Verify that if the listener is not subscribed to receive serial data, the callback is not 
	 * executed although a unicast serial data packet is received.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUnicastSerialDataReceiveNotSubscribed() throws Exception {
		// Fire the private packetReceived method of the dataReader with a RX64Packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rx16Packet);
		
		// Verify that the notifySerialDataReceived private method was called with the correct address and data.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_SERIAL_DATA_RECEIVED_METHOD, 
				XBEE_16BIT_ADDRESS.toString(), RECEIVED_DATA_BYTES, IS_NOT_BROADCAST_DATA);
		
		// As the receiveSerialDataListener was not subscribed in the serialDataReceiveListeners of the dataReader object, the 
		// address and serial data of the receiveSerialDataListener should be null.
		assertNull(receiveSerialDataListener.getAddress());
		assertNull(receiveSerialDataListener.getSerialData());
	}
	
	/**
	 * Verify that if the listener is not subscribed to receive serial data, the callback is not 
	 * executed although a broadcast serial data packet is received.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBroadcastSerialDataReceiveNotSubscribed() throws Exception {
		// Configure the rx16 packet to return broadcast receive options.
		Mockito.when(rx16Packet.getReceiveOptions()).thenReturn(RECEIVE_OPTIONS_BROADCAST);
		// Fire the private packetReceived method of the dataReader with a RX64Packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rx16Packet);
		
		// Verify that the notifySerialDataReceived private method was called with the correct address and data.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_SERIAL_DATA_RECEIVED_METHOD, 
				XBEE_16BIT_ADDRESS.toString(), RECEIVED_DATA_BYTES, IS_BROADCAST_DATA);
		
		// As the receiveSerialDataListener was not subscribed in the serialDataReceiveListeners of the dataReader object, the 
		// address and serial data of the receiveSerialDataListener should be null.
		assertNull(receiveSerialDataListener.getAddress());
		assertNull(receiveSerialDataListener.getSerialData());
	}
	
	/**
	 * Verify that, when subscribed to receive serial data and a unicast Rx16 packet is received, the unicast 
	 * callback of the listener is executed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUnicastSerialDataReceiveSubscribedRx16() throws Exception {
		// Subscribe to listen for serial data.
		dataReader.addSerialDatatReceiveListener(receiveSerialDataListener);
		
		// Fire the private packetReceived method of the dataReader with a RX16Packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rx16Packet);
		
		// Verify that the notifySerialDataReceived private method was called with the correct address and data.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_SERIAL_DATA_RECEIVED_METHOD, 
				XBEE_16BIT_ADDRESS.toString(), RECEIVED_DATA_BYTES, IS_NOT_BROADCAST_DATA);
		
		// Verify that the serialDataReceived method of the listener was executed one time.
		Mockito.verify(receiveSerialDataListener, Mockito.times(1)).serialDataReceived(XBEE_16BIT_ADDRESS.toString(), 
				RECEIVED_DATA_BYTES);
		
		assertEquals(XBEE_16BIT_ADDRESS.toString(), receiveSerialDataListener.getAddress());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveSerialDataListener.getSerialData());
	}
	
	/**
	 * Verify that, when subscribed to receive serial data and a broadcast Rx16 packet is received, the broadcast 
	 * callback of the listener is executed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBroadcastSerialDataReceiveSubscribedRx16() throws Exception {
		// Configure the rx16 packet to return broadcast receive options.
		Mockito.when(rx16Packet.getReceiveOptions()).thenReturn(RECEIVE_OPTIONS_BROADCAST);
		
		// Subscribe to listen for serial data.
		dataReader.addSerialDatatReceiveListener(receiveSerialDataListener);
		
		// Fire the private packetReceived method of the dataReader with a RX16Packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rx16Packet);
		
		// Verify that the notifySerialDataReceived private method was called with the correct address and data.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_SERIAL_DATA_RECEIVED_METHOD, 
				XBEE_16BIT_ADDRESS.toString(), RECEIVED_DATA_BYTES, IS_BROADCAST_DATA);
		
		// Verify that the broadcastSerialDataReceived method of the listener was executed one time.
		Mockito.verify(receiveSerialDataListener, Mockito.times(1)).broadcastSerialDataReceived(XBEE_16BIT_ADDRESS.toString(), 
				RECEIVED_DATA_BYTES);
		
		assertEquals(XBEE_16BIT_ADDRESS.toString(), receiveSerialDataListener.getAddress());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveSerialDataListener.getSerialData());
	}
	
	/**
	 * Verify that, when subscribed to receive serial data and a PAN broadcast Rx16 packet is received, the broadcast 
	 * callback of the listener is executed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPANBroadcastSerialDataReceiveSubscribedRx16() throws Exception {
		// Configure the rx16 packet to return broadcast receive options.
		Mockito.when(rx16Packet.getReceiveOptions()).thenReturn(RECEIVE_OPTIONS_BROADCAST_PAN);
		
		// Subscribe to listen for serial data.
		dataReader.addSerialDatatReceiveListener(receiveSerialDataListener);
		
		// Fire the private packetReceived method of the dataReader with a RX16Packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rx16Packet);
		
		// Verify that the notifySerialDataReceived private method was called with the correct address and data.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_SERIAL_DATA_RECEIVED_METHOD, 
				XBEE_16BIT_ADDRESS.toString(), RECEIVED_DATA_BYTES, IS_BROADCAST_DATA);
		
		// Verify that the broadcastSerialDataReceived method of the listener was executed one time.
		Mockito.verify(receiveSerialDataListener, Mockito.times(1)).broadcastSerialDataReceived(XBEE_16BIT_ADDRESS.toString(), 
				RECEIVED_DATA_BYTES);
		
		assertEquals(XBEE_16BIT_ADDRESS.toString(), receiveSerialDataListener.getAddress());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveSerialDataListener.getSerialData());
	}
	
	/**
	 * Verify that, when subscribed to receive serial data and a unicast Rx64 packet is received, the unicast 
	 * callback of the listener is executed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUnicastSerialDataReceiveSubscribedRx64() throws Exception {
		// Subscribe to listen for serial data.
		dataReader.addSerialDatatReceiveListener(receiveSerialDataListener);
		
		// Fire the private packetReceived method of the dataReader with a RX64Packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rx64Packet);
		
		// Verify that the notifySerialDataReceived private method was called with the correct address and data.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_SERIAL_DATA_RECEIVED_METHOD, 
				XBEE_64BIT_ADDRESS.toString(), RECEIVED_DATA_BYTES, IS_NOT_BROADCAST_DATA);
		
		// Verify that the serialDataReceived method of the listener was executed one time.
		Mockito.verify(receiveSerialDataListener, Mockito.times(1)).serialDataReceived(XBEE_64BIT_ADDRESS.toString(), 
				RECEIVED_DATA_BYTES);
		
		assertEquals(XBEE_64BIT_ADDRESS.toString(), receiveSerialDataListener.getAddress());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveSerialDataListener.getSerialData());
	}
	
	/**
	 * Verify that, when subscribed to receive serial data and a broadcast Rx64 packet is received, the broadcast 
	 * callback of the listener is executed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBroadcastSerialDataReceiveSubscribedRx64() throws Exception {
		// Configure the rx64 packet to return broadcast receive options.
		Mockito.when(rx64Packet.getReceiveOptions()).thenReturn(RECEIVE_OPTIONS_BROADCAST);
		
		// Subscribe to listen for serial data.
		dataReader.addSerialDatatReceiveListener(receiveSerialDataListener);
		
		// Fire the private packetReceived method of the dataReader with a RX64Packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rx64Packet);
		
		// Verify that the notifySerialDataReceived private method was called with the correct address and data.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_SERIAL_DATA_RECEIVED_METHOD, 
				XBEE_64BIT_ADDRESS.toString(), RECEIVED_DATA_BYTES, IS_BROADCAST_DATA);
		
		// Verify that the broadcastSerialDataReceived method of the listener was executed one time.
		Mockito.verify(receiveSerialDataListener, Mockito.times(1)).broadcastSerialDataReceived(XBEE_64BIT_ADDRESS.toString(), 
				RECEIVED_DATA_BYTES);
		
		assertEquals(XBEE_64BIT_ADDRESS.toString(), receiveSerialDataListener.getAddress());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveSerialDataListener.getSerialData());
	}
	
	/**
	 * Verify that, when subscribed to receive serial data and a PAN broadcast Rx64 packet is received, the broadcast 
	 * callback of the listener is executed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPANBroadcastSerialDataReceiveSubscribedRx64() throws Exception {
		// Configure the rx64 packet to return broadcast receive options.
		Mockito.when(rx64Packet.getReceiveOptions()).thenReturn(RECEIVE_OPTIONS_BROADCAST_PAN);
		
		// Subscribe to listen for serial data.
		dataReader.addSerialDatatReceiveListener(receiveSerialDataListener);
		
		// Fire the private packetReceived method of the dataReader with a RX64Packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rx64Packet);
		
		// Verify that the notifySerialDataReceived private method was called with the correct address and data.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_SERIAL_DATA_RECEIVED_METHOD, 
				XBEE_64BIT_ADDRESS.toString(), RECEIVED_DATA_BYTES, IS_BROADCAST_DATA);
		
		// Verify that the broadcastSerialDataReceived method of the listener was executed one time.
		Mockito.verify(receiveSerialDataListener, Mockito.times(1)).broadcastSerialDataReceived(XBEE_64BIT_ADDRESS.toString(), 
				RECEIVED_DATA_BYTES);
		
		assertEquals(XBEE_64BIT_ADDRESS.toString(), receiveSerialDataListener.getAddress());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveSerialDataListener.getSerialData());
	}
	
	/**
	 * Verify that, when subscribed to receive serial data and a unicast Receive packet is received, the unicast 
	 * callback of the listener is executed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUnicastSerialDataReceiveSubscribedReceive() throws Exception {
		// Subscribe to listen for serial data.
		dataReader.addSerialDatatReceiveListener(receiveSerialDataListener);
		
		// Fire the private packetReceived method of the dataReader with a ReceivePacket.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, receivePacket);
		
		// Verify that the notifySerialDataReceived private method was called with the correct address and data.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_SERIAL_DATA_RECEIVED_METHOD, 
				XBEE_64BIT_ADDRESS.toString(), RECEIVED_DATA_BYTES, IS_NOT_BROADCAST_DATA);
		
		// Verify that the serialDataReceived method of the listener was executed one time.
		Mockito.verify(receiveSerialDataListener, Mockito.times(1)).serialDataReceived(XBEE_64BIT_ADDRESS.toString(), 
				RECEIVED_DATA_BYTES);
		
		assertEquals(XBEE_64BIT_ADDRESS.toString(), receiveSerialDataListener.getAddress());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveSerialDataListener.getSerialData());
	}
	
	/**
	 * Verify that, when subscribed to receive serial data and a broadcast Receive packet is received, the 
	 * broadcast callback of the listener is executed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBroadcastSerialDataReceiveSubscribedReceive() throws Exception {
		// Configure the receive packet to return broadcast receive options.
		Mockito.when(receivePacket.getReceiveOptions()).thenReturn(RECEIVE_OPTIONS_BROADCAST);
				
		// Subscribe to listen for serial data.
		dataReader.addSerialDatatReceiveListener(receiveSerialDataListener);
		
		// Fire the private packetReceived method of the dataReader with a ReceivePacket.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, receivePacket);
		
		// Verify that the notifySerialDataReceived private method was called with the correct address and data.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_SERIAL_DATA_RECEIVED_METHOD, 
				XBEE_64BIT_ADDRESS.toString(), RECEIVED_DATA_BYTES, IS_BROADCAST_DATA);
		
		// Verify that the broadcastSerialDataReceived method of the listener was executed one time.
		Mockito.verify(receiveSerialDataListener, Mockito.times(1)).broadcastSerialDataReceived(XBEE_64BIT_ADDRESS.toString(), 
				RECEIVED_DATA_BYTES);
		
		assertEquals(XBEE_64BIT_ADDRESS.toString(), receiveSerialDataListener.getAddress());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveSerialDataListener.getSerialData());
	}
	
	/**
	 * Verify that, when subscribed to receive serial data and a packet that does not correspond to serial 
	 * data, none of the callbacks of the listener are executed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSerialDataReceiveSubscribedInvalid() throws Exception {
		// Subscribe to listen for serial data.
		dataReader.addSerialDatatReceiveListener(receiveSerialDataListener);
		
		// Fire the private packetReceived method of the dataReader with an invalid packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, invalidPacket);
		
		// Verify that the notifySerialDataReceived private method was not called.
		PowerMockito.verifyPrivate(dataReader, Mockito.never()).invoke(NOTIFY_SERIAL_DATA_RECEIVED_METHOD, 
				Mockito.anyString(), Mockito.anyObject(), Mockito.anyBoolean());
		
		// Verify that none of the listener callbacks are executed
		Mockito.verify(receiveSerialDataListener, Mockito.never()).serialDataReceived(Mockito.anyString(), (byte[])Mockito.any());
		Mockito.verify(receiveSerialDataListener, Mockito.never()).broadcastSerialDataReceived(Mockito.anyString(), (byte[])Mockito.any());
		
		assertNull(receiveSerialDataListener.getAddress());
		assertNull(receiveSerialDataListener.getSerialData());
	}
	
	
	/**
	 * This method directly notifies the ISerialDataReceiveListeners of the dataReader instance that new 
	 * serial data has been received. This method intends to replace the original 'notifySerialDataReceived' 
	 * located within the dataReader object because it generates a thread for each notify process.
	 * 
	 * @param address The address of the node that sent the data.
	 * @param data The serial data received.
	 * @param isBroadcastData Indicates whether or not the data was sent via broadcast.
	 */
	private void notifySerialReceivedListeners(String address, byte[] data, boolean isBroadcastData) {
		@SuppressWarnings("unchecked")
		ArrayList<ISerialDataReceiveListener> serialDataReceiveListeners = (ArrayList<ISerialDataReceiveListener>)Whitebox.getInternalState(dataReader, "serialDataReceiveListeners");
		for (ISerialDataReceiveListener listener:serialDataReceiveListeners) {
			if (isBroadcastData)
				listener.broadcastSerialDataReceived(address, data);
			else
				listener.serialDataReceived(address, data);
		}
	}
	
	/**
	 * Helper class to test the ISerialDataReceiveListener.
	 */
	private class MyReceiveListener implements ISerialDataReceiveListener {
		
		// Variables.
		private byte[] data = null;
		private String address = null;
		
		/*
		 * (non-Javadoc)
		 * @see com.digi.xbee.api.listeners.ISerialDataReceiveListener#serialDataReceived(java.lang.String, byte[])
		 */
		public void serialDataReceived(String address, byte[] data) {
			this.address = address;
			this.data = data;
		}
		
		/*
		 * (non-Javadoc)
		 * @see com.digi.xbee.api.listeners.ISerialDataReceiveListener#bradcastSerialDataReceived(java.lang.String, byte[])
		 */
		public void broadcastSerialDataReceived(String address, byte[] data) {
			this.address = address;
			this.data = data;
		}
		
		/**
		 * Retrieves the serial data received.
		 * 
		 * @return The serial data.
		 */
		public byte[] getSerialData() {
			return data;
		}
		
		/**
		 * Retrieves the source address of the node that sent the data.
		 * 
		 * @return The remote address.
		 */
		public String getAddress() {
			return address;
		}
	}
}

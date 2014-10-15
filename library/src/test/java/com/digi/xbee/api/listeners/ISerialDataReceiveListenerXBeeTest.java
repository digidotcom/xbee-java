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
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.connection.DataReader;
import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.packet.common.ReceivePacket;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DataReader.class})
public class ISerialDataReceiveListenerXBeeTest {
	
	// Constants.
	private static final XBee64BitAddress XBEE_64BIT_ADDRESS = new XBee64BitAddress("0123456789ABCDEF");
	
	private static final String RECEIVED_DATA = "data";
	private static final byte[] RECEIVED_DATA_BYTES = RECEIVED_DATA.getBytes();
	
	private static final String PACKET_RECEIVED_METHOD = "packetReceived";
	private static final String NOTIFY_SERIAL_DATA_RECEIVED_METHOD = "notifySerialDataReceived";
	
	// Variables.
	private MyReceiveListener receiveSerialDataListener;
	
	private static ReceivePacket receivePacket;
	private static ATCommandResponsePacket invalidPacket;
	
	private static RemoteXBeeDevice remoteXBeeDevice;
	
	private DataReader dataReader;
	
	@BeforeClass
	public static void setupOnce() {
		// Mock Receive Packet.
		receivePacket = Mockito.mock(ReceivePacket.class);
		Mockito.when(receivePacket.getFrameType()).thenReturn(APIFrameType.RECEIVE_PACKET);
		Mockito.when(receivePacket.getRFData()).thenReturn(RECEIVED_DATA_BYTES);
		Mockito.when(receivePacket.get64bitSourceAddress()).thenReturn(XBEE_64BIT_ADDRESS);
		Mockito.when(receivePacket.isBroadcast()).thenReturn(false);
		
		// Mock an invalid packet.
		invalidPacket = Mockito.mock(ATCommandResponsePacket.class);
		
		// Mock a remote XBee device.
		remoteXBeeDevice = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(remoteXBeeDevice.get64BitAddress()).thenReturn(XBEE_64BIT_ADDRESS);
	}
	
	@Before
	public void setup() throws Exception {
		// Serial data receive listener.
		receiveSerialDataListener = PowerMockito.spy(new MyReceiveListener());
		
		// Data reader.
		dataReader = PowerMockito.spy(new DataReader(Mockito.mock(IConnectionInterface.class), OperatingMode.UNKNOWN, Mockito.mock(XBeeDevice.class)));
		// Stub the 'notifySerialDataReceived' method of the dataReader instance so it directly notifies the 
		// listeners instead of opening a new thread per listener (which is what the real method does). This avoids us 
		// having to wait for the executor to run the threads.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				XBeeMessage xbeeMessage = (XBeeMessage)args[0];
				notifySerialReceivedListeners(xbeeMessage);
				return null;
			}
		}).when(dataReader, NOTIFY_SERIAL_DATA_RECEIVED_METHOD, Mockito.any(XBeeMessage.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.ISerialDataReceiveListener#serialDataReceived(XBeeMessage)}.
	 * 
	 * <p>Verify that the serial data received callback of the ISerialDataReceive interface is executed 
	 * correctly when a unicast XBeeMessage (originated by a receive frame) is received.</p>
	 */
	@Test
	public void testUnicastSerialDataReceiveEvent() {
		// This is the message that should have been created if a receive frame would have been received.
		XBeeMessage xbeeMessage = new XBeeMessage(remoteXBeeDevice, RECEIVED_DATA_BYTES, false);
		
		receiveSerialDataListener.serialDataReceived(xbeeMessage);
		
		assertEquals(XBEE_64BIT_ADDRESS, receiveSerialDataListener.get64BitAddress());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveSerialDataListener.getSerialData());
		assertFalse(receiveSerialDataListener.isBroadcast());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.ISerialDataReceiveListener#serialDataReceived(XBeeMessage)}.
	 * 
	 * <p>Verify that the serial data received callback of the ISerialDataReceive interface is executed 
	 * correctly when a broadcast XBeeMessage (originated by a receive frame) is received.</p>
	 */
	@Test
	public void testBroadcastSerialDataReceiveEvent() {
		// This is the message that should have been created if a receive frame would have been received.
		XBeeMessage xbeeMessage = new XBeeMessage(remoteXBeeDevice, RECEIVED_DATA_BYTES, true);
		
		receiveSerialDataListener.serialDataReceived(xbeeMessage);
		
		assertEquals(XBEE_64BIT_ADDRESS, receiveSerialDataListener.get64BitAddress());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveSerialDataListener.getSerialData());
		assertTrue(receiveSerialDataListener.isBroadcast());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.ISerialDataReceiveListener#serialDataReceived(XBeeMessage)} and
	 * {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that if the listener is not subscribed to receive serial data, the callback is not 
	 * executed although a serial data packet is received.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSerialDataReceiveNotSubscribed() throws Exception {
		// Whenever a new remote XBee device needs to be instantiated, return the mocked one.
		PowerMockito.whenNew(RemoteXBeeDevice.class).withAnyArguments().thenReturn(remoteXBeeDevice);
		
		// Fire the private packetReceived method of the dataReader with a receive packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, receivePacket);
		
		// Verify that the notifySerialDataReceived private method was called.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_SERIAL_DATA_RECEIVED_METHOD, 
				Mockito.any(XBeeMessage.class));
		
		// As the receiveSerialDataListener was not subscribed in the serialDataReceiveListeners of the dataReader object, the 
		// address and serial data of the receiveSerialDataListener should be null.
		assertNull(receiveSerialDataListener.get64BitAddress());
		assertNull(receiveSerialDataListener.getSerialData());
		assertFalse(receiveSerialDataListener.isBroadcast());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.ISerialDataReceiveListener#serialDataReceived(XBeeMessage)} and
	 * {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that, when subscribed to receive serial data and a Receive packet is received, the 
	 * callback of the listener is executed.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSerialDataReceiveSubscribedReceive() throws Exception {
		// Whenever a new remote XBee device needs to be instantiated, return the mocked one.
		PowerMockito.whenNew(RemoteXBeeDevice.class).withAnyArguments().thenReturn(remoteXBeeDevice);
		
		// Subscribe to listen for serial data.
		dataReader.addSerialDatatReceiveListener(receiveSerialDataListener);
		
		// Fire the private packetReceived method of the dataReader with a ReceivePacket.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, receivePacket);
		
		// Verify that the notifySerialDataReceived private method was called.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_SERIAL_DATA_RECEIVED_METHOD, 
				Mockito.any(XBeeMessage.class));
		
		// Verify that the serialDataReceived method of the listener was executed one time.
		Mockito.verify(receiveSerialDataListener, Mockito.times(1)).serialDataReceived(Mockito.any(XBeeMessage.class));
		
		// All the parameters of our listener should be correct.
		assertEquals(XBEE_64BIT_ADDRESS, receiveSerialDataListener.get64BitAddress());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveSerialDataListener.getSerialData());
		assertFalse(receiveSerialDataListener.isBroadcast());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.ISerialDataReceiveListener#serialDataReceived(XBeeMessage)} and
	 * {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that, when subscribed to receive serial data and a packet that does not correspond to serial 
	 * data, the callback of the listener is not executed.</p>
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
				Mockito.any(XBeeMessage.class));
		
		// Verify that the callback of the listener was not executed
		Mockito.verify(receiveSerialDataListener, Mockito.never()).serialDataReceived(Mockito.any(XBeeMessage.class));
		
		// All the parameters of our listener should be empty.
		assertNull(receiveSerialDataListener.get64BitAddress());
		assertNull(receiveSerialDataListener.getSerialData());
		assertFalse(receiveSerialDataListener.isBroadcast());
	}
	
	
	/**
	 * This method directly notifies the ISerialDataReceiveListeners of the dataReader instance that new 
	 * serial data has been received. This method intends to replace the original 'notifySerialDataReceived' 
	 * located within the dataReader object because it generates a thread for each notify process.
	 * 
	 * @param xbeeMessage The XBeeMessage containing the address of the node that sent the data, the data 
	 *                    and a flag indicating if the data was sent via broadcast.
	 */
	private void notifySerialReceivedListeners(XBeeMessage xbeeMessage) {
		@SuppressWarnings("unchecked")
		ArrayList<ISerialDataReceiveListener> serialDataReceiveListeners = (ArrayList<ISerialDataReceiveListener>)Whitebox.getInternalState(dataReader, "serialDataReceiveListeners");
		for (ISerialDataReceiveListener listener:serialDataReceiveListeners)
			listener.serialDataReceived(xbeeMessage);
	}
	
	/**
	 * Helper class to test the ISerialDataReceiveListener.
	 */
	private class MyReceiveListener implements ISerialDataReceiveListener {
		
		// Variables.
		private byte[] data = null;
		private XBee64BitAddress address64 = null;
		private boolean isBroadcast = false;
		
		/*
		 * (non-Javadoc)
		 * @see com.digi.xbee.api.listeners.ISerialDataReceiveListener#serialDataReceived(java.lang.String, byte[])
		 */
		public void serialDataReceived(XBeeMessage xbeeMessage) {
			this.address64 = xbeeMessage.getDevice().get64BitAddress();
			this.data = xbeeMessage.getData();
			this.isBroadcast = xbeeMessage.isBroadcast();
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
		 * Retrieves the 64-bit source address of the node that sent the data.
		 * 
		 * @return The remote 64-bit address.
		 */
		public XBee64BitAddress get64BitAddress() {
			return address64;
		}
		
		/**
		 * Retrieves whether or not the data was sent via broadcast.
		 * 
		 * @return True if the data was sent via broadcast, false otherwise.
		 */
		public boolean isBroadcast() {
			return isBroadcast;
		}
	}
}

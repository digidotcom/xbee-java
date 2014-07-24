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
		receiveSerialDataListener = new MyReceiveListener();
		
		// Data reader.
		dataReader = PowerMockito.spy(new DataReader(Mockito.mock(IConnectionInterface.class), OperatingMode.UNKNOWN));
		// Stub the 'notifySerialDataReceived' method of the dataReader instance so it directly notifies the 
		// listeners instead of opening a new thread per listener (which is what the real method does).
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				String address = (String)args[0];
				byte[] data = (byte[])args[1];
				notifySerialReceivedListeners(address, data);
				return null;
			}
		}).when(dataReader, NOTIFY_SERIAL_DATA_RECEIVED_METHOD, Mockito.anyString(), Mockito.anyObject());
		
		// Mock Rx16 Packet.
		rx16Packet = Mockito.mock(RX16Packet.class);
		Mockito.when(rx16Packet.getFrameType()).thenReturn(APIFrameType.RX_16);
		Mockito.when(rx16Packet.getReceivedData()).thenReturn(RECEIVED_DATA_BYTES);
		Mockito.when(rx16Packet.getSourceAddress()).thenReturn(XBEE_16BIT_ADDRESS);
		
		// Mock Rx64 Packet.
		rx64Packet = Mockito.mock(RX64Packet.class);
		Mockito.when(rx64Packet.getFrameType()).thenReturn(APIFrameType.RX_64);
		Mockito.when(rx64Packet.getReceivedData()).thenReturn(RECEIVED_DATA_BYTES);
		Mockito.when(rx64Packet.getSourceAddress()).thenReturn(XBEE_64BIT_ADDRESS);
		
		// Mock Receive Packet.
		receivePacket = Mockito.mock(ReceivePacket.class);
		Mockito.when(receivePacket.getFrameType()).thenReturn(APIFrameType.RECEIVE_PACKET);
		Mockito.when(receivePacket.getReceivedData()).thenReturn(RECEIVED_DATA_BYTES);
		Mockito.when(receivePacket.get64bitAddress()).thenReturn(XBEE_64BIT_ADDRESS);
		
		// Mock an invalid packet.
		invalidPacket = Mockito.mock(ATCommandResponsePacket.class);
	}
	
	@Test
	/**
	 * Verify that the callback of the ISerialDataReceive interface is executed correctly.
	 */
	public void testSerialDataReceiveEvent() {
		receiveSerialDataListener.serialDataReceived(XBEE_64BIT_ADDRESS.toString(), RECEIVED_DATA_BYTES);
		
		assertEquals(receiveSerialDataListener.getAddress(), XBEE_64BIT_ADDRESS.toString());
		assertEquals(receiveSerialDataListener.getSerialData(), RECEIVED_DATA_BYTES);
	}
	
	@Test
	/**
	 * Verify that if the listener is not subscribed to receive serial data, the callback is not 
	 * executed although a serial data packet is received.
	 * @throws Exception
	 */
	public void testSerialDataReceiveNotSubscribed() throws Exception {
		// Fire the private packetReceived method of the dataReader with a RX64Packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rx16Packet);
		
		// Verify that the notifySerialDataReceived private method was called with the correct address and data.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_SERIAL_DATA_RECEIVED_METHOD, XBEE_16BIT_ADDRESS.toString(), RECEIVED_DATA_BYTES);
		
		// As the receiveSerialDataListener was not subscribed in the serialDataReceiveListeners of the dataReader object, the 
		// address and serial data of the receiveSerialDataListener should be null.
		assertNull(receiveSerialDataListener.getAddress());
		assertNull(receiveSerialDataListener.getSerialData());
	}
	
	@Test
	/**
	 * Verify that, when subscribed to receive serial data and a Rx16 packet is received, the callback 
	 * of the listener is executed.
	 * @throws Exception
	 */
	public void testSerialDataReceiveSubscribedRx16() throws Exception {
		dataReader.addSerialDatatReceiveListener(receiveSerialDataListener);
		
		// Fire the private packetReceived method of the dataReader with a RX16Packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rx16Packet);
		
		// Verify that the notifySerialDataReceived private method was called with the correct address and data.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_SERIAL_DATA_RECEIVED_METHOD, XBEE_16BIT_ADDRESS.toString(), RECEIVED_DATA_BYTES);
		
		assertEquals(receiveSerialDataListener.getAddress(), XBEE_16BIT_ADDRESS.toString());
		assertEquals(receiveSerialDataListener.getSerialData(), RECEIVED_DATA_BYTES);
	}
	
	@Test
	/**
	 * Verify that, when subscribed to receive serial data and a Rx64 packet is received, the callback 
	 * of the listener is executed.
	 * @throws Exception
	 */
	public void testSerialDataReceiveSubscribedRx64() throws Exception {
		dataReader.addSerialDatatReceiveListener(receiveSerialDataListener);
		
		// Fire the private packetReceived method of the dataReader with a RX64Packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rx64Packet);
		
		// Verify that the notifySerialDataReceived private method was called with the correct address and data.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_SERIAL_DATA_RECEIVED_METHOD, XBEE_64BIT_ADDRESS.toString(), RECEIVED_DATA_BYTES);
		
		assertEquals(receiveSerialDataListener.getAddress(), XBEE_64BIT_ADDRESS.toString());
		assertEquals(receiveSerialDataListener.getSerialData(), RECEIVED_DATA_BYTES);
	}
	
	@Test
	/**
	 * Verify that, when subscribed to receive serial data and a Receive packet is received, the callback 
	 * of the listener is executed.
	 * @throws Exception
	 */
	public void testSerialDataReceiveSubscribedReceive() throws Exception {
		dataReader.addSerialDatatReceiveListener(receiveSerialDataListener);
		
		// Fire the private packetReceived method of the dataReader with a ReceivePacket.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, receivePacket);
		
		// Verify that the notifySerialDataReceived private method was called with the correct address and data.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_SERIAL_DATA_RECEIVED_METHOD, XBEE_64BIT_ADDRESS.toString(), RECEIVED_DATA_BYTES);
		
		assertEquals(receiveSerialDataListener.getAddress(), XBEE_64BIT_ADDRESS.toString());
		assertEquals(receiveSerialDataListener.getSerialData(), RECEIVED_DATA_BYTES);
	}
	
	@Test
	/**
	 * Verify that, when subscribed to receive serial data and a packet that does not correspond to serial 
	 * data is received, the callback of the listener is not executed.
	 * @throws Exception
	 */
	public void testSerialDataReceiveSubscribedInvalid() throws Exception {
		dataReader.addSerialDatatReceiveListener(receiveSerialDataListener);
		
		// Fire the private packetReceived method of the dataReader with an invalid packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, invalidPacket);
		
		// Verify that the notifySerialDataReceived private method was not called.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(0)).invoke(NOTIFY_SERIAL_DATA_RECEIVED_METHOD, Mockito.anyString(), Mockito.anyObject());
		
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
	 */
	private void notifySerialReceivedListeners(String address, byte[] data) {
		@SuppressWarnings("unchecked")
		ArrayList<ISerialDataReceiveListener> serialDataReceiveListeners = (ArrayList<ISerialDataReceiveListener>)Whitebox.getInternalState(dataReader, "serialDataReceiveListeners");
		for (ISerialDataReceiveListener listener:serialDataReceiveListeners)
			listener.serialDataReceived(address, data);
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
	
	// TODO: More test cases can be added here to try null data, address, etc.
}

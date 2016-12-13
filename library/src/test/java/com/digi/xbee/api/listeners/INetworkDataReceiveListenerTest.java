/**
 * Copyright (c) 2016 Digi International Inc.,
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

import java.net.Inet4Address;
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

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.connection.DataReader;
import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.models.NetworkMessage;
import com.digi.xbee.api.models.NetworkProtocol;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.packet.network.RXIPv4Packet;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DataReader.class})
public class INetworkDataReceiveListenerTest {
	
	// Constants.
	private static final String IP_ADDRESS = "10.10.2.123";
	
	private static final int SOURCE_PORT = 123;
	private static final int DEST_PORT = 456;
	
	private static final NetworkProtocol PROTOCOL = NetworkProtocol.TCP;
	
	private static final String RECEIVED_DATA = "data";
	private static final byte[] RECEIVED_DATA_BYTES = RECEIVED_DATA.getBytes();
	
	private static final String PACKET_RECEIVED_METHOD = "packetReceived";
	private static final String NOTIFY_DATA_RECEIVED_METHOD = "notifyNetworkDataReceived";
	
	// Variables.
	private static XBeeDevice xbeeDevice;
	
	private MyReceiveListener receiveNetworkDataListener;
	
	private static RXIPv4Packet rxIPv4Packet;
	private static ATCommandResponsePacket invalidPacket;
	
	private static Inet4Address sourceAddress;
	
	private DataReader dataReader;
	
	@BeforeClass
	public static void setupOnce() throws Exception {
		sourceAddress = (Inet4Address) Inet4Address.getByName(IP_ADDRESS);
		
		// Mock RX IPV4 Packet.
		rxIPv4Packet = Mockito.mock(RXIPv4Packet.class);
		Mockito.when(rxIPv4Packet.getFrameType()).thenReturn(APIFrameType.RX_IPV4);
		Mockito.when(rxIPv4Packet.getSourcePort()).thenReturn(SOURCE_PORT);
		Mockito.when(rxIPv4Packet.getDestPort()).thenReturn(DEST_PORT);
		Mockito.when(rxIPv4Packet.getProtocol()).thenReturn(PROTOCOL);
		Mockito.when(rxIPv4Packet.getData()).thenReturn(RECEIVED_DATA_BYTES);
		Mockito.when(rxIPv4Packet.getSourceAddress()).thenReturn(sourceAddress);
		
		// Mock an invalid packet.
		invalidPacket = Mockito.mock(ATCommandResponsePacket.class);
		
		// Mock the XBee device.
		xbeeDevice = Mockito.mock(XBeeDevice.class);
	}
	
	@Before
	public void setup() throws Exception {
		// Data receive listener.
		receiveNetworkDataListener = PowerMockito.spy(new MyReceiveListener());
		
		// Data reader.
		dataReader = PowerMockito.spy(new DataReader(Mockito.mock(IConnectionInterface.class), OperatingMode.UNKNOWN, xbeeDevice));
		// Stub the 'notifyDataReceived' method of the dataReader instance so it directly notifies the 
		// listeners instead of opening a new thread per listener (which is what the real method does). This avoids us 
		// having to wait for the executor to run the threads.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				NetworkMessage networkMessage = (NetworkMessage)args[0];
				notifyDataReceiveListeners(networkMessage);
				return null;
			}
		}).when(dataReader, NOTIFY_DATA_RECEIVED_METHOD, Mockito.any(NetworkMessage.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.INetworkDataReceiveListener#networkDataReceived(NetworkMessage)}.
	 * 
	 * <p>Verify that the network data received callback of the INetworkDataReceiveListener interface
	 * is executed correctly when a NetworkMessage (originated by an RXIPV4 frame) is received.</p>
	 */
	@Test
	public void testDataReceiveEvent() {
		// This is the message that should have been created if an RXIPV4 frame would have been received.
		NetworkMessage networkMessage = new NetworkMessage(sourceAddress, SOURCE_PORT, DEST_PORT, PROTOCOL, RECEIVED_DATA_BYTES);
		
		receiveNetworkDataListener.networkDataReceived(networkMessage);
		
		assertEquals(sourceAddress, receiveNetworkDataListener.getIPAddress());
		assertEquals(SOURCE_PORT, receiveNetworkDataListener.getSourcePort());
		assertEquals(DEST_PORT, receiveNetworkDataListener.getDestPort());
		assertEquals(PROTOCOL, receiveNetworkDataListener.getProtocol());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveNetworkDataListener.getData());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.INetworkDataReceiveListener#networkDataReceived(NetworkMessage)} and
	 * {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that if the listener is not subscribed to receive network data, the callback 
	 * is not executed although a data packet is received.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataReceiveNotSubscribed() throws Exception {
		// Whenever a new IP address needs to be instantiated, return the constant one.
		PowerMockito.whenNew(Inet4Address.class).withAnyArguments().thenReturn(sourceAddress);
		
		// Fire the private packetReceived method of the dataReader with an RXIPV4Packet packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rxIPv4Packet);
		
		// Verify that the notifyNetworkDataReceived private method was called.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_DATA_RECEIVED_METHOD, 
				Mockito.any(NetworkMessage.class));
		
		// As the receiveNetworkDataListener was not subscribed in the networkDataReceiveListeners of 
		// the dataReader object, the IP address and data of the receiveNetworkDataListener should be null.
		assertNull(receiveNetworkDataListener.getIPAddress());
		assertEquals(-1, receiveNetworkDataListener.getSourcePort());
		assertEquals(-1, receiveNetworkDataListener.getDestPort());
		assertNull(receiveNetworkDataListener.getProtocol());
		assertNull(receiveNetworkDataListener.getData());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.INetworkDataReceiveListener#networkDataReceived(NetworkMessage)} and
	 * {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that, when subscribed to receive network data and an RXIPV4 packet is received,
	 * the callback of the listener is executed.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataReceiveSubscribedReceive() throws Exception {
		// Whenever a new IP address needs to be instantiated, return the constant one.
		PowerMockito.whenNew(Inet4Address.class).withAnyArguments().thenReturn(sourceAddress);
		
		// Subscribe to listen for network data.
		dataReader.addNetworkDataReceiveListener(receiveNetworkDataListener);
		
		// Fire the private packetReceived method of the dataReader with an RXIPV4 packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rxIPv4Packet);
		
		// Verify that the notifyNetworkDataReceived private method was called.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_DATA_RECEIVED_METHOD, 
				Mockito.any(NetworkMessage.class));
		
		// Verify that the networkDataReceived method of the listener was executed one time.
		Mockito.verify(receiveNetworkDataListener, Mockito.times(1)).networkDataReceived(Mockito.any(NetworkMessage.class));
		
		// All the parameters of our listener should be correct.
		assertEquals(sourceAddress, receiveNetworkDataListener.getIPAddress());
		assertEquals(SOURCE_PORT, receiveNetworkDataListener.getSourcePort());
		assertEquals(DEST_PORT, receiveNetworkDataListener.getDestPort());
		assertEquals(PROTOCOL, receiveNetworkDataListener.getProtocol());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveNetworkDataListener.getData());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.INetworkDataReceiveListener#networkDataReceived(NetworkMessage)} and
	 * {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that, when subscribed to receive network data and a packet that does not 
	 * correspond to network data is received, the callback of the listener is not executed.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataReceiveSubscribedInvalid() throws Exception {
		// Subscribe to listen for network data.
		dataReader.addNetworkDataReceiveListener(receiveNetworkDataListener);
		
		// Fire the private packetReceived method of the dataReader with an invalid packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, invalidPacket);
		
		// Verify that the notifyNetworkDataReceived private method was not called.
		PowerMockito.verifyPrivate(dataReader, Mockito.never()).invoke(NOTIFY_DATA_RECEIVED_METHOD, 
				Mockito.any(NetworkMessage.class));
		
		// Verify that the callback of the listener was not executed
		Mockito.verify(receiveNetworkDataListener, Mockito.never()).networkDataReceived(Mockito.any(NetworkMessage.class));
		
		// All the parameters of our listener should be empty.
		assertNull(receiveNetworkDataListener.getIPAddress());
		assertEquals(-1, receiveNetworkDataListener.getSourcePort());
		assertEquals(-1, receiveNetworkDataListener.getDestPort());
		assertNull(receiveNetworkDataListener.getProtocol());
		assertNull(receiveNetworkDataListener.getData());
	}
	
	/**
	 * This method directly notifies the INetworkDataReceiveListeners of the dataReader instance 
	 * that new network data has been received. This method intends to replace the original 
	 * 'notifyNetworkDataReceived' located within the dataReader object because it generates a 
	 * thread for each notify process.
	 * 
	 * @param networkMessage The NetworkMessage containing the IP address that sent the data, the data.
	 */
	private void notifyDataReceiveListeners(NetworkMessage networkMessage) {
		@SuppressWarnings("unchecked")
		ArrayList<INetworkDataReceiveListener> networkDataReceiveListeners = (ArrayList<INetworkDataReceiveListener>)Whitebox.getInternalState(dataReader, "networkDataReceiveListeners");
		for (INetworkDataReceiveListener listener:networkDataReceiveListeners)
			listener.networkDataReceived(networkMessage);
	}
	
	/**
	 * Helper class to test the INetworkDataReceiveListener.
	 */
	private class MyReceiveListener implements INetworkDataReceiveListener {
		
		// Variables.
		private byte[] data = null;
		private Inet4Address ipAddress = null;
		private int sourcePort = -1;
		private int destPort = -1;
		private NetworkProtocol protocol = null;
		
		/*
		 * (non-Javadoc)
		 * @see com.digi.xbee.api.listeners.INetworkDataReceiveListener#networkDataReceived(com.digi.xbee.api.models.NetworkMessage)
		 */
		public void networkDataReceived(NetworkMessage networkMessage) {
			this.ipAddress = networkMessage.getIPAddress();
			this.sourcePort = networkMessage.getSourcePort();
			this.destPort = networkMessage.getDestPort();
			this.protocol = networkMessage.getProtocol();
			this.data = networkMessage.getData();
		}
		
		/**
		 * Retrieves the data received.
		 * 
		 * @return The data.
		 */
		public byte[] getData() {
			return data;
		}
		
		/**
		 * Retrieves the IP address that sent the data.
		 * 
		 * @return The IP address.
		 */
		public Inet4Address getIPAddress() {
			return ipAddress;
		}
		
		/**
		 * Retrieves the source port of the transmission.
		 * 
		 * @return The source port of the transmission.
		 */
		public int getSourcePort() {
			return sourcePort;
		}
		
		/**
		 * Retrieves the destination port of the transmission.
		 * 
		 * @return The destination port of the transmission.
		 */
		public int getDestPort() {
			return destPort;
		}
		
		/**
		 * Retrieves the protocol of the transmission.
		 * 
		 * @return The protocol of the transmission.
		 */
		public NetworkProtocol getProtocol() {
			return protocol;
		}
	}
}

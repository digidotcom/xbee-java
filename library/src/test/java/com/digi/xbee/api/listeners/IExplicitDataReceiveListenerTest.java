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
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.connection.DataReader;
import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.models.ExplicitXBeeMessage;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DataReader.class})
public class IExplicitDataReceiveListenerTest {
	
	// Constants.
	private static final XBee64BitAddress XBEE_64BIT_ADDRESS = new XBee64BitAddress("0123456789ABCDEF");
	
	private static final String RECEIVED_DATA = "data";
	private static final byte[] RECEIVED_DATA_BYTES = RECEIVED_DATA.getBytes();
	
	private static final int RECEIVED_SOURCE_ENDPOINT = 0xA0;
	private static final int RECEIVED_DESTINATION_ENDPOINT = 0xA1;
	private static final int RECEIVED_CLUSTER_ID = 0x1554;
	private static final int RECEIVED_PROFILE_ID = 0xC105;
	
	private static final String PACKET_RECEIVED_METHOD = "packetReceived";
	private static final String NOTIFY_EXPLICIT_DATA_RECEIVED_METHOD = "notifyExplicitDataReceived";
	
	// Variables.
	private static XBeeDevice xbeeDevice;
	
	private MyExplicitDataReceiveListener explicitDataReceiveListener;
	
	private static ExplicitRxIndicatorPacket explicitDataPacket;
	private static ATCommandResponsePacket invalidPacket;
	
	private static RemoteXBeeDevice remoteXBeeDevice;
	
	private DataReader dataReader;
	
	@BeforeClass
	public static void setupOnce() throws Exception {
		// Mock Explicit Data Packet.
		explicitDataPacket = Mockito.mock(ExplicitRxIndicatorPacket.class);
		Mockito.when(explicitDataPacket.getFrameType()).thenReturn(APIFrameType.EXPLICIT_RX_INDICATOR);
		Mockito.when(explicitDataPacket.get64BitSourceAddress()).thenReturn(XBEE_64BIT_ADDRESS);
		Mockito.when(explicitDataPacket.getSourceEndpoint()).thenReturn(RECEIVED_SOURCE_ENDPOINT);
		Mockito.when(explicitDataPacket.getDestinationEndpoint()).thenReturn(RECEIVED_DESTINATION_ENDPOINT);
		Mockito.when(explicitDataPacket.getClusterID()).thenReturn(RECEIVED_CLUSTER_ID);
		Mockito.when(explicitDataPacket.getProfileID()).thenReturn(RECEIVED_PROFILE_ID);
		Mockito.when(explicitDataPacket.getRFData()).thenReturn(RECEIVED_DATA_BYTES);
		Mockito.when(explicitDataPacket.isBroadcast()).thenReturn(false);
		
		// Mock an invalid packet.
		invalidPacket = Mockito.mock(ATCommandResponsePacket.class);
		
		// Mock a remote XBee device.
		remoteXBeeDevice = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(remoteXBeeDevice.get64BitAddress()).thenReturn(XBEE_64BIT_ADDRESS);
		
		// Mock the XBee network.
		XBeeNetwork network = Mockito.mock(XBeeNetwork.class);
		Mockito.when(network.getDevice(Mockito.any(XBee64BitAddress.class))).thenReturn(remoteXBeeDevice);
		
		// Mock the XBee device.
		xbeeDevice = Mockito.mock(XBeeDevice.class);
		Mockito.when(xbeeDevice.getNetwork()).thenReturn(network);
	}
	
	@Before
	public void setup() throws Exception {
		// Data receive listener.
		explicitDataReceiveListener = PowerMockito.spy(new MyExplicitDataReceiveListener());
		
		// Data reader.
		dataReader = PowerMockito.spy(new DataReader(Mockito.mock(IConnectionInterface.class), OperatingMode.UNKNOWN, xbeeDevice));
		// Stub the 'notifyExplicitDataReceived' method of the dataReader instance so it directly notifies the 
		// listeners instead of opening a new thread per listener (which is what the real method does). This avoids us 
		// having to wait for the executor to run the threads.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				ExplicitXBeeMessage explicitXBeeMessage = (ExplicitXBeeMessage)args[0];
				notifyDataReceiveListeners(explicitXBeeMessage);
				return null;
			}
		}).when(dataReader, NOTIFY_EXPLICIT_DATA_RECEIVED_METHOD, Mockito.any(ExplicitXBeeMessage.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IExplicitDataReceiveListener#explicitDataReceived(ExplicitXBeeMessage)}.
	 * 
	 * <p>Verify that the explicit data received callback of the IExplicitDataReceive interface is executed 
	 * correctly when a unicast ExplicitXBeeMessage (originated by an explicit data receive frame) is received.</p>
	 */
	@Test
	public void testExplicitUnicastDataReceiveEvent() {
		// This is the message that should have been created if an explicit unicast data frame would have been received.
		ExplicitXBeeMessage explicitXBeeMessage = new ExplicitXBeeMessage(remoteXBeeDevice, RECEIVED_SOURCE_ENDPOINT, RECEIVED_DESTINATION_ENDPOINT, RECEIVED_CLUSTER_ID, RECEIVED_PROFILE_ID, RECEIVED_DATA_BYTES, false);
		
		explicitDataReceiveListener.explicitDataReceived(explicitXBeeMessage);
		
		assertEquals(XBEE_64BIT_ADDRESS, explicitDataReceiveListener.get64BitAddress());
		assertEquals(RECEIVED_SOURCE_ENDPOINT, explicitDataReceiveListener.getSourceEndpoint());
		assertEquals(RECEIVED_DESTINATION_ENDPOINT, explicitDataReceiveListener.getDestinationEndpoint());
		assertEquals(RECEIVED_CLUSTER_ID, explicitDataReceiveListener.getClusterID());
		assertEquals(RECEIVED_PROFILE_ID, explicitDataReceiveListener.getProfileID());
		assertArrayEquals(RECEIVED_DATA_BYTES, explicitDataReceiveListener.getData());
		assertFalse(explicitDataReceiveListener.isBroadcast());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IExplicitDataReceiveListener#explicitDataReceived(ExplicitXBeeMessage)}.
	 * 
	 * <p>Verify that the explicit data received callback of the IExplicitDataReceive interface is executed 
	 * correctly when a broadcast ExplicitXBeeMessage (originated by an explicit data receive frame) is received.</p>
	 */
	@Test
	public void testExplicitBroadcastDataReceiveEvent() {
		// This is the message that should have been created if an explicit broadcast data frame would have been received.
		ExplicitXBeeMessage explicitXBeeMessage = new ExplicitXBeeMessage(remoteXBeeDevice, RECEIVED_SOURCE_ENDPOINT, RECEIVED_DESTINATION_ENDPOINT, RECEIVED_CLUSTER_ID, RECEIVED_PROFILE_ID, RECEIVED_DATA_BYTES, true);
		
		explicitDataReceiveListener.explicitDataReceived(explicitXBeeMessage);
		
		assertEquals(XBEE_64BIT_ADDRESS, explicitDataReceiveListener.get64BitAddress());
		assertEquals(RECEIVED_SOURCE_ENDPOINT, explicitDataReceiveListener.getSourceEndpoint());
		assertEquals(RECEIVED_DESTINATION_ENDPOINT, explicitDataReceiveListener.getDestinationEndpoint());
		assertEquals(RECEIVED_CLUSTER_ID, explicitDataReceiveListener.getClusterID());
		assertEquals(RECEIVED_PROFILE_ID, explicitDataReceiveListener.getProfileID());
		assertArrayEquals(RECEIVED_DATA_BYTES, explicitDataReceiveListener.getData());
		assertTrue(explicitDataReceiveListener.isBroadcast());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IExplicitDataReceiveListener#explicitDataReceived(ExplicitXBeeMessage)} and
	 * {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that if the listener is not subscribed to receive explicit data, the callback is not 
	 * executed although an explicit data packet is received.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExplicitDataReceiveNotSubscribed() throws Exception {
		// Whenever a new remote XBee device needs to be instantiated, return the mocked one.
		PowerMockito.whenNew(RemoteXBeeDevice.class).withAnyArguments().thenReturn(remoteXBeeDevice);
		
		// Fire the private packetReceived method of the dataReader with an explicit data packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, explicitDataPacket);
		
		// Verify that the notifyExplicitDataReceived private method was called.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_EXPLICIT_DATA_RECEIVED_METHOD, 
				Mockito.any(ExplicitXBeeMessage.class));
		
		// As the receiveExplicitDataListener was not subscribed in the ExplicitDataReceiveListeners of the 
		// dataReader object, the values of the receiveExplicitDataListener should be null.
		assertNull(explicitDataReceiveListener.get64BitAddress());
		assertEquals(-1, explicitDataReceiveListener.getSourceEndpoint());
		assertEquals(-1, explicitDataReceiveListener.getDestinationEndpoint());
		assertEquals(-1, explicitDataReceiveListener.getClusterID());
		assertEquals(-1, explicitDataReceiveListener.getProfileID());
		assertNull(explicitDataReceiveListener.getData());
		assertFalse(explicitDataReceiveListener.isBroadcast());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IExplicitDataReceiveListener#explicitDataReceived(ExplicitXBeeMessage)} and
	 * {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that, when subscribed to receive explicit data and an Explicit Rx Indicator packet is received, 
	 * the callback of the listener is executed.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExplicitDataReceiveSubscribedReceive() throws Exception {
		// Whenever a new remote XBee device needs to be instantiated, return the mocked one.
		PowerMockito.whenNew(RemoteXBeeDevice.class).withAnyArguments().thenReturn(remoteXBeeDevice);
		
		// Subscribe to listen for data.
		dataReader.addExplicitDataReceiveListener(explicitDataReceiveListener);
		
		// Fire the private packetReceived method of the dataReader with an explicit data packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, explicitDataPacket);
		
		// Verify that the notifyExplicitDataReceived private method was called.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_EXPLICIT_DATA_RECEIVED_METHOD, 
				Mockito.any(ExplicitXBeeMessage.class));
		
		// Verify that the explicitDataReceived method of the listener was executed one time.
		Mockito.verify(explicitDataReceiveListener, Mockito.times(1)).explicitDataReceived(Mockito.any(ExplicitXBeeMessage.class));
		
		// All the parameters of our listener should be correct.
		assertEquals(XBEE_64BIT_ADDRESS, explicitDataReceiveListener.get64BitAddress());
		assertEquals(RECEIVED_SOURCE_ENDPOINT, explicitDataReceiveListener.getSourceEndpoint());
		assertEquals(RECEIVED_DESTINATION_ENDPOINT, explicitDataReceiveListener.getDestinationEndpoint());
		assertEquals(RECEIVED_CLUSTER_ID, explicitDataReceiveListener.getClusterID());
		assertEquals(RECEIVED_PROFILE_ID, explicitDataReceiveListener.getProfileID());
		assertArrayEquals(RECEIVED_DATA_BYTES, explicitDataReceiveListener.getData());
		assertFalse(explicitDataReceiveListener.isBroadcast());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IExplicitDataReceiveListener#explicitDataReceived(ExplicitXBeeMessage)} and
	 * {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that, when subscribed to receive data and a packet that does not correspond to 
	 * data, the callback of the listener is not executed.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExplicitDataReceiveSubscribedInvalid() throws Exception {
		// Subscribe to listen for explicit data.
		dataReader.addExplicitDataReceiveListener(explicitDataReceiveListener);
		
		// Fire the private packetReceived method of the dataReader with an invalid packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, invalidPacket);
		
		// Verify that the notifyExplicitDataReceived private method was not called.
		PowerMockito.verifyPrivate(dataReader, Mockito.never()).invoke(NOTIFY_EXPLICIT_DATA_RECEIVED_METHOD, 
				Mockito.any(ExplicitXBeeMessage.class));
		
		// Verify that the callback of the listener was not executed
		Mockito.verify(explicitDataReceiveListener, Mockito.never()).explicitDataReceived(Mockito.any(ExplicitXBeeMessage.class));
		
		// All the parameters of our listener should be empty (not initialized).
		assertNull(explicitDataReceiveListener.get64BitAddress());
		assertEquals(-1, explicitDataReceiveListener.getSourceEndpoint());
		assertEquals(-1, explicitDataReceiveListener.getDestinationEndpoint());
		assertEquals(-1, explicitDataReceiveListener.getClusterID());
		assertEquals(-1, explicitDataReceiveListener.getProfileID());
		assertNull(explicitDataReceiveListener.getData());
		assertFalse(explicitDataReceiveListener.isBroadcast());
	}
	
	
	/**
	 * This method directly notifies the IExplicitDataReceiveListeners of the dataReader instance that new 
	 * explicit data has been received. This method intends to replace the original 'notifyExplicitDataReceived' 
	 * located within the dataReader object because it generates a thread for each notify process.
	 * 
	 * @param explicitXBeeMessage The ExplicitXBeeMessage containing the node that sent the data, the data, a 
	 *                            flag indicating if the data was sent via broadcast and the application layer 
	 *                            fields (source endpoint, destination endpoint, cluster ID and profile ID).
	 */
	private void notifyDataReceiveListeners(ExplicitXBeeMessage explicitXBeeMessage) {
		@SuppressWarnings("unchecked")
		ArrayList<IExplicitDataReceiveListener> explicitDataReceiveListeners = (ArrayList<IExplicitDataReceiveListener>)Whitebox.getInternalState(dataReader, "explicitDataReceiveListeners");
		for (IExplicitDataReceiveListener listener:explicitDataReceiveListeners)
			listener.explicitDataReceived(explicitXBeeMessage);
	}
	
	/**
	 * Helper class to test the IExplicitDataReceiveListener.
	 */
	private class MyExplicitDataReceiveListener implements IExplicitDataReceiveListener {
		
		// Variables.
		private byte[] data = null;
		
		private int sourceEndpoint = -1;
		private int destEndpoint = -1;
		private int clusterID = -1;
		private int profileID = -1;
		
		private XBee64BitAddress address64 = null;
		
		private boolean isBroadcast = false;
		
		/*
		 * (non-Javadoc)
		 * @see com.digi.xbee.api.listeners.IExplicitDataReceiveListener#explicitDataReceived(com.digi.xbee.api.models.ExplicitXBeeMessage)
		 */
		@Override
		public void explicitDataReceived(ExplicitXBeeMessage explicitXBeeMessage) {
			this.address64 = explicitXBeeMessage.getDevice().get64BitAddress();
			this.clusterID = explicitXBeeMessage.getClusterID();
			this.profileID = explicitXBeeMessage.getProfileID();
			this.sourceEndpoint = explicitXBeeMessage.getSourceEndpoint();
			this.destEndpoint = explicitXBeeMessage.getDestinationEndpoint();
			this.data = explicitXBeeMessage.getData();
			this.isBroadcast = explicitXBeeMessage.isBroadcast();
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
		 * Returns the endpoint of the source that initiated the transmission.
		 * 
		 * @return The endpoint of the source that initiated the transmission.
		 */
		public int getSourceEndpoint() {
			return sourceEndpoint;
		}
		
		/**
		 * Returns the endpoint of the destination the message was addressed to.
		 * 
		 * @return The endpoint of the destination the message was addressed to.
		 */
		public int getDestinationEndpoint() {
			return destEndpoint;
		}
		
		/**
		 * Returns the cluster ID the packet was addressed to.
		 * 
		 * @return The cluster ID the packet was addressed to.
		 */
		public int getClusterID() {
			return clusterID;
		}
		
		/**
		 * Returns the profile ID the packet was addressed to.
		 * 
		 * @return The profile ID the packet was addressed to.
		 */
		public int getProfileID() {
			return profileID;
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
		 * Retrieves whether or not the data was sent via broadcast.
		 * 
		 * @return True if the data was sent via broadcast, false otherwise.
		 */
		public boolean isBroadcast() {
			return isBroadcast;
		}
	}
}

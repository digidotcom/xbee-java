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
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.packet.common.ExplicitRxIndicatorPacket;
import com.digi.xbee.api.packet.common.ReceivePacket;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DataReader.class})
public class IDataReceiveListenerXBeeTest {
	
	// Constants.
	private static final XBee64BitAddress XBEE_64BIT_ADDRESS = new XBee64BitAddress("0123456789ABCDEF");
	
	private static final String RECEIVED_DATA = "data";
	private static final byte[] RECEIVED_DATA_BYTES = RECEIVED_DATA.getBytes();
	
	private static final String PACKET_RECEIVED_METHOD = "packetReceived";
	private static final String NOTIFY_DATA_RECEIVED_METHOD = "notifyDataReceived";
	
	// Variables.
	private static XBeeDevice xbeeDevice;
	
	private MyReceiveListener receiveDataListener;
	
	private static ReceivePacket receivePacket;
	private static ExplicitRxIndicatorPacket explicitPacket;
	private static ATCommandResponsePacket invalidPacket;
	
	private static RemoteXBeeDevice remoteXBeeDevice;
	
	private DataReader dataReader;
	
	@BeforeClass
	public static void setupOnce() throws Exception {
		// Mock Receive Packet.
		receivePacket = Mockito.mock(ReceivePacket.class);
		Mockito.when(receivePacket.getFrameType()).thenReturn(APIFrameType.RECEIVE_PACKET);
		Mockito.when(receivePacket.getRFData()).thenReturn(RECEIVED_DATA_BYTES);
		Mockito.when(receivePacket.get64bitSourceAddress()).thenReturn(XBEE_64BIT_ADDRESS);
		Mockito.when(receivePacket.isBroadcast()).thenReturn(false);
		
		// Mock the Explicit RX Indicator Packet.
		explicitPacket = Mockito.mock(ExplicitRxIndicatorPacket.class);
		Mockito.when(explicitPacket.getFrameType()).thenReturn(APIFrameType.EXPLICIT_RX_INDICATOR);
		Mockito.when(explicitPacket.getRFData()).thenReturn(RECEIVED_DATA_BYTES);
		Mockito.when(explicitPacket.get64BitSourceAddress()).thenReturn(XBEE_64BIT_ADDRESS);
		Mockito.when(explicitPacket.get16BitSourceAddress()).thenReturn(XBee16BitAddress.UNKNOWN_ADDRESS);
		Mockito.when(explicitPacket.isBroadcast()).thenReturn(false);
		
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
		receiveDataListener = PowerMockito.spy(new MyReceiveListener());
		
		// Data reader.
		dataReader = PowerMockito.spy(new DataReader(Mockito.mock(IConnectionInterface.class), OperatingMode.UNKNOWN, xbeeDevice));
		// Stub the 'notifyDataReceived' method of the dataReader instance so it directly notifies the 
		// listeners instead of opening a new thread per listener (which is what the real method does). This avoids us 
		// having to wait for the executor to run the threads.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				XBeeMessage xbeeMessage = (XBeeMessage)args[0];
				notifyDataReceiveListeners(xbeeMessage);
				return null;
			}
		}).when(dataReader, NOTIFY_DATA_RECEIVED_METHOD, Mockito.any(XBeeMessage.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IDataReceiveListener#dataReceived(XBeeMessage)}.
	 * 
	 * <p>Verify that the data received callback of the IDataReceive interface is executed 
	 * correctly when a unicast XBeeMessage (originated by a receive frame) is received.</p>
	 */
	@Test
	public void testUnicastDataReceiveEvent() {
		// This is the message that should have been created if a receive frame would have been received.
		XBeeMessage xbeeMessage = new XBeeMessage(remoteXBeeDevice, RECEIVED_DATA_BYTES, false);
		
		receiveDataListener.dataReceived(xbeeMessage);
		
		assertEquals(XBEE_64BIT_ADDRESS, receiveDataListener.get64BitAddress());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveDataListener.getData());
		assertFalse(receiveDataListener.isBroadcast());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IDataReceiveListener#dataReceived(XBeeMessage)}.
	 * 
	 * <p>Verify that the data received callback of the IDataReceive interface is executed 
	 * correctly when a broadcast XBeeMessage (originated by a receive frame) is received.</p>
	 */
	@Test
	public void testBroadcastDataReceiveEvent() {
		// This is the message that should have been created if a receive frame would have been received.
		XBeeMessage xbeeMessage = new XBeeMessage(remoteXBeeDevice, RECEIVED_DATA_BYTES, true);
		
		receiveDataListener.dataReceived(xbeeMessage);
		
		assertEquals(XBEE_64BIT_ADDRESS, receiveDataListener.get64BitAddress());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveDataListener.getData());
		assertTrue(receiveDataListener.isBroadcast());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IDataReceiveListener#dataReceived(XBeeMessage)} and
	 * {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that if the listener is not subscribed to receive data, the callback is not 
	 * executed although a data packet is received.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataReceiveNotSubscribed() throws Exception {
		// Whenever a new remote XBee device needs to be instantiated, return the mocked one.
		PowerMockito.whenNew(RemoteXBeeDevice.class).withAnyArguments().thenReturn(remoteXBeeDevice);
		
		// Fire the private packetReceived method of the dataReader with a receive packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, receivePacket);
		
		// Verify that the notifyDataReceived private method was called.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_DATA_RECEIVED_METHOD, 
				Mockito.any(XBeeMessage.class));
		
		// As the receiveDataListener was not subscribed in the DataReceiveListeners of the dataReader object, the 
		// address and data of the receiveDataListener should be null.
		assertNull(receiveDataListener.get64BitAddress());
		assertNull(receiveDataListener.getData());
		assertFalse(receiveDataListener.isBroadcast());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IDataReceiveListener#dataReceived(XBeeMessage)} and
	 * {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that, when subscribed to receive data and a Receive packet is received, the 
	 * callback of the listener is executed.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataReceiveSubscribedReceive() throws Exception {
		// Whenever a new remote XBee device needs to be instantiated, return the mocked one.
		PowerMockito.whenNew(RemoteXBeeDevice.class).withAnyArguments().thenReturn(remoteXBeeDevice);
		
		// Subscribe to listen for data.
		dataReader.addDataReceiveListener(receiveDataListener);
		
		// Fire the private packetReceived method of the dataReader with a ReceivePacket.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, receivePacket);
		
		// Verify that the notifyDataReceived private method was called.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_DATA_RECEIVED_METHOD, 
				Mockito.any(XBeeMessage.class));
		
		// Verify that the dataReceived method of the listener was executed one time.
		Mockito.verify(receiveDataListener, Mockito.times(1)).dataReceived(Mockito.any(XBeeMessage.class));
		
		// All the parameters of our listener should be correct.
		assertEquals(XBEE_64BIT_ADDRESS, receiveDataListener.get64BitAddress());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveDataListener.getData());
		assertFalse(receiveDataListener.isBroadcast());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IDataReceiveListener#dataReceived(XBeeMessage)} and
	 * {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that, when subscribed to receive data and a packet that does not correspond to 
	 * data, the callback of the listener is not executed.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataReceiveSubscribedInvalid() throws Exception {
		// Subscribe to listen for data.
		dataReader.addDataReceiveListener(receiveDataListener);
		
		// Fire the private packetReceived method of the dataReader with an invalid packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, invalidPacket);
		
		// Verify that the notifyDataReceived private method was not called.
		PowerMockito.verifyPrivate(dataReader, Mockito.never()).invoke(NOTIFY_DATA_RECEIVED_METHOD, 
				Mockito.any(XBeeMessage.class));
		
		// Verify that the callback of the listener was not executed
		Mockito.verify(receiveDataListener, Mockito.never()).dataReceived(Mockito.any(XBeeMessage.class));
		
		// All the parameters of our listener should be empty.
		assertNull(receiveDataListener.get64BitAddress());
		assertNull(receiveDataListener.getData());
		assertFalse(receiveDataListener.isBroadcast());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IDataReceiveListener#dataReceived(XBeeMessage)} and
	 * {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that, when subscribed to receive data and an Explicit Receive data packet for Digi transmissions
	 * is received, the callback of the listener is executed.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExplicitDataReceiveSubscribedReceive() throws Exception {
		// Whenever a new remote XBee device needs to be instantiated, return the mocked one.
		PowerMockito.whenNew(RemoteXBeeDevice.class).withAnyArguments().thenReturn(remoteXBeeDevice);
		
		// The packet is an explicit data packet.
		Mockito.when(explicitPacket.getSourceEndpoint()).thenReturn(ExplicitRxIndicatorPacket.DATA_ENDPOINT);
		Mockito.when(explicitPacket.getDestinationEndpoint()).thenReturn(ExplicitRxIndicatorPacket.DATA_ENDPOINT);
		Mockito.when(explicitPacket.getClusterID()).thenReturn(ExplicitRxIndicatorPacket.DATA_CLUSTER);
		Mockito.when(explicitPacket.getProfileID()).thenReturn(ExplicitRxIndicatorPacket.DIGI_PROFILE);
		
		// Subscribe to listen for data.
		dataReader.addDataReceiveListener(receiveDataListener);
		
		// Fire the private packetReceived method of the dataReader with an ExplicitRxIndicatorPacket.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, explicitPacket);
		
		// Verify that the notifyDataReceived private method was called.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_DATA_RECEIVED_METHOD, 
				Mockito.any(XBeeMessage.class));
		
		// Verify that the dataReceived method of the listener was executed one time.
		Mockito.verify(receiveDataListener, Mockito.times(1)).dataReceived(Mockito.any(XBeeMessage.class));
		
		// All the parameters of our listener should be correct.
		assertEquals(XBEE_64BIT_ADDRESS, receiveDataListener.get64BitAddress());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveDataListener.getData());
		assertFalse(receiveDataListener.isBroadcast());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IDataReceiveListener#dataReceived(XBeeMessage)} and
	 * {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that, when subscribed to receive data and an Explicit Receive packet is received, the 
	 * callback of the listener is not executed.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExplicitDataReceiveSubscribedInvalid() throws Exception {
		// Whenever a new remote XBee device needs to be instantiated, return the mocked one.
		PowerMockito.whenNew(RemoteXBeeDevice.class).withAnyArguments().thenReturn(remoteXBeeDevice);
		
		// The packet is not an explicit data packet.
		Mockito.when(explicitPacket.getSourceEndpoint()).thenReturn(0x1A);
		Mockito.when(explicitPacket.getDestinationEndpoint()).thenReturn(0x1B);
		Mockito.when(explicitPacket.getClusterID()).thenReturn(0x1010);
		Mockito.when(explicitPacket.getProfileID()).thenReturn(0x350B);
		
		// Subscribe to listen for data.
		dataReader.addDataReceiveListener(receiveDataListener);
		
		// Fire the private packetReceived method of the dataReader with an ExplicitRxIndicatorPacket.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, explicitPacket);
		
		// Verify that the notifyDataReceived private method was not called.
		PowerMockito.verifyPrivate(dataReader, Mockito.never()).invoke(NOTIFY_DATA_RECEIVED_METHOD, 
				Mockito.any(XBeeMessage.class));
		
		// Verify that the callback of the listener was not executed
		Mockito.verify(receiveDataListener, Mockito.never()).dataReceived(Mockito.any(XBeeMessage.class));
		
		// All the parameters of our listener should be empty.
		assertNull(receiveDataListener.get64BitAddress());
		assertNull(receiveDataListener.getData());
		assertFalse(receiveDataListener.isBroadcast());
	}
	
	/**
	 * This method directly notifies the IDataReceiveListeners of the dataReader instance that new 
	 * data has been received. This method intends to replace the original 'notifyDataReceived' 
	 * located within the dataReader object because it generates a thread for each notify process.
	 * 
	 * @param xbeeMessage The XBeeMessage containing the address of the node that sent the data, the data 
	 *                    and a flag indicating if the data was sent via broadcast.
	 */
	private void notifyDataReceiveListeners(XBeeMessage xbeeMessage) {
		@SuppressWarnings("unchecked")
		ArrayList<IDataReceiveListener> dataReceiveListeners = (ArrayList<IDataReceiveListener>)Whitebox.getInternalState(dataReader, "dataReceiveListeners");
		for (IDataReceiveListener listener:dataReceiveListeners)
			listener.dataReceived(xbeeMessage);
	}
	
	/**
	 * Helper class to test the IDataReceiveListener.
	 */
	private class MyReceiveListener implements IDataReceiveListener {
		
		// Variables.
		private byte[] data = null;
		private XBee64BitAddress address64 = null;
		private boolean isBroadcast = false;
		
		/*
		 * (non-Javadoc)
		 * @see com.digi.xbee.api.listeners.IDataReceiveListener#dataReceived(com.digi.xbee.api.models.XBeeMessage)
		 */
		public void dataReceived(XBeeMessage xbeeMessage) {
			this.address64 = xbeeMessage.getDevice().get64BitAddress();
			this.data = xbeeMessage.getData();
			this.isBroadcast = xbeeMessage.isBroadcast();
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

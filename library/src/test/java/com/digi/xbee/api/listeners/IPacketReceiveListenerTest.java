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
import java.util.HashMap;

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
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.packet.common.IODataSampleRxIndicatorPacket;
import com.digi.xbee.api.packet.common.ReceivePacket;
import com.digi.xbee.api.packet.common.TransmitStatusPacket;
import com.digi.xbee.api.packet.raw.RX16Packet;
import com.digi.xbee.api.packet.raw.RX64Packet;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DataReader.class})
public class IPacketReceiveListenerTest {
	
	// Constants.
	private static final String PACKET_RECEIVED_METHOD = "packetReceived";
	private static final String NOTIFY_PACKET_RECEIVED_METHOD = "notifyPacketReceived";
	
	private final static int ALL_FRAME_IDS = 99999;
	
	// Variables.
	private MyReceiveListener receivePacketListener;
	
	private static RX16Packet rx16Packet;
	private static RX64Packet rx64Packet;
	private static ReceivePacket receivePacket;
	private static ATCommandResponsePacket atResponsePacket;
	private static TransmitStatusPacket transmitStatusPacket;
	private static IODataSampleRxIndicatorPacket ioSamplePacket;
	
	private static ArrayList<XBeePacket> xbeePackets = new ArrayList<XBeePacket>();
	
	private DataReader dataReader;
	
	@BeforeClass
	public static void setupOnce() {
		// Mock Rx16 Packet.
		rx16Packet = Mockito.mock(RX16Packet.class);
		
		// Mock Rx64 Packet.
		rx64Packet = Mockito.mock(RX64Packet.class);
		
		// Mock Receive Packet.
		receivePacket = Mockito.mock(ReceivePacket.class);
		
		// Mock an AT Command Response Packet.
		atResponsePacket = Mockito.mock(ATCommandResponsePacket.class);
		
		// Mock an Transmit Status Packet.
		transmitStatusPacket = Mockito.mock(TransmitStatusPacket.class);
		
		// Mock an IO Data Sample Packet.
		ioSamplePacket = Mockito.mock(IODataSampleRxIndicatorPacket.class);
		
		// Add all the mocked packets to the list of packets.
		xbeePackets.add(rx16Packet);
		xbeePackets.add(rx64Packet);
		xbeePackets.add(receivePacket);
		xbeePackets.add(atResponsePacket);
		xbeePackets.add(transmitStatusPacket);
		xbeePackets.add(ioSamplePacket);
	}
	
	@Before
	public void setup() throws Exception {
		// Data receive listener.
		receivePacketListener = PowerMockito.spy(new MyReceiveListener());
		
		// Data reader.
		dataReader = PowerMockito.spy(new DataReader(Mockito.mock(IConnectionInterface.class), OperatingMode.UNKNOWN, Mockito.mock(XBeeDevice.class)));
		// Stub the 'notifyPacketReceived' method of the dataReader instance so it directly notifies the 
		// listeners instead of opening a new thread per listener (which is what the real method does). This avoids us 
		// having to wait for the executor to run the threads.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				XBeePacket receivedPacket = (XBeePacket)args[0];
				notifyPacketReceivedListeners(receivedPacket);
				return null;
			}
		}).when(dataReader, NOTIFY_PACKET_RECEIVED_METHOD, (XBeePacket)Mockito.any());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that the callback of the IPacketReceiveListener interface is executed correctly.</p>
	 */
	@Test
	public void testUnicastDataReceiveEvent() {
		// The callback should work for any kind of packet.
		for (XBeePacket packet:xbeePackets) {
			receivePacketListener.packetReceived(packet);
			assertEquals(packet, receivePacketListener.getPacket());
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IPacketReceiveListener#packetReceived(XBeePacket)} and
	 * {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that if the listener is not subscribed to receive packets, the callback is not 
	 * executed although a packet is received.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPacketReceiveNotSubscribed() throws Exception {
		for (XBeePacket packet:xbeePackets) {
			// Fire the private packetReceived method of the dataReader.
			Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, packet);
			
			// Verify that the notifyPacketReceived private method was called with the correct packet.
			PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_PACKET_RECEIVED_METHOD, packet);
			
			// As the receivePacketListener was not subscribed in the packetReceiveListeners of the dataReader object, the 
			// packet of the receiveDataListener should be null.
			assertNull(receivePacketListener.getPacket());
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IPacketReceiveListener#packetReceived(XBeePacket)} and
	 * {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that, when subscribed to receive packets with any frame ID, and any packet is received, the 
	 * callback of the listener is always executed.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPacketReceiveSubscribedAllFrameIDs() throws Exception {
		// Subscribe to listen for packets.
		dataReader.addPacketReceiveListener(receivePacketListener);
		
		for (XBeePacket packet:xbeePackets) {
			// Fire the private packetReceived method of the dataReader.
			Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, packet);
			
			// Verify that the notifyPacketReceived private method was called with the correct address and data.
			PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_PACKET_RECEIVED_METHOD, packet);
			
			// Verify that the packetReceived method of the listener was executed one time.
			Mockito.verify(receivePacketListener, Mockito.times(1)).packetReceived(packet);
			
			// Verify that the packet received by the listener is correct.
			assertEquals(packet, receivePacketListener.getPacket());
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IPacketReceiveListener#packetReceived(XBeePacket)} and
	 * {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that, when subscribed to receive packets of a specific frame ID, and a packet with that ID is 
	 * received, the callback of the listener is executed, but next time that the same packet or other packets 
	 * are received, it is not.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPacketReceiveSubscribedSpecificFrameID() throws Exception {
		// Subscribe to listen for packet with frame ID 0x05.
		dataReader.addPacketReceiveListener(receivePacketListener, 0x05);
		
		// The listener should not receive any frame that doesn't have frame ID or it is not 
		// the expected one (0x05).
		for (XBeePacket packet:xbeePackets) {
			// Fire the private packetReceived method of the dataReader.
			Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, packet);
			
			// Verify that the notifyPacketReceived private method was called with the correct address and data.
			PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_PACKET_RECEIVED_METHOD, packet);
			
			// Verify that the packetReceived method of the listener was not called.
			Mockito.verify(receivePacketListener, Mockito.never()).packetReceived(packet);
			
			// Verify that the packet received by the listener is null.
			assertNull(receivePacketListener.getPacket());
		}
		
		// Change the frame ID of one packet to 0x05.
		Mockito.when(atResponsePacket.needsAPIFrameID()).thenReturn(true);
		Mockito.when(atResponsePacket.getFrameID()).thenReturn(0x05);
		
		// Fire the private packetReceived method of the dataReader.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, atResponsePacket);
		
		// Verify that the packetReceived method of the listener was called with the correct packet.
		Mockito.verify(receivePacketListener, Mockito.times(1)).packetReceived(atResponsePacket);
		
		// Verify that the packet received by the listener is the expected one.
		assertEquals(atResponsePacket, receivePacketListener.getPacket());
		
		// Send the packet again and verify that the callback of the listener was still called 1 time, 
		// but not more.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, atResponsePacket);
		Mockito.verify(receivePacketListener, Mockito.times(1)).packetReceived(atResponsePacket);
	}
	
	/**
	 * This method directly notifies the IPacketReceiveListener of the dataReader instance that new 
	 * XBee packet has been received. This method intends to replace the original 'notifyPacketReceived' 
	 * located within the dataReader object because it generates a thread for each notify process.
	 * 
	 * @param receivedPacket The packet received.
	 */
	private void notifyPacketReceivedListeners(XBeePacket receivedPacket) {
		@SuppressWarnings("unchecked")
		HashMap<IPacketReceiveListener, Integer> xbeePacketReceiveListeners = (HashMap<IPacketReceiveListener, Integer>)Whitebox.getInternalState(dataReader, "packetReceiveListeners");
		for (IPacketReceiveListener listener:xbeePacketReceiveListeners.keySet()) {
			if (xbeePacketReceiveListeners.get(listener) == ALL_FRAME_IDS)
				listener.packetReceived(receivedPacket);
			else if (((XBeeAPIPacket)receivedPacket).needsAPIFrameID() && 
					((XBeeAPIPacket)receivedPacket).getFrameID() == xbeePacketReceiveListeners.get(listener)) {
				listener.packetReceived(receivedPacket);
				dataReader.removePacketReceiveListener(listener);
			}
		}
	}
	
	/**
	 * Helper class to test the IPacketReceiveListener.
	 */
	private class MyReceiveListener implements IPacketReceiveListener {
		
		// Variables.
		private XBeePacket receivedPacket = null;
		
		/*
		 * (non-Javadoc)
		 * @see com.digi.xbee.api.listeners.IPacketReceiveListener#packetReceived(com.digi.xbee.api.packet.XBeePacket)
		 */
		public void packetReceived(XBeePacket receivedPacket) {
			this.receivedPacket = receivedPacket;
		}
		
		/**
		 * Retrieves the XBee packet received.
		 * 
		 * @return The XBee packet received.
		 */
		public XBeePacket getPacket() {
			return receivedPacket;
		}
	}
}

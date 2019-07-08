/*
 * Copyright 2017-2019, Digi International Inc.
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

import java.net.Inet6Address;
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

import com.digi.xbee.api.DataReader;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.models.IPMessage;
import com.digi.xbee.api.models.IPProtocol;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.packet.thread.RXIPv6Packet;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DataReader.class})
public class IIPDataReceiveListenerIPv6Test {
	
	// Constants.
	private static final String IPV6_SRC_ADDRESS = "FDB3:0001:0002:0000:0004:0005:0006:0007";
	private static final String IPV6_DST_ADDRESS = "FDB4:0002:0003:0000:0005:0006:0007:0008";
	
	private static final int SOURCE_PORT = 123;
	private static final int DEST_PORT = 456;
	
	private static final IPProtocol PROTOCOL = IPProtocol.TCP;
	
	private static final String RECEIVED_DATA = "data";
	private static final byte[] RECEIVED_DATA_BYTES = RECEIVED_DATA.getBytes();
	
	private static final String PACKET_RECEIVED_METHOD = "packetReceived";
	private static final String NOTIFY_DATA_RECEIVED_METHOD = "notifyIPDataReceived";
	
	// Variables.
	private static XBeeDevice xbeeDevice;
	
	private MyReceiveListener receiveIPDataListener;
	
	private static RXIPv6Packet rxIPv6Packet;
	private static ATCommandResponsePacket invalidPacket;
	
	private static Inet6Address sourceAddress;
	private static Inet6Address destAddress;
	
	private DataReader dataReader;
	
	@BeforeClass
	public static void setupOnce() throws Exception {
		sourceAddress = (Inet6Address) Inet6Address.getByName(IPV6_SRC_ADDRESS);
		destAddress = (Inet6Address) Inet6Address.getByName(IPV6_DST_ADDRESS);
		
		// Mock RX IPV4 Packet.
		rxIPv6Packet = Mockito.mock(RXIPv6Packet.class);
		Mockito.when(rxIPv6Packet.getFrameType()).thenReturn(APIFrameType.RX_IPV6);
		Mockito.when(rxIPv6Packet.getSourcePort()).thenReturn(SOURCE_PORT);
		Mockito.when(rxIPv6Packet.getDestPort()).thenReturn(DEST_PORT);
		Mockito.when(rxIPv6Packet.getProtocol()).thenReturn(PROTOCOL);
		Mockito.when(rxIPv6Packet.getData()).thenReturn(RECEIVED_DATA_BYTES);
		Mockito.when(rxIPv6Packet.getSourceAddress()).thenReturn(sourceAddress);
		Mockito.when(rxIPv6Packet.getDestAddress()).thenReturn(destAddress);
		
		// Mock an invalid packet.
		invalidPacket = Mockito.mock(ATCommandResponsePacket.class);
		
		// Mock the XBee device.
		xbeeDevice = Mockito.mock(XBeeDevice.class);
	}
	
	@Before
	public void setup() throws Exception {
		// Data receive listener.
		receiveIPDataListener = PowerMockito.spy(new MyReceiveListener());
		
		// Data reader.
		dataReader = PowerMockito.spy(new DataReader(Mockito.mock(IConnectionInterface.class), OperatingMode.UNKNOWN, xbeeDevice));
		// Stub the 'notifyDataReceived' method of the dataReader instance so it directly notifies the 
		// listeners instead of opening a new thread per listener (which is what the real method does). This avoids us 
		// having to wait for the executor to run the threads.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				IPMessage ipMessage = (IPMessage)args[0];
				notifyDataReceiveListeners(ipMessage);
				return null;
			}
		}).when(dataReader, NOTIFY_DATA_RECEIVED_METHOD, Mockito.any(IPMessage.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IIPDataReceiveListener#ipDataReceived(IPMessage)}.
	 * 
	 * <p>Verify that the IPv6 data received callback of the IIPDataReceiveListener interface
	 * is executed correctly when a IPMessage (originated by an RXIPV4 frame) is received.</p>
	 */
	@Test
	public void testDataReceiveEvent() {
		// This is the message that should have been created if an RXIPV6 frame would have been received.
		IPMessage ipMessage = new IPMessage(sourceAddress, SOURCE_PORT, DEST_PORT, PROTOCOL, RECEIVED_DATA_BYTES);
		
		receiveIPDataListener.ipDataReceived(ipMessage);
		
		assertEquals(sourceAddress, receiveIPDataListener.getIPAddress());
		assertEquals(SOURCE_PORT, receiveIPDataListener.getSourcePort());
		assertEquals(DEST_PORT, receiveIPDataListener.getDestPort());
		assertEquals(PROTOCOL, receiveIPDataListener.getProtocol());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveIPDataListener.getData());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IIPDataReceiveListener#ipDataReceived(IPMessage)} and
	 * {@link com.digi.xbee.api.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that if the listener is not subscribed to receive IPv6 data, the callback 
	 * is not executed although a data packet is received.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataReceiveNotSubscribed() throws Exception {
		// Whenever a new IPv6 address needs to be instantiated, return the constant one.
		PowerMockito.whenNew(Inet6Address.class).withAnyArguments().thenReturn(sourceAddress);
		
		// Fire the private packetReceived method of the dataReader with an RXIPV4Packet packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rxIPv6Packet);
		
		// Verify that the notifyIPDataReceived private method was called.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_DATA_RECEIVED_METHOD, 
				Mockito.any(IPMessage.class));
		
		// As the receiveIPDataListener was not subscribed in the ipDataReceiveListeners of 
		// the dataReader object, the IPv6 address and data of the receiveIPDataListener should be null.
		assertNull(receiveIPDataListener.getIPAddress());
		assertEquals(-1, receiveIPDataListener.getSourcePort());
		assertEquals(-1, receiveIPDataListener.getDestPort());
		assertNull(receiveIPDataListener.getProtocol());
		assertNull(receiveIPDataListener.getData());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IIPDataReceiveListener#ipDataReceived(IPMessage)} and
	 * {@link com.digi.xbee.api.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that, when subscribed to receive IPv6 data and an RXIPV4 packet is received,
	 * the callback of the listener is executed.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataReceiveSubscribedReceive() throws Exception {
		// Whenever a new IPv6 address needs to be instantiated, return the constant one.
		PowerMockito.whenNew(Inet6Address.class).withAnyArguments().thenReturn(sourceAddress);
		
		// Subscribe to listen for IPv6 data.
		dataReader.addIPDataReceiveListener(receiveIPDataListener);
		
		// Fire the private packetReceived method of the dataReader with an RXIPV4 packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rxIPv6Packet);
		
		// Verify that the notifyIPDataReceived private method was called.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_DATA_RECEIVED_METHOD, 
				Mockito.any(IPMessage.class));
		
		// Verify that the ipDataReceived method of the listener was executed one time.
		Mockito.verify(receiveIPDataListener, Mockito.times(1)).ipDataReceived(Mockito.any(IPMessage.class));
		
		// All the parameters of our listener should be correct.
		assertEquals(sourceAddress, receiveIPDataListener.getIPAddress());
		assertEquals(SOURCE_PORT, receiveIPDataListener.getSourcePort());
		assertEquals(DEST_PORT, receiveIPDataListener.getDestPort());
		assertEquals(PROTOCOL, receiveIPDataListener.getProtocol());
		assertArrayEquals(RECEIVED_DATA_BYTES, receiveIPDataListener.getData());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IIPDataReceiveListener#ipDataReceived(IPMessage)} and
	 * {@link com.digi.xbee.api.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that, when subscribed to receive IPv6 data and a packet that does not 
	 * correspond to IPv6 data is received, the callback of the listener is not executed.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataReceiveSubscribedInvalid() throws Exception {
		// Subscribe to listen for IPv6 data.
		dataReader.addIPDataReceiveListener(receiveIPDataListener);
		
		// Fire the private packetReceived method of the dataReader with an invalid packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, invalidPacket);
		
		// Verify that the notifyIPDataReceived private method was not called.
		PowerMockito.verifyPrivate(dataReader, Mockito.never()).invoke(NOTIFY_DATA_RECEIVED_METHOD, 
				Mockito.any(IPMessage.class));
		
		// Verify that the callback of the listener was not executed
		Mockito.verify(receiveIPDataListener, Mockito.never()).ipDataReceived(Mockito.any(IPMessage.class));
		
		// All the parameters of our listener should be empty.
		assertNull(receiveIPDataListener.getIPAddress());
		assertEquals(-1, receiveIPDataListener.getSourcePort());
		assertEquals(-1, receiveIPDataListener.getDestPort());
		assertNull(receiveIPDataListener.getProtocol());
		assertNull(receiveIPDataListener.getData());
	}
	
	/**
	 * This method directly notifies the IIPDataReceiveListeners of the dataReader instance 
	 * that new IPv6 data has been received. This method intends to replace the original 
	 * 'notifyIPDataReceived' located within the dataReader object because it generates a 
	 * thread for each notify process.
	 * 
	 * @param ipMessage The IPMessage containing the IPv6 address that sent the data, the data.
	 */
	private void notifyDataReceiveListeners(IPMessage ipMessage) {
		@SuppressWarnings("unchecked")
		ArrayList<IIPDataReceiveListener> ipDataReceiveListeners = (ArrayList<IIPDataReceiveListener>)Whitebox.getInternalState(dataReader, "ipDataReceiveListeners");
		for (IIPDataReceiveListener listener:ipDataReceiveListeners)
			listener.ipDataReceived(ipMessage);
	}
	
	/**
	 * Helper class to test the IIPDataReceiveListener.
	 */
	private class MyReceiveListener implements IIPDataReceiveListener {
		
		// Variables.
		private byte[] data = null;
		private Inet6Address ipAddress = null;
		private int sourcePort = -1;
		private int destPort = -1;
		private IPProtocol protocol = null;
		
		/*
		 * (non-Javadoc)
		 * @see com.digi.xbee.api.listeners.IIPDataReceiveListener#ipDataReceived(com.digi.xbee.api.models.IPMessage)
		 */
		public void ipDataReceived(IPMessage ipMessage) {
			this.ipAddress = ipMessage.getIPv6Address();
			this.sourcePort = ipMessage.getSourcePort();
			this.destPort = ipMessage.getDestPort();
			this.protocol = ipMessage.getProtocol();
			this.data = ipMessage.getData();
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
		 * Retrieves the IPv6 address that sent the data.
		 * 
		 * @return The IPv6 address.
		 */
		public Inet6Address getIPAddress() {
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
		public IPProtocol getProtocol() {
			return protocol;
		}
	}
}

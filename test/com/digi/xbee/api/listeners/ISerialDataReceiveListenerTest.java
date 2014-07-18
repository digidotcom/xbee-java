package com.digi.xbee.api.listeners;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.digi.xbee.api.connection.DataReader;
import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.XBeeAPIType;
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
	
	// Variables.
	private MyReceiveListener receiveSerialDataListener = new MyReceiveListener();
	
	private RX16Packet rx16Packet;
	private RX64Packet rx64Packet;
	private ReceivePacket receivePacket;
	private ATCommandResponsePacket invalidPacket;
	
	private DataReader dataReader;
	
	@Before
	public void setup() {
		// Serial data receive listener.
		//receiveSerialDataListener = new MyReceiveListener();
		
		// Data reader.
		dataReader = PowerMockito.spy(new DataReader(Mockito.mock(IConnectionInterface.class)));
		
		// Mock Rx16 Packet.
		rx16Packet = Mockito.mock(RX16Packet.class);
		Mockito.when(rx16Packet.getAPIID()).thenReturn(XBeeAPIType.RX_16);
		Mockito.when(rx16Packet.getReceivedData()).thenReturn(RECEIVED_DATA_BYTES);
		Mockito.when(rx16Packet.getSourceAddress()).thenReturn(XBEE_16BIT_ADDRESS);
		
		// Mock Rx64 Packet.
		rx64Packet = Mockito.mock(RX64Packet.class);
		Mockito.when(rx64Packet.getAPIID()).thenReturn(XBeeAPIType.RX_64);
		Mockito.when(rx64Packet.getReceivedData()).thenReturn(RECEIVED_DATA_BYTES);
		Mockito.when(rx64Packet.getSourceAddress()).thenReturn(XBEE_64BIT_ADDRESS);
		
		// Mock Receive Packet.
		receivePacket = Mockito.mock(ReceivePacket.class);
		Mockito.when(receivePacket.getAPIID()).thenReturn(XBeeAPIType.RECEIVE_PACKET);
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
		// Verify that if the listener is not subscribed, the callback is not executed when a Rx16 packet is received.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rx16Packet);
		
		// Verify that the notifySerialDataReceived private method was not called.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke("notifySerialDataReceived", Mockito.anyString(), Mockito.anyObject());
		
		// notifySerialDataReceived method starts a thread executor, need some time to let the thread of the listener run.
		Thread.sleep(10);
		
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
		
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rx16Packet);
		
		// Verify that the notifySerialDataReceived private method was called with the correct address and data.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke("notifySerialDataReceived", XBEE_16BIT_ADDRESS.toString(), RECEIVED_DATA_BYTES);
		
		// notifySerialDataReceived method starts a thread executor, need some time to let the thread of the listener run.
		Thread.sleep(10);
		
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
		
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rx64Packet);
		
		// Verify that the notifySerialDataReceived private method was called with the correct address and data.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke("notifySerialDataReceived", XBEE_64BIT_ADDRESS.toString(), RECEIVED_DATA_BYTES);
		
		// notifySerialDataReceived method starts a thread executor, need some time to let the thread of the listener run.
		Thread.sleep(10);
		
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
		
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, receivePacket);
		
		// Verify that the notifySerialDataReceived private method was called with the correct address and data.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke("notifySerialDataReceived", XBEE_64BIT_ADDRESS.toString(), RECEIVED_DATA_BYTES);
		
		// notifySerialDataReceived method starts a thread executor, need some time to let the thread of the listener run.
		Thread.sleep(10);
		
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
		
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, invalidPacket);
		
		// Verify that the notifySerialDataReceived private method was not called.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(0)).invoke("notifySerialDataReceived", Mockito.anyString(), Mockito.anyObject());
		
		// notifySerialDataReceived method starts a thread executor, need some time to let the thread of the listener run.
		Thread.sleep(10);
		
		assertNull(receiveSerialDataListener.getAddress());
		assertNull(receiveSerialDataListener.getSerialData());
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
}

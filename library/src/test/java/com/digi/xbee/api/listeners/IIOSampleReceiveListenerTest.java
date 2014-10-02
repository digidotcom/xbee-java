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
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.packet.common.IODataSampleRxIndicatorPacket;
import com.digi.xbee.api.packet.raw.RX16IOPacket;
import com.digi.xbee.api.packet.raw.RX64IOPacket;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DataReader.class})
public class IIOSampleReceiveListenerTest {
	
	// Constants.
	private static final byte[] IO_SAMPLE_PAYLOAD = new byte[] {0x01, 0x00, 0x08, 0x00, 0x00, 0x08};
	private static final IOSample IO_SAMPLE = new IOSample(IO_SAMPLE_PAYLOAD);
	
	private static final String PACKET_RECEIVED_METHOD = "packetReceived";
	private static final String NOTIFY_IO_SAMPLE_RECEIVED_METHOD = "notifyIOSampleReceived";
	
	// Variables.
	private MyReceiveListener receiveIOSampleListener;
	
	private IODataSampleRxIndicatorPacket ioDataSampleRxIndicatorPacket;
	private RX16IOPacket rx16ioPacket;
	private RX64IOPacket rx64ioPacket;
	private ATCommandResponsePacket invalidPacket;
	
	private DataReader dataReader;
	
	@Before
	public void setup() throws Exception {
		// IO sample receive listener.
		receiveIOSampleListener = PowerMockito.spy(new MyReceiveListener());
		
		// Data reader.
		dataReader = PowerMockito.spy(new DataReader(Mockito.mock(IConnectionInterface.class), OperatingMode.UNKNOWN));
		// Stub the 'notifyIOSampleReceived' method of the dataReader instance so it directly notifies the 
		// listeners instead of opening a new thread per listener (which is what the real method does). This avoids us 
		// having to wait for the executor to run the threads.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				IOSample ioSample = (IOSample)args[0];
				notifyIOSampleReceivedListeners(ioSample);
				return null;
			}
		}).when(dataReader, NOTIFY_IO_SAMPLE_RECEIVED_METHOD, (IOSample)Mockito.any());
		
		// Mock an IODataSampleRXIndicator packet.
		ioDataSampleRxIndicatorPacket = Mockito.mock(IODataSampleRxIndicatorPacket.class);
		Mockito.when(ioDataSampleRxIndicatorPacket.getFrameType()).thenReturn(APIFrameType.IO_DATA_SAMPLE_RX_INDICATOR);
		Mockito.when(ioDataSampleRxIndicatorPacket.getIOSample()).thenReturn(IO_SAMPLE);
		
		// Mock a RX16IO packet.
		rx16ioPacket = Mockito.mock(RX16IOPacket.class);
		Mockito.when(rx16ioPacket.getFrameType()).thenReturn(APIFrameType.RX_IO_16);
		Mockito.when(rx16ioPacket.getIOSample()).thenReturn(IO_SAMPLE);
		
		// Mock a RX64IO packet.
		rx64ioPacket = Mockito.mock(RX64IOPacket.class);
		Mockito.when(rx64ioPacket.getFrameType()).thenReturn(APIFrameType.RX_IO_64);
		Mockito.when(rx64ioPacket.getIOSample()).thenReturn(IO_SAMPLE);
		
		// Mock an invalid packet.
		invalidPacket = Mockito.mock(ATCommandResponsePacket.class);
	}
	
	/**
	 * Verify that the callback of the IIOSampleReceiveListener interface is executed correctly.
	 */
	@Test
	public void testIOSampleReceiveEvent() {
		receiveIOSampleListener.ioSampleReceived(IO_SAMPLE);
		
		assertEquals(IO_SAMPLE, receiveIOSampleListener.getIOSample());
	}
	
	/**
	 * Verify that if the listener is not subscribed to receive IO samples, the callback is not 
	 * executed although an IO sample packet is received.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIOSampleReceiveNotSubscribed() throws Exception {
		// Fire the private packetReceived method of the dataReader with a RX64Packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, ioDataSampleRxIndicatorPacket);
		
		// Verify that the notifyIOSampleReceived private method was called with the correct IOSample.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_IO_SAMPLE_RECEIVED_METHOD, 
				IO_SAMPLE);
		
		// As the receiveIOSampleListener was not subscribed in the ioSampleReceiveListeners of the dataReader object, the 
		// IOSample of the receiveIOSampleListener should be null.
		assertNull(receiveIOSampleListener.getIOSample());
	}
	
	/**
	 * Verify that, when subscribed to receive IO samples and an IODataSampleRxIndicator packet is received, 
	 * the callback of the listener is executed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIOSampleReceiveSubscribedRxIndicator() throws Exception {
		// Subscribe to listen for IO samples.
		dataReader.addIOSampleReceiveListener(receiveIOSampleListener);
		
		// Fire the private packetReceived method of the dataReader with an IODataSampleRxIndicatorPacket.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, ioDataSampleRxIndicatorPacket);
		
		// Verify that the notifyIOSampleReceived private method was called with the correct IOSample.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_IO_SAMPLE_RECEIVED_METHOD, 
				IO_SAMPLE);
		
		// Verify that the listener callback was executed one time.
		Mockito.verify(receiveIOSampleListener, Mockito.times(1)).ioSampleReceived(IO_SAMPLE);
		
		assertEquals(IO_SAMPLE, receiveIOSampleListener.getIOSample());
	}
	
	/**
	 * Verify that, when subscribed to receive IO samples and an RX16IO packet is received, 
	 * the callback of the listener is executed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIOSampleReceiveSubscribedRx16IO() throws Exception {
		// Subscribe to listen for IO samples.
		dataReader.addIOSampleReceiveListener(receiveIOSampleListener);
		
		// Fire the private packetReceived method of the dataReader with an RX16IOPacket.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rx16ioPacket);
		
		// Verify that the notifyIOSampleReceived private method was called with the correct IOSample.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_IO_SAMPLE_RECEIVED_METHOD, 
				IO_SAMPLE);
		
		// Verify that the listener callback was executed one time.
		Mockito.verify(receiveIOSampleListener, Mockito.times(1)).ioSampleReceived(IO_SAMPLE);
		
		assertEquals(IO_SAMPLE, receiveIOSampleListener.getIOSample());
	}
	
	/**
	 * Verify that, when subscribed to receive IO samples and an RX16IO packet is received, 
	 * the callback of the listener is executed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIOSampleReceiveSubscribedRx64IO() throws Exception {
		// Subscribe to listen for IO samples.
		dataReader.addIOSampleReceiveListener(receiveIOSampleListener);
		
		// Fire the private packetReceived method of the dataReader with an RX64IOPacket.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rx64ioPacket);
		
		// Verify that the notifyIOSampleReceived private method was called with the correct IOSample.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_IO_SAMPLE_RECEIVED_METHOD, 
				IO_SAMPLE);
		
		// Verify that the listener callback was executed one time.
		Mockito.verify(receiveIOSampleListener, Mockito.times(1)).ioSampleReceived(IO_SAMPLE);
		
		assertEquals(IO_SAMPLE, receiveIOSampleListener.getIOSample());
	}

	
	/**
	 * Verify that, when subscribed to receive IO samples and a packet that does not correspond to IO sample, 
	 * the callback of the listener is not executed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIOSampleReceiveSubscribedInvalid() throws Exception {
		// Subscribe to listen for IO samples.
		dataReader.addIOSampleReceiveListener(receiveIOSampleListener);
		
		// Fire the private packetReceived method of the dataReader with an invalid packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, invalidPacket);
		
		// Verify that the notifyIOSampleReceived private method was not called.
		PowerMockito.verifyPrivate(dataReader, Mockito.never()).invoke(NOTIFY_IO_SAMPLE_RECEIVED_METHOD, 
				Mockito.anyObject());
		
		// Verify that the listener callback is not executed
		Mockito.verify(receiveIOSampleListener, Mockito.never()).ioSampleReceived((IOSample)Mockito.any());
		
		assertNull(receiveIOSampleListener.getIOSample());
	}
	
	/**
	 * This method directly notifies the IIOSampleReceiveListener of the dataReader instance that a new 
	 * IO sample has been received. This method intends to replace the original 'notifyIOSampleReceived' 
	 * located within the dataReader object because it generates a thread for each notify process.
	 * 
	 * @param ioSample The IO sample received.
	 */
	private void notifyIOSampleReceivedListeners(IOSample ioSample) {
		@SuppressWarnings("unchecked")
		ArrayList<IIOSampleReceiveListener> ioSampleReceiveListeners = (ArrayList<IIOSampleReceiveListener>)Whitebox.getInternalState(dataReader, "ioSampleReceiveListeners");
		for (IIOSampleReceiveListener listener:ioSampleReceiveListeners) {
			listener.ioSampleReceived(ioSample);
		}
	}
	
	/**
	 * Helper class to test the IIOSampleReceiveListener.
	 */
	private class MyReceiveListener implements IIOSampleReceiveListener {
		
		// Variables.
		private IOSample ioSample = null;
		
		/*
		 * (non-Javadoc)
		 * @see com.digi.xbee.api.listeners.IIOSampleReceiveListener#ioSampleReceived(com.digi.xbee.api.io.IOSample)
		 */
		@Override
		public void ioSampleReceived(IOSample ioSample) {
			this.ioSample = ioSample;
			
		}
		
		/**
		 * Retrieves the IO sample received.
		 * 
		 * @return The IO sample.
		 */
		public IOSample getIOSample() {
			return ioSample;
		}
	}
}

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

import com.digi.xbee.api.RemoteRaw802Device;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.connection.DataReader;
import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.packet.raw.RX16IOPacket;
import com.digi.xbee.api.packet.raw.RX64IOPacket;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DataReader.class})
public class IIOSampleReceiveListener802Test {
	
	// Constants.
	private static final byte[] IO_SAMPLE_PAYLOAD = new byte[] {0x01, 0x00, 0x08, 0x00, 0x00, 0x08};
	private static final IOSample IO_SAMPLE = new IOSample(IO_SAMPLE_PAYLOAD);
	
	private static final XBee64BitAddress XBEE_64BIT_ADDRESS = new XBee64BitAddress("0123456789ABCDEF");
	private static final XBee16BitAddress XBEE_16BIT_ADDRESS = new XBee16BitAddress("0123");
	
	private static final String PACKET_RECEIVED_METHOD = "packetReceived";
	private static final String NOTIFY_IO_SAMPLE_RECEIVED_METHOD = "notifyIOSampleReceived";
	
	// Variables.
	private static XBeeDevice xbeeDevice;
	
	private static RemoteRaw802Device remote802Device;
	
	private MyReceiveListener receiveIOSampleListener;
	
	private static RX16IOPacket rx16ioPacket;
	private static RX64IOPacket rx64ioPacket;
	private static ATCommandResponsePacket invalidPacket;
	
	private DataReader dataReader;
	
	@BeforeClass
	public static void setupOnce() {
		// Mock the RemoteRaw802Device.
		remote802Device = Mockito.mock(RemoteRaw802Device.class);
		Mockito.when(remote802Device.get64BitAddress()).thenReturn(XBEE_64BIT_ADDRESS);
		Mockito.when(remote802Device.get16BitAddress()).thenReturn(XBEE_16BIT_ADDRESS);
		
		// Mock a RX16IO packet.
		rx16ioPacket = Mockito.mock(RX16IOPacket.class);
		Mockito.when(rx16ioPacket.getFrameType()).thenReturn(APIFrameType.RX_IO_16);
		Mockito.when(rx16ioPacket.getIOSample()).thenReturn(IO_SAMPLE);
		Mockito.when(rx16ioPacket.get16bitSourceAddress()).thenReturn(XBEE_16BIT_ADDRESS);
		
		// Mock a RX64IO packet.
		rx64ioPacket = Mockito.mock(RX64IOPacket.class);
		Mockito.when(rx64ioPacket.getFrameType()).thenReturn(APIFrameType.RX_IO_64);
		Mockito.when(rx64ioPacket.getIOSample()).thenReturn(IO_SAMPLE);
		Mockito.when(rx64ioPacket.get64bitSourceAddress()).thenReturn(XBEE_64BIT_ADDRESS);
		
		// Mock an invalid packet.
		invalidPacket = Mockito.mock(ATCommandResponsePacket.class);
		
		// Mock the XBee network.
		XBeeNetwork network = Mockito.mock(XBeeNetwork.class);
		Mockito.when(network.getDevice(Mockito.any(XBee64BitAddress.class))).thenReturn(remote802Device);
		
		// Mock the XBee device.
		xbeeDevice = Mockito.mock(XBeeDevice.class);
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		Mockito.when(xbeeDevice.getNetwork()).thenReturn(network);
	}
	
	@Before
	public void setup() throws Exception {
		// IO sample receive listener.
		receiveIOSampleListener = PowerMockito.spy(new MyReceiveListener());
		
		// Data reader.
		dataReader = PowerMockito.spy(new DataReader(Mockito.mock(IConnectionInterface.class), OperatingMode.UNKNOWN, xbeeDevice));
		// Stub the 'notifyIOSampleReceived' method of the dataReader instance so it directly notifies the 
		// listeners instead of opening a new thread per listener (which is what the real method does). This avoids us 
		// having to wait for the executor to run the threads.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				RemoteXBeeDevice remoteDevice = (RemoteXBeeDevice) args[0];
				IOSample ioSample = (IOSample) args[1];
				notifyIOSampleReceivedListeners(remoteDevice, ioSample);
				return null;
			}
		}).when(dataReader, NOTIFY_IO_SAMPLE_RECEIVED_METHOD, (IOSample) Mockito.any(), (RemoteXBeeDevice) Mockito.any());
	}
	
	/**
	 * Verify that the callback of the IIOSampleReceiveListener interface is executed correctly.
	 */
	@Test
	public void testIOSampleReceiveEvent() {
		receiveIOSampleListener.ioSampleReceived(remote802Device, IO_SAMPLE);
		
		assertEquals(IO_SAMPLE, receiveIOSampleListener.getIOSample());
		assertEquals(remote802Device, receiveIOSampleListener.getRemoteXBeeDevice());
	}
	
	/**
	 * Verify that if the listener is not subscribed to receive IO samples, the callback is not 
	 * executed although an IO sample packet is received.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIOSampleReceiveNotSubscribed() throws Exception {
		// Whenever a new Remote device needs to be instantiated, return the mocked one.
		PowerMockito.whenNew(RemoteXBeeDevice.class).withAnyArguments().thenReturn(remote802Device);
		
		// Fire the private packetReceived method of the dataReader with an IODataSampleRxIndicatorPacket.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rx64ioPacket);
		
		// Verify that the notifyIOSampleReceived private method was called with the correct IOSample and RemoteXBeeDevice.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_IO_SAMPLE_RECEIVED_METHOD, remote802Device, IO_SAMPLE);
		
		// As the receiveIOSampleListener was not subscribed in the ioSampleReceiveListeners of the dataReader object, the 
		// IOSample and the RemoteXBeeDevice of the receiveIOSampleListener should be null.
		assertNull(receiveIOSampleListener.getIOSample());
		assertNull(receiveIOSampleListener.getRemoteXBeeDevice());
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
		
		// Whenever a new Remote device needs to be instantiated, return the mocked one.
		PowerMockito.whenNew(RemoteRaw802Device.class).withAnyArguments().thenReturn(remote802Device);
		
		// Fire the private packetReceived method of the dataReader with an RX16IOPacket.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rx16ioPacket);
		
		// Verify that the notifyIOSampleReceived private method was called with the correct IOSample and RemoteXBeeDevice.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_IO_SAMPLE_RECEIVED_METHOD, remote802Device, IO_SAMPLE);
		
		// Verify that the listener callback was executed one time.
		Mockito.verify(receiveIOSampleListener, Mockito.times(1)).ioSampleReceived(remote802Device, IO_SAMPLE);
		
		assertEquals(IO_SAMPLE, receiveIOSampleListener.getIOSample());
		assertEquals(remote802Device, receiveIOSampleListener.getRemoteXBeeDevice());
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
		
		// Whenever a new Remote device needs to be instantiated, return the mocked one.
		PowerMockito.whenNew(RemoteXBeeDevice.class).withAnyArguments().thenReturn(remote802Device);
		
		// Fire the private packetReceived method of the dataReader with an RX64IOPacket.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rx64ioPacket);
		
		// Verify that the notifyIOSampleReceived private method was called with the correct IOSample and RemoteXBeeDevice.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_IO_SAMPLE_RECEIVED_METHOD, remote802Device, IO_SAMPLE);
		
		// Verify that the listener callback was executed one time.
		Mockito.verify(receiveIOSampleListener, Mockito.times(1)).ioSampleReceived(remote802Device, IO_SAMPLE);
		
		assertEquals(IO_SAMPLE, receiveIOSampleListener.getIOSample());
		assertEquals(remote802Device, receiveIOSampleListener.getRemoteXBeeDevice());
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
		
		// Whenever a new Remote device needs to be instantiated, return the mocked one.
		PowerMockito.whenNew(RemoteXBeeDevice.class).withAnyArguments().thenReturn(remote802Device);
		
		// Fire the private packetReceived method of the dataReader with an invalid packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, invalidPacket);
		
		// Verify that the notifyIOSampleReceived private method was not called.
		PowerMockito.verifyPrivate(dataReader, Mockito.never()).invoke(NOTIFY_IO_SAMPLE_RECEIVED_METHOD, Mockito.anyObject(), Mockito.anyObject());
		
		// Verify that the listener callback is not executed
		Mockito.verify(receiveIOSampleListener, Mockito.never()).ioSampleReceived((RemoteXBeeDevice) Mockito.any(), (IOSample) Mockito.any());
		
		assertNull(receiveIOSampleListener.getIOSample());
		assertNull(receiveIOSampleListener.getRemoteXBeeDevice());
	}
	
	/**
	 * This method directly notifies the IIOSampleReceiveListener of the dataReader instance that a new 
	 * IO sample has been received. This method intends to replace the original 'notifyIOSampleReceived' 
	 * located within the dataReader object because it generates a thread for each notify process.
	 * 
	 * @param ioSample The IO sample received.
	 */
	private void notifyIOSampleReceivedListeners(RemoteXBeeDevice remoteDevice, IOSample ioSample) {
		@SuppressWarnings("unchecked")
		ArrayList<IIOSampleReceiveListener> ioSampleReceiveListeners = (ArrayList<IIOSampleReceiveListener>) 
				Whitebox.getInternalState(dataReader, "ioSampleReceiveListeners");
		for (IIOSampleReceiveListener listener : ioSampleReceiveListeners) {
			listener.ioSampleReceived(remoteDevice, ioSample);
		}
	}
	
	/**
	 * Helper class to test the IIOSampleReceiveListener.
	 */
	private class MyReceiveListener implements IIOSampleReceiveListener {
		
		// Variables.
		private RemoteXBeeDevice remoteDevice = null;
		private IOSample ioSample = null;
		
		/*
		 * (non-Javadoc)
		 * @see com.digi.xbee.api.listeners.IIOSampleReceiveListener#ioSampleReceived(com.digi.xbee.api.io.IOSample)
		 */
		@Override
		public void ioSampleReceived(RemoteXBeeDevice remoteDevice, IOSample ioSample) {
			this.remoteDevice = remoteDevice;
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
		
		/**
		 * Retrieves the remote XBee device.
		 * 
		 * @return The remote XBee device.
		 */
		public RemoteXBeeDevice getRemoteXBeeDevice() {
			return remoteDevice;
		}
	}
}

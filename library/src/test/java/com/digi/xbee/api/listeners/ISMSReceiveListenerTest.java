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

import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.connection.DataReader;
import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.SMSMessage;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.cellular.RXSMSPacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DataReader.class})
public class ISMSReceiveListenerTest {
	
	// Constants.
	private static final String PHONE = "55512345678";
	private static final String RECEIVED_DATA = "data";
	
	private static final String PACKET_RECEIVED_METHOD = "packetReceived";
	private static final String NOTIFY_DATA_RECEIVED_METHOD = "notifySMSReceived";
	
	// Variables.
	private static XBeeDevice xbeeDevice;
	
	private MyReceiveListener receiveSMSListener;
	
	private static RXSMSPacket rxSMSPacket;
	private static ATCommandResponsePacket invalidPacket;
	
	private DataReader dataReader;
	
	@BeforeClass
	public static void setupOnce() throws Exception {
		// Mock RX IPV4 Packet.
		rxSMSPacket = Mockito.mock(RXSMSPacket.class);
		Mockito.when(rxSMSPacket.getFrameType()).thenReturn(APIFrameType.RX_SMS);
		Mockito.when(rxSMSPacket.getData()).thenReturn(RECEIVED_DATA);
		Mockito.when(rxSMSPacket.getPhoneNumber()).thenReturn(PHONE);
		Mockito.when(rxSMSPacket.isBroadcast()).thenReturn(false);
		
		// Mock an invalid packet.
		invalidPacket = Mockito.mock(ATCommandResponsePacket.class);
		
		// Mock the XBee device.
		xbeeDevice = Mockito.mock(XBeeDevice.class);
	}
	
	@Before
	public void setup() throws Exception {
		// Data receive listener.
		receiveSMSListener = PowerMockito.spy(new MyReceiveListener());
		
		// Data reader.
		dataReader = PowerMockito.spy(new DataReader(Mockito.mock(IConnectionInterface.class), OperatingMode.UNKNOWN, xbeeDevice));
		// Stub the 'notifyDataReceived' method of the dataReader instance so it directly notifies the 
		// listeners instead of opening a new thread per listener (which is what the real method does). This avoids us 
		// having to wait for the executor to run the threads.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				SMSMessage smsMessage = (SMSMessage)args[0];
				notifySMSReceiveListeners(smsMessage);
				return null;
			}
		}).when(dataReader, NOTIFY_DATA_RECEIVED_METHOD, Mockito.any(SMSMessage.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.ISMSReceiveListener#smsReceived(SMSMessage)}.
	 * 
	 * <p>Verify that the SMS received callback of the ISMSReceiveListener interface
	 * is executed correctly when a SMSMessage (originated by an RXSMS frame) is
	 * received.</p>
	 */
	@Test
	public void testBroadcastDataReceiveEvent() {
		// This is the message that should have been created if an RXSMS frame would have been received.
		SMSMessage smsMessage = new SMSMessage(PHONE, RECEIVED_DATA);
		
		receiveSMSListener.smsReceived(smsMessage);
		
		assertEquals(PHONE, receiveSMSListener.getPhone());
		assertEquals(RECEIVED_DATA, receiveSMSListener.getData());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.ISMSReceiveListener#smsReceived(SMSMessage)} and
	 * {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that if the listener is not subscribed to receive SMS, the callback 
	 * is not executed although an SMS packet is received.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataReceiveNotSubscribed() throws Exception {
		// Fire the private packetReceived method of the dataReader with an RXSMS packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rxSMSPacket);
		
		// Verify that the notifySMSReceived private method was called.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_DATA_RECEIVED_METHOD, 
				Mockito.any(SMSMessage.class));
		
		// As the receiveSMSListener was not subscribed in the smsReceiveListeners of 
		// the dataReader object, the phone number and text of the receiveSMSListener should be null.
		assertNull(receiveSMSListener.getPhone());
		assertNull(receiveSMSListener.getData());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.ISMSReceiveListener#smsReceived(SMSMessage)} and
	 * {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that, when subscribed to receive SMS and an RXSMS packet is received,
	 * the callback of the listener is executed.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataReceiveSubscribedReceive() throws Exception {
		// Subscribe to listen for SMS.
		dataReader.addSMSReceiveListener(receiveSMSListener);
		
		// Fire the private packetReceived method of the dataReader with an RXSMS packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, rxSMSPacket);
		
		// Verify that the notifySMSReceived private method was called.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_DATA_RECEIVED_METHOD, 
				Mockito.any(SMSMessage.class));
		
		// Verify that the smsReceived method of the listener was executed one time.
		Mockito.verify(receiveSMSListener, Mockito.times(1)).smsReceived(Mockito.any(SMSMessage.class));
		
		// All the parameters of our listener should be correct.
		assertEquals(PHONE, receiveSMSListener.getPhone());
		assertEquals(RECEIVED_DATA, receiveSMSListener.getData());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.ISMSReceiveListener#smsReceived(SMSMessage)} and
	 * {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that, when subscribed to receive SMS and a packet that does not 
	 * correspond to SMS is received, the callback of the listener is not executed.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataReceiveSubscribedInvalid() throws Exception {
		// Subscribe to listen for SMS.
		dataReader.addSMSReceiveListener(receiveSMSListener);
		
		// Fire the private packetReceived method of the dataReader with an invalid packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, invalidPacket);
		
		// Verify that the notifySMSReceived private method was not called.
		PowerMockito.verifyPrivate(dataReader, Mockito.never()).invoke(NOTIFY_DATA_RECEIVED_METHOD, 
				Mockito.any(SMSMessage.class));
		
		// Verify that the callback of the listener was not executed
		Mockito.verify(receiveSMSListener, Mockito.never()).smsReceived(Mockito.any(SMSMessage.class));
		
		// All the parameters of our listener should be empty.
		assertNull(receiveSMSListener.getPhone());
		assertNull(receiveSMSListener.getData());
	}
	
	/**
	 * This method directly notifies the ISMSReceiveListener of the dataReader instance 
	 * that new a SMS has been received. This method intends to replace the original 
	 * 'notifySMSReceived' located within the dataReader object because it generates a 
	 * thread for each notify process.
	 * 
	 * @param smsMessage The SMSMessage containing the phone number that sent the SMS and the 
	 *                   text of the SMS.
	 */
	private void notifySMSReceiveListeners(SMSMessage smsMessage) {
		@SuppressWarnings("unchecked")
		ArrayList<ISMSReceiveListener> smsReceiveListeners = (ArrayList<ISMSReceiveListener>)Whitebox.getInternalState(dataReader, "smsReceiveListeners");
		for (ISMSReceiveListener listener:smsReceiveListeners)
			listener.smsReceived(smsMessage);
	}
	
	/**
	 * Helper class to test the ISMSReceiveListener.
	 */
	private class MyReceiveListener implements ISMSReceiveListener {
		
		// Variables.
		private String data = null;
		private String phone = null;
		
		/*
		 * (non-Javadoc)
		 * @see com.digi.xbee.api.listeners.ISMSReceiveListener#smsReceived(com.digi.xbee.api.models.SMSMessage)
		 */
		public void smsReceived(SMSMessage smsMessage) {
			this.phone = smsMessage.getPhoneNumber();
			this.data = smsMessage.getData();
		}
		
		/**
		 * Retrieves the data (text) received.
		 * 
		 * @return The data.
		 */
		public String getData() {
			return data;
		}
		
		/**
		 * Retrieves the phone number that sent the SMS.
		 * 
		 * @return The phone number.
		 */
		public String getPhone() {
			return phone;
		}
	}
}

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
import com.digi.xbee.api.models.ModemStatusEvent;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.packet.common.ModemStatusPacket;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DataReader.class})
public class IModemStatusReceiveListenerTest {
	
	// Constants.
	private static final ModemStatusEvent MODEM_STATUS_EVENT = ModemStatusEvent.STATUS_JOINED_NETWORK;
	
	private static final String PACKET_RECEIVED_METHOD = "packetReceived";
	private static final String NOTIFY_MODEM_STATUS_RECEIVED_METHOD = "notifyModemStatusReceived";
	
	// Variables.
	private static XBeeDevice xbeeDevice;
	
	private MyModemStatusReceiveListener receiveModemStatusListener;
	
	private static ModemStatusPacket modemStatusPacket;
	private static ATCommandResponsePacket invalidPacket;
	
	private DataReader dataReader;
	
	@BeforeClass
	public static void setupOnce() {
		// Mock a ModemStatusPacket packet.
		modemStatusPacket = new ModemStatusPacket(MODEM_STATUS_EVENT);
		
		// Mock an invalid packet.
		invalidPacket = Mockito.mock(ATCommandResponsePacket.class);
		
		// Mock the XBee device.
		xbeeDevice = Mockito.mock(XBeeDevice.class);
	}
	
	@Before
	public void setup() throws Exception {
		// Modem Status receive listener.
		receiveModemStatusListener = PowerMockito.spy(new MyModemStatusReceiveListener());
		
		// Data reader.
		dataReader = PowerMockito.spy(new DataReader(Mockito.mock(IConnectionInterface.class), OperatingMode.UNKNOWN, xbeeDevice));
		// Stub the 'notifyModemStatusReceived' method of the dataReader instance so it directly notifies the 
		// listeners instead of opening a new thread per listener (which is what the real method does). This avoids us 
		// having to wait for the executor to run the threads.
		PowerMockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				ModemStatusEvent modemStatusEvent = (ModemStatusEvent) args[0];
				notifyModemStatusListeners(modemStatusEvent);
				return null;
			}
		}).when(dataReader, NOTIFY_MODEM_STATUS_RECEIVED_METHOD, Mockito.any(ModemStatusEvent.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IModemStatusReceiveListener#modemStatusEventReceived(ModemStatusEvent)}.
	 * 
	 * Verify that the callback of the IModemStatusReceiveListener interface is executed correctly.
	 */
	@Test
	public void testModemStausReceiveEvent() {
		receiveModemStatusListener.modemStatusEventReceived(MODEM_STATUS_EVENT);
		
		assertEquals(MODEM_STATUS_EVENT, receiveModemStatusListener.getModemStatus());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IModemStatusReceiveListener#modemStatusEventReceived(ModemStatusEvent)} 
	 * and {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that if the listener is not subscribed to receive Modem Status events, the callback is not 
	 * executed although a Modem Status packet is received.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testModemStatusReceiveNotSubscribed() throws Exception {
		// Fire the private packetReceived method of the dataReader with a ModemStatusPacket.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, modemStatusPacket);
		
		// Verify that the notifyModemStatusReceived private method was called with the correct Modem Status event.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_MODEM_STATUS_RECEIVED_METHOD, MODEM_STATUS_EVENT);
		
		// As the receiveModemStatusListener was not subscribed in the modemStatusListeners of the dataReader object, the 
		// Modem Status of the receiveModemStatusListener should be null.
		assertNull(receiveModemStatusListener.getModemStatus());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IModemStatusReceiveListener#modemStatusEventReceived(ModemStatusEvent)} 
	 * and {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that, when subscribed to receive Modem Status events and a ModemStatusPacket is received, 
	 * the callback of the listener is executed.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testModemStatusReceiveSubscribed() throws Exception {
		// Subscribe to listen for Modem Status events.
		dataReader.addModemStatusReceiveListener(receiveModemStatusListener);
		
		// Fire the private packetReceived method of the dataReader with a ModemStatusPacket.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, modemStatusPacket);
		
		// Verify that the notifyModemStatusReceived private method was called with the correct Modem Status event.
		PowerMockito.verifyPrivate(dataReader, Mockito.times(1)).invoke(NOTIFY_MODEM_STATUS_RECEIVED_METHOD, MODEM_STATUS_EVENT);
		
		// Verify that the listener callback was executed one time.
		Mockito.verify(receiveModemStatusListener, Mockito.times(1)).modemStatusEventReceived(Mockito.any(ModemStatusEvent.class));
		
		assertEquals(MODEM_STATUS_EVENT, receiveModemStatusListener.getModemStatus());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.listeners.IModemStatusReceiveListener#modemStatusEventReceived(ModemStatusEvent)} 
	 * and {@link com.digi.xbee.api.connection.DataReader#packetReceived(XBeePacket)}.
	 * 
	 * <p>Verify that, when subscribed to receive Modem Status events and a packet that does not correspond to a Modem 
	 * Status event is received, the callback of the listener is not executed.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testModemStatusReceiveSubscribedInvalid() throws Exception {
		// Subscribe to listen for Modem Status events.
		dataReader.addModemStatusReceiveListener(receiveModemStatusListener);
		
		// Fire the private packetReceived method of the dataReader with an invalid packet.
		Whitebox.invokeMethod(dataReader, PACKET_RECEIVED_METHOD, invalidPacket);
		
		// Verify that the notifyModemStatusReceived private method was not called.
		PowerMockito.verifyPrivate(dataReader, Mockito.never()).invoke(NOTIFY_MODEM_STATUS_RECEIVED_METHOD, Mockito.any(ModemStatusEvent.class));
		
		// Verify that the listener callback is not executed
		Mockito.verify(receiveModemStatusListener, Mockito.never()).modemStatusEventReceived(Mockito.any(ModemStatusEvent.class));
		
		assertNull(receiveModemStatusListener.getModemStatus());
	}
	
	/**
	 * This method directly notifies the IModemStatusReceiveListener of the dataReader instance that a new 
	 * Modem Status event has been received. This method intends to replace the original 'notifyModemStatusReceived' 
	 * located within the dataReader object because it generates a thread for each notify process.
	 * 
	 * @param modemStatusEvent The Modem Status event received.
	 */
	private void notifyModemStatusListeners(ModemStatusEvent modemStatusEvent) {
		@SuppressWarnings("unchecked")
		ArrayList<IModemStatusReceiveListener> modemStatusListeners = (ArrayList<IModemStatusReceiveListener>)Whitebox.getInternalState(dataReader, "modemStatusListeners");
		for (IModemStatusReceiveListener listener : modemStatusListeners)
			listener.modemStatusEventReceived(modemStatusEvent);
	}
	
	/**
	 * Helper class to test the IModemStatusReceiveListener.
	 */
	private class MyModemStatusReceiveListener implements IModemStatusReceiveListener {
		
		// Variables.
		private ModemStatusEvent modemStatusEvent = null;
		
		@Override
		public void modemStatusEventReceived(ModemStatusEvent modemStatusEvent) {
			this.modemStatusEvent = modemStatusEvent;
		}
		
		/**
		 * Retrieves the {@code ModemStatusEvent} received.
		 * 
		 * @return The modem status event.
		 */
		public ModemStatusEvent getModemStatus() {
			return modemStatusEvent;
		}
	}
}

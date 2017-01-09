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
package com.digi.xbee.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.TransmitException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.packet.cellular.TXSMSPacket;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CellularDevice.class})
public class SendSMSTest {
	
	// Constants.
	private static final String PHONE = "+155512345678";
	private static final String DATA = "This is the text";
	
	// Variables.
	private CellularDevice cellularDevice;
	
	private TXSMSPacket smsPacket;
	
	@Before
	public void setup() throws Exception {
		// Instantiate a CellularDevice object with a mocked interface.
		cellularDevice = PowerMockito.spy(new CellularDevice(Mockito.mock(SerialPortRxTx.class)));
		
		// Mock Transmit Request packet.
		smsPacket = Mockito.mock(TXSMSPacket.class);
		
		// Whenever a TXSMSPacket class is instantiated, the mocked smsPacket packet should be returned.
		PowerMockito.whenNew(TXSMSPacket.class).withAnyArguments().thenReturn(smsPacket);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#sendSMS(String, String)}.
	 * 
	 * <p>Verify that SMS cannot be sent if the phone number is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendSMSPhoneNull() throws TimeoutException, XBeeException {
		cellularDevice.sendSMS(null, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#sendSMS(String, String)}.
	 * 
	 * <p>Verify that SMS cannot be sent if the data is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendSMSDataNull() throws TimeoutException, XBeeException {
		cellularDevice.sendSMS(PHONE, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#sendSMS(String, String)}.
	 * 
	 * <p>Verify that SMS cannot be sent if the sender is a remote XBee device.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testSendSMSFromRemoteDevices() throws TimeoutException, XBeeException {
		// Return that the XBee device is remote when asked.
		Mockito.when(cellularDevice.isRemote()).thenReturn(true);
		
		cellularDevice.sendSMS(PHONE, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#sendSMS(String, String)}.
	 * 
	 * <p>Verify that SMS cannot be sent if the device is not open.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSendSMSConnectionClosed() throws TimeoutException, XBeeException {
		// Throw an Interface not open exception when sending and checking any SMS packet.
		Mockito.doThrow(new InterfaceNotOpenException()).when(cellularDevice).sendAndCheckXBeePacket(Mockito.any(TXSMSPacket.class), Mockito.eq(false));
		
		cellularDevice.sendSMS(PHONE, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#sendSMS(String, String)}.
	 * 
	 * <p>Verify that SMS cannot be sent if the device has an invalid operating mode.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSendSMSInvalidOperatingMode() throws TimeoutException, XBeeException {
		// Throw an invalid operating mode exception when sending and checking any SMS packet.
		Mockito.doThrow(new InvalidOperatingModeException()).when(cellularDevice).sendAndCheckXBeePacket(Mockito.any(TXSMSPacket.class), Mockito.eq(false));
		
		cellularDevice.sendSMS(PHONE, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#sendSMS(String, String)}.
	 * 
	 * <p>Verify that SMS cannot be sent if there is a timeout sending and checking the SMS packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TimeoutException.class)
	public void testSendSMSTimeout() throws TimeoutException, XBeeException {
		// Throw a timeout exception when sending and checking any SMS packet.
		Mockito.doThrow(new TimeoutException()).when(cellularDevice).sendAndCheckXBeePacket(Mockito.any(TXSMSPacket.class), Mockito.eq(false));
		
		cellularDevice.sendSMS(PHONE, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#sendSMS(String, String)}.
	 * 
	 * <p>Verify that SMS cannot be sent if there is a transmit exception when sending and checking the SMS packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TransmitException.class)
	public void testSendSMSTransmitException() throws TimeoutException, XBeeException {
		// Throw a transmit exception when sending and checking any SMS packet.
		Mockito.doThrow(new TransmitException(null)).when(cellularDevice).sendAndCheckXBeePacket(Mockito.any(TXSMSPacket.class), Mockito.eq(false));
		
		cellularDevice.sendSMS(PHONE, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#sendSMS(String, String)}.
	 * 
	 * <p>Verify that SMS cannot be sent if there is an IO error when sending and checking the SMS packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=XBeeException.class)
	public void testSendSMSIOException() throws TimeoutException, XBeeException {
		// Throw an XBee exception when sending and checking any SMS packet.
		Mockito.doThrow(new XBeeException()).when(cellularDevice).sendAndCheckXBeePacket(Mockito.any(TXSMSPacket.class), Mockito.eq(false));
		
		cellularDevice.sendSMS(PHONE, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#sendSMS(String, String)}.
	 * 
	 * <p>Verify that SMS is sent successfully if there is not any error when sending and checking the SMS packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testSendSMSSuccess() throws TimeoutException, XBeeException {
		// Do nothing when sending and checking any SMS packet.
		Mockito.doNothing().when(cellularDevice).sendAndCheckXBeePacket(Mockito.any(TXSMSPacket.class), Mockito.eq(false));
		
		cellularDevice.sendSMS(PHONE, DATA);
		
		// Verify the sendAndCheckXBeePacket(XBeePacket, boolean) method was called.
		Mockito.verify(cellularDevice, Mockito.times(1)).sendAndCheckXBeePacket(Mockito.eq(smsPacket), Mockito.eq(false));
	}
}

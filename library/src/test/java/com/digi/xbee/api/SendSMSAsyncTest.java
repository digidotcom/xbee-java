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
public class SendSMSAsyncTest {
	
	// Constants.
	private static final String PHONE = "+155512345678";
	private static final String DATA = "This is the text";
	
	// Variables.
	private CellularDevice cellularDevice;
	
	private TXSMSPacket smsPacket;
	
	@Before
	public void setup() throws Exception {
		// Instantiate an CellularDevice object with a mocked interface.
		cellularDevice = PowerMockito.spy(new CellularDevice(Mockito.mock(SerialPortRxTx.class)));
		
		// Mock SMS packet.
		smsPacket = Mockito.mock(TXSMSPacket.class);
		
		// Whenever a TXSMSPacket class is instantiated, the mocked smsPacket packet should be returned.
		PowerMockito.whenNew(TXSMSPacket.class).withAnyArguments().thenReturn(smsPacket);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#sendSMSAsync(String, String)}.
	 * 
	 * <p>Verify that async. SMS cannot be sent if the phone number is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendSMSAsync64BitAddrNull() throws TimeoutException, XBeeException {
		cellularDevice.sendSMSAsync(null, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#sendSMSAsync(String, String)}.
	 * 
	 * <p>Verify that async. SMS cannot be sent if the data is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendSMSAsyncDataNull() throws TimeoutException, XBeeException {
		cellularDevice.sendSMSAsync(PHONE, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#sendSMSAsync(String, String)}.
	 * 
	 * <p>Verify that async. SMS cannot be sent if the sender is a remote XBee device.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testSendSMSAsyncFromRemoteDevices() throws TimeoutException, XBeeException {
		// Return that the XBee device is remote when asked.
		Mockito.when(cellularDevice.isRemote()).thenReturn(true);
		
		cellularDevice.sendSMSAsync(PHONE, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#sendSMSAsync(String, String)}.
	 * 
	 * <p>Verify that async. SMS cannot be sent if the device is not open.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSendSMSAsyncConnectionClosed() throws TimeoutException, XBeeException {
		// Throw an Interface not open exception when sending and checking any SMS packet.
		Mockito.doThrow(new InterfaceNotOpenException()).when(cellularDevice).sendAndCheckXBeePacket(Mockito.any(TXSMSPacket.class), Mockito.eq(true));
		
		cellularDevice.sendSMSAsync(PHONE, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#sendSMSAsync(String, String)}.
	 * 
	 * <p>Verify that async. SMS cannot be sent if the device has an invalid operating mode.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSendSMSAsyncInvalidOperatingMode() throws TimeoutException, XBeeException {
		// Throw an invalid operating mode exception when sending and checking any SMS packet.
		Mockito.doThrow(new InvalidOperatingModeException()).when(cellularDevice).sendAndCheckXBeePacket(Mockito.any(TXSMSPacket.class), Mockito.eq(true));
		
		cellularDevice.sendSMSAsync(PHONE, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#sendSMSAsync(String, String)}.
	 * 
	 * <p>Verify that async. SMS cannot be sent if there is a timeout sending and checking the SMS packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TimeoutException.class)
	public void testSendSMSAsyncTimeout() throws TimeoutException, XBeeException {
		// Throw a timeout exception when sending and checking any SMS packet.
		Mockito.doThrow(new TimeoutException()).when(cellularDevice).sendAndCheckXBeePacket(Mockito.any(TXSMSPacket.class), Mockito.eq(true));
		
		cellularDevice.sendSMSAsync(PHONE, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#sendSMSAsync(String, String)}.
	 * 
	 * <p>Verify that async. SMS cannot be sent if there is a transmit exception when sending and checking the SMS packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TransmitException.class)
	public void testSendSMSAsyncTransmitException() throws TimeoutException, XBeeException {
		// Throw a transmit exception when sending and checking any SMS packet.
		Mockito.doThrow(new TransmitException(null)).when(cellularDevice).sendAndCheckXBeePacket(Mockito.any(TXSMSPacket.class), Mockito.eq(true));
		
		cellularDevice.sendSMSAsync(PHONE, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#sendSMSAsync(String, String)}.
	 * 
	 * <p>Verify that async. SMS cannot be sent if there is an IO error when sending and checking the SMS packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=XBeeException.class)
	public void testSendSMSAsyncIOException() throws TimeoutException, XBeeException {
		// Throw an XBee exception when sending and checking any SMS packet.
		Mockito.doThrow(new XBeeException()).when(cellularDevice).sendAndCheckXBeePacket(Mockito.any(TXSMSPacket.class), Mockito.eq(true));
		
		cellularDevice.sendSMSAsync(PHONE, DATA);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.CellularDevice#sendSMSAsync(String, String)}.
	 * 
	 * <p>Verify that async. SMS is sent successfully if there is not any error when sending and checking the SMS packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testSendSMSAsyncSuccess() throws TimeoutException, XBeeException {
		// Do nothing when sending and checking any SMS packet.
		Mockito.doNothing().when(cellularDevice).sendAndCheckXBeePacket(Mockito.any(TXSMSPacket.class), Mockito.eq(true));
		
		cellularDevice.sendSMSAsync(PHONE, DATA);
		
		// Verify the sendAndCheckXBeePacket(XBeePacket, boolean) method was called.
		Mockito.verify(cellularDevice, Mockito.times(1)).sendAndCheckXBeePacket(Mockito.eq(smsPacket), Mockito.eq(true));
	}
}

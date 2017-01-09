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
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.packet.raw.TX16Packet;
import com.digi.xbee.api.packet.raw.TX64Packet;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBeeDevice.class, Raw802Device.class})
public class SendDataHighLevel802Test {
	
	// Constants.
	private static final XBee64BitAddress XBEE_64BIT_ADDRESS = new XBee64BitAddress("0123456789ABCDEF");
	private static final XBee16BitAddress XBEE_16BIT_ADDRESS = new XBee16BitAddress("0123");
	
	private static final String DATA = "data";
	
	// Variables.
	private Raw802Device raw802Device;
	
	private TX16Packet tx16Packet;
	private TX64Packet tx64Packet;
	
	@Before
	public void setup() throws Exception {
		// Instantiate a Raw802Device object with a mocked interface.
		raw802Device = PowerMockito.spy(new Raw802Device(Mockito.mock(SerialPortRxTx.class)));
		
		// Mock Tx16 packet.
		tx16Packet = Mockito.mock(TX16Packet.class);
		// Mock Tx64 packet.
		tx64Packet = Mockito.mock(TX64Packet.class);
		
		// Whenever a TX16Packet class is instantiated, the mocked tx16Packet packet should be returned.
		PowerMockito.whenNew(TX16Packet.class).withAnyArguments().thenReturn(tx16Packet);
		// Whenever a TX64Packet class is instantiated, the mocked txPacket packet should be returned.
		PowerMockito.whenNew(TX64Packet.class).withAnyArguments().thenReturn(tx64Packet);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#sendData(XBee16BitAddress, byte[])}.
	 * 
	 * <p>Verify that data cannot be sent to a 16-bit address if the 16-Bit address is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendData16BitAddrNull() throws TimeoutException, XBeeException {
		raw802Device.sendData((XBee16BitAddress)null, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#sendData(XBee16BitAddress, byte[])}.
	 * 
	 * <p>Verify that data cannot be sent to a 16-bit address if the data is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendData16BitAddrDataNull() throws TimeoutException, XBeeException {
		raw802Device.sendData(XBEE_16BIT_ADDRESS, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#sendData(XBee16BitAddress, byte[])}.
	 * 
	 * <p>Verify that data cannot be sent to a 16-bit address if the sender is a remote 802.15.4 device.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testSendData16BitAddrFromRemoteDevices() throws TimeoutException, XBeeException {
		// Return that the XBee device is remote when asked.
		Mockito.when(raw802Device.isRemote()).thenReturn(true);
		
		raw802Device.sendData(XBEE_16BIT_ADDRESS, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#sendData(XBee16BitAddress, byte[])}.
	 * 
	 * <p>Verify that data cannot be sent to a 16-bit address if the device is not open.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSendData16BitAddrConnectionClosed() throws TimeoutException, XBeeException {
		// Throw an Interface not open exception when sending and checking any TX16Packet packet.
		Mockito.doThrow(new InterfaceNotOpenException()).when(raw802Device).sendAndCheckXBeePacket(Mockito.any(TX16Packet.class), Mockito.eq(false));
		
		raw802Device.sendData(XBEE_16BIT_ADDRESS, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#sendData(XBee16BitAddress, byte[])}.
	 * 
	 * <p>Verify that data cannot be sent to a 16-bit address if and the device has an 
	 * invalid operating mode.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSendData16BitAddrInvalidOperatingMode() throws TimeoutException, XBeeException {
		// Throw an invalid operating mode exception when sending and checking any TX16Packet packet.
		Mockito.doThrow(new InvalidOperatingModeException()).when(raw802Device).sendAndCheckXBeePacket(Mockito.any(TX16Packet.class), Mockito.eq(false));
		
		raw802Device.sendData(XBEE_16BIT_ADDRESS, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#sendData(XBee16BitAddress, byte[])}.
	 * 
	 * <p>Verify that data cannot be sent to a 16-bit address if and there is a timeout 
	 * sending and checking the TX16Packet packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TimeoutException.class)
	public void testSendData16BitAddrTimeout() throws TimeoutException, XBeeException {
		// Throw a timeout exception when sending and checking any TX16Packet packet.
		Mockito.doThrow(new TimeoutException()).when(raw802Device).sendAndCheckXBeePacket(Mockito.any(TX16Packet.class), Mockito.eq(false));
		
		raw802Device.sendData(XBEE_16BIT_ADDRESS, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#sendData(XBee16BitAddress, byte[])}.
	 * 
	 * <p>Verify that data cannot be sent to a 16-bit address if and there is a transmit 
	 * exception when sending and checking the TX16Packet packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TransmitException.class)
	public void testSendData16BitAddrTransmitException() throws TimeoutException, XBeeException {
		// Throw a transmit exception when sending and checking any TX16Packet packet.
		Mockito.doThrow(new TransmitException(null)).when(raw802Device).sendAndCheckXBeePacket(Mockito.any(TX16Packet.class), Mockito.eq(false));
		
		raw802Device.sendData(XBEE_16BIT_ADDRESS, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#sendData(XBee16BitAddress, byte[])}.
	 * 
	 * <p>Verify that data cannot be sent to a 16-bit address if there is an IO error 
	 * when sending and checking the TX16Packet packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=XBeeException.class)
	public void testSendData16BitAddrIOException() throws TimeoutException, XBeeException {
		// Throw an XBee exception when sending and checking any TX16Packet packet.
		Mockito.doThrow(new XBeeException()).when(raw802Device).sendAndCheckXBeePacket(Mockito.any(TX16Packet.class), Mockito.eq(false));
		
		raw802Device.sendData(XBEE_16BIT_ADDRESS, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#sendData(XBee16BitAddress, byte[])}.
	 * 
	 * <p>Verify that data is sent successfully to a 16-bit address if and there is not 
	 * any error when sending and checking the TX16Packet packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testSendData16BitAddrSuccess() throws TimeoutException, XBeeException {
		// Do nothing when sending and checking any TX16Packet packet.
		Mockito.doNothing().when(raw802Device).sendAndCheckXBeePacket(Mockito.any(TX16Packet.class), Mockito.eq(false));
		
		raw802Device.sendData(XBEE_16BIT_ADDRESS, DATA.getBytes());
		
		// Verify the sendAndCheckXBeePacket(XBeePacket, boolean) method was called.
		Mockito.verify(raw802Device, Mockito.times(1)).sendAndCheckXBeePacket(Mockito.eq(tx16Packet), Mockito.eq(false));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#sendData(XBee64BitAddress, byte[])}.
	 * 
	 * <p>Verify that the super com.digi.xbee.api.XBeeDevice#sendData(XBee64BitAddress, byte[]) method is 
	 * called when executing the com.digi.xbee.api.Raw802Device#sendData(XBee64BitAddress, byte[]) method.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testSendData64BitAddrSuccess() throws TimeoutException, XBeeException {
		// Do nothing when sending and checking any TX64Packet packet.
		Mockito.doNothing().when(raw802Device).sendAndCheckXBeePacket(Mockito.any(TX64Packet.class), Mockito.eq(false));
		
		raw802Device.sendData(XBEE_64BIT_ADDRESS, DATA.getBytes());
		
		// Verify the sendAndCheckXBeePacket(XBeePacket, boolean) method was called. If this method is called it means that 
		// the super() one was necessary called.
		Mockito.verify(raw802Device, Mockito.times(1)).sendAndCheckXBeePacket(Mockito.eq(tx64Packet), Mockito.eq(false));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#sendDataAsync(XBee16BitAddress, byte[])}.
	 * 
	 * <p>Verify that async. data cannot be sent to a 16-bit address if the 16-Bit address is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendDataAsync16BitAddrNull() throws TimeoutException, XBeeException {
		raw802Device.sendDataAsync((XBee16BitAddress)null, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#sendDataAsync(XBee16BitAddress, byte[])}.
	 * 
	 * <p>Verify that async. data cannot be sent to a 16-bit address if the data is {@code null}.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendDataAsync16BitAddrDataNull() throws TimeoutException, XBeeException {
		raw802Device.sendDataAsync(XBEE_16BIT_ADDRESS, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#sendDataAsync(XBee16BitAddress, byte[])}.
	 * 
	 * <p>Verify that async. data cannot be sent to a 16-bit address if the sender is a remote 802.15.4 device.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testSendDataAsync16BitAddrFromRemoteDevices() throws TimeoutException, XBeeException {
		// Return that the XBee device is remote when asked.
		Mockito.when(raw802Device.isRemote()).thenReturn(true);
		
		raw802Device.sendDataAsync(XBEE_16BIT_ADDRESS, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#sendDataAsync(XBee16BitAddress, byte[])}.
	 * 
	 * <p>Verify that async. data cannot be sent to a 16-bit address if the device is not open.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSendDataAsync16BitAddrConnectionClosed() throws TimeoutException, XBeeException {
		// Throw an Interface not open exception when sending and checking any TX16Packet packet.
		Mockito.doThrow(new InterfaceNotOpenException()).when(raw802Device).sendAndCheckXBeePacket(Mockito.any(TX16Packet.class), Mockito.eq(true));
		
		raw802Device.sendDataAsync(XBEE_16BIT_ADDRESS, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#sendDataAsync(XBee16BitAddress, byte[])}.
	 * 
	 * <p>Verify that async. data cannot be sent to a 16-bit address if and the device has an 
	 * invalid operating mode.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSendDataAsync16BitAddrInvalidOperatingMode() throws TimeoutException, XBeeException {
		// Throw an invalid operating mode exception when sending and checking any TX16Packet packet.
		Mockito.doThrow(new InvalidOperatingModeException()).when(raw802Device).sendAndCheckXBeePacket(Mockito.any(TX16Packet.class), Mockito.eq(true));
		
		raw802Device.sendDataAsync(XBEE_16BIT_ADDRESS, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#sendDataAsync(XBee16BitAddress, byte[])}.
	 * 
	 * <p>Verify that async. data cannot be sent to a 16-bit address if and there is a timeout 
	 * sending and checking the TX16Packet packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TimeoutException.class)
	public void testSendDataAsync16BitAddrTimeout() throws TimeoutException, XBeeException {
		// Throw a timeout exception when sending and checking any TX16Packet packet.
		Mockito.doThrow(new TimeoutException()).when(raw802Device).sendAndCheckXBeePacket(Mockito.any(TX16Packet.class), Mockito.eq(true));
		
		raw802Device.sendDataAsync(XBEE_16BIT_ADDRESS, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#sendDataAsync(XBee16BitAddress, byte[])}.
	 * 
	 * <p>Verify that async. data cannot be sent to a 16-bit address if and there is a transmit 
	 * exception when sending and checking the TX16Packet packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=TransmitException.class)
	public void testSendDataAsync16BitAddrTransmitException() throws TimeoutException, XBeeException {
		// Throw a transmit exception when sending and checking any TX16Packet packet.
		Mockito.doThrow(new TransmitException(null)).when(raw802Device).sendAndCheckXBeePacket(Mockito.any(TX16Packet.class), Mockito.eq(true));
		
		raw802Device.sendDataAsync(XBEE_16BIT_ADDRESS, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#sendDataAsync(XBee16BitAddress, byte[])}.
	 * 
	 * <p>Verify that async. data cannot be sent to a 16-bit address if there is an IO error 
	 * when sending and checking the TX16Packet packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test(expected=XBeeException.class)
	public void testSendDataAsync16BitAddrIOException() throws TimeoutException, XBeeException {
		// Throw an XBee exception when sending and checking any TX16Packet packet.
		Mockito.doThrow(new XBeeException()).when(raw802Device).sendAndCheckXBeePacket(Mockito.any(TX16Packet.class), Mockito.eq(true));
		
		raw802Device.sendDataAsync(XBEE_16BIT_ADDRESS, DATA.getBytes());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#sendDataAsync(XBee16BitAddress, byte[])}.
	 * 
	 * <p>Verify that async. data is sent successfully to a 16-bit address if and there is not 
	 * any error when sending and checking the TX16Packet packet.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testSendDataAsync16BitAddrSuccess() throws TimeoutException, XBeeException {
		// Do nothing when sending and checking any TX16Packet packet.
		Mockito.doNothing().when(raw802Device).sendAndCheckXBeePacket(Mockito.any(TX16Packet.class), Mockito.eq(true));
		
		raw802Device.sendDataAsync(XBEE_16BIT_ADDRESS, DATA.getBytes());
		
		// Verify the sendAndCheckXBeePacket(XBeePacket, boolean) method was called.
		Mockito.verify(raw802Device, Mockito.times(1)).sendAndCheckXBeePacket(Mockito.eq(tx16Packet), Mockito.eq(true));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.Raw802Device#sendDataAsync(XBee64BitAddress, byte[])}.
	 * 
	 * <p>Verify that the super com.digi.xbee.api.XBeeDevice#sendDataAsync(XBee64BitAddress, byte[]) method is 
	 * called when executing the com.digi.xbee.api.Raw802Device#sendDataAsync(XBee64BitAddress, byte[]) method.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testSendDataAsync64BitAddrSuccess() throws TimeoutException, XBeeException {
		// Do nothing when sending and checking any TX64Packet packet.
		Mockito.doNothing().when(raw802Device).sendAndCheckXBeePacket(Mockito.any(TX64Packet.class), Mockito.eq(true));
		
		raw802Device.sendDataAsync(XBEE_64BIT_ADDRESS, DATA.getBytes());
		
		// Verify the sendAndCheckXBeePacket(XBeePacket, boolean) method was called. If this method is called it means that 
		// the super() one was necessary called.
		Mockito.verify(raw802Device, Mockito.times(1)).sendAndCheckXBeePacket(Mockito.eq(tx64Packet), Mockito.eq(true));
	}
}

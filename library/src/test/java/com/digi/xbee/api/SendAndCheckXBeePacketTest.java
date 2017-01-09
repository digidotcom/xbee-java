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

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.TransmitException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeTransmitStatus;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.TransmitPacket;
import com.digi.xbee.api.packet.common.TransmitStatusPacket;
import com.digi.xbee.api.packet.raw.TXStatusPacket;

public class SendAndCheckXBeePacketTest {
	
	// Constants.
	private static final XBee64BitAddress XBEE_64BIT_ADDRESS = new XBee64BitAddress("0123456789ABCDEF");
	
	// Variables.
	private XBeeDevice xbeeDevice;
	private RemoteXBeeDevice mockedRemoteDevice;
	
	private TransmitPacket transmitPacket;
	
	private TransmitStatusPacket transmitStatusSuccess;
	private TransmitStatusPacket transmitStatusError;
	
	private TXStatusPacket txStatusSuccess;
	private TXStatusPacket txStatusError;
	
	@Before
	public void setup() throws Exception {
		// Instantiate an XBeeDevice object with the mocked interface.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(Mockito.mock(SerialPortRxTx.class)));
		
		// Mock Explicit Addressing packet.
		transmitPacket = Mockito.mock(TransmitPacket.class);
		
		// Mock Transmit Status packet SUCCESS.
		transmitStatusSuccess = Mockito.mock(TransmitStatusPacket.class);
		Mockito.when(transmitStatusSuccess.getTransmitStatus()).thenReturn(XBeeTransmitStatus.SUCCESS);
		
		// Mock Transmit Status packet ERROR.
		transmitStatusError = Mockito.mock(TransmitStatusPacket.class);
		Mockito.when(transmitStatusError.getTransmitStatus()).thenReturn(XBeeTransmitStatus.ADDRESS_NOT_FOUND);
		
		// Mock Tx Status packet SUCCESS.
		txStatusSuccess = Mockito.mock(TXStatusPacket.class);
		Mockito.when(transmitStatusSuccess.getTransmitStatus()).thenReturn(XBeeTransmitStatus.SUCCESS);
		
		// Mock TX Status packet ERROR.
		txStatusError = Mockito.mock(TXStatusPacket.class);
		Mockito.when(transmitStatusError.getTransmitStatus()).thenReturn(XBeeTransmitStatus.ADDRESS_NOT_FOUND);
		
		// Mock a RemoteXBeeDevice to be used as parameter in the send explicit data command.
		mockedRemoteDevice = Mockito.mock(RemoteXBeeDevice.class);
		Mockito.when(mockedRemoteDevice.get64BitAddress()).thenReturn(XBEE_64BIT_ADDRESS);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendAndCheckXBeePacket(com.digi.xbee.api.packet.XBeePacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails if the {@code XBeePacket} provided is null and the transmission is 
	 * synchronous.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TransmitException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendAndCheckSyncPacketNull() throws TransmitException, XBeeException {
		xbeeDevice.sendAndCheckXBeePacket(null, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendAndCheckXBeePacket(com.digi.xbee.api.packet.XBeePacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails if the {@code XBeePacket} provided is null and the transmission is 
	 * asynchronous.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TransmitException 
	 */
	@Test(expected=NullPointerException.class)
	public void testSendAndCheckAsyncPacketNull() throws TransmitException, XBeeException {
		xbeeDevice.sendAndCheckXBeePacket(null, true);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendAndCheckXBeePacket(com.digi.xbee.api.packet.XBeePacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails if the connection of the device is closed and the transmission is 
	 * synchronous.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TransmitException 
	 * @throws IOException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSendAndCheckSyncConnectionClosed() throws TransmitException, XBeeException, IOException {
		// Throw an interface not open exception when sending any XBee packet synchronously.
		Mockito.doThrow(new InterfaceNotOpenException()).when(xbeeDevice).sendXBeePacket(Mockito.any(XBeePacket.class));
		
		xbeeDevice.sendAndCheckXBeePacket(transmitPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendAndCheckXBeePacket(com.digi.xbee.api.packet.XBeePacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails if the connection of the device is closed and the transmission is 
	 * asynchronous.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TransmitException 
	 * @throws IOException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSendAndCheckAsyncConnectionClosed() throws TransmitException, XBeeException, IOException {
		// Throw an interface not open exception when sending any XBee packet asynchronously.
		Mockito.doThrow(new InterfaceNotOpenException()).when(xbeeDevice).sendXBeePacketAsync(Mockito.any(XBeePacket.class));
		
		xbeeDevice.sendAndCheckXBeePacket(transmitPacket, true);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendAndCheckXBeePacket(com.digi.xbee.api.packet.XBeePacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails if the operating mode of the device is not valid and the transmission is 
	 * synchronous.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TransmitException 
	 * @throws IOException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSendAndCheckSyncInvalidOperatingMode() throws TransmitException, XBeeException, IOException {
		// Throw an invalid operating mode exception when sending any XBee packet synchronously.
		Mockito.doThrow(new InvalidOperatingModeException()).when(xbeeDevice).sendXBeePacket(Mockito.any(XBeePacket.class));
		
		xbeeDevice.sendAndCheckXBeePacket(transmitPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendAndCheckXBeePacket(com.digi.xbee.api.packet.XBeePacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails if the operating mode of the device is not valid and the transmission is 
	 * asynchronous.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TransmitException 
	 * @throws IOException 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSendAndCheckAsyncInvalidOperatingMode() throws TransmitException, XBeeException, IOException {
		// Throw an invalid operating mode exception when sending any XBee packet asynchronously.
		Mockito.doThrow(new InvalidOperatingModeException()).when(xbeeDevice).sendXBeePacketAsync(Mockito.any(XBeePacket.class));
		
		xbeeDevice.sendAndCheckXBeePacket(transmitPacket, true);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendAndCheckXBeePacket(com.digi.xbee.api.packet.XBeePacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails if there is a timeout sending the packet and the transmission is 
	 * synchronous.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TransmitException 
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testSendAndCheckSyncTimeout() throws TransmitException, XBeeException, IOException {
		// Throw a timeout exception when sending any XBee packet synchronously.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).sendXBeePacket(Mockito.any(XBeePacket.class));
		
		xbeeDevice.sendAndCheckXBeePacket(transmitPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendAndCheckXBeePacket(com.digi.xbee.api.packet.XBeePacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails when the received packet is {@code null} and the transmission is 
	 * synchronous.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TransmitException 
	 * @throws IOException 
	 */
	@Test(expected=TransmitException.class)
	public void testSendAndCheckSyncReceivedPacketNull() throws IOException, TransmitException, XBeeException {
		// Return the mocked TransmitStatus error packet when sending any XBee packet synchronously.
		Mockito.doReturn(null).when(xbeeDevice).sendXBeePacket(Mockito.any(XBeePacket.class));
		
		xbeeDevice.sendAndCheckXBeePacket(transmitPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendAndCheckXBeePacket(com.digi.xbee.api.packet.XBeePacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails when the received packet is not a {@code TransmitStatusPacket} or 
	 * {@code TXStatusPacket} and the transmission is synchronous.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TransmitException 
	 * @throws IOException 
	 */
	@Test(expected=TransmitException.class)
	public void testSendAndCheckSyncReceivedPacketInvalid() throws IOException, TransmitException, XBeeException {
		// Return a packet different than TransmitStatusPacket and TXStatusPacket when sending any XBee packet synchronously.
		Mockito.doReturn(transmitPacket).when(xbeeDevice).sendXBeePacket(Mockito.any(XBeePacket.class));
		
		xbeeDevice.sendAndCheckXBeePacket(transmitPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendAndCheckXBeePacket(com.digi.xbee.api.packet.XBeePacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails when the transmission status of the received Transmit Status 
	 * packet is {@code null} and the transmission is synchronous.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TransmitException 
	 * @throws IOException 
	 */
	@Test(expected=TransmitException.class)
	public void testSendAndCheckSyncTransmitStatusTransmissionStatusNull() throws IOException, TransmitException, XBeeException {
		// Return a null transmit status value when requesting it from the transmitStatusSuccess packet.
		Mockito.doReturn(null).when(transmitStatusSuccess).getTransmitStatus();
		// Return the mocked TransmitStatus error packet when sending any XBee packet synchronously.
		Mockito.doReturn(transmitStatusSuccess).when(xbeeDevice).sendXBeePacket(Mockito.any(XBeePacket.class));
		
		xbeeDevice.sendAndCheckXBeePacket(transmitPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendAndCheckXBeePacket(com.digi.xbee.api.packet.XBeePacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails when the received Transmit Status packet contains a status different 
	 * than SUCCESS and the transmission is synchronous.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TransmitException 
	 * @throws IOException 
	 */
	@Test(expected=TransmitException.class)
	public void testSendAndCheckSyncTransmitStatusError() throws IOException, TransmitException, XBeeException {
		// Return the mocked TransmitStatus error packet when sending any XBee packet synchronously.
		Mockito.doReturn(transmitStatusError).when(xbeeDevice).sendXBeePacket(Mockito.any(XBeePacket.class));
		
		xbeeDevice.sendAndCheckXBeePacket(transmitPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendAndCheckXBeePacket(com.digi.xbee.api.packet.XBeePacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails when the transmission status of the received Tx Status 
	 * packet is {@code null} and the transmission is synchronous.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TransmitException 
	 * @throws IOException 
	 */
	@Test(expected=TransmitException.class)
	public void testSendAndCheckSyncTxStatusTransmissionStatusNull() throws IOException, TransmitException, XBeeException {
		// Return a null transmit status value when requesting it from the txStatusSuccess packet.
		Mockito.doReturn(null).when(txStatusSuccess).getTransmitStatus();
		// Return the mocked TxStatus error packet when sending any XBee packet synchronously.
		Mockito.doReturn(txStatusSuccess).when(xbeeDevice).sendXBeePacket(Mockito.any(XBeePacket.class));
		
		xbeeDevice.sendAndCheckXBeePacket(transmitPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendAndCheckXBeePacket(com.digi.xbee.api.packet.XBeePacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails when the received Tx Status packet contains a status different 
	 * than SUCCESS and the transmission is synchronous.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TransmitException 
	 * @throws IOException 
	 */
	@Test(expected=TransmitException.class)
	public void testSendAndCheckSyncTxStatusError() throws IOException, TransmitException, XBeeException {
		// Return the mocked TxStatus error packet when sending any XBee packet synchronously.
		Mockito.doReturn(txStatusError).when(xbeeDevice).sendXBeePacket(Mockito.any(XBeePacket.class));
		
		xbeeDevice.sendAndCheckXBeePacket(transmitPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendAndCheckXBeePacket(com.digi.xbee.api.packet.XBeePacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails if there is an IO exception sending the packet and the 
	 * transmission is synchronous.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TransmitException 
	 * @throws IOException 
	 */
	@Test(expected=XBeeException.class)
	public void testSendAndCheckSyncIOError() throws IOException, TransmitException, XBeeException {
		// Throw an IO exception when sending any XBee packet synchronously.
		Mockito.doThrow(new IOException()).when(xbeeDevice).sendXBeePacket(Mockito.any(XBeePacket.class));
		
		xbeeDevice.sendAndCheckXBeePacket(transmitPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendAndCheckXBeePacket(com.digi.xbee.api.packet.XBeePacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails if there is an IO exception sending the packet and the 
	 * transmission is synchronous.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TransmitException 
	 * @throws IOException 
	 */
	@Test(expected=XBeeException.class)
	public void testSendAndCheckAsyncIOError() throws IOException, TransmitException, XBeeException {
		// Throw an IO exception when sending any XBee packet asynchronously.
		Mockito.doThrow(new IOException()).when(xbeeDevice).sendXBeePacketAsync(Mockito.any(XBeePacket.class));
		
		xbeeDevice.sendAndCheckXBeePacket(transmitPacket, true);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendAndCheckXBeePacket(com.digi.xbee.api.packet.XBeePacket, boolean)}.
	 * 
	 * <p>Verify that packet is considered successfully sent and checked when the received Transmit Status packet 
	 * contains a SUCCESS status.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TransmitException 
	 * @throws IOException 
	 */
	@Test
	public void testSendAndCheckSyncSuccess() throws IOException, TransmitException, XBeeException {
		// Return the mocked TransmitStatus success packet when sending any XBee packet synchronously.
		Mockito.doReturn(transmitStatusSuccess).when(xbeeDevice).sendXBeePacket(Mockito.any(XBeePacket.class));
		
		xbeeDevice.sendAndCheckXBeePacket(transmitPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#sendAndCheckXBeePacket(com.digi.xbee.api.packet.XBeePacket, boolean)}.
	 * 
	 * <p>Verify that packet is considered successfully sent and checked when the received Transmit Status packet 
	 * contains a SUCCESS status.</p>
	 * 
	 * @throws XBeeException 
	 * @throws TransmitException 
	 * @throws IOException 
	 */
	@Test
	public void testSendAndCheckAsyncSuccess() throws IOException, TransmitException, XBeeException {
		// Do nothing when sending any XBee packet asynchronously.
		Mockito.doNothing().when(xbeeDevice).sendXBeePacketAsync(Mockito.any(XBeePacket.class));
		
		xbeeDevice.sendAndCheckXBeePacket(transmitPacket, true);
	}
}

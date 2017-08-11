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
import java.net.Inet6Address;
import java.util.ArrayList;

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
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.TransmitException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.RestFulStatusEnum;
import com.digi.xbee.api.models.XBeeTransmitStatus;
import com.digi.xbee.api.packet.raw.TXStatusPacket;
import com.digi.xbee.api.packet.thread.CoAPRxResponsePacket;
import com.digi.xbee.api.packet.thread.CoAPTxRequestPacket;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ThreadDevice.class)
public class SendAndCheckCoAPPacketTest {
	
	// Constants.
	private static final String IPV6_ADDRESS = "FDB3:0001:0002:0000:0004:0005:0006:0007";
	
	// Variables.
	private Inet6Address ipv6Address;
	
	private ThreadDevice threadDevice;
	private RemoteThreadDevice mockedRemoteDevice;
	
	private CoAPTxRequestPacket coapTxPacket;
	
	private CoAPRxResponsePacket coapRxPacketSuccess;
	private CoAPRxResponsePacket coapRxPacketError;
	
	private TXStatusPacket txStatusSuccess;
	private TXStatusPacket txStatusError;
	
	private ArrayList<CoAPRxResponsePacket> coapResponsePackets;
	
	@Before
	public void setup() throws Exception {
		ipv6Address = (Inet6Address) Inet6Address.getByName(IPV6_ADDRESS);
		coapResponsePackets = new ArrayList<CoAPRxResponsePacket>();
		
		// Instantiate an ThreadDevice object with the mocked interface.
		threadDevice = PowerMockito.spy(new ThreadDevice(Mockito.mock(SerialPortRxTx.class)));
		
		// Mock CoAP Tx Request packet.
		coapTxPacket = Mockito.mock(CoAPTxRequestPacket.class);
		
		// Mock CoAP Rx Response packet SUCCESS.
		coapRxPacketSuccess = Mockito.mock(CoAPRxResponsePacket.class);
		Mockito.when(coapRxPacketSuccess.getStatus()).thenReturn(RestFulStatusEnum.SUCCESS);
		
		// Mock CoAP Rx Response packet ERROR.
		coapRxPacketError = Mockito.mock(CoAPRxResponsePacket.class);
		Mockito.when(coapRxPacketError.getStatus()).thenReturn(RestFulStatusEnum.SERVER_ERROR_GATEWAY_TIMEOUT);
		
		// Mock Tx Status packet SUCCESS.
		txStatusSuccess = Mockito.mock(TXStatusPacket.class);
		Mockito.when(txStatusSuccess.getTransmitStatus()).thenReturn(XBeeTransmitStatus.SUCCESS);
		
		// Mock TX Status packet ERROR.
		txStatusError = Mockito.mock(TXStatusPacket.class);
		Mockito.when(txStatusError.getTransmitStatus()).thenReturn(XBeeTransmitStatus.ADDRESS_NOT_FOUND);
		
		// Mock a RemoteThreadDevice to be used as parameter in the send explicit data command.
		mockedRemoteDevice = Mockito.mock(RemoteThreadDevice.class);
		Mockito.when(mockedRemoteDevice.getIPv6Address()).thenReturn(ipv6Address);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendAndCheckCoAPPacket(com.digi.xbee.api.thread.CoAPTxRequestPacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails if the {@code CoAPTxRequestPacket} 
	 * provided is null and the transmission is synchronous.</p>
	 * 
	 * @throws XBeeException
	 * @throws TransmitException
	 */
	@Test(expected=NullPointerException.class)
	public void testSendAndCheckSyncCoAPPacketNull() throws TransmitException, XBeeException {
		threadDevice.sendAndCheckCoAPPacket(null, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendAndCheckCoAPPacket(com.digi.xbee.api.thread.CoAPTxRequestPacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails if the {@code CoAPTxRequestPacket} 
	 * provided is null and the transmission is asynchronous.</p>
	 * 
	 * @throws XBeeException
	 * @throws TransmitException
	 */
	@Test(expected=NullPointerException.class)
	public void testSendAndCheckAsyncCoAPPacketNull() throws TransmitException, XBeeException {
		threadDevice.sendAndCheckCoAPPacket(null, true);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendAndCheckCoAPPacket(com.digi.xbee.api.thread.CoAPTxRequestPacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails if the connection of the 
	 * device is closed and the transmission is synchronous.</p>
	 * 
	 * @throws XBeeException
	 * @throws TransmitException
	 * @throws IOException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSendAndCheckSyncCoAPConnectionClosed() throws TransmitException, XBeeException, IOException {
		// Throw an interface not open exception when sending any XBee packet synchronously.
		Mockito.doThrow(new InterfaceNotOpenException()).when(threadDevice).sendXBeePacket(Mockito.any(CoAPTxRequestPacket.class));
		
		threadDevice.sendAndCheckCoAPPacket(coapTxPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendAndCheckCoAPPacket(com.digi.xbee.api.thread.CoAPTxRequestPacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails if the connection of the 
	 * device is closed and the transmission is asynchronous.</p>
	 * 
	 * @throws XBeeException
	 * @throws TransmitException
	 * @throws IOException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSendAndCheckAsyncCoAPConnectionClosed() throws TransmitException, XBeeException, IOException {
		// Throw an interface not open exception when sending any XBee packet asynchronously.
		Mockito.doThrow(new InterfaceNotOpenException()).when(threadDevice).sendXBeePacket(Mockito.any(CoAPTxRequestPacket.class));
		
		threadDevice.sendAndCheckCoAPPacket(coapTxPacket, true);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendAndCheckCoAPPacket(com.digi.xbee.api.thread.CoAPTxRequestPacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails if the operating mode of 
	 * the device is not valid and the transmission is synchronous.</p>
	 * 
	 * @throws XBeeException
	 * @throws TransmitException
	 * @throws IOException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSendAndCheckSyncCoAPInvalidOperatingMode() throws TransmitException, XBeeException, IOException {
		// Throw an invalid operating mode exception when sending any XBee packet synchronously.
		Mockito.doThrow(new InvalidOperatingModeException()).when(threadDevice).sendXBeePacket(Mockito.any(CoAPTxRequestPacket.class));
		
		threadDevice.sendAndCheckCoAPPacket(coapTxPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendAndCheckCoAPPacket(com.digi.xbee.api.thread.CoAPTxRequestPacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails if the operating mode of 
	 * the device is not valid and the transmission is asynchronous.</p>
	 * 
	 * @throws XBeeException
	 * @throws TransmitException
	 * @throws IOException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSendAndCheckAsyncCoAPInvalidOperatingMode() throws TransmitException, XBeeException, IOException {
		// Throw an invalid operating mode exception when sending any XBee packet asynchronously.
		Mockito.doThrow(new InvalidOperatingModeException()).when(threadDevice).sendXBeePacketAsync(Mockito.any(CoAPTxRequestPacket.class));
		
		threadDevice.sendAndCheckCoAPPacket(coapTxPacket, true);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendAndCheckCoAPPacket(com.digi.xbee.api.thread.CoAPTxRequestPacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails if there is a timeout 
	 * sending the packet and the transmission is synchronous.</p>
	 * 
	 * @throws XBeeException
	 * @throws TransmitException
	 * @throws IOException
	 */
	@Test(expected=TimeoutException.class)
	public void testSendAndCheckSyncCoAPTimeout() throws TransmitException, XBeeException, IOException {
		// Throw a timeout exception when sending any XBee packet synchronously.
		Mockito.doThrow(new TimeoutException()).when(threadDevice).sendXBeePacket(Mockito.any(CoAPTxRequestPacket.class));
		
		threadDevice.sendAndCheckCoAPPacket(coapTxPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendAndCheckCoAPPacket(com.digi.xbee.api.thread.CoAPTxRequestPacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails when the received packet 
	 * is {@code null} and the transmission is synchronous.</p>
	 * 
	 * @throws XBeeException
	 * @throws TransmitException
	 * @throws IOException
	 */
	@Test(expected=TransmitException.class)
	public void testSendAndCheckSyncCoAPReceivedPacketNull() throws IOException, TransmitException, XBeeException {
		// Return the mocked TransmitStatus error packet when sending any XBee packet synchronously.
		Mockito.doReturn(null).when(threadDevice).sendXBeePacket(Mockito.any(CoAPTxRequestPacket.class));
		
		threadDevice.sendAndCheckCoAPPacket(coapTxPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendAndCheckCoAPPacket(com.digi.xbee.api.thread.CoAPTxRequestPacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails when the received packet 
	 * is not a {@code TXStatusPacket} and the transmission is synchronous.</p>
	 * 
	 * @throws XBeeException
	 * @throws TransmitException
	 * @throws IOException
	 */
	@Test(expected=TransmitException.class)
	public void testSendAndCheckSyncCoAPReceivedPacketInvalid() throws IOException, TransmitException, XBeeException {
		// Return a packet different from a TXStatusPacket when sending any XBee packet synchronously.
		Mockito.doReturn(coapTxPacket).when(threadDevice).sendXBeePacket(Mockito.any(CoAPTxRequestPacket.class));
		
		threadDevice.sendAndCheckCoAPPacket(coapTxPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendAndCheckCoAPPacket(com.digi.xbee.api.thread.CoAPTxRequestPacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails when the transmission 
	 * status of the received Tx Status packet is {@code null} and the 
	 * transmission is synchronous.</p>
	 * 
	 * @throws XBeeException
	 * @throws TransmitException
	 * @throws IOException
	 */
	@Test(expected=TransmitException.class)
	public void testSendAndCheckSyncCoAPTxStatusNull() throws IOException, TransmitException, XBeeException {
		// Return a null transmit status value when requesting it from the txStatusSuccess packet.
		Mockito.doReturn(null).when(txStatusSuccess).getTransmitStatus();
		// Return the mocked TxStatus error packet when sending any XBee packet synchronously.
		Mockito.doReturn(txStatusSuccess).when(threadDevice).sendXBeePacket(Mockito.any(CoAPTxRequestPacket.class));
		
		threadDevice.sendAndCheckCoAPPacket(coapTxPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendAndCheckCoAPPacket(com.digi.xbee.api.thread.CoAPTxRequestPacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails when the received Tx 
	 * Status packet contains a status different from SUCCESS and the 
	 * transmission is synchronous.</p>
	 * 
	 * @throws XBeeException
	 * @throws TransmitException
	 * @throws IOException
	 */
	@Test(expected=TransmitException.class)
	public void testSendAndCheckSyncCoAPTxStatusError() throws IOException, TransmitException, XBeeException {
		// Return the mocked TxStatus error packet when sending any XBee packet synchronously.
		Mockito.doReturn(txStatusError).when(threadDevice).sendXBeePacket(Mockito.any(CoAPTxRequestPacket.class));
		
		threadDevice.sendAndCheckCoAPPacket(coapTxPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendAndCheckCoAPPacket(com.digi.xbee.api.thread.CoAPTxRequestPacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails when the transmission 
	 * status of the received Tx Status packet is correct, the CoAP Rx Response 
	 * is {@code null} and the transmission is synchronous.</p>
	 * 
	 * @throws XBeeException
	 * @throws TransmitException
	 * @throws IOException
	 */
	@Test(expected=XBeeException.class)
	public void testSendAndCheckSyncCoAPTxStatusOkAndRxNull() throws IOException, TransmitException, XBeeException {
		// Return the mocked TxStatus success packet when sending any XBee packet synchronously.
		Mockito.doReturn(txStatusSuccess).when(threadDevice).sendXBeePacket(Mockito.any(CoAPTxRequestPacket.class));
		
		// Return null when waiting for response packet.
		try {
			PowerMockito.doReturn(null).when(threadDevice, "waitForCoAPRxResponsePacket", coapResponsePackets);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		threadDevice.sendAndCheckCoAPPacket(coapTxPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendAndCheckCoAPPacket(com.digi.xbee.api.thread.CoAPTxRequestPacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails when the received Tx 
	 * Status packet contains a status different from SUCCESS and the 
	 * transmission is synchronous.</p>
	 * 
	 * @throws XBeeException
	 * @throws TransmitException
	 * @throws IOException
	 */
	@Test(expected=XBeeException.class)
	public void testSendAndCheckSyncCoAPTxStatuOksAndRxError() throws IOException, TransmitException, XBeeException {
		// Return the mocked TxStatus success packet  when sending any XBee packet synchronously.
		Mockito.doReturn(txStatusSuccess).when(threadDevice).sendXBeePacket(Mockito.any(CoAPTxRequestPacket.class));
		
		// Return XBee exception when waiting for Error Packet.
		try {
			PowerMockito.doThrow(new XBeeException()).when(threadDevice, "waitForCoAPRxResponsePacket", coapResponsePackets);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		threadDevice.sendAndCheckCoAPPacket(coapTxPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendAndCheckCoAPPacket(com.digi.xbee.api.thread.CoAPTxRequestPacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails if there is an IO 
	 * exception sending the packet and the transmission is synchronous.</p>
	 * 
	 * @throws XBeeException
	 * @throws TransmitException
	 * @throws IOException
	 */
	@Test(expected=XBeeException.class)
	public void testSendAndCheckSyncCoAPIOError() throws IOException, TransmitException, XBeeException {
		// Throw an IO exception when sending any XBee packet synchronously.
		Mockito.doThrow(new IOException()).when(threadDevice).sendXBeePacket(Mockito.any(CoAPTxRequestPacket.class));
		
		threadDevice.sendAndCheckCoAPPacket(coapTxPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendAndCheckCoAPPacket(com.digi.xbee.api.thread.CoAPTxRequestPacket, boolean)}.
	 * 
	 * <p>Verify that the send and check method fails if there is an IO 
	 * exception sending the packet and the transmission is asynchronous.</p>
	 * 
	 * @throws XBeeException
	 * @throws TransmitException
	 * @throws IOException
	 */
	@Test(expected=XBeeException.class)
	public void testSendAndCheckAsyncCoAPIOError() throws IOException, TransmitException, XBeeException {
		// Throw an IO exception when sending any XBee packet asynchronously.
		Mockito.doThrow(new IOException()).when(threadDevice).sendXBeePacketAsync(Mockito.any(CoAPTxRequestPacket.class));
		
		threadDevice.sendAndCheckCoAPPacket(coapTxPacket, true);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendAndCheckCoAPPacket(com.digi.xbee.api.thread.CoAPTxRequestPacket, boolean)}.
	 * 
	 * <p>Verify that packet is considered successfully sent and checked when 
	 * the received TX Status packet contains a SUCCESS status.</p>
	 * 
	 * @throws XBeeException
	 * @throws TransmitException
	 * @throws IOException
	 */
	@Test
	public void testSendAndCheckSyncCoAPSuccess() throws IOException, TransmitException, XBeeException {
		// Return the mocked TxStatus success packet when sending any XBee packet synchronously.
		Mockito.doReturn(txStatusSuccess).when(threadDevice).sendXBeePacket(Mockito.any(CoAPTxRequestPacket.class));
		
		// Return the mocked CoAP Rx Response packet when waiting for it.
		try {
			PowerMockito.doReturn(coapRxPacketSuccess).when(threadDevice, "waitForCoAPRxResponsePacket", coapResponsePackets);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		threadDevice.sendAndCheckCoAPPacket(coapTxPacket, false);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendAndCheckCoAPPacket(com.digi.xbee.api.thread.CoAPTxRequestPacket, boolean)}.
	 * 
	 * <p>Verify that packet is considered successfully sent and checked when 
	 * the received TX Status packet contains a SUCCESS status.</p>
	 * 
	 * @throws XBeeException
	 * @throws TransmitException
	 * @throws IOException
	 */
	@Test
	public void testSendAndCheckAsyncCoAPSuccess() throws IOException, TransmitException, XBeeException {
		// Do nothing when sending any XBee packet asynchronously.
		Mockito.doNothing().when(threadDevice).sendXBeePacketAsync(Mockito.any(CoAPTxRequestPacket.class));
		
		threadDevice.sendAndCheckCoAPPacket(coapTxPacket, true);
	}
}

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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Inet6Address;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.ATCommandException;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.IPProtocol;
import com.digi.xbee.api.models.ThreadAssociationIndicationStatus;
import com.digi.xbee.api.models.XBee64BitAddress;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ThreadDevice.class, ThreadAssociationIndicationStatus.class})
public class ThreadDeviceTest {
	
	// Constants.
	private static final String PARAMETER_AI = "AI";
	private static final byte[] RESPONSE_AI = new byte[]{0x00};
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Variables.
	private ThreadDevice threadDevice;
	
	private ThreadAssociationIndicationStatus validThreadAIStatus = ThreadAssociationIndicationStatus.ASSOCIATED;
	
	@Before
	public void setup() throws Exception {
		// Suppress the 'readDeviceInfo' method of the parent class so that it is not
		// called from the child (ThreadDevice) class.
		PowerMockito.suppress(PowerMockito.method(IPv6Device.class, "readDeviceInfo"));
		
		// Spy the ThreadDevice class.
		SerialPortRxTx mockPort = Mockito.mock(SerialPortRxTx.class);
		threadDevice = PowerMockito.spy(new ThreadDevice(mockPort));
		
		// Always return a valid Thread association indication when requested.
		PowerMockito.mockStatic(ThreadAssociationIndicationStatus.class);
		PowerMockito.when(ThreadAssociationIndicationStatus.get(Mockito.anyInt())).thenReturn(validThreadAIStatus);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#getDestinationAddress()}.
	 * 
	 * <p>Verify that the not supported methods of the Thread protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationGetDestinationAddress() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		threadDevice.getDestinationAddress();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#setDestinationAddress(XBee64BitAddress)}.
	 * 
	 * <p>Verify that the not supported methods of the Thread protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationSetDestinationAddress() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		threadDevice.setDestinationAddress(XBee64BitAddress.BROADCAST_ADDRESS);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#addDataListener(com.digi.xbee.api.listeners.IDataReceiveListener)}.
	 * 
	 * <p>Verify that the not supported methods of the Thread protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedAddDataListener() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		threadDevice.addDataListener(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#removeDataListener(com.digi.xbee.api.listeners.IDataReceiveListener)}.
	 * 
	 * <p>Verify that the not supported methods of the Thread protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedRemoveDataListener() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		threadDevice.removeDataListener(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readData()}.
	 * 
	 * <p>Verify that the not supported methods of the Thread protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationReadData() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		threadDevice.readData();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readData(int)}.
	 * 
	 * <p>Verify that the not supported methods of the Thread protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationReadDataTimeout() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		threadDevice.readData(4);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readDataFrom(com.digi.xbee.api.RemoteXBeeDevice)}.
	 * 
	 * <p>Verify that the not supported methods of the Thread protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationReadDataFrom() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		threadDevice.readDataFrom(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readDataFrom(com.digi.xbee.api.RemoteXBeeDevice, int)}.
	 * 
	 * <p>Verify that the not supported methods of the Thread protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationReadDataFromTimeout() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		threadDevice.readDataFrom(null, 4);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#sendBroadcastData(byte[])}.
	 * 
	 * <p>Verify that the not supported methods of the Thread protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationSendBroadcastData() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		threadDevice.sendBroadcastData(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#sendData(com.digi.xbee.api.RemoteXBeeDevice, byte[])}.
	 * 
	 * <p>Verify that the not supported methods of the Thread protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationSendData() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		threadDevice.sendData((RemoteXBeeDevice)null, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#sendDataAsync(com.digi.xbee.api.RemoteXBeeDevice, byte[])}.
	 * 
	 * <p>Verify that the not supported methods of the Thread protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationSendDataAsync() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in this module.")));
		
		// Call the method that should throw the exception.
		threadDevice.sendDataAsync((RemoteXBeeDevice)null, null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#getAssociationIndicationStatus()}.
	 * 
	 * <p>Verify that the not supported methods of the Thread protocol throw an
	 * {@code UnsupportedOperationException}.</p>
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testNotSupportedOperationGetAssociationIndicationStatus() throws Exception {
		exception.expect(UnsupportedOperationException.class);
		exception.expectMessage(is(equalTo("Operation not supported in Thread protocol.")));
		
		// Call the method that should throw the exception.
		threadDevice.getAssociationIndicationStatus();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#getThreadAssociationIndicationStatus()}.
	 * 
	 * <p>Check that the Thread association indication method returns values 
	 * successfully.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testThreadAssociationIndicationStatus() throws Exception {
		// Return a valid response when requesting the AI parameter value.
		Mockito.doReturn(RESPONSE_AI).when(threadDevice).getParameter(PARAMETER_AI);
		
		assertEquals(validThreadAIStatus, threadDevice.getThreadAssociationIndicationStatus());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#isConnected()}.
	 * 
	 * <p>Verify that it is not possible to determine if the device is connected if 
	 * the connection of the device is closed.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testIsConnectedErrorConnectionClosed() throws XBeeException {
		// Throw an interface not open exception when getting the ThreadAssociationIndicationStatus.
		Mockito.doThrow(new InterfaceNotOpenException()).when(threadDevice).getThreadAssociationIndicationStatus();
		
		// Check the connection.
		threadDevice.isConnected();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#isConnected()}.
	 * 
	 * <p>Verify that it is not possible to determine if the device is connected if 
	 * the operating mode of the device is not valid.</p>
	 * 
	 * @throws XBeeException
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testIsConnectedErrorInvalidOperatingMode() throws XBeeException {
		// Throw an invalid operating mode exception when getting the ThreadAssociationIndicationStatus.
		Mockito.doThrow(new InvalidOperatingModeException()).when(threadDevice).getThreadAssociationIndicationStatus();
		
		// Check the connection.
		threadDevice.isConnected();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#isConnected()}.
	 * 
	 * <p>Verify that it is not possible to determine if the device is connected 
	 * when there is an Timeout exception getting the ThreadAssociationIndicationStatus.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=TimeoutException.class)
	public void testIsConnectedErrorTimeout() throws XBeeException, IOException {
		// Throw a timeout exception when getting the ThreadAssociationIndicationStatus.
		Mockito.doThrow(new TimeoutException()).when(threadDevice).getThreadAssociationIndicationStatus();
		
		// Check the connection.
		threadDevice.isConnected();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#isConnected()}.
	 * 
	 * <p>Verify that it is not possible to determine if the device is connected 
	 * when there is an AT command exception getting the ThreadAssociationIndicationStatus.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException 
	 */
	@Test(expected=ATCommandException.class)
	public void testIsConnectedErrorInvalidAnswer() throws XBeeException, IOException {
		// Throw an AT command exception when when getting the ThreadAssociationIndicationStatus.
		Mockito.doThrow(new ATCommandException(null)).when(threadDevice).getThreadAssociationIndicationStatus();
		
		// Check the connection.
		threadDevice.isConnected();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#isConnected()}.
	 * 
	 * <p>Verify that it is possible to determine if the device is connected or not 
	 * when it is connected.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testIsConnectedSuccessConnected() throws XBeeException, IOException {
		// Return the connected value when when getting the ThreadAssociationIndicationStatus.
		Mockito.doReturn(ThreadAssociationIndicationStatus.ASSOCIATED).when(threadDevice).getThreadAssociationIndicationStatus();
		
		// Check the connection.
		assertTrue(threadDevice.isConnected());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#isConnected()}.
	 * 
	 * <p>Verify that it is possible to determine if the device is connected or not 
	 * when it is disconnected.</p>
	 * 
	 * @throws XBeeException
	 * @throws IOException
	 */
	@Test
	public void testIsConnectedSuccessDisconnected() throws XBeeException, IOException {
		// Return a valid disconnected value when when getting the ThreadAssociationIndicationStatus.
		Mockito.doReturn(ThreadAssociationIndicationStatus.DISASSOCIATED).when(threadDevice).getThreadAssociationIndicationStatus();
		
		// Check the connection.
		assertFalse(threadDevice.isConnected());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendIPData(Inet6Address, int, IPProtocol, byte[])
	 * 
	 * <p>Verify that TCP protocol is not supported when sending IPv6 data synchronously.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSendIPDataProtocolIllegalTCP() throws Exception {
		// Set up the resources for the test.
		Inet6Address address = (Inet6Address) Inet6Address.getByName("FDB3:0001:0002:0000:0004:0005:0006:0007");
		byte[] data = "Hello XBee".getBytes();
		int destPort = 1234;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo(String.format("Protocol must be %s or %s.", 
				IPProtocol.UDP.getName(), IPProtocol.COAP.getName()))));
		
		// Call the method under test that should throw an IllegalArgumentException.
		threadDevice.sendIPData(address, destPort, IPProtocol.TCP, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendIPData(Inet6Address, int, IPProtocol, byte[])
	 * 
	 * <p>Verify that TCP SSL protocol is not supported when sending IPv6 data synchronously.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSendIPDataProtocolIllegalTCPSSL() throws Exception {
		// Set up the resources for the test.
		Inet6Address address = (Inet6Address) Inet6Address.getByName("FDB3:0001:0002:0000:0004:0005:0006:0007");
		byte[] data = "Hello XBee".getBytes();
		int destPort = 1234;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo(String.format("Protocol must be %s or %s.", 
				IPProtocol.UDP.getName(), IPProtocol.COAP.getName()))));
		
		// Call the method under test that should throw an IllegalArgumentException.
		threadDevice.sendIPData(address, destPort, IPProtocol.TCP_SSL, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendIPDataAsync(Inet6Address, int, IPProtocol, byte[])
	 * 
	 * <p>Verify that TCP protocol is not supported when sending IPv6 data asynchronously.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSendIPDataAsyncProtocolIllegalTCP() throws Exception {
		// Set up the resources for the test.
		Inet6Address address = (Inet6Address) Inet6Address.getByName("FDB3:0001:0002:0000:0004:0005:0006:0007");
		byte[] data = "Hello XBee".getBytes();
		int destPort = 1234;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo(String.format("Protocol must be %s or %s.", 
				IPProtocol.UDP.getName(), IPProtocol.COAP.getName()))));
		
		// Call the method under test that should throw an IllegalArgumentException.
		threadDevice.sendIPDataAsync(address, destPort, IPProtocol.TCP, data);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.ThreadDevice#sendIPDataAsync(Inet6Address, int, IPProtocol, byte[])
	 * 
	 * <p>Verify that TCP SSL protocol is not supported when sending IPv6 data asynchronously.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSendIPDataAsyncProtocolIllegalTCPSSL() throws Exception {
		// Set up the resources for the test.
		Inet6Address address = (Inet6Address) Inet6Address.getByName("FDB3:0001:0002:0000:0004:0005:0006:0007");
		byte[] data = "Hello XBee".getBytes();
		int destPort = 1234;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo(String.format("Protocol must be %s or %s.", 
				IPProtocol.UDP.getName(), IPProtocol.COAP.getName()))));
		
		// Call the method under test that should throw an IllegalArgumentException.
		threadDevice.sendIPDataAsync(address, destPort, IPProtocol.TCP_SSL, data);
	}
}

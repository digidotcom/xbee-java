/*
 * Copyright 2017-2019, Digi International Inc.
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
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.net.Inet6Address;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.models.IPMessage;
import com.digi.xbee.api.models.IPProtocol;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBeePacketsQueue;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.thread.RXIPv6Packet;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IPv6Device.class, DataReader.class})
public class IPv6DeviceReadDataTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Variables.
	private IPv6Device ipv6Device;
	
	private SerialPortRxTx mockConnectionInterface;
	
	private XBeePacketsQueue mockXBeePacketsQueue;
	
	private Inet6Address sourceIPv6Address;
	private Inet6Address destIPv6Address;
	
	private String receivedIPv6Data;
	
	private int sourcePort = 123;
	private int destPort = 456;
	
	private IPProtocol protocol = IPProtocol.TCP;
	
	@Before
	public void setUp() throws Exception {
		sourceIPv6Address = (Inet6Address) Inet6Address.getByName("FDB3:0001:0002:0000:0004:0005:0006:0007");
		destIPv6Address = (Inet6Address) Inet6Address.getByName("FDB3:0002:0003:0000:0005:0006:0007:0008");
		receivedIPv6Data = "Received IPv6 data";
		
		mockConnectionInterface = Mockito.mock(SerialPortRxTx.class);
		
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Mockito.when(mockConnectionInterface.isOpen()).thenReturn(true);
				return null;
			}
		}).when(mockConnectionInterface).open();
		
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Exception {
				Mockito.when(mockConnectionInterface.isOpen()).thenReturn(false);
				return null;
			}
		}).when(mockConnectionInterface).close();
		
		mockXBeePacketsQueue = Mockito.mock(XBeePacketsQueue.class);
		
		Mockito.doAnswer(new Answer<XBeePacket>() {
			public XBeePacket answer(InvocationOnMock invocation) throws Exception {
				return new RXIPv6Packet(destIPv6Address, sourceIPv6Address, destPort, sourcePort,
						protocol, receivedIPv6Data.getBytes());
			}
		}).when(mockXBeePacketsQueue).getFirstIPv6DataPacketFrom(Mockito.any(Inet6Address.class), Mockito.anyInt());
		
		Mockito.doAnswer(new Answer<XBeePacket>() {
			public XBeePacket answer(InvocationOnMock invocation) throws Exception {
				return new RXIPv6Packet(destIPv6Address, sourceIPv6Address, destPort, sourcePort,
						protocol, receivedIPv6Data.getBytes());
			}
		}).when(mockXBeePacketsQueue).getFirstIPv6DataPacket(Mockito.anyInt());
		
		PowerMockito.whenNew(XBeePacketsQueue.class).withAnyArguments().thenReturn(mockXBeePacketsQueue);
		
		ipv6Device = PowerMockito.spy(new IPv6Device(mockConnectionInterface));
		
		Mockito.doReturn(OperatingMode.API).when(ipv6Device).determineOperatingMode();
		Mockito.doNothing().when(ipv6Device).readDeviceInfo();
		
		ipv6Device.open();
	}
	
	@After
	public void tearDown() throws Exception {
		ipv6Device.close();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readIPData()} when 
	 * the connection interface is not open.
	 */
	@Test
	public final void testReadIPDataInterfaceNotOpenException() {
		// Setup the resources for the test.
		Mockito.when(ipv6Device.isOpen()).thenReturn(false);
		
		exception.expect(InterfaceNotOpenException.class);
		exception.expectMessage(is(equalTo("The connection interface is not open.")));
		
		// Call the method under test and verify the result.
		ipv6Device.readIPData();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readIPData()}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPData() throws Exception {
		// Call the method under test.
		IPMessage readMessage = ipv6Device.readIPData();
		
		// Verify the result.
		PowerMockito.verifyPrivate(ipv6Device).invoke("readIPDataPacket", null, 3000);
		
		assertThat("IPv6 address must not be null", readMessage.getIPv6Address(), is(not(equalTo(null))));
		assertThat("IPv6 address must be '" + sourceIPv6Address + "' and not '" + readMessage.getIPv6Address() + "'", readMessage.getIPv6Address(), is(equalTo(sourceIPv6Address)));
		assertThat("Source port must be '" + sourcePort + "' and not '" + readMessage.getSourcePort(), readMessage.getSourcePort(), is(equalTo(sourcePort)));
		assertThat("Destination port must be '" + destPort + "' and not '" + readMessage.getSourcePort(), readMessage.getDestPort(), is(equalTo(destPort)));
		assertThat("Protocol port must be '" + protocol.getName() + "' and not '" + readMessage.getProtocol().getName(), readMessage.getProtocol(), is(equalTo(protocol)));
		assertThat("Received data must be '" + receivedIPv6Data + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedIPv6Data)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readIPData(int))} 
	 * when timeout is negative.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataTimeoutNegative() throws Exception {
		// Setup the resources for the test.
		int timeout = -100;
		IPMessage message = new IPMessage(sourceIPv6Address, sourcePort, destPort, protocol, new byte[0]);
		PowerMockito.doReturn(message).when(ipv6Device, "readIPDataPacket", null, timeout);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Read timeout must be 0 or greater.")));
		
		// Call the method under test and verify the result.
		ipv6Device.readIPData(timeout);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readIPData(int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataTimeout() throws Exception {
		// Setup the resources for the test.
		int timeout = 100;
		
		// Call the method under test.
		IPMessage readMessage = ipv6Device.readIPData(timeout);
		
		// Verify the result.
		PowerMockito.verifyPrivate(ipv6Device).invoke("readIPDataPacket", null, timeout);
		
		assertThat("IPv6 address must not be null", readMessage.getIPv6Address(), is(not(equalTo(null))));
		assertThat("IPv6 address must be '" + sourceIPv6Address + "' and not '" + readMessage.getIPv6Address() + "'", readMessage.getIPv6Address(), is(equalTo(sourceIPv6Address)));
		assertThat("Source port must be '" + sourcePort + "' and not '" + readMessage.getSourcePort(), readMessage.getSourcePort(), is(equalTo(sourcePort)));
		assertThat("Destination port must be '" + destPort + "' and not '" + readMessage.getSourcePort(), readMessage.getDestPort(), is(equalTo(destPort)));
		assertThat("Protocol port must be '" + protocol.getName() + "' and not '" + readMessage.getProtocol().getName(), readMessage.getProtocol(), is(equalTo(protocol)));
		assertThat("Received data must be '" + receivedIPv6Data + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedIPv6Data)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readIPDataFrom(Inet6Address, int))} 
	 * when IPv6 is null.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataFromNullIPv6() throws Exception {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("IPv6 address cannot be null.")));

		// Call the method under test and verify the result.
		ipv6Device.readIPDataFrom(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readIPDataFrom(Inet6Address))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataFrom() throws Exception {
		// Call the method under test.
		IPMessage readMessage = ipv6Device.readIPDataFrom(sourceIPv6Address);

		// Verify the result.
		PowerMockito.verifyPrivate(ipv6Device).invoke("readIPDataPacket", sourceIPv6Address, 3000);
		
		assertThat("IPv6 address must not be null", readMessage.getIPv6Address(), is(not(equalTo(null))));
		assertThat("IPv6 address must be '" + sourceIPv6Address + "' and not '" + readMessage.getIPv6Address() + "'", readMessage.getIPv6Address(), is(equalTo(sourceIPv6Address)));
		assertThat("Source port must be '" + sourcePort + "' and not '" + readMessage.getSourcePort(), readMessage.getSourcePort(), is(equalTo(sourcePort)));
		assertThat("Destination port must be '" + destPort + "' and not '" + readMessage.getSourcePort(), readMessage.getDestPort(), is(equalTo(destPort)));
		assertThat("Protocol port must be '" + protocol.getName() + "' and not '" + readMessage.getProtocol().getName(), readMessage.getProtocol(), is(equalTo(protocol)));
		assertThat("Received data must be '" + receivedIPv6Data + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedIPv6Data)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readIPDataFrom(Inet6Address, int))} 
	 * when timeout is negative.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataFromTimeoutNegative() throws Exception {
		// Setup the resources for the test.
		int timeout = -100;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Read timeout must be 0 or greater.")));

		// Call the method under test and verify the result.
		ipv6Device.readIPDataFrom(sourceIPv6Address, timeout);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readIPDataFrom(Inet6Address, int))} 
	 * when timeout is null.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataFromTimeoutNullIPv6() throws Exception {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("IPv6 address cannot be null.")));

		// Call the method under test and verify the result.
		ipv6Device.readIPDataFrom(null, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readIPDataFrom(Inet6Address, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataFromTimeout() throws Exception {
		// Setup the resources for the test.
		int timeout = 100;
		
		// Call the method under test.
		IPMessage readMessage = ipv6Device.readIPDataFrom(sourceIPv6Address, timeout);
		
		// Verify the result.
		PowerMockito.verifyPrivate(ipv6Device).invoke("readIPDataPacket", sourceIPv6Address, timeout);
		
		assertThat("IPv6 address must not be null", readMessage.getIPv6Address(), is(not(equalTo(null))));
		assertThat("IPv6 address must be '" + sourceIPv6Address + "' and not '" + readMessage.getIPv6Address() + "'", readMessage.getIPv6Address(), is(equalTo(sourceIPv6Address)));
		assertThat("Source port must be '" + sourcePort + "' and not '" + readMessage.getSourcePort(), readMessage.getSourcePort(), is(equalTo(sourcePort)));
		assertThat("Destination port must be '" + destPort + "' and not '" + readMessage.getSourcePort(), readMessage.getDestPort(), is(equalTo(destPort)));
		assertThat("Protocol port must be '" + protocol.getName() + "' and not '" + readMessage.getProtocol().getName(), readMessage.getProtocol(), is(equalTo(protocol)));
		assertThat("Received data must be '" + receivedIPv6Data + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedIPv6Data)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readIPDataPacket(Inet6Address, int))} 
	 * when a Not Open Exception is previously thrown.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataPacketInterfaceNotOpenException() throws Exception {
		// Setup the resources for the test.
		Mockito.when(ipv6Device.isOpen()).thenReturn(false);
		
		exception.expect(InterfaceNotOpenException.class);
		exception.expectMessage(is(equalTo("The connection interface is not open.")));
		
		// Call the method under test and verify the result.
		Whitebox.invokeMethod(ipv6Device, "readIPDataPacket", (Inet6Address)null, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readIPDataPacket(Inet6Address, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataPacketNotIPv6Timeout() throws Exception {
		// Setup the resources for the test.
		Mockito.doAnswer(new Answer<XBeePacket>() {
			public XBeePacket answer(InvocationOnMock invocation) throws Exception {
				return null;
			}
		}).when(mockXBeePacketsQueue).getFirstIPv6DataPacket(Mockito.anyInt());
		
		// Call the method under test.
		IPMessage message = Whitebox.invokeMethod(ipv6Device, "readIPDataPacket", (Inet6Address)null, 100);
		
		// Verify the result.
		assertThat("Message must be null", message, is(equalTo(null)));
		
		Mockito.verify(mockXBeePacketsQueue).getFirstIPv6DataPacket(100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readIPDataPacket(Inet6Address, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataPacketNotIPv6() throws Exception {
		// Setup the resources for the test.
		receivedIPv6Data = "Received message data";
		
		// Call the method under test.
		IPMessage readMessage = Whitebox.invokeMethod(ipv6Device, "readIPDataPacket", (Inet6Address)null, 100);
		
		// Verify the result.
		assertThat("Message must not be null", readMessage, is(not(equalTo(null))));
		assertThat("IPv6 address must not be null", readMessage.getIPv6Address(), is(not(equalTo(null))));
		assertThat("IPv6 address must be '" + sourceIPv6Address + "' and not '" + readMessage.getIPv6Address() + "'", readMessage.getIPv6Address(), is(equalTo(sourceIPv6Address)));
		assertThat("Source port must be '" + sourcePort + "' and not '" + readMessage.getSourcePort(), readMessage.getSourcePort(), is(equalTo(sourcePort)));
		assertThat("Destination port must be '" + destPort + "' and not '" + readMessage.getSourcePort(), readMessage.getDestPort(), is(equalTo(destPort)));
		assertThat("Protocol port must be '" + protocol.getName() + "' and not '" + readMessage.getProtocol().getName(), readMessage.getProtocol(), is(equalTo(protocol)));
		assertThat("Received data must be '" + receivedIPv6Data + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedIPv6Data)));
		
		Mockito.verify(mockXBeePacketsQueue).getFirstIPv6DataPacket(100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readIPDataPacket(Inet6Address, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataPacketWithIPv6Timeout() throws Exception {
		Mockito.doAnswer(new Answer<XBeePacket>() {
			public XBeePacket answer(InvocationOnMock invocation) throws Exception {
				return null;
			}
		}).when(mockXBeePacketsQueue).getFirstIPv6DataPacketFrom(Mockito.eq(sourceIPv6Address), Mockito.anyInt());
		
		// Call the method under test.
		IPMessage message = Whitebox.invokeMethod(ipv6Device, "readIPDataPacket", sourceIPv6Address, 100);
		
		// Verify the result.
		assertThat("Message must be null", message, is(equalTo(null)));
		
		Mockito.verify(mockXBeePacketsQueue).getFirstIPv6DataPacketFrom(sourceIPv6Address, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPv6Device#readIPDataPacket(Inet6Address, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataPacketWithIPv6() throws Exception {
		// Setup the resources for the test.
		receivedIPv6Data = "Received message data";
		
		// Call the method under test.
		IPMessage readMessage = Whitebox.invokeMethod(ipv6Device, "readIPDataPacket", sourceIPv6Address, 100);
		
		// Verify the result.
		assertThat("Message must not be null", readMessage, is(not(equalTo(null))));
		assertThat("Message must not be null", readMessage, is(not(equalTo(null))));
		assertThat("IPv6 address must not be null", readMessage.getIPv6Address(), is(not(equalTo(null))));
		assertThat("IPv6 address must be '" + sourceIPv6Address + "' and not '" + readMessage.getIPv6Address() + "'", readMessage.getIPv6Address(), is(equalTo(sourceIPv6Address)));
		assertThat("Source port must be '" + sourcePort + "' and not '" + readMessage.getSourcePort(), readMessage.getSourcePort(), is(equalTo(sourcePort)));
		assertThat("Destination port must be '" + destPort + "' and not '" + readMessage.getSourcePort(), readMessage.getDestPort(), is(equalTo(destPort)));
		assertThat("Protocol port must be '" + protocol.getName() + "' and not '" + readMessage.getProtocol().getName(), readMessage.getProtocol(), is(equalTo(protocol)));
		assertThat("Received data must be '" + receivedIPv6Data + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedIPv6Data)));
		
		Mockito.verify(mockXBeePacketsQueue).getFirstIPv6DataPacketFrom(sourceIPv6Address, 100);
	}
}

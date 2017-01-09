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
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.net.Inet4Address;

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

import com.digi.xbee.api.connection.DataReader;
import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.models.IPMessage;
import com.digi.xbee.api.models.IPProtocol;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBeePacketsQueue;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.ip.RXIPv4Packet;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IPDevice.class, DataReader.class})
public class IPDeviceReadDataTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Variables.
	private IPDevice ipDevice;
	
	private SerialPortRxTx mockConnectionInterface;
	
	private XBeePacketsQueue mockXBeePacketsQueue;
	
	private Inet4Address ipAddress;
	private String receivedIPData;
	
	private int sourcePort = 123;
	private int destPort = 456;
	
	private IPProtocol protocol = IPProtocol.TCP;
	
	@Before
	public void setUp() throws Exception {
		ipAddress = (Inet4Address) Inet4Address.getByName("10.101.1.123");
		receivedIPData = "Received IP data";
		
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
				return new RXIPv4Packet(ipAddress, destPort, sourcePort,
						protocol, receivedIPData.getBytes());
			}
		}).when(mockXBeePacketsQueue).getFirstIPDataPacketFrom(Mockito.any(Inet4Address.class), Mockito.anyInt());
		
		Mockito.doAnswer(new Answer<XBeePacket>() {
			public XBeePacket answer(InvocationOnMock invocation) throws Exception {
				return new RXIPv4Packet(ipAddress, destPort, sourcePort,
						protocol, receivedIPData.getBytes());
			}
		}).when(mockXBeePacketsQueue).getFirstIPDataPacket(Mockito.anyInt());
		
		PowerMockito.whenNew(XBeePacketsQueue.class).withAnyArguments().thenReturn(mockXBeePacketsQueue);
		
		ipDevice = PowerMockito.spy(new IPDevice(mockConnectionInterface));
		
		Mockito.doReturn(OperatingMode.API).when(ipDevice).determineOperatingMode();
		Mockito.doNothing().when(ipDevice).readDeviceInfo();
		
		ipDevice.open();
	}
	
	@After
	public void tearDown() throws Exception {
		ipDevice.close();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readIPData()}.
	 */
	@Test
	public final void testReadIPDataInterfaceNotOpenException() {
		// Setup the resources for the test.
		Mockito.when(ipDevice.isOpen()).thenReturn(false);
		
		exception.expect(InterfaceNotOpenException.class);
		exception.expectMessage(is(equalTo("The connection interface is not open.")));
		
		// Call the method under test and verify the result.
		ipDevice.readIPData();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readNetowrkData()}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadNetowrkData() throws Exception {
		// Call the method under test.
		IPMessage readMessage = ipDevice.readIPData();
		
		// Verify the result.
		PowerMockito.verifyPrivate(ipDevice).invoke("readIPDataPacket", null, 3000);
		
		assertThat("IP address must not be null", readMessage.getIPAddress(), is(not(equalTo(null))));
		assertThat("IP address must be '" + ipAddress + "' and not '" + readMessage.getIPAddress() + "'", readMessage.getIPAddress(), is(equalTo(ipAddress)));
		assertThat("Source port must be '" + sourcePort + "' and not '" + readMessage.getSourcePort(), readMessage.getSourcePort(), is(equalTo(sourcePort)));
		assertThat("Destination port must be '" + destPort + "' and not '" + readMessage.getSourcePort(), readMessage.getDestPort(), is(equalTo(destPort)));
		assertThat("Protocol port must be '" + protocol.getName() + "' and not '" + readMessage.getProtocol().getName(), readMessage.getProtocol(), is(equalTo(protocol)));
		assertThat("Received data must be '" + receivedIPData + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedIPData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readIPData(int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataTimeoutNegative() throws Exception {
		// Setup the resources for the test.
		int timeout = -100;
		IPMessage message = new IPMessage(ipAddress, sourcePort, destPort, protocol, new byte[0]);
		PowerMockito.doReturn(message).when(ipDevice, "readIPDataPacket", null, timeout);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Read timeout must be 0 or greater.")));
		
		// Call the method under test and verify the result.
		ipDevice.readIPData(timeout);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readIPData(int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataTimeout() throws Exception {
		// Setup the resources for the test.
		int timeout = 100;
		
		// Call the method under test.
		IPMessage readMessage = ipDevice.readIPData(timeout);
		
		// Verify the result.
		PowerMockito.verifyPrivate(ipDevice).invoke("readIPDataPacket", null, timeout);
		
		assertThat("IP address must not be null", readMessage.getIPAddress(), is(not(equalTo(null))));
		assertThat("IP address must be '" + ipAddress + "' and not '" + readMessage.getIPAddress() + "'", readMessage.getIPAddress(), is(equalTo(ipAddress)));
		assertThat("Source port must be '" + sourcePort + "' and not '" + readMessage.getSourcePort(), readMessage.getSourcePort(), is(equalTo(sourcePort)));
		assertThat("Destination port must be '" + destPort + "' and not '" + readMessage.getSourcePort(), readMessage.getDestPort(), is(equalTo(destPort)));
		assertThat("Protocol port must be '" + protocol.getName() + "' and not '" + readMessage.getProtocol().getName(), readMessage.getProtocol(), is(equalTo(protocol)));
		assertThat("Received data must be '" + receivedIPData + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedIPData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readIPDataFrom(Inet4Address, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataFromNullIP() throws Exception {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("IP address cannot be null.")));

		// Call the method under test and verify the result.
		ipDevice.readIPDataFrom(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readIPDataFrom(Inet4Address))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataFrom() throws Exception {
		// Call the method under test.
		IPMessage readMessage = ipDevice.readIPDataFrom(ipAddress);

		// Verify the result.
		PowerMockito.verifyPrivate(ipDevice).invoke("readIPDataPacket", ipAddress, 3000);
		
		assertThat("IP address must not be null", readMessage.getIPAddress(), is(not(equalTo(null))));
		assertThat("IP address must be '" + ipAddress + "' and not '" + readMessage.getIPAddress() + "'", readMessage.getIPAddress(), is(equalTo(ipAddress)));
		assertThat("Source port must be '" + sourcePort + "' and not '" + readMessage.getSourcePort(), readMessage.getSourcePort(), is(equalTo(sourcePort)));
		assertThat("Destination port must be '" + destPort + "' and not '" + readMessage.getSourcePort(), readMessage.getDestPort(), is(equalTo(destPort)));
		assertThat("Protocol port must be '" + protocol.getName() + "' and not '" + readMessage.getProtocol().getName(), readMessage.getProtocol(), is(equalTo(protocol)));
		assertThat("Received data must be '" + receivedIPData + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedIPData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readIPDataFrom(Inet4Address, int))}.
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
		ipDevice.readIPDataFrom(ipAddress, timeout);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readIPDataFrom(Inet4Address, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataFromTimeoutNullIP() throws Exception {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("IP address cannot be null.")));

		// Call the method under test and verify the result.
		ipDevice.readIPDataFrom(null, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readIPDataFrom(Inet4Address, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataFromTimeout() throws Exception {
		// Setup the resources for the test.
		int timeout = 100;
		
		// Call the method under test.
		IPMessage readMessage = ipDevice.readIPDataFrom(ipAddress, timeout);
		
		// Verify the result.
		PowerMockito.verifyPrivate(ipDevice).invoke("readIPDataPacket", ipAddress, timeout);
		
		assertThat("IP address must not be null", readMessage.getIPAddress(), is(not(equalTo(null))));
		assertThat("IP address must be '" + ipAddress + "' and not '" + readMessage.getIPAddress() + "'", readMessage.getIPAddress(), is(equalTo(ipAddress)));
		assertThat("Source port must be '" + sourcePort + "' and not '" + readMessage.getSourcePort(), readMessage.getSourcePort(), is(equalTo(sourcePort)));
		assertThat("Destination port must be '" + destPort + "' and not '" + readMessage.getSourcePort(), readMessage.getDestPort(), is(equalTo(destPort)));
		assertThat("Protocol port must be '" + protocol.getName() + "' and not '" + readMessage.getProtocol().getName(), readMessage.getProtocol(), is(equalTo(protocol)));
		assertThat("Received data must be '" + receivedIPData + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedIPData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readIPDataPacket(Inet4Address, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataPacketInterfaceNotOpenException() throws Exception {
		// Setup the resources for the test.
		Mockito.when(ipDevice.isOpen()).thenReturn(false);
		
		exception.expect(InterfaceNotOpenException.class);
		exception.expectMessage(is(equalTo("The connection interface is not open.")));
		
		// Call the method under test and verify the result.
		Whitebox.invokeMethod(ipDevice, "readIPDataPacket", (Inet4Address)null, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readIPDataPacket(Inet4Address, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataPacketNotIPTimeout() throws Exception {
		// Setup the resources for the test.
		Mockito.doAnswer(new Answer<XBeePacket>() {
			public XBeePacket answer(InvocationOnMock invocation) throws Exception {
				return null;
			}
		}).when(mockXBeePacketsQueue).getFirstIPDataPacket(Mockito.anyInt());
		
		// Call the method under test.
		IPMessage message = Whitebox.invokeMethod(ipDevice, "readIPDataPacket", (Inet4Address)null, 100);
		
		// Verify the result.
		assertThat("Message must be null", message, is(equalTo(null)));
		
		Mockito.verify(mockXBeePacketsQueue).getFirstIPDataPacket(100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readIPDataPacket(Inet4Address, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataPacketNotIP() throws Exception {
		// Setup the resources for the test.
		receivedIPData = "Received message data";
		
		// Call the method under test.
		IPMessage readMessage = Whitebox.invokeMethod(ipDevice, "readIPDataPacket", (Inet4Address)null, 100);
		
		// Verify the result.
		assertThat("Message must not be null", readMessage, is(not(equalTo(null))));
		assertThat("IP address must not be null", readMessage.getIPAddress(), is(not(equalTo(null))));
		assertThat("IP address must be '" + ipAddress + "' and not '" + readMessage.getIPAddress() + "'", readMessage.getIPAddress(), is(equalTo(ipAddress)));
		assertThat("Source port must be '" + sourcePort + "' and not '" + readMessage.getSourcePort(), readMessage.getSourcePort(), is(equalTo(sourcePort)));
		assertThat("Destination port must be '" + destPort + "' and not '" + readMessage.getSourcePort(), readMessage.getDestPort(), is(equalTo(destPort)));
		assertThat("Protocol port must be '" + protocol.getName() + "' and not '" + readMessage.getProtocol().getName(), readMessage.getProtocol(), is(equalTo(protocol)));
		assertThat("Received data must be '" + receivedIPData + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedIPData)));
		
		Mockito.verify(mockXBeePacketsQueue).getFirstIPDataPacket(100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readNetowrkDataPacket(Inet4Address, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataPacketWithIPTimeout() throws Exception {
		Mockito.doAnswer(new Answer<XBeePacket>() {
			public XBeePacket answer(InvocationOnMock invocation) throws Exception {
				return null;
			}
		}).when(mockXBeePacketsQueue).getFirstIPDataPacketFrom(Mockito.eq(ipAddress), Mockito.anyInt());
		
		// Call the method under test.
		IPMessage message = Whitebox.invokeMethod(ipDevice, "readIPDataPacket", ipAddress, 100);
		
		// Verify the result.
		assertThat("Message must be null", message, is(equalTo(null)));
		
		Mockito.verify(mockXBeePacketsQueue).getFirstIPDataPacketFrom(ipAddress, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readIPDataPacket(Inet4Address, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadIPDataPacketWithIP() throws Exception {
		// Setup the resources for the test.
		receivedIPData = "Received message data";
		
		// Call the method under test.
		IPMessage readMessage = Whitebox.invokeMethod(ipDevice, "readIPDataPacket", ipAddress, 100);
		
		// Verify the result.
		assertThat("Message must not be null", readMessage, is(not(equalTo(null))));
		assertThat("Message must not be null", readMessage, is(not(equalTo(null))));
		assertThat("IP address must not be null", readMessage.getIPAddress(), is(not(equalTo(null))));
		assertThat("IP address must be '" + ipAddress + "' and not '" + readMessage.getIPAddress() + "'", readMessage.getIPAddress(), is(equalTo(ipAddress)));
		assertThat("Source port must be '" + sourcePort + "' and not '" + readMessage.getSourcePort(), readMessage.getSourcePort(), is(equalTo(sourcePort)));
		assertThat("Destination port must be '" + destPort + "' and not '" + readMessage.getSourcePort(), readMessage.getDestPort(), is(equalTo(destPort)));
		assertThat("Protocol port must be '" + protocol.getName() + "' and not '" + readMessage.getProtocol().getName(), readMessage.getProtocol(), is(equalTo(protocol)));
		assertThat("Received data must be '" + receivedIPData + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedIPData)));
		
		Mockito.verify(mockXBeePacketsQueue).getFirstIPDataPacketFrom(ipAddress, 100);
	}
}

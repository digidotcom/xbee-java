/**
 * Copyright (c) 2016 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

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
import com.digi.xbee.api.models.IP32BitAddress;
import com.digi.xbee.api.models.NetworkMessage;
import com.digi.xbee.api.models.NetworkProtocol;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBeePacketsQueue;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.network.RXIPv4Packet;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IPDevice.class, DataReader.class})
public class IPDeviceReadDataTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Variables.
	private IPDevice ipDevice;
	
	private SerialPortRxTx mockConnectionInterface;
	
	private XBeePacketsQueue mockXBeePacketsQueue;
	
	private IP32BitAddress ipAddress;
	private String receivedNetworkData;
	
	private int sourcePort = 123;
	private int destPort = 456;
	
	private NetworkProtocol protocol = NetworkProtocol.TCP;
	
	@Before
	public void setUp() throws Exception {
		ipAddress = new IP32BitAddress("10.101.1.123");
		receivedNetworkData = "Received network data";
		
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
						protocol, receivedNetworkData.getBytes());
			}
		}).when(mockXBeePacketsQueue).getFirstNetworkDataPacketFrom(Mockito.any(IP32BitAddress.class), Mockito.anyInt());
		
		Mockito.doAnswer(new Answer<XBeePacket>() {
			public XBeePacket answer(InvocationOnMock invocation) throws Exception {
				return new RXIPv4Packet(ipAddress, destPort, sourcePort,
						protocol, receivedNetworkData.getBytes());
			}
		}).when(mockXBeePacketsQueue).getFirstNetworkDataPacket(Mockito.anyInt());
		
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
	 * Test method for {@link com.digi.xbee.api.IPDevice#readNetworkData()}.
	 */
	@Test
	public final void testReadNetworkDataInterfaceNotOpenException() {
		// Setup the resources for the test.
		Mockito.when(ipDevice.isOpen()).thenReturn(false);
		
		exception.expect(InterfaceNotOpenException.class);
		exception.expectMessage(is(equalTo("The connection interface is not open.")));
		
		// Call the method under test and verify the result.
		ipDevice.readNetworkData();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readNetowrkData()}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadNetowrkData() throws Exception {
		// Call the method under test.
		NetworkMessage readMessage = ipDevice.readNetworkData();
		
		// Verify the result.
		PowerMockito.verifyPrivate(ipDevice).invoke("readNetworkDataPacket", null, 3000);
		
		assertThat("IP address must not be null", readMessage.getIPAddress(), is(not(equalTo(null))));
		assertThat("IP address must be '" + ipAddress + "' and not '" + readMessage.getIPAddress() + "'", readMessage.getIPAddress(), is(equalTo(ipAddress)));
		assertThat("Source port must be '" + sourcePort + "' and not '" + readMessage.getSourcePort(), readMessage.getSourcePort(), is(equalTo(sourcePort)));
		assertThat("Destination port must be '" + destPort + "' and not '" + readMessage.getSourcePort(), readMessage.getDestPort(), is(equalTo(destPort)));
		assertThat("Protocol port must be '" + protocol.getName() + "' and not '" + readMessage.getProtocol().getName(), readMessage.getProtocol(), is(equalTo(protocol)));
		assertThat("Received data must be '" + receivedNetworkData + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedNetworkData)));
		assertThat("Receive message must not be broadcast", readMessage.isBroadcast(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readNetworkData(int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadNetworkDataTimeoutNegative() throws Exception {
		// Setup the resources for the test.
		int timeout = -100;
		NetworkMessage message = new NetworkMessage(ipAddress, sourcePort, destPort, protocol, new byte[0]);
		PowerMockito.doReturn(message).when(ipDevice, "readNetworkDataPacket", null, timeout);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Read timeout must be 0 or greater.")));
		
		// Call the method under test and verify the result.
		ipDevice.readNetworkData(timeout);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readNetworkData(int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadNetworkDataTimeout() throws Exception {
		// Setup the resources for the test.
		int timeout = 100;
		
		// Call the method under test.
		NetworkMessage readMessage = ipDevice.readNetworkData(timeout);
		
		// Verify the result.
		PowerMockito.verifyPrivate(ipDevice).invoke("readNetworkDataPacket", null, timeout);
		
		assertThat("IP address must not be null", readMessage.getIPAddress(), is(not(equalTo(null))));
		assertThat("IP address must be '" + ipAddress + "' and not '" + readMessage.getIPAddress() + "'", readMessage.getIPAddress(), is(equalTo(ipAddress)));
		assertThat("Source port must be '" + sourcePort + "' and not '" + readMessage.getSourcePort(), readMessage.getSourcePort(), is(equalTo(sourcePort)));
		assertThat("Destination port must be '" + destPort + "' and not '" + readMessage.getSourcePort(), readMessage.getDestPort(), is(equalTo(destPort)));
		assertThat("Protocol port must be '" + protocol.getName() + "' and not '" + readMessage.getProtocol().getName(), readMessage.getProtocol(), is(equalTo(protocol)));
		assertThat("Received data must be '" + receivedNetworkData + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedNetworkData)));
		assertThat("Receive message must not be broadcast", readMessage.isBroadcast(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readNetworkDataFrom(IP32BitAddress, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadNetworkDataFromNullIP() throws Exception {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("IP address cannot be null.")));

		// Call the method under test and verify the result.
		ipDevice.readNetworkDataFrom(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readNetworkDataFrom(IP32BitAddress))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadNetworkDataFrom() throws Exception {
		// Call the method under test.
		NetworkMessage readMessage = ipDevice.readNetworkDataFrom(ipAddress);

		// Verify the result.
		PowerMockito.verifyPrivate(ipDevice).invoke("readNetworkDataPacket", ipAddress, 3000);
		
		assertThat("IP address must not be null", readMessage.getIPAddress(), is(not(equalTo(null))));
		assertThat("IP address must be '" + ipAddress + "' and not '" + readMessage.getIPAddress() + "'", readMessage.getIPAddress(), is(equalTo(ipAddress)));
		assertThat("Source port must be '" + sourcePort + "' and not '" + readMessage.getSourcePort(), readMessage.getSourcePort(), is(equalTo(sourcePort)));
		assertThat("Destination port must be '" + destPort + "' and not '" + readMessage.getSourcePort(), readMessage.getDestPort(), is(equalTo(destPort)));
		assertThat("Protocol port must be '" + protocol.getName() + "' and not '" + readMessage.getProtocol().getName(), readMessage.getProtocol(), is(equalTo(protocol)));
		assertThat("Received data must be '" + receivedNetworkData + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedNetworkData)));
		assertThat("Receive message must not be broadcast", readMessage.isBroadcast(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readNetworkDataFrom(IP32BitAddress, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadNetworkDataFromTimeoutNegative() throws Exception {
		// Setup the resources for the test.
		int timeout = -100;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Read timeout must be 0 or greater.")));

		// Call the method under test and verify the result.
		ipDevice.readNetworkDataFrom(ipAddress, timeout);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readNetworkDataFrom(IP32BitAddress, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadNetworkDataFromTimeoutNullIP() throws Exception {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("IP address cannot be null.")));

		// Call the method under test and verify the result.
		ipDevice.readNetworkDataFrom(null, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readNetworkDataFrom(IP32BitAddress, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadNetworkDataFromTimeout() throws Exception {
		// Setup the resources for the test.
		int timeout = 100;
		
		// Call the method under test.
		NetworkMessage readMessage = ipDevice.readNetworkDataFrom(ipAddress, timeout);
		
		// Verify the result.
		PowerMockito.verifyPrivate(ipDevice).invoke("readNetworkDataPacket", ipAddress, timeout);
		
		assertThat("IP address must not be null", readMessage.getIPAddress(), is(not(equalTo(null))));
		assertThat("IP address must be '" + ipAddress + "' and not '" + readMessage.getIPAddress() + "'", readMessage.getIPAddress(), is(equalTo(ipAddress)));
		assertThat("Source port must be '" + sourcePort + "' and not '" + readMessage.getSourcePort(), readMessage.getSourcePort(), is(equalTo(sourcePort)));
		assertThat("Destination port must be '" + destPort + "' and not '" + readMessage.getSourcePort(), readMessage.getDestPort(), is(equalTo(destPort)));
		assertThat("Protocol port must be '" + protocol.getName() + "' and not '" + readMessage.getProtocol().getName(), readMessage.getProtocol(), is(equalTo(protocol)));
		assertThat("Received data must be '" + receivedNetworkData + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedNetworkData)));
		assertThat("Receive message must not be broadcast", readMessage.isBroadcast(), is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readNetworkDataPacket(IP32BitAddress, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadNetworkDataPacketInterfaceNotOpenException() throws Exception {
		// Setup the resources for the test.
		Mockito.when(ipDevice.isOpen()).thenReturn(false);
		
		exception.expect(InterfaceNotOpenException.class);
		exception.expectMessage(is(equalTo("The connection interface is not open.")));
		
		// Call the method under test and verify the result.
		Whitebox.invokeMethod(ipDevice, "readNetworkDataPacket", (IP32BitAddress)null, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readNetworkDataPacket(IP32BitAddress, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadNetworkDataPacketNotIPTimeout() throws Exception {
		// Setup the resources for the test.
		Mockito.doAnswer(new Answer<XBeePacket>() {
			public XBeePacket answer(InvocationOnMock invocation) throws Exception {
				return null;
			}
		}).when(mockXBeePacketsQueue).getFirstNetworkDataPacket(Mockito.anyInt());
		
		// Call the method under test.
		NetworkMessage message = Whitebox.invokeMethod(ipDevice, "readNetworkDataPacket", (IP32BitAddress)null, 100);
		
		// Verify the result.
		assertThat("Message must be null", message, is(equalTo(null)));
		
		Mockito.verify(mockXBeePacketsQueue).getFirstNetworkDataPacket(100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readNetworkDataPacket(IP32BitAddress, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadNetworkDataPacketNotIP() throws Exception {
		// Setup the resources for the test.
		receivedNetworkData = "Received message data";
		
		// Call the method under test.
		NetworkMessage readMessage = Whitebox.invokeMethod(ipDevice, "readNetworkDataPacket", (IP32BitAddress)null, 100);
		
		// Verify the result.
		assertThat("Message must not be null", readMessage, is(not(equalTo(null))));
		assertThat("IP address must not be null", readMessage.getIPAddress(), is(not(equalTo(null))));
		assertThat("IP address must be '" + ipAddress + "' and not '" + readMessage.getIPAddress() + "'", readMessage.getIPAddress(), is(equalTo(ipAddress)));
		assertThat("Source port must be '" + sourcePort + "' and not '" + readMessage.getSourcePort(), readMessage.getSourcePort(), is(equalTo(sourcePort)));
		assertThat("Destination port must be '" + destPort + "' and not '" + readMessage.getSourcePort(), readMessage.getDestPort(), is(equalTo(destPort)));
		assertThat("Protocol port must be '" + protocol.getName() + "' and not '" + readMessage.getProtocol().getName(), readMessage.getProtocol(), is(equalTo(protocol)));
		assertThat("Received data must be '" + receivedNetworkData + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedNetworkData)));
		assertThat("Receive message must not be broadcast", readMessage.isBroadcast(), is(equalTo(false)));
		
		Mockito.verify(mockXBeePacketsQueue).getFirstNetworkDataPacket(100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readNetowrkDataPacket(IP32BitAddress, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadNetworkDataPacketWithIPTimeout() throws Exception {
		Mockito.doAnswer(new Answer<XBeePacket>() {
			public XBeePacket answer(InvocationOnMock invocation) throws Exception {
				return null;
			}
		}).when(mockXBeePacketsQueue).getFirstNetworkDataPacketFrom(Mockito.eq(ipAddress), Mockito.anyInt());
		
		// Call the method under test.
		NetworkMessage message = Whitebox.invokeMethod(ipDevice, "readNetworkDataPacket", ipAddress, 100);
		
		// Verify the result.
		assertThat("Message must be null", message, is(equalTo(null)));
		
		Mockito.verify(mockXBeePacketsQueue).getFirstNetworkDataPacketFrom(ipAddress, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.IPDevice#readNetworkDataPacket(IP32BitAddress, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadNetworkDataPacketWithIP() throws Exception {
		// Setup the resources for the test.
		receivedNetworkData = "Received message data";
		
		// Call the method under test.
		NetworkMessage readMessage = Whitebox.invokeMethod(ipDevice, "readNetworkDataPacket", ipAddress, 100);
		
		// Verify the result.
		assertThat("Message must not be null", readMessage, is(not(equalTo(null))));
		assertThat("Message must not be null", readMessage, is(not(equalTo(null))));
		assertThat("IP address must not be null", readMessage.getIPAddress(), is(not(equalTo(null))));
		assertThat("IP address must be '" + ipAddress + "' and not '" + readMessage.getIPAddress() + "'", readMessage.getIPAddress(), is(equalTo(ipAddress)));
		assertThat("Source port must be '" + sourcePort + "' and not '" + readMessage.getSourcePort(), readMessage.getSourcePort(), is(equalTo(sourcePort)));
		assertThat("Destination port must be '" + destPort + "' and not '" + readMessage.getSourcePort(), readMessage.getDestPort(), is(equalTo(destPort)));
		assertThat("Protocol port must be '" + protocol.getName() + "' and not '" + readMessage.getProtocol().getName(), readMessage.getProtocol(), is(equalTo(protocol)));
		assertThat("Received data must be '" + receivedNetworkData + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedNetworkData)));
		assertThat("Receive message must not be broadcast", readMessage.isBroadcast(), is(equalTo(false)));
		
		Mockito.verify(mockXBeePacketsQueue).getFirstNetworkDataPacketFrom(ipAddress, 100);
	}
}

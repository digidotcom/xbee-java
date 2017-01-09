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
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;
import com.digi.xbee.api.models.XBeePacketsQueue;
import com.digi.xbee.api.models.XBeeReceiveOptions;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ReceivePacket;
import com.digi.xbee.api.packet.raw.RX16Packet;
import com.digi.xbee.api.packet.raw.RX64Packet;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBeeDevice.class, DataReader.class})
public class XBeeDeviceReadDataTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Variables.
	private XBeeDevice xbeeDevice;
	
	private SerialPortRxTx mockConnectionInterface;
	
	//private DataReader mockDataReader;
	
	private XBeePacketsQueue mockXBeePacketsQueue;
	
	private XBee64BitAddress addr64;
	private XBee16BitAddress addr16;
	private String receivedData;
	
	@Before
	public void setUp() throws Exception {
		addr64 = new XBee64BitAddress("0013A20040A820DB");
		addr16 = new XBee16BitAddress("9634");
		receivedData = "Received data";
		
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
				RemoteXBeeDevice rDevice = (RemoteXBeeDevice)invocation.getArguments()[0];
				return new ReceivePacket(rDevice.get64BitAddress(), rDevice.get16BitAddress(), 
						XBeeReceiveOptions.NONE, receivedData.getBytes());
			}
		}).when(mockXBeePacketsQueue).getFirstDataPacketFrom(Mockito.any(RemoteXBeeDevice.class), Mockito.anyInt());
		
		Mockito.doAnswer(new Answer<XBeePacket>() {
			public XBeePacket answer(InvocationOnMock invocation) throws Exception {
				return new ReceivePacket(addr64, addr16, XBeeReceiveOptions.NONE, receivedData.getBytes());
			}
		}).when(mockXBeePacketsQueue).getFirstDataPacket(Mockito.anyInt());
		
		PowerMockito.whenNew(XBeePacketsQueue.class).withAnyArguments().thenReturn(mockXBeePacketsQueue);
		
		xbeeDevice = PowerMockito.spy(new XBeeDevice(mockConnectionInterface));
		
		Mockito.doReturn(OperatingMode.API).when(xbeeDevice).determineOperatingMode();
		Mockito.doNothing().when(xbeeDevice).readDeviceInfo();
		
		xbeeDevice.open();
	}
	
	@After
	public void tearDown() throws Exception {
		//mockDataReader.stopReader();
		xbeeDevice.close();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readData()}.
	 */
	@Test
	public final void testReadDataInterfaceNotOpenException() {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.isOpen()).thenReturn(false);
		
		exception.expect(InterfaceNotOpenException.class);
		exception.expectMessage(is(equalTo("The connection interface is not open.")));
		
		// Call the method under test and verify the result.
		xbeeDevice.readData();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readData()}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadData() throws Exception {
		// Setup the resources for the test.
		
		assertThat("Network should be empty", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		XBeeMessage readMessage = xbeeDevice.readData();
		
		// Verify the result.
		PowerMockito.verifyPrivate(xbeeDevice).invoke("readDataPacket", null, 3000);
		
		RemoteXBeeDevice src = readMessage.getDevice();
		assertThat("Source remote device must not be null", src, is(not(equalTo(null))));
		assertThat("64-bit address must be '" + addr64 + "' and not '" + src.get64BitAddress() + "'", src.get64BitAddress(), is(equalTo(addr64)));
		assertThat("16-bit address must be '" + addr16 + "' and not '" + src.get16BitAddress() + "'", src.get16BitAddress(), is(equalTo(addr16)));
		
		assertThat("Received data must be '" + receivedData + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedData)));
		assertThat("Receive message must not be broadcast", readMessage.isBroadcast(), is(equalTo(false)));
		
		assertThat("Network should contain only 1 device", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readData(int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadDataTimeoutNegative() throws Exception {
		// Setup the resources for the test.
		int timeout = -100;
		XBeeMessage message = new XBeeMessage(new RemoteXBeeDevice(xbeeDevice, XBee64BitAddress.COORDINATOR_ADDRESS), new byte[0]);
		PowerMockito.doReturn(message).when(xbeeDevice, "readDataPacket", null, timeout);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Read timeout must be 0 or greater.")));
		
		// Call the method under test and verify the result.
		xbeeDevice.readData(timeout);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readData(int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadDataTimeout() throws Exception {
		// Setup the resources for the test.
		int timeout = 100;
		
		assertThat("Network should be empty", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		XBeeMessage readMessage = xbeeDevice.readData(timeout);
		
		// Verify the result.
		PowerMockito.verifyPrivate(xbeeDevice).invoke("readDataPacket", null, timeout);
		
		RemoteXBeeDevice src = readMessage.getDevice();
		assertThat("Source remote device must not be null", src, is(not(equalTo(null))));
		assertThat("64-bit address must be '" + addr64 + "' and not '" + src.get64BitAddress() + "'", src.get64BitAddress(), is(equalTo(addr64)));
		assertThat("16-bit address must be '" + addr16 + "' and not '" + src.get16BitAddress() + "'", src.get16BitAddress(), is(equalTo(addr16)));
		
		assertThat("Received data must be '" + receivedData + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedData)));
		assertThat("Receive message must not be broadcast", readMessage.isBroadcast(), is(equalTo(false)));
		
		assertThat("Network should contain only 1 device", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDataFrom(RemoteXBeeDevice, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadDataFromNullRemoteDevice() throws Exception {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Remote XBee device cannot be null.")));

		// Call the method under test and verify the result.
		xbeeDevice.readDataFrom(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDataFrom(RemoteXBeeDevice))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadDataFrom() throws Exception {
		// Setup the resources for the test.
		addr64 = XBee64BitAddress.COORDINATOR_ADDRESS;
		addr16 = XBee16BitAddress.UNKNOWN_ADDRESS;
		RemoteXBeeDevice remoteDevice = new RemoteXBeeDevice(xbeeDevice, addr64, addr16, null);

		assertThat("Network should be empty", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		XBeeMessage readMessage = xbeeDevice.readDataFrom(remoteDevice);

		// Verify the result.
		PowerMockito.verifyPrivate(xbeeDevice).invoke("readDataPacket", remoteDevice, 3000);
		
		RemoteXBeeDevice src = readMessage.getDevice();
		assertThat("Source remote device must not be null", src, is(not(equalTo(null))));
		assertThat("64-bit address must be '" + addr64 + "' and not '" + src.get64BitAddress() + "'", src.get64BitAddress(), is(equalTo(addr64)));
		assertThat("16-bit address must be '" + addr16 + "' and not '" + src.get16BitAddress() + "'", src.get16BitAddress(), is(equalTo(addr16)));
		
		assertThat("Received data must be '" + receivedData + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedData)));
		assertThat("Receive message must not be broadcast", readMessage.isBroadcast(), is(equalTo(false)));
		
		assertThat("Network should contain only 1 device", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDataFrom(RemoteXBeeDevice, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadDataFromTimeoutNegative() throws Exception {
		// Setup the resources for the test.
		int timeout = -100;
		RemoteXBeeDevice remoteDevice = new RemoteXBeeDevice(xbeeDevice, XBee64BitAddress.COORDINATOR_ADDRESS);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Read timeout must be 0 or greater.")));

		// Call the method under test and verify the result.
		xbeeDevice.readDataFrom(remoteDevice, timeout);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDataFrom(RemoteXBeeDevice, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadDataFromTimeoutNullRemoteDevice() throws Exception {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Remote XBee device cannot be null.")));

		// Call the method under test and verify the result.
		xbeeDevice.readDataFrom(null, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDataFrom(RemoteXBeeDevice, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadDataFromTimeout() throws Exception {
		// Setup the resources for the test.
		int timeout = 100;
		addr64 = XBee64BitAddress.COORDINATOR_ADDRESS;
		addr16 = XBee16BitAddress.UNKNOWN_ADDRESS;
		RemoteXBeeDevice remoteDevice = new RemoteXBeeDevice(xbeeDevice, addr64, addr16, null);
		
		assertThat("Network should be empty", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		XBeeMessage readMessage = xbeeDevice.readDataFrom(remoteDevice, timeout);
		
		// Verify the result.
		PowerMockito.verifyPrivate(xbeeDevice).invoke("readDataPacket", remoteDevice, timeout);
		
		//assertThat(readMessage, is(equalTo(message)));
		
		//assertThat("Network should contain only 1 device", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(1)));
		RemoteXBeeDevice src = readMessage.getDevice();
		assertThat("Source remote device must not be null", src, is(not(equalTo(null))));
		assertThat("64-bit address must be '" + addr64 + "' and not '" + src.get64BitAddress() + "'", src.get64BitAddress(), is(equalTo(addr64)));
		assertThat("16-bit address must be '" + addr16 + "' and not '" + src.get16BitAddress() + "'", src.get16BitAddress(), is(equalTo(addr16)));
		
		assertThat("Received data must be '" + receivedData + "' and not '" + readMessage.getDataString() + "'", readMessage.getDataString(), is(equalTo(receivedData)));
		assertThat("Receive message must not be broadcast", readMessage.isBroadcast(), is(equalTo(false)));
		
		assertThat("Network should contain only 1 device", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDataPacket(RemoteXBeeDevice, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadDataPacketInterfaceNotOpenException() throws Exception {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.isOpen()).thenReturn(false);
		
		exception.expect(InterfaceNotOpenException.class);
		exception.expectMessage(is(equalTo("The connection interface is not open.")));
		
		// Call the method under test and verify the result.
		Whitebox.invokeMethod(xbeeDevice, "readDataPacket", (RemoteXBeeDevice)null, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDataPacket(RemoteXBeeDevice, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadDataPacketNotRemoteDeviceTimeout() throws Exception {
		// Setup the resources for the test.
		Mockito.doAnswer(new Answer<XBeePacket>() {
			public XBeePacket answer(InvocationOnMock invocation) throws Exception {
				return null;
			}
		}).when(mockXBeePacketsQueue).getFirstDataPacket(Mockito.anyInt());
		
		assertThat("Network should be empty", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		XBeeMessage message = Whitebox.invokeMethod(xbeeDevice, "readDataPacket", (RemoteXBeeDevice)null, 100);
		
		// Verify the result.
		assertThat("Message must be null", message, is(equalTo(null)));
		
		assertThat("Network should be empty", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(0)));
		
		Mockito.verify(mockXBeePacketsQueue).getFirstDataPacket(100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDataPacket(RemoteXBeeDevice, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadDataPacketNotRemoteDevice() throws Exception {
		// Setup the resources for the test.
		addr64 = new XBee64BitAddress("0013A20040A820DB");
		addr16 = new XBee16BitAddress("9634");
		receivedData = "Received message data";
		
		assertThat("Network should be empty", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		XBeeMessage message = Whitebox.invokeMethod(xbeeDevice, "readDataPacket", (RemoteXBeeDevice)null, 100);
		
		// Verify the result.
		assertThat("Message must not be null", message, is(not(equalTo(null))));
		
		RemoteXBeeDevice src = message.getDevice();
		assertThat("Source remote device must not be null", src, is(not(equalTo(null))));
		assertThat("64-bit address must be '" + addr64 + "' and not '" + src.get64BitAddress() + "'", src.get64BitAddress(), is(equalTo(addr64)));
		assertThat("16-bit address must be '" + addr16 + "' and not '" + src.get16BitAddress() + "'", src.get16BitAddress(), is(equalTo(addr16)));
		
		assertThat("Received data must be '" + receivedData + "' and not '" + message.getDataString() + "'", message.getDataString(), is(equalTo(receivedData)));
		assertThat("Receive message must not be broadcast", message.isBroadcast(), is(equalTo(false)));
		
		assertThat("Network should contain only 1 device", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(1)));
		
		Mockito.verify(mockXBeePacketsQueue).getFirstDataPacket(100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDataPacket(RemoteXBeeDevice, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadDataPacketWithRemoteDeviceTimeout() throws Exception {
		// Setup the resources for the test.
		RemoteXBeeDevice rDevice = new RemoteXBeeDevice(xbeeDevice, 
				new XBee64BitAddress("0013A20040A820DB"), new XBee16BitAddress("9634"), "identifier");
		
		Mockito.doAnswer(new Answer<XBeePacket>() {
			public XBeePacket answer(InvocationOnMock invocation) throws Exception {
				return null;
			}
		}).when(mockXBeePacketsQueue).getFirstDataPacketFrom(Mockito.eq(rDevice), Mockito.anyInt());
		
		assertThat("Network should be empty", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		XBeeMessage message = Whitebox.invokeMethod(xbeeDevice, "readDataPacket", rDevice, 100);
		
		// Verify the result.
		assertThat("Message must be null", message, is(equalTo(null)));
		
		assertThat("Network should be empty", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(0)));
		
		Mockito.verify(mockXBeePacketsQueue).getFirstDataPacketFrom(rDevice, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDataPacket(RemoteXBeeDevice, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadDataPacketWithRemoteDevice() throws Exception {
		// Setup the resources for the test.
		addr64 = new XBee64BitAddress("0013A20040A820DB");
		addr16 = new XBee16BitAddress("9634");
		receivedData = "Received message data";
		RemoteXBeeDevice rDevice = new RemoteXBeeDevice(xbeeDevice, addr64, addr16, "identifier");
		
		assertThat("Network should be empty", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		XBeeMessage message = Whitebox.invokeMethod(xbeeDevice, "readDataPacket", rDevice, 100);
		
		// Verify the result.
		assertThat("Message must not be null", message, is(not(equalTo(null))));
		
		RemoteXBeeDevice src = message.getDevice();
		assertThat("Source remote device must not be null", src, is(not(equalTo(null))));
		assertThat("64-bit address must be '" + rDevice.get64BitAddress() + "' and not '" + src.get64BitAddress() + "'", 
				src.get64BitAddress(), is(equalTo(rDevice.get64BitAddress())));
		assertThat("16-bit address must be '" + rDevice.get16BitAddress() + "' and not '" + src.get16BitAddress() + "'", 
				src.get16BitAddress(), is(equalTo(rDevice.get16BitAddress())));
		
		assertThat("Received data must be '" + receivedData + "' and not '" + message.getDataString() + "'", message.getDataString(), is(equalTo(receivedData)));
		assertThat("Receive message must not be broadcast", message.isBroadcast(), is(equalTo(false)));
		
		assertThat("Network should contain only 1 device", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(1)));
		
		Mockito.verify(mockXBeePacketsQueue).getFirstDataPacketFrom(rDevice, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDataPacket(RemoteXBeeDevice, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadDataPacketWithRemoteDeviceRX16Packet() throws Exception {
		// Setup the resources for the test.
		RemoteXBeeDevice rDevice = new RemoteXBeeDevice(xbeeDevice, 
				new XBee64BitAddress("0013A20040A820DB"), new XBee16BitAddress("9634"), "identifier");
		final String data = "Received message data";
		
		Mockito.doAnswer(new Answer<XBeePacket>() {
			public XBeePacket answer(InvocationOnMock invocation) throws Exception {
				RemoteXBeeDevice rDevice = (RemoteXBeeDevice)invocation.getArguments()[0];
				return new RX16Packet(rDevice.get16BitAddress(), 0x49, XBeeReceiveOptions.NONE, data.getBytes());
			}
		}).when(mockXBeePacketsQueue).getFirstDataPacketFrom(Mockito.any(RemoteXBeeDevice.class), Mockito.anyInt());
		
		assertThat("Network should be empty", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		XBeeMessage message = Whitebox.invokeMethod(xbeeDevice, "readDataPacket", rDevice, 100);
		
		// Verify the result.
		assertThat("Message must not be null", message, is(not(equalTo(null))));
		
		RemoteXBeeDevice src = message.getDevice();
		assertThat("Source remote device must not be null", src, is(not(equalTo(null))));
		assertThat("64-bit address must be '" + rDevice.get64BitAddress() + "' and not '" + src.get64BitAddress() + "'", 
				src.get64BitAddress(), is(equalTo(rDevice.get64BitAddress())));
		assertThat("16-bit address must be '" + rDevice.get16BitAddress() + "' and not '" + src.get16BitAddress() + "'", 
				src.get16BitAddress(), is(equalTo(rDevice.get16BitAddress())));
		
		assertThat("Received data must be '" + data + "' and not '" + message.getDataString() + "'", message.getDataString(), is(equalTo(data)));
		assertThat("Receive message must not be broadcast", message.isBroadcast(), is(equalTo(false)));
		
		assertThat("Network should contain only 1 device", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(1)));
		
		Mockito.verify(mockXBeePacketsQueue).getFirstDataPacketFrom(rDevice, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDataPacket(RemoteXBeeDevice, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadDataPacketWithRemoteDeviceRX64Packet() throws Exception {
		// Setup the resources for the test.
		RemoteXBeeDevice rDevice = new RemoteXBeeDevice(xbeeDevice, 
				new XBee64BitAddress("0013A20040A820DB"), new XBee16BitAddress("9634"), "identifier");
		final String data = "Received message data";
		
		Mockito.doAnswer(new Answer<XBeePacket>() {
			public XBeePacket answer(InvocationOnMock invocation) throws Exception {
				RemoteXBeeDevice rDevice = (RemoteXBeeDevice)invocation.getArguments()[0];
				return new RX64Packet(rDevice.get64BitAddress(), 0x49, XBeeReceiveOptions.NONE, data.getBytes());
			}
		}).when(mockXBeePacketsQueue).getFirstDataPacketFrom(Mockito.any(RemoteXBeeDevice.class), Mockito.anyInt());
		
		assertThat("Network should be empty", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		XBeeMessage message = Whitebox.invokeMethod(xbeeDevice, "readDataPacket", rDevice, 100);
		
		// Verify the result.
		assertThat("Message must not be null", message, is(not(equalTo(null))));
		
		RemoteXBeeDevice src = message.getDevice();
		assertThat("Source remote device must not be null", src, is(not(equalTo(null))));
		assertThat("64-bit address must be '" + rDevice.get64BitAddress() + "' and not '" + src.get64BitAddress() + "'", 
				src.get64BitAddress(), is(equalTo(rDevice.get64BitAddress())));
		assertThat("16-bit address must be '" + rDevice.get16BitAddress() + "' and not '" + src.get16BitAddress() + "'", 
				src.get16BitAddress(), is(equalTo(rDevice.get16BitAddress())));
		
		assertThat("Received data must be '" + data + "' and not '" + message.getDataString() + "'", message.getDataString(), is(equalTo(data)));
		assertThat("Receive message must not be broadcast", message.isBroadcast(), is(equalTo(false)));
		
		assertThat("Network should contain only 1 device", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(1)));
		
		Mockito.verify(mockXBeePacketsQueue).getFirstDataPacketFrom(rDevice, 100);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#readDataPacket(RemoteXBeeDevice, int))}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testReadDataPacketWithRemoteDeviceAlreadyInTheNetwork() throws Exception {
		// Setup the resources for the test.
		addr64 = new XBee64BitAddress("0013A20040A820DB");
		addr16 = new XBee16BitAddress("9634");
		receivedData = "Received message data";
		RemoteXBeeDevice rDevice = new RemoteXBeeDevice(xbeeDevice, addr64, addr16, null);
		
		RemoteXBeeDevice deviceInNetwork = new RemoteXBeeDevice(xbeeDevice, addr64, new XBee16BitAddress("5986"), "identifier");
		xbeeDevice.getNetwork().addRemoteDevice(deviceInNetwork);
		
		assertThat("Network should contain 1 device", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(1)));
		
		// Call the method under test.
		XBeeMessage message = Whitebox.invokeMethod(xbeeDevice, "readDataPacket", rDevice, 100);
		
		// Verify the result.
		assertThat("Message must not be null", message, is(not(equalTo(null))));
		
		RemoteXBeeDevice src = message.getDevice();
		assertThat("Source remote device must not be null", src, is(not(equalTo(null))));
		assertThat("64-bit address must be '" + deviceInNetwork.get64BitAddress() + "' and not '" + src.get64BitAddress() + "'", 
				src.get64BitAddress(), is(equalTo(deviceInNetwork.get64BitAddress())));
		assertThat("16-bit address must be '" + deviceInNetwork.get16BitAddress() + "' and not '" + src.get16BitAddress() + "'", 
				src.get16BitAddress(), is(equalTo(deviceInNetwork.get16BitAddress())));
		assertThat("Node ID must be '" + deviceInNetwork.getNodeID() + "' and not '" + src.getNodeID() + "'", 
				src.getNodeID(), is(equalTo(deviceInNetwork.getNodeID())));
		
		assertThat("Received data must be '" + receivedData + "' and not '" + message.getDataString() + "'", message.getDataString(), is(equalTo(receivedData)));
		assertThat("Receive message must not be broadcast", message.isBroadcast(), is(equalTo(false)));
		
		assertThat("Network should contain only 1 device", xbeeDevice.getNetwork().getNumberOfDevices(), is(equalTo(1)));
		
		Mockito.verify(mockXBeePacketsQueue).getFirstDataPacketFrom(rDevice, 100);
	}
}

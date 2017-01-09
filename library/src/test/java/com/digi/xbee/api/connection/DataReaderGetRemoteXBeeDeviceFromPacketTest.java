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
package com.digi.xbee.api.connection;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.RemoteDigiMeshDevice;
import com.digi.xbee.api.RemoteDigiPointDevice;
import com.digi.xbee.api.RemoteRaw802Device;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.RemoteZigBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.ModemStatusEvent;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.models.XBeeReceiveOptions;
import com.digi.xbee.api.packet.UnknownXBeePacket;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.common.IODataSampleRxIndicatorPacket;
import com.digi.xbee.api.packet.common.ModemStatusPacket;
import com.digi.xbee.api.packet.common.ReceivePacket;
import com.digi.xbee.api.packet.raw.RX16IOPacket;
import com.digi.xbee.api.packet.raw.RX16Packet;
import com.digi.xbee.api.packet.raw.RX64IOPacket;
import com.digi.xbee.api.packet.raw.RX64Packet;

@RunWith(PowerMockRunner.class)
public class DataReaderGetRemoteXBeeDeviceFromPacketTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Variables.
	private DataReader dataReader;
	
	private IConnectionInterface mockConnectionInterface;
	private XBeeDevice xbeeDevice;
	
	private XBeeNetwork network;
	
	@Before
	public void setUp() {
		mockConnectionInterface = Mockito.mock(IConnectionInterface.class);
		
		xbeeDevice = PowerMockito.spy(new XBeeDevice(mockConnectionInterface));
		Mockito.when(xbeeDevice.isOpen()).thenReturn(true);
		
		dataReader = PowerMockito.spy(new DataReader(mockConnectionInterface, OperatingMode.API, xbeeDevice));
		
		network = PowerMockito.spy(xbeeDevice.getNetwork());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPacketNullPacket() throws XBeeException {
		// Setup the resources for the test.
		XBeeAPIPacket packet = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("XBee API packet cannot be null.")));
		
		// Call the method under test and verify the result.
		dataReader.getRemoteXBeeDeviceFromPacket(packet);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPacketNotValidFrameType() throws XBeeException {
		// Setup the resources for the test.
		UnknownXBeePacket packet = new UnknownXBeePacket(0x25, new byte[0]);
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device must be null", remoteDevice, is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPacketExceptionWhenAddingToNetwork() throws XBeeException {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_MESH);
		Mockito.doThrow(new OperationNotSupportedException()).when(network).getDevice(Mockito.any(XBee16BitAddress.class));
		
		RX16Packet packet = new RX16Packet(new XBee16BitAddress("1234"), 
				0x49, XBeeReceiveOptions.NONE, new byte[0]);
		
		exception.expect(OperationNotSupportedException.class);
		exception.expectMessage(is(equalTo("DigiMesh protocol does not support 16-bit addressing.")));
		
		// Call the method under test and verify the result.
		dataReader.getRemoteXBeeDeviceFromPacket(packet);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPacketModemStatusPacket() throws XBeeException {
		// Setup the resources for the test.
		ModemStatusPacket packet = new ModemStatusPacket(ModemStatusEvent.STATUS_DISASSOCIATED);
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device must be null", remoteDevice, is(equalTo(null)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPacketReceivePacketZigBee() throws Exception {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		XBee64BitAddress addr64 = new XBee64BitAddress("0013A20040A6A0DB");
		XBee16BitAddress addr16 = new XBee16BitAddress("1234");
		ReceivePacket packet = new ReceivePacket(addr64, addr16, XBeeReceiveOptions.NONE, new byte[0]);
		
		assertThat("The network must contain 0 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device should not be null", remoteDevice, is(not(equalTo(null))));
		
		assertThat("Returned remote device must be a ZigBee device", remoteDevice instanceof RemoteZigBeeDevice, is(equalTo(true)));
		assertThat("Returned remote device 64-bit address must be '" + addr64 + "' and not '" + remoteDevice.get64BitAddress() + "'", 
				remoteDevice.get64BitAddress(), is(equalTo(addr64)));
		assertThat("Returned remote device 16-bit address must be '" + addr16 + "' and not '" + remoteDevice.get16BitAddress() + "'", 
				remoteDevice.get16BitAddress(), is(equalTo(addr16)));
		assertThat("Returned remote device NI must be 'null' and not '" + remoteDevice.getNodeID() + "'", 
				remoteDevice.getNodeID(), is(equalTo(null)));
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPacketReceivePacketZigBeeDeviceAlreadyInNetwork() throws XBeeException {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		String ni = "myRemoteZigBee";
		XBee64BitAddress addr64 = new XBee64BitAddress("0013A20040A6A0DB");
		XBee16BitAddress addr16 = new XBee16BitAddress("1234");
		ReceivePacket packet = new ReceivePacket(addr64, addr16, XBeeReceiveOptions.NONE, new byte[0]);
		
		network.addRemoteDevice(new RemoteZigBeeDevice(xbeeDevice, addr64, addr16, ni));
		
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device should not be null", remoteDevice, is(not(equalTo(null))));
		
		assertThat("Returned remote device must be a ZigBee device", remoteDevice instanceof RemoteZigBeeDevice, is(equalTo(true)));
		assertThat("Returned remote device 64-bit address must be '" + addr64 + "' and not '" + remoteDevice.get64BitAddress() + "'", 
				remoteDevice.get64BitAddress(), is(equalTo(addr64)));
		assertThat("Returned remote device 16-bit address must be '" + addr16 + "' and not '" + remoteDevice.get16BitAddress() + "'", 
				remoteDevice.get16BitAddress(), is(equalTo(addr16)));
		assertThat("Returned remote device NI must be '" + ni + "' and not '" + remoteDevice.getNodeID() + "'", 
				remoteDevice.getNodeID(), is(equalTo(ni)));
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPacketIODataSampleRxIndicatorPacketZigBee() throws XBeeException {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		XBee64BitAddress addr64 = new XBee64BitAddress("0013A20040A6A0DB");
		XBee16BitAddress addr16 = new XBee16BitAddress("1234");
		IODataSampleRxIndicatorPacket packet = new IODataSampleRxIndicatorPacket(
				addr64, addr16, XBeeReceiveOptions.NONE, new byte[] {0x00, 0x01, 0x02, 0x03, 0x04});
		
		assertThat("The network must contain 0 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device should not be null", remoteDevice, is(not(equalTo(null))));
		
		assertThat("Returned remote device must be a ZigBee device", remoteDevice instanceof RemoteZigBeeDevice, is(equalTo(true)));
		assertThat("Returned remote device 64-bit address must be '" + addr64 + "' and not '" + remoteDevice.get64BitAddress() + "'", 
				remoteDevice.get64BitAddress(), is(equalTo(addr64)));
		assertThat("Returned remote device 16-bit address must be '" + addr16 + "' and not '" + remoteDevice.get16BitAddress() + "'", 
				remoteDevice.get16BitAddress(), is(equalTo(addr16)));
		assertThat("Returned remote device NI must be 'null' and not '" + remoteDevice.getNodeID() + "'", 
				remoteDevice.getNodeID(), is(equalTo(null)));
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPacketIODataSampleRxIndicatorPacketZigBeeDeviceAlreadyInNetwork() throws XBeeException {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		String ni = "myRemoteZigBee";
		XBee64BitAddress addr64 = new XBee64BitAddress("0013A20040A6A0DB");
		XBee16BitAddress addr16 = new XBee16BitAddress("1234");
		IODataSampleRxIndicatorPacket packet = new IODataSampleRxIndicatorPacket(
				addr64, addr16, XBeeReceiveOptions.NONE, new byte[] {0x00, 0x01, 0x02, 0x03, 0x04});
		
		network.addRemoteDevice(new RemoteZigBeeDevice(xbeeDevice, addr64, addr16, ni));
		
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device should not be null", remoteDevice, is(not(equalTo(null))));
		
		assertThat("Returned remote device must be a ZigBee device", remoteDevice instanceof RemoteZigBeeDevice, is(equalTo(true)));
		assertThat("Returned remote device 64-bit address must be '" + addr64 + "' and not '" + remoteDevice.get64BitAddress() + "'", 
				remoteDevice.get64BitAddress(), is(equalTo(addr64)));
		assertThat("Returned remote device 16-bit address must be '" + addr16 + "' and not '" + remoteDevice.get16BitAddress() + "'", 
				remoteDevice.get16BitAddress(), is(equalTo(addr16)));
		assertThat("Returned remote device NI must be '" + ni + "' and not '" + remoteDevice.getNodeID() + "'", 
				remoteDevice.getNodeID(), is(equalTo(ni)));
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPacketReceivePacketDigiMesh() throws XBeeException {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_MESH);
		
		XBee64BitAddress addr64 = new XBee64BitAddress("0013A20040A6A0DB");
		XBee16BitAddress addr16 = new XBee16BitAddress("FFFE");
		ReceivePacket packet = new ReceivePacket(addr64, addr16, XBeeReceiveOptions.NONE, new byte[0]);
		
		assertThat("The network must contain 0 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device should not be null", remoteDevice, is(not(equalTo(null))));
		
		assertThat("Returned remote device must be a DigiMesh device", remoteDevice instanceof RemoteDigiMeshDevice, is(equalTo(true)));
		assertThat("Returned remote device 64-bit address must be '" + addr64 + "' and not '" + remoteDevice.get64BitAddress() + "'", 
				remoteDevice.get64BitAddress(), is(equalTo(addr64)));
		assertThat("Returned remote device 16-bit address must be '" + addr16 + "' and not '" + remoteDevice.get16BitAddress() + "'", 
				remoteDevice.get16BitAddress(), is(equalTo(addr16)));
		assertThat("Returned remote device NI must be 'null' and not '" + remoteDevice.getNodeID() + "'", 
				remoteDevice.getNodeID(), is(equalTo(null)));
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPacketReceivePacketDigiMeshDeviceAlreadyInNetwork() throws XBeeException {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_MESH);
		
		String ni = "myRemoteDigiMesh";
		XBee64BitAddress addr64 = new XBee64BitAddress("0013A20040A6A0DB");
		XBee16BitAddress addr16 = new XBee16BitAddress("FFFE");
		ReceivePacket packet = new ReceivePacket(addr64, addr16, XBeeReceiveOptions.NONE, new byte[0]);
		
		network.addRemoteDevice(new RemoteDigiMeshDevice(xbeeDevice, addr64, ni));
		
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device should not be null", remoteDevice, is(not(equalTo(null))));
		
		assertThat("Returned remote device must be a DigiMesh device", remoteDevice instanceof RemoteDigiMeshDevice, is(equalTo(true)));
		assertThat("Returned remote device 64-bit address must be '" + addr64 + "' and not '" + remoteDevice.get64BitAddress() + "'", 
				remoteDevice.get64BitAddress(), is(equalTo(addr64)));
		assertThat("Returned remote device 16-bit address must be '" + addr16 + "' and not '" + remoteDevice.get16BitAddress() + "'", 
				remoteDevice.get16BitAddress(), is(equalTo(addr16)));
		assertThat("Returned remote device NI must be '" + ni + "' and not '" + remoteDevice.getNodeID() + "'", 
				remoteDevice.getNodeID(), is(equalTo(ni)));
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPacketIODataSampleRxIndicatorPacketDigiMesh() throws XBeeException {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_MESH);
		
		XBee64BitAddress addr64 = new XBee64BitAddress("0013A20040A6A0DB");
		XBee16BitAddress addr16 = new XBee16BitAddress("FFFE");
		IODataSampleRxIndicatorPacket packet = new IODataSampleRxIndicatorPacket(
				addr64, addr16, XBeeReceiveOptions.NONE, new byte[] {0x00, 0x01, 0x02, 0x03, 0x04});
		
		assertThat("The network must contain 0 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device should not be null", remoteDevice, is(not(equalTo(null))));
		
		assertThat("Returned remote device must be a DigiMesh device", remoteDevice instanceof RemoteDigiMeshDevice, is(equalTo(true)));
		assertThat("Returned remote device 64-bit address must be '" + addr64 + "' and not '" + remoteDevice.get64BitAddress() + "'", 
				remoteDevice.get64BitAddress(), is(equalTo(addr64)));
		assertThat("Returned remote device 16-bit address must be '" + addr16 + "' and not '" + remoteDevice.get16BitAddress() + "'", 
				remoteDevice.get16BitAddress(), is(equalTo(addr16)));
		assertThat("Returned remote device NI must be 'null' and not '" + remoteDevice.getNodeID() + "'", 
				remoteDevice.getNodeID(), is(equalTo(null)));
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPacketIODataSampleRxIndicatorPackettDigiMeshDeviceAlreadyInNetwork() throws XBeeException {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_MESH);
		
		String ni = "myRemoteDigiMesh";
		XBee64BitAddress addr64 = new XBee64BitAddress("0013A20040A6A0DB");
		XBee16BitAddress addr16 = new XBee16BitAddress("FFFE");
		IODataSampleRxIndicatorPacket packet = new IODataSampleRxIndicatorPacket(
				addr64, addr16, XBeeReceiveOptions.NONE, new byte[] {0x00, 0x01, 0x02, 0x03, 0x04});
		
		network.addRemoteDevice(new RemoteDigiMeshDevice(xbeeDevice, addr64, ni));
		
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device should not be null", remoteDevice, is(not(equalTo(null))));
		
		assertThat("Returned remote device must be a DigiMesh device", remoteDevice instanceof RemoteDigiMeshDevice, is(equalTo(true)));
		assertThat("Returned remote device 64-bit address must be '" + addr64 + "' and not '" + remoteDevice.get64BitAddress() + "'", 
				remoteDevice.get64BitAddress(), is(equalTo(addr64)));
		assertThat("Returned remote device 16-bit address must be '" + addr16 + "' and not '" + remoteDevice.get16BitAddress() + "'", 
				remoteDevice.get16BitAddress(), is(equalTo(addr16)));
		assertThat("Returned remote device NI must be '" + ni + "' and not '" + remoteDevice.getNodeID() + "'", 
				remoteDevice.getNodeID(), is(equalTo(ni)));
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPacketReceivePacketDigiPoint() throws XBeeException {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_POINT);
		
		XBee64BitAddress addr64 = new XBee64BitAddress("0013A20040A6A0DB");
		XBee16BitAddress addr16 = new XBee16BitAddress("FFFE");
		ReceivePacket packet = new ReceivePacket(addr64, addr16, XBeeReceiveOptions.NONE, new byte[0]);
		
		assertThat("The network must contain 0 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device should not be null", remoteDevice, is(not(equalTo(null))));
		
		assertThat("Returned remote device must be a DigiPoint device", remoteDevice instanceof RemoteDigiPointDevice, is(equalTo(true)));
		assertThat("Returned remote device 64-bit address must be '" + addr64 + "' and not '" + remoteDevice.get64BitAddress() + "'", 
				remoteDevice.get64BitAddress(), is(equalTo(addr64)));
		assertThat("Returned remote device 16-bit address must be '" + addr16 + "' and not '" + remoteDevice.get16BitAddress() + "'", 
				remoteDevice.get16BitAddress(), is(equalTo(addr16)));
		assertThat("Returned remote device NI must be 'null' and not '" + remoteDevice.getNodeID() + "'", 
				remoteDevice.getNodeID(), is(equalTo(null)));
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPacketReceivePacketDigiPointDeviceAlreadyInNetwork() throws XBeeException {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_POINT);
		
		String ni = "myRemoteDigiPoint";
		XBee64BitAddress addr64 = new XBee64BitAddress("0013A20040A6A0DB");
		XBee16BitAddress addr16 = new XBee16BitAddress("FFFE");
		ReceivePacket packet = new ReceivePacket(addr64, addr16, XBeeReceiveOptions.NONE, new byte[0]);
		
		network.addRemoteDevice(new RemoteDigiPointDevice(xbeeDevice, addr64, ni));
		
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device should not be null", remoteDevice, is(not(equalTo(null))));
		
		assertThat("Returned remote device must be a DigiPoint device", remoteDevice instanceof RemoteDigiPointDevice, is(equalTo(true)));
		assertThat("Returned remote device 64-bit address must be '" + addr64 + "' and not '" + remoteDevice.get64BitAddress() + "'", 
				remoteDevice.get64BitAddress(), is(equalTo(addr64)));
		assertThat("Returned remote device 16-bit address must be '" + addr16 + "' and not '" + remoteDevice.get16BitAddress() + "'", 
				remoteDevice.get16BitAddress(), is(equalTo(addr16)));
		assertThat("Returned remote device NI must be '" + ni + "' and not '" + remoteDevice.getNodeID() + "'", 
				remoteDevice.getNodeID(), is(equalTo(ni)));
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPacketIODataSampleRxIndicatorPacketDigiPoint() throws XBeeException {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_POINT);
		
		XBee64BitAddress addr64 = new XBee64BitAddress("0013A20040A6A0DB");
		XBee16BitAddress addr16 = new XBee16BitAddress("FFFE");
		IODataSampleRxIndicatorPacket packet = new IODataSampleRxIndicatorPacket(
				addr64, addr16, XBeeReceiveOptions.NONE, new byte[] {0x00, 0x01, 0x02, 0x03, 0x04});
		
		assertThat("The network must contain 0 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device should not be null", remoteDevice, is(not(equalTo(null))));
		
		assertThat("Returned remote device must be a DigiPoint device", remoteDevice instanceof RemoteDigiPointDevice, is(equalTo(true)));
		assertThat("Returned remote device 64-bit address must be '" + addr64 + "' and not '" + remoteDevice.get64BitAddress() + "'", 
				remoteDevice.get64BitAddress(), is(equalTo(addr64)));
		assertThat("Returned remote device 16-bit address must be '" + addr16 + "' and not '" + remoteDevice.get16BitAddress() + "'", 
				remoteDevice.get16BitAddress(), is(equalTo(addr16)));
		assertThat("Returned remote device NI must be 'null' and not '" + remoteDevice.getNodeID() + "'", 
				remoteDevice.getNodeID(), is(equalTo(null)));
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPacketIODataSampleRxIndicatorPacketDigiPointDeviceAlreadyInNetwork() throws XBeeException {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_POINT);
		
		String ni = "myRemoteDigiPoint";
		XBee64BitAddress addr64 = new XBee64BitAddress("0013A20040A6A0DB");
		XBee16BitAddress addr16 = new XBee16BitAddress("FFFE");
		IODataSampleRxIndicatorPacket packet = new IODataSampleRxIndicatorPacket(
				addr64, addr16, XBeeReceiveOptions.NONE, new byte[] {0x00, 0x01, 0x02, 0x03, 0x04});
		
		network.addRemoteDevice(new RemoteDigiPointDevice(xbeeDevice, addr64, ni));
		
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device should not be null", remoteDevice, is(not(equalTo(null))));
		
		assertThat("Returned remote device must be a DigiPoint device", remoteDevice instanceof RemoteDigiPointDevice, is(equalTo(true)));
		assertThat("Returned remote device 64-bit address must be '" + addr64 + "' and not '" + remoteDevice.get64BitAddress() + "'", 
				remoteDevice.get64BitAddress(), is(equalTo(addr64)));
		assertThat("Returned remote device 16-bit address must be '" + addr16 + "' and not '" + remoteDevice.get16BitAddress() + "'", 
				remoteDevice.get16BitAddress(), is(equalTo(addr16)));
		assertThat("Returned remote device NI must be '" + ni + "' and not '" + remoteDevice.getNodeID() + "'", 
				remoteDevice.getNodeID(), is(equalTo(ni)));
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPacketRX64Packet802Dot15Dot4() throws XBeeException {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		
		XBee64BitAddress addr64 = new XBee64BitAddress("0013A20040A6A0DB");
		RX64Packet packet = new RX64Packet(addr64, 0x49, XBeeReceiveOptions.NONE, new byte[0]);
		
		assertThat("The network must contain 0 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device should not be null", remoteDevice, is(not(equalTo(null))));
		
		assertThat("Returned remote device must be a 802.15.4 device", remoteDevice instanceof RemoteRaw802Device, is(equalTo(true)));
		assertThat("Returned remote device 64-bit address must be '" + addr64 + "' and not '" + remoteDevice.get64BitAddress() + "'", 
				remoteDevice.get64BitAddress(), is(equalTo(addr64)));
		assertThat("Returned remote device 16-bit address must be '" + XBee16BitAddress.UNKNOWN_ADDRESS + "' and not '" + remoteDevice.get16BitAddress() + "'", 
				remoteDevice.get16BitAddress(), is(equalTo(XBee16BitAddress.UNKNOWN_ADDRESS)));
		assertThat("Returned remote device NI must be 'null' and not '" + remoteDevice.getNodeID() + "'", 
				remoteDevice.getNodeID(), is(equalTo(null)));
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPackeRX64Packet802Dot15Dot4DeviceAlreadyInNetwork() throws XBeeException {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		
		String ni = "myRemote802.15.4";
		XBee64BitAddress addr64 = new XBee64BitAddress("0013A20040A6A0DB");
		XBee16BitAddress addr16 = new XBee16BitAddress("6589");
		RX64Packet packet = new RX64Packet(addr64, 0x49, XBeeReceiveOptions.NONE, new byte[0]);
		
		network.addRemoteDevice(new RemoteRaw802Device(xbeeDevice, addr64, addr16, ni));
		
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device should not be null", remoteDevice, is(not(equalTo(null))));
		
		assertThat("Returned remote device must be a 802.15.4 device", remoteDevice instanceof RemoteRaw802Device, is(equalTo(true)));
		assertThat("Returned remote device 64-bit address must be '" + addr64 + "' and not '" + remoteDevice.get64BitAddress() + "'", 
				remoteDevice.get64BitAddress(), is(equalTo(addr64)));
		assertThat("Returned remote device 16-bit address must be '" + addr16 + "' and not '" + remoteDevice.get16BitAddress() + "'", 
				remoteDevice.get16BitAddress(), is(equalTo(addr16)));
		assertThat("Returned remote device NI must be '" + ni + "' and not '" + remoteDevice.getNodeID() + "'", 
				remoteDevice.getNodeID(), is(equalTo(ni)));
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPacketRX16Packet802Dot15Dot4() throws XBeeException {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		
		XBee16BitAddress addr16 = new XBee16BitAddress("6589");
		RX16Packet packet = new RX16Packet(addr16, 0x49, XBeeReceiveOptions.NONE, new byte[0]);
		
		assertThat("The network must contain 0 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device should not be null", remoteDevice, is(not(equalTo(null))));
		
		assertThat("Returned remote device must be a 802.15.4 device", remoteDevice instanceof RemoteRaw802Device, is(equalTo(true)));
		assertThat("Returned remote device 64-bit address must be '" + XBee64BitAddress.UNKNOWN_ADDRESS + "' and not '" + remoteDevice.get64BitAddress() + "'", 
				remoteDevice.get64BitAddress(), is(equalTo(XBee64BitAddress.UNKNOWN_ADDRESS)));
		assertThat("Returned remote device 16-bit address must be '" + addr16 + "' and not '" + remoteDevice.get16BitAddress() + "'", 
				remoteDevice.get16BitAddress(), is(equalTo(addr16)));
		assertThat("Returned remote device NI must be 'null' and not '" + remoteDevice.getNodeID() + "'", 
				remoteDevice.getNodeID(), is(equalTo(null)));
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPackeRX16Packet802Dot15Dot4DeviceAlreadyInNetwork() throws XBeeException {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		
		String ni = "myRemote802.15.4";
		XBee64BitAddress addr64 = new XBee64BitAddress("0013A20040A6A0DB");
		XBee16BitAddress addr16 = new XBee16BitAddress("6589");
		RX16Packet packet = new RX16Packet(addr16, 0x49, XBeeReceiveOptions.NONE, new byte[0]);
		
		network.addRemoteDevice(new RemoteRaw802Device(xbeeDevice, addr64, addr16, ni));
		
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device should not be null", remoteDevice, is(not(equalTo(null))));
		
		assertThat("Returned remote device must be a 802.15.4 device", remoteDevice instanceof RemoteRaw802Device, is(equalTo(true)));
		assertThat("Returned remote device 64-bit address must be '" + addr64 + "' and not '" + remoteDevice.get64BitAddress() + "'", 
				remoteDevice.get64BitAddress(), is(equalTo(addr64)));
		assertThat("Returned remote device 16-bit address must be '" + addr16 + "' and not '" + remoteDevice.get16BitAddress() + "'", 
				remoteDevice.get16BitAddress(), is(equalTo(addr16)));
		assertThat("Returned remote device NI must be '" + ni + "' and not '" + remoteDevice.getNodeID() + "'", 
				remoteDevice.getNodeID(), is(equalTo(ni)));
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPacketRX64IOPacketPacket802Dot15Dot4() throws XBeeException {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		
		XBee64BitAddress addr64 = new XBee64BitAddress("0013A20040A6A0DB");
		RX64IOPacket packet = new RX64IOPacket(addr64, 0x49, XBeeReceiveOptions.NONE, new byte[] {0x00, 0x01, 0x02, 0x03, 0x04});
		
		assertThat("The network must contain 0 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device should not be null", remoteDevice, is(not(equalTo(null))));
		
		assertThat("Returned remote device must be a 802.15.4 device", remoteDevice instanceof RemoteRaw802Device, is(equalTo(true)));
		assertThat("Returned remote device 64-bit address must be '" + addr64 + "' and not '" + remoteDevice.get64BitAddress() + "'", 
				remoteDevice.get64BitAddress(), is(equalTo(addr64)));
		assertThat("Returned remote device 16-bit address must be '" + XBee16BitAddress.UNKNOWN_ADDRESS + "' and not '" + remoteDevice.get16BitAddress() + "'", 
				remoteDevice.get16BitAddress(), is(equalTo(XBee16BitAddress.UNKNOWN_ADDRESS)));
		assertThat("Returned remote device NI must be 'null' and not '" + remoteDevice.getNodeID() + "'", 
				remoteDevice.getNodeID(), is(equalTo(null)));
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPackeRX64IOPacketPacket802Dot15Dot4DeviceAlreadyInNetwork() throws XBeeException {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		
		String ni = "myRemote802.15.4";
		XBee64BitAddress addr64 = new XBee64BitAddress("0013A20040A6A0DB");
		XBee16BitAddress addr16 = new XBee16BitAddress("6589");
		RX64IOPacket packet = new RX64IOPacket(addr64, 0x49, XBeeReceiveOptions.NONE, new byte[] {0x00, 0x01, 0x02, 0x03, 0x04});
		
		network.addRemoteDevice(new RemoteRaw802Device(xbeeDevice, addr64, addr16, ni));
		
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device should not be null", remoteDevice, is(not(equalTo(null))));
		
		assertThat("Returned remote device must be a 802.15.4 device", remoteDevice instanceof RemoteRaw802Device, is(equalTo(true)));
		assertThat("Returned remote device 64-bit address must be '" + addr64 + "' and not '" + remoteDevice.get64BitAddress() + "'", 
				remoteDevice.get64BitAddress(), is(equalTo(addr64)));
		assertThat("Returned remote device 16-bit address must be '" + addr16 + "' and not '" + remoteDevice.get16BitAddress() + "'", 
				remoteDevice.get16BitAddress(), is(equalTo(addr16)));
		assertThat("Returned remote device NI must be '" + ni + "' and not '" + remoteDevice.getNodeID() + "'", 
				remoteDevice.getNodeID(), is(equalTo(ni)));
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPacketRX16IOPacket802Dot15Dot4() throws XBeeException {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		
		XBee16BitAddress addr16 = new XBee16BitAddress("6589");
		RX16IOPacket packet = new RX16IOPacket(addr16, 0x49, XBeeReceiveOptions.NONE, new byte[] {0x00, 0x01, 0x02, 0x03, 0x04});
		
		assertThat("The network must contain 0 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(0)));
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device should not be null", remoteDevice, is(not(equalTo(null))));
		
		assertThat("Returned remote device must be a 802.15.4 device", remoteDevice instanceof RemoteRaw802Device, is(equalTo(true)));
		assertThat("Returned remote device 64-bit address must be '" + XBee64BitAddress.UNKNOWN_ADDRESS + "' and not '" + remoteDevice.get64BitAddress() + "'", 
				remoteDevice.get64BitAddress(), is(equalTo(XBee64BitAddress.UNKNOWN_ADDRESS)));
		assertThat("Returned remote device 16-bit address must be '" + addr16 + "' and not '" + remoteDevice.get16BitAddress() + "'", 
				remoteDevice.get16BitAddress(), is(equalTo(addr16)));
		assertThat("Returned remote device NI must be 'null' and not '" + remoteDevice.getNodeID() + "'", 
				remoteDevice.getNodeID(), is(equalTo(null)));
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#getRemoteXBeeDeviceFromPacket(com.digi.xbee.api.packet.XBeeAPIPacket)}.
	 * 
	 * @throws XBeeException 
	 */
	@Test
	public final void testGetRemoteXBeeDeviceFromPackeRX16IOPacket802Dot15Dot4DeviceAlreadyInNetwork() throws XBeeException {
		// Setup the resources for the test.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		
		String ni = "myRemote802.15.4";
		XBee64BitAddress addr64 = new XBee64BitAddress("0013A20040A6A0DB");
		XBee16BitAddress addr16 = new XBee16BitAddress("6589");
		RX16IOPacket packet = new RX16IOPacket(addr16, 0x49, XBeeReceiveOptions.NONE, new byte[] {0x00, 0x01, 0x02, 0x03, 0x04});
		
		network.addRemoteDevice(new RemoteRaw802Device(xbeeDevice, addr64, addr16, ni));
		
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
		
		// Call the method under test.
		RemoteXBeeDevice remoteDevice = dataReader.getRemoteXBeeDeviceFromPacket(packet);
		
		// Verify the result.
		assertThat("Returned remote device should not be null", remoteDevice, is(not(equalTo(null))));
		
		assertThat("Returned remote device must be a 802.15.4 device", remoteDevice instanceof RemoteRaw802Device, is(equalTo(true)));
		assertThat("Returned remote device 64-bit address must be '" + addr64 + "' and not '" + remoteDevice.get64BitAddress() + "'", 
				remoteDevice.get64BitAddress(), is(equalTo(addr64)));
		assertThat("Returned remote device 16-bit address must be '" + addr16 + "' and not '" + remoteDevice.get16BitAddress() + "'", 
				remoteDevice.get16BitAddress(), is(equalTo(addr16)));
		assertThat("Returned remote device NI must be '" + ni + "' and not '" + remoteDevice.getNodeID() + "'", 
				remoteDevice.getNodeID(), is(equalTo(ni)));
		assertThat("The network must contain 1 device and not " + network.getNumberOfDevices(), 
				network.getNumberOfDevices(), is(equalTo(1)));
	}
}

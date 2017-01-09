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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.digi.xbee.api.NodeDiscovery;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IPacketReceiveListener;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.packet.common.ATCommandPacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;

@PrepareForTest({XBeeDevice.class, NodeDiscovery.class})
@RunWith(PowerMockRunner.class)
public class NodeDiscoveryDiscoverDevicesBlockTest {
	
	// Constants.
	public static final String SEND_NODE_DISCOVERY_COMMAND_METHOD = "sendNodeDiscoverCommand";
	public static final String DISCOVER_DEVICES_API_METHOD = "discoverDevicesAPI";
	public static final String PARSE_DISCOVERY_API_DATA_METHOD = "parseDiscoveryAPIData";
	public static final String NOTIFY_DEVICE_DISCOVERED = "notifyDeviceDiscovered";
		
	// Variables.
	private NodeDiscovery nd;
	
	private XBeeDevice deviceMock;
	
	private IConnectionInterface cInterfaceMock;
	
	private XBeeNetwork networkMock;
	
	private IPacketReceiveListener packetListener;
	
	private List<ATCommandResponsePacket> ndAnswers = new ArrayList<ATCommandResponsePacket>(0);
	
	@Before
	public void setUp() throws Exception {
		ndAnswers.clear();
		packetListener = null;
		
		deviceMock = PowerMockito.mock(XBeeDevice.class);
		cInterfaceMock = PowerMockito.mock(IConnectionInterface.class);
		networkMock = PowerMockito.mock(XBeeNetwork.class);
		
		PowerMockito.when(deviceMock.isOpen()).thenReturn(true);
		PowerMockito.when(deviceMock.getConnectionInterface()).thenReturn(cInterfaceMock);
		PowerMockito.when(cInterfaceMock.toString()).thenReturn("Mocked IConnectionInterface for NodeDiscovery test.");
		PowerMockito.when(deviceMock.getNetwork()).thenReturn(networkMock);
		PowerMockito.doThrow(new XBeeException()).when(deviceMock).getParameter("N?");
		
		PowerMockito.when(networkMock.addRemoteDevice(Mockito.any(RemoteXBeeDevice.class))).thenAnswer(
			new Answer<RemoteXBeeDevice>() {
				@Override
				public RemoteXBeeDevice answer(InvocationOnMock invocation)
						throws Throwable {
					return (RemoteXBeeDevice)invocation.getArguments()[0];
				}
		});
		
		PowerMockito.when(networkMock.addRemoteDevices(Mockito.anyListOf(RemoteXBeeDevice.class))).thenAnswer(
			new Answer<Object>() {
				@Override
				public Object answer(InvocationOnMock invocation)
						throws Throwable {
					return invocation.getArguments()[0];
				}
		});
		
		PowerMockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				packetListener = ((IPacketReceiveListener) invocation.getArguments()[0]);
				return null;
			}
		}).when(deviceMock).addPacketListener(Mockito.any(IPacketReceiveListener.class));
		
		PowerMockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable {
				if (packetListener == null)
					return null;
				Thread t = new Thread() {
					@Override
					public void run() {
						if (invocation == null || invocation.getArguments() == null
								|| invocation.getArguments().length == 0)
							return;
						
						Object obj = invocation.getArguments()[0];
						if (!(obj instanceof ATCommandPacket))
							return;
						
						ATCommandPacket packet = (ATCommandPacket)obj;
						if (!packet.getCommand().equals("ND"))
							return;
						
						for (int i = 0; i < ndAnswers.size(); i++) {
							packetListener.packetReceived(ndAnswers.get(i));
							try {
								sleep(200);
							} catch (InterruptedException e) {e.printStackTrace();}
						}
					}
				};
				t.start();
				
				return null;
			}
		}).when(deviceMock).sendPacketAsync(Mockito.any(ATCommandPacket.class));
		
		nd = PowerMockito.spy(new NodeDiscovery(deviceMock));
		Whitebox.setInternalState(nd, "frameID", 1);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#discoverDevice(String)}.
	 * 
	 * <p>An {@code InterfaceNotOpenException} exception must be thrown when 
	 * the local device connection is not open.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public final void testDiscoverDeviceDeviceNotOpen() throws XBeeException {
		// Setup the resources for the test.
		PowerMockito.when(deviceMock.isOpen()).thenReturn(false);
		
		// Call the method under test.
		nd.discoverDevice("id");
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#discoverDevice(String)}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testDiscoverDeviceNoDevice() throws Exception {
		// Setup the resources for the test.
		String id = "id";
		
		byte[] deviceTimeoutByteArray = new byte[]{0x01};
		
		PowerMockito.when(deviceMock.getParameter("NT")).thenReturn(deviceTimeoutByteArray);
		
		// Call the method under test.
		RemoteXBeeDevice remote = nd.discoverDevice(id);
		
		// Verify the result.
		assertThat("The discovered device should be null", remote, is(equalTo(null)));
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(SEND_NODE_DISCOVERY_COMMAND_METHOD, Mockito.anyString());
		Mockito.verify(deviceMock, Mockito.times(1)).addPacketListener(packetListener);
		Mockito.verify(deviceMock, Mockito.times(1)).removePacketListener(packetListener);
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(DISCOVER_DEVICES_API_METHOD, null, id);
		
		Mockito.verify(networkMock, Mockito.never()).addRemoteDevices(Mockito.anyListOf(RemoteXBeeDevice.class));
		Mockito.verify(networkMock, Mockito.never()).addRemoteDevice(Mockito.any(RemoteXBeeDevice.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#discoverDevice(String)}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testDiscoverDevice() throws Exception {
		// Setup the resources for the test.
		String id = "id";
		
		byte[] deviceTimeoutByteArray = new byte[]{0x50};
		
		ATCommandResponsePacket packet = createPacket(1, ATCommandStatus.OK, 
				new XBee16BitAddress("0000"), new XBee64BitAddress("0013A20040A6A0DB"), 
				id, new XBee16BitAddress("FFFE"), (byte)0x00, (byte)0x49, false);
		ndAnswers.add(packet);
		
		PowerMockito.when(deviceMock.getParameter("NT")).thenReturn(deviceTimeoutByteArray);
		PowerMockito.when(deviceMock.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		// Call the method under test.
		RemoteXBeeDevice remote = nd.discoverDevice(id);
		
		// Verify the result.
		assertThat("The discovered device should not be null", remote, is(not(equalTo(null))));
		assertThat("The Node ID of the discovered device should be '" + id + "'", remote.getNodeID(), is(equalTo(id)));
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(SEND_NODE_DISCOVERY_COMMAND_METHOD, Mockito.anyString());
		Mockito.verify(deviceMock, Mockito.times(1)).addPacketListener(packetListener);
		Mockito.verify(deviceMock, Mockito.times(1)).removePacketListener(packetListener);
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(DISCOVER_DEVICES_API_METHOD, null, id);
		
		Mockito.verify(networkMock, Mockito.never()).addRemoteDevices(Mockito.anyListOf(RemoteXBeeDevice.class));
		Mockito.verify(networkMock, Mockito.times(1)).addRemoteDevice(Mockito.any(RemoteXBeeDevice.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#discoverDevice(String)}.
	 * 
	 * This method tests that a remote DigiMesh device is discovered and 
	 * properly added to the local device network.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testDiscoverDeviceDigiMeshDevice() throws Exception {
		// Setup the resources for the test.
		String id = "idDigiMesh";

		byte[] deviceTimeoutByteArray = new byte[]{0x50};

		ATCommandResponsePacket packet = createPacket(1, ATCommandStatus.OK, 
				new XBee16BitAddress("FFFE"), new XBee64BitAddress("0013A20040A6A001"), 
				id, new XBee16BitAddress("FFFE"), (byte)0x00, (byte)0x49, false);
		ndAnswers.add(packet);

		PowerMockito.when(deviceMock.getParameter("NT")).thenReturn(deviceTimeoutByteArray);
		PowerMockito.when(deviceMock.getParameter("SM")).thenReturn(new byte[]{0x00}); // Not sleeping device
		PowerMockito.when(deviceMock.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_MESH);

		// Call the method under test.
		RemoteXBeeDevice remote = nd.discoverDevice(id);

		// Verify the result.
		assertThat("The discovered device should not be null", remote, is(not(equalTo(null))));
		assertThat("The Node ID of the discovered device should be '" + id + "'", remote.getNodeID(), is(equalTo(id)));
		assertThat("The discovered device should be a Remote DigiMesh device", remote instanceof RemoteDigiMeshDevice, is(equalTo(true)));
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(SEND_NODE_DISCOVERY_COMMAND_METHOD, Mockito.anyString());
		Mockito.verify(deviceMock, Mockito.times(1)).addPacketListener(packetListener);
		Mockito.verify(deviceMock, Mockito.times(1)).removePacketListener(packetListener);

		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(DISCOVER_DEVICES_API_METHOD, null, id);

		Mockito.verify(networkMock, Mockito.never()).addRemoteDevices(Mockito.anyListOf(RemoteXBeeDevice.class));
		Mockito.verify(networkMock, Mockito.times(1)).addRemoteDevice(Mockito.any(RemoteXBeeDevice.class));
		Mockito.verify(networkMock, Mockito.times(1)).addRemoteDevice(remote);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#discoverDevice(String)}.
	 * 
	 * This method tests that a remote DigiPoint device is discovered and 
	 * properly added to the local device network.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testDiscoverDeviceDigiPointDevice() throws Exception {
		// Setup the resources for the test.
		String id = "idDigiPoint";

		byte[] deviceTimeoutByteArray = new byte[]{0x50};

		ATCommandResponsePacket packet = createPacket(1, ATCommandStatus.OK, 
				new XBee16BitAddress("FFFE"), new XBee64BitAddress("0013A20040A6A001"), 
				id, new XBee16BitAddress("FFFE"), (byte)0x00, (byte)0x49, false);
		ndAnswers.add(packet);

		PowerMockito.when(deviceMock.getParameter("NT")).thenReturn(deviceTimeoutByteArray);
		PowerMockito.when(deviceMock.getXBeeProtocol()).thenReturn(XBeeProtocol.DIGI_POINT);

		// Call the method under test.
		RemoteXBeeDevice remote = nd.discoverDevice(id);

		// Verify the result.
		assertThat("The discovered device should not be null", remote, is(not(equalTo(null))));
		assertThat("The Node ID of the discovered device should be '" + id + "'", remote.getNodeID(), is(equalTo(id)));
		assertThat("The discovered device should be a Remote DigiPoint device", remote instanceof RemoteDigiPointDevice, is(equalTo(true)));
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(SEND_NODE_DISCOVERY_COMMAND_METHOD, Mockito.anyString());
		Mockito.verify(deviceMock, Mockito.times(1)).addPacketListener(packetListener);
		Mockito.verify(deviceMock, Mockito.times(1)).removePacketListener(packetListener);

		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(DISCOVER_DEVICES_API_METHOD, null, id);

		Mockito.verify(networkMock, Mockito.never()).addRemoteDevices(Mockito.anyListOf(RemoteXBeeDevice.class));
		Mockito.verify(networkMock, Mockito.times(1)).addRemoteDevice(Mockito.any(RemoteXBeeDevice.class));
		Mockito.verify(networkMock, Mockito.times(1)).addRemoteDevice(remote);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#discoverDevice(String)}.
	 * 
	 * This method tests that a remote 802.15.4 device is discovered and 
	 * properly added to the local device network.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testDiscoverDeviceRaw802Device() throws Exception {
		// Setup the resources for the test.
		String id = "id802.15.4";

		byte[] deviceTimeoutByteArray = new byte[]{0x50};

		ATCommandResponsePacket packet = createPacket(1, ATCommandStatus.OK, 
				new XBee16BitAddress("1234"), new XBee64BitAddress("0013A20040A6A001"), id, 
				new XBee16BitAddress("FFFE"), (byte)0x00, (byte)0x49, true);
		ndAnswers.add(packet);
		ndAnswers.add(new ATCommandResponsePacket(1, 
				ATCommandStatus.OK, "ND", new byte[0])); // End packet for 802.15.4

		PowerMockito.when(deviceMock.getParameter("NT")).thenReturn(deviceTimeoutByteArray);
		PowerMockito.when(deviceMock.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);

		// Call the method under test.
		RemoteXBeeDevice remote = nd.discoverDevice(id);

		// Verify the result.
		assertThat("The discovered device should not be null", remote, is(not(equalTo(null))));
		assertThat("The Node ID of the discovered device should be '" + id + "'", remote.getNodeID(), is(equalTo(id)));
		assertThat("The discovered device should be a Remote 802.15.4 device", remote instanceof RemoteRaw802Device, is(equalTo(true)));
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(SEND_NODE_DISCOVERY_COMMAND_METHOD, Mockito.anyString());
		Mockito.verify(deviceMock, Mockito.times(1)).addPacketListener(packetListener);
		Mockito.verify(deviceMock, Mockito.times(1)).removePacketListener(packetListener);

		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(DISCOVER_DEVICES_API_METHOD, null, id);

		Mockito.verify(networkMock, Mockito.never()).addRemoteDevices(Mockito.anyListOf(RemoteXBeeDevice.class));
		Mockito.verify(networkMock, Mockito.times(1)).addRemoteDevice(Mockito.any(RemoteXBeeDevice.class));
		Mockito.verify(networkMock, Mockito.times(1)).addRemoteDevice(remote);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#discoverDevice(String)}.
	 * 
	 * This method tests that a remote ZigBee device is discovered and 
	 * properly added to the local device network.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testDiscoverDeviceZigbeeDevice() throws Exception {
		// Setup the resources for the test.
		String id = "idZigBee";

		byte[] deviceTimeoutByteArray = new byte[]{0x50};

		ATCommandResponsePacket packet = createPacket(1, ATCommandStatus.OK, 
				new XBee16BitAddress("1234"), new XBee64BitAddress("0013A20040A6A001"), 
				id, new XBee16BitAddress("FFFE"), (byte)0x00, (byte)0x49, false);
		ndAnswers.add(packet);

		PowerMockito.when(deviceMock.getParameter("NT")).thenReturn(deviceTimeoutByteArray);
		PowerMockito.when(deviceMock.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		// Call the method under test.
		RemoteXBeeDevice remote = nd.discoverDevice(id);
		
		// Verify the result.
		assertThat("The discovered device should not be null", remote, is(not(equalTo(null))));
		assertThat("The Node ID of the discovered device should be '" + id + "'", remote.getNodeID(), is(equalTo(id)));
		assertThat("The discovered device should be a Remote ZigBee device", remote instanceof RemoteZigBeeDevice, is(equalTo(true)));
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(SEND_NODE_DISCOVERY_COMMAND_METHOD, Mockito.anyString());
		Mockito.verify(deviceMock, Mockito.times(1)).addPacketListener(packetListener);
		Mockito.verify(deviceMock, Mockito.times(1)).removePacketListener(packetListener);

		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(DISCOVER_DEVICES_API_METHOD, null, id);

		Mockito.verify(networkMock, Mockito.never()).addRemoteDevices(Mockito.anyListOf(RemoteXBeeDevice.class));
		Mockito.verify(networkMock, Mockito.times(1)).addRemoteDevice(Mockito.any(RemoteXBeeDevice.class));
		Mockito.verify(networkMock, Mockito.times(1)).addRemoteDevice(remote);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#discoverDevices(List)}.
	 * 
	 * <p>An {@code InterfaceNotOpenException} exception must be thrown when 
	 * the local device connection is not open.</p>
	 * 
	 * @throws XBeeException 
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public final void testDiscoverDevicesDeviceNotOpen() throws XBeeException {
		// Setup the resources for the test.
		PowerMockito.when(deviceMock.isOpen()).thenReturn(false);
		
		List<String> list = new ArrayList<String>();
		list.add("id");
		
		// Call the method under test.
		nd.discoverDevices(list);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#discoverDevices(List)}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testDiscoverDevicesNoDevices() throws Exception {
		// Setup the resources for the test.
		List<String> list = new ArrayList<String>();
		list.add("id");
		
		byte[] deviceTimeoutByteArray = new byte[]{0x01};
		
		PowerMockito.when(deviceMock.getParameter("NT")).thenReturn(deviceTimeoutByteArray);
		
		// Call the method under test.
		List<RemoteXBeeDevice> remotes = nd.discoverDevices(list);
		
		// Verify the result.
		assertThat("The discovered devices list should be empty", remotes.size(), is(equalTo(0)));
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(SEND_NODE_DISCOVERY_COMMAND_METHOD, Mockito.anyString());
		Mockito.verify(deviceMock, Mockito.times(1)).addPacketListener(packetListener);
		Mockito.verify(deviceMock, Mockito.times(1)).removePacketListener(packetListener);
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(DISCOVER_DEVICES_API_METHOD, null, null);
		
		Mockito.verify(networkMock, Mockito.never()).addRemoteDevices(Mockito.anyListOf(RemoteXBeeDevice.class));
		Mockito.verify(networkMock, Mockito.never()).addRemoteDevice(Mockito.any(RemoteXBeeDevice.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#discoverDevices(List)}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testDiscoverDevicesOneNodeId() throws Exception {
		// Setup the resources for the test.
		String id = "id";
		List<String> list = new ArrayList<String>();
		list.add(id);
		
		byte[] deviceTimeoutByteArray = new byte[]{0x01};
		
		PowerMockito.when(deviceMock.getParameter("NT")).thenReturn(deviceTimeoutByteArray);
		PowerMockito.when(deviceMock.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		ATCommandResponsePacket packet = createPacket(1, ATCommandStatus.OK, 
				new XBee16BitAddress("0000"), new XBee64BitAddress("0013A20040A6A0DB"), 
				id, new XBee16BitAddress("FFFE"), (byte)0x00, (byte)0x49, false);
		ndAnswers.add(packet);
		
		// Call the method under test.
		List<RemoteXBeeDevice> remotes = nd.discoverDevices(list);
		
		// Verify the result.
		assertThat("The discovered devices list must have a size of " + ndAnswers.size(), remotes.size(), is(equalTo(ndAnswers.size())));
		assertThat("The Node ID of the discovered device should be '" + id + "'", remotes.get(0).getNodeID(), is(equalTo(id)));
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(SEND_NODE_DISCOVERY_COMMAND_METHOD, Mockito.anyString());
		Mockito.verify(deviceMock, Mockito.times(1)).addPacketListener(packetListener);
		Mockito.verify(deviceMock, Mockito.times(1)).removePacketListener(packetListener);
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(DISCOVER_DEVICES_API_METHOD, null, null);
		
		Mockito.verify(networkMock, Mockito.never()).addRemoteDevices(Mockito.anyListOf(RemoteXBeeDevice.class));
		Mockito.verify(networkMock, Mockito.times(list.size())).addRemoteDevice(Mockito.any(RemoteXBeeDevice.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#discoverDevices(List)}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testDiscoverDevicesOneNodeIdTwoDevices() throws Exception {
		// Setup the resources for the test.
		String id = "id";
		List<String> list = new ArrayList<String>();
		list.add(id);
		
		byte[] deviceTimeoutByteArray = new byte[]{0x05};
		
		PowerMockito.when(deviceMock.getParameter("NT")).thenReturn(deviceTimeoutByteArray);
		PowerMockito.when(deviceMock.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		XBee64BitAddress[] macs = new XBee64BitAddress[]{new XBee64BitAddress("0013A20040A6A0DB"), 
				new XBee64BitAddress("0013A20040AD1585")};
		XBee16BitAddress[] addr16 = new XBee16BitAddress[]{new XBee16BitAddress("0000"), 
				new XBee16BitAddress("7971")};
		XBee16BitAddress[] parentAddr = new XBee16BitAddress[]{new XBee16BitAddress("FFFE"), 
				new XBee16BitAddress("0000")};
		
		ATCommandResponsePacket packet = createPacket(1, ATCommandStatus.OK, 
				addr16[0], macs[0], id, parentAddr[0], (byte)0x00, (byte)0x49, false);
		ndAnswers.add(packet);
		packet = createPacket(1, ATCommandStatus.OK, 
				addr16[1], macs[1], id, parentAddr[1], (byte)0x01, (byte)0x67, false);
		ndAnswers.add(packet);
		
		// Call the method under test.
		List<RemoteXBeeDevice> remotes = nd.discoverDevices(list);
		
		// Verify the result.
		assertThat("The discovered devices list must have a size of " + ndAnswers.size(), remotes.size(), is(equalTo(ndAnswers.size())));
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(SEND_NODE_DISCOVERY_COMMAND_METHOD, Mockito.anyString());
		Mockito.verify(deviceMock, Mockito.times(1)).addPacketListener(packetListener);
		Mockito.verify(deviceMock, Mockito.times(1)).removePacketListener(packetListener);
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(DISCOVER_DEVICES_API_METHOD, null, null);
		
		Mockito.verify(networkMock, Mockito.never()).addRemoteDevices(Mockito.anyListOf(RemoteXBeeDevice.class));
		Mockito.verify(networkMock, Mockito.times(2)).addRemoteDevice(Mockito.any(RemoteXBeeDevice.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#discoverDevices(List)}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testDiscoverDevicesTwoNodeIds() throws Exception {
		// Setup the resources for the test.
		String[] nIds = new String[]{"id", "id2"};
		List<String> list = new ArrayList<String>();
		list.add(nIds[0]);
		list.add(nIds[1]);
		
		byte[] deviceTimeoutByteArray = new byte[]{0x05};
		
		PowerMockito.when(deviceMock.getParameter("NT")).thenReturn(deviceTimeoutByteArray);
		PowerMockito.when(deviceMock.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		XBee64BitAddress[] macs = new XBee64BitAddress[]{new XBee64BitAddress("0013A20040A6A0DB"), 
				new XBee64BitAddress("0013A20040AD1585")};
		XBee16BitAddress[] addr16 = new XBee16BitAddress[]{new XBee16BitAddress("0000"), 
				new XBee16BitAddress("7971")};
		XBee16BitAddress[] parentAddr = new XBee16BitAddress[]{new XBee16BitAddress("FFFE"), 
				new XBee16BitAddress("0000")};
		
		ATCommandResponsePacket packet = createPacket(1, ATCommandStatus.OK, 
				addr16[0], macs[0], nIds[0], parentAddr[0], (byte)0x00, (byte)0x49, false);
		ndAnswers.add(packet);
		packet = createPacket(1, ATCommandStatus.OK, 
				addr16[1], macs[1], nIds[1], parentAddr[1], (byte)0x01, (byte)0x67, false);
		ndAnswers.add(packet);
		
		// Call the method under test.
		List<RemoteXBeeDevice> remotes = nd.discoverDevices(list);
		
		// Verify the result.
		assertThat("The discovered devices list must have a size of " + ndAnswers.size(), remotes.size(), is(equalTo(ndAnswers.size())));
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(SEND_NODE_DISCOVERY_COMMAND_METHOD, Mockito.anyString());
		Mockito.verify(deviceMock, Mockito.times(1)).addPacketListener(packetListener);
		Mockito.verify(deviceMock, Mockito.times(1)).removePacketListener(packetListener);
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(DISCOVER_DEVICES_API_METHOD, null, null);
		
		Mockito.verify(networkMock, Mockito.never()).addRemoteDevices(Mockito.anyListOf(RemoteXBeeDevice.class));
		Mockito.verify(networkMock, Mockito.times(list.size())).addRemoteDevice(Mockito.any(RemoteXBeeDevice.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#discoverDevices(List)}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testDiscoverDevicesDuplicatedNodeIds() throws Exception {
		// Setup the resources for the test.
		String id = "id";
		List<String> list = new ArrayList<String>();
		list.add(id);
		list.add(id);
		
		byte[] deviceTimeoutByteArray = new byte[]{0x01};
		
		PowerMockito.when(deviceMock.getParameter("NT")).thenReturn(deviceTimeoutByteArray);
		PowerMockito.when(deviceMock.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		ATCommandResponsePacket packet = createPacket(1, ATCommandStatus.OK, 
				new XBee16BitAddress("0000"), new XBee64BitAddress("0013A20040A6A0DB"), 
				id, new XBee16BitAddress("FFFE"), (byte)0x00, (byte)0x49, false);
		ndAnswers.add(packet);
		
		// Call the method under test.
		List<RemoteXBeeDevice> remotes = nd.discoverDevices(list);
		
		// Verify the result.
		assertThat("The discovered devices list must have a size of " + ndAnswers.size(), remotes.size(), is(equalTo(ndAnswers.size())));
		assertThat("The Node ID of the discovered device should be '" + id + "'", remotes.get(0).getNodeID(), is(equalTo(id)));
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(SEND_NODE_DISCOVERY_COMMAND_METHOD, Mockito.anyString());
		Mockito.verify(deviceMock, Mockito.times(1)).addPacketListener(packetListener);
		Mockito.verify(deviceMock, Mockito.times(1)).removePacketListener(packetListener);
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(DISCOVER_DEVICES_API_METHOD, null, null);
		
		Mockito.verify(networkMock, Mockito.never()).addRemoteDevices(Mockito.anyListOf(RemoteXBeeDevice.class));
		Mockito.verify(networkMock, Mockito.times(list.size())).addRemoteDevice(Mockito.any(RemoteXBeeDevice.class));
	}
	
	/**
	 * Helper method to create ND responses.
	 * 
	 * @param frameId
	 * @param status
	 * @param addr16
	 * @param addr64
	 * @param ni
	 * @param parentAddr
	 * @param role
	 * @param rssi
	 * @param raw802Packet
	 * @return
	 */
	private ATCommandResponsePacket createPacket(int frameId, ATCommandStatus status, 
			XBee16BitAddress addr16, XBee64BitAddress addr64, String ni, XBee16BitAddress parentAddr, 
			byte role, byte rssi, boolean raw802Packet) {
		byte[] value = new byte[2 /* 16-bit addr */ + 8 /* 64-bit addr */ + ni.length() + 1 
		                        + 2 /* parent addr */ + 1 /* role */ + 1 /* status */ + 2 /* profile */ 
		                        + 2 /* manufacturer */ + 4 /* DD */ + 1 /* rssi */];
		
		byte[] profile =  new byte[]{(byte)0xC1, 0x05};
		byte[] manufacturer = new byte[]{0x10, 0x1E};
		byte[] dd = new byte[]{0x00, 0x03, 0x00, 0x00};
		
		System.arraycopy(addr16.getValue(), 0, value, 0, 2);
		System.arraycopy(addr64.getValue(), 0, value, 2, 8);
		if (raw802Packet) {
			value[10] = rssi;
			System.arraycopy(ni.getBytes(), 0, value, 11, ni.length());
		} else {
			System.arraycopy(ni.getBytes(), 0, value, 10, ni.length());
			value[10 + ni.length()] = 0x00;
			System.arraycopy(parentAddr.getValue(), 0, value, 11 + ni.length(), 2);
			value[13 + ni.length()] = role;
			value[14 + ni.length()] = 0x00;
			System.arraycopy(profile, 0, value, 15 + ni.length(), 2);
			System.arraycopy(manufacturer, 0, value, 17 + ni.length(), 2);
			System.arraycopy(dd, 0, value, 19 + ni.length(), 4);
			value[23 + ni.length()] = rssi;
		}
		return new ATCommandResponsePacket(frameId, status, "ND", value);
	}
}

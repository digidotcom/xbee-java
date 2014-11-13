/**
 * Copyright (c) 2014 Digi International Inc.,
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
import static org.hamcrest.core.IsNull.nullValue;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThat;

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

import com.digi.xbee.api.NodeDiscovery;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.RemoteZigBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.connection.IConnectionInterface;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import com.digi.xbee.api.listeners.IPacketReceiveListener;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.packet.common.ATCommandPacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;
import com.digi.xbee.api.utils.ByteUtils;

@PrepareForTest({XBeeDevice.class, NodeDiscovery.class})
@RunWith(PowerMockRunner.class)
public class NodeDiscoveryDiscoverDevicesListenerTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Constants.
	public static final String SEND_NODE_DISCOVERY_COMMAND_METHOD = "sendNodeDiscoverCommand";
	public static final String DISCOVER_DEVICES_API_METHOD = "discoverDevicesAPI";
	public static final String PARSE_DISCOVERY_API_DATA_METHOD = "parseDiscoveryAPIData";
	public static final String NOTIFY_DEVICE_DISCOVERED = "notifyDeviceDiscovered";
	
	public static final String DEVICE_LIST = "deviceList";
	
	private static final long TIMEOUT = 100;
	private static final byte[] DEVICE_TIMEOUT = ByteUtils.longToByteArray(TIMEOUT / 100);
	
	// Variables.
	private NodeDiscovery nd;
	
	private XBeeDevice deviceMock;
	
	private IConnectionInterface cInterfaceMock;
	
	private XBeeNetwork networkMock;
	
	private IPacketReceiveListener packetListener;
	
	private List<ATCommandResponsePacket> ndAnswers = new ArrayList<ATCommandResponsePacket>(0);
	
	private ArrayList<IDiscoveryListener> listeners = new ArrayList<IDiscoveryListener>();
	
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
		PowerMockito.when(deviceMock.getParameter("NT")).thenReturn(DEVICE_TIMEOUT);
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
								sleep(50);
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
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#startDiscoveryProcess(ArrayList)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when the 
	 * list of listeners is null.</p>
	 */
	@Test
	public final void testDiscoverDevicesNullListenersList() {
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Listeners list cannot be null.")));
		
		// Call the method under test.
		nd.startDiscoveryProcess(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#startDiscoveryProcess(ArrayList)}.
	 * 
	 * <p>An {@code InterfaceNotOpenException} exception must be thrown when 
	 * the local device connection is not open.</p>
	 */
	@Test
	public final void testDiscoverDevicesDeviceNotOpen() {
		// Setup the resources for the test.
		PowerMockito.when(deviceMock.isOpen()).thenReturn(false);
		
		exception.expect(InterfaceNotOpenException.class);
		exception.expectMessage(is(equalTo("The connection interface is not open.")));
		
		// Call the method under test.
		nd.startDiscoveryProcess(listeners);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#startDiscoveryProcess(ArrayList)}.
	 * 
	 * <p>The discovery process must finish with error if the device cannot send
	 * the {@code ND} command.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testDiscoverDevicesNDError() throws Exception {
		// Setup the resources for the test.
		DiscoveryListener listener = new DiscoveryListener();
		listeners.add(listener);
		
		String error = "error";
		Mockito.doThrow(new XBeeException(error)).when(deviceMock).sendPacketAsync(Mockito.any(XBeePacket.class));
		
		// Call the method under test.
		nd.startDiscoveryProcess(listeners);
		
		// Verify the result.
		assertThat("The 'discoverDevices' should be running", listener.isFinished(), is(equalTo(false)));
		
		while(!listener.isFinished()) {
			Thread.sleep(30);
		}
		
		// Verify the result.
		assertThat("The discovered devices list should be empty", listener.getDiscoveredDevices().size(), is(equalTo(ndAnswers.size())));
		assertThat("The discovery process should finish with the '" + error + "' error", listener.getFinishError(), is(equalTo(error)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#startDiscoveryProcess(ArrayList)}.
	 * 
	 * <p>The {@code discoveryError} method of the listener must be called when
	 * a discovered device cannot be added to the network.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testDiscoverDevicesErrorAddingDeviceToNetwork() throws Exception {
		// Setup the resources for the test.
		XBeeProtocol protocol = XBeeProtocol.ZIGBEE;
		PowerMockito.when(deviceMock.getXBeeProtocol()).thenReturn(protocol);
		
		ATCommandResponsePacket packet = createPacket(1, ATCommandStatus.OK, 
				new XBee16BitAddress("0000"), new XBee64BitAddress("0013A20040A6A0DB"),
				"Ni string", new XBee16BitAddress("FFFE"), (byte)0x00 /* coordinator */, (byte)0x49);
		ndAnswers.add(packet);
		
		DiscoveryListener listener = new DiscoveryListener();
		listeners.add(listener);
		
		Mockito.when(networkMock.addRemoteDevice(Mockito.any(RemoteXBeeDevice.class))).thenReturn(null);
		
		// Call the method under test.
		nd.startDiscoveryProcess(listeners);
		
		// Verify the result.
		assertThat("The 'discoverDevices' should be running", listener.isFinished(), is(equalTo(false)));
		
		while(!listener.isFinished()) {
			Thread.sleep(30);
		}
		
		String error = "Error adding device '0013A20040A6A0DB - Ni string' to the network.";
		
		assertThat("The discovered devices list should be empty", listener.getDiscoveredDevices().size(), is(equalTo(0)));
		assertThat("The discovery error should be " + error, listener.getErrors().get(0), is(equalTo(error)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#startDiscoveryProcess(ArrayList)}.
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testDiscoverDevicesNoDevices() throws Exception {
		// Setup the resources for the test.
		XBeeProtocol protocol = XBeeProtocol.ZIGBEE;
		PowerMockito.when(deviceMock.getXBeeProtocol()).thenReturn(protocol);
		
		DiscoveryListener listener = new DiscoveryListener();
		listeners.add(listener);
		
		// Call the method under test.
		nd.startDiscoveryProcess(listeners);
		
		// Verify the result.
		assertThat("The 'discoverDevices' should be running", listener.isFinished(), is(equalTo(false)));
		
		while(!listener.isFinished()) {
			Thread.sleep(30);
		}
		
		assertThat("The discovered devices list should be empty", listener.getDiscoveredDevices().size(), is(equalTo(ndAnswers.size())));
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(SEND_NODE_DISCOVERY_COMMAND_METHOD, Mockito.anyString());
		Mockito.verify(deviceMock, Mockito.times(1)).addPacketListener(packetListener);
		Mockito.verify(deviceMock, Mockito.times(1)).removePacketListener(packetListener);
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(DISCOVER_DEVICES_API_METHOD, listeners, null);
		
		List<RemoteXBeeDevice> ndInternalDeviceList = Whitebox.<List<RemoteXBeeDevice>> getInternalState(nd, DEVICE_LIST);
		assertThat("Internal Node Discovery list must be empty", ndInternalDeviceList.size(), is(equalTo(0)));
		
		Mockito.verify(networkMock, Mockito.never()).addRemoteDevices(Mockito.anyListOf(RemoteXBeeDevice.class));
		Mockito.verify(networkMock, Mockito.never()).addRemoteDevice(Mockito.any(RemoteXBeeDevice.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#startDiscoveryProcess(ArrayList)}.
	 * 
	 * <p>One packet received from a ZigBee device.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testDiscoverDevicesOneZigBeeDevice() throws Exception {
		// Setup the resources for the test.
		XBeeProtocol protocol = XBeeProtocol.ZIGBEE;
		PowerMockito.when(deviceMock.getXBeeProtocol()).thenReturn(protocol);
		
		ATCommandResponsePacket packet = createPacket(1, ATCommandStatus.OK, 
				new XBee16BitAddress("0000"), new XBee64BitAddress("0013A20040A6A0DB"),
				"Ni string", new XBee16BitAddress("FFFE"), (byte)0x00 /* coordinator */, (byte)0x49);
		ndAnswers.add(packet);
		
		DiscoveryListener listener = new DiscoveryListener();
		listeners.add(listener);
		
		// Call the method under test.
		nd.startDiscoveryProcess(listeners);
		
		// Verify the result.
		assertThat("The 'discoverDevices' should be running", listener.isFinished(), is(equalTo(false)));
		
		while(!listener.isFinished()) {
			Thread.sleep(30);
		}
		
		assertThat("The discovered devices list should be empty", listener.getDiscoveredDevices().size(), is(equalTo(ndAnswers.size())));
		
		RemoteXBeeDevice remoteDevice = listener.getDiscoveredDevices().get(0);
		
		assertThat("Discovered device must not be null", remoteDevice, is(not(nullValue())));
		assertThat("Remote device protocol must be " + protocol.getDescription(), remoteDevice.getXBeeProtocol(), is(equalTo(protocol)));
		assertThat(remoteDevice instanceof RemoteZigBeeDevice, is(equalTo(true)));
		
		assertThat("Remote device Node ID is not right", remoteDevice.getNodeID(), is(equalTo("Ni string")));
		assertThat("Remote device 64-bit address is not right", remoteDevice.get64BitAddress(), 
					is(equalTo(new XBee64BitAddress("0013A20040A6A0DB"))));
		assertThat("Remote device 16-bit address is not right", ((RemoteZigBeeDevice)remoteDevice).get16BitAddress(), 
				is(equalTo(new XBee16BitAddress("0000"))));
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(SEND_NODE_DISCOVERY_COMMAND_METHOD, Mockito.anyString());
		Mockito.verify(deviceMock, Mockito.times(1)).addPacketListener(packetListener);
		Mockito.verify(deviceMock, Mockito.times(1)).removePacketListener(packetListener);
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(DISCOVER_DEVICES_API_METHOD, listeners, null);
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(PARSE_DISCOVERY_API_DATA_METHOD, Mockito.any(byte[].class), Mockito.eq(deviceMock));
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(NOTIFY_DEVICE_DISCOVERED, Mockito.eq(listeners), Mockito.any(RemoteXBeeDevice.class));
		
		List<RemoteXBeeDevice> ndInternalDeviceList = Whitebox.<List<RemoteXBeeDevice>> getInternalState(nd, DEVICE_LIST);
		assertThat("Internal Node Discovery list must be empty", ndInternalDeviceList.size(), is(equalTo(0)));
		
		Mockito.verify(networkMock, Mockito.never()).addRemoteDevices(Mockito.anyListOf(RemoteXBeeDevice.class));
		Mockito.verify(networkMock, Mockito.times(1)).addRemoteDevice(Mockito.any(RemoteXBeeDevice.class));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#startDiscoveryProcess(ArrayList)}.
	 * 
	 * <p>Two packets received from a ZigBee device.</p>
	 * 
	 * @throws Exception 
	 */
	@Test
	public final void testDiscoverDevicesTwoZigBeeDevice() throws Exception {
		// Setup the resources for the test.
		XBeeProtocol protocol = XBeeProtocol.ZIGBEE;
		PowerMockito.when(deviceMock.getXBeeProtocol()).thenReturn(protocol);
		
		XBee64BitAddress[] macs = new XBee64BitAddress[]{new XBee64BitAddress("0013A20040A6A0DB"), 
				new XBee64BitAddress("0013A20040AD1585")};
		XBee16BitAddress[] addr16 = new XBee16BitAddress[]{new XBee16BitAddress("0000"), 
				new XBee16BitAddress("7971")};
		XBee16BitAddress[] parentAddr = new XBee16BitAddress[]{new XBee16BitAddress("FFFE"), 
				new XBee16BitAddress("0000")};
		String[] nIds = new String[]{"Ni string 1", "Ni string 2"};
		
		ATCommandResponsePacket packet = createPacket(1, ATCommandStatus.OK, 
				addr16[0], macs[0], nIds[0], parentAddr[0], (byte)0x00 /* coordinator */, (byte)0x49);
		ndAnswers.add(packet);
		packet = createPacket(1, ATCommandStatus.OK, 
				addr16[1], macs[1], nIds[1], parentAddr[1], (byte)0x01 /* router */, (byte)0x67);
		ndAnswers.add(packet);
		
		DiscoveryListener listener = new DiscoveryListener();
		listeners.add(listener);
		
		// Call the method under test.
		nd.startDiscoveryProcess(listeners);
		
		// Verify the result.
		assertThat("The 'discoverDevices' should be running", listener.isFinished(), is(equalTo(false)));
		
		while(!listener.isFinished()) {
			Thread.sleep(30);
		}
		
		assertThat("The discovered devices list should be empty", listener.getDiscoveredDevices().size(), is(equalTo(ndAnswers.size())));
		
		List<RemoteXBeeDevice> list = listener.getDiscoveredDevices();
		
		for (int i = 0; i < list.size(); i++) {
			RemoteXBeeDevice remoteDevice = list.get(i);
			
			assertThat("Discovered device must not be null", remoteDevice, is(not(nullValue())));
			assertThat("Remote device protocol must be " + protocol.getDescription(), remoteDevice.getXBeeProtocol(), is(equalTo(protocol)));
			assertThat(remoteDevice instanceof RemoteZigBeeDevice, is(equalTo(true)));
			
			assertThat("Remote device Node ID is not right", remoteDevice.getNodeID(), is(equalTo(nIds[i])));
			assertThat("Remote device 64-bit address is not right", remoteDevice.get64BitAddress(), 
					is(equalTo(macs[i])));
			assertThat("Remote device 16-bit address is not right", ((RemoteZigBeeDevice)remoteDevice).get16BitAddress(), 
				is(equalTo(addr16[i])));
		}
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(SEND_NODE_DISCOVERY_COMMAND_METHOD, Mockito.anyString());
		Mockito.verify(deviceMock, Mockito.times(1)).addPacketListener(packetListener);
		Mockito.verify(deviceMock, Mockito.times(1)).removePacketListener(packetListener);
		
		PowerMockito.verifyPrivate(nd, Mockito.times(1)).invoke(DISCOVER_DEVICES_API_METHOD, listeners, null);
		PowerMockito.verifyPrivate(nd, Mockito.times(2)).invoke(PARSE_DISCOVERY_API_DATA_METHOD, Mockito.any(byte[].class), Mockito.eq(deviceMock));
		PowerMockito.verifyPrivate(nd, Mockito.times(2)).invoke(NOTIFY_DEVICE_DISCOVERED, Mockito.eq(listeners), Mockito.any(RemoteXBeeDevice.class));
		
		List<RemoteXBeeDevice> ndInternalDeviceList = Whitebox.<List<RemoteXBeeDevice>> getInternalState(nd, DEVICE_LIST);
		assertThat("Internal Node Discovery list must be empty", ndInternalDeviceList.size(), is(equalTo(0)));
		
		Mockito.verify(networkMock, Mockito.never()).addRemoteDevices(Mockito.anyListOf(RemoteXBeeDevice.class));
		Mockito.verify(networkMock, Mockito.times(2)).addRemoteDevice(Mockito.any(RemoteXBeeDevice.class));
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
	 * @return
	 */
	private ATCommandResponsePacket createPacket(int frameId, ATCommandStatus status, 
			XBee16BitAddress addr16, XBee64BitAddress addr64, String ni, XBee16BitAddress parentAddr, 
			byte role, byte rssi) {
		byte[] value = new byte[2 /* 16-bit addr */ + 8 /* 64-bit addr */ + ni.length() + 1 
		                        + 2 /* parent addr */ + 1 /* role */ + 1 /* status */ + 2 /* profile */ 
		                        + 2 /* manufacturer */ + 4 /* DD */ + 1 /* rssi */];
		
		byte[] profile =  new byte[]{(byte)0xC1, 0x05};
		byte[] manufacturer = new byte[]{0x10, 0x1E};
		byte[] dd = new byte[]{0x00, 0x03, 0x00, 0x00};
		
		System.arraycopy(addr16.getValue(), 0, value, 0, 2);
		System.arraycopy(addr64.getValue(), 0, value, 2, 8);
		System.arraycopy(ni.getBytes(), 0, value, 10, ni.length());
		value[10 + ni.length()] = 0x00;
		System.arraycopy(parentAddr.getValue(), 0, value, 11 + ni.length(), 2);
		value[13 + ni.length()] = role;
		value[14 + ni.length()] = 0x00;
		System.arraycopy(profile, 0, value, 15 + ni.length(), 2);
		System.arraycopy(manufacturer, 0, value, 17 + ni.length(), 2);
		System.arraycopy(dd, 0, value, 19 + ni.length(), 4);
		value[13 + ni.length()] = rssi;
		
		return new ATCommandResponsePacket(frameId, status, "ND", value);
	}
	
	static class DiscoveryListener implements IDiscoveryListener {
		
		List<RemoteXBeeDevice> discoveredDevices;
		List<String> errors;
		boolean finish;
		String finishError;
		
		public DiscoveryListener() {
			discoveredDevices = new ArrayList<RemoteXBeeDevice>(0);
			errors = new ArrayList<String>(0);
			finish = false;
		}
		
		public List<RemoteXBeeDevice> getDiscoveredDevices() {
			return discoveredDevices;
		}
		
		public List<String> getErrors() {
			return errors;
		}
		
		public boolean isFinished() {
			return finish;
		}
		
		public String getFinishError() {
			return finishError;
		}
		
		@Override
		public void discoveryFinished(String error) {
			finish = true;
			finishError = error;
		}
		
		@Override
		public void discoveryError(String error) {
			errors.add(error);
		}
		
		@Override
		public void deviceDiscovered(RemoteXBeeDevice discoveredDevice) {
			discoveredDevices.add(discoveredDevice);
		}
	};
}

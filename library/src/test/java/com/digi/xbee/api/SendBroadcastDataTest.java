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

import static org.junit.Assert.*;

import java.io.IOException;

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
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.TransmitException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.models.XBeeTransmitStatus;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.common.TransmitPacket;
import com.digi.xbee.api.packet.common.TransmitStatusPacket;
import com.digi.xbee.api.packet.raw.TX64Packet;
import com.digi.xbee.api.packet.raw.TXStatusPacket;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBeeDevice.class})
public class SendBroadcastDataTest {
	
	// Constants.
	private static final String SEND_DATA = "data";
	private static final byte[] SEND_DATA_BYTES = SEND_DATA.getBytes();
	
	private static final String SEND_XBEE_PACKET_METHOD = "sendXBeePacket";
	
	// Variables.
	private SerialPortRxTx mockedPort;
	private XBeeDevice xbeeDevice;
	
	private TX64Packet tx64Packet;
	private TXStatusPacket txStatusSuccess;
	private TXStatusPacket txStatusError;
	private TransmitPacket transmitPacket;
	private TransmitStatusPacket transmitStatusSuccess;
	private TransmitStatusPacket transmitStatusError;
	
	@Before
	public void setup() throws Exception {
		// Mock an RxTx IConnectionInterface.
		mockedPort = Mockito.mock(SerialPortRxTx.class);
		// When checking if the connection is open, return true.
		Mockito.when(mockedPort.isOpen()).thenReturn(true);
		
		// Instantiate an XBeeDevice object with the mocked interface.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(mockedPort));
		
		// Mock Tx64 packet.
		tx64Packet = Mockito.mock(TX64Packet.class);
		
		// Mock Tx Status packet SUCCESS.
		txStatusSuccess = Mockito.mock(TXStatusPacket.class);
		Mockito.when(txStatusSuccess.getTransmitStatus()).thenReturn(XBeeTransmitStatus.SUCCESS);
		
		// Mock Tx Status packet ERROR.
		txStatusError = Mockito.mock(TXStatusPacket.class);
		Mockito.when(txStatusError.getTransmitStatus()).thenReturn(XBeeTransmitStatus.ADDRESS_NOT_FOUND);
		
		// Mock Transmit Request packet.
		transmitPacket = Mockito.mock(TransmitPacket.class);
		
		// Mock Transmit Status packet SUCCESS.
		transmitStatusSuccess = Mockito.mock(TransmitStatusPacket.class);
		Mockito.when(transmitStatusSuccess.getTransmitStatus()).thenReturn(XBeeTransmitStatus.SUCCESS);
		
		// Mock Transmit Status packet ERROR.
		transmitStatusError = Mockito.mock(TransmitStatusPacket.class);
		Mockito.when(transmitStatusError.getTransmitStatus()).thenReturn(XBeeTransmitStatus.ADDRESS_NOT_FOUND);
		
		// Whenever a TX64Packet class is instantiated, the mocked tx64Packet packet should be returned.
		PowerMockito.whenNew(TX64Packet.class).withAnyArguments().thenReturn(tx64Packet);
		
		// Whenever a TransmitPacket class is instantiated, the mocked transmitPacket packet should be returned.
		PowerMockito.whenNew(TransmitPacket.class).withAnyArguments().thenReturn(transmitPacket);
	}
	
	
	/**
	 * Verify that we receive a {@code NullPointerException} when the broadcast data to be sent is null.
	 * 
	 * @throws Exception
	 */
	@Test(expected=NullPointerException.class)
	public void testSendBroadcastDataInvalidParams() throws Exception {
		// Try to send broadcast data with null data
		xbeeDevice.sendBroadcastData(null);
	}
	
	/**
	 * Verify that we receive an interface not open exception when the device is not open and 
	 * we try to send the broadcast data.
	 * 
	 * @throws Exception
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSendBroadcastDataConnectionClosed() throws Exception {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Send broadcast data.
		xbeeDevice.sendBroadcastData(SEND_DATA_BYTES);
	}
	
	/**
	 * Verify that broadcast data is considered successfully sent when the received TxStatus 
	 * packet contains a SUCCESS status. In this test case the protocol of the XBee device is 802.15.4.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testSendBroadcastData802Success() throws Exception {
		// Return that the protocol of the device is 802.15.4 when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		
		// Return the mocked TxStatus success packet when sending the mocked tx64Packet packet.
		PowerMockito.doReturn(txStatusSuccess).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(tx64Packet));
		
		// Verify that the broadcast packet is sent successfully.
		xbeeDevice.sendBroadcastData(SEND_DATA_BYTES);
		
		// Verify the sendXBeePacket method was called 1 time.
		PowerMockito.verifyPrivate(xbeeDevice, Mockito.times(1)).invoke(SEND_XBEE_PACKET_METHOD, (XBeeAPIPacket)Mockito.any());
	}
	
	/**
	 * Verify that broadcast data send fails when the received TxStatus packet contains a status 
	 * different than SUCCESS. In this test case the protocol of the XBee device is 802.15.4.
	 * 
	 * @throws Exception 
	 */
	@Test(expected=TransmitException.class)
	public void testSendBroadcastData802TxStatusError() throws Exception {
		// Return that the protocol of the device is 802.15.4 when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		
		// Return the mocked TxStatus error packet when sending the mocked tx64Packet packet.
		PowerMockito.doReturn(txStatusError).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(tx64Packet));
		
		// Send broadcast data.
		xbeeDevice.sendBroadcastData(SEND_DATA_BYTES);
	}
	
	/**
	 * Verify that broadcast data send fails when the operating mode is AT. In this test 
	 * case the protocol of the XBee device is 802.15.4.
	 * 
	 * @throws Exception 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSendBroadcastData802InvalidOperatingMode() throws Exception {
		// Return that the operating mode of the device is AT when asked.
		Mockito.when(xbeeDevice.getOperatingMode()).thenReturn(OperatingMode.AT);
		// Return that the protocol of the device is 802.15.4 when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		
		// Send broadcast data.
		xbeeDevice.sendBroadcastData(SEND_DATA_BYTES);
	}
	
	/**
	 * Verify that we receive a timeout exception when there is a timeout trying to send the 
	 * broadcast data. In this test case the protocol of the XBee device is 802.15.4.
	 * 
	 * @throws Exception 
	 */
	@Test(expected=TimeoutException.class)
	public void testSendBroadcastData802Timeout() throws Exception {
		// Return that the protocol of the device is 802.15.4 when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		
		// Throw a timeout exception when sending the mocked tx64Packet packets.
		PowerMockito.doThrow(new TimeoutException()).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(tx64Packet));
		
		// Send broadcast data.
		xbeeDevice.sendBroadcastData(SEND_DATA_BYTES);
	}
	
	/**
	 * Verify that broadcast data send fails (XBee exception thrown) when the 
	 * {@code sendXBeePacket} method throws an IO exception. In this test case the 
	 * protocol of the XBee device is 802.15.4.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testSendBroadcastData802IOError() throws Exception {
		// Return that the protocol of the device is 802.15.4 when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		
		// Throw an IO exception when trying to send an XBee packet.
		PowerMockito.doThrow(new IOException()).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(tx64Packet));
		
		// Send broadcast data.
		try {
			xbeeDevice.sendBroadcastData(SEND_DATA_BYTES);
			fail("Broadcast data shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(XBeeException.class, e.getClass());
			assertEquals(IOException.class, e.getCause().getClass());
		}
	}
	
	/**
	 * Verify that broadcast data is considered successfully sent when the received TxStatus 
	 * packet contains a SUCCESS status. In this test case the protocol of the XBee device is ZigBee 
	 * (other protocols but 802.15.4 behave the same way).
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testSendBroadcastDataOtherProtocolsSuccess() throws Exception {
		// Return that the protocol of the device is ZigBee when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		// Return the mocked TransmitStatus success packet when sending the mocked transmitPacket packet.
		PowerMockito.doReturn(transmitStatusSuccess).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(transmitPacket));
		
		// Verify that the broadcast packet is sent successfully.
		xbeeDevice.sendBroadcastData(SEND_DATA_BYTES);
		
		// Verify the sendXBeePacket method was called 1 time.
		PowerMockito.verifyPrivate(xbeeDevice, Mockito.times(1)).invoke(SEND_XBEE_PACKET_METHOD, (XBeeAPIPacket)Mockito.any());
	}
	
	/**
	 * Verify that broadcast data send fails when the operating mode is AT. In this 
	 * test case the protocol of the XBee device is ZigBee (other protocols but 802.15.4 behave 
	 * the same way).
	 * 
	 * @throws Exception 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSendBroadcastDataOtherProtocolsInvalidOperatingMode() throws Exception {
		// Return that the operating mode of the device is AT when asked.
		Mockito.when(xbeeDevice.getOperatingMode()).thenReturn(OperatingMode.AT);
		// Return that the protocol of the device is ZigBee when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		// Send broadcast data.
		xbeeDevice.sendBroadcastData(SEND_DATA_BYTES);
	}
	
	/**
	 * Verify that broadcast data send fails when the received TxStatus packet contains a 
	 * status different than SUCCESS. In this test case the protocol of the XBee device is ZigBee 
	 * (other protocols but 802.15.4 behave the same way).
	 * 
	 * @throws Exception
	 */
	@Test(expected=TransmitException.class)
	public void testSendBroadcastDataOtherProtocolsTxStatusError() throws Exception {
		// Return that the protocol of the device is ZigBee when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		// Return the mocked TransmitStatus error packet when sending the mocked transmitPacket packet.
		PowerMockito.doReturn(transmitStatusError).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(transmitPacket));
		
		// Send broadcast data.
		xbeeDevice.sendBroadcastData(SEND_DATA_BYTES);
	}
	
	/**
	 * Verify that we receive a timeout exception when there is a timeout trying to send the 
	 * broadcast data. In this test case the protocol of the XBee device is ZigBee 
	 * (other protocols but 802.15.4 behave the same way).
	 * 
	 * @throws Exception 
	 */
	@Test(expected=TimeoutException.class)
	public void testSendBroadcastDataOtherProtocolsTimeout() throws Exception {
		// Return that the protocol of the device is ZigBee when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		// Throw a timeout exception when sending the mocked transmitPacket packet.
		PowerMockito.doThrow(new TimeoutException()).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(transmitPacket));
		
		// Send broadcast data.
		xbeeDevice.sendBroadcastData(SEND_DATA_BYTES);
	}
	
	/**
	 * Verify that broadcast data send fails (XBee exception thrown) when the 
	 * {@code sendXBeePacket} method throws an IO exception. In this test case the protocol of 
	 * the XBee device is ZigBee (other protocols but 802.15.4 behave the same way).
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testSendBroadcastDataOtherProtocolsIOError() throws Exception {
		// Return that the protocol of the device is ZigBee when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		// Throw an IO exception when trying to send an XBee packet.
		PowerMockito.doThrow(new IOException()).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(transmitPacket));
		
		// Send broadcast data.
		try {
			xbeeDevice.sendBroadcastData(SEND_DATA_BYTES);
			fail("Broadcast data shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(XBeeException.class, e.getClass());
			assertEquals(IOException.class, e.getCause().getClass());
		}
	}
	
	/**
	 * Verify that when trying to send broadcast data from a remote XBee device to 
	 * a remote XBee device, an OperationNotSupportedException is thrown.
	 * 
	 * @throws Exception
	 */
	@Test(expected=OperationNotSupportedException.class)
	public void testSendBroadcastDataFromRemoteDevices() throws Exception {
		// Return that the XBee device is remote when asked.
		Mockito.when(xbeeDevice.isRemote()).thenReturn(true);
		
		// Send broadcast data.
		xbeeDevice.sendBroadcastData(SEND_DATA_BYTES);
	}
}
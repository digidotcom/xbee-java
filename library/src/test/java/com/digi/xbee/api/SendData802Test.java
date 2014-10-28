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
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeTransmitStatus;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.raw.TX16Packet;
import com.digi.xbee.api.packet.raw.TX64Packet;
import com.digi.xbee.api.packet.raw.TXStatusPacket;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Raw802Device.class})
public class SendData802Test {
	
	// Constants.
	private static final XBee16BitAddress XBEE_16BIT_ADDRESS = new XBee16BitAddress("0123");
	private static final XBee64BitAddress XBEE_64BIT_ADDRESS = new XBee64BitAddress("0123456789ABCDEF");
	
	private static final String SEND_DATA = "data";
	private static final byte[] SEND_DATA_BYTES = SEND_DATA.getBytes();
	
	private static final String SEND_XBEE_PACKET_METHOD = "sendXBeePacket";
	
	// Variables.
	private SerialPortRxTx mockedPort;
	private Raw802Device raw802Device;
	private RemoteRaw802Device mockedRemoteDevice;
	
	private TX16Packet tx16Packet;
	private TX64Packet tx64Packet;
	private TXStatusPacket txStatusSuccess;
	private TXStatusPacket txStatusError;
	
	@Before
	public void setup() throws Exception {
		// Mock an RxTx IConnectionInterface.
		mockedPort = Mockito.mock(SerialPortRxTx.class);
		// When checking if the connection is open, return true.
		Mockito.when(mockedPort.isOpen()).thenReturn(true);
		
		// Instantiate a Raw802Device object with the mocked interface.
		raw802Device = PowerMockito.spy(new Raw802Device(mockedPort));
		
		// Mock Tx16 packet.
		tx16Packet = Mockito.mock(TX16Packet.class);
		
		// Mock Tx64 packet.
		tx64Packet = Mockito.mock(TX64Packet.class);
		
		// Mock Tx Status packet SUCCESS.
		txStatusSuccess = Mockito.mock(TXStatusPacket.class);
		Mockito.when(txStatusSuccess.getTransmitStatus()).thenReturn(XBeeTransmitStatus.SUCCESS);
		
		// Mock Tx Status packet ERROR.
		txStatusError = Mockito.mock(TXStatusPacket.class);
		Mockito.when(txStatusError.getTransmitStatus()).thenReturn(XBeeTransmitStatus.ADDRESS_NOT_FOUND);
		
		// Mock a RemoteRaw802Device to be used as parameter in the send data command.
		mockedRemoteDevice = Mockito.mock(RemoteRaw802Device.class);
		Mockito.when(mockedRemoteDevice.get64BitAddress()).thenReturn(XBEE_64BIT_ADDRESS);
		
		// Whenever a TX16Packet class is instantiated, the mocked tx16Packet packet should be returned.
		PowerMockito.whenNew(TX16Packet.class).withAnyArguments().thenReturn(tx16Packet);
		
		// Whenever a TX64Packet class is instantiated, the mocked tx64Packet packet should be returned.
		PowerMockito.whenNew(TX64Packet.class).withAnyArguments().thenReturn(tx64Packet);
	}
	
	/**
	 * Verify that we receive a {@code NullPointerException} when either the address or the 
	 * data to be sent are null.
	 */
	@Test
	public void testSendData802InvalidParams() {
		// Try to send data with a null 16-bit address.
		try {
			raw802Device.sendData((XBee16BitAddress)null, SEND_DATA_BYTES);
			fail("Data shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		// Try to send data with a null 64-bit address.
		try {
			raw802Device.sendData((XBee64BitAddress)null, SEND_DATA_BYTES);
			fail("Data shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		// Try to send data with a null RemoteRaw802Device.
		try {
			raw802Device.sendData((RemoteRaw802Device)null, SEND_DATA_BYTES);
			fail("Data shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		} 
		// Try to send data with null data. 64-bit address.
		try {
			raw802Device.sendData(XBEE_64BIT_ADDRESS, null);
			fail("Data shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		// Try to send data with null data. 16-bit address.
		try {
			raw802Device.sendData(XBEE_16BIT_ADDRESS, null);
			fail("Data shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		// Try to send data with null data. RemoteRaw802Device device.
		try {
			raw802Device.sendData(mockedRemoteDevice, null);
			fail("Data shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		} 
	}
	
	/**
	 * Verify that we receive an interface not open exception when the device is not open and 
	 * we try to send the data.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSendData802ConnectionClosed() throws Exception {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Send data using the 16-bit address.
		try {
			raw802Device.sendData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Data shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(InterfaceNotOpenException.class, e.getClass());
		}
		// Send data using the 64-bit address.
		try {
			raw802Device.sendData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Data shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(InterfaceNotOpenException.class, e.getClass());
		}
		// Send data using a RemoteRaw802Device as parameter.
		try {
			raw802Device.sendData(mockedRemoteDevice, SEND_DATA_BYTES);
			fail("Data frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(InterfaceNotOpenException.class, e.getClass());
		}
	}
	
	/**
	 * Verify that data is considered successfully sent when the received TxStatus packet 
	 * contains a SUCCESS status.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testSendData802Success() throws Exception {
		// Return the mocked TxStatus success packet when sending the mocked tx16Packet or tx64Packet packets.
		PowerMockito.doReturn(txStatusSuccess).when(raw802Device, SEND_XBEE_PACKET_METHOD, Mockito.eq(tx16Packet));
		PowerMockito.doReturn(txStatusSuccess).when(raw802Device, SEND_XBEE_PACKET_METHOD, Mockito.eq(tx64Packet));
		
		// Verify that the packet is sent successfully when using the 16-bit address.
		raw802Device.sendData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
		// Verify that the packet is sent successfully when using the 64-bit address.
		raw802Device.sendData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
		// Verify that the packet is sent successfully when using a RemoteRaw802Device as parameter.
		raw802Device.sendData(mockedRemoteDevice, SEND_DATA_BYTES);
		
		// Verify the sendXBeePacket method was called 3 times (one for each data send).
		PowerMockito.verifyPrivate(raw802Device, Mockito.times(3)).invoke(SEND_XBEE_PACKET_METHOD, (XBeeAPIPacket)Mockito.any());
	}
	
	/**
	 * Verify that data send fails when the received TxStatus packet contains a status different 
	 * than SUCCESS.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testSendData802TxStatusError() throws Exception {
		// Return the mocked TxStatus error packet when sending the mocked tx16Packet or tx64Packet packets.
		PowerMockito.doReturn(txStatusError).when(raw802Device, SEND_XBEE_PACKET_METHOD, Mockito.eq(tx16Packet));
		PowerMockito.doReturn(txStatusError).when(raw802Device, SEND_XBEE_PACKET_METHOD, Mockito.eq(tx64Packet));
		
		// Send data using the 16-bit address.
		try {
			raw802Device.sendData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Tx16 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TransmitException.class, e.getClass());
		}
		// Send data using the 64-bit address.
		try {
			raw802Device.sendData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Tx64 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TransmitException.class, e.getClass());
		}
		// Send data using a RemoteRaw802Device as parameter.
		try {
			raw802Device.sendData(mockedRemoteDevice, SEND_DATA_BYTES);
			fail("Tx64 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TransmitException.class, e.getClass());
		}
	}
	
	/**
	 * Verify that data send fails when the operating mode is AT.
	 */
	@Test
	public void testSendData802InvalidOperatingMode() {
		// Return that the operating mode of the device is AT when asked.
		Mockito.when(raw802Device.getOperatingMode()).thenReturn(OperatingMode.AT);
		
		// Send data using the 16-bit address.
		try {
			raw802Device.sendData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Tx16 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(InvalidOperatingModeException.class, e.getClass());
		}
		// Send data using the 64-bit address.
		try {
			raw802Device.sendData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Tx64 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(InvalidOperatingModeException.class, e.getClass());
		}
		// Send data using a RemoteRaw802Device as parameter.
		try {
			raw802Device.sendData(mockedRemoteDevice, SEND_DATA_BYTES);
			fail("Tx64 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(InvalidOperatingModeException.class, e.getClass());
		}
	}
	
	/**
	 * Verify that we receive a timeout exception when there is a timeout trying to send the 
	 * data.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testSendData802Timeout() throws Exception {
		// Throw a timeout exception when sending the mocked tx16Packet or tx64Packet packets.
		PowerMockito.doThrow(new TimeoutException()).when(raw802Device, SEND_XBEE_PACKET_METHOD, Mockito.eq(tx16Packet));
		PowerMockito.doThrow(new TimeoutException()).when(raw802Device, SEND_XBEE_PACKET_METHOD, Mockito.eq(tx64Packet));
		
		// Send data using the 16-bit address.
		try {
			raw802Device.sendData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Tx16 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TimeoutException.class, e.getClass());
		}
		// Send data using the 64-bit address.
		try {
			raw802Device.sendData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Tx64 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TimeoutException.class, e.getClass());
		}
		// Send data using a RemoteRaw802Device as parameter.
		try {
			raw802Device.sendData(mockedRemoteDevice, SEND_DATA_BYTES);
			fail("Tx64 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TimeoutException.class, e.getClass());
		}
	}
	
	/**
	 * Verify that data send fails (XBee exception thrown) when the {@code sendXBeePacket} 
	 * method throws an IO exception.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testSendData802IOError() throws Exception {
		// Throw an IO exception when trying to send an XBee packet.
		PowerMockito.doThrow(new IOException()).when(raw802Device, SEND_XBEE_PACKET_METHOD, Mockito.eq(tx16Packet));
		PowerMockito.doThrow(new IOException()).when(raw802Device, SEND_XBEE_PACKET_METHOD, Mockito.eq(tx64Packet));
		
		// Send data using the 16-bit address.
		try {
			raw802Device.sendData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Tx16 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(XBeeException.class, e.getClass());
			assertEquals(IOException.class, e.getCause().getClass());
		}
		// Send data using the 64-bit address.
		try {
			raw802Device.sendData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Tx64 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(XBeeException.class, e.getClass());
			assertEquals(IOException.class, e.getCause().getClass());
		}
		// Send data using a RemoteRaw802Device as parameter.
		try {
			raw802Device.sendData(mockedRemoteDevice, SEND_DATA_BYTES);
			fail("Tx64 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(XBeeException.class, e.getClass());
			assertEquals(IOException.class, e.getCause().getClass());
		}
	}
	
	/**
	 * Verify that when trying to send data from a remote 802.15.4 XBee device to 
	 * other device, an OperationNotSupportedException is thrown.
	 */
	@Test
	public void testSendData802FromRemoteDevices() {
		// Return that the XBee device is remote when asked.
		Mockito.when(raw802Device.isRemote()).thenReturn(true);
		
		// Send data using the 16-bit address.
		try {
			raw802Device.sendData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Tx16 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(OperationNotSupportedException.class, e.getClass());
		}
		// Send data using the 64-bit address.
		try {
			raw802Device.sendData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Tx64 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(OperationNotSupportedException.class, e.getClass());
		}
		// Send data using a RemoteRaw802Device as parameter.
		try {
			raw802Device.sendData(mockedRemoteDevice, SEND_DATA_BYTES);
			fail("Tx64 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(OperationNotSupportedException.class, e.getClass());
		}
	}
}

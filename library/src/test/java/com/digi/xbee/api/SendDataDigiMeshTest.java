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
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeTransmitStatus;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.common.TransmitPacket;
import com.digi.xbee.api.packet.common.TransmitStatusPacket;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DigiMeshDevice.class})
public class SendDataDigiMeshTest {
	
	// Constants.
	private static final XBee64BitAddress XBEE_64BIT_ADDRESS = new XBee64BitAddress("0123456789ABCDEF");
	
	private static final String SEND_DATA = "data";
	private static final byte[] SEND_DATA_BYTES = SEND_DATA.getBytes();
	
	private static final String SEND_XBEE_PACKET_METHOD = "sendXBeePacket";
	
	// Variables.
	private SerialPortRxTx mockedPort;
	private DigiMeshDevice digiMeshDevice;
	private RemoteDigiMeshDevice mockedRemoteDevice;
	
	private TransmitPacket transmitPacket;
	private TransmitStatusPacket transmitStatusSuccess;
	private TransmitStatusPacket transmitStatusError;
	
	@Before
	public void setup() throws Exception {
		// Mock an RxTx IConnectionInterface.
		mockedPort = Mockito.mock(SerialPortRxTx.class);
		// When checking if the connection is open, return true.
		Mockito.when(mockedPort.isOpen()).thenReturn(true);
		
		// Instantiate a DigiMeshDevice object with the mocked interface.
		digiMeshDevice = PowerMockito.spy(new DigiMeshDevice(mockedPort));
		
		// Mock Transmit Request packet.
		transmitPacket = Mockito.mock(TransmitPacket.class);
		
		// Mock Transmit Status packet SUCCESS.
		transmitStatusSuccess = Mockito.mock(TransmitStatusPacket.class);
		Mockito.when(transmitStatusSuccess.getTransmitStatus()).thenReturn(XBeeTransmitStatus.SUCCESS);
		
		// Mock Transmit Status packet ERROR.
		transmitStatusError = Mockito.mock(TransmitStatusPacket.class);
		Mockito.when(transmitStatusError.getTransmitStatus()).thenReturn(XBeeTransmitStatus.ADDRESS_NOT_FOUND);
		
		// Mock a RemoteDigiMeshDevice to be used as parameter in the send data command.
		mockedRemoteDevice = Mockito.mock(RemoteDigiMeshDevice.class);
		Mockito.when(mockedRemoteDevice.get64BitAddress()).thenReturn(XBEE_64BIT_ADDRESS);
		
		// Whenever a TransmitPacket class is instantiated, the mocked transmitPacket packet should be returned.
		PowerMockito.whenNew(TransmitPacket.class).withAnyArguments().thenReturn(transmitPacket);
	}
	
	/**
	 * Verify that we receive a {@code NullPointerException} when either the address or the 
	 * data to be sent are null.
	 */
	@Test
	public void testSendDataDigiMeshInvalidParams() {
		// Try to send data with a null 64-bit address.
		try {
			digiMeshDevice.sendData((XBee64BitAddress)null, SEND_DATA_BYTES);
			fail("Data shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		// Try to send data with a null XBeeDevice.
		try {
			digiMeshDevice.sendData((RemoteDigiMeshDevice)null, SEND_DATA_BYTES);
			fail("Data shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		} 
		// Try to send data with null data. 64-bit address.
		try {
			digiMeshDevice.sendData(XBEE_64BIT_ADDRESS, null);
			fail("Data shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		// Try to send data with null data. RemoteDigiMeshDevice device.
		try {
			digiMeshDevice.sendData(mockedRemoteDevice, null);
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
	public void testSendDataDigiMeshConnectionClosed() throws Exception {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Send data using the 64-bit address.
		try {
			digiMeshDevice.sendData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Data shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(InterfaceNotOpenException.class, e.getClass());
		}
		// Send data using a RemoteDigiMeshDevice as parameter.
		try {
			digiMeshDevice.sendData(mockedRemoteDevice, SEND_DATA_BYTES);
			fail("Data shouldn't have been sent successfully.");
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
	public void testSendDataDigiMeshSuccess() throws Exception {
		// Return the mocked TransmitStatus success packet when sending the mocked transmitPacket packet.
		PowerMockito.doReturn(transmitStatusSuccess).when(digiMeshDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(transmitPacket));
		
		// Verify that the packet is sent successfully when using the 64-bit address.
		digiMeshDevice.sendData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
		// Verify that the packet is sent successfully when using a RemoteDigiMeshDevice as parameter.
		digiMeshDevice.sendData(mockedRemoteDevice, SEND_DATA_BYTES);
		
		// Verify the sendXBeePacket method was called 3 times (one for each data send).
		PowerMockito.verifyPrivate(digiMeshDevice, Mockito.times(2)).invoke(SEND_XBEE_PACKET_METHOD, (XBeeAPIPacket)Mockito.any());
	}
	
	/**
	 * Verify that data send fails when the received TxStatus packet contains a status different 
	 * than SUCCESS.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testSendDataDigiMeshTxStatusError() throws Exception {
		// Return the mocked TransmitStatus error packet when sending the mocked transmitPacket packet.
		PowerMockito.doReturn(transmitStatusError).when(digiMeshDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(transmitPacket));
		
		// Send data using the 64-bit address.
		try {
			digiMeshDevice.sendData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TransmitException.class, e.getClass());
		}
		// Send data using a RemoteDigiMeshDevice as parameter.
		try {
			digiMeshDevice.sendData(mockedRemoteDevice, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TransmitException.class, e.getClass());
		}
	}
	
	/**
	 * Verify that data send fails when the operating mode is AT.
	 */
	@Test
	public void testSendDataDigiMeshInvalidOperatingMode() {
		// Return that the operating mode of the device is AT when asked.
		Mockito.when(digiMeshDevice.getOperatingMode()).thenReturn(OperatingMode.AT);
		
		// Send data using the 64-bit address.
		try {
			digiMeshDevice.sendData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(InvalidOperatingModeException.class, e.getClass());
		}
		// Send data using a RemoteDigiMeshDevice as parameter.
		try {
			digiMeshDevice.sendData(mockedRemoteDevice, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
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
	public void testSendDataDigiMeshTimeout() throws Exception {
		// Throw a timeout exception when sending the mocked transmitPacket packet.
		PowerMockito.doThrow(new TimeoutException()).when(digiMeshDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(transmitPacket));
		
		// Send data using the 64-bit address.
		try {
			digiMeshDevice.sendData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TimeoutException.class, e.getClass());
		}
		// Send data using a RemoteDigiMeshDevice as parameter.
		try {
			digiMeshDevice.sendData(mockedRemoteDevice, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
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
	public void testSendDataDigiMeshIOError() throws Exception {
		// Throw an IO exception when trying to send an XBee packet.
		PowerMockito.doThrow(new IOException()).when(digiMeshDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(transmitPacket));
		
		// Send data using the 64-bit address.
		try {
			digiMeshDevice.sendData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(XBeeException.class, e.getClass());
			assertEquals(IOException.class, e.getCause().getClass());
		}
		// Send data using a RemoteDigiMeshDevice as parameter.
		try {
			digiMeshDevice.sendData(mockedRemoteDevice, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(XBeeException.class, e.getClass());
			assertEquals(IOException.class, e.getCause().getClass());
		}
	}
	
	/**
	 * Verify that when trying to send data from a remote DigiMesh XBee device to 
	 * other device, an OperationNotSupportedException is thrown.
	 */
	@Test
	public void testSendDataDigiMeshFromRemoteDevices() {
		// Return that the XBee device is remote when asked.
		Mockito.when(digiMeshDevice.isRemote()).thenReturn(true);
		
		// Send data using the 64-bit address.
		try {
			digiMeshDevice.sendData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(OperationNotSupportedException.class, e.getClass());
		}
		// Send data using a RemoteDigiMeshDevice as parameter.
		try {
			digiMeshDevice.sendData(mockedRemoteDevice, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(OperationNotSupportedException.class, e.getClass());
		}
	}
}

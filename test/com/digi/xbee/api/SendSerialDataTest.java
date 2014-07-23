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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.connection.serial.SerialPortRxTx;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.XBee16BitAddress;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeProtocol;
import com.digi.xbee.api.models.XBeeTransmitStatus;
import com.digi.xbee.api.packet.common.TransmitPacket;
import com.digi.xbee.api.packet.common.TransmitStatusPacket;
import com.digi.xbee.api.packet.raw.TX16Packet;
import com.digi.xbee.api.packet.raw.TX64Packet;
import com.digi.xbee.api.packet.raw.TXStatusPacket;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBeeDevice.class, TX16Packet.class, TX64Packet.class, TransmitPacket.class})
public class SendSerialDataTest {
	
	// Constants.
	private static final XBee16BitAddress XBEE_16BIT_ADDRESS = new XBee16BitAddress("0123");
	private static final XBee64BitAddress XBEE_64BIT_ADDRESS = new XBee64BitAddress("0123456789ABCDEF");
	
	private static final String SEND_DATA = "data";
	private static final byte[] SEND_DATA_BYTES = SEND_DATA.getBytes();
	
	// Variables.
	private XBeeDevice xbeeDevice;
	private XBeeDevice mockedDevice;
	
	private TX16Packet tx16Packet;
	private TX64Packet tx64Packet;
	private TXStatusPacket txStatusSuccess;
	private TXStatusPacket txStatusError;
	private TransmitPacket transmitPacket;
	private TransmitStatusPacket transmitStatusSuccess;
	private TransmitStatusPacket transmitStatusError;
	
	@Before
	public void setup() throws Exception {
		// Instantiate an XBeeDevice object with basic parameters.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(Mockito.mock(SerialPortRxTx.class)));
		
		// Mock Tx16 packet.
		tx16Packet = Mockito.mock(TX16Packet.class);
		
		// Mock Tx64 packet.
		tx64Packet = Mockito.mock(TX64Packet.class);
		
		// Mock Tx Status packet SUCCESS.
		txStatusSuccess = Mockito.mock(TXStatusPacket.class);
		Mockito.when(txStatusSuccess.getStatus()).thenReturn(XBeeTransmitStatus.SUCCESS);
		
		// Mock Tx Status packet ERROR.
		txStatusError = Mockito.mock(TXStatusPacket.class);
		Mockito.when(txStatusError.getStatus()).thenReturn(XBeeTransmitStatus.ADDRESS_NOT_FOUND);
		
		// Mock Transmit Request packet.
		transmitPacket = Mockito.mock(TransmitPacket.class);
		
		// Mock Transmit Status packet SUCCESS.
		transmitStatusSuccess = Mockito.mock(TransmitStatusPacket.class);
		Mockito.when(transmitStatusSuccess.getDeliveryStatus()).thenReturn(XBeeTransmitStatus.SUCCESS);
		
		// Mock Transmit Status packet ERROR.
		transmitStatusError = Mockito.mock(TransmitStatusPacket.class);
		Mockito.when(transmitStatusError.getDeliveryStatus()).thenReturn(XBeeTransmitStatus.ADDRESS_NOT_FOUND);
		
		// Mock an XBeeDevice to be used as parameter in the send serial data command.
		mockedDevice = Mockito.mock(XBeeDevice.class);
		Mockito.when(mockedDevice.get64BitAddress()).thenReturn(XBEE_64BIT_ADDRESS);
		
		// Whenever a TX16Packet class is instantiated, the mocked tx16Packet packet should be returned.
		PowerMockito.whenNew(TX16Packet.class).withAnyArguments().thenReturn(tx16Packet);
		
		// Whenever a TX64Packet class is instantiated, the mocked tx64Packet packet should be returned.
		PowerMockito.whenNew(TX64Packet.class).withAnyArguments().thenReturn(tx64Packet);
		
		// Whenever a TransmitPacket class is instantiated, the mocked transmitPacket packet should be returned.
		PowerMockito.whenNew(TransmitPacket.class).withAnyArguments().thenReturn(transmitPacket);
	}
	
	@Test
	/**
	 * Verify that serial data is considered successfully sent when the received TxStatus packet 
	 * contains a SUCCESS status. In this test case the protocol of the XBee device is 802.15.4 
	 * and the test is executed using all the different addressing parameters.
	 * 
	 * @throws Exception
	 */
	public void testSendSerialData802Success() throws Exception {
		// Return that the protocol of the device is 802.15.4 when asked.
		PowerMockito.doReturn(XBeeProtocol.RAW_802_15_4).when(xbeeDevice, "getXBeeProtocol");
		
		// Return the mocked TxStatus success packet when sending the mocked tx16Packet or tx64Packet packets.
		PowerMockito.doReturn(txStatusSuccess).when(xbeeDevice, "sendXBeePacket", tx16Packet);
		PowerMockito.doReturn(txStatusSuccess).when(xbeeDevice, "sendXBeePacket", tx64Packet);
		
		// Verify that the packet is sent successfully when using the 16-bit address.
		assertTrue(xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES));
		// Verify that the packet is sent successfully when using the 64-bit address.
		assertTrue(xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES));
		// Verify that the packet is sent successfully when using an XBeeDevice as parameter.
		assertTrue(xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES));
	}
	
	@Test
	/**
	 * Verify that serial data send fails when the received TxStatus packet contains a status different 
	 * than SUCCESS. In this test case the protocol of the XBee device is 802.15.4 and the test is 
	 * executed using all the different addressing parameters.
	 * 
	 * @throws Exception
	 */
	public void testSendSerialData802Error() throws Exception {
		// Return that the protocol of the device is 802.15.4 when asked.
		PowerMockito.doReturn(XBeeProtocol.RAW_802_15_4).when(xbeeDevice, "getXBeeProtocol");
		
		// Return the mocked TxStatus error packet when sending the mocked tx16Packet or tx64Packet packets.
		PowerMockito.doReturn(txStatusError).when(xbeeDevice, "sendXBeePacket", tx16Packet);
		PowerMockito.doReturn(txStatusError).when(xbeeDevice, "sendXBeePacket", tx64Packet);
		
		// Verify that the packet was not sent when using the 16-bit address..
		assertFalse(xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES));
		// Verify that the packet was not sent when using the 64-bit address.
		assertFalse(xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES));
		// Verify that the packet was not sent when using an XBeeDevice as parameter.
		assertFalse(xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES));
	}
	
	@Test
	/**
	 * Verify that we receive a timeout exception when there is a timeout trying to send the 
	 * serial data. In this test case the protocol of the XBee device is 802.15.4 and the test 
	 * is executed using all the different addressing parameters.
	 * 
	 * @throws Exception
	 */
	public void testSendSerialData802Timeout() throws Exception {
		// Return that the protocol of the device is 802.15.4 when asked.
		PowerMockito.doReturn(XBeeProtocol.RAW_802_15_4).when(xbeeDevice, "getXBeeProtocol");
		
		// Throw a timeout exception when sending the mocked tx16Packet or tx64Packet packets.
		PowerMockito.doThrow(new XBeeException(XBeeException.CONNECTION_TIMEOUT)).when(xbeeDevice, "sendXBeePacket", tx16Packet);
		PowerMockito.doThrow(new XBeeException(XBeeException.CONNECTION_TIMEOUT)).when(xbeeDevice, "sendXBeePacket", tx64Packet);
		
		// Send serial data using the 16-bit address.
		try {
			xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Tx64 frame shouldn't have been sent successfully.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			// Verify that we have received a timeout exception.
			assertEquals(XBeeException.CONNECTION_TIMEOUT, e.getErrorCode());
		}
		// Send serial data using the 64-bit address.
		try {
			xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			// Verify that we have received a timeout exception.
			assertEquals(XBeeException.CONNECTION_TIMEOUT, e.getErrorCode());
		}
		// Send serial data using an XBeeDevice as parameter.
		try {
			xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			// Verify that we have received a timeout exception.
			assertEquals(XBeeException.CONNECTION_TIMEOUT, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that we receive a connection not opened exception when the device is not connected and 
	 * we try to send the serial data. In this test case the protocol of the XBee device is 802.15.4 
	 * and the test is executed using all the different addressing parameters.
	 * 
	 * @throws Exception
	 */
	public void testSendSerialData802ConnectionClosed() throws Exception {
		// Return that the protocol of the device is 802.15.4 when asked.
		PowerMockito.doReturn(XBeeProtocol.RAW_802_15_4).when(xbeeDevice, "getXBeeProtocol");
		
		// Throw a connection closed exception when sending the mocked tx16Packet or tx64Packet packets.
		PowerMockito.doThrow(new XBeeException(XBeeException.CONNECTION_NOT_OPEN)).when(xbeeDevice, "sendXBeePacket", tx16Packet);
		PowerMockito.doThrow(new XBeeException(XBeeException.CONNECTION_NOT_OPEN)).when(xbeeDevice, "sendXBeePacket", tx64Packet);
		
		// Send serial data using the 16-bit address.
		try {
			xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Tx64 frame shouldn't have been sent successfully.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			// Verify that we have received a connection closed exception.
			assertEquals(XBeeException.CONNECTION_NOT_OPEN, e.getErrorCode());
		}
		// Send serial data using the 64-bit address.
		try {
			xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			// Verify that we have received a connection closed exception.
			assertEquals(XBeeException.CONNECTION_NOT_OPEN, e.getErrorCode());
		}
		// Send serial data using an XBeeDevice as parameter.
		try {
			xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			// Verify that we have received a connection closed exception.
			assertEquals(XBeeException.CONNECTION_NOT_OPEN, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that serial data is considered successfully sent when the received TxStatus packet 
	 * contains a SUCCESS status. In this test case the protocol of the XBee device is ZigBee 
	 * (other protocols but 802.15.4 behave the same way) and the test is executed using all the 
	 * different addressing parameters.
	 * 
	 * @throws Exception
	 */
	public void testSendSerialDataOtherProtocolsSuccess() throws Exception {
		// Return that the protocol of the device is ZigBee when asked.
		PowerMockito.doReturn(XBeeProtocol.ZIGBEE).when(xbeeDevice, "getXBeeProtocol");
		
		// Return the mocked TransmitStatus success packet when sending the mocked transmitPacket packet.
		PowerMockito.doReturn(transmitStatusSuccess).when(xbeeDevice, "sendXBeePacket", transmitPacket);
		
		// Verify that the packet is sent successfully when using the 16-bit address.
		assertTrue(xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES));
		// Verify that the packet is sent successfully when using the 64-bit address.
		assertTrue(xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES));
		// Verify that the packet is sent successfully when using an XBeeDevice as parameter.
		assertTrue(xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES));
	}
	
	@Test
	/**
	 * Verify that serial data send fails when the received TxStatus packet contains a status different 
	 * than SUCCESS. In this test case the protocol of the XBee device is ZigBee (other protocols 
	 * but 802.15.4 behave the same way) and the test is  executed using all the different addressing 
	 * parameters.
	 * 
	 * @throws Exception
	 */
	public void testSendSerialDataOtherProtocolsError() throws Exception {
		// Return that the protocol of the device is ZigBee when asked.
		PowerMockito.doReturn(XBeeProtocol.ZIGBEE).when(xbeeDevice, "getXBeeProtocol");
		
		// Return the mocked TransmitStatus error packet when sending the mocked transmitPacket packet.
		PowerMockito.doReturn(transmitStatusError).when(xbeeDevice, "sendXBeePacket", transmitPacket);
		
		// Verify that the packet was not sent when using the 16-bit address.
		assertFalse(xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES));
		// Verify that the packet was not sent when using the 64-bit address.
		assertFalse(xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES));
		// Verify that the packet was not sent when using an XBeeDevice as parameter.
		assertFalse(xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES));
	}
	
	@Test
	/**
	 * Verify that we receive a timeout exception when there is a timeout trying to send the 
	 * serial data. In this test case the protocol of the XBee device is ZigBee (other protocols 
	 * but 802.15.4 behave the same way) and the test is executed using all the different 
	 * addressing parameters.
	 * 
	 * @throws Exception
	 */
	public void testSendSerialDataOtherProtocolsTimeout() throws Exception {
		// Return that the protocol of the device is ZigBee when asked.
		PowerMockito.doReturn(XBeeProtocol.ZIGBEE).when(xbeeDevice, "getXBeeProtocol");
		
		// Throw a timeout exception when sending the mocked transmitPacket packet.
		PowerMockito.doThrow(new XBeeException(XBeeException.CONNECTION_TIMEOUT)).when(xbeeDevice, "sendXBeePacket", transmitPacket);
		
		// Send serial data using the 16-bit address.
		try {
			xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			// Verify that we have received a timeout exception.
			assertEquals(XBeeException.CONNECTION_TIMEOUT, e.getErrorCode());
		}
		// Send serial data using the 64-bit address.
		try {
			xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			// Verify that we have received a timeout exception.
			assertEquals(XBeeException.CONNECTION_TIMEOUT, e.getErrorCode());
		}
		// Send serial data using an XBeeDevice as parameter.
		try {
			xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			// Verify that we have received a timeout exception.
			assertEquals(XBeeException.CONNECTION_TIMEOUT, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that we receive a connection not opened exception when the device is not connected and 
	 * we try to send the serial data. In this test case the protocol of the XBee device is ZigBee 
	 * (other protocols but 802.15.4 behave the same way) and the test is executed using all the 
	 * different addressing parameters.
	 * 
	 * @throws Exception
	 */
	public void testSendSerialDataOtherProtocolsConnectionClosed() throws Exception {
		// Return that the protocol of the device is ZigBee when asked.
		PowerMockito.doReturn(XBeeProtocol.ZIGBEE).when(xbeeDevice, "getXBeeProtocol");
		
		// Throw a connection closed exception when sending the mocked transmitPacket packet.
		PowerMockito.doThrow(new XBeeException(XBeeException.CONNECTION_NOT_OPEN)).when(xbeeDevice, "sendXBeePacket", transmitPacket);
		
		// Send serial data using the 16-bit address.
		try {
			xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			// Verify that we have received a connection closed exception.
			assertEquals(XBeeException.CONNECTION_NOT_OPEN, e.getErrorCode());
		}
		// Send serial data using the 64-bit address.
		try {
			xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			// Verify that we have received a connection closed exception.
			assertEquals(XBeeException.CONNECTION_NOT_OPEN, e.getErrorCode());
		}
		// Send serial data using an XBeeDevice as parameter.
		try {
			xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			// Verify that we have received a connection closed exception.
			assertEquals(XBeeException.CONNECTION_NOT_OPEN, e.getErrorCode());
		}
	}
	
	@Test
	/**
	 * Verify that we receive an invalid argument exception when either the address or the 
	 * data to be sent is null.
	 * 
	 * @throws Exception
	 */
	public void testSendSerialDataInvalidParams() {
		// Try to send serial data with a null 16-bit address.
		try {
			xbeeDevice.sendSerialData((XBee16BitAddress)null, SEND_DATA_BYTES);
			fail("Serial data shouldn't have been sent successfully.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			// Verify that we have received an invalid argument exception.
			assertEquals(XBeeException.INVALID_ARGUMENT, e.getErrorCode());
		}
		// Try to send serial data with a null 64-bit address.
		try {
			xbeeDevice.sendSerialData((XBee64BitAddress)null, SEND_DATA_BYTES);
			fail("Serial data shouldn't have been sent successfully.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			// Verify that we have received an invalid argument exception.
			assertEquals(XBeeException.INVALID_ARGUMENT, e.getErrorCode());
		}
		// Try to send serial data with a null XBeeDevice.
		try {
			xbeeDevice.sendSerialData((XBeeDevice)null, SEND_DATA_BYTES);
			fail("Serial data shouldn't have been sent successfully.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			// Verify that we have received an invalid argument exception.
			assertEquals(XBeeException.INVALID_ARGUMENT, e.getErrorCode());
		}
		// Try to send serial data with null data.
		try {
			xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, null);
			fail("Serial data shouldn't have been sent successfully.");
		} catch (InvalidOperatingModeException e) {
			fail("This exception shouldn't be thrown now.");
		} catch (XBeeException e) {
			// Verify that we have received an invalid argument exception.
			assertEquals(XBeeException.INVALID_ARGUMENT, e.getErrorCode());
		}
	}
}

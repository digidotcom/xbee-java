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
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.OperationNotSupportedException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.TransmitException;
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
@PrepareForTest({XBeeDevice.class})
public class SendSerialDataTest {
	
	// Constants.
	private static final XBee16BitAddress XBEE_16BIT_ADDRESS = new XBee16BitAddress("0123");
	private static final XBee64BitAddress XBEE_64BIT_ADDRESS = new XBee64BitAddress("0123456789ABCDEF");
	
	private static final String SEND_DATA = "data";
	private static final byte[] SEND_DATA_BYTES = SEND_DATA.getBytes();
	
	private static final String SEND_XBEE_PACKET_METHOD = "sendXBeePacket";
	
	// Variables.
	private SerialPortRxTx mockedPort;
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
		// Mock an RxTx IConnectionInterface.
		mockedPort = Mockito.mock(SerialPortRxTx.class);
		// When checking if the connection is open, return true.
		Mockito.when(mockedPort.isOpen()).thenReturn(true);
		
		// Instantiate an XBeeDevice object with the mocked interface.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(mockedPort));
		
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
		
		// Mock Transmit Request packet.
		transmitPacket = Mockito.mock(TransmitPacket.class);
		
		// Mock Transmit Status packet SUCCESS.
		transmitStatusSuccess = Mockito.mock(TransmitStatusPacket.class);
		Mockito.when(transmitStatusSuccess.getTransmitStatus()).thenReturn(XBeeTransmitStatus.SUCCESS);
		
		// Mock Transmit Status packet ERROR.
		transmitStatusError = Mockito.mock(TransmitStatusPacket.class);
		Mockito.when(transmitStatusError.getTransmitStatus()).thenReturn(XBeeTransmitStatus.ADDRESS_NOT_FOUND);
		
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
	
	/**
	 * Verify that serial data is considered successfully sent when the received TxStatus packet 
	 * contains a SUCCESS status. In this test case the protocol of the XBee device is 802.15.4 
	 * and the test is executed using all the different addressing parameters.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSendSerialData802Success() throws Exception {
		// Return that the protocol of the device is 802.15.4 when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		
		// Return the mocked TxStatus success packet when sending the mocked tx16Packet or tx64Packet packets.
		PowerMockito.doReturn(txStatusSuccess).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(tx16Packet), Mockito.anyBoolean());
		PowerMockito.doReturn(txStatusSuccess).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(tx64Packet), Mockito.anyBoolean());
		
		// Verify that the packet is sent successfully when using the 16-bit address.
		xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
		// Verify that the packet is sent successfully when using the 64-bit address.
		xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
		// Verify that the packet is sent successfully when using an XBeeDevice as parameter.
		xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
	}
	
	/**
	 * Verify that serial data send fails when the received TxStatus packet contains a status different 
	 * than SUCCESS. In this test case the protocol of the XBee device is 802.15.4 and the test is 
	 * executed using all the different addressing parameters.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSendSerialData802Error() throws Exception {
		// Return that the protocol of the device is 802.15.4 when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		
		// Return the mocked TxStatus error packet when sending the mocked tx16Packet or tx64Packet packets.
		PowerMockito.doReturn(txStatusError).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, tx16Packet, true);
		PowerMockito.doReturn(txStatusError).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, tx64Packet, true);
		
		// Send serial data using the 16-bit address.
		try {
			xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Tx16 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TransmitException.class, e.getClass());
		}
		// Send serial data using the 64-bit address.
		try {
			xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Tx64 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TransmitException.class, e.getClass());
		}
		// Send serial data using an XBeeDevice as parameter.
		try {
			xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TransmitException.class, e.getClass());
		}
	}
	
	/**
	 * Verify that we receive a timeout exception when there is a timeout trying to send the 
	 * serial data. In this test case the protocol of the XBee device is 802.15.4 and the test 
	 * is executed using all the different addressing parameters.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSendSerialData802Timeout() throws Exception {
		// Return that the protocol of the device is 802.15.4 when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		
		// Throw a timeout exception when sending the mocked tx16Packet or tx64Packet packets.
		PowerMockito.doThrow(new TimeoutException()).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(tx16Packet), Mockito.anyBoolean());
		PowerMockito.doThrow(new TimeoutException()).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(tx64Packet), Mockito.anyBoolean());
		
		// Send serial data using the 16-bit address.
		try {
			xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Tx16 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TimeoutException.class, e.getClass());
		}
		// Send serial data using the 64-bit address.
		try {
			xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Tx64 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TimeoutException.class, e.getClass());
		}
		// Send serial data using an XBeeDevice as parameter.
		try {
			xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TimeoutException.class, e.getClass());
		}
	}
	
	/**
	 * Verify that we receive a connection not opened exception when the device is not connected and 
	 * we try to send the serial data. In this test case the protocol of the XBee device is 802.15.4 
	 * and the test is executed using all the different addressing parameters.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSendSerialData802ConnectionClosed() throws Exception {
		// Return that the protocol of the device is 802.15.4 when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.RAW_802_15_4);
		
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Send serial data using the 16-bit address.
		try {
			xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Tx16 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(InterfaceNotOpenException.class, e.getClass());
		}
		// Send serial data using the 64-bit address.
		try {
			xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Tx64 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(InterfaceNotOpenException.class, e.getClass());
		}
		// Send serial data using an XBeeDevice as parameter.
		try {
			xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(InterfaceNotOpenException.class, e.getClass());
		}
	}
	
	/**
	 * Verify that serial data is considered successfully sent when the received TxStatus packet 
	 * contains a SUCCESS status. In this test case the protocol of the XBee device is ZigBee 
	 * (other protocols but 802.15.4 behave the same way) and the test is executed using all the 
	 * different addressing parameters.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSendSerialDataOtherProtocolsSuccess() throws Exception {
		// Return that the protocol of the device is ZigBee when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		// Return the mocked TransmitStatus success packet when sending the mocked transmitPacket packet.
		PowerMockito.doReturn(transmitStatusSuccess).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(transmitPacket), Mockito.anyBoolean());
		
		// Verify that the packet is sent successfully when using the 16-bit address.
		xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
		// Verify that the packet is sent successfully when using the 64-bit address.
		xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
		// Verify that the packet is sent successfully when using an XBeeDevice as parameter.
		xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
	}
	
	/**
	 * Verify that serial data send fails when the received TxStatus packet contains a status different 
	 * than SUCCESS. In this test case the protocol of the XBee device is ZigBee (other protocols 
	 * but 802.15.4 behave the same way) and the test is  executed using all the different addressing 
	 * parameters.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSendSerialDataOtherProtocolsError() throws Exception {
		// Return that the protocol of the device is ZigBee when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		// Return the mocked TransmitStatus error packet when sending the mocked transmitPacket packet.
		PowerMockito.doReturn(transmitStatusError).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(transmitPacket), Mockito.anyBoolean());
		
		// Send serial data using the 16-bit address.
		try {
			xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Tx16 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TransmitException.class, e.getClass());
		}
		// Send serial data using the 64-bit address.
		try {
			xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
			fail("Tx64 frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TransmitException.class, e.getClass());
		}
		// Send serial data using an XBeeDevice as parameter.
		try {
			xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TransmitException.class, e.getClass());
		}
	}
	
	/**
	 * Verify that we receive a timeout exception when there is a timeout trying to send the 
	 * serial data. In this test case the protocol of the XBee device is ZigBee (other protocols 
	 * but 802.15.4 behave the same way) and the test is executed using all the different 
	 * addressing parameters.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSendSerialDataOtherProtocolsTimeout() throws Exception {
		// Return that the protocol of the device is ZigBee when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		// Throw a timeout exception when sending the mocked transmitPacket packet.
		PowerMockito.doThrow(new TimeoutException()).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(transmitPacket), Mockito.anyBoolean());
		
		// Send serial data using the 16-bit address.
		try {
			xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TimeoutException.class, e.getClass());
		}
		// Send serial data using the 64-bit address.
		try {
			xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TimeoutException.class, e.getClass());
		}
		// Send serial data using an XBeeDevice as parameter.
		try {
			xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(TimeoutException.class, e.getClass());
		}
	}
	
	/**
	 * Verify that we receive a connection not opened exception when the device is not connected and 
	 * we try to send the serial data. In this test case the protocol of the XBee device is ZigBee 
	 * (other protocols but 802.15.4 behave the same way) and the test is executed using all the 
	 * different addressing parameters.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSendSerialDataOtherProtocolsConnectionClosed() throws Exception {
		// Return that the protocol of the device is ZigBee when asked.
		Mockito.when(xbeeDevice.getXBeeProtocol()).thenReturn(XBeeProtocol.ZIGBEE);
		
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Send serial data using the 16-bit address.
		try {
			xbeeDevice.sendSerialData(XBEE_16BIT_ADDRESS, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(InterfaceNotOpenException.class, e.getClass());
		}
		// Send serial data using the 64-bit address.
		try {
			xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(InterfaceNotOpenException.class, e.getClass());
		}
		// Send serial data using an XBeeDevice as parameter.
		try {
			xbeeDevice.sendSerialData(mockedDevice, SEND_DATA_BYTES);
			fail("TransmitRequest frame shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(InterfaceNotOpenException.class, e.getClass());
		}
	}
	
	/**
	 * Verify that we receive an invalid argument exception when either the address or the 
	 * data to be sent is null.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSendSerialDataInvalidParams() {
		// Try to send serial data with a null 16-bit address.
		try {
			xbeeDevice.sendSerialData((XBee16BitAddress)null, SEND_DATA_BYTES);
			fail("Serial data shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		} 
		// Try to send serial data with a null 64-bit address.
		try {
			xbeeDevice.sendSerialData((XBee64BitAddress)null, SEND_DATA_BYTES);
			fail("Serial data shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		} 
		// Try to send serial data with a null XBeeDevice.
		try {
			xbeeDevice.sendSerialData((XBeeDevice)null, SEND_DATA_BYTES);
			fail("Serial data shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		} 
		// Try to send serial data with null data.
		try {
			xbeeDevice.sendSerialData(XBEE_64BIT_ADDRESS, null);
			fail("Serial data shouldn't have been sent successfully.");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		} 
	}
}

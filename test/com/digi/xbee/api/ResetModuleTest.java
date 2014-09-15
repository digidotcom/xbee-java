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
import com.digi.xbee.api.exceptions.ATCommandException;
import com.digi.xbee.api.exceptions.InterfaceNotOpenException;
import com.digi.xbee.api.exceptions.InvalidOperatingModeException;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.ATCommandStatus;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.common.ATCommandPacket;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBeeDevice.class})
public class ResetModuleTest {

	// Constants.
	private static final String SEND_XBEE_PACKET_METHOD = "sendXBeePacket";
	private static final String WAIT_FOR_MODEM_STATUS_PACKET_METHOD = "waitForModemStatusPacket";
	
	// Variables.
	private SerialPortRxTx mockedPort;
	private XBeeDevice xbeeDevice;
	
	private ATCommandPacket atCommandPacket;
	private ATCommandResponsePacket atCommandResponseOk;
	private ATCommandResponsePacket atCommandResponseError;
	
	@Before
	public void setup() throws Exception {
		// Mock an RxTx IConnectionInterface.
		mockedPort = Mockito.mock(SerialPortRxTx.class);
		// When checking if the connection is open, return true.
		Mockito.when(mockedPort.isOpen()).thenReturn(true);
		
		// Instantiate an XBeeDevice object with the mocked interface.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(mockedPort));
		// When checking the operating mode, return API.
		Mockito.when(xbeeDevice.getOperatingMode()).thenReturn(OperatingMode.API);
		
		// Mock ATCommand packet.
		atCommandPacket = Mockito.mock(ATCommandPacket.class);
		
		// Mock ATCommandResponse packet OK.
		atCommandResponseOk = Mockito.mock(ATCommandResponsePacket.class);
		Mockito.when(atCommandResponseOk.getStatus()).thenReturn(ATCommandStatus.OK);
		
		// Mock ATCommandResponse packet ERROR.
		atCommandResponseError = Mockito.mock(ATCommandResponsePacket.class);
		Mockito.when(atCommandResponseError.getStatus()).thenReturn(ATCommandStatus.ERROR);
		
		// Whenever a ATCommandPacket class in instantiated, the mocked atCommandPacket object should be returned.
		PowerMockito.whenNew(ATCommandPacket.class).withAnyArguments().thenReturn(atCommandPacket);
		
		// Return the mocked ATCommand OK packet when sending the mocked atCommandPacket object.
		PowerMockito.doReturn(atCommandResponseOk).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(atCommandPacket), Mockito.anyBoolean());
	}
	
	/**
	 * Verify that we receive an {@code InterfaceNotOpenException} exception
	 * when the device is not open and we try to perform a software reset.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSoftwareResetConnectionClosed() throws Exception {
		// When checking if the connection is open, return false.
		Mockito.when(mockedPort.isOpen()).thenReturn(false);
		
		// Perform a software reset.
		try {
			xbeeDevice.reset();
			fail("Software reset shouldn't have been performed successfully.");
		} catch (Exception e) {
			assertEquals(InterfaceNotOpenException.class, e.getClass());
		}
	}
	
	/**
	 * Verify that the software reset is considered successfully performed when
	 * the received ATCommand Response packet contains the status OK.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSoftwareResetOk() throws Exception {
		// Return True when waiting for the Modem Status packet.
		PowerMockito.doReturn(true).when(xbeeDevice, WAIT_FOR_MODEM_STATUS_PACKET_METHOD);
		
		// Verify that the software reset is performed successfully.
		xbeeDevice.reset();
		
		// Verify the sendATCommand Method was called 1 time.
		PowerMockito.verifyPrivate(xbeeDevice, Mockito.times(1)).invoke(SEND_XBEE_PACKET_METHOD, (XBeeAPIPacket)Mockito.any(), Mockito.anyBoolean());
	}
	
	/**
	 * Verify that the software reset fails when the received ATCommand Response 
	 * packet contains a status different than OK.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSoftwareResetError() throws Exception {
		// Return the mocked ATCommand OK packet when sending the mocked atCommandPacket object.
		PowerMockito.doReturn(atCommandResponseError).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(atCommandPacket), Mockito.anyBoolean());
		
		// Perform a software reset.
		try {
			xbeeDevice.reset();
			fail("Software reset shouldn't have been performed successfully.");
		} catch (Exception e) {
			assertEquals(ATCommandException.class, e.getClass());
		}
	}
	
	/**
	 * Verify that the software reset throws a {@code TimeoutException}
	 * exception when the Modem Status packet is not received.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSoftwareResetModemStatusPacketNotReceived() throws Exception {
		// Return False when waiting for the Modem Status packet.
		PowerMockito.doReturn(false).when(xbeeDevice, WAIT_FOR_MODEM_STATUS_PACKET_METHOD);
		
		// Perform a software reset.
		try {
			xbeeDevice.reset();
			fail("Software reset shouldn't have been performed successfully.");
		} catch (Exception e) {
			assertEquals(TimeoutException.class, e.getClass());
		}
	}
	
	/**
	 * Verify that the software reset fails when the operating mode is AT.
	 */
	@Test
	public void testSoftwareResetInvalidOperatingMode() {
		// Return that the operating mode of the device is AT when asked.
		Mockito.when(xbeeDevice.getOperatingMode()).thenReturn(OperatingMode.AT);
		
		// Perform a software reset.
		try {
			xbeeDevice.reset();
			fail("Software reset shouldn't have been performed successfully.");
		} catch (Exception e) {
			assertEquals(InvalidOperatingModeException.class, e.getClass());
		}
	}
	
	/**
	 * Verify that we receive a {@code TimeoutException} exception when there is 
	 * a timeout trying to perform the software reset.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSoftwareResetTimeout() throws Exception {
		// Throw a TimeoutException exception when sending the mocked atCommandPacket packet.
		PowerMockito.doThrow(new TimeoutException()).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(atCommandPacket), Mockito.anyBoolean());
		
		// Perform a software reset.
		try {
			xbeeDevice.reset();
			fail("Software reset shouldn't have been performed successfully.");
		} catch (Exception e) {
			assertEquals(TimeoutException.class, e.getClass());
		}
	}
	
	/**
	 * Verify that the software reset fails (XBeeException) when the
	 * {@code XBeeDevice#sendXBeePacket(com.digi.xbee.api.packet.XBeePacket)}
	 * method throws an {@code IOException} exception.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSoftwareResetIOError() throws Exception {
		// Throw a TimeoutException exception when sending the mocked atCommandPacket packet.
		PowerMockito.doThrow(new IOException()).when(xbeeDevice, SEND_XBEE_PACKET_METHOD, Mockito.eq(atCommandPacket), Mockito.anyBoolean());
		
		// Perform a software reset.
		try {
			xbeeDevice.reset();
			fail("Software reset shouldn't have been performed successfully.");
		} catch (Exception e) {
			assertEquals(XBeeException.class, e.getClass());
			assertEquals(IOException.class, e.getCause().getClass());
		}
	}
}

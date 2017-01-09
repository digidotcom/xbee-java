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
import com.digi.xbee.api.models.ATCommand;
import com.digi.xbee.api.models.ATCommandResponse;
import com.digi.xbee.api.models.ATCommandStatus;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XBeeDevice.class})
public class ResetModuleTest {

	// Constants.
	private static final String SEND_AT_COMMAND_METHOD = "sendATCommand";
	private static final String WAIT_FOR_MODEM_RESET_STATUS_PACKET_METHOD = "waitForModemResetStatusPacket";
	
	// Variables.
	private SerialPortRxTx connectionInterface;
	private XBeeDevice xbeeDevice;
	
	private ATCommand atCommand;
	private ATCommandResponse atCommandResponseOk;
	private ATCommandResponse atCommandResponseError;
	
	@Before
	public void setup() throws Exception {
		// Mock an RxTx IConnectionInterface.
		connectionInterface = Mockito.mock(SerialPortRxTx.class);
		// When checking if the connection is open, return true.
		Mockito.when(connectionInterface.isOpen()).thenReturn(true);
		
		// Instantiate an XBeeDevice object with the mocked interface.
		xbeeDevice = PowerMockito.spy(new XBeeDevice(connectionInterface));
		
		// Mock ATCommand.
		atCommand = Mockito.mock(ATCommand.class);
		
		// Mock ATCommandResponse packet OK.
		atCommandResponseOk = Mockito.mock(ATCommandResponse.class);
		Mockito.when(atCommandResponseOk.getResponseStatus()).thenReturn(ATCommandStatus.OK);
		
		// Mock ATCommandResponse packet ERROR.
		atCommandResponseError = Mockito.mock(ATCommandResponse.class);
		Mockito.when(atCommandResponseError.getResponseStatus()).thenReturn(ATCommandStatus.ERROR);
		
		// Whenever a ATCommandPacket class in instantiated, the mocked atCommandPacket object should be returned.
		PowerMockito.whenNew(ATCommand.class).withAnyArguments().thenReturn(atCommand);
		
		// Return the mocked ATCommand OK packet when sending the mocked atCommandPacket object.
		Mockito.doReturn(atCommandResponseOk).when(xbeeDevice).sendATCommand(atCommand);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#reset()}.
	 * 
	 * <p>Verify that we receive an {@code InterfaceNotOpenException} exception
	 * when the device is not open and we try to perform a software reset.</p>
	 * 
	 * @throws Exception
	 */
	@Test(expected=InterfaceNotOpenException.class)
	public void testSoftwareResetConnectionClosed() throws Exception {
		// When checking if the connection is open, return false.
		Mockito.when(connectionInterface.isOpen()).thenReturn(false);
		
		// Perform a software reset.
		xbeeDevice.reset();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#reset()}.
	 * 
	 * <p>Verify that the software reset is considered successfully performed when
	 * the received ATCommand Response packet contains the status OK.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSoftwareResetOk() throws Exception {
		// Return True when waiting for the Modem Status packet.
		PowerMockito.doReturn(true).when(xbeeDevice, WAIT_FOR_MODEM_RESET_STATUS_PACKET_METHOD);
		
		// Verify that the software reset is performed successfully.
		xbeeDevice.reset();
		
		// Verify the sendATCommand Method was called 1 time.
		PowerMockito.verifyPrivate(xbeeDevice, Mockito.times(1)).invoke(SEND_AT_COMMAND_METHOD, atCommand);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#reset()}.
	 * 
	 * <p>Verify that the software reset fails when the received ATCommand Response 
	 * packet contains a status different than OK.</p>
	 * 
	 * @throws Exception
	 */
	@Test(expected=ATCommandException.class)
	public void testSoftwareResetError() throws Exception {
		// Return the mocked ATCommand OK packet when sending the mocked atCommandPacket object.
		Mockito.doReturn(atCommandResponseError).when(xbeeDevice).sendATCommand(atCommand);
		
		// Perform a software reset.
		xbeeDevice.reset();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#reset()}.
	 * 
	 * <p>Verify that the software reset throws a {@code TimeoutException}
	 * exception when the Modem Status packet is not received.</p>
	 * 
	 * @throws Exception
	 */
	@Test(expected=TimeoutException.class)
	public void testSoftwareResetModemStatusPacketNotReceived() throws Exception {
		// Return False when waiting for the Modem Status packet.
		PowerMockito.doReturn(false).when(xbeeDevice, WAIT_FOR_MODEM_RESET_STATUS_PACKET_METHOD);
		
		// Perform a software reset.
		xbeeDevice.reset();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#reset()}.
	 * 
	 * <p>Verify that the software reset fails when the operating mode is AT.</p>
	 * 
	 * @throws Exception 
	 */
	@Test(expected=InvalidOperatingModeException.class)
	public void testSoftwareResetInvalidOperatingMode() throws Exception {
		// Return an invalid operating mode exception when trying to send an AT command.
		Mockito.doThrow(new InvalidOperatingModeException()).when(xbeeDevice).sendATCommand(atCommand);
		
		// Perform a software reset.
		xbeeDevice.reset();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#reset()}.
	 * 
	 * <p>Verify that we receive a {@code TimeoutException} exception when there is 
	 * a timeout trying to perform the software reset.</p>
	 * 
	 * @throws Exception
	 */
	@Test(expected=TimeoutException.class)
	public void testSoftwareResetTimeout() throws Exception {
		// Throw a TimeoutException exception when sending the mocked atCommandPacket packet.
		Mockito.doThrow(new TimeoutException()).when(xbeeDevice).sendATCommand(atCommand);
		
		// Perform a software reset.
		xbeeDevice.reset();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.XBeeDevice#reset()}.
	 * 
	 * <p>Verify that the software reset fails (XBeeException) when the
	 * {@code XBeeDevice#sendATCommand(com.digi.xbee.api.models.ATCommand)}
	 * method throws an {@code IOException} exception.</p>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSoftwareResetIOError() throws Exception {
		// Throw a TimeoutException exception when sending the mocked atCommandPacket packet.
		Mockito.doThrow(new IOException()).when(xbeeDevice).sendATCommand(atCommand);
		
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

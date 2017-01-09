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
package com.digi.xbee.api.models;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class XBeeProtocolTest {

	// Constants.
	private static final int INVALID_ID = -1;
	
	private static final String FILE_FIRMWARE_ENTRIES = "firmware_entries_xctu.txt";
	
	// Variables.
	private XBeeProtocol[] xbeeProtocolValues;
	
	@Before
	public void setup() {
		// Retrieve the list of enum. values.
		xbeeProtocolValues = XBeeProtocol.values();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeProtocol#getID()}.
	 * 
	 * <p>Verify that the value of each HardwareVersionEnum entry is valid.</p>
	 */
	@Test
	public void testHardwareVersionEnumValues() {
		for (XBeeProtocol xbeeProtocol:xbeeProtocolValues)
			assertTrue(xbeeProtocol.getID() >= 0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeProtocol#name()}.
	 * 
	 * <p>Verify that the name of each XBeeProtocol entry is valid.</p>
	 */
	@Test
	public void testXBeeProtocolNames() {
		for (XBeeProtocol xbeeProtocol:xbeeProtocolValues) {
			assertNotNull(xbeeProtocol.name());
			assertTrue(xbeeProtocol.name().length() > 0);
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeProtocol#getDescription()}.
	 * 
	 * <p>Verify that the description of each XBeeProtocol entry is valid.</p>
	 */
	@Test
	public void testXBeeProtocolDescriptions() {
		for (XBeeProtocol xbeeProtocol:xbeeProtocolValues) {
			assertNotNull(xbeeProtocol.getDescription());
			assertTrue(xbeeProtocol.getDescription().length() > 0);
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeProtocol#get(int)}.
	 * 
	 * <p>Verify that each XBeeProtocol entry can be retrieved statically using its ID.</p>
	 */
	@Test
	public void testXBeeProtocolStaticAccess() {
		for (XBeeProtocol xbeeProtocol:xbeeProtocolValues)
			assertEquals(xbeeProtocol, XBeeProtocol.get(xbeeProtocol.getID()));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeProtocol#get(int)}.
	 * 
	 * <p>Verify that "UNKNOWN" protocol is retrieved statically using an invalid ID.</p>
	 */
	@Test
	public void testUnknownProtocolIsRetrievedWithInvalidID() {
		assertEquals(XBeeProtocol.UNKNOWN, XBeeProtocol.get(INVALID_ID));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeProtocol#toString()}.
	 * 
	 * <p>Verify that the {@code toString()} method of an XBee protocol entry returns its description.</p>
	 */
	@Test
	public void testXBeeProtocolToString() {
		for (XBeeProtocol xbeeProtocol:xbeeProtocolValues)
			assertEquals(xbeeProtocol.getDescription(), xbeeProtocol.toString());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeProtocol#determineProtocol(HardwareVersion, String)}
	 * 
	 * <p>Verify that the {@code determineProtocol()} method return UNKNOWN for {@code null} hardware version.</p>
	 */
	@Test
	public void testDetermineProtocolHardwareVersionNull() {
		// Setup the resources for the test.
		String fv = "";
		HardwareVersion hv = null;
		XBeeProtocol expectedProtocol = XBeeProtocol.UNKNOWN;
		
		// Call the method under test.
		XBeeProtocol p = XBeeProtocol.determineProtocol(hv, fv);
		
		// Verify the result.
		assertThat("Expected protocol is XBeeProtocol.UNKNOWN", p, is(equalTo(expectedProtocol)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeProtocol#determineProtocol(HardwareVersion, String)}
	 * 
	 * <p>Verify that the {@code determineProtocol()} method return UNKNOWN for {@code null} firmware version.</p>
	 */
	@Test
	public void testDetermineProtocolFirmwareVersionNull() {
		// Setup the resources for the test.
		String fv = null;
		HardwareVersion hv = HardwareVersion.get(HardwareVersionEnum.X24_019.getValue());
		XBeeProtocol expectedProtocol = XBeeProtocol.UNKNOWN;
		
		// Call the method under test.
		XBeeProtocol p = XBeeProtocol.determineProtocol(hv, fv);
		
		// Verify the result.
		assertThat("Expected protocol is XBeeProtocol.UNKNOWN", p, is(equalTo(expectedProtocol)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeProtocol#determineProtocol(HardwareVersion, String)}
	 * 
	 * <p>Verify that the {@code determineProtocol()} method return UNKNOWN for a hardware version less than 0x09.</p>
	 */
	@Test
	public void testDetermineProtocolHardwareVersionValueLessThan9() {
		// Setup the resources for the test.
		String fv = "2040";
		HardwareVersion hv = HardwareVersion.get(5);
		XBeeProtocol expectedProtocol = XBeeProtocol.UNKNOWN;
		
		// Call the method under test.
		XBeeProtocol p = XBeeProtocol.determineProtocol(hv, fv);
		
		// Verify the result.
		assertThat("Expected protocol is XBeeProtocol.UNKNOWN", p, is(equalTo(expectedProtocol)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeProtocol#determineProtocol(HardwareVersion, String)}
	 * 
	 * <p>Verify that the {@code determineProtocol()} method return UNKNOWN for a hardware version less than 0x09.</p>
	 */
	@Test
	public void testDetermineProtocolHardwareVersionNotInTheList() {
		// Setup the resources for the test.
		String fv = "2040";
		HardwareVersion hv = HardwareVersion.get(999999999);
		XBeeProtocol expectedProtocol = XBeeProtocol.UNKNOWN;
		
		// Call the method under test.
		XBeeProtocol p = XBeeProtocol.determineProtocol(hv, fv);
		
		// Verify the result.
		assertThat("Expected protocol is XBeeProtocol.UNKNOWN", p, is(equalTo(expectedProtocol)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.XBeeProtocol#determineProtocol(HardwareVersion, String)}
	 * 
	 * <p>Verify that the {@code determineProtocol()} method is able to obtain the protocol of any firmware file 
	 * contained in the XCTU application.</p>
	 * 
	 * @throws IOException
	 */
	@Test
	public void testDetermineProtocol() throws IOException {
		ArrayList<FirmwareEntry> firmwareEntries = new ArrayList<FirmwareEntry>();
		
		// Generate the list of firmware entries from the firmware_entries_xctu.txt file.
		InputStream inputStream = getClass().getResourceAsStream(FILE_FIRMWARE_ENTRIES);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		try {
			String line = reader.readLine();
			while (line != null) {
				// Skip empty lines.
				if (line.trim().length() == 0) {
					line = reader.readLine();
					continue;
				}
				// Stop the test if any entry of the file is wrong.
				String firmwareValues[] = line.split(";");
				if (firmwareValues.length < 3)
					fail("Invalid firmware entry (incomplete): " + line);
				String hardwareVersion = firmwareValues[0].trim();
				try {
					Integer.parseInt(hardwareVersion, 16);
				} catch (NumberFormatException e) {
					fail("Invalid firmware entry (hardware version): " + line);
				}
				String firmwareVersion = firmwareValues[1].trim();
				String xbeeProtocolName = firmwareValues[2].trim();
				
				// Generate a FirmwareEntry object and add it to the list.
				FirmwareEntry firmwareEntry = new FirmwareEntry(Integer.parseInt(hardwareVersion, 16), firmwareVersion, xbeeProtocolName);
				firmwareEntries.add(firmwareEntry);
				
				line = reader.readLine();
			}
		} catch (IOException e) {
			try {
				reader.close();
			} catch (IOException e1) { }
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) { }
		}
		
		// Verify that the determineProtocol method is able to determine the protocol of all the firmware entries of the list.
		for (FirmwareEntry firmwareEntry:firmwareEntries) {
			XBeeProtocol determinedProtocol = XBeeProtocol.determineProtocol(firmwareEntry.getHardwareVersion(), firmwareEntry.getFirmwareVersion());
			// The protocol of the entry should be the same as the one determined by the method.
			assertEquals(firmwareEntry.getXBeeProtocolName(), determinedProtocol.getDescription());
		}
	}
	
	/**
	 * Helper class that represents a firmware entry from the list of XCTU firmware entries.
	 */
	private class FirmwareEntry {
		HardwareVersion hardwareVersion;
		String firmwareversion;
		String xbeeProtocolName;
		
		public FirmwareEntry(int hardwareVersion, String firmwareVersion, String xbeeProtocolName) {
			this.hardwareVersion = HardwareVersion.get(hardwareVersion);
			this.firmwareversion = firmwareVersion;
			this.xbeeProtocolName = xbeeProtocolName;
		}
		
		/**
		 * Retrieves the firmware version of the firmware entry.
		 * 
		 * @return The firmware version.
		 */
		public String getFirmwareVersion() {
			return firmwareversion;
		}
		
		/**
		 * Retrieves the hardware version of the firmware entry.
		 * 
		 * @return The hardware version.
		 */
		public HardwareVersion getHardwareVersion() {
			return hardwareVersion;
		}
		
		/**
		 * Retrieves the XBee protocol name corresponding to the firmware entry.
		 * 
		 * @return The XBee protocol name.
		 */
		public String getXBeeProtocolName() {
			return xbeeProtocolName;
		}
	}
}

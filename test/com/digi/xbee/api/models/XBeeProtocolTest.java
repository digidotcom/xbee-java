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
package com.digi.xbee.api.models;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
	 * <p>Verify that the {@code determineProtocol()} method is able to obtain the protocol of any firmware file 
	 * contained in the XCTU application.</p>
	 * 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testDetermineProtocol() throws FileNotFoundException {
		ArrayList<FirmwareEntry> firmwareEntries = new ArrayList<FirmwareEntry>();
		
		// Generate the list of firmware entries from the firmware_entries_xctu.txt file.
		File firmwareEntriesFile = new File(getClass().getResource(FILE_FIRMWARE_ENTRIES).getFile());
		BufferedReader reader = new BufferedReader(new FileReader(firmwareEntriesFile.getAbsolutePath()));
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

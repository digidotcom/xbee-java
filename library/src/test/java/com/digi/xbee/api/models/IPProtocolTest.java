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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class IPProtocolTest {

	// Variables.
	private IPProtocol[] protocolValues;


	@Before
	public void setup() {
		// Retrieve the list of enum. values.
		protocolValues = IPProtocol.values();
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IPProtocol#getID()}.
	 *
	 * <p>Verify that the ID of each {@code IPProtocol} entry is valid.</p>
	 */
	@Test
	public void testIPProtocolEnumValues() {
		for (IPProtocol protocol:protocolValues)
			assertTrue(protocol.getID() >= 0);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IPProtocol#name()}.
	 *
	 * <p>Verify that the name of each {@code IPProtocol} entry is valid.
	 * </p>
	 */
	@Test
	public void testIPProtocolEnumNames() {
		for (IPProtocol protocol:protocolValues) {
			assertNotNull(protocol.name());
			assertTrue(protocol.name().length() > 0);
		}
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IPProtocol#get(int)}.
	 *
	 * <p>Verify that each {@code IPProtocol} entry can be retrieved
	 * statically using its value.</p>
	 */
	@Test
	public void testIPProtocolStaticAccess() {
		for (IPProtocol protocol:protocolValues)
			assertEquals(protocol, IPProtocol.get(protocol.getID()));
	}

	/**
	 * Test method for {@link com.digi.xbee.api.models.IPProtocol#toString()}.
	 *
	 * <p>Verify that the {@code toString()} method of a {@code IPProtocol}
	 * entry returns its description correctly.</p>
	 */
	@Test
	public void testIPProtocolToString() {
		for (IPProtocol protocol:protocolValues)
			assertEquals(protocol.getName(), protocol.toString());
	}
}
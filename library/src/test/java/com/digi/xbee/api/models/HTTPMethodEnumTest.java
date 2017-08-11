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

import com.digi.xbee.api.utils.HexUtils;

public class HTTPMethodEnumTest {

	// Variables.
	private HTTPMethodEnum[] httpMethodValues;
	
	
	@Before
	public void setup() {
		// Retrieve the list of enum. values.
		httpMethodValues = HTTPMethodEnum.values();
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HTTPMethodEnum#getValue()}.
	 * 
	 * <p>Verify that the value of each HTTPMethodEnum entry is valid.</p>
	 */
	@Test
	public void testHTTPMethodEnumValues() {
		for (HTTPMethodEnum httpMethodEnum:httpMethodValues)
			assertTrue(httpMethodEnum.getValue() >= 0);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HTTPMethodEnum#name()}.
	 * 
	 * <p>Verify that the name of each HTTPMethodEnum entry is valid.</p>
	 */
	@Test
	public void testHTTPMethodEnumNames() {
		for (HTTPMethodEnum httpMethodEnum:httpMethodValues) {
			assertNotNull(httpMethodEnum.name());
			assertTrue(httpMethodEnum.name().length() > 0);
		}
	}
	
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HTTPMethodEnum#get(int)}.
	 * 
	 * <p>Verify that each HTTPMethodEnum entry can be retrieved statically using its ID.</p>
	 */
	@Test
	public void testHTTPMethodEnumStaticAccess() {
		for (HTTPMethodEnum httpMethodEnum:httpMethodValues) {
			assertEquals(httpMethodEnum, HTTPMethodEnum.get(httpMethodEnum.getValue()));
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HTTPMethodEnum#get(int)}.
	 * 
	 * <p>Verify that when trying to get an invalid {@code HTTPMethodEnum} 
	 * entry, a {@code null} entry is retrieved.</p>
	 */
	@Test
	public void testHTTPMethodEnumStaticInvalidAccess() {
		assertNull(HTTPMethodEnum.get(0xAA));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HTTPMethodEnum#toString()}.
	 * 
	 * <p>Verify that the {@code toString()} method of an {@code HTTPMethodEnum} 
	 * entry returns its description correctly.</p>
	 */
	@Test
	public void testHTTPMethodEnumToString() {
		for (HTTPMethodEnum httpMethodEnum:httpMethodValues)
			assertEquals(HexUtils.byteToHexString((byte)httpMethodEnum.getValue()) + 
					": " + httpMethodEnum.getName(), httpMethodEnum.toString());
	}
}

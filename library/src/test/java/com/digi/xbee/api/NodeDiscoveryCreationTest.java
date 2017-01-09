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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.digi.xbee.api.NodeDiscovery;
import com.digi.xbee.api.XBeeDevice;

@PrepareForTest({XBeeDevice.class, NodeDiscovery.class})
@RunWith(PowerMockRunner.class)
public class NodeDiscoveryCreationTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Variables.
	private XBeeDevice deviceMock;
	
	@Before
	public void setUp() throws Exception {
		deviceMock = PowerMockito.mock(XBeeDevice.class);
		PowerMockito.when(deviceMock.isOpen()).thenReturn(true);
	}

	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#NodeDiscovery(com.digi.xbee.api.XBeeDevice)}.
	 * 
	 * <p>A {@code NullPointerException} exception must be thrown when parsing a 
	 * {@code null} local deviceMock is passed.</p>
	 */
	@Test
	public final void testCreateNodeDiscoveryNullDevice() {
		// Setup the resources for the test.
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Local XBee device cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new NodeDiscovery(null);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.NodeDiscovery#NodeDiscovery(com.digi.xbee.api.XBeeDevice)}.
	 * 
	 * <p>Test for the creation of a {@code NodeDiscovery} object.</p>
	 */
	@Test
	public final void testCreateNodeDiscovery() {
		// Setup the resources for the test.
		XBeeDevice device = PowerMockito.mock(XBeeDevice.class);
		
		// Call the method under test.
		NodeDiscovery nd = new NodeDiscovery(device);
		
		// Verify the result.
		assertThat("Node discovery should not be null", nd, is(not(nullValue())));
	}
}

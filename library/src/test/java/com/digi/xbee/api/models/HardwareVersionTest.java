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
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HardwareVersionEnum.class})
public class HardwareVersionTest {

	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#get(int, String)}.
	 * 
	 * <p>Verify that it is not possible to get a HardWareVersion object statically if any of 
	 * the provided parameters is not valid using the get() method of the class.</p>
	 */
	@Test
	public void testGetHardwareVersionInvalid() {
		// Try to instantiate a HardwareVersion object with a null description.
		try {
			HardwareVersion.get(0, null);
		} catch(Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		// Try to instantiate a HardwareVersion object with an invalid ID.
		try {
			HardwareVersion.get(-1, "Description");
		} catch(Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
		// Try to instantiate a HardwareVersion object with an invalid description.
		try {
			HardwareVersion.get(0, "");
		} catch(Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#get(int, String)}.
	 * 
	 * <p>Verify that it is possible to get a valid HardWareVersion object statically using 
	 * the get() method of the class.</p>
	 */
	@Test
	public void testGetHardwareVersionValid() {
		HardwareVersion hardwareVersion = HardwareVersion.get(0, "Description");
		
		assertEquals(0, hardwareVersion.getValue());
		assertEquals("Description", hardwareVersion.getDescription());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#HardwareVersion(String, int)}.
	 * 
	 * <p>Verify that HardwareVersion object can be created successfully.</p>
	 */
	@Test
	public void testCreateHardwareVersionSuccess() {
		HardwareVersion hardwareVersion = HardwareVersion.get(0, "Description");
		
		assertEquals(0, hardwareVersion.getValue());
		assertEquals("Description", hardwareVersion.getDescription());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#equals(Object)}.
	 * 
	 * <p>Verify that HardwareVersion equals method works as expected.</p>
	 */
	@Test
	public void testHardwareVersionEquals() {
		HardwareVersion hardwareVersion1 = HardwareVersion.get(0, "Description");
		HardwareVersion hardwareVersion2 = HardwareVersion.get(0, "Description");
		HardwareVersion hardwareVersion3 = HardwareVersion.get(0, "Different description");
		HardwareVersion hardwareVersion4 = HardwareVersion.get(1, "Description");
		HardwareVersion hardwareVersion5 = HardwareVersion.get(1, "Different description");
		
		assertTrue(hardwareVersion1.equals(hardwareVersion2));
		assertFalse(hardwareVersion1.equals(hardwareVersion3));
		assertFalse(hardwareVersion1.equals(hardwareVersion4));
		assertFalse(hardwareVersion1.equals(hardwareVersion5));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#get(int)}.
	 * 
	 * <p>Verify that a valid HardwareVersion is received when querying it using the static get() method 
	 * of the class and there is a HardwareVersionEnum entry for the provided ID.</p>
	 */
	@Test
	public void testGetHardwareVersionKnown() {
		// Prepare the HardwareVersionEnum class to return the XBP24B entry value when asked for one.
		PowerMockito.mockStatic(HardwareVersionEnum.class);
		PowerMockito.when(HardwareVersionEnum.get(Mockito.anyInt())).thenReturn(HardwareVersionEnum.XBP24B);
		
		HardwareVersion hardwareVersion = HardwareVersion.get(0);
		
		assertEquals(HardwareVersionEnum.XBP24B.getValue(), hardwareVersion.getValue());
		assertEquals(HardwareVersionEnum.XBP24B.getDescription(), hardwareVersion.getDescription());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#get(int)}.
	 * 
	 * <p>Verify that an Unknown HardwareVersion is received when querying it using the static get() method 
	 * of the class and there is not a HardwareVersionEnum entry for the provided ID.</p>
	 */
	@Test
	public void testGetHardwareVersionUnknown() {
		// Prepare the HardwareVersionEnum class to return a null entry value when asked for one.
		PowerMockito.mockStatic(HardwareVersionEnum.class);
		PowerMockito.when(HardwareVersionEnum.get(Mockito.anyInt())).thenReturn(null);
		
		HardwareVersion hardwareVersion = HardwareVersion.get(0);
		
		assertEquals(0, hardwareVersion.getValue());
		assertEquals("Unknown", hardwareVersion.getDescription());
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#equals(Object)}.
	 * 
	 * <p>Test the equals method with a {@code null} value.</p>
	 */
	@Test
	public final void testEqualsWithNull() {
		// Setup the resources for the test.
		HardwareVersion version = HardwareVersion.get(HardwareVersionEnum.XBP24B.getValue());
		
		// Call the method under test.
		boolean areEqual = version.equals(null);
		
		// Verify the result.
		assertThat("Hardware version cannot be equal to null", areEqual, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#equals(Object)}.
	 * 
	 * <p>Test the equals method with a non {@code HardwareVersion} value.</p>
	 */
	@Test
	public final void testEqualsWithNonHardwareVersion() {
		// Setup the resources for the test.
		HardwareVersion version = HardwareVersion.get(HardwareVersionEnum.XBP24B.getValue());
		
		// Call the method under test.
		boolean areEqual = version.equals(new Object());
		
		// Verify the result.
		assertThat("Hardware version cannot be equal to an Object", areEqual, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.models.HardwareVersion(Object)}.
	 * 
	 * <p>Test the equals method with different {@code HardwareVersion}.</p>
	 */
	@Test
	public final void testEqualsWithDifferentHardwareVersion() {
		// Setup the resources for the test.
		HardwareVersion version1 = HardwareVersion.get(HardwareVersionEnum.XBP24B.getValue());
		HardwareVersion version2 = HardwareVersion.get(HardwareVersionEnum.X09_001.getValue());
		
		// Call the method under test.
		boolean areEqual1 = version1.equals(version2);
		boolean areEqual2 = version2.equals(version1);
		
		// Verify the result.
		assertThat("Hardware version1 must be different from Hardware version2", areEqual1, is(equalTo(false)));
		assertThat("Hardware version2 must be different from Hardware version1", areEqual2, is(equalTo(false)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#equals(Object)}.
	 * 
	 * <p>Test the equals method with equal {@code HardwareVersion}.</p>
	 */
	@Test
	public final void testEqualsIsSymetric() {
		// Setup the resources for the test.
		HardwareVersion version1 = HardwareVersion.get(HardwareVersionEnum.XBP24B.getValue());
		HardwareVersion version2 = HardwareVersion.get(HardwareVersionEnum.XBP24B.getValue());
		
		// Call the method under test.
		boolean areEqual1 = version1.equals(version2);
		boolean areEqual2 = version2.equals(version1);
		
		// Verify the result.
		assertThat("Hardware version1 must be equal to Hardware version2", areEqual1, is(equalTo(true)));
		assertThat("Hardware version2 must be equal to Hardware version1", areEqual2, is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#equals(Object)}.
	 */
	@Test
	public final void testEqualsIsReflexive() {
		// Setup the resources for the test.
		HardwareVersion version = HardwareVersion.get(HardwareVersionEnum.XBP24B.getValue());
		
		// Call the method under test.
		boolean areEqual = version.equals(version);
		
		// Verify the result.
		assertThat("Hardware version must be equal to itself", areEqual, is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#equals(Object)}.
	 */
	@Test
	public final void testEqualsIsTransitive() {
		// Setup the resources for the test.
		HardwareVersion version1 = HardwareVersion.get(HardwareVersionEnum.XBP24B.getValue());
		HardwareVersion version2 = HardwareVersion.get(HardwareVersionEnum.XBP24B.getValue());
		HardwareVersion version3 = HardwareVersion.get(HardwareVersionEnum.XBP24B.getValue());
		
		// Call the method under test.
		boolean areEqual1 = version1.equals(version2);
		boolean areEqual2 = version2.equals(version3);
		boolean areEqual3 = version1.equals(version3);
		
		// Verify the result.
		assertThat("Hardware version1 must be equal to Hardware version2", areEqual1, is(equalTo(true)));
		assertThat("Hardware version2 must be equal to Hardware version3", areEqual2, is(equalTo(true)));
		assertThat("Hardware version1 must be equal to Hardware version3", areEqual3, is(equalTo(true)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#equals(Object)}.
	 */
	@Test
	public final void testEqualsIsConsistent() {
		// Setup the resources for the test.
		HardwareVersion version1 = HardwareVersion.get(HardwareVersionEnum.XBP24B.getValue());
		HardwareVersion version2 = HardwareVersion.get(HardwareVersionEnum.XBP24B.getValue());
		HardwareVersion version3 = HardwareVersion.get(HardwareVersionEnum.X08_004.getValue());
		
		// Verify the result.
		assertThat("Consistent test fail version1,version2", version1.equals(version2), is(equalTo(true)));
		assertThat("Consistent test fail version1,version2", version1.equals(version2), is(equalTo(true)));
		assertThat("Consistent test fail version1,version2", version1.equals(version2), is(equalTo(true)));
		assertThat("Consistent test fail version3,version1", version3.equals(version1), is(equalTo(false)));
		assertThat("Consistent test fail version3,version1", version3.equals(version1), is(equalTo(false)));
		assertThat("Consistent test fail version3,version1", version3.equals(version1), is(equalTo(false)));

	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#hashCode()}.
	 */
	@Test
	public final void testHashCodeWithEqualHardwareVersions() {
		// Setup the resources for the test.
		HardwareVersion version1 = HardwareVersion.get(HardwareVersionEnum.XBP24B.getValue());
		HardwareVersion version2 = HardwareVersion.get(HardwareVersionEnum.XBP24B.getValue());
		
		// Call the method under test.
		int hashHV1 = version1.hashCode();
		int hashHV2 = version2.hashCode();
		
		// Verify the result.
		assertThat("Hardware version1 must be equal to Hardware version2", version1.equals(version2), is(equalTo(true)));
		assertThat("Hardware version2 must be equal to Hardware version1", version2.equals(version1), is(equalTo(true)));
		assertThat("Hash codes must be equal", hashHV1, is(equalTo(hashHV2)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#hashCode()}.
	 */
	@Test
	public final void testHashCodeWithDifferentHardwareVersions() {
		// Setup the resources for the test.
		HardwareVersion version1 = HardwareVersion.get(HardwareVersionEnum.XBP24B.getValue());
		HardwareVersion version2 = HardwareVersion.get(HardwareVersionEnum.X09_001.getValue());;
		
		// Call the method under test.
		int hashHV1 = version1.hashCode();
		int hashHV2 = version2.hashCode();
		
		// Verify the result.
		assertThat("Hardware version1 must be different from Hardware version2", version1.equals(version2), is(equalTo(false)));
		assertThat("Hardware version2 must be different from to Hardware version1", version2.equals(version1), is(equalTo(false)));
		assertThat("Hash codes must be different", hashHV1, is(not(equalTo(hashHV2))));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.models.HardwareVersion#hashCode()}.
	 */
	@Test
	public final void testHashCodeIsConsistent() {
		// Setup the resources for the test.
		HardwareVersion version = HardwareVersion.get(HardwareVersionEnum.XBP24B.getValue());
		
		int initialHashCode = version.hashCode();
		
		// Verify the result.
		assertThat("Consistent hashcode test fails", version.hashCode(), is(equalTo(initialHashCode)));
		assertThat("Consistent hashcode test fails", version.hashCode(), is(equalTo(initialHashCode)));
	}
}

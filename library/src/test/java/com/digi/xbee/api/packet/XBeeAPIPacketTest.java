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
package com.digi.xbee.api.packet;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import static org.junit.Assert.assertThat;

import java.util.LinkedHashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.digi.xbee.api.utils.HexUtils;

public class XBeeAPIPacketTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private static class TestXBeeAPIPacket extends XBeeAPIPacket {
		
		protected TestXBeeAPIPacket(APIFrameType frameType) {
			super(frameType);
		}
		
		protected TestXBeeAPIPacket(int frameTypeValue) {
			super(frameTypeValue);
		}

		@Override
		public boolean needsAPIFrameID() {
			return false;
		}
		
		@Override
		public boolean isBroadcast() {
			return false;
		}
		
		@Override
		protected LinkedHashMap<String, String> getAPIPacketParameters() {
			return null;
		}
		
		@Override
		protected byte[] getAPIPacketSpecificData() {
			return null;
		}
	}
	
	public XBeeAPIPacketTest() {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#XBeeAPIPacket(APIFrameType)}.
	 * 
	 * <p>Construct a new XBee API packet but with a {@code null} api frame 
	 * type. This must throw a {@code NullPointerException}.</p>
	 */
	@Test
	public final void testCreateXBeeAPIPacketAPIFrameTypeNull() {
		// Setup the resources for the test.
		APIFrameType frameType = null;
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(is(equalTo("Frame type cannot be null.")));
		
		// Call the method under test that should throw a NullPointerException.
		new TestXBeeAPIPacket(frameType);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#XBeeAPIPacket(APIFrameType)}.
	 * 
	 * <p>Construct a new XBee API packet with a valid api frame type.</p>
	 */
	@Test
	public final void testCreateXBeeAPIPacketValidAPIFrameType() {
		// Setup the resources for the test.
		APIFrameType frameType = APIFrameType.GENERIC;
		
		int expectedLength = 1 /* Frame type */;
		
		// Call the method under test.
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(frameType);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned frame type is not the expected one", packet.getFrameType(), is(equalTo(frameType)));
		assertThat("Returned frame type value is not the expected one", packet.getFrameTypeValue(), is(equalTo(frameType.getValue())));
		assertThat("Returned frame type value is not the expected one", packet.getFrameID(), is(equalTo(XBeeAPIPacket.NO_FRAME_ID)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#XBeeAPIPacket(int)}.
	 * 
	 * <p>Construct a new XBee API packet but with a api frame type bigger than 
	 * 255. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateXBeeAPIPacketFrameTypeBiggerThan255() {
		// Setup the resources for the test.
		int frameType = 6321;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame type value must be between 0 and 255.")));
		
		// Call the method under test that should throw a IllegalArgumentException.
		new TestXBeeAPIPacket(frameType);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#XBeeAPIPacket(int)}.
	 * 
	 * <p>Construct a new XBee API packet but with a negative api frame 
	 * type. This must throw an {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testCreateXBeeAPIPacketNegativeFrameType() {
		// Setup the resources for the test.
		int frameType = -5;
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame type value must be between 0 and 255.")));
		
		// Call the method under test that should throw a IllegalArgumentException.
		new TestXBeeAPIPacket(frameType);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#XBeeAPIPacket(int)}.
	 * 
	 * <p>Construct a new XBee API packet with a non-supported frame type.</p>
	 */
	@Test
	public final void testCreateXBeeAPIPacketNotSupportedFrameType() {
		// Setup the resources for the test.
		int frameTypeValue = 0xFD;
		
		int expectedLength = 1 /* Frame type */;
		
		// Call the method under test.
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(frameTypeValue);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned frame type is not the expected one", packet.getFrameType(), is(equalTo(APIFrameType.UNKNOWN)));
		assertThat("Returned frame type value is not the expected one", packet.getFrameTypeValue(), is(equalTo(frameTypeValue)));
		assertThat("Returned frame type value is not the expected one", packet.getFrameID(), is(equalTo(XBeeAPIPacket.NO_FRAME_ID)));
	}
	
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#XBeeAPIPacket(int)}.
	 * 
	 * <p>Construct a new XBee API packet with a valid api frame type.</p>
	 */
	@Test
	public final void testCreateXBeeAPIPacketValidAPIFrameTypeValue() {
		// Setup the resources for the test.
		APIFrameType frameType = APIFrameType.GENERIC;
		int frameTypeValue = frameType.getValue();
		
		int expectedLength = 1 /* Frame type */;
		
		// Call the method under test.
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(frameTypeValue);
		
		// Verify the result.
		assertThat("Returned length is not the expected one", packet.getPacketLength(), is(equalTo(expectedLength)));
		assertThat("Returned frame type is not the expected one", packet.getFrameType(), is(equalTo(frameType)));
		assertThat("Returned frame type value is not the expected one", packet.getFrameTypeValue(), is(equalTo(frameTypeValue)));
		assertThat("Returned frame type value is not the expected one", packet.getFrameID(), is(equalTo(XBeeAPIPacket.NO_FRAME_ID)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#getPacketData()}.
	 * 
	 * <p>Test the get packet data method with null API frame data.</p>
	 */
	@Test
	public final void testGetPacketDataGetAPIDataNull() {
		// Setup the resources for the test.
		APIFrameType frameType = APIFrameType.GENERIC;
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(frameType) {
			@Override
			public byte[] getAPIData() {
				return null;
			}
		};
		byte[] expectedData = new byte[]{(byte)frameType.getValue()};
		
		// Call the method under test.
		byte[] data = packet.getPacketData();
		
		// Verify the result.
		assertThat("Returned packet data is not the expected one", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#getPacketData()}.
	 * 
	 * <p>Test the get packet data method without frame data.</p>
	 */
	@Test
	public final void testGetPacketDataWithoutDataNoFrameID() {
		// Setup the resources for the test.
		APIFrameType frameType = APIFrameType.GENERIC;
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(frameType);
		byte[] expectedData = new byte[]{(byte)frameType.getValue()};
		
		// Call the method under test.
		byte[] data = packet.getPacketData();
		
		// Verify the result.
		assertThat("Returned packet data is not the expected one", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#getPacketData()}.
	 * 
	 * <p>Test the get packet data method without frame data.</p>
	 */
	@Test
	public final void testGetPacketDataWithoutDataWithFrameID() {
		// Setup the resources for the test.
		APIFrameType frameType = APIFrameType.GENERIC;
		int frameID = 5;
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(frameType) {
			@Override
			public boolean needsAPIFrameID() {
				return true;
			}
		};
		packet.setFrameID(frameID);
		
		byte[] expectedData = new byte[]{(byte)frameType.getValue(), (byte)frameID};
		
		// Call the method under test.
		byte[] data = packet.getPacketData();
		
		// Verify the result.
		assertThat("Returned packet data is not the expected one", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#getPacketData()}.
	 * 
	 * <p>Test the get packet data method without frame data.</p>
	 */
	@Test
	public final void testGetPacketDataWithoutDataWithFrameIDNotConfigured() {
		// Setup the resources for the test.
		APIFrameType frameType = APIFrameType.GENERIC;
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(frameType) {
			@Override
			public boolean needsAPIFrameID() {
				return true;
			}
		};
		
		byte[] expectedData = new byte[]{(byte)frameType.getValue(), (byte)XBeeAPIPacket.NO_FRAME_ID};
		
		// Call the method under test.
		byte[] data = packet.getPacketData();
		
		// Verify the result.
		assertThat("Returned packet data is not the expected one", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#getPacketData()}.
	 * 
	 * <p>Test the get packet data method with frame data.</p>
	 */
	@Test
	public final void testGetPacketDataWithDataNoFrameID() {
		// Setup the resources for the test.
		APIFrameType frameType = APIFrameType.GENERIC;
		final byte[] packetData = new byte[] {0x00, 0x01, 0x02};
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(frameType) {
			@Override
			public byte[] getAPIPacketSpecificData() {
				return packetData;
			}
		};
		byte[] expectedData = new byte[1 + packetData.length];
		expectedData[0] = (byte)frameType.getValue();
		System.arraycopy(packetData, 0, expectedData, 1, packetData.length);
		
		// Call the method under test.
		byte[] data = packet.getPacketData();
		
		// Verify the result.
		assertThat("Returned packet data is not the expected one", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#getPacketData()}.
	 * 
	 * <p>Test the get packet data method with frame data.</p>
	 */
	@Test
	public final void testGetPacketDataWithDataWithFrameID() {
		// Setup the resources for the test.
		APIFrameType frameType = APIFrameType.GENERIC;
		final byte[] packetData = new byte[] {0x00, 0x01, 0x02};
		int frameID = 5;
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(frameType) {
			@Override
			public byte[] getAPIPacketSpecificData() {
				return packetData;
			}
			
			@Override
			public boolean needsAPIFrameID() {
				return true;
			}
		};
		packet.setFrameID(frameID);
		
		byte[] expectedData = new byte[2 + packetData.length];
		expectedData[0] = (byte)frameType.getValue();
		expectedData[1] = (byte)frameID;
		System.arraycopy(packetData, 0, expectedData, 2, packetData.length);
		
		// Call the method under test.
		byte[] data = packet.getPacketData();
		
		// Verify the result.
		assertThat("Returned packet data is not the expected one", data, is(equalTo(expectedData)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#setFrameID(int)}.
	 * 
	 * <p>Test the set frame ID with ID bigger than 255. This must throw an 
	 * {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testSetFrameIDBiggerThan255() {
		// Setup the resources for the test.
		int frameID = 300;
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(APIFrameType.GENERIC);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw a IllegalArgumentException.
		packet.setFrameID(frameID);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#setFrameID(int)}.
	 * 
	 * <p>Test the set frame ID with ID negative. This must throw an 
	 * {@code IllegalArgumentException}.</p>
	 */
	@Test
	public final void testSetFrameIDNegative() {
		// Setup the resources for the test.
		int frameID = -1;
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(APIFrameType.GENERIC);
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(is(equalTo("Frame ID must be between 0 and 255.")));
		
		// Call the method under test that should throw a IllegalArgumentException.
		packet.setFrameID(frameID);
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#setFrameID(int)}.
	 * 
	 * <p>Test the set frame ID with valid ID, when it is not needed.</p>
	 */
	@Test
	public final void testSetFrameIDValidAndNotNeeded() {
		// Setup the resources for the test.
		int frameID = 52;
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(APIFrameType.GENERIC);
		
		// Call the method under test.
		packet.setFrameID(frameID);
		
		// Verify the result.
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(XBeeAPIPacket.NO_FRAME_ID)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#setFrameID(int)}.
	 * 
	 * <p>Test the set frame ID with valid ID, when it is needed.</p>
	 */
	@Test
	public final void testSetFrameIDValidAndNeeded() {
		// Setup the resources for the test.
		int frameID = 52;
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(APIFrameType.GENERIC) {
			@Override
			public boolean needsAPIFrameID() {
				return true;
			}
		};
		
		// Call the method under test.
		packet.setFrameID(frameID);
		
		// Verify the result.
		assertThat("Returned frame ID is not the expected one", packet.getFrameID(), is(equalTo(frameID)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#checkFrameID(int)}.
	 * 
	 * <p>Test the check frame ID when it is not needed.</p>
	 */
	@Test
	public final void testCheckFrameIDWhenNoNeededAndEqual() {
		// Setup the resources for the test.
		int frameID = 52;
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(APIFrameType.GENERIC);
		packet.setFrameID(frameID);
				
		// Call the method under test.
		boolean equal = packet.checkFrameID(frameID);
		
		// Verify the result.
		assertThat(false, is(equalTo(equal)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#checkFrameID(int)}.
	 * 
	 * <p>Test the check frame ID when it is not needed.</p>
	 */
	@Test
	public final void testCheckFrameIDWhenNoNeededAndNoEqual() {
		// Setup the resources for the test.
		int frameID = 52;
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(APIFrameType.GENERIC);
		packet.setFrameID(frameID);
				
		// Call the method under test.
		boolean equal = packet.checkFrameID(79);
		
		// Verify the result.
		assertThat(false, is(equalTo(equal)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#checkFrameID(int)}.
	 * 
	 * <p>Test the check frame ID when it is needed.</p>
	 */
	@Test
	public final void testCheckFrameIDWhenNeededAndEqual() {
		// Setup the resources for the test.
		int frameID = 52;
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(APIFrameType.GENERIC) {
			@Override
			public boolean needsAPIFrameID() {
				return true;
			}
		};
		packet.setFrameID(frameID);
				
		// Call the method under test.
		boolean equal = packet.checkFrameID(frameID);
		
		// Verify the result.
		assertThat(true, is(equalTo(equal)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#checkFrameID(int)}.
	 * 
	 * <p>Test the check frame ID when it is needed.</p>
	 */
	@Test
	public final void testCheckFrameIDWhenNeededAndNotEqual() {
		// Setup the resources for the test.
		int frameID = 52;
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(APIFrameType.GENERIC) {
			@Override
			public boolean needsAPIFrameID() {
				return true;
			}
		};
		packet.setFrameID(frameID);
				
		// Call the method under test.
		boolean equal = packet.checkFrameID(79);
		
		// Verify the result.
		assertThat(false, is(equalTo(equal)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#getPacketParameters()}.
	 * 
	 * <p>Test the get Packet parameters without extra data.</p>
	 */
	@Test
	public final void testGetPacketParametersFrameTypeNotSupported() {
		// Setup the resources for the test.
		int frameType = 153; //0x99;
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(frameType);
		
		String expectedFrameType = HexUtils.integerToHexString(frameType, 1) + " (" + APIFrameType.UNKNOWN.getName() + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(1)));
		assertThat("Frame type is not the expected one", packetParams.get("Frame type"), is(equalTo(expectedFrameType)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#getPacketParameters()}.
	 * 
	 * <p>Test the get Packet parameters without extra data.</p>
	 */
	@Test
	public final void testGetPacketParametersFrameTypeNull() {
		// Setup the resources for the test.
		int frameType = 153; //0x99;
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(frameType){
			@Override
			public APIFrameType getFrameType() {
				return null;
			}
		};
		
		String expectedFrameType = HexUtils.integerToHexString(frameType, 1);
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(1)));
		assertThat("Frame type is not the expected one", packetParams.get("Frame type"), is(equalTo(expectedFrameType)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#getPacketParameters()}.
	 * 
	 * <p>Test the get Packet parameters without extra data.</p>
	 */
	@Test
	public final void testGetPacketParametersNoFrameIDWithoutData() {
		// Setup the resources for the test.
		APIFrameType frameType = APIFrameType.GENERIC;
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(frameType);
		
		String expectedFrameType = HexUtils.integerToHexString(frameType.getValue(), 1) + " (" + frameType.getName() + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(1)));
		assertThat("Frame type is not the expected one", packetParams.get("Frame type"), is(equalTo(expectedFrameType)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#getPacketParameters()}.
	 * 
	 * <p>Test the get Packet parameters without extra data.</p>
	 */
	@Test
	public final void testGetPacketParametersWithFrameIDWithoutData() {
		// Setup the resources for the test.
		APIFrameType frameType = APIFrameType.GENERIC;
		int frameID = 5;
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(APIFrameType.GENERIC) {
			@Override
			public boolean needsAPIFrameID() {
				return true;
			}
		};
		packet.setFrameID(frameID);
		
		String expectedFrameType = HexUtils.integerToHexString(frameType.getValue(), 1) + " (" + frameType.getName() + ")";
		String expectedFrameID = HexUtils.prettyHexString(HexUtils.integerToHexString(frameID, 1)) + " (" + frameID + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(2)));
		assertThat("Frame type is not the expected one", packetParams.get("Frame type"), is(equalTo(expectedFrameType)));
		assertThat("Frame ID is not the expected one", packetParams.get("Frame ID"), is(equalTo(expectedFrameID)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#getPacketParameters()}.
	 * 
	 * <p>Test the get Packet parameters without extra data.</p>
	 */
	@Test
	public final void testGetPacketParametersNoSetFrameIDWithoutData() {
		// Setup the resources for the test.
		APIFrameType frameType = APIFrameType.GENERIC;
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(APIFrameType.GENERIC) {
			@Override
			public boolean needsAPIFrameID() {
				return true;
			}
		};
		
		String expectedFrameType = HexUtils.integerToHexString(frameType.getValue(), 1) + " (" + frameType.getName() + ")";
		String expectedFrameID = "(NO FRAME ID)";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(2)));
		assertThat("Frame type is not the expected one", packetParams.get("Frame type"), is(equalTo(expectedFrameType)));
		assertThat("Frame ID is not the expected one", packetParams.get("Frame ID"), is(equalTo(expectedFrameID)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#getPacketParameters()}.
	 * 
	 * <p>Test the get Packet parameters with extra data.</p>
	 */
	@Test
	public final void testGetPacketParametersNoFrameIDWithData() {
		// Setup the resources for the test.
		APIFrameType frameType = APIFrameType.GENERIC;
		final String expectedField = HexUtils.prettyHexString(HexUtils.byteArrayToHexString(new byte[]{0x00, 0x01, 0x02, 0x03}));
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(APIFrameType.GENERIC) {
			@Override
			protected LinkedHashMap<String, String> getAPIPacketParameters() {
				LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
				parameters.put("My field", expectedField);
				return parameters;
			}
		};
		
		String expectedFrameType = HexUtils.integerToHexString(frameType.getValue(), 1) + " (" + frameType.getName() + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(2)));
		assertThat("Frame type is not the expected one", packetParams.get("Frame type"), is(equalTo(expectedFrameType)));
		assertThat("Custom field is not the expected one", packetParams.get("My field"), is(equalTo(expectedField)));
	}
	
	/**
	 * Test method for {@link com.digi.xbee.api.packet.XBeeAPIPacket#getPacketParameters()}.
	 * 
	 * <p>Test the get Packet parameters with extra data.</p>
	 */
	@Test
	public final void testGetPacketParametersWithFrameIDWithData() {
		// Setup the resources for the test.
		APIFrameType frameType = APIFrameType.GENERIC;
		int frameID = 5;
		final String expectedField = HexUtils.prettyHexString(HexUtils.byteArrayToHexString(new byte[]{0x00, 0x01, 0x02, 0x03}));
		TestXBeeAPIPacket packet = new TestXBeeAPIPacket(APIFrameType.GENERIC) {
			@Override
			protected LinkedHashMap<String, String> getAPIPacketParameters() {
				LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
				parameters.put("My field", expectedField);
				return parameters;
			}
			@Override
			public boolean needsAPIFrameID() {
				return true;
			}
		};
		packet.setFrameID(frameID);
		
		String expectedFrameType = HexUtils.integerToHexString(frameType.getValue(), 1) + " (" + frameType.getName() + ")";
		String expectedFrameID = HexUtils.prettyHexString(HexUtils.integerToHexString(frameID, 1)) + " (" + frameID + ")";
		
		// Call the method under test.
		LinkedHashMap<String, String> packetParams = packet.getPacketParameters();
		
		// Verify the result.
		assertThat("Packet parameters map size is not the expected one", packetParams.size(), is(equalTo(3)));
		assertThat("Frame type is not the expected one", packetParams.get("Frame type"), is(equalTo(expectedFrameType)));
		assertThat("Frame ID is not the expected one", packetParams.get("Frame ID"), is(equalTo(expectedFrameID)));
		assertThat("Custom field is not the expected one", packetParams.get("My field"), is(equalTo(expectedField)));
	}
}

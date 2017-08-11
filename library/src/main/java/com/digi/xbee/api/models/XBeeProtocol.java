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

import java.util.HashMap;

/**
 * Enumerates the available XBee protocols. The XBee protocol is determined 
 * by the combination of hardware and firmware of an XBee device.
 */
public enum XBeeProtocol {
	
	// Enumeration entries
	ZIGBEE(0, "ZigBee"),
	RAW_802_15_4(1, "802.15.4"),
	XBEE_WIFI(2, "Wi-Fi"),
	DIGI_MESH(3, "DigiMesh"),
	XCITE(4, "XCite"),
	XTEND(5, "XTend (Legacy)"),
	XTEND_DM(6, "XTend (DigiMesh)"),
	SMART_ENERGY(7, "Smart Energy"),
	DIGI_POINT(8, "Point-to-multipoint"),
	ZNET(9, "ZNet 2.5"),
	XC(10, "XSC"),
	XLR(11, "XLR"),
	XLR_DM(12, "XLR"), // TODO [XLR_DM] XLR device with DigiMesh support.
	SX(13, "XBee SX"),
	XLR_MODULE(14, "XLR Module"),
	/** @since 1.2.0 */
	CELLULAR(15, "Cellular"),
	/** @since 1.2.1 */
	CELLULAR_NBIOT(16, "Cellular NB-IoT"),
	/** @since 1.2.1 */
	THREAD(17, "Thread"),
	UNKNOWN(99, "Unknown");
	
	// Variables
	private static final HashMap<Integer, XBeeProtocol> lookupTable = new HashMap<Integer, XBeeProtocol>();
	
	private final int id;
	
	private final String description;
	
	static {
		for (XBeeProtocol xbeeProtocol:values())
			lookupTable.put(xbeeProtocol.getID(), xbeeProtocol);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code XBeeProtocol} enumeration 
	 * entry with the given parameters.
	 * 
	 * @param id XBee protocol ID.
	 * @param description XBee protocol description.
	 */
	private XBeeProtocol(int id, String description) {
		this.id = id;
		this.description = description;
	}
	
	/**
	 * Returns the XBee protocol ID.
	 * 
	 * @return XBee protocol ID.
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Returns the XBee protocol description.
	 * 
	 * @return XBee protocol description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the {@code XBeeProtocol} associated to the given ID.
	 * 
	 * @param id The ID of the {@code XBeeProtocol} to retrieve.
	 * 
	 * @return The {@code XBeeProtocol} associated to the given ID.
	 */
	public static XBeeProtocol get(int id) {
		if (!lookupTable.containsKey(id))
			return UNKNOWN;
		return lookupTable.get(id);
	}
	
	/**
	 * Determines the XBee protocol based on the given Hardware and firmware 
	 * versions.
	 * 
	 * @param hardwareVersion The hardware version of the protocol to 
	 *                        determine.
	 * @param firmwareVersion The firmware version of the protocol to 
	 *                        determine.
	 * 
	 * @return The XBee protocol corresponding to the given hardware and 
	 *         firmware versions.
	 * 
	 * @see HardwareVersion
	 */
	@SuppressWarnings("deprecation")
	public static XBeeProtocol determineProtocol(HardwareVersion hardwareVersion, String firmwareVersion) {
		if (hardwareVersion == null || firmwareVersion == null || hardwareVersion.getValue() < 0x09 
				|| HardwareVersionEnum.get(hardwareVersion.getValue()) == null)
			return UNKNOWN;
		switch (HardwareVersionEnum.get(hardwareVersion.getValue())) {
		case XC09_009:
		case XC09_038:
			return XCITE;
		case XT09_XXX:
		case XT09B_XXX:
			if ((firmwareVersion.length() == 4 && firmwareVersion.startsWith("8"))
					|| (firmwareVersion.length() == 5 && firmwareVersion.charAt(1) == '8'))
				return XTEND_DM;
			return XTEND;
		case XB24_AXX_XX:
		case XBP24_AXX_XX:
			if ((firmwareVersion.length() == 4 && firmwareVersion.startsWith("8")))
				return DIGI_MESH;
			return RAW_802_15_4;
		case XB24_BXIX_XXX:
		case XBP24_BXIX_XXX:
			if ((firmwareVersion.length() == 4 && firmwareVersion.startsWith("1") && firmwareVersion.endsWith("20")) 
					|| (firmwareVersion.length() == 4 && firmwareVersion.startsWith("2")))
				return ZIGBEE;
			else if (firmwareVersion.length() == 4 && firmwareVersion.startsWith("3"))
				return SMART_ENERGY;
			return ZNET;
		case XBP09_DXIX_XXX:
			if ((firmwareVersion.length() == 4 && firmwareVersion.startsWith("8") 
					|| (firmwareVersion.length() == 4 && firmwareVersion.charAt(1) == '8'))
					|| (firmwareVersion.length() == 5 && firmwareVersion.charAt(1) == '8'))
				return DIGI_MESH;
			return DIGI_POINT;
		case XBP09_XCXX_XXX:
			return XC;
		case XBP08_DXXX_XXX:
			return DIGI_POINT;
		case XBP24B:
			if (firmwareVersion.length() == 4 && firmwareVersion.startsWith("3"))
				return SMART_ENERGY;
			return ZIGBEE;
		case XB24_WF:
		case WIFI_ATHEROS:
		case SMT_WIFI_ATHEROS:
			return XBEE_WIFI;
		case XBP24C:
		case XB24C:
			if (firmwareVersion.length() == 4 && (firmwareVersion.startsWith("5") || firmwareVersion.startsWith("6")))
				return SMART_ENERGY;
			else if (firmwareVersion.startsWith("2"))
				return RAW_802_15_4;
			else if (firmwareVersion.startsWith("9"))
				return DIGI_MESH;
			return ZIGBEE;
		case XSC_GEN3:
		case SRD_868_GEN3:
			if (firmwareVersion.length() == 4 && firmwareVersion.startsWith("8"))
				return DIGI_MESH;
			else if (firmwareVersion.length() == 4 && firmwareVersion.startsWith("1"))
				return DIGI_POINT;
			return XC;
		case XBEE_CELL_TH:
			return UNKNOWN;
		case XLR_MODULE:
			// This is for the old version of the XLR we have (K60), and it is 
			// reporting the firmware of the module (8001), this will change in 
			// future (after K64 integration) reporting the hardware and firmware
			// version of the baseboard (see the case HardwareVersionEnum.XLR_BASEBOARD).
			// TODO maybe this should be removed in future, since this case will never be released.
			if (firmwareVersion.startsWith("1"))
				return XLR;
			else
				return XLR_MODULE;
		case XLR_BASEBOARD:
			// XLR devices with K64 will report the baseboard hardware version, 
			// and also firmware version (the one we have here is 1002, but this value
			// is not being reported since is an old K60 version, the module fw version
			// is reported instead).
			
			// TODO [XLR_DM] The next version of the XLR will add DigiMesh support should be added.
			// Probably this XLR_DM and XLR will depend on the firmware version.
			
			if (firmwareVersion.startsWith("1"))
				return XLR;
			else
				return XLR_MODULE;
		case XB900HP_NZ:
			return DIGI_POINT;
		case XBP24C_TH_DIP:
		case XB24C_TH_DIP:
		case XBP24C_S2C_SMT:
			if (firmwareVersion.length() == 4 && (firmwareVersion.startsWith("5") || firmwareVersion.startsWith("6")))
				return SMART_ENERGY;
			else if (firmwareVersion.startsWith("2"))
				return RAW_802_15_4;
			else if (firmwareVersion.startsWith("9"))
				return DIGI_MESH;
			return ZIGBEE;
		case SX_PRO:
		case SX:
		case XTR:
			if (firmwareVersion.startsWith("2"))
				return XTEND;
			else if (firmwareVersion.startsWith("8"))
				return XTEND_DM;
			else
				return SX;
		case S2D_SMT_PRO:
		case S2D_SMT_REG:
		case S2D_TH_PRO:
		case S2D_TH_REG:
			if (firmwareVersion.startsWith("8"))
				return THREAD;
			else
				return ZIGBEE;
		case CELLULAR:
		case CELLULAR_CAT1_LTE_VERIZON:
		case CELLULAR_3G:
		case CELLULAR_LTE_VERIZON:
		case CELLULAR_LTE_ATT:
			return CELLULAR;
		case CELLULAR_NBIOT_EUROPE:
			return CELLULAR_NBIOT;
		default:
			return ZIGBEE;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return description;
	}
}

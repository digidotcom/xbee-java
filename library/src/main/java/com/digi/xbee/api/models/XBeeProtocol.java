/**
 * Copyright (c) 2014-2015 Digi International Inc.,
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
	public static XBeeProtocol determineProtocol(HardwareVersion hardwareVersion, String firmwareVersion) {
		if (hardwareVersion == null || firmwareVersion == null || hardwareVersion.getValue() < 0x09)
			return UNKNOWN;
		else if (hardwareVersion.getValue() == HardwareVersionEnum.XC09_009.getValue() 
				|| hardwareVersion.getValue() == HardwareVersionEnum.XC09_038.getValue())
			return XCITE;
		else if (hardwareVersion.getValue() == HardwareVersionEnum.XT09_XXX.getValue()) {
			if ((firmwareVersion.length() == 4 && firmwareVersion.startsWith("8"))
					|| (firmwareVersion.length() == 5 && firmwareVersion.charAt(1) == '8'))
				return XTEND_DM;
			return XTEND;
		}
		else if (hardwareVersion.getValue() == HardwareVersionEnum.XB24_AXX_XX.getValue() 
				|| hardwareVersion.getValue() == HardwareVersionEnum.XBP24_AXX_XX.getValue()) {
			if ((firmwareVersion.length() == 4 && firmwareVersion.startsWith("8")))
					return DIGI_MESH;
			return RAW_802_15_4;
		}
		else if (hardwareVersion.getValue() == HardwareVersionEnum.XB24_BXIX_XXX.getValue() 
				|| hardwareVersion.getValue() == HardwareVersionEnum.XBP24_BXIX_XXX.getValue()) {
			if ((firmwareVersion.length() == 4 && firmwareVersion.startsWith("1") && firmwareVersion.endsWith("20")) 
					|| (firmwareVersion.length() == 4 && firmwareVersion.startsWith("2")))
				return ZIGBEE;
			else if (firmwareVersion.length() == 4 && firmwareVersion.startsWith("3"))
				return SMART_ENERGY;
			return ZNET;
		}
		else if (hardwareVersion.getValue() == HardwareVersionEnum.XBP09_DXIX_XXX.getValue()) {
			if ((firmwareVersion.length() == 4 && firmwareVersion.startsWith("8") 
					|| (firmwareVersion.length() == 4 && firmwareVersion.charAt(1) == '8'))
					|| (firmwareVersion.length() == 5 && firmwareVersion.charAt(1) == '8'))
				return DIGI_MESH;
			return DIGI_POINT;
		}
		else if (hardwareVersion.getValue() == HardwareVersionEnum.XBP09_XCXX_XXX.getValue())
			return XC;
		else if (hardwareVersion.getValue() == HardwareVersionEnum.XBP08_DXXX_XXX.getValue())
			return DIGI_POINT;
		else if (hardwareVersion.getValue() == HardwareVersionEnum.XBP24B.getValue()) {
			if (firmwareVersion.length() == 4 && firmwareVersion.startsWith("3"))
				return SMART_ENERGY;
			return ZIGBEE;
		}
		else if (hardwareVersion.getValue() == HardwareVersionEnum.XB24_WF.getValue() 
					|| hardwareVersion.getValue() == HardwareVersionEnum.WIFI_ATHEROS.getValue() 
					|| hardwareVersion.getValue() == HardwareVersionEnum.SMT_WIFI_ATHEROS.getValue())
			return XBEE_WIFI;
		else if (hardwareVersion.getValue() == HardwareVersionEnum.XBP24C.getValue() 
					|| hardwareVersion.getValue() == HardwareVersionEnum.XB24C.getValue()) {
			if (firmwareVersion.length() == 4 && firmwareVersion.startsWith("5"))
				return SMART_ENERGY;
			return ZIGBEE;
		}
		else if (hardwareVersion.getValue() == HardwareVersionEnum.XSC_GEN3.getValue() 
					|| hardwareVersion.getValue() == HardwareVersionEnum.SRD_868_GEN3.getValue()) {
			if (firmwareVersion.length() == 4 && firmwareVersion.startsWith("8"))
				return DIGI_MESH;
			else if (firmwareVersion.length() == 4 && firmwareVersion.startsWith("1"))
				return DIGI_POINT;
			return XC;
		}
		else if (hardwareVersion.getValue() == HardwareVersionEnum.XBEE_CELL_TH.getValue()) {
			return UNKNOWN;
		}
		else if (hardwareVersion.getValue() == HardwareVersionEnum.XLR_MODULE.getValue()) {
			// This is for the old version of the XLR we have (K60), and it is 
			// reporting the firmware of the module (8001), this will change in 
			// future (after K64 integration) reporting the hardware and firmware
			// version of the baseboard (see the case HardwareVersionEnum.XLR_BASEBOARD).
			// TODO maybe this should be removed in future, since this case will never be released.
			return XLR;
		}
		else if (hardwareVersion.getValue() == HardwareVersionEnum.XLR_BASEBOARD.getValue()) {
			// XLR devices with K64 will report the baseboard hardware version, 
			// and also firmware version (the one we have here is 1002, but this value
			// is not being reported since is an old K60 version, the module fw version
			// is reported instead).
			
			// TODO [XLR_DM] The next version of the XLR will add DigiMesh support should be added.
			// Probably this XLR_DM and XLR will depend on the firmware version.
			
			return XLR;
		}
		else if (hardwareVersion.getValue() == HardwareVersionEnum.XB900HP_NZ.getValue()) {
			return DIGI_POINT;
		}
		else if (hardwareVersion.getValue() == HardwareVersionEnum.XBP24C_TH_DIP.getValue()
				|| hardwareVersion.getValue() == HardwareVersionEnum.XB24C_TH_DIP.getValue()
				|| hardwareVersion.getValue() == HardwareVersionEnum.XBP24C_S2C_SMT.getValue()) {
			if (firmwareVersion.length() == 4 && firmwareVersion.startsWith("5"))
				return SMART_ENERGY;
			return ZIGBEE;
		}
		// If the hardware is not in the list, lets return Unknown.
		else if (HardwareVersionEnum.get(hardwareVersion.getValue()) == null) {
			return UNKNOWN;
		}
		
		// TODO: Logic protocol goes here.
		return ZIGBEE;
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

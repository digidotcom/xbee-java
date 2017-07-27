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
 * Enumerates the different hardware versions of the XBee devices.
 */
public enum HardwareVersionEnum {

	// Enumeration entries
	X09_009(0x01, "X09-009"),
	X09_019(0x02, "X09-019"),
	XH9_009(0x03, "XH9-009"),
	XH9_019(0x04, "XH9-019"),
	X24_009(0x05, "X24-009"),
	X24_019(0x06, "X24-019"),
	X09_001(0x07, "X09-001"),
	XH9_001(0x08, "XH9-001"),
	X08_004(0x09, "X08-004"),
	XC09_009(0x0A, "XC09-009"),
	XC09_038(0x0B, "XC09-038"),
	X24_038(0x0C, "X24-038"),
	X09_009_TX(0x0D, "X09-009-TX"),
	X09_019_TX(0x0E, "X09-019-TX"),
	XH9_009_TX(0x0F, "XH9-009-TX"),
	XH9_019_TX(0x10, "XH9-019-TX"),
	X09_001_TX(0x11, "X09-001-TX"),
	XH9_001_TX(0x12, "XH9-001-TX"),
	XT09B_XXX(0x13, "XT09B-xxx (Attenuator version)"),
	XT09_XXX(0x14, "XT09-xxx"),
	XC08_009(0x15, "XC08-009"),
	XC08_038(0x16, "XC08-038"),
	XB24_AXX_XX(0x17, "XB24-Axx-xx"),
	XBP24_AXX_XX(0x18, "XBP24-Axx-xx"),
	XB24_BXIX_XXX(0x19, "XB24-BxIx-xxx and XB24-Z7xx-xxx"),
	XBP24_BXIX_XXX(0x1A, "XBP24-BxIx-xxx and XBP24-Z7xx-xxx"),
	XBP09_DXIX_XXX(0x1B, "XBP09-DxIx-xxx Digi Mesh"),
	XBP09_XCXX_XXX(0x1C, "XBP09-XCxx-xxx: S3 XSC Compatibility"),
	XBP08_DXXX_XXX(0x1D, "XBP08-Dxx-xxx 868MHz"),
	XBP24B(0x1E, "XBP24B: Low cost ZB PRO and PLUS S2B"),
	XB24_WF(0x1F, "XB24-WF: XBee 802.11 (Redpine module)"),
	AMBER_MBUS(0x20, "??????: M-Bus module made by Amber"),
	XBP24C(0x21, "XBP24C: XBee PRO SMT Ember 357 S2C PRO"),
	XB24C(0x22, "XB24C: XBee SMT Ember 357 S2C"),
	XSC_GEN3(0x23, "XSC_GEN3: XBP9 XSC 24 dBm"),
	SRD_868_GEN3(0x24, "SDR_868_GEN3: XB8 12 dBm"),
	ABANDONATED(0x25, "Abandonated"),
	SMT_900LP(0x26, "900LP (SMT): 900LP on 'S8 HW'"),
	WIFI_ATHEROS(0x27, "WiFi Atheros (TH-DIP) XB2S-WF"),
	SMT_WIFI_ATHEROS(0x28, "WiFi Atheros (SMT) XB2B-WF"),
	SMT_475LP(0x29, "475LP (SMT): Beta 475MHz"),
	XBEE_CELL_TH(0x2A, "XBee-Cell (TH): XBee Cellular"),
	XLR_MODULE(0x2B, "XLR Module"),
	XB900HP_NZ(0x2C, "XB900HP (New Zealand): XB9 NZ HW/SW"),
	XBP24C_TH_DIP(0x2D, "XBP24C (TH-DIP): XBee PRO DIP"),
	XB24C_TH_DIP(0x2E, "XB24C (TH-DIP): XBee DIP"),
	XLR_BASEBOARD(0x2F, "XLR Baseboard"),
	XBP24C_S2C_SMT(0x30, "XBee PRO SMT"),
	SX_PRO(0x31, "SX Pro"),
	S2D_SMT_PRO(0x32, "XBP24D: S2D SMT PRO"),
	S2D_SMT_REG(0x33, "XB24D: S2D SMT Reg"),
	S2D_TH_PRO(0x34, "XBP24D: S2D TH PRO"),
	S2D_TH_REG(0x35, "XB24D: S2D TH Reg"),
	SX(0x3E, "SX"),
	XTR(0x3F, "XTR"),
	/** 
	 * @deprecated Use {@link #CELLULAR_CAT1_LTE_VERIZON} instead.
	 * @since 1.2.0 */
	CELLULAR(0x40, "CELLULAR"),
	/** @since 1.2.1 */
	CELLULAR_CAT1_LTE_VERIZON(0x40, "XBee Cellular Cat 1 LTE Verizon"),
	/** @since 1.2.1 */
	CELLULAR_3G(0x44, "XBee Cellular 3G"),
	/** @since 1.2.1 */
	CELLULAR_LTE_VERIZON(0x46, "XBee Cellular LTE-M Verizon"),
	/** @since 1.2.1 */
	CELLULAR_LTE_ATT(0x47, "XBee Cellular LTE-M AT&T"),
	/** @since 1.2.1 */
	CELLULAR_NBIOT_EUROPE(0x48, "XBee Cellular NBIoT Europe");
	
	// Variables
	private final int value;
	
	private final String description;
	
	private final static HashMap<Integer, HardwareVersionEnum> lookupTable = new HashMap<Integer, HardwareVersionEnum>();
	
	static {
		for (HardwareVersionEnum hv:values())
			lookupTable.put(hv.getValue(), hv);
	}
	
	/**
	 * Class constructor. Instantiates a new {@code HardwareVersion} 
	 * enumeration entry with the given parameters.
	 * 
	 * @param value Hardware version numeric value 
	 * @param description Hardware version description.
	 */
	private HardwareVersionEnum(int value, String description) {
		this.value = value;
		this.description = description;
	}
	
	/**
	 * Returns the Hardware version numeric value.
	 * 
	 * @return The hardware version numeric value.
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Returns the hardware version description.
	 * 
	 * @return The hardware version description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the {@code HardwareVersionEnum} associated to the given 
	 * numeric value.
	 * 
	 * @param value Numeric value of the {@code HardwareVersionEnum} to 
	 *              retrieve.
	 * 
	 * @return The {@code HardwareVersionEnum} associated to the given numeric 
	 *         value, {@code null} if there is not any 
	 *         {@code HardwareVersionEnum} associated to that value.
	 */
	public static HardwareVersionEnum get(int value) {
		return lookupTable.get(value);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return String.format("0x%02X: %s", value, description);
	}
}

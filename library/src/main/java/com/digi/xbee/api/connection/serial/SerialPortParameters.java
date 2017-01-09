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
package com.digi.xbee.api.connection.serial;

/**
 * Helper class used to store serial connection parameters information.
 * 
 * <p>Parameters are stored as public variables so that they can be accessed
 * and read from any class.</p>
 */
public final class SerialPortParameters {
	
	// Constants.
	private static final int HASH_SEED = 23;
	
	// Variables.
	public final int baudrate;
	public final int dataBits;
	public final int stopBits;
	public final int parity;
	public final int flowControl;
	
	/**
	 * Class constructor. Instances a new {@code SerialPortParameters} object
	 * with the given parameters.
	 * 
	 * @param baudrate Serial connection baud rate,
	 * @param dataBits Serial connection data bits.
	 * @param stopBits Serial connection stop bits.
	 * @param parity Serial connection parity.
	 * @param flowControl Serial connection flow control.
	 * 
	 * @throws IllegalArgumentException if {@code baudrate < 0} or
	 *                                  if {@code dataBits < 0} or
	 *                                  if {@code stopBits < 0} or
	 *                                  if {@code parity < 0} or
	 *                                  if {@code flowControl < 0}.
	 */
	public SerialPortParameters(int baudrate, int dataBits, int stopBits, int parity, int flowControl) {
		if (baudrate < 0)
			throw new IllegalArgumentException("Baudrate cannot be less than 0.");
		if (dataBits < 0)
			throw new IllegalArgumentException("Number of data bits cannot be less than 0.");
		if (stopBits < 0)
			throw new IllegalArgumentException("Number of stop bits cannot be less than 0.");
		if (parity < 0)
			throw new IllegalArgumentException("Illegal parity value.");
		if (flowControl < 0)
			throw new IllegalArgumentException("Illegal flow control value.");
		
		this.baudrate = baudrate;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		this.parity = parity;
		this.flowControl = flowControl;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SerialPortParameters)
			return ((SerialPortParameters)obj).baudrate == baudrate 
				&& ((SerialPortParameters)obj).dataBits == dataBits 
				&& ((SerialPortParameters)obj).stopBits == stopBits
				&& ((SerialPortParameters)obj).parity == parity
				&& ((SerialPortParameters)obj).flowControl == flowControl;
		else
			return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = HASH_SEED;
		hash = hash * (hash + baudrate);
		hash = hash * (hash + dataBits);
		hash = hash * (hash + stopBits);
		hash = hash * (hash + parity);
		hash = hash * (hash + flowControl);
		return hash;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Baud Rate: "+ baudrate + ", Data Bits: " + dataBits 
				+ ", Stop Bits: " + stopBits + ", Parity: " + parity 
				+ ", Flow Control: " + flowControl;
	}
}

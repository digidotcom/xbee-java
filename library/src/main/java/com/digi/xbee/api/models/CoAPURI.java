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

/**
 * Class containing the different constants that can be used as Uniform Resource 
 * Identifier (URI) when working with CoAP transmissions.
 * 
 * @since 1.2.1
 */
public class CoAPURI {

	/**
	 * URI for data transmissions (PUT). 
	 */
	public static final String URI_DATA_TRANSMISSION = "XB/TX";

	/**
	 * URI for AT Command operation (PUT or GET).
	 */
	public static final String URI_AT_COMMAND = "XB/AT";

	/**
	 * URI for IO operation (POST).
	 */
	public static final String URI_IO_SAMPLING = "XB/IO";
}
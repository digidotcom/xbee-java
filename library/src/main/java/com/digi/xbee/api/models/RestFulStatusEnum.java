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
 * Enumerates the different RestFul statuses.
 * 
 * @since 1.2.1
 */
public enum RestFulStatusEnum {

	// Enumeration types.
	SUCCESS(0x0200, "Success"),
	CREATED(0x0201, "Success: Created"),
	ACCEPTED(0x0202, "Success: Accepted"),
	NON_AUTHORITATIVE(0x0203, "Success: Non-Authoritative Information"),
	NO_CONTENT(0x0204, "Success: No Content"),
	RESET_CONTENT(0x0205, "Success: Reset Content"),
	CLIENT_ERROR_BAD_REQUEST(0x0400, "Client Error: Bad Request"),
	CLIENT_ERROR_UNAUTHORIZED(0x0401, "Client Error: Unauthorized"),
	CLIENT_ERROR_BAD_OPTION(0x0402, "Client Error: Bad Option"),
	CLIENT_ERROR_FORBIDDEN(0x0403, "Client Error: Forbidden"),
	CLIENT_ERROR_NOT_FOUND(0x0404, "Client Error: Not Found"),
	CLIENT_ERROR_NOT_ALLOWED(0x0405, "Client Error: Method Not Allowed"),
	CLIENT_ERROR_NOT_ACCEPTED(0x0406, "Client Error: Not Accepted"),
	CLIENT_ERROR_PRECONDITION_FAILED(0x0412, "Client Error: Precondition Failed"),
	CLIENT_ERROR_ENTITY_TOO_LARGE(0x0413, "Client Error: Request Entity Too Large"),
	CLIENT_ERROR_UNSUPPORTED_FORMAT(0x0415, "Client Error: Unsupported Content Format"),
	SERVER_ERROR(0x0500, "Server Error"),
	SERVER_ERROR_BAD_GATEWAY(0x0502, "Server Error: Bad Gateway"),
	SERVER_ERROR_SERVICE_UNAVAILABLE(0x0503, "Server Error: Service Unavailable"),
	SERVER_ERROR_GATEWAY_TIMEOUT(0x0504, "Server Error: Gateway Timeout"),
	SERVER_ERROR_PROXYING_NOT_SUPPORTED(0x0505, "Server Error: Proxying Not Supported");

	// Variables
	private final int id;
	
	private final String description;
	
	private final static HashMap<Integer, RestFulStatusEnum> lookupTable = new HashMap<Integer, RestFulStatusEnum>();
	
	static {
		for (RestFulStatusEnum status:values())
			lookupTable.put(status.getID(), status);
	}

	/**
	 * Class constructor. Instantiates a new {@code RestFulStatusEnum} enumeration
	 * entry with the given parameters.
	 *
	 * @param id RestFul status id.
	 * @param description RestFul status description.
	 */
	private RestFulStatusEnum(int id, String description) {
		this.id = id;
		this.description = description;
	}

	/**
	 * Retrieves the RestFul status ID.
	 *
	 * @return RestFul status ID.
	 */
	public int getID() {
		return id;
	}

	/**
	 * Retrieves the RestFul status description.
	 *
	 * @return RestFul status description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Retrieves the RestFul status for the given ID.
	 *
	 * @param id ID to retrieve the RestFul status.
	 *
	 * @return The RestFul status associated with the given ID.
	 */
	public static RestFulStatusEnum get(int id) {
		return lookupTable.get(id);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return description + " [" + id + "]";
	}
}

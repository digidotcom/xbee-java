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
package com.digi.xbee.api.packet.devicecloud;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digi.xbee.api.models.SendDataRequestOptions;
import com.digi.xbee.api.packet.APIFrameType;
import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.utils.HexUtils;

/**
 * This class represents a Send Data Request packet. Packet is built
 * using the parameters of the constructor or providing a valid API payload.
 *
 * <p>This frame type is used to send a file of the given name and type to
 * Device Cloud.</p>
 *
 * <p>If the frame ID is non-zero, a {@link SendDataResponsePacket} will be
 * received.</p>
 *
 * @see SendDataResponsePacket
 * @see com.digi.xbee.api.packet.XBeeAPIPacket
 * 
 * @since 1.2.0
 */
public class SendDataRequestPacket extends XBeeAPIPacket {

	// Constants.
	private static final int MIN_API_PAYLOAD_LENGTH = 6; /* 1 (Frame type) + 1 (Frame ID) +  1 (Path length) +
																1 (Content type length) + 1 (Transport) + 1 (Options) */

	private static final String ERROR_PAYLOAD_NULL = "Send Data Request packet payload cannot be null.";
	private static final String ERROR_INCOMPLETE_PACKET = "Incomplete Send Data Request packet.";
	private static final String ERROR_NOT_VALID = "Payload is not a Send Data Request packet.";
	private static final String ERROR_FRAME_ID_ILLEGAL = "Frame ID must be between 0 and 255.";
	private static final String ERROR_OPTIONS_NULL = "Options cannot be null.";

	// Variables.
	private String path;
	private String contentType;

	private final int transport = 0x00; // Must be 0 to indicate TCP.

	private SendDataRequestOptions options;

	private byte[] fileData;

	private Logger logger;

	/**
	 * Creates a new {@code SendDataRequestPacket} object from the given payload.
	 *
	 * @param payload The API frame payload. It must start with the frame type
	 *                corresponding to a Send Data Request packet
	 *                ({@code 0x28}). The byte array must be in
	 *                {@code OperatingMode.API} mode.
	 *
	 * @return Parsed Send Data Request packet.
	 *
	 * @throws IllegalArgumentException if {@code payload[0] != APIFrameType.SEND_DATA_REQUEST.getValue()} or
	 *                                  if {@code payload.length < }{@value #MIN_API_PAYLOAD_LENGTH}.
	 * @throws NullPointerException if {@code payload == null}.
	 */
	public static SendDataRequestPacket createPacket(byte[] payload) {
		if (payload == null)
			throw new NullPointerException(ERROR_PAYLOAD_NULL);

		if (payload.length < MIN_API_PAYLOAD_LENGTH)
			throw new IllegalArgumentException(ERROR_INCOMPLETE_PACKET);

		if ((payload[0] & 0xFF) != APIFrameType.SEND_DATA_REQUEST.getValue())
			throw new IllegalArgumentException(ERROR_NOT_VALID);

		// payload[0] is the frame type.
		int index = 1;

		// Frame ID byte.
		int frameID = payload[index] & 0xFF;
		index = index + 1;

		// Path length byte.
		int pathLength = payload[index] & 0xFF;
		index = index + 1;

		// Path string.
		String path = null;
		if (pathLength > 0) {
			path = new String(Arrays.copyOfRange(payload, index, index + pathLength));
			index = index + pathLength;
		}

		// Content type length byte.
		int contentTypeLength = payload[index] & 0xFF;
		index = index + 1;

		// Content type string.
		String contentType = null;
		if (contentTypeLength > 0) {
			contentType = new String(Arrays.copyOfRange(payload, index, index + contentTypeLength));
			index = index + contentTypeLength;
		}

		// Transport byte, reserved.
		index = index + 1;

		// Target length byte.
		SendDataRequestOptions options = SendDataRequestOptions.get(payload[index] & 0xFF);
		index = index + 1;

		// Get data.
		byte[] data = null;
		if (index < payload.length)
			data = Arrays.copyOfRange(payload, index, payload.length);

		return new SendDataRequestPacket(frameID, path, contentType, options, data);
	}

	/**
	 * Class constructor. Instantiates a new {@code SendDataRequestPacket}
	 * object with the given parameters.
	 *
	 * @param frameID Frame ID.
	 * @param path Path of the file to upload to Device Cloud.
	 * @param contentType Content type of the file to upload.
	 * @param options Options.
	 * @param fileData Data of the file to upload.
	 *
	 * @throws IllegalArgumentException if {@code frameID < 0} or
	 *                                  if {@code frameID > 255}.
	 * @throws NullPointerException if {@code options == null}.
	 *
	 * @see SendDataRequestOptions
	 */
	public SendDataRequestPacket(int frameID, String path, String contentType, SendDataRequestOptions options, byte[] fileData) {
		super(APIFrameType.SEND_DATA_REQUEST);

		if (frameID < 0 || frameID > 255)
			throw new IllegalArgumentException(ERROR_FRAME_ID_ILLEGAL);
		if (options == null)
			throw new NullPointerException(ERROR_OPTIONS_NULL);

		this.frameID = frameID;
		this.path = path;
		this.contentType = contentType;
		this.options = options;
		this.fileData = fileData;
		this.logger = LoggerFactory.getLogger(SendDataRequestPacket.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#getAPIPacketSpecificData()
	 */
	@Override
	protected byte[] getAPIPacketSpecificData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			if (path == null)
				os.write(0x00);
			else {
				os.write(path.length());
				os.write(path.getBytes());
			}
			if (contentType == null)
				os.write(0x00);
			else {
				os.write(contentType.length());
				os.write(contentType.getBytes());
			}
			os.write(transport);
			os.write(options.getID());
			if (fileData != null)
				os.write(fileData);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return os.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#needsAPIFrameID()
	 */
	@Override
	public boolean needsAPIFrameID() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.api.packet.XBeeAPIPacket#isBroadcast()
	 */
	@Override
	public boolean isBroadcast() {
		return false;
	}

	/**
	 * Sets the path of the file to upload to Device Cloud.
	 *
	 * @param path Path of the file to upload to Device Cloud.
	 *
	 * @see #getPath()
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Retrieves the path of the file to upload to Device Cloud.
	 *
	 * @return The path of the file to upload to Device Cloud.
	 *
	 * @see #setPath(String)
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the content type of the file to upload.
	 *
	 * @param contentType The content type of the file to upload.
	 *
	 * @see #getContentType()
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Retrieves the content type of the file to upload.
	 *
	 * @return The content type of the file to upload.
	 *
	 * @see #setContentType(String)
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Retrieves the transport.
	 *
	 * @return The transport.
	 */
	public int getTransport() {
		return transport;
	}

	/**
	 * Sets the file upload operation options.
	 *
	 * @param options File upload operation options.
	 *
	 * @throws NullPointerException if {@code options == null}.
	 *
	 * @see #getOptions()
	 * @see SendDataRequestOptions
	 */
	public void setOptions(SendDataRequestOptions options) {
		if (options == null)
			throw new NullPointerException(ERROR_OPTIONS_NULL);

		this.options = options;
	}

	/**
	 * Retrieves the file upload operation options.
	 *
	 * @return The file upload operation options.
	 *
	 * @see #setOptions(SendDataRequestOptions)
	 * @see SendDataRequestOptions
	 */
	public SendDataRequestOptions getOptions() {
		return options;
	}

	/**
	 * Sets the data of the file to upload.
	 *
	 * @param fileData Data of the file to upload.
	 *
	 * @see #getFileData()
	 */
	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	/**
	 * Retrieves the data of the file to upload.
	 *
	 * @return The data of the file to upload.
	 *
	 * @see #setFileData(byte[])
	 */
	public byte[] getFileData() {
		return fileData;
	}

	/*
	 * (non-Javadoc)
	 * @see com.digi.xbee.packet.XBeeAPIPacket#getAPIPacketParameters()
	 */
	@Override
	public LinkedHashMap<String, String> getAPIPacketParameters() {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		if (path != null) {
			parameters.put("Path length", HexUtils.prettyHexString(HexUtils.integerToHexString(path.length(), 1)) + " (" + path.length() + ")");
			parameters.put("Path", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(path.getBytes())) + " (" + path + ")");
		} else
			parameters.put("Path length", HexUtils.prettyHexString(HexUtils.integerToHexString(0x00, 1)) + " (0)");
		if (contentType != null) {
			parameters.put("Content Type length", HexUtils.prettyHexString(HexUtils.integerToHexString(contentType.length(), 1)) + " (" + contentType.length() + ")");
			parameters.put("Content Type", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(contentType.getBytes())) + " (" + contentType + ")");
		} else
			parameters.put("Content Type length", HexUtils.prettyHexString(HexUtils.integerToHexString(0x00, 1)) + " (0)");
		parameters.put("Transport", HexUtils.prettyHexString(HexUtils.integerToHexString(transport, 1)));
		parameters.put("Options", HexUtils.prettyHexString(HexUtils.integerToHexString(options.getID(), 1)) + " (" + options.getName() + ")");
		if (fileData != null)
			parameters.put("Data", HexUtils.prettyHexString(HexUtils.byteArrayToHexString(fileData)));
		return parameters;
	}
}

package com.digi.xbee.api.exceptions;

/**
 * Generic XBee API exception. This exception is capable of encapsulating
 * several error causes generating an error message for each one.
 */
public class XBeeException extends Exception {

	// Constants
	private static final long serialVersionUID = 1L;
	
	/**
	 * The selected serial port is not valid.
	 */
	public static final int INVALID_PORT = 0;
	
	/**
	 * XBee connection is already open.
	 */
	public static final int CONNECTION_ALREADY_OPEN = 1;

	/**
	 * XBee connection not open.
	 */
	public static final int CONNECTION_NOT_OPEN = 2;

	/**
	 * XBee connection timeout.
	 */
	public static final int CONNECTION_TIMEOUT = 3;
	
	/**
	 * XBee connection not OK.
	 */
	public static final int CONNECTION_NOT_OK = 4;

	/**
	 * XBee invalid packet.
	 */
	public static final int INVALID_PACKET = 5;
	
	/**
	 * XBee no valid port configuration.
	 */
	public static final int NO_VALID_PORT_CONFIGURATION = 6;
	
	/**
	 * XBee connection configuration not found.
	 */
	public static final int CONFIGURATION_NOT_FOUND = 7;
	
	/**
	 * Invalid argument.
	 */
	public static final int INVALID_ARGUMENT = 8;
	
	/**
	 * Invalid operation.
	 */
	public static final int INVALID_OPERATION = 9;
	
	/**
	 * Port in use.
	 */
	public static final int PORT_IN_USE = 10;
	
	/**
	 * Not valid XBee device was found.
	 */
	public static final int NO_DEVICE_FOUND = 11;
	
	/**
	 * Insufficient permissions to access XBee device.
	 */
	public static final int PERMISSION_NOT_GRANTED = 12;
	
	/**
	 * Invalid command answer.
	 */
	public static final int INVALID_COMMAND_ANSWER = 13;
	
	/**
	 * Generic exception.
	 */
	public static final int GENERIC = 99;
	
	// Variables
	private int errorCode;
	
	/**
	 * Creates an XBeeException with the specified error code.
	 * 
	 * @param errorCode The code of the error.
	 */
	public XBeeException(int errorCode) {
		super(getTypeString(errorCode));
		this.errorCode = errorCode;
	}
	
	/**
	 * Creates an XBeeException that wraps another exception.
	 * 
	 * @param cause The cause of this exception.
	 */
	public XBeeException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Creates an XBeeException that wraps another exception.
	 * 
	 * @param errorCode The code of the error.
	 * @param cause The cause of this exception.
	 */
	public XBeeException(int errorCode, Throwable cause) {
		super(getTypeString(errorCode), cause);
		this.errorCode = errorCode;
	}

	/**
	 * Creates an XBeeException with the specified error code.
	 * 
	 * @param errorCode The code of the error.
	 * @param message The associated message.
	 */
	public XBeeException(int errorCode, String message) {
		super(message.trim());
		this.errorCode = errorCode;
	}

	/**
	 * Creates a XBeeException that wraps another exception.
	 * 
	 * @param errorCode The code of the error.
	 * @param message The associated message.
	 * @param cause The cause of this exception.
	 */
	public XBeeException(int errorCode, String message, Throwable cause) {
		super(message.trim(), cause);
		this.errorCode = errorCode;
	}

	/**
	 * Returns the cause of this exception or null if no cause was
	 * set.
	 * 
	 * @return The cause of this exception or null if no cause was set.
	 */
	public Throwable getCause() {
		return super.getCause();
	}

	/**
	 * Returns the error code associated with this exception.
	 * 
	 * @return The error code of this exception.
	 */
	public int getErrorCode() {
		return errorCode;
	}
	
	/**
	 * Retrieves a message depending on the error code.
	 * 
	 * @param errorCode Error code to retrieve message for.
	 * @return Message depending on the error type.
	 */
	public static String getTypeString(int errorCode) {
		switch (errorCode) {
		case CONFIGURATION_NOT_FOUND:
			return "Serial configuration not found.";
		case CONNECTION_ALREADY_OPEN:
			return "Connection already open.";
		case CONNECTION_NOT_OK:
			return "Command did not return OK.";
		case CONNECTION_NOT_OPEN:
			return "Connection not open.";
		case CONNECTION_TIMEOUT:
			return "Connection timeout.";
		case INVALID_ARGUMENT:
			return "Invalid argument.";
		case INVALID_PACKET:
			return "Invalid packet.";
		case INVALID_PORT:
			return "Invalid port.";
		case NO_VALID_PORT_CONFIGURATION:
			return "Port configuration not valid.";
		case INVALID_OPERATION:
			return "Invalid operation.";
		case PORT_IN_USE:
			return "Port is already in use by other applications.";
		case NO_DEVICE_FOUND:
			return "No compatible XBee device found.";
		case PERMISSION_NOT_GRANTED:
			return "Insufficient permissions to access XBee device.";
		case INVALID_COMMAND_ANSWER:
			return "Received invalid command answer from device.";
		}
		return "";
	}
}

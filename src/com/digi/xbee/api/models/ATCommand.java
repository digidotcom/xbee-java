package com.digi.xbee.api.models;

/**
 * This class represents an AT command used to read or set different
 * properties of the XBee device. AT commands can be sent directly to
 * the connected device or to remote devices and may have parameters. 
 * 
 * After executing an AT Command, an AT Response is received from the device.
 *  See {@link com.digi.xbee.models.ATResponse}.
 */
public class ATCommand {

	// Constants
	private static final String AT_HEADER = "AT";
	private static final String END_LINE = "\r";
	
	// Variables
	private String command;
	
	private byte[] parameter;
	
	/**
	 * Class constructor. Instances a new object of type ATCommand with
	 * the given parameters.
	 * 
	 * @param command The AT Command.
	 */
	public ATCommand(String command) {
		this.command = command;
		this.parameter = null;
	}
	
	/**
	 * Class constructor. Instances a new object of type ATCommand with
	 * the given parameters.
	 * 
	 * @param command The AT Command.
	 * @param parameter The command parameter as string, null if no parameter is required.
	 */
	public ATCommand(String command, String parameter) {
		this.command = command;
		this.parameter = parameter.getBytes();
	}
	
	/**
	 * Class constructor. Instances a new object of type ATCommand with
	 * the given parameters.
	 * 
	 * @param command The AT Command.
	 * @param parameter The command parameter as byte array, null if no parameter is required.
	 */
	public ATCommand(String command, byte[] parameter) {
		this.command = command;
		this.parameter = parameter;
	}
	
	/**
	 * Retrieves the AT command.
	 * 
	 * @return The AT command.
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * Retrieves the AT command parameter.
	 * 
	 * @return The AT command parameter, null if none.
	 */
	public byte[] getParameter() {
		return parameter;
	}
	
	/**
	 * Retrieves the AT command parameter in string format.
	 * 
	 * @return The AT command parameter, null if none.
	 */
	public String getParameterString() {
		if (parameter == null)
			return null;
		return new String(parameter);
	}
	
	/**
	 * Sets the AT command.
	 * 
	 * @param command The AT command.
	 */
	public void setCommand(String command) {
		this.command = command;
	}
	
	/**
	 * Sets the AT command parameter as string.
	 * 
	 * @param parameter The AT command parameter as string, null if none.
	 */
	public void setParameter(String parameter) {
		this.parameter = parameter.getBytes();
	}
	
	/**
	 * Sets the AT command parameter as byte array.
	 * 
	 * @param parameter The AT command parameter as byte array, null if none.
	 */
	public void setParameter(byte[] parameter) {
		this.parameter = parameter;
	}
	
	/**
	 * Generates the AT command byte array.
	 * 
	 * @return The full AT command as byte array. 
	 */
	public byte[] generateByteArray() {
		byte[] data;
		if (parameter != null) {
			data = new byte[AT_HEADER.length() + command.length() + parameter.length + END_LINE.length()];
			System.arraycopy(AT_HEADER.getBytes(), 0, data, 0, AT_HEADER.length());
			System.arraycopy(command.getBytes(), 0, data, AT_HEADER.length(), command.length());
			System.arraycopy(parameter, 0, data, AT_HEADER.length() + command.length(), parameter.length);
			System.arraycopy(END_LINE.getBytes(), 0, data, AT_HEADER.length() + command.length() + parameter.length, END_LINE.length());
		} else {
			data = new byte[AT_HEADER.length() + command.length() + END_LINE.length()];
			System.arraycopy(AT_HEADER.getBytes(), 0, data, 0, AT_HEADER.length());
			System.arraycopy(command.getBytes(), 0, data, AT_HEADER.length(), command.length());
			System.arraycopy(END_LINE.getBytes(), 0, data, AT_HEADER.length() + command.length() , END_LINE.length());
		}
		return data;
	}
}

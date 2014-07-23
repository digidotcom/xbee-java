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
	
	// Variables
	private final String command;
	
	private byte[] parameter;
	
	/**
	 * Class constructor. Instances a new object of type ATCommand with
	 * the given parameters.
	 * 
	 * @param command The AT Command.
	 * 
	 * @throws NullPointerException if {@code command == null}.
	 */
	public ATCommand(String command) {
		if (command == null)
			throw new NullPointerException("Command cannot be null.");
		
		this.command = command;
		this.parameter = null;
	}
	
	/**
	 * Class constructor. Instances a new object of type ATCommand with
	 * the given parameters.
	 * 
	 * @param command The AT Command.
	 * @param parameter The command parameter as string, null if no parameter is required.
	 * 
	 * @throws NullPointerException if {@code command == null}.
	 */
	public ATCommand(String command, String parameter) {
		if (command == null)
			throw new NullPointerException("Command cannot be null.");
		
		this.command = command;
		if (parameter != null)
			this.parameter = parameter.getBytes();
	}
	
	/**
	 * Class constructor. Instances a new object of type ATCommand with
	 * the given parameters.
	 * 
	 * @param command The AT Command.
	 * @param parameter The command parameter as byte array, null if no parameter is required.
	 * 
	 * @throws NullPointerException if {@code command == null}.
	 */
	public ATCommand(String command, byte[] parameter) {
		if (command == null)
			throw new NullPointerException("Command cannot be null.");
		
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
}

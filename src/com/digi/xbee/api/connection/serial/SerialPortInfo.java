package com.digi.xbee.api.connection.serial;

/**
 * This is a helper class used to store port information.
 */
public class SerialPortInfo {
	
	// Variables
	private String portName;
	private String portDescription;
	
	/**
	 * Class constructor. Instances a new serial port info object with the 
	 * given parameters.
	 * 
	 * @param portName Name of the port.
	 */
	public SerialPortInfo(String portName) {
		this(portName, null);
	}
	
	/**
	 * Class constructor. Instances a new serial port info object with the 
	 * given parameters.
	 * 
	 * @param portName Name of the port.
	 * @param portDescription Description of the port.
	 */
	public SerialPortInfo(String portName, String portDescription) {
		this.portName = portName;
		this.portDescription = portDescription;
	}
	
	/**
	 * Retrieves the port name.
	 * 
	 * @return The port name.
	 */
	public String getPortName() {
		return portName;
	}
	
	/**
	 * Retrieves the serial port description.
	 * 
	 * @return The serial port description.
	 */
	public String getPortDescription() {
		return portDescription;
	}
	
	/**
	 * Sets the serial port description.
	 * 
	 * @param portDescription The serial port description.
	 */
	public void setPortDescription(String portDescription) {
		this.portDescription = portDescription;
	}
}

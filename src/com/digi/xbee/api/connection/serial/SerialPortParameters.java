package com.digi.xbee.api.connection.serial;

/**
 * Helper class used to store serial connection parameters information.
 * 
 * Parameters are stored as public variables so that can be accessed and read
 * from any class.
 */
public class SerialPortParameters {

	// Variables
	public int baudrate;
	public int dataBits;
	public int stopBits;
	public int parity;
	public int flowControl;
	
	/**
	 * Class constructor. Instances a new object of type XBeeSerialPortParameters with
	 * the given parameters.
	 * 
	 * @param baudrate Serial connection baud rate,
	 * @param dataBits Serial connection data bits.
	 * @param stopBits Serial connection stop bits.
	 * @param parity Serial connection parity.
	 * @param flowControl Serial connection flow control.
	 */
	public SerialPortParameters(int baudrate, int dataBits, int stopBits, int parity, int flowControl) {
		setParams(baudrate, dataBits, stopBits, parity, flowControl);
	}
	
	/**
	 * Sets the serial port configuration parameters.
	 * 
	 * @param baudrate Serial connection baud rate.
	 * @param dataBits Serial connection data bits.
	 * @param stopBits Serial connection stop bits.
	 * @param parity Serial connection parity.
	 * @param flowControl Serial connection flow control.
	 */
	public void setParams(int baudrate, int dataBits, int stopBits, int parity, int flowControl) {
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
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Baud Rate: "+ baudrate + ", Data Bits: " + dataBits + ", Stop Bits:" + stopBits + ", Parity: " + parity + ", Flow Control: " + flowControl;
	}
}

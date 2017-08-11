  Introduction
  ------------
  This sample Java application shows how to send CoAP data from an IPv6 device to 
  another one connected to a network using the XBee Java Library.

  The application sends CoAP data to another Thread device on the network with a 
  specific IPv6 address and port number.

  NOTE: This example uses the Thread device (ThreadDevice) class.


  Files
  -----
    * com.digi.xbee.api.sendcoapdata.MainApp.java:
      Main application class. It instantiates a Thread device, establishes a 
      serial connection with it and sends the CoAP data to the XBee device with 
      the IPv6 address and port specified. Finally it prints out the result of the 
      sent operation.


  Requirements
  ------------
  To run this example you will need:

    * At least two XBee Thread radios in API mode and their corresponding carrier
      boards (XBIB or equivalent).
    * The XCTU application (available at www.digi.com/xctu).


  Compatible protocols
  --------------------
    * Thread


  Example setup
  -------------
    1) Plug the XBee radios into the XBee adapters and connect them to your
       computer's USB or serial ports.

    2) Ensure that the modules are in API mode and connected to the same network.
       For further information on how to perform this task, read the 
       'Configuring Your XBee Modules' topic of the Getting Started guide.

    3) Set the port and baud rate of the sender (local) XBee radio in the 
       MainApp class.
       If you configured the modules in the previous step with the XCTU, you 
       will see the port number and baud rate in the 'Port' label of the device 
       on the left view.

    4) Set the destination IPv6 address in the MainApp class. You can find it 
       by reading the 'MY' ('GA' or 'LA').


  Running the example
  -------------------
  First, build the application. Then, you need to set up XCTU to see the data 
  received by the remote XBee device. Follow these steps to do so:

    1) Launch the XCTU application.

    2) Add the remote XBee module to the XCTU, specifying its port settings.

    3) Switch to the 'Consoles' working mode and open the serial connection 
       so you can see the data when it is received.

  Finally, launch the sample application, some CoAP data is sent to the configured 
  remote XBee device. When that happens, a line with the result of the operation 
  is printed to the standard output:

    Sending CoAP data to XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:XXXX >> 48 65 6C 6C 6F 20 58 42 65 65 21 | Hello XBee!... Success

     - Where XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:XXXX:XXXX is the IPv6 address address 
       of the remote XBee device.

  Verify that in the XCTU console a new RX IPv6 frame has been received by the 
  remote XBee device. Select it and review the details, some of the details 
  will be similar to:

    - Start delimiter:         7E
    - Length:                  Variable
    - Frame type:              9A (IPv6 Rx Response)
    - Destination address:     The XBee receiver's IP address.
    - Source address:          The XBee sender's IP address.
    - Destination port:        The configured port number.
    - Source port:             A random port chosen by the sender module.
    - Protocol:                03 (CoAP)
    - Status:                  00 (Reserved)
    - RF data:                 48 65 6C 6C 6F 20 58 42 65 65 21
                               Hello XBee!

  Introduction
  ------------
  This sample Java application shows how to send UDP data from an IP device to 
  another one connected to the Internet using the XBee Java Library.

  The application sends UDP data to the specified IP address and port number.

  NOTE: This example uses the Cellular device (CellularDevice) class, but it
        can be applied to other Internet capable XBee device classes such as 
        WiFiDevice.


  Files
  -----
    * com.digi.xbee.api.sendudpdata.MainApp.java:
      Main application class. It instantiates a NB-IoT device, establishes a 
      serial connection with it and sends the UDP data to the specified IP 
      address and port. Finally it prints out the result of the sent operation.


  Requirements
  ------------
  To run this example you will need:

    * One XBee Cellular in API mode and its corresponding carrier board 
      (XBIB or equivalent).
    * The XCTU application (available at www.digi.com/xctu).


  Compatible protocols
  --------------------
    * Cellular
    * Wi-Fi


  Example setup
  -------------
    1) Plug the XBee radio into the XBee adapter and connect it to your
       computer's USB or serial ports.

    2) Ensure that the module is in API mode and connected to the Internet.
       For further information on how to perform this task, read the 
       'Configuring Your XBee Modules' topic of the Getting Started guide.

    3) Set the port and baud rate of the XBee radio in the MainApp class.
       If you configured the module in the previous step with XCTU, you will
       see the port number and baud rate in the 'Port' label of the device 
       on the left view.

    4) Set the destination IP address and port number in the MainApp class.


  Running the example
  -------------------
  First, build and launch the application. As soon as the application is 
  executed, it will send the UDP packet to the specified IP address and port
  number. If the transmission was sent successfully, the following message will
  be printed out in the console:
  
    Sending data to 192.168.1.2:9750 >> 48 65 6C 6C 6F 20 58 42 65 65 21 | Hello XBee!... Success

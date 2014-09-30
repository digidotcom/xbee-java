  Introduction
  ------------
  This is a sample Java application to show how to perform a software reset on
  the local XBee module.
  
  NOTE: This example uses the generic XBee device (XBeeDevice) class, 
        but it can be applied to any other local XBee device class.


  Files
  ----------
    * com.digi.xbee.api.resetmodule.MainApp.java:
      Main application class. It instantiates an XBee device, establishes a 
      serial connection with it and resets the module.


  Requirements
  ------------
  To run this example you will need:
  
    * One XBee radio in API mode and its corresponding carrier board (XBIB 
      or XBee Development Board).


  Example setup
  -------------
    1) Plug the XBee radio into the XBee adapter and connect it to your
       computer's USB or serial port.
       
    2) Ensure that the module is in API mode.
       For further information on how to perform this task, go to [...]
       
    3) Set the port and baud rate of the XBee radio in the MainApp class.
       If you do not know the serial/USB port where your module is connected to,
       see [...]


  Running the example
  -------------------
  First, build and launch the application.
  To test the functionality, check that the message "XBee module reset
  successfully" is printed in the output console.
       
  Introduction
  ------------
  This sample Java application shows how to perform a software reset on the 
  local XBee module.
  
  NOTE: This example uses the generic XBee device (XBeeDevice) class, 
        but it can be applied to any other local XBee device class.


  Files
  -----
    * com.digi.xbee.api.resetmodule.MainApp.java:
      Main application class. It instantiates an XBee device, establishes a 
      serial connection with it and resets the module.


  Requirements
  ------------
  To run this example you will need:
  
    * One XBee radio in API mode and its corresponding carrier board (XBIB 
      or XBee Development Board).
    * The XCTU application (available at www.digi.com/xctu).


  Compatible protocols
  --------------------
    * 802.15.4
    * Cellular
    * DigiMesh
    * Point-to-Multipoint
    * Wi-Fi
    * ZigBee


  Example setup
  -------------
    1) Plug the XBee radio into the XBee adapter and connect it to your
       computer's USB or serial port.
       
    2) Ensure that the module is in API mode.
       For further information on how to perform this task, read the 
       'Configuring Your XBee Modules' topic of the Getting Started guide.
       
    3) Set the port and baud rate of the XBee radio in the MainApp class.
       If you configured the module in the previous step with the XCTU, you 
       will see the port number and baud rate in the 'Port' label of the device 
       on the left view.


  Running the example
  -------------------
  First, build and launch the application. To test the functionality, check 
  that the following message is printed out in the console of the launched 
  application:
    
    ">> XBee module reset successfully"
    
  That message indicates that the module was reset correctly.
  
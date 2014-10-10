  Introduction
  ------------
  This is a sample Java application to show how to obtain the XBee network 
  object from a local XBee device and discover the remote XBee devices that 
  form the network. This example uses an asynchronous discover mechanism. It 
  means that the application will not block during the process. Instead of 
  that, discovery events will be received by the callbacks provided by the 
  discovery listener object.
  
  As the discovery mechanism used in this example does not block the 
  application, remote XBee devices will be printed out as soon as they are 
  found during the discovery.
  
  NOTE: This example uses the generic XBee device (XBeeDevice) class, but it 
        can be applied to any other local XBee device class.


  Files
  ----------
    * com.digi.xbee.api.discoverdevicesasync.MainApp.java:
      Main application class. It instantiates an XBee device, establishes a 
      serial connection with it and gets the XBee network object. Then, 
      performs a device discovery and prints the nodes as soon as they are 
      found.
      
    * com.digi.xbee.api.discoverdevicesasync.MyDiscoveryListener.java:
      Class that handles the remote devices discovery events.


  Requirements
  ------------
  To run this example you will need:
  
    * At least two XBee radios in API mode and their corresponding carrier 
      board (XBIB or equivalent). More than two radios are recommended.


  Example setup
  -------------
    1) Plug the XBee radios into the XBee adapters and connect them to your
       computer's USB or serial ports.
       
    2) Ensure that the modules are in API mode and on the same network.
       For further information on how to perform this task, go to [...]
       
    3) Set the port and baud rate of the local XBee radio in the MainApp class.
       If you do not know the serial/USB port where your module is connected to,
       see [...]


  Running the example
  -------------------
  First, build and launch the application. As soon as the application is 
  executed, it will perform a device discovery in the network. To verify the 
  application is working properly, check that the following happens:
  
    1) The output console states the following message: "Discovering remote 
       XBee devices..."
    
    2) For each discovered device the output console should display the 
       following message: "Device discovered: XXXXXXXXXXXXXXXX", where 
       XXXXXXXXXXXXXXXX is the MAC address of the remote XBee device.
    
    3) When the discovery process finishes the following message should be 
       displayed: "Device discovery finished successfully."
  
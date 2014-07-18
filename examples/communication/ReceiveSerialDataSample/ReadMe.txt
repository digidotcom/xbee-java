  Introduction
  ------------
  This is a sample Java application that notifies the user when new XBee serial
  data packets are received from the connected XBee device.
  
  In this example, one XBee module will act as a ZigBee coordinator and the
  other as a ZigBee router, both in API mode. With the assistance of the XCTU
  application, you will send a packet from the router and see that it is 
  received by the coordinator successfully.


  Main files
  ----------
    * com.digi.xbee.api.receiveserialdata.MainApp.java:
      Main application class. Instantiates an XBee device and connects to it.

    * com.digi.xbee.api.receiveserialdata.MyPacketReceiveListener.java:
      Class that handles the received packets.


  Requirements
  ------------
  To run this example you will need:
  
    * One XBee radio configured as a ZigBee Coordinator API mode.
    * One XBee radio configured as a ZigBee Router API mode.
    * Two XBee USB adapter boards.
    * The XCTU application, available at www.digi.com/xctu.


  Example setup
  -------------
    1) Plug the XBee radios into the XBee adapters and connect them to your
       computer's USB ports.

    2) Ensure that both modules are in the same network.
       For further information on how to do this, go to [...]
       
    3) Set the port and baud rate of the XBee radio configured as Coordinator
       in the MainApp class.
       If you do not know the serial/USB port where your module is connected to,
       see [...]


  Running the example
  -------------------
  The example is already configured, so all you need to do is build and launch 
  the application.
  
  To verify that the application is running successfully, follow these steps:
  
    1) Launch the XCTU application.
    
    2) Add the XBee module configured as router, specifying the port settings.
       
    3) Once the module is added, change to the Consoles working mode and open
       the serial connection.
       
    4) Create and add a frame using the 'Frames Generator' tool with the 
       following parameters:
       
       - Protocol: ZigBee
       - Frame type: 0x10 - Transmit Request
       - Frame ID: 01
       - 64-bit dest. address: 00 00 00 00 00 00 00 00 (coordinator's address)
       - 16-bit dest. address: FF FE
       - Broadcast radius: 00
       - Options: 00
       - RF data (ASCII): Hello XBee!
       
    5) Send the selected frame and check the output console that the packet has 
       been received.
       
  NOTE: For further information on how to use the XCTU application, see [...]
       
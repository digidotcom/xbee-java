  Introduction
  ------------
  This is a sample Java application to show how to receive data packets from 
  another XBee device in the same network.
  
  The application prints the received data to the standard output in ASCII and 
  hexadecimal formats after the sender address.


  Main files
  ----------
    * com.digi.xbee.api.receiveserialdata.MainApp.java:
      Main application class. Instantiates an XBee device and establishes a 
      serial connection with it.
      
    * com.digi.xbee.api.receiveserialdata.MyPacketReceiveListener.java:
      Class that handles the received data packets.


  Requirements
  ------------
  To run this example you will need:
  
    * Al least, two XBee radios in API mode and their corresponding carrier
      board (XBIB or equivalent).
    * The XCTU application, available at www.digi.com/xctu.


  Example setup
  -------------
    1) Plug the XBee radios into the XBee adapters and connect them to your
       computer's USB or serial ports.
       
    2) Ensure that both modules are in API mode and in the same network.
       For further information on how to do this, go to [...]
       
    3) Set the port and baud rate of the receiver XBee radio in the MainApp 
       class.
       If you do not know the serial/USB port where your module is connected to,
       see [...]


  Running the example
  -------------------
  All you need to do is build and launch the application.
  To test its functionality you need some other device in the network to send 
  unicast data to the receiver module. If you want to send data to this module 
  you could follow these steps:
  
    a) Use the 'SendSerialDataSample' example included in the library.
       Follow the instructions on its ReadMe file to do it.
       
    b) Use the XCTU:
       
       1) Launch the XCTU application.
          
       2) First, you need to know the 64-bit address of the destination device 
          (receiver) you will use the XCTU to get it.
          Add the receiver XBee module to the XCTU, specifying its port 
          settings.
          
       3) In the 'Configuration' working mode, select the module you have just 
          added to read its parameters.
          
          To get the 64-bit address of the receiver module look for the 'SH' 
          and 'SL' parameters and copy their value without spaces, starting 
          from the 'SH' parameter and immediately followed by the 'SL'.
          
       4) Remove this module from the XCTU.
          
       5) Add the sender XBee module to the XCTU, specifying its port settings.
          
       6) Once the module is added, change to the 'Consoles' working mode and 
          open the serial connection.
          
       7) Create and add a frame using the 'Frames Generator' tool with the 
          following parameters:
          
          - Protocol:                               Select the protocol of your device.
          - Frame type:                             Select a 64-bit Transmit Request frame.
          - Frame ID:                               01
          - 64-bit dest. address:                   Use the SH and SL values you copied before.
          - 16-bit dest. address (only if present): FF FE
          - Broadcast radius (only if present):     00
          - Options:                                00
          - RF data (ASCII):                        Hello XBee!
          
       8) Send this frame and check the launched application output console: 
          The packet has been received and a line containing the data included 
          in the 'RF data' field of the sent frame is printed out:
          
          Data received from 0013A20040A198B4 >> 48656C6C6F205842656521 | Hello XBee!
          
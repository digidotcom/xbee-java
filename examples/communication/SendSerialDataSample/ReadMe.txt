  Introduction
  ------------
  This is a sample Java application to show how to send data from the connected 
  XBee device to another remote device in the same network.
  
  The application sends data from an XBee device to another specified by its 
  address in the application. It prints in the standard output the destination 
  address followed by the sent data and finally the result of this sent 
  operation.


  Main files
  ----------
    * com.digi.xbee.api.sendserialdata.MainApp1.java:
      Main application class. Instantiates an XBee device, establishes a serial 
      connection with it and sends the serial data to the specified XBee device.


  Requirements
  ------------
  To run this example you will need:
  
    * At least, two XBee radios in API mode and their corresponding carrier
      board (XBIB or equivalent).
    * The XCTU application, available at www.digi.com/xctu.


  Example setup
  -------------
    1) Type the 64-bit address of the receiver XBee module in the MainApp class.
       Find the 64-bit address labeled on the back of the device, a 16 
       characters string that follows the format 0013A20040XXXXXX.
       
    2) Plug the XBee radios into the XBee adapters and connect them to your
       computer's USB or serial ports.
       
    3) Ensure that the modules are in API mode and in the same network.
       For further information on how to do this, go to [...]
       
    4) Set the port and baud rate of the sender XBee radio in the MainApp class.
       If you do not know the serial/USB port where your module is connected to,
       see [...]


  Running the example
  -------------------
  All you need to do is build and launch the application.
  To test its functionality you need the device with the configured address 
  during the setup above to be listening to unicast data. For this purpose you 
  could:
  
    a) Use the 'ReceiveSerialDataSample' example included in the library.
       Follow the instructions on its 'ReadMe' file to do it.
       
    b) Use the XCTU:
       
       1) Launch the XCTU application.
          
       2) Add the receiver XBee module to the XCTU, specifying its port settings.
          
       3) Once the module is added, change to the 'Consoles' working mode and 
          open the serial connection so you can see the data when it is received.
          
       4) Launch the sample application, some data is sent to the configured 
          destination address and a line with the result of the operation is 
          printed out to the standard output:
          
          Sending data to 0013A20040A6A0DB >> 48 65 6C 6C 6F 20 58 42 65 65 21 | Hello XBee!... Success
          
          Also, in the XCTU console a new RX frame has been received. Select it 
          and review its details, some of them will be similar to:
          
          - Start delimiter:         7E
          - Length:                  Depends on the XBee protocol.
          - Frame type:              Depends on the XBee protocol.
          - 64-bit source address:   The XBee sender's 64-bit address.
          - RF data/Received data:   48 65 6C 6C 6F 20 58 42 65 65 21
                                     Hello XBee!
          
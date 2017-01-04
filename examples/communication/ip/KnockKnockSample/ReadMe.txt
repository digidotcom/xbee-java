  Introduction
  ------------
  This sample Java application shows how to connect an Internet radio module 
  to a web server to send and receive data using the XBee Java Library.

  In this example, the server starts a Knock Knock joke and you have to respond
  to its messages accordingly to continue the joke.

  NOTE: This example uses the Wi-Fi device (WiFiDevice) class, but it can be
        applied to other Internet capable XBee device classes such as
        CellularDevice.


  Files
  -----
    * com.digi.xbee.api.knockknock.MainApp.java:
      Main application class. It starts the web server, instantiates a Wi-Fi
      device and establishes a serial connection with it. Then, starts the
      communication with the server by sending an empty message.

    * com.digi.xbee.api.knockknock.WebServer.java:
      Class that creates a web server and listens for incoming messages. It uses
      the KnockKnockProtocol to parse the message and generate the response.

    * com.digi.xbee.api.knockknock.KnockKnockProtocol.java:
      Class that generates a response based on the input message following the
      Knock Knock jokes.


  Compatible protocols
  --------------------
    * Cellular
    * Wi-Fi


  Requirements
  ------------
  To run this example you will need:

    * One XBee Wi-Fi radio in API mode and its corresponding carrier board (XBIB
      or equivalent).
    * The XCTU application (available at www.digi.com/xctu).


  Example setup
  -------------
    1) Plug the Wi-Fi radio into the XBee adapter and connect it to your 
       computer's USB or serial port.

    2) Ensure that the module is in API mode and connected to the same network
       as your computer.
       For further information on how to perform this task, read the 
       'Configuring Your XBee Modules' topic of the Getting Started guide.

    3) Set the port and baud rate of the XBee radio in the MainApp class.
       If you configured the module in the previous step with the XCTU, you 
       will see the port number and baud rate in the 'Port' label of the device 
       on the left view.


  Running the example
  -------------------
  First, build and launch the application. When the application starts, it 
  connects to the web server and sends an empty message to start the
  communication. You will receive the following message through the standard 
  output:

    Knock! Knock!

  You have to respond typing the following text:

    Who's there?

  The server then responds with the clue:

    Turnip

  Now, respond with:

    Turnip who?

  Finally the server responds with the punch line. You can continue with more
  jokes by sending a 'y' or finishing the connection with 'n' and then 'Bye.'.

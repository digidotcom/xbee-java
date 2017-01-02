/**
 * Copyright (c) 2016-2017 Digi International Inc.,
 * All rights not expressly granted are reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Digi International Inc. 11001 Bren Road East, Minnetonka, MN 55343
 * =======================================================================
 */
package com.digi.xbee.api.knockknock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Simple web server that responds to clients with a text message.
 */
public class WebServer {

	/**
	 * Starts the web server in the given port.
	 *
	 * @param port The port number of the web server.
	 */
	public static void start(final int port) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ServerSocket serverSocket = null;
				Socket client = null;
				try {
					serverSocket = new ServerSocket(port);
					KnockKnockProtocol kkp = new KnockKnockProtocol();
					while (true) {
						client = serverSocket.accept();
						PrintWriter out = new PrintWriter(client.getOutputStream());
						BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
						String inputLine, outputLine;
						while ((inputLine = in.readLine()) != null) {
							outputLine = kkp.processInput(inputLine);
							out.print(outputLine);
							out.flush();
							if (outputLine.equals("Bye."))
								break;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (serverSocket != null)
						try {
							serverSocket.close();
						} catch (IOException e) {}
				}
			}
		}).start();
	}
}

/**
 * Copyright 2017, Digi International Inc.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES 
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR 
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES 
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN 
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF 
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
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

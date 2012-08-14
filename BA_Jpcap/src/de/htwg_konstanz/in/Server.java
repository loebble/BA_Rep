/**
 * 
 */
package de.htwg_konstanz.in;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * @author Ellen Wieland
 *
 */
public class Server implements Runnable {

	private SwitchableSocket switchableSocket;
	private OutputStream out;
	private InputStream in;
	
	private static final int SERVER_PORT = 8205;
	private static final int BUFFER_SIZE = 512;
	
	private static Date c;
		
	public Server(Socket socket) {
		try {
			switchableSocket = new SwitchableSocket(socket);
			out = switchableSocket.getOutputStream();
			in = switchableSocket.getInputStream();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	/**
	 * @return the switchableSocket
	 */
	public SwitchableSocket getSwitchableSocket() {
		return switchableSocket;
	}



	public void run() {		
		try {
			byte[] buffer = new byte[BUFFER_SIZE];
			int recvMsgSize;
			while ((recvMsgSize = in.read(buffer)) != -1) {				
				
				String message = new String(buffer).trim();
				c = new Date();
				System.out.println(c+ ": " + message.substring(0, recvMsgSize));
				out.write(buffer, 0, recvMsgSize);
				out.flush();
			}
			c = new Date();
			System.out.println(c+ ": Received EOF -> end server");
			// end of test
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {		
		try {
			ServerSocket serverSocket = new ServerSocket(SERVER_PORT);			
			System.out.println("TCPServer waiting for messages..");
			
			Server server = new Server(serverSocket.accept());
			Thread thread = new Thread(server);
			thread.start();
			
			while (true) {
				// switch socket connection when a new client connects
				Socket newSocket = serverSocket.accept();
				c = new Date();
				System.out.println(c+ ": SWITCH");
				server.switchableSocket.switchSocket(newSocket);
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}

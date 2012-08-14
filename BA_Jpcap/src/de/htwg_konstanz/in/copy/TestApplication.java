/**
 * 
 */
package de.htwg_konstanz.in.copy;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import javax.swing.JOptionPane;


/**
 * @author Ellen
 *
 */
public class TestApplication implements Runnable {
	
	Client sender;
	Date c;
	
	public TestApplication(Client sender) {
		this.sender = sender;
	}

	public static void main(String[] args) throws IOException {
		String p = FileChecker.convertIt("C:/jdk-6u3");
		System.out.println("File converted path: " + p);
		Client sender = new Client();	
		TestApplication testApplication = new TestApplication(sender);
		Thread thread = new Thread(testApplication);
		thread.start();
		boolean done = false;
		while (!done) {
			String m = 
				JOptionPane.showInputDialog("File Located: ");
			if(m == null || m.equals("") ){
				m = "C:/convertedFile";
			}
			try {
//				int res = 
				done = sender.parseAndSendFile(m);
//				switch (res){
//				case 0:
//					System.out.println("Normal Message sent");
//					break;
//				case -1:
//					System.out.println("Message stored because of switching");
//					break;
//				case -2:
//					System.out.println("Not all Stored Message where sent because of switching");
//					break;
//				case 1:
//					System.out.println("All stored Message where sent");
//					break;
//				default:
//					System.out.println("Shouldn't happen!");
//					break;
//				}
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		thread.interrupt();
		
		try {
			sender.getSocket().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("END");
	}

	
	public void run() {
		int i = 1;
		while (true) {
			int time = 4000;
			// "random" time to wait before switching connection
			// int randomTimeInMillis = (int) (Math.random() * 5000)+5000;
			System.out.println("SLEEP: "+ time + "secs" );
			try {
				Thread.sleep(time);
				c = new Date();
				System.out.println(c+ ": SWITCH");
				Socket socket = new Socket("localhost", 8205);
				sender.isSwitching = true;
				sender.getSocket().switchSocket(socket);
				sender.getSocket().clientName = "Client NR " +i;
				c = new Date();
				//Waiting some seconds to simulate the switching-process time
		//		System.out.println(c+  ": Switch ended: waiting for another 3 secs...");
		//		Thread.sleep(1000);
				sender.isSwitching = false;
				c = new Date();
				System.out.println(c+  ": done with switching...");
				//Thread.sleep(6000);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
			i++;
		}
		
	}

	
}

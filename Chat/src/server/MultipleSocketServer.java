package server;

import java.net.*;
import java.io.*;
import java.util.*;

/*Code derived from http://edn.embarcadero.com/article/31995*/
public class MultipleSocketServer implements Runnable {

	private Socket connection;
	private String TimeStamp;
	private int ID;
	 
	public MultipleSocketServer(Socket s, int i) {
		this.connection = s;
		this.ID = i;
	}
	
	public static void main(String[] args) {
		int port = 2500;
		int count = 0;
		try{
			ServerSocket socket1 = new ServerSocket(port);
			System.out.println("MultipleSocketServer Initialized");
			int number = 0;
			while (true) {
				System.out.println("connecting to client");
				Socket clientSocket = socket1.accept(); //this is the connection to the client
				System.out.println("connected to client now");
				Runnable runnable = new MultipleSocketServer(clientSocket, ++count);
				Thread thread = new Thread(runnable);
				thread.start();
			}
		}
		catch (Exception e) {}
	}



	public void run() {
		try {
			BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
			InputStreamReader isr = new InputStreamReader(is);
			int character;
			StringBuffer process = new StringBuffer();
			while((character = isr.read()) != 13) {
				process.append((char)character);
			}
			System.out.println(process);
			//need to wait 10 seconds to pretend that we're processing something
			try {
				Thread.sleep(10000);
			}
			catch (Exception e){}
			TimeStamp = new java.util.Date().toString();
			String returnCode = "MultipleSocketServer repsonded at "+ TimeStamp + (char) 13;
			BufferedOutputStream os = new BufferedOutputStream(connection.getOutputStream());
			OutputStreamWriter osw = new OutputStreamWriter(os, "US-ASCII");
			osw.write(returnCode);
			osw.flush();
		}
		catch (Exception e) {
			System.out.println(e);
		}
		finally {
			try {
				connection.close();
			}
			catch (IOException e){}
		}
	}
}


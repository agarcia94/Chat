package client;

import java.net.*;
import java.io.*;

public class ClientHandler implements Runnable{
	private Socket connectionSocket;
	

	public ClientHandler(Socket socket){
		connectionSocket = socket;
	}

	
	//code derived from http://edn.embarcadero.com/article/31995
	public void run(){
		
		//System.out.println("Hello  World!!!");
		
//		try {
//			DataOutputStream stream = new DataOutputStream(connectionSocket.getOutputStream());
//			stream.writeBytes("Connected! Yay!");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		try {
//			BufferedOutputStream bos = new BufferedOutputStream(connectionSocket.getOutputStream());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		//while (true) {

			BufferedReader input = null;
			try {
				input = new BufferedReader
						(new InputStreamReader(connectionSocket.getInputStream()));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			while(true){
				try {
					String line = input.readLine();
					System.out.println(line);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
//			StringBuffer process = new StringBuffer();
//			int character;
//			
//			try {
//				while((character = isr.read()) != 13) {
//					System.out.println("input stream");
//					process.append((char)character);
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			System.out.println("process: " + process);

			//TimeStamp = new java.util.Date().toString();
			//String returnCode = "SingleSocketServer repsonded at "+ TimeStamp + (char) 13;
//			String returnCode = "connected! Yay!";
//			BufferedOutputStream os;
//			try {
//				os = new BufferedOutputStream(connectionSocket.getOutputStream());
//				
//				OutputStreamWriter osw = new OutputStreamWriter(os, "US-ASCII");
//				osw.write(returnCode);
//				osw.flush();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

		//}
	}

}

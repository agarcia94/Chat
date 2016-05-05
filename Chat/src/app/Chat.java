package app;

import java.net.*;
import java.util.HashMap;
import java.io.*;

public class Chat {
	
	private int id;  //differentiate between which IP connection
	private String hostAddress;
	private ServerSocket listenerSocket;
	private Socket clientSocket;
	private int listeningPort;
	private HashMap<String, Integer> addressList;
	
	public Chat(int port){
		listenerSocket = null;
		listeningPort = port;
		hostAddress = "";
		clientSocket = null;
		id = 0;
		addressList = new HashMap<>();
	}
	
	//Display the IP address of local computer
	public String myIP(){
		
		String address = "";
		try {
			address = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		hostAddress = address;
		return hostAddress;
		
	}
	
	//Call this when we use the myport command
	public int getPortNumber(){
		return listeningPort;
	}
	
	public void connect(String ip, int port){
		listeningPort = port;
		
		//MultipleSocketServer serverSocket;
		
		
		//1. Create ServerSocket and bind it to port
		//2. Calling the accept method and that ServerSocket
		//3. Read the contents from the client socket
		
		try {
			listenerSocket = new ServerSocket(listeningPort);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		new Thread(() -> {
			
			while(true){
				
				try {
					clientSocket = listenerSocket.accept();
					
					//Create client object that implements Runnable
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		

	}
	
//	class MultipleSocketServer{
//		int ID;
//	}
	
	public static void main(String[] args){
		int port = Integer.parseInt(args[0]);
	}

}

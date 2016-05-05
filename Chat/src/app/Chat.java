package app;

import java.net.*;
import java.util.HashMap;
import java.io.*;

public class Chat {
	
	private int id;  //differentiate between which IP connection
	private String hostAddress;
	private Socket clientSocket;
	private int listeningPort;
	private HashMap<String, Integer> addressList;
	
	public Chat(int port){
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
		
		MultipleSocketServer serverSocket;
		
		new Thread(() -> {
			
		});
		

	}
	
	class MultipleSocketServer{
		int ID;
	}
	
	public static void main(String[] args){
		int port = Integer.parseInt(args[0]);
	}

}

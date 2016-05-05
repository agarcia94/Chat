package app;

import java.net.*;
import java.util.HashMap;
import java.util.Scanner;
import java.io.*;

import client.Client;

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
	
	//Displays the menu for commands for the user to see. Uses text file
	public static void help() throws FileNotFoundException, IOException{
		try (BufferedReader br = new BufferedReader(new FileReader("Help.txt"))) {
			   String line = null;
			   while ((line = br.readLine()) != null) {
			       System.out.println(line);
			   }
			}	
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
					new Thread(new Client(clientSocket)).start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		

	}
	

	public static void list(){
		//Check if there any connected peers
		//if not, return no peers conected
		//else
		//show System.out.println("ID:               IP Address                     Port Number");
		//for every peer	
	}
	
	/*
	 * Needed??
	public static void mainMenu(){
		
		Scanner in = new Scanner(System.in);
		System.out.println("Please Enter a Command: ");
		String command = in.nextLine();
		String errorMessage;

        switch (msg) {
            case "help":
            	help();
                break;
            case "myip":
            	myip();
                break;
            case "myport":
            	myport();
                break;
            case "connect????"
            	connect(ip,port#);
                break;
            case "list":
            	list();
                break;
            case "terminate????"
            	terminate(ip);
                break;
            case "send???"
            	send(ip,message);
                break;
            case "exit":
            	exit();
            break;
            
            default: errorMessage = "Invalid month";
            	System.out.println(errorMessage);
            	break;
        }
    }
    */
	
//	class MultipleSocketServer{
//		int ID;
//	}

	
	public static void main(String[] args){
		int port = Integer.parseInt(args[0]);
	}

}

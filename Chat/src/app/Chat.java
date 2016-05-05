package app;

import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.*;

import client.ClientHandler;

public class Chat {

	private int id;  //differentiate between which IP connection
	private String hostAddress;
	private ServerSocket listenerSocket;
	private Socket clientSocket;
	private int listeningPort;
	private HashMap<Integer, String> addressList;
	private HashMap<Integer, Socket> socketList;

	public Chat(int port){
		listeningPort = port;
		
		try {
			listenerSocket = new ServerSocket(listeningPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		hostAddress = "";
		clientSocket = null;
		id = 0;
		addressList = new HashMap<>();
		socketList = new HashMap<>();
		System.out.println("Welcome!");
	}

	//Displays the menu for commands for the user to see. Uses text file
	public void help() throws FileNotFoundException, IOException{
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
			System.out.println("Cannot recognize host");
		}

		hostAddress = address;
		return hostAddress;

	}

	//Call this when we use the myport command
	public int getPortNumber(){
		return listeningPort;
	}
	
	//Setup the server socket
	//Wait for any connection requests
	//Read from the client socket once the connection is made
	public void setupListeningSocket(){
		
		System.out.println("Waiting for a client to connect...");
		
		try {
			clientSocket = listenerSocket.accept(); //wait for client to connect
			System.out.println("Reading from client socket");
			
			new Thread(() -> {
				while(true){
					try {
						new Thread(new ClientHandler(clientSocket)).start();
					} catch (Exception e) {
						System.out.println("cannot connect to client");
						//e.printStackTrace();
					}
				}

			});
			
			
			
		} catch (IOException e) {
			System.out.println("cannot create server socket");
			e.printStackTrace();
		}
	}

	//Connect to a client
	//Bind the clientSocket to the server's ip and port
	//This is more client-oriented
	public void connect(String ip, int port){
		
		try {
			clientSocket = new Socket(ip, port); //bind the client to the server's ip and port
			System.out.println("Connecting to server");
		} catch (IOException e1) {
			System.out.println("Cannot connect to server");
			//e1.printStackTrace();
		}
		
		
		//1. Create ServerSocket and bind it to port
		//2. Call the accept method on that ServerSocket
		//3. Read the contents from the client socket

//		new Thread(() -> {
//			while(true){
//				try {
//					clientSocket = listenerSocket.accept(); //wait for incoming connection from client
//					new Thread(new ClientHandler(clientSocket)).start();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					System.out.println("cannot connect to client");
//					//e.printStackTrace();
//				}
//			}
//
//		});


	}

	public void list(){
		//Check if there any connected peers
		if(addressList.isEmpty()){
			System.out.println("No Connected peers to Show");
			return;
		}
		//if not, return no peers conected
		//else
		else{
			System.out.println("ID:               IP Address                     Port Number");

//			for(Map.Entry<Integer, String> entry : addressList.entrySet()){
//
//			}

		}
	
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



	public static void main(String[] args){
		//int port = Integer.parseInt(args[0]); 
		
		int port = 2500;
		Chat chat = new Chat(port);
		
		Scanner input = new Scanner(System.in);
		System.out.print("Please provide a command: ");
		String command = input.nextLine();
		
		chat.setupListeningSocket();
		
		
		if(command.equals("help")){
			try {
				chat.help();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(command.contains("connect")){
			String[] values = command.split(" ");
			String destIP = values[1];
			int destPort = Integer.parseInt(values[2]);
		}
		else if(command.equals("list")){
			chat.list();
		}
		else if(command.equals("myip")){
			System.out.println(chat.myIP());
		}
		
//		do{
//			System.out.print("Welcome! Please enter a command: ");
//			command = input.next();
//		}while(!command.equalsIgnoreCase("exit"));


	}

}

package app;

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.*;

import client.Client;
//import client.ClientHandler;


/**
 * Chat application where peers can communicate with each other
 * @author Andrew Garcia
 * @author Claudia Seidel
 * @author Alex Perez
 * @version 1.0
 * @since 5/7/2016
 */
public class Chat {
	private int maxNumPeers;

	private int id;  //differentiate between which IP connection
	private String hostAddress;
	private ServerSocket listenerSocket;
	private Socket clientSocket;
	private int listeningPort;
	private Client client;
	//private HashMap<Integer, String> addressList;
	private HashMap<Integer, Client> clientList;
	private HashMap<Client, DataOutputStream> clientStreamList;
	private HashMap<Client, Socket> clientSocketMap;
	
	/**
	 * Chat constructor
	 * @param port the port the server socket will be bound to
	 */
	public Chat(int port){
		listeningPort = port;
		maxNumPeers = 0;

		try {
			listenerSocket = new ServerSocket(listeningPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		hostAddress = "";
		clientSocket = null;
		id = 0;
		//addressList = new HashMap<>();
		clientList = new HashMap<>();
		client = null;
		clientStreamList = new HashMap<>();
		clientSocketMap = new HashMap<>();
		System.out.println("Welcome!");
	}

	
	/**
	 * Displays the menu for commands for the user to see. Uses text file
	 * @throws IOException Thrown if file not found or error reading the file
	 */
	public void help() throws IOException{
		try (BufferedReader br = new BufferedReader(new FileReader("Help.txt"))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		}	
	}
	
	/**
	 * Display the IP address of local computer
	 * @return IP address of local computer as a string
	 */
	public String myIP(){

		String address = "";
		try {
			address = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			System.out.println("Cannot recognize host");
		}

		hostAddress = address;
		return hostAddress;

	}

	
	/**
	 * Get the listening port number for the host
	 * @return listening port for the host
	 */
	public int getPortNumber(){
		return listeningPort;
	}

	
	/**
	 * Setup the server socket. This method waits for any connection requests from clients.
	 * Once a connection is established, the IP address and the listening port number for 
	 * the client is extracted.
	 */
	public void setupListeningSocket(){

		System.out.println("Waiting for a client to connect...");

		//System.out.println("Reading from client socket");
		
		if(clientList.size() < 3){
			new Thread(() -> {
				while(true){
					try {
						clientSocket = listenerSocket.accept(); //wait for client to connect
						System.out.println("Connected!");
						new Thread(new ClientHandler(clientSocket)).start();
					} catch (Exception e) {
						System.out.println("Cannot connect to client!");
					}
				}

			}).start();
		}
		else{
			System.out.println("You have 3 peers connected to you. You can have no more than 3.");
		}
	}
	

	

	public void exit() {
		for (int i = 0; i < clientList.size(); i++) {
			terminate(i);
		}
		System.out.println("Program now exiting.");
		System.exit(0);
	}

	
	/**
	 * Allows client to connect to server/host
	 * @param ip host IP address to bind client socket to
	 * @param port listening port number for host
	 */
	public void connect(String ip, int port){
		
		if(maxNumPeers < 3){
			System.out.println("Connecting...");
			try {
				clientSocket = new Socket(ip, port); //bind the client to the server's ip and port
				maxNumPeers++;
				System.out.println("Connected!");
			} catch (IOException e1) {
				System.out.println("Cannot connect to server!");
				//e1.printStackTrace();
			}
			
			int tempID = id++;

			Client client  = new Client(tempID, ip, port);
			clientList.put(tempID, client);
			clientSocketMap.put(client, clientSocket);
			try {
				clientStreamList.put(client, new DataOutputStream(clientSocket.getOutputStream()));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			DataOutputStream response;
			try {
				response = new DataOutputStream(clientSocket.getOutputStream());
				response.writeBytes(myIP() + " " + listeningPort + "\r \n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			System.out.println("Cannot connect to more than 3 peers");
		}
		




		//1. Create ServerSocket and bind it to port
		//2. Call the accept method on that ServerSocket
		//3. Read the contents from the client socket
	}

	
	/**
	 * Terminate connection with specific client
	 * @param id ID of client to disconnect from
	 */
	public void terminate(int id) {
		
		Client c;
		Socket s;
		try {
			c = clientList.get(id); // get the Client at ip 
			s = clientSocketMap.get(c);
			s.shutdownInput();
			s.shutdownOutput();
			s.close();
			clientSocketMap.remove(s);
			clientList.remove(id);
			System.out.println("Socket at " + c.getAddress() + " and port " + c.getPort() + " was closed!");
		} catch (Exception e1) {
			System.out.println("Cannot find socket at that ID!");
			//e1.printStackTrace();
		}
	}

	/** List the peers connected to this host*/
	public void list(){
		//Check if there any connected peers
		if(clientList.isEmpty()){
			System.out.println("No peers are currently connected.");
			return;
		}
		//if not, return no peers connected
		//else
		else{
			System.out.println("ID:\tIP Address\tPort Number");

			for(Map.Entry<Integer, Client> entry : clientList.entrySet()){
				int id = entry.getKey();
				Client value = entry.getValue();

				System.out.println(id + "\t" + value.getAddress() + "\t" + value.getPort());
			}

		}

	}
	
	/**
	 * Send a message to a peer of your choice
	 * @param id ID of client to send message to 
	 * @param message The message that will be sent to the other peer
	 */
	public void send(int id, String message){
		Client client = clientList.get(id);
		DataOutputStream clientStream = clientStreamList.get(client);
		try {
			clientStream.writeBytes("Message received from " + myIP());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	/**
	 * Helper class designed to extract the IP address and listening port number from the client socket
	 */
	private class ClientHandler implements Runnable{
		private Socket connectionSocket;


		public ClientHandler(Socket socket){
			connectionSocket = socket;
		}


		//code derived from http://edn.embarcadero.com/article/31995
		public void run(){

			BufferedReader input = null;
			try {
				input = new BufferedReader
						(new InputStreamReader(connectionSocket.getInputStream()));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


			try {
				String line = input.readLine();
				System.out.println(line);
				String[] clientInfo = line.split(" ");
				ArrayList<String> clientInfoList = new ArrayList<String>(Arrays.asList(clientInfo));
				System.out.println("Client ArrayList size: " + clientInfoList.size());
				
				if(clientInfo[0].contains("Message")){
					System.out.println("I got a message");
				}
				else{
					String ipAddress = clientInfo[0];
					System.out.println("Client IP: " + ipAddress);

					int clientListenerPort = Integer.parseInt(clientInfo[1]);
					System.out.println("Client port number: " + clientListenerPort);
				}



				//int tempID = id++;

//				Client client  = new Client(tempID, ipAddress, clientListenerPort);
//				clientList.put(tempID, client);
//				clientSocketMap.put(client, connectionSocket);
//				clientStreamList.put(client, new DataOutputStream(connectionSocket.getOutputStream()));

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}

	}



	public static void main(String[] args){

		Scanner input = new Scanner(System.in);
		System.out.print("Port: ");
		int port = input.nextInt();

		Chat chat = new Chat(port);
		String command = "";
		chat.setupListeningSocket();
		
		do{

			System.out.print("Provide a command: ");
			Scanner secondInput = new Scanner(System.in);
			command = secondInput.nextLine();

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
				chat.connect(destIP, destPort);
			}
			else if(command.equals("list")){
				chat.list();
			}
			else if(command.equals("myip")){
				System.out.println(chat.myIP());
			}
			else if(command.equals("myport")){
				System.out.println(chat.getPortNumber());
			} else if (command.contains("terminate")) {
				String[] values = command.split(" ");
				String id = values[1];
				int termId = Integer.parseInt(id);
				System.out.println("Terminating...");
				chat.terminate(termId);
			}
			else if(command.contains("send")){
				String[] values = command.split(" ");
				int destID = Integer.parseInt(values[1]);
				String message = values[2];
				chat.send(destID, message);
			}



		}while(!command.equals("exit"));


		if (command.equals("exit")) {
			chat.exit();
		}

		
	}
	
	

}

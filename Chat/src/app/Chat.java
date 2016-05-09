package app;

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.*;

//import client.Client;
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
	public static int maxNumPeers;

	private int id;  //differentiate between which IP connection
	private String hostAddress;
	private ServerSocket listenerSocket;
	private Socket clientSocket;
	private int listeningPort;
	//private Client client;
	//private HashMap<Integer, String> addressList;
	public static HashMap<Integer, Client> clientList;
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
		//client = null;
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



		new Thread(() -> {
			while(true){
				try {
					//						System.out.println("amount of clients connected to me: " + clientList.size());
					//						if(clientList.size() >= 3){
					//							System.out.println("Cannot be connected to more than 3 peers");
					//						}

					clientSocket = listenerSocket.accept(); //wait for client to connect
					System.out.println("\nThis host connected to a client!");
					new Thread(new ClientHandler(clientSocket)).start();
				} catch (Exception e) {
					System.out.println("Cannot connect to client!");
				}
			}

		}).start();

	}




	public void exit() {
		for (int i = 0; i < clientList.size(); i++) {
			if (clientList.containsKey(i)) terminate(i);
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
				response.writeBytes("c" + " " + myIP() + " " + listeningPort + "\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			System.out.println("Cannot connect to more than 3 peers!");
		}
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

			DataOutputStream terminateNotification = new DataOutputStream(s.getOutputStream());
			terminateNotification.writeBytes("t" + " " + "Socket at " + this.myIP() + " and port " + this.getPortNumber() + 
					" is terminating.");

			//terminateNotification.writeBytes("ts" + " termination successful");

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
	public void list() {
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
	 * @throws IOException If error occurs while accessing the output stream from a client
	 */
	public void send(int id, String message) throws IOException{
		Client client = clientList.get(id);
		Socket communicationSocket = clientSocketMap.get(client);
		DataOutputStream communicationStream = new DataOutputStream(communicationSocket.getOutputStream());


		//new Thread(new ClientHandler(communicationSocket)).start();

		if (message.length() <= 100) {
			communicationStream.writeBytes("m" + " " + "Message from:" + " " + myIP() + "\r\n");
			communicationStream.writeBytes("p" + " " + "Sender's port: " + client.getPort() + "\r\n");
			communicationStream.writeBytes("s" + " " + "Message: " + message + "\r\n");
		} else {
			System.out.println("Your message must be under 100 characters, including spaces!");
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

				while(true){
					
					String line = "";
					try{
						line = input.readLine();
						System.out.println(line);
					}catch(NullPointerException e){
						return;
					}


					String[] clientInfo = null;
					try{
						clientInfo = line.split(" ");
					}catch(NullPointerException o){
						return;
					}

					ArrayList<String> clientInfoList = new ArrayList<String>(Arrays.asList(clientInfo));
					//System.out.println("Client ArrayList size: " + clientInfoList.size());

					if(line.startsWith("c")){

						if(clientList.size() < 3){
							String clientAddress = clientInfo[1];
							int clientListeningPort = Integer.parseInt(clientInfo[2]);

							Socket connectionSocket = new Socket(clientAddress, clientListeningPort);

							int tempID = id++;
							Client client = new Client(tempID, clientAddress, clientListeningPort);
							clientList.put(client.getId(), client);
							clientSocketMap.put(client, connectionSocket);
							
							if(clientList.size() > 3){
								DataOutputStream response = new DataOutputStream(connectionSocket.getOutputStream());
								response.writeBytes("d" + " " + "Cannot connect at this moment. Too many peers. Closing"
										+ "connection" + "\r\n");
								terminate(tempID);
							}
							else{
								DataOutputStream response = new DataOutputStream(connectionSocket.getOutputStream());
								response.writeBytes("r" + " " + "Connect message received!" + "\r\n");

								System.out.println("Connection established to other peer!");
							}

						}
						/*
						else{
							DataOutputStream response = new DataOutputStream(connectionSocket.getOutputStream());
							response.writeBytes("d" + " " + "Cannot connect at this moment. Too many peers.Closing"
									+ "connection" + "\r\n");
							connectionSocket.close();
						}
						*/

					}
					else if(line.startsWith("r")){
						StringBuffer buff= new StringBuffer();
						
						for(int i =1; i < clientInfo.length; i++){
							buff.append(clientInfo[i] + " ");
						}
						
						System.out.println("Response: " + buff.toString());
					}
					else if(line.startsWith("m")){
						StringBuffer buff= new StringBuffer();
						
						for(int i =1; i < clientInfo.length; i++){
							buff.append(clientInfo[i] + " ");
						}
						
						System.out.println(buff.toString());
					}
					else if(line.startsWith("t")){
						System.out.println(line);
						String address = clientInfo[3];
						int port = Integer.parseInt(clientInfo[6]);

						int removeID = 0;
						Client clientToRemove = null;

						for(Map.Entry<Integer, Client> entry : clientList.entrySet()){
							int id = entry.getKey();
							Client client = entry.getValue();

							if(client.getAddress().equals(address) &&
									client.getPort() == port){
								removeID = id;
								clientToRemove = client;
								break;
							}
						}


						Socket closingSocket = clientSocketMap.get(clientToRemove);
						clientList.remove(removeID);
						clientSocketMap.remove(clientToRemove);
						System.out.println("Connection successfully terminated.");
					} else if(line.startsWith("p")){
						StringBuffer buff= new StringBuffer();
						
						for(int i =1; i < clientInfo.length; i++){
							buff.append(clientInfo[i] + " ");
						}
						
						if (buff.toString() != null && !buff.toString().equals("null")) System.out.println(buff.toString());
					} else if(line.startsWith("s")){
						StringBuffer buff= new StringBuffer();
						
						for(int i =1; i < clientInfo.length; i++){
							buff.append(clientInfo[i] + " ");
						}
						
						if (buff.toString() != null && !buff.toString().equals("null")) System.out.println(buff.toString());
					}
					else if(line.startsWith("d")){
						StringBuffer buff= new StringBuffer();
						
						for(int i =1; i < clientInfo.length; i++){
							buff.append(clientInfo[i] + " ");
						}
						if (buff.toString() != null && !buff.toString().equals("null")) System.out.println(buff.toString());
					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	private class Client {
		private int id;
		private String address;
		private int port;
		
		public Client(int ID){
			this.id = ID;
			address = "";
			port = 0;
		}
		
		public Client(int ID, String ipAddress, int destPort){
			this.id= ID;
			address = ipAddress;
			port = destPort;
		}

		public int getId() {
			return id;
		}
		
		public void setId(int id) {
			this.id = id;
		}

		public String getAddress() {
			return address;
		}
		
		public void setAddress(String address) {
			this.address = address;
		}

		public int getPort() {
			return port;
		}
		
		public void setPort(int port){
			this.port = port;
		}
	}




	public static void main(String[] args){
		boolean start = false;
		Scanner input = new Scanner(System.in);
		String init = "";
		int port = 0;
		
		// loop that locks a user out until the proper starting command is entered
		do {
			System.out.print("> ");
			init = input.nextLine();
			if (init.equals("./chat")) {
				String[] values = init.split(" ");
				try { 
			        port = Integer.parseInt(args[1]); 
			        start = true;
			    } catch(NumberFormatException e) { 
			        System.out.println("Please enter command \"./chat <port>\" to begin.");
			    }
				catch (IndexOutOfBoundsException e){
					System.out.println("Port Number was not inputted, Enter \"./chat <port>\"");
				}
			} else {
				System.out.println("Please enter command \"./chat <port>\" to begin.");
			}
			//port = input.nextInt();
		} while(!start);

		Chat chat = new Chat(port);
		String command = "";
		chat.setupListeningSocket();
		
		do{
			System.out.print("\nProvide a command: ");
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
				try {
					String destIP = values[1];
					int destPort = Integer.parseInt(values[2]);
					chat.connect(destIP, destPort);
				} catch (Exception e) {
					System.out.println("Please enter your command in \"connect <IP> <port>\" format.\n"
							+ "(Make sure that your IP and port numbers are valid!)");
				}
			}
			else if(command.equals("list")){
				chat.list();
			}
			else if(command.equals("myip")){
				
				System.out.println(chat.myIP());
				
			} else if(command.equals("myport")){
				
				System.out.println(chat.getPortNumber());
				
			} else if (command.contains("terminate")) {
				
				String[] values = command.split(" ");
				try {
					String id = values[1];
					int termId = Integer.parseInt(id);
					System.out.println("Terminating...");
					chat.terminate(termId);
				} catch (Exception e) {
					System.out.println("Please enter your command in \"terminate <Client ID>\" format.\n" 
							+ "(Make sure your client ID exists before attempting to send!)");
				}
				
			} else if(command.contains("send")){
				// attempt to send the message listed, otherwise inform user of correct format of command
				try {
					String[] values = command.split(" ");
					int destID = Integer.parseInt(values[1]);
					String message = "";
					for (int i = 2; i < values.length; i++) {
						message += values[i] + " ";
					}
					try {
						chat.send(destID, message);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				//String message = values[2];
				} catch (Exception e) {
					System.out.println("Please enter your command in \"send <Client ID> <message>\" format.\n"
							+ "(Make sure your client ID exists before attempting to send!)");
				}
			}
		}while(!command.equals("exit"));

		if (command.equals("exit")) {
			chat.exit();
		}
	}
}

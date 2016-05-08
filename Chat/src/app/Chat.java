package app;

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.*;

import client.Client;
import client.ClientHandler;

public class Chat {

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
		//addressList = new HashMap<>();
		clientList = new HashMap<>();
		client = null;
		clientStreamList = new HashMap<>();
		clientSocketMap = new HashMap<>();
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
			address = Inet4Address.getLocalHost().getHostAddress();
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

		//System.out.println("Reading from client socket");

		new Thread(() -> {
			while(true){
				try {
					clientSocket = listenerSocket.accept(); //wait for client to connect
					//int newID = id++;

					//					Client client = new Client(newID, myIP(), getPortNumber());
					//					clientList.put(newID, client);
					System.out.println("using client thread");
					new Thread(new ClientHandler(clientSocket)).start();
				} catch (Exception e) {
					System.out.println("cannot connect to client");
					//e.printStackTrace();
				}
			}

		}).start();
	}

	//Connect to a client
	//Bind the clientSocket to the server's ip and port
	//This is more client-oriented
	public void connect(String ip, int port){
		System.out.println("connecting");
		try {
			clientSocket = new Socket(ip, port); //bind the client to the server's ip and port
			System.out.println("Connecting to server");
		} catch (IOException e1) {
			System.out.println("Cannot connect to server");
			//e1.printStackTrace();
		}

		DataOutputStream response;
		try {
			response = new DataOutputStream(clientSocket.getOutputStream());
			response.writeBytes(myIP() + " " + listeningPort + "\r \n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		//1. Create ServerSocket and bind it to port
		//2. Call the accept method on that ServerSocket
		//3. Read the contents from the client socket
	}
	
	public void terminate(int id) {
		System.out.println("Terminating.....");
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
			System.out.println("Socket closed!");
		} catch (Exception e1) {
			System.out.println("Cannot find socket at that ID!");
			//e1.printStackTrace();
		}
	}

	public void list(){
		//Check if there any connected peers
		if(clientList.isEmpty()){
			System.out.println("No Connected peers to Show");
			return;
		}
		//if not, return no peers conected
		//else
		else{
			System.out.println("ID:  IP Address       Port Number");

			for(Map.Entry<Integer, Client> entry : clientList.entrySet()){
				int id = entry.getKey();
				Client value = entry.getValue();

				System.out.println(id + "   " + value.getAddress() + "         " + value.getPort());
			}

		}

	}

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
				System.out.println("ArrayList client size: " + clientInfoList.size());

				
				String ipAddress = clientInfo[0];
				System.out.println("client IP: " + ipAddress);

				int clientListenerPort = Integer.parseInt(clientInfo[1]);
				System.out.println("client listener port: " + clientListenerPort);

				int tempID = id++;

				Client client  = new Client(tempID, ipAddress, clientListenerPort);
				clientList.put(tempID, client);
				clientSocketMap.put(client, connectionSocket);
				clientStreamList.put(client, new DataOutputStream(connectionSocket.getOutputStream()));

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


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
			}


		}while(!command.equals("exit"));


	}
	
	

}

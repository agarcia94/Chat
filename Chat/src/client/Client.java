package client;

import java.net.*;
import java.io.*;

public class Client implements Runnable{
	private Socket connectionSocket;
	
	public Client(Socket socket){
		connectionSocket = socket;
	}
	
	public void run(){
		
	}

}

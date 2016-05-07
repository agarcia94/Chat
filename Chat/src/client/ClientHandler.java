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

	}

}

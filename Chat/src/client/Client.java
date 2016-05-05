package client;

import java.net.*;
import java.io.*;

public class Client implements Runnable{
	private Socket connectionSocket;
	
	public Client(Socket socket){
		connectionSocket = socket;
	}
	
	public void run(){
		
		
        try (
                PrintWriter out = new PrintWriter(connectionSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(connectionSocket.getInputStream()));
            ) {
                BufferedReader stdIn =
                    new BufferedReader(new InputStreamReader(System.in));
                String fromServer;
                String fromUser;

                while ((fromServer = in.readLine()) != null) {
                    System.out.println("Server: " + fromServer);
                    if (fromServer.equals("Bye."))
                        break;
                    
                    fromUser = stdIn.readLine();
                    if (fromUser != null) {
                        System.out.println("Client: " + fromUser);
                        out.println(fromUser);
                    }
                }
            } catch (UnknownHostException e) {
//                System.err.println("Don't know about host ");
//                System.exit(1);
            	e.printStackTrace();
            } catch (IOException e) {
                //System.err.println("Couldn't get I/O for the connection to ");
                //System.exit(1);
                e.printStackTrace();
            }
	}

}

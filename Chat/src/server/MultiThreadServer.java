package server;


/*Acquired from http://www.tutorialspoint.com/javaexamples/net_multisoc.htm*/
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadServer implements Runnable{
	private Socket csocket;
	private boolean isDone;
	MultiThreadServer(Socket csocket) {
		this.csocket = csocket;
		isDone = false;
	}

	public static void main(String args[]) 
			throws Exception {
		ServerSocket ssock = new ServerSocket(1024);
		System.out.println("Listening");
		while (true) {
			Socket sock = ssock.accept();
			System.out.println("Connected");
			new Thread(new MultiThreadServer(sock)).start();
		}
	}
	
	public void run() {
		try {
			PrintStream pstream = new PrintStream
					(csocket.getOutputStream());
			for (int i = 100; i >= 0; i--) {
				pstream.println(i + 
						" bottles of beer on the wall");
			}
			pstream.close();
			csocket.close();
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}
}


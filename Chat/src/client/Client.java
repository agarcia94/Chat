package client;

public class Client {
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

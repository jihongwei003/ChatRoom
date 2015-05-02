package Client;

import java.net.Socket;

import Client.ResCollection.ResponseCollectionManager;

public class ClientAdmin {

	private Socket socket;
	private String userName;
	
	public ClientAdmin(Socket socket, String userName){
		this.socket = socket;
		this.userName = userName;
	}
	
	//主要负责两条线程的依赖注入
	public void runAdmin(){		
		ResponseCollectionManager resCollec = new ResponseCollectionManager();
		
		ClientReaderThread readerThread = new ClientReaderThread(socket);
		readerThread.setResponseCollectionManager(resCollec);
		readerThread.start();
		
		ResponseManager manager = new ResponseManager(socket, userName);
		manager.setResponseCollectionManager(resCollec);
		manager.runManager();
	
	}
}

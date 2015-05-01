package Server;

import java.io.IOException;
import java.io.PrintWriter;

import Client.Director;
import Client.SocketWriterBuilder;

public class DisplayFriendsHandler extends RequestHandler {

	public DisplayFriendsHandler(UserDB users, String name) {
		super(users, name);
	}

	@Override
	public void handleRequest() {
		//指导者
		Director director = new Director(new SocketWriterBuilder(
				users.getSocket(name)));
		try {
			//使用指导者生成writer
			PrintWriter writer = (PrintWriter) director.construct();
			
			writer.println(users.getUsersList(name));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

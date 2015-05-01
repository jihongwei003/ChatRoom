package Server;

import java.io.IOException;
//import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import net.sf.json.JSONObject;
import Client.Director;
import Client.PrintWriterBuilder;

public class DisplayFriendsHandler extends RequestHandler {

	public DisplayFriendsHandler(UserDB users, String name) {
		super(users, name);
	}

	@Override
	public void handleRequest() {
		//指导者
		Director director = new Director(new PrintWriterBuilder(
				users.getSocket(name)));
		try {
			//使用指导者生成writer
			PrintWriter writer = (PrintWriter) director.construct();
			
			//将hashMap转化成string
			JSONObject json = JSONObject.fromObject(users.getSocketsMap());
			String jsonString = JsonTrans.buildJson("userMap", json);
			
			writer.println(jsonString.toString());
			
			System.out.println(name + "请求好友列表");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

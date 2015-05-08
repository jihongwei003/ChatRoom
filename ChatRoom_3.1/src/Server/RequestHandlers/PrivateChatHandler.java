package Server.RequestHandlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import net.sf.json.JSONObject;
import Server.ResTrans;
//import Server.UserCollection.UserDB;
import Server.UserCollection.UserList;
import Tools.JsonTrans;
import Tools.Bulider.Director;
import Tools.Bulider.PrintWriterBuilder;

public class PrivateChatHandler extends RequestHandler {

	public PrivateChatHandler(UserList users) {
		super(users);
	}

	@Override
	public void handleRequest() {
		JSONObject json = (JSONObject) JsonTrans.parseJson(super.requestMsg,
				"msg");

		String name = json.getString("publisher");// 解析出消息的发送者（真实名字）
		String friendName = json.getString("receiver");// 解析出消息的接受者（真实名字）
		String words = json.getString("words");

		System.out.println("私聊消息");
		String str = name + " 对 " + friendName + " 说：" + words;
		System.out.println(str);

		//从真实名字找到连接名字，再从连接名字找到socket
		String friLinkName = users.getOnlineUsers().get(friendName);
		
		Socket friendSocket = users.getSocket(friLinkName);
		try {
			if (friendSocket == null) {
				System.out.println("没有找到friendSocket！");
			} else {
				// 好友的writer
				Director director = new Director(new PrintWriterBuilder(
						friendSocket));
				PrintWriter writerF = (PrintWriter) director.construct();

				// 将字符串打包成需要客户端解析的形式
				ResTrans trans = new ResTrans();
				trans.setPublisher(name);
				trans.setMsgNum("2");
				trans.setContent(words);

				String result = trans.getResult();

				// 添加服务器的包头
				String output = JsonTrans.buildJson("res", result);

				writerF.println(output);
				writerF.flush();

				System.out.println("转发的私聊消息：" + output);

			}// end of else

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

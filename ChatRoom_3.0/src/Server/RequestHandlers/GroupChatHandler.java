package Server.RequestHandlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;

import net.sf.json.JSONObject;
import Server.ResTrans;
import Server.UserCollection.UserList;
import Tools.JsonTrans;
import Tools.Bulider.Director;
import Tools.Bulider.PrintWriterBuilder;

public class GroupChatHandler extends RequestHandler {

	public GroupChatHandler(UserList users) {
		super(users);
	}

	@Override
	public void handleRequest() {
		JSONObject json = (JSONObject) JsonTrans.parseJson(super.requestMsg,
				"msg");

		String name = json.getString("publisher");// 解析出消息的发送者
		String words = json.getString("words");// 解析出消息的内容

		System.out.println("群聊消息");
		System.out.println(name + "说：" + words);

		try {
			// 群聊时，服务器负责将消息转发给所有用户
			Iterator<String> iter = users.getSocketsMap().keySet().iterator();
			Director director;
			PrintWriter writerF;
			while (iter.hasNext()) {
				String key = iter.next();

				//不向自己发送消息
				if (name.equals(key))
					continue;

				// 创建目标客户端socket的引用
				Socket friendSocket = users.getSocket(key);

				director = new Director(new PrintWriterBuilder(friendSocket));
				writerF = (PrintWriter) director.construct();

				// 将字符串打包成需要客户端解析的形式
				ResTrans trans = new ResTrans();
				trans.setPublisher(name);
				trans.setMsgNum("3");
				trans.setContent(words);
				String result = trans.getResult();

				// 添加服务器的包头
				String output = JsonTrans.buildJson("res", result);

				writerF.println(output);
				writerF.flush();

				System.out.println("转发的群聊消息：" + output);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

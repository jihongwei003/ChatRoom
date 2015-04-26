package Server.RequestHandlers;

import java.io.IOException;
//import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import Server.ResTrans;
import Server.UserCollection.UserDB;
import Tools.JsonTrans;
import Tools.Bulider.Director;
import Tools.Bulider.PrintWriterBuilder;
import net.sf.json.JSONObject;

public class DisplayFriendsHandler extends RequestHandler {

	public DisplayFriendsHandler(UserDB users) {
		super(users);
	}

	@Override
	public void handleRequest() {
		JSONObject json = (JSONObject) JsonTrans.parseJson(super.requestMsg,
				"msg");
		String name = json.getString("publisher");// 解析出消息的发送者

		System.out.println(name + "请求好友列表");

		// 指导者
		Director director = new Director(new PrintWriterBuilder(
				users.getSocket(name)));
		try {
			// 使用指导者生成writer
			PrintWriter writer = (PrintWriter) director.construct();

			// 将hashMap转化成string作为content
			JSONObject json1 = JSONObject.fromObject(users.getSocketsMap());
			String userMapStr = JsonTrans.buildJson("userMap", json1);

			// 将字符串打包成需要客户端解析的形式
			ResTrans trans = new ResTrans();
			trans.setMsgNum("1");
			trans.setContent(userMapStr);

			String result = trans.getResult();

			// 添加服务器的包头
			String output = JsonTrans.buildJson("res", result);

			writer.println(output);

			System.out.println("回应Client的消息为：" + output);

			System.out.println("已向" + name + "发送好友列表");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

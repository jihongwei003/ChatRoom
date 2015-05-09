package Server.RequestHandlers;

import java.io.IOException;
import java.io.PrintWriter;

import net.sf.json.JSONObject;
import Server.ResTrans;
import Server.UserCollection.UserList;
import Tools.JsonTrans;
import Tools.Bulider.Director;
import Tools.Bulider.PrintWriterBuilder;

public class RegistHandler extends RequestHandler{

	public RegistHandler(UserList users) {
		super(users);
	}

	@Override
	public void handleRequest() {
		JSONObject json = (JSONObject) JsonTrans.parseJson(super.requestMsg,
				"msg");
		String name = json.getString("publisher");// 解析出消息的发送者（临时名字）
		String formalName = json.getString("words");
		
		System.out.println(name + "注册");

		// 指导者
		Director director = new Director(new PrintWriterBuilder(
				users.getSocket(name)));
		try {
			// 使用指导者生成writer
			PrintWriter writer = (PrintWriter) director.construct();

			String content = null;
			if (users.isRegister(formalName))
				content = "false";
			else {
				content = "true"; // 说明可以注册
				users.registUser(formalName);
			}
			
			// 将字符串打包成需要客户端解析的形式
			ResTrans trans = new ResTrans();
			trans.setMsgNum("5");
			trans.setContent(content);
			String result = trans.getResult();

			// 添加服务器的包头
			String output = JsonTrans.buildJson("res", result);

			writer.println(output);

			System.out.println("回应Client的消息为：" + output);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}

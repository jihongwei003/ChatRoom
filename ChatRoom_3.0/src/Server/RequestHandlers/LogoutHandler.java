package Server.RequestHandlers;

import net.sf.json.JSONObject;
//import Server.UserCollection.UserDB;
import Server.UserCollection.UserList;
import Tools.JsonTrans;

public class LogoutHandler extends RequestHandler {

	public LogoutHandler(UserList users) {
		super(users);
	}

	@Override
	public void handleRequest() {
		JSONObject json = (JSONObject) JsonTrans.parseJson(super.requestMsg, "msg");
		String name = json.getString("publisher");// 解析出消息的发送者
		
		// 用户退出，将用户信息从HashMap中删除
		users.deleteUser(name);
		System.out.println(name + "成功退出系统");
	}
}

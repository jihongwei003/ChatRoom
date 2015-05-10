package Server.RequestHandlers;

import java.io.PrintWriter;
import java.net.Socket;

import net.sf.json.JSONObject;
import Server.ResTrans;
import Server.UserCollection.UserList;
import Tools.JsonTrans;
import Tools.MapKey;
import Tools.Bulider.Director;
import Tools.Bulider.PrintWriterBuilder;

public class StopSoundChatHandler extends RequestHandler {

	private SoundChatHandler soundChatHandler;

	public void setSoundChatHandler(SoundChatHandler soundChatHandler) {
		this.soundChatHandler = soundChatHandler;
	}

	public StopSoundChatHandler(UserList users) {
		super(users);
	}

	@Override
	public void handleRequest() {
		JSONObject json = (JSONObject) JsonTrans.parseJson(super.requestMsg,
				"msg");
		String name = json.getString("publisher");// 解析出消息的发送者
		String friendName = json.getString("receiver");
		String flag = json.getString("words"); // 取消还是关闭

		// user的连接名
		String linkName = users.getOnlineUsers().get(name);
		Socket socket = users.getSocket(linkName);

		// friend的连接名
		String linkFriName = users.getOnlineUsers().get(friendName);
		Socket friendSocket = users.getSocketsMap().get(linkFriName);

		System.out.println(name + " 与 " + friendName + " 中止语音聊天");

		try {
			/** 通知发起者停止语音聊天 */
			Director director = new Director(new PrintWriterBuilder(socket));
			PrintWriter writerF = (PrintWriter) director.construct();

			// 将字符串打包成需要客户端解析的形式
			ResTrans trans = new ResTrans();
			trans.setPublisher(friendName);
			trans.setMsgNum("11");
			if (flag.equals("cancel"))
				trans.setContent(flag);
			else
				trans.setContent("close");
			String result = trans.getResult();
			// 添加服务器的包头
			String output = JsonTrans.buildJson("res", result);

			writerF.println(output);
			writerF.flush();

			/** 通知接收者停止语音聊天 */
			director = new Director(new PrintWriterBuilder(friendSocket));
			writerF = (PrintWriter) director.construct();

			// 将字符串打包成需要客户端解析的形式
			trans = new ResTrans();
			trans.setPublisher(name);
			trans.setMsgNum("11");
			if (flag.equals("cancel"))
				trans.setContent(flag);
			else
				trans.setContent("close");
			result = trans.getResult();
			// 添加服务器的包头
			output = JsonTrans.buildJson("res", result);

			writerF.println(output);
			writerF.flush();

			Thread.sleep(1000);
			// 关闭服务器语音转发线程
			soundChatHandler.stopTransProcess(new MapKey(name, friendName));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

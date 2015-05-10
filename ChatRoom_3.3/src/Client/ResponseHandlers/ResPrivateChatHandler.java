package Client.ResponseHandlers;

import java.net.Socket;

import Client.Pages.ChatBox;
import Tools.JsonTrans;
import net.sf.json.JSONObject;

public class ResPrivateChatHandler extends ResponseHandler{

	public ResPrivateChatHandler(Socket socket) {
		super(socket);
	}

	@Override
	public void handleResponse() {
		System.out.println("收到私聊消息");
		
		//解析msgQ中的消息，分发到不同的chatBox中
		JSONObject json = (JSONObject) JsonTrans.parseJson(super.responseMsg,
				"res");
		String friName = json.getString("publisher");
		String content = json.getString("content");

		// 在map中找到chatBox
		ChatBox chatbox = ChatBox.getInstance(socket, SetNameHandler.getRealName(), friName);
		
		//将消息传入chatBox
		chatbox.getMsgQueue().offer(content);		
	}

}

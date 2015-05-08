package Client.ResponseHandlers;

import java.net.Socket;

import Client.Pages.GroupChatBox;

public class ResGroupChatHandler extends ResponseHandler {

	public ResGroupChatHandler(Socket socket) {
		super(socket);
	}

	@Override
	public void handleResponse() {
		// 得到实例
		GroupChatBox chatbox = GroupChatBox.getInstance(socket,
				SetNameHandler.getRealName());

		// 将消息传入chatBox
		chatbox.getMsgQueue().offer(super.responseMsg);

	}

}

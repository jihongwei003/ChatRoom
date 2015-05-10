package Client.ResponseHandlers;

import java.net.Socket;

import net.sf.json.JSONObject;
import Tools.JsonTrans;
import Client.Pages.SoundChatBox;

public class ResStopSoundChatHandler extends ResponseHandler {

	private ResSoundChatHandler resSoundChatHandler;

	public void setResSoundChatHandler(ResSoundChatHandler resSoundChatHandler) {
		this.resSoundChatHandler = resSoundChatHandler;
	}

	public ResStopSoundChatHandler(Socket socket) {
		super(socket);
	}

	@Override
	public void handleResponse() {
		JSONObject json = (JSONObject) JsonTrans.parseJson(super.responseMsg,
				"res");
		String friendName = json.getString("publisher");
		String flag = json.getString("content");

		if (flag.equals("cancel")) {
			SoundChatBox box = SoundChatBox.getInstance(socket,
					SetNameHandler.getRealName(), friendName);
			box.jbtOK.setEnabled(true);
			
			System.out.println("‘›Õ£”Ô“Ù¡ƒÃÏ");
		} else {
			System.out.println("Õ£÷π”Ô“Ù¡ƒÃÏ");
		}
		resSoundChatHandler.stopSoundChatThreads(friendName);
	}

}

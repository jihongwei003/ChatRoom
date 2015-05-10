package Server.UserCollection;

import java.net.Socket;
import java.util.HashMap;

public class SoundSocketList {
	// 语音连接表<连接名字，socket > ：用来语音的另一个端口的socket
	private HashMap<String, Socket> soundSocketMap = new HashMap<String, Socket>();
	
	public void loginSoundSocket(String linkName, Socket socket) {
		soundSocketMap.put(linkName, socket);
		System.out.println("新语音socket连接：" + linkName);
	}

	public void logoutSoundSocket(String linkName) {
		soundSocketMap.remove(linkName);
		System.out.println("语音socket连接：" + linkName + "断开");
	}

	public HashMap<String, Socket> getSoundSocketsMap() {
		return soundSocketMap;
	}
}

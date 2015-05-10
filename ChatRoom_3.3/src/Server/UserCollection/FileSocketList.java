package Server.UserCollection;

import java.net.Socket;
import java.util.HashMap;

public class FileSocketList {
	// 文件连接表<临时名字，socket > ：用来传输文件的另一个端口的socket
	private HashMap<String, Socket> fileSocketMap = new HashMap<String, Socket>();

	public void loginFileSocket(String linkName, Socket socket) {
		fileSocketMap.put(linkName, socket);
		System.out.println("新文件socket连接：" + linkName);
	}

	public void logoutFileSocket(String linkName) {
		fileSocketMap.remove(linkName);
		System.out.println("文件socket连接：" + linkName + "断开");
	}

	public HashMap<String, Socket> getFileSocketsMap() {
		return fileSocketMap;
	}
}

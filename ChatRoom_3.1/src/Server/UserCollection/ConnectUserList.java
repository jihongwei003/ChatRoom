package Server.UserCollection;

import java.net.Socket;
import java.util.HashMap;

public class ConnectUserList {
	// 存储与服务器正在建立连接的用户<临时名字，socket> ：用来通信的
	private HashMap<String, Socket> socketsMap = new HashMap<String, Socket>();

	// 查找临时用户
	public Socket getSocket(String name) {
		return (Socket) socketsMap.get(name);
	}

	// 上线临时用户
	public void loginUser(String name, Socket socket) {
		socketsMap.put(name, socket);
		System.out.println("新建立连接：" + name);
	}

	// 下线临时用户
	public void logoutUser(String name) {
		socketsMap.remove(name);
		System.out.println("连接：" + name + "断开");
	}
	
	public HashMap<String, Socket> getSocketsMap() {
		return socketsMap; // 群发时用
	}
}

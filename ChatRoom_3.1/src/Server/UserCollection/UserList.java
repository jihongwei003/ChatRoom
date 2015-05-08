package Server.UserCollection;

import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;

//用户列表的抽象类，子类可以由数据库表示，也可以由内存容器表示
public abstract class UserList implements Serializable {

	private static final long serialVersionUID = 1L;

	public UserList() {
	}

	// 获取连接
	public abstract Socket getSocket(String name);

	// 上线一个用户
	public abstract void loginUser(String name, Socket socket);

	// 下线一个用户
	public abstract void logoutUser(String name);

	// 上线正式用户
	public abstract void loginFormalUser(String tempName, String name);

	// 下线正式用户
	public abstract void logoutFormalUser(String name);

	// 注册用户
	public abstract void registUser(String name);

	// 注销用户
	public abstract void deleteUser(String name);

	// 是否注册
	public abstract boolean isRegister(String name);

	// 查询所有连接
	public abstract HashMap<String, Socket> getSocketsMap();

	// 查询所有在线用户
	public abstract HashMap<String, String> getOnlineUsers();

}

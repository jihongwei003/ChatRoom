package Server.UserCollection;

import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;

//用户列表的抽象类，子类可以由数据库表示，也可以由内存容器表示
public abstract class UserList implements Serializable{

	private static final long serialVersionUID = 1L;

	public UserList() {

	}

	public abstract Socket getSocket(String name);

	// 添加一个成员
	public abstract void addUser(String name, Socket socket);

	// 删除一个成员
	public abstract void deleteUser(String name);

	// 查找所有的用户 
	public abstract String getUsersList(String name); // 已经废弃的方法！！！

	// 这个方法是为了在不改变现有实现的情况下，补全依赖倒转的方法（其实是一开始没有对依赖实现好）
	public abstract HashMap<String, Socket> getSocketsMap();
}

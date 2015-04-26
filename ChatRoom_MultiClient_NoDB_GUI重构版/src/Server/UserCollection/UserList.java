package Server.UserCollection;
import java.net.Socket;

//用户列表的抽象类，子类可以由数据库表示，也可以由内存容器表示
public abstract class UserList {

	public UserList() {

	}

	public abstract Socket getSocket(String name);

	// 添加一个成员
	public abstract void addUser(String name, Socket socket);

	// 删除一个成员
	public abstract void deleteUser(String name);

	// 查找所有的用户
	public abstract String getUsersList(String name);
}

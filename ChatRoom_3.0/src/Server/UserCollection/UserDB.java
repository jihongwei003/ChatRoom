package Server.UserCollection;

import java.io.Serializable;
import java.net.*;
import java.util.HashMap;
import java.util.Iterator;

//用映射模拟数据库
public class UserDB extends UserList implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 12L;

	private HashMap<String, Socket> socketsMap = new HashMap<String, Socket>();
	
	//单件模式，不可由外部对象new出本对象实例
	private static UserDB s_userDB;
	
	private UserDB(){
		
	}
	
	public static UserDB getInstance(){
		if(s_userDB == null){
			s_userDB = new UserDB();
			System.out.println("用户数据库创建成功");
		}
		else{
			System.out.println("用户数据库已经存在，返回已存在的实例");
		}
		return s_userDB;
	}
	
	// get
	public HashMap<String, Socket> getSocketsMap() {
		return socketsMap; 
	}

	// set //没有用过
	public void setSocketsMap(HashMap<String, Socket> socketsMap) {
		this.socketsMap = socketsMap;
	}

	// 根据映射的key找到对应的valve(socket)
	public Socket getSocket(String name) {
		Socket socket = null;
		socket = socketsMap.get(name);//
		return socket;
	}

	// 添加一个成员
	public void addUser(String name, Socket socket) {
		socketsMap.put(name, socket);
	}

	// 删除一个成员
	public void deleteUser(String name) {
		socketsMap.remove(name);
	}

	// 查找所有的用户	（已经废弃的方法！！！）
	public String getUsersList(String name) {
		//本来是要返回一个没有当前用户的列表的，为了方便就把所有用户都返回了
		
		Iterator<String> iter = socketsMap.keySet().iterator();

		String str = new String();// 封装类
		while (iter.hasNext()) {
			String s = (String) iter.next();// 返回迭代器的下一个key值

			// 使所有的用户信息叠加，以整体方式输出
			str = str + "用户名:" + s + "\n";
		}
		return str;
	}
}
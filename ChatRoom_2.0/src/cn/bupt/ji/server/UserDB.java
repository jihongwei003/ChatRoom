package cn.bupt.ji.server;

import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
 
//用映射模拟数据库
public class UserDB {
    private HashMap<String, Socket> sockets = new HashMap<String, Socket>();

    //返回当前映射 
    public HashMap<String, Socket> getSockets() {
        return sockets;
    }
    
    //设置当前映射
    public void setSockets(HashMap<String, Socket> sockets) {
        this.sockets = sockets;
    }
 
    //根据映射的key找到对应的valve(socket)
    public Socket getSocket(String name){
        Socket socket = null;
        socket = sockets.get(name);//
        return socket;
    }
    
    //添加一个成员
    public void addUser(String name,Socket socket){
        sockets.put(name, socket);
    }
    
    //删除一个成员
    public void deleteUser(String name){
        sockets.remove(name);
    }
    
    //遍历所有的用户 
	public String GetAllUsers() {
		/**迭代器一定要用局部的！！！如果作为成员变量，初始化的时候它是空的*/
		Iterator<String> iter = this.getSockets().keySet().iterator();
		String str = new String();// 封装类，什么时候用？？？
		
		while (iter.hasNext()) {
			String s = (String) iter.next();// 返回迭代器的下一个key值

			// 使所有的用户信息叠加，以整体方式输出
			str = str + "用户名:" + s + "\n";
		}
		System.out.println(str);
		return str;
	}
}
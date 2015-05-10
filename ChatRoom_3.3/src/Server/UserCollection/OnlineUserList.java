package Server.UserCollection;

import java.util.HashMap;

public class OnlineUserList {

	// 在线好友<名字， 临时名字> ：用来回复Client好友列表的
	private HashMap<String, String> usersMap = new HashMap<String, String>();

	// 上线正式用户
	public void loginFormalUser(String name, String linkName) {
		usersMap.put(name, linkName);
		System.out.println("新上线用户：" + name);
	}

	// 下线正式用户
	public void logoutFormalUser(String name) {
		usersMap.remove(name);
		System.out.println("用户：" + name + "下线");
	}
	
	public HashMap<String, String> getOnlineUsersMap() {
		// 用户注册成功后，就将这条信息存入这个map
		return usersMap;
	}
}

package Server;

public class LogoutHandler extends RequestHandler {

	public LogoutHandler(UserDB users, String name) {
		super(users, name);
	}

	@Override
	public void handleRequest() {
		// 用户退出，将用户信息从HashMap中删除
		users.deleteUser(name);

	}

}

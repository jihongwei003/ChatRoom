package Server;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.concurrent.*;

import Server.Log.LogInvoHandler;
import Server.MsgCollection.MsgManager;
import Server.MsgCollection.MsgManagerInter;
import Server.RequestHandlers.DisplayFriendsHandler;
import Server.RequestHandlers.GroupChatHandler;
import Server.RequestHandlers.LoginHandler;
import Server.RequestHandlers.LogoutHandler;
import Server.RequestHandlers.PrivateChatHandler;
import Server.RequestHandlers.RegistHandler;
import Server.RequestHandlers.RequestHandler;
//import Server.UserCollection.UserDB;
import Server.UserCollection.UserList;
import Server.UserCollection.UserPersistence;

public class MultiServer {
	private ServerSocket server;
	private UserList users;

	public MultiServer(int port) throws IOException {
		//users = UserDB.getInstance();
		users = new UserPersistence();
		server = new ServerSocket(port);
	}

	public void runServer() throws IOException {

		// 使用日志代理，记录MsgManager类的行为 （类似于Spring AOP 的使用）
		MsgManagerInter msgController = LogInvoHandler
				.getProxyInstance(MsgManager.class);
		// MsgManager msgController = new MsgManager();

		HashMap<String, RequestHandler> requestMap = new HashMap<String, RequestHandler>();

		RequestHandler listFriendsHandler = new DisplayFriendsHandler(users);
		RequestHandler privateChatHandler = new PrivateChatHandler(users);
		RequestHandler groupChatHandler = new GroupChatHandler(users);
		RequestHandler logoutHandler = new LogoutHandler(users);
		RequestHandler registHandler = new RegistHandler(users);
		RequestHandler loginHandler = new LoginHandler(users);
		
		requestMap.put("1", listFriendsHandler);
		requestMap.put("2", privateChatHandler);
		requestMap.put("3", groupChatHandler);
		requestMap.put("4", logoutHandler);
		requestMap.put("5", registHandler);
		requestMap.put("6", loginHandler);
		
		// 消息处理线程
		RequestsManager manager = new RequestsManager();
		manager.setMsgManager(msgController);
		manager.setRequestMap(requestMap);
		manager.start();

		// 创建线程池，将任务添加到runnable中，然后在创建线程后自动启动这些任务
		ExecutorService exec = Executors.newCachedThreadPool();

		System.out.println("服务器启动，开始监听。。。");
		while (true) {
			Socket socket = server.accept();
			System.out.println("接受客户：" + socket.getInetAddress()
					+ "的连接请求，开始通信。。。");

			// 用连接后的socket新建线程
			SingleServer single = new SingleServer(socket, users);
			single.setMsgManager(msgController);
			exec.execute(single);

		}
	}
	
	public void closeServer(){
		try{
			server.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {

		MultiServer server = new MultiServer(8888);
		server.runServer();

	}
}

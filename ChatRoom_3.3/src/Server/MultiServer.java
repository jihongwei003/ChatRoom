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
import Server.RequestHandlers.ReceiveFileHandler;
import Server.RequestHandlers.RegistHandler;
import Server.RequestHandlers.RequestHandler;
import Server.RequestHandlers.SendFileHandler;
import Server.RequestHandlers.SoundChatHandler;
import Server.RequestHandlers.StopSoundChatHandler;
//import Server.UserCollection.UserDB;
import Server.UserCollection.UserList;
import Server.UserCollection.UserPersistence;

public class MultiServer {
	private ServerSocket server; // 通信用
	private ServerSocket fileServer; // 传输文件用
	private ServerSocket soundServer; // 语音通话

	private UserList users;

	public MultiServer() throws IOException {
		// users = UserDB.getInstance();
		users = new UserPersistence();

		server = new ServerSocket(8888);
		System.out.println("通信服务器启动，开始监听。。。");

		fileServer = new ServerSocket(9999);
		System.out.println("文件传输服务器启动，开始监听。。。");

		soundServer = new ServerSocket(9090);
		System.out.println("声音传输服务器启动，开始监听。。。");
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
		RequestHandler sendFileHandler = new SendFileHandler(users);
		RequestHandler receiveFileHandler = new ReceiveFileHandler(users);
		RequestHandler soundChatHandler = new SoundChatHandler(users);
		RequestHandler stopSoundChatHandler = new StopSoundChatHandler(users);
		//注入停止语音Handler的依赖
		((StopSoundChatHandler) stopSoundChatHandler)
				.setSoundChatHandler((SoundChatHandler) soundChatHandler);

		requestMap.put("1", listFriendsHandler);
		requestMap.put("2", privateChatHandler);
		requestMap.put("3", groupChatHandler);
		requestMap.put("4", logoutHandler);
		requestMap.put("5", registHandler);
		requestMap.put("6", loginHandler);
		requestMap.put("8", sendFileHandler);
		requestMap.put("9", receiveFileHandler);
		requestMap.put("10", soundChatHandler);
		requestMap.put("11", stopSoundChatHandler);

		// 消息处理线程
		RequestsManager manager = new RequestsManager();
		manager.setMsgManager(msgController);
		manager.setRequestMap(requestMap);
		manager.start();

		// 创建线程池，将任务添加到runnable中，然后在创建线程后自动启动这些任务
		ExecutorService exec = Executors.newCachedThreadPool();

		while (true) {
			Socket socket = server.accept();
			System.out.println("【通信服务器接受客户：" + socket.getInetAddress()
					+ "的连接请求，开始通信。。。】");

			Socket fileSocket = fileServer.accept();
			System.out.println("【文件传输服务器接受客户：" + socket.getInetAddress()
					+ "的连接请求，等待文件传输。。。】");

			Socket soundSocket = soundServer.accept();
			System.out.println("【声音传输服务器接受客户：" + socket.getInetAddress()
					+ "的连接请求，等待语音通话。。。】");

			// 用连接后的socket新建线程
			SingleServer single = new SingleServer(socket, fileSocket,
					soundSocket, users);
			single.setMsgManager(msgController);
			exec.execute(single);

		}
	}

	public void closeServer() {
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {

		MultiServer server = new MultiServer();
		server.runServer();

	}
}

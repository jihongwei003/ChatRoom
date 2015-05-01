package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import Client.Director;
import Client.SocketReaderBuilder;
import Client.SocketWriterBuilder;

/* 线程池类实现了Runnable接口 */
class SingleServer implements Runnable {

	private UserDB users;
	private Socket socket;

	public SingleServer(UserDB users, Socket socket) {
		this.users = users;
		this.socket = socket;
	}

	private RequestsManager manager;

	// 创建实例时自动调用
	public void run() {
		try {
			Director director = new Director(new SocketWriterBuilder(socket));
			PrintWriter writer = (PrintWriter) director.construct();
			
			director = new Director(new SocketReaderBuilder(socket));
			BufferedReader reader = (BufferedReader) director.construct();

			// 检测名字是否重复
			while (true) {
				writer.println("请输入昵称：");
				String name = reader.readLine();

				if (users.getSocket(name) != null) {
					writer.println("该用户已注册！");
					continue;
				} else {

					// 将用户昵称和其套接字添加到HashMap
					users.addUser(name, socket);
					writer.println("用户名设置成功！\n");
					System.out.println("新注册用户：" + name);
					
					HashMap<String, RequestHandler> requestMap = new HashMap<String, RequestHandler>();

					RequestHandler listFriendsHandler = new DisplayFriendsHandler(
							users, name);
					RequestHandler privateChatHandler = new PrivateChatHandler(users,
							name);
					RequestHandler groupChatHandler = new GroupChatHandler(users,
							name);
					RequestHandler logoutHandler = new LogoutHandler(users,
							name);

					requestMap.put("1", listFriendsHandler);
					requestMap.put("2", privateChatHandler);
					requestMap.put("3", groupChatHandler);
					requestMap.put("4", logoutHandler);

					manager = new RequestsManager(users, name, requestMap);
					if(!manager.runManager())
						break;
				}
			}//end of while
			 reader.close();
			 writer.close();
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			System.out.println("服务器与客户" + socket.getInetAddress() + "通信结束！");
			try {
				socket.close();// 当前已连接的客户端
			} catch (IOException e) {
				System.out
						.println("服务器与客户" + socket.getInetAddress() + "通信未正确关闭！");
			}
		}
	}

}

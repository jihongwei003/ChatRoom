package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

//import Server.MsgCollection.MsgManager;
import Server.MsgCollection.MsgManagerInter;
import Server.UserCollection.UserDB;
import Tools.Bulider.BufferedReaderBuilder;
import Tools.Bulider.Director;
import Tools.Bulider.PrintWriterBuilder;

/* 线程池类实现了Runnable接口 */
class SingleServer implements Runnable {

	private Socket socket;
	private UserDB users;
	private MsgManagerInter msgManager;

	public SingleServer(Socket socket, UserDB users) {
		this.socket = socket;
		this.users = users;
		this.msgManager = null;
	}

	public void setMsgManager(MsgManagerInter msgManager) {
		this.msgManager = msgManager;
	}

	// 创建实例时自动调用
	public void run() {
		String name = null;
		try {
			Director director = new Director(new PrintWriterBuilder(socket));
			PrintWriter writer = (PrintWriter) director.construct();

			director = new Director(new BufferedReaderBuilder(socket));
			BufferedReader reader = (BufferedReader) director.construct();

			// 先设置用户名
			// 检测名字是否重复
			while (true) {
				// writer.println("请输入昵称：");
				// 接受一个名字
				name = reader.readLine();

				// 回应一个 成功 或者 失败
				if (users.getSocket(name) != null) {
					writer.println("失败");
					continue;
				} else {
					// 将用户昵称和其套接字添加到HashMap
					users.addUser(name, socket);
					writer.println("成功");

					System.out.println("新注册用户：" + name);

					// 循环接收到用户的消息，将消息存入msgManager
					while (true) {
						String msg = reader.readLine();
						msgManager.addMsg(msg);
					}
				}
			}
			// reader.close();
			// writer.close();
		} catch (Exception e) {
			System.out.println(socket.getInetAddress() + "下线！");

			//强制删除异常下线用户
			if (name != null)
				users.deleteUser(name);

			System.out.println(e);
		} finally {
			System.out.println("服务器与客户" + socket.getInetAddress() + "通信结束！");
			try {
				socket.close();// 当前已连接的客户端
			} catch (IOException e) {
				System.out.println("服务器与客户" + socket.getInetAddress()
						+ "通信未正确关闭！");
			}
		}
	}

}

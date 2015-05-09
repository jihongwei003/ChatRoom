package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;

import Server.MsgCollection.MsgManagerInter;
import Server.UserCollection.UserList;
import Tools.ClientNameGenerator;
import Tools.JsonTrans;
import Tools.Bulider.BufferedReaderBuilder;
import Tools.Bulider.Director;
import Tools.Bulider.PrintWriterBuilder;

/* 线程池类实现了Runnable接口 */
class SingleServer implements Runnable {

	private Socket socket;
	private Socket fileSocket;
	private UserList users;
	private MsgManagerInter msgManager;

	public SingleServer(Socket socket, Socket fileSocket, UserList users) {
		this.socket = socket;
		this.fileSocket = fileSocket;
		this.users = users;
		this.msgManager = null;
	}

	public void setMsgManager(MsgManagerInter msgManager) {
		this.msgManager = msgManager;
	}

	// 创建实例时自动调用
	public void run() {
		String tempName = null;
		try {
			Director director = new Director(new BufferedReaderBuilder(socket));
			BufferedReader reader = (BufferedReader) director.construct();

			director = new Director(new PrintWriterBuilder(socket));
			// 使用指导者生成writer
			PrintWriter writer = (PrintWriter) director.construct();

			// 给当前连接的socket分配一个名字
			tempName = ClientNameGenerator.gen();

			// 将临时名字发送给客户端
			ResTrans trans = new ResTrans();
			trans.setMsgNum("0");
			trans.setContent(tempName);
			String result = trans.getResult();
			// 添加服务器的包头
			String output = JsonTrans.buildJson("res", result);

			writer.println(output);

			/** 将当前连接存入socket连接表和文件socket表中 */
			users.loginUser(tempName, socket);
			users.loginFileSocket(tempName, fileSocket);

			// 循环接收到用户的消息，将消息存入msgManager
			while (true) {
				String msg = reader.readLine();
				msgManager.addMsg(msg);
			}

			// reader.close();
			// writer.close();
		} catch (Exception e) {
			System.out.println(socket.getInetAddress() + "下线！");

			/** 强制下线未登录连接 */
			if (tempName != null) {
				users.logoutUser(tempName);
				users.logoutFileSocket(tempName);
				
				// 下线连接异常的已经登陆的用户，好比客户端突然断电（办法不太好）
				Iterator<?> it = users.getOnlineUsers().keySet().iterator();
				while (it.hasNext()) {
					String a = (String) it.next();
					if (users.getOnlineUsers().get(a).equals(tempName))
						users.logoutFormalUser(a);
				}
			}
			System.out.println("【剩余在线用户：" + users.getOnlineUsers().size());
			System.out.println("【剩余连接数：" + users.getSocketsMap().size());
			System.out.println("【剩余文件客户端连接：" + users.getFileSocketsMap().size());

			System.out.println(e);
		} finally {
			System.out.println("服务器与客户" + socket.getInetAddress() + "通信结束！");
			try {
				socket.close();// 当前已连接的客户端
				fileSocket.close();
			} catch (IOException e) {
				System.out.println("服务器与客户" + socket.getInetAddress()
						+ "通信未正确关闭！");
			}
		}
	}

}

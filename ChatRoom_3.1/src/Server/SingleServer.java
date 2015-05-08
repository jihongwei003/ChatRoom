package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

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
	private UserList users;
	private MsgManagerInter msgManager;

	public SingleServer(Socket socket, UserList users) {
		this.socket = socket;
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

			/** 将当前连接存入socket连接表中 */
			users.loginUser(tempName, socket);

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
			}
			
			System.out.println("剩余在线用户：" + users.getOnlineUsers().size());
			System.out.println("剩余连接数：" + users.getSocketsMap().size());

			// 想办法删除已经登陆的用户？？？ -- 这里没有登录过的用户吧
			// if (null != users.getOnlineUsers().get(tempName)) {
			// users.logoutFormalUser(tempName);
			// }

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

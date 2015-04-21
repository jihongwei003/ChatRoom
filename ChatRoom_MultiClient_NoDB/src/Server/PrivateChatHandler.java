package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import Client.Director;
import Client.SocketReaderBuilder;
import Client.SocketWriterBuilder;

public class PrivateChatHandler extends RequestHandler {

	public PrivateChatHandler(UserDB users, String name) {
		super(users, name);
	}

	@Override
	public void handleRequest() {
		// 私聊时，服务器只负责接收并转发消息(将userSocket的消息转发给friendSocket)
		Socket userSocket = users.getSocket(name);

		try {
			// 当前用户的writer
			Director director = new Director(
					new SocketWriterBuilder(userSocket));
			PrintWriter writerU = (PrintWriter) director.construct();

			// 当前用户的reader
			director = new Director(new SocketReaderBuilder(userSocket));
			BufferedReader readerU = (BufferedReader) director.construct();

			writerU.println("请输入好友名：");
			String friendName = readerU.readLine();

			Socket friendSocket = users.getSocket(friendName);
			if (friendSocket == null) {
				writerU.println("好友不存在!");
			} else {
				writerU.println("连接成功，进入【私聊】状态\n");

				System.out.println(name + "与" + friendName + "进入聊天室");

				// 用户好友的writer
				director = new Director(new SocketWriterBuilder(friendSocket));
				PrintWriter writerF = (PrintWriter) director.construct();

				String sendmsg = "";
				while (!sendmsg.equals("bye")) {
					String str = null;
					try {
						str = readerU.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}

					sendmsg = str;
					str = name + " 对 " + friendName + " 说：" + str;

					writerF.println(str);// 向目标客户端缓冲区发送数据
					System.out.println(str);

				}// end of while
					// 因为都是同一个socket的引用，所以不能在内部关闭吗？
					// writerU.close();
				// readerU.close();
				// writerF.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

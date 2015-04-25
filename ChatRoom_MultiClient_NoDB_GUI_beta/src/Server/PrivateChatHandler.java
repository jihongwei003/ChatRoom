package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import Client.Director;
import Client.BufferedReaderBuilder;
import Client.PrintWriterBuilder;

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
			Director director = new Director(new PrintWriterBuilder(userSocket));
			PrintWriter writerU = (PrintWriter) director.construct();

			// 当前用户的reader
			director = new Director(new BufferedReaderBuilder(userSocket));
			BufferedReader readerU = (BufferedReader) director.construct();

			writerU.println("Server已进入私聊状态");//为了与Client同步状态！
			
			// writerU.println("请输入好友名：");
			String friendName = readerU.readLine();

			Socket friendSocket = users.getSocket(friendName);
			System.out.println(name + " <-> " + friendName);
			if(friendName.equals("bye"))
				System.out.println("出错了！没有正确接收到friendName！");

			if (friendSocket == null) {
				writerU.println("好友不存在!"); //客户端为什么没有接到这句话？？？
				
				System.out.println("没有找到friendSocket！");
			} else {
				writerU.println("连接成功，进入【私聊】状态");

				System.out.println(name + "与" + friendName + "进入聊天室");

				// 用户好友的writer
				director = new Director(new PrintWriterBuilder(friendSocket));
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

				}//end of while
				System.out.println(name + "与" + friendName + "退出聊天室");
			}//end of else

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;

import Client.Director;
import Client.BufferedReaderBuilder;
import Client.PrintWriterBuilder;

public class GroupChatHandler extends RequestHandler {

	public GroupChatHandler(UserDB users, String name) {
		super(users, name);
	}

	@Override
	public void handleRequest() {
		try {
			// 当前用户的writer
			Director director = new Director(new PrintWriterBuilder(
					users.getSocket(name)));
			PrintWriter writerU = (PrintWriter) director.construct();

			// 当前用户的reader
			director = new Director(new BufferedReaderBuilder(
					users.getSocket(name)));
			BufferedReader readerU = (BufferedReader) director.construct();

			writerU.println("连接成功，进入【群聊】状态\n");

			String sendmsg = "";
			while (!sendmsg.equals("bye")) {
				String stri = readerU.readLine();// BufferedReader客户端发过来的数据
				sendmsg = stri;
				System.out.println(name + " 说：" + sendmsg);
				stri = name + " 说：" + stri;// 发给目标客户端的

				Iterator<String> iter = users.getSocketsMap().keySet()
						.iterator();

				while (iter.hasNext()) {
					String key = iter.next();
					// 创建目标客户端socket的引用
					Socket friendSocket = users.getSocket(key);

					director = new Director(new PrintWriterBuilder(
							friendSocket));
					PrintWriter writerF = (PrintWriter) director.construct();

					writerF.println(stri);
					writerF.flush();
				}
			}
			//close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

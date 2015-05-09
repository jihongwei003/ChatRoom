package Server.RequestHandlers;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import net.sf.json.JSONObject;
import Server.ResTrans;
import Server.UserCollection.UserList;
import Tools.JsonTrans;
import Tools.Bulider.Director;
import Tools.Bulider.PrintWriterBuilder;

public class ReceiveFileHandler extends RequestHandler {

	public ReceiveFileHandler(UserList users) {
		super(users);
	}

	private String filePath; // 服务器上的文件名
	// private long fileLength; //发送方不需要知道文件大小
	private String bool;
	private Socket fileSocket;

	@Override
	public void handleRequest() {
		JSONObject json = (JSONObject) JsonTrans.parseJson(super.requestMsg,
				"msg");
		String name = json.getString("publisher");// 解析出消息的发送者
		String friendName = json.getString("receiver");
		String filePart = json.getString("words");

		String linkName = users.getOnlineUsers().get(name);
		Socket socket = users.getSocket(linkName);
		fileSocket = users.getFileSocketsMap().get(linkName);

		// 解析文件名和文件大小
		JSONObject json1 = (JSONObject) JsonTrans.parseJson(filePart,
				"filePart");
		filePath = json1.getString("serverFileName"); // 服务器文件名
		// String length = json1.getString("length");
		// fileLength = Long.parseLong(length);
		bool = json1.getString("bool"); // 是否接收文件

		System.out.println(name + " 是否接收文件：" + bool);

		/* 向接收者对话框中回馈消息 */
		Director director = new Director(new PrintWriterBuilder(socket));

		// 使用指导者生成writer
		PrintWriter writer = null;
		try {
			writer = (PrintWriter) director.construct();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 将字符串打包成需要客户端解析的形式
		ResTrans trans = new ResTrans();
		trans.setPublisher(friendName);
		trans.setMsgNum("2");
		trans.setContent(name + " 是否接收文件：" + bool);
		String result = trans.getResult();

		// 添加服务器的包头
		String output = JsonTrans.buildJson("res", result);
		writer.println(output);

		SendFileThread thread = new SendFileThread();
		thread.setDaemon(true);
		thread.start();
	}

	public class SendFileThread extends Thread {
		public void run() {
			if (bool.equals("true")) {
				try {
					byte[] buffer = new byte[1024]; // 每次读取1024字节（1Kb）
					File f = new File("uploadFiles//" + filePath);

					// 数据输出流
					DataOutputStream dout = new DataOutputStream(
							new BufferedOutputStream(
									fileSocket.getOutputStream()));
					// 文件读入流
					FileInputStream fin = new FileInputStream(f);

					System.out.println("开始传输文件");

					// int i = 1;
					int length = 0;
					while ((length = fin.read(buffer)) != -1) {
						dout.write(buffer, 0, length);
						// System.out.println(i++ + " 次：" + length);
						dout.flush();
					}
					// System.out.println(i++ + " 次：" + length);

					System.out.println("文件传输完毕");

					// 关闭流
					fin.close();
					// dout.close();//会关闭socket

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}

package Server.RequestHandlers;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import net.sf.json.JSONObject;
import Server.ResTrans;
import Server.UserCollection.UserList;
import Tools.ClientNameGenerator;
import Tools.JsonTrans;
import Tools.Bulider.Director;
import Tools.Bulider.PrintWriterBuilder;

public class SendFileHandler extends RequestHandler {

	public SendFileHandler(UserList users) {
		super(users);
	}

	private Socket fileSocket;
	private Socket friSocket;
	//private Socket fileFriSocket;
	private String filePath; // 原文件名
	private long fileLength;
	private String fileName; // 存在服务器上的文件名

	@Override
	public void handleRequest() {
		JSONObject json = (JSONObject) JsonTrans.parseJson(super.requestMsg,
				"msg");
		String name = json.getString("publisher");// 解析出消息的发送者
		String friendName = json.getString("receiver");
		String filePart = json.getString("words");

		// 解析文件名和文件大小
		JSONObject json1 = (JSONObject) JsonTrans.parseJson(filePart,
				"filePart");
		filePath = json1.getString("fileName");
		String length = json1.getString("length");
		fileLength = Long.parseLong(length);

		// user的连接名
		String linkName = users.getOnlineUsers().get(name);
		fileSocket = users.getFileSocketsMap().get(linkName);

		// friend的连接名
		String linkFriName = users.getOnlineUsers().get(friendName);
		friSocket = users.getSocketsMap().get(linkFriName);
		// friend的文件传输名
		//fileFriSocket = users.getFileSocketsMap().get(linkFriName);

		System.out.println(name + " 向 " + friendName + " 发送文件");

		try {
			/* 回复发送者，通知发送者开始传输文件 */
			Director director = new Director(new PrintWriterBuilder(
					users.getSocket(linkName)));

			// 使用指导者生成writer
			PrintWriter writer = (PrintWriter) director.construct();

			// 将字符串打包成需要客户端解析的形式
			ResTrans trans = new ResTrans();
			trans.setMsgNum("8");
			trans.setContent(filePath);
			String result = trans.getResult();

			// 添加服务器的包头
			String output = JsonTrans.buildJson("res", result);
			writer.println(output);

			System.out.println("回应发送方 " + name + " 的消息为：" + output);

			ReceiveFileThread thread = new ReceiveFileThread(name, friendName);
			thread.setDaemon(true);
			thread.start();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public class ReceiveFileThread extends Thread {
		private String name;
		private String friName;

		public ReceiveFileThread(String name, String friName) {
			this.name = name;
			this.friName = friName;
		}

		public void run() {
			try {
				byte[] buffer = new byte[1024];
				DataInputStream din = new DataInputStream(
						new BufferedInputStream(fileSocket.getInputStream()));

				// 将文件名唯一化
				fileName = ClientNameGenerator.gen() + "."
						+ getFileType(filePath);

				// 创建要保存的文件
				File f = new File("uploadFiles//" + fileName);
				// RandomAccessFile fw = new RandomAccessFile(f, "rw");
				FileOutputStream fos = new FileOutputStream(f);

				System.out.println("准备接收文件");

				// int i = 1;
				int length = 0;
				int sum = 0;
				while ((length = din.read(buffer)) != -1) {
					sum += length;
					fos.write(buffer, 0, length);
					// System.out.println(i++ + " times：" + length);
					fos.flush();
					if (sum == fileLength)
						break;
				}
				// System.out.println(i++ + " times：" + length);

				System.out.println("接收文件完毕");
				fos.close();
				// fw.close();
				// din.close();//会关闭socket

			} catch (Exception e) {
				System.out.println("啦啦啦");
				e.printStackTrace();
			}

			sendFileMsg(name, friName);
		}
	}

	private void sendFileMsg(String publisher, String friendName) {
		try {
			/* 通知接收者接收文件 */
			Director director = new Director(new PrintWriterBuilder(friSocket));
			PrintWriter writer = (PrintWriter) director.construct();

			// 将文件名和文件大小打包成content
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("serverFileName", fileName); //服务器文件名
			String realFileName = getFileName(filePath); //得到不含文件路径的文件名
			map.put("fileName", realFileName);
			map.put("length", fileLength);
			JSONObject filePart1 = JSONObject.fromObject(map);
			String filePartJson = JsonTrans.buildJson("filePart", filePart1);

			// 将字符串打包成需要客户端解析的形式
			ResTrans trans = new ResTrans();
			trans.setPublisher(publisher);
			trans.setMsgNum("9");
			trans.setContent(filePartJson);
			String result = trans.getResult();

			// 添加服务器的包头
			String output = JsonTrans.buildJson("res", result);
			writer.println(output);

			System.out.println("回应接收方 " + friendName + "的消息为：" + output);

			/* 在接收者的聊天窗口中显示出“有文件来了” */
			trans = new ResTrans();
			trans.setPublisher(publisher);
			trans.setMsgNum("2");
			trans.setContent(publisher + "发送了文件：" + realFileName);

			result = trans.getResult();

			// 添加服务器的包头
			output = JsonTrans.buildJson("res", result);

			writer.println(output);
			writer.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取文件后缀名
	private String getFileType(String originalFileName) {
		String fileType = originalFileName.substring(originalFileName
				.lastIndexOf(".") + 1);
		return fileType;
	}

	private String getFileName(String fileNameWithFolder) {
		String fileName = fileNameWithFolder.substring(fileNameWithFolder
				.lastIndexOf("/") + 1);
		return fileName;
	}
}

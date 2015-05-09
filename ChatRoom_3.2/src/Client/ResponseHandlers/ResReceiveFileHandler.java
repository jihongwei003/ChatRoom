package Client.ResponseHandlers;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import javax.swing.JOptionPane;

import net.sf.json.JSONObject;
import Client.MsgTrans;
import Client.Pages.ChatBox;
import Tools.JsonTrans;
import Tools.Bulider.Director;
import Tools.Bulider.PrintWriterBuilder;

public class ResReceiveFileHandler extends ResponseHandler {

	public ResReceiveFileHandler(Socket socket, Socket fileSocket) {
		super(socket);
		this.fileSocket = fileSocket;
	}

	private Socket fileSocket;
	private String filePath;
	private String originalFileName;
	private long fileLength;

	private String bool;

	@Override
	public void handleResponse() {
		System.out.println("收到文件");

		JSONObject json = (JSONObject) JsonTrans.parseJson(super.responseMsg,
				"res");
		String friendName = json.getString("publisher");
		String filePart = json.getString("content");

		// 解析文件名和文件大小
		JSONObject json1 = (JSONObject) JsonTrans.parseJson(filePart,
				"filePart");
		filePath = json1.getString("serverFileName"); // 服务器文件名
		String length = json1.getString("length");
		fileLength = Long.parseLong(length);
		originalFileName = json1.getString("fileName");

		ChatBox c = ChatBox.getInstance(socket, SetNameHandler.getRealName(),
				friendName);
		c.setTitle("File Chat Box");

		/* 向服务器发送消息：是否接收文件 */
		int n = JOptionPane.showConfirmDialog(null, "用户 " + friendName + " 传来文件 "
				+ originalFileName + "，是否接收文件？", "确认接收文件",
				JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			bool = "true";
		} else if (n == JOptionPane.NO_OPTION) {
			bool = "false";
		}

		System.out.println("是否接收文件：" + bool);

		Director director = new Director(new PrintWriterBuilder(socket));
		try {
			PrintWriter writer = (PrintWriter) director.construct();

			// 将文件名和文件大小打包成content
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("serverFileName", filePath); // 服务器文件名
			map.put("bool", bool);
			JSONObject filePart1 = JSONObject.fromObject(map);
			String filePartJson = JsonTrans.buildJson("filePart", filePart1);

			// 将字符串打包成需要客户端解析的形式
			MsgTrans trans = new MsgTrans();
			trans.setPublisher(SetNameHandler.getRealName());
			trans.setMsgNum("9");
			trans.setReceiver(friendName);
			trans.setWords(filePartJson);
			String result = trans.getResult();

			// 添加客户端的包头
			String output = JsonTrans.buildJson("msg", result);
			writer.println(output);

		} catch (IOException e) {
			e.printStackTrace();
		}

		ReceiveFileThread thread = new ReceiveFileThread();
		thread.setDaemon(true);
		thread.start();
	}

	public class ReceiveFileThread extends Thread {
		public void run() {
			if (bool.equals("true")) {
				try {
					byte[] buffer = new byte[1024];

					DataInputStream din = new DataInputStream(
							new BufferedInputStream(fileSocket.getInputStream()));

					// 创建要保存的文件
					File f = new File("downloadFiles//" + originalFileName);
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

					System.out.println("接收文件完毕");
					fos.close();

					JOptionPane.showMessageDialog(null, "文件接收完毕！");

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}

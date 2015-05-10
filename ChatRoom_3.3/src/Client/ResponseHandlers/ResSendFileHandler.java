package Client.ResponseHandlers;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import net.sf.json.JSONObject;
import Tools.JsonTrans;

public class ResSendFileHandler extends ResponseHandler {

	public ResSendFileHandler(Socket socket, Socket fileSocket) {
		super(socket);
		this.fileSocket = fileSocket;
	}

	private Socket fileSocket;
	private String filePath;

	@Override
	public void handleResponse() {
		System.out.println("发送文件");
		
		JSONObject json = (JSONObject) JsonTrans.parseJson(super.responseMsg,
				"res");
		filePath = json.getString("content");

		//System.out.println(fileSocket.getPort());

		SendFileThread thread = new SendFileThread();
		thread.setDaemon(true);
		thread.start();
	}

	public class SendFileThread extends Thread {
		public void run() {
			try {
				byte[] buffer = new byte[1024]; // 每次读取1024字节（1Kb）

				File f = new File(filePath);

				// 数据输出流
				DataOutputStream dout = new DataOutputStream(
						new BufferedOutputStream(fileSocket.getOutputStream()));
				// 文件读入流
				FileInputStream fin = new FileInputStream(f);

				System.out.println("开始传输文件");

				//int i = 1;
				int length = 0;
				while ((length = fin.read(buffer)) != -1) {
					dout.write(buffer, 0, length);
					//System.out.println(i++ + " 次：" + length);
					dout.flush();
				}
				//System.out.println(i++ + " 次：" + length);
				
				System.out.println("文件传输完毕");

				// 关闭流
				fin.close();
				//dout.close();//会关闭socket

				JOptionPane.showMessageDialog(null, "上传成功！");

			} catch (Exception ex) {
				System.out.println("啦啦啦");
				ex.printStackTrace();
			}
		}
	}

}

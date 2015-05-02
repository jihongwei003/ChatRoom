package Client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import Tools.JsonTrans;
import Tools.Bulider.Director;
import Tools.Bulider.PrintWriterBuilder;

public class GroupChatWriter {

	private Socket socket;
	private String userName;

	private PrintWriter writer;

	public GroupChatWriter(Socket socket, String userName) {
		this.socket = socket;
		this.userName = userName;

		// 指导者
		Director director = new Director(new PrintWriterBuilder(this.socket));
		// 使用指导者生成一个writer
		try {
			writer = (PrintWriter) director.construct();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(String msg) {
		try {
			MsgTrans msgTrans = new MsgTrans();
			msgTrans.setPublisher(userName);
			msgTrans.setMsgNum("3");

			msgTrans.setWords(msg);
			String sendMsg = msgTrans.getResult();
			String a = JsonTrans.buildJson("msg", sendMsg);

			writer.println(a);
			writer.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}

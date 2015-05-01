package Client;

import java.io.BufferedReader;
import java.io.IOException;
//import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

//客户端的读取线程
public class ClientReaderThread extends Thread {

	private Socket socket;

	// private InputStream input;

	public ClientReaderThread(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		// 指导者
		Director director = new Director(new BufferedReaderBuilder(socket));
		try {
			// 使用指导者生成reader
			BufferedReader reader = (BufferedReader) director.construct();

			String receiveMsg = "";
			while (!receiveMsg.equalsIgnoreCase("bye")) {
				receiveMsg = reader.readLine();
				System.out.println(receiveMsg);
			}
			//reader.close();// 关闭流会导致socket的关闭！！
		} catch (SocketException e) {
			System.out.print("客户端socket已关闭！");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

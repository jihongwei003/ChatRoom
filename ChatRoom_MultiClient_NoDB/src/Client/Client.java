package Client;

import java.net.*;
import java.io.*;

public class Client {

	private Socket socket;

	public Client() {
		socket = null;
	}

	void runClient() {
		try {
			// 向服务器发出连接请求
			socket = new Socket("localhost", 8888);
			
			// 新建读线程
			Thread readerThread = new ClientReaderThread(socket);
			readerThread.start();

			// 新建写线程
			Thread writerThread = new ClientWriterThread(socket);
			writerThread.start();

		} catch (Exception e) {
			System.out.print("runclient");
			System.out.print(e);
		} finally {
			//socket.close();
		}
	}

	public static void main(String[] args) throws IOException {

		Client client = new Client();
		client.runClient();
	}
}
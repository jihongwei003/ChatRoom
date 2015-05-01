package cn.bupt.ji.client;

import java.net.*;
import java.util.Scanner;
import java.io.*;

/**主线程用来写数据，新建线程用来读数据*/
public class Client {

	public static void main(String[] args) throws IOException {
		// 向服务器发出连接请求
		Socket socket = new Socket("127.0.0.1", 8888);
		try {
			// 创建【新线程】，用来读数据
			Thread msr = new ReaderThread(socket);
			msr.start();
			
            //使用Scanner从键盘读入数据
			Scanner in = new Scanner(System.in);
			OutputStream out;
			try {
				//Output.write()
				out = socket.getOutputStream();
				PrintWriter pw = new PrintWriter(out, true);

				String sendMsg = "";

				while (!sendMsg.equalsIgnoreCase("4")) {
					sendMsg = in.next();
					pw.println(sendMsg);
					pw.flush();
				}
				System.out.println("bye!");
				pw.close();
				in.close();
			} catch (IOException e) {
				System.out.println(e);
			}
		} finally {
			socket.close();
		}
	}
}
package test;
import java.net.*;
import java.io.*;

public class Client {

	private Socket socket;

	public Client() {
		socket = null;
	}

	public void runClient() {
		try {
			socket = new Socket(InetAddress.getLocalHost(), 1234);
			System.out.println("连接到服务器");

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 发送文件
	public void sendFile(String str) {
		byte[] b = new byte[1024];

		File f = new File(str);
		try {
			// 数据输出流
			DataOutputStream dout = new DataOutputStream(
					new BufferedOutputStream(socket.getOutputStream()));
			// 文件读入流
			FileInputStream fr = new FileInputStream(f);

			System.out.println("开始传输文件");
			
			int n = fr.read(b); 
			while (n != -1) { // 向网络中写入数据
				dout.write(b, 0, n);
				dout.flush(); // 再次读取n字节
				n = fr.read(b);
			}
			
			System.out.println("文件传输完毕");
			
			// 关闭流
			fr.close();
			dout.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		Client client = new Client();
		client.runClient();
		client.sendFile("C://Users//JiHongwei//Desktop//Client.jpg");

	}

}

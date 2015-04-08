
import java.io.*;
import java.net.*;

public class Server {

	public static void main(String[] args) throws IOException {
		// 创建ServerSocket对象必须抛出异常处理
		// 创建服务器套接字，如果在已经占用的端口上创建，将会抛出异常
		ServerSocket server = new ServerSocket(8888);

		// server创建成功后，使用try-finally确保无论后面语句运行是否异常，它都能正确关闭
		try {
			System.out.println("服务器启动，开始监听。。。");

			// 服务器套接字监听来自客户端的连接请求
			// accept()返回一个与客户端相对应的套接字，用以IO通信
			Socket socket = server.accept();
			// socket创建成功后，使用try-finally确保无论后面语句运行是否异常，它都能正确关闭
			try {
				// 通过socket对象调用方法getInetAddress,可以获得其连接的另一端计算机的IP地址
				System.out.println("接受客户：" + socket.getInetAddress()
						+ "的连接请求，开始通信。。。");

				// 创建输入流对象
				DataInputStream in = new DataInputStream(
						new BufferedInputStream(socket.getInputStream()));
				//并创建读取数据线程
				Thread msr = new ReaderThread(in);
				msr.start();
				
				// 创建输出流对象，用来写数据
				DataOutputStream dos = new DataOutputStream(
						new BufferedOutputStream(socket.getOutputStream()));

				InputStreamReader isr = new InputStreamReader(System.in);
				BufferedReader br = new BufferedReader(isr);

				String msg;
				try {
					while (true) {
						msg = br.readLine();
						System.out.println("服务器说：" + msg);
						
						dos.writeUTF(msg);
						dos.flush();
					}
				} catch (IOException e) {
					System.out.println(e);
				}
			} finally {
				socket.close();
				System.out.println("通信结束！");
			}
		} finally {
			server.close();
		}
	}
}
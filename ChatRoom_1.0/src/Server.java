
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

				DataInputStream in = new DataInputStream(
						new BufferedInputStream(socket.getInputStream()));
				DataOutputStream out = new DataOutputStream(
						new BufferedOutputStream(socket.getOutputStream()));

				do {
					double length = in.readDouble();
					System.out.println("从客户就收到正方形边长：" + length);
					
					double area = length * length;
					//
					out.writeDouble(area);
					out.flush();
					
					System.out.println("正方形面积：" + area);
					
				} while (in.readInt() != 0);//
				
			} finally {
				socket.close();
				System.out.println("通信结束！");
			}
		} finally {
			server.close();
		}
	}
}
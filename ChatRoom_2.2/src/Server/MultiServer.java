package Server;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class MultiServer {
	private ServerSocket server;
	private UserDB users;

	public MultiServer() throws IOException {
		users = UserDB.getInstance();
		server = new ServerSocket(8888);
	}

	public MultiServer(int port) throws IOException {
		users = UserDB.getInstance();
		server = new ServerSocket(port);
	}

	public void runServer() throws IOException {
		// 创建线程池，将任务添加到runnable中，然后在创建线程后自动启动这些任务
		ExecutorService exec = Executors.newCachedThreadPool();

		System.out.println("服务器启动，开始监听。。。");
		while (true) {
			Socket socket = server.accept();
			System.out.println("接受客户：" + socket.getInetAddress()
					+ "的连接请求，开始通信。。。");

			// 用连接后的socket新建线程
			exec.execute(new SingleServer(users, socket));

			//server.close();
		}
	}

	public static void main(String[] args) throws IOException {

		MultiServer server = new MultiServer(8888);
		server.runServer();

	}
}

package cn.bupt.ji.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class MultiServer {
	public static void main(String[] args) throws IOException {
		/* 定义一个UserDB的对象，全局使用作为一个存储在线用户的虚拟数据库 */
		UserDB users = new UserDB();

		ServerSocket server = new ServerSocket(8888);
		
		/* 创建线程池，将任务添加到runnable中，然后在创建线程后自动启动这些任务 */
		ExecutorService exec = Executors.newCachedThreadPool();

		try {
			System.out.println("服务器启动，开始监听。。。");
			
			while (true) {
				Socket socket = server.accept();
				System.out.println("接受客户：" + socket.getInetAddress()
						+ "的连接请求，开始通信。。。");

				/* 用连接后的socket新建线程 */
				exec.execute(new SingleServer(users, socket));
			}
		} finally {
			// exec.close();线程池应该显示地关闭，可是为什么没有定义close()？？？
			server.close();//通信结束了居然没有关掉服务器？？？
		}
	}
}
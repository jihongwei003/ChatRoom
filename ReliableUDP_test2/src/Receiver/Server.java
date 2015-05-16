package Receiver;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class Server {

	private DatagramSocket dataSocket;
	private ReceiveWindow window;
	private int numScope;

	public Server(String host, int port, int numScope) {
		InetSocketAddress socketAddress = new InetSocketAddress(host, port);
		try {
			dataSocket = new DatagramSocket(socketAddress);
		} catch (SocketException e) {
			e.printStackTrace();
		}

		this.numScope = numScope;
		window = new ReceiveWindow((this.numScope + 1) / 2);// 接收窗口
		System.out.println("服务端启动。。。");
	}

	public void runServer() {
		// 接收线程
		ReceiveThread thread = new ReceiveThread(dataSocket, window);
		thread.start();

		// 回应线程
		ResponseThread thread2 = new ResponseThread(dataSocket, window);
		thread2.start();
	}

	public static void main(String[] args) {

		String serverHost = "127.0.0.1";
		int serverPort = 3344;
		int numScope = 8;// 序号空间0 - 7，窗口大小为序号空间的一半

		Server server = new Server(serverHost, serverPort, numScope);// 服务器
		server.runServer();
	}
}

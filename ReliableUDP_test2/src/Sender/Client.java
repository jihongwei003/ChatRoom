package Sender;

import java.net.DatagramSocket;
import java.net.SocketException;

public class Client {

	private DatagramSocket dataSocket;
	private SendWindow window;
	private int numScope;

	public Client(int numScope) {
		try {
			dataSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}

		this.numScope = numScope;
		window = new SendWindow((this.numScope + 1) / 2);
		System.out.println("客户端启动。。。");
	}

	public void runClient(String host, int port) {
		// 发送数据包
		SendThread thread = new SendThread(dataSocket, window);
		thread.setNumScope(numScope);
		thread.setServerHost(host);
		thread.setServerPort(port);
		thread.start();

		// 接收确认包
		ACKReceiverThread ackReceiver = new ACKReceiverThread(dataSocket,
				window);
		ackReceiver.start();

		// 数据包重发线程
		ReSendThread thread2 = new ReSendThread(dataSocket, window);
		thread2.setServerHost(host);
		thread2.setServerPort(port);
		thread2.start();
	}

	public static void main(String[] args) {
		int numScope = 8;// 序号空间0 - 7 , 窗口大小为序号空间的一半

		String serverHost = "127.0.0.1"; // 指定接收端地址
		int serverPort = 3344;

		Client client = new Client(numScope);
		client.runClient(serverHost, serverPort);
	}
}

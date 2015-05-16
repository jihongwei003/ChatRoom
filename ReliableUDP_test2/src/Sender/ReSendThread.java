package Sender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import Flyweight.Msg;

public class ReSendThread extends Thread {

	private DatagramSocket dataSocket;
	private SendWindow window;

	private String serverHost;
	private int serverPort;

	public ReSendThread(DatagramSocket dataSocket, SendWindow window) {
		this.dataSocket = dataSocket;
		this.window = window;

		System.out.println("客户端重发线程启动。。。");
	}

	public void run() {
		// 遍历窗口，当某个窗口中的数据停留超过3秒没有删除，重发这个数据包
		while (true) {
			// 从window中取出数据包
			DatagramPacket packet = window.peek();
			if (packet != null) {
				Msg msg = new Msg(packet.getData());

				long cTime = System.currentTimeMillis();
				if (cTime - msg.getLastSendTime() > 3000) {
					send(msg);
				}
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// 发送包，并将包放入窗口
	public void send(Msg msg) {
		try {
			DatagramPacket packet = new DatagramPacket(msg.toByte(),
					msg.toByte().length, InetAddress.getByName(serverHost),
					serverPort);

			dataSocket.send(packet);
			System.out.println("重新发送数据包:" + msg.getID());
			System.out.println("");

		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setServerHost(String host) {
		this.serverHost = host;
	}

	public void setServerPort(int port) {
		this.serverPort = port;
	}
}

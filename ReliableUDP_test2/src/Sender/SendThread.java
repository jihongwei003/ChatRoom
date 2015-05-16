package Sender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import Flyweight.Msg;

public class SendThread extends Thread {

	private DatagramSocket dataSocket;
	private SendWindow window;
	
	private int numScope;
	private String serverHost;
	private int serverPort;

	public SendThread(DatagramSocket dataSocket, SendWindow window) {
		this.dataSocket = dataSocket;
		this.window = window;

		System.out.println("客户端发送线程启动。。。");
	}

	public void run() {
		int pointer = 0;
		while (true) {
			Msg msg = new Msg(pointer);
			msg.setLastSendTime(System.currentTimeMillis());
			
			send(msg); // 阻塞式的发，一定会发出去

			// 移动序号空间
			pointer = (++pointer) % numScope;

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

			// 判断窗口是否已满
			while (true) {
				if (!window.isFull()) {
					System.out.println("");
					window.add(packet);//先放入window再发送
					
					dataSocket.send(packet);
					System.out.println("发送数据包:" + msg.getID());
					System.out.println("");
					break;
				}
			}
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setNumScope(int s) {
		this.numScope = s;
	}

	public void setServerHost(String host) {
		this.serverHost = host;
	}

	public void setServerPort(int port) {
		this.serverPort = port;
	}
}

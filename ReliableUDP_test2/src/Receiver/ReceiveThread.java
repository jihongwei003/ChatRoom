package Receiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

import Flyweight.Msg;

public class ReceiveThread extends Thread {
	private DatagramSocket dataSocket;
	private ReceiveWindow window;

	public ReceiveThread(DatagramSocket dataSocket, ReceiveWindow window) {
		this.dataSocket = dataSocket;
		this.window = window;

		System.out.println("服务器接收线程启动。。。");
	}

	public void run() {
		byte[] buffer = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		while (true) {
			try {
				dataSocket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}

			window.printExpectNum();

			// 解析出Msg
			Msg msg = new Msg(packet.getData());
			int num = msg.getID();
			System.out.println("接收到数据包：" + num);

			// 取出buffer的数据(如果引用同一个buffer，改变一个值，所有引用这个buffer的对象都会改变)
			byte[] b = Arrays.copyOf(buffer, buffer.length);

			// 添加到窗口
			if (!window.add(new DatagramPacket(b, b.length,
					packet.getAddress(), packet.getPort()))) {
				System.out.println("数据包序号不在期望序号内，被丢弃");
				System.out.println("");
			}

		}// end of while
	}

}

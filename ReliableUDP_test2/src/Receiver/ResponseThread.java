package Receiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import Flyweight.Msg;

public class ResponseThread extends Thread {

	private ReceiveWindow window;
	private DatagramSocket dataSocket;
	private int pointer;// 期待删除的序号
	private int preDelete[]; // 预删除包的集合

	public ResponseThread(DatagramSocket dataSocket, ReceiveWindow window) {
		this.dataSocket = dataSocket;
		this.window = window;
		this.pointer = 0;
		this.preDelete = new int[window.getWindowSize()];

		for (int i = 0; i < window.getWindowSize(); i++) {
			preDelete[i] = -1;
		}

		System.out.println("服务器回复线程启动。。。");
	}

	public void run() {
		try {
			while (true) {
				// System.out.println("hahahahahha");

				// 不暂停的话不执行？？？？？？？？？？？？？？？？
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				DatagramPacket packet = window.peek();

				if (packet != null) {
					Msg msg = new Msg(packet.getData());

					// 发送确认包
					String a = String.valueOf(msg.getID());
					packet.setData(a.getBytes());
					dataSocket.send(packet);
					System.out.println("回复确认消息：" + msg.getID());

					// 确认之后的包就是预删除的包
					int position = msg.getID() % window.getWindowSize();
					preDelete[position] = msg.getID();

					System.out.println("pointer:" + pointer);

					// 删除连续缓冲区(循环块的头一定从position开始：[4][5][6][3])
					for (int i = position; i < position
							+ window.getWindowSize(); i++) {
						if (pointer == preDelete[i % window.getWindowSize()]) {
							window.delete(pointer);
							pointer = (pointer + 1) % window.getNumScope(); // 下一个期待删除的序号
							preDelete[i % window.getWindowSize()] = -1; // 重置
						}
					}

				}// end of if
			}// end of while
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
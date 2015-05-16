package Sender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ACKReceiverThread extends Thread {

	private SendWindow window;
	private DatagramSocket dataSocket;

	private int pointer;// 期待删除的序号
	private int preDelete[]; //预删除包的集合

	public ACKReceiverThread(DatagramSocket dataSocket, SendWindow window) {
		// 接收来自所有地址的包
		this.dataSocket = dataSocket;
		this.window = window;

		this.pointer = 0;
		this.preDelete = new int[window.getWindowSize()];

		for (int i = 0; i < window.getWindowSize(); i++) {
			preDelete[i] = -1;
		}

		System.out.println("客户端ACK接收线程启动。。。");
	}

	private byte[] buffer = new byte[1024];

	public void run() {
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		try {
			// 循环等待接受确认包
			while (true) {
				dataSocket.receive(packet);

				String info = new String(packet.getData(), 0,
						packet.getLength());
				int ACKnum = Integer.parseInt(info);
				System.out.println("接收到确认消息：" + ACKnum);

				int position = ACKnum % window.getWindowSize();
				preDelete[position] = ACKnum;

				//System.out.println("pointer:" + pointer);

				// 删除连续缓冲区(循环块的头一定从position开始：[4][5][6][3])
				for (int i = position; i < position + window.getWindowSize(); i++) {
					if (pointer == preDelete[i % window.getWindowSize()]) {
						window.delete(pointer);
						pointer = (pointer + 1) % window.getNumScope(); // 下一个期待删除的序号
						preDelete[i % window.getWindowSize()] = -1; // 重置
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

package Sender;

import java.net.DatagramPacket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Flyweight.Msg;

public class SendWindow {

	private int windowSize;
	private DatagramPacket[] buffer; 

	private Lock lock = new ReentrantLock();// 上锁:插入和删除互斥

	public SendWindow(int windowSize) {
		this.windowSize = windowSize;
		buffer = new DatagramPacket[windowSize];

		System.out.println("发送端窗口创建成功，窗口大小：" + windowSize);
	}

	/** ———————————————————————————————————————————————————————— */

	// 窗口是否已满
	public boolean isFull() {
		boolean flag = true;
		for (int i = 0; i < windowSize; i++) {
			if (buffer[i] == null) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	// 插入一个缓冲区
	public void add(DatagramPacket p) {
		lock.lock();

		Msg msg = new Msg(p.getData());
		int msgID = msg.getID();
		int pointer = msgID % windowSize;// 插入位置

		// 当前缓冲区是否有数据
		if (buffer[pointer] == null) {
			buffer[pointer] = p;

			System.out.println("窗口插入包：" + msgID);
			printWindowContent();// //////////////////////////////
		}

		lock.unlock();
	}

	// 删除一个缓冲区
	public void delete(int ACK) {
		lock.lock();

		int position = ACK % windowSize; // 删除的位置
		if (buffer[position] != null) {
			buffer[position] = null;

			System.out.println("窗口删除包：" + ACK);
			printWindowContent();// //////////////////////////////
		}
		lock.unlock();
	}

	private int pointer = 0;
	
	// 查看一个窗口元素
	public DatagramPacket peek() {		
		if (buffer[pointer] != null) {
			int position = pointer;

			Msg msg = new Msg(buffer[position].getData());
			System.out.println("peek()：" + msg.getID());

			pointer = (pointer + 1) % windowSize;
			return buffer[position];
		}
		return null;
	}

	/** ———————————————————————————————————————————————————————— */

	public void printWindowContent() {
		for (int i = 0; i < windowSize; i++) {
			if (buffer[i] != null) {
				Msg msg = new Msg(buffer[i].getData());
				System.out.println("【窗口数据包：" + msg.getID() + "】");
			} else {
				System.out.println("【窗口数据包：" + buffer[i] + "】");
			}
		}
	}

	public int getWindowSize() {
		return windowSize;
	}

	public int getNumScope() {
		return windowSize * 2;
	}

}

package Receiver;

import java.net.DatagramPacket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Flyweight.Msg;

public class ReceiveWindow {
	private int windowSize;
	private DatagramPacket[] buffer; 

	private int[] expectNumArray; // 期望的序号

	private Lock lock = new ReentrantLock();// 上锁:插入和删除不能同时进行

	public ReceiveWindow(int windowSize) {
		this.windowSize = windowSize;
		buffer = new DatagramPacket[windowSize];
		pointer = 0;

		expectNumArray = new int[windowSize];
		// 初始时期望0、1、2、3
		for (int i = 0; i < this.windowSize; i++) {
			expectNumArray[i] = i;
		}

		System.out.println("接收端窗口创建成功，窗口大小：" + windowSize);
	}

	/** ———————————————————————————————————————————————————————— */

	// 插入的包是否在期望序号内
	private boolean isExpected(DatagramPacket packet) {
		Msg msg = new Msg(packet.getData());
		int msgID = msg.getID();

		for (int i = 0; i < windowSize; i++) {
			if (msgID == expectNumArray[i]) {
				return true;
			}
		}
		return false;
	}

	// 插入一个缓冲区
	public boolean add(DatagramPacket p) {
		lock.lock();

		Msg msg = new Msg(p.getData());
		int msgID = msg.getID();

		if (isExpected(p)) {
			int position = msgID % windowSize;// 插入位置

			// 当前缓冲区是否有数据
			if (buffer[position] == null) {
				buffer[position] = p;
				System.out.println("窗口插入包：" + msgID);

				printWindowContent();// //////////////////////////////
				lock.unlock();
				return true;
			}
		}
		lock.unlock();
		return false;
	}

	// 删除一个缓冲区
	public void delete(int num) {
		lock.lock();

		int position = num % windowSize; // 删除的位置
		if (buffer[position] != null) {
			buffer[position] = null;

			System.out.println("窗口删除包：" + num);
			printWindowContent();// //////////////////////////////

			// 改变期望序号(即滑动窗口)
			expectNumArray[position] = (expectNumArray[position] + windowSize)
					% getNumScope();
		}
		lock.unlock();
	}

	private int pointer;

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

	private void printWindowContent() {
		for (int i = 0; i < windowSize; i++) {
			if (buffer[i] != null) {
				Msg msg = new Msg(buffer[i].getData());
				System.out.println("【窗口数据包：" + msg.getID() + "】");
			} else {
				System.out.println("【窗口数据包：" + null + "】");
			}
		}
		System.out.println("");
	}

	public void printExpectNum() {
		for (int i = 0; i < windowSize; i++) {
			System.out.println("-> 期待的数据包：" + expectNumArray[i]);
		}
	}

	public int getWindowSize() {
		return windowSize;
	}

	public int getNumScope() {
		return windowSize * 2;
	}
}

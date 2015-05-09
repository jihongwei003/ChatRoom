package Server.MsgCollection;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class MsgQueue extends MsgCollection {

	private Queue<String> queue = new LinkedBlockingQueue<String>();

	private static MsgQueue s_msgQueue;

	private MsgQueue() {

	}

	public static MsgQueue getInstance() {
		if (s_msgQueue == null) {
			s_msgQueue = new MsgQueue();
			System.out.println("服务器消息队列创建成功，等待接收消息。。。");
		} else {
			System.out.println("消息队列已经存在，返回已存在的实例");
		}
		return s_msgQueue;
	}

	@Override
	public  void addMsg(String msg) {
		queue.offer(msg); // 添加一个元素并返回true；如果队列已满，则返回false
	}

	@Override
	public  String getMsg() {
		String result = null;
		try {
			result = ((LinkedBlockingQueue<String>) queue).take();// pull移除并返问队列头部的元素；如果队列为空，则返回null
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * put 添加一个元素 如果队列满，则阻塞; take 移除并返回队列头部的元素 如果队列为空，则阻塞
	 */

}

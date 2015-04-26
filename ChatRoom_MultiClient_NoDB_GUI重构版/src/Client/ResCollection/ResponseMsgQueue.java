package Client.ResCollection;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


public class ResponseMsgQueue extends ResponseMsgCollection{

	private Queue<String> queue = new LinkedBlockingQueue<String>();

	private static ResponseMsgQueue s_msgQueue;

	private ResponseMsgQueue() {

	}

	public static ResponseMsgQueue getInstance() {
		if (s_msgQueue == null) {
			s_msgQueue = new ResponseMsgQueue();
			System.out.println("消息队列创建成功");
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
			result = ((LinkedBlockingQueue<String>) queue).take();// 阻塞式的取
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}

}

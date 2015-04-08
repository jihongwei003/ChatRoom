import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
	public static void main(String[] args){

		// 创建线程池，处理过程中将任务添加到队列，然后在创建线程后自动启动这些任务
		ExecutorService exec = Executors.newCachedThreadPool();
		// 新建线程
		exec.execute(new SingleThread(1));
		exec.execute(new SingleThread(2));
		exec.execute(new SingleThread(3));

		//exec.close();
	}
}

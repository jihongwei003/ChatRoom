package cn.bupt.ji;

//新建线程类需要继承Thread父类，并且实现run()方法，该方法在调用start()后自动执行
public class Customthread extends Thread {
	public Customthread() {

	}

	public void run() {
		while(true){
			try {
				Thread.sleep(100);
				System.out.println("我是线程2");	
			} catch (InterruptedException e) {
				e.printStackTrace();
			}		
		}//end of while
	}
}

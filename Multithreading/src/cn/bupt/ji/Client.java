package cn.bupt.ji;

public class Client {
	public static void main(String[] args) {
		//创建一条线程
		Customthread thread2 = new Customthread();
		thread2.start();
		
		//主线程
		while(true){
			try {
				Thread.sleep(1000);
				System.out.println("我是主线程");	
			} catch (InterruptedException e) {
				e.printStackTrace();
			}		
		}//end of while
	}

}

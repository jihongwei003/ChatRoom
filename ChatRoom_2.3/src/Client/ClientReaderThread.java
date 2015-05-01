package Client;

import java.io.BufferedReader;
import java.io.IOException;
//import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

import Tools.Bulider.BufferedReaderBuilder;
import Tools.Bulider.Director;
import Client.ResCollection.ResponseCollectionManager;

//客户端的读取线程
public class ClientReaderThread extends Thread {

	private Socket socket;
	private ResponseCollectionManager resCollection;
	
	public ClientReaderThread(Socket socket) {
		this.socket = socket;
		this.resCollection = null;
	}
	
	public void setResponseCollectionManager(ResponseCollectionManager m){
		this.resCollection = m;
	}

	public void run() {
		// 指导者
		Director director = new Director(new BufferedReaderBuilder(socket));
		try {
			// 使用指导者生成reader
			BufferedReader reader = (BufferedReader) director.construct();

			String receiveMsg = "";
			while (true) {
				//接收来自服务器的消息
				receiveMsg = reader.readLine();
				
				//存入消息队列
				resCollection.addMsg(receiveMsg);				
				//System.out.println(receiveMsg);
			}
			// reader.close();// 关闭流会导致socket的关闭！！
		} catch (SocketException e) {
			System.out.print("客户端socket已关闭！");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.print("读进程抛出异常！");
			e.printStackTrace();
		} finally {
			System.out.println("读进程执行完毕！");
		}
	}

}

package test;

import java.net.*;
import java.io.*;

public class Server {

	private Socket socket = null;

	public void runServer() {
		ServerSocket server = null;
		try {
			server = new ServerSocket(1234);
			System.out.println("服务器启动，开始监听。。。");
			
			socket = server.accept();
			System.out.println("接受客户：" + socket.getInetAddress()
					+ "的连接请求，开始通信。。。");
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
	
	public void getFile() {
		byte[] b = new byte[1024];
		
		try { 
			DataInputStream din = new DataInputStream(new BufferedInputStream(
					socket.getInputStream())); 
			// 创建要保存的文件
			File f = new File("C://Users//JiHongwei//Desktop//copy.jpg");

			RandomAccessFile fw = new RandomAccessFile(f, "rw");
			
			System.out.println("准备接收文件");
			
			// 向文件中写入0~num个字节
			int num = din.read(b); //阻塞
			while (num != -1) { 
				fw.write(b, 0, num); // 跳过num个字节再次写入文件
				fw.skipBytes(num); // 读取num个字节
				num = din.read(b);
			} 
			
			System.out.println("接收文件完毕");
			
			// 关闭输入，输出流
			din.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		Server server = new Server();
		server.runServer();
		server.getFile();
	}

}

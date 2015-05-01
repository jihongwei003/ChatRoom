package Client;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

//客户端的写进程
public class ClientWriterThread extends Thread {

	private Socket socket;

	public ClientWriterThread(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		//指导者
		Director director = new Director(new SocketWriterBuilder(socket));
		
		// 使用Scanner从键盘读入数据
		Scanner in = new Scanner(System.in);
 
		try {
			//使用指导者生成一个writer
			PrintWriter writer = (PrintWriter) director.construct();

			String sendMsg = "";

			while (!sendMsg.equalsIgnoreCase("4")) {
				sendMsg = in.next();
				writer.println(sendMsg);
				writer.flush();
			}
			System.out.println("bye!");
			writer.close();
			in.close();

		} catch(SocketException e){
			System.out.print("客户端socket已关闭！");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

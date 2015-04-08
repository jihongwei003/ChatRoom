import java.net.*;
import java.io.*;

public class Client {

	public static void main(String[] args) throws IOException {
		// 向服务器发出连接请求
		Socket socket = new Socket("localhost", 8888);

		try {
			// 创建输入流对象，用来读数据
			DataInputStream dis = new DataInputStream(new BufferedInputStream(
					socket.getInputStream()));

			Thread msr = new ReaderThread(dis);
			msr.start();

			// 创建输出流对象，用来写数据
			DataOutputStream dos = new DataOutputStream(
					new BufferedOutputStream(socket.getOutputStream()));

			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(isr);

			String msg;
			try {
				while (true) {
					msg = br.readLine();
					System.out.println("客户端说：" + msg);
					
					dos.writeUTF(msg);
					dos.flush();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		} catch (SocketException e) {
			System.out.println(e);
		} finally {
			socket.close();
		}
	}
}

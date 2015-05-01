import java.io.IOException;
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) throws IOException {
		// TODO 自动生成的方法存根
		// 向服务器发出连接请求
		Socket socket = new Socket("localhost", 8888);
		try {
			// 输入流是【对方的输入流】，从输入流中read()读数据
			// 输出流是【自己的输出流】，向输出流中write()写数据
			DataInputStream in = new DataInputStream(new BufferedInputStream(
					socket.getInputStream()));
			DataOutputStream out = new DataOutputStream(
					new BufferedOutputStream(socket.getOutputStream()));

			// 使用Scanner读取自己键入的数据
			Scanner scanner = new Scanner(System.in);

			boolean stop = false;
			while (!stop) {
				System.out.println("请输入正方形的边长：");

				double length = scanner.nextDouble();// 读取一个输入的double

				out.writeDouble(length);// 给输出流写入一个数据
				out.flush();

				double area = in.readDouble();// 从输入流读出一个数据
				System.out.println("从服务器接受的正方形面积：" + area);

				while (true) {
					System.out.println("继续？（Y/N）");

					String str = scanner.next();// 读取一个输入的String
					if (str.equalsIgnoreCase("N")) {
						out.writeInt(0);
						out.flush();
						
						stop = true;
						
						break;
					} else if (str.equalsIgnoreCase("Y")) {
						out.writeInt(1);
						out.flush();
						
						break;
					}
				}
			}
			scanner.close();
		} finally {
			socket.close();
		}
	}
}

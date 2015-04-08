import java.io.*;

/** 创建一个进程用来读取【对方输入流】的数据 */
public class ReaderThread extends Thread {
	private DataInputStream dis; // DataInput

	public ReaderThread(DataInputStream dis) {
		this.dis = dis;
	}

	public void run() {
		String msg;
		try {
			while (true) {
				/*
				 * readUTF()该方法读取使用 UTF-8 修改版格式编码的 Unicode 字符串的表示形式； 然后以 String
				 * 的形式返回此字符串。
				 */
				msg = dis.readUTF();
				System.out.println("对方说:" + msg);
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
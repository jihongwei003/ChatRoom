package cn.bupt.ji.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;

/** 线程池类实现了Runnable接口 */
class SingleServer implements Runnable {
	UserDB users;
	private Socket socket;

	// 构造方法
	public SingleServer(UserDB users, Socket socket) {
		this.users = users;
		this.socket = socket;
	}

	/** 私聊客户端（from） ， name（from），friendSocket（to） */
	public void Client(BufferedReader reader, String name, Socket friendSocket) {
		try {
			System.out.println(name + "与" + friendSocket.getInetAddress()
					+ "进入聊天室"); 
			
			/* 用目标用户的套接字创建数据流 */
			OutputStream socketOut = friendSocket.getOutputStream();
			PrintWriter pw = new PrintWriter(socketOut, true);// 不会用？？？

			String sendmsg = "";
			
			while (!sendmsg.equals("bye")) {
				String str = reader.readLine();// （参数BufferedReader）客户端发过来的数据
				sendmsg = str;
				str = name + " 说：" + str;

				pw.println(str);// 向目标客户端缓冲区发送数据
				System.out.println(name + " 说：" + sendmsg);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("用户退出私聊室");
		return;
	}

	// 创建实例时自动调用
	public void run() {
		InputStream input;
		try {
			// 创建数据流（已连接客户端的socket）
			input = socket.getInputStream();

			InputStreamReader isreader = new InputStreamReader(input);
			BufferedReader reader = new BufferedReader(isreader);// Buffer的读取器

			OutputStream socketOut = socket.getOutputStream();// 用来处理信息（回复客户端）
			PrintWriter pw = new PrintWriter(socketOut, true);

			pw.println("请输入昵称：");
			// 获取用户输入的第一行字符作为该用户的昵称
			String name = reader.readLine();

			// 将用户昵称和其套接字添加到HashMap
			users.addUser(name, socket);
			pw.println("用户名设置成功！\n"); 
			
			// 循环获取用户输入的信息，并给出对应的回应
			while (true) {
				pw.println("1.“群聊”开启群聊\n" + "2.“私聊”开启私聊\n" + "3.“List”显示在线好友\n"
						+ "4.“exit”退出\n");
				String str = reader.readLine();
				System.out.println(name + " 选择：" + str);

				switch (str) {
				case "4":
					System.out.println(socket.getInetAddress() + "已经退出！");
					// 用户退出，将用户信息从HashMap中删除
					users.deleteUser(name);
					break;

				/* 群聊就是将一条信息发给所有客户 */
				case "1":
					// 创建 局部变量 的迭代器
					// getSockets()返回当前的 Map
					pw.println("连接成功，进入【群聊】状态··\n");
					
					String sendmsg = "";
					while (!sendmsg.equals("bye")) {
						String stri = reader.readLine();// BufferedReader客户端发过来的数据
						sendmsg = stri;
						System.out.println(name + " 说：" + sendmsg);
						stri = name + " 说：" + stri;//发给目标客户端的

						Iterator<String> iter = users.getSockets().keySet()
								.iterator();
						
						while (iter.hasNext()) {//让map指回第一个？？？
							String key = iter.next();
							// 创建目标客户端socket的引用
							Socket friendSocket = users.getSocket(key);
							
							OutputStream socketOutMul = friendSocket
									.getOutputStream();
							PrintWriter pwTo = new PrintWriter(socketOutMul,
									true);
							
							pwTo.println(stri);
							pwTo.flush();
							
							//pwTo.close();
						}
					}
					break;

				case "2":
					pw.println("请输入好友的昵称：");
					String friend = reader.readLine();

					// 创建目标客户端socket的引用
					Socket friendSocket = users.getSocket(friend);// 键值比较
					if (friendSocket == null) {
						pw.println("好友不存在!");
					} else {
						pw.println("连接成功，进入【私聊】状态··\n");
						// 目标客户端名字,处理私聊进程
						Client(reader, name, friendSocket);
					}
					break;

				case "3":
					pw.println(users.GetAllUsers());
					break;

				default:
					pw.println("您输入的内容无法解析！");
					break;
				}
			}
			// reader.close();
			// isreader.close();
			// input.close();
			// pw.close();
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			System.out.println("与客户" + socket.getInetAddress() + "通信结束！");
			try {
				socket.close();//当前已连接的客户端
			} catch (IOException e) {
				System.out
						.println("与客户" + socket.getInetAddress() + "通信未正确关闭！");
			}
		}
	}
}

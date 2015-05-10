package Server.RequestHandlers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import net.sf.json.JSONObject;
import Server.ResTrans;
import Server.UserCollection.UserList;
import Tools.JsonTrans;
import Tools.Bulider.Director;
import Tools.Bulider.PrintWriterBuilder;
import Tools.MapKey;

public class SoundChatHandler extends RequestHandler {

	private Socket soundSocket;
	private Socket soundFriSocket;

	// 需要一个map对应不同个进程，这样打开、关闭时才知道要操作哪个
	private HashMap<MapKey, SoundTransProcess> processMap;

	public SoundChatHandler(UserList users) {
		super(users);
		processMap = new HashMap<MapKey, SoundTransProcess>();// MapKey(userName,friendName)
	}

	@Override
	public void handleRequest() {
		JSONObject json = (JSONObject) JsonTrans.parseJson(super.requestMsg,
				"msg");
		String name = json.getString("publisher");// 解析出消息的发送者
		String friendName = json.getString("receiver");

		// user的连接名
		String linkName = users.getOnlineUsers().get(name);
		Socket socket = users.getSocket(linkName);
		soundSocket = users.getSoundSocketsMap().get(linkName);

		// friend的连接名
		String linkFriName = users.getOnlineUsers().get(friendName);
		Socket friendSocket = users.getSocketsMap().get(linkFriName);
		soundFriSocket = users.getSoundSocketsMap().get(linkFriName);

		System.out.println(name + " 向 " + friendName + " 发起语音聊天");

		try {
			/* 通知被叫者准备接收、发送数据 */
			Director director = new Director(new PrintWriterBuilder(
					friendSocket));
			PrintWriter writerF = (PrintWriter) director.construct();

			// 将字符串打包成需要客户端解析的形式
			ResTrans trans = new ResTrans();
			trans.setPublisher(name);
			trans.setMsgNum("10");
			String result = trans.getResult();

			// 添加服务器的包头
			String output = JsonTrans.buildJson("res", result);

			writerF.println(output);
			writerF.flush();

			/* 通知主叫者准备发送、接收数据 */
			director = new Director(new PrintWriterBuilder(socket));
			writerF = (PrintWriter) director.construct();

			// 将字符串打包成需要客户端解析的形式
			trans = new ResTrans();
			trans.setPublisher(friendName);
			trans.setMsgNum("10");
			result = trans.getResult();

			// 添加服务器的包头
			output = JsonTrans.buildJson("res", result);

			writerF.println(output);
			writerF.flush();

			MapKey key = new MapKey(name, friendName);
			SoundTransProcess process = processMap.get(key);
			if (null == process) {
				process = new SoundTransProcess();
				//存入map
				processMap.put(key, process);
			}
			process.runProcess();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class SoundTransProcess {

		public SoundTransProcess() {
		}

		public void runProcess() {
			// 开启转发线程
			thread = new SoundTransThread();
			thread.setDaemon(true);
			thread.start();

			// 开启转发线程2
			// thread2 = new SoundTransThread2();
			// thread2.setDaemon(true);
			// thread2.start();
		}

		private SoundTransThread thread;

		// private SoundTransThread2 thread2;

		// 转发语音数据 主叫者->被叫者
		public class SoundTransThread extends Thread {
			public void run() {
				byte[] buffer = new byte[1024];

				try {
					BufferedOutputStream outputStream = new BufferedOutputStream(
							soundSocket.getOutputStream());
					BufferedInputStream inputStream = new BufferedInputStream(
							soundFriSocket.getInputStream());

					int n = 0;
					while ((n = inputStream.read(buffer, 0, buffer.length)) != -1) {
						if (n > 0) {
							outputStream.write(buffer, 0, n);
							outputStream.flush();
						}
					}// end of while
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// 转发语音数据 被叫者->主叫者
		public class SoundTransThread2 extends Thread {
			public void run() {
				byte[] buffer = new byte[1024];

				try {
					BufferedOutputStream outputStream = new BufferedOutputStream(
							soundFriSocket.getOutputStream());
					BufferedInputStream inputStream = new BufferedInputStream(
							soundSocket.getInputStream());

					int n = 0;
					while ((n = inputStream.read(buffer, 0, buffer.length)) != -1) {
						if (n > 0) {
							outputStream.write(buffer, 0, n);
							outputStream.flush();
						}
					}// end of while
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		@SuppressWarnings("deprecation")
		public void stopSoundChatThreads() {
			thread.stop();
			// thread2.stop();
		}
	}

	public void stopTransProcess(MapKey key) {
		SoundTransProcess process = processMap.get(key);

		if (null != process) {
			process.stopSoundChatThreads();
			// 移除
			processMap.remove(key);
		}
	}

}

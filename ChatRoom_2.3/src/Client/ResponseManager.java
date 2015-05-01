package Client;

import java.net.Socket;

import net.sf.json.JSONObject;
import Client.ResCollection.ResponseCollectionManager;
import Tools.JsonTrans;

public class ResponseManager {

	private ResponseCollectionManager resCollection;
	private Socket socket;
	private String userName;

	private HomePage homePage;

	private String friendListJsonTemp;

	public ResponseManager(Socket socket, String userName) {
		this.resCollection = null;
		this.socket = socket;
		this.userName = userName;
	}

	// 需要依赖注入
	public void setResponseCollectionManager(
			ResponseCollectionManager resCollection) {
		this.resCollection = resCollection;
	}

	public void runManager() {
		// 创建主页窗口
		homePage = new HomePage(socket, userName);
		homePage.setManager(this);// 依赖注入

		ResDistributeThread thread = new ResDistributeThread();
		thread.start();

	}

	// 提供给homePage使用的方法，双向依赖，called
	public void updateFriendListStr() {
		homePage.setFriendListStr(friendListJsonTemp);
	}

	public class ResDistributeThread extends Thread {
		public void run() {
			while (true) {
				String msg = resCollection.getMsg(); // 阻塞式的取
				System.out.println("取出来自Server的回应消息为：" + msg);

				try {
					// 解析服务器包头
					JSONObject json = (JSONObject) JsonTrans.parseJson(msg,
							"res");
					String key = json.getString("msgNum");// 解析出的消息编号

					// 群聊的时候解析不了单引号了？？？？？？？？？
					
					// 根据消息编号分发消息，先用if-else凑合一下
					if (key.equals("1")) {
						// 接收到“1”，就把它存在一个字段里，等待homePage调用更新方法
						friendListJsonTemp = msg;

						// 通知homePage更新好友列表
						homePage.updateFriendList();
					}

					if (key.equals("2")) {
						homePage.getMsgQueue().offer(msg); // 私聊信息
					}
					
					if (key.equals("3")) {
						homePage.getMsgGroupQueue().offer(msg); // 群聊信息
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}

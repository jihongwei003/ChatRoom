package Client.ResponseHandlers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import net.sf.json.JSONObject;
import Client.Pages.ChatBox;
import Client.Pages.SoundChatBox;
import Tools.JsonTrans;

public class ResSoundChatHandler extends ResponseHandler {

	private Socket soundSocket;

	// 管理已经存在的语音聊天线程
	private Map<String, SoundChatProcess> soundChatThreadsMap;

	public ResSoundChatHandler(Socket socket, Socket soundSocket) {
		super(socket);
		this.soundSocket = soundSocket;
		soundChatThreadsMap = new HashMap<String, SoundChatProcess>();
	}

	@Override
	public void handleResponse() {
		System.out.println("语音聊天");

		JSONObject json = (JSONObject) JsonTrans.parseJson(super.responseMsg,
				"res");
		String friendName = json.getString("publisher");

		// 从map中找到对应的好友名的语音聊天handler
		SoundChatProcess process = soundChatThreadsMap.get(friendName);
		if (null == process) {
			process = new SoundChatProcess(friendName);
			// 存入
			soundChatThreadsMap.put(friendName, process);
		}
		process.runProcess();

	}

	// 音频处理进程
	public class SoundChatProcess {
		private String friendName;

		public SoundChatProcess(String friendName) {
			this.friendName = friendName;
		}

		public void runProcess() {
			// 得到私聊窗口实例
			ChatBox c = ChatBox.getInstance(socket,
					SetNameHandler.getRealName(), friendName);
			c.setTitle("Sound Chat Box");

			// 得到语音聊天窗口实例
			SoundChatBox soundChatBox = SoundChatBox.getInstance(socket,
					SetNameHandler.getRealName(), friendName);
			soundChatBox.setTitle("Voice Chat");

			// 语音聊天窗口的“开始”按钮设置为disable
			soundChatBox.jbtOK.setEnabled(false);

			// 新建线程，开启声音播放器
			thread = new SoundPlaybackThread();
			thread.setDaemon(true);
			thread.start();

			// 新建线程， 开启声音采集器
			thread2 = new SoundCaptureThread();
			thread2.setDaemon(true);
			thread2.start();
		}

		private SoundPlaybackThread thread;
		private SoundCaptureThread thread2;

		private SourceDataLine sourceDataLine;
		private TargetDataLine targetDataLine;

		public class SoundCaptureThread extends Thread {
			public void run() {
				AudioFormat format = new AudioFormat(8000, 16, 2, true, true);
				try {
					BufferedOutputStream captrueOutputStream = new BufferedOutputStream(
							soundSocket.getOutputStream());

					// 1.取得输入设备信息
					DataLine.Info info = new DataLine.Info(
							TargetDataLine.class, format);

					// 2.取得输入设备
					targetDataLine = (TargetDataLine) AudioSystem.getLine(info);

					// 3.打开输入设备
					targetDataLine.open(format, targetDataLine.getBufferSize());

					// 4.开始录音
					targetDataLine.start();

					byte[] data = new byte[1024];
					while (true) {
						if (targetDataLine != null) {
							// 5.读取录音数据
							int cnt = targetDataLine.read(data, 0, 128);// 取数据（1024）的大小直接关系到传输的速度，一般越小越快
							if (cnt > 0) {
								// 6.发送录音数据
								captrueOutputStream.write(data, 0, cnt);
								captrueOutputStream.flush();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		private final int bufSize = 16384;

		public class SoundPlaybackThread extends Thread {
			public void run() {
				AudioFormat format = new AudioFormat(8000, 16, 2, true, true);
				try {
					BufferedInputStream playbackInputStream = new BufferedInputStream(
							new AudioInputStream(soundSocket.getInputStream(),
									format, 2147483647));

					// 1.取得输出设备信息
					DataLine.Info info = new DataLine.Info(
							SourceDataLine.class, format);

					// 2.取得输出设备（AudioSystem起着工厂的作用）
					sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);

					// 3.打开输出设备
					sourceDataLine.open(format, bufSize);

					// 4.开始播放
					sourceDataLine.start();

					byte[] data = new byte[1024];
					int cnt;
					// 6.读取数据到缓存数据
					while ((cnt = playbackInputStream
							.read(data, 0, data.length)) != -1) {
						if (cnt > 0) {
							if (sourceDataLine != null)
								// 7.播放缓存数据，通过此源数据行将音频数据写入混频器
								sourceDataLine.write(data, 0, cnt);
						}

					}
					// Block等待临时数据被输出为空
					sourceDataLine.drain();
					sourceDataLine.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}// end of class SoundPlaybackThread

		@SuppressWarnings("deprecation")
		public void stopSoundChatProcess() {
			if (null != sourceDataLine) {
				sourceDataLine.stop();
				sourceDataLine.close();
			}
			sourceDataLine = null;

			if (null != targetDataLine) {
				targetDataLine.stop();
				targetDataLine.close();
			}
			targetDataLine = null;

			thread.stop();
			thread2.stop();
		}
	}

	public void stopSoundChatThreads(String friendName) {
		// 找到对应的处理进程
		SoundChatProcess process = soundChatThreadsMap.get(friendName);
		if (null != process) {
			process.stopSoundChatProcess();
			// 移除
			soundChatThreadsMap.remove(friendName);
		}
	}

}

import java.io.BufferedInputStream;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/**
 * javaSound API 音频的播放
 * 
 * 音频的输出类：SoundDataLine 代表输出设备
 * 实现了Line接口。Line接口用来关闭/打开设备、注册事件监听器，以及提供一些用来调整声音效果的对象
 */

public class SoundPlayback extends Thread {

	private final int bufSize = 16384;

	private SourceDataLine sourceDataLine;// 音频数据格式对象

	private Socket socket;
	private BufferedInputStream playbackInputStream;

	public SoundPlayback(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		AudioFormat format = new AudioFormat(8000, 16, 2, true, true);
		try {
			playbackInputStream = new BufferedInputStream(new AudioInputStream(
					socket.getInputStream(), format, 2147483647));

			// 1.取得输出设备信息
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

			// 2.取得输出设备（AudioSystem起着工厂的作用）
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);

			// 3.打开输出设备
			sourceDataLine.open(format, bufSize);

			// 4.开始播放
			sourceDataLine.start();

			byte[] data = new byte[1024];
			int cnt;
			// 6.读取数据到缓存数据
			while ((cnt = playbackInputStream.read(data, 0, data.length)) != -1) {
				if (cnt > 0) {
					// 7.播放缓存数据，通过此源数据行将音频数据写入混频器
					sourceDataLine.write(data, 0, cnt);
				}

			}
			// Block等待临时数据被输出为空
			sourceDataLine.drain();
			sourceDataLine.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sourceDataLine.stop();
			sourceDataLine.close();
			sourceDataLine = null;
		}
	}

}

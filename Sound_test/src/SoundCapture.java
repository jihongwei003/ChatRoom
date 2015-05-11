import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

/**
 * javaSound API 音频的录制
 * 
 * 音频的输入类：TargetDataLine 代表输入设备
 * 实现了Line接口。Line接口用来关闭/打开设备、注册事件监听器，以及提供一些用来调整声音效果的对象
 */

public class SoundCapture extends Thread {

	private TargetDataLine targetDataLine;// 输入设备

	private Socket socket;
	private BufferedOutputStream captrueOutputStream;// 输出录制的声音

	public SoundCapture(Socket socket) throws IOException {
		this.socket = socket;
	}

	public void run() {
		// 音频的数据格式
		// 音频数据——也就是从TargetDataLine输入或从SourceDataLine输出的数据，必须符合音频格式的标准
		// AudioFormat(float sampleRate,
		// int sampleSizeInBits,
		// int channels,
		// boolean signed,
		// boolean bigEndian）
		AudioFormat format = new AudioFormat(8000, 16, 2, true, true);
		try {
			captrueOutputStream = new BufferedOutputStream(
					socket.getOutputStream());

			// 1.取得输入设备信息
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

			// 2.取得输入设备
			targetDataLine = (TargetDataLine) AudioSystem.getLine(info);

			// 3.打开输入设备
			targetDataLine.open(format, targetDataLine.getBufferSize());

			// 4.开始录音
			targetDataLine.start();

			byte[] data = new byte[1024];
			while (true) {
				// 5.读取录音数据
				int cnt = targetDataLine.read(data, 0, 128);// 取数据（1024）的大小直接关系到传输的速度，一般越小越快

				if (cnt > 0) {
					// 6.发送录音数据
					captrueOutputStream.write(data, 0, cnt);
					captrueOutputStream.flush();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			targetDataLine.stop();
			targetDataLine.close();
			targetDataLine = null;

			try {
				captrueOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

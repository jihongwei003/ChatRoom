import java.io.*;
import java.net.*;

public class UdpClient {

	private DatagramSocket ds = null;

	public UdpClient() throws Exception {
		ds = new DatagramSocket();
	}

	// 向指定的服务端发送数据信息.
	public void send(String host, int port, byte[] bytes) throws IOException {
		DatagramPacket dp = new DatagramPacket(bytes, bytes.length,
				InetAddress.getByName(host), port);
		ds.send(dp);
	}

	private byte[] buffer = new byte[1024];

	// 接收从指定的服务端发回的数据.
	public String receive() throws Exception {
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
		ds.receive(dp);

		String info = new String(dp.getData(), 0, dp.getLength());
		return info;
	}

	public final void close() {
		try {
			ds.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		UdpClient client = new UdpClient();
		
		String serverHost = "127.0.0.1";
		int serverPort = 3344;

		client.send(serverHost, serverPort, ("你好，阿蜜果!").getBytes());

		String info = client.receive();
		System.out.println("服务端回应数据：" + info);
	}
}

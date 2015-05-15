import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class UdpServer {

	private DatagramSocket ds = null; // 相当于client socket

	public UdpServer(String host, int port) {
		InetSocketAddress socketAddress = new InetSocketAddress(host, port);
		try {
			ds = new DatagramSocket(socketAddress);

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("服务端启动!");
	}

	private byte[] buffer = new byte[1024];
	private DatagramPacket packet = null; // 接收到的报文

	// 接收数据包，该方法会造成线程阻塞.返回接收的数据串信息
	public void receive() throws IOException {
		packet = new DatagramPacket(buffer, buffer.length);
		ds.receive(packet);

		String info = new String(packet.getData(), 0, packet.getLength());
		System.out.println("接收信息：" + info);
	}

	// 将响应包发送给请求端.回应报文
	public void response(String info) throws IOException {
		System.out.println("客户端地址 : " + packet.getAddress().getHostAddress()
				+ ",端口：" + packet.getPort());

		DatagramPacket dp = new DatagramPacket(buffer, buffer.length,
				packet.getAddress(), packet.getPort());
		dp.setData(info.getBytes());

		ds.send(dp);
	}

	// 关闭udp监听口.
	public final void close() {
		try {
			ds.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		String serverHost = "127.0.0.1";
		int serverPort = 3344;

		UdpServer udpServerSocket = new UdpServer(serverHost, serverPort);

		while (true) {
			udpServerSocket.receive();
			udpServerSocket.response("你好,sterning!");

		}
	}
}

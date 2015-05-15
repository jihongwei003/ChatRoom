import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class UdpServerSocket {
	private byte[] buffer = new byte[1024];

	private DatagramSocket ds = null;

	private DatagramPacket packet = null;

	private InetSocketAddress socketAddress = null;

	private String orgIp;

	// 构造函数，绑定主机和端口.
	public UdpServerSocket(String host, int port) throws Exception {
		socketAddress = new InetSocketAddress(host, port);
		ds = new DatagramSocket(socketAddress);
		System.out.println("服务端启动!");
	}

	public final String getOrgIp() {
		return orgIp;
	}

	// 设置超时时间，该方法必须在bind方法之后使用.
	public final void setSoTimeout(int timeout) throws Exception {
		ds.setSoTimeout(timeout);
	}

	// 获得超时时间.
	public final int getSoTimeout() throws Exception {
		return ds.getSoTimeout();
	}

	// 绑定监听地址和端口.
	public final void bind(String host, int port) throws SocketException {
		socketAddress = new InetSocketAddress(host, port);
		ds = new DatagramSocket(socketAddress);
	}

	// 接收数据包，该方法会造成线程阻塞.返回接收的数据串信息
	public final String receive() throws IOException {
		packet = new DatagramPacket(buffer, buffer.length);
		ds.receive(packet);
		orgIp = packet.getAddress().getHostAddress();
		String info = new String(packet.getData(), 0, packet.getLength());
		System.out.println("接收信息：" + info);
		return info;
	}

	// 将响应包发送给请求端.回应报文
	public final void response(String info) throws IOException {
		System.out.println("客户端地址 : " + packet.getAddress().getHostAddress()
				+ ",端口：" + packet.getPort());
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length,
				packet.getAddress(), packet.getPort());
		dp.setData(info.getBytes());
		ds.send(dp);
	}

	// 设置报文的缓冲长度.
	public final void setLength(int bufsize) {
		packet.setLength(bufsize);
	}

	// 获得发送回应的IP地址.
	public final InetAddress getResponseAddress() {
		return packet.getAddress();
	}

	// 获得回应的主机的端口.
	public final int getResponsePort() {
		return packet.getPort();
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
		UdpServerSocket udpServerSocket = new UdpServerSocket(serverHost,
				serverPort);
		while (true) {
			udpServerSocket.receive();
			udpServerSocket.response("你好,sterning!");

		}
	}
}

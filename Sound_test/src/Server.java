import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public void runServer() {
		try {
			// 启动语音socket端口服务器
			ServerSocket serverSocket = new ServerSocket(6000);
			System.out.println("服务器启动");
			
			Socket client = serverSocket.accept();

			// 启动playback播放声音	
			SoundPlayback player = new SoundPlayback(client);
			player.run();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Server server = new Server();
		server.runServer();
	}
}

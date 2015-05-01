package Client;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

//客户端的读取线程
public class ClientReaderThread extends Thread{

	private Socket socket;
	
	public ClientReaderThread(Socket socket){
		this.socket = socket;	
	}
	
	public void run(){
		//指导者
		Director director = new Director(new SocketReaderBuilder(socket));
		try {
			//使用指导者生成reader
			BufferedReader reader = (BufferedReader) director.construct();
			
			String receiveMsg = "";
            while(!receiveMsg.equalsIgnoreCase("4")){
                receiveMsg = reader.readLine();
                System.out.println(receiveMsg);
            }
            reader.close();
		}catch(SocketException e){
			System.out.print("客户端socket已关闭！");
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
}

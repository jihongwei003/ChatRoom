package Client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

//从socket到bufferedReader的建造者
public class SocketReaderBuilder extends Builder{

	private Socket socket;
	
	public SocketReaderBuilder(){
		super();
		this.socket = null;
	}
	
	public SocketReaderBuilder(Socket socket){
		super();
		this.socket = socket;
	}
	
	@Override
	public Object getResult() throws IOException {
		InputStream input = socket.getInputStream();
		InputStreamReader isreader = new InputStreamReader(input);
		BufferedReader reader = new BufferedReader(isreader);
		
		return reader;
	}

}

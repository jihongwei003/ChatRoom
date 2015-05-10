package Tools.Bulider;

import java.io.IOException;

//指导者，用于指导生产所需的builder类对象
public class Director {

	private Builder builder;
	
	public Director(){
		builder = null;
	}
	
	public Director(Builder builder){
		this.builder = builder;
	}
	
	public Object construct() throws IOException{
		return builder.getResult();
	}
}

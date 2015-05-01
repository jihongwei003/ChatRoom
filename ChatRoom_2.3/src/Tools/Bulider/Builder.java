package Tools.Bulider;

import java.io.IOException;

//建造者抽象类
public abstract class Builder {

	public Builder(){}
	protected abstract Object getResult() throws IOException;
}

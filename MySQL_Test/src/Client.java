import java.sql.SQLException;

public class Client {
	public static void main(String args[]) throws SQLException {
		ItemPersistence cd = new ItemPersistence();
		cd.connectToDB();

		// Item item = new Item(3, "haha");
		// cd.Insert(item);

		//String selectStr = "select * from item";
		//cd.Select(selectStr);
		
		// cd.Update(3, "nimei");

		// cd.Delet(3);

		cd.CutConnection(cd.con);
	}
}

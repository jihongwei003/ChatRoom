import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemPersistence {

	public Connection con;
	private ResultSet result;

	/* 构造方法 */
	public ItemPersistence() {
		con = null;
		result = null;
	}

	/* 连接数据库 */
	public Connection connectToDB() {
		String driveName = "com.mysql.jdbc.Driver";// 驱动程序名
		String databaseURL = "jdbc:mysql://localhost/market";// URL指向要访问的数据库名
		String user = "root";// MySQL配置时的用户名
		String password = "root";// MySQL配置时的密码
		// Connection con = null;//在这里创建局部变量覆盖掉了成员变量！！！
		
		try {
			Class.forName(driveName);// 加载驱动
			System.out.println("成功加载数据库驱动程序！");
			con = DriverManager.getConnection(databaseURL, user, password);// 连接MySQL数据库
			System.out.println("连接数据库成功！");
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
		return con;
	}

	/* 断开数据库 */
	public void CutConnection(Connection con) throws SQLException {
		try {
			if (result != null)
				;
			if (con != null)
				;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (result != null)
				result.close();
			con.close();
			System.out.print("数据库关闭成功！");
		}
	}

	/* 插入数据 */
	public boolean Insert(Item item) {
		try {
			String insertsql = "insert into Item(item_id,item_name)"
					+ " values(?,?)";
			PreparedStatement ps = con.prepareStatement(insertsql);

			ps.setInt(1, item.getID());
			ps.setString(2, item.getName());

			int result = ps.executeUpdate();
			// ps.executeUpdate();无法判断是否已经插入
			if (result > 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/* 删除数据 */
	public boolean Delet(int ID) {
		try {
			String delsql = "delete from Item where item_id=" + ID;

			PreparedStatement ps = con.prepareStatement(delsql);
			int result = ps.executeUpdate(delsql);
			if (result > 0)
				return true;
		} catch (SQLException ex) {
			Logger.getLogger(Item.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}
	
	/* 修改信息 */
	public boolean Update(int ID, String newName) {
		try {
			String upsql = "update Item set item_name='" + newName
					+ "' where item_id=" + ID;

			PreparedStatement ps = con.prepareStatement(upsql);
			int result = ps.executeUpdate();// 返回行数或者0
			if (result > 0)
				return true;
		} catch (SQLException ex) {
			Logger.getLogger(Item.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	/* 查询数据 */
	public void Select(String sql) {
		try {
			Statement statement = con.createStatement();
			result = statement.executeQuery(sql);// sql:"select * from item"
			while (result.next()) {
				System.out.println(result.getString("item_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

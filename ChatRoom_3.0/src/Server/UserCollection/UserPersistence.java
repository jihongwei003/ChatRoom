package Server.UserCollection;

import java.io.Serializable;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class UserPersistence extends UserList implements Serializable {

	private static final long serialVersionUID = 1L;

	private Connection con;

	// 存储在线用户
	private HashMap<String, Socket> socketsMap = new HashMap<String, Socket>();

	public UserPersistence() {
		con = null;
		ConnectToDB();
	}

	// 连接数据库
	public void ConnectToDB() {
		String driveName = "com.mysql.jdbc.Driver";// 驱动程序名
		String databaseURL = "jdbc:mysql://localhost/chat_room";// URL指向要访问的数据库名
		String user = "root";// MySQL配置时的用户名
		String password = "root";// MySQL配置时的密码

		try {
			Class.forName(driveName);// 加载驱动
			System.out.println("成功加载数据库驱动程序！");
			con = DriverManager.getConnection(databaseURL, user, password);// 连接MySQL数据库
			System.out.println("连接数据库成功！");
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
	}

	// 断开数据库
	public void CutConnection(Connection con) throws SQLException {
		try {
			// if (result != null)
			// ;
			if (con != null)
				;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// if (result != null)
			// result.close();
			con.close();
			System.out.print("数据库关闭成功！");
		}
	}

	// 插入数据
	private boolean Insert(String name, Socket socket) {
		try {
			String insertsql = "insert into user(user_id, user_name)"
					+ " values(?,?)";
			PreparedStatement ps = con.prepareStatement(insertsql);

			ps.setInt(1, 0);// 数据库自增
			ps.setString(2, name);

			int result = ps.executeUpdate();
			// ps.executeUpdate();无法判断是否已经插入
			if (result > 0) {
				System.out.println("新注册用户：" + name);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// // 删除数据
	// private boolean Delete(String delsql) {
	// try {
	// PreparedStatement ps = con.prepareStatement(delsql);
	// int result = ps.executeUpdate(delsql);
	// if (result > 0)
	// return true;
	// } catch (SQLException ex) {
	// ex.printStackTrace();
	// }
	// return false;
	// }

	// 查询数据
	private ResultSet Select(String sql) {
		ResultSet result = null;
		try {
			Statement statement = con.createStatement();
			result = statement.executeQuery(sql);// sql:"select * from item"
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// 重写抽象父类方法
	
	// 注册的同时，让用户上线
	@Override
	public void addUser(String name, Socket socket) {
		Insert(name, socket);

		// 上线
		socketsMap.put(name, socket);
	}

	@Override
	public Socket getSocket(String name) {
		ResultSet result = Select("select user_name from user where user_name = \'"
				+ name + "\'");
		Socket socket = null;
		try {
			// 如果不为空
			if (!result.wasNull()) {
				socket = (Socket) socketsMap.get(name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return socket;
	}

	// 这个方法其实是下线方法
	@Override
	public void deleteUser(String name) {
		ResultSet result = Select("select user_name from user where user_name = \'"
				+ name + "\'");
		try {
			if (!result.wasNull()) {
				socketsMap.remove(name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getUsersList(String name) {
		// 已经废弃的方法！！！
		return null;
	}

	@Override
	public HashMap<String, Socket> getSocketsMap() {
		return socketsMap;
	}
}

/*
 * CREATE SCHEMA IF NOT EXISTS `chat_room` DEFAULT CHARACTER SET utf8 ; USE
 * `chat_room` ;
 * 
 * -- ----------------------------------------------------- -- Table
 * `chat_room`.`user` -- -----------------------------------------------------
 * CREATE TABLE IF NOT EXISTS `chat_room`.`user` ( `user_id` INT(11) NOT NULL
 * AUTO_INCREMENT, `user_name` VARCHAR(45) NULL DEFAULT NULL, PRIMARY KEY
 * (`user_id`)) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8;
 */

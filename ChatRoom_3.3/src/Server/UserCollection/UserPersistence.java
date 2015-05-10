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

	// 在线用户表
	private OnlineUserList onlineUserList;

	// 连接表
	private ConnectUserList connectUserList;

	// 文件传输连接表
	private FileSocketList fileSocketList;

	// 语音链接表
	private SoundSocketList soundSocketList;

	public UserPersistence() {
		onlineUserList = new OnlineUserList();
		connectUserList = new ConnectUserList();
		fileSocketList = new FileSocketList();
		soundSocketList = new SoundSocketList();
		
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
	private boolean Insert(String name) {
		try {
			String insertsql = "insert into user(user_id, user_name)"
					+ " values(?,?)";
			PreparedStatement ps = con.prepareStatement(insertsql);

			ps.setInt(1, 0);// 数据库自增
			ps.setString(2, name);

			int result = ps.executeUpdate();
			// ps.executeUpdate();无法判断是否已经插入
			if (result > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// 删除数据
	private boolean Delete(String name) {
		try {
			String delsql = "delete from user where user_name = \'" + name
					+ "\'";
			PreparedStatement ps = con.prepareStatement(delsql);
			int result = ps.executeUpdate(delsql);
			if (result > 0)
				return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return false;
	}

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

	/** ———————————————————————————————————————————————————————————————————————— */

	// 上线正式用户
	@Override
	public void loginFormalUser(String name, String linkName) {
		onlineUserList.loginFormalUser(name, linkName);
	}

	// 下线正式用户
	@Override
	public void logoutFormalUser(String name) {
		onlineUserList.logoutFormalUser(name);
	}

	// 返回在线好友<零时名字， 名字>
	@Override
	public HashMap<String, String> getOnlineUsers() {
		return onlineUserList.getOnlineUsersMap();
	}

	/** ———————————————————————————————————————————————————————————————————————— */

	// 查找临时用户
	@Override
	public Socket getSocket(String name) {
		return connectUserList.getSocket(name);
	}

	// 上线临时用户
	@Override
	public void loginUser(String name, Socket socket) {
		connectUserList.loginUser(name, socket);
	}

	// 下线临时用户
	@Override
	public void logoutUser(String name) {
		connectUserList.logoutUser(name);
	}

	// 存储与服务器正在建立连接的用户 <零时名字，socket>
	@Override
	public HashMap<String, Socket> getSocketsMap() {
		return connectUserList.getSocketsMap();
	}

	/** ———————————————————————————————————————————————————————————————————————— */

	// 文件socket
	@Override
	public void loginFileSocket(String linkName, Socket socket) {
		fileSocketList.loginFileSocket(linkName, socket);
	}

	@Override
	public void logoutFileSocket(String linkName) {
		fileSocketList.logoutFileSocket(linkName);
	}

	@Override
	public HashMap<String, Socket> getFileSocketsMap() {
		return fileSocketList.getFileSocketsMap();
	}

	/** ———————————————————————————————————————————————————————————————————————— */

	// 是否注册
	@Override
	public boolean isRegister(String name) {
		ResultSet result = Select("select user_name from user where user_name = \'"
				+ name + "\'");
		try {
			// result.next()取不到返回falser
			if (result.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// 注册用户
	@Override
	public void registUser(String name) {
		Insert(name);
		System.out.println("新注册用户：" + name);
	}

	// 注销用户
	@Override
	public void deleteUser(String name) {
		Delete(name);
		System.out.println("注销用户：" + name);
	}

	/** ———————————————————————————————————————————————————————————————————————— */

	// 语音socket
	@Override
	public void loginSoundSocket(String linkName, Socket socket) {
		soundSocketList.loginSoundSocket(linkName, socket);
	}

	@Override
	public void logoutSoundSocket(String linkName) {
		soundSocketList.logoutSoundSocket(linkName);
	}

	@Override
	public HashMap<String, Socket> getSoundSocketsMap() {
		return soundSocketList.getSoundSocketsMap();
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

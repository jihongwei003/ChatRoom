package test;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class JFileFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private Socket socket = null;
	
	private JTextField jtfFile;

	public JFileFrame() {
		setLocation(500, 250);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 150);
		setVisible(true);
		setLayout(new GridLayout(3, 1));

		JButton jbtUpload = new JButton("上传");
		JButton jbtBrowse = new JButton("浏览");

		jtfFile = new JTextField();
		jtfFile.setEditable(false);

		add(jtfFile);
		add(jbtBrowse);
		add(jbtUpload);

		jbtBrowse.addActionListener(new BtnBrowse_ActionAdapter(this));

		jbtUpload.addActionListener(new BtnUpload_ActionAdapter(this));

	}

	// 浏览按钮事件
	public void btnBrowse_actionPerformed(ActionEvent e) {

		FileDialog fd = new FileDialog(this, "上传文件", FileDialog.LOAD);
		fd.show();
		String jfPath = fd.getDirectory() + fd.getFile();

		if ("null".equals(jfPath) == false) { // 浏览文件不选情况
			jtfFile.setText(jfPath);

			// JOptionPane.showMessageDialog(null, fd.getFile(), "标题条文字串",
			// JOptionPane.ERROR_MESSAGE);
		}
	}

	// 上传按钮事件
	public void btnUpload_actionPerformed(ActionEvent e) {

		String filePath = jtfFile.getText();

		if ("".equals(filePath)) { // 检查是否选择文件
			JOptionPane.showMessageDialog(this, "请选择文件", "提示", 2);
			return;
		}
		byte[] b = new byte[1024];

		File f = new File(filePath);
		try {
			// 数据输出流
			DataOutputStream dout = new DataOutputStream(
					new BufferedOutputStream(socket.getOutputStream()));
			// 文件读入流
			FileInputStream fr = new FileInputStream(f);

			System.out.println("开始传输文件");
			
			int n = fr.read(b);
			while (n != -1) { // 向网络中写入数据
				dout.write(b, 0, n);
				dout.flush(); // 再次读取n字节
				n = fr.read(b);
			}
			
			System.out.println("文件传输完毕");
			
			// 关闭流
			fr.close();
			dout.close();

			JOptionPane.showMessageDialog(this, "上传成功！");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// 浏览 双向依赖
	public class BtnBrowse_ActionAdapter implements ActionListener {
		private JFileFrame adaptee;

		BtnBrowse_ActionAdapter(JFileFrame adaptee) {
			this.adaptee = adaptee;
		}

		public void actionPerformed(ActionEvent e) {
			adaptee.btnBrowse_actionPerformed(e);
		}
	}

	// 上传 双向依赖
	public class BtnUpload_ActionAdapter implements ActionListener {
		private JFileFrame adaptee;

		BtnUpload_ActionAdapter(JFileFrame adaptee) {
			this.adaptee = adaptee;
		}

		public void actionPerformed(ActionEvent e) {
			adaptee.btnUpload_actionPerformed(e);
		}
	}


	// 连接服务器
	public void runJFileFrame() {
		try {
			socket = new Socket("localhost", 1234);
			System.out.println("连接到服务器");
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public static void main(String[] args) {
		JFileFrame frame = new JFileFrame();
		frame.runJFileFrame();
	}
}

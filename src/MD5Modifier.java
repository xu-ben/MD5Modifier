/*
 * 类名：		MD5Modifier
 * 创建日期：	2015/05/04
 * 最近修改：	2015/05/04
 * 作者：		徐犇
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

/**
 * @author ben
 */
@SuppressWarnings("serial")
public final class MD5Modifier extends JFrame implements ActionListener {
	/**
	 * 处理的文件夹数目
	 */
	public static int dirnum = 0;

	/**
	 * 处理的文件数目
	 */
	public static int filenum = 0;

	private JMenuItem menuModifyFile = new JMenuItem("处理单个文件...");

	private JMenuItem menuModifyDir = new JMenuItem("处理一个目录下所有文件...");

	MD5Modifier() {

		JMenu menuOperate = new JMenu("操作(O)");
		menuOperate.setMnemonic('O');
		menuOperate.add(menuModifyFile);
		menuModifyFile.addActionListener(this);
		menuOperate.add(menuModifyDir);
		menuModifyDir.addActionListener(this);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(menuOperate);
		this.setJMenuBar(menuBar);

		// Container con = this.getContentPane();
		/**
		 * 使程序运行时在屏幕居中显示
		 */
		final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		final int width = 500;
		final int height = 309;
		final int left = (screen.width - width) / 2;
		final int top = (screen.height - height) / 2;
		this.setLocation(left, top);
		this.setSize(width, height);
		this.setTitle("批量修改媒体文件(视频)MD5小工具");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MD5Modifier();
	}

	private boolean treatDir(File f) {
		System.out.println(f.getAbsolutePath());
		return true;
	}
	
	private void runcmd(String[] cmds) throws Exception {
		Runtime run = Runtime.getRuntime();
		for (int i = 0; i < cmds.length; i++) {
			run.exec(cmds[i]);
		}
	}

	private boolean treatFile(String file) {
		File f = new File(file);
		String parent = f.getParent();
		String name = f.getName();
		String[] cmds = new String[5]; 
//		System.out.println("parent = " + parent);
		if (parent == null) {
			return false;
		}
		try {
			// 生成一个有2个空格的文本文件
			String tmpfile = parent + "\\ilzl1988.txt";
//			System.out.println("tmpfile = " + tmpfile);
			cmds[0] = "fsutil file createnew \"" + tmpfile + "\" 2";
			//runcmd("fsutil file createnew \"" + tmpfile + "\" 2");
//			Thread.sleep(100);
			
			cmds[1] = "cmd /c copy /b \"" + file + "\"+\"" + tmpfile + "\" \"" + file + "_tmp\"";
			
			cmds[2] = "cmd /c del /q /s \"" + tmpfile + "\"";
			
			cmds[3] = "cmd /c del /q /s \"" + file + "\"";
			
			cmds[4] = "cmd /c ren \"" + file + "_tmp\" \"" + name + "\"";
		//	Runtime run = Runtime.getRuntime();
	//		run.exec(cmds);
			runcmd(cmds);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem m = (JMenuItem) e.getSource();
		String filePath = null;
		if (m == menuModifyFile) {
			try {
				JFileChooser fileChooser = new JFileChooser(".");
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int n = fileChooser.showOpenDialog(this);
				if (JFileChooser.APPROVE_OPTION == n) {
					filePath = fileChooser.getSelectedFile().getPath();
				} else {
					return;
				}
			} catch (Exception ex) {
				return;
			}
			if (treatFile(filePath)) {

			} else {
				JOptionPane.showMessageDialog(this, "处理出错，请检查!", "抱歉",
						JOptionPane.ERROR_MESSAGE);
			}
		} else if (m == menuModifyDir) {
			try {
				JFileChooser fileChooser = new JFileChooser(".");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int n = fileChooser.showOpenDialog(this);
				if (JFileChooser.APPROVE_OPTION == n) {
					filePath = fileChooser.getSelectedFile().getPath();
				} else {
					return;
				}
			} catch (Exception ex) {
				return;
			}
			if (treatDir(new File(filePath))) {

			} else {
				JOptionPane.showMessageDialog(this, "处理出错，请检查!", "抱歉",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		/*
		 * CleanProject ce = new CleanProject(); if (ce.run(filePath, type)) {
		 * JOptionPane.showMessageDialog(this, "清理完毕!\n共清理文件夹" + dirnum + "个，文件"
		 * + filenum + "个!", "恭喜", JOptionPane.OK_OPTION); } else {
		 * JOptionPane.showMessageDialog(this, "清理出错!", "抱歉",
		 * JOptionPane.ERROR_MESSAGE); }/*
		 */

	}
}

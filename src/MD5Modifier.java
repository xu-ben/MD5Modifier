/*
 * 类名：		MD5Modifier
 * 创建日期：	2015/05/04
 * 最近修改：	2015/05/04
 * 作者：		徐犇
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.logging.Logger;

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
	
	public static Logger log = Logger.getLogger("log.txt");

	private JMenuItem menuModifyFile = new JMenuItem("处理单个文件...");

	private JMenuItem menuModifyDir = new JMenuItem("处理一个目录下所有文件...");
	
	private JMenuItem menuRename = new JMenuItem("批量去掉文件名中连续空格...");

	MD5Modifier() {

		JMenu menuOperate = new JMenu("操作(O)");
		menuOperate.setMnemonic('O');
		menuOperate.add(menuModifyFile);
		menuModifyFile.addActionListener(this);
		menuOperate.add(menuModifyDir);
		menuModifyDir.addActionListener(this);
		menuOperate.add(menuRename);
		menuRename.addActionListener(this);

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
		this.setTitle("批量修改媒体文件(视频)MD5小工具1.0-徐犇开发");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MD5Modifier();
	}
	
	private boolean rename(File file) {
		if (file.isDirectory()) {
			File[] fs = file.listFiles();
			for (File f : fs) {
				rename(f);
			}
		}
		String parent = file.getParent();
		String name = file.getName();
		String name2 = name.replaceAll("[ ]+", " ");
		if (!name2.equals(name)) {
			file.renameTo(new File(parent + "\\" + name2));
			log.info("filepath: " + file.getAbsolutePath());
		}
		return true;
	}

	private boolean treatDir(File f) {
		if (f.isDirectory()) {
			dirnum++;
			log.info(String.format("%d: %s\n", dirnum, f.getAbsoluteFile()));
			File[] fs = f.listFiles();
			if (fs == null) {
				return true;
			}
			for (File child : fs) {
				treatDir(child);
			}
		} else {
			try {
				return treatFile(f.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	private void run(String cmd) throws Exception {
		Runtime run = Runtime.getRuntime();
		Process p = run.exec(cmd);
		if (p.waitFor() != 0) {
			throw new Exception(String.format("p.waitFor() != 0 : %s\n", cmd));
		}
	}

	private boolean treatFile(String file) {
		filenum++;
		log.info(String.format("%d: %s\n", filenum, file));
		File f = new File(file);
		String parent = f.getParent();
		String name = f.getName();
		if (parent == null) {
			return false;
		}
		try {

			String tmpfile = parent + "\\ilzl1988.txt";
			// 生成一个指定大小的文本文件
			run(String.format("fsutil file createnew \"%s\" 1", tmpfile));
			// 将文本文件合并到视频文件中
			run(String.format("cmd /c copy /b \"%s\"+\"%s\" \"%s_tmp~\"", file,
					tmpfile, file));

			run(String.format("cmd /c del /q \"%s\"", tmpfile));
			run(String.format("cmd /c del /q \"%s\"", file));
			run(String.format("cmd /c ren \"%s_tmp~\" \"%s\"", file, name));

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
		filenum = 0;
		dirnum = 0;
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
				String show = String.format("对文件%s的修改完毕!", filePath);
				JOptionPane.showMessageDialog(this, show, "恭喜",
						JOptionPane.OK_OPTION);

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
				String show = String.format("对目录%s的修改完毕!共处理%d个目录, %d个文件",
						filePath, dirnum, filenum);
				JOptionPane.showMessageDialog(this, show, "恭喜",
						JOptionPane.OK_OPTION);
			} else {
				JOptionPane.showMessageDialog(this, "处理出错，请检查!", "抱歉",
						JOptionPane.ERROR_MESSAGE);
			}
		} else if (m == menuRename) {
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
			rename(new File(filePath));
		}
	}
}

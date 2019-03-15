package pcn;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import extern.ExternModel;

public class MainFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	private final String bg = "Background.jpg"; // 背景
	private final Font titleFont = new Font("宋体", Font.BOLD, 20); // 标题字体
	private final Font buttonFont = new Font("宋体", Font.PLAIN, 18); // 按钮字体
	private JButton btn_picture, btn_video, btn_exit;
	private JFileChooser fc = new JFileChooser();
	private String path = null;

	/**
	 * 主函数
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			new MainFrame();
		});
	}

	public MainFrame() {
		super("PCN 人脸识别程序");
		setContentPane(new BgPanel(bg));
		setLayout(null);

		// 设置文件过滤器
		fc.setFileFilter(new FileFilter() {
			public boolean accept(File file) {
				String name = file.getName();  
		        return file.isDirectory() || name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png");
			}

			public String getDescription() {
				return "*.jpg;*.png";
			}
		});

		JLabel title = new JLabel("请选择识别模式", JLabel.LEFT);
		title.setFont(titleFont);
		title.setForeground(Color.WHITE);

		btn_picture = new CircleButton("照片识别", new Color(26, 83, 92));
		btn_video = new CircleButton("视频识别", new Color(0, 91, 172));
		btn_exit = new CircleButton("退出", new Color(255, 107, 107));

		Color c_text = new Color(248, 255, 247);
		btn_picture.setForeground(c_text);
		btn_video.setForeground(c_text);
		btn_exit.setForeground(c_text);

		btn_picture.addActionListener(this);
		btn_video.addActionListener(this);
		btn_exit.addActionListener(this);
		setFont(buttonFont, btn_picture, btn_video, btn_exit);

		int b_w = 120;
		int b_h = 120;
		title.setBounds(30, 10, 150, 40);
		btn_picture.setBounds(title.getX() + 10, title.getY() + title.getHeight() + 20, b_w, b_h);
		btn_video.setBounds(btn_picture.getX() + (b_w / 2) + (b_w / 4) + 20,
				btn_picture.getY() + btn_picture.getHeight() + 8, b_w, b_h);
		btn_exit.setBounds(btn_picture.getX(), btn_video.getY() + btn_video.getHeight() + 10, b_w, b_h);

		add(title);
		add(btn_video);
		add(btn_picture);
		add(btn_exit);

		setSize(560, 500);
		setLocationRelativeTo(null);// 设置窗口到屏幕中央
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}

	/**
	 * 按钮监听器实现方法
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_picture) {
			setVisible(false);
			do {
				path = choose();
				if (path != null) {
					// 得到文件路径
					ExternModel.picture(path);
					int res = JOptionPane.showConfirmDialog(null, "你要识别下一张图片吗？", "提示", JOptionPane.YES_NO_OPTION);
					if (res == JOptionPane.YES_OPTION)
						continue;
					else if (res == JOptionPane.NO_OPTION)
						break;
				} else
					break;
			} while (true);
			setVisible(true);
		} else if (e.getSource() == btn_video) {
			setVisible(false);
			ExternModel.video();
			setVisible(true);
		} else if (e.getSource() == btn_exit) {
			// 退出系统
			dispose();
			System.exit(0);
		}
	}

	/**
	 * 选择文件
	 * 
	 * @return 文件路径；null：无选择的文件
	 */
	private String choose() {
		fc.setDialogTitle("选择文件");
		String result = null;
		int flag;
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);// 只能选择文件
		try {
			flag = fc.showOpenDialog(null);
		} catch (HeadlessException head) {
			return null;
		}
		if (flag == JFileChooser.APPROVE_OPTION)
			// 获得该文件
			result = fc.getSelectedFile().getAbsolutePath();
		return result;
	}

	/**
	 * 设置指定字体
	 * 
	 * @param font 要设置的字体
	 * @param coms 要设置字体的容器
	 */
	private void setFont(Font font, Component... coms) {
		for (Component c : coms)
			c.setFont(font);
	}

	/**
	 * 背景图片面板
	 * 
	 * @author 雨橙Orainge
	 *
	 */
	private final class BgPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private Image img;

		/**
		 * 创建一个指定图片为背景的 Panel
		 * 
		 * @param src 图片路径
		 */
		public BgPanel(String src) {
			setLayout(new BorderLayout());
			img = new ImageIcon(MainFrame.class.getResource(src)).getImage();
			setBorder(BorderFactory.createLineBorder(Color.blue, 0));
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			// 下面是为了背景图片可以跟随窗口自行调整大小，可以自己设置成固定大小
			g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
		}
	}

	/**
	 * 圆形按钮
	 * 
	 * @author 雨橙Orainge
	 */
	public class CircleButton extends JButton {
		private static final long serialVersionUID = 1L;
		private Color bgColor; // 背景颜色
		private Color highColor = new Color(224, 158, 12); // 点击时高亮颜色

		public CircleButton(String label, Color bgColor) {
			super(label);
			this.bgColor = bgColor;

			// 获取按钮的最佳大小
			Dimension size = getPreferredSize();
			size.width = size.height = 20;
			setPreferredSize(size);

			setContentAreaFilled(false);
			this.setBorderPainted(false); // 不绘制边框
			this.setFocusPainted(false); // 不绘制焦点状态
		}

		protected void paintComponent(Graphics g) {
			if (getModel().isArmed()) {
				g.setColor(highColor); // 点击时高亮
			} else {
				g.setColor(bgColor);
			}
			g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
			super.paintComponent(g);
		}

		protected void paintBorder(Graphics g) {
			if (getModel().isArmed()) {
				g.setColor(highColor); // 点击时高亮
			} else {
				g.setColor(bgColor);
			}
			g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
		}

		Shape shape;

		public boolean contains(int x, int y) {
			if ((shape == null) || (!shape.getBounds().equals(getBounds()))) {
				// 构造一个椭圆形对象
				shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
			}
			// 判断鼠标的x、y坐标是否落在按钮形状内。
			return shape.contains(x, y);
		}
	}
}

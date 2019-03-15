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

	private final String bg = "Background.jpg"; // Background
	private final Font titleFont = new Font("Noto Sans Mono", Font.BOLD, 19); // Title Font
	private final Font buttonFont = new Font("Noto Sans Mono", Font.PLAIN, 18); // Button Font
	private JButton btn_picture, btn_video, btn_exit;
	private JFileChooser fc = new JFileChooser();
	private String path = null;
	private int frame_w = 560, frame_h = 500;

	/**
	 * Main Function
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			new MainFrame();
		});
	}

	public MainFrame() {
		super("Face Recognition");
		setContentPane(new BgPanel(bg));
		setLayout(null);

		// Set File Filter
		fc.setFileFilter(new FileFilter() {
			public boolean accept(File file) {
				String name = file.getName();
				return file.isDirectory() || name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png");
			}

			public String getDescription() {
				return "*.jpg;*.png";
			}
		});
		
		JLabel title = new JLabel(">>>Face Recognition<<<", JLabel.CENTER);
		title.setFont(titleFont);
		title.setForeground(Color.WHITE);

		JLabel subtitle = new JLabel("Please Select the Recognized Mode", JLabel.CENTER);
		subtitle.setFont(titleFont);
		subtitle.setForeground(Color.WHITE);

		btn_picture = new CircleButton("Photo", new Color(26, 83, 92));
		btn_video = new CircleButton("Video", new Color(0, 91, 172));
		btn_exit = new CircleButton("Exit", new Color(255, 107, 107));

		Color c_text = new Color(248, 255, 247);
		btn_picture.setForeground(c_text);
		btn_video.setForeground(c_text);
		btn_exit.setForeground(c_text);

		btn_picture.addActionListener(this);
		btn_video.addActionListener(this);
		btn_exit.addActionListener(this);
		setFont(buttonFont, btn_picture, btn_video, btn_exit);

		int b_w = 100;
		int b_h = 100;
		int b_interval = 60;
		int b_y = frame_h - b_interval - b_w;
		title.setBounds((frame_w - 260) / 2, 10, 260, 40);
		subtitle.setBounds((frame_w - 400) / 2, 50, 400, 40);
		btn_picture.setBounds(b_interval, b_y, b_w, b_h);
		btn_video.setBounds(b_interval * 2 + b_w, b_y, b_w, b_h);
		btn_exit.setBounds(b_interval * 3 + b_w * 2, b_y, b_w, b_h);

		add(title);
		add(subtitle);
		add(btn_video);
		add(btn_picture);
		add(btn_exit);

		setSize(frame_w, frame_h);
		setLocationRelativeTo(null); // Set Window to the Center of the Screen
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}

	/**
	 * Button Listener Implementation
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_picture) {
			setVisible(false);
			do {
				path = choose();
				if (path != null) {
					// Get File Path
					ExternModel.picture(path);
					int res = JOptionPane.showConfirmDialog(null, "Do you want to recognize another image?", "Question", JOptionPane.YES_NO_OPTION);
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
			// Exit Program
			dispose();
			System.exit(0);
		}
	}

	/**
	 * Choose File
	 * 
	 * @return File path；null：No File Selected
	 */
	private String choose() {
		fc.setDialogTitle("Choose File");
		String result = null;
		int flag;
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);// Can Only Select Files
		try {
			flag = fc.showOpenDialog(null);
		} catch (HeadlessException head) {
			return null;
		}
		if (flag == JFileChooser.APPROVE_OPTION)
			// Obtain the File
			result = fc.getSelectedFile().getAbsolutePath();
		return result;
	}

	/**
	 * Set the Specified Font
	 * 
	 * @param font The Font to be Set
	 * @param coms The Container to Set the Font
	 */
	private void setFont(Font font, Component... coms) {
		for (Component c : coms)
			c.setFont(font);
	}

	/**
	 * Background Image Panel
	 */
	private final class BgPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private Image img;

		/**
		 * Create a Panel With the Specified Image As the Background
		 * 
		 * @param src Image Path
		 */
		public BgPanel(String src) {
			setLayout(new BorderLayout());
			img = new ImageIcon(MainFrame.class.getResource(src)).getImage();
			setBorder(BorderFactory.createLineBorder(Color.blue, 0));
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
		}
	}

	/**
	 * Circle Button
	 */
	public class CircleButton extends JButton {
		private static final long serialVersionUID = 1L;
		private Color bgColor; // Background Color
		private Color highColor = new Color(224, 158, 12); // Highlight Color When Clicked

		public CircleButton(String label, Color bgColor) {
			super(label);
			this.bgColor = bgColor;

			// Get the Best Size of the Button
			Dimension size = getPreferredSize();
			size.width = size.height = 20;
			setPreferredSize(size);

			setContentAreaFilled(false);
			this.setBorderPainted(false);
			this.setFocusPainted(false);
		}

		protected void paintComponent(Graphics g) {
			if (getModel().isArmed()) {
				g.setColor(highColor);
			} else {
				g.setColor(bgColor);
			}
			g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
			super.paintComponent(g);
		}

		protected void paintBorder(Graphics g) {
			if (getModel().isArmed()) {
				g.setColor(highColor);
			} else {
				g.setColor(bgColor);
			}
			g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
		}

		Shape shape;

		public boolean contains(int x, int y) {
			if ((shape == null) || (!shape.getBounds().equals(getBounds()))) {
				// Construct an Elliptical Object
				shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
			}
			return shape.contains(x, y);
		}
	}
}

package main;

import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.JFrame;

import Utils.JHardware;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.SystemColor;
import javax.swing.border.EtchedBorder;
import javax.swing.JSplitPane;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

public class MainMenu {

    /** Main frame */
    private JFrame frame;
    /** The Width of the screen */
    public static final int SCREEN_WIDTH = (int) JHardware.getScreenSize().getWidth();
    /** The Height of the screen */
    public static final int SCREEN_HEIGHT = (int) JHardware.getScreenSize().getHeight();
    /** The size of the frame determined by the ratio of screen size */
    public static final double RATIO = 0.86;
    /** The size of the frame */
    public Size frame_size = new Size(toInt(SCREEN_WIDTH * RATIO), toInt(SCREEN_HEIGHT * RATIO));

    public Size menu_size = new Size(60, 22);
    public Size left_navbar_size = new Size(355, 603);
    private JTextField textField;
    private JTextField textField_1;
    private JTextField textField_2;

    class Size {
	int w;
	int h;

	public Size(int W, int H) {
	    w = W;
	    h = H;
	}
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    MainMenu window = new MainMenu();
		    window.frame.setVisible(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the application.
     */
    public MainMenu() {
	initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {

	frame = new JFrame();
	frame.setResizable(false);
	frame.setBounds(SCREEN_WIDTH / 2 - frame_size.w / 2, SCREEN_HEIGHT / 2 - frame_size.h / 2, 1174, 660);
	// frame.setBounds(SCREEN_WIDTH / 2 - frame_size.w / 2, SCREEN_HEIGHT / 2 -
	// frame_size.h / 2, frame_size.w,
	// frame_size.h);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().setLayout(null);

	JMenuBar menuBar = new JMenuBar();
	menuBar.setBounds(0, 0, 1168, 30);
	// menuBar.setBounds(0, 0, frame_size.w, 30);
	frame.getContentPane().add(menuBar);

	JMenu mnFile = new JMenu("File");
	mnFile.setPreferredSize(new Dimension(60, 22));
	// mnFile.setPreferredSize(new Dimension(menu_size.w, menu_size.h));
	menuBar.add(mnFile);

	JMenuItem mntmNewFile = new JMenuItem("New");
	mnFile.add(mntmNewFile);

	JMenuItem mntmNewMenuItem = new JMenuItem("Open...");
	mnFile.add(mntmNewMenuItem);

	JMenuItem mntmExit = new JMenuItem("Exit");
	mnFile.add(mntmExit);

	JMenu mnEdit = new JMenu("Edit");
	mnEdit.setPreferredSize(new Dimension(60, 22));
	// mnEdit.setPreferredSize(new Dimension(menu_size.w, menu_size.h));
	menuBar.add(mnEdit);

	JMenuItem mntmCut = new JMenuItem("Cut");
	mnEdit.add(mntmCut);

	JMenuItem mntmCopy = new JMenuItem("Copy");
	mnEdit.add(mntmCopy);

	JMenuItem mntmPaste = new JMenuItem("Paste");
	mnEdit.add(mntmPaste);

	JMenuItem mntmDelete = new JMenuItem("Delete");
	mnEdit.add(mntmDelete);

	JPanel panel = new JPanel();
	panel.setBackground(Color.LIGHT_GRAY);
	panel.setBounds(0, 28, 1168, 603);
	frame.getContentPane().add(panel);
	panel.setLayout(null);

	JPanel left_navbar = new JPanel();
	left_navbar.setBackground(SystemColor.activeCaptionBorder);
	left_navbar.setBorder(new SoftBevelBorder(BevelBorder.RAISED, SystemColor.windowBorder, null, null, null));
	left_navbar.setBounds(0, 0, 369, 605);
	panel.add(left_navbar);
	left_navbar.setLayout(null);

	JLabel lblNewLabel = new JLabel("Metro Map");
	lblNewLabel.setBackground(SystemColor.controlShadow);
	lblNewLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
	lblNewLabel.setForeground(SystemColor.textHighlight);
	lblNewLabel.setFont(new Font("HP Simplified", Font.BOLD, 20));
	lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
	lblNewLabel.setBounds(10, 11, 335, 58);
	left_navbar.add(lblNewLabel);

	JButton btnNewButton = new JButton("Fastest");
	btnNewButton.setBounds(10, 113, 110, 39);
	left_navbar.add(btnNewButton);

	JButton btnShortest = new JButton("Shortest");
	btnShortest.setBounds(123, 113, 110, 39);
	left_navbar.add(btnShortest);

	JButton btnMinimum = new JButton("Minimum");
	btnMinimum.setBounds(236, 113, 110, 39);
	left_navbar.add(btnMinimum);

	JPanel panel_1 = new JPanel();
	panel_1.setBounds(10, 163, 335, 150);
	left_navbar.add(panel_1);
	panel_1.setLayout(null);

	textField = new JTextField();
	textField.setBounds(113, 10, 212, 30);
	panel_1.add(textField);
	textField.setColumns(10);

	textField_1 = new JTextField();
	textField_1.setColumns(10);
	textField_1.setBounds(113, 60, 212, 30);
	panel_1.add(textField_1);

	textField_2 = new JTextField();
	textField_2.setColumns(10);
	textField_2.setBounds(113, 110, 212, 30);
	panel_1.add(textField_2);

	JLabel lblNewLabel_1 = new JLabel("DPT.");
	lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
	lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
	lblNewLabel_1.setBounds(10, 10, 93, 26);
	panel_1.add(lblNewLabel_1);

	JLabel lblVia = new JLabel("VIA ");
	lblVia.setFont(new Font("Tahoma", Font.PLAIN, 15));
	lblVia.setHorizontalAlignment(SwingConstants.CENTER);
	lblVia.setBounds(10, 62, 93, 26);
	panel_1.add(lblVia);

	JLabel lblArv = new JLabel("ARV.");
	lblArv.setFont(new Font("Tahoma", Font.PLAIN, 15));
	lblArv.setHorizontalAlignment(SwingConstants.CENTER);
	lblArv.setBounds(10, 110, 93, 26);
	panel_1.add(lblArv);

	JButton btnRefresh = new JButton("Refresh");
	btnRefresh.setBounds(256, 324, 89, 23);
	left_navbar.add(btnRefresh);

	JButton btnSubmit = new JButton("Submit");
	btnSubmit.setBounds(157, 324, 89, 23);
	left_navbar.add(btnSubmit);

	JPanel drag_pnl = new JPanel();
	drag_pnl.setOpaque(false);
	drag_pnl.setBackground(SystemColor.activeCaptionBorder);
	drag_pnl.addMouseMotionListener(new MouseMotionAdapter() {
	    Point prev;

	    @Override
	    public void mouseDragged(MouseEvent e) {
		Point onscreen = e.getLocationOnScreen();

		if (prev == null)
		    prev = onscreen;

		left_navbar.setLocation(left_navbar.getX() + (onscreen.x - prev.x), left_navbar.getY());
		prev = onscreen;

		// if (left_navbar.getX() < -left_navbar_size.w + drag_pnl.getWidth() / 2) {
		// left_navbar.setLocation(-left_navbar_size.w + drag_pnl.getWidth() / 2,
		// left_navbar.getY());
		// prev = null;
		// }
	    }
	});
	drag_pnl.setBounds(347, 0, 20, 603);
	left_navbar.add(drag_pnl);
    }

    private int toInt(double a) {
	return (int) a;
    }
}

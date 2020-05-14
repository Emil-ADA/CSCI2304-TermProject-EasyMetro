package main;

import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.JFrame;

import Utils.Autocompletion;
import Utils.JHardware;
import edu.emory.mathcs.backport.java.util.Arrays;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import org.bouncycastle.jcajce.provider.keystore.bc.BcKeyStoreSpi.Std;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemColor;
import javax.swing.border.EtchedBorder;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButtonMenuItem;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import DS.DepthFirstSearch;
import DS.DijkstraUndirectedSP;
import DS.Edge;
import DS.Graph;
import DS.LinearProbingHashST;
import DS.Queue;
import DS.Stack;
import Dependencies.FilenameUtils;
import Dependencies.StdOut;

import java.awt.ScrollPane;
import javax.swing.ImageIcon;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import java.awt.Cursor;

public class MainMenu {
    // FIXME: try to make input case insensitive when sober
    // FIXME: TRY TO ADD VARIABLES TO .cfg file
    // FIXME: DONT FORGET TO CHANGE MAGIC NUMBERS

    /** Main frame */
    private JFrame frame;
    /** The Width of the screen */
    public static final int SCREEN_WIDTH = (int) JHardware.getScreenSize().getWidth();
    /** The Height of the screen */
    public static final int SCREEN_HEIGHT = (int) JHardware.getScreenSize().getHeight();
    /** The size of the frame determined by the ratio of screen size */
    public static final double RATIO = 0.86;
    /** The unit increment of the Vertical scroll bar of Scroll pane */
    public static final short VSCROLL_RATE = 16;
    /** The unit increment of the Horizontal scroll bar of Scroll pane */
    public static final short HSCROLL_RATE = 16;
    /** Variable ratio for zooming in and out, default value 10% */
    public static final double ZOOM_SCALE = 0.1;
    /** Variable for scaling the map, defines which method will be used */
    public static int MAP_SCALE_HINTS = Image.SCALE_DEFAULT;
    /** The dimension of the frame */
    public static final Rectangle FRAME_SIZE = new Rectangle(toInt(SCREEN_WIDTH * RATIO), toInt(SCREEN_HEIGHT * RATIO));
    /** The dimension of the menu bar */
    public static final Rectangle MENU_SIZE = new Rectangle(60, 22);
    /** The dimension of the navigation bar on the left */
    public static final Rectangle L_NAVBAR_SIZE = new Rectangle(355, 603);
    /** The data repository */
    private static final String DATA_REPO = ".//data//Cities//";

    /** Current city of which map is displayed */
    private static String city = "Istanbul";

    /** Image of the map to be displayed */
    private static Image MAP_IMAGE;

    /** Label component containing the image of the map */
    public static JLabel MAP_IMG;
    /* Points used for dragging left navigation bar */
    Point prev_p;
    Point onscreen_p;
    /**
     * By default it is 0; 0 means Fastest route, 1 means Shortest route, 2 means to
     * find minimum of these
     */
    public int mode = 0;

    /**
     * Flag variable, shows whether left navbar can be dragged or not, Dragging
     * disabled by default
     */
    public boolean drag = false;

    public JTextField textField;
    public JTextField textField_1;
    public JTextField textField_2;

    private JTextPane textPane;

    /***************************************************************************
     * VARIABLES FOR ALGO
     ***************************************************************************/
    private LinearProbingHashST<String, Integer> hash;
    private Graph map;
    private StringBuilder searchResultsExtended;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    MainMenu window = new MainMenu();
		    window.frame.setVisible(true);
		    Thread.sleep(200);
		    MAP_SCALE_HINTS = Image.SCALE_SMOOTH;
		    // scaleMAP(MAP_IMG, -ZOOM_SCALE * 8.9);
		    scaleMAP(MAP_IMG, -ZOOM_SCALE * 7);
		    MAP_SCALE_HINTS = Image.SCALE_DEFAULT;

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
	initVars();
	initMAP();
	initialize();
    }

    private void initVars() {
	hash = new LinearProbingHashST<>();
	MAP_IMAGE = new ImageIcon(DATA_REPO + city + "//map.png").getImage();
	MAP_IMG = new JLabel("");

    }

    private void initMAP() {

	File[] roadlines = new File(DATA_REPO + city + "//lines").listFiles();

	int ID = 0;
	for (File line : roadlines) {
	    try (Scanner sc = new Scanner(line);) {
		sc.nextLine();
		while (sc.hasNext()) {
		    String key = sc.nextLine();
		    if (key.contains("CR/LF")) {
			continue;
		    }

		    if (!hash.contains(key))
			hash.put(key, ID++);
		}
	    } catch (FileNotFoundException e) {
	    }
	}

	for (String s : hash.keys()) {
	    StdOut.println(hash.get(s) + ":\t" + s);
	}

	map = new Graph(hash.size());

	for (File line : roadlines) {
	    try (Scanner sc = new Scanner(line)) {
		String[] first_line = sc.nextLine().split(" ");
		double time = Double.parseDouble(first_line[0]);
		double distance = Double.parseDouble(first_line[1]);

		String prev = sc.nextLine();

		while (sc.hasNext()) {
		    String key = sc.nextLine();

		    if (key.contains("CR/LF")) {
			prev = sc.nextLine();
			key = sc.nextLine();
		    }
		    Edge newEdge = new Edge(hash.get(prev), hash.get(key), time, distance);
		    newEdge.setVertexNames(prev, key);
		    newEdge.setLine(FilenameUtils.getBaseName(line.getAbsolutePath()));
		    map.addEdge(newEdge);
		    prev = key;
		}

	    } catch (FileNotFoundException e) {
	    }
	}

    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {

	frame = new JFrame();
	frame.setResizable(false);
	frame.setBounds(SCREEN_WIDTH / 2 - FRAME_SIZE.width / 2, SCREEN_HEIGHT / 2 - FRAME_SIZE.height / 2, 1174, 660);
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
	mnFile.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	mnFile.setPreferredSize(new Dimension(60, 22));
	// mnFile.setPreferredSize(new Dimension(menu_size.w, menu_size.h));
	menuBar.add(mnFile);
	
	JMenuItem mntmTest = new JMenuItem("Test");
	mntmTest.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	mnFile.add(mntmTest);

	JMenuItem mntmNewFile = new JMenuItem("New");
	mntmNewFile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	mnFile.add(mntmNewFile);

	JMenuItem mntmNewMenuItem = new JMenuItem("Open...");
	mntmNewMenuItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	mnFile.add(mntmNewMenuItem);

	JMenuItem mntmExit = new JMenuItem("Exit");
	mntmExit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	mnFile.add(mntmExit);

	JMenu mnEdit = new JMenu("Edit");
	mnEdit.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	mnEdit.setPreferredSize(new Dimension(60, 22));
	// mnEdit.setPreferredSize(new Dimension(menu_size.w, menu_size.h));
	menuBar.add(mnEdit);

	JMenuItem mntmRefresh = new JMenuItem("Refresh");
	mntmRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	mnEdit.add(mntmRefresh);

	JMenuItem mntmCut = new JMenuItem("Cut");
	mntmCut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	mnEdit.add(mntmCut);

	JMenuItem mntmCopy = new JMenuItem("Copy");
	mntmCopy.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	mnEdit.add(mntmCopy);

	JMenuItem mntmPaste = new JMenuItem("Paste");
	mntmPaste.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	mnEdit.add(mntmPaste);

	JMenuItem mntmDelete = new JMenuItem("Delete");
	mntmDelete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	mnEdit.add(mntmDelete);

	JMenu mnNewMenu = new JMenu("Settings");
	mnNewMenu.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	menuBar.add(mnNewMenu);

	JMenuItem mntmShowMapDetails = new JMenuItem("Show Map Details");
	mntmShowMapDetails.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	mntmShowMapDetails.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		textPane.setText(map.toString());
		textPane.setCaretPosition(0);
	    }
	});
	mnNewMenu.add(mntmShowMapDetails);

	JMenu mnMapScale = new JMenu("Map Scale");
	mnMapScale.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	mnNewMenu.add(mnMapScale);

	JCheckBoxMenuItem chcDEF = new JCheckBoxMenuItem("Default");
	chcDEF.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	chcDEF.setState(true);
	mnMapScale.add(chcDEF);

	JCheckBoxMenuItem chcSM = new JCheckBoxMenuItem("Smooth");
	chcSM.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	mnMapScale.add(chcSM);

	JCheckBoxMenuItem chcFAST = new JCheckBoxMenuItem("Fast");
	chcFAST.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	mnMapScale.add(chcFAST);

	JCheckBoxMenuItem chcAREA = new JCheckBoxMenuItem("Area Averaging");
	chcAREA.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	mnMapScale.add(chcAREA);

	JCheckBoxMenuItem chcREP = new JCheckBoxMenuItem("Replicate");
	chcREP.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	mnMapScale.add(chcREP);

	JRadioButtonMenuItem rdbtnmntmDrag = new JRadioButtonMenuItem("Drag");
	rdbtnmntmDrag.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	mnNewMenu.add(rdbtnmntmDrag);

	JPanel panel = new JPanel();
	panel.setBackground(Color.LIGHT_GRAY);
	panel.setBounds(0, 28, 1168, 603);
	frame.getContentPane().add(panel);
	panel.setLayout(null);

	JPanel left_navbar = new JPanel();
	left_navbar.setBackground(new Color(60, 60, 84));
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
	lblNewLabel.setBounds(10, 11, 349, 58);
	left_navbar.add(lblNewLabel);

	Color textHighlight = new Color(77, 159, 206);

	JButton btnNewButton = new JButton("Fastest");
	btnNewButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	btnNewButton.setFont(new Font("Courier New", Font.PLAIN, 13));
	btnNewButton.setForeground(textHighlight);
	btnNewButton.setBounds(10, 80, 110, 39);
	left_navbar.add(btnNewButton);

	JButton btnShortest = new JButton("Shortest");
	btnShortest.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	btnShortest.setFont(new Font("Courier New", Font.PLAIN, 13));
	btnShortest.setBounds(130, 80, 110, 39);
	left_navbar.add(btnShortest);

	JButton btnMinimum = new JButton("Minimum");
	btnMinimum.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	btnMinimum.setFont(new Font("Courier New", Font.PLAIN, 13));
	btnMinimum.setBounds(249, 80, 110, 39);
	left_navbar.add(btnMinimum);

	JPanel panel_1 = new JPanel();
	panel_1.setBackground(new Color(100, 100, 116));
	panel_1.setBounds(10, 130, 349, 150);
	left_navbar.add(panel_1);
	panel_1.setLayout(null);

	textField = new JTextField();
	textField.setFont(new Font("Courier New", Font.PLAIN, 13));
	textField.setBounds(113, 10, 226, 30);
	panel_1.add(textField);
	textField.setColumns(10);

	textField_1 = new JTextField();
	textField_1.setFont(new Font("Courier New", Font.PLAIN, 13));

	textField_1.setColumns(10);
	textField_1.setBounds(113, 60, 226, 30);
	panel_1.add(textField_1);

	textField_2 = new JTextField();
	textField_2.setFont(new Font("Courier New", Font.PLAIN, 13));

	textField_2.setColumns(10);
	textField_2.setBounds(113, 110, 226, 30);
	panel_1.add(textField_2);

	JLabel lblNewLabel_1 = new JLabel("DPT.");
	lblNewLabel_1.setForeground(Color.WHITE);
	lblNewLabel_1.setFont(new Font("Courier New", Font.PLAIN, 13));
	lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
	lblNewLabel_1.setBounds(10, 10, 93, 26);
	panel_1.add(lblNewLabel_1);

	JLabel lblVia = new JLabel("Via");
	lblVia.setForeground(Color.WHITE);
	lblVia.setFont(new Font("Courier New", Font.PLAIN, 13));
	lblVia.setHorizontalAlignment(SwingConstants.CENTER);
	lblVia.setBounds(10, 62, 93, 26);
	panel_1.add(lblVia);

	JLabel lblArv = new JLabel("ARV.");
	lblArv.setForeground(Color.WHITE);
	lblArv.setFont(new Font("Courier New", Font.PLAIN, 13));
	lblArv.setHorizontalAlignment(SwingConstants.CENTER);
	lblArv.setBounds(10, 110, 93, 26);
	panel_1.add(lblArv);

	JButton btnRefresh = new JButton("Refresh");
	btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	btnRefresh.setFont(new Font("Courier New", Font.PLAIN, 13));
	btnRefresh.setBounds(249, 291, 110, 23);
	left_navbar.add(btnRefresh);

	JButton btnSearch = new JButton("Search");
	btnSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	btnSearch.setFont(new Font("Courier New", Font.PLAIN, 13));
	btnSearch.setBounds(130, 291, 110, 23);
	left_navbar.add(btnSearch);

	JButton btnNewButton_1 = new JButton("+");
	btnNewButton_1.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	btnNewButton_1.setFont(new Font("MS Reference Sans Serif", Font.BOLD, 14));
	btnNewButton_1.setBorder(null);
	btnNewButton_1.setBounds(1115, 15, 30, 30);
	panel.add(btnNewButton_1);

	JButton button = new JButton("-");
	button.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	button.setFont(new Font("MS Reference Sans Serif", Font.BOLD, 14));
	button.setBorder(null);
	button.setBounds(1085, 15, 30, 30);
	panel.add(button);

	ScrollPane scrollPane = new ScrollPane();
	scrollPane.setBounds(375, 0, 793, 605);
	panel.add(scrollPane);
	scrollPane.getVAdjustable().setUnitIncrement(VSCROLL_RATE);
	scrollPane.getHAdjustable().setUnitIncrement(HSCROLL_RATE);

	MAP_IMG.setIcon(new ImageIcon(MAP_IMAGE));
	MAP_IMG.setBounds(519, 102, 46, 14);
	scrollPane.add(MAP_IMG);

	/*
	 * .**************************************************************************
	 * LISTENERS
	 ***************************************************************************/
	left_navbar.addMouseMotionListener(new MouseMotionAdapter() {

	    @Override
	    public void mouseDragged(MouseEvent e) {
		if (!drag)
		    return;
		popup_show(false);

		onscreen_p = e.getLocationOnScreen();

		if (prev_p == null)
		    prev_p = onscreen_p;

		left_navbar.setLocation(left_navbar.getX() + (onscreen_p.x - prev_p.x), left_navbar.getY());

		prev_p = onscreen_p;

		// FIXME: MAKE SO THAT DRAGING DOES NOT GO TO FAR

	    }
	});
	btnNewButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		btnNewButton.setForeground(textHighlight);
		btnMinimum.setForeground(SystemColor.BLACK);
		btnShortest.setForeground(SystemColor.BLACK);
		mode = 0;
	    }
	});
	btnShortest.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		btnShortest.setForeground(textHighlight);
		btnMinimum.setForeground(SystemColor.BLACK);
		btnNewButton.setForeground(SystemColor.BLACK);
		mode = 1;
	    }
	});
	btnMinimum.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		btnMinimum.setForeground(textHighlight);
		btnNewButton.setForeground(SystemColor.BLACK);
		btnShortest.setForeground(SystemColor.BLACK);
		mode = 2;
	    }
	});
	btnRefresh.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		refresh(new Component[] { left_navbar, btnNewButton, btnMinimum, btnShortest, rdbtnmntmDrag });
		scrollPane.setScrollPosition(0, 0);
	    }
	});

	/* Refresh option, menu item, Refreshes the screen */
	mntmRefresh.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		refresh(new Component[] { left_navbar, btnNewButton, btnMinimum, btnShortest, rdbtnmntmDrag });
		scrollPane.setScrollPosition(0, 0);

	    }
	});

	/* Radio button, menu item, flag for dragging */
	rdbtnmntmDrag.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent arg0) {
		drag = rdbtnmntmDrag.isSelected();
	    }
	});

	btnNewButton_1.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		scaleMAP(MAP_IMG, ZOOM_SCALE);
	    }
	});
	button.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		scaleMAP(MAP_IMG, -ZOOM_SCALE);
	    }
	});

	/*----------------------ADDING AUTOCOMPLETION-----------------------*/
	ArrayList<String> list = new ArrayList<>();

	hash.keys().iterator().forEachRemaining(list::add);
	textField.addKeyListener(addAutoCompletion(textField, list));
	textField_1.addKeyListener(addAutoCompletion(textField_1, list));
	textField_2.addKeyListener(addAutoCompletion(textField_2, list));
	textField.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		popup_show(false);
	    }
	});
	textField_1.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		popup_show(false);
	    }
	});
	textField_2.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		popup_show(false);
	    }
	});

	JScrollPane scrollPane_1 = new JScrollPane();
	scrollPane_1.setBounds(10, 325, 349, 269);
	left_navbar.add(scrollPane_1);

	textPane = new JTextPane();
	textPane.setFont(new Font("Consolas", Font.PLAIN, 11));
	// FIXME: VAXT QALANDA html output-a convert et
	// textPane.setContentType("text/html");

	textPane.setEditable(false);
	scrollPane_1.setViewportView(textPane);

	JButton btnExpand = new JButton("Expand");
	btnExpand.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	btnExpand.setFont(new Font("Courier New", Font.PLAIN, 13));
	btnExpand.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		// FIXME: Might add: !sRE.toString().isEmpty()
		if (searchResultsExtended != null) {
		    textPane.setText(searchResultsExtended.toString());
		    textPane.setCaretPosition(0);
		}
	    }
	});
	btnExpand.setBounds(10, 291, 110, 23);
	left_navbar.add(btnExpand);

	/*-------------------------------------------------------------------*/
	/*-----------------MENUBAR -> SETTINGS -> MAP SCALE-----------------*/
	/*-----------------------------------------------------------------*/
	chcDEF.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (chcDEF.isEnabled()) {
		    MAP_SCALE_HINTS = Image.SCALE_DEFAULT;
		    chcSM.setState(false);
		    chcFAST.setState(false);
		    chcAREA.setState(false);
		    chcREP.setState(false);
		}
	    }
	});
	chcSM.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (chcSM.isEnabled()) {
		    MAP_SCALE_HINTS = Image.SCALE_SMOOTH;
		    chcDEF.setState(false);
		    chcFAST.setState(false);
		    chcAREA.setState(false);
		    chcREP.setState(false);
		}
	    }
	});
	chcFAST.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (chcFAST.isEnabled()) {
		    MAP_SCALE_HINTS = Image.SCALE_FAST;
		    chcSM.setState(false);
		    chcDEF.setState(false);
		    chcAREA.setState(false);
		    chcREP.setState(false);
		}
	    }
	});
	chcAREA.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (chcAREA.isEnabled()) {
		    MAP_SCALE_HINTS = Image.SCALE_AREA_AVERAGING;
		    chcSM.setState(false);
		    chcFAST.setState(false);
		    chcDEF.setState(false);
		    chcREP.setState(false);
		}
	    }
	});
	chcREP.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (chcREP.isEnabled()) {
		    MAP_SCALE_HINTS = Image.SCALE_REPLICATE;
		    chcSM.setState(false);
		    chcFAST.setState(false);
		    chcAREA.setState(false);
		    chcDEF.setState(false);
		}
	    }
	});
	/*------------------------------------------------------------------*/
	btnSearch.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		String from = textField.getText();
		String via = (hash.contains(textField_1.getText())) ? textField_1.getText() : null;
		String to = textField_2.getText();

		if (hash.contains(from) && hash.contains(to)) {
		    /** If the mode is on MINIMUM */
		    if (mode == 2) {
			StringBuilder searchResults = displayMin(from, via, to);
			textPane.setText(searchResults.toString());
			return;
		    }

		    /** For the modes SHORTEST and FASTEST */
		    StringBuilder searchResults = search(from, via, to);
		    textPane.setText(searchResults.toString());
		}
		textPane.setCaretPosition(0);
	    }
	});
    }

    private boolean hasTranfer(String path, String transfer) {
	boolean retval = false;

	retval = (retval || (path.contains(transfer)));

	String arr[] = transfer.split("/");
	/**
	 * We need to consider, for ex.: <br>
	 * Bayrampasa-Maltepe/Topkapi-Ulubatli/Fetihkapi<br>
	 * AND <br>
	 * Fetihkapi/Topkapi-Ulubatli/Bayrampasa-Maltepe
	 */
	transfer = arr[2] + "/" + arr[1] + "/" + arr[0];
	retval = (retval || (path.contains(transfer)));

	return retval;
    }

    private Queue<Edge> searchMin(String from, String to) {
	DepthFirstSearch dfs = new DepthFirstSearch(map, hash, from, to);
	ArrayList<Stack<String>> ALL_PATHS = dfs.getAllPaths();
	int size = ALL_PATHS.size();
	int W[] = new int[size];

	/** Calculating the amount of transfers */
	for (int i = 0; i < size; i++) {
	    String path = ALL_PATHS.get(i).toString();
	    try (Scanner sc = new Scanner(new File(DATA_REPO + city + "//transfer.txt"))) {
		while (sc.hasNext()) {
		    String transfer = sc.nextLine();
		    if (hasTranfer(path, transfer)) {
			W[i]++;
		    }
		}
	    } catch (FileNotFoundException e) {
		System.err.print("NOT FOUND" + e);
	    }

	}

	int min = Integer.MAX_VALUE;
	int ind = 0;
	for (int i = 0; i < size; i++) {
	    if (W[i] < min) {
		min = W[i];
		ind = i;
	    }
	}

	System.out.println(map);
	String min_path_vertices[] = ALL_PATHS.get(ind).toString().split("/");
	Queue<Edge> path = new Queue<>();
	for (Edge e : map.edges()) {
	    String name = e.w_name + e.v_name;
	    for (int i = 0; i < min_path_vertices.length - 1; i++) {

		if (name.equals(min_path_vertices[i] + min_path_vertices[i + 1])
			|| name.equals(min_path_vertices[i + 1] + min_path_vertices[i])) {
		    path.enqueue(e);
		}
	    }

	}

	return path;
    }

    private StringBuilder displayMin(String from, String via, String to) {
	/*-------------------------------------------------------------------*/
	// I understand that this part is being repeated 3 times, but each case has
	// minor difference that made it hard for me to short it out
	/*-------------------------------------------------------------------*/

	StringBuilder searchResults = new StringBuilder();
	searchResultsExtended = new StringBuilder();
	String str2Append = "";
	double time = 0;
	double distance = 0;
	/*-------------------------------------------------------------------*/
	/*----------------------------from->via-----------------------------*/
	/*-----------------------------------------------------------------*/

	/** if 'via' is missing, 'to' becomes 'via' */
	if (via == null) {
	    via = to;
	    to = null;
	}
	/** ALL SHORTEST PATHS FROM 'from' */
	Queue<Edge> pathTo = searchMin(from, via);
	String line_changed = null;
	str2Append = "\nDEPARTURE\n";
	searchResults.append(str2Append);
	searchResultsExtended.append(str2Append);

	/** Shortest path between 'from' adn 'via */
	Edge e = null;
	int transfer = 0;
	int stations = 1; // Stations start at one, because oz mindiyin esseyi de saymaq lazimdir

	/* DEPARTURE STATION */
	e = pathTo.dequeue();
	str2Append = e + "\n";
	searchResults.append(str2Append);
	searchResultsExtended.append(str2Append);
	line_changed = e.getLine();
	stations++;

	while (!pathTo.isEmpty()) {
	    e = pathTo.dequeue();
	    time += e.weight(0);
	    distance += e.weight(1);
	    stations++;
	    str2Append = e + "\n";
	    searchResultsExtended.append(str2Append); // extended results saves all, while normal not

	    /** Display the only edge where the transfer has happened */
	    if (!e.getLine().equals(line_changed)) {
		transfer++;
		searchResults.append("TRANSFER" + str2Append.substring(str2Append.indexOf(':'), str2Append.length()));
	    }
	    line_changed = e.getLine();
	}

	// append DESTINATION as well
	if (!searchResults.toString().contains(str2Append) || !searchResults.toString()
		.contains("TRANSFER" + str2Append.substring(str2Append.indexOf(':'), str2Append.length())))
	    searchResults.append(str2Append);
	/*-------------------------------------------------------------------*/
	/*----------------------------via->to-----------------------------*/
	/*-----------------------------------------------------------------*/

	/** if via station has been given */
	if (to != null) {
	    str2Append = "STOP\n";
	    searchResults.append(str2Append);
	    searchResultsExtended.append(str2Append);

	    pathTo = searchMin(via, to);
	    e = null;

	    /* DEPARTURE STATION */
	    e = pathTo.dequeue();
	    stations++;
	    str2Append = e + "\n";
	    line_changed = e.getLine();
	    searchResults.append(str2Append);

	    while (!pathTo.isEmpty()) {
		e = pathTo.dequeue();
		time += e.weight(0);
		distance += e.weight(1);
		stations++;
		str2Append = e + "\n";

		searchResultsExtended.append(str2Append);

		if (!e.getLine().equals(line_changed)) {
		    transfer++;
		    searchResults
			    .append("TRANSFER" + str2Append.substring(str2Append.indexOf(':'), str2Append.length()));

		}
		line_changed = e.getLine();
	    }

	    // append DESTINATION as well
	    if (!searchResults.toString().contains(str2Append) || !searchResults.toString()
		    .contains("TRANSFER" + str2Append.substring(str2Append.indexOf(':'), str2Append.length())))
		searchResults.append(str2Append);
	}
	str2Append = "DESTINATION\n\n";
	searchResults.append(str2Append);
	searchResultsExtended.append(str2Append);

	/*
	 * Adding travel info.
	 */

	try (Scanner sc = new Scanner(new File(DATA_REPO + city + "//settings.cfg"))) {
	    String line = sc.nextLine();
	    /* Add total cost */
	    line = line.substring(line.indexOf('=') + 1, line.length());
	    /**
	     * Transfer + 1, because we pay when we enter the metro first time
	     */
	    str2Append = "Fare: ";
	    String cost = String.format("%.2f", Double.parseDouble(line) * (transfer + 1));
	    str2Append += cost;

	    /* Add currency */
	    line = sc.nextLine();
	    line = line.substring(line.indexOf('=') + 1, line.length());
	    str2Append += " " + line + "\n";
	} catch (FileNotFoundException e1) {
	}

	/**
	 * This substring concationation is done only because, results are calculated
	 * only after traversing
	 */
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());

	/** Temporarily changing modes for displaying different parameters */
	int temp = mode;

	/* Mode = 0, is for displaying time */
	mode = 0;

	str2Append = "Lead Time: " + formatResult(time) + "\n";
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.append(str2Append);

	/* Mode = 0, is for displaying distance */
	mode = 1;

	str2Append = "Distance: " + formatResult(distance) + "\n";
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.append(str2Append);

	mode = temp;

	str2Append = stations + " Station(s). | " + transfer + " Transfer(s)." + "\n";
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.append(str2Append);

	return searchResults;
    }

    private StringBuilder search(String from, String via, String to) {
	/** Output variables */
	StringBuilder searchResults = new StringBuilder();
	searchResultsExtended = new StringBuilder();
	String str2Append = "";
	/*-------------------------------------------------------------------*/
	/*----------------------------from->via-----------------------------*/
	/*-----------------------------------------------------------------*/

	/** if 'via' is missing, 'to' becomes 'via' */
	if (via == null) {
	    via = to;
	    to = null;
	}
	/** ALL SHORTEST PATHS FROM 'from' */
	DijkstraUndirectedSP first = new DijkstraUndirectedSP(map, hash.get(from), mode);
	String line_changed = null;
	str2Append = "\nDEPARTURE\n";
	searchResults.append(str2Append);
	searchResultsExtended.append(str2Append);

	/** Shortest path between 'from' adn 'via */
	Iterator<Edge> pathTo = first.pathTo(hash.get(via)).iterator();
	Edge e = null;
	int transfer = 0;
	int stations = 1; // Stations start at one, because oz mindiyin esseyi de saymaq lazimdir

	/* DEPARTURE STATION */
	e = pathTo.next();
	str2Append = e + "\n";
	searchResults.append(str2Append);
	searchResultsExtended.append(str2Append);
	line_changed = e.getLine();
	stations++;

	while (pathTo.hasNext()) {
	    e = pathTo.next();
	    stations++;
	    str2Append = e + "\n";
	    searchResultsExtended.append(str2Append); // extended results saves all, while normal not

	    /** Display the only edge where the transfer has happened */
	    if (!e.getLine().equals(line_changed)) {
		transfer++;
		searchResults.append("TRANSFER" + str2Append.substring(str2Append.indexOf(':'), str2Append.length()));
	    }
	    line_changed = e.getLine();
	}

	// append DESTINATION as well
	if (!searchResults.toString().contains(str2Append) || !searchResults.toString()
		.contains("TRANSFER" + str2Append.substring(str2Append.indexOf(':'), str2Append.length())))
	    searchResults.append(str2Append);
	/*-------------------------------------------------------------------*/
	/*----------------------------via->to-----------------------------*/
	/*-----------------------------------------------------------------*/
	DijkstraUndirectedSP second = null;

	/** if via station has been given */
	if (to != null) {
	    second = new DijkstraUndirectedSP(map, hash.get(via), mode);
	    str2Append = "STOP\n";
	    searchResults.append(str2Append);
	    searchResultsExtended.append(str2Append);

	    pathTo = second.pathTo(hash.get(to)).iterator();
	    e = null;

	    /* DEPARTURE STATION */
	    e = pathTo.next();
	    stations++;
	    str2Append = e + "\n";
	    line_changed = e.getLine();
	    searchResults.append(str2Append);

	    while (pathTo.hasNext()) {
		e = pathTo.next();
		stations++;
		str2Append = e + "\n";

		searchResultsExtended.append(str2Append);

		if (!e.getLine().equals(line_changed)) {
		    transfer++;
		    searchResults
			    .append("TRANSFER" + str2Append.substring(str2Append.indexOf(':'), str2Append.length()));

		}
		line_changed = e.getLine();
	    }

	    // append DESTINATION as well
	    if (!searchResults.toString().contains(str2Append) || !searchResults.toString()
		    .contains("TRANSFER" + str2Append.substring(str2Append.indexOf(':'), str2Append.length())))
		searchResults.append(str2Append);
	}
	str2Append = "DESTINATION\n\n";
	searchResults.append(str2Append);
	searchResultsExtended.append(str2Append);

	/*
	 * Adding travel info.
	 */

	try (Scanner sc = new Scanner(new File(DATA_REPO + city + "//settings.cfg"))) {
	    String line = sc.nextLine();
	    /* Add total cost */
	    line = line.substring(line.indexOf('=') + 1, line.length());
	    /**
	     * Transfer + 1, because we pay when we enter the metro first time
	     */
	    str2Append = "Fare: ";
	    String cost = String.format("%.2f", Double.parseDouble(line) * (transfer + 1));
	    str2Append += cost;

	    /* Add currency */
	    line = sc.nextLine();
	    line = line.substring(line.indexOf('=') + 1, line.length());
	    str2Append += " " + line + "\n";
	} catch (FileNotFoundException e1) {
	}

	/**
	 * This substring concationation is done only because, results are calculated
	 * only after traversing
	 */
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());

	/** Temporarily changing modes for displaying different parameters */
	int temp = mode;

	/* Mode = 0, is for displaying time */
	mode = 0;
	double time = first.distTo(hash.get(via));
	if (to != null)
	    time += second.distTo(hash.get(to));
	str2Append = "Lead Time: " + formatResult(time) + "\n";
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.append(str2Append);

	/* Mode = 0, is for displaying distance */
	mode = 1;
	double distance = first.distTo(hash.get(via));
	if (to != null)
	    distance += second.distTo(hash.get(to));
	str2Append = "Distance: " + formatResult(distance) + "\n";
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.append(str2Append);

	mode = temp;

	str2Append = stations + " Station(s). | " + transfer + " Transfer(s)." + "\n";
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.append(str2Append);

	return searchResults;
    }

    /* VARIABLES FOR AUTO-SUGGESTION */
    /*
     * These Variables are shared between 3 text fields in order to reduce memory
     * usage plus only one instance of suggestion needed at a time
     */
    private JToolTip toolTip = new JToolTip();
    private PopupFactory pf = new PopupFactory();
    private Popup popup;
    private List<String> sugg = new ArrayList<String>();

    private KeyAdapter addAutoCompletion(JTextField text_field, List<String> list) {
	popup_show(false);
	return new KeyAdapter() {
	    @Override
	    public void keyTyped(KeyEvent key) {
		sugg = Autocompletion.query(text_field.getText(), list);

		if (text_field.getText().length() == 0 || sugg.size() == 0 || !text_field.hasFocus()) {
		    popup_show(false);
		    return;
		}

		toolTip.setTipText(sugg.get(0));

		int x = toInt(text_field.getLocationOnScreen().getX());
		int y = toInt(text_field.getLocationOnScreen().getY()) + text_field.getHeight();
		popup = pf.getPopup(text_field, toolTip, x, y);
		popup_show(true);

		toolTip.addMouseListener(new MouseAdapter() {

		    @Override
		    public void mouseClicked(MouseEvent e) {
			if (textField.hasFocus()) {
			    textField.setText(toolTip.getTipText());
			}
			if (textField_1.hasFocus()) {
			    textField_1.setText(toolTip.getTipText());
			}
			if (textField_2.hasFocus()) {
			    textField_2.setText(toolTip.getTipText());
			}
			popup_show(false);
		    }
		});
	    }
	};
    }

    private void popup_show(boolean yes) {
	if (popup == null)
	    return;
	if (yes)
	    popup.show();
	else {
	    popup.hide();
	    popup = null;
	}
    }

    /***************************************************************************
     * AUX-METHODS
     ***************************************************************************/

    public static void scaleMAP(JLabel label, double scale) {
	int w = label.getWidth();
	int h = label.getHeight();
	Image newImg = MAP_IMAGE.getScaledInstance(toInt(w + w * scale), toInt(h + h * scale), MAP_SCALE_HINTS);
	label.setIcon(new ImageIcon(newImg));
    }

    private void refresh(Component[] a) {
	a[0].setBounds(0, 0, 369, 605);
	prev_p = null;
	onscreen_p = null;
	a[1].setForeground(SystemColor.textHighlight);
	a[2].setForeground(SystemColor.BLACK);
	a[3].setForeground(SystemColor.BLACK);
	mode = 0;
	textField.setText("");
	textField_1.setText("");
	textField_2.setText("");
	((JRadioButtonMenuItem) a[4]).setSelected(false);
	drag = false;
	searchResultsExtended = new StringBuilder();
	textPane.setText("");
    }

    private static int toInt(double a) {
	return (int) a;
    }

    private String formatResult(double res) {
	String retval = "";
	if (mode == 0) {
	    if (res > 60)
		retval += toInt(res / 60) + " hours, ";
	    retval += toInt(res % 60) + " minutes";

	} else if (mode == 1) {
	    if (res > 1)
		retval += toInt(res) + " kilometers, ";
	    retval += toInt(1000 * (res - toInt(res))) + " meters";
	}
	return retval;
    }
}

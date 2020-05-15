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
    // FIXME: try to make input case insensitive when sober - UNRESOLVABLE
    // FIXME: TRY TO ADD VARIABLES TO .cfg file
    // FIXME: DONT FORGET TO CHANGE MAGIC NUMBERS
    // FIXME: Try from 'A' to 'A'

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

    public static final short DISPLAY_PANE_LINE_LENGTH = 56;
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

    private Color textBright = new Color(255, 255, 255);
    private Color textDark = new Color(77, 159, 206);

    /** Current city of which map is displayed */
    private static String city = "Istanbul";

    /** Image of the map to be displayed */
    private static Image MAP_IMAGE;

    /** Label component containing the image of the map */
    public static JLabel MAP_IMG_LBL;
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

    public JTextField textfield_dpt;
    public JTextField textfield_via;
    public JTextField textfield_arv;

    private JTextPane display_pane;

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
		    scaleMAP(MAP_IMG_LBL, -ZOOM_SCALE * 7);
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
	MAP_IMG_LBL = new JLabel("");

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
	frame.setBounds(SCREEN_WIDTH / 2 - FRAME_SIZE.width / 2, SCREEN_HEIGHT / 2 - FRAME_SIZE.height / 2 - 10,
		FRAME_SIZE.width, FRAME_SIZE.height);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().setLayout(null);

	JMenuBar menuBar = new JMenuBar();

	menuBar.setBounds(0, 0, FRAME_SIZE.width, 30);
	frame.getContentPane().add(menuBar);

	JMenu menu_file = new JMenu("File");
	menu_file.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	menu_file.setPreferredSize(new Dimension(MENU_SIZE.width, MENU_SIZE.height));
	menuBar.add(menu_file);

	JMenuItem menu_item_test = new JMenuItem("Test");
	menu_item_test.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_file.add(menu_item_test);

	JMenuItem menu_item_newFile = new JMenuItem("New");
	menu_item_newFile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_file.add(menu_item_newFile);

	JMenuItem menu_item_Open = new JMenuItem("Open...");
	menu_item_Open.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_file.add(menu_item_Open);

	JMenuItem menu_item_exit = new JMenuItem("Exit");
	menu_item_exit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_file.add(menu_item_exit);

	JMenu menu_edit = new JMenu("Edit");
	menu_edit.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	menu_edit.setPreferredSize(new Dimension(MENU_SIZE.width, MENU_SIZE.height));
	menuBar.add(menu_edit);

	JMenuItem menu_item_refresh = new JMenuItem("Refresh");
	menu_item_refresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_edit.add(menu_item_refresh);

	JMenuItem menu_item_cut = new JMenuItem("Cut");
	menu_item_cut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_edit.add(menu_item_cut);

	JMenuItem menu_item_copy = new JMenuItem("Copy");
	menu_item_copy.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_edit.add(menu_item_copy);

	JMenuItem menu_item_paste = new JMenuItem("Paste");
	menu_item_paste.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_edit.add(menu_item_paste);

	JMenuItem menu_item_delete = new JMenuItem("Delete");
	menu_item_delete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_edit.add(menu_item_delete);

	JMenu menu_settings = new JMenu("Settings");
	menu_settings.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	menuBar.add(menu_settings);

	JMenuItem menu_item_showDetails = new JMenuItem("Show Map Details");
	menu_item_showDetails.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_item_showDetails.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		display_pane.setText(map.toString());
		display_pane.setCaretPosition(0);
	    }
	});
	menu_settings.add(menu_item_showDetails);

	JMenu menu_mapScale = new JMenu("Map Scale");
	menu_mapScale.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	menu_settings.add(menu_mapScale);

	JCheckBoxMenuItem chcDEF = new JCheckBoxMenuItem("Default");
	chcDEF.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	chcDEF.setState(true);
	menu_mapScale.add(chcDEF);

	JCheckBoxMenuItem chcSM = new JCheckBoxMenuItem("Smooth");
	chcSM.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_mapScale.add(chcSM);

	JCheckBoxMenuItem chcFAST = new JCheckBoxMenuItem("Fast");
	chcFAST.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_mapScale.add(chcFAST);

	JCheckBoxMenuItem chcAREA = new JCheckBoxMenuItem("Area Averaging");
	chcAREA.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_mapScale.add(chcAREA);

	JCheckBoxMenuItem chcREP = new JCheckBoxMenuItem("Replicate");
	chcREP.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_mapScale.add(chcREP);

	JRadioButtonMenuItem radio_button_drag = new JRadioButtonMenuItem("Drag");
	radio_button_drag.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_settings.add(radio_button_drag);

	JPanel main_panel = new JPanel();
	main_panel.setBackground(Color.LIGHT_GRAY);
	main_panel.setBounds(0, 28, 1168, 603);
	frame.getContentPane().add(main_panel);
	main_panel.setLayout(null);

	JPanel left_navbar = new JPanel();
	left_navbar.setBackground(new Color(60, 60, 84));
	left_navbar.setBorder(new SoftBevelBorder(BevelBorder.RAISED, SystemColor.windowBorder, null, null, null));
	left_navbar.setBounds(0, 0, 369, 605);
	main_panel.add(left_navbar);
	left_navbar.setLayout(null);

	JLabel label_metromap = new JLabel("Metro Map");
	label_metromap.setBackground(SystemColor.controlShadow);
	label_metromap.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
	label_metromap.setForeground(SystemColor.textHighlight);
	label_metromap.setFont(new Font("HP Simplified", Font.BOLD, 20));
	label_metromap.setHorizontalAlignment(SwingConstants.CENTER);
	label_metromap.setBounds(10, 11, 349, 58);
	left_navbar.add(label_metromap);

	JButton optbutton_fastest = new JButton("Fastest");
	optbutton_fastest.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	optbutton_fastest.setFont(new Font("Consolas", Font.BOLD, 13));
	optbutton_fastest.setForeground(textBright);
	optbutton_fastest.setBounds(10, 80, 110, 39);
	optbutton_fastest.setUI(new MyButton());
	left_navbar.add(optbutton_fastest);

	JButton optbutton_shortest = new JButton("Shortest");
	optbutton_shortest.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	optbutton_shortest.setFont(new Font("Consolas", Font.BOLD, 13));
	optbutton_shortest.setForeground(textDark);
	optbutton_shortest.setBounds(130, 80, 110, 39);
	optbutton_shortest.setUI(new MyButton());
	left_navbar.add(optbutton_shortest);

	JButton optbutton_min = new JButton("Minimum");
	optbutton_min.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	optbutton_min.setFont(new Font("Consolas", Font.BOLD, 13));
	optbutton_min.setForeground(textDark);
	optbutton_min.setBounds(249, 80, 110, 39);
	optbutton_min.setUI(new MyButton());
	left_navbar.add(optbutton_min);

	JPanel input_panel = new JPanel();
	input_panel.setBackground(new Color(100, 100, 116));
	input_panel.setBounds(10, 130, 349, 150);
	left_navbar.add(input_panel);
	input_panel.setLayout(null);

	textfield_dpt = new JTextField();
	textfield_dpt.setFont(new Font("Courier New", Font.PLAIN, 13));
	textfield_dpt.setBounds(113, 10, 226, 30);
	input_panel.add(textfield_dpt);
	textfield_dpt.setColumns(10);

	textfield_via = new JTextField();
	textfield_via.setFont(new Font("Courier New", Font.PLAIN, 13));

	textfield_via.setColumns(10);
	textfield_via.setBounds(113, 60, 226, 30);
	input_panel.add(textfield_via);

	textfield_arv = new JTextField();
	textfield_arv.setFont(new Font("Courier New", Font.PLAIN, 13));

	textfield_arv.setColumns(10);
	textfield_arv.setBounds(113, 110, 226, 30);
	input_panel.add(textfield_arv);

	JLabel label_dpt = new JLabel("DPT.");
	label_dpt.setForeground(Color.WHITE);
	label_dpt.setFont(new Font("Consolas", Font.BOLD, 13));
	label_dpt.setHorizontalAlignment(SwingConstants.CENTER);
	label_dpt.setBounds(10, 10, 93, 26);
	input_panel.add(label_dpt);

	JLabel label_via = new JLabel("Via");
	label_via.setForeground(Color.WHITE);
	label_via.setFont(new Font("Consolas", Font.BOLD, 13));
	label_via.setHorizontalAlignment(SwingConstants.CENTER);
	label_via.setBounds(10, 62, 93, 26);
	input_panel.add(label_via);

	JLabel label_arv = new JLabel("ARV.");
	label_arv.setForeground(Color.WHITE);
	label_arv.setFont(new Font("Consolas", Font.BOLD, 13));
	label_arv.setHorizontalAlignment(SwingConstants.CENTER);
	label_arv.setBounds(10, 110, 93, 26);
	input_panel.add(label_arv);

	JButton button_refresh = new JButton("Refresh");
	button_refresh.setForeground(Color.WHITE);
	button_refresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	button_refresh.setFont(new Font("Consolas", Font.BOLD, 13));
	button_refresh.setBounds(249, 291, 110, 32);
	button_refresh.setUI(new MyButton());
	left_navbar.add(button_refresh);

	JButton button_search = new JButton("Search");
	button_search.setForeground(Color.WHITE);
	button_search.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	button_search.setFont(new Font("Consolas", Font.BOLD, 13));
	button_search.setBounds(130, 291, 110, 32);
	button_search.setUI(new MyButton());
	left_navbar.add(button_search);

	JButton button_expand = new JButton("Expand");
	button_expand.setForeground(Color.WHITE);
	button_expand.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	button_expand.setFont(new Font("Consolas", Font.BOLD, 13));
	button_expand.setUI(new MyButton());
	button_expand.setBounds(10, 291, 110, 32);
	left_navbar.add(button_expand);

	JButton button_zoom_in = new JButton("+");
	button_zoom_in.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	button_zoom_in.setFont(new Font("MS Reference Sans Serif", Font.BOLD, 14));
	button_zoom_in.setBorder(null);
	button_zoom_in.setBounds(1115, 15, 30, 30);
	main_panel.add(button_zoom_in);

	JButton button_zoom_out = new JButton("-");
	button_zoom_out.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	button_zoom_out.setFont(new Font("MS Reference Sans Serif", Font.BOLD, 14));
	button_zoom_out.setBorder(null);
	button_zoom_out.setBounds(1085, 15, 30, 30);
	main_panel.add(button_zoom_out);

	ScrollPane scrollpane_map = new ScrollPane();
	scrollpane_map.setBounds(375, 0, 793, 605);
	main_panel.add(scrollpane_map);
	scrollpane_map.getVAdjustable().setUnitIncrement(VSCROLL_RATE);
	scrollpane_map.getHAdjustable().setUnitIncrement(HSCROLL_RATE);

	MAP_IMG_LBL.setIcon(new ImageIcon(MAP_IMAGE));
	MAP_IMG_LBL.setBounds(519, 102, 46, 14);
	scrollpane_map.add(MAP_IMG_LBL);

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
	optbutton_fastest.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		optbutton_fastest.setForeground(textBright);
		optbutton_min.setForeground(textDark);
		optbutton_shortest.setForeground(textDark);
		mode = 0;
	    }
	});
	optbutton_shortest.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		optbutton_shortest.setForeground(textBright);
		optbutton_min.setForeground(textDark);
		optbutton_fastest.setForeground(textDark);
		mode = 1;
	    }
	});
	optbutton_min.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		optbutton_min.setForeground(textBright);
		optbutton_fastest.setForeground(textDark);
		optbutton_shortest.setForeground(textDark);
		mode = 2;
	    }
	});
	button_refresh.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		refresh(new Component[] { left_navbar, optbutton_fastest, optbutton_min, optbutton_shortest,
			radio_button_drag });
		scrollpane_map.setScrollPosition(0, 0);
	    }
	});

	/* Refresh option, menu item, Refreshes the screen */
	menu_item_refresh.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		refresh(new Component[] { left_navbar, optbutton_fastest, optbutton_min, optbutton_shortest,
			radio_button_drag });
		scrollpane_map.setScrollPosition(0, 0);

	    }
	});

	/* Radio button, menu item, flag for dragging */
	radio_button_drag.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent arg0) {
		drag = radio_button_drag.isSelected();
	    }
	});

	button_zoom_in.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		scaleMAP(MAP_IMG_LBL, ZOOM_SCALE);
	    }
	});
	button_zoom_out.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		scaleMAP(MAP_IMG_LBL, -ZOOM_SCALE);
	    }
	});

	/*----------------------ADDING AUTOCOMPLETION-----------------------*/
	ArrayList<String> list = new ArrayList<>();

	hash.keys().iterator().forEachRemaining(list::add);
	textfield_dpt.addKeyListener(addAutoCompletion(textfield_dpt, list));
	textfield_via.addKeyListener(addAutoCompletion(textfield_via, list));
	textfield_arv.addKeyListener(addAutoCompletion(textfield_arv, list));
	textfield_dpt.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		popup_show(false);
	    }
	});
	textfield_via.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		popup_show(false);
	    }
	});
	textfield_arv.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		popup_show(false);
	    }
	});

	JScrollPane scrollpane_display = new JScrollPane();
	scrollpane_display.setBounds(10, 334, 349, 260);
	left_navbar.add(scrollpane_display);

	display_pane = new JTextPane();
	display_pane.setFont(new Font("Consolas", Font.PLAIN, 11));
	// FIXME: VAXT QALANDA html output-a convert et
	// textPane.setContentType("text/html");

	display_pane.setEditable(false);
	scrollpane_display.setViewportView(display_pane);

	button_expand.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		// FIXME: Might add: !sRE.toString().isEmpty()
		if (searchResultsExtended != null) {
		    display_pane.setText(searchResultsExtended.toString());
		    display_pane.setCaretPosition(0);
		}
	    }
	});

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
	button_search.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		String from = textfield_dpt.getText();
		String via = (hash.contains(textfield_via.getText())) ? textfield_via.getText() : null;
		String to = textfield_arv.getText();

		if (hash.contains(from) && hash.contains(to)) {
		    /** If the mode is on MINIMUM */
		    if (mode == 2) {

			 StringBuilder searchResults = displayMin(from, via, to);
			 display_pane.setText(searchResults.toString());
			return;
		    }

		    /** For the modes SHORTEST and FASTEST */
		    StringBuilder searchResults = search(from, via, to);
		    display_pane.setText(searchResults.toString());
		}
		display_pane.setCaretPosition(0);
	    }
	});
    }

    private boolean hasTranfer(String path, String transfer) {
	System.out.println("HEEEY: " + path + "<====|====>" + transfer);
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
	System.out.println("HI: " + retval);
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
	System.out.println(from + "---->" + via);
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
	str2Append = n_times_char(DISPLAY_PANE_LINE_LENGTH, '-');
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());

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
	    str2Append += " " + line;
	    str2Append = n_times_char(54 / 2 - str2Append.length() / 2, ' ') + str2Append + "\n";
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

	str2Append = "Lead Time: " + formatResult(time);
	str2Append = n_times_char(54 / 2 - str2Append.length() / 2, ' ') + str2Append + "\n";
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.append(str2Append);

	/* Mode = 0, is for displaying distance */
	mode = 1;

	str2Append = "Distance: " + formatResult(distance);
	str2Append = n_times_char(54 / 2 - str2Append.length() / 2, ' ') + str2Append + "\n";
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.append(str2Append);

	mode = temp;

	str2Append = stations + " Station(s). | " + transfer + " Transfer(s).";
	str2Append = n_times_char(54 / 2 - str2Append.length() / 2, ' ') + str2Append + "\n";
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.append(str2Append);

	return searchResults;
    }

    private StringBuilder search(String from, String via, String to) {
	/** Output variables */
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
	str2Append = n_times_char(DISPLAY_PANE_LINE_LENGTH, '-');
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
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
	    str2Append += " " + line;
	    str2Append = n_times_char(54 / 2 - str2Append.length() / 2, ' ') + str2Append + "\n";
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
	str2Append = "Lead Time: " + formatResult(time);
	str2Append = n_times_char(54 / 2 - str2Append.length() / 2, ' ') + str2Append + "\n";
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.append(str2Append);

	/* Mode = 0, is for displaying distance */
	mode = 1;
	str2Append = "Distance: " + formatResult(distance);
	str2Append = n_times_char(54 / 2 - str2Append.length() / 2, ' ') + str2Append + "\n";
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.append(str2Append);

	mode = temp;

	str2Append = stations + " Station(s). | " + transfer + " Transfer(s).";
	str2Append = n_times_char(54 / 2 - str2Append.length() / 2, ' ') + str2Append + "\n";
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
			if (textfield_dpt.hasFocus()) {
			    textfield_dpt.setText(toolTip.getTipText());
			}
			if (textfield_via.hasFocus()) {
			    textfield_via.setText(toolTip.getTipText());
			}
			if (textfield_arv.hasFocus()) {
			    textfield_arv.setText(toolTip.getTipText());
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
	textfield_dpt.setText("");
	textfield_via.setText("");
	textfield_arv.setText("");
	((JRadioButtonMenuItem) a[4]).setSelected(false);
	drag = false;
	searchResultsExtended = new StringBuilder();
	display_pane.setText("");
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

    private String n_times_char(int n, char ch) {
	String retval = "";
	for (int i = 0; i < n; i++)
	    retval += ch;
	return retval;
    }
}

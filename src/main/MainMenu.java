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

import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import com.gnostice.pdfone.PdfViewer;

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
import java.io.IOException;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.StreamSupport;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButtonMenuItem;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import DS.DepthFirstSearch;
import DS.DepthFirstSearch;
import DS.DijkstraUndirectedSP;
import DS.Edge;
import DS.Graph;
import DS.LinearProbingHashST;
import DS.Stack;
import Dependencies.FilenameUtils;
import Dependencies.StdOut;

import java.awt.ScrollPane;
import javax.swing.JScrollBar;
import javax.swing.ImageIcon;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;

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
	    try {
		Scanner sc = new Scanner(line);
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

	JMenuItem mntmRefresh = new JMenuItem("Refresh");
	mnEdit.add(mntmRefresh);

	JMenuItem mntmCut = new JMenuItem("Cut");
	mnEdit.add(mntmCut);

	JMenuItem mntmCopy = new JMenuItem("Copy");
	mnEdit.add(mntmCopy);

	JMenuItem mntmPaste = new JMenuItem("Paste");
	mnEdit.add(mntmPaste);

	JMenuItem mntmDelete = new JMenuItem("Delete");
	mnEdit.add(mntmDelete);

	JMenu mnNewMenu = new JMenu("Settings");
	menuBar.add(mnNewMenu);

	JMenu mnMapScale = new JMenu("Map Scale");
	mnNewMenu.add(mnMapScale);

	JCheckBoxMenuItem chcDEF = new JCheckBoxMenuItem("Default");
	chcDEF.setState(true);
	mnMapScale.add(chcDEF);

	JCheckBoxMenuItem chcSM = new JCheckBoxMenuItem("Smooth");
	mnMapScale.add(chcSM);

	JCheckBoxMenuItem chcFAST = new JCheckBoxMenuItem("Fast");
	mnMapScale.add(chcFAST);

	JCheckBoxMenuItem chcAREA = new JCheckBoxMenuItem("Area Averaging");
	mnMapScale.add(chcAREA);

	JCheckBoxMenuItem chcREP = new JCheckBoxMenuItem("Replicate");
	mnMapScale.add(chcREP);

	JRadioButtonMenuItem rdbtnmntmDrag = new JRadioButtonMenuItem("Drag");
	mnNewMenu.add(rdbtnmntmDrag);

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
	lblNewLabel.setBounds(10, 11, 349, 58);
	left_navbar.add(lblNewLabel);

	JButton btnNewButton = new JButton("Fastest");
	btnNewButton.setFont(new Font("Arial", Font.PLAIN, 13));
	btnNewButton.setForeground(SystemColor.textHighlight);
	btnNewButton.setBounds(10, 80, 110, 39);
	left_navbar.add(btnNewButton);

	JButton btnShortest = new JButton("Shortest");
	btnShortest.setFont(new Font("Arial", Font.PLAIN, 13));
	btnShortest.setBounds(130, 80, 110, 39);
	left_navbar.add(btnShortest);

	JButton btnMinimum = new JButton("Minimum");
	btnMinimum.setFont(new Font("Arial", Font.PLAIN, 13));
	btnMinimum.setBounds(249, 80, 110, 39);
	left_navbar.add(btnMinimum);

	JPanel panel_1 = new JPanel();
	panel_1.setBounds(10, 130, 349, 150);
	left_navbar.add(panel_1);
	panel_1.setLayout(null);

	textField = new JTextField();
	textField.setBounds(113, 10, 226, 30);
	panel_1.add(textField);
	textField.setColumns(10);

	textField_1 = new JTextField();

	textField_1.setColumns(10);
	textField_1.setBounds(113, 60, 226, 30);
	panel_1.add(textField_1);

	textField_2 = new JTextField();

	textField_2.setColumns(10);
	textField_2.setBounds(113, 110, 226, 30);
	panel_1.add(textField_2);

	JLabel lblNewLabel_1 = new JLabel("DPT.");
	lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
	lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
	lblNewLabel_1.setBounds(10, 10, 93, 26);
	panel_1.add(lblNewLabel_1);

	JLabel lblVia = new JLabel("Via");
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
	btnRefresh.setBounds(270, 291, 89, 23);
	left_navbar.add(btnRefresh);

	JButton btnSearch = new JButton("Search");
	btnSearch.setBounds(171, 291, 89, 23);
	left_navbar.add(btnSearch);

	JButton btnNewButton_1 = new JButton("+");
	btnNewButton_1.setFont(new Font("MS Reference Sans Serif", Font.BOLD, 14));
	btnNewButton_1.setBorder(null);
	btnNewButton_1.setBounds(1115, 15, 30, 30);
	panel.add(btnNewButton_1);

	JButton button = new JButton("-");
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
		btnNewButton.setForeground(SystemColor.textHighlight);
		btnMinimum.setForeground(SystemColor.BLACK);
		btnShortest.setForeground(SystemColor.BLACK);
		mode = 0;
	    }
	});
	btnShortest.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		btnShortest.setForeground(SystemColor.textHighlight);
		btnMinimum.setForeground(SystemColor.BLACK);
		btnNewButton.setForeground(SystemColor.BLACK);
		mode = 1;
	    }
	});
	btnMinimum.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		btnMinimum.setForeground(SystemColor.textHighlight);
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
	btnExpand.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		// FIXME: Might add: !sRE.toString().isEmpty()
		if (searchResultsExtended != null)
		    textPane.setText(searchResultsExtended.toString());
	    }
	});
	btnExpand.setBounds(72, 291, 89, 23);
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
		if (mode == 2) {
		    // DepthFirstSearch dfs = new DepthFirstSearch(map, hash, textField.getText(),
		    // textField_2.getText());
		    //
		    // for (Stack<String> path : dfs.getAllPaths())
		    // StdOut.println(path.toString());
		    StdOut.println(map);

		    return;
		}

		if (hash.contains(textField.getText()) && hash.contains(textField_2.getText())) {

		    // If the 'Via' Station has been set
		    if (hash.contains(textField_1.getText())) {
			// FIXME: CODE FOR VIA
		    } else {
			StringBuilder searchResults = search_map(textField.getText(), textField_2.getText());
			textPane.setText(searchResults.toString());
		    }
		}

	    }
	});
    }

    private StringBuilder search_map(String from, String to) {
	DijkstraUndirectedSP sp = new DijkstraUndirectedSP(map, hash.get(from), mode);

	StringBuilder searchResults = new StringBuilder();
	searchResultsExtended = new StringBuilder();

	String str2Append = "";
	String line_changed = null;
	searchResults.append("DEPARTURE\n");
	searchResultsExtended.append("DEPARTURE\n");
	// Iterating through path and generating output
	Iterator<Edge> pathTo = sp.pathTo(hash.get(to)).iterator();
	Edge e = null;

	int transfer = 0;
	int stations = 1; // Stations start at one, because oz mindiyin esseyi de saymaq lazimdir

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
		searchResults.append("TRANSFER" + str2Append.substring(str2Append.indexOf(':'), str2Append.length()));

	    }
	    line_changed = e.getLine();
	}

	// append destination as well
	if (!searchResults.toString().contains(str2Append))
	    searchResults.append(str2Append);

	str2Append = "DESTINATION\n\n";
	searchResults.append(str2Append);
	searchResultsExtended.append(str2Append);

	/*
	 * Adding travel info.
	 */

	str2Append = "Fare: ";
	try (Scanner sc = new Scanner(new File(DATA_REPO + city + "//settings.cfg"))) {
	    String line = sc.nextLine();

	    /* Add total cost */
	    line = line.substring(line.indexOf('=') + 1, line.length());

	    /**
	     * Transfer + 1, because we pay when we enter the metro first time
	     */
	    String cost = String.format("%.2f", Double.parseDouble(line) * (transfer + 1));
	    str2Append += cost;

	    /* Add currency */
	    line = sc.nextLine();
	    line = line.substring(line.indexOf('=') + 1, line.length());
	    str2Append += " " + line + "\n";
	} catch (FileNotFoundException e1) {
	}
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());

	int temp = mode;
	mode = 0;
	str2Append = "Lead Time: " + formatResult(sp.distTo(hash.get(to))) + "\n";
	searchResults.replace(0, searchResults.length(), str2Append + searchResults.toString());
	searchResultsExtended.append(str2Append);

	mode = 1;
	str2Append = "Distance: " + formatResult(sp.distTo(hash.get(to))) + "\n";
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
	else
	    popup.hide();
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

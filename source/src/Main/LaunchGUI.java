package Main;

import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.JFrame;

import Utils.FilenameUtils;
import Utils.JHardware;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.awt.Rectangle;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.SystemColor;
import javax.swing.border.EtchedBorder;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
//import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButtonMenuItem;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import DS.Graph;
import DS.Edge;
import DS.Basic.ArrayList;
import DS.Basic.LinearProbingHashST;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JTextPane;
import javax.swing.LookAndFeel;
import javax.swing.JScrollPane;
import java.awt.Cursor;

public class LaunchGUI {
    
    // TODO: need an idea to how to fix the direction of an edge while being
    // displaying for example: A-B-C-D-E<br> Edges: A-B, B-C, C-D, D-E<br> when you
    // search for a route from E to C it gives: D-E, C-D
    // TODO: Try to make auto-suggestion in a list, instead of one item
    // TODO: ADD HISTORY
    /*
     * .**************************************************************************
     * GETTING CONFIGURATIONS
     ***************************************************************************/
    /** Command-Line Argument from fare.txt */
    static ArrayList<String> args;

    static {
	try (Scanner sc = new Scanner(new File(".//settings.cfg"))) {
	    args = new ArrayList<>();

	    while (sc.hasNext())
		args.add(sc.nextLine());

	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }

    /*
     * .**************************************************************************
     * GETTING CONFIGURATIONS
     ***************************************************************************/

    /** Main frame */
    private static JFrame frame;
    /** The Width of the screen */
    public static final int SCREEN_WIDTH = (int) JHardware.getScreenSize().getWidth();
    /** The Height of the screen */
    public static final int SCREEN_HEIGHT = (int) JHardware.getScreenSize().getHeight();
    /** The size of the frame determined by the ratio of screen size */
    public static final double RATIO = Double.parseDouble(IOL.getArgValue("RATIO"));
    /** The unit increment of the Vertical scroll bar of Scroll pane */
    public static final short VSCROLL_RATE = (short) Integer.parseInt(IOL.getArgValue("VSCROLL_RATE"));
    /** The unit increment of the Horizontal scroll bar of Scroll pane */
    public static final short HSCROLL_RATE = (short) Integer.parseInt(IOL.getArgValue("HSCROLL_RATE"));

    public static final short COMPONENT_MARGIN = (short) Integer.parseInt(IOL.getArgValue("COMPONENT_MARGIN"));
    /**
     * The max length of a <code>String</code> that can be printed in one line
     * without reduction
     */
    /* 54~56,I don't know why it varies, but yes magic */
    public static int DISPLAY_PANE_LINE_LENGTH = Integer.parseInt(IOL.getArgValue("DISPLAY_PANE_LINE_LENGTH"));

    /** Variable ratio for zooming in and out, default value 10% */
    public static final double ZOOM_SCALE = Double.parseDouble(IOL.getArgValue("ZOOM_SCALE"));

    /**
     * By default it is 0; 0 means Fastest route, 1 means Shortest route, 2 means to
     * find minimum of these
     */
    public static int MODE = 0;

    /** Variable for scaling the map, defines which method will be used */
    public static int MAP_SCALE_HINTS = Image.SCALE_DEFAULT;
    /** The dimension of the frame */
    public static final Rectangle FRAME_SIZE = new Rectangle(IOL.toInt(SCREEN_WIDTH * RATIO),
	    IOL.toInt(SCREEN_HEIGHT * RATIO));
    /** The dimension of the menu bar */
    public static final Rectangle MENU_SIZE = new Rectangle(100, 22);
    /** The dimension of the navigation bar on the left */
    public static final Rectangle L_NAVBAR_SIZE = new Rectangle(IOL.toInt(FRAME_SIZE.getWidth() * 0.314),
	    IOL.toInt(FRAME_SIZE.getHeight() - MENU_SIZE.height));
    /** The dimension of the buttons */
    public static final Rectangle BUTTON_SIZE = new Rectangle(
	    (int) ((L_NAVBAR_SIZE.getWidth() - COMPONENT_MARGIN * 4) / 3), (int) (L_NAVBAR_SIZE.getHeight() * 0.0611));
    /** The data repository */
    static final String DATA_REPO = ".//data//Cities//";

    private static final Font DEFAULT_FONT = new Font(IOL.getArgValue("DEFAULT_FONT"), Font.BOLD, SCREEN_WIDTH / 100);

    /** Bright color for the text highlight */
    private static final Color TEXT_BRIGHT_COLOR = IOL.getArgColor("TEXT_BRIGHT_COLOR");
    /** Dark color for the text highlight */
    private static final Color TEXT_DARK_COLOR = IOL.getArgColor("TEXT_DARK_COLOR");

    /** Current city of which map is displayed */
    public static String CITY = IOL.getArgValue("CITY");

    /** Image of the map to be displayed */
    private static Image MAP_IMAGE;

    /** Label component containing the image of the map */
    public static JLabel MAP_IMG_LBL;

    /**
     * Flag variable, shows whether left navigation bar can be dragged or not,
     * Dragging disabled by default
     */
    public static boolean DRAG_FLAG = false;
    /**
     * An instance of <code>JTextField</code> that will get an input dedicated to
     * departure station.
     */
    public JTextField textfield_dpt;
    /**
     * An instance of <code>JTextField</code> that will get an input dedicated to
     * via station.
     */
    public JTextField textfield_via;
    /**
     * An instance of <code>JTextField</code> that will get an input dedicated to
     * destination station.
     */
    public JTextField textfield_arv;
    /**
     * An output pane
     */
    private JTextPane display_pane;

    /***************************************************************************
     * VARIABLES FOR ALGO
     ***************************************************************************/
    /**
     * The linear probing hash of String keys and Integer values, which maps names
     * of the stations to theirs indices
     */
    private LinearProbingHashST<String, Integer> hash;

    private LinearProbingHashST<String, Color> colors;
    /** Metro Map */
    private Graph GRAPH;

    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    @SuppressWarnings("static-access")
	    public void run() {
		try {
		    LaunchGUI window = new LaunchGUI();
		    window.frame.setVisible(true);
		    // Thread.sleep(100);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});

    }

    /**
     * Create the application.
     */
    public LaunchGUI() {
	initMAP();
	initialize();

    }

    private void initMAP() {
	/* Initialize Data Structures */
	hash = new LinearProbingHashST<>();

	/* The Lines in metro */
	File[] roadlines = new File(DATA_REPO + CITY + "//lines").listFiles();

	/* Adding each station and assigning it an ID */
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
	IOL.setHash(hash);

	/* Display on the console, for Developers only */
	// for (String s : hash.keys()) {
	// System.out.println(hash.get(s) + ":\t" + s);
	// }

	/* Initialize initial space for Map */
	GRAPH = new Graph(hash.size());

	/* Adding stations and creating Edges */
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
		    GRAPH.addEdge(newEdge);
		    prev = key;
		}

	    } catch (FileNotFoundException e) {
	    }
	}

	// Getting the colours of the city
	try (Scanner sc = new Scanner(new File(DATA_REPO + CITY + "//line-color.txt"))) {
	    colors = new LinearProbingHashST<>();
	    while (sc.hasNext()) {
		String key_val[] = sc.nextLine().split("=");

		String cols[] = key_val[1].split(",");
		colors.put(key_val[0],
			new Color(Integer.parseInt(cols[0]), Integer.parseInt(cols[1]), Integer.parseInt(cols[2])));
	    }
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}

    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {

	frame = new JFrame();
	frame.setIconImage(new ImageIcon(".//data//icon.png").getImage());
	frame.setTitle("Easy Metro");
	frame.setResizable(false);
	frame.setSize(FRAME_SIZE.width, FRAME_SIZE.height);
	frame.setLocationRelativeTo(null);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().setLayout(null);

	JMenuBar menuBar = new JMenuBar();
	menuBar.setBounds(0, 0, FRAME_SIZE.width, 30);
	frame.getContentPane().add(menuBar);

	JMenu menu_file = new JMenu("File");
	menu_file.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	menu_file.setPreferredSize(new Dimension(MENU_SIZE.width, MENU_SIZE.height));
	menuBar.add(menu_file);

	JMenuItem menu_item_test = new JMenuItem("Test (Experimental)");
	menu_item_test.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		// TODO: ADD YOUR CODE HERE TO TEST

	    }
	});
	menu_item_test.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_file.add(menu_item_test);

	JMenuItem mntmSave = new JMenuItem("Save");
	mntmSave.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		JFileChooser fileChooser = new JFileChooser();
		LookAndFeel previousLF = UIManager.getLookAndFeel();
		try {
		    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		    fileChooser = new JFileChooser();
		    UIManager.setLookAndFeel(previousLF);
		} catch (IllegalAccessException | UnsupportedLookAndFeelException | InstantiationException
			| ClassNotFoundException e) {
		}

		/* GETTING DOCUMENTS FOLDER */
		String myDocuments = null;

		try {
		    Process p = Runtime.getRuntime().exec(
			    "reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\" /v personal");
		    p.waitFor();

		    InputStream in = p.getInputStream();
		    byte[] b = new byte[in.available()];
		    in.read(b);
		    in.close();

		    myDocuments = new String(b);
		    myDocuments = myDocuments.split("\\s\\s+")[4];

		} catch (Throwable t) {
		    t.printStackTrace();
		}

		/* GETTING DOCUMENTS FOLDER */
		fileChooser.setDialogTitle("Select Folder");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		File defaultLocation = new File(myDocuments + "//Easy Metro");
		defaultLocation.mkdir();
		fileChooser.setCurrentDirectory(defaultLocation);
		int result = fileChooser.showOpenDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION) {

		    String from = formatInput(textfield_dpt.getText());
		    String via = formatInput(textfield_via.getText());
		    String to = formatInput(textfield_arv.getText());

		    /* Making via station optional */
		    if (via != null && via.length() == 0)
			via = null;

		    /* Adding case insensitivity */
		    for (String station : hash.keys()) {
			from = IOL.equalsCaseInsensitive(station, from);
			via = IOL.equalsCaseInsensitive(station, via);
			to = IOL.equalsCaseInsensitive(station, to);
		    }

		    /* Input validation, spell check */
		    if (!IOL.validate(from, via, to)) {
			return;
		    }

		    File selectedFile = fileChooser.getSelectedFile();
		    int num = (selectedFile.listFiles() == null) ? 0 : selectedFile.listFiles().length;
		    File output = new File(selectedFile + "\\EasyMetro-Save(" + num + ").txt");
		    FileWriter fWriter;
		    try {
			fWriter = new FileWriter(output);
			PrintWriter pWriter = new PrintWriter(fWriter);
			pWriter.println(from + "→" + ((via != null) ? via + "→" : "") + to);
			pWriter.println(display_pane.getText().trim());
			pWriter.close();
		    } catch (IOException e) {
			e.printStackTrace();
		    }

		}
	    }
	});
	menu_file.add(mntmSave);

	JMenuItem menu_item_Open = new JMenuItem("Open...");
	menu_item_Open.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {

	    }
	});
	menu_item_Open.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_file.add(menu_item_Open);

	JMenuItem menu_item_exit = new JMenuItem("Exit");
	menu_item_exit.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		System.exit(0);
	    }
	});
	menu_item_exit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_file.add(menu_item_exit);

	JMenu menu_edit = new JMenu("Edit");
	menu_edit.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	menu_edit.setPreferredSize(new Dimension(MENU_SIZE.width, MENU_SIZE.height));
	menuBar.add(menu_edit);

	JMenu menu_City = new JMenu("City");
	menu_edit.add(menu_City);

	JRadioButtonMenuItem radio_button_Istanbul = new JRadioButtonMenuItem(CITY);
	radio_button_Istanbul.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	radio_button_Istanbul.setSelected(true);
	menu_City.add(radio_button_Istanbul);

	JRadioButtonMenuItem radio_button_Seoul = new JRadioButtonMenuItem("Seoul");
	radio_button_Seoul.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_City.add(radio_button_Seoul);

	JMenuItem menu_item_refresh = new JMenuItem("Refresh");
	menu_item_refresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_edit.add(menu_item_refresh);

	JMenu menu_settings = new JMenu("Settings");
	menu_settings.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	menu_settings.setPreferredSize(new Dimension(MENU_SIZE.width, MENU_SIZE.height));
	menuBar.add(menu_settings);

	JMenuItem menu_item_showDetails = new JMenuItem("Show Map Details");
	menu_item_showDetails.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_settings.add(menu_item_showDetails);
	menu_item_showDetails.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		display_pane.setText(GRAPH.toString());
		display_pane.setCaretPosition(0);
	    }
	});

	JRadioButtonMenuItem radio_button_drag = new JRadioButtonMenuItem("Drag");
	radio_button_drag.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	menu_settings.add(radio_button_drag);

	JMenu menu_mapScale = new JMenu("Scale Options");
	menu_mapScale.setPreferredSize(new Dimension(MENU_SIZE.width, MENU_SIZE.height));
	menuBar.add(menu_mapScale);
	menu_mapScale.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

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

	/////////////////////////////////////////////////////////////////////////////////
	JMenu menu_LookFeel = new JMenu("Look & Feel");
	menu_LookFeel.setPreferredSize(new Dimension(MENU_SIZE.width, MENU_SIZE.height));
	menuBar.add(menu_LookFeel);
	ArrayList<JCheckBoxMenuItem> looknfeels = new ArrayList<>();
	ArrayList<JButton> buttons = new ArrayList<>();
	for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {

	    String name = info.getName();
	    JCheckBoxMenuItem chc = new JCheckBoxMenuItem(name);
	    chc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    menu_LookFeel.add(chc);
	    looknfeels.add(chc);

	    if (UIManager.getLookAndFeel().toString().contains(info.getName())) {
		chc.setState(true);
	    }

	    chc.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (chc.isEnabled()) {
			for (int i = 0; i < looknfeels.size; i++) {
			    JCheckBoxMenuItem item = looknfeels.get(i);
			    if (!item.equals(chc))
				item.setState(false);
			}

			try {
			    UIManager.setLookAndFeel(info.getClassName());
			    SwingUtilities.updateComponentTreeUI(frame);
			    for (int i = 0; i < buttons.size; i++) {
				JButton button = buttons.get(i);
				button.setUI(new MyButton());
			    }
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {

			}

		    }
		}
	    });
	}
	///////////////////////////////////////////////////////////////////////////////////////

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

	JPanel main_panel = new JPanel();
	// main_panel.setBackground(Color.LIGHT_GRAY);
	main_panel.setBounds(IOL.toInt(FRAME_SIZE.getX()), IOL.toInt(FRAME_SIZE.getY() + 28),
		IOL.toInt(FRAME_SIZE.getWidth() - 6), IOL.toInt(FRAME_SIZE.getHeight() - 57));
	frame.getContentPane().add(main_panel);
	main_panel.setLayout(null);

	JPanel left_navbar = new JPanel();
	left_navbar.setBackground(new Color(60, 60, 84));
	left_navbar.setBorder(new SoftBevelBorder(BevelBorder.RAISED, SystemColor.windowBorder, null, null, null));
	left_navbar.setBounds(L_NAVBAR_SIZE);
	left_navbar.setLayout(null);
	main_panel.add(left_navbar);

	JLabel lblMetroMap = new JLabel("EASY METRO");
	lblMetroMap.setBackground(SystemColor.controlShadow);
	lblMetroMap.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
	lblMetroMap.setForeground(new Color(220, 220, 220));
	lblMetroMap.setFont(new Font("HP Simplified", Font.BOLD, 22));
	lblMetroMap.setHorizontalAlignment(SwingConstants.CENTER);
	lblMetroMap.setBounds(COMPONENT_MARGIN, COMPONENT_MARGIN,
		IOL.toInt(L_NAVBAR_SIZE.getWidth() - COMPONENT_MARGIN * 2),
		IOL.toInt(L_NAVBAR_SIZE.getHeight() * 0.095));
	left_navbar.add(lblMetroMap);

	JButton optbutton_fastest = new JButton("Fastest");
	optbutton_fastest.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	optbutton_fastest.setFont(DEFAULT_FONT);
	optbutton_fastest.setForeground(TEXT_BRIGHT_COLOR);
	optbutton_fastest.setBounds(COMPONENT_MARGIN, lblMetroMap.getY() + lblMetroMap.getHeight() + COMPONENT_MARGIN,
		IOL.toInt(BUTTON_SIZE.getWidth()), IOL.toInt(BUTTON_SIZE.getHeight()));
	optbutton_fastest.setUI(new MyButton());
	left_navbar.add(optbutton_fastest);
	buttons.add(optbutton_fastest);

	JButton optbutton_shortest = new JButton("Shortest");
	optbutton_shortest.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	optbutton_shortest.setFont(DEFAULT_FONT);
	optbutton_shortest.setForeground(TEXT_DARK_COLOR);
	optbutton_shortest.setBounds(optbutton_fastest.getX() + optbutton_fastest.getWidth() + COMPONENT_MARGIN,
		lblMetroMap.getY() + lblMetroMap.getHeight() + COMPONENT_MARGIN, IOL.toInt(BUTTON_SIZE.getWidth()),
		IOL.toInt(BUTTON_SIZE.getHeight()));
	optbutton_shortest.setUI(new MyButton());
	left_navbar.add(optbutton_shortest);
	buttons.add(optbutton_shortest);

	JButton optbutton_min = new JButton("Minimum");
	optbutton_min.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	optbutton_min.setFont(DEFAULT_FONT);
	optbutton_min.setForeground(TEXT_DARK_COLOR);
	optbutton_min.setBounds(optbutton_shortest.getX() + optbutton_shortest.getWidth() + COMPONENT_MARGIN,
		lblMetroMap.getY() + lblMetroMap.getHeight() + COMPONENT_MARGIN, IOL.toInt(BUTTON_SIZE.getWidth()),
		IOL.toInt(BUTTON_SIZE.getHeight()));
	optbutton_min.setUI(new MyButton());
	left_navbar.add(optbutton_min);
	buttons.add(optbutton_min);

	JPanel input_panel = new JPanel();
	input_panel.setBackground(new Color(100, 100, 116));
	input_panel.setBounds(COMPONENT_MARGIN, optbutton_min.getY() + optbutton_min.getHeight() + COMPONENT_MARGIN,
		(int) (L_NAVBAR_SIZE.getWidth() - COMPONENT_MARGIN * 2), (int) (L_NAVBAR_SIZE.getHeight() * 0.235 + 1));
	left_navbar.add(input_panel);
	input_panel.setLayout(null);

	textfield_dpt = new JTextField();
	textfield_dpt.setFont(DEFAULT_FONT);
	textfield_dpt.setColumns(10);
	textfield_dpt.setBounds(input_panel.getWidth() * 1 / 3, COMPONENT_MARGIN,
		input_panel.getWidth() * 2 / 3 - COMPONENT_MARGIN,
		(input_panel.getHeight() - COMPONENT_MARGIN * 4) / 3);// y:10~40
	input_panel.add(textfield_dpt);

	textfield_via = new JTextField();
	textfield_via.setFont(DEFAULT_FONT);
	textfield_via.setColumns(10);
	textfield_via.setBounds(input_panel.getWidth() * 1 / 3,
		textfield_dpt.getY() + textfield_dpt.getHeight() + COMPONENT_MARGIN,
		input_panel.getWidth() * 2 / 3 - COMPONENT_MARGIN,
		(input_panel.getHeight() - COMPONENT_MARGIN * 4) / 3);// y:60~90
	input_panel.add(textfield_via);

	textfield_arv = new JTextField();
	textfield_arv.setFont(DEFAULT_FONT);

	textfield_arv.setColumns(10);
	textfield_arv.setBounds(input_panel.getWidth() * 1 / 3,
		textfield_via.getY() + textfield_via.getHeight() + COMPONENT_MARGIN,
		input_panel.getWidth() * 2 / 3 - COMPONENT_MARGIN,
		(input_panel.getHeight() - COMPONENT_MARGIN * 4) / 3);// y:110~140
	input_panel.add(textfield_arv);

	JLabel label_dpt = new JLabel("DPT.");
	label_dpt.setForeground(Color.WHITE);
	label_dpt.setFont(DEFAULT_FONT);
	label_dpt.setHorizontalAlignment(SwingConstants.CENTER);
	label_dpt.setBounds(COMPONENT_MARGIN, COMPONENT_MARGIN, input_panel.getWidth() * 1 / 3,
		(input_panel.getHeight() - COMPONENT_MARGIN * 4) / 3);
	input_panel.add(label_dpt);

	JLabel label_via = new JLabel("Via");
	label_via.setForeground(Color.WHITE);
	label_via.setFont(DEFAULT_FONT);
	label_via.setHorizontalAlignment(SwingConstants.CENTER);
	label_via.setBounds(COMPONENT_MARGIN, label_dpt.getY() + label_dpt.getHeight() + COMPONENT_MARGIN,
		input_panel.getWidth() * 1 / 3, (input_panel.getHeight() - COMPONENT_MARGIN * 4) / 3);
	input_panel.add(label_via);

	JLabel label_arv = new JLabel("ARV.");
	label_arv.setForeground(Color.WHITE);
	label_arv.setFont(DEFAULT_FONT);
	label_arv.setHorizontalAlignment(SwingConstants.CENTER);
	label_arv.setBounds(COMPONENT_MARGIN, label_via.getY() + label_via.getHeight() + COMPONENT_MARGIN,
		input_panel.getWidth() * 1 / 3, (input_panel.getHeight() - COMPONENT_MARGIN * 4) / 3);
	input_panel.add(label_arv);

	JButton button_expand = new JButton("Expand");
	button_expand.setForeground(TEXT_DARK_COLOR);
	button_expand.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	button_expand.setFont(DEFAULT_FONT);
	button_expand.setUI(new MyButton());
	button_expand.setBounds(COMPONENT_MARGIN, input_panel.getY() + input_panel.getHeight() + COMPONENT_MARGIN,
		IOL.toInt(BUTTON_SIZE.getWidth()), IOL.toInt(BUTTON_SIZE.getHeight()));
	left_navbar.add(button_expand);
	buttons.add(button_expand);

	JButton button_search = new JButton("Search");
	button_search.setForeground(TEXT_DARK_COLOR);
	button_search.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	button_search.setFont(DEFAULT_FONT);
	button_search.setBounds(button_expand.getX() + button_expand.getWidth() + COMPONENT_MARGIN,
		input_panel.getY() + input_panel.getHeight() + COMPONENT_MARGIN, IOL.toInt(BUTTON_SIZE.getWidth()),
		IOL.toInt(BUTTON_SIZE.getHeight()));
	button_search.setUI(new MyButton());
	left_navbar.add(button_search);
	buttons.add(button_search);

	JButton button_refresh = new JButton("Refresh");
	button_refresh.setForeground(TEXT_DARK_COLOR);
	button_refresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	button_refresh.setFont(DEFAULT_FONT);
	button_refresh.setBounds(button_search.getX() + button_search.getWidth() + COMPONENT_MARGIN,
		input_panel.getY() + input_panel.getHeight() + COMPONENT_MARGIN, IOL.toInt(BUTTON_SIZE.getWidth()),
		IOL.toInt(BUTTON_SIZE.getHeight()));
	button_refresh.setUI(new MyButton());
	left_navbar.add(button_refresh);
	buttons.add(button_refresh);

	JScrollPane scrollpane_display = new JScrollPane();
	scrollpane_display.setBounds(10, button_search.getHeight() + button_search.getY() + 10,
		left_navbar.getWidth() - 19,
		left_navbar.getHeight() - (button_search.getHeight() + button_search.getY() + 55));
	left_navbar.add(scrollpane_display);
	scrollpane_display.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	display_pane = new JTextPane();
	EmptyBorder eb = new EmptyBorder(new Insets(10, 10, 10, 10));
	display_pane.setBorder(eb);
	// tPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
	display_pane.setMargin(new Insets(5, 5, 5, 5));
	display_pane.setFont(new Font(DEFAULT_FONT.getFamily(), Font.BOLD, DEFAULT_FONT.getSize()));
	display_pane.setEditable(false);
	scrollpane_display.setViewportView(display_pane);

	JButton button_zoom_in = new JButton("");
	button_zoom_in.setIcon(new ImageIcon(".//data//zin.png"));
	button_zoom_in.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	button_zoom_in.setBorder(null);
	button_zoom_in.setBounds((int) (FRAME_SIZE.getWidth() - COMPONENT_MARGIN * 3 - 35), COMPONENT_MARGIN, 35, 35);
	button_zoom_in.setUI(new MyButton());
	main_panel.add(button_zoom_in);
	buttons.add(button_zoom_in);

	JButton button_zoom_out = new JButton("");
	button_zoom_out.setIcon(new ImageIcon(".//data//zout.png"));
	button_zoom_out.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	button_zoom_out.setBorder(null);
	button_zoom_out.setBounds(button_zoom_in.getX() - COMPONENT_MARGIN - 35, COMPONENT_MARGIN, 35, 35);
	button_zoom_out.setUI(new MyButton());
	main_panel.add(button_zoom_out);
	buttons.add(button_zoom_out);

	JScrollPane scrollpane_map = new JScrollPane();
	scrollpane_map.setBounds(left_navbar.getWidth(), 0,
		IOL.toInt(FRAME_SIZE.getWidth() - left_navbar.getWidth() - 5), IOL.toInt(FRAME_SIZE.getHeight() - 55));
	scrollpane_map.setVisible(true);
	scrollpane_map.getVerticalScrollBar().setUnitIncrement(VSCROLL_RATE);
	scrollpane_map.getHorizontalScrollBar().setUnitIncrement(HSCROLL_RATE);
	main_panel.add(scrollpane_map);

	MAP_IMAGE = new ImageIcon(DATA_REPO + CITY + "//map.png").getImage();
	MAP_IMG_LBL = new JLabel("");
	MAP_IMG_LBL.setBorder(new BevelBorder(BevelBorder.RAISED, new Color(153, 180, 209), new Color(0, 120, 215),
		new Color(64, 64, 64), new Color(128, 128, 128)));
	MAP_IMG_LBL.setIcon(new ImageIcon(MAP_IMAGE));
	MAP_IMG_LBL.setBounds(scrollpane_map.getBounds());
	setMAP_size(scrollpane_map.getWidth() - COMPONENT_MARGIN, scrollpane_map.getHeight() - COMPONENT_MARGIN);
	scrollpane_map.setViewportView(MAP_IMG_LBL);

	/*
	 * .**************************************************************************
	 * LISTENERS
	 ***************************************************************************/
	/*---------------------- SWITCH BETWEEN CITIES ----------------------*/
	radio_button_Seoul.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {

		CITY = "Seoul";
		radio_button_Seoul.setSelected(true);
		radio_button_Istanbul.setSelected(false);
		initMAP();
		MAP_IMAGE = new ImageIcon(DATA_REPO + CITY + "//map.png").getImage();
		MAP_IMG_LBL.setIcon(new ImageIcon(MAP_IMAGE));

		setMAP_size(scrollpane_map.getWidth() - COMPONENT_MARGIN,
			scrollpane_map.getHeight() - COMPONENT_MARGIN);
		refresh(new Component[] { left_navbar, optbutton_fastest, optbutton_min, optbutton_shortest,
			radio_button_drag });
	    }
	});
	radio_button_Istanbul.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		CITY = "Istanbul";
		radio_button_Istanbul.setSelected(true);
		radio_button_Seoul.setSelected(false);
		initMAP();
		MAP_IMAGE = new ImageIcon(DATA_REPO + CITY + "//map.png").getImage();
		MAP_IMG_LBL.setIcon(new ImageIcon(MAP_IMAGE));
		setMAP_size(scrollpane_map.getWidth() - COMPONENT_MARGIN,
			scrollpane_map.getHeight() - COMPONENT_MARGIN);
		refresh(new Component[] { left_navbar, optbutton_fastest, optbutton_min, optbutton_shortest,
			radio_button_drag });

	    }
	});
	/*----------------------ADDING AUTOCOMPLETION-----------------------*/

	textfield_dpt.addKeyListener(IOL.addAutoCompletion(textfield_dpt));
	textfield_via.addKeyListener(IOL.addAutoCompletion(textfield_via));
	textfield_arv.addKeyListener(IOL.addAutoCompletion(textfield_arv));

	textfield_dpt.addMouseListener(IOL.closePopup());
	textfield_via.addMouseListener(IOL.closePopup());
	textfield_arv.addMouseListener(IOL.closePopup());

	/*-------------------------------------------------------------------*/
	/*-----------------MENUBAR -> SETTINGS -> MAP SCALE-----------------*/
	/*-----------------------------------------------------------------*/
	/** ----------------------SEARCH BUTTON---------------------- */
	button_search.addActionListener(pressSearch());

	/** ----------------------DRAG FEATURE---------------------- */
	left_navbar.addMouseMotionListener(IOL.dragLeftNavBar(left_navbar));

	/** ----------------------'FASTEST' BUTTON---------------------- */
	optbutton_fastest.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		optbutton_fastest.setForeground(TEXT_BRIGHT_COLOR);
		optbutton_min.setForeground(TEXT_DARK_COLOR);
		optbutton_shortest.setForeground(TEXT_DARK_COLOR);
		MODE = 0;
	    }
	});/** ----------------------'SHORTEST' BUTTON---------------------- */
	optbutton_shortest.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		optbutton_shortest.setForeground(TEXT_BRIGHT_COLOR);
		optbutton_min.setForeground(TEXT_DARK_COLOR);
		optbutton_fastest.setForeground(TEXT_DARK_COLOR);
		MODE = 1;
	    }
	});/** ----------------------'MINIMUM' BUTTON---------------------- */
	optbutton_min.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		optbutton_min.setForeground(TEXT_BRIGHT_COLOR);
		optbutton_fastest.setForeground(TEXT_DARK_COLOR);
		optbutton_shortest.setForeground(TEXT_DARK_COLOR);
		MODE = 2;
	    }
	});

	/** ----------------------EXPAND BUTTON---------------------- */
	button_expand.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		if (Algorithm.searchResultsExtended != null) {
		    display_pane.setText(Algorithm.searchResultsExtended.toString());
		    display_pane.setCaretPosition(0);
		}
	    }
	});

	button_refresh.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		refresh(new Component[] { left_navbar, optbutton_fastest, optbutton_min, optbutton_shortest,
			radio_button_drag });
	    }
	});
	/** ----------------------REFRESH BUTTON---------------------- */
	/* Refresh option, menu item, Refreshes the screen */
	menu_item_refresh.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		refresh(new Component[] { left_navbar, optbutton_fastest, optbutton_min, optbutton_shortest,
			radio_button_drag });

	    }
	});

	/** ----------------------DRAG BUTTON---------------------- */
	/* Radio button, menu item, flag for dragging */
	radio_button_drag.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent arg0) {
		DRAG_FLAG = radio_button_drag.isSelected();
	    }
	});

	/** ----------------------'ZOOM IN' BUTTON---------------------- */
	button_zoom_in.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		button_zoom_out.requestFocus();
		button_zoom_in.requestFocus();
		/* ZOOM IN THRESHOLD */
		int w = MAP_IMG_LBL.getWidth();
		int h = MAP_IMG_LBL.getHeight();
		w = (int) (w + w * ZOOM_SCALE);
		h = (int) (h + h * ZOOM_SCALE);
		if (w >= scrollpane_map.getWidth() * 5 || h >= scrollpane_map.getHeight() * 5) {
		    setMAP_size(scrollpane_map.getWidth() * 5, scrollpane_map.getHeight() * 5);
		    return;
		}

		/* if zoom in is possible */
		scaleMAP(ZOOM_SCALE);

	    }
	});
	/** ----------------------'ZOOM OUT' BUTTON---------------------- */
	button_zoom_out.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		button_zoom_in.requestFocus();
		button_zoom_out.requestFocus();
		/* ZOOM OUT THRESHOLD */
		int w = MAP_IMG_LBL.getWidth();
		int h = MAP_IMG_LBL.getHeight();
		w = (int) (w + w * -ZOOM_SCALE);
		h = (int) (h + h * -ZOOM_SCALE);
		if (w <= scrollpane_map.getWidth() - COMPONENT_MARGIN
			|| h <= scrollpane_map.getHeight() - COMPONENT_MARGIN) {
		    setMAP_size(scrollpane_map.getWidth() - COMPONENT_MARGIN,
			    scrollpane_map.getHeight() - COMPONENT_MARGIN);
		    return;
		}

		/* if zoom out is possible */
		scaleMAP(-ZOOM_SCALE);

	    }
	});

    }

    /***************************************************************************
     * AUX-METHODS
     ***************************************************************************/

    private static void scaleMAP(double scale) {
	int w = MAP_IMG_LBL.getWidth();
	int h = MAP_IMG_LBL.getHeight();
	setMAP_size(IOL.toInt(w + w * scale), IOL.toInt(h + h * scale));

    }

    public static void setMAP_size(int w, int h) {
	Image newImg = MAP_IMAGE.getScaledInstance(w, h, MAP_SCALE_HINTS);
	MAP_IMG_LBL.setIcon(new ImageIcon(newImg));
    }

    private void refresh(Component[] a) {
	a[0].setLocation(0, 0);
	// IOL.prev_p = null;
	// IOL.curr_p = null;
	a[1].setForeground(TEXT_BRIGHT_COLOR);
	a[2].setForeground(TEXT_DARK_COLOR);
	a[3].setForeground(TEXT_DARK_COLOR);
	MODE = 0;
	textfield_dpt.setText("");
	textfield_via.setText("");
	textfield_arv.setText("");
	((JRadioButtonMenuItem) a[4]).setSelected(false);
	DRAG_FLAG = false;
	Algorithm.searchResultsExtended = new StringBuilder();
	display_pane.setText("");
    }

    private ActionListener pressSearch() {
	return new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		String from = formatInput(textfield_dpt.getText());
		String via = formatInput(textfield_via.getText());
		String to = formatInput(textfield_arv.getText());

		/* Making via station optional */
		if (via != null && via.length() == 0)
		    via = null;

		/* Adding case insensitivity */
		for (String station : hash.keys()) {
		    from = IOL.equalsCaseInsensitive(station, from);
		    via = IOL.equalsCaseInsensitive(station, via);
		    to = IOL.equalsCaseInsensitive(station, to);
		}

		/* Input validation, spell check */
		if (!IOL.validate(from, via, to)) {
		    return;
		}

		StringBuilder searchResults;

		/* If the mode is on MINIMUM */
		if (MODE == 2) {
		    searchResults = Algorithm.displayMin(from, via, to, GRAPH, hash);
		} else {
		    /* For the modes SHORTEST and FASTEST */
		    searchResults = Algorithm.search(from, via, to, GRAPH, hash);
		}

		display_pane.setText("");// Clear
		colorifyOutput(display_pane, searchResults);// Add Color
		display_pane.setCaretPosition(0);
	    }
	};
    }

    private void colorifyOutput(JTextPane tp, StringBuilder sb) {
	String[] lines = sb.toString().split("\\n");
	for (String line : lines) {
	    Color lastToGet = Color.BLACK;

	    Iterator<String> iter = colors.keys().iterator();
	    while (iter.hasNext()) {
		String color = iter.next();
		if (line.contains(color)) {
		    String str = line.substring(line.indexOf("|") + 1, line.indexOf(":")).trim();
		    if (str.contains(" "))
			str = str.substring(str.lastIndexOf(" "), str.length()).trim();
		    lastToGet = colors.get(str);

		    break;
		}
	    }

	    tp.setEditable(true);

	    appendToPane(tp, line.substring(0, line.indexOf("|") + 1), Color.BLACK);
	    appendToPane(tp, line.substring(line.indexOf("|") + 1, line.length()) + "\n", lastToGet);

	    tp.setEditable(false);
	}
    }

    private void appendToPane(JTextPane tp, String msg, Color c) {
	if (c == null)
	    c = Color.BLACK;
	StyleContext sc = StyleContext.getDefaultStyleContext();
	AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
	tp.setCharacterAttributes(aset, false);
	tp.replaceSelection(msg);
    }

    private String formatInput(String str) {
	if (str == null)
	    return str;
	str = str.trim();
	return (str.equals("")) ? null : str;
    }

    /**
     * @deprecated Bad access to frame
     * @return main frame
     */
    public static JFrame getFrame() {
	return frame;
    }
}

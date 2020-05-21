package main;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
/**Note: Sorry for not using mine but I get problems while casting*/
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import DS.Basic.LinearProbingHashST;

/***
 * Class used for manipulating and checking Input-Output as well as contains
 * listeners
 * 
 * @author Sadig Akhund
 *
 */
public class IOL {
    /** Reference to the hash in main class */
    private static LinearProbingHashST<String, Integer> hash;

    /**
     * @return the hash
     */
    public static LinearProbingHashST<String, Integer> getHash() {
	return hash;
    }

    /**
     * @param hash
     *                 the hash to set
     */
    public static void setHash(LinearProbingHashST<String, Integer> hash) {
	IOL.hash = hash;
    }

    public static int toInt(double a) {
	return (int) a;
    }

    public static String formatResult(double res, int mode) {
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

    /**
     * Method used to check input by user. Malformed input cases: <br>
     * 1) Departure station was not specified.<br>
     * 2) Incorrect Departure station was given<br>
     * 3) Incorrect via station was given<br>
     * 4) Destination station was not specified. <br>
     * 5) The departure station was given but not correctly. <br>
     * 6) When user tries to input the same station. <br>
     * 7) <br>
     * 
     * Corresponding caution messages:<br>
     * 1) "Please specify the departure station."<br>
     * 2) "Departure station does not exist."<br>
     * 3) "Via station does not exist."<br>
     * 4) "Please specify the destination station." <br>
     * 5) "Destination station does not exist." <br>
     * 6) "Can't choose the same station." <br>
     * 
     * @param frame
     *                    the main frame to display caution messages on.
     * @param varargs
     *                    the array of input which has 3 values; the names of
     *                    departure, via and the destination station.
     * @return true if input is correct
     */
    @SuppressWarnings("deprecation")
    public static boolean validate(String... varargs) {

	/** Case 1 */
	if (varargs[0] == null) {
	    JOptionPane.showMessageDialog(MainMenu.getFrame(), "Please specify the departure station.");
	    return false;
	}
	/** Case 2. */
	if (!IOL.hash.contains(varargs[0])) {
	    JOptionPane.showMessageDialog(MainMenu.getFrame(), "Departure station does not exist.");
	    return false;
	}

	/** Case 3. */
	if (varargs[1] != null && !IOL.hash.contains(varargs[1])) {
	    JOptionPane.showMessageDialog(MainMenu.getFrame(), "Via station does not exist.");
	    return false;
	}

	/** Case 4. */
	if (varargs[2] == null) {
	    JOptionPane.showMessageDialog(MainMenu.getFrame(), "Please specify the destionation station.");
	    return false;
	}

	/** Case 5. */
	if (!IOL.hash.contains(varargs[2])) {
	    JOptionPane.showMessageDialog(MainMenu.getFrame(), "Destination station does not exist.");
	    return false;
	}

	/** Case 6. */
	boolean flag = false;
	if (varargs[1] == null) {
	    /* when there is no 'via' */
	    if (varargs[0].equals(varargs[2])) {
		flag = true;
	    }
	} else {
	    /** Case 7. */
	    if (!IOL.hash.contains(varargs[2])) {
		JOptionPane.showMessageDialog(MainMenu.getFrame(), "Destination station does not exist.");
		return false;
	    }
	    if (varargs[0].equals(varargs[1]) || varargs[1].equals(varargs[2])) {
		flag = true;
	    }
	}
	if (flag) {
	    JOptionPane.showMessageDialog(MainMenu.getFrame(), "Can't choose the same station.");
	    return false;
	}

	return true;

    }

    /* VARIABLES FOR AUTO-SUGGESTION */
    /*
     * These Variables are shared between 3 text fields in order to reduce memory
     * usage plus only one instance of suggestion needed at a time
     */
    private static JToolTip toolTip = new JToolTip();
    private static Popup popup;
    private static PopupFactory pf = new PopupFactory();
    private static List<String> sugg = new ArrayList<String>();
    private static ArrayList<JTextField> textFields = new ArrayList<>();

    /**
     * There is a need to test for a concurrency since each keyboard key press is
     * one thread, and trying to show pop-up at the same time creates leftover
     * pieces of components which are not stored in variables but at the same time
     * stored inside of some component. This is unwanted because such 'leftovers'
     * obscure GUI.
     */
    public static boolean CONCURRENCY = false;

    /**
     * Method that initializes an auto-completion <code>KeyAdapter</code> for text
     * fields.
     * 
     * @param textFldCurrent
     *                           The text field which is being added.
     * @param list
     *                           The list of string out of which suggestion will be
     *                           done.
     * @return An instance of <code>KeyAdapter</code> that will contain instruction.
     */
    public static KeyAdapter addAutoCompletion(JTextField textFldCurrent) {

	return new KeyAdapter() {
	    @Override
	    public void keyTyped(KeyEvent key) {
		if (CONCURRENCY)
		    return;
		CONCURRENCY = true;

		ArrayList<String> list = new ArrayList<>();
		hash.keys().iterator().forEachRemaining(list::add);

		/* Save the Text Field in a list */
		textFields.add(textFldCurrent);
		/* Get the list of suggested options */
		sugg = query(textFldCurrent.getText(), list);

		/*
		 * If there is no input, or no suggestion or else current text field has no
		 * focus, don't do anything
		 */
		if (textFldCurrent.getText().length() == 0 || sugg.size() == 0 || !textFldCurrent.hasFocus()) {
		    popup_show(false);
		    CONCURRENCY = false;
		    return;
		}

		/* Set the first suggested word */
		toolTip.setTipText(sugg.get(0));

		// Coordinates
		int x = IOL.toInt(textFldCurrent.getLocationOnScreen().getX());
		int y = IOL.toInt(textFldCurrent.getLocationOnScreen().getY()) + textFldCurrent.getHeight();

		/* Initialize pop-up */
		popup_show(false);
		popup = pf.getPopup(textFldCurrent, toolTip, x, y);
		popup_show(true);

		/*
		 * Mouse Listener for clicking on pop-up to paste the contents into text field
		 */
		toolTip.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
			for (JTextField textfield : textFields)
			    if (textfield.hasFocus())
				textfield.setText(toolTip.getTipText());
			popup_show(false);
		    }
		});
		CONCURRENCY = false;
	    }
	};

    }

    public static List<String> query(String queryStr, List<String> list) {
	List<String> suggestion = new ArrayList<>();
	list.forEach(std -> {
	    if (isMatched(queryStr, std)) {
		suggestion.add(String.valueOf(std));
	    }
	});

	return suggestion;
    }

    private static boolean isMatched(String query, String text) {
	return text.toLowerCase().contains(query.toLowerCase());
    }

    /**
     * Method used to close pop-ups.
     * 
     * @return An instance of <code>MouseAdapter</code> that will contain
     *         instruction.
     */
    public static MouseAdapter closePopup() {
	return new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		IOL.popup_show(false);
	    }
	};
    }

    /**
     * AUX method for showing and hiding suggestion popup
     * 
     * @param yes
     *                boolean flag
     */
    public static void popup_show(boolean yes) {
	if (popup == null)
	    return;
	if (yes)
	    popup.show();
	else {
	    popup.hide();
	}
    }

    /* Points used for dragging left navigation bar */
    /** Previous point on screen */
    public static Point prev_p;
    /** Current point on screen */
    public static Point curr_p;

    /**
     * Method that initializes a dragging feature in
     * <code>MouseMotionAdapter</code>.
     * 
     * @param left_navbar
     *                        the component that is intended to be dragable.
     * @return An instance of <code>MouseMotionAdapter</code> that will contain
     *         instruction.
     */
    public static MouseMotionAdapter dragLeftNavBar(Component left_navbar) {

	return new MouseMotionAdapter() {

	    @Override
	    public void mouseDragged(MouseEvent e) {
		/* check for dragging option */
		if (!MainMenu.DRAG_FLAG)
		    return;

		/* Close all pop-ups */
		IOL.popup_show(false);

		/* Current location of the mouse on the screen */
		curr_p = e.getLocationOnScreen();

		/* First time */
		if (prev_p == null)
		    prev_p = curr_p;

		/*
		 * Move the component by the difference of previous and current locations of the
		 * mouse pointer
		 */
		left_navbar.setLocation(left_navbar.getX() + (curr_p.x - prev_p.x), left_navbar.getY());

		/* Refresh */
		prev_p = curr_p;

	    }
	};
    }

    public static String equalsCaseInsensitive(String a, String b) {
	if (a == null || b == null)
	    return null;
	if (a.toLowerCase().equals(b.toLowerCase()))
	    return a;
	else
	    return b;
    }

    public static String getValue(String pattern) {
	for (int i = 0; i < MainMenu.args.length; i++) {
	    if (MainMenu.args[i].contains(pattern)) {
		return MainMenu.args[i].substring(MainMenu.args[i].indexOf("=") + 1);
	    }
	}
	return null;
    }
}

package Main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*I am using this only for lambda expressions, since lambda exp. uses List interface. */

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import DS.Basic.ArrayList;
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

    /* VARIABLES FOR AUTO-SUGGESTION */
    /*
     * These Variables are shared between 3 text fields in order to reduce memory
     * usage plus only one instance of suggestion needed at a time
     */
    private static JToolTip toolTip = new JToolTip();
    private static Popup popup;
    private static PopupFactory pf = new PopupFactory();

    /** List containing the suggestions */
    private static ArrayList<String> sugg = new ArrayList<String>();
    /** List containing references for the text-fields */
    private static DS.Basic.ArrayList<JTextField> textFields = new DS.Basic.ArrayList<>();

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
     * 2) Incorrect Departure station was given.<br>
     * 3) Incorrect via station was given; but only when it is not empty<br>
     * 4) Destination station was not specified. <br>
     * 5) The Destination station was given but not correctly. <br>
     * 6) When user tries to input the same station. <br>
     * 7) When there is no possible route or the lines are not connect. (This case
     * can only be tested during calculation). See: <code>Algorithm.java</code><br>
     * 
     * Corresponding caution messages:<br>
     * 1) "Please specify the departure station."<br>
     * 2) "Departure station does not exist."<br>
     * 3) "Via station does not exist."<br>
     * 4) "Please specify the destination station." <br>
     * 5) "Destination station does not exist." <br>
     * 6) "Can't choose the same station." <br>
     * 7) "There is no route."
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
	    JOptionPane.showMessageDialog(Main.getFrame(), "Please specify the departure station.");
	    return false;
	}
	/** Case 2. */
	if (!IOL.hash.contains(varargs[0])) {
	    JOptionPane.showMessageDialog(Main.getFrame(), "Departure station does not exist.");
	    return false;
	}

	/** Case 3. */
	if (varargs[1] != null && !IOL.hash.contains(varargs[1])) {
	    JOptionPane.showMessageDialog(Main.getFrame(), "Via station does not exist.");
	    return false;
	}

	/** Case 4. */
	if (varargs[2] == null) {
	    JOptionPane.showMessageDialog(Main.getFrame(), "Please specify the destination station.");
	    return false;
	}

	/** Case 5. */
	if (!IOL.hash.contains(varargs[2])) {
	    JOptionPane.showMessageDialog(Main.getFrame(), "Destination station does not exist.");
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

	    if (varargs[0].equals(varargs[1]) || varargs[1].equals(varargs[2])) {
		flag = true;
	    }
	}
	if (flag) {
	    JOptionPane.showMessageDialog(Main.getFrame(), "Can't choose the same station.");
	    return false;
	}

	return true;

    }

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
		    showPopUp(false);
		    CONCURRENCY = false;
		    return;
		}

		/* Set the first suggested word */
		toolTip.setTipText(sugg.get(0));

		// Coordinates
		int x = IOL.toInt(textFldCurrent.getLocationOnScreen().getX());
		int y = IOL.toInt(textFldCurrent.getLocationOnScreen().getY()) + textFldCurrent.getHeight();

		/* Initialize pop-up */
		showPopUp(false);
		popup = pf.getPopup(textFldCurrent, toolTip, x, y);
		showPopUp(true);

		/*
		 * Mouse Listener for clicking on pop-up to paste the contents into text field
		 */
		toolTip.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {

			for (int i = 0; i < textFields.size; i++) {
			    JTextField textfield = textFields.get(i);
			    if (textfield.hasFocus())
				textfield.setText(toolTip.getTipText());
			}
			showPopUp(false);
		    }
		});
		CONCURRENCY = false;
	    }
	};

    }

    /**
     * Finds the list of suggested strings.
     * 
     * @param queryStr
     *                     string to look for
     * @param list
     *                     Looking out of string in this list
     * @return list of suggested strings
     */
    public static ArrayList<String> query(String queryStr, ArrayList<String> list) {
	ArrayList<String> suggestion = new ArrayList<>();
	java.util.Iterator<String> iter = list.iterator();
	while (iter.hasNext()) {
	    String std = iter.next();
	    if (isMatched(queryStr, std)) {
		suggestion.add(String.valueOf(std));
	    }
	}
	return suggestion;
    }

    /**
     * Asserts equality of given strings, where case of a letter is not taken into
     * account.
     * 
     * @param query
     *                  query string
     * @param text
     *                  text string
     * @return true if equals.
     */
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
		IOL.showPopUp(false);
	    }
	};
    }

    /**
     * AUX method for showing and hiding suggestion pop-up
     * 
     * @param show
     *                 boolean flag
     */
    public static void showPopUp(boolean show) {
	if (popup == null)
	    return;
	if (show)
	    popup.show();
	else {
	    popup.hide();
	}
    }

    /**
     * Method that initializes a dragging feature in an instance of
     * <code><b>MouseMotionAdapter</b></code>.
     * 
     * @param left_navbar
     *                        the component that is intended to be drag-able.
     * @return An instance of <code><b>MouseMotionAdapter</b></code> that will
     *         contain instruction.
     */
    public static MouseAdapter dragLeftNavBar(Component left_navbar) {

	return new MouseAdapter() {

	    /* Points used for dragging left navigation bar */
	    /** Previous point on screen */
	    public Point prev_p;
	    /** Current point on screen */
	    public Point curr_p;

	    @Override
	    public void mouseDragged(MouseEvent e) {

		/* check for dragging option */
		if (!Main.DRAG_FLAG) {
		    prev_p = null;
		    return;
		}

		/* Close all pop-ups */
		IOL.showPopUp(false);

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

    /**
     * Asserts equality of given strings, where case of a letter is not taken into
     * account.
     * 
     * @param a
     *              first string
     * @param b
     *              second string
     * @return The first String if equals, else the second one
     */
    public static String equalsCaseInsensitive(String a, String b) {

	if (a == null || b == null)
	    return null;
	if (a.toLowerCase().equals(b.toLowerCase()))
	    return a;
	else
	    return b;
    }

    /**
     * Retrieves value of a given key, similar to hash map. Example: "key=value"
     * method returns "value".
     * 
     * @param pattern
     *                    a string pattern containing information for color
     * @return retrieved value
     */
    public static String getArgValue(String pattern) {
	for (int i = 0; i < Main.args.size(); i++) {
	    String item = Main.args.get(i);
	    if (item.contains(pattern)) {
		return item.substring(item.indexOf("=") + 1);
	    }
	}
	return null;
    }

    /**
     * Gets an instance of <code>Color</code> class, from given pattern.
     * 
     * @param pattern
     *                    a string pattern containing information for color
     * @return retrieved Color
     */
    public static Color getArgColor(String pattern) {
	String[] RGB = getArgValue(pattern).split(",");
	return new Color(Integer.parseInt(RGB[0].trim()), Integer.parseInt(RGB[1].trim()),
		Integer.parseInt(RGB[2].trim()));
    }

    /**
     * Setter for this class's field of
     * <code><b>LinearProbingHashST<String, Integer></b></code> instance.
     * 
     * @param hash
     *                 the hash to set
     */
    public static void setHash(LinearProbingHashST<String, Integer> hash) {
	IOL.hash = hash;
    }

    public static int toInt(double a) {
	return (int) a;
    }

}

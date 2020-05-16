package main;

import java.awt.EventQueue;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.FormSubmitEvent;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.JTextField;
import java.io.File;
import java.util.Scanner;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

public class Reportform {

    private JFrame frame;
    private JTextField textField;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    Reportform window = new Reportform();
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
    public Reportform() {
	initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {

	JTextPane textPane = new JTextPane();
	StyledDocument doc = textPane.getStyledDocument();

	Style style = textPane.addStyle("I'm a Style", null);
	StyleConstants.setForeground(style, Color.red);

	try {
	    doc.insertString(doc.getLength(), "BLAH ", style);
	} catch (BadLocationException e) {
	}

	StyleConstants.setForeground(style, Color.blue);

	try {
	    doc.insertString(doc.getLength(), "BLEH", style);
	} catch (BadLocationException e) {
	}

	frame = new JFrame("Test");
	frame.getContentPane().add(textPane);
	frame.pack();

    }

}

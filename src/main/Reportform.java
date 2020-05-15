package main;

import java.awt.EventQueue;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.FormSubmitEvent;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.JTextField;
import java.io.File;
import java.util.Scanner;

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

	frame = new JFrame();
	frame.setBounds(100, 100, 701, 425);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	JEditorPane textPane = new JEditorPane();
	textPane.setContentType("text/html");
	textPane.setEditable(false);

	StringBuilder sb = new StringBuilder();
	try (Scanner sc = new Scanner(new File("./data/form/index.html"))) {
	    while (sc.hasNext()) {
		sb.append(sc.nextLine() + "\n");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	System.out.print(sb.toString());
	textPane.setText(sb.toString());

//	frame.getContentPane().add(textPane);

	


    }

}

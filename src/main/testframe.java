package main;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

public class testframe {

    private JFrame frame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    testframe window = new testframe();
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
    public testframe() {
	initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
	frame = new JFrame();
	frame.setBounds(100, 100, 450, 300);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	NativeInterface.initialize();
	NativeInterface.open();
	JWebBrowser fileBrowser = new JWebBrowser();
	fileBrowser.setVisible(true);
	fileBrowser.setBarsVisible(false);
	fileBrowser.setStatusBarVisible(false);
	frame.add(fileBrowser, BorderLayout.CENTER);
	fileBrowser.navigate(".\\Cut-Rod.pdf");
    }

}

package Main;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class test {

    test() {
	JFrame f = new JFrame(this.getClass().getSimpleName());
	f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	f.add(new ColoredPanel(Color.GREEN));
	f.add(new ColoredPanel(Color.RED));

	f.pack();
	f.setVisible(true);
    }

    public static void main(String[] args) {
	// Runnable r = new Runnable() {
	// @Override
	// public void run() {
	// new test();
	// }
	// };
	// SwingUtilities.invokeLater(r);
	int cnt = 0;
	int arr[] = { 1, 2, 3, 4, 5 };
	for (int i = 0; i < 5; i++) {
	    for (int j = 0; j < 5; j++) {
		for (int k = 0; k < 5; k++) {
		    System.out.println("(" + arr[i] + ", " + arr[j] + ", " + arr[k] + ")");
		    cnt++;
		}
	    }
	}
	System.out.println(cnt);
    }
}

class ColoredPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    ColoredPanel(Color color) {
	setBackground(color);
	setBorder(new EmptyBorder(20, 150, 20, 150));
    }
}
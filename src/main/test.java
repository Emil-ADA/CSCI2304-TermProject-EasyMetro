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
        Runnable r = new Runnable() {
            @Override
            public void run() {
                new test();
            }
        };
        SwingUtilities.invokeLater(r);
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
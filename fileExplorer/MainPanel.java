package fileExplorer;

import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

class MainPanel extends JPanel {

    private static final long serialVersionUID = -2749753580332537665L;
    private GridBagLayout layout;

    public MainPanel() {
        Dimension size = getPreferredSize();
        size.height = 900;
        setPreferredSize(size);

        setBorder(BorderFactory.createTitledBorder("Directory"));

        layout = new GridBagLayout();
        setLayout(layout);

    }
}
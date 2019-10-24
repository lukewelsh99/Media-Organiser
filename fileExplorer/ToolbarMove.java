package fileExplorer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

class ToolbarPanelMove extends ToolbarPanel {

    private static final long serialVersionUID = -947627618410029133L;
    public JButton backButton;
    public JButton moveHereButton;
    public JButton cancelButton;

    public ToolbarPanelMove() {
        Dimension size = getPreferredSize();
        size.height = 100;
        setPreferredSize(size);

        moveHereButton = new JButton("Move Here");
        cancelButton = new JButton("Cancel");
        backButton = new JButton("Back");

        backButton.addActionListener(new ActionListener() {
            private final String type = "back";

            public void actionPerformed(ActionEvent e) {
                fireToolbarEvent(new ToolbarEvent(this, type));
            }
        });

        moveHereButton.addActionListener(new ActionListener() {
            private final String type = "moveHere";

            public void actionPerformed(ActionEvent e) {
                fireToolbarEvent(new ToolbarEvent(this, type));
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            private final String type = "cancelMove";

            public void actionPerformed(ActionEvent e) {
                fireToolbarEvent(new ToolbarEvent(this, type));
            }
        });

        add(backButton, BorderLayout.WEST);
        add(moveHereButton, BorderLayout.CENTER);
        add(cancelButton, BorderLayout.EAST);
    }

}
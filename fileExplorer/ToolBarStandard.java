package fileExplorer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

class ToolbarPanelStandard extends ToolbarPanel {

    private static final long serialVersionUID = -947627618410029133L;
    public JButton backButton;
    public JButton deselectButton;
    public JButton openButton;
    public JButton deleteButton;
    public JButton moveButton;

    public ToolbarPanelStandard() {
        Dimension size = getPreferredSize();
        size.height = 100;
        setPreferredSize(size);

        deselectButton = new JButton("Deselect");
        openButton = new JButton("Open");
        deleteButton = new JButton("Delete");
        moveButton = new JButton("Move");
        backButton = new JButton("Back");

        backButton.addActionListener(new ActionListener() {
            private final String type = "back";

            public void actionPerformed(ActionEvent e) {
                fireToolbarEvent(new ToolbarEvent(this, type));
            }
        });

        deselectButton.addActionListener(new ActionListener() {
            private final String type = "deselect";

            public void actionPerformed(ActionEvent e) {
                fireToolbarEvent(new ToolbarEvent(this, type));
            }
        });

        openButton.addActionListener(new ActionListener() {
            private final String type = "open";

            public void actionPerformed(ActionEvent e) {
                fireToolbarEvent(new ToolbarEvent(this, type));
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            private final String type = "delete";

            public void actionPerformed(ActionEvent e) {
                fireToolbarEvent(new ToolbarEvent(this, type));
            }
        });

        moveButton.addActionListener(new ActionListener() {
            private final String type = "move";

            public void actionPerformed(ActionEvent e) {
                fireToolbarEvent(new ToolbarEvent(this, type));
            }
        });

        deselectButton.setVisible(false);
        openButton.setVisible(false);
        deleteButton.setVisible(false);
        moveButton.setVisible(false);

        add(backButton, BorderLayout.WEST);
        add(deselectButton, BorderLayout.SOUTH);
        add(openButton, BorderLayout.CENTER);
        add(deleteButton, BorderLayout.EAST);
        add(moveButton, BorderLayout.NORTH);

    }

}
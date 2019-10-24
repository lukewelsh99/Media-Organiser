package fileExplorer;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.event.EventListenerList;

class MainFrame extends JFrame {

    public ToolbarPanelStandard toolbarPanel;
    public ToolbarPanelMove movePanel;
    public ToolbarPanelInfo infoPanel;
    public ToolbarPanelGeneral generalPanel;
    private final MainPanel mainPanel;

    private static final long serialVersionUID = -3188546411353950071L;

    private final EventListenerList listenerList = new EventListenerList();

    public MainFrame(String title) {
        super(title);

        // Set layout
        setLayout(new BorderLayout());

        // Tool panel
        toolbarPanel = new ToolbarPanelStandard();

        // Pass the event up to the file explorer
        toolbarPanel.addToolbarListener(new ToolbarListener() {
            public void toolbarEventOccurred(ToolbarEvent event) {
                fireToolbarEvent(event);
            }
        });

        // Move panel
        movePanel = new ToolbarPanelMove();

        // Pass the event up to the file explorer
        movePanel.addToolbarListener(new ToolbarListener() {
            public void toolbarEventOccurred(ToolbarEvent event) {
                fireToolbarEvent(event);
            }
        });

        movePanel.setVisible(false);

        // Info panel
        infoPanel = new ToolbarPanelInfo();

        // Pass the event up to the file explorer
        infoPanel.addToolbarListener(new ToolbarListener() {
            public void toolbarEventOccurred(ToolbarEvent event) {
                fireToolbarEvent(event);
            }
        });

        infoPanel.setVisible(false);

        // Category panel
        generalPanel = new ToolbarPanelGeneral();

        // Pass the event up to the file explorer
        generalPanel.addToolbarListener(new ToolbarListener() {
            public void toolbarEventOccurred(ToolbarEvent event) {
                fireToolbarEvent(event);
            }
        });

        // Main panel
        mainPanel = new MainPanel();

        // Add components
        Container container = getContentPane();
        container.add(toolbarPanel, BorderLayout.NORTH);
        container.add(mainPanel, BorderLayout.CENTER);
        container.add(movePanel, BorderLayout.SOUTH);
        container.add(infoPanel, BorderLayout.EAST);
        container.add(generalPanel, BorderLayout.WEST);

    }

    public void fireToolbarEvent(ToolbarEvent event) {
        Object[] listeners = listenerList.getListenerList();

        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == ToolbarListener.class) {
                ((ToolbarListener) listeners[i + 1]).toolbarEventOccurred(event);
            }
        }
    }

    public void addToolbarListener(ToolbarListener listener) {
        listenerList.add(ToolbarListener.class, listener);
    }

    public void removeToolbarListener(ToolbarListener listener) {
        listenerList.remove(ToolbarListener.class, listener);
    }

    public MainPanel getMainPanel() {
        return this.mainPanel;
    }

}
package fileExplorer;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

class ToolbarPanel extends JPanel {

    private static final long serialVersionUID = -2749753580332537665L;
    private BorderLayout layout;

    private EventListenerList listenerList = new EventListenerList();

    public ToolbarPanel() {
        setBorder(BorderFactory.createTitledBorder("Toolbar"));

        this.layout = new BorderLayout();
        setLayout(layout);

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
}
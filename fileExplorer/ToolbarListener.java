package fileExplorer;

import java.util.EventListener;

interface ToolbarListener extends EventListener {

    public void toolbarEventOccurred(ToolbarEvent event);

}
package fileExplorer;

import java.util.EventObject;

class ToolbarEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    private String type;

    public ToolbarEvent(Object source, String type) {
        super(source);
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
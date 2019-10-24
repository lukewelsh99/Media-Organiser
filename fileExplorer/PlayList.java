package fileExplorer;

import java.util.ArrayList;

class PlayList {

    private final String name;
    private ArrayList<String> filePaths;

    public ArrayList<String> getFilePaths() {
        return filePaths;
    }

    public void setFilePaths(ArrayList<String> filePaths) {
        this.filePaths = filePaths;
    }

    public String getName() {
        return name;
    }

    public void addFilePath(String filePath) {
        filePaths.add(filePath);
    }

    public PlayList(String playListName) {
        this.name = playListName;
        this.filePaths = new ArrayList<>();
    }

}

package fileExplorer;

import java.util.ArrayList;

public class PlayListFactory {

    private static ArrayList<PlayList> playListList = new ArrayList<>();

    public static ArrayList<PlayList> getPlayListList() {
        return playListList;
    }

    public static PlayList makePlayList(String playListName) {
        PlayList newPlayList = new PlayList(playListName);
        playListList.add(newPlayList);
        return newPlayList;
    }

    public static PlayList getPlayList(String playListName) {
        PlayList playList = null;
        for (int i = 0; i < playListList.size(); i++) {
            if (playListList.get(i).getName().equals(playListName)) {
                playList = playListList.get(i);
                break;
            }
        }
        return playList;
    }

    public static void removePlayList(String playListName) {
        for (int i = 0; i < playListList.size(); i++) {
            if (playListList.get(i).getName().equals(playListName)) {
                playListList.remove(i);
                break;
            }
        }
    }

}

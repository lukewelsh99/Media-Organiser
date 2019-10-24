package fileExplorer;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

final class FileExplorer {

    private boolean initialised;
    private ArrayList<String> historyStack;
    private ArrayList<JButton> buttonList;
    private String selectedItem;
    private String fileToMove;
    private String currentDirectory;
    private String originDirectory;
    private PlayList currentPlayList;
    private MainFrame mainFrame;
    private final String saveFilePath;

    private static final String DEFAULT_ORIGIN_DIRECTORY = "C:/";
    private static final String SAVE_FILE_NAME = "saveFile.json";

    public String getOriginDirectory() {
        return this.originDirectory;
    }

    public String getcurrentDirectory() {
        return this.currentDirectory;
    }

    public boolean isInitialised() {
        return this.initialised;
    }

    public FileExplorer() {
        this(DEFAULT_ORIGIN_DIRECTORY);
    }

    public FileExplorer(String originDirectory) {
        this.originDirectory = originDirectory;
        this.currentDirectory = originDirectory;
        this.historyStack = new ArrayList<>();
        this.buttonList = new ArrayList<>();
        this.selectedItem = "";
        this.saveFilePath = this.originDirectory + "\\" + FileExplorer.SAVE_FILE_NAME;
        this.currentPlayList = null;

        this.buildPanel();

        //Load categories and playlists from file
        this.loadCategories();
        this.loadPlayLists();

        this.loadFolder(this.currentDirectory);
    }

    public MediaFile[] getFilesIn(String path) {
        File directory = new File(path);
        return getFilesIn(directory);
    }

    public MediaFile[] getFilesIn(File directory) {
        File[] fileList = directory.listFiles();
        MediaFile[] returnArray = new MediaFile[fileList.length];

        for (int i = 0; i < fileList.length; i++) {
            //Do not configure the save file
            if (!fileList[i].getName().equals(FileExplorer.SAVE_FILE_NAME)) {
                MediaFile file = null;

                String path = fileList[i].getAbsolutePath();
                JSONObject mediaFile = this.readMediaFile(path);

                if (mediaFile != null) {
                    file = new MediaFile(mediaFile, path);
                } else {
                    file = new MediaFile(path);
                    saveMediaFile(file);
                }

                returnArray[i] = file;
            }
        }

        return returnArray;

    }

    private void loadFolder(MediaFile[] fileList) {
        mainFrame.setVisible(false);
        MainPanel mainPanel = mainFrame.getMainPanel();

        // Delete old buttons
        for (int i = 0; i < buttonList.size(); i++) {
            mainPanel.remove(buttonList.get(i));
        }
        buttonList = new ArrayList<>();

        //// Calculate required grid
        int numButtons = fileList.length;
        // Grid to be 4 colulms, dynamic row number
        int rowNumber = numButtons / 4 + numButtons % 4;
        mainPanel.setLayout(new GridLayout(4, rowNumber));

        String typeSearch = mainFrame.generalPanel.getTypeSearch();
        String[] allowedTypes = null;
        if (typeSearch != null && typeSearch.length() > 0) {
            allowedTypes = typeSearch.split(",");
        }

        // Create buttons
        for (MediaFile file : fileList) {
            if (file != null) {

                boolean matchesType = false;
                //Check type search
                if (allowedTypes != null) {
                    String fileType = getFileType(file.getName());
                    for (int i = 0; i < allowedTypes.length; i++) {
                        if (allowedTypes[i].trim().equalsIgnoreCase(fileType)) {
                            matchesType = true;
                            break;
                        }
                    }
                } else {
                    matchesType = true;
                }

                if (matchesType) {

                    //Check if theres an image
                    String imagePath = file.getImagePath();
                    Icon buttonIcon = null;
                    if (imagePath.length() > 0) {
                        buttonIcon = new ImageIcon(imagePath, file.getName());
                    }

                    // Create button
                    JButton newBtn;
                    if (buttonIcon != null) {
                        newBtn = new JButton(buttonIcon);

                    } else {
                        newBtn = new JButton(file.getName());
                    }

                    // Add click event
                    newBtn.addActionListener(new ActionListener() {
                        String absolutePath = file.getAbsolutePath();

                        @Override
                        public void actionPerformed(ActionEvent e) {

                            if (file.isFile()) {
                                // If this item is selected
                                if (FileExplorer.isSameFile(selectedItem, absolutePath)) {
                                    openFile(file);
                                } else {
                                    selectFile(file, newBtn);
                                }
                            } else {
                                openFile(file);

                            }
                        }

                    });

                    // Set button style
                    newBtn.setFont(newBtn.getFont().deriveFont(Font.PLAIN));
                    newBtn.setBackground(Color.WHITE);
                    newBtn.setForeground(Color.BLACK);

                    // Add to main frame
                    mainPanel.add(newBtn);

                    // Add to list
                    buttonList.add(newBtn);
                }
            }
        }

        mainFrame.setVisible(true);
    }

    private void loadFolder(String path) {
        // Change origin directory to new path
        this.currentDirectory = path;
        this.currentPlayList = null;

        // Deselect any currently selected
        deselect();
        MediaFile[] fileList = getFilesIn(path);

        loadFolder(fileList);

    }

    void openPlayList() {
        String playListName = selectPlayList();
        if (playListName != null) {

            PlayList playList = PlayListFactory.getPlayList(playListName);

            if (playList != null) {
                currentPlayList = playList;
                openPlayList(playList);
            }

        }
    }

    void openPlayList(PlayList playList) {
        ArrayList<String> filePaths = playList.getFilePaths();
        ArrayList<MediaFile> mediaFiles = new ArrayList<>();

        JSONArray jsonMediaFiles = readMediaFiles();

        for (int i = 0; i < filePaths.size(); i++) {
            String filePath = filePaths.get(i);

            JSONObject jsonMediaFile = null;
            for (int i1 = 0; i1 < jsonMediaFiles.size(); i1++) {
                JSONObject tempJsonMediaFile = (JSONObject) jsonMediaFiles.get(i1);
                String jsonFilePath = (String) tempJsonMediaFile.get("absolutePath");

                if (isSameFile(jsonFilePath, filePath)) {
                    jsonMediaFile = tempJsonMediaFile;
                    break;
                }
            }

            MediaFile mediaFile;
            if (jsonMediaFile != null) {
                mediaFile = new MediaFile(jsonMediaFile, filePath);
            } else {
                mediaFile = new MediaFile(filePath);
                saveMediaFile(mediaFile);
            }

            mediaFiles.add(mediaFile);

        }

        MediaFile[] fileList = new MediaFile[mediaFiles.size()];
        for (int i = 0; i < mediaFiles.size(); i++) {
            fileList[i] = mediaFiles.get(i);
        }

        loadFolder(fileList);

    }

    void selectFile(MediaFile file) {
        JButton btn = null;
        for (int i = 0; i < buttonList.size(); i++) {
            if (buttonList.get(i).getText().equals(file.getName())) {
                btn = buttonList.get(i);
            }
        }
        if (btn != null) {
            selectFile(file, btn);
        }
    }

    void selectFile(MediaFile file, JButton newBtn) {
        String absolutePath = file.getAbsolutePath();

        // If not selected, select it
        deselect();
        selectedItem = absolutePath;

        // Set style
        newBtn.setFont(newBtn.getFont().deriveFont(Font.BOLD));
        newBtn.setBackground(Color.BLACK);
        newBtn.setForeground(Color.WHITE);

        //Set comments
        mainFrame.infoPanel.setCommentsFieldText(file.getComment());
        mainFrame.infoPanel.setImagePathFieldText(file.getImagePath());

        //Set categories
        StringBuilder catNames = new StringBuilder();
        ArrayList<Category> categories = file.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            if (i > 0) {
                catNames.append(", ");
            }

            catNames.append(categories.get(i).getName());
        }
        mainFrame.infoPanel.setCategoriesLabelText(catNames.toString());

        // Show editing panel
        mainFrame.toolbarPanel.deselectButton.setVisible(true);
        mainFrame.toolbarPanel.openButton.setVisible(true);
        mainFrame.toolbarPanel.deleteButton.setVisible(true);
        mainFrame.toolbarPanel.moveButton.setVisible(true);
        mainFrame.infoPanel.setVisible(true);
    }

    private void buildPanel() {
        mainFrame = new MainFrame("Media Organiser");
        mainFrame.setSize(1000, 800);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(false);
        mainFrame.addToolbarListener((ToolbarEvent event) -> {
            if (null != event.getType()) {
                switch (event.getType()) {
                    case "back":
                        if (historyStack.size() > 0) {
                            String lastDir = historyStack.get(historyStack.size() - 1);
                            historyStack.remove(historyStack.size() - 1);
                            loadFolder(lastDir);
                        } else {
                            loadFolder(this.originDirectory);
                        }
                        break;
                    case "deselect":
                        deselect();
                        break;
                    case "open":
                        openFile(selectedItem);
                        break;
                    case "delete":
                        deleteFile(selectedItem);
                        break;
                    case "move":
                        mainFrame.toolbarPanel.setVisible(false);
                        fileToMove = selectedItem;
                        mainFrame.movePanel.setVisible(true);
                        break;
                    case "moveHere":
                        if (fileToMove != null) {
                            moveFile(fileToMove, currentDirectory);
                        } else {
                            JOptionPane.showMessageDialog(null, "File not found");
                        }
                        break;
                    case "cancelMove":
                        mainFrame.movePanel.setVisible(false);
                        fileToMove = null;
                        mainFrame.toolbarPanel.setVisible(true);
                        break;
                    case "saveComments":
                        saveComments(selectedItem);
                        break;
                    case "saveImagePath":
                        saveImagePath(selectedItem);
                        break;
                    case "addCategory":
                        addCategory(selectedItem);
                        break;
                    case "removeCategory":
                        removeCategory(selectedItem);
                        break;
                    case "createCategory":
                        createCategory();
                        break;
                    case "deleteCategory":
                        deleteCategory();
                        break;
                    case "createPlayList":
                        createPlayList();
                        break;
                    case "deletePlayList":
                        removePlayList();
                        break;
                    case "addToPlayList":
                        addFileToPlayList(selectedItem);
                        break;
                    case "removeFromPlayList":
                        removeFileFromPlayList(selectedItem);
                        break;
                    case "openPlayList":
                        openPlayList();
                        break;
                    case "search":
                        if (this.currentPlayList != null) {
                            openPlayList(this.currentPlayList);
                        } else {
                            loadFolder(this.originDirectory);
                        }
                    default:
                        break;
                }
            }
        });

    }

    /*
        Add category to a media file.
        Brings up selection for which category to add
     */
    void addCategory(String filePath) {
        JSONObject jsonFile = readMediaFile(filePath);
        MediaFile file;
        if (jsonFile == null) {
            file = new MediaFile(filePath);
        } else {
            file = new MediaFile(jsonFile, filePath);
        }

        ArrayList<String> options = new ArrayList<>();

        ArrayList<Category> currentCategories = file.getCategories();
        ArrayList<Category> allCategories = CategoryFactory.getCategoryList();

        if (allCategories.size() > 0) {

            for (int i = 0; i < allCategories.size(); i++) {
                boolean contains = false;
                for (int i1 = 0; i1 < currentCategories.size(); i1++) {
                    if (allCategories.get(i).getName().equals(currentCategories.get(i1).getName())) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    options.add(allCategories.get(i).getName());
                }
            }

            if (currentCategories.size() < allCategories.size()) {

                String input = (String) JOptionPane.showInputDialog(null, "Chose a category to add", "Add Category", JOptionPane.QUESTION_MESSAGE, null, options.toArray(), options.get(0));

                if (input != null) {

                    Category newCategory = CategoryFactory.getCategory(input);

                    currentCategories.add(newCategory);
                    file.setCategories(currentCategories);
                    saveMediaFile(file);

                    loadFolder(this.currentDirectory);
                    selectFile(file);
                }
            } else {
                JOptionPane.showMessageDialog(null, "This media file already has all categories");

            }

        } else {
            JOptionPane.showMessageDialog(null, "No categories found");
        }

    }

    /*
        Deletes category from a media file.
        Brings up selection for which category to remove
     */
    void removeCategory(String filePath) {
        JSONObject jsonFile = readMediaFile(filePath);
        MediaFile file;
        if (jsonFile == null) {
            file = new MediaFile(filePath);
        } else {
            file = new MediaFile(jsonFile, filePath);
        }

        ArrayList<Category> currentCategories = file.getCategories();

        String[] options = new String[currentCategories.size()];
        for (int i = 0; i < currentCategories.size(); i++) {
            options[i] = currentCategories.get(i).getName();
        }

        if (options.length > 0) {

            String input = (String) JOptionPane.showInputDialog(null, "Chose a category to remove", "Remove Category", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            if (input != null) {

                Category newCategory = CategoryFactory.getCategory(input);

                currentCategories.remove(newCategory);

                file.setCategories(currentCategories);
                saveMediaFile(file);

                loadFolder(this.currentDirectory);
                selectFile(file);
            }

        } else {
            JOptionPane.showMessageDialog(null, "No categories assigned");
        }

    }

    void saveComments(String filePath) {
        JSONObject jsonFile = readMediaFile(filePath);
        MediaFile file;
        if (jsonFile == null) {
            file = new MediaFile(filePath);
        } else {
            file = new MediaFile(jsonFile, filePath);
        }

        file.setComment(mainFrame.infoPanel.getCommentsFieldText());
        saveMediaFile(file);
        JOptionPane.showMessageDialog(null, "Comments saved");
        loadFolder(this.currentDirectory);
        selectFile(file);

    }

    void saveImagePath(String filePath) {
        JSONObject jsonFile = readMediaFile(filePath);
        MediaFile file;
        if (jsonFile == null) {
            file = new MediaFile(filePath);
        } else {
            file = new MediaFile(jsonFile, filePath);
        }

        file.setImagePath(mainFrame.infoPanel.getImagePathFieldText());
        saveMediaFile(file);
        JOptionPane.showMessageDialog(null, "Image path saved");
        loadFolder(this.currentDirectory);
        selectFile(file);

    }

    void openFile(String filePath) {
        openFile(new MediaFile(filePath));
    }

    void openFile(MediaFile file) {
        if (file.isFile()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "We were unable to open this file.");
            }
        } else {
            // Add the current location to the history stack and open it
            historyStack.add(currentDirectory);
            loadFolder(file.getAbsolutePath());
        }
    }

    void deleteFile(String filePath) {
        deleteFile(new MediaFile(filePath));
    }

    void deleteFile(MediaFile file) {
        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to permanentely delete the file: " + file.getName());
        if (confirm == 0) {
            if (file.delete()) {
                JOptionPane.showMessageDialog(null, "File deleted.");
                loadFolder(this.currentDirectory);
                deleteSavedMediaFilePath(file);
            } else {
                JOptionPane.showMessageDialog(null, "We were unable to delete this file.");
            }
        }
    }

    void moveFile(String filePath, String newPath) {
        moveFile(new MediaFile(filePath), newPath);
    }

    void moveFile(MediaFile file, String newPath) {
        String newName = newPath + "\\" + file.getName();

        if (file.renameTo(new MediaFile(newName))) {
            //Display success message
            JOptionPane.showMessageDialog(null, "MediaFile moved");

            //Organise panels
            mainFrame.movePanel.setVisible(false);
            fileToMove = null;
            mainFrame.toolbarPanel.setVisible(true);

            //Change save
            changeSavedMediaFilePath(file.getAbsolutePath(), newName);

            //Reload (This will reload the media file from the path also
            loadFolder(this.currentDirectory);
        } else {
            JOptionPane.showMessageDialog(null, "We were unable to move this file.");
        }
    }

    void deselect() {
        this.selectedItem = "";

        JButton button;
        for (int i = 0; i < buttonList.size(); i++) {
            button = buttonList.get(i);
            button.setFont(button.getFont().deriveFont(Font.PLAIN));
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
        }

        // Hide buttons
        mainFrame.toolbarPanel.deselectButton.setVisible(false);
        mainFrame.toolbarPanel.openButton.setVisible(false);
        mainFrame.toolbarPanel.deleteButton.setVisible(false);
        mainFrame.toolbarPanel.moveButton.setVisible(false);
        mainFrame.infoPanel.setVisible(false);
    }

    //READ/SAVE SECTION

    /*
        Reads save file and returns all contents as JSONObject
     */
    JSONObject readSaveFile() {
        try {
            FileReader reader = new FileReader(this.saveFilePath);
            JSONObject saveFile = (JSONObject) (new JSONParser()).parse(reader);
            return saveFile;
        } catch (IOException | ParseException e) {
        }
        return new JSONObject();
    }

    /*
        Reads save file and returns media files array as JSONArray
     */
    JSONArray readMediaFiles() {
        JSONObject saveFile = readSaveFile();
        JSONArray mediaFiles = (JSONArray) saveFile.get("mediaFiles");

        if (mediaFiles == null) {
            mediaFiles = new JSONArray();
        }

        return mediaFiles;
    }

    JSONObject readMediaFile(String filePath) {
        JSONArray mediaFiles = readMediaFiles();

        if (mediaFiles != null) {
            for (int i = 0; i < mediaFiles.size(); i++) {
                JSONObject mediaFile = (JSONObject) mediaFiles.get(i);

                if (isSameFile((String) mediaFile.get("absolutePath"), filePath)) {
                    return mediaFile;
                }
            }
        }
        return null;
    }

    /*
        Overwrites file mediaFiles with the passed JSONArray
     */
    void saveMediaFiles(JSONArray mediaFiles) {
        JSONObject saveFile = readSaveFile();
        saveFile.put("mediaFiles", mediaFiles);
        try (FileWriter file = new FileWriter(this.saveFilePath)) {

            file.write(saveFile.toJSONString());
            file.flush();

        } catch (IOException ex) {
            Logger.getLogger(FileExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
        Saves new media file or overwrites existing if one already exists with the same absolute path
     */
    void saveMediaFile(MediaFile file) {

        //Check if there is one currently for this file
        JSONArray mediaFiles = readMediaFiles();
        int fileIndex = -1;

        if (mediaFiles != null) {
            for (int i = 0; i < mediaFiles.size(); i++) {
                JSONObject mediaFile = (JSONObject) mediaFiles.get(i);
                if (isSameFile((String) mediaFile.get("absolutePath"), file.getAbsolutePath())) {
                    fileIndex = i;
                    break;
                }
            }
        }

        //Create new JSON object
        JSONObject newFile = new JSONObject();

        //Assign basics
        newFile.put("fileName", file.getName());
        newFile.put("absolutePath", file.getAbsolutePath());
        newFile.put("comment", file.getComment());
        newFile.put("imagePath", file.getImagePath());

        //Save categories
        //New json array
        JSONArray jsonCategories = new JSONArray();
        ArrayList<Category> categories = file.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            jsonCategories.add(categories.get(i).getName());
        }

        newFile.put("categories", jsonCategories);

        //File generated, now to add it to the array
        //If it has an old one, overwrite it. Otherwise append to end
        if (fileIndex == -1) {
            mediaFiles.add(newFile);
        } else {
            mediaFiles.set(fileIndex, newFile);
        }

        //Now overwrite the file with the new data
        saveMediaFiles(mediaFiles);

    }

    void changeSavedMediaFilePath(String oldPath, String newPath) {
        JSONArray mediaFiles = readMediaFiles();
        if (mediaFiles != null) {
            for (int i = 0; i < mediaFiles.size(); i++) {
                JSONObject mediaFile = (JSONObject) mediaFiles.get(i);
                if (isSameFile((String) mediaFile.get("absolutePath"), oldPath)) {
                    //Change save file
                    mediaFile.put("absolutePath", newPath);
                    mediaFiles.set(i, mediaFile);
                    saveMediaFiles(mediaFiles);

                    //Change entries in playlists
                    JSONArray playListList = readPlayLists();
                    for (int i1 = 0; i1 < playListList.size(); i1++) {

                        JSONObject playList = (JSONObject) playListList.get(i1);
                        JSONArray filePaths = (JSONArray) playList.get("filePaths");

                        for (int i2 = 0; i2 < filePaths.size(); i2++) {
                            String filePath = (String) filePaths.get(i2);

                            if (isSameFile(filePath, oldPath)) {
                                filePaths.set(i2, newPath);
                                break;
                            }

                        }

                    }

                    break;
                }
            }
        }
    }

    void deleteSavedMediaFilePath(MediaFile file) {
        deleteSavedMediaFilePath(file.getAbsolutePath());
    }

    void deleteSavedMediaFilePath(String filePath) {
        JSONArray mediaFiles = readMediaFiles();
        if (mediaFiles != null) {
            for (int i = 0; i < mediaFiles.size(); i++) {
                JSONObject mediaFile = (JSONObject) mediaFiles.get(i);
                if (isSameFile((String) mediaFile.get("absolutePath"), filePath)) {
                    mediaFiles.remove(i);
                    saveMediaFiles(mediaFiles);
                    break;
                }
            }
        }
    }

    /*
        Overwrites file categories with the passed JSONArray
     */
    void saveCategories(JSONArray categoryList) {
        JSONObject saveFile = readSaveFile();
        saveFile.put("categories", categoryList);
        try (FileWriter file = new FileWriter(this.saveFilePath)) {

            file.write(saveFile.toJSONString());
            file.flush();

        } catch (IOException ex) {
            Logger.getLogger(FileExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    JSONArray readCategories() {
        JSONObject saveFile = readSaveFile();
        JSONArray categoryList = (JSONArray) saveFile.get("categories");

        if (categoryList == null) {
            categoryList = new JSONArray();
        }

        return categoryList;
    }

    void loadCategories() {
        JSONArray categoryList = readCategories();
        StringBuilder catNames = new StringBuilder("");
        for (int i = 0; i < categoryList.size(); i++) {
            if (i > 0) {
                catNames.append(", ");
            }
            catNames.append((String) categoryList.get(i));

            if (CategoryFactory.getCategory((String) categoryList.get(i)) == null) {
                CategoryFactory.makeCategory((String) categoryList.get(i));
            }
        }
        mainFrame.generalPanel.setCategoriesLabelText(catNames.toString());
    }

    /*
        Creates new category
        Brings up selection for which category to add
     */
    void createCategory() {

        ArrayList<Category> categoryList = CategoryFactory.getCategoryList();
        String input = (String) JOptionPane.showInputDialog(null, "Enter a category name");
        if (input != null && !input.equals("")) {
            boolean exists = false;
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getName().equalsIgnoreCase(input)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                JSONArray jsonCategoryList = readCategories();
                jsonCategoryList.add(input);
                saveCategories(jsonCategoryList);
                loadCategories();
                JOptionPane.showMessageDialog(null, "Category created");
            } else {
                JOptionPane.showMessageDialog(null, "This category already exists");
            }
        }

    }

    void deleteCategory() {
        ArrayList<Category> categoryList = CategoryFactory.getCategoryList();

        String[] options = new String[categoryList.size()];
        for (int i = 0; i < categoryList.size(); i++) {
            options[i] = categoryList.get(i).getName();
        }

        if (options.length > 0) {

            //Get category needed to delete
            String input = (String) JOptionPane.showInputDialog(null, "Chose a category to delete", "Delete Category", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            if (input != null) {
                //Delete category from array
                Category category = CategoryFactory.getCategory(input);
                categoryList.remove(category);

                //Overwrite file with new array
                JSONArray jsonCategoryList = new JSONArray();
                for (int i = 0; i < categoryList.size(); i++) {
                    jsonCategoryList.add(categoryList.get(i).getName());
                }
                saveCategories(jsonCategoryList);

                //Delete category from all files
                JSONArray mediaFiles = readMediaFiles();
                boolean fileChanged = false;
                for (int i = 0; i < mediaFiles.size(); i++) {
                    JSONObject mediaFile = (JSONObject) mediaFiles.get(i);
                    JSONArray fileCategories = (JSONArray) mediaFile.get("categories");

                    for (int i1 = 0; i1 < fileCategories.size(); i1++) {
                        if (((String) fileCategories.get(i1)).equals(category.getName())) {
                            fileChanged = true;
                            fileCategories.remove(i1);
                            mediaFile.put("categories", fileCategories);
                        }
                    }
                }
                if (fileChanged) {
                    saveMediaFiles(mediaFiles);
                    loadFolder(this.currentDirectory);
                }

                //Reload categories
                loadCategories();
                JOptionPane.showMessageDialog(null, "Category deleted");

            }

        } else {
            JOptionPane.showMessageDialog(null, "There are no categories to delete");
        }

    }

    /*
        Creates new category
        Brings up selection for which category to add
     */
    JSONArray readPlayLists() {
        JSONObject saveFile = readSaveFile();
        JSONArray playListList = (JSONArray) saveFile.get("playlists");

        if (playListList == null) {
            playListList = new JSONArray();
        }

        return playListList;
    }

    void savePlayLists(JSONArray playListList) {
        JSONObject saveFile = readSaveFile();
        saveFile.put("playlists", playListList);
        try (FileWriter file = new FileWriter(this.saveFilePath)) {

            file.write(saveFile.toJSONString());
            file.flush();

        } catch (IOException ex) {
            Logger.getLogger(FileExplorer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void loadPlayLists() {
        JSONArray playListList = readPlayLists();
        StringBuilder plNames = new StringBuilder("");
        for (int i = 0; i < playListList.size(); i++) {
            if (i > 0) {
                plNames.append(", ");
            }
            JSONObject jsonPlayList = (JSONObject) playListList.get(i);
            String playListName = (String) (jsonPlayList.get("name"));

            plNames.append(playListName);

            //Remove it if it is already there
            if (PlayListFactory.getPlayList(playListName) != null) {
                PlayListFactory.removePlayList(playListName);
            }

            //Create / recreate
            PlayList newPlayList = PlayListFactory.makePlayList(playListName);
            JSONArray playListFiles = (JSONArray) jsonPlayList.get("filePaths");

            for (int i1 = 0; i1 < playListFiles.size(); i1++) {
                newPlayList.addFilePath((String) playListFiles.get(i1));
            }

        }
        mainFrame.generalPanel.setPlayListLabelText(plNames.toString());
    }

    void createPlayList() {

        ArrayList<PlayList> playListList = PlayListFactory.getPlayListList();
        String input = (String) JOptionPane.showInputDialog(null, "Enter a new playlist name");
        if (input != null && !input.equals("")) {
            boolean exists = false;
            for (int i = 0; i < playListList.size(); i++) {
                if (playListList.get(i).getName().equalsIgnoreCase(input)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                JSONArray jsonPlayListList = readPlayLists();

                JSONObject newPlayList = new JSONObject();
                newPlayList.put("name", input);
                newPlayList.put("filePaths", new JSONArray());

                jsonPlayListList.add(newPlayList);
                savePlayLists(jsonPlayListList);
                loadPlayLists();
                JOptionPane.showMessageDialog(null, "Playlist created");
            } else {
                JOptionPane.showMessageDialog(null, "This Playlist already exists");
            }
        }
    }

    String selectPlayList() {
        ArrayList<PlayList> playListList = PlayListFactory.getPlayListList();

        String[] options = new String[playListList.size()];
        for (int i = 0; i < playListList.size(); i++) {
            options[i] = playListList.get(i).getName();
        }

        if (options.length > 0) {
            String input = (String) JOptionPane.showInputDialog(null, "Chose a playlist", "Select Category", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (input != null) {
                return input;
            }
        } else {
            JOptionPane.showMessageDialog(null, "There are no playlists to select");
        }
        return null;
    }

    void removePlayList() {
        JSONArray jsonPlayListList = readPlayLists();

        String input = selectPlayList();
        if (input != null) {
            for (int i = 0; i < jsonPlayListList.size(); i++) {
                JSONObject playList = (JSONObject) jsonPlayListList.get(i);
                if (((String) playList.get("name")).equals(input)) {
                    jsonPlayListList.remove(i);
                    savePlayLists(jsonPlayListList);
                    loadPlayLists();
                    JOptionPane.showMessageDialog(null, "Playlist removed");
                    break;
                }
            }
        }
    }

    void addFileToPlayList(String filePath) {
        JSONArray jsonPlayListList = readPlayLists();

        if (filePath != null && !filePath.equals("")) {

            MediaFile file = new MediaFile(filePath);
            if (file.isFile()) {

                String input = selectPlayList();
                if (input != null) {
                    for (int i = 0; i < jsonPlayListList.size(); i++) {
                        JSONObject playList = (JSONObject) jsonPlayListList.get(i);
                        if (((String) playList.get("name")).equals(input)) {

                            JSONArray playListFiles = (JSONArray) playList.get("filePaths");

                            boolean contains = false;
                            for (int i1 = 0; i1 < playListFiles.size(); i1++) {
                                if (isSameFile((String) playListFiles.get(i1), filePath)) {
                                    contains = true;
                                    break;
                                }
                            }

                            if (!contains) {

                                playListFiles.add(filePath);
                                savePlayLists(jsonPlayListList);
                                loadPlayLists();
                                JOptionPane.showMessageDialog(null, "File added to Playlist");

                            } else {
                                JOptionPane.showMessageDialog(null, "Selected file is already in this playlist");
                            }

                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Selected file cannot be added to a playlist");
            }

        } else {
            JOptionPane.showMessageDialog(null, "No file selected");
        }
    }

    void removeFileFromPlayList(String filePath) {
        JSONArray jsonPlayListList = readPlayLists();

        if (filePath != null && !filePath.equals("")) {

            MediaFile file = new MediaFile(filePath);
            if (file.isFile()) {

                String input = selectPlayList();
                if (input != null) {
                    for (int i = 0; i < jsonPlayListList.size(); i++) {
                        JSONObject playList = (JSONObject) jsonPlayListList.get(i);
                        if (((String) playList.get("name")).equals(input)) {

                            JSONArray playListFiles = (JSONArray) playList.get("filePaths");

                            for (int i1 = 0; i1 < playListFiles.size(); i1++) {
                                if (isSameFile((String) playListFiles.get(i1), filePath)) {
                                    playListFiles.remove(i1);
                                    playList.put("filePaths", playListFiles);
                                    jsonPlayListList.set(i, playList);
                                    savePlayLists(jsonPlayListList);
                                    JOptionPane.showMessageDialog(null, "File removed from playlist");
                                    loadPlayLists();
                                    break;
                                }
                            }

                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Selected file cannot be part of playlist");
            }

        } else {
            JOptionPane.showMessageDialog(null, "No file selected");
        }
    }

    /*
        Compares two paths and returns true if they point towards the same file
     */
    public static boolean isSameFile(String path1, String path2) {

        final Path pathA = Paths.get(path1);
        final Path pathB = Paths.get(path2);

        try {
            return Files.isSameFile(pathA, pathB);
        } catch (IOException ex) {
            return false;
        }
    }

    /*
        Returns the file type ( eg "mp3", "txt") from a file name or path
     */
    public static String getFileType(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > -1) {
            return fileName.substring(dotIndex);
        }
        return "";
    }
}

package fileExplorer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

class ToolbarPanelGeneral extends ToolbarPanel {

    private static final long serialVersionUID = -947627618410029133L;

    private final JLabel categoryListLabel;
    private final JLabel playListListLabel;
    private final JTextField typeSearch;

    public ToolbarPanelGeneral() {
        Dimension size = getPreferredSize();
        size.width = 200;
        setPreferredSize(size);

        GridBagLayout gl = new GridBagLayout();
        setLayout(gl);

        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.NORTH;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(2, 2, 2, 2);

        //Type search
        JLabel typeLabel = new JLabel("Type Filter:");
        typeSearch = new JTextField();
        JButton typeButton = new JButton("Refresh");

        typeButton.addActionListener(new ActionListener() {
            private final String type = "search";

            @Override
            public void actionPerformed(ActionEvent e) {
                fireToolbarEvent(new ToolbarEvent(this, type));
            }
        });

        //Categories
        JLabel categoriesLabel = new JLabel("Categories");
        categoryListLabel = new JLabel("");
        JButton addCategoryButton = new JButton("Add");
        JButton removeCategoryButton = new JButton("Remove");

        addCategoryButton.addActionListener(new ActionListener() {
            private final String type = "createCategory";

            @Override
            public void actionPerformed(ActionEvent e) {
                fireToolbarEvent(new ToolbarEvent(this, type));
            }
        });

        removeCategoryButton.addActionListener(new ActionListener() {
            private final String type = "deleteCategory";

            @Override
            public void actionPerformed(ActionEvent e) {
                fireToolbarEvent(new ToolbarEvent(this, type));
            }
        });

        //Playlists
        JLabel playListLabel = new JLabel("Playlists");
        playListListLabel = new JLabel("");
        JButton addPlayListButton = new JButton("Add");
        JButton removePlayListButton = new JButton("Remove");
        JButton addToPlayListButton = new JButton("Add File to Playlist");
        JButton removeFromPlayListButton = new JButton("Remove File from Playlist");
        JButton openPlayListButton = new JButton("Open");

        addPlayListButton.addActionListener(new ActionListener() {
            private final String type = "createPlayList";

            @Override
            public void actionPerformed(ActionEvent e) {
                fireToolbarEvent(new ToolbarEvent(this, type));
            }
        });

        removePlayListButton.addActionListener(new ActionListener() {
            private final String type = "deletePlayList";

            @Override
            public void actionPerformed(ActionEvent e) {
                fireToolbarEvent(new ToolbarEvent(this, type));
            }
        });

        addToPlayListButton.addActionListener(new ActionListener() {
            private final String type = "addToPlayList";

            @Override
            public void actionPerformed(ActionEvent e) {
                fireToolbarEvent(new ToolbarEvent(this, type));
            }
        });

        removeFromPlayListButton.addActionListener(new ActionListener() {
            private final String type = "removeFromPlayList";

            @Override
            public void actionPerformed(ActionEvent e) {
                fireToolbarEvent(new ToolbarEvent(this, type));
            }
        });

        openPlayListButton.addActionListener(new ActionListener() {
            private final String type = "openPlayList";

            @Override
            public void actionPerformed(ActionEvent e) {
                fireToolbarEvent(new ToolbarEvent(this, type));
            }
        });

        gc.gridx = 0;
        gc.gridy = 0;
        add(typeLabel, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        add(typeSearch, gc);

        gc.gridx = 0;
        gc.gridy = 2;
        add(typeButton, gc);

        gc.gridx = 0;
        gc.gridy = 3;
        add(categoriesLabel, gc);

        gc.gridx = 0;
        gc.gridy = 4;
        add(categoryListLabel, gc);

        gc.gridx = 0;
        gc.gridy = 5;
        add(addCategoryButton, gc);

        gc.gridx = 0;
        gc.gridy = 6;
        add(removeCategoryButton, gc);

        gc.gridx = 0;
        gc.gridy = 7;
        add(playListLabel, gc);

        gc.gridx = 0;
        gc.gridy = 8;
        add(playListListLabel, gc);

        gc.gridx = 0;
        gc.gridy = 9;
        add(addPlayListButton, gc);

        gc.gridx = 0;
        gc.gridy = 10;
        add(removePlayListButton, gc);

        gc.gridx = 0;
        gc.gridy = 11;
        add(addToPlayListButton, gc);

        gc.gridx = 0;
        gc.gridy = 12;
        add(removeFromPlayListButton, gc);

        gc.gridx = 0;
        gc.gridy = 13;
        add(openPlayListButton, gc);
    }

    public void setCategoriesLabelText(String categories) {
        categoryListLabel.setText(categories);
    }

    public void setPlayListLabelText(String categories) {
        playListListLabel.setText(categories);
    }

    public String getTypeSearch() {
        return typeSearch.getText();
    }
}

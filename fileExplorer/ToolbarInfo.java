package fileExplorer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

class ToolbarPanelInfo extends ToolbarPanel {

    private static final long serialVersionUID = -947627618410029133L;

    private final JTextArea commentsField;
    private final JTextField imagePathField;
    private final JLabel categoryListLabel;

    public ToolbarPanelInfo() {
        Dimension size = getPreferredSize();
        size.width = 200;
        setPreferredSize(size);

        GridBagLayout gl = new GridBagLayout();
        setLayout(gl);

        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.NORTH;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(2, 2, 2, 2);

        //Comments
        JLabel commentLabel = new JLabel("Comments");
        commentsField = new JTextArea();
        JButton commentsButton = new JButton("Save Comments");

        commentsButton.addActionListener(new ActionListener() {
            private final String type = "saveComments";

            @Override
            public void actionPerformed(ActionEvent e) {
                fireToolbarEvent(new ToolbarEvent(this, type));
            }
        });

        //Image path
        JLabel imagePathLabel = new JLabel("Image Path");
        imagePathField = new JTextField();
        JButton imagePathButton = new JButton("Save Image Path");

        imagePathButton.addActionListener(new ActionListener() {
            private final String type = "saveImagePath";

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
            private final String type = "addCategory";

            @Override
            public void actionPerformed(ActionEvent e) {
                fireToolbarEvent(new ToolbarEvent(this, type));
            }
        });

        removeCategoryButton.addActionListener(new ActionListener() {
            private final String type = "removeCategory";

            @Override
            public void actionPerformed(ActionEvent e) {
                fireToolbarEvent(new ToolbarEvent(this, type));
            }
        });

        gc.gridx = 0;
        gc.gridy = 0;
        add(commentLabel, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        add(commentsField, gc);

        gc.gridx = 0;
        gc.gridy = 2;
        add(commentsButton, gc);

        gc.gridx = 0;
        gc.gridy = 3;
        add(imagePathLabel, gc);

        gc.gridx = 0;
        gc.gridy = 4;
        add(imagePathField, gc);

        gc.gridx = 0;
        gc.gridy = 5;
        add(imagePathButton, gc);

        gc.gridx = 0;
        gc.gridy = 6;
        add(categoriesLabel, gc);

        gc.gridx = 0;
        gc.gridy = 7;
        add(categoryListLabel, gc);

        gc.gridx = 0;
        gc.gridy = 8;
        add(addCategoryButton, gc);

        gc.gridx = 0;
        gc.gridy = 9;
        add(removeCategoryButton, gc);

    }

    public String getCommentsFieldText() {
        return commentsField.getText();
    }

    public void setCommentsFieldText(String comments) {
        commentsField.setText(comments);
    }

    public String getImagePathFieldText() {
        return imagePathField.getText();
    }

    public void setImagePathFieldText(String comments) {
        imagePathField.setText(comments);
    }

    public void setCategoriesLabelText(String categories) {
        categoryListLabel.setText(categories);
    }

}

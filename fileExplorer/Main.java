package fileExplorer;

import javax.swing.JOptionPane;

class Main {

    public static void main(String[] args) {
        openNewExplorer();
    }

    static void openNewExplorer() {

        String input = (String) JOptionPane.showInputDialog(null, "Enter a directory path");

        if (input != null) {
            new FileExplorer(input);
        }

    }

}

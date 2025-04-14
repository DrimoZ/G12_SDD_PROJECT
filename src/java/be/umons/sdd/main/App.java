package be.umons.sdd.main;

import be.umons.sdd.panels.MainFrame;
import javax.swing.*;

public class App {

    /**
     * The main entry point of the application.
     *
     * @param args Command-line arguments passed to the program.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::getInstance);
    }
}

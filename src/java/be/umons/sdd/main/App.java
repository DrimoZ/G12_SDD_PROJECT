package be.umons.sdd.main;

import javax.swing.*;

import be.umons.sdd.main.panels.MainApp;

public class App {

    /**
     * The main entry point of the application.
     *
     * @param args Command-line arguments passed to the program.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp());
    }
}

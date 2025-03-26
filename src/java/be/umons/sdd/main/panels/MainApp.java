package be.umons.sdd.main.panels;

import be.umons.sdd.builders.AbstractBSPTreeBuilder;
import be.umons.sdd.models.BSPNode;
import be.umons.sdd.models.Scene2D;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

public class MainApp extends JFrame {

    private FormPanel formPanel;
    private SceneVisulatizerPanel sceneVisulatizerPanel;
    private SceneVisulatizerPanel painterVisualizerPanel;

    private Scene2D scene;
    private BSPNode bspTree;
    private AbstractBSPTreeBuilder bspTreeBuilder;

    public MainApp() {
        createAndShowGUI();
    }

    /**
     * Initializes and displays the main application window.
     */
    private void createAndShowGUI() {
        // Set the look and feel to the system default.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException e) {
            // fallback to default look and feel
        }

        // Window Options
        setTitle("BSP Tree Builder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Visualizer panel
        sceneVisulatizerPanel = new SceneVisulatizerPanel(null);
        painterVisualizerPanel = new SceneVisulatizerPanel(null);

        // Form panel
        formPanel = new FormPanel();

        // Read Listeners
        this.setupListeners();

        // Split the window into two parts: left form and right visualization.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, sceneVisulatizerPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.0);

        // Add the split pane to the main frame.
        add(splitPane, BorderLayout.CENTER);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);

    }

    /**
     * Sets up listeners for the form panel to handle scene and tree builder loading events.
     * <p>
     * This method initializes two listeners:
     * - Scene Loaded Listener: Updates the main application scene and visualizer panel when a new scene is loaded.</li>
     * - Tree Builder Loaded Listener: Updates the BSP tree builder and painter visualizer panel when a new tree builder is loaded.</li>
     */
    private void setupListeners() {
        formPanel.setSceneLoadedListener(
            (Scene2D loadedScene) -> {
                MainApp.this.scene = loadedScene;
                sceneVisulatizerPanel.setScene(loadedScene);
                sceneVisulatizerPanel.repaint();
            }
        );

        formPanel.setTreeBuilderLoadedListener(
            (AbstractBSPTreeBuilder loadedBuilder) -> {
                MainApp.this.bspTreeBuilder = loadedBuilder;
                painterVisualizerPanel.setScene(scene);
                painterVisualizerPanel.repaint();
            }
        );
    }
}

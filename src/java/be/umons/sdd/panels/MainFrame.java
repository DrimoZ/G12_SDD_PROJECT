package be.umons.sdd.panels;

import be.umons.sdd.builders.BSPTreeBuilder;
import be.umons.sdd.interfaces.SceneObserver;
import be.umons.sdd.interfaces.TreeBuilderObserver;
import be.umons.sdd.models.BSPNode;
import be.umons.sdd.models.Scene2D;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

public class MainFrame extends JFrame implements SceneObserver, TreeBuilderObserver {

    private static MainFrame instance;

    private Scene2D currentScene = null;
    private BSPTreeBuilder currentTreeBuilder = null;
    private BSPNode currentBspTree = null;

    private JSplitPane mainSplitPane;
    private JPanel formPanel;
    private JPanel visualizationPanel;

    private JButton sceneVisualizerButton, painterVisualizerButton;
    private SceneVisualizerPanel sceneVisualizerPanel;
    private PaintersVisualizerPanel painterVisualizerPanel;

    // A container that uses CardLayout to switch between panels
    private JPanel visualizerCardPanel;
    private CardLayout cardLayout;

    public static MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
        }
        return instance;
    }

    private  MainFrame() {
        setStyle();
        initUI();
        initObservers();
    }

    /**
     * Sets the look and feel of the application to the default of the running
     * system. If the system look and feel is not available, the default look and
     * feel is used.
     */
    private void setStyle() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException e) {
            // fallback to default look and feel
        }
    }

    private void initUI() {
        setTitle("BSP Tree Builder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920,960);
        setLocationRelativeTo(null);
        setVisible(true);

        // Frame must be divided into 2 big parts : left form and right visualization.
        // Form part is 300 pixels wide and the visualization part takes the remaining space.

        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(300);
        mainSplitPane.setResizeWeight(0.0);

        mainSplitPane.setLeftComponent(initFormPanel());
        mainSplitPane.setRightComponent(initVisualizationPanel());


        add(mainSplitPane, BorderLayout.CENTER);
    }

    private void initObservers() {
        onSceneSelected(SceneSelectorPanel.getInstance().addSceneObserver(this));
        TreeBuilderSelectorPanel.getInstance().addTreeBuilderObserver(this);
    }

    private JPanel initFormPanel() {
        formPanel = new JPanel();

        formPanel.setPreferredSize(new Dimension(300, 800));

        formPanel.add(SceneSelectorPanel.getInstance());
        formPanel.add(TreeBuilderSelectorPanel.getInstance());
        formPanel.add(ObserverSelectorPanel.getInstance());
        formPanel.add(DetailsPanel.getInstance());

        return formPanel;
    }

    private JPanel initVisualizationPanel() {
        visualizationPanel = new JPanel(new BorderLayout());
        visualizationPanel.setPreferredSize(new Dimension(900, 800));

        // Top menu panel for visualizer selection.
        JPanel menuPanel = initVisualizerMenuPanel();
        visualizationPanel.add(menuPanel, BorderLayout.NORTH);

        // CardLayout container to switch between visualizers.
        visualizerCardPanel = new JPanel();
        cardLayout = new CardLayout();
        visualizerCardPanel.setLayout(cardLayout);
        visualizerCardPanel.setPreferredSize(new Dimension(900, 740));

        // Get the visualizer panels.
        sceneVisualizerPanel = SceneVisualizerPanel.getInstance();
        painterVisualizerPanel = PaintersVisualizerPanel.getInstance();

        // Add panels to the card layout.
        visualizerCardPanel.add(sceneVisualizerPanel, "Scene");
        visualizerCardPanel.add(painterVisualizerPanel, "Painter");

        visualizationPanel.add(visualizerCardPanel, BorderLayout.CENTER);
        // Initially show the scene visualizer.
        cardLayout.show(visualizerCardPanel, "Scene");

        return visualizationPanel;
    }

    private JPanel initVisualizerMenuPanel() {
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        menuPanel.setPreferredSize(new Dimension(900, 50));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        sceneVisualizerButton = new JButton("Scene Visualizer");
        sceneVisualizerButton.setPreferredSize(new Dimension(430, 30));
        painterVisualizerButton = new JButton("Painter's Algorithm Visualizer");
        painterVisualizerButton.setPreferredSize(new Dimension(430, 30));

        menuPanel.add(sceneVisualizerButton);
        menuPanel.add(painterVisualizerButton);

        // Initially disable the button for the visible panel.
        sceneVisualizerButton.setEnabled(false);

        sceneVisualizerButton.addActionListener(e -> {
            sceneVisualizerButton.setEnabled(false);
            painterVisualizerButton.setEnabled(true);
            cardLayout.show(visualizerCardPanel, "Scene");
        });

        painterVisualizerButton.addActionListener(e -> {
            painterVisualizerButton.setEnabled(false);
            sceneVisualizerButton.setEnabled(true);
            cardLayout.show(visualizerCardPanel, "Painter");
        });

        return menuPanel;
    }

    private void updateBspTree() {
        if (currentScene != null && currentTreeBuilder != null) {
            currentBspTree = currentTreeBuilder.buildTree(currentScene.getSegments(), null);
        }
        sceneVisualizerPanel.setScene(currentScene);
        sceneVisualizerPanel.onBSPUpdated(currentBspTree);
        painterVisualizerPanel.onBSPUpdated(currentBspTree);
    }

    @Override
    public void onSceneSelected(Scene2D scene) {
        currentScene = scene;
        updateBspTree();
    }

    @Override
    public void onTreeBuilderSelected(BSPTreeBuilder treeBuilder) {
        currentTreeBuilder = treeBuilder;
        updateBspTree();
    }

    
}

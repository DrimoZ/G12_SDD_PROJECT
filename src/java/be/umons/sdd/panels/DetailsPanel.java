package be.umons.sdd.panels;

import be.umons.sdd.builders.BSPTreeBuilder;
import be.umons.sdd.builders.DeterministicBSPTreeBuilder;
import be.umons.sdd.builders.RandomBSPTreeBuilder;
import be.umons.sdd.builders.TellerBSPTreeBuilder;
import be.umons.sdd.interfaces.ObserverObserver;
import be.umons.sdd.interfaces.SceneObserver;
import be.umons.sdd.interfaces.TreeBuilderObserver;
import be.umons.sdd.models.Point2D;
import be.umons.sdd.models.Scene2D;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DetailsPanel extends JPanel implements SceneObserver, TreeBuilderObserver, ObserverObserver {

    private static DetailsPanel instance;

    private Scene2D currentScene;
    private BSPTreeBuilder currentBuilder;
    private Point2D observerPosition;
    private double observerStartAngle;
    private double observerEndAngle;

    private JPanel selectedScenePanel;
    private JPanel selectedBuilderPanel;
    private JPanel selectedObserverPanel;

    private JLabel sceneTitleLabel, sceneNameLabel, sceneSizeLabel, sceneSegmentCountLabel, noSceneLabel;
    private JLabel builderTitleLabel, builderNameLabel, builderTauValueLabel, noBuilderLabel;
    private JLabel observerTitleLabel, observerPositionLabel, observerAngleRangeLabel, noObserverLabel;

    /**
     * Returns the single instance of the DetailsPanel class.
     * The panel is created on first call to this method.
     * @return the single instance of DetailsPanel
     */
    public static synchronized DetailsPanel getInstance() {
        if (instance == null) {
            instance = new DetailsPanel();
        }
        return instance;
    }

    private DetailsPanel() {
        initUI();
        initObservers();
    }

    /**
     * Initializes the user interface for the DetailsPanel.
     * 
     * <p> This method sets up the main panel with a vertical BoxLayout and a titled border
     * to display the current selection of scene and builder. It also initializes and adds
     * two sub-panels: one for displaying information about the selected scene and another
     * for the selected tree builder.
     */
    private void initUI() {
        // Main panel settings
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Current Selection"));

        // Only set width, height should be determined by content
        setPreferredSize(new Dimension(300, 250));

        createSelectedScenePanel();
        createSelectedBuilderPanel();
        createSelectedObserverPanel();

        add(selectedScenePanel);
        add(selectedBuilderPanel);
        add(selectedObserverPanel);
    }

    /**
     * Initializes the observers for scene and tree builder selections.
     * 
     * <p> This method registers the DetailsPanel as an observer for both the
     * SceneSelectorPanel and TreeBuilderSelectorPanel. It immediately updates
     * the panel with the current scene and tree builder selections by invoking
     * the respective onSceneSelected and onTreeBuilderSelected methods.
     */
    private void initObservers() {
        onSceneSelected(SceneSelectorPanel.getInstance().addSceneObserver(this));
        onTreeBuilderSelected(TreeBuilderSelectorPanel.getInstance().addTreeBuilderObserver(this));
        ObserverSelectorPanel.getInstance().addObserver(this);
    }

    /**
     * Creates the panel that displays information about the currently selected scene.
     * <p>
     * This method initializes the panel with a title label and creates labels for the
     * scene name, size and segment count (if applicable for the selected scene). It also
     * adds a label that appears when no scene is selected.
     */
    private void createSelectedScenePanel() {
        selectedScenePanel = new JPanel();
        selectedScenePanel.setLayout(new BoxLayout(selectedScenePanel, BoxLayout.Y_AXIS));
        selectedScenePanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 0, 0));
        
        sceneTitleLabel = new JLabel("Scene Information :");
        sceneTitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        selectedScenePanel.add(sceneTitleLabel);

        // Initialize labels
        sceneNameLabel = new JLabel();
        sceneSizeLabel = new JLabel();
        sceneSegmentCountLabel = new JLabel();
        noSceneLabel = new JLabel(" - No scene selected.");
    }

    /**
     * Creates the panel that displays information about the currently selected tree builder.
     * <p>
     * This method initializes the panel with a title label and creates labels for the
     * builder name and tau value (if applicable for the selected builder). It also adds
     * a label that appears when no builder is selected.
     */
    private void createSelectedBuilderPanel() {
        selectedBuilderPanel = new JPanel();
        selectedBuilderPanel.setLayout(new BoxLayout(selectedBuilderPanel, BoxLayout.Y_AXIS));
        selectedBuilderPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 0, 0));
        
        builderTitleLabel = new JLabel("Builder Information :");
        builderTitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        selectedBuilderPanel.add(builderTitleLabel);

        // Initialize labels
        builderNameLabel = new JLabel();
        builderTauValueLabel = new JLabel();
        noBuilderLabel = new JLabel(" - No builder selected.");
    }
    
    /**
     * Creates the panel that displays information about the currently selected observer.
     * <p>
     * This method initializes the panel with a title label and creates labels for the
     * observer's position and angle range (if applicable for the selected observer). It also
     * adds a label that appears when no observer is selected.
     */
    private void createSelectedObserverPanel() {
        selectedObserverPanel = new JPanel();
        selectedObserverPanel.setLayout(new BoxLayout(selectedObserverPanel, BoxLayout.Y_AXIS));
        selectedObserverPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 0, 0));

        observerTitleLabel = new JLabel("Observer Information:");
        observerTitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        selectedObserverPanel.add(observerTitleLabel);

        // Initialize observer labels.
        observerPositionLabel = new JLabel();
        observerAngleRangeLabel = new JLabel();
        noObserverLabel = new JLabel(" - No observer selected.");
    }

    // Observers

    /**
     * Updates the current selected scene to the given scene.
     * 
     * @param scene the scene to be set as the current selected scene
     */
    @Override
    public void onSceneSelected(Scene2D scene) {
        currentScene = scene;
        
        // Update labels accordingly
        if (currentScene != null) {
            sceneNameLabel.setText(" - Name: " + currentScene.getName());
            sceneSizeLabel.setText(" - Size: " + (currentScene.getExtentX() * 2) + "x" + (currentScene.getExtentY() * 2));
            sceneSegmentCountLabel.setText(" - Segment Count: " + currentScene.getSegments().size());
            
            // Remove all labels and add the new ones
            selectedScenePanel.removeAll();
            selectedScenePanel.add(sceneTitleLabel);
            selectedScenePanel.add(sceneNameLabel);
            selectedScenePanel.add(sceneSizeLabel);
            selectedScenePanel.add(sceneSegmentCountLabel);
        } else {
            selectedScenePanel.removeAll();
            selectedScenePanel.add(sceneTitleLabel);
            selectedScenePanel.add(noSceneLabel);
        }

        // Refresh the UI
        selectedScenePanel.revalidate();
        selectedScenePanel.repaint();
    }

    /**
     * Updates the current selected builder to the given builder.
     * 
     * @param builder the builder to be set as the current selected builder
     */
    @Override
    public void onTreeBuilderSelected(BSPTreeBuilder builder) {
        currentBuilder = builder;

        // Update labels accordingly
        if (currentBuilder != null) {
            // Based on instance type : Teller or Random or Deterministic
            if (builder instanceof TellerBSPTreeBuilder) {
                builderTauValueLabel.setText(" - Tau: " + ((TellerBSPTreeBuilder) currentBuilder).getTau());
                builderNameLabel.setText(" - Name: Teller Algorithm Builder");
            }
            else if (builder instanceof RandomBSPTreeBuilder) {
                builderNameLabel.setText(" - Name: Random Algorithm Builder");
            }
            else if (builder instanceof DeterministicBSPTreeBuilder) {
                builderNameLabel.setText(" - Name: Deterministic Algorithm Builder");
            }

            
            // Remove all labels and add the new ones
            selectedBuilderPanel.removeAll();
            selectedBuilderPanel.add(builderTitleLabel);
            selectedBuilderPanel.add(builderNameLabel);
            selectedBuilderPanel.add(builderTauValueLabel);
        } else {
            selectedBuilderPanel.removeAll();
            selectedBuilderPanel.add(builderTitleLabel);
            selectedBuilderPanel.add(noBuilderLabel);
        }

        // Refresh the UI
        selectedBuilderPanel.revalidate();
        selectedBuilderPanel.repaint();
    }

    /**
     * Updates the observer details.
     *
     * @param pos the selected position.
     * @param startAngle the start angle.
     * @param endAngle the end angle.
     */
    @Override
    public void onObserverSelected(Point2D pos, double startAngle, double endAngle) {
        observerPosition = pos;
        observerStartAngle = startAngle;
        observerEndAngle = endAngle;

        // Update labels accordingly
        selectedObserverPanel.removeAll();
        selectedObserverPanel.add(observerTitleLabel);

        if (observerPosition != null) {
            observerPositionLabel.setText(" - Position: (" + observerPosition.x + ", " + observerPosition.y + ")");
            observerAngleRangeLabel.setText(" - Angle: " + observerStartAngle + "° to " + observerEndAngle + "°");
            selectedObserverPanel.add(observerPositionLabel);
            selectedObserverPanel.add(observerAngleRangeLabel);
        } else {
            selectedObserverPanel.add(noObserverLabel);
        }

        // Refresh the UI
        selectedObserverPanel.revalidate();
        selectedObserverPanel.repaint();
    }
}

package be.umons.sdd.panels;

import be.umons.sdd.enums.EScenes;
import be.umons.sdd.interfaces.SceneObserver;
import be.umons.sdd.models.Scene2D;
import be.umons.sdd.utils.SceneSerializer;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SceneSelectorPanel extends JPanel {

    private static SceneSelectorPanel instance;

    private File selectedSceneFile;
    private Scene2D selectedScene;

    public List<SceneObserver> sceneObservers = new ArrayList<>();

    private JComboBox<String> sceneSelector;
    private JButton fileChooserButton;
    private JFileChooser fileChooser;

    private JPanel sceneSelectorRow;
    private JPanel fileChooserRow;

    /**
     * Returns the single instance of the TreeBuilderSelectorPanel class.
     * The panel is created on first call to this method.
     * @return the single instance of TreeBuilderSelectorPanel
     */
    public static synchronized SceneSelectorPanel getInstance() {
        if (instance == null) {
            instance = new SceneSelectorPanel();
        }
        return instance;
    }

    private SceneSelectorPanel() {
        initUI();
        initActionListeners();
    }

    /**
     * Initializes the user interface for the SceneSelectorPanel.
     * Sets the background color to white, configures the layout to use a vertical BoxLayout,
     * and adds padding around the panel. This method also adds two sub-panels to the SceneSelectorPanel,
     * including a "Select Scene" panel and a "Scene File" panel, with vertical spacing between each sub-panel.
     */
    private void initUI() {
        // Main panel settings
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Scene Selection"));
        setPreferredSize(new Dimension(300, 80));

        // Row 1: "Select Scene:" label and sceneSelector combo box
        sceneSelectorRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        sceneSelectorRow.setPreferredSize(new Dimension(260, 20));

        JLabel label1 = new JLabel("Select Scene:");
        label1.setPreferredSize(new Dimension(80, 20));
        sceneSelectorRow.add(label1);

        sceneSelector = new JComboBox<>(EScenes.getAllDisplayNames().toArray(new String[0]));
        sceneSelector.setSelectedIndex(0);
        sceneSelector.setPreferredSize(new Dimension(150, 20));
        sceneSelectorRow.add(sceneSelector);

        // Row 2: "Scene File:" label and fileChooserButton
        fileChooserRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        fileChooserRow.setPreferredSize(new Dimension(260, 20));

        JLabel label2 = new JLabel("Scene File:");
        label2.setPreferredSize(new Dimension(80, 20));
        fileChooserRow.add(label2);
        
        fileChooserButton = new JButton("Open File Chooser");
        fileChooserButton.setEnabled(false);
        fileChooserButton.setPreferredSize(new Dimension(150, 20));
        fileChooserRow.add(fileChooserButton);

        // Add rows to main panel
        add(sceneSelectorRow);
        add(fileChooserRow);
    }

    /**
     * Sets up action listeners for the scene selector and file chooser button.
     * 
     * <p> This method performs the following:
     * 1. Enables the file chooser button when the "Custom" option is selected
     *    in the scene selector combo box.
     * 2. Handles scene selection by invoking the onSceneSelected method for
     *    predefined scenes, or opening the file chooser if the "Custom" option
     *    is selected and no file has been selected yet.
     * 3. Handles file selection by opening the file chooser dialog when the
     *    file chooser button is clicked.
     */
    private void initActionListeners() {
        // Set fileChooserButton enabled when "Custom" option is selected => Default to disabled
        sceneSelector.addActionListener(e -> fileChooserButton.setEnabled(sceneSelector.getSelectedItem().equals(EScenes.CUSTOM.getDisplayName())));

        // Handle scene selection
        sceneSelector.addActionListener(e -> {
            if (!sceneSelector.getSelectedItem().equals("Custom")) {
                onSceneSelected();
            }
            else {
                // Check if fileChooser already has a file selected
                if (selectedSceneFile != null) {
                    fileChooser.setSelectedFile(selectedSceneFile);
                    onSceneSelected();
                }
                else {
                    openFileChooser();
                }
            }
        });

        // Handle file selection
        fileChooserButton.addActionListener(e -> openFileChooser());
    }

    /**
     * Handles the selection of a scene from the scene selector.
     * <p>
     * This method performs the following operations:
     * 1. Retrieves the selected scene name from the scene selector.
     * 2. Validates the selection; if invalid, displays an error message.
     * 3. If the "Custom" option is selected, it checks for a selected 
     *    custom scene file. If not selected, an error message is shown.
     * 4. Loads the scene from the appropriate file path using the 
     *    SceneSerializer.
     * 5. If any error occurs during loading, an error message is displayed.
     * 6. If the scene is successfully loaded, it updates the selectedScene 
     *    field and notifies all registered scene observers.
     */
    public void onSceneSelected() {
        String sceneSelectorValue = (String) sceneSelector.getSelectedItem();
        if (sceneSelectorValue == null) {
            JOptionPane.showMessageDialog(this, "Please select a scene.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (sceneSelectorValue.equals(EScenes.SELECT_SCENE.getDisplayName())) {
            selectedScene = null;
            notifySceneObservers();
            return;
        }

        Optional<EScenes> sceneOpt = EScenes.fromDisplayName(sceneSelectorValue);
        if (!sceneOpt.isPresent()) {
            JOptionPane.showMessageDialog(this, "Invalid scene selected.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        EScenes scene = sceneOpt.get();

        Scene2D scene2D;
        try {
            if (scene == EScenes.CUSTOM) {
                if (selectedSceneFile == null) {
                    JOptionPane.showMessageDialog(this, "Please select a custom scene file.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                scene2D = SceneSerializer.readScene(scene.getDisplayName(), selectedSceneFile);
            }
            else {
                scene2D = SceneSerializer.readScene(scene.getDisplayName(), scene.getPath());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading the custom scene file : " + e, "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (scene2D == null) {
            JOptionPane.showMessageDialog(this, "Error loading the scene.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        selectedScene = scene2D;
        notifySceneObservers();
    }

    /**
     * Opens a file chooser dialog for selecting a custom scene file.
     * 
     * The selected file will be stored in the selectedSceneFile field and the
     * text of the fileChooserButton will be set to the name of the selected
     * file.
     * 
     * If a file is selected, the onSceneSelected method will be called to
     * notify the scene observers.
     */
    public void openFileChooser() {
        fileChooser = new JFileChooser();
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedSceneFile = fileChooser.getSelectedFile();
            fileChooserButton.setText(fileChooser.getSelectedFile().getName());

            onSceneSelected();
        }
    }

    // Getters
    
    /**
     * Returns the currently selected scene.
     * 
     * @return the selected Scene2D object, or null if no scene has been selected
     */
    public Scene2D getSelectedScene() {
        return selectedScene;
    }

    // Public Scene Observers

    /**
     * Adds a SceneObserver to the list of observers.
     * 
     * After adding an observer, the onSceneSelected method of the added
     * observer will be called when a new scene is selected.
     * 
     * @param observer the SceneObserver to be added
     * @throws IllegalArgumentException if the observer is null or already registered
     */
    public Scene2D addSceneObserver(SceneObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("SceneObserver cannot be null.");
        }

        if (sceneObservers.contains(observer)) {
            throw new IllegalArgumentException("SceneObserver is already registered.");
        }

        sceneObservers.add(observer);

        return getSelectedScene();
    }

    /**
     * Removes a SceneObserver from the list of observers.
     * 
     * After removing an observer, the onSceneSelected method of the removed
     * observer will no longer be called when a new scene is selected.
     * 
     * @param observer the SceneObserver to be removed
     * @throws IllegalArgumentException if the observer is null
     */
    public void removeSceneObserver(SceneObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("SceneObserver cannot be null.");
        }

        sceneObservers.remove(observer);
    }

    /**
     * Notifies all registered SceneObservers that a new scene has been selected.
     * 
     * This method iterates over all registered SceneObservers and calls the
     * onSceneSelected method of each one, passing the currently selected scene
     * as an argument.
     */
    public void notifySceneObservers() {
        for (SceneObserver observer : sceneObservers) {
            observer.onSceneSelected(selectedScene);
        }
    }
}

package be.umons.sdd.main.panels;

import be.umons.sdd.builders.AbstractBSPTreeBuilder;
import be.umons.sdd.builders.DeterministicBSPTreeBuilder;
import be.umons.sdd.builders.RandomBSPTreeBuilder;
import be.umons.sdd.builders.TellerBSPTreeBuilder;
import be.umons.sdd.main.enums.EScenes;
import be.umons.sdd.main.enums.ETreeBuilder;
import be.umons.sdd.main.listeners.SceneLoadedListener;
import be.umons.sdd.main.listeners.TreeBuilderLoadedListener;
import be.umons.sdd.models.Scene2D;
import be.umons.sdd.utils.SceneSerializer;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.NumberFormatter;

public class FormPanel extends JPanel {

    private JComboBox<String> sceneSelector;
    private JButton fileChooserButton;
    private String selectedCustomFilePath;

    private JComboBox<String> treeBuilderSelector;
    private JFormattedTextField tauField;

    private JButton validateButton;

    private SceneLoadedListener sceneLoadedListener;
    private TreeBuilderLoadedListener treeBuilderLoadedListener;
    
    public FormPanel() {
        this.initUI();
    }

    /**
     * Sets the listener that will be notified when the scene is loaded.
     *
     * @param listener the SceneLoadedListener to be set
     */
    public void setSceneLoadedListener(SceneLoadedListener listener) {
        this.sceneLoadedListener = listener;
    }

    /**
     * Sets the listener that will be notified when the BSP builder method is loaded.
     *
     * @param listener the TreeBuilderLoadedListener to be set
     */
    public void setTreeBuilderLoadedListener(TreeBuilderLoadedListener listener) {
        this.treeBuilderLoadedListener = listener;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    /**
     * Initializes the user interface for the FormPanel.
     * Sets the background color to white, configures the layout to use a vertical BoxLayout,
     * and adds padding around the panel. This method also adds several sub-panels to the FormPanel,
     * including a scene selector panel, a tree builder selector panel, and a validation panel,
     * with vertical spacing between each sub-panel.
     */
    private void initUI() {
        setBackground(Color.WHITE);
        
        // Layout
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(createSceneSelectorPanel());
        add(Box.createVerticalStrut(10));
        add(createTreeBuilderSelectorPanel());
        add(Box.createVerticalStrut(10));
        add(createValidationPanel());
    }

    /**
     * Creates the scene selection panel.
     * <p>
     * It contains a labeled combo box to choose a scene and, if the "Custom" option is selected,
     * a file chooser button becomes enabled.
     *
     * @return the scene selection JPanel.
     */
    private JPanel createSceneSelectorPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Scene Selection"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
    
        // First row : Label "Select Scene" and the combo box.
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(new JLabel("Select Scene:"), gbc);
    
        java.util.List<String> sceneNames = new ArrayList<>(EScenes.getAllDisplayNames()); // Custom scene is included.
        sceneSelector = new JComboBox<>(sceneNames.toArray(new String[sceneNames.size()]));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(sceneSelector, gbc);
    
        // Second row : Label "Scene File" and the file chooser button.
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel fileLabel = new JLabel("Scene File:");
        fileLabel.setVisible(false);
        panel.add(fileLabel, gbc);
    
        fileChooserButton = new JButton("Choose File");
        fileChooserButton.setEnabled(false);
        fileChooserButton.setVisible(false);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(fileChooserButton, gbc);
    
        // Action listener for the file chooser button.
        fileChooserButton.addActionListener(e -> openFileChooser());
    
        // Action listener for the scene selector combo box. (Hidden and disabled file chooser for non-custom scenes)
        sceneSelector.addActionListener(e -> {
            String selected = (String) sceneSelector.getSelectedItem();
            boolean isCustom = EScenes.isCustomScene(selected);
            fileChooserButton.setEnabled(isCustom);
            fileChooserButton.setVisible(isCustom);
            fileLabel.setVisible(isCustom);
            if (!isCustom) {
                fileChooserButton.setText("Choose File");
                selectedCustomFilePath = null;
            }
        });
    
        return panel;
    }

    /**
     * Opens a file chooser dialog for selecting a custom scene file.
     */
    private void openFileChooser() {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedCustomFilePath = chooser.getSelectedFile().getAbsolutePath();
            fileChooserButton.setText(chooser.getSelectedFile().getName());
        }
    }

    /**
     * Creates the BSP builder selection panel.
     * It contains a labeled combo box to select the BSP builder method. If the "Teller" method is selected,
     * an input field for the tau value (default 0.5) appears.
     *
     * @return the BSP builder selection JPanel.
     */
    private JPanel createTreeBuilderSelectorPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("BSP Builder Selection"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
    
        // First row : Label "Select BSP Builder" and the combo box.
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(new JLabel("Select BSP Builder:"), gbc);
    
        java.util.List<String> builderNames = new ArrayList<>(ETreeBuilder.getAllDisplayNames());
        treeBuilderSelector = new JComboBox<>(builderNames.toArray(new String[builderNames.size()]));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(treeBuilderSelector, gbc);
    
        // Second row : Label "Tau Value" and the spinner for the Teller method.
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel tauLabel = new JLabel("Tau Value:");
        tauLabel.setVisible(false);
        panel.add(tauLabel, gbc);
    
        // Create a NumberFormatter with a NumberFormat that does not use grouping.
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setGroupingUsed(false);
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(8);
        NumberFormatter formatter = new NumberFormatter(numberFormat);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(1e-8);
        formatter.setMaximum(0.99999999);
        formatter.setAllowsInvalid(false);

        tauField = new JFormattedTextField(formatter);
        tauField.setValue(0.5);
        tauField.setColumns(10);
        tauField.setVisible(false);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(tauField, gbc);
    
        treeBuilderSelector.addActionListener(e -> {
            String selected = (String) treeBuilderSelector.getSelectedItem();
            boolean isTeller = selected != null && selected.equalsIgnoreCase("Teller");
            tauField.setVisible(isTeller);
            tauLabel.setVisible(isTeller);
            panel.revalidate();
            panel.repaint();
        });
    
        return panel;
    }

    /**
     * Creates the validation panel containing the "Validate" button.
     *
     * @return the validation JPanel.
     */
    private JPanel createValidationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(new TitledBorder("Validation"));

        validateButton = new JButton("Validate");
        validateButton.addActionListener(e -> onValidate());
        panel.add(validateButton);
        return panel;
    }

    /**
     * Validates the selected scene and BSP builder method, and loads the corresponding scene.
     * 
     * This method performs the following steps:
     * 1. Retrieves the selected scene and BSP builder method from the UI components.
     * 2. Validates that both selections are not null.
     * 3. Converts the selected scene and builder method to their corresponding enum values.
     * 4. Validates the tau value if the Teller method is selected.
     * 5. Loads the scene based on the selected scene type (custom or predefined).
     * 6. Fires a scene loaded event if the scene is successfully loaded.
     * 
     * If any validation fails or an error occurs during scene loading, an error message is displayed to the user.
     */
    private void onValidate() {
        this.validateScene();
        this.validateTreeBuilder();
    }

    /**
     * Validates the selected scene and BSP builder method, and loads the scene if valid.
     * 
     * This method performs the following steps:
     * 1. Retrieves the selected scene and BSP builder method from the UI components.
     * 2. Validates that both selections are not null.
     * 3. Checks if the selected scene and builder method are valid.
     * 4. Validates the tau value if the Teller method is selected.
     * 5. Loads the scene from the appropriate file path.
     * 6. Fires a scene loaded event if the scene is successfully loaded.
     * 
     * If any validation fails, an appropriate error message is displayed to the user.
     */
    private void validateScene() {
        String selectedScene = (String) sceneSelector.getSelectedItem();
        if (selectedScene == null) {
            JOptionPane.showMessageDialog(this, "Please select a scene.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Optional<EScenes> sceneOpt = EScenes.fromDisplayName(selectedScene);
        if (!sceneOpt.isPresent()) {
            JOptionPane.showMessageDialog(this, "Invalid scene selected.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        EScenes scene = sceneOpt.get();

        Scene2D scene2D;
        try {
            if (scene == EScenes.CUSTOM) {
                if (selectedCustomFilePath == null) {
                    JOptionPane.showMessageDialog(this, "Please select a custom scene file.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                scene2D = SceneSerializer.readScene(selectedCustomFilePath);
            }
            else {
                scene2D = SceneSerializer.readScene(scene.getPath());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading the custom scene file : " + e, "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (scene2D == null) {
            JOptionPane.showMessageDialog(this, "Error loading the scene.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        this.fireSceneLoadedEvent(scene2D);
    }

    /**
     * Validates the selected BSP tree builder method and tau value, and initializes the corresponding tree builder.
     * 
     * This method performs the following steps:
     * 1. Retrieves the selected BSP builder method from the treeBuilderSelector.
     * 2. Retrieves the tau value from the tauField.
     * 3. Checks if a builder method is selected. If not, shows an error message.
     * 4. Validates the selected builder method. If invalid, shows an error message.
     * 5. If the Teller method is selected, validates the tau value to ensure it is within the range (0, 1).
     * 6. Initializes the appropriate tree builder based on the selected method and tau value.
     * 7. Fires an event to indicate that the tree builder has been loaded.
     * 
     * Error messages are displayed using JOptionPane if any validation fails.
     */
    private void validateTreeBuilder() {
        String selectedBuilder = (String) treeBuilderSelector.getSelectedItem();
        double tau = (double) tauField.getValue();

        if (selectedBuilder == null) {
            JOptionPane.showMessageDialog(this, "Please select a BSP builder method.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Optional<ETreeBuilder> builderOpt = ETreeBuilder.fromDisplayName(selectedBuilder);
        if (!builderOpt.isPresent()) {
            JOptionPane.showMessageDialog(this, "Invalid BSP builder method selected.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ETreeBuilder builder = builderOpt.get();

        if (builder == ETreeBuilder.TELLER && (tau < 1e-8 || tau >= 1.0)) {
            JOptionPane.showMessageDialog(this, "Invalid tau value for the Teller method. Must be in the range (0, 1).", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        AbstractBSPTreeBuilder treeBuilder;
        treeBuilder = switch (builder) {
            case TELLER -> new TellerBSPTreeBuilder(tau);
            case RANDOM -> new RandomBSPTreeBuilder();
            default -> new DeterministicBSPTreeBuilder();
        };

        this.fireTreeBuilderLoadedEvent(treeBuilder);
    }

    /**
     * Fires an event indicating that a scene has been loaded.
     * If a sceneLoadedListener is registered, its onSceneLoaded method
     * will be called with the provided scene.
     *
     * @param scene the Scene2D object that has been loaded
     */
    private void fireSceneLoadedEvent(Scene2D scene) {
        if (sceneLoadedListener != null) {
            sceneLoadedListener.onSceneLoaded(scene);
        }
    }

    /**
     * Fires an event indicating that a BSP builder method has been loaded.
     * If a treeBuilderLoadedListener is registered, its onTreeBuilderLoaded method
     * will be called with the provided BSP builder method and tau value.
     *
     * @param builder the BSP builder method that has been loaded
     * @param tau the tau value for the Teller method
     */
    private void fireTreeBuilderLoadedEvent(AbstractBSPTreeBuilder builder) {
        if (treeBuilderLoadedListener != null) {
            treeBuilderLoadedListener.onBuilderLoaded(builder);
        }
    }
}

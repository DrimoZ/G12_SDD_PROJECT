package be.umons.sdd.panels;

import be.umons.sdd.builders.BSPTreeBuilder;
import be.umons.sdd.builders.DeterministicBSPTreeBuilder;
import be.umons.sdd.builders.RandomBSPTreeBuilder;
import be.umons.sdd.builders.TellerBSPTreeBuilder;
import be.umons.sdd.enums.ETreeBuilder;
import be.umons.sdd.interfaces.TreeBuilderObserver;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.NumberFormatter;

public class TreeBuilderSelectorPanel extends JPanel {

    private static TreeBuilderSelectorPanel instance;

    private BSPTreeBuilder selectedTreeBuilder;

    public List<TreeBuilderObserver> treeBuilderObservers = new ArrayList<>();

    private JComboBox<String> treeBuilderSelector;
    private JFormattedTextField tauValueField;

    private JPanel builderSelectorRow;
    private JPanel tellerTauValueRow;

    /**
     * Returns the single instance of the TreeBuilderSelectorPanel class.
     * The panel is created on first call to this method.
     * @return the single instance of TreeBuilderSelectorPanel
     */
    public static synchronized TreeBuilderSelectorPanel getInstance() {
        if (instance == null) {
            instance = new TreeBuilderSelectorPanel();
        }
        return instance;
    }

    private TreeBuilderSelectorPanel() {
        initUI();
        initActionListeners();
    }

    /**
     * Initializes the user interface for the TreeBuilderSelectorPanel.
     * 
     * This method sets up the main panel with a vertical BoxLayout and a titled border.
     * It configures two rows: one for selecting the tree builder and another for
     * specifying the Teller method's tau value. The "Select Scene" row includes a 
     * combo box populated with tree builder names. The "Teller Tau Value" row includes 
     * a formatted text field for tau value input, which is initially disabled.
     */
    private void initUI() {
        // Main panel settings
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Tree Builder Selection"));
        setPreferredSize(new Dimension(300, 80));

        // Row 1: "Builder"
        builderSelectorRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        builderSelectorRow.setPreferredSize(new Dimension(260, 20));

        JLabel label1 = new JLabel("Select Scene:");
        label1.setPreferredSize(new Dimension(80, 20));
        builderSelectorRow.add(label1);

        treeBuilderSelector = new JComboBox<>(ETreeBuilder.getAllDisplayNames().toArray(new String[0]));
        treeBuilderSelector.setSelectedIndex(0);
        treeBuilderSelector.setPreferredSize(new Dimension(150, 20));
        builderSelectorRow.add(treeBuilderSelector);

        // Row 2: "Tau Value"
        tellerTauValueRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        tellerTauValueRow.setPreferredSize(new Dimension(260, 20));

        JLabel label2 = new JLabel("Teller Tau Value:");
        label2.setPreferredSize(new Dimension(80, 20));
        tellerTauValueRow.add(label2);
        
        tauValueField = new JFormattedTextField(getFormater());
        tauValueField.setValue(0.5);
        tauValueField.setEnabled(false);
        tauValueField.setPreferredSize(new Dimension(150, 20));
        tellerTauValueRow.add(tauValueField);

        // Add rows to main panel
        add(builderSelectorRow);
        add(tellerTauValueRow);
    }

    /**
     * Sets up action listeners for the tree builder selector and tau value field.
     * 
     * This method performs the following:
     * 1. Enables the tau value field when the "Teller" method is selected in the
     *    tree builder selector combo box.
     * 2. Handles tree builder selection by invoking the onBuilderSelected method
     *    for predefined scenes, or opening the file chooser if the "Custom" option
     *    is selected and no file has been selected yet.
     * 3. Handles tau value input by invoking the onBuilderSelected method when the
     *    tau value field is edited.
     */
    private void initActionListeners() {
        treeBuilderSelector.addActionListener(e -> tauValueField.setEnabled(treeBuilderSelector.getSelectedItem().equals(ETreeBuilder.TELLER.getDisplayName())));

        treeBuilderSelector.addActionListener(e -> onBuilderSelected());
        tauValueField.addActionListener(e -> onBuilderSelected());
    }

    /**
     * Handles tree builder selection by validating the selected builder method and tau value
     * for the Teller method, and notifying the tree builder observers if the validation is successful.
     * 
     * This method performs the following steps:
     * 1. Retrieves the selected builder method and tau value from the UI components.
     * 2. Validates that the selected builder method is not null.
     * 3. Converts the selected builder method to its corresponding enum value.
     * 4. Validates the tau value if the Teller method is selected.
     * 5. Creates the appropriate tree builder based on the selected method and tau value.
     * 6. Notifies the tree builder observers with the new tree builder.
     * 
     * If any validation fails, an appropriate error message is displayed to the user.
     */
    private void onBuilderSelected() {
        String selectedBuilder = (String) treeBuilderSelector.getSelectedItem();
        double tau = (double) tauValueField.getValue();

        
        if (selectedBuilder == null) {
            JOptionPane.showMessageDialog(this, "Please select a BSP builder method.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedBuilder.equals(ETreeBuilder.SELECT_BUILDER.getDisplayName())) {
            selectedTreeBuilder = null;
            notifyTreeBuilderObservers();
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

        BSPTreeBuilder treeBuilder;
        treeBuilder = switch (builder) {
            case TELLER -> new TellerBSPTreeBuilder(tau);
            case RANDOM -> new RandomBSPTreeBuilder();
            default -> new DeterministicBSPTreeBuilder();
        };

        selectedTreeBuilder = treeBuilder;
        notifyTreeBuilderObservers();
    }

    // Getters

    /**
     * Returns the currently selected BSP tree builder.
     * 
     * @return the selected BSPTreeBuilder
     */
    public BSPTreeBuilder getSelectedTreeBuilder() {
        return selectedTreeBuilder;
    }

    // Public Scene Observers

    /**
     * Adds a TreeBuilderObserver to the list of observers.
     * 
     * After adding an observer, the onTreeBuilderSelected method of the added
     * observer will be called when a new tree builder is selected.
     * 
     * @param observer the TreeBuilderObserver to be added
     * @return the currently selected BSPTreeBuilder
     * @throws IllegalArgumentException if the observer is null or already registered
     */
    public BSPTreeBuilder addTreeBuilderObserver(TreeBuilderObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("TreeBuilderObserver cannot be null.");
        }

        if (treeBuilderObservers.contains(observer)) {
            throw new IllegalArgumentException("TreeBuilderObserver is already registered.");
        }

        treeBuilderObservers.add(observer);

        return getSelectedTreeBuilder();
    }

    /**
     * Removes a TreeBuilderObserver from the list of observers.
     * 
     * After removing an observer, the onSceneSelected method of the removed
     * observer will no longer be called when a new scene is selected.
     * 
     * @param observer the TreeBuilderObserver to be removed
     * @throws IllegalArgumentException if the observer is null
     */
    public void removeTreeBuilderObserver(TreeBuilderObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("TreeBuilderObserver cannot be null.");
        }

        treeBuilderObservers.remove(observer);
    }

    /**
     * Notifies all registered TreeBuilderObserver that a new scene has been selected.
     * 
     * This method iterates over all registered TreeBuilderObserver and calls the
     * onSceneSelected method of each one, passing the currently selected scene
     * as an argument.
     */
    public void notifyTreeBuilderObservers() {
        for (TreeBuilderObserver observer : treeBuilderObservers) {
            observer.onTreeBuilderSelected(selectedTreeBuilder);
        }

        System.out.println("Builder selected: " + selectedTreeBuilder);
    }

    // Helper

    /**
     * Creates and returns a NumberFormatter configured for double values.
     *
     * The formatter is set to disallow grouping, allows a maximum of 8 fractional digits,
     * and restricts values between 1e-8 and 0.99999999. The formatter does not allow
     * invalid input.
     *
     * @return a configured NumberFormatter for double values.
     */
    private NumberFormatter getFormater() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setGroupingUsed(false);
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(8);
        NumberFormatter formatter = new NumberFormatter(numberFormat);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(1e-8);
        formatter.setMaximum(0.99999999);
        formatter.setAllowsInvalid(false);
        return formatter;
    }
}

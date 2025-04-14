package be.umons.sdd.panels;

import be.umons.sdd.interfaces.ObserverObserver;
import be.umons.sdd.interfaces.SceneObserver;
import be.umons.sdd.models.Point2D;
import be.umons.sdd.models.Scene2D;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class ObserverSelectorPanel extends JPanel implements SceneObserver {

    private static ObserverSelectorPanel instance;

    private Scene2D currentScene;
    private final Point2D position = new Point2D(0, 0);
    private double startAngle = 0.0;
    private double endAngle = 360.0;

    public List<ObserverObserver> observerObservers = new ArrayList<>();

    private JPanel observerPositionPanel;
    private JPanel observerAnglePanel;

    // Spinners for Position
    private JSpinner spinnerX;
    private JSpinner spinnerY;
    
    // Spinners for Angle Range
    private JSpinner spinnerStartAngle;
    private JSpinner spinnerEndAngle;

    /**
     * Returns the single instance of the TreeBuilderSelectorPanel class.
     * The panel is created on first call to this method.
     * @return the single instance of TreeBuilderSelectorPanel
     */
    public static synchronized ObserverSelectorPanel getInstance() {
        if (instance == null) {
            instance = new ObserverSelectorPanel();
        }
        return instance;
    }

    private ObserverSelectorPanel() {
        initUI();
        initActionListeners();
    }

    /**
     * Initializes the user interface for the ObserverSelectorPanel.
     * Sets the background color, configures a vertical BoxLayout,
     * and adds sub-panels for selecting the observer's position and angle.
     */
    private void initUI() {
        // Main panel settings.
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Observer Selection"));
        setPreferredSize(new Dimension(300, 80));

        // Row 1: Position selection panel.
        observerPositionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        observerPositionPanel.setPreferredSize(new Dimension(260, 20));

        JLabel label1 = new JLabel("Select Position:");
        label1.setPreferredSize(new Dimension(80, 20));
        observerPositionPanel.add(label1);

        // Create JSpinners for X and Y.
        spinnerX = new JSpinner(new SpinnerNumberModel(0, -100000, 100000, 1));
        spinnerX.setPreferredSize(new Dimension(80, 20));
        spinnerY = new JSpinner(new SpinnerNumberModel(0, -100000, 100000, 1));
        spinnerY.setPreferredSize(new Dimension(80, 20));
        
        observerPositionPanel.add(spinnerX);
        observerPositionPanel.add(spinnerY);

        // Row 2: Angle selection panel.
        observerAnglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        observerAnglePanel.setPreferredSize(new Dimension(260, 20));

        JLabel label2 = new JLabel("Select Angle:");
        label2.setPreferredSize(new Dimension(80, 20));
        observerAnglePanel.add(label2);

        // Create JSpinners for start and end angle.
        spinnerStartAngle = new JSpinner(new SpinnerNumberModel(0.0, -360.0, 360.0, 1.0));
        spinnerStartAngle.setPreferredSize(new Dimension(80, 20));
        spinnerEndAngle = new JSpinner(new SpinnerNumberModel(360.0, -360.0, 360.0, 1.0));
        spinnerEndAngle.setPreferredSize(new Dimension(80, 20));
        
        observerAnglePanel.add(spinnerStartAngle);
        observerAnglePanel.add(spinnerEndAngle);

        // Add sub-panels to main panel.
        add(observerPositionPanel);
        add(observerAnglePanel);
    }

    /**
     * Sets up action listeners for the input controls.
     * When any spinner value changes, update the corresponding variables
     * and notify the registered ObserverObservers.
     */
    private void initActionListeners() {
        onSceneSelected(SceneSelectorPanel.getInstance().addSceneObserver(this));

        spinnerX.addChangeListener(e -> {
            double newX = ((Number) spinnerX.getValue()).doubleValue();
            position.x = newX;
            notifyObservers();   
        });

        spinnerY.addChangeListener(e -> {
            double newY = ((Number) spinnerY.getValue()).doubleValue();
            position.y = newY;
            notifyObservers();
        });

        spinnerStartAngle.addChangeListener(e -> {            
            startAngle = ((Number) spinnerStartAngle.getValue()).doubleValue();

            // Max Angle diff is 360
            if (endAngle - startAngle > 360) {
                endAngle = startAngle + 360;
                spinnerEndAngle.setValue(endAngle);
            }

            notifyObservers();
        });

        spinnerEndAngle.addChangeListener(e -> {
            endAngle = ((Number) spinnerEndAngle.getValue()).doubleValue();

            // Max Angle diff is 360
            if (endAngle - startAngle > 360) {
                startAngle = endAngle - 360;
                spinnerStartAngle.setValue(startAngle);
            }

            notifyObservers();
        });
    }

    // Public Observers

    @Override
    public void onSceneSelected(Scene2D scene) {
        currentScene = scene;

        if (currentScene != null) {
            double extentX = currentScene.getExtentX();
            double extentY = currentScene.getExtentY();
            spinnerX.setModel(new SpinnerNumberModel(position.x, -extentX, extentX, 1.0));
            spinnerY.setModel(new SpinnerNumberModel(position.y, -extentY, extentY, 1.0));
        }
    }

    /**
     * Adds an ObserverObserver to the list of observers.
     *
     * After adding an observer, the onObserverSelected method of the added
     * observer will be called with the current position, startAngle, and
     * endAngle as arguments.
     *
     * @param observer the ObserverObserver to be added
     * @throws IllegalArgumentException if the observer is null or already registered
     */
    public void addObserver(ObserverObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("ObserverObserver cannot be null.");
        }

        if (observerObservers.contains(observer)) {
            throw new IllegalArgumentException("ObserverObserver is already registered.");
        }

        observerObservers.add(observer);
        observer.onObserverSelected(position, startAngle, endAngle);
    }

    /**
     * Removes a ObserverObserver from the list of observers.
     * 
     * After removing an observer, the onObserverSelected method of the removed
     * observer will no longer be called when the position, startAngle or endAngle
     * of the selected observer changes.
     * 
     * @param observer the ObserverObserver to be removed
     * @throws IllegalArgumentException if the observer is null
     */
    public void removeObserver(ObserverObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("ObserverObserver cannot be null.");
        }

        observerObservers.remove(observer);
    }

    /**
     * Notifies all registered ObserverObserver that the position, startAngle
     * or endAngle of the selected observer has changed.
     * 
     * This method iterates over all registered ObserverObserver and calls the
     * onObserverSelected method of each one, passing the current position,
     * startAngle and endAngle as arguments.
     */
    public void notifyObservers() {
        for (ObserverObserver observer : observerObservers) {
            observer.onObserverSelected(position, startAngle, endAngle);
        }
    }
}

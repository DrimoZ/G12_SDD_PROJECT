package be.umons.sdd.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

public class MainFrame extends JFrame {

    private JSplitPane mainSplitPane;
    private JPanel formPanel;
    private JPanel visulatizerPanel;

    public MainFrame() {
        setStyle();
        initUI();
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
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);

        // Frame must be divided into 2 big parts : left form and right visualization.
        // Form part is 300 pixels wide and the visualization part takes the remaining space.

        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(300);
        mainSplitPane.setResizeWeight(0.0);

        mainSplitPane.setLeftComponent(initFormPanel());
        mainSplitPane.setRightComponent(new JPanel());


        add(mainSplitPane, BorderLayout.CENTER);
    }

    private JPanel initFormPanel() {
        formPanel = new JPanel();

        formPanel.setPreferredSize(new Dimension(300, 800));

        formPanel.add(SceneSelectorPanel.getInstance());
        formPanel.add(TreeBuilderSelectorPanel.getInstance());
        formPanel.add(DetailsPanel.getInstance());

        return formPanel;
    }
}

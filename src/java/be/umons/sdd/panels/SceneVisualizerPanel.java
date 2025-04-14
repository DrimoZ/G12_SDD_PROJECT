package be.umons.sdd.panels;

import be.umons.sdd.interfaces.BSPNodeObserver;
import be.umons.sdd.models.BSPNode;
import be.umons.sdd.models.Line2D;
import be.umons.sdd.models.Point2D;
import be.umons.sdd.models.Scene2D;
import be.umons.sdd.models.StraightSegment2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class SceneVisualizerPanel extends JPanel implements BSPNodeObserver {
    
    private static SceneVisualizerPanel instance;

    private BSPNode currentNode;
    private Scene2D currentScene;

    private boolean drawPartitionLine = false;

    /**
     * Returns the single instance of the SceneVisualizerPanel class.
     * The panel is created on first call to this method.
     * @return the single instance of SceneVisualizerPanel
     */
    public static synchronized SceneVisualizerPanel getInstance() {
        if (instance == null) {
            instance = new SceneVisualizerPanel();
        }
        return instance;
    }

    private SceneVisualizerPanel() {
        initUI();
    }

    private void initUI() {
        setPreferredSize(new Dimension(900, 740));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Scene Visualization"));
    }

    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        System.out.println("SceneVisualizerPanel.paintComponent()");

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (currentScene != null && currentNode != null) {
            drawNode(g2, currentNode, drawPartitionLine);
        } else {
            int width = getWidth() - 2 * 20;
            int height = getHeight() - 2 * 20;

            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(20, 20, width, height);
            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(20, 20, width, height);

            String text = "Please load a scene before visualizing it.";
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            int x = 20 + (width - textWidth) / 2;
            int y = 20 + (height + textHeight) / 2;

            g2.drawString(text, x, y);
        }
    }

    private void drawNode(Graphics2D g2, BSPNode node, boolean drawPartitionLine) {
        // Recursively draw the BSP tree (draw the coplanar segments first)
        int sceneWidth = currentScene.getExtentX() * 2;
        int sceneHeight = currentScene.getExtentY() * 2;

        // Panel size (with an assumed 20 pixels margin)
        int panelWidth = getWidth() - 2 * 20;
        int panelHeight = getHeight() - 2 * 20;

        // Scale factor
        double scaleX = ((double) panelWidth) / sceneWidth;
        double scaleY = ((double) panelHeight) / sceneHeight;
        // Use the lowest scale factor to make the whole scene visible
        if (scaleX < scaleY) {
            scaleY = scaleX;
        } else {
            scaleX = scaleY;
        }

        // Center of panel
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Draw all segments
        for (StraightSegment2D segment : node.getCoplanarObjects()) {
            int x1 = (int) (centerX + segment.getStart().x * scaleX);
            int y1 = (int) (centerY - segment.getStart().y * scaleY);
            int x2 = (int) (centerX + segment.getEnd().x * scaleX);
            int y2 = (int) (centerY - segment.getEnd().y * scaleY);

            g2.setColor(segment.getColor());
            g2.drawLine(x1, y1, x2, y2);
        }

        // Draw the partition line if applicable.
        if (node.getPartition() != null && drawPartitionLine) {
            Line2D partitionLine = node.getPartition();

            // Define the scene bounding box (scene coordinates)
            double xmin = -currentScene.getExtentX();
            double xmax = currentScene.getExtentX();
            double ymin = -currentScene.getExtentY();
            double ymax = currentScene.getExtentY();

            // Use a small epsilon to avoid division by zero issues.
            final double EPSILON = 1e-6;

            // Store intersection points.
            java.util.List<Point2D> intersections = new java.util.ArrayList<>();

            // Intersect with left border: x = xmin.
            if (Math.abs(partitionLine.getB()) > EPSILON) {
                double y = -(partitionLine.getA() * xmin + partitionLine.getC()) / partitionLine.getB();
                if (y >= ymin && y <= ymax) {
                    intersections.add(new Point2D(xmin, y));
                }
            }
            // Intersect with right border: x = xmax.
            if (Math.abs(partitionLine.getB()) > EPSILON) {
                double y = -(partitionLine.getA() * xmax + partitionLine.getC()) / partitionLine.getB();
                if (y >= ymin && y <= ymax) {
                    intersections.add(new Point2D(xmax, y));
                }
            }
            // Intersect with bottom border: y = ymin.
            if (Math.abs(partitionLine.getA()) > EPSILON) {
                double x = -(partitionLine.getB() * ymin + partitionLine.getC()) / partitionLine.getA();
                if (x >= xmin && x <= xmax) {
                    intersections.add(new Point2D(x, ymin));
                }
            }
            // Intersect with top border: y = ymax.
            if (Math.abs(partitionLine.getA()) > EPSILON) {
                double x = -(partitionLine.getB() * ymax + partitionLine.getC()) / partitionLine.getA();
                if (x >= xmin && x <= xmax) {
                    intersections.add(new Point2D(x, ymax));
                }
            }

            // Remove duplicate or very-close points (if necessary)
            if (intersections.size() >= 2) {
                // In most cases two distinct intersection points should be found.
                Point2D p1 = intersections.get(0);
                Point2D p2 = intersections.get(1);

                // Transform scene coordinates to panel coordinates.
                int x1 = (int) (centerX + p1.x * scaleX);
                int y1 = (int) (centerY - p1.y * scaleY);
                int x2 = (int) (centerX + p2.x * scaleX);
                int y2 = (int) (centerY - p2.y * scaleY);

                // Set a distinct color for partition lines.
                g2.setColor(Color.RED);
                g2.drawLine(x1, y1, x2, y2);
            }
        }

        if (node.isLeaf()) {
            return;
        }

        drawNode(g2, node.getLeft(), drawPartitionLine);
        drawNode(g2, node.getRight(), drawPartitionLine);
    }


    // Observers
    
    @Override
    public void onBSPUpdated(BSPNode node) {
        currentNode = node;
    }

    public void setScene(Scene2D scene) {
        currentScene = scene;
        repaint();
    }

}

package be.umons.sdd.main.panels;

import be.umons.sdd.models.Scene2D;
import be.umons.sdd.models.StraightSegment2D;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class SceneVisulatizerPanel extends JPanel {

    private Scene2D scene;

    private final int margin = 20;

    public SceneVisulatizerPanel(Scene2D scene) {
        this.scene = scene;

        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Scene Visualization"));
    }

    public void setScene(Scene2D scene) {
        this.scene = scene;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (scene != null) {

            // Draw the scene in the center of the panel.

            // Scene size
            int sceneWidth = scene.getExtentX() * 2;
            int sceneHeight = scene.getExtentY() * 2;

            // Panel size
            int panelWidth = getWidth() - 2 * margin;
            int panelHeight = getHeight() - 2 * margin;

            // Scale factor
            double scaleX = ((double) panelWidth) / sceneWidth;
            double scaleY =  ((double) panelHeight) / sceneHeight;

            // Need to take the lowest scale factor !! To make the whole scene visible
            if (scaleX < scaleY) {
                scaleY = scaleX;
            } else {
                scaleX = scaleY;
            }

            // Center
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            // Draw the segments
            for (StraightSegment2D segment : scene.getSegments()) {
                int x1 = (int) (centerX + segment.getStart().x * scaleX);
                int y1 = (int) (centerY - segment.getStart().y * scaleY);
                int x2 = (int) (centerX + segment.getEnd().x * scaleX);
                int y2 = (int) (centerY - segment.getEnd().y * scaleY);

                g2.setColor(segment.getColor());
                g2.drawLine(x1, y1, x2, y2);
            }
        } else {
            int width = getWidth() - 2 * margin;
            int height = getHeight() - 2 * margin;

            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(margin, margin, width, height);
            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(margin, margin, width, height);

            String text = "Please load a scene before visualizing it.";
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            int x = margin + (width - textWidth) / 2;
            int y = margin + (height + textHeight) / 2;

            g2.drawString(text, x, y);
        }
    }
}

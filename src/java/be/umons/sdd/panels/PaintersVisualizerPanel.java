package be.umons.sdd.panels;

import be.umons.sdd.builders.PaintersViewBuilder;
import be.umons.sdd.interfaces.BSPNodeObserver;
import be.umons.sdd.interfaces.ObserverObserver;
import be.umons.sdd.models.AngularSegment;
import be.umons.sdd.models.BSPNode;
import be.umons.sdd.models.Point2D;
import be.umons.sdd.models.View360;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class PaintersVisualizerPanel extends JPanel implements BSPNodeObserver, ObserverObserver {
    
    private static PaintersVisualizerPanel instance;

    private BSPNode currentNode;
    private Point2D observerPosition;
    private double observerStartAngle;
    private double observerEndAngle;


    /**
     * Returns the single instance of the SceneVisualizerPanel class.
     * The panel is created on first call to this method.
     * @return the single instance of SceneVisualizerPanel
     */
    public static synchronized PaintersVisualizerPanel getInstance() {
        if (instance == null) {
            instance = new PaintersVisualizerPanel();
        }
        return instance;
    }

    private PaintersVisualizerPanel() {
        initUI();
        initObservers();
    }

    private void initUI() {
        setPreferredSize(new Dimension(900, 740));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Painter's Visualizer"));
    }

    private void initObservers() {
        ObserverSelectorPanel.getInstance().addObserver(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Setup rendering hints.
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Check if we have the BSP node and a valid observer position.
        if (currentNode != null && observerPosition != null) {
            // Compute the 360° view using the Painters algorithm.
            View360 view = PaintersViewBuilder.paintersAlgorithm(currentNode, observerPosition);
            // Draw the result.
            drawView360(g2, view);
        } else {
            int width = getWidth() - 2 * 20;
            int height = getHeight() - 2 * 20;

            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(20, 20, width, height);
            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(20, 20, width, height);

            String text = "No painter view available (BSP tree or observer missing).";
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            int x = 20 + (width - textWidth) / 2;
            int y = 20 + (height + textHeight) / 2;

            g2.drawString(text, x, y);
        }
    }
    
    /**
     * Draws the computed 360° view as a small preview in the top-left corner.
     * The preview shows the circle outline, colored segments drawn only on the circle line,
     * degree markers (with 0° matching the top, which is where a blue radial line is drawn),
     * and blue radial lines for the observer's viewing range.
     *
     * @param g2 The Graphics2D context.
     * @param view The View360 result from the Painter's algorithm.
     */
    private void drawView360(Graphics2D g2, View360 view) {
        // PREVIEW CONFIGURATION
        int previewMargin = 45;          // margin from top and left
        int previewRadius = 130;         // fixed radius for the preview circle
        int centerX = previewMargin + previewRadius;
        int centerY = previewMargin + previewRadius;
        
        // DRAW DEGREE MARKERS
        g2.setFont(g2.getFont().deriveFont(10f));
        FontMetrics fm = g2.getFontMetrics();

        // For 0° at the top, shift by +90 degrees.
        for (int deg = 0; deg < 360; deg += 30) {
            double rad = Math.toRadians(deg + 90);  // so that deg=0 appears at top

            int xMarker = centerX + (int)(previewRadius * Math.cos(rad));
            int yMarker = centerY - (int)(previewRadius * Math.sin(rad));
            int labelX = centerX + (int)((previewRadius + 15) * Math.cos(rad)) ;
            int labelY = centerY - (int)((previewRadius + 15) * Math.sin(rad));

            String label = deg + "°";
            int labelWidth = fm.stringWidth(label);
            int labelHeight = fm.getAscent();

            g2.setColor(Color.BLACK);
            g2.drawString(label, labelX - labelWidth / 2, labelY + labelHeight / 2);

            // Draw a small tick mark.
            int tickLength = 4;
            int xTickStart = centerX + (int)((previewRadius - tickLength) * Math.cos(rad));
            int yTickStart = centerY - (int)((previewRadius - tickLength) * Math.sin(rad));
            g2.drawLine(xTickStart, yTickStart, xMarker, yMarker);
        }
        
        // DRAW COLORED SEGMENTS ONLY ON THE CIRCLE LINE
        List<AngularSegment> angularSegments = view.getAngularSegments();

        // For each segment, compute its start and extent in degrees.
        // Assume AngularSegment angles are in radians with 0 at east.
        // Convert them to degrees with 0 at top: (deg = Math.toDegrees(angle) - 90)
        for (AngularSegment as : angularSegments) {
            double segStartDeg = Math.toDegrees(as.getStartAngle());
            double segEndDeg = Math.toDegrees(as.getEndAngle());
            

            double segExtentDeg = segEndDeg - segStartDeg;

            // Handle wrap-around if needed.
            if (segExtentDeg < 0) {
                segExtentDeg += 360;
            }

            
            g2.setColor(Color.white);
            g2.setStroke(new BasicStroke(6));  // thick stroke on the circle line
            g2.drawArc(centerX - previewRadius, centerY - previewRadius,
                       previewRadius * 2, previewRadius * 2,
                       (int) segStartDeg, (int) segExtentDeg);

            // Draw the arc along the circle with the actual segment color.
            Color segmentColor = as.getSegment().getColor();
            g2.setColor(segmentColor);
            g2.setStroke(new BasicStroke(2));  // thick stroke on the circle line
            g2.drawArc(centerX - previewRadius, centerY - previewRadius,
                       previewRadius * 2, previewRadius * 2,
                       (int) segStartDeg, (int) segExtentDeg);
        }

        // DRAW ARC TO SHOW THE WHOLE VIEWING RANGE
        g2.setColor(Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(2));
        g2.drawArc(centerX - 20, centerY - 20,
                    20 * 2, 20 * 2,
                (int) observerStartAngle + 90, (int) (observerEndAngle  - observerStartAngle));
        
        // DRAW OBSERVER'S VIEWING RANGE AS BLUE RADIAL LINES
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(2));
        int lineLength = previewRadius;
        // When observerStartAngle is 0, the blue line should be at the top.
        int xStart = centerX + (int)(lineLength * Math.cos(Math.toRadians(observerStartAngle + 90)));
        int yStart = centerY - (int)(lineLength * Math.sin(Math.toRadians(observerStartAngle + 90)));
        int xEnd = centerX + (int)(lineLength * Math.cos(Math.toRadians(observerEndAngle + 90)));
        int yEnd = centerY - (int)(lineLength * Math.sin(Math.toRadians(observerEndAngle + 90)));
        g2.drawLine(centerX, centerY, xStart, yStart);
        g2.drawLine(centerX, centerY, xEnd, yEnd);
    }

    // Observers
    
    @Override
    public void onBSPUpdated(BSPNode node) {
        currentNode = node;

        revalidate();
        repaint();
    }

    @Override
    public void onObserverSelected(Point2D pos, double startAngle, double endAngle) {
        observerPosition = pos;
        observerStartAngle = startAngle;
        observerEndAngle = endAngle;

        revalidate();
        repaint();
    }
}

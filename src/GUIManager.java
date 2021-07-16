/*
   The GUI Manager creates and runs the JFrame window of the application, and its two components.
   The two components are a drawingPanel object (custom JPanel for visual painting), and
   a GUISettings object (custom JPanel for starting and resetting the visualization)
*/

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import java.awt.BorderLayout;

public class GUIManager {

    private JFrame frame;             // The application window
    private GUISettings settings;     // The start/reset panel
    private Grid drawingPanel;        // The visualization panel

    public GUIManager() {
        // Starts the Event Dispatch Thread (EDT) for future GUI operations
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private void createAndShowGUI() {
        frame = new JFrame("A Star Path Finding Visualizer");
        drawingPanel = new Grid();

        /* Note - The GUISettings instance is given access to the Grid drawingPanel via
           passing it in as a parameter to its constructor. This way, when the user clicks
           on the setting options, it can respond by calling methods on the drawingPanel instance!
        */
        settings = new GUISettings(drawingPanel);

        // Set up frame properties and add components to it
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.add(drawingPanel, BorderLayout.CENTER);
        frame.add(settings, BorderLayout.NORTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}



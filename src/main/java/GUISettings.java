/*
   Customized JPanel class responsible for controlling the settings
   of the GUI. The panel consists of the start and reset buttons that
   will (when clicked on) call the Grid's runAglorithm and reset functions
*/

package main.java;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUISettings extends JPanel {
    private final Grid graphicPanel;

    private final JButton start;
    private final JButton reset;

    public GUISettings(Grid drawingPanel) {
        graphicPanel = drawingPanel;
        start = new JButton("Start");
        reset = new JButton("Reset");
        add(start);
        add(reset);
        addListeners();
    }

    public Dimension setPreferredSize() {
        return new Dimension(200, 400);
    }

    private void addListeners() {
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graphicPanel.runAlgorithm();
            }
        });

        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graphicPanel.reset();
            }
        });
    }
}

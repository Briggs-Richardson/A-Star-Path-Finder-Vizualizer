/*
    Grid is the panel responsible for painting the visualization of the A*
    algorithm. It contains an AStarLogic member in order to obtain
    the necessary information from the A* algorithm (such as grid size,
    open sites, closed sites, etc.) It passes a reference to itself to the
    AStarLogic instance so that the logic class can issue repaints() during
    the process (iterations) of the A* algorithm (on a swingworker thread)
 */

package main.java;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.*;
import java.util.*;


public class Grid extends JPanel {

    private final AStarLogic logic;

    /* These two fields are used to determine if the user has clicked and is dragging on either
       the start or target node. If they have, then we know the user is requesting that node's
       relocation. Otherwise, they must be drawing an obstacle.
     */
    private boolean startNodeClicked;
    private boolean targetNodeClicked;

    public Grid() {
        logic = new AStarLogic(this);
        startNodeClicked = false;
        targetNodeClicked = false;
        addPanelListeners();
    }

    /* Add some mouse listeners to the panel for user input on the panel */
    private void addPanelListeners() {
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int xPos = getXAtRawCoordinate(e.getPoint().x);
                int yPos = getYAtRawCoordinate(e.getPoint().y);

                // Do not draw an obstacle on the starting/target nodes!
                if ((xPos != logic.getStart().getX() || yPos != logic.getStart().getY()) &&
                        (xPos != logic.getTarget().getX() || yPos != logic.getTarget().getY())) {
                    logic.setObstaclePosition(xPos, yPos);
                    repaint(xPos, yPos, logic.getGridSize(), logic.getGridSize());
                }
                targetNodeClicked = false;
                startNodeClicked = false;
            }
            public void mousePressed(MouseEvent e) {
                int xPos = getXAtRawCoordinate(e.getPoint().x);
                int yPos = getYAtRawCoordinate(e.getPoint().y);
                /* Check if user clicked on start or target node, otherwise
                they're drawing an obstacle */
                if (xPos == logic.getStart().getX() && yPos == logic.getStart().getY()) {
                    startNodeClicked = true;
                    targetNodeClicked = false;
                } else if (xPos == logic.getTarget().getX() &&
                     yPos == logic.getTarget().getY()) {
                    targetNodeClicked = true;
                    startNodeClicked = false;
                } else {
                    startNodeClicked = false;
                    targetNodeClicked = false;
                }
            }
            public void mouseReleased(MouseEvent e) {
                int xPos = getXAtRawCoordinate(e.getPoint().x);
                int yPos = getYAtRawCoordinate(e.getPoint().y);
                if (startNodeClicked) {
                    if (!logic.isAnObstacle(xPos, yPos)) {
                        logic.setStartLocation(xPos, yPos);
                        repaint(xPos, yPos, logic.getGridSize(), logic.getGridSize());
                    }
                } else if (targetNodeClicked) {
                    if (!logic.isAnObstacle(xPos, yPos)) {
                        logic.setTargetLocation(xPos, yPos);
                        repaint(xPos, yPos, logic.getGridSize(), logic.getGridSize());
                    }
                }
            }
            public void mouseEntered(MouseEvent e) {
            }
            public void mouseExited(MouseEvent e) {
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!startNodeClicked && !targetNodeClicked) {
                    int xPos = getXAtRawCoordinate(e.getPoint().x);
                    int yPos = getYAtRawCoordinate(e.getPoint().y);
                    if ((xPos != logic.getStart().getX() || yPos != logic.getStart().getY()) &&
                        (xPos != logic.getTarget().getX() || yPos != logic.getTarget().getY())) {
                        logic.setObstaclePosition(xPos, yPos);
                        repaint(xPos, yPos, logic.getGridSize(), logic.getGridSize());
                    }
                }
            }
        });
    }

    /* Given some raw x or y coordinate (found at a given mouse position),
       return the x,y corner that would be at the top left corner of the
       square in the grid */
    private int getXAtRawCoordinate(int x) {
        return x / logic.getGridSize() * logic.getGridSize();
    }
    private int getYAtRawCoordinate(int y) {
        return y / logic.getGridSize() * logic.getGridSize();
    }

    // To set size of the panel, override setPreferredSize
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    public void runAlgorithm() {
        /* Create a SwingWorker thread and command the AStarLogic instance
           to start the A* algorithm, which computes the A* algo logic and
           repaints the panel throughout for visualization */
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                logic.executeAStar();
                return null;
            }
        };
        worker.execute(); // Start the thread
    }

    /* Tells the logic instance to reset its data structures and halt the algo */
    public void reset() {
        logic.reset();
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int gridSize = logic.getGridSize();

        /* Paint the background white */
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, panelWidth, panelHeight);

        /* Paint the starting node (cyan) */
        g.setColor(Color.cyan);
        g.fillRect(logic.getStart().getX(), logic.getStart().getY(), gridSize, gridSize);

        /* Paint the target node (orange) */
        g.setColor(Color.orange);
        g.fillRect(logic.getTarget().getX(), logic.getTarget().getY(), gridSize, gridSize);

        /* Paint obstacles */
        Iterator<Node> obstacleIterator = logic.getObstacleIterator();
        g.setColor(Color.BLACK);
        while (obstacleIterator.hasNext()) {
            Node tmp = obstacleIterator.next();
            g.fillRect(tmp.getX(), tmp.getY(), gridSize, gridSize);
        }

        // Only paint the progress of the algorithm if it is running!
        if (logic.isRunning()) {
            /* Paint the open nodes (Blue) */
            Iterator<Node> openIterator = logic.getOpenSitesIterator();
            g.setColor(Color.BLUE);
            while (openIterator.hasNext()) {
                Node tmp = openIterator.next();
                g.fillRect(tmp.getX(), tmp.getY(), gridSize, gridSize);
            }

            /* Paint the closed nodes  (Red) */
            Iterator<Node> closedIterator = logic.getClosedSitesIterator();
            g.setColor(Color.RED);
            while (closedIterator.hasNext()) {
                Node tmp = closedIterator.next();
                g.fillRect(tmp.getX(), tmp.getY(), gridSize, gridSize);
            }

            /* Paint the finished path */
            Iterator<Node> finishedIterator = logic.getOptimalPathIterator();
            g.setColor(Color.GREEN);
            while (finishedIterator.hasNext()) {
                Node tmp = finishedIterator.next();
                g.fillRect(tmp.getX(), tmp.getY(), gridSize, gridSize);
            }
        }

        /* Paint the underlying grid */
        g.setColor(Color.LIGHT_GRAY);
        for (int y = 0; y < panelHeight; y += gridSize) {
            for (int x = 0; x < panelWidth; x += gridSize) {
                g.drawRect(x, y, gridSize, gridSize);
            }
        }
    }
}

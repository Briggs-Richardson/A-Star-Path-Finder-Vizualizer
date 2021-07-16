/* Handles the logic of the A* algorithm. */

import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.util.Iterator;

public class AStarLogic {

    private final Grid graphicPanel;

    private final int gridSize;
    private final Node start;
    private final Node target;

    private final PriorityQueue<Node> openSites; // Store open nodes to consider
    private final Set<Node> closedSites;         // Store nodes already considered
    private final Set<Node> obstacles;           // Store nodes that are non-traversable
    private final Set<Node> optimalPathNodes;    // Store nodes in the path (start->end)

    private boolean running;

    public AStarLogic (Grid panel) {
        graphicPanel = panel;
        gridSize = 10; // side length of each square in grid

        /* Initialize the data structures */
        Comparator<Node> comparator = new NodeComparator();
        openSites = new PriorityQueue<>(comparator);
        closedSites = new HashSet<>();
        obstacles = new HashSet<>();
        optimalPathNodes = new HashSet<>();

        /* Initialize the start and target Nodes */
        start = new Node(null, 40, 500);
        target = new Node(null, 720,  20);

        running = false; // Start button sets to true, Reset sets to false
    }

    /* The graphicPanel can call setObstaclePosition when the user draws
       obstacles on the grid, the AStarLogic keeps track of the coordinates
       of the obstacles in the obstacles set */
    public void setObstaclePosition(int x, int y) {
        Node insertion = new Node(null, x, y);
        obstacles.add(insertion);
    }

    // Accessors for the graphic panel
    public int getGridSize() {
        return gridSize;
    }
    public Node getStart() {
        return start;
    }
    public Node getTarget() {
        return target;
    }
    public Iterator<Node> getOpenSitesIterator() {
        return openSites.iterator();
    }
    public Iterator<Node> getClosedSitesIterator() {
        return closedSites.iterator();
    }
    public Iterator<Node> getObstacleIterator() {
        return obstacles.iterator();
    }
    public Iterator<Node> getOptimalPathIterator() {
        return optimalPathNodes.iterator();
    }
    public boolean isRunning() {
        return running;
    }

    public boolean isAnObstacle(int x, int y) {
        Node test = new Node(null, x, y);
        return obstacles.contains(test);
    }

    /* Resets our data structures and sets running to false, effectively
       resetting the algorithm to a fresh start */
    public void reset() {
        openSites.clear();
        obstacles.clear();
        closedSites.clear();
        optimalPathNodes.clear();
        running = false;
    }

    /* Allows the user to drag the start/target nodes, here we set their new pos */
    public void setStartLocation(int x, int y) {
        int oldX = start.getX();
        int oldY = start.getY();
        start.setX(x);
        start.setY(y);
        graphicPanel.repaint(oldX, oldY, gridSize, gridSize);
    }
    public void setTargetLocation(int x, int y) {
        int oldX = target.getX();
        int oldY = target.getY();
        target.setX(x);
        target.setY(y);
        graphicPanel.repaint(oldX, oldY, gridSize, gridSize);
    }

    /* Given an x,y coordinate from the A* (executeAStar) function, drawSquare
       tells the graphicPanel to paint it after a Thread sleep */
    public void drawSquare(int x, int y) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
        }
        graphicPanel.repaint(x, y, gridSize, gridSize);
    }

    /* The A* Algorithm. Assumes known start/target positions, gridSize */
    public void executeAStar() {
        running = true;

        openSites.add(start); // First Node to consider is Start Node

        Node current;     // current -> reference to node currently considered
        Node neighbor;    // neighbor -> reference for current's surrounding Nodes

        /* Reusable reference to a Node whose coordinates will be set for checking
          if closedSites or obstacles contains a certain x,y marked Node */
        Node testNode = new Node(null, 0, 0);

        /* Loop that will keep iterating (searching) until one of two exit
           conditions are met or user chooses to reset (set running to false) */
        while (running) {
            current = openSites.poll(); // Consider the open node w/ lowest f_cost

            /* Exit Condition 1 : current is null -> No more nodes to consider */
            if (current == null) {
                System.out.println("Failure: Did not find path");
                break;
            }
            /* Exit condition 2 : current is target -> path found, we're done */
            if (current.equals(target)) {
                System.out.println("Success: Found optimal path!");
                setOptimalPath(current);
                break;
            }
            closedSites.add(current); // Add current to set of examined Nodes

            /* tell the graphics panel to paint the square current resides on */
            drawSquare(current.getX(), current.getY());

            /* Loop through current's surrounding Nodes
               via obtaining x,y coordinates from multiplying i,j and gridSize

               Note: as we consider the neighbor nodes, there are certain
               conditions that if met, make the neighbor illegible for
               consideration. We use the keyword "continue" which effectively
               skips to the next iteration of the loop (skipping the neighbor) */
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    /* This would consider the current node, so we skip */
                    if (i == 0 && j == 0)
                        continue;

                    // Calculate the x,y coordinates of the neighbor Node
                    int neighborX = current.getX() + (gridSize * i);
                    int neighborY = current.getY() + (gridSize * j);

                    // If the neighbor Node is out of bounds, skip
                    if (!inBounds(neighborX, neighborY)) {
                        continue;
                    }

                    /* Now we define the testNode w/ the neighbor X,Y
                      coordinates, to see if the neighbor node is in
                      closedSites (already been considered), or is a
                      obstacle */
                    testNode.setX(neighborX);
                    testNode.setY(neighborY);
                    if (closedSites.contains(testNode) ||
                        obstacles.contains(testNode)) {
                        continue;
                    }

                    /* Search openSites for the actual neighbor node
                       (comparing with testNode) if returned null, then the
                       neighbor node hasn't been considered prior, it should be
                       Otherwise, if the gCost from current is less than its
                       old gCost, then update it. */

                    Node actualNeighborNode = getActualNodeInOpenSites(testNode);
                    int gCostCurrentToNeighbor = current.getGCostFromNode(testNode);
                    if (actualNeighborNode == null) {
                        neighbor = new Node(current, neighborX, neighborY);
                        neighbor.setGCost(gCostCurrentToNeighbor);
                        neighbor.setHCost(target);
                        neighbor.setFCost();
                        openSites.add(neighbor);
                        drawSquare(neighborX, neighborY);
                    } else if (gCostCurrentToNeighbor < actualNeighborNode.getGCost()) {
                        openSites.remove(actualNeighborNode);
                        neighbor = new Node(current, neighborX, neighborY);
                        neighbor.setGCost(gCostCurrentToNeighbor);
                        neighbor.setHCost(target);
                        neighbor.setFCost();
                        openSites.add(neighbor);
                    }
                }
            }
        }
    }

    /* Helper function -> Determines if a coordinate is within bounds */
    private boolean inBounds(int x, int y) {
        return x >= 0 && x < graphicPanel.getWidth()
                && y >= 0 && y < graphicPanel.getHeight();
    }
    /* Helper function -> Determines if a given Node has already been considered,
       iterates through priority queue, returning given key node if found, or null */
    private Node getActualNodeInOpenSites(Node key) {
        if (key == null)
            return null;

        Iterator<Node> iterator = openSites.iterator();
        Node curr;
        while (iterator.hasNext()) {
            curr = iterator.next();
            if (curr.equals(key))
                return curr;
        }
        return null;
    }

    /* Once a path has been found, the setOptimalPath function executes, painting
       the path from the target node to the start node (backtracks Node parents)
     */
    private void setOptimalPath(Node curr) {
        if (curr == null)
            return;

        while (!curr.equals(start)) {
            optimalPathNodes.add(curr);
            curr = curr.getParent();
            graphicPanel.repaint(curr.getX(), curr.getY(), gridSize, gridSize);
        }
    }
}

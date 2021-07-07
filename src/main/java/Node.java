/*
     Node class -> Represents a square on the graphical grid
     which is created using the Grid class. The Node has properties
     such as the x,y position on the grid, who its parent is, and
     f,g,h costs. These are all properties used in the A* algorithm

     The Node class will be used to store open / closed sites on a graph
 */

package main.java;

public class Node {
    private int x, y;     // 2D coordinates on the Grid (Customized JPanel)
    private int gCost;    // Distance from starting Node
    private int hCost;    // Distance from target Node
    private int fCost;    // gCost + hCost (for value comparisons for best path)
    private final Node parent;  // A reference to the preceding Node in the current path

    /* Constructor -> Sets the position and parent of the node. The f,g,h costs
       will be defaulted to zero, and will need to be set later. */
    public Node(Node parentNode, int xPos, int yPos) {
        parent = parentNode;
        x = xPos;
        y = yPos;
        gCost = 0;
        hCost = 0;
        fCost = 0;
    }

    // Accessors
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getFCost() {
        return fCost;
    }
    public int getGCost() {
        return gCost;
    }
    public Node getParent() {
        return parent;
    }

    // Setters
    public void setX(int givenX) {
        x = givenX;
    }
    public void setY(int givenY) {
        y = givenY;
    }
    public void setGCost(int g) {
        gCost = g;
    }
    public void setFCost() { // Purpose: updating fCost with current g,h costs
        fCost = gCost + hCost;
    }

    // Given the target node, compute and set the hCost
    public void setHCost(Node target) {
        if (target != null) {
            hCost = (int) Math.round( Math.sqrt(Math.pow((target.getX() - x), 2.0)
                    + Math.pow((target.getY() - y), 2.0)));
        }
    }
    /* Given a Node, calculate the g_cost from Start to given Node to this Node */
    public int getGCostFromNode(Node inBetweenNode) {
        return gCost + distanceBetween(inBetweenNode);
    }


    private int distanceBetween(Node other) {
        int aSquared = (int)Math.pow(x - other.getX(), 2.0);
        int bSquared = (int)Math.pow(y - other.getY(), 2.0);
        return (int)Math.round(Math.sqrt(aSquared + bSquared));
    }

    /* Two Override functions, equals and hashCode so Nodes can be used in
       general use data structures */
    @Override
    public boolean equals(Object other) {
        // Only use for A* is comparing position
        if (other instanceof Node) {
            Node otherNode = (Node)other;
            if (otherNode.getX() == x && otherNode.getY() == y)
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        // hashcode generation from x,y coordinates found online
        // source: https://www.cs.upc.edu/~alvarez/calculabilitat/enumerabilitat.pdf
        int tmp = ( y +  ((x+1)/2));
        return x +  ( tmp * tmp);
    }
}

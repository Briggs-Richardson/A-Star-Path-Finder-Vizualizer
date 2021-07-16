/* Custom comparator for Nodes, for the A* algorithm we want to order/sort
  nodes by considering their f_cost values. Custom comparator for the
  priority queue data structure (min heap -> lowest f_cost on top) */

import java.util.Comparator;

public class NodeComparator implements Comparator<Node> {
    public int compare(Node a, Node b) {
        if (a == null || b == null)
            return 0;

        return Integer.compare(a.getFCost(), b.getFCost());
    }
}

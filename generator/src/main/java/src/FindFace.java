package src;
import java.util.ArrayList;
import java.util.List;
import routing.*;

/**
 * Created by Nichlas on 06-04-2015.
 */
public class FindFace {

    private int maxPathLength = 10;
    List<List<Node>> currentPaths = new ArrayList<>();
    private int face = -1;

    void FindFaces(Node node)
    {
        currentPaths.clear();
        List<Node> startList = new ArrayList<>();
        startList.add(node);
        FindPath(startList, node, node, node, 1);
        for (List<Node> printNodes : currentPaths)
        {
            face++;
            System.out.println("New path \n");
            for(Node printNode : printNodes)
            {
                printNode.setFace(face);
                System.out.println(printNode.toString() + "\n");
            }
        }
    }
    List<Node> FindNeighborNodes(Node startNode)
    {
        List<Node> Neighbors = new ArrayList<Node>();
        Edge startEdge = startNode.getFirstEdge();

        if (startEdge.getNode1() == startNode)
            Neighbors.add(startEdge.getNode2());
        else if (startEdge.getNode2() == startNode)
            Neighbors.add(startEdge.getNode1());
        else
            throw new IllegalStateException("Node has no neighbors");

        for (int i = 1; i < startNode.getNumOfEdges(); i++)
        {
            Edge currentEdge = startNode.getNextEdge();
            if (currentEdge.getNode1() == startNode)
                Neighbors.add(currentEdge.getNode2());
            else
                Neighbors.add(currentEdge.getNode1());
        }
        /* Print Neighbors
        System.out.println("Start Node:" + startNode.toString());
        for (Node node : Neighbors){
            System.out.println("Neighbor: " + node);
        }*/

        return Neighbors;
    }
    void FindPath(List<Node> pathNodes, Node currentNode, Node startNode, Node lastNode, int pathLength) {

        for (Node node : FindNeighborNodes(currentNode)) {
            if (currentNode == startNode) {
                pathNodes.add(node);
                FindPath(pathNodes, node, startNode, startNode, pathLength++);
            }
            else if (lastNode == startNode) {
                if (node != startNode) {
                    pathNodes.add(node);
                    FindPath(pathNodes, node, startNode, currentNode, pathLength++);
                }
            }
            else if (node == startNode) {
                pathNodes.add(node);
                currentPaths.add(pathNodes);
            }
            else if (pathLength < maxPathLength) {
                if (!pathNodes.contains(node)) {
                    pathNodes.add(node);
                    FindPath(pathNodes, node, startNode, currentNode, pathLength++);
                }
            }
        }
    }

    public FindFace() {

    }
}

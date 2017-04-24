package routing;
/**
 * Created by Ulf on 13/06/15.
 */
public class Pathway {
    private Building building;
    private Node node;

    public Pathway(Building building, Node node) {
        this.building = building;
        this.node = node;
    }

    public Building getBuilding() {
        return building;
    }

    public Node getNode() {
        return node;
    }
}

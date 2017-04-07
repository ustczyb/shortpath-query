package routing;
import java.awt.*;
import java.util.List;

/**
 * Created by Ulf on 13/06/15.
 */
public class Door {
    private int floor;
    private List<Room> rooms;
    private Point position;
    private int id;

    public Door(int id,int floor, List<Room> rooms, Point position) {
        this.floor = floor;
        this.rooms = rooms;
        this.position = position;
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public int getFloor() {
        return floor;
    }
    public List<Room> getRooms() {
        return rooms;
    }

    public Point getPosition() {
        return position;
    }
}

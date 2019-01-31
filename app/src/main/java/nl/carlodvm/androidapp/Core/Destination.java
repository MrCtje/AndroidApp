package nl.carlodvm.androidapp.Core;

public class Destination extends Grid {
    private String name, comment;
    private int imageIndex;
    private Facing direction;

    public enum Facing {NORTH, EAST, SOUTH, WEST}

    ;

    public Destination(int x, int y, String name, int imageIndex, String comment, String dir) {
        super(x, y, true);
        this.name = name;
        this.imageIndex = imageIndex;
        this.comment = comment;
        switch (dir) {
            case "N":
                direction = Facing.NORTH;
                break;
            case "E":
                direction = Facing.EAST;
                break;
            case "S":
                direction = Facing.SOUTH;
                break;
            case "W":
                direction = Facing.WEST;
                break;

            default:
                direction = Facing.SOUTH;
        }
    }

    public Facing getDirection() {
        return direction;
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public String getComment() {return comment;}

    @Override
    public String toString() {
        return name;
    }
}

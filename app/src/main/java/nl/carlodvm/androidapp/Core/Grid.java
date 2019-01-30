package nl.carlodvm.androidapp.Core;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Grid {
    private int y;
    private int x;
    private boolean passable;
    //In meters
    public static float GridResolution = 0.595f;

    public Grid(int x, int y, boolean passable) {
        this.x = x;
        this.y = y;
        this.passable = passable;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public void setPassable(boolean passable) {
        this.passable = passable;
    }

    public boolean isPassable() {
        return passable;
    }

    public List<Pair<Integer, Integer>> getDirections() {
        List<Pair<Integer, Integer>> directions = new ArrayList<>();
        //LEFT
        directions.add(Pair.create(x - 1, y));
        //RIGHT
        directions.add(Pair.create(x + 1, y));
        //UP
        directions.add(Pair.create(x, y - 1));
        //Down
        directions.add(Pair.create(x, y + 1));
        return directions;
    }

    @Override
    public String toString() {
        return "(" + (x + 1) + "," + (y + 1) + ")";
    }
}

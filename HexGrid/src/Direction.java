/*
 * The Direction class is just a bunch of static varubles.
 * This is so you can say something like (d==Direction.up)
 */

/**
 *
 * @author joeyn
 */
public class Direction {
    private double angle;
    private int code;
    private Direction (double a, int c) {
        angle = a;
        code = c;
    }
    public double getAngle () {
        return angle;
    }
    public int getCode () {
        return code;
    }
    public static Direction fromCode (int code) {
        switch (code) {
            case 0:
                return up;
            case 1:
                return upRight;
            case 2:
                return downRight;
            case 3:
                return down;
            case 4:
                return downLeft;
            case 5:
                return upLeft;
            default:
                return null;
        }
    }
    public static final Direction up = new Direction(Math.PI*1.5,0);
    public static final Direction upRight = new Direction(Math.PI*(11/6.0),1);
    public static final Direction downRight = new Direction(Math.PI*(1/6.0),2);
    public static final Direction down = new Direction(Math.PI*0.5,3);
    public static final Direction downLeft = new Direction(Math.PI*(5/6.0),4);
    public static final Direction upLeft = new Direction(Math.PI*(7/6.0),5);
}

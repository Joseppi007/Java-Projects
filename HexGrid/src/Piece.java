
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

/*
 * A piece moves around on a HexGrid. It can move Up, Down, LeftUp, LeftDown, RightUp, and RightDown.
 * A piece can also look at the Hex it is on, or any of the Hexs it can move to.
 */

/**
 *
 * @author joeyn
 */
public class Piece extends GridThing {
    private HexGrid world; // The environment the Piece is in
    private int posX; // The position
    private int posY;
    private double size; // 1 means 100% of the size of the Hex it is on - 0.5 means 50% - ect
    
    // This function will trigger when this Piece tries to move on to a Hex from Direction from
    public boolean canMoveTo(Tuple<Direction, Hex> from, Piece self) {return canMoveToVar.func(from, self);}
    public void setCanMoveTo(AsyncFunc<Tuple<Direction, Hex>, Piece, Boolean> func) {canMoveToVar=func;}
    protected AsyncFunc<Tuple<Direction, Hex>, Piece, Boolean> canMoveToVar;
    
    // This function will trigger when clicked on.
    public void onClick(Tuple<MouseEvent, Direction> t, Piece self) {onClickVar.func(t, self);}
    public void setOnClick(AsyncFunc<Tuple<MouseEvent, Direction>, Piece, Void> func) {onClickVar=func;}
    protected AsyncFunc<Tuple<MouseEvent, Direction>, Piece, Void> onClickVar;
    
    // This function will trigger when hovered over.
    public void onHover(Tuple<Tuple<MouseEvent,Graphics>, Tuple<Direction,HexGrid>> t, Piece self) {if (onHoverVar!=null) {onHoverVar.func(t, self);}}
    public void setOnHover(AsyncFunc<Tuple<Tuple<MouseEvent,Graphics>, Tuple<Direction,HexGrid>>, Piece, Void> func) {onHoverVar=func;}
    protected AsyncFunc<Tuple<Tuple<MouseEvent,Graphics>, Tuple<Direction,HexGrid>>, Piece, Void> onHoverVar;
    
    public Piece (HexGrid w, int x, int y) { // Add piece to world
        this(w,x,y,Color.black,0.1,Color.white,0.5);
    }
    public Piece (HexGrid w, int x, int y, Color theOutline, double theStrokeWeight, Color theFill, double theSize) {
        super(theOutline,theStrokeWeight,theFill);
        world=w;
        posX=x;
        posY=y;
        size=theSize;
        canMoveToVar=(Tuple<Direction, Hex> e, Piece self) -> true;
    }

    /**
     *
     * @param d - The direction to move in
     * @return - Tile to land on (j,i)
     */
    public Tuple<Integer, Integer> positionIfMove (Direction d) {
        int j, i; // Find the position you are trying to get to
        if (posX%2==0) { // High
            if (d==Direction.up) {
                i=posX;
                j=posY-1;
            } else if (d==Direction.upRight) {
                i=posX+1;
                j=posY-1;
            } else if (d==Direction.downRight) {
                i=posX+1;
                j=posY;
            } else if (d==Direction.down) {
                i=posX;
                j=posY+1;
            } else if (d==Direction.downLeft) {
                i=posX-1;
                j=posY;
            } else {
                i=posX-1;
                j=posY-1;
            }
        } else { // Low
            if (d==Direction.up) {
                i=posX;
                j=posY-1;
            } else if (d==Direction.upRight) {
                i=posX+1;
                j=posY;
            } else if (d==Direction.downRight) {
                i=posX+1;
                j=posY+1;
            } else if (d==Direction.down) {
                i=posX;
                j=posY+1;
            } else if (d==Direction.downLeft) {
                i=posX-1;
                j=posY+1;
            } else {
                i=posX-1;
                j=posY;
            }
        }
        return new Tuple<Integer, Integer> (j,i);
    }
    /**
     *
     * @param d - The direction to move in
     * @return - Did it move?
     */
    public boolean move (Direction d) {
        int j, i; // Find the position you are trying to get to
        j = positionIfMove(d).getFirst();
        i = positionIfMove(d).getSecond();
        if (world.getHex(j, i) == null) {
            return false;
        }
        if (canMoveToVar == null) {
            posX=i;
            posY=j;
            return false;
        }
        if (canMoveToVar.func(new Tuple(d,world.getHex(j, i)),this)) { // Can we go there?
            Tuple<Direction, Piece> pair = new Tuple<Direction, Piece>(d,this);
            world.getHex(posY, posX).onMoveFrom(pair, world.getHex(posY, posX));
            world.getHex(j, i).onMoveTo(pair, world.getHex(j, i));
            posX=i;
            posY=j;
            return true;
        }
        return false;
    }
    @Override
    public Piece copy() { // Make a copy
        Piece ret = new Piece(world, posX, posY, outline, strokeWeight, fill, size);
        ret.onClickVar=onClickVar;
        ret.canMoveToVar=canMoveToVar;
        return ret;
    }
    public int getPosX () {
        return posX;
    }
    public int getPosY () {
        return posY;
    }
    public void setPosX (int x) {
        posX = x;
    }
    public void setPosY (int y) {
        posY = y;
    }
    public HexGrid getWorld () {
        return world;
    }
    public double getSize () {
        return size;
    }
    public void setSize (double s) {
        size = s;
    }
}

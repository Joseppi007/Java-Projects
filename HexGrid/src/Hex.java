import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

/*
 * A Hex is a hexagonal tile of a HexGrid. The HexGrid is tiled so that adjacent Hexs are above, below, to the right and up, to the right and down, to the left and up, and to the left and down.
 * A Hex object has color information, a few functions, and flags.
 *
 * Pieces are a subclass of Hex becuase they have the same properties.
 */

/**
 *
 * @author joeyn
 */

public class Hex extends GridThing {
    // This function will trigger when a Piece moves on to this Hex from Direction from
    public void onMoveTo(Tuple<Direction, Piece> from, Hex self) {if (onMoveToVar!=null) {onMoveToVar.func(from, self);}}
    public void setOnMoveTo(AsyncFunc<Tuple<Direction, Piece>, Hex, Void> func) {onMoveToVar=func;}
    protected AsyncFunc<Tuple<Direction, Piece>, Hex, Void> onMoveToVar;
    // This function will trigger when a Piece moves from this Hex in Direction to
    public void onMoveFrom(Tuple<Direction, Piece> to, Hex self) {if (onMoveFromVar!=null) {onMoveFromVar.func(to, self);}}
    public void setOnMoveFrom(AsyncFunc<Tuple<Direction, Piece>, Hex, Void> func) {onMoveFromVar=func;}
    protected AsyncFunc<Tuple<Direction, Piece>, Hex, Void> onMoveFromVar;
    // This function will trigger when clicked on.
    public void onClick(Tuple<MouseEvent, Direction> t, Hex self) {if (onClickVar!=null) {onClickVar.func(t, self);}}
    public void setOnClick(AsyncFunc<Tuple<MouseEvent, Direction>, Hex, Void> func) {onClickVar=func;}
    protected AsyncFunc<Tuple<MouseEvent, Direction>, Hex, Void> onClickVar;
    // This function will trigger when hovered over.
    public void onHover(Tuple<Tuple<MouseEvent,Graphics>, Tuple<Direction,HexGrid>> t, Hex self) {if (onHoverVar!=null) {onHoverVar.func(t, self);}}
    public void setOnHover(AsyncFunc<Tuple<Tuple<MouseEvent,Graphics>, Tuple<Direction,HexGrid>>, Hex, Void> func) {onHoverVar=func;}
    protected AsyncFunc<Tuple<Tuple<MouseEvent,Graphics>, Tuple<Direction,HexGrid>>, Hex, Void> onHoverVar;
    //some basic constructors
    Hex(Color theOutline, double theStrokeWeight, Color theFill){
        super(theOutline,theStrokeWeight,theFill);
    }
    //Default - Black outline and White fill
    Hex(){
        super();
    }
    @Override
    public Hex copy() { // Make a copy
        Hex ret = new Hex(outline, strokeWeight, fill);
        ret.onClickVar=onClickVar;
        ret.onMoveToVar=onMoveToVar;
        ret.onMoveFromVar=onMoveFromVar;
        return ret;
    }
}

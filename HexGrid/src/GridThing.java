
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

/*
 * A grid thing is a part of a grid, like a Piece or Hex.
 */

/**
 *
 * @author joeyn
 */
public class GridThing {
    protected Color outline;
    protected double strokeWeight; // The persentage of the hex's size that is edge
    protected Color fill;
    protected LinkedList<String> flags; // Custom flags can be added to test against - a "wall" tag may prevent movement to
    // All of the following need little explanation, so here you go!
    public Color  getOutline() {return outline;}
    public double getStrokeWeight() {return strokeWeight;}
    public Color  getFill() {return fill;}
    public void   setOutline(Color setTo) {outline=setTo;}
    public void   setStrokeWeight(double setTo) {strokeWeight=setTo;}
    public void   setFill(Color setTo) {fill=setTo;}
    // flags
    public boolean hasFlag(String flagName) { // test for a flag
        return flags.contains(flagName);
    }
    public boolean addFlag(String flagName) { // add a flag, but only if it is not present - return true if added, and false if it was already there
        if (hasFlag(flagName)) {
            return false;
        }else{
            flags.add(flagName);
            return true;
        }
    }
    public boolean removeFlag(String flagName) { // remove a flag, but only if it is present - return true if removed, and false if it was already absent
        return flags.remove(flagName);
    }
    public void toggleFlag(String flagName) { // toggle a flag
        if (hasFlag(flagName)) {
            removeFlag(flagName);
        }else{
            flags.add(flagName);
        }
    }
    //some basic constructors
    GridThing(Color theOutline, double theStrokeWeight, Color theFill){
        outline=theOutline;
        strokeWeight=theStrokeWeight;
        fill=theFill;
    }
    //Default - Black outline and White fill
    GridThing(){
        this(Color.black,0.1,Color.white);
    }
    public GridThing copy() { // Make a copy
        GridThing ret = new GridThing(outline, strokeWeight, fill);
        return ret;
    }
}

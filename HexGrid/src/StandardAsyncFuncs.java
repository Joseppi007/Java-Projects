import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

/*
 * These are a few AsyncFuncs that are nice to have avalible without having to re-make them.
 */

/**
 *
 * @author joeyn
 * @see AsyncFunc
 */
public class StandardAsyncFuncs {
    /**
     * This AsyncFunc is for Pieces that you want to be able to move freely.
     * It will move the piece in the direction of the side clicked on.
     */
    public static AsyncFunc<Tuple<MouseEvent,Direction>,Piece,Void> moveOnClick = new AsyncFunc<Tuple<MouseEvent, Direction>, Piece, Void> () {
        @Override
        public Void func(Tuple<MouseEvent, Direction> t, Piece self) {
            self.move(t.getSecond());
            return null;
        }
    };
    
    /**
     * This AsyncFunc is for Pieces that you want to be able to move to adjacent similar colors.
     * It will move the piece in the direction of the side clicked on.
     */
    public static AsyncFunc<Tuple<MouseEvent,Direction>,Piece,Void> moveOnClickIfSameColor = new AsyncFunc<Tuple<MouseEvent, Direction>, Piece, Void> () {
        @Override
        public Void func(Tuple<MouseEvent, Direction> t, Piece self) {
            Color c1 = self.getWorld().getHex(self.getPosY(), self.getPosX()).getFill();
            Tuple<Integer, Integer> positionTwo = self.positionIfMove(t.getSecond());
            Color c2 = self.getWorld().getHex(positionTwo.getFirst(), positionTwo.getSecond()).getFill();
            if (c1.equals(c2)) {
                self.move(t.getSecond());
            }
            return null;
        }
    };
    
    /**
     * This AsyncFunc is for Pieces that you want to be able to move to slide in a direction on similar colors, stopping before a different fill.
     * It will move the piece in the direction of the side clicked on.
     */
    public static AsyncFunc<Tuple<MouseEvent,Direction>,Piece,Void> slideOnClickIfSameColor = new AsyncFunc<Tuple<MouseEvent, Direction>, Piece, Void> () {
        @Override
        public Void func(Tuple<MouseEvent, Direction> t, Piece self) {
            while (true) {
                Color c1 = self.getWorld().getHex(self.getPosY(), self.getPosX()).getFill();
                Tuple<Integer, Integer> positionTwo = self.positionIfMove(t.getSecond());
                Color c2 = self.getWorld().getHex(positionTwo.getFirst(), positionTwo.getSecond()).getFill();
                if (c1.equals(c2)) {
                    if (!self.move(t.getSecond())) {
                        break;
                    }
                }else{
                    break;
                }
            }
            return null;
        }
    };
    
    /**
     * This AsyncFunc is for Pieces that you want to be able to move freely.
     * It will move the piece in the direction of the side clicked on.
     */
    public static AsyncFunc<Tuple<Tuple<MouseEvent,Graphics>, Tuple<Direction,HexGrid>>, Piece, Void> pointOnHover = new AsyncFunc<Tuple<Tuple<MouseEvent,Graphics>, Tuple<Direction,HexGrid>>, Piece, Void> () {
        @Override
        public Void func(Tuple<Tuple<MouseEvent, Graphics>, Tuple<Direction, HexGrid>> t, Piece self) {
            Graphics g = t.getFirst().getSecond();
            Direction d = t.getSecond().getFirst();
            HexGrid hg = t.getSecond().getSecond();
            MouseEvent e = t.getFirst().getFirst();
            Tuple<Double, Double> displayCords = hg.gridToDisplayCords(hg.mouseToGrid(e));
            
            g.setColor(new Color(127,127,127,200));
            double[] x_points_unrotated = new double[3];
            double[] y_points_unrotated = new double[3];
            x_points_unrotated[0] = hg.getHexHeight()*hg.getZoom()*0.65;
            y_points_unrotated[0] = 0;
            x_points_unrotated[1] = hg.getHexHeight()*hg.getZoom()*0.35;
            y_points_unrotated[1] = hg.getHexWidth()*hg.getZoom()*0.15;
            x_points_unrotated[2] = hg.getHexHeight()*hg.getZoom()*0.35;
            y_points_unrotated[2] = -hg.getHexWidth()*hg.getZoom()*0.15;
            int[] x_points = new int[3];
            int[] y_points = new int[3];
            for (int i = 0; i < 3; i++) {
                x_points[i] = (int)(displayCords.getFirst()+(Math.cos(d.getAngle())*x_points_unrotated[i]-Math.sin(d.getAngle())*y_points_unrotated[i]));
                y_points[i] = (int)(displayCords.getSecond()+(Math.sin(d.getAngle())*x_points_unrotated[i]+Math.cos(d.getAngle())*y_points_unrotated[i]));
            }
            g.fillPolygon(x_points, y_points, 3);
            
            return null;
        }
    };
    
    /**
     * This AsyncFunc is for Hexs.
     * It will move the Hex change colors when you click on it - swapping the fill and outline.
     */
    public static AsyncFunc<Tuple<MouseEvent,Direction>,Hex,Void> swapColorsOnClick = new AsyncFunc <Tuple<MouseEvent,Direction>,Hex,Void> () {
        @Override
        public Void func(Tuple<MouseEvent, Direction> t, Hex self) {
            //self.setStrokeWeight(1-self.getStrokeWeight());
            Color temporary = self.getOutline();
            self.setOutline(self.getFill());
            self.setFill(temporary);
            return(null);
        }
    };
}

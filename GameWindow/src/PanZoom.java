import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * PanZoom handles panning and zooming.
 * @author joeyn
 */
public class PanZoom {
    private double zoom;
    private double panX;
    private double panY;
    
    private double minPanX = Double.NEGATIVE_INFINITY; // Acts like a wall more than a min value
    private double minPanY = Double.NEGATIVE_INFINITY; // Acts like a ceiling more than a min value
    private double maxPanX = Double.POSITIVE_INFINITY; // Acts like a wall more than a max value
    private double maxPanY = Double.POSITIVE_INFINITY; // Acts like a floor more than a max value
    private double minZoom = 0;                        // This one is actually a min value
    private double maxZoom = Double.POSITIVE_INFINITY; // This one is actually a max value
    
    private double ratio; // The width of the game vs the height
                          // This is so a streched out window doesn't strech out the game.
    
    public PanZoom (double theZoom, double thePanX, double thePanY, double theRatio) {
        zoom = theZoom;
        panX = thePanX;
        panY = thePanY;
        ratio = theRatio;
    }
    
    public PanZoom (double theZoom, double thePanX, double thePanY) {
        this(theZoom,thePanX,thePanY,16.0/9.0);
    }

    /**
     * Maps a position to screen coordinates for drawing
     * @param x A position's x coordinate (0 = left, 1 = right)
     * @param width the screen width
     * @param height the screen height
     * @return screen coordinate x value
     */
    public int mapX (double x, int width, int height) {
        double ret = ((x+panX)*width*zoom); // Set return value to a mapped x value
        if (((double)width)/height>ratio) {
            ret *= (height*ratio)/width;
            ret += ( width - (height*ratio) ) / 2;
        }
        return (int)ret;
    }
    /**
     * Maps a position to screen coordinates for drawing
     * @param y A position's y coordinate (0 = top, 1 = bottom)
     * @return screen coordinate y value
     */
    public int mapY (double y, int width, int height) {
        double ret = ((y+panY)*height*zoom); // Set return value to a mapped x value
        if (((double)width)/height<ratio) {
            ret *= (width/ratio)/height;
            ret += ( height - (width/ratio) ) / 2;
        }
        return (int)ret;
    }
    /**
     * Maps the width for borders
     * @param width the screen width
     * @param height the screen height
     * @return screen coordinate x value
     */
    public double mapWidth (int width, int height) {
        if (((double)width)/height>ratio) {
            return (height*ratio);
        }
        return width;
    }
    /**
     * Maps the height for borders
     * @param width the screen width
     * @param height the screen height
     * @return screen coordinate x value
     */
    public double mapHeight (int width, int height) {
        if (((double)width)/height<ratio) {
            return (width/ratio);
        }
        return height;
    }
    /**
     * Maps screen coordinates to a position to for mouse clicks
     * @param x A position's x coordinate 
     * @return (0 = left, 1 = right)
     */
    public double unmapX (double x, int width, int height) {
        double ret = x; // Set return value to an un-mapped x value
        if (((double)width)/height>ratio) {
            ret -= ( width - (height*ratio) ) / 2;
            ret /= (height*ratio)/width;
        }
        ret = ((ret/zoom/width)-panX);
        return ret;
    }
    /**
     * Maps screen coordinates to a position to for mouse clicks
     * @param y A position's y coordinate
     * @return (0 = top, 1 = bottom)
     */
    public double unmapY (double y, int width, int height) {
        double ret = y; // Set return value to an un-mapped x value
        if (((double)width)/height<ratio) {
            ret -= ( height - (width/ratio) ) / 2;
            ret /= (width/ratio)/height;
        }
        ret = ((ret/height)-panY*zoom)/zoom;
        return ret;
    }
    /**
     * @return this.panX
     */
    public double getPanX () {
        return panX;
    }
    /**
     * @return this.panY
     */
    public double getPanY () {
        return panY;
    }
    /**
     * @return this.zoom
     */
    public double getZoom () {
        return zoom;
    }
    /**
     * @return this.ratio
     */
    public double getRatio () {
        return ratio;
    }
    /**
     * @param newPanX Set this.panX to newPanX, but keeps it in bounds.
     * @param width The width of the game window - required for math purposes
     */
    public void setPanX (double newPanX, int width) {
        panX=Math.max(Math.min(newPanX, maxPanX), minPanX+1/zoom);
    }
    /**
     * @param newPanY Set this.panY to newPanY, but keeps it in bounds.
     * @param height The height of the game window - required for math purposes
     */
    public void setPanY (double newPanY, int height) {
        panY=Math.max(Math.min(newPanY, maxPanY), minPanY+1/zoom);
    }
    /**
     * @param newZoom Set this.zoom to newZoom, but keeps it in bounds.
     * @param width The width of the game window - required for math purposes
     * @param height The height of the game window - required for math purposes
     */
    public void setZoom (double newZoom, int width, int height) {
        zoom=Math.max(Math.min(newZoom, maxZoom), minZoom);
        setPanX(panX,width);
        setPanY(panY,height);
    }

    /**
     * Sets a new ratio
     * @param theRatio The new ratio
     */
    public void setRatio (double theRatio) {
        ratio = theRatio;
    }
    /**
     * Sets the bounds of how far one can zoom in/out and pan.
     * @param minX What the minPanX will be set to.
     * @param minY What the minPanY will be set to.
     * @param maxX What the maxPanX will be set to.
     * @param maxY What the maxPanY will be set to.
     * @param minZ What the minZoom will be set to.
     * @param maxZ What the maxZoom will be set to.
     */
    public void setBounds (double minX, double minY, double maxX, double maxY, double minZ, double maxZ) {
        minPanX = minX;
        minPanY = minY;
        maxPanX = maxX;
        maxPanY = maxY;
        minZoom = minZ;
        maxZoom = maxZ;
    }
    /**
     * Turns mouse scrolling into zooming
     * @param e The mouse scrolling
     * @param width The width of the game window - required for math purposes
     * @param height The height of the game window - required for math purposes
     */
    public void doZoom (MouseWheelEvent e, int width, int height) {
        double scale_ammount = Math.pow(1.05, e.getPreciseWheelRotation());
        double previousZoom = zoom;
        double previousUnmappedX = unmapX(e.getX(),width,height);
        double previousUnmappedY = unmapY(e.getY(),width,height);
        setZoom(zoom*scale_ammount,width,height);
        double deltaPanX = (unmapX(e.getX(),width,height)-previousUnmappedX);
        double deltaPanY = (unmapY(e.getY(),width,height)-previousUnmappedY);
        //System.out.println("Delta Pan : ("+deltaPanX+", "+deltaPanY+")");
        setPanX(panX+deltaPanX,width);
        setPanY(panY+deltaPanY,height);
    }
    /**
     * Turns mouse moving into panning
     * @param e The mouse now
     * @param last The mouse one frame ago
     * @param width The width of the game window - required for math purposes
     * @param height The height of the game window - required for math purposes
     */
    public void doPan (MouseEvent e, MouseEvent last, int width, int height) {
        setPanX(panX+(e.getX()-last.getX()+0.0)/mapWidth(width,height)/zoom,width);
        setPanY(panY+(e.getY()-last.getY()+0.0)/mapHeight(width,height)/zoom,height);
    }
    /**
     * Draws in the boarders to keep the ratio
     * @param g The Graphics object
     * @param width The frame's width
     * @param height The frame's height
     * @param c The color of the boarder
     */
    public void paintBoarders (Graphics g, int width, int height, Color c) {
        g.setColor(c);
        if (((double)width)/height>ratio) {
            int w = (int)((width-mapWidth(width,height))/2); // Sidebar width
            g.fillRect(0,0,w,height);
            g.fillRect(width-w,0,w,height);
        } else if (((double)width)/height<ratio) {
            int h = (int)((height-mapHeight(width,height))/2); // Sidebar height
            g.fillRect(0,0,width,h);
            g.fillRect(0,height-h,width,h);
        }
    }
    /**
     * Draws in the boarders to keep the ratio
     * @param g The Graphics object
     * @param width The frame's width
     * @param height The frame's height
     */
    public void paintBoarders (Graphics g, int width, int height) {
        paintBoarders(g,width,height,new Color(0,0,0));
    }
    /**
     * Makes it so the paint stays in the inner ratio thing.
     * @param g The Graphics object
     * @param width The frame's width
     * @param height The frame's height
     */
    public void setClipping (Graphics g, int width, int height) {
        if (((double)width)/height>ratio) {
            int w = (int)((width-mapWidth(width,height))/2); // Sidebar width
            g.setClip(w, 0, width-w*2, height);
        } else if (((double)width)/height<ratio) {
            int h = (int)((height-mapHeight(width,height))/2); // Sidebar height
            g.setClip(0, h, width, height-h*2);
        }
    }
}

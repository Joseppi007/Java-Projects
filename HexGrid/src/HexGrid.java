import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * A HexGrid is a 2D array of Hex objects (I'd reccomend children of Hex objects, if you want more functionality) that can be
 * panned with mouse drags, and can zoom with the scroll whell (or functions). There can also be Pieces that move around as if this were a game board.
 * To learn more about them, look at that class.
 */

/**
 *
 * @author joeyn
 */
public class HexGrid extends JFrame{
    private Hex[][] grid; // Columns alternate between "high" and "low", beginning on high.
    /*
      ###     ###
     #   #   #   #
    #high ###     #
     #   #   #   #
      ### low ###
         #   #
          ###
    */
    private LinkedList<Piece> pieces; // All of the pieces on the grid
    
    private int panX; // The number of pixels panned to the Right
    private int panY; // The number of pixels panned Down
    private double zoom; // The factor zoomed in or out (0.25 means zoom in %400)
    
    private int hexWidth; // The average width of the hexagon (the average of the top length and middle length)
    private int hexHeight; // The height of the hexagon
    private int hexSide; // The ammount of variation between the top length and middle length
    
    private MouseEvent previousMouseEvent; // For panning
    
    private AsyncFunc<KeyEvent,HexGrid,Void> onKeyPress; // Function to run on key press
    
    public HexGrid(int width, int height){this(width,height,100,100,25);}//Default sizes
    public HexGrid(int width, int height, int hexagonWidth, int hexagonHeight, int hexagonSide) {
        super("");
        
        hexWidth=hexagonWidth;
        hexHeight=hexagonHeight;
        hexSide=hexagonSide;
        panX=0;
        panY=0;
        zoom=1.0;
        grid=new Hex[height][width];
        pieces=new LinkedList<Piece>();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        add(new JP(this));
        
        setSize(600, 375);
        this.setVisible(true);
    }
    /*
    * Balance against the zooming and paning
    */
    public Tuple<Double, Double> scaleCords(MouseEvent e) {
        return new Tuple((e.getX()/zoom)-panX/zoom,(e.getY()/zoom)-panY/zoom);
    }
    /*
    * Mouse event to what Hex is clicked
    */
    public Tuple<Integer, Integer> mouseToGrid(MouseEvent e) {
        double scaled_x = scaleCords(e).getFirst(); // So that pans and zooms are canceled out
        double scaled_y = scaleCords(e).getSecond();
        int selected_position_x; // -1 is out of bounds
        int selected_position_y; // The index of the clicked Hex
        if (Math.abs((((scaled_x+hexWidth/2)%hexWidth+hexWidth)%hexWidth)-hexWidth/2)<hexWidth/2-hexSide/2) { // Is the click in the square part in the middle of a Hexagon?
            selected_position_x=(int)Math.floor((scaled_x+hexWidth/2)/hexWidth); // Set the x position
        }else{ // Weird triangles on the end! Oh, no!
            selected_position_x=(int)Math.floor((scaled_x+hexWidth/2+(((scaled_x%(hexWidth*2)+(hexWidth*2))%(hexWidth*2)>hexWidth)?-1:1)*((hexSide/2)-((0.5-Math.abs(((scaled_y%hexHeight+hexHeight)%hexHeight)/hexHeight-0.5))*hexSide*2)))/hexWidth); // Set the x position, but it's weird math now!
        }
        selected_position_y=(int)Math.floor(scaled_y/hexHeight+(((selected_position_x%2+2)%2==0)?1.0:0.5));
        return new Tuple(selected_position_x,selected_position_y);
    }
    /*
    * Get the display cords of the center of a hex
    */
    public Tuple<Double, Double> gridToDisplayCords(Tuple<Integer, Integer> t) {
        double scaled_x = t.getFirst()*hexWidth*zoom+panX;
        double scaled_y = (t.getSecond()-((t.getFirst()%2==0)?0.5:0.0))*hexHeight*zoom+panY;
        return new Tuple(scaled_x,scaled_y);
    }
    /*
    * The offset from the middle
    */
    public Tuple<Double, Double> mouseGridOffset (MouseEvent e) {
        return new Tuple(scaleCords(e).getFirst()-(mouseToGrid(e).getFirst()*hexWidth),scaleCords(e).getSecond()-((mouseToGrid(e).getSecond()+((mouseToGrid(e).getFirst()%2==0)?0.0:0.5))*hexHeight)+(hexHeight/2));
    }
    /*
    * The offset from the middle
    */
    public Direction mouseToDir (MouseEvent e) {
        Direction part = null;
        double displacement_from_center_x = mouseGridOffset(e).getFirst();
        double displacement_from_center_y = mouseGridOffset(e).getSecond();
        if (displacement_from_center_y<0) {
            //Upper
            if (displacement_from_center_y+(hexHeight*displacement_from_center_x/(hexWidth-hexSide))<0) {
                //Up-Lefter
                if (displacement_from_center_y-(hexHeight*displacement_from_center_x/(hexWidth-hexSide))<0) {
                    //Up-Righter
                    part = Direction.up;
                }else{
                    //Low-Lefter
                    part = Direction.upLeft;
                }
            }else{
                //Low-Righter
                part = Direction.upRight;
            }
        }else{
            //Lower
            if (displacement_from_center_y+(hexHeight*displacement_from_center_x/(hexWidth-hexSide))<0) {
                //Up-Lefter
                part = Direction.downLeft;
            }else{
                //Low-Righter
                if (displacement_from_center_y-(hexHeight*displacement_from_center_x/(hexWidth-hexSide))<0) {
                    //Up-Righter
                    part = Direction.downRight;
                }else{
                    //Low-Lefter
                    part = Direction.down;
                }
            }
        }
        return part;
    }
    protected class JP extends JPanel {
        HexGrid hg;
        public JP(HexGrid hexGrid) {
            hg=hexGrid;
            addMouseListener(new java.awt.event.MouseAdapter() {
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    double scaled_x = scaleCords(e).getFirst(); // So that pans and zooms are canceled out
                    double scaled_y = scaleCords(e).getSecond();
                    int selected_position_x = mouseToGrid(e).getFirst(); // -1 is out of bounds
                    int selected_position_y = mouseToGrid(e).getSecond(); // The index of the clicked Hex
                    double displacement_from_center_x = mouseGridOffset(e).getFirst();
                    double displacement_from_center_y = mouseGridOffset(e).getSecond();
                    
                    // We know the cords, but which *piece* of the GridThing did we click on? The upper part? The lower?
                    Direction part = mouseToDir(e);
                    
                    // Now that we know where we clicked, let's run the necicary code!
                    boolean noPieceOnThisHex = true;
                    for (int i = pieces.size()-1; i > -1; i--) { // Prioritize Pieces
                        if (pieces.get(i).getPosX()==selected_position_x && pieces.get(i).getPosY()==selected_position_y) {
                            if (
                                (displacement_from_center_y>-hexHeight*pieces.get(i).getSize()*0.5)&&//Below Top Line
                                (displacement_from_center_y<hexHeight*pieces.get(i).getSize()*0.5)&&//Above Bottom Line
                                (displacement_from_center_y-(hexHeight*pieces.get(i).getSize()/2)>hexHeight/2/hexSide*(displacement_from_center_x-(hexWidth-(hexSide/2))*pieces.get(i).getSize()))&&//Below Top Right Line
                                (displacement_from_center_y+(hexHeight*pieces.get(i).getSize()/2)<-hexHeight/2/hexSide*(displacement_from_center_x-(hexWidth-(hexSide/2))*pieces.get(i).getSize()))&&//Above Bottom Right Line
                                (displacement_from_center_y-(hexHeight*pieces.get(i).getSize()/2)>-hexHeight/2/hexSide*(displacement_from_center_x+(hexWidth-(hexSide/2))*pieces.get(i).getSize()))&&//Below Top Left Line
                                (displacement_from_center_y+(hexHeight*pieces.get(i).getSize()/2)<hexHeight/2/hexSide*(displacement_from_center_x+(hexWidth-(hexSide/2))*pieces.get(i).getSize()))//Above Bottom Left Line
                            ) {
                                noPieceOnThisHex = false;
                                pieces.get(i).onClick(new Tuple(e,part), pieces.get(i));
                                break;
                            }
                        }
                    }
                    if(getHex(selected_position_y,selected_position_x)!=null && noPieceOnThisHex){
                        getHex(selected_position_y,selected_position_x).onClick(new Tuple(e,part),getHex(selected_position_y,selected_position_x));
                    }
                    repaint();
                }
                
                /*@Override
                public void mouseReleased(MouseEvent e) {
                    previousMouseEvent = null;
                }*/
            });
            addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (previousMouseEvent!=null) {
                        panX+=e.getX()-previousMouseEvent.getX(); // Pan
                        panY+=e.getY()-previousMouseEvent.getY();
                    }
                    previousMouseEvent = e;
                    repaint();
                }
                @Override
                public void mouseMoved(MouseEvent e) {
                    previousMouseEvent = e;
                    repaint();
                }
            });
            addMouseWheelListener(new java.awt.event.MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    double scale_ammount = Math.pow(1.05, e.getPreciseWheelRotation());
                    zoom*=scale_ammount;
                    panX+=(int)((e.getX()-panX)*(1-scale_ammount));
                    panY+=(int)((e.getY()-panY)*(1-scale_ammount));
                    repaint();
                }
            });
            addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent evt) {

                }
                @Override
                public void keyReleased(java.awt.event.KeyEvent evt) {

                }
            });
        }
        @Override
        public void paintComponent(Graphics g) { // Works as paint rather than paintComponents - there are no components
            super.paintComponent(g);
            // With how I set things up, with the default pan, (0,0) is the center of the Hex below the top left. Oops! (Don't worry, you can pan it how you want it after it starts.)
            for (int j = 0; j < grid.length; j++) {
                for (int i = 0; i < grid[j].length; i++) {
                    if (grid[j][i]!=null) {
                        double vertical_displacement = ((i%2==0)?0.5:0.0);//This is how we get that zig zag thing going
                        {// Boarder / Outline
                            g.setColor(grid[j][i].getOutline());
                            int[] x_points = {(int)(panX-zoom*hexWidth/2-zoom*hexSide/2+i*hexWidth*zoom),(int)(panX-zoom*hexWidth/2+zoom*hexSide/2+i*hexWidth*zoom),(int)(panX+zoom*hexWidth/2-zoom*hexSide/2+i*hexWidth*zoom),(int)(panX+zoom*hexWidth/2+zoom*hexSide/2+i*hexWidth*zoom),(int)(panX+zoom*hexWidth/2-zoom*hexSide/2+i*hexWidth*zoom),(int)(panX-zoom*hexWidth/2+zoom*hexSide/2+i*hexWidth*zoom)};
                            int[] y_points = {(int)(panY+j*hexHeight*zoom-hexHeight*zoom*vertical_displacement),(int)(panY+zoom*hexHeight/2+j*hexHeight*zoom-hexHeight*zoom*vertical_displacement),(int)(panY+zoom*hexHeight/2+j*hexHeight*zoom-hexHeight*zoom*vertical_displacement),(int)(panY+j*hexHeight*zoom-hexHeight*zoom*vertical_displacement),(int)(panY-zoom*hexHeight/2+j*hexHeight*zoom-hexHeight*zoom*vertical_displacement),(int)(panY-zoom*hexHeight/2+j*hexHeight*zoom-hexHeight*zoom*vertical_displacement)};
                            g.fillPolygon(x_points, y_points, 6);
                        }
                        {// Inside
                            double inner_scale = 1-grid[j][i].getStrokeWeight(); // Scale the inside to create a boarder, or outline
                            g.setColor(grid[j][i].getFill());
                            int[] x_points = {(int)(panX-zoom*hexWidth/2*inner_scale-zoom*hexSide/2*inner_scale+i*hexWidth*zoom),(int)(panX-zoom*hexWidth/2*inner_scale+zoom*hexSide/2*inner_scale+i*hexWidth*zoom),(int)(panX+zoom*hexWidth/2*inner_scale-zoom*hexSide/2*inner_scale+i*hexWidth*zoom),(int)(panX+zoom*hexWidth/2*inner_scale+zoom*hexSide/2*inner_scale+i*hexWidth*zoom),(int)(panX+zoom*hexWidth/2*inner_scale-zoom*hexSide/2*inner_scale+i*hexWidth*zoom),(int)(panX-zoom*hexWidth/2*inner_scale+zoom*hexSide/2*inner_scale+i*hexWidth*zoom)};
                            int[] y_points = {(int)(panY+j*hexHeight*zoom-hexHeight*zoom*vertical_displacement),(int)(panY+zoom*hexHeight/2*inner_scale+j*hexHeight*zoom-hexHeight*zoom*vertical_displacement),(int)(panY+zoom*hexHeight/2*inner_scale+j*hexHeight*zoom-hexHeight*zoom*vertical_displacement),(int)(panY+j*hexHeight*zoom-hexHeight*zoom*vertical_displacement),(int)(panY-zoom*hexHeight/2*inner_scale+j*hexHeight*zoom-hexHeight*zoom*vertical_displacement),(int)(panY-zoom*hexHeight/2*inner_scale+j*hexHeight*zoom-hexHeight*zoom*vertical_displacement)};
                            g.fillPolygon(x_points, y_points, 6);
                        }
                    }
                }
            }
            for (int i = 0; i < pieces.size(); i++) {
                double vertical_displacement = ((pieces.get(i).getPosX()%2==0)?0.5:0.0);//This is how we get that zig zag thing going
                {// Boarder / Outline
                    double outer_scale = pieces.get(i).getSize();
                    g.setColor(pieces.get(i).getOutline());
                    int[] x_points = {(int)(panX-zoom*hexWidth/2*outer_scale-zoom*hexSide/2*outer_scale+pieces.get(i).getPosX()*hexWidth*zoom),(int)(panX-zoom*hexWidth/2*outer_scale+zoom*hexSide/2*outer_scale+pieces.get(i).getPosX()*hexWidth*zoom),(int)(panX+zoom*hexWidth/2*outer_scale-zoom*hexSide/2*outer_scale+pieces.get(i).getPosX()*hexWidth*zoom),(int)(panX+zoom*hexWidth/2*outer_scale+zoom*hexSide/2*outer_scale+pieces.get(i).getPosX()*hexWidth*zoom),(int)(panX+zoom*hexWidth/2*outer_scale-zoom*hexSide/2*outer_scale+pieces.get(i).getPosX()*hexWidth*zoom),(int)(panX-zoom*hexWidth/2*outer_scale+zoom*hexSide/2*outer_scale+pieces.get(i).getPosX()*hexWidth*zoom)};
                    int[] y_points = {(int)(panY+pieces.get(i).getPosY()*hexHeight*zoom-hexHeight*zoom*vertical_displacement),(int)(panY+zoom*hexHeight/2*outer_scale+pieces.get(i).getPosY()*hexHeight*zoom-hexHeight*zoom*vertical_displacement),(int)(panY+zoom*hexHeight/2*outer_scale+pieces.get(i).getPosY()*hexHeight*zoom-hexHeight*zoom*vertical_displacement),(int)(panY+pieces.get(i).getPosY()*hexHeight*zoom-hexHeight*zoom*vertical_displacement),(int)(panY-zoom*hexHeight/2*outer_scale+pieces.get(i).getPosY()*hexHeight*zoom-hexHeight*zoom*vertical_displacement),(int)(panY-zoom*hexHeight/2*outer_scale+pieces.get(i).getPosY()*hexHeight*zoom-hexHeight*zoom*vertical_displacement)};
                    g.fillPolygon(x_points, y_points, 6);
                }
                {// Inside
                    double inner_scale = pieces.get(i).getSize()*(1-pieces.get(i).getStrokeWeight()); // Scale the inside to create a boarder, or outline
                    g.setColor(pieces.get(i).getFill());
                    int[] x_points = {(int)(panX-zoom*hexWidth/2*inner_scale-zoom*hexSide/2*inner_scale+pieces.get(i).getPosX()*hexWidth*zoom),(int)(panX-zoom*hexWidth/2*inner_scale+zoom*hexSide/2*inner_scale+pieces.get(i).getPosX()*hexWidth*zoom),(int)(panX+zoom*hexWidth/2*inner_scale-zoom*hexSide/2*inner_scale+pieces.get(i).getPosX()*hexWidth*zoom),(int)(panX+zoom*hexWidth/2*inner_scale+zoom*hexSide/2*inner_scale+pieces.get(i).getPosX()*hexWidth*zoom),(int)(panX+zoom*hexWidth/2*inner_scale-zoom*hexSide/2*inner_scale+pieces.get(i).getPosX()*hexWidth*zoom),(int)(panX-zoom*hexWidth/2*inner_scale+zoom*hexSide/2*inner_scale+pieces.get(i).getPosX()*hexWidth*zoom)};
                    int[] y_points = {(int)(panY+pieces.get(i).getPosY()*hexHeight*zoom-hexHeight*zoom*vertical_displacement),(int)(panY+zoom*hexHeight/2*inner_scale+pieces.get(i).getPosY()*hexHeight*zoom-hexHeight*zoom*vertical_displacement),(int)(panY+zoom*hexHeight/2*inner_scale+pieces.get(i).getPosY()*hexHeight*zoom-hexHeight*zoom*vertical_displacement),(int)(panY+pieces.get(i).getPosY()*hexHeight*zoom-hexHeight*zoom*vertical_displacement),(int)(panY-zoom*hexHeight/2*inner_scale+pieces.get(i).getPosY()*hexHeight*zoom-hexHeight*zoom*vertical_displacement),(int)(panY-zoom*hexHeight/2*inner_scale+pieces.get(i).getPosY()*hexHeight*zoom-hexHeight*zoom*vertical_displacement)};
                    g.fillPolygon(x_points, y_points, 6);
                }
            }
            if (previousMouseEvent!=null) { // Hover paint
                double scaled_x = hg.scaleCords(previousMouseEvent).getFirst(); // So that pans and zooms are canceled out
                double scaled_y = hg.scaleCords(previousMouseEvent).getSecond();
                int selected_position_x = hg.mouseToGrid(previousMouseEvent).getFirst(); // -1 is out of bounds
                int selected_position_y = hg.mouseToGrid(previousMouseEvent).getSecond(); // The index of the clicked Hex
                double displacement_from_center_x = hg.mouseGridOffset(previousMouseEvent).getFirst();
                double displacement_from_center_y = hg.mouseGridOffset(previousMouseEvent).getSecond();

                // We know the cords, but which *piece* of the GridThing did we click on? The upper part? The lower?
                Direction part = hg.mouseToDir(previousMouseEvent);

                // Now that we know where we clicked, let's run the necicary code!
                boolean noPieceOnThisHex = true;
                for (int i = pieces.size()-1; i > -1; i--) { // Prioritize Pieces
                    if (pieces.get(i).getPosX()==selected_position_x && pieces.get(i).getPosY()==selected_position_y) {
                        if (
                            (displacement_from_center_y>-hexHeight*pieces.get(i).getSize()*0.5)&&//Below Top Line
                            (displacement_from_center_y<hexHeight*pieces.get(i).getSize()*0.5)&&//Above Bottom Line
                            (displacement_from_center_y-(hexHeight*pieces.get(i).getSize()/2)>hexHeight/2/hexSide*(displacement_from_center_x-(hexWidth-(hexSide/2))*pieces.get(i).getSize()))&&//Below Top Right Line
                            (displacement_from_center_y+(hexHeight*pieces.get(i).getSize()/2)<-hexHeight/2/hexSide*(displacement_from_center_x-(hexWidth-(hexSide/2))*pieces.get(i).getSize()))&&//Above Bottom Right Line
                            (displacement_from_center_y-(hexHeight*pieces.get(i).getSize()/2)>-hexHeight/2/hexSide*(displacement_from_center_x+(hexWidth-(hexSide/2))*pieces.get(i).getSize()))&&//Below Top Left Line
                            (displacement_from_center_y+(hexHeight*pieces.get(i).getSize()/2)<hexHeight/2/hexSide*(displacement_from_center_x+(hexWidth-(hexSide/2))*pieces.get(i).getSize()))//Above Bottom Left Line
                        ) {
                            noPieceOnThisHex = false;
                            pieces.get(i).onHover(new Tuple(new Tuple(previousMouseEvent,g),new Tuple(part,hg)), pieces.get(i));
                            break;
                        }
                    }
                }
                if(getHex(selected_position_y,selected_position_x)!=null && noPieceOnThisHex){
                    getHex(selected_position_y,selected_position_x).onHover(new Tuple(new Tuple(previousMouseEvent,g),new Tuple(part,hg)),getHex(selected_position_y,selected_position_x));
                }
            }
        }
    }
    public Hex getHex (int j, int i) { // Get a hex from grid
        try {
            return grid[j][i];
        }catch(Exception e){
            return null;
        }
    }
    public void setHex (int j, int i, Hex hex) { // Place a hex in the grid
        grid[j][i]=hex.copy();
    }
    public void clearHex (int j, int i) { // Clear a hex from the grid
        grid[j][i]=null;
    }
    public void fillWithHex (Hex hex) { // Fill the grid with a hex
        for (int j = 0; j < grid.length; j++) {
            for (int i = 0; i < grid[j].length; i++) {
                grid[j][i]=hex.copy();
            }
        }
    }
    public void addPiece (Piece p) {
        pieces.add(p);
    }
    public void removePiece (Piece p) {
        pieces.remove(p);
    }
    public void setOnKeyPress (AsyncFunc<KeyEvent,HexGrid,Void> func) {
        onKeyPress = func;
    }
    public double getZoom () {
        return zoom;
    }
    public void setZoom (double z) {
        zoom = z;
    }
    public Tuple<Integer, Integer> getPan () {
        return new Tuple(panX,panY);
    }
    public void setPan (Tuple<Integer, Integer> t) {
        panX = t.getFirst();
        panY = t.getSecond();
    }
    public int getHexHeight () {
        return hexHeight;
    }
    public void setHexHeight (int i) {
        hexHeight = i;
    }
    public int getHexWidth () {
        return hexWidth;
    }
    public void setHexWidth (int i) {
        hexWidth = i;
    }
    public int getHexSide () {
        return hexSide;
    }
    public void setHexSide (int i) {
        hexSide = i;
    }
}

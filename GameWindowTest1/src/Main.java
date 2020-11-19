import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/*
 * For testing GameWindow
 */

/**
 *
 * @author joeyn
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Game gm = new Game(new PanZoom(1,0,0,16/9.0)) {
            boolean toggleThing = false;
            double lastClickX = 0;
            double lastClickY = 0;

            @Override
            public void keyPressed(KeyEvent e) {
                
            }

            @Override
            public void mouseClicked(MouseEvent e, int width, int height) {
                System.out.println("Clicked @ ("+panZoom.unmapX(e.getX(), width, height)+", "+panZoom.unmapY(e.getY(), width, height)+")");
                if (panZoom.unmapX(e.getX(), width, height)<0.0) {
                    toggleThing=!toggleThing;
                }
                if (panZoom.unmapX(e.getX(), width, height)>3.0) {
                    gameWindow.advance();
                }
                lastClickX = panZoom.unmapX(e.getX(), width, height);
                lastClickY = panZoom.unmapY(e.getY(), width, height);
            }

            @Override
            public void paint(Graphics g, int width, int height) {
                g.setColor(Color.black);
                g.fillRect(0, 0, width, height);
                panZoom.setClipping(g, width, height);
                g.setColor(new Color(((toggleThing)?255:0),((toggleThing)?0:255),((toggleThing)?127:0)));
                g.fillRect(0,0,width,height);
                g.setColor(new Color(((toggleThing)?127:255),0,((toggleThing)?255:0)));
                for (int i = 0; i < 15; i++) {
                    g.fillRect(panZoom.mapX(0.2*(i+((toggleThing)?0.5:0)),width,height), panZoom.mapY(0,width,height), (int)(panZoom.mapWidth(width, height)*panZoom.getZoom()*0.1), (int)(panZoom.mapHeight(width, height)*panZoom.getZoom()*0.5));
                    g.fillRect(panZoom.mapX(0.2*(i+((toggleThing)?0:0.5)),width,height), panZoom.mapY(0.5,width,height), (int)(panZoom.mapWidth(width, height)*panZoom.getZoom()*0.1), (int)(panZoom.mapHeight(width, height)*panZoom.getZoom()*0.5));
                }
                g.setColor(new Color(0,0,255,127));
                //"Fixed" Overlay
                //g.fillRect(width/2 - panZoom.mapWidth(width, height)/4,height/2 - panZoom.mapHeight(width, height)/4,panZoom.mapWidth(width, height)/2,panZoom.mapHeight(width, height)/2);
                //System.out.println(panZoom.mapX(0,width,height)+", "+panZoom.mapY(0,width,height)+"      "+(int)(width*panZoom.getZoom())+", "+(int)(height*panZoom.getZoom())+" : "+panZoom.getZoom());
                //panZoom.paintBoarders(g,width,height,Color.black);
                g.fillOval(panZoom.mapX(lastClickX, width, height)-10, panZoom.mapY(lastClickY, width, height)-10, 20, 20);
            }

            @Override
            public boolean assetsLoaded() {
                return true;
            }
        };
        gm.getPanZoom().setBounds(-3,-1,0,0,1,Double.POSITIVE_INFINITY);
        //                       (minX,minY,maxX,maxY,minZ,maxZ);
        GameWindow gw = new GameWindow(gm);
        gw.loadGame(new PictureTest());
    }
    
}

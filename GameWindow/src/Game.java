
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/*
 * A Game object needs a loaded AssetLoader or a null one.
 * It controlls what happens on click events and paint events.
 * It's basically a whole game that can be loaded into a GameWindow.
 */

/**
 *
 * @author joeyn
 */
public abstract class Game {
    protected PanZoom panZoom;
    protected GameWindow gameWindow;
    
    /**
     * Gets the panZoom
     * @return the panZoom
     */
    public PanZoom getPanZoom () {
        return panZoom;
    }
    
    /**
     * Creates a game object
     * @param pz Init the panZoom
     * @param al Init the loadedAssets
     */
    public Game (PanZoom pz) {
        panZoom = pz;
    }
    
    public Game () {
        this(new PanZoom(1, 0, 0));
    }
    
    public abstract void keyPressed (KeyEvent e);
    public abstract void mouseClicked (MouseEvent e, int width, int height);
    public abstract void paint (Graphics g, int width, int height);
    /**
     * Are all the assets loaded?
     * @return true if all assets are loaded, and false if not
     */
    public abstract boolean assetsLoaded ();
    
    public void setGameWindow (GameWindow gw) {gameWindow = gw;}
}

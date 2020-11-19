import java.awt.Graphics;
import java.awt.PopupMenu;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * A GameWindow object's Game object can load audio, images, text, and json files
 * Multiple Game objects can be used through a single GameWindow object.
 * GameWindows can be panned and zoomed into.
 * -------------------------------------------------------------------------------------
 * Long story short, it's a JFrame with a JPanel in it and a bunch of loading functions.
 */

/**
 * @author joeyn
 */
public class GameWindow extends JFrame {
    private Game game;
    private MouseEvent lastMouseEvent;
    private Game nextGame;
    public GameWindow (Game loadedGame, String title, int width, int height) {
        super(title);
        game = loadedGame;
        game.setGameWindow(this);
        add(new JPanelInstence(this));
        setSize(width, height);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        setVisible(true);
    }
    public GameWindow (Game loadedGame) {
        this(loadedGame, "Unnamed Game", 400, 225);
    }
    public Game getGame() {
        return game;
    }
    /**
     * Begins preparing a game
     * @see advance
     * @param gameToLoad The game to be loaded
     */
    public void loadGame(Game gameToLoad) {
        nextGame = gameToLoad;
        nextGame.setGameWindow(this);
    }
    /**
     * Tries to load in the game that was prepared via loadGame(Game gameToLoad).
     * @see loadGame
     * @return Was the game able to load? (If no, the assets probably didn't finish loading.)
     */
    public boolean advance() {
        if (nextGame.assetsLoaded()) {
            game = nextGame;
        }
        return false;
    }
    private static class JPanelInstence extends JPanel {
        GameWindow gameWindow;
        public JPanelInstence(GameWindow gameWindowObject) {
            super();
            gameWindow = gameWindowObject;
            setFocusable(true);
            addKeyListener(new KeyListener(){
                @Override
                public void keyPressed(KeyEvent e) {
                    gameWindow.game.keyPressed(e);
                    repaint();
                }
                @Override
                public void keyTyped(KeyEvent e) {}
                @Override
                public void keyReleased(KeyEvent e) {}
            });
            addMouseListener(new MouseListener(){
                @Override
                public void mouseClicked(MouseEvent e) {
                    gameWindow.game.mouseClicked(e,getWidth(),getHeight());
                    repaint();
                }
                @Override
                public void mousePressed(MouseEvent e) {}
                @Override
                public void mouseReleased(MouseEvent e) {}
                @Override
                public void mouseEntered(MouseEvent e) {}
                @Override
                public void mouseExited(MouseEvent e) {}
            });
            addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (gameWindow.lastMouseEvent!=null) {
                        gameWindow.game.getPanZoom().doPan(e, gameWindow.lastMouseEvent, getWidth(), getHeight());
                    }
                    gameWindow.lastMouseEvent=e;
                    repaint();
                }
                @Override
                public void mouseMoved(MouseEvent e) {
                    gameWindow.lastMouseEvent=e;
                }
            });
            addMouseWheelListener(new java.awt.event.MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    gameWindow.game.getPanZoom().doZoom(e, getWidth(), getHeight());
                    repaint();
                }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            gameWindow.game.paint(g,getWidth(),getHeight());
        }
    }
}

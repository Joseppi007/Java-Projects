


import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.*;

/*
 * A sub-game for testing pictures
 */

/**
 * A "game" for testing pictures (and let's throw audio in here, too.)
 * @author joeyn
 */
public class PictureTest extends Game {
    BufferedImage image;
    Clip sound;
    AudioInputStream soundStream;
    
    PictureTest(){
        //Create walls around the image
        panZoom.setBounds(-1,-1,0,0,1,Double.POSITIVE_INFINITY);
        try {
            // Loading an image
                // From a file
                //image = ImageIO.read(new File("Assets\\Test.png"));//"C:\\Users\\joeyn\\OneDrive\\Desktop\\Delete.png"
                // From within the jar (neater)
                image = ImageIO.read(new BufferedInputStream(ClassLoader.getSystemClassLoader().getResourceAsStream("Assets/Test.png")));
            // Loading a sound
            soundStream = AudioSystem.getAudioInputStream(new BufferedInputStream(ClassLoader.getSystemClassLoader().getResourceAsStream("Assets/Test.wav")));
            sound = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, soundStream.getFormat()));
            sound.open(soundStream);
            //Set the ratio to the image's ratio
            panZoom.setRatio((double)image.getWidth()/image.getHeight());
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
    }

    @Override
    public void mouseClicked(MouseEvent e, int width, int height) {
        sound.setFramePosition(0);
        sound.start();
    }

    @Override
    public void paint(Graphics g, int width, int height) {
        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);
        panZoom.setClipping(g, width, height);
        
        //g.fillRect(panZoom.mapX(0,width,height), panZoom.mapY(0,width,height), (int)(panZoom.mapWidth(width, height)*panZoom.getZoom()*0.1), (int)(panZoom.mapHeight(width, height)*panZoom.getZoom()*0.5));
        g.drawImage(image, panZoom.mapX(0,width,height), panZoom.mapY(0,width,height), (int)(panZoom.mapWidth(width, height)*panZoom.getZoom()), (int)(panZoom.mapHeight(width, height)*panZoom.getZoom()), null);
    }

    @Override
    public boolean assetsLoaded() {
        return !((image==null)||(sound==null)||(soundStream==null));
    }
}

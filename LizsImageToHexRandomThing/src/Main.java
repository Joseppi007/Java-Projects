
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/*
 * Image goes in
 * HexGrid with random colors from the image comes out
 */

/**
 *
 * @author joeyn
 */
public class Main {
    /**
     * @param args the command line arguments - width and height
     */
    public static void main(String[] args) {
        BufferedImage bi = null; // This is the image, I guess.

        Scanner input = new Scanner(System.in);
        System.out.println("Enter the path of an image: (I think you can just drag it, too.)");
        try {
            bi = ImageIO.read(new File(input.nextLine()));
        } catch (IOException ex) {
            System.err.println(ex);
        }
        
        if (bi==null) {
            return;
        }
        
        HexGrid hg;
        
        int width = 30;
        int height = 30;
        
        if (args.length>1) {
            width = Integer.parseInt(args[0]);
            height = Integer.parseInt(args[1]);
        }
        
        hg = new HexGrid(width,height,100,100,25);
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                //random
                //hg.setHex(y, x, new Hex(new Color(bi.getRGB((int)(Math.random()*bi.getWidth()), (int)(Math.random()*bi.getHeight()))),0.1,new Color(bi.getRGB((int)(Math.random()*bi.getWidth()), (int)(Math.random()*bi.getHeight())))));
                //not random
                hg.setHex(y, x, new Hex(new Color(bi.getRGB((int)(x*bi.getWidth()/width), (int)(y*bi.getHeight()/height))),0.1,new Color(bi.getRGB((int)((x+0.5)*bi.getWidth()/width), (int)((y+0.5)*bi.getHeight()/height)))));
            }
        }
    }
    
}

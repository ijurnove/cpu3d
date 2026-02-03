package com.ijurnove.cpu3d;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * <code>Textures</code> contains one method for reading image files, <code>Textures.read()</code>.
 */
public class Textures {
    private static BufferedImage createTransformed(BufferedImage image, AffineTransform at) {
        BufferedImage newImage = new BufferedImage(
            image.getWidth(), image.getHeight(),
            BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    /**
     * Reads a specified file and returns a <code>BufferedImage</code>.
     * @param path the specified path
     * @return a BufferedImage
     */
    public static BufferedImage read(String path) {
        try {
            BufferedImage image = ImageIO.read(new File(path));

            // stackoverflow code to flip an image
            AffineTransform at = new AffineTransform();
            at.concatenate(AffineTransform.getScaleInstance(1, -1));
            at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
            
            return createTransformed(image, at);

            // return ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}

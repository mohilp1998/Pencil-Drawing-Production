import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.*;

import javax.imageio.ImageIO;

public class PencilDrawing {
    
    BufferedImage image;
    int width;
    int height;

    public PencilDrawing() {
        // Constructor for the class
        System.out.println("Welcome to Pencil Drawing generator!!");
    }

    public static void main(String[] args) {
        // Prints "Hello, World" to the terminal window.
        PencilDrawing myDrawing = new PencilDrawing();
        myDrawing.readImage();

        try{
            File output = new File("output.jpg");
            ImageIO.write(myDrawing.image, "jpg", output);
        } catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }
    }
    
    public void readImage() {
        try {
            File input = new File("testImage-1.jpg");
            this.image = ImageIO.read(input);
            this.width = image.getWidth(); 
            this.height = image.getHeight();
        } catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
        }
    }
}
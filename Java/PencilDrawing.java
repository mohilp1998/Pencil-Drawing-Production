import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.*;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class PencilDrawing {
    
    BufferedImage image;
    int width;
    int height;

    public PencilDrawing() {
        // Constructor for the class
        System.out.println("Fn:PencilDrawing()::Welcome to Pencil Drawing generator!!");
    }

    public static void main(String[] args) {
        // Prints "Hello, World" to the terminal window.
        PencilDrawing myDrawing = new PencilDrawing();
        myDrawing.readImage("testImage-1.jpg");
        myDrawing.convertToGrayScale();
        myDrawing.outputImage("GrayScale.jpg");
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        Mat src = Imgcodecs.imread("GrayScale.jpg");
        Mat dst = new Mat();
        Imgproc.Sobel(src, dst, -1, 0, 1);
        HighGui.imshow("Sobel - x:1 & y:0 ", dst);
        HighGui.waitKey();
    }
    
    public void readImage(String inputPath) {
        System.out.println("Fn:readImage()::Reading Image from Path " + inputPath);
        try {
            File input = new File(inputPath);
            this.image = ImageIO.read(input);
            this.width = image.getWidth(); 
            this.height = image.getHeight();
        } catch (Exception e) {
            System.err.println("Fn:readImage()::Exception: " + e.getMessage());
        }
    }

    public void convertToGrayScale() {
        // While doing gray scale conversion we use
        // color = 0.299R + 0.587G + 0.114B because this are scaled
        // according to human perception/wavelength of the color
        System.out.println("Fn:convertToGrayScale()::Converting Image to grayscale version");
        try {
            for (int i = 0; i < this.width; i++) {
                for (int j = 0; j < this.height; j++) {
                    Color c = new Color(this.image.getRGB(i, j));
                    int red = (int)(c.getRed() * 0.299);
                    int green = (int)(c.getGreen() * 0.587);
                    int blue = (int)(c.getBlue() * 0.114);
                    
                    Color grayScale = new Color(red+green+blue, red+green+blue, red+green+blue);

                    this.image.setRGB(i, j, grayScale.getRGB());
                }
            }
        } catch (Exception e) {
            System.err.println("Fn:convertToGrayScale()::Exception: " + e.getMessage());
        }
    }

    public void outputImage(String outPath) {
        System.out.println("Fn:outputImage()::Outputting the image to path " + outPath);
        try {
            File output = new File(outPath);
            ImageIO.write(this.image, "jpg", output);
        } catch (Exception e) {
            System.err.println("Fn:outputImage()::Exception: " + e.getMessage());
        }
    }
}
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.*;

public class PencilDrawing {
   
    // For logging
    LogManager lgmngr = LogManager.getLogManager();
    Logger log = lgmngr.getLogger(Logger.GLOBAL_LOGGER_NAME);

    // For Image processing
    Mat image;
    int width;
    int height;

    public PencilDrawing() {
        // Setting Log Level - Please Update as required for messages
        try {
            log.setLevel(Level.INFO);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception: " + e.getMessage());
        }

        log.log(Level.INFO, "Welcome to Pencil Drawing generator!!");
        // Loading the opencv library
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
    }

    public static void main(String[] args) {
        PencilDrawing myDrawing = new PencilDrawing();
        myDrawing.readImage("testImage-1.jpg");
        myDrawing.convertToGrayScale();
        myDrawing.displayImage();
        myDrawing.outputImage("GrayScale.jpg");
    }
    
    public void readImage(String inputPath) {
        log.log(Level.INFO, "Reading Image from Path " + inputPath);
        try {
            this.image = Imgcodecs.imread(inputPath);
            this.width = image.width(); 
            this.height = image.height();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception: " + e.getMessage());
        }
    }

    public void convertToGrayScale() {
        // While doing gray scale conversion we use
        // color = 0.299R + 0.587G + 0.114B because this are scaled
        // according to human perception/wavelength of the color
        log.log(Level.INFO, "Converting Image to grayscale version");
        try {
            Mat grey = new Mat();
            Imgproc.cvtColor(this.image, grey, Imgproc.COLOR_BGR2GRAY);
            this.image = grey;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception: " + e.getMessage());
        }
    }

    public void outputImage(String outPath) {
        log.log(Level.INFO, "Outputting the image to path " + outPath);
        try {
            Imgcodecs.imwrite(outPath, this.image);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception: " + e.getMessage());
        }
    }

    public void displayImage() {
        log.log(Level.INFO, "Displaying the current image in GUI");
        try {
            HighGui.imshow("image", this.image);
            HighGui.waitKey();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception: " + e.getMessage());
        } 
    }
}
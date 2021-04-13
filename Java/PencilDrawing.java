import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.*;

public class PencilDrawing {
   
    // For logging
    LogManager lgmngr = LogManager.getLogManager();
    Logger log = lgmngr.getLogger(Logger.GLOBAL_LOGGER_NAME);

    // For Image processing
    Mat imgColor;
    Mat imgGray;
    int width;
    int height;

    public PencilDrawing() {
        // Setting Log Level - Please Update as required for messages
        try {
            log.setLevel(Level.WARNING);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception: " + e.getMessage());
        }

        log.log(Level.INFO, "Welcome to Pencil Drawing generator!!");
        // Loading the opencv library
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
    }

    public static void main(String[] args) {
        PencilDrawing myDrawing = new PencilDrawing();
        lineDrawingWithStrokes(myDrawing);
    }
    
    public static void lineDrawingWithStrokes(PencilDrawing myDrawing) {
        myDrawing.readImage("testImage-1.jpg");
        myDrawing.convertToGrayScale();
        myDrawing.gradientImage();
        myDrawing.displayImage(myDrawing.imgGray);
        myDrawing.outputImage("GrayScale.jpg", myDrawing.imgGray);
    }

    public void readImage(String inputPath) {
        log.log(Level.INFO, "Reading Image from Path " + inputPath);
        try {
            this.imgColor = Imgcodecs.imread(inputPath);
            this.imgColor.convertTo(this.imgColor, CvType.CV_32F);
            this.width = imgColor.width(); 
            this.height = imgColor.height();
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
            Mat gray = new Mat();
            Imgproc.cvtColor(this.imgColor, gray, Imgproc.COLOR_BGR2GRAY);
            Imgcodecs.imwrite("gray.jpg", gray);
            this.imgGray = Imgcodecs.imread("gray.jpg", Imgcodecs.IMREAD_GRAYSCALE);
            this.imgGray.convertTo(this.imgGray, CvType.CV_32F);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception: " + e.getMessage());
        }
    }

    public void outputImage(String outPath, Mat img) {
        log.log(Level.INFO, "Outputting the image to path " + outPath);
        try {
            img.convertTo(img, CvType.CV_8U);
            Imgcodecs.imwrite(outPath, img);
            img.convertTo(img, CvType.CV_32F);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception: " + e.getMessage());
        }
    }

    public void displayImage(Mat img) {
        log.log(Level.INFO, "Displaying the current image in GUI");
        try {
            img.convertTo(img, CvType.CV_8U);
            HighGui.imshow("image", img);
            HighGui.waitKey();
            img.convertTo(img, CvType.CV_32F);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception: " + e.getMessage());
        } 
    }

    public void gradientImage() {
        Mat grad_x = new Mat(); 
        Mat grad_y = new Mat();

        Imgproc.Sobel(this.imgGray, grad_x, -1, 1, 0);
        Imgproc.Sobel(this.imgGray, grad_y, -1, 0, 1);

        Mat grad = new Mat();
        Core.pow(grad_x, 2, grad_x);
        Core.pow(grad_y, 2, grad_y);
        Core.add(grad_x, grad_y, grad);
        Core.sqrt(grad, this.imgGray);
    }

    public double [] getPixelValue(int row, int col, Mat img) {
        double [] data = img.get(row, col);
        return data;
        // Reference Code for getting all pixel values of a Gray Scale single channel image.
        // It returns the values from 0-255 range values. Also the returned double [] data
        // array will be of size = number of channels.
        // for (int i = 0; i < myDrawing.imgGray.height(); i++) {
        //     for (int j = 0; j < myDrawing.imgGray.width(); j++) {
        //         System.out.print(myDrawing.getPixelValue(i, j, myDrawing.imgGray)[0]);
        //         System.out.print(" ");
        //     }
        //     System.out.println(" ");
        // }
    }

    public void updatePixelVal(int row, int col, double [] data, Mat img) {
        img.put(row, col, data);

        // Reference for using put() method to update the image. Note that
        // the value of data ranges from 0-255 and double [] data should have
        // number of array elements = number of channels
        // for (int i = 0; i < myDrawing.imgGray.height(); i++) {
        //     for (int j = 0; j < myDrawing.imgGray.width(); j++) {
        //         double [] data = {(i+j)%255};
        //         myDrawing.imgGray.put(i, j, data);
        //         // myDrawing.updatePixelVal(i, j, data, myDrawing.imgGray); - Equivalent
        //     }
        // }
    }
}
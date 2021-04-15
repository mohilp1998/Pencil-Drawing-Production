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
import java.lang.Math;

public class PencilDrawing {
   
    // For logging
    LogManager lgmngr = LogManager.getLogManager();
    Logger log = lgmngr.getLogger(Logger.GLOBAL_LOGGER_NAME);

    // For Image processing
    Mat imgColor;
    int width;
    int height;

    // Variables for Line Drawing with Strokes
    Mat imgGray;
    Mat imgGradient;
    Mat [] Li = new Mat[4]; // degree-> index: 0 -> 0, 45 -> 1, 90 -> 2, 135 -> 3
    Mat [] Ci = new Mat[4]; // degree-> index: 0 -> 0, 45 -> 1, 90 -> 2, 135 -> 3

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
        myDrawing.lineDrawingWithStrokes();
    }
    
    public void lineDrawingWithStrokes() {
        readImage("testImage-1.jpg");
        convertToGrayScale();
        gradientImage();
        generateCi();
        displayImage(Ci[1]);


        // outputImage("GrayScale.jpg", this.imgGray);
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

    public void gradientImage() {
        Mat grad_x = new Mat(); 
        Mat grad_y = new Mat();

        Imgproc.Sobel(this.imgGray, grad_x, -1, 1, 0);
        Imgproc.Sobel(this.imgGray, grad_y, -1, 0, 1);

        Mat grad = new Mat();
        Core.pow(grad_x, 2, grad_x);
        Core.pow(grad_y, 2, grad_y);
        Core.add(grad_x, grad_y, grad);
        this.imgGradient = new Mat();
        Core.sqrt(grad, this.imgGradient);
        this.imgGradient.convertTo(this.imgGradient, CvType.CV_32F);
        Core.normalize(this.imgGradient, this.imgGradient, 0.0, 255.0, Core.NORM_MINMAX);
        log.log(Level.INFO, "Gradient Image Created");
    }

    public void generateCi() {
        // First we will generate 4 Li - For now only using 4 directions only
        int size = Math.max(this.imgColor.width(), this.imgColor.height())/30;
        size = size + (size % 2) + 1;
        double intensity = 10;

        Li[0] = new Mat(size, size, CvType.CV_32FC1, new Scalar(0));
        Li[0].row(size/2).setTo(new Scalar(intensity));

        Li[1] = new Mat(size, size, CvType.CV_32FC1, new Scalar(0));
        for (int i = 0; i < Li[1].width(); i++) {
            for (int j = 0; j < Li[1].height(); j++) {
                if ( (i+j+1) == size ){
                    double [] data = {intensity};
                    updatePixelVal(i, j, data, Li[1]);
                }
            }
        }
        
        Li[2] = new Mat(size, size, CvType.CV_32FC1, new Scalar(0));
        Li[2].col(size/2).setTo(new Scalar(intensity));
        
        Li[3] = new Mat(size, size, CvType.CV_32FC1, new Scalar(0));
        for (int i = 0; i < Li[3].width(); i++) {
            for (int j = 0; j < Li[3].height(); j++) {
                if(i == j){
                    double [] data = {intensity};
                    updatePixelVal(i, j, data, Li[3]);
                }
            }
        }

        // Generating Gis corresponding to Lis
        Mat [] Gi = new Mat[4];

        Gi[0] = new Mat();
        Imgproc.filter2D(this.imgGradient, Gi[0], CvType.CV_32F, Li[0]);
        Core.normalize(Gi[0], Gi[0], 0, 255, Core.NORM_MINMAX);

        Gi[1] = new Mat();
        Imgproc.filter2D(this.imgGradient, Gi[1], CvType.CV_32F, Li[1]);
        Core.normalize(Gi[1], Gi[1], 0, 255, Core.NORM_MINMAX);

        Gi[2] = new Mat();
        Imgproc.filter2D(this.imgGradient, Gi[2], CvType.CV_32F, Li[2]);
        Core.normalize(Gi[2], Gi[2], 0, 255, Core.NORM_MINMAX);

        Gi[3] = new Mat();
        Imgproc.filter2D(this.imgGradient, Gi[3], CvType.CV_32F, Li[3]);
        Core.normalize(Gi[3], Gi[3], 0, 255, Core.NORM_MINMAX);

        // Generating Cis
        Ci[0] = new Mat(this.imgGradient.rows(), this.imgGradient.cols(), CvType.CV_32F, new Scalar(0));
        Ci[1] = new Mat(this.imgGradient.rows(), this.imgGradient.cols(), CvType.CV_32F, new Scalar(0));
        Ci[2] = new Mat(this.imgGradient.rows(), this.imgGradient.cols(), CvType.CV_32F, new Scalar(0));
        Ci[3] = new Mat(this.imgGradient.rows(), this.imgGradient.cols(), CvType.CV_32F, new Scalar(0));

        for (int i = 0; i < this.imgGradient.rows(); i++) {
            for (int j = 0; j < this.imgGradient.cols(); j++) {
                // Step 1: Get all Gi and G pixel values
                double [] Gi_data = new double[4];
                Gi_data[0] = getPixelValue(i, j, Gi[0])[0];
                Gi_data[1] = getPixelValue(i, j, Gi[1])[0];
                Gi_data[2] = getPixelValue(i, j, Gi[2])[0];
                Gi_data[3] = getPixelValue(i, j, Gi[3])[0];

                double G_data = getPixelValue(i, j, this.imgGradient)[0];
                // Step 2: Calculate max of Gi_data
                double max = -1;
                for (int k = 0; k < Gi_data.length; k++) {
                    max = Math.max(max, Gi_data[k]);
                }

                // Step 3: Loop through Gi_data & if Max == Gi_data[i]. Set Ci[i] = G_data
                for (int k = 0; k < Gi_data.length; k++) {
                    double [] data = new double[1];
                    if (Gi_data[k] == max) {
                        data[0] = G_data;
                        updatePixelVal(i, j, data, Ci[k]);
                    }
                    else {
                        data[0] = 0;
                        updatePixelVal(i, j, data, Ci[k]);
                    }
                }
            }
        }
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
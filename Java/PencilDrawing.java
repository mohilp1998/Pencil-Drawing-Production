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
    Mat [] Li = new Mat[6]; //degree->index: 0->0, 45->1, 90->2, 135->3, ~60->4, ~120->5
    Mat [] Ci = new Mat[6]; //degree->index: 0->0, 45->1, 90->2, 135->3, ~60->4, ~120->5
    Mat imgLine;

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

        // Generating Line Drawing with strokes image
        imgLine = new Mat(Ci[0].rows(), Ci[0].cols(), CvType.CV_32FC1, new Scalar(0));
        Mat [] convLiCi = new Mat[6];
        for (int i = 0; i < convLiCi.length; i++) {
            convLiCi[i] = new Mat();
            Imgproc.filter2D(Ci[i], convLiCi[i], CvType.CV_32F, Li[i]);
            Core.normalize(convLiCi[i], convLiCi[i], 0, 255, Core.NORM_MINMAX);
        }

        for (int i = 0; i < convLiCi.length; i++) {
            Core.add(imgLine, convLiCi[i], imgLine);
        }
        Core.normalize(imgLine, imgLine, 0, 255, Core.NORM_MINMAX);

        // Inverting the imgLine image
        for (int i = 0; i < imgLine.rows(); i++) {
            for (int j = 0; j < imgLine.cols(); j++) {
                double [] data;
                data = getPixelValue(i, j, imgLine);
                for (int k = 0; k < data.length; k++) {
                    data[k] = (255 - data[k]);
                }
                updatePixelVal(i, j, data, imgLine);
            }
        }

        displayImage(imgLine);
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

        Li[4] = new Mat(size, size, CvType.CV_32FC1, new Scalar(0));
        int Li4sum = (Li[4].rows() - (Li[4].rows()/4));
        for (int i = 0; i < Li[4].rows(); i++) {
            for (int j = 0; j < Li[4].cols(); j++) {
                if ((i % 2) == 0) {
                    if ((i + j) == Li4sum) {
                        double [] data = {intensity+230};
                        updatePixelVal(i, j, data, Li[4]);
                    }
                }
            }
            if ( (i % 2) == 0){
                Li4sum = Li4sum + 1;
            }
        }

        Li[5] = new Mat(size, size, CvType.CV_32FC1, new Scalar(0));
        int Li5sum = (Li[5].rows()/4);
        for (int i = 0; i < Li[5].rows(); i++) {
            for (int j = 0; j < Li[5].cols(); j++) {
                if ((i % 2) == 0) {
                    if ((i + j) == Li5sum) {
                        double [] data = {intensity+230};
                        updatePixelVal(i, j, data, Li[5]);
                    }
                }
            }
            if ( (i % 2) == 0){
                Li5sum = Li5sum + 3;
            }
        }

        // Generating Gis corresponding to Lis
        Mat [] Gi = new Mat[6];

        for (int i = 0; i < Gi.length; i++) {
            Gi[i] = new Mat();
            Imgproc.filter2D(this.imgGradient, Gi[i], CvType.CV_32F, Li[i]);
            Core.normalize(Gi[i], Gi[i], 0, 255, Core.NORM_MINMAX);
        }

        // Generating Cis
        for (int i = 0; i < Ci.length; i++) {
            Ci[i] = new Mat(this.imgGradient.rows(), this.imgGradient.cols(), CvType.CV_32F, new Scalar(0));
        }

        for (int i = 0; i < this.imgGradient.rows(); i++) {
            for (int j = 0; j < this.imgGradient.cols(); j++) {
                // Step 1: Get all Gi and G pixel values
                double [] Gi_data = new double[Ci.length];
                for (int k = 0; k < Gi_data.length; k++) {
                    Gi_data[k] = getPixelValue(i, j, Gi[k])[0];
                }

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
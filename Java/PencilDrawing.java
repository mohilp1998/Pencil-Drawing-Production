import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;

import java.util.*;
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
        myDrawing.lineDrawingWithStrokes("testImage-3.jpg");
        myDrawing.generatePencilTexture();
        myDrawing.displayImage(myDrawing.imgEqualized);
    }
    
    //*******************************************************************//
    //************* Following are the basic functionalities *************//
    //*******************************************************************//

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

    public double [] getPixelValue(int row, int col, Mat img) {
        double [] data = img.get(row, col);
        return data;
    }

    public void updatePixelVal(int row, int col, double [] data, Mat img) {
        img.put(row, col, data);
    }

    //*******************************************************************//
    //**** Following part of code is to Generate Pencil Stroke Image ****//
    //*******************************************************************//
    Mat imgGray;
    Mat imgGradient;
    int numDirections = 8;
    Mat [] Li = new Mat[numDirections]; //degree->index: 0->0, 45->1, 90->2, 135->3, ~60->4, ~120->5
    Mat [] Ci = new Mat[numDirections]; //degree->index: 0->0, 45->1, 90->2, 135->3, ~60->4, ~120->5
    Mat imgLine;
    double imgLineDarkness = 1.0;

    public void lineDrawingWithStrokes(String imgPath) {
        readImage(imgPath);
        convertToGrayScale();
        gradientImage();
        int customSize = 8;
        generateCi(customSize);

        // Generating Line Drawing with strokes image
        imgLine = new Mat(Ci[0].rows(), Ci[0].cols(), CvType.CV_32FC1, new Scalar(0));
        Mat [] convLiCi = new Mat[numDirections];
        for (int i = 0; i < convLiCi.length; i++) {
            convLiCi[i] = new Mat();
            Imgproc.filter2D(Ci[i], convLiCi[i], CvType.CV_32F, Li[i]);
            Core.normalize(convLiCi[i], convLiCi[i], 0, 255, Core.NORM_MINMAX);
        }

        for (int i = 0; i < convLiCi.length; i++) {
            Core.add(imgLine, convLiCi[i], imgLine);
        }
        Core.normalize(imgLine, imgLine, 0, 255, Core.NORM_MINMAX);
        Core.pow(imgLine, imgLineDarkness, imgLine);

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

        log.log(Level.INFO, "Generated image with Pencil Strokes");
        // displayImage(imgLine);
        // outputImage("GrayScale.jpg", this.imgGray);
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

    public void generateCi(int customSize) {
        // First we will generate 4 Li - For now only using 4 directions only
        int size = Math.max(this.imgColor.width(), this.imgColor.height())/30;
        size = Math.min(size, customSize);
        size = size + (size % 2) + 1;
        double intensity = 10;

        Point center = new Point(size/2.0, size/2.0);
        Mat src = new Mat(size, size, CvType.CV_32FC1, new Scalar(0));
        src.row(size/2).setTo(new Scalar(intensity));

        for (int i = 0; i < Li.length; i++) {
            Mat rotMat = Imgproc.getRotationMatrix2D(center, i*(180.0/numDirections), 1.0);
            Li[i] = new Mat();
            Imgproc.warpAffine(src, Li[i], rotMat, src.size(), Imgproc.INTER_CUBIC);
        }

        // Generating Gis corresponding to Lis
        Mat [] Gi = new Mat[numDirections];

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

        log.log(Level.INFO, "Completed generation of Cis");
    }

    //*******************************************************************//
    //****** Following part of code is to Generate Pencil Texture  ******//
    //*******************************************************************//
    double [] CDFImg = new double[256];
    double [] CDFParametric = new double[256];
    Mat imgY;
    Mat imgEqualized;

    void generatePencilTexture() {
        // Step 1: Generate current image histogram
        getImageCDF();
        // Step 2: Equalize histogram to the predefined histogram
        getParametricCDF();
        doHistogramEqualization();
        // Step 3: Calculate beta matrix for pencil texture

        // Step 4: Generate texture image

    }

    void getImageCDF() {
        log.log(Level.INFO, "Getting Image CDF");
        
        imgY = new Mat();
        // Generated the Y value from the image i.e. tone map
        Imgproc.cvtColor(imgColor, imgY, Imgproc.COLOR_BGR2YUV);
        List<Mat> YUV = new ArrayList<Mat>();
        Core.split(imgY, YUV);
        imgY = YUV.get(0);
        Core.normalize(imgY, imgY, 0, 255, Core.NORM_MINMAX);

        // Generating histogram of the image. Here we will use 8U type & 0-255 values
        for (int i = 0; i < CDFImg.length; i++) {
            CDFImg[i] = 0.0;
        }

        double totalPixels = imgY.width() * imgY.height();
        imgY.convertTo(imgY, CvType.CV_8U);
        for (int i = 0; i < imgY.rows(); i++) {
            for (int j = 0; j < imgY.cols(); j++) {
                int data;
                data = (int) getPixelValue(i, j, imgY)[0];
                CDFImg[data] += 1;
            }
        }
        
        // We are generating CDF because we need that to do histogram equalization
        double last = 0.0;
        for (int i = 0; i < CDFImg.length; i++) {
            CDFImg[i] = (CDFImg[i] / totalPixels) + last;
            last = CDFImg[i];
            // System.out.printf("Probability for color %d is %f\n", i, CDFImg[i]);
        }
    }

    void getParametricCDF() {
        log.log(Level.INFO, "Generating Parameteric CDF to which we will equalize the image CDF");
        double [] w = {52, 37, 11}; // Wi for weights
        double sigmab = 9;
        double ua = 105;
        double ub = 225;
        double mud = 90;
        double sigmad = 11;
        
        for (int i = 0; i < CDFParametric.length; i++) {
            CDFParametric[i] = 0.0;
        }

        double sum = 0.0;
        for (int i = 0; i < CDFParametric.length; i++) {
            // Generating p1 which will be for light tone
            double p0 = (1.0/sigmab) * (Math.exp( (-1*(255-i))/sigmab ));
            
            // Generating p2 for mild tone
            double p1 = 0.0;
            if ( (i <= ub) && (i >= ua)) {
                p1 = 1 / (ub - ua);
            }

            // Generating p3 for dark tone
            double p2 = (1 / Math.sqrt(2*3.14*sigmad)) * Math.exp( (-1*(i - mud)*(i - mud)) / (2*sigmad*sigmad) );

            CDFParametric[i] = (w[0]*p0) + (w[1]*p1) + (w[2]*p2);
            sum += CDFParametric[i]; //Calculating sum for normalization
        }

        double last = 0.0;
        for (int i = 0; i < CDFParametric.length; i++) {
            CDFParametric[i] = (CDFParametric[i] / sum) + last;
            last = CDFParametric[i];
            // System.out.printf("Probability for color %d is %f\n", i, CDFParametric[i]);
        }
    }

    void doHistogramEqualization() {
        imgEqualized = new Mat(imgY.rows(), imgY.cols(), CvType.CV_8UC1);
        for (int i = 0; i < imgY.rows(); i++) {
            for (int j = 0; j < imgY.cols(); j++) {
                // Step 1: Get Pixel value for this col
                int pixelVal = (int) getPixelValue(i, j, imgY)[0];
                // Step 2: Find nearest probability greater than or equal
                int newVal = pixelVal;
                double cdfVal = CDFImg[pixelVal];
                for (int k = 0; k < CDFParametric.length; k++) {
                    if (CDFParametric[k] < cdfVal) {
                        continue;        
                    } else {
                        newVal = k;
                        break;
                    }
                }
                //Step 3: Setting pixel to newVal
                double [] data = {newVal};
                updatePixelVal(i, j, data, imgEqualized);
            }
        }
    }

    //*******************************************************************//
    //********* References for different methods to regular use *********//
    //*******************************************************************//
    
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
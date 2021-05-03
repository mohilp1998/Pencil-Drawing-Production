# Pencil-Drawing-Production

## Introduction
In this project, I am trying to recreate the research paper **Combining Sketch and Tone for Pencil Drawing Production** by Cewu Lu, Li Xu & Jiaya Jia. The main aim of the paper is to create a pencil drawing from a given image. You can refer to the research paper at this [Link](http://www.cse.cuhk.edu.hk/~leojia/projects/pencilsketch/pencil_drawing.htm).

## Main Ideas
The paper proposes a new system to generate pencil drawing from natural images. It suggests new techniques to capture the pencil drawing properties like Pencil Strokes and Texture to create a pencil drawing. In the following sections, I will discuss the main ideas proposed by the paper. The following explanations are based on my understanding of the research work. Please read the paper for more details.

### Line Drawing With Strokes
A major difference between natural images and pencil drawings is the curves. We will observe continuous curves in natural images, but if an artist is drawing a pencil image, he will use small discontinuous strokes to create the same curve.

The authors use this fact and suggest a method to convert the continuous curves in the natural image to small discontinuous strokes. They convert the original image to a gray-scale image & then use multiple directional kernels & convolution to generate a line drawing of the input image with strokes.
![Line Drawing with Strokes](/Java/readme-images/LineStrokesGen.PNG "Line Drawing with Strokes") 

### Pencil Texture Rendering
Once we have created a line drawing, the next step is to change the image texture to reflect a pencil texture. An artist uses dense strokes, such as hatching, to emphasize the darkness, shadows, and dark objects. To transfer these kinds of dense strokes onto a natural image, the authors use an existing human pencil texture. They use the human pencil texture and the natural image tonal map as a reference and suggest a technique to calibrate the density of human pencil texture based on natural image tone values & transfer the pencil texture onto the image.
![Pencil Texture Rendering](/Java/readme-images/PencilTextureRendering.PNG "Pencil Texture Rendering")

### Combining Both Together
Once both the line drawing and pencil texture images are generated, we combine those images to create the gray-scale pencil drawing images. This step involves the multiplication of the images followed by some brightness rescaling.
![Gray Scale Pencil Drawing](/Java/readme-images/GrayPencilImage.PNG "Gray Scale Pencil Drawing")

### Generating Color Pencil Drawing
Lastly, to generate the color pencil image drawing, we use the gray-scale pencil drawing as the Y component of the YUV image composition in the natural image. This method will effectively transfer the texture and line strokes details to the image without affecting the color parts of the image.
![Color Pencil Drawing](/Java/readme-images/ColorPencilImage.PNG "Color Pencil Drawing")

## Results
Following are some example results:
![Color Pencil Drawing](/Java/readme-images/ColorPencilImage-2.PNG "Color Pencil Drawing")
![Color Pencil Drawing](/Java/readme-images/ColorPencilImage-3.PNG "Color Pencil Drawing")
![Color Pencil Drawing](/Java/readme-images/ColorPencilImage-4.PNG "Color Pencil Drawing")

## Building Code Yourself
This section aims to help anyone who wishes to tinker with this project or use the code for further development. Here you will find details on how to configure and use the code.

### Prerequistes
1. Install Java Development Kit (JDK): https://www.oracle.com/in/java/technologies/javase-downloads.html
2. Install vscode: https://code.visualstudio.com/ (Feel free to use any code editor/IDE you prefer, I have used vscode and have launch.json & setting.json configure for it)

### Build the Code
1. If you are using vscode please go through this tutorial for setting up Java in vscode and running basic code: https://code.visualstudio.com/docs/java/java-tutorial
2. Add the PencilDrawing.java to your run environment in vscode
3. If you are not using vscode please add the file references opencv_java343.dll & opencv-343.jar in you IDE at appropriate positions
4. In the main function change the input image path and run the code
5. Additionally, in the PencilDrawing class constructor, you can change the log level according to your need. Currently, the code only uses two log levels, either SEVERE or INFO

## Future Improvements
1. Improving Pencil Texture Rendering Method: Currently, the pencil texture generation method used in the code is not the same as the research paper but is a crude approximate method. The research paper requires solving a linear equation using the conjugate gradient method to transfer pencil texture. As Java does not have an easy library available for these methods and implementing these methods by myself will take significant time. This work is deemed as a future improvement.
2. Android Application: Building an Android application to generate pencil Drawing from natural images.

## Acknowledgements:
* [Original Paper](http://www.cse.cuhk.edu.hk/~leojia/projects/pencilsketch/pencil_drawing.htm)
* [Open CV Documentation](https://docs.opencv.org/3.4/index.html)
* [candaycat1992 Matlab Implementation](https://github.com/candycat1992/PencilDrawing): This is a good reference for understand proper pencil texture rendering method (as suggested in future improvements)
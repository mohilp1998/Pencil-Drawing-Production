# Pencil-Drawing-Production

## Introduction
In this project, I am trying to recreate the research paper **Combining Sketch and Tone for Pencil Drawing Production** by Cewu Lu, Li Xu & Jiaya Jia. The main aim of the paper is to create a pencil drawing from a given image. You can refer to the research paper at this [Link](http://www.cse.cuhk.edu.hk/~leojia/projects/pencilsketch/pencil_drawing.htm).

## Main Ideas
The paper proposes a new system to generate pencil drawing from natural images. It proposes new techniques to capature the pencil drawing properties like Pencil Strokes and Texture to create a pencil drawing. In the following sections I will discuss the main ideas proposed by the paper. Following explanations are based on my understanding of the research work. Please read the paper for more details.

### Line Drawing With Strokes
A major difference between natural images and pencil drawings are the curves. In natural images we will observe continuous curves, but if an artist drawing a pencil image will use small discontinuous strokes to create the same curve.

The authors use this fact and suggests a method a convert the continuous curves in natural image to small discontinuous strokes by using directional kernel and taking convolution with them to generate a line drawing of the input image with strokes.
![Line Drawing with Strokes](/Java/readme-images/LineStrokesGen.PNG "Line Drawing with Strokes") 

### Pencil Texture Rendering
Once we have created a line Drawing, the next step is to change the image texture to reflect a pencil texture. An artist uses dense strokes, such as hatching to emphasize the darkness, shadows and dark objects. To transfer these kind of dense strokes onto a natural image, the authors use an existing human pencil texture. Using the human pencil texture as refer and the natural image tonal map, the paper suggests a techinque a calibrate the density of human pencil texture based on natural image tone values & transfer the pencil texture onto the image.
![Pencil Texture Rendering](/Java/readme-images/PencilTextureRendering.PNG "Pencil Texture Rendering")

### Combining Both Together
Once both the line drawing and pencil texture images are generated, we combine those images together to create a gray scale pencil drawing images. This step involves multiplication of the images followed by some brightness rescaling.
![Gray Scale Pencil Drawing](/Java/readme-images/GrayPencilImage.PNG "Gray Scale Pencil Drawing")

### Generating Color Pencil Drawing
Lastly to generate the color pencil image drawing we use the gray scale pencil drawing as Y component of the YUV image composition in the natural image. This method will effect transfer the texture and line strokes details to the image without affecting the color parts of the image.
![Color Pencil Drawing](/Java/readme-images/ColorPencilImage.PNG "Color Pencil Drawing")

## Results



## Building Code Yourself

### Prerequistes

### Build the Code

Link for Java Setting up: https://code.visualstudio.com/docs/java/java-tutorial

### Debugging Message Configuration:


## Future Work
1. Improving Pencil Texture Method
2. Android Code
# Pencil-Drawing-Production

## Introduction
In this project, I am trying to recreate the research paper **Combining Sketch and Tone for Pencil Drawing Production** by Cewu Lu, Li Xu & Jiaya Jia. The main aim of the paper is to create a pencil drawing from a given image. You can refer to the research paper at this [Link](http://www.cse.cuhk.edu.hk/~leojia/projects/pencilsketch/pencil_drawing.htm).

## Main Ideas
The paper proposes a new system to generate pencil drawing from natural images. It proposes new techniques to capature the pencil drawing properties like Pencil Strokes and Texture to create a pencil drawing. In the following sections I will discuss the main ideas proposed by the paper. Following explanations are based on my understanding of the research work. Please read the paper for more details.

### Line Drawing With Strokes
A major difference between natural images and pencil drawings are the curves. In natural images we will observe continuous curves, but if an artist drawing a pencil image will use small discontinuous strokes to create the same curve.

The authors use this fact and suggests a method a convert the continuous curves in natural image to small discontinuous strokes by using directional kernel and taking convolution with them to generate a line drawing of the input image with strokes.
![Line Drawing with Strokes](/Java/readme-images/LineStrokesGen.PNG "Line Drawing with Strokes") 

### Pencil Texture

### Combining Both Together

### Generating Color Pencil Drawing


## Results



## Building Code Yourself

### Prerequistes

### Build the Code

Link for Java Setting up: https://code.visualstudio.com/docs/java/java-tutorial

### Debugging Message Configuration:


## Future Work
1. Improving Pencil Texture Method
2. Android Code
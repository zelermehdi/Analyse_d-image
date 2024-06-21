package com.example.image_analyzer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.global.opencv_imgcodecs;

@SpringBootApplication
public class ImageAnalyzerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ImageAnalyzerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String imagePath = "C:\\Users\\orore\\Downloads\\20240416_155037.jpg";
        analyzeImage(imagePath);
    }

    public void analyzeImage(String imagePath) {
        try {
            Mat image = opencv_imgcodecs.imread(imagePath);
            if (image.empty()) {
                System.out.println("Image not loaded.");
                return;
            }

            int width = image.cols();
            int height = image.rows();

            System.out.println("Image Width: " + width);
            System.out.println("Image Height: " + height);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.example.promar;

import com.example.promar.model.BoxPosition;
import com.example.promar.model.DescriptorType;
import com.example.promar.model.ImageFeature;
import com.example.promar.model.Recognition;
import com.example.promar.util.ImageUtil;
import org.opencv.core.*;
import org.opencv.features2d.Features2d;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        testTensorFlow();
        testPROMAR();
    }

    static void testTensorFlow() {
        String imgPath = "src/main/resources/image/dog_cat.jpg";
        BufferedImage img = ImageUtil.loadImage(imgPath);

        ObjectDetector objectDetector = new ObjectDetector();
        objectDetector.init();
        List<Recognition> recognitions = objectDetector.recognizeImage(img);

        Graphics2D graphics = (Graphics2D) img.getGraphics();
        for (Recognition r : recognitions) {
            //get scaled box location
            BoxPosition bp = r.getScaledLocation((float)img.getWidth()/r.getModelSize(), (float)img.getHeight()/r.getModelSize());
            graphics.drawString(r.getTitle() + " " + r.getConfidence(), bp.getLeft(), bp.getTop() - 7);
            // draw bounding box
            graphics.drawRect(bp.getLeftInt(), bp.getTopInt(), bp.getWidthInt(), bp.getHeightInt());
        }
        //display recognized result
        ImageUtil.displayImage(img);
    }

    static void testPROMAR() {
        String path = "src/main/resources/image/furry_bear/";
        Mat t_img = ImageUtil.loadMatImage(path+"0.png");
//        Mat q_img = ImageUtil.loadMatImage(path+"20.png");
        Mat q_img = ImageUtil.loadMatImage(path+"false/20.jpg");

        ImageFeature tIF = ImageProcessor.extractRobustFeatures(t_img,
                ImageProcessor.changeToLeftPerspective(t_img, t_img.width()/80f, 10),
                100, 500, DescriptorType.ORB, null);
        ImageFeature qIF = ImageProcessor.extractORBFeatures(q_img);

        MatOfDMatch m = ImageProcessor.matchWithRegression(qIF, tIF,5, 400, 20);
        Mat display1 = new Mat();
        Features2d.drawMatches(q_img, qIF.getObjectKeypoints(),t_img, tIF.getObjectKeypoints(),  m, display1);
        ImageUtil.displayImage(ImageUtil.Mat2BufferedImage(display1));
        System.out.printf("PROMAR matching ratio: %.2f\n", (float)m.total()/ tIF.getSize());

        ImageFeature regularIF = ImageProcessor.extractORBFeatures(t_img, 100);
        MatOfDMatch m2 = ImageProcessor.BFMatchWithCrossCheck(qIF, regularIF);
        Mat display2 = new Mat();
        Features2d.drawMatches(q_img, qIF.getObjectKeypoints(),t_img, regularIF.getObjectKeypoints(),  m2, display2);
        ImageUtil.displayImage(ImageUtil.Mat2BufferedImage(display2));
        System.out.printf("Regular matching ratio: %.2f\n", (float)m2.total()/ regularIF.getSize());
    }
}

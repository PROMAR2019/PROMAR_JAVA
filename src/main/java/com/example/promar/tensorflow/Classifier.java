package com.example.promar.tensorflow;

import com.example.promar.model.Recognition;

import java.awt.image.BufferedImage;
import java.util.List;

public interface Classifier {
    List<Recognition> recognizeImage(BufferedImage image);

//    void enableStatLogging(final boolean debug);
//
//    String getStatString();

//    void close();
}


// src/main/java/com/example/demo/PredictionController.java
package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    @PostMapping("/predict")
    public String handleImageUpload(@RequestParam("file") MultipartFile file) {
        return predictionService.predict(file);
    }
}

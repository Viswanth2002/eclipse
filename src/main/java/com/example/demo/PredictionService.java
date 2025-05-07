package com.example.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PredictionService {

    public String predict(MultipartFile file) {
        try {
            // Create uploads directory
            String rootPath = System.getProperty("user.dir");
            File uploadDir = new File(rootPath, "uploads");
            if (!uploadDir.exists()) uploadDir.mkdirs();

            // Save uploaded image
            File savedFile = new File(uploadDir, file.getOriginalFilename());
            file.transferTo(savedFile);

            // Set the correct Python executable (from Anaconda) and script location
            String pythonExecutable = "/opt/anaconda3/bin/python3"; // Use Anaconda's Python executable
            String pythonScript = "python/predict.py"; // Your Python script location

            // Run Python subprocess
            ProcessBuilder pb = new ProcessBuilder(pythonExecutable, pythonScript, savedFile.getAbsolutePath());
            pb.directory(new File(rootPath)); // Make sure the working directory is project root
            pb.redirectErrorStream(true);     // Combine stderr with stdout

            // Environment variables (if needed for conda environment)
            pb.environment().put("PATH", "/opt/anaconda3/bin:" + System.getenv("PATH")); // Ensure conda/bin is in PATH

            // Start the Python process
            Process process = pb.start();

            // Read STDOUT from Python
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("PYTHON OUT: " + line); // log all output
                if(line.equals("yes")) {
                	output.append("Congragulations!!! You do not have pneumonia.").append("This is an AI-generated prediction. Although it has over 90% accuracy, if you experience any symptoms, it's best to consult a medical professional.")
                    .append("\n");
                	}
                else {
                	 output.append("ohhh, very bad, You may have pneumonia! Please take immediate medication.").append("\n");
                }
            }

            // Wait for process and clean up
            int exitCode = process.waitFor();
            savedFile.delete(); // Optional: clean up image after processing

            if (exitCode != 0) {
                System.err.println("Python script exited with code " + exitCode);
            }

            return output.toString().trim(); // Return model result or full error message

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

}

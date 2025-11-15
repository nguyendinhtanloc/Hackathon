package com.wellness.wellness.service;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Service for facial emotion detection using AWS Rekognition
 */
public class FacialEmotionService {
    
    private static RekognitionClient rekognitionClient;
    
    static {
        // Load AWS credentials from properties file
        try {
            Properties props = new Properties();
            InputStream input = FacialEmotionService.class.getClassLoader()
                    .getResourceAsStream("aws.properties");
            
            if (input != null) {
                props.load(input);
                
                String accessKey = props.getProperty("aws.access.key.id");
                String secretKey = props.getProperty("aws.secret.access.key");
                String region = props.getProperty("aws.region", "us-east-1");
                
                // Check if credentials are configured
                if (accessKey != null && !accessKey.startsWith("YOUR_") && 
                    secretKey != null && !secretKey.startsWith("YOUR_")) {
                    
                    AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
                    rekognitionClient = RekognitionClient.builder()
                            .region(Region.of(region))
                            .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                            .build();
                    
                    System.out.println("✅ AWS Rekognition initialized successfully with region: " + region);
                } else {
                    System.err.println("⚠️ AWS credentials not configured in aws.properties");
                    System.err.println("   Edit src/main/resources/aws.properties and add your credentials");
                }
                
                input.close();
            } else {
                System.err.println("⚠️ aws.properties file not found in resources folder");
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading AWS credentials: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Detect emotions from base64 image
     */
    public static EmotionResult detectEmotion(String base64Image) {
        if (rekognitionClient == null) {
            throw new RuntimeException("AWS Rekognition client not initialized. Check AWS credentials.");
        }
        
        try {
            // Remove data URL prefix if present
            if (base64Image.contains(",")) {
                base64Image = base64Image.split(",")[1];
            }
            
            // Decode base64 to bytes
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            SdkBytes sourceBytes = SdkBytes.fromByteArray(imageBytes);
            
            // Create image object
            Image image = Image.builder()
                    .bytes(sourceBytes)
                    .build();
            
            // Detect faces with attributes
            DetectFacesRequest request = DetectFacesRequest.builder()
                    .image(image)
                    .attributes(Attribute.ALL)
                    .build();
            
            DetectFacesResponse response = rekognitionClient.detectFaces(request);
            List<FaceDetail> faceDetails = response.faceDetails();
            
            if (faceDetails.isEmpty()) {
                return new EmotionResult(false, "No face detected", null, 0.0f);
            }
            
            // Get first face
            FaceDetail face = faceDetails.get(0);
            
            // Get dominant emotion
            List<software.amazon.awssdk.services.rekognition.model.Emotion> emotions = face.emotions();
            software.amazon.awssdk.services.rekognition.model.Emotion dominantEmotion = emotions.stream()
                    .max((e1, e2) -> Float.compare(e1.confidence(), e2.confidence()))
                    .orElse(null);
            
            if (dominantEmotion != null) {
                String emotion = dominantEmotion.type().toString().toLowerCase();
                float confidence = dominantEmotion.confidence();
                
                // Map AWS emotions to our format
                Map<String, Float> allEmotions = new HashMap<>();
                for (software.amazon.awssdk.services.rekognition.model.Emotion e : emotions) {
                    allEmotions.put(e.type().toString().toLowerCase(), e.confidence());
                }
                
                EmotionResult result = new EmotionResult(true, "Success", emotion, confidence / 100.0f);
                result.setAllEmotions(allEmotions);
                result.setFaceConfidence(face.confidence());
                
                return result;
            }
            
            return new EmotionResult(false, "No emotion detected", null, 0.0f);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new EmotionResult(false, "Error: " + e.getMessage(), null, 0.0f);
        }
    }
    
    /**
     * Result class for emotion detection
     */
    public static class EmotionResult {
        private boolean success;
        private String message;
        private String emotion;
        private float confidence;
        private Map<String, Float> allEmotions;
        private Float faceConfidence;
        
        public EmotionResult(boolean success, String message, String emotion, float confidence) {
            this.success = success;
            this.message = message;
            this.emotion = emotion;
            this.confidence = confidence;
        }
        
        // Getters and setters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getEmotion() { return emotion; }
        public float getConfidence() { return confidence; }
        public Map<String, Float> getAllEmotions() { return allEmotions; }
        public Float getFaceConfidence() { return faceConfidence; }
        
        public void setAllEmotions(Map<String, Float> allEmotions) { this.allEmotions = allEmotions; }
        public void setFaceConfidence(Float faceConfidence) { this.faceConfidence = faceConfidence; }
    }
}

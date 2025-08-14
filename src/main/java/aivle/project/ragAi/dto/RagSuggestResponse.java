package aivle.project.ragAi.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RagSuggestResponse {
    private String inspectionId;
    private String level;
    private String title;
    private List<String> actions;
    // private List<String> tools; // Removed as per request
    // private List<String> parts; // Removed as per request
    private int timeMin;
    private List<String> verification;
    private List<String> safety;
    private List<Map<String, String>> sources;
    private double overallConfidence;
    private boolean needHumanReview;
}

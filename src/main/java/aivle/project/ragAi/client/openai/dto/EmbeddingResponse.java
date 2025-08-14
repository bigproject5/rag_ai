package aivle.project.ragAi.client.openai.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmbeddingResponse {
    private List<EmbeddingData> data;

    @Data
    public static class EmbeddingData {
        private double[] embedding;
    }
}

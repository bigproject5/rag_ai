package aivle.project.ragAi.client.openai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingRequest {
    private String input;
    private String model;
}

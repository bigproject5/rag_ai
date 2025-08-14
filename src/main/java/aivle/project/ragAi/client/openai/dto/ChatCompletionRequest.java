package aivle.project.ragAi.client.openai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletionRequest {

    private String model;
    private List<ChatMessage> messages;

    @JsonProperty("response_format")
    private ResponseFormat responseFormat;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessage {
        private String role;
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseFormat {
        private String type;
    }
}

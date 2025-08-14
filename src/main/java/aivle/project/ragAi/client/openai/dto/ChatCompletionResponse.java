package aivle.project.ragAi.client.openai.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatCompletionResponse {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private Message message;
    }

    @Data
    public static class Message {
        private String content;
    }
}

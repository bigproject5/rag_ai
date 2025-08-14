package aivle.project.ragAi.client.openai;

import aivle.project.ragAi.client.LlmClient;
import aivle.project.ragAi.client.openai.dto.ChatCompletionRequest;
import aivle.project.ragAi.client.openai.dto.ChatCompletionResponse;
import aivle.project.ragAi.config.properties.OpenAiProperties;
import aivle.project.ragAi.dto.RagSuggestRequest;
import aivle.project.ragAi.dto.RagSuggestResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAiLlmClient implements LlmClient {

    private final OpenAiProperties properties;
    private final ObjectMapper objectMapper; // Spring Boot provides this bean by default
    private final WebClient webClient = WebClient.create();

    private static final String SYSTEM_PROMPT_TEMPLATE = """
            You are a super-intelligent AI assistant for vehicle inspection triage. Your task is to generate a structured JSON response based on the provided context of related documents. The JSON response must strictly follow this schema: %s. Do not include any other text or explanations outside of the JSON structure.
            """;

    private static final String USER_PROMPT_TEMPLATE = """
            Here is the context from the technical manuals:
            --- CONTEXT ---
            %s
            --- END CONTEXT ---
            
            Based on the context above, please generate the triage suggestion for the following inspection case:
            - Process: %s
            - Vehicle Model: %s
            """;

    @Override
    public RagSuggestResponse generate(RagSuggestRequest request, String context, Map<String, Object> schema) {
        try {
            String schemaString = objectMapper.writeValueAsString(schema);
            String systemPrompt = String.format(SYSTEM_PROMPT_TEMPLATE, schemaString);
            String userPrompt = String.format(USER_PROMPT_TEMPLATE, context, request.getProcess(), request.getVehicleModel());

            ChatCompletionRequest.ResponseFormat responseFormat = new ChatCompletionRequest.ResponseFormat("json_object");
            List<ChatCompletionRequest.ChatMessage> messages = List.of(
                    new ChatCompletionRequest.ChatMessage("system", systemPrompt),
                    new ChatCompletionRequest.ChatMessage("user", userPrompt)
            );

            ChatCompletionRequest chatRequest = new ChatCompletionRequest(properties.getChatModel(), messages, responseFormat);

            ChatCompletionResponse response = webClient.post()
                    .uri(properties.getBaseUrl() + "/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(chatRequest)
                    .retrieve()
                    .bodyToMono(ChatCompletionResponse.class)
                    .block();

            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                String jsonContent = response.getChoices().get(0).getMessage().getContent();
                return objectMapper.readValue(jsonContent, RagSuggestResponse.class);
            }
            throw new RuntimeException("Failed to get a valid response from OpenAI Chat API.");

        } catch (Exception e) {
            throw new RuntimeException("Error during LLM generation: " + e.getMessage(), e);
        }
    }
}

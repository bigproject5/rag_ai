package aivle.project.ragAi.client.openai;

import aivle.project.ragAi.client.EmbeddingClient;
import aivle.project.ragAi.client.openai.dto.EmbeddingRequest;
import aivle.project.ragAi.client.openai.dto.EmbeddingResponse;
import aivle.project.ragAi.config.properties.OpenAiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class OpenAiEmbeddingClient implements EmbeddingClient {

    private final OpenAiProperties properties;
    private final WebClient webClient = WebClient.create();

    @Override
    public double[] embed(String text) {
        EmbeddingRequest request = new EmbeddingRequest(text, properties.getEmbedModel());

        EmbeddingResponse response = webClient.post()
                .uri(properties.getBaseUrl() + "/embeddings")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(EmbeddingResponse.class)
                .block(); // Using block() for simplicity, async handling is also possible

        if (response != null && response.getData() != null && !response.getData().isEmpty()) {
            return response.getData().get(0).getEmbedding();
        }
        return null;
    }
}

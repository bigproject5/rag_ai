package aivle.project.ragAi.client;

import aivle.project.ragAi.dto.RagSuggestRequest;
import aivle.project.ragAi.dto.RagSuggestResponse;

import java.util.Map;

public interface LlmClient {
    RagSuggestResponse generate(RagSuggestRequest request, String context, Map<String, Object> schema);
}

package aivle.project.ragAi.service;

import aivle.project.ragAi.client.EmbeddingClient;
import aivle.project.ragAi.client.LlmClient;
import aivle.project.ragAi.domain.GuideChunk;
import aivle.project.ragAi.dto.RagSuggestRequest;
import aivle.project.ragAi.dto.RagSuggestResponse;
import aivle.project.ragAi.repository.GuideChunkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RagService {

    private final GuideChunkRepository repo;
    private final EmbeddingClient embeddingClient;
    private final LlmClient llmClient;

    public RagSuggestResponse suggest(RagSuggestRequest req) {
        if (!req.isDefect()) {
            RagSuggestResponse normal = new RagSuggestResponse();
            normal.setLevel("TRIAGE");
            normal.setTitle("정상 판정");
            normal.setActions(Collections.emptyList());
            normal.setTimeMin(0);
            normal.setVerification(List.of("특이사항 없음"));
            normal.setSafety(Collections.emptyList());
            normal.setSources(new ArrayList<>());
            normal.setOverallConfidence(1.0);
            normal.setNeedHumanReview(false);
            normal.setInspectionId(req.getInspectionId());
            return normal;
        }

        // 1) 검색 (Retrieval)
        String query = "공정 1차 점검 기본 조치 절차 안전 확인"; // 공구, 부품 키워드 제거
        double[] qvec = embeddingClient.embed(query);
        String qvecString = Arrays.stream(qvec)
                                  .mapToObj(String::valueOf)
                                  .collect(Collectors.joining(",", "[", "]"));
        List<GuideChunk> chunks = repo.searchByProcess(req.getProcess(), qvecString, 8);

        String chunksText = chunks.stream()
                .map(c -> "- (" + c.getDocName() + " " + c.getSection() + " v" + c.getVersion() + ")\n" + c.getContent())
                .collect(Collectors.joining("\n\n"));

        // 2) LLM 생성 (Generation) & Fallback
        Map<String, Object> schema = createResponseSchema();

        RagSuggestResponse resp;
        try {
            resp = llmClient.generate(req, chunksText, schema);
        } catch (Exception ex) {
            // Fallback logic
            resp = createFallbackResponse(req, chunks);
        }
        resp.setInspectionId(req.getInspectionId());
        return resp;
    }

    private Map<String, Object> createResponseSchema() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("level", "TRIAGE");
        schema.put("title", "string");
        schema.put("actions", List.of("string"));
        // schema.put("tools", List.of("string")); // Removed
        // schema.put("parts", List.of("string")); // Removed
        schema.put("time_min", 0);
        schema.put("verification", List.of("string"));
        schema.put("safety", List.of("string"));
        schema.put("sources", List.of(Map.of("doc","string","section","string","version","string")));
        schema.put("overall_confidence", 0.0);
        schema.put("need_human_review", false);
        return schema;
    }

    private RagSuggestResponse createFallbackResponse(RagSuggestRequest req, List<GuideChunk> chunks) {
        List<String> actions = new ArrayList<>();
        chunks.stream().limit(4).forEach(c -> {
            for (String line : c.getContent().split("\n")) {
                var t = line.trim();
                if (t.startsWith("[조치]")) actions.add(t.replace("[조치]","").trim());
                // Logic for [공구] and [부품] removed
            }
        });

        RagSuggestResponse resp = new RagSuggestResponse();
        resp.setLevel("TRIAGE");
        resp.setTitle(req.getProcess() + " 공정 1차 점검(트리아지)");
        resp.setActions(actions.isEmpty()? List.of("문서 근거를 바탕으로 1차 점검을 수행하세요.") : actions);
        resp.setTimeMin(10);
        resp.setVerification(List.of("기준 충족 확인"));
        resp.setSafety(List.of("안전수칙 준수"));
        var src = chunks.stream().limit(3).map(c -> Map.of("doc", c.getDocName(), "section", c.getSection(), "version", c.getVersion())).toList();
        resp.setSources(new ArrayList<>(src));
        resp.setOverallConfidence(0.5);
        resp.setNeedHumanReview(true);
        return resp;
    }
}

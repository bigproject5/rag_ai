package aivle.project.ragAi.web;

import aivle.project.ragAi.dto.RagSuggestRequest;
import aivle.project.ragAi.dto.RagSuggestResponse;
import aivle.project.ragAi.service.DocxIngestService;
import aivle.project.ragAi.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RagController {

    private final RagService ragService;
    private final DocxIngestService ingestService;

    @GetMapping("/health")
    public String health() { return "ok"; }

    @PostMapping("/suggest")
    public RagSuggestResponse suggest(@RequestBody RagSuggestRequest req) {
        return ragService.suggest(req);
    }

    @PostMapping(value = "/admin/ingest", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String ingest(@RequestParam("process") String process,
                         @RequestParam("version") String version,
                         @RequestParam("file") MultipartFile file) throws IOException {
        return ingestService.requestIngestion(process, version, file);
    }
}

package aivle.project.ragAi.service;

import aivle.project.ragAi.client.EmbeddingClient;
import aivle.project.ragAi.domain.GuideChunk;
import aivle.project.ragAi.dto.IngestRequestMessage;
import aivle.project.ragAi.repository.GuideChunkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocxIngestWorker {

    private final EmbeddingClient embeddingClient;
    private final GuideChunkRepository guideChunkRepository;

    @KafkaListener(topics = "${rag.topic.ingest}", groupId = "${spring.kafka.consumer.group-id}")
    public void processIngestion(IngestRequestMessage message) {
        log.info("Received ingestion request for file: {}", message.getOriginalFilename());

        try {
            // Step 1: Parse the DOCX file to extract text.
            String textContent;
            try (InputStream inputStream = new ByteArrayInputStream(message.getFileContent());
                 XWPFDocument document = new XWPFDocument(inputStream);
                 XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
                textContent = extractor.getText();
            }

            // Step 2: Split the text into manageable chunks.
            String[] chunks = textContent.split("\n\n");

            // Step 3: Process and save each chunk.
            for (String chunkContent : chunks) {
                if (chunkContent.trim().isEmpty()) continue;

                // Create embedding for the chunk content
                double[] embeddingVector = embeddingClient.embed(chunkContent);
                String embeddingString = Arrays.stream(embeddingVector)
                                               .mapToObj(String::valueOf)
                                               .collect(Collectors.joining(",", "[", "]"));

                // Create and populate the entity
                GuideChunk chunk = new GuideChunk();
                chunk.setProcessCode(message.getProcess());
                chunk.setVersion(message.getVersion());
                chunk.setDocName(message.getOriginalFilename());
                chunk.setContent(chunkContent);
                chunk.setEmbedding(embeddingString);

                // Save to the database
                guideChunkRepository.save(chunk);
            }
            log.info("Successfully processed and saved {} chunks for file: {}", chunks.length, message.getOriginalFilename());

        } catch (Exception e) {
            log.error("Failed to process ingestion request for file: {}", message.getOriginalFilename(), e);
        }
    }
}

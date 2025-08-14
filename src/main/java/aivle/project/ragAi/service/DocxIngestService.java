package aivle.project.ragAi.service;

import aivle.project.ragAi.dto.IngestRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class DocxIngestService {

    private final KafkaTemplate<String, IngestRequestMessage> kafkaTemplate;
    private static final String INGEST_TOPIC = "ingest-topic";

    /**
     * Receives a document file, creates an ingest request message,
     * and sends it to a Kafka topic for asynchronous processing.
     * @return A confirmation message that the request has been accepted.
     */
    public String requestIngestion(String process, String version, MultipartFile file) throws IOException {
        // Create the message payload
        IngestRequestMessage message = new IngestRequestMessage(
                process,
                version,
                file.getBytes(),
                file.getOriginalFilename()
        );

        // Send the message to the Kafka topic
        kafkaTemplate.send(INGEST_TOPIC, message);

        // Immediately return a confirmation to the user
        return "Document ingestion request for '" + file.getOriginalFilename() + "' has been accepted and is being processed.";
    }
}

package aivle.project.ragAi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngestRequestMessage {
    private String process;
    private String version;
    private byte[] fileContent;
    private String originalFilename;
}

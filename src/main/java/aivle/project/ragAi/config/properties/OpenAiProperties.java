package aivle.project.ragAi.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {
    private String baseUrl;
    private String apiKey;
    private String chatModel;
    private String embedModel;
}

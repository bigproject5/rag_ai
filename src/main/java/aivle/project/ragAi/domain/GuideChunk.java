package aivle.project.ragAi.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "guide_chunks")
@Data
public class GuideChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "process_code", nullable = false)
    private String processCode;

    @Column(name = "doc_name")
    private String docName;

    private String section;

    private String version;

    @Column(nullable = false)
    private String content;

    // The 'embedding' field is mapped to the 'vector' column in the database.
    // We handle it as a String in Java and let the database cast it to the vector type.
    @Column(columnDefinition = "vector(3072)")
    private String embedding;

    // The 'keywords' field could be handled similarly if needed.
    // @Column(columnDefinition = "text[]")
    // private String[] keywords;
}

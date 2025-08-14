package aivle.project.ragAi.repository;

import aivle.project.ragAi.domain.GuideChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuideChunkRepository extends JpaRepository<GuideChunk, Long> {

    // This native query performs a cosine similarity search on the 'embedding' vector.
    // It requires the pgvector extension to be enabled in PostgreSQL.
    // The vector parameter needs to be passed in the format required by pgvector.
    @Query(value = "SELECT * FROM guide_chunks WHERE process_code = :process ORDER BY embedding <=> :vector LIMIT :limit", nativeQuery = true)
    List<GuideChunk> searchByProcess(
        @Param("process") String process,
        @Param("vector") String vector, // We'll pass the vector as a string e.g., '[1.0, 2.0, ...]' 
        @Param("limit") int limit
    );
}

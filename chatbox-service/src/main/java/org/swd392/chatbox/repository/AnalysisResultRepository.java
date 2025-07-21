package org.swd392.chatbox.repository;

import org.swd392.chatbox.entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, UUID> {

    Optional<AnalysisResult> findBySessionId(String sessionId);

    boolean existsBySessionId(String sessionId);

    void deleteBySessionId(String sessionId);

    /**
     * Tìm kết quả phân tích theo cả userId và sessionId để đảm bảo an toàn.
     */
    Optional<AnalysisResult> findByUserIdAndSessionId(String userId, String sessionId);

    /**
     * Lấy tất cả các kết quả phân tích của một người dùng.
     */
    List<AnalysisResult> findAllByUserId(String userId);
}

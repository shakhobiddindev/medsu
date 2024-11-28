package uz.medsu.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import uz.medsu.entity.Attachment;

import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<Attachment, String> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Attachment a WHERE a.fullyName = :fullyName")
    void deleteByFullyName(String fullyName);

    Optional<Attachment> findByFullyName(String fullyName);
}

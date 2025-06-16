package at.modoo.repository;

import at.modoo.domain.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, String> {
    List<Attachment> findAllByReferenceIdIn(List<String> referenceIds);
}

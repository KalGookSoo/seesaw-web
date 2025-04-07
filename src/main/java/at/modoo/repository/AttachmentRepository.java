package at.modoo.repository;

import at.modoo.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, String> {
    List<Attachment> findAllByReferenceIdIn(List<String> referenceIds);
}

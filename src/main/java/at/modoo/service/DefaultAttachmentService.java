package at.modoo.service;

import at.modoo.model.Attachment;
import at.modoo.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Transactional
@Service
public class DefaultAttachmentService implements AttachmentService {

    private final String filepath;

    private final AttachmentRepository attachmentRepository;

    public DefaultAttachmentService(
            @Value("${at.modoo.filepath}") String filepath,
            AttachmentRepository attachmentRepository
    ) {
        this.filepath = filepath;
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public Attachment find(String id) {
        return attachmentRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public String getAbsolutePath(String id) {
        Attachment attachment = find(id);
        return filepath + attachment.getPathName() + attachment.getName();
    }

}

package at.modoo.service;

import at.modoo.command.CreateAttachmentCommand;
import at.modoo.core.file.FileIOService;
import at.modoo.model.Attachment;
import at.modoo.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
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
        return getAbsolutePath(attachment);
    }

    @Override
    public String getAbsolutePath(Attachment attachment) {
        return filepath + attachment.getPathName() + File.separator + attachment.getName();
    }

    @Override
    public Attachment create(CreateAttachmentCommand command) throws IOException {
        Attachment attachment = Attachment.create(command);
        attachmentRepository.save(attachment);
        FileIOService.write(getAbsolutePath(attachment), command.getMultipartFile().getBytes());
        return attachment;
    }

}

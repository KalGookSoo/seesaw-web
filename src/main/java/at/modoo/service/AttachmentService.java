package at.modoo.service;

import at.modoo.command.CreateAttachmentCommand;
import at.modoo.model.Attachment;

import java.io.IOException;

public interface AttachmentService {
    Attachment find(String id);
    String getAbsolutePath(String id);
    String getAbsolutePath(Attachment attachment);
    Attachment create(CreateAttachmentCommand command) throws IOException;
}

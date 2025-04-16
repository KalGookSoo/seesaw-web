package at.modoo.service;

import at.modoo.command.CreateAttachmentCommand;
import at.modoo.model.Attachment;

public interface AttachmentService {
    Attachment find(String id);
    String getAbsolutePath(String id);
    Attachment create(CreateAttachmentCommand command);
}

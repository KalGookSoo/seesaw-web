package at.modoo.service;

import at.modoo.model.Attachment;

public interface AttachmentService {
    Attachment find(String id);
    String getAbsolutePath(String id);
    String getAbsolutePath(Attachment attachment);
}

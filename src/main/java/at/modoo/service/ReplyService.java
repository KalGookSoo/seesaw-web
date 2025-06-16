package at.modoo.service;

import at.modoo.command.CreateReplyCommand;
import at.modoo.command.UpdateReplyCommand;
import at.modoo.domain.Reply;

public interface ReplyService {
    Reply find(String id);
    void create(CreateReplyCommand command);
    void update(String id, UpdateReplyCommand command);
    boolean isOwner(String id, String username);
    void delete(String id);
}

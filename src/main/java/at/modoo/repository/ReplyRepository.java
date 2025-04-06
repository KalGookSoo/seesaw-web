package at.modoo.repository;

import at.modoo.model.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, String> {
    List<Reply> findAllByArticleIdIn(List<String> articleIds);
}

package at.modoo.repository;

import at.modoo.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * 계정 저장소
 */
public interface UserRepository extends JpaRepository<User, String> {
    @EntityGraph(attributePaths = {"authorities"})
    Optional<User> findByUsername(@NonNull String username);
}

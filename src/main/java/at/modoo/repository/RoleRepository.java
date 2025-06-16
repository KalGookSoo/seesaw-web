package at.modoo.repository;

import at.modoo.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {
    List<Role> findAllByReferenceId(String referenceId);
    List<Role> findAllByReferenceIdIn(List<String> referenceIds);
    Optional<Role> findByName(String name);
}

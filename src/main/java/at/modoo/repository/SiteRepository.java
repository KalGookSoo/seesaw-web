package at.modoo.repository;

import at.modoo.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface SiteRepository extends JpaRepository<Site, String> {
    Optional<Site> findByDomainName(@NonNull String domainName);
}

package at.modoo.repository;

import at.modoo.model.User;
import at.modoo.search.UserSearch;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class UserSearchRepository implements SearchRepository<User, UserSearch> {

    private final EntityManager em;

    @Override
    public Page<User> search(@NonNull UserSearch search) {
        Pageable pageable = search.pageable();
        String jpql = "select user from User user where 1=1";
        jpql += generateJpql(search);

        if (pageable.getSort().isSorted()) {
            jpql += " order by " + pageable.getSort().toString().replace(":", "");
        }

        TypedQuery<User> query = em.createQuery(jpql, User.class);
        setParameters(query, search);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<User> users = query.getResultList();

        String countJpql = "select count(user) from User user where 1=1";
        countJpql += generateJpql(search);

        TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);
        setParameters(countQuery, search);

        Long count = countQuery.getSingleResult();

        return new PageImpl<>(users, pageable, count);
    }

    private String generateJpql(@Nonnull UserSearch search) {
        StringBuilder jpql = new StringBuilder();
        if (StringUtils.hasText(search.getUsername())) {
            jpql.append(" and user.username like :username");
        }
        if (StringUtils.hasText(search.getName())) {
            jpql.append(" and user.name like :name");
        }
        if (StringUtils.hasText(search.getEmail())) {
            jpql.append(" and user.email.id || '@' || user.email.domain like :email");
        }
        if (StringUtils.hasText(search.getContactNumber())) {
            jpql.append(" and user.contactNumber.first || user.contactNumber.middle || user.contactNumber.last like :contactNumber");
        }
        return jpql.toString();
    }

    private void setParameters(@Nonnull TypedQuery<?> query, @Nonnull UserSearch search) {
        if (StringUtils.hasText(search.getUsername())) {
            query.setParameter("username", "%" + search.getUsername() + "%");
        }
        if (StringUtils.hasText(search.getName())) {
            query.setParameter("name", "%" + search.getName() + "%");
        }
        if (StringUtils.hasText(search.getEmail())) {
            query.setParameter("email", "%" + search.getEmail() + "%");
        }
        if (StringUtils.hasText(search.getContactNumber())) {
            query.setParameter("contactNumber", "%" + search.getContactNumber() + "%");
        }
    }

}

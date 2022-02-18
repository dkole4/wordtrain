package xyz.wordtr41n.api.specification;

import xyz.wordtr41n.api.domain.User;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification implements Specification<User> {

    private User filter;

    public UserSpecification(User filter) {
        super();
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> cq,
            CriteriaBuilder cb) {
        Predicate p = cb.disjunction();

        if (filter.getUsername() != null) {
            p.getExpressions().add(cb.like(root.get("username"), "%" + filter.getUsername() + "%"));
        }

        return p;
    }
}
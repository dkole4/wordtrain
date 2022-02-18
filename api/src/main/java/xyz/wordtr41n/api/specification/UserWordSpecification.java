package xyz.wordtr41n.api.specification;

import xyz.wordtr41n.api.domain.UserWord;
import xyz.wordtr41n.api.domain.UserWordPK;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class UserWordSpecification implements Specification<UserWord> {

    private UserWord filter;

    public UserWordSpecification(UserWord filter) {
        super();
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<UserWord> root, CriteriaQuery<?> cq,
            CriteriaBuilder cb) {
        Predicate p = cb.disjunction();

        if (filter.getId() != null) {
            p.getExpressions().add(cb.like(root.get("id"), "%" + filter.getId() + "%"));
        }

        return p;
    }
}
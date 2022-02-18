package xyz.wordtr41n.api.specification;

import xyz.wordtr41n.api.domain.Word;

import java.util.List;
import java.util.ArrayList;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

public class WordSpecification implements Specification<Word> {

    private Word filter;

    public WordSpecification(Word filter) {
        super();
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<Word> root, CriteriaQuery<?> cq,
            CriteriaBuilder cb) {
        List<Predicate> p = new ArrayList<>();

        if (!ObjectUtils.isEmpty(filter.getWord()))
            p.add(cb.equal(root.get("word"), filter.getWord()));
        
        if (!ObjectUtils.isEmpty(filter.getTranslation()))
            p.add(cb.equal(root.get("translation"), filter.getTranslation()));
        
        if (!ObjectUtils.isEmpty(filter.getLangWord()))
            p.add(cb.equal(root.get("langWord"), filter.getLangWord()));
        
        if (!ObjectUtils.isEmpty(filter.getLangTranslation()))
            p.add(cb.equal(root.get("langTranslation"), filter.getLangTranslation()));

        return cb.and(p.toArray(new Predicate[0]));
    }
}
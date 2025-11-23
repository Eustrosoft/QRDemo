package org.eustrosoft.repositories.specifications;

import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.repositories.projections.QRSimpleProjection;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class QRSpecifications implements Specification<QRSimpleProjection> {

    private final Long participantId;
    private final List<QRRange> ranges;

    public QRSpecifications(Long participantId, List<QRRange> ranges) {
        this.participantId = participantId;
        this.ranges = ranges;
    }

    public static Specification<QRSimpleProjection> withParticipantId(Long participantId) {
        if (participantId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("participantId"), participantId);
    }

    public static Specification<QRSimpleProjection> betweenRange(Long fromCode, Long toCode) {
        if (fromCode == null || toCode == null) {
            return null;
        }
        return (root, query, cb) -> cb.between(root.get("code"), fromCode, toCode);
    }

    @Override
    public Predicate toPredicate(Root<QRSimpleProjection> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate participantPredicate = null;
        List<Predicate> orPredicates = new ArrayList<>();
        List<Predicate> finalPredicates = new ArrayList<>();

        if (participantId != null) {
            participantPredicate = criteriaBuilder.equal(root.get("participantId"), participantId);
        }

        if (!CollectionUtils.isEmpty(ranges)) {
            for (QRRange range : ranges) {
                orPredicates.add(criteriaBuilder.between(root.get("code"), range.getFrom(), range.getTo()));
            }
        }
        Predicate or = criteriaBuilder.or(orPredicates.toArray(new Predicate[0]));
        finalPredicates.add(participantPredicate);
        finalPredicates.add(or);
        return criteriaBuilder.and(finalPredicates.toArray(new Predicate[0]));
    }
}

package org.eustrosoft.repositories.specifications;

import org.eustrosoft.repositories.projections.QRSimpleProjection;
import org.springframework.data.jpa.domain.Specification;

public class QRSpecifications {

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
}

package org.eustrosoft.entitites;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import java.io.IOException;
import java.util.Comparator;

@EqualsAndHashCode(callSuper = true)
@Data
@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "qr_range", schema = "qrdemo")
public class QRRange extends DbEntity implements Comparable<QRRange> {
    public static final String TYPE = "QRR";

    @Column(name = "from_range")
    private Long from;

    @Column(name = "to_range")
    private Long to;

    @Getter
    public enum Type {
        DEMO(16),
        CONFIRMED(16),
        COMMERCIAL(4096),
        REDIRECT(4096),
        INDIVIDUAL(4096);

        private final int size;

        Type(int size) {
            this.size = size;
        }
    }

    @Override
    public int compareTo(QRRange o) {
        return Comparator
                .nullsLast(Comparator.comparingLong(QRRange::getFrom))
                .compare(this, o);
    }
}

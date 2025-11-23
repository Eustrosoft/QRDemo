package org.eustrosoft.entitites;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "m_range", schema = "qrdemo")
public class MRange extends DbEntity {
    public static final String TYPE = "MRG";

    @Column(name = "rstart")
    private Long rStart;

    @Column(name = "rBitl")
    private Long rBitl;

    @Column(name = "rtype")
    private String rType;

    @Column(name = "status")
    private Character status;

    @Column(name = "action")
    private String action;

    @Column(name = "redirect")
    private String redirect;

    @Column(name = "alloc")
    private Timestamp alloc;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "doc_id")
    private Long docId;

    @Column(name = "owiki")
    private String oWiki;
}

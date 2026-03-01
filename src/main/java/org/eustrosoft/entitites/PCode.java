package org.eustrosoft.entitites;

import lombok.Data;
import org.eustrosoft.entitites.composite.PCodeCompositeId;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@IdClass(PCodeCompositeId.class)
@Table(name = "p_code", schema = "qrdemo")
public class PCode implements Serializable {
    public static final String TYPE = "PCD";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "p_code_seq_gen")
    @SequenceGenerator(
            name = "p_code_seq_gen",
            sequenceName = "p_code_seq",
            allocationSize = 1
    )
    @Column(name = "row_id", nullable = false)
    private Long rowId;

    @Id
    @Column(name = "doc_id")
    private Long docId;

    @Column(name = "participant_id")
    private Long participantId;

    @Column(name = "h_fields")
    private Character hFields;

    @Column(name = "h_files")
    private Character hFiles;

    @Column(name = "p")
    private String p;

    @Column(name = "p2")
    private String p2;

    @Column(name = "p2_mode")
    private String p2Mode;

    @Column(name = "p2_prompt")
    private String p2Prompt;

    @Column(name = "comment")
    private String comment;
}

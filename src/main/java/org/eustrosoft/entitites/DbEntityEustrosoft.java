package org.eustrosoft.entitites;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@MappedSuperclass
public class DbEntityEustrosoft implements Serializable {
    public static final String FIELD_NAME_TYPE = "TYPE";

    @Id
    @Column(name = "ZOID")
    private Long ZOID;

    @Id
    @Column(name = "ZRID")
    private Long ZRID;

    @Id
    @Column(name = "ZVER")
    private Long ZVER;

    @Column(name = "ZTOV")
    private Long ZTOV;

    @Column(name = "ZSID")
    private Long ZSID;

    @Column(name = "ZLVL")
    private Short ZLVL;

    @Column(name = "ZPID")
    private Long ZPID;

    @Column(name = "ZUID")
    private Long ZUID;

    @Column(name = "ZUIDO")
    private Long ZUIDO;

    @Column(name = "ZSTA")
    private Character ZSTA;

    @Column(name = "ZDATE")
    private Timestamp ZDATE;

    @Column(name = "ZDATO")
    private Timestamp ZDATO;

    protected DbEntityEustrosoft(Long ZOID) {
        this.ZOID = ZOID;
    }
}

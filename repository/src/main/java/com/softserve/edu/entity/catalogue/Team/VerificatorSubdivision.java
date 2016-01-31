package com.softserve.edu.entity.catalogue.Team;

import com.softserve.edu.entity.organization.Organization;
import lombok.*;

import javax.persistence.*;

/**
 * Project system-calibration-devices
 * Created by bo4ek on 27.01.2016.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "SUBDIVISION")
public class VerificatorSubdivision {

    @Id
    @Setter(AccessLevel.PRIVATE)
    private String id;

    private String name;
    private String leader;
    private String leaderEmail;
    private String leaderPhone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizationId")
    private Organization organization;

    public VerificatorSubdivision(String id, String name, String leader, String leaderEmail, String leaderPhone) {
        this.id = id;
        this.name = name;
        this.leader = leader;
        this.leaderEmail = leaderEmail;
        this.leaderPhone = leaderPhone;
    }
}
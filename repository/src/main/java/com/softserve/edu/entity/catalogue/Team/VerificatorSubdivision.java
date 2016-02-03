package com.softserve.edu.entity.catalogue.Team;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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
    @Column(length = 12)
    private String id;

    private String name;
    private String leader;
    private String leaderEmail;
    private String leaderPhone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizationId")
    private Organization organization;

    @OneToMany(mappedBy = "verificatorSubdivision", cascade = CascadeType.REFRESH)
    @JsonBackReference
    private Set<User> users = new HashSet<>();

    public VerificatorSubdivision(String id, String name, String leader, String leaderEmail, String leaderPhone) {
        this.id = id;
        this.name = name;
        this.leader = leader;
        this.leaderEmail = leaderEmail;
        this.leaderPhone = leaderPhone;
    }
}
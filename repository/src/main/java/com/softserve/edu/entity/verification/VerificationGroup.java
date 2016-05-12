package com.softserve.edu.entity.verification;

import com.softserve.edu.entity.catalogue.Team.DisassemblyTeam;
import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.verification.Verification;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "VERIFICATION_GROUP")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class VerificationGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Verification> verifications;

    public VerificationGroup() {
    }

    public VerificationGroup(Set<Verification> verifications) {
        this.verifications = verifications;
    }


    @Override
    public String toString() {
        return "VerificationGroup{" +
                "id =" + id +
                '}';
    }
}

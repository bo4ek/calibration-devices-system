package com.softserve.edu.entity.catalogue;

import com.softserve.edu.entity.organization.Organization;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

import static com.softserve.edu.entity.catalogue.util.Checker.checkForEmptyText;
import static com.softserve.edu.entity.catalogue.util.Checker.checkForNull;

@Entity
@Table(name = "LOCALITY")
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Locality extends AbstractCatalogue {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column
    private String designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "districtId")
    private District district;

    @Setter
    private String mailIndex;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ORGANIZATION_LOCALITY", joinColumns = @JoinColumn(name = "localityId"),
            inverseJoinColumns = @JoinColumn(name = "organizationId"))
    private Set<Organization> organizations;

    public Locality(District district, String designation) {
        this.district = district;
        this.designation = designation;
    }

    private void setDesignation(String designation) {
        checkForEmptyText(designation);
        this.designation = designation;
    }

    private void setDistrict(District district) {
        checkForNull(district);
        this.district = district;
    }
}

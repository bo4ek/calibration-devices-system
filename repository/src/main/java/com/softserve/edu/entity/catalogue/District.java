package com.softserve.edu.entity.catalogue;

import lombok.*;

import javax.persistence.*;

import static com.softserve.edu.entity.catalogue.util.Checker.checkForEmptyText;
import static com.softserve.edu.entity.catalogue.util.Checker.checkForNull;

@Entity
@Table(name = "DISTRICT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class District extends AbstractCatalogue {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue
    private Long id;

    @Column
    private String designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regionId")
    private Region region;

    public District(String name, Region region) {
        this.designation = name;
        this.region = region;
    }

    public String getDesignation() {
        return designation;
    }

    private void setDesignation(String designation) {
        checkForEmptyText(designation);
        this.designation = designation;
    }

    private void setRegion(Region region) {
        checkForNull(region);
        this.region = region;
    }
}

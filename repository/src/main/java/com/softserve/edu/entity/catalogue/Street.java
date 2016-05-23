package com.softserve.edu.entity.catalogue;

import lombok.*;

import javax.persistence.*;

import static com.softserve.edu.entity.catalogue.util.Checker.checkForEmptyText;
import static com.softserve.edu.entity.catalogue.util.Checker.checkForNull;

@Entity
@Table(name = "STREET")
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Street extends AbstractCatalogue {

    @Id
    private Long id;

    @Column
    private String designation;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "localityId")
    private Locality locality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "streetTypeId")
    @Setter
    private StreetType streetType;

    public Street(String designation, Locality locality, StreetType streetType) {
        setDesignation(designation);
        setLocality(locality);
        setStreetType(streetType);
    }

    public Street(Long id, String designation, Locality locality) {
        this(designation, locality, null);
        this.id = id;
    }

    private void setDesignation(String designation) {
        checkForEmptyText(designation);
        this.designation = designation;
    }

    private void setLocality(Locality locality) {
        checkForNull(locality);
        this.locality = locality;
    }

    public StreetType getStreetType() {
        return streetType;
    }
}

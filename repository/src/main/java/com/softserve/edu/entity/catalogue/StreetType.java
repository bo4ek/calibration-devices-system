package com.softserve.edu.entity.catalogue;

import lombok.*;

import javax.persistence.*;

import static com.softserve.edu.entity.catalogue.util.Checker.checkForEmptyText;


@Entity
@Table(name = "STREET_TYPE")
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StreetType extends AbstractCatalogue {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column
    private String designation;

    public StreetType(String designation) {
        setDesignation(designation);
    }

    private void setDesignation(String designation) {
        checkForEmptyText(designation);
        this.designation = designation;
    }
}

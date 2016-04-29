package com.softserve.edu.entity.catalogue;

import lombok.*;

import javax.persistence.*;

import static com.softserve.edu.entity.catalogue.util.Checker.checkForEmptyText;
import static com.softserve.edu.entity.catalogue.util.Checker.checkForNull;

@Entity
@Table(name = "BUILDING")
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Building extends AbstractCatalogue {

    @Id
    @GeneratedValue
    @Setter(value = AccessLevel.PRIVATE)
    private Long id;

    @Column
    private String designation;

    @ManyToOne
    @JoinColumn(name = "streetId")
    private Street street;


    public Building(Street street, String designation) {
        setStreet(street);
        setDesignation(designation);
    }

    private void setDesignation(String designation) {
        checkForEmptyText(designation);
        this.designation = designation;
    }

    public void setStreet(Street street) {
        checkForNull(street);
        this.street = street;
    }
}

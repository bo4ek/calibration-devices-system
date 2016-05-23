package com.softserve.edu.entity.organization;

import com.softserve.edu.entity.Address;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "ORGANIZATION_CHANGES_HISTORY")
public class OrganizationEditHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date date;

    private String name;
    private String email;
    private String phone;
    private Integer employeesCapacity;
    private Integer maxProcessTime;
    private String types;

    private String username;
    private String firstName;
    private String lastName;
    private String middleName;
    
    private String adminName;

    @Embedded
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizationId")
    private Organization organization;

    public OrganizationEditHistory(Date date, String name, String email, String phone, Integer employeesCapacity, Integer maxProcessTime, String types, String username, String firstName, String lastName, String middleName, Organization organization, Address address, String adminName) {
        this.date = date;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.employeesCapacity = employeesCapacity;
        this.maxProcessTime = maxProcessTime;
        this.types = types;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.organization = organization;
        this.address = address;
        this.middleName = middleName;
        this.adminName = adminName;
    }
}

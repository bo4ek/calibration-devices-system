package com.softserve.edu.entity.device;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.verification.Verification;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

/**
 * Device Entity represents mose global essence then Counter Entity for Device Category or something like this.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Table(name = "DEVICE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column
    private String deviceName;

    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    private String deviceSign;

    private String number;

    private Boolean defaultDevice;

    @OneToMany(mappedBy = "device", fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<Verification> verifications;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "providerId")
    @JsonManagedReference
    private Organization provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturerId")
    private Manufacturer manufacturer;

    @OneToMany(mappedBy = "device", fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<CounterType> counterTypeSet;

    @OneToMany(mappedBy = "device", fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<UnsuitabilityReason> unsuitabilitySet;

    public Device(String number, Set<Verification> verifications, Manufacturer manufacturer) {
        this.number = number;
        this.verifications = verifications;
        this.manufacturer = manufacturer;
    }

    public Device(DeviceType deviceType, String deviceName) {
        this.deviceType = deviceType;
        this.deviceName = deviceName;
    }

    /**
     * Represents types of measurement devices.
     */
    public enum DeviceType {
        WATER(1),
        THERMAL(2);
//        ELECTRICAL(3),
//        GASEOUS(4);

        private int id;

        DeviceType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}

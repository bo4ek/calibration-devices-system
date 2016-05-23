package com.softserve.edu.entity.device;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "REASONS_UNSUITABILITY")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class UnsuitabilityReason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deviceId")
    private Device device;

    public UnsuitabilityReason(String name, Device device) {
        this.name = name;
        this.device = device;
    }
}

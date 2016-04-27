package com.softserve.edu.entity.verification.calibration;

import com.softserve.edu.entity.verification.Verification;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "REJECTED_INFO")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class RejectedInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public RejectedInfo(String name) {
        this.name = name;
    }

    public RejectedInfo(Long id, String name) {
        this.name = name;
        this.id = id;
    }
}

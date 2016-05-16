package com.softserve.edu.entity.verification;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Table(name = "BBI_PROTOCOL")
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BbiProtocol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verificationId")
    private Verification verification;
    
    public BbiProtocol(String fileName, Verification verification) {
        this.fileName = fileName;
        this.verification = verification;
    }
}
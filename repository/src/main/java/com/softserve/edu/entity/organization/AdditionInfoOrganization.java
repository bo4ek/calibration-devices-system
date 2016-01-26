package com.softserve.edu.entity.organization;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import javax.persistence.Embeddable;
import java.util.Date;

@Embeddable
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdditionInfoOrganization {

    private String codeEDRPOU;
    private String subordination;
    private String certificateNumberAuthorization;
    private Date certificateDate;

    public AdditionInfoOrganization(String codeEDRPOU, String subordination, String certificateNumberAuthorization, Long certificateDate) {
        this.codeEDRPOU = codeEDRPOU;
        this.subordination = subordination;
        this.certificateNumberAuthorization = certificateNumberAuthorization;
        this.certificateDate = (certificateDate != null) ? new Date(certificateDate) : null;
    }
}
package com.softserve.edu.dto.provider;

import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class ProviderStageVerificationDTO {
    private String surname;
    private String name;
    private String middleName;
    private String phone;
    private String secondPhone;
    private String region;
    private String district;
    private String locality;
    private String street;
    private String building;
    private String flat;
    private Organization calibrator;
    private Device device;
    private User providerEmployee;

}

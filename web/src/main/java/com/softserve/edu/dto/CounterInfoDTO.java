package com.softserve.edu.dto;

import com.softserve.edu.entity.device.Device;
import lombok.Setter;
import lombok.Getter;

import java.util.Date;

@Setter
@Getter

public class CounterInfoDTO {

    private String verificationId;
    private Long deviceId;
    private Device.DeviceType deviceType;
    private String deviceName;
    private Boolean dismantled;
    private String comment;
    private Date dateOfDismantled;
    private Date dateOfMounted;
    private String numberCounter;
    private Boolean sealPresence;
    private String releaseYear;
    private String accumulatedVolume;
    private String symbol;
    private String standardSize;
    private Boolean verificationWithDismantle;

}
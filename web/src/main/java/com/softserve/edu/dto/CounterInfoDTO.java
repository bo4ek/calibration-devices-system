package com.softserve.edu.dto;

import com.softserve.edu.entity.device.Device;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter

public class CounterInfoDTO {

    private String verificationId;
    private Long deviceId;
    private Device.DeviceType deviceType;
    private String deviceName;
    private Boolean dismantled;
    private String comment;
    private Long dateOfDismantled;
    private Long dateOfMounted;
    private String numberCounter;
    private Boolean sealPresence;
    private String releaseYear;
    private String accumulatedVolume;
    private String symbol;
    private String standardSize;
    private Boolean verificationWithDismantle;

}
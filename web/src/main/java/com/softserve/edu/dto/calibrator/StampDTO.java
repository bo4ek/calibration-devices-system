package com.softserve.edu.dto.calibrator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class StampDTO {

    String stamp;
    String verificationId;

    public StampDTO(String stamp) {
        this.stamp = stamp;
    }

    public StampDTO(String stamp, String verificationId) {
        this(stamp);
        this.verificationId = verificationId;
    }
}

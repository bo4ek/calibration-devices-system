package com.softserve.edu.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CounterTypeDTO {

    private Long id;
    private String name;
    private String symbol;
    private String standardSize;
    private String manufacturer;
    private Integer calibrationInterval;
    private Integer yearIntroduction;
    private String gost;
    private Long deviceId;
    private String typeWater;

    public CounterTypeDTO() {
    }

    public CounterTypeDTO(Long id, String name, String symbol, String standardSize, String manufacturer,
                          Integer calibrationInterval, Integer yearIntroduction, String gost, Long deviceId) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.standardSize = standardSize;
        this.manufacturer = manufacturer;
        this.calibrationInterval = calibrationInterval;
        this.yearIntroduction = yearIntroduction;
        this.gost = gost;
        this.deviceId = deviceId;
    }

    public CounterTypeDTO(Long id, String name, String typeWater, String symbol, String standardSize, String manufacturer,
                          Integer calibrationInterval, Integer yearIntroduction, String gost, Long deviceId) {
        this.id = id;
        this.name = name;
        this.typeWater = typeWater;
        this.symbol = symbol;
        this.standardSize = standardSize;
        this.manufacturer = manufacturer;
        this.calibrationInterval = calibrationInterval;
        this.yearIntroduction = yearIntroduction;
        this.gost = gost;
        this.deviceId = deviceId;
    }

    public CounterTypeDTO(Long id, String name){
        this.id = id;
        this.name = name;
    }

    public CounterTypeDTO(Long id, String symbol, String standardSize,String typeWater, String manufacturer) {
        this.id=id;
        this.symbol=symbol;
        this.standardSize=standardSize;
        this.typeWater = typeWater;
        this.manufacturer = manufacturer;
    }


}

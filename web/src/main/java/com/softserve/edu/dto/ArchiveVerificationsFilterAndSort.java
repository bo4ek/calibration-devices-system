package com.softserve.edu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArchiveVerificationsFilterAndSort {
	
	private String id;
	private String date;
	private String endDate;
	private String client_full_name;
	private String street;
	private String region;
	private String district;
	private String locality;
	private String status;
	private String employee_last_name;


	private Long protocol_id;
	private String protocol_status;

	private Long measurement_device_id;
	private String measurement_device_type;
    private String numberCounter;

}

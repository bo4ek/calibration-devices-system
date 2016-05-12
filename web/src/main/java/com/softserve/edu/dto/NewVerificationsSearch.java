package com.softserve.edu.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NewVerificationsSearch {

	private String idText;
	private String formattedDate;
	private String client_full_name;
	private String streetText;
	private String region;
	private String district;
	private String locality;
	private String status;
	private String employee;

	public NewVerificationsSearch() {
	}

	public NewVerificationsSearch(String idText, String formattedDate, String client_full_name, String streetText, String region, String district, String locality, String status, String employee) {

		this.idText = idText;
		this.formattedDate = formattedDate;
		this.client_full_name = client_full_name;
		this.streetText = streetText;
		this.region = region;
		this.district = district;
		this.locality = locality;
		this.status = status;
		this.employee = employee;
	}

}

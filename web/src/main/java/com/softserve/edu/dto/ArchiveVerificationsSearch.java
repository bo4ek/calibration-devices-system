package com.softserve.edu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArchiveVerificationsSearch {
	
	private String idText;
	private String formattedDate;
	private String lastNameText;
	private String streetText;
	private String status;
	private String employee;
	
	public ArchiveVerificationsSearch() {};
	
	public ArchiveVerificationsSearch(String idText, String formattedDate, String lastNameText, String streetText, String status, String employee) {

		this.idText = idText;
		this.formattedDate = formattedDate;
		this.lastNameText = lastNameText;
		this.streetText = streetText;
		this.status = status;
		this.employee = employee;
	}
}
